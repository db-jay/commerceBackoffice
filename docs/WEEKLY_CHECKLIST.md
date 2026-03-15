# 주차별 실행 보드 (학습 중심)

## 1. 이 문서의 역할
- 이 문서는 "완료 체크 박스"보다 "이번 주 무엇을 학습/구현하는지"를 한눈에 보는 보드다.
- 세부 설명은 `docs/learning/week*/` 문서에서 읽는다.

## 2. 운영 규칙
- 처리 이슈와 학습 이슈를 분리한다.
- 학습 이슈는 체크리스트 대신 "숙지 문서 링크"를 남긴다.
- 한 주에 처리 목표는 1~2개로 제한한다.

## 3. 진행 상태
- [x] W1
- [ ] W2
- [ ] W3
- [ ] W4
- [ ] W5
- [ ] W6
- [ ] W7
- [ ] W8
- [ ] W9
- [ ] W10
- [ ] W11
- [ ] W12
- [ ] W13

## 4. W1 실행 항목
### 처리(구현)
- 프로젝트 멀티모듈 구조 세팅
- 공통 예외/응답 포맷/로깅 규격 정의
- PostgreSQL 스키마 초안 및 마이그레이션 환경 구성
- catalog/member/order 도메인 모델 초안 작성

### 학습(숙지 문서)
- `docs/learning/week1/00-architecture-target-ddd-hexagonal-dip.md`
- `docs/learning/week1/01-project-multi-module-setup.md`
- `docs/learning/week1/02-common-response-exception-logging.md`
- `docs/learning/week1/03-postgresql-schema-and-migration.md`
- `docs/learning/week1/04-domain-draft-catalog-member-order.md`
- `docs/learning/week1/05-request-wrapper-filter.md`
- `docs/learning/week1/06-request-id-filter-and-mdc.md`
- `docs/learning/week1/07-filter-chain-order.md`
- `docs/learning/week1/08-interceptor-and-webmvc-config.md`
- `docs/learning/week1/09-base-response-and-response-mapper.md`
- `docs/learning/week1/10-global-exception-handling.md`

## 5. W1 최소 완료선
- 처리 이슈 1개 이상 완료(PR 머지 기준)
- 학습 문서 4개 읽고 핵심 질문에 답변 가능
- `./gradlew test`, `./gradlew check` 최소 1회 성공

## 6. W1에서 다음 주로 넘어가는 기준
- W1 항목 중 미완료가 있으면 W2로 넘어가지 않는다.
- 미완료 사유와 다음 액션을 이슈 코멘트로 남긴다.

## 7. W2 실행 항목
### 처리(구현)
- 상품/회원 API 구현 (CRUD + 기본 검증)
- 주문 생성 API 구현 (주문 상태 모델 포함)
- JWT 로그인/재발급(Access/Refresh) API 구현
- 단위/통합 테스트 템플릿 구축

### W2 보안 범위 메모
- W2의 JWT 목표는 **기본 로그인/재발급/401·403 구분**이다.
- Refresh는 **stateless 검증 기반 재발급**까지만 포함한다.
- 아래 항목은 W2 완료선이 아니라 후속 보안 강화 백로그로 본다.
  - Refresh Token 저장소(Redis/DB)
  - Refresh Rotation
  - 로그아웃 무효화
  - Refresh 재사용 감지

### 학습(숙지 문서)
- `docs/issues/week2/11-learning-api-layer-flow.md`
- `docs/issues/week2/12-learning-jwt-flow-and-test-points.md`

## 8. W2 최소 완료선
- 처리 이슈 2개 이상 완료(PR 또는 검증 완료 기준)
- JWT 로그인/재발급 + 401/403 테스트가 통과한다.
- `./gradlew test`, `./gradlew check` 최소 1회 성공

## 9. 다음 주로 넘어가기 전 보안 메모
- Refresh 저장소 미구현은 W2 미완료 사유가 아니다.
- 다만 후속 보안 강화 단계에서 반드시 다시 다뤄야 한다.
- 진행 시점 기준은 README의 보안 강화 구간을 따른다.

## 10. 변경 이력
- v2.0 (2026-03-01): 체크리스트 중심 문서에서 학습/진행 보드 형태로 개편
- v2.1 (2026-03-15): W2 JWT 범위와 Refresh 저장소 후속 보안 강화 구분 추가
