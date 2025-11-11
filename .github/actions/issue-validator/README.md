# Issue Validator Composite Action

This reusable composite action analyzes GitHub issues to ensure they contain all required information for effective triage and resolution.

## Purpose

Automatically validates that issues include:
- **Description**: Clear explanation of the problem
- **Steps to Reproduce**: Detailed reproduction steps
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens (with error messages/stack traces)
- **Environment/Configuration**: HPCC version, HPCC4J module/version, Java version, connection details

## Inputs

| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `github-token` | GitHub token for API access | Yes | - |
| `issue-number` | Issue number to analyze | Yes | - |
| `is-revalidation` | Whether this is a re-validation (`true`) or initial validation (`false`) | No | `false` |

## Behavior

### Analysis
- Scans issue title and body for required information sections
- Checks for minimum content length (100 characters)
- Identifies present and missing information

### Comment Posting
- Posts an automated analysis comment to the issue
- Provides checklist of required information if incomplete
- Adjusts messaging based on `is-revalidation` flag

### Label Management
- **Complete issue**: Removes `needs-more-info`, adds `ready-for-review`
- **Incomplete issue**: Adds `needs-more-info`, removes `ready-for-review`

## Usage

```yaml
- name: Validate Issue
  uses: ./.github/actions/issue-validator
  with:
    github-token: ${{ secrets.GITHUB_TOKEN }}
    issue-number: ${{ github.event.issue.number }}
    is-revalidation: ${{ github.event.action != 'opened' }}
```

### Example: Combined Validation Workflow
```yaml
name: Issue Validation

on:
  issues:
    types: [opened, edited, labeled]

jobs:
  validate-issue:
    runs-on: ubuntu-latest
    if: |
      github.event.action == 'opened' ||
      (github.event.action == 'edited' && contains(github.event.issue.labels.*.name, 'needs-more-info')) ||
      (github.event.action == 'labeled' && github.event.label.name == 'ready-for-review')
    steps:
      - uses: actions/checkout@v4
      - uses: ./.github/actions/issue-validator
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          issue-number: ${{ github.event.issue.number }}
          is-revalidation: ${{ github.event.action != 'opened' }}
```

## Workflows Using This Action

- `issue-validation.yml` - Unified workflow that validates newly opened issues and re-validates edited issues

## Customization

To modify validation criteria, edit the `requiredSections` array in `action.yml`:

```javascript
const requiredSections = [
  { name: 'Description', keywords: ['description', 'summary', 'problem'] },
  // Add or modify sections as needed
];
```

## Labels Required

This action expects the following labels to exist in the repository:
- `needs-more-info` - Applied to incomplete issues
- `ready-for-review` - Applied to complete issues

If labels don't exist, the action will log a warning but continue to post analysis comments.
