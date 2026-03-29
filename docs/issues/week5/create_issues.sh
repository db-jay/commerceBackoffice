#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

detect_repo() {
  local origin
  origin="$(git remote get-url origin 2>/dev/null || true)"
  if [[ -z "${origin}" ]]; then
    echo "Cannot detect git remote origin. Pass repo as first arg: owner/name" >&2
    exit 1
  fi

  local repo
  repo="$(echo "${origin}" | sed -E 's#(git@|https://)github.com[:/]##; s#\.git$##')"
  if [[ "${repo}" != */* ]]; then
    echo "Invalid repo format: ${repo}" >&2
    exit 1
  fi

  echo "${repo}"
}

require_gh_auth() {
  if ! command -v gh >/dev/null 2>&1; then
    echo "gh is not installed." >&2
    exit 1
  fi
  if ! gh auth status >/dev/null 2>&1; then
    echo "gh is not authenticated. Run: gh auth login" >&2
    exit 1
  fi
}

ensure_label() {
  local repo="$1"
  local name="$2"
  local color="$3"
  local description="$4"
  gh label create "${name}" --repo "${repo}" --color "${color}" --description "${description}" --force >/dev/null
}

ensure_default_labels() {
  local repo="$1"
  ensure_label "${repo}" "week:5" "C2E0C6" "5주차 작업"
  ensure_label "${repo}" "track:processing" "1D76DB" "구현/코드 작업"
  ensure_label "${repo}" "track:learning" "5319E7" "학습/숙지 작업"
  ensure_label "${repo}" "track:control" "FBCA04" "진행관리 이슈"
}

create_issue() {
  local repo="$1"
  local title="$2"
  local body_file="$3"
  shift 3
  gh issue create --repo "${repo}" --title "${title}" --body-file "${body_file}" "$@"
}

REPO="${1:-$(detect_repo)}"
require_gh_auth
ensure_default_labels "${REPO}"

P1_URL="$(create_issue "${REPO}" "[W5][처리] display/event 컨텍스트 1차 구현" "${BASE_DIR}/01-processing-display-event-context.md" --label "week:5" --label "track:processing")"
L1_URL="$(create_issue "${REPO}" "[W5][학습] display/event 컨텍스트와 기간 규칙 이해" "${BASE_DIR}/11-learning-display-event-and-period-rule.md" --label "week:5" --label "track:learning")"

TMP_CONTROL="$(mktemp)"
cat "${BASE_DIR}/00-process-control-tower.md" > "${TMP_CONTROL}"
{
  echo ""
  echo "## Created Child Issues"
  echo "- ${P1_URL}"
  echo "- ${L1_URL}"
} >> "${TMP_CONTROL}"

CONTROL_URL="$(create_issue "${REPO}" "[W5][진행관리] 5주차 실행 컨트롤타워" "${TMP_CONTROL}" --label "week:5" --label "track:control")"
rm -f "${TMP_CONTROL}"

echo ""
echo "Created issues:"
echo "- ${CONTROL_URL}"
echo "- ${P1_URL}"
echo "- ${L1_URL}"
