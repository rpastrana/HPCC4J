#!/usr/bin/env python3
"""
HPCC4J Issue Analyzer
Analyzes GitHub issues to determine if sufficient information is provided.
"""

import re
import sys
import json

def check_version_format(version_str, component):
    """Check if a version string is valid."""
    if not version_str:
        return False, "Not provided"
    
    # Common version patterns: X.Y.Z, X.Y, vX.Y.Z
    pattern = r'v?\d+\.\d+(\.\d+)?'
    if re.search(pattern, version_str):
        # Extract version number
        match = re.search(r'(\d+)\.\d+', version_str)
        if match:
            major = int(match.group(1))
            if component == "hpcc" and major in [7, 8, 9, 10]:
                return True, version_str
            elif component == "hpcc4j" and major >= 7:
                return True, version_str
            elif component == "java" and major >= 8:
                return True, version_str
        return True, version_str
    return False, "Invalid format"

def analyze_issue(title, body):
    """Analyze the issue and return structured feedback."""
    
    title_lower = title.lower()
    body_lower = body.lower() if body else ""
    
    analysis = {
        "assessment": "NEEDS_MORE_INFO",
        "summary": "",
        "identified_info": {},
        "missing_info": [],
        "validation_issues": [],
        "recommended_questions": [],
        "suggested_labels": [],
        "priority": "Medium"
    }
    
    # Issue type classification
    if any(word in title_lower for word in ["bug", "error", "fail", "broken", "issue", "problem"]):
        issue_type = "bug"
    elif any(word in title_lower + body_lower for word in ["feature", "enhancement", "request", "add", "support"]):
        issue_type = "enhancement"
    elif any(word in title_lower for word in ["question", "how to", "help"]):
        issue_type = "question"
    else:
        issue_type = "unclear"
    
    analysis["identified_info"]["issue_type"] = issue_type
    analysis["suggested_labels"].append(issue_type if issue_type != "unclear" else "needs-clarification")
    
    # Module identification
    modules = {
        "wsclient": ["wsclient", "ws client", "web service", "hpccwsclient", "connection"],
        "dfsclient": ["dfsclient", "dfs client", "file system", "direct file"],
        "spark-hpcc": ["spark", "dataframe", "rdd", "pyspark"],
        "clienttools": ["clienttools", "eclcc", "client tools"],
        "rdf2hpcc": ["rdf2hpcc", "rdf"],
        "commons": ["commons-hpcc", "commons"]
    }
    
    identified_module = "unclear"
    for module, keywords in modules.items():
        if any(keyword in body_lower for keyword in keywords):
            identified_module = module
            analysis["suggested_labels"].append(module)
            break
    
    analysis["identified_info"]["affected_module"] = identified_module
    
    # Version detection
    hpcc4j_version = "NOT PROVIDED"
    hpcc_version = "NOT PROVIDED"
    java_version = "NOT PROVIDED"
    
    # HPCC4J version patterns
    hpcc4j_patterns = [
        r'hpcc4j[:\s]+v?(\d+\.\d+\.\d+)',
        r'version[:\s]+v?(\d+\.\d+\.\d+)',
        r'<version>(\d+\.\d+\.\d+)</version>',
        r'wsclient.*?(\d+\.\d+\.\d+)'
    ]
    
    for pattern in hpcc4j_patterns:
        match = re.search(pattern, body_lower)
        if match:
            hpcc4j_version = match.group(1)
            break
    
    # HPCC platform version patterns
    hpcc_patterns = [
        r'hpcc[:\s]+v?(\d+\.\d+\.\d+)',
        r'platform[:\s]+v?(\d+\.\d+\.\d+)',
        r'cluster[:\s]+v?(\d+\.\d+\.\d+)'
    ]
    
    for pattern in hpcc_patterns:
        match = re.search(pattern, body_lower)
        if match:
            hpcc_version = match.group(1)
            break
    
    # Java version patterns
    java_patterns = [
        r'java[:\s]+v?(\d+)',
        r'jdk[:\s]+v?(\d+)',
        r'java version[:\s]+"?(\d+)'
    ]
    
    for pattern in java_patterns:
        match = re.search(pattern, body_lower)
        if match:
            java_version = match.group(1)
            break
    
    analysis["identified_info"]["hpcc4j_version"] = hpcc4j_version
    analysis["identified_info"]["hpcc_platform_version"] = hpcc_version
    analysis["identified_info"]["java_version"] = java_version
    
    # Validate versions
    valid, msg = check_version_format(hpcc4j_version, "hpcc4j")
    if hpcc4j_version != "NOT PROVIDED" and not valid:
        analysis["validation_issues"].append(f"HPCC4J version format appears invalid: {msg}")
    
    valid, msg = check_version_format(hpcc_version, "hpcc")
    if hpcc_version != "NOT PROVIDED" and not valid:
        analysis["validation_issues"].append(f"HPCC platform version appears invalid: {msg}")
    
    # Check for reproduction steps
    has_repro = any(phrase in body_lower for phrase in [
        "steps to reproduce", "to reproduce", "reproduction", "how to reproduce",
        "1.", "2.", "3.", "step 1", "step 2"
    ])
    analysis["identified_info"]["has_reproduction_steps"] = "YES" if has_repro else "NO"
    
    # Check for error details
    has_errors = any(phrase in body_lower for phrase in [
        "error", "exception", "stack trace", "stacktrace", "caused by",
        "at org.hpccsystems", "failed", "threw"
    ])
    analysis["identified_info"]["has_error_details"] = "YES" if has_errors else "PARTIAL" if "error" in body_lower else "NO"
    
    # Check for connection details
    has_connection = any(phrase in body_lower for phrase in [
        "http://", "https://", "connection", "endpoint", ":8010", ":18010", "cluster"
    ])
    analysis["identified_info"]["has_connection_info"] = "YES" if has_connection else "NO"
    
    # Determine missing information
    score = 0
    max_score = 8
    
    if hpcc4j_version == "NOT PROVIDED":
        analysis["missing_info"].append("HPCC4J library version")
        analysis["recommended_questions"].append({
            "question": "What version of HPCC4J are you using?",
            "help": "Check your pom.xml dependency or run: `mvn dependency:tree | grep hpccsystems`"
        })
    else:
        score += 1
    
    if hpcc_version == "NOT PROVIDED":
        analysis["missing_info"].append("HPCC Systems platform version")
        analysis["recommended_questions"].append({
            "question": "What is your HPCC Systems platform version?",
            "help": "Found in ESP interface footer or run: `eclcc --version` on the HPCC server"
        })
    else:
        score += 1
    
    if java_version == "NOT PROVIDED":
        analysis["missing_info"].append("Java version")
        analysis["recommended_questions"].append({
            "question": "What Java version are you using?",
            "help": "Run: `java -version`"
        })
    else:
        score += 1
    
    if not has_repro and issue_type == "bug":
        analysis["missing_info"].append("Steps to reproduce the issue")
        analysis["recommended_questions"].append({
            "question": "Can you provide step-by-step instructions to reproduce this issue?",
            "help": "Include: 1) What you did, 2) What you expected, 3) What actually happened"
        })
    else:
        score += 1
    
    if analysis["identified_info"]["has_error_details"] == "NO" and issue_type == "bug":
        analysis["missing_info"].append("Error messages or stack traces")
        analysis["recommended_questions"].append({
            "question": "Can you provide the full error message and stack trace?",
            "help": "Copy the complete error from your console or log files. Enable debug logging if needed."
        })
    else:
        score += 1
    
    if not has_connection and "connection" in body_lower:
        analysis["missing_info"].append("HPCC cluster connection details")
        analysis["recommended_questions"].append({
            "question": "What is your HPCC cluster endpoint URL and target cluster name?",
            "help": "Example: http://mycluster:8010, target cluster: 'mythor'. Don't share passwords!"
        })
    else:
        score += 1
    
    if identified_module == "unclear":
        analysis["missing_info"].append("Which HPCC4J module is affected")
        analysis["recommended_questions"].append({
            "question": "Which HPCC4J module are you using?",
            "help": "Options: wsclient, dfsclient, spark-hpcc, clienttools, rdf2hpcc, commons-hpcc"
        })
    else:
        score += 1
    
    if not body or len(body.strip()) < 50:
        analysis["missing_info"].append("Detailed description of the issue")
        analysis["recommended_questions"].append({
            "question": "Can you provide more details about what you're trying to accomplish and what's going wrong?",
            "help": "A clear description helps us understand and resolve your issue faster."
        })
    else:
        score += 1
    
    # Determine assessment
    if score >= 7:
        analysis["assessment"] = "SUFFICIENT"
        analysis["summary"] = f"This {issue_type} report contains most of the necessary information for investigation."
    elif score >= 5:
        analysis["assessment"] = "MOSTLY_SUFFICIENT"
        analysis["summary"] = f"This {issue_type} report has good information but is missing a few details that would help with investigation."
        analysis["suggested_labels"].append("needs-more-info")
    else:
        analysis["assessment"] = "NEEDS_MORE_INFO"
        analysis["summary"] = f"This {issue_type} report needs additional information to be actionable."
        analysis["suggested_labels"].append("needs-more-info")
    
    # Priority assessment
    if "critical" in title_lower or "urgent" in title_lower or "production" in body_lower:
        analysis["priority"] = "High"
    elif has_errors and has_repro:
        analysis["priority"] = "Medium"
    else:
        analysis["priority"] = "Low"
    
    return analysis

