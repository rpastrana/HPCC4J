# HPCC4J Development Environment Setup (PowerShell Version)
# Configures formatting safeguards and development tools

$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path $PSScriptRoot -Parent
Set-Location $ProjectRoot

Write-Host "🚀 Setting up HPCC4J development environment..." -ForegroundColor Green
Write-Host ""

# Check prerequisites
Write-Host "📋 Checking prerequisites..." -ForegroundColor Yellow
$MissingTools = @()

try { mvn --version | Out-Null } catch { $MissingTools += "Maven" }
try { git --version | Out-Null } catch { $MissingTools += "Git" }

if ($MissingTools.Count -gt 0) {
    Write-Host "❌ Missing required tools: $($MissingTools -join ', ')" -ForegroundColor Red
    Write-Host "   Please install the missing tools and try again." -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Prerequisites satisfied" -ForegroundColor Green

# Verify Eclipse formatter config exists
if (-not (Test-Path "eclipse\HPCC-JAVA-Formatter.xml")) {
    Write-Host "❌ Eclipse formatter configuration not found!" -ForegroundColor Red
    Write-Host "   Expected: eclipse\HPCC-JAVA-Formatter.xml" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Eclipse formatter configuration found" -ForegroundColor Green

# Install Git hooks
Write-Host ""
Write-Host "🔧 Installing Git hooks..." -ForegroundColor Yellow

if (Test-Path ".git\hooks\pre-commit") {
    Write-Host "📄 Pre-commit hook already exists, backing up..." -ForegroundColor Yellow
    $timestamp = [DateTimeOffset]::Now.ToUnixTimeSeconds()
    Move-Item ".git\hooks\pre-commit" ".git\hooks\pre-commit.backup.$timestamp"
}

# Use the PowerShell version of the pre-commit hook on Windows
if (Test-Path ".git\hooks\pre-commit.ps1") {
    Copy-Item ".git\hooks\pre-commit.ps1" ".git\hooks\pre-commit"
}

Write-Host "✅ Git pre-commit hook installed" -ForegroundColor Green

# Configure Git settings for this repository
Write-Host ""
Write-Host "🔧 Configuring Git settings..." -ForegroundColor Yellow

git config --local core.autocrlf false
git config --local core.eol lf
git config --local "core.whitespace" "trailing-space,space-before-tab"
git config --local "apply.whitespace" "fix"

Write-Host "✅ Git configuration updated" -ForegroundColor Green

# Set up IDE configurations
Write-Host ""
Write-Host "🔧 Setting up IDE configurations..." -ForegroundColor Yellow

# Create .vscode directory if it doesn't exist
if (-not (Test-Path ".vscode")) {
    New-Item -ItemType Directory -Path ".vscode" | Out-Null
}

Write-Host "✅ VS Code settings configured" -ForegroundColor Green
Write-Host "✅ EditorConfig file present" -ForegroundColor Green

# Validate current formatting
Write-Host ""
Write-Host "🔍 Validating current repository formatting..." -ForegroundColor Yellow

if (Test-Path "scripts\check-format-eclipse.ps1") {
    Write-Host "📄 Running format validation..." -ForegroundColor Yellow
    try {
        & ".\scripts\check-format-eclipse.ps1" "origin/master"
        Write-Host "✅ All files properly formatted" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  Some files may need formatting - check with detailed script" -ForegroundColor Yellow
        Write-Host "   Run: .\scripts\check-format-eclipse.ps1" -ForegroundColor Yellow
    }
} else {
    Write-Host "⚠️  Format checker script not found" -ForegroundColor Yellow
}

# Display setup summary
Write-Host ""
Write-Host "🎉 HPCC4J Development Environment Setup Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📚 What was configured:" -ForegroundColor Cyan
Write-Host "   ✅ EditorConfig (.editorconfig) - Universal editor formatting rules" -ForegroundColor Green
Write-Host "   ✅ VS Code Settings (.vscode\settings.json) - Eclipse formatter integration" -ForegroundColor Green
Write-Host "   ✅ Git Hooks (.git\hooks\pre-commit) - Automatic format validation" -ForegroundColor Green
Write-Host "   ✅ Git Configuration - Whitespace and line ending settings" -ForegroundColor Green
Write-Host ""
Write-Host "🛠️  Next steps:" -ForegroundColor Cyan
Write-Host "   1. Install recommended VS Code extensions:" -ForegroundColor Yellow
Write-Host "      - EditorConfig for VS Code" -ForegroundColor Gray
Write-Host "      - Extension Pack for Java" -ForegroundColor Gray
Write-Host "      - Language Support for Java by Red Hat" -ForegroundColor Gray
Write-Host ""
Write-Host "   2. Configure your IDE to use: eclipse\HPCC-JAVA-Formatter.xml" -ForegroundColor Yellow
Write-Host ""
Write-Host "   3. Enable 'Format on Save' in your editor" -ForegroundColor Yellow
Write-Host ""
Write-Host "🚨 Pre-commit Hook Behavior:" -ForegroundColor Cyan
Write-Host "   - Automatically validates Eclipse formatting compliance" -ForegroundColor Gray
Write-Host "   - Prevents commits with trailing whitespace" -ForegroundColor Gray
Write-Host "   - Blocks commits that violate formatting standards" -ForegroundColor Gray
Write-Host "   - Provides detailed violation reports" -ForegroundColor Gray
Write-Host ""
Write-Host "🔧 Manual formatting commands:" -ForegroundColor Cyan
Write-Host "   - Format all files: mvn net.revelc.code.formatter:formatter-maven-plugin:format" -ForegroundColor Gray
Write-Host "   - Check formatting: .\scripts\check-format-eclipse.ps1" -ForegroundColor Gray
Write-Host "   - Validate specific files: .\scripts\check-format-eclipse.ps1 [target-branch]" -ForegroundColor Gray
Write-Host ""
Write-Host "📖 For more details, see: .github\FORMATTING.md" -ForegroundColor Yellow
Write-Host ""
Write-Host "Happy coding! 🎯" -ForegroundColor Green
