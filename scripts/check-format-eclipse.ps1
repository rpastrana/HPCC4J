# Check Java files changed in PR against Eclipse formatter configuration
param(
    [string]$TargetBranch = "origin/master"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path $PSScriptRoot -Parent
$eclipseConfig = Join-Path $projectRoot "eclipse\HPCC-JAVA-Formatter.xml"

Write-Host "Checking changed Java files against Eclipse formatter standards..." -ForegroundColor Yellow

if (-not (Test-Path $eclipseConfig)) {
    Write-Host "Eclipse formatter config not found: $eclipseConfig" -ForegroundColor Red
    exit 1
}

# Get changed Java files (excluding generated code)
# Check both committed changes vs target branch AND uncommitted changes
$changedFiles = @()

# Get committed changes vs target branch
$committedFiles = git diff --name-only "$TargetBranch...HEAD" | Where-Object {
    $_ -match '\.java$' -and
    $_ -match '(src/main/java|src/test/java)' -and
    $_ -notmatch '(gen/|wrappers/gen/|antlr/|extended/)'
}

# Get uncommitted changes
$uncommittedFiles = git diff --name-only | Where-Object {
    $_ -match '\.java$' -and
    $_ -match '(src/main/java|src/test/java)' -and
    $_ -notmatch '(gen/|wrappers/gen/|antlr/|extended/)'
}

# Combine and deduplicate
$changedFiles = ($committedFiles + $uncommittedFiles) | Sort-Object -Unique

if (-not $changedFiles) {
    Write-Host "No Java files changed that require formatting check" -ForegroundColor Green
    exit 0
}

Write-Host "Files to check:" -ForegroundColor Yellow
$changedFiles | ForEach-Object { Write-Host "  $_" }
Write-Host ""

# Create temporary Maven project for formatting validation
$tempDir = [System.IO.Path]::GetTempPath() + [System.Guid]::NewGuid().ToString()
New-Item -ItemType Directory -Path $tempDir | Out-Null

# Change to temp directory
Push-Location $tempDir

try {
    # Create minimal POM with formatter plugin
    @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.hpccsystems</groupId>
    <artifactId>format-checker</artifactId>
    <version>1.0</version>
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>net.revelc.code.formatter</groupId>
                <artifactId>formatter-maven-plugin</artifactId>
                <version>2.16.0</version>
                <configuration>
                    <configFile>$($eclipseConfig -replace '\\', '/')</configFile>
                    <encoding>UTF-8</encoding>
                    <skipFormatting>false</skipFormatting>
                    <lineEnding>LF</lineEnding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
"@ | Out-File -FilePath "pom.xml" -Encoding UTF8

    # Create directory structure
    New-Item -ItemType Directory -Path "src\main\java" -Force | Out-Null

    # Process each changed file
    $violations = 0
    Write-Host "Checking formatting with Eclipse formatter..." -ForegroundColor Yellow

    foreach ($file in $changedFiles) {
        $fullPath = Join-Path $projectRoot $file
        if (-not (Test-Path $fullPath)) {
            continue
        }
        
        # Extract relative path after src/main/java or src/test/java
        if ($file -match '.*/src/main/java/(.*)') {
            $relPath = $matches[1]
        } elseif ($file -match '.*/src/test/java/(.*)') {
            $relPath = $matches[1]
        } else {
            continue
        }
        
        # Copy file to temp project
        $targetDir = Split-Path (Join-Path "src\main\java" $relPath) -Parent
        if ($targetDir) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        }
        
        $targetFile = Join-Path "src\main\java" $relPath
        Copy-Item $fullPath $targetFile
        
        # Create backup
        Copy-Item $targetFile "$targetFile.orig"
        
        # Run formatter on this file
        $includePattern = $relPath -replace '\\', '/'
        try {
            mvn -q "net.revelc.code.formatter:formatter-maven-plugin:format" "-Dformatter.includes=$includePattern" 2>$null
            
            # Compare original vs formatted
            $origContent = Get-Content "$targetFile.orig" -Raw
            $formattedContent = Get-Content $targetFile -Raw
            
            if ($origContent -ne $formattedContent) {
                Write-Host "$file has formatting violations" -ForegroundColor Red
                
                # Show detailed differences
                $origLines = $origContent -split "`r`n|`n"
                $formattedLines = $formattedContent -split "`r`n|`n"
                
                Write-Host "   Violations found:" -ForegroundColor Yellow
                
                # Find and show differences line by line
                $maxLines = [Math]::Max($origLines.Length, $formattedLines.Length)
                $diffCount = 0
                
                for ($lineNum = 0; $lineNum -lt $maxLines -and $diffCount -lt 10; $lineNum++) {
                    $origLine = if ($lineNum -lt $origLines.Length) { $origLines[$lineNum] } else { "" }
                    $formattedLine = if ($lineNum -lt $formattedLines.Length) { $formattedLines[$lineNum] } else { "" }
                    
                    if ($origLine -ne $formattedLine) {
                        $displayLineNum = $lineNum + 1
                        Write-Host "     Line ${displayLineNum}:" -ForegroundColor Cyan
                        
                        if ($origLine.Trim() -ne $formattedLine.Trim()) {
                            Write-Host "       - Current: $origLine" -ForegroundColor Red
                            Write-Host "       + Expected: $formattedLine" -ForegroundColor Green
                        } elseif ($origLine -ne $formattedLine) {
                            # Same content, different whitespace
                            Write-Host "       - Wrong indentation/spacing: '$origLine'" -ForegroundColor Red
                            Write-Host "       + Correct indentation/spacing: '$formattedLine'" -ForegroundColor Green
                        }
                        
                        $diffCount++
                        if ($diffCount -eq 10) {
                            Write-Host "       ... (showing first 10 violations, run formatter to see all)" -ForegroundColor Gray
                        }
                    }
                }
                
                # Add analysis of violation types
                Write-Host "   Issue Summary:" -ForegroundColor Yellow
                $violationTypes = @()
                
                # Analyze the differences to categorize violations
                if ($origContent -match '{\s*$' -and $formattedContent -match '\n\s*{') {
                    $violationTypes += "Brace placement (opening braces should be on new line for methods)"
                }
                
                if ($origContent -match '[a-zA-Z0-9]=' -and $formattedContent -match '[a-zA-Z0-9]\s+=') {
                    $violationTypes += "Missing spaces around assignment operators (=, +=, etc.)"
                }
                
                if ($origContent -match '(?<![a-zA-Z0-9])[a-zA-Z]+\(' -and $formattedContent -match '(?<![a-zA-Z0-9])[a-zA-Z]+\s+\(') {
                    $violationTypes += "Missing spaces after keywords (if, for, while, etc.)"
                }
                
                if ($origContent -match ',\S' -and $formattedContent -match ',\s+\S') {
                    $violationTypes += "Missing spaces after commas"
                }
                
                if ($origContent -match '\t') {
                    $violationTypes += "Use 4 spaces instead of tabs for indentation"
                }
                
                if ($violationTypes.Count -eq 0) {
                    $violationTypes += "Indentation and/or alignment issues"
                }
                
                foreach ($violationType in $violationTypes) {
                    Write-Host "     • $violationType" -ForegroundColor Cyan
                }
                
                Write-Host "" # Empty line for readability
                $violations++
            }
        } catch {
            Write-Host "Could not format $file (might have syntax errors)" -ForegroundColor Yellow
        }
    }
    
    if ($violations -eq 0) {
        Write-Host "All changed files follow Eclipse formatting standards!" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "Found formatting violations in $violations file(s)" -ForegroundColor Red
        Write-Host ""
        Write-Host "To fix formatting violations:" -ForegroundColor Yellow
        Write-Host "  1. Use Eclipse IDE with the project formatter config"
        Write-Host "  2. Or run: mvn net.revelc.code.formatter:formatter-maven-plugin:format"
        Write-Host "  3. Or use VS Code with Java formatter extension"
        exit 1
    }
    
} finally {
    # Cleanup
    Pop-Location
    Remove-Item $tempDir -Recurse -Force -ErrorAction SilentlyContinue
}