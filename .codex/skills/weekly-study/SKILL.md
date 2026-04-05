---
name: weekly-study
description: Run the commerceBackoffice weekly study workflow consistently: sync weekly progress, derive the next week from the roadmap, scaffold and register issue packs, help the junior understand large-commerce structure and why each feature exists, implement on a feature branch, and write/update the PR in the repo's Korean template.
metadata:
  short-description: Run the weekly study / issue / PR cycle consistently
---

# Weekly Study

Use this skill when the user asks to:
- prepare the next week (`week5`, `week6`, etc.)
- sync weekly progress/status before starting the next cycle
- create or register weekly issues
- provide junior learning points for the week
- implement the weekly requirement and open/update a PR
- understand the structure of a large commerce backoffice step by step
- explain why this week's feature matters in the bigger commerce operating model
- keep the response shape and wording consistent across sessions

Do **not** use this skill for one-off bug fixes unrelated to the weekly issue/learning/PR workflow.

## Core purpose

This workflow is repetitive on purpose.

The weekly cycle exists to help a junior developer:
- understand how a **large commerce/backoffice system** is decomposed over time
- learn not only **what feature to implement**, but also **why that feature exists in real operations**
- connect domain structure, issue management, implementation, verification, and PR communication into one repeatable habit

When writing roadmap / issue / PR / learning content, always optimize for both:
1. **구성틀 이해** — "이 기능이 커머스 전체 구조에서 어디에 놓이는가?"
2. **구현 이유 이해** — "왜 지금 이 기능을 만들고, 어떤 운영 문제를 해결하는가?"

## Source of truth

Read these first, in this order:
1. `README.md` — roadmap and monthly/week targets
2. `docs/WEEKLY_CHECKLIST.md` — current weekly execution board
3. `docs/PROJECT_EXECUTION_GUIDE.md` — operating rules and branch/verification conventions
4. `docs/issues/week*/README.md` + `docs/issues/week*/create_issues.sh` — existing issue-pack pattern
5. Recent merged PRs — copy the established **Korean** PR body template

Also infer the weekly position from the larger roadmap:
- which commerce domain/context the current week belongs to
- how it connects to earlier weeks
- what operational capability it unlocks for later weeks

## Non-negotiable repo conventions

- New branches use `feature/{description}` only
- Keep the weekly issue pack split into:
  - `진행관리`
  - `처리`
  - `학습`
- Learning issues are not checkbox-driven; they are explanation-driven
- Final PR body must use the repo's Korean template sections:
  - `## 연결 이슈`
  - `## 이번 단계에서 한 일`
  - `## 왜 이렇게 바꿨나`
  - `## 검증`
  - `## 리스크 / 사이드이펙트`
  - `## 롤백`
  - `## 학습 포인트`

## Default execution flow

### 1) Sync before starting a new week

Before proposing the next week:
- inspect current branch, weekly docs, existing issues, and latest PRs
- mark completed prior weeks in `docs/WEEKLY_CHECKLIST.md`
- update prior control-tower docs if the child issues are already closed
- sync stale branch-rule text or workflow text if it conflicts with current repo directives

### 2) Build the next week's roadmap

When the user asks for the next week's plan:
- derive the target week from the roadmap + current progress
- keep the week scoped to **1 processing issue + 1 learning issue + 1 control issue** unless the repo pattern clearly shows otherwise
- produce:
  - weekly goal
  - where this week sits in the larger commerce architecture
  - what operational/business problem this feature solves
  - processing scope
  - out-of-scope
  - junior learning points
  - done criteria
  - verification targets

### 3) Scaffold the issue pack

Create/update `docs/issues/weekN/` with:
- `README.md`
- `00-process-control-tower.md`
- `01-processing-*.md`
- `11-learning-*.md`
- `create_issues.sh`

Use the helper script when possible:

