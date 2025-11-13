#!/usr/bin/env python3
"""
Simple GitHub Copilot CLI Test Script
Tests basic Copilot CLI functionality when an issue is created or edited.
"""

import os
import subprocess
import sys

def main():
    print("=== GitHub Copilot CLI Test Script ===")
    
    # Get environment variables
    issue_number = os.environ.get('ISSUE_NUMBER', 'unknown')
    issue_title = os.environ.get('ISSUE_TITLE', 'No title')
    issue_body = os.environ.get('ISSUE_BODY', 'No description')
    issue_author = os.environ.get('ISSUE_AUTHOR', 'unknown')
    copilot_pat = os.environ.get('COPILOT_PAT', '')
    
    print(f"[DEBUG] Processing issue #{issue_number}")
    print(f"[DEBUG] Title: {issue_title}")
    print(f"[DEBUG] Author: {issue_author}")
    print(f"[DEBUG] Body length: {len(issue_body)} characters")
    
    # Verify COPILOT_PAT is set
    if not copilot_pat:
        print("[ERROR] COPILOT_PAT environment variable is not set")
        print("[ERROR] Cannot authenticate with GitHub Copilot CLI")
        return 1
    
    print(f"[DEBUG] COPILOT_PAT is set (length: {len(copilot_pat)} characters)")
    
    # Check if copilot CLI is available
    print("[DEBUG] Checking if copilot CLI is available...")
    try:
        result = subprocess.run(
            ['which', 'copilot'],
            capture_output=True,
            text=True,
            timeout=5
        )
        if result.returncode == 0:
            copilot_path = result.stdout.strip()
            print(f"[DEBUG] Copilot CLI found at: {copilot_path}")
        else:
            print("[ERROR] Copilot CLI not found in PATH")
            return 1
    except Exception as e:
        print(f"[ERROR] Failed to check copilot CLI: {e}")
        return 1
    
    # Check copilot version
    print("[DEBUG] Checking copilot version...")
    try:
        result = subprocess.run(
            ['copilot', '--version'],
            capture_output=True,
            text=True,
            timeout=5
        )
        if result.returncode == 0:
            print(f"[DEBUG] Copilot version: {result.stdout.strip()}")
        else:
            print(f"[WARNING] Could not get copilot version: {result.stderr}")
    except Exception as e:
        print(f"[WARNING] Failed to get copilot version: {e}")
    
    # Prepare simple prompt
    prompt = f"""Analyze this GitHub issue and provide a brief summary:

Issue #{issue_number}: {issue_title}
Author: {issue_author}

Description:
{issue_body[:500]}

Please provide:
1. Issue type (bug/feature/question)
2. One-sentence summary
3. Priority assessment (high/medium/low)"""
    
    print(f"[DEBUG] Prepared prompt with {len(prompt)} characters")
    print("[DEBUG] Prompt preview:")
    print("-" * 40)
    print(prompt[:200] + "..." if len(prompt) > 200 else prompt)
    print("-" * 40)
    
    # Execute copilot CLI
    print("[DEBUG] Executing copilot CLI command...")
    
    env = os.environ.copy()
    env['GITHUB_TOKEN'] = copilot_pat
    env['GH_TOKEN'] = copilot_pat
    env['COPILOT_GITHUB_TOKEN'] = copilot_pat
    
    print(f"[DEBUG] Set authentication tokens in environment")
    print(f"[DEBUG] GITHUB_TOKEN length: {len(env.get('GITHUB_TOKEN', ''))}")
    print(f"[DEBUG] GH_TOKEN length: {len(env.get('GH_TOKEN', ''))}")
    print(f"[DEBUG] COPILOT_GITHUB_TOKEN length: {len(env.get('COPILOT_GITHUB_TOKEN', ''))}")
    print(f"[DEBUG] Token starts with: {copilot_pat[:7]}..." if len(copilot_pat) > 7 else "[DEBUG] Token too short")
    
    # Test if token format is valid (should start with ghp_, gho_, or github_pat_)
    if not (copilot_pat.startswith('ghp_') or copilot_pat.startswith('gho_') or 
            copilot_pat.startswith('github_pat_') or copilot_pat.startswith('ghs_')):
        print(f"[WARNING] Token may have invalid format. Expected to start with ghp_, gho_, ghs_, or github_pat_")
    
    print("[DEBUG] Environment check:")
    for key in ['GITHUB_TOKEN', 'GH_TOKEN', 'COPILOT_GITHUB_TOKEN']:
        print(f"[DEBUG]   {key} = {env.get(key, 'NOT SET')[:20]}..." if env.get(key) else f"[DEBUG]   {key} = NOT SET")
    
    # Authenticate GitHub CLI with COPILOT_PAT token
    print("[DEBUG] Authenticating GitHub CLI with COPILOT_PAT...")
    print(f"[DEBUG] Token to use: {copilot_pat[:20]}...")
    print(f"[DEBUG] Token length: {len(copilot_pat)}")
    
    # Create a clean environment for gh auth login
    # Remove GitHub Actions token so it doesn't interfere
    gh_env = os.environ.copy()
    gh_env.pop('GITHUB_TOKEN', None)  # Remove GitHub Actions token
    gh_env.pop('GH_TOKEN', None)
    
    print("[DEBUG] Removed GITHUB_TOKEN from environment for gh auth")
    
    try:
        # Use gh auth login with COPILOT_PAT token from stdin
        # This will authenticate gh CLI which copilot can then use
        print(f"[DEBUG] Calling: gh auth login --with-token")
        print(f"[DEBUG] Stdin will receive: {copilot_pat[:10]}...")
        
        gh_auth = subprocess.run(
            ['gh', 'auth', 'login', '--with-token'],
            input=copilot_pat,
            capture_output=True,
            text=True,
            env=gh_env,  # Use clean environment
            timeout=30
        )
        
        if gh_auth.returncode == 0:
            print("[SUCCESS] GitHub CLI authenticated successfully with COPILOT_PAT")
            if gh_auth.stdout:
                print(f"[DEBUG] gh auth stdout: {gh_auth.stdout}")
            if gh_auth.stderr:
                print(f"[DEBUG] gh auth stderr: {gh_auth.stderr}")
        else:
            print(f"[ERROR] GitHub CLI authentication failed")
            print(f"[ERROR] Return code: {gh_auth.returncode}")
            print(f"[ERROR] stdout: {gh_auth.stdout}")
            print(f"[ERROR] stderr: {gh_auth.stderr}")
            return 1
        
        # Verify gh authentication status
        print("[DEBUG] Verifying gh authentication...")
        gh_status = subprocess.run(
            ['gh', 'auth', 'status'],
            capture_output=True,
            text=True,
            timeout=10
        )
        print("[DEBUG] gh auth status:")
        print(gh_status.stdout)
        if gh_status.stderr:
            print(f"[DEBUG] gh auth status stderr: {gh_status.stderr}")
        
        # Switch to the authenticated account (not github-actions[bot])
        print("[DEBUG] Switching to authenticated account...")
        
        # Parse the username from gh auth status output
        # Look for lines like "✓ Logged in to github.com account USERNAME"
        try:
            import re
            
            # Find all logged-in accounts from the status output
            auth_status_text = gh_status.stdout
            
            # Parse accounts - look for pattern: "account USERNAME"
            # Exclude github-actions[bot]
            accounts = []
            for line in auth_status_text.split('\n'):
                match = re.search(r'account\s+(\S+)', line)
                if match:
                    account = match.group(1)
                    if account != 'github-actions[bot]':
                        accounts.append(account)
                        print(f"[DEBUG] Found account: {account}")
            
            if accounts:
                # Use the first non-bot account
                username = accounts[0]
                print(f"[DEBUG] Will switch to account: {username}")
                
                # Create clean environment without GITHUB_TOKEN for the switch
                switch_env = os.environ.copy()
                switch_env.pop('GITHUB_TOKEN', None)
                switch_env.pop('GH_TOKEN', None)
                
                print("[DEBUG] Removed GITHUB_TOKEN for gh auth switch")
                
                # Switch to this account
                gh_switch = subprocess.run(
                    ['gh', 'auth', 'switch', '--user', username],
                    capture_output=True,
                    text=True,
                    env=switch_env,  # Use clean environment
                    timeout=10
                )
                
                if gh_switch.returncode == 0:
                    print(f"[SUCCESS] Switched active account to: {username}")
                    
                    # Verify the switch worked - use clean environment here too
                    gh_status2 = subprocess.run(
                        ['gh', 'auth', 'status'],
                        capture_output=True,
                        text=True,
                        env=switch_env,  # Use same clean environment
                        timeout=10
                    )
                    print("[DEBUG] Updated gh auth status:")
                    print(gh_status2.stdout)
                    if gh_status2.stderr:
                        print(f"[DEBUG] stderr: {gh_status2.stderr}")
                else:
                    print(f"[ERROR] Could not switch account")
                    print(f"[ERROR] Return code: {gh_switch.returncode}")
                    print(f"[ERROR] stdout: {gh_switch.stdout}")
                    print(f"[ERROR] stderr: {gh_switch.stderr}")
                    return 1
            else:
                print("[ERROR] Could not find non-bot account in gh auth status")
                print("[ERROR] Cannot proceed without switching accounts")
                return 1
                
        except Exception as e:
            print(f"[ERROR] Exception during account switch: {e}")
            import traceback
            traceback.print_exc()
            return 1
            
    except Exception as e:
        print(f"[ERROR] Exception during gh authentication: {e}")
        import traceback
        traceback.print_exc()
        return 1
    
    # Now run copilot - it should use the authenticated gh CLI
    print("[DEBUG] Running copilot command...")
    
    # Copilot needs GITHUB_TOKEN in env, but we'll use the PAT token
    # since gh auth switch worked
    copilot_env = os.environ.copy()
    # Remove the GitHub Actions token
    copilot_env.pop('GITHUB_TOKEN', None)
    copilot_env.pop('GH_TOKEN', None)
    # Set our PAT token for copilot
    copilot_env['GITHUB_TOKEN'] = copilot_pat
    
    print("[DEBUG] Set GITHUB_TOKEN to COPILOT_PAT for copilot CLI")
    print(f"[DEBUG] Token in env: {copilot_env['GITHUB_TOKEN'][:20]}...")
    
    # First try a simple test
    print("[DEBUG] Testing copilot with simple prompt...")
    try:
        test_result = subprocess.run(
            ['copilot', '--version'],
            capture_output=True,
            text=True,
            env=copilot_env,
            timeout=10
        )
        print(f"[DEBUG] Copilot version check: rc={test_result.returncode}")
        print(f"[DEBUG] Version stdout: {test_result.stdout}")
        print(f"[DEBUG] Version stderr: {test_result.stderr}")
    except Exception as e:
        print(f"[WARNING] Version check failed: {e}")
    
    # Try copilot with the actual prompt
    print(f"[DEBUG] Running: copilot -p '<prompt of length {len(prompt)}>' --allow-all-tools")
    print("[DEBUG] Note: --allow-all-tools is required for non-interactive mode")
    
    try:
        # Use -p flag with --allow-all-tools for non-interactive mode
        result = subprocess.run(
            ['copilot', '-p', prompt, '--allow-all-tools'],
            capture_output=True,
            text=True,
            env=copilot_env,
            timeout=60
        )
        
        print(f"[DEBUG] Copilot command completed with return code: {result.returncode}")
        
        if result.returncode == 0:
            print("[SUCCESS] Copilot response received")
            print("=" * 60)
            print("COPILOT RESPONSE:")
            print("=" * 60)
            print(result.stdout)
            print("=" * 60)
            
            if result.stderr:
                print("[DEBUG] Copilot stderr output:")
                print(result.stderr)
            
            return 0
        else:
            print(f"[ERROR] Copilot command failed with return code: {result.returncode}")
            print(f"[ERROR] stdout: '{result.stdout}' (length: {len(result.stdout)})")
            print(f"[ERROR] stderr: '{result.stderr}' (length: {len(result.stderr)})")
            
            # Try to get more info about what happened
            if not result.stdout and not result.stderr:
                print("[DIAGNOSTIC] Command produced no output - may have failed to execute")
                print("[DIAGNOSTIC] Check if copilot binary exists and is executable")
            
            # Common error diagnostics
            if "authentication" in result.stderr.lower() or "auth" in result.stderr.lower():
                print("[DIAGNOSTIC] Authentication issue detected")
                print("[DIAGNOSTIC] Verify that COPILOT_PAT is a valid GitHub token with Copilot access")
            
            if "not found" in result.stderr.lower():
                print("[DIAGNOSTIC] Command not found issue detected")
                print("[DIAGNOSTIC] Verify copilot CLI installation")
            
            if "timeout" in result.stderr.lower():
                print("[DIAGNOSTIC] Timeout issue detected")
                print("[DIAGNOSTIC] Copilot service may be slow or unavailable")
            
            return 1
            
    except subprocess.TimeoutExpired:
        print("[ERROR] Copilot command timed out after 60 seconds")
        print("[DIAGNOSTIC] This may indicate network issues or service unavailability")
        return 1
    except FileNotFoundError:
        print("[ERROR] Copilot command not found")
        print("[DIAGNOSTIC] Ensure 'copilot' is installed and in PATH")
        return 1
    except Exception as e:
        print(f"[ERROR] Unexpected error running copilot: {e}")
        print(f"[ERROR] Exception type: {type(e).__name__}")
        import traceback
        print("[DEBUG] Full traceback:")
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    try:
        exit_code = main()
        print(f"[DEBUG] Script exiting with code: {exit_code}")
        sys.exit(exit_code)
    except Exception as e:
        print(f"[FATAL] Unhandled exception in main: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
