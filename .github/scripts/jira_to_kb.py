#!/usr/bin/env python3
import os
import sys
import json
import argparse
import datetime as dt
from typing import List, Dict, Any, Optional

import requests
from slugify import slugify

DEFAULT_JIRA_BASE = "https://hpccsystems.atlassian.net"

# --- Helpers ---
def ymd(ts: Optional[str]) -> Optional[str]:
    if not ts:
        return None
    try:
        ts2 = ts.replace('Z', '+00:00')
        return dt.datetime.fromisoformat(ts2).date().isoformat()
    except Exception:
        return ts.split('T')[0] if 'T' in ts else ts

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

# --- Fetch ---

def fetch_jira_issues(base: str, auth_tuple, jql: str, limit: int = 25) -> List[Dict[str, Any]]:
    headers = {'Accept': 'application/json'}
    issues: List[Dict[str, Any]] = []
    url = base.rstrip('/') + '/rest/api/3/search/jql'
    params = {'jql': jql, 'maxResults': 100}
    next_token = None
    session = requests.Session()
    session.auth = auth_tuple

    try:
        while True:
            q = params.copy()
            if next_token:
                q['nextPageToken'] = next_token
            r = session.get(url, headers=headers, params=q, timeout=30)
            if r.status_code == 404:
                raise RuntimeError('search/jql not available')
            r.raise_for_status()
            data = r.json()
            batch = data.get('issues') or []
            issues.extend(batch)
            next_token = data.get('nextPageToken')
            if not next_token or len(issues) >= limit:
                break
        return issues[:limit]
    except Exception:
        # fallback to classic /search
        url2 = base.rstrip('/') + '/rest/api/3/search'
        startAt = 0
        while True:
            q = {'jql': jql, 'maxResults': 50, 'startAt': startAt}
            r = session.get(url2, headers=headers, params=q, timeout=30)
            r.raise_for_status()
            data = r.json()
            batch = data.get('issues', [])
            issues.extend(batch)
            if len(batch) < 50 or len(issues) >= limit:
                break
            startAt += 50
        return issues[:limit]

# --- Transform ---

def to_kb_markdown(issue: Dict[str, Any], base_url: str) -> str:
    key = issue.get('key')
    fields = issue.get('fields', {})
    summary = fields.get('summary', '')
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
        'title': f'{key} – {summary}',
        'source': {
            'type': 'jira',
            'url': f"{base_url.rstrip('/')}/browse/{key}",
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

    desc_text = description if isinstance(description, str) else json.dumps(description)[:1000]
    if summary and desc_text and desc_text != summary:
        body = summary + "\n\n" + desc_text
    else:
        body = summary or desc_text or ""

    parts.append("## Outcome (from JIRA)\n" + body + "\n")

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
    args = p.parse_args()

    base = (args.base or "").strip()
    if not (base.startswith('http://') or base.startswith('https://')):
        print(
            f"Error: --base must be a full URL like https://your-domain.atlassian.net; got: {args.base!r}",
            file=sys.stderr
        )
        sys.exit(2)

    if ':' not in args.auth:
        print('Invalid --auth format; expected email:token', file=sys.stderr)
        sys.exit(2)

    email, token = args.auth.split(':', 1)
    session = requests.Session()
    session.auth = (email, token)

    issues = fetch_jira_issues(base, (email, token), args.jql, args.limit)
    os.makedirs(args.out, exist_ok=True)

    for it in issues:
        key = it.get('key')
        summary = (it.get('fields') or {}).get('summary', '')
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
        else:
            print('UNCHANGED', outpath)

if __name__ == '__main__':
    main()