def format_analysis(analysis):
    """Format the analysis as markdown."""
    output = []
    output.append("## 🤖 HPCC4J Issue Analysis")
    output.append("")
    output.append(f"### Assessment: {analysis['assessment']}")
    output.append("")
    output.append(f"**{analysis['summary']}**")
    output.append("")
    
    output.append("### Identified Information")
    for key, value in analysis['identified_info'].items():
        formatted_key = key.replace('_', ' ').title()
        output.append(f"- **{formatted_key}:** {value}")
    output.append("")
    
    if analysis['validation_issues']:
        output.append("### ⚠️ Validation Issues")
        for issue in analysis['validation_issues']:
            output.append(f"- {issue}")
        output.append("")
    
    if analysis['missing_info']:
        output.append("### Missing Information")
        for item in analysis['missing_info']:
            output.append(f"- {item}")
        output.append("")
    
    if analysis['recommended_questions']:
        output.append("### Recommended Questions for Reporter")
        output.append("")
        for i, q in enumerate(analysis['recommended_questions'], 1):
            output.append(f"{i}. **{q['question']}**")
            output.append(f"   - {q['help']}")
            output.append("")
    
    if analysis['suggested_labels']:
        output.append(f"### Suggested Labels")
        output.append(f"`{', '.join(set(analysis['suggested_labels']))}`")
        output.append("")
    
    output.append(f"**Priority:** {analysis['priority']}")
    output.append("")
    output.append("---")
    output.append("*This analysis was automatically generated. For the complete checklist, see `.github/copilot-issue-analysis-prompt.md`*")
    
    return "\n".join(output)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: analyze_issue.py <title> <body>")
        sys.exit(1)
    
    title = sys.argv[1]
    body = sys.argv[2] if len(sys.argv) > 2 else ""
    
    analysis = analyze_issue(title, body)
    markdown = format_analysis(analysis)
    
    print(markdown)
    
    # Output as JSON for workflow processing
    print("\n\n<!-- JSON_OUTPUT")
    print(json.dumps(analysis))
    print("-->")
