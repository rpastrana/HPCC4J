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
    if not os.path.exists(config_file):
        import json
        with open(config_file, 'w') as f:
            json.dump({"telemetry": {"enabled": False}, "stream": True}, f)
    
    # Prepare prompt
    prompt = f"Analyze GitHub issue #{issue_number}: {issue_title}. {issue_body[:300]}"
    
    print(f"[DEBUG] Starting copilot with pexpect (TTY simulation)...")
    print(f"[DEBUG] Prompt: {prompt[:100]}...")
    
    try:
        # Spawn copilot with PTY
        child = pexpect.spawn('copilot', env=copilot_env, timeout=60)
        child.logfile = sys.stdout.buffer  # Log output for debugging
        
        # Wait for copilot to initialize and show prompt
        print("[DEBUG] Waiting for copilot to initialize...")
        index = child.expect(['>', 'error', pexpect.TIMEOUT, pexpect.EOF], timeout=30)
        
        if index == 0:
            print("[DEBUG] Copilot ready, sending prompt...")
            child.sendline(prompt)
            
            # Wait for response
            print("[DEBUG] Waiting for response...")
            child.expect([pexpect.TIMEOUT, pexpect.EOF], timeout=60)
            
            # Get all output
            output = child.before.decode('utf-8', errors='ignore') if child.before else ""
            print("\n" + "=" * 60)
            print("COPILOT RESPONSE:")
            print("=" * 60)
            print(output)
            print("=" * 60)
            
            child.sendline('/exit')
            child.close()
            return 0
        else:
            print(f"[ERROR] Unexpected response from copilot (index={index})")
            print(f"[ERROR] Before: {child.before}")
            return 1
            
    except pexpect.exceptions.TIMEOUT:
        print("[ERROR] Timeout waiting for copilot")
        return 1
    except Exception as e:
        print(f"[ERROR] Failed to run copilot: {e}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    sys.exit(main())
