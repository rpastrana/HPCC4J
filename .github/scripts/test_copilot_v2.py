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
            json.dump({
                "telemetry": {"enabled": False}, 
                "stream": True,
                "banner": "never"  # Disable animated banner
            }, f)
    
    # Prepare prompt
    output_file = f"/tmp/copilot_analysis_{issue_number}.md"
    prompt = f"Analyze GitHub issue #{issue_number}: {issue_title}. {issue_body[:300]}. Write your analysis to the file {output_file} in markdown format with sections for: Issue Type, Summary, Priority, and Recommendations."
    
    print(f"[DEBUG] Starting copilot with pexpect (TTY simulation)...")
    print(f"[DEBUG] Prompt: {prompt[:150]}...")
    print(f"[DEBUG] Output will be written to: {output_file}")
    
    try:
        # Spawn copilot with PTY
        child = pexpect.spawn('copilot', env=copilot_env, timeout=60)
        child.logfile = sys.stdout.buffer  # Log output for debugging
        
        # Wait for copilot to initialize and show prompt
        # Copilot shows a welcome banner first, then waits for input
        print("[DEBUG] Waiting for copilot to initialize...")
        
        # Look for various possible prompts or indicators that copilot is ready
        # The banner shows "● Connected to GitHub MCP Server" when ready
        index = child.expect([
            'Connected to GitHub MCP Server',  # 0: Ready indicator
            'check for mistakes',               # 1: End of welcome message
            'error',                            # 2: Error
            pexpect.TIMEOUT,                    # 3: Timeout
            pexpect.EOF                         # 4: EOF
        ], timeout=30)
        
        if index in [0, 1]:
            print(f"[DEBUG] Copilot initialized (matched pattern index={index})")
            
            # Wait a moment for the prompt to appear
            import time
            time.sleep(2)
            
            # Now send the prompt
            print("[DEBUG] Sending prompt...")
            child.sendline(prompt)
            
            # Wait for the AI to generate a response
            print("[DEBUG] Waiting for AI response (this may take 30-60 seconds)...")
            
            # Give copilot time to fully generate and render the response
            time.sleep(45)  # Wait for AI generation to complete
            
            # Send Ctrl+L to trigger a screen refresh/redraw
            child.sendcontrol('l')
            time.sleep(1)
            
            # Now capture everything visible on the terminal screen
            # Send a newline to get current screen state
            child.sendline('')
            time.sleep(2)
            
            # Try to get the terminal's screen contents
            # Read whatever is available
            try:
                child.expect([pexpect.TIMEOUT], timeout=3)
            except:
                pass
            
            # Get the raw output including all escape sequences
            raw_output = child.before.decode('utf-8', errors='ignore') if child.before else ""
            
            # Also try reading from child directly
            try:
                remaining = child.read_nonblocking(size=8192, timeout=1)
                raw_output += remaining.decode('utf-8', errors='ignore')
            except:
                pass
            
            # Strip ANSI escape sequences to get readable text
            import re
            ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
            clean_output = ansi_escape.sub('', raw_output)
            
            print("\n" + "=" * 60)
            print("RAW COPILOT OUTPUT (first 2000 chars):")
            print("=" * 60)
            print(raw_output[:2000])
            print("=" * 60)
            
            print("\n" + "=" * 60)
            print("CLEANED COPILOT RESPONSE:")
            print("=" * 60)
            print(clean_output if clean_output.strip() else "(no response captured)")
            print("=" * 60)
            
            # Check if copilot wrote the file
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
            else:
                print("[WARNING] Output file was not created by copilot")
            
            # Try to exit gracefully
            try:
                child.sendcontrol('d')  # Send Ctrl+D to exit
                time.sleep(1)
            except:
                pass
            
            child.close()
            return 0
        else:
            print(f"[ERROR] Unexpected response from copilot (index={index})")
            print(f"[ERROR] Before: {child.before}")
            if child.after:
                print(f"[ERROR] After: {child.after}")
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
