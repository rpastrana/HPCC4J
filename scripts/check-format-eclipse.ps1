# Check Java files changed in PR against Eclipse formatter configuration
param(
    [string]$TargetBranch = "origin/master"
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path $PSScriptRoot -Parent
$eclipseConfig = Join-Path $projectRoot "eclipse\HPCC-JAVA-Formatter.xml"

# Function to analyze line differences and create violation reports
function Get-LineViolations {
    param(
        [string]$OriginalLine,
        [string]$FormattedLine,
        [int]$LineNumber
    )

    $violations = @()
    $maxLength = [Math]::Max($OriginalLine.Length, $FormattedLine.Length)

    # Find first difference position
    $firstDiffPos = -1
    for ($i = 0; $i -lt $maxLength; $i++) {
        $origChar = if ($i -lt $OriginalLine.Length) { $OriginalLine[$i] } else { $null }
        $formChar = if ($i -lt $FormattedLine.Length) { $FormattedLine[$i] } else { $null }

        if ($origChar -ne $formChar) {
            $firstDiffPos = $i
            break
        }
    }

    if ($firstDiffPos -eq -1) { return @() }

    # Analyze what type of violation this is
    $violationType = "Unknown formatting issue"
    $pointer = ""

    # Check for indentation issues (leading whitespace)
    if ($firstDiffPos -eq 0 -or ($firstDiffPos -lt 20 -and $OriginalLine.Substring(0, $firstDiffPos).Trim() -eq "")) {
        $origIndent = ($OriginalLine | Select-String '^\s*').Matches[0].Value
        $formIndent = ($FormattedLine | Select-String '^\s*').Matches[0].Value

        if ($origIndent.Contains("`t")) {
            $violationType = "Invalid indentation: tabs found, should use 4 spaces"
            $tabPos = $origIndent.IndexOf("`t")
            $pointer = " " * $tabPos + "^" + "-" * ([Math]::Max(1, $origIndent.Length - $tabPos - 1)) + " (tab here)"
        } elseif ($origIndent.Length -ne $formIndent.Length) {
            $violationType = "Invalid indentation: expected $($formIndent.Length) spaces, found $($origIndent.Length)"
            $pointer = " " * [Math]::Min($origIndent.Length, $formIndent.Length) + "^" + "-" * [Math]::Abs($origIndent.Length - $formIndent.Length)
        } else {
            $violationType = "Invalid indentation: incorrect spacing"
            $pointer = " " * $firstDiffPos + "^" + "-" * [Math]::Max(1, [Math]::Abs($OriginalLine.Length - $FormattedLine.Length))
        }
    }
    # Check for operator spacing issues
    elseif ($OriginalLine -match '\S=\S' -and $FormattedLine -match '\S\s+=\s+\S') {
        $violationType = "Missing spaces around assignment operator"
        $eqPos = $OriginalLine.IndexOf('=')
        if ($OriginalLine[$eqPos-1] -ne ' ') {
            $pointer = " " * ($eqPos-1) + "^-" + " missing space before '='"
        } else {
            $pointer = " " * ($eqPos+1) + "^-" + " missing space after '='"
        }
    }
    # Check for keyword spacing issues
    elseif ($OriginalLine -match '\b(if|for|while|switch|catch)\(' -and $FormattedLine -match '\b(if|for|while|switch|catch)\s+\(') {
        $matches = [regex]::Matches($OriginalLine, '\b(if|for|while|switch|catch)\(')
        if ($matches.Count -gt 0) {
            $keywordPos = $matches[0].Index + $matches[0].Value.Length - 1
            $violationType = "Missing space after keyword '$($matches[0].Value.Substring(0, $matches[0].Value.Length-1))'"
            $pointer = " " * $keywordPos + "^-" + " missing space before '('"
        }
    }
    # Check for comma spacing issues
    elseif ($OriginalLine -match ',\S' -and $FormattedLine -match ',\s+\S') {
        $commaPos = $OriginalLine.IndexOf(',')
        while ($commaPos -ge 0 -and $commaPos + 1 -lt $OriginalLine.Length) {
            if ($OriginalLine[$commaPos + 1] -ne ' ') {
                $violationType = "Missing space after comma"
                $pointer = " " * ($commaPos + 1) + "^-" + " missing space after ','"
                break
            }
            $commaPos = $OriginalLine.IndexOf(',', $commaPos + 1)
        }
    }
    # Check for brace placement issues
    elseif ($OriginalLine.Trim() -eq '{' -and $FormattedLine -notmatch '^\s*{\s*$') {
        $violationType = "Invalid brace placement: opening brace should be on new line"
        $bracePos = $OriginalLine.IndexOf('{')
        $pointer = " " * $bracePos + "^-" + " brace placement issue"
    }
    # Generic spacing/formatting issue
    else {
        $violationType = "Formatting issue at column $($firstDiffPos + 1)"
        $pointer = " " * $firstDiffPos + "^" + "-" * [Math]::Max(1, [Math]::Abs($OriginalLine.Length - $FormattedLine.Length))
    }

    return @{
        Type = $violationType
        Position = $firstDiffPos
        Pointer = $pointer
        OriginalLine = $OriginalLine
        FormattedLine = $FormattedLine
    }
}

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

                        # Get detailed violation analysis
                        $violation = Get-LineViolations -OriginalLine $origLine -FormattedLine $formattedLine -LineNumber $displayLineNum

                        if ($violation) {
                            Write-Host "       ❌ $($violation.Type)" -ForegroundColor Yellow
                            Write-Host "       Current : $origLine" -ForegroundColor Red
                            if ($violation.Pointer) {
                                Write-Host "                $($violation.Pointer)" -ForegroundColor Red
                            }
                            Write-Host "       Expected: $formattedLine" -ForegroundColor Green
                        } else {
                            # Fallback for unanalyzed differences
                            if ($origLine.Trim() -ne $formattedLine.Trim()) {
                                Write-Host "       - Current: $origLine" -ForegroundColor Red
                                Write-Host "       + Expected: $formattedLine" -ForegroundColor Green
                            } else {
                                Write-Host "       - Wrong spacing: '$origLine'" -ForegroundColor Red
                                Write-Host "       + Correct spacing: '$formattedLine'" -ForegroundColor Green
                            }
                        }

                        Write-Host "" # Empty line for readability
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
