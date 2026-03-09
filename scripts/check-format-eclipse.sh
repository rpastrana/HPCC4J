#!/bin/bash
# Check Java files changed in PR against Eclipse formatter configuration
set -e
TARGET_BRANCH="${1:-origin/master}"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ECLIPSE_CONFIG="$PROJECT_ROOT/eclipse/HPCC-JAVA-Formatter.xml"
# Function to analyze line differences and create violation reports
get_line_violations() {
    local orig_line="$1"
    local formatted_line="$2"
    local line_num="$3"
    local max_length=${#orig_line}
    if [ ${#formatted_line} -gt $max_length ]; then
        max_length=${#formatted_line}
    fi
    # Find first difference position
    local first_diff_pos=-1
    for ((i=0; i<max_length; i++)); do
        local orig_char="${orig_line:$i:1}"
        local form_char="${formatted_line:$i:1}"
        if [ "$orig_char" != "$form_char" ]; then
            first_diff_pos=$i
            break
        fi
    done
    if [ $first_diff_pos -eq -1 ]; then
        return 1
    fi
    local violation_type="Unknown formatting issue"
    local pointer=""
    # Extract leading whitespace
    local orig_indent=$(echo "$orig_line" | sed 's/^\(\s*\).*/\1/')
    local form_indent=$(echo "$formatted_line" | sed 's/^\(\s*\).*/\1/')
    # Check for indentation issues (leading whitespace)
    if [ $first_diff_pos -eq 0 ] || ([ $first_diff_pos -lt 20 ] && [ "$(echo "${orig_line:0:$first_diff_pos}" | xargs)" = "" ]); then
        if echo "$orig_indent" | grep -q $'\t'; then
            violation_type="Invalid indentation: tabs found, should use 4 spaces"
            local tab_pos=$(echo "$orig_indent" | grep -b -o $'\t' | head -1 | cut -d: -f1)
            pointer=$(printf "%*s^%s (tab here)" $tab_pos "" "$(printf '%*s' $((${#orig_indent} - tab_pos - 1)) '' | tr ' ' '-')")
        elif [ ${#orig_indent} -ne ${#form_indent} ]; then
            violation_type="Invalid indentation: expected ${#form_indent} spaces, found ${#orig_indent}"
            local min_indent=$((${#orig_indent} < ${#form_indent} ? ${#orig_indent} : ${#form_indent}))
            local diff_len=$((${#orig_indent} - ${#form_indent}))
            [ $diff_len -lt 0 ] && diff_len=$((-diff_len))
            pointer=$(printf "%*s^%s" $min_indent "" "$(printf '%*s' $diff_len '' | tr ' ' '-')")
        else
            violation_type="Invalid indentation: incorrect spacing"
            pointer=$(printf "%*s^%s" $first_diff_pos "" "$(printf '%*s' $((${#orig_line} - ${#formatted_line})) '' | tr ' ' '-')")
        fi
    # Check for operator spacing issues
    elif echo "$orig_line" | grep -q '[^[:space:]]=[^[:space:]]' && echo "$formatted_line" | grep -q '[^[:space:]][[:space:]]+=[[:space:]]+[^[:space:]]'; then
        violation_type="Missing spaces around assignment operator"
        local eq_pos=$(echo "$orig_line" | grep -b -o '=' | head -1 | cut -d: -f1)
        if [ "${orig_line:$((eq_pos-1)):1}" != " " ]; then
            pointer=$(printf "%*s^- missing space before '='" $((eq_pos-1)) "")
        else
            pointer=$(printf "%*s^- missing space after '='" $((eq_pos+1)) "")
        fi
    # Check for keyword spacing issues
    elif echo "$orig_line" | grep -q '\b\(if\|for\|while\|switch\|catch\)(' && echo "$formatted_line" | grep -q '\b\(if\|for\|while\|switch\|catch\)[[:space:]]+\('; then
        local keyword=$(echo "$orig_line" | grep -o '\b\(if\|for\|while\|switch\|catch\)(')
        local keyword_name=${keyword%(*}
        local keyword_pos=$(echo "$orig_line" | grep -b -o "\b${keyword_name}(" | head -1 | cut -d: -f1)
        violation_type="Missing space after keyword '$keyword_name'"
        pointer=$(printf "%*s^- missing space before '('" $((keyword_pos + ${#keyword_name})) "")
    # Check for comma spacing issues
    elif echo "$orig_line" | grep -q ',[^[:space:]]' && echo "$formatted_line" | grep -q ',[[:space:]]+[^[:space:]]'; then
        local comma_pos=$(echo "$orig_line" | grep -b -o ',[^[:space:]]' | head -1 | cut -d: -f1)
        violation_type="Missing space after comma"
        pointer=$(printf "%*s^- missing space after ','" $((comma_pos + 1)) "")
    # Check for brace placement issues
    elif [ "$(echo "$orig_line" | xargs)" = "{" ] && ! echo "$formatted_line" | grep -q '^[[:space:]]*{[[:space:]]*$'; then
        violation_type="Invalid brace placement: opening brace should be on new line"
        local brace_pos=$(echo "$orig_line" | grep -b -o '{' | head -1 | cut -d: -f1)
        pointer=$(printf "%*s^- brace placement issue" $brace_pos "")
    # Generic spacing/formatting issue
    else
        violation_type="Formatting issue at column $((first_diff_pos + 1))"
        local diff_len=$((${#orig_line} - ${#formatted_line}))
        [ $diff_len -lt 0 ] && diff_len=$((-diff_len))
        [ $diff_len -eq 0 ] && diff_len=1
        pointer=$(printf "%*s^%s" $first_diff_pos "" "$(printf '%*s' $diff_len '' | tr ' ' '-')")
    fi
    echo "TYPE:$violation_type"
    echo "POINTER:$pointer"
    echo "ORIGINAL:$orig_line"
    echo "FORMATTED:$formatted_line"
    return 0
}
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
CHANGED_FILES=$(printf "%s\n%s\n" "$COMMITTED_FILES" "$UNCOMMITTED_FILES" | sort -u | sed '/^$/d')
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
            # Show detailed differences with enhanced analysis
            echo "   Violations found:"
            # Read files into arrays for line-by-line comparison
            mapfile -t orig_lines < "src/main/java/$rel_path.orig"
            mapfile -t formatted_lines < "src/main/java/$rel_path"
            max_lines=${#orig_lines[@]}
            if [ ${#formatted_lines[@]} -gt $max_lines ]; then
                max_lines=${#formatted_lines[@]}
            fi
            diff_count=0
            for ((line_num=0; line_num<max_lines && diff_count<10; line_num++)); do
                orig_line="${orig_lines[$line_num]-}"
                formatted_line="${formatted_lines[$line_num]-}"
                if [ "$orig_line" != "$formatted_line" ]; then
                    display_line_num=$((line_num + 1))
                    echo "     📍 Line $display_line_num:"
                    # Get detailed violation analysis
                    violation_info=""
                    if violation_info=$(get_line_violations "$orig_line" "$formatted_line" "$display_line_num"); then
                        violation_type=$(echo "$violation_info" | grep "^TYPE:" | cut -d: -f2-)
                        pointer=$(echo "$violation_info" | grep "^POINTER:" | cut -d: -f2-)
                        echo "       ❌ $violation_type"
                        echo "       Current : $orig_line"
                        if [ -n "$pointer" ]; then
                            echo "                $pointer"
                        fi
                        echo "       Expected: $formatted_line"
                    else
                        # Fallback for unanalyzed differences
                        if [ "$(echo "$orig_line" | xargs)" != "$(echo "$formatted_line" | xargs)" ]; then
                            echo "       - Current: $orig_line"
                            echo "       + Expected: $formatted_line"
                        else
                            echo "       - Wrong spacing: '$orig_line'"
                            echo "       + Correct spacing: '$formatted_line'"
                        fi
                    fi
                    echo "" # Empty line for readability
                    diff_count=$((diff_count + 1))
                    if [ $diff_count -eq 10 ]; then
                        echo "       ... (showing first 10 violations, run formatter to see all)"
                    fi
                fi
            done
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
