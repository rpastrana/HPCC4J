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
    prompt = f"Analyze GitHub issue #{issue_number}: {issue_title}. {issue_body[:300]}"
    
    print(f"[DEBUG] Starting copilot with pexpect (TTY simulation)...")
    print(f"[DEBUG] Prompt: {prompt[:100]}...")
    
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
            # Copilot will show thinking indicators, then generate response
            print("[DEBUG] Waiting for AI response (this may take 30-60 seconds)...")
            
            # Give copilot plenty of time to generate the response
            time.sleep(5)  # Initial wait for processing to start
            
            # Now wait for output to stabilize (no new output for a few seconds)
            output_buffer = []
            stable_count = 0
            max_wait = 60  # Maximum 60 seconds total wait
            start_wait = time.time()
            
            while time.time() - start_wait < max_wait:
                try:
                    # Try to read with short timeout
                    child.expect([pexpect.TIMEOUT], timeout=2)
                    if child.before:
                        new_data = child.before.decode('utf-8', errors='ignore')
                        if new_data.strip():
                            output_buffer.append(new_data)
                            stable_count = 0  # Reset stability counter
                            print(f"[DEBUG] Received {len(new_data)} chars...")
                        else:
                            stable_count += 1
                    else:
                        stable_count += 1
                    
                    # If output has been stable for 3 cycles (6 seconds), we're done
                    if stable_count >= 3:
                        print("[DEBUG] Output appears complete")
                        break
                        
                except Exception as e:
                    print(f"[DEBUG] Read exception: {e}")
                    break
            
            # Combine all output
            full_output = ''.join(output_buffer)
            
            print("\n" + "=" * 60)
            print("COPILOT RESPONSE:")
            print("=" * 60)
            print(full_output if full_output.strip() else "(no response captured)")
            print("=" * 60)
            
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
