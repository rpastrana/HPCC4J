# Check Java files changed in PR against formatting standards
# 
# This is a simple checker - for comprehensive Eclipse formatter validation
# use check-format-eclipse.ps1 instead

param(
    [string]$TargetBranch = "origin/master"
)

Write-Host "ℹ️  For comprehensive formatting check using Eclipse configuration:"
Write-Host "   .\scripts\check-format-eclipse.ps1"
Write-Host ""
Write-Host "Running comprehensive formatting check..."

# Run the comprehensive checker
& "$PSScriptRoot\check-format-eclipse.ps1" $TargetBranch