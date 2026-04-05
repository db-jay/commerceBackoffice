#!/usr/bin/env python3
from __future__ import annotations

import argparse
from pathlib import Path
import textwrap


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(description="Scaffold weekly-study issue-pack docs for commerceBackoffice")
    parser.add_argument("--week", type=int, required=True)
    parser.add_argument("--slug", required=True, help="file slug, e.g. display-event-context")
    parser.add_argument("--processing-title", required=True)
    parser.add_argument("--learning-title", required=True)
    parser.add_argument("--base-dir", default="docs/issues", help="base issues directory")
    args = parser.parse_args()

    week = args.week
    week_dir = Path(args.base_dir) / f"week{week}"
    processing_file = f"01-processing-{args.slug}.md"
    learning_file = f"11-learning-{args.slug}.md"

    readme = f"""
    # Week {week} Issue Pack (Processing vs Learning)

    이 디렉터리는 {week}주차 이슈를 `처리(구현)`와 `학습` 트랙으로 분리해 발행하기 위한 패키지다.

    ## 생성 이슈 구성
    1. `[W{week}][진행관리] {week}주차 실행 컨트롤타워`
    2. `[W{week}][처리] {args.processing_title}`
    3. `[W{week}][학습] {args.learning_title}`

    ## 운영 규칙
    - 처리 트랙 이슈는 코드/테스트/증빙 중심으로 완료한다.
    - 학습 트랙 이슈는 체크리스트를 두지 않고, 주니어가 읽고 숙지할 핵심 내용 중심으로 작성한다.
    - 이슈 생성 시 라벨을 반드시 부착한다.
      - 공통: `week:{week}`
      - 처리: `track:processing`
      - 학습: `track:learning`
      - 진행관리: `track:control`

    ## 발행 방법
    ```bash
    bash docs/issues/week{week}/create_issues.sh
    ```
    """

    control = f"""
    ## Goal
    - {week}주차 실행 기준과 이슈 상태를 한 곳에서 추적한다.
    - `처리`와 `학습` 트랙의 완료 조건을 분리 관리한다.

    ## Scope
    - 하위 이슈 진행 상태 집계
    - 주간 완료 규칙 점검
    - 주간 보고/회고 링크 수집

    ## Checklist
    - [ ] 처리 트랙 이슈 1개가 `Done` 처리되었다.
    - [ ] 학습 트랙 이슈 1개가 `Done` 처리되었다.
    - [ ] `docs/WEEKLY_CHECKLIST.md`의 W{week} 항목과 결과를 대조했다.
    - [ ] 주간 보고 템플릿으로 보고를 남겼다.
    - [ ] 주간 회고 5줄 링크를 연결했다.

    ## Done Criteria
    - W{week} 하위 이슈 2개가 모두 종료되고 증빙 링크가 본문에 정리되어 있다.

    ## Evidence
    - Processing Issue #:
    - Learning Issue #:
    - Weekly report link:
    - Retrospective link:
    """

    processing = f"""
    ## Goal
    - {args.processing_title} 목표를 구현 가능한 단위로 정리한다.

    ## Scope
    - TODO: 처리 범위를 현재 주차 목표에 맞게 구체화한다.

    ## Why
    - TODO: 이 기능이 커머스 운영에서 왜 필요한지 적는다.
    - TODO: 이 주차가 전체 로드맵/구성틀에서 어디에 놓이는지 적는다.

    ## Out of Scope
    - TODO: 이번 주에 하지 않을 항목을 명시한다.

    ## Checklist
    - [ ] TODO: 핵심 성공 조건 1
    - [ ] TODO: 핵심 성공 조건 2
    - [ ] TODO: 실패/경계 케이스 1
    - [ ] TODO: 테스트/조회/동기화 조건 1

    ## Done Criteria
    - TODO: 코드 + 테스트 + 설명 가능성 기준을 적는다.

    ## Verification
    - [ ] `cd backend && ./gradlew test`
    - [ ] `cd backend && ./gradlew check`

    ## Branch
    - `feature/w{week}-{args.slug}-<issueNo>`
    """

    learning = f"""
    ## 목적
    - 주니어가 `{args.learning_title}`를 구현 전에 설명할 수 있게 만든다.

    ## 숙지해야 할 내용
    - TODO: 이 기능이 큰 커머스 구조에서 어디에 놓이는가
    - TODO: 왜 필요한가
    - TODO: 어떤 선택을 했는가
    - TODO: 장점/단점
    - TODO: 언제 문제가 생기는가
    - TODO: 다음 단계에서 무엇이 달라지는가

    ## 스스로 설명해보기
    - TODO: 구조 질문 1
    - TODO: 설명 질문 1
    - TODO: 설명 질문 2
    - TODO: 설명 질문 3
    - TODO: 운영 질문 1
    """

    create_issues = textwrap.dedent(
        f"""\
        #!/usr/bin/env bash
        set -euo pipefail

        BASE_DIR="$(cd "$(dirname "${{BASH_SOURCE[0]}}")" && pwd)"

        detect_repo() {{
          local origin
          origin="$(git remote get-url origin 2>/dev/null || true)"
          if [[ -z "${{origin}}" ]]; then
            echo "Cannot detect git remote origin. Pass repo as first arg: owner/name" >&2
            exit 1
          fi

          local repo
          repo="$(echo "${{origin}}" | sed -E 's#(git@|https://)github.com[:/]##; s#\\.git$##')"
          if [[ "${{repo}}" != */* ]]; then
            echo "Invalid repo format: ${{repo}}" >&2
            exit 1
          fi

          echo "${{repo}}"
        }}

        require_gh_auth() {{
          if ! command -v gh >/dev/null 2>&1; then
            echo "gh is not installed." >&2
            exit 1
          fi
          if ! gh auth status >/dev/null 2>&1; then
            echo "gh is not authenticated. Run: gh auth login" >&2
            exit 1
          fi
        }}

        ensure_label() {{
          local repo="$1"
          local name="$2"
          local color="$3"
          local description="$4"
          gh label create "${{name}}" --repo "${{repo}}" --color "${{color}}" --description "${{description}}" --force >/dev/null
        }}

        ensure_default_labels() {{
          local repo="$1"
          ensure_label "${{repo}}" "week:{week}" "C2E0C6" "{week}주차 작업"
          ensure_label "${{repo}}" "track:processing" "1D76DB" "구현/코드 작업"
          ensure_label "${{repo}}" "track:learning" "5319E7" "학습/숙지 작업"
          ensure_label "${{repo}}" "track:control" "FBCA04" "진행관리 이슈"
        }}

        create_issue() {{
          local repo="$1"
          local title="$2"
          local body_file="$3"
          shift 3
          gh issue create --repo "${{repo}}" --title "${{title}}" --body-file "${{body_file}}" "$@"
        }}

        REPO="${{1:-$(detect_repo)}}"
        require_gh_auth
        ensure_default_labels "${{REPO}}"

        P1_URL="$(create_issue "${{REPO}}" "[W{week}][처리] {args.processing_title}" "${{BASE_DIR}}/{processing_file}" --label "week:{week}" --label "track:processing")"
        L1_URL="$(create_issue "${{REPO}}" "[W{week}][학습] {args.learning_title}" "${{BASE_DIR}}/{learning_file}" --label "week:{week}" --label "track:learning")"

        TMP_CONTROL="$(mktemp)"
        cat "${{BASE_DIR}}/00-process-control-tower.md" > "${{TMP_CONTROL}}"
        {{
          echo ""
          echo "## Created Child Issues"
          echo "- ${{P1_URL}}"
          echo "- ${{L1_URL}}"
        }} >> "${{TMP_CONTROL}}"

        CONTROL_URL="$(create_issue "${{REPO}}" "[W{week}][진행관리] {week}주차 실행 컨트롤타워" "${{TMP_CONTROL}}" --label "week:{week}" --label "track:control")"
        rm -f "${{TMP_CONTROL}}"

        echo ""
        echo "Created issues:"
        echo "- ${{CONTROL_URL}}"
        echo "- ${{P1_URL}}"
        echo "- ${{L1_URL}}"
        """
    )

    write(week_dir / "README.md", readme)
    write(week_dir / "00-process-control-tower.md", control)
    write(week_dir / processing_file, processing)
    write(week_dir / learning_file, learning)
    write(week_dir / "create_issues.sh", create_issues)
    (week_dir / "create_issues.sh").chmod(0o755)

    print(f"Scaffolded {week_dir}")
    print("Next:")
    print("1) refine processing/learning scope")
    print("2) sync docs/WEEKLY_CHECKLIST.md")
    print(f"3) run: bash docs/issues/week{week}/create_issues.sh")


if __name__ == "__main__":
    main()