```bash
python3 .codex/skills/weekly-study/scripts/scaffold_week.py \
  --week 5 \
  --slug display-event-context \
  --processing-title "display/event 컨텍스트 1차 구현" \
  --learning-title "display/event 컨텍스트와 기간 규칙 이해"
```

After scaffolding, always open and refine the generated files so the week-specific scope, learning questions, and verification steps are concrete.

### 4) Register GitHub issues

Before issuing:
- verify `gh auth status`
- inspect whether the week's issues already exist
- run `bash docs/issues/weekN/create_issues.sh`
- collect and report created issue URLs/numbers

### 5) Junior learning support

For the learning issue:
- optimize for "can explain it back", not checklist completion
- always connect this week's feature to the **bigger commerce operating picture**
- include:
  - 왜 필요한가
  - 어떤 선택을 했는가
  - 장점/단점
  - 언제 문제가 생기는가
  - 다음 단계에서 무엇이 달라지는가

Prefer 3–5 concrete “스스로 설명해보기” questions.

In addition, include at least one of each:
- **구조 질문** — "이 기능은 전체 커머스 구성 중 어디에 속하는가?"
- **운영 질문** — "실무 운영에서 어떤 문제를 막거나 쉽게 만드는가?"
- **경계 질문** — "이 기능이 catalog/order/display/delivery/claim 등 다른 컨텍스트와 어떻게 나뉘는가?"

### 6) Implementation phase

When moving from plan/issues to code:
- create `feature/wN-...-<issueNo>` or similar `feature/...` branch
- follow existing layer patterns from the repo
- add/adjust tests before claiming completion
- run:
  - `cd backend && ./gradlew test`
  - `cd backend && ./gradlew check`

### 7) PR phase

When opening or fixing a PR:
- inspect recent PRs and match the repo's Korean template exactly
- reference the weekly issue numbers
- keep risks honest and scoped
- mention excluded scope clearly
- if docs under `docs/issues/weekN/**` are intentionally included despite ignore rules, say so explicitly
- make sure `## 왜 이렇게 바꿨나` explains both:
  - the implementation reason
  - the commerce-domain / operating reason

### 8) Control-tower / issue comments

After implementation:
- comment on the processing issue with:
  - branch
  - PR number
  - 핵심 변경
  - 검증 결과
- update or comment on the control-tower issue with:
  - processing issue
  - learning issue
  - PR
  - current status

## Response shape for consistency

When the user asks broadly ("이번 주 진행해줘", "로드맵 정리해줘", "issue 발행해줘"), structure responses in this order:
1. 현재 파악 결과
2. 이번 주 목표
3. 이번 주가 커머스 전체 구조에서 가지는 의미
4. 처리 범위
5. 학습 포인트
6. 실행 순서
7. 검증/완료 기준
8. 다음 액션

When the user asks for execution status, structure responses in this order:
1. 완료/진행중
2. 변경 파일
3. 검증 결과
4. 이슈/PR 링크
5. 남은 리스크
6. 이번 주 학습 관점에서 남은 설명 포인트

## Guardrails

- Do not invent week scope without checking `README.md` and `docs/WEEKLY_CHECKLIST.md`
- Do not write an English PR body in this repo unless the user explicitly asks for English
- Do not leave weekly docs half-synced if you already know previous week issues were closed
- Do not claim Swagger/UI/PR/issue creation worked unless you actually verified it
- Preserve unrelated user-local changes (`.idea`, root scratch files, etc.)
- Do not explain the week as mere feature shipping; always connect it to the commerce operating model and the reason the feature exists

## Quick checklist

- [ ] previous week synced
- [ ] next week issue pack drafted
- [ ] this week is positioned in the larger commerce structure
- [ ] the feature's implementation reason and operating reason are both explained
- [ ] junior learning points included
- [ ] GitHub issues created or updated
- [ ] feature branch follows `feature/` rule
- [ ] tests/check run
- [ ] PR uses Korean template
- [ ] processing/control issues commented
