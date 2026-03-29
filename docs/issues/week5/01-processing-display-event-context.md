## Goal
- 운영 도메인 확장 1주차로 `display/event` 컨텍스트를 독립적으로 추가한다.
- 전시 기간과 이벤트 활성 조건을 "Controller 조건문"이 아니라 Domain/Application 규칙으로 설명 가능한 최소 구현으로 만든다.

## Scope
- `display/event` 컨텍스트의 최소 패키지/계층 구조 추가
- 이벤트 활성 상태 + 기간(`startAt`, `endAt`) 판단 규칙 정의
- 상품 대상 연결과 노출 가능 여부 조회 흐름 추가
- display/event 조회 성공/실패/경계값 테스트 추가

## Out of Scope
- 할인 계산 엔진
- 쿠폰/프로모션 정책
- 이벤트 우선순위/중첩 적용
- 캐시/검색엔진 연동
- 배너 CMS 수준 편집 기능

## Checklist
- [ ] 활성 이벤트이면서 기간 내이면 노출 가능 결과를 반환한다.
- [ ] 비활성 이벤트면 기간 내여도 적용되지 않는다.
- [ ] 기간 밖 이벤트는 적용되지 않는다.
- [ ] `startAt`, `endAt` 경계값 테스트가 존재한다.
- [ ] display 노출 정책이 catalog 상품 기본 정보와 분리된 책임으로 드러난다.

## Done Criteria
- `display/event` 규칙이 Domain/Application/Infrastructure 코드와 테스트에서 함께 설명 가능해야 한다.
- 시간 의존 규칙은 임의의 `now` 주입 또는 동등한 테스트 전략으로 재현 가능해야 한다.

## Verification
- [ ] `cd backend && ./gradlew test`
- [ ] `cd backend && ./gradlew check`

## Branch
- `feature/w5-display-event-context-<issueNo>`
