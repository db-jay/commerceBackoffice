## Goal
- 운영 도메인 확장 3주차로 `claim` 컨텍스트를 독립적으로 추가한다.
- 취소/반품/교환과 환불 연계 조건을 "Controller 조건문"이 아니라 Domain/Application 규칙으로 설명 가능한 최소 구현으로 만든다.

## Scope
- `claim` 컨텍스트의 최소 패키지/계층 구조 추가
- 취소/반품/교환 요청 구분 규칙 정의
- claim 상태 전이 규칙 정의
- 환불 필요 여부 또는 환불 연계 지점 표현
- claim 요청 성공/실패/상태 전이 테스트 추가

## Out of Scope
- PG사 실환불 연동
- 택배 회수 자동 배차
- 부분 환불 정산 고도화
- 고객 알림 발송
- 운영자 승인 정책 세분화

## Checklist
- [ ] 취소/반품/교환 요청이 구분되어 생성된다.
- [ ] 배송 전 취소와 배송 후 반품/교환이 다른 흐름으로 드러난다.
- [ ] claim 상태는 허용된 순서로만 전이된다.
- [ ] 환불 필요 여부 또는 환불 연계 지점이 코드에서 설명 가능하다.
- [ ] order/delivery 이후 운영 책임이 claim으로 분리되어 드러난다.

## Done Criteria
- claim 규칙이 Domain/Application/Infrastructure 코드와 테스트에서 함께 설명 가능해야 한다.
- 취소/반품/교환 흐름 차이를 주니어가 코드 기준으로 설명할 수 있어야 한다.

## Verification
- [ ] `cd backend && ./gradlew test`
- [ ] `cd backend && ./gradlew check`

## Branch
- `feature/w7-claim-context-<issueNo>`
