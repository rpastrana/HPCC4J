#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
jira_to_kb.py

Fetch Jira issues via JQL and export them to a knowledge base folder.

Enhancements:
- POST-first to /rest/api/3/search/jql with JSON body (cleaner with enhanced search).
- Explicit 'fields' list; new endpoint returns IDs only unless fields are requested.
- Cursor pagination via nextPageToken (in request body) for subsequent pages.
- One-time retry without 'reconcileIssues' if tenant rejects it.
- Removed fallback to deprecated /rest/api/3/search (410 Gone); retain a guarded GET /search/jql retry.
- Consolidated digest mode (--single-file) retained; per-issue mode unchanged when flag is omitted.
"""

import os
import sys
import json
import argparse
import datetime as dt
import logging
from typing import List, Dict, Any, Optional, Tuple

import requests
from slugify import slugify

DEFAULT_JIRA_BASE = "https://hpccsystems.atlassian.net"

# --- Logging helpers ---
def setup_logging(debug: bool) -> None:
    level = logging.DEBUG if debug else logging.INFO
    logging.basicConfig(
        level=level,
        format="%(asctime)s %(levelname)s %(message)s",
    )

def mask_email(email: str) -> str:
    try:
        name, domain = email.split("@", 1)
        if len(name) <= 2:
            return "***@" + domain
        return name[0] + "***" + name[-1] + "@" + domain
    except Exception:
        return "***"

# --- Helpers ---
def ymd(ts: Optional[str]) -> Optional[str]:
    if not ts:
        return None
    try:
        ts2 = ts.replace('Z', '+00:00')
        return dt.datetime.fromisoformat(ts2).date().isoformat()
    except Exception:
        return ts.split('T')[0] if 'T' in ts else ts

def parse_dt(ts: Optional[str]) -> Optional[dt.datetime]:
    """Best-effort parsing for Jira timestamps."""
    if not ts:
        return None
    try:
        ts2 = ts.replace('Z', '+00:00')
        return dt.datetime.fromisoformat(ts2)
    except Exception:
        for fmt in ("%Y-%m-%dT%H:%M:%S.%f%z", "%Y-%m-%dT%H:%M:%S%z"):
            try:
                return dt.datetime.strptime(ts, fmt)
            except Exception:
                pass
        return None

def safe_list(x):
    return x if isinstance(x, list) else []

FM_ORDER = [
    'id', 'title', 'source', 'applies_to', 'audience', 'confidence', 'tags'
]

def front_matter(data: Dict[str, Any]) -> str:
    def emit_val(v, indent=''):
        if isinstance(v, dict):
            lines = []
            for k in v:
                val = emit_val(v[k], indent + '  ')
                if "\n" in val:
                    lines.append(f"{indent}{k}:\n{val}")
                else:
                    lines.append(f"{indent}{k}: {val}")
            return "\n".join(lines)
        elif isinstance(v, list):
            if not v:
                return '[]'
            lines = [f"{indent}- {emit_val(i, indent + '  ').strip()}" for i in v]
            return "\n".join(lines)
        elif v is None:
            return 'null'
        elif isinstance(v, bool):
            return 'true' if v else 'false'
        elif isinstance(v, (int, float)):
            return str(v)
        else:
            s = str(v)
            if any(ch in s for ch in [":", "\n", '"', "'"]):
                s = s.replace('\r\n', '\n').replace('\r', '\n')
                s = s.replace('"', '\\"')
                return '"' + s + '"'
            return s

    lines = ['---']
    for k in FM_ORDER:
        if k in data:
            v = emit_val(data[k])
            if "\n" in v:
                lines.append(f"{k}:\n{v}")
            else:
                lines.append(f"{k}: {v}")
    for k in sorted(set(data.keys()) - set(FM_ORDER)):
        v = emit_val(data[k])
        if "\n" in v:
            lines.append(f"{k}:\n{v}")
        else:
            lines.append(f"{k}: {v}")
    lines.append('---')
    return "\n".join(lines)

# --- Fetch (POST-first to enhanced search) ---
ENHANCED_FIELDS = [
    "summary","status","created","updated","resolution",
    "components","labels","fixVersions","issuelinks","description"
]

def _post_search_jql(session: requests.Session, url: str, body: Dict[str, Any]) -> Dict[str, Any]:
    headers = {"Accept": "application/json", "Content-Type": "application/json"}
    r = session.post(url, headers=headers, json=body, timeout=30)
    logging.debug("HTTP POST %s -> %s", url, r.status_code)
    if r.status_code >= 400:
        try:
            logging.error("POST response: %s", r.text[:2000])
        except Exception:
            pass
    r.raise_for_status()
    return r.json()

def _get_search_jql(session: requests.Session, url: str, params: Dict[str, Any]) -> Dict[str, Any]:
    headers = {"Accept": "application/json"}
    r = session.get(url, headers=headers, params=params, timeout=30)
    logging.debug("HTTP GET %s -> %s", r.url, r.status_code)
    if r.status_code >= 400:
        try:
            logging.error("GET response: %s", r.text[:2000])
        except Exception:
            pass
    r.raise_for_status()
    return r.json()

def fetch_jira_issues(base: str, auth_tuple, jql: str, limit: int = 25) -> List[Dict[str, Any]]:
    """
    Use Jira Cloud enhanced search endpoint via POST to /rest/api/3/search/jql
    with JSON body:
      { jql, maxResults, fields: [...], reconcileIssues: true, nextPageToken? }
    - Paginate using nextPageToken from the response.
    - If POST with reconcileIssues fails (400), retry once without it.
    - As a last resort, try GET /search/jql with same fields (not recommended) and log body.
    NOTE: old /rest/api/3/search is removed (410 Gone). Do not call it.  # CHANGE-2046
    """
    issues: List[Dict[str, Any]] = []
    url = base.rstrip('/') + '/rest/api/3/search/jql'
    session = requests.Session()
    session.auth = auth_tuple

    # POST body template
    body = {
        "jql": jql,
        "maxResults": 100,
        "fields": ENHANCED_FIELDS,
        "reconcileIssues": True
    }
    next_token = None
    tried_without_reconcile = False

    logging.debug("FETCH (v3 search/jql POST) URL=%s body.keys=%s", url, list(body.keys()))

    try:
        while True:
            if next_token:
                body["nextPageToken"] = next_token
            else:
                body.pop("nextPageToken", None)

            data = _post_search_jql(session, url, body)
            data_keys = list(data.keys())
            logging.debug("Response keys: %s", data_keys)

            batch = data.get("issues") or []
            before = len(batch)
            batch = [it for it in batch if it.get("key")]
            if len(batch) != before:
                logging.warning("Filtered %d issues lacking 'key' (IDs-only rows).", before - len(batch))

            logging.info("Fetched %d issues in batch (token=%s)", len(batch), next_token)
            issues.extend(batch)
            if len(issues) >= limit:
                break

            next_token = data.get("nextPageToken")
            is_last = data.get("isLast", None)
            if not next_token or is_last is True:
                break

        return issues[:limit]

    except requests.HTTPError as http_err:
        code = getattr(http_err.response, "status_code", None)
        text = getattr(http_err.response, "text", "")[:1000]
        logging.error("POST /search/jql failed (%s): %s", code, text)

        # Retry once without reconcileIssues if present
        if not tried_without_reconcile:
            tried_without_reconcile = True
            logging.info("Retrying without reconcileIssues...")
            body.pop("reconcileIssues", None)
            next_token = None
            try:
                while True:
                    if next_token:
                        body["nextPageToken"] = next_token
                    else:
                        body.pop("nextPageToken", None)

                    data = _post_search_jql(session, url, body)
                    batch = data.get("issues") or []
                    batch = [it for it in batch if it.get("key")]
                    logging.info("Fetched %d issues in batch (no-reconcile)", len(batch))
                    issues.extend(batch)
                    if len(issues) >= limit:
                        break

                    next_token = data.get("nextPageToken")
                    is_last = data.get("isLast", None)
                    if not next_token or is_last is True:
                        break

                return issues[:limit]
            except Exception as e2:
                logging.error("Retry without reconcileIssues also failed: %s", e2)

        # Final guarded GET (not preferred, but sometimes helpful)
        try:
            params = {
                "jql": jql,
                "maxResults": 100,
                "fields": ",".join(ENHANCED_FIELDS)
            }
            logging.info("Attempting guarded GET /search/jql (final fallback)")
            data = _get_search_jql(session, url, params)
            batch = data.get("issues") or []
            batch = [it for it in batch if it.get("key")]
            logging.info("Fetched %d issues via GET fallback", len(batch))
            return batch[:limit]
        except Exception as e3:
            logging.error("GET /search/jql fallback failed: %s", e3)
            raise

    except Exception:
        # Any non-HTTP errors
        raise

# --- Transform (per-issue rendering: unchanged) ---
def to_kb_markdown(issue: Dict[str, Any], base_url: str) -> str:
    """Original per-issue file rendering, with front matter (unchanged)."""
    key = issue.get('key')
    fields = issue.get('fields', {}) or {}
    summary = fields.get('summary', '') or ''
    status = (fields.get('status') or {}).get('name')
    statusCat = ((fields.get('status') or {}).get('statusCategory') or {}).get('name')
    resolution = (fields.get('resolution') or {}).get('name')
    created = ymd(fields.get('created'))
    updated = ymd(fields.get('updated'))
    resolved = ymd(fields.get('resolutiondate'))
    components = [c.get('name') for c in (fields.get('components') or []) if c.get('name')]
    labels = fields.get('labels') or []
    fixVersions = [fv.get('name') for fv in (fields.get('fixVersions') or []) if fv.get('name')]
    description = fields.get('description') or ''

    rel_lines = []
    for link in (fields.get('issuelinks') or []):
        outkey = None
        if link.get('inwardIssue'):
            outkey = link['inwardIssue'].get('key')
        if link.get('outwardIssue'):
            outkey = link['outwardIssue'].get('key')
        if outkey:
            desc = (link.get('type', {}) or {}).get('name') or 'related'
            rel_lines.append(f"- {desc}: {outkey}")

    fm = {
        'id': f'jira-{key}',
        'title': f'{key} – {summary}' if key else summary or 'Jira issue',
        'source': {
            'type': 'jira',
            'url': f"{base_url.rstrip('/')}/browse/{key}" if key else base_url.rstrip('/'),
            'project': 'HPCC4J',
            'key': key,
            'status': status,
            'statusCategory': statusCat,
            'resolution': resolution,
            'created': created,
            'updated': updated,
            'resolved': resolved,
            'components': components,
            'fixVersions': fixVersions,
            'labels': labels,
        },
        'applies_to': {
            'hpcc4j': '*' if not fixVersions else f">= {fixVersions[0]}"
        },
        'audience': 'developer',
        'confidence': 'high' if (statusCat == 'Done') else 'medium',
        'tags': ['jira', 'change-log', 'rationale']
    }

    parts: List[str] = []
    parts.append(front_matter(fm))

    # Keep original description handling to avoid churn:
    desc_text = description if isinstance(description, str) else json.dumps(description)[:1000]
    if summary and desc_text and desc_text != summary:
        body = summary + "\n\n" + desc_text
    else:
        body = summary or desc_text or ""

    if body.strip():
        parts.append("## Outcome (from JIRA)\n" + body.strip() + "\n")

    guidance: List[str] = []
    if resolution == 'Fixed':
        guidance.append('Prefer the fixed/updated path; avoid re-introducing previous behavior.')
    elif resolution in ("Won't Fix", 'Rejected'):
        guidance.append('Avoid this approach; rationale documented in the JIRA ticket.')
    elif statusCat != 'Done':
        guidance.append('Do not depend on unmerged behavior; consider adding tests or defensive checks.')
    else:
        guidance.append('Follow documented outcome; gate by fixVersions if applicable.')

    if guidance:
        parts.append("## Agent guidance\n- " + "\n- ".join(guidance) + "\n")

    if rel_lines:
        parts.append("## Related\n" + "\n".join(rel_lines) + "\n")

    return "\n".join(parts)

# --- Digest (single-file) helpers ---
def issue_section_markdown(issue: Dict[str, Any], base_url: str) -> Tuple[str, str]:
    """
    Produce a digest-friendly section for a single issue (no front matter).
    Returns (anchor_id, section_md).
    """
    key = issue.get('key') or issue.get('id') or "issue"
    fields = issue.get('fields', {}) or {}
    summary = fields.get('summary', '') or ''
    status = (fields.get('status') or {}).get('name')
    statusCat = ((fields.get('status') or {}).get('statusCategory') or {}).get('name')
    resolution = (fields.get('resolution') or {}).get('name')
    created = ymd(fields.get('created'))
    updated = ymd(fields.get('updated'))
    resolved = ymd(fields.get('resolutiondate'))
    components = [c.get('name') for c in (fields.get('components') or []) if c.get('name')]
    labels = fields.get('labels') or []
    fixVersions = [fv.get('name') for fv in (fields.get('fixVersions') or []) if fv.get('name')]
    description = fields.get('description') or ''

    # Keep description handling consistent with per-issue output:
    desc_text = description if isinstance(description, str) else json.dumps(description)[:1000]
    body = (summary or "") + ("\n\n" + desc_text if desc_text and desc_text != summary else "")
    body = body.strip()

    rel_lines = []
    for link in (fields.get('issuelinks') or []):
        outkey = None
        if link.get('inwardIssue'):
            outkey = link['inwardIssue'].get('key')
        if link.get('outwardIssue'):
            outkey = link['outwardIssue'].get('key')
        if outkey:
            desc = (link.get('type', {}) or {}).get('name') or 'related'
            rel_lines.append(f"- {desc}: {outkey}")

    anchor_id = f"{key.lower()}"
    link = f"{base_url.rstrip('/')}/browse/{key}" if key else base_url.rstrip('/')
    meta_lines = [
        f"- **Key:** {link}",
        f"- **Status:** {status or 'n/a'} ({statusCat or 'n/a'})",
        f"- **Resolution:** {resolution or 'n/a'}",
        f"- **Created:** {created or 'n/a'}",
        f"- **Updated:** {updated or 'n/a'}",
    ]
    if resolved:
        meta_lines.append(f"- **Resolved:** {resolved}")
    if components:
        meta_lines.append(f"- **Components:** {', '.join(components)}")
    if labels:
        meta_lines.append(f"- **Labels:** {', '.join(labels)}")
    if fixVersions:
        meta_lines.append(f"- **FixVersions:** {', '.join(fixVersions)}")

    guidance: List[str] = []
    if resolution == 'Fixed':
        guidance.append('Prefer the fixed/updated path; avoid re-introducing previous behavior.')
    elif resolution in ("Won't Fix", 'Rejected'):
        guidance.append('Avoid this approach; rationale documented in the JIRA ticket.')
    elif statusCat != 'Done':
        guidance.append('Do not depend on unmerged behavior; consider adding tests or defensive checks.')
    else:
        guidance.append('Follow documented outcome; gate by fixVersions if applicable.')

    parts = []
    parts.append(f"### {key} — {summary}\n")
    if meta_lines:
        parts.append("\n".join(meta_lines) + "\n")
    if body:
        parts.append("\n#### Outcome (from JIRA)\n" + body + "\n")
    if guidance:
        parts.append("#### Agent guidance\n- " + "\n- ".join(guidance) + "\n")
    if rel_lines:
        parts.append("#### Related\n" + "\n".join(rel_lines) + "\n")

    return anchor_id, "\n".join(parts).strip() + "\n"

def build_digest_markdown(
    issues: List[Dict[str, Any]],
    base_url: str,
    jql: str,
    digest_title: Optional[str] = None,
    top_n: int = 20
) -> str:
    """Render a single-file Jira digest suitable for LLM chunking."""
    today = dt.date.today().isoformat()
    title = digest_title or f"Jira Digest — {today}"

    # KPIs / tallies
    status_count: Dict[str, int] = {}
    component_count: Dict[str, int] = {}
    label_count: Dict[str, int] = {}

    def _inc(d: Dict[str, int], k: Optional[str]):
        if not k:
            return
        d[k] = d.get(k, 0) + 1

    for it in issues:
        f = it.get('fields') or {}
        s = (f.get('status') or {}).get('name')
        _inc(status_count, s)
        comps = [c.get('name') for c in (f.get('components') or []) if c.get('name')]
        for c in comps:
            _inc(component_count, c)
        for lb in f.get('labels') or []:
            _inc(label_count, lb)

    # Recently updated
    def _upd_key(it: Dict[str, Any]) -> dt.datetime:
        ts = parse_dt((it.get('fields') or {}).get('updated'))
        return ts or dt.datetime.min.replace(tzinfo=None)

    sorted_recent = sorted(issues, key=_upd_key, reverse=True)
    recent = sorted_recent[:max(0, int(top_n))]

    # Front matter for the digest
    fm = {
        'id': f'jira-digest-{today}',
        'title': title,
        'source': {
            'type': 'jira-digest',
            'url': base_url.rstrip('/'),
            'jql': jql,
            'generated': dt.datetime.utcnow().replace(microsecond=0).isoformat() + "Z",
            'total_issues': len(issues),
        },
        'applies_to': {'hpcc4j': '*'},
        'audience': 'developer',
        'confidence': 'medium',
        'tags': ['jira', 'digest']
    }

    parts: List[str] = []
    parts.append(front_matter(fm))

    # Title
    parts.append(f"# {title}\n")

    # KPI Summary
    parts.append("## Summary KPIs\n")
    parts.append(f"- **Total issues:** {len(issues)}")
    if status_count:
        parts.append("- **By status:** " + ", ".join(f"{k}: {v}" for k, v in sorted(status_count.items())))
    if component_count:
        top_comps = ", ".join(f"{k}: {v}" for k, v in sorted(component_count.items(), key=lambda x: (-x[1], x[0]))[:10])
        parts.append(f"- **Top components:** {top_comps}")
    if label_count:
        top_labels = ", ".join(f"{k}: {v}" for k, v in sorted(label_count.items(), key=lambda x: (-x[1], x[0]))[:10])
        parts.append(f"- **Top labels:** {top_labels}")
    parts.append("")

    # Recently Updated
    parts.append("## Recently Updated\n")
    if not recent:
        parts.append("_No recent updates found._\n")
    else:
        for it in recent:
            k = it.get('key')
            f = it.get('fields') or {}
            s = (f.get('status') or {}).get('name') or 'n/a'
            up = ymd(f.get('updated')) or 'n/a'
            sm = (f.get('summary') or '')[:120]
            link = f"{base_url.rstrip('/')}/browse/{k}" if k else base_url.rstrip('/')
            parts.append(f"- **{k or 'n/a'}** ({s}; updated {up}) — {sm} — {link}")
        parts.append("")

    # Detail sections
    parts.append("## Detail\n")
    if not issues:
        parts.append("_No issues returned by the JQL query._\n")
    else:
        for it in issues:
            anchor, sec = issue_section_markdown(it, base_url)
            parts.append(sec)

    # Optional index of keys
    parts.append("\n## Index\n")
    if not issues:
        parts.append("_No entries._\n")
    else:
        for it in sorted(issues, key=lambda x: (x.get('key') or "")):
            k = it.get('key') or "issue"
            parts.append(f"- {k}")

    parts.append("")  # final newline
    return "\n".join(parts)

# --- Main ---
def main():
    p = argparse.ArgumentParser(
        description="Fetch Jira issues via JQL and export them to a knowledge base folder."
    )
    p.add_argument(
        '--base',
        default=os.environ.get('JIRA_BASE', DEFAULT_JIRA_BASE),
        help=f'Jira base URL (default: %(default)s)'
    )
    p.add_argument('--auth', required=True, help='email:api_token')
    p.add_argument('--jql', required=True, help='JQL query string')
    p.add_argument('--out', required=True, help='Output folder for markdown files')
    p.add_argument('--limit', type=int, default=25, help='Max number of issues to fetch (default: 25)')
    p.add_argument('--debug', action='store_true', help='Enable verbose debug logging')

    # Consolidated-report options
    p.add_argument('--single-file', default='', help='If set, write a single consolidated Markdown digest at this path')
    p.add_argument('--digest-title', default='', help='Optional custom title for the consolidated digest')
    p.add_argument('--digest-top', type=int, default=20, help='Number of items in "Recently Updated" (default: 20)')

    args = p.parse_args()
    setup_logging(args.debug)

    base = (args.base or "").strip() or os.environ.get('JIRA_BASE', '').strip() or DEFAULT_JIRA_BASE

    masked = mask_email(args.auth.split(':', 1)[0]) if ':' in args.auth else '***'
    logging.info("Starting jira_to_kb")
    logging.info("Base: %s", base)
    logging.info("Auth (email): %s", masked)
    logging.info("Limit: %s", args.limit)
    logging.info("JQL (first 200 chars): %s", (args.jql or '')[:200])
    if args.single_file:
        logging.info("Mode: single consolidated file -> %s", args.single_file)
        logging.info("Digest title: %s", args.digest_title or "(default)")
        logging.info("Digest top N: %d", args.digest_top)
    else:
        logging.info("Mode: per-issue files under %s", args.out)

    if not (base.startswith('http://') or base.startswith('https://')):
        print(f"Error: --base must be a full URL like https://your-domain.atlassian.net; got: {args.base!r}", file=sys.stderr)
        sys.exit(2)

    if ':' not in args.auth:
        print('Invalid --auth format; expected email:token', file=sys.stderr)
        sys.exit(2)

    email, token = args.auth.split(':', 1)
    session = requests.Session()
    session.auth = (email, token)

    try:
        issues = fetch_jira_issues(base, (email, token), args.jql, args.limit)
    except requests.HTTPError as http_err:
        logging.error("HTTP error: %s", http_err)
        if getattr(http_err, 'response', None) is not None:
            logging.error("Response body: %s", http_err.response.text[:2000])
        raise
    except Exception as e:
        logging.error("Failed to fetch issues: %s", e)
        raise

    logging.info("Total issues retrieved: %d", len(issues))

    # Preview the first issue to confirm we have key & fields in --debug
    if issues:
        try:
            sample = {k: issues[0].get(k) for k in ('id', 'key', 'fields')}
            logging.debug("Sample issue(0) preview: %s", json.dumps(sample)[:1500])
        except Exception:
            pass

    os.makedirs(args.out, exist_ok=True)

    if args.single_file:
        digest_path = args.single_file
        if not os.path.isabs(digest_path):
            digest_path = os.path.join(args.out, digest_path)
        os.makedirs(os.path.dirname(digest_path), exist_ok=True)

        md = build_digest_markdown(
            issues=issues,
            base_url=base,
            jql=args.jql,
            digest_title=(args.digest_title or None),
            top_n=args.digest_top
        )
        prev = None
        if os.path.exists(digest_path):
            with open(digest_path, 'r', encoding='utf-8') as f:
                prev = f.read()
        if prev != md:
            with open(digest_path, 'w', encoding='utf-8') as f:
                f.write(md)
            print('WROTE', digest_path)
        else:
            print('UNCHANGED', digest_path)
        logging.info("Digest write complete: %s", digest_path)
        return

    # Per-issue files (unchanged)
    written = 0
    skipped = 0
    unchanged = 0
    for it in issues:
        key = it.get('key')
        fields = (it.get('fields') or {})
        summary = (fields or {}).get('summary', '') or ''

        logging.debug("ISSUE raw keys=%s", list(it.keys()))
        logging.debug("ISSUE %s | %s", key, summary)

        if not key:
            fallback_id = it.get('id')
            if fallback_id:
                logging.warning("Issue missing 'key', using 'id' as filename base: %s", fallback_id)
                key = fallback_id
            else:
                logging.warning("Skipping issue without 'key' and 'id': %s", json.dumps(it)[:500])
                skipped += 1
                continue

        slug = slugify(summary)[:80] if summary else 'issue'
        outpath = os.path.join(args.out, f"{key}--{slug}.md")
        md = to_kb_markdown(it, base)

        prev = None
        if os.path.exists(outpath):
            with open(outpath, 'r', encoding='utf-8') as f:
                prev = f.read()

        if prev != md:
            with open(outpath, 'w', encoding='utf-8') as f:
                f.write(md)
            print('WROTE', outpath)
            written += 1
        else:
            print('UNCHANGED', outpath)
            unchanged += 1

    logging.info("Done. Written=%d, Unchanged=%d, Skipped=%d", written, unchanged, skipped)

if __name__ == '__main__':
    main()