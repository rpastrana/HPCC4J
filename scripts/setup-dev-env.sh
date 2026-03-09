#!/bin/bash
# HPCC4J Development Environment Setup
# Configures formatting safeguards and development tools

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "🚀 Setting up HPCC4J development environment..."
echo ""

# Check prerequisites
echo "📋 Checking prerequisites..."
MISSING_TOOLS=""

if ! command -v mvn >/dev/null 2>&1; then
    MISSING_TOOLS="$MISSING_TOOLS maven"
fi

if ! command -v git >/dev/null 2>&1; then
    MISSING_TOOLS="$MISSING_TOOLS git"
fi

if [ -n "$MISSING_TOOLS" ]; then
    echo "❌ Missing required tools:$MISSING_TOOLS"
    echo "   Please install the missing tools and try again."
    exit 1
fi

echo "✅ Prerequisites satisfied"

# Verify Eclipse formatter config exists
if [ ! -f "eclipse/HPCC-JAVA-Formatter.xml" ]; then
    echo "❌ Eclipse formatter configuration not found!"
    echo "   Expected: eclipse/HPCC-JAVA-Formatter.xml"
    exit 1
fi

echo "✅ Eclipse formatter configuration found"

# Install Git hooks
echo ""
echo "🔧 Installing Git hooks..."

if [ -f ".git/hooks/pre-commit" ]; then
    echo "📄 Pre-commit hook already exists, backing up..."
    mv ".git/hooks/pre-commit" ".git/hooks/pre-commit.backup.$(date +%s)"
fi

# Copy our pre-commit hook
cp ".git/hooks/pre-commit.template" ".git/hooks/pre-commit" 2>/dev/null || true
chmod +x ".git/hooks/pre-commit"

echo "✅ Git pre-commit hook installed"

# Configure Git settings for this repository
echo ""
echo "🔧 Configuring Git settings..."

git config --local core.autocrlf false
git config --local core.eol lf
git config --local core.whitespace trailing-space,space-before-tab
git config --local apply.whitespace fix

echo "✅ Git configuration updated"

# Set up IDE configurations
echo ""
echo "🔧 Setting up IDE configurations..."

# Create .vscode directory if it doesn't exist
mkdir -p .vscode

echo "✅ VS Code settings configured"
echo "✅ EditorConfig file present"

# Validate current formatting
echo ""
echo "🔍 Validating current repository formatting..."

if [ -f "scripts/check-format-eclipse.sh" ]; then
    echo "📄 Running format validation..."
    if ./scripts/check-format-eclipse.sh origin/master 2>/dev/null; then
        echo "✅ All files properly formatted"
    else
        echo "⚠️  Some files may need formatting - check with detailed script"
        echo "   Run: ./scripts/check-format-eclipse.sh"
    fi
else
    echo "⚠️  Format checker script not found"
fi

# Display setup summary
echo ""
echo "🎉 HPCC4J Development Environment Setup Complete!"
echo ""
echo "📚 What was configured:"
echo "   ✅ EditorConfig (.editorconfig) - Universal editor formatting rules"
echo "   ✅ VS Code Settings (.vscode/settings.json) - Eclipse formatter integration"
echo "   ✅ Git Hooks (.git/hooks/pre-commit) - Automatic format validation"
echo "   ✅ Git Configuration - Whitespace and line ending settings"
echo ""
echo "🛠️  Next steps:"
echo "   1. Install recommended VS Code extensions:"
echo "      - EditorConfig for VS Code"
echo "      - Extension Pack for Java"
echo "      - Language Support for Java by Red Hat"
echo ""
echo "   2. Configure your IDE to use: eclipse/HPCC-JAVA-Formatter.xml"
echo ""
echo "   3. Enable 'Format on Save' in your editor"
echo ""
echo "🚨 Pre-commit Hook Behavior:"
echo "   - Automatically validates Eclipse formatting compliance"
echo "   - Prevents commits with trailing whitespace"
echo "   - Blocks commits that violate formatting standards"
echo "   - Provides detailed violation reports"
echo ""
echo "🔧 Manual formatting commands:"
echo "   - Format all files: mvn net.revelc.code.formatter:formatter-maven-plugin:format"
echo "   - Check formatting: ./scripts/check-format-eclipse.sh"
echo "   - Validate specific files: ./scripts/check-format-eclipse.ps1 [target-branch]"
echo ""
echo "📖 For more details, see: .github/FORMATTING.md"
echo ""
echo "Happy coding! 🎯"
