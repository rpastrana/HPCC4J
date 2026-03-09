#!/bin/bash
# Check Java files changed in PR against Eclipse formatter configuration

set -e

TARGET_BRANCH="${1:-origin/master}"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ECLIPSE_CONFIG="$PROJECT_ROOT/eclipse/HPCC-JAVA-Formatter.xml"

echo "🔍 Checking changed Java files against Eclipse formatter standards..."

if [ ! -f "$ECLIPSE_CONFIG" ]; then
    echo "❌ Eclipse formatter config not found: $ECLIPSE_CONFIG"
    exit 1
fi

# Get changed Java files (excluding generated code)
# Check both committed changes vs target branch AND uncommitted changes
COMMITTED_FILES=$(git diff --name-only "$TARGET_BRANCH...HEAD" | \
    grep '\.java$' | \
    grep -E '(src/main/java|src/test/java)' | \
    grep -v -E '(gen/|wrappers/gen/|antlr/|extended/)' || true)

UNCOMMITTED_FILES=$(git diff --name-only | \
    grep '\.java$' | \
    grep -E '(src/main/java|src/test/java)' | \
    grep -v -E '(gen/|wrappers/gen/|antlr/|extended/)' || true)

# Combine and deduplicate
CHANGED_FILES=$(printf "%s\n%s\n" "$COMMITTED_FILES" "$UNCOMMITTED_FILES" | sort -u | grep -v '^$')

if [ -z "$CHANGED_FILES" ]; then
    echo "✅ No Java files changed that require formatting check"
    exit 0
fi

echo "📁 Files to check:"
echo "$CHANGED_FILES" | sed 's/^/  /'
echo ""

# Create temporary Maven project for formatting validation
TEMP_DIR=$(mktemp -d)
cd "$TEMP_DIR"

# Create minimal POM with formatter plugin
cat > pom.xml << EOF
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
                    <configFile>$ECLIPSE_CONFIG</configFile>
                    <encoding>UTF-8</encoding>
                    <skipFormatting>false</skipFormatting>
                    <lineEnding>LF</lineEnding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Create directory structure
mkdir -p src/main/java

# Process each changed file
violations=0
echo "🔧 Checking formatting with Eclipse formatter..."

for file in $CHANGED_FILES; do
    if [ ! -f "$PROJECT_ROOT/$file" ]; then
        continue
    fi
    
    # Copy file to temp project
    rel_path=$(echo "$file" | sed 's|.*/src/main/java/||; s|.*/src/test/java/||')
    target_dir="src/main/java/$(dirname "$rel_path")"
    mkdir -p "$target_dir"
    cp "$PROJECT_ROOT/$file" "src/main/java/$rel_path"
    
    # Create a backup of original
    cp "src/main/java/$rel_path" "src/main/java/$rel_path.orig"
    
    # Run formatter on this file
    if mvn -q net.revelc.code.formatter:formatter-maven-plugin:format \
        -Dformatter.includes="$rel_path" >/dev/null 2>&1; then
        
        # Compare original vs formatted
        if ! diff -q "src/main/java/$rel_path.orig" "src/main/java/$rel_path" >/dev/null; then
            echo "❌ $file has formatting violations"
            
            # Show detailed differences
            echo "   Violations found:"
            
            # Generate diff and show with better formatting
            diff_output=$(diff -u "src/main/java/$rel_path.orig" "src/main/java/$rel_path" | head -30)
            
            echo "$diff_output" | while IFS= read -r line; do
                case "$line" in
                    @@*@@*)
                        echo "     📍 $line" | sed 's/@@.*@@/Line change:/'
                        ;;
                    -*)
                        echo "       ❌ Current : ${line:1}"
                        ;;
                    +*)
                        echo "       ✅ Expected: ${line:1}"
                        ;;
                    " "*|"	"*)
                        # Context line
                        echo "         ${line:1}" | head -c 80
                        ;;
                esac
            done
            
            # Analyze common formatting issues
            echo ""
            echo "   Common issues detected:"
            
            # Check for brace placement issues  
            orig_file="src/main/java/$rel_path.orig"
            formatted_file="src/main/java/$rel_path"
            
            if grep -q "class.*{$" "$orig_file" && ! grep -q "class.*{$" "$formatted_file"; then
                echo "     • Brace placement: Opening braces should be on new line for method declarations"
            fi
            
            # Check for spacing issues around operators
            if grep -qE "[a-zA-Z0-9]=|=[a-zA-Z0-9]" "$orig_file"; then
                echo "     • Missing spaces around assignment operators (=, +=, -=, etc.)"
            fi
            
            if grep -qE "(if|for|while|switch)\(" "$orig_file"; then
                echo "     • Missing space after control flow keywords (if, for, while, etc.)"
            fi
            
            # Check for comma spacing
            if grep -q ",[^ ]" "$orig_file"; then
                echo "     • Missing space after commas in parameter lists"
            fi
            
            # Check indentation (basic check)
            if grep -q $'\t' "$orig_file"; then
                echo "     • Use 4 spaces for indentation instead of tabs"
            fi
            
            echo ""
            violations=$((violations + 1))
        fi
    else
        echo "⚠️  Could not format $file (might have syntax errors)"
    fi
done

# Cleanup
cd "$PROJECT_ROOT"
rm -rf "$TEMP_DIR"

if [ $violations -eq 0 ]; then
    echo "✅ All changed files follow Eclipse formatting standards!"
else
    echo "❌ Found formatting violations in $violations file(s)"
    echo ""
    echo "💡 To fix formatting violations:"
    echo "   1. Use Eclipse IDE with the project formatter config"
    echo "   2. Or run: mvn net.revelc.code.formatter:formatter-maven-plugin:format"
    echo "   3. Or use VS Code with Java formatter extension"
    exit 1
fi