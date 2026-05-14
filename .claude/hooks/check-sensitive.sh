#!/usr/bin/env bash
# .claude/hooks/check-sensitive.sh
#
# PreToolUse hook invoked from Claude Code on Bash commands.
# When the command is a `git commit`, scan staged files under .claude/
# for sensitive filename patterns and block the commit if any match.
#
# See CLAUDE.md "자동 검증" section for the policy this enforces.
#
# Exit codes:
#   0 — allow (no sensitive content detected, or not a commit)
#   2 — block with feedback (Claude sees the message and stops)

set -euo pipefail

# Read JSON payload from stdin
payload=$(cat || true)

# Extract the bash command via jq; bail silently if jq missing
if ! command -v jq >/dev/null 2>&1; then
    exit 0
fi

cmd=$(printf '%s' "$payload" | jq -r '.tool_input.command // empty')

# Only act on `git commit` invocations
case "$cmd" in
    *"git commit"*) ;;
    *) exit 0 ;;
esac

# Resolve repo root (run from any subdir)
repo_root=$(git rev-parse --show-toplevel 2>/dev/null || true)
if [[ -z "$repo_root" ]]; then
    exit 0
fi
cd "$repo_root"

# Sensitive filename patterns (extended-regex anchored against basename)
# - *.env, *.env.<anything> EXCEPT *.env.example
# - *.pem, *.key, *.p12, *.pfx
# - *credentials*, *secret*, *.token, *-token.*, *private*key*
# - id_rsa, id_ed25519, id_ecdsa
sensitive_regex='(^|/)((.+\.env(\..+)?|.+\.(pem|key|p12|pfx)|.*credentials.*|.*secret.*|.*\.token|.*-token\..*|.*private.*key.*|id_(rsa|ed25519|ecdsa).*))$'

# Staged additions/modifications under .claude/
staged=$(git diff --cached --name-only --diff-filter=ACMR -- '.claude/' || true)

violations=()
while IFS= read -r f; do
    [[ -z "$f" ]] && continue
    # Allow *.env.example explicitly
    case "$f" in
        *.env.example|*/.env.example) continue ;;
    esac
    if [[ "$f" =~ $sensitive_regex ]]; then
        violations+=("$f")
    fi
done <<< "$staged"

if [[ ${#violations[@]} -gt 0 ]]; then
    {
        echo "BLOCKED: sensitive filename patterns detected in .claude/:"
        printf '  - %s\n' "${violations[@]}"
        echo ""
        echo "Policy: CLAUDE.md (자동 검증 — .claude/ 민감 파일 차단)"
        echo "Move these to a gitignored location (e.g., CLAUDE.local.md, .claude/settings.local.json, project root .env)"
        echo "or rotate any exposed secrets before retrying."
    } >&2
    exit 2
fi

exit 0
