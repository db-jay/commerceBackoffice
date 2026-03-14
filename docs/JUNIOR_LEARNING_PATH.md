# Junior Developer 학습 로드맵

## 1. 이 문서의 역할
- 이 문서는 "무엇을 먼저 이해해야 하는지"를 순서대로 안내한다.
- 각 학습 포인트는 별도 문서(`1포인트 = 1문서`)로 관리한다.

## 2. 학습 원칙
- 코드를 보기 전에 "왜 이 구조인지"를 먼저 이해한다.
- 한 번에 한 개념만 학습한다.
- 이해 확인은 "설명 가능 여부"로 판단한다.

## 3. 1주차 학습 포인트 (필수)
0. 아키텍처 개선 목표 이해
- 문서: `docs/learning/week1/00-architecture-target-ddd-hexagonal-dip.md`
- 이해 기준: 계층 책임, DIP, Port/Adapter, 요청 흐름을 말로 설명할 수 있다.

1. 프로젝트 멀티모듈 구조 세팅
- 문서: `docs/learning/week1/01-project-multi-module-setup.md`
- 이해 기준: 왜 모듈을 분리하는지, 장점/단점을 설명할 수 있다.

2. 공통 응답/예외/로깅 규격
- 문서: `docs/learning/week1/02-common-response-exception-logging.md`
- 이해 기준: 왜 API 응답 포맷을 통일하는지 설명할 수 있다.

3. PostgreSQL 스키마 초안 + 마이그레이션
- 문서: `docs/learning/week1/03-postgresql-schema-and-migration.md`
- 이해 기준: 왜 마이그레이션 도구가 필요한지 설명할 수 있다.

4. catalog/member/order 도메인 모델 초안
- 문서: `docs/learning/week1/04-domain-draft-catalog-member-order.md`
- 이해 기준: 엔티티/값 객체/애그리거트 경계를 설명할 수 있다.

5. Request Wrapper Filter
- 문서: `docs/learning/week1/05-request-wrapper-filter.md`
- 이해 기준: 본문 캐싱 이유와 doFilter 필수성을 설명할 수 있다.

6. RequestId Filter + MDC
- 문서: `docs/learning/week1/06-request-id-filter-and-mdc.md`
- 이해 기준: Request ID 재사용과 MDC 정리 이유를 설명할 수 있다.

7. FilterChain 실행 순서
- 문서: `docs/learning/week1/07-filter-chain-order.md`
- 이해 기준: FilterChain 역할과 호출 누락 시 문제를 설명할 수 있다.

8. Interceptor 등록과 역할
- 문서: `docs/learning/week1/08-interceptor-and-webmvc-config.md`
- 이해 기준: Filter와 Interceptor 차이를 설명할 수 있다.

9. 공통 응답 구조와 ResponseMapper
- 문서: `docs/learning/week1/09-base-response-and-response-mapper.md`
- 이해 기준: BaseResponse 필드와 Mapper 책임을 설명할 수 있다.

10. 전역 예외 처리
- 문서: `docs/learning/week1/10-global-exception-handling.md`
- 이해 기준: 예외 흐름과 ControllerAdvice 역할을 설명할 수 있다.

## 4. 주차 운영 방식
- 월: 학습 문서 먼저 읽기
- 화~목: 처리 이슈 구현
- 금: 검증 + 회고

## 5. 금지 사항
- 문서 없이 바로 코드부터 작성하지 않는다.
- 이해하지 못한 상태에서 다음 주차로 넘어가지 않는다.

## 6. 변경 이력
- v2.0 (2026-03-01): 1포인트 1문서 방식으로 재구성
