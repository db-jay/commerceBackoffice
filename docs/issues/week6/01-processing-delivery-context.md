## Goal
- `delivery` 컨텍스트를 주문(order)와 분리된 운영 도메인으로 도입하고,
- 출고/송장/배송 상태 전이를 설명 가능한 최소 구현으로 만든다.

## Scope
- delivery 도메인/애플리케이션/프레젠테이션/인프라 기본 패키지 추가
- 배송 생성/조회 또는 주문 연계 기반 최소 API 추가
- 송장 번호 등록 API 추가
- 배송 상태 전이 규칙 정의 (`READY`, `SHIPPED`, `IN_DELIVERY`, `DELIVERED` 등)
- 잘못된 상태 전이 실패 테스트 추가

## Why
- 주문이 생성되었다고 배송이 끝난 것이 아니므로, order 이후 fulfillment 책임을 delivery로 분리해야 한다.
- W6는 "주문 생성/취소" 중심 학습에서 "주문 이후 실제 운영 흐름"으로 확장되는 주차다.

## Out of Scope
- 택배사 외부 연동
- 배송비 계산/정책 고도화
- 다중 배송지/합포장/분리배송
- 배송 추적 이벤트 webhook
- 클레임(반품/교환) 연계

## Checklist
- [ ] 배송이 생성되면 초기 상태가 올바르게 설정된다.
- [ ] 송장 번호는 출고 가능한 상태에서만 등록된다.
- [ ] 배송 상태는 허용된 순서로만 전이된다.
- [ ] 잘못된 상태 전이는 테스트로 실패가 검증된다.

## Done Criteria
- delivery 규칙이 Domain/Application/Infrastructure 코드와 테스트에서 함께 설명 가능해야 한다.
- order와 delivery의 책임 차이를 주니어가 코드 기준으로 설명할 수 있어야 한다.

    ## Verification
    - [ ] `cd backend && ./gradlew test`
    - [ ] `cd backend && ./gradlew check`

    ## Branch
    - `feature/w6-delivery-context-<issueNo>`
