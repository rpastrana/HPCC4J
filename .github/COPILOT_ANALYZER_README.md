# GitHub Copilot Issue Analyzer

This workflow automatically analyzes new GitHub issues to determine if they contain sufficient information for investigation and resolution.

## Features

- 🤖 **AI-Powered Analysis**: Uses GitHub Copilot API to intelligently analyze issue content
- 📋 **Smart Fallback**: If Copilot unavailable, uses comprehensive rule-based analysis
- ✅ **Version Validation**: Checks HPCC4J, HPCC Platform, and Java versions for validity
- 🏷️ **Auto-Labeling**: Suggests and applies appropriate labels (bug, enhancement, needs-more-info, etc.)
- 📝 **Actionable Feedback**: Provides specific questions with instructions on how to gather missing information
- 🎯 **HPCC4J-Specific**: Tailored for HPCC4J connection issues, module identification, and common problems
- 🔍 **Access Verification**: Automatically checks if Copilot access is available

## Setup

### Using GitHub Copilot API (Recommended)

To enable AI-powered analysis with GitHub Copilot:

1. **Prerequisites**:
   - Active GitHub Copilot subscription (Individual, Business, or Enterprise)
   - Repository admin access to add secrets

2. **Create a Personal Access Token (PAT)**:
   - Go to GitHub Settings → Developer settings → Personal access tokens → Fine-grained tokens
   - Click "Generate new token"
   - Name: `HPCC4J Issue Analyzer`
   - Expiration: Set as needed (90 days recommended)
   - Repository access: Select `rpastrana/HPCC4J` only
   - Repository permissions:
     - ✅ **Contents**: Read access (required)
     - ✅ **Issues**: Write access (required for comments/labels)
     - ✅ **Metadata**: Read access (automatic)
   - **Important**: The account creating this token MUST have an active Copilot subscription

3. **Add the PAT as a repository secret**:
   - Go to repository → Settings → Secrets and variables → Actions
   - Click "New repository secret"
   - Name: `COPILOT_PAT`
   - Value: Paste your PAT
   - Click "Add secret"

4. **Verify setup**:
   - The workflow will automatically check for Copilot access
   - Check workflow logs for "✓ GitHub Copilot access confirmed"
   - If Copilot access is detected, AI analysis will be used
   - If not detected, it gracefully falls back to rule-based analysis

### How Copilot Access Works

- The PAT authenticates as your GitHub account
- The workflow checks if your account has an active Copilot subscription via `/user/copilot_seat_details` API
- If confirmed, it uses the GitHub Copilot Chat API for intelligent analysis
- The Copilot API understands the HPCC4J context and provides tailored feedback

### Fallback Mode (No Setup Required)

If you don't have GitHub Copilot or prefer not to use it:

- No additional setup required
- The workflow automatically falls back to the Python-based rule analysis
- Still provides comprehensive issue analysis using pattern matching and heuristics

## How It Works

### Triggers

The workflow runs automatically when:
- A new issue is opened
- An existing issue is edited
- Manually triggered via workflow_dispatch (with issue number input)

### Analysis Process

1. **Fetch issue details**: Retrieves title, body, and labels
2. **Authenticate with Copilot PAT**: Uses your PAT to authenticate
3. **Verify Copilot access**: Checks if the account has an active Copilot subscription
4. **Load analysis prompt**: Uses the comprehensive checklist from `.github/copilot-issue-analysis-prompt.md`
5. **AI Analysis (if available)**: Sends prompt to GitHub Copilot Chat API (GPT-4)
6. **Fallback (if needed)**: Uses Python script if Copilot unavailable (`.github/scripts/analyze_issue.py`)
7. **Post comment**: Adds analysis results as a comment on the issue
8. **Apply label**: Automatically adds "needs-more-info" label if necessary

### What It Checks

- ✅ Issue type (bug, enhancement, question)
- ✅ Affected module (wsclient, dfsclient, spark-hpcc, etc.)
- ✅ HPCC4J version (validates format and compatibility)
- ✅ HPCC Platform version (validates 7.x, 8.x, 9.x, 10.x)
- ✅ Java version (checks for minimum Java 8)
- ✅ Reproduction steps (for bug reports)
- ✅ Error messages and stack traces
- ✅ Connection details (endpoints, clusters)
- ✅ Code examples and context

### Output

The workflow posts a comment with:

```markdown
## 🤖 HPCC4J Issue Analysis

### Assessment: SUFFICIENT / NEEDS_MORE_INFO / MOSTLY_SUFFICIENT

**Summary of findings**

### Identified Information
- Issue Type: bug
- Affected Module: wsclient
- HPCC4J Version: 9.6.0
- HPCC Platform Version: 9.4.0
- Java Version: 11
- Has Reproduction Steps: YES
- Has Error Details: YES

### Missing Information
- List of missing items

### Recommended Questions for Reporter
1. **What version of HPCC4J are you using?**
   - Check your pom.xml dependency or run: `mvn dependency:tree | grep hpccsystems`
...
```

## Customization

### Modify Analysis Criteria

Edit `.github/copilot-issue-analysis-prompt.md` to:
- Add new checklist items
- Change validation rules
- Customize question templates

### Modify Rule-Based Analysis

Edit `.github/scripts/analyze_issue.py` to:
- Add new pattern matching rules
- Change scoring thresholds
- Customize output format

### Adjust Workflow Behavior

Edit `.github/workflows/copilot-issue-analyzer.yml` to:
- Change trigger conditions
- Modify label application logic
- Add notifications or integrations

## Troubleshooting

### "⚠️ No GitHub Copilot access detected"
- The account associated with `COPILOT_PAT` doesn't have an active Copilot subscription
- **Solution**: Verify your Copilot subscription at https://github.com/settings/copilot
- If you don't have Copilot, the workflow will automatically use rule-based analysis (still very effective!)

### "✓ Using Copilot PAT for authentication" but still using fallback
- Copilot API call may have failed or returned an error
- Check the workflow logs for API error messages
- Verify your PAT has not expired
- Ensure your Copilot subscription is active

### PAT expired or invalid
- Generate a new PAT following the setup steps
- Update the `COPILOT_PAT` secret in repository settings
- The workflow will automatically use the new token on next run

### Analysis seems incorrect
- Check if issue follows standard format
- Ensure version numbers are clearly stated
- Review `.github/copilot-issue-analysis-prompt.md` for expected format

## Manual Testing

Test the analyzer manually:

```bash
# Trigger via GitHub CLI
gh workflow run copilot-issue-analyzer.yml -f issue_number=123

# Test Python script locally
python3 .github/scripts/analyze_issue.py "Issue title" "Issue body text"
```

## Benefits

1. **Faster Triage**: Automatically identifies issues needing more information
2. **Better Quality**: Encourages reporters to provide complete details
3. **Consistent Standards**: Applies uniform analysis criteria to all issues
4. **Time Savings**: Reduces back-and-forth requesting missing information
5. **Education**: Teaches reporters what information is needed through helpful prompts

## Privacy & Security

- ⚠️ Never share actual passwords or sensitive credentials
- ✅ Token only needs read access to Copilot
- ✅ Analysis is posted publicly on issues (no sensitive data)
- ✅ PAT should have minimal required permissions
