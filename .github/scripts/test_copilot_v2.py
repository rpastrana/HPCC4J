#!/usr/bin/env python3
"""
GitHub Copilot CLI Test Script - Using pexpect for TTY simulation
Tests Copilot CLI in non-interactive mode by simulating a TTY.
"""

import os
import sys
import subprocess

def main():
    print("=== GitHub Copilot CLI Test Script (v2 - PTY approach) ===")
    
    # Get environment variables
    issue_number = os.environ.get('ISSUE_NUMBER', 'unknown')
    issue_title = os.environ.get('ISSUE_TITLE', 'No title')
    issue_body = os.environ.get('ISSUE_BODY', 'No description')
    copilot_pat = os.environ.get('COPILOT_PAT', '')
    
    print(f"[DEBUG] Processing issue #{issue_number}: {issue_title}")
    
    if not copilot_pat:
        print("[ERROR] COPILOT_PAT not set")
        return 1
    
    # Install pexpect
    print("[DEBUG] Installing pexpect for TTY simulation...")
    subprocess.run([sys.executable, '-m', 'pip', 'install', 'pexpect'], check=False)
    
    try:
        import pexpect
    except ImportError:
        print("[ERROR] Failed to import pexpect")
        return 1
    
    # Authenticate gh CLI first
    print("[DEBUG] Authenticating GitHub CLI...")
    gh_env = os.environ.copy()
    gh_env.pop('GITHUB_TOKEN', None)
    gh_env.pop('GH_TOKEN', None)
    
    try:
        gh_auth = subprocess.run(
            ['gh', 'auth', 'login', '--with-token'],
            input=copilot_pat,
            capture_output=True,
            text=True,
            env=gh_env,
            timeout=30
        )
        if gh_auth.returncode != 0:
            print(f"[ERROR] gh auth failed: {gh_auth.stderr}")
            return 1
        print("[SUCCESS] gh CLI authenticated")
        
        # Switch account
        gh_status = subprocess.run(['gh', 'auth', 'status'], capture_output=True, text=True)
        import re
        accounts = [m.group(1) for line in gh_status.stdout.split('\n') 
                   if (m := re.search(r'account\s+(\S+)', line)) and m.group(1) != 'github-actions[bot]']
        
        if accounts:
            subprocess.run(['gh', 'auth', 'switch', '--user', accounts[0]], env=gh_env, check=False)
            print(f"[SUCCESS] Switched to account: {accounts[0]}")
    
    except Exception as e:
        print(f"[ERROR] Authentication failed: {e}")
        return 1
    
    # Prepare copilot environment
    copilot_env = os.environ.copy()
    copilot_env['GITHUB_TOKEN'] = copilot_pat
    copilot_env['TERM'] = 'xterm-256color'  # Proper terminal type
    copilot_env['CI'] = 'true'
    
    # Create config
    config_dir = os.path.expanduser("~/.config/.copilot")
    os.makedirs(config_dir, exist_ok=True)
    config_file = os.path.join(config_dir, "config.json")
    
    import json
    with open(config_file, 'w') as f:
        json.dump({
            "telemetry": {"enabled": False}, 
            "stream": False,  # Disable streaming for complete output
            "banner": "never",  # Disable animated banner
            "parallel_tool_execution": True,  # Enable parallel tools
            "render_markdown": False  # Disable markdown rendering in terminal
        }, f)
    print(f"[DEBUG] Created config at {config_file}")
    
    # Prepare prompt
    output_file = f"/tmp/copilot_analysis_{issue_number}.md"
    prompt = f"Analyze GitHub issue #{issue_number}: {issue_title}. {issue_body[:300]}. Write your analysis to the file {output_file} in markdown format with sections for: Issue Type, Summary, Priority, and Recommendations."
    
    print(f"[DEBUG] Starting copilot with subprocess instead of pexpect...")
    print(f"[DEBUG] Prompt: {prompt[:150]}...")
    print(f"[DEBUG] Output will be written to: {output_file}")
    print(f"[DEBUG] Command: copilot -p '<prompt>' --allow-all-tools")
    
    try:
        # Use subprocess.run instead of pexpect - simpler and captures all output
        result = subprocess.run(
            ['copilot', '-p', prompt, '--allow-all-tools'],
            env=copilot_env,
            capture_output=True,
            text=True,
            timeout=120
        )
        
        print(f"[DEBUG] Process completed with exit code: {result.returncode}")
        
        full_output = result.stdout
        stderr_output = result.stderr
        
        # Strip ANSI escape sequences to get readable text
        import re
        ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
        clean_output = ansi_escape.sub('', full_output)
        clean_stderr = ansi_escape.sub('', stderr_output)
        
        print("\n" + "=" * 60)
        print(f"STDOUT ({len(full_output)} chars):")
        print("=" * 60)
        print(full_output if full_output else "(empty)")
        print("=" * 60)
        
        print("\n" + "=" * 60)
        print(f"STDERR ({len(stderr_output)} chars):")
        print("=" * 60)
        print(stderr_output if stderr_output else "(empty)")
        print("=" * 60)
        
        print("\n" + "=" * 60)
        print("CLEANED OUTPUT (without ANSI codes):")
        print("=" * 60)
        print(clean_output if clean_output.strip() else "(no output)")
        print("=" * 60)
        
        # Check if copilot wrote the file
        import time
        time.sleep(2)  # Give filesystem a moment to sync
        print(f"\n[DEBUG] Checking for output file: {output_file}")
        if os.path.exists(output_file):
            print("[SUCCESS] Copilot created the output file!")
            with open(output_file, 'r') as f:
                file_content = f.read()
            print("\n" + "=" * 60)
            print("COPILOT ANALYSIS FROM FILE:")
            print("=" * 60)
            print(file_content)
            print("=" * 60)
            return 0
        else:
            print("[WARNING] Output file was not created by copilot")
            return 1
            
    except subprocess.TimeoutExpired:
        print("[ERROR] Copilot command timed out after 120 seconds")
        return 1
    except Exception as e:
        print(f"[ERROR] Failed to run copilot: {e}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    sys.exit(main())
