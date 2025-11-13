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
    
    try:
        result = subprocess.run(
            ['copilot', '-p', prompt],
            capture_output=True,
            text=True,
            env=env,
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
            print(f"[ERROR] stdout: {result.stdout}")
            print(f"[ERROR] stderr: {result.stderr}")
            
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
