#!/usr/bin/env python3
"""
Build the Copilot analysis prompt by combining template with issue data.
"""
import sys

def build_prompt(template_file, title_file, body_file):
    """Read template and issue data, substitute placeholders."""
    with open(template_file, 'r') as f:
        template = f.read()
    
    with open(title_file, 'r') as f:
        title = f.read()
    
    with open(body_file, 'r') as f:
        body = f.read()
    
    # Replace placeholders
    prompt = template.replace('{ISSUE_TITLE}', title).replace('{ISSUE_BODY}', body)
    
    return prompt

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: build_prompt.py <template_file> <title_file> <body_file>", file=sys.stderr)
        sys.exit(1)
    
    prompt = build_prompt(sys.argv[1], sys.argv[2], sys.argv[3])
    print(prompt)
