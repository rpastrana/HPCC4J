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
    
    print(f"[DEBUG] Starting copilot with pexpect (PTY simulation)...")
    print(f"[DEBUG] Prompt: {prompt[:150]}...")
    print(f"[DEBUG] Output will be written to: {output_file}")
    
    try:
        # Spawn copilot with PTY and the -p flag to provide prompt directly
        # Use --allow-all-tools to pre-approve all tool usage (no interactive prompts)
        child = pexpect.spawn('copilot', ['-p', prompt, '--allow-all-tools'], 
                            env=copilot_env, timeout=120)
        child.logfile = sys.stdout.buffer  # Log output for debugging
        
        # Wait for copilot to initialize and show prompt
        # Copilot shows a welcome banner first, then waits for input
        print("[DEBUG] Waiting for copilot to process prompt...")
        
        # Continuously read output until process completes
        all_output = []
        while True:
            try:
                # Read with short timeout to get incremental output
                index = child.expect([pexpect.TIMEOUT, pexpect.EOF], timeout=2)
                
                # Capture whatever came before the match
                if child.before:
                    chunk = child.before.decode('utf-8', errors='ignore')
                    if chunk:
                        all_output.append(chunk)
                        print(f"[DEBUG] Captured {len(chunk)} chars...")
                
                # If we hit EOF, process is done
                if index == 1:
                    print("[DEBUG] Copilot process completed (EOF)")
                    break
                    
            except Exception as e:
                print(f"[DEBUG] Read exception: {e}")
                break
        
        # Also try to get any remaining output
        try:
            remaining = child.read()
            if remaining:
                all_output.append(remaining.decode('utf-8', errors='ignore'))
                print(f"[DEBUG] Captured remaining {len(remaining)} bytes")
        except:
            pass
        
        # Combine all output
        full_output = ''.join(all_output)
        
        # Strip ANSI escape sequences to get readable text
        import re
        ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
        clean_output = ansi_escape.sub('', full_output)
        
        print("\n" + "=" * 60)
        print(f"FULL COPILOT OUTPUT ({len(full_output)} chars total):")
        print("=" * 60)
        print(full_output)
        print("=" * 60)
        
        print("\n" + "=" * 60)
        print("CLEANED COPILOT RESPONSE (without ANSI codes):")
        print("=" * 60)
        print(clean_output if clean_output.strip() else "(no response captured)")
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
        else:
            print("[WARNING] Output file was not created by copilot")
        
        # Try to close gracefully
        try:
            child.close()
        except:
            pass
        
        return 0
            
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
