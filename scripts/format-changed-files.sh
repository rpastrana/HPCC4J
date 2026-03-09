#!/bin/bash
# Check if Java files changed in PR follow formatting standards

set -e

TARGET_BRANCH="${1:-origin/master}"
ECLIPSE_CONFIG="eclipse/HPCC-JAVA-Formatter.xml"

echo "🔍 Checking changed Java files against formatting standards..."

# Get changed Java files (excluding generated code)
CHANGED_FILES=$(git diff --name-only "$TARGET_BRANCH...HEAD" | \
    grep '\.java$' | \
    grep -E '(src/main/java|src/test/java)' | \
    grep -v -E '(gen/|wrappers/gen/|antlr/|extended/)' || true)

if [ -z "$CHANGED_FILES" ]; then
    echo "✅ No Java files changed that require formatting check"
    exit 0
fi

echo "📁 Files to check:"
echo "$CHANGED_FILES" | sed 's/^/  /'
echo ""

# Check if any file is improperly formatted by creating formatted copies
VIOLATIONS=0
TEMP_DIR=$(mktemp -d)

echo "🔧 Checking formatting..."

for file in $CHANGED_FILES; do
    if [ ! -f "$file" ]; then
        continue
    fi
    
    # Create temp formatted version
    temp_file="$TEMP_DIR/$(basename "$file")"
    cp "$file" "$temp_file"
    
    # Format the temp file using Eclipse formatter (via Maven)
    if command -v mvn >/dev/null 2>&1; then
        # Create minimal pom for this check
        cat > "$TEMP_DIR/pom.xml" << EOF
<?xml version="1.0"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>temp</groupId>
    <artifactId>temp</artifactId>
    <version>1.0</version>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>net.revelc.code.formatter</groupId>
                <artifactId>formatter-maven-plugin</artifactId>
                <version>2.16.0</version>
                <configuration>
                    <configFile>$(pwd)/$ECLIPSE_CONFIG</configFile>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF
        
        # Format the temp file
        if ! mvn -f "$TEMP_DIR/pom.xml" net.revelc.code.formatter:formatter-maven-plugin:format \
            -Dformatter.includes="$(basename "$file")" \
            -q >/dev/null 2>&1; then
            echo "⚠️  Could not format $file"
        fi
    fi
    
    # Compare original vs formatted
    if ! diff -q "$file" "$temp_file" >/dev/null 2>&1; then
        echo "❌ $file has formatting violations"
        VIOLATIONS=$((VIOLATIONS + 1))
    fi
done

# Cleanup
rm -rf "$TEMP_DIR"

if [ $VIOLATIONS -eq 0 ]; then
    echo "✅ All changed files follow formatting standards!"
else
    echo ""
    echo "❌ Found formatting violations in $VIOLATIONS file(s)"
    echo "💡 To fix: use your IDE formatter or run mvn formatter:format"
    exit 1
fi