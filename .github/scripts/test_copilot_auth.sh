#!/bin/bash
# Test script to verify Copilot PAT is working

echo "Testing Copilot PAT authentication..."

# Check if COPILOT_PAT is set (when running locally, you'll need to export it)
if [ -z "$COPILOT_PAT" ]; then
    echo "❌ COPILOT_PAT environment variable not set"
    echo "For local testing, run: export COPILOT_PAT='your-token-here'"
    echo ""
    echo "✅ In GitHub Actions, the secret is already configured as 'COPILOT_PAT'"
    exit 0
fi

# Authenticate with GitHub CLI
echo "$COPILOT_PAT" | gh auth login --with-token

# Try to install Copilot extension
echo ""
echo "Installing Copilot CLI extension..."
gh extension install github/gh-copilot 2>&1 | head -n 5

# Test if Copilot is available
echo ""
echo "Testing Copilot CLI access..."
gh copilot explain "What is HPCC4J?" 2>&1 | head -n 10

echo ""
echo "If you see a Copilot response above, it's working! 🎉"
echo "If you see an error, the PAT might not have the necessary permissions or Copilot subscription."
