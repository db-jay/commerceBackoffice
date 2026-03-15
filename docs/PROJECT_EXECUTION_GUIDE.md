# Food Commerce Backoffice 프로젝트 실행 지침서

## 1. 이 문서의 역할
- 이 문서는 "어떻게 진행할지"를 정의하는 운영 문서다.
- 세부 학습 내용은 `docs/JUNIOR_LEARNING_PATH.md`와 `docs/learning/**`에서 관리한다.
- 원칙: 한 번에 많이 구현하지 않고, 작은 단위 이슈로 설계-구현-검증-회고를 반복한다.

## 2. 운영 원칙 (주니어 학습 우선)
- 기능 수보다 "설명 가능한 설계"를 우선한다.
- 처리(구현)와 학습(개념 숙지)은 분리해서 운영한다.
- 학습은 `1포인트 = 1문서(.md)` 규칙으로 작성한다.
- 학습 문서에는 반드시 아래 항목이 있어야 한다.
  - 왜 필요한가
  - 어떤 선택을 했는가
  - 장점/단점
  - 언제 문제가 생기는가
  - 다음 단계에서 무엇이 달라지는가

## 3. 작업 사이클 (이슈 단위)
1. 이슈 생성
- 처리 이슈: 코드/설정 변경 목표
- 학습 이슈: 읽고 이해할 개념 목표

2. 브랜치 생성
- 형식: `codex/<context>-<topic>-<issueNo>`

3. 작업 수행
- 설계 설명
- 최소 구현
- 검증(`test`, `check`, API 확인)
- 결과 기록

4. PR 발행
- 변경 이유
- 검증 결과
- 리스크/롤백
- 학습 포인트 연결 문서

5. 리뷰/머지
- 아키텍처 원칙 위반 여부 확인 후 머지

## 4. 아키텍처 기준 (고정)
- 스타일: DDD + Layered + Hexagonal
- 의존성: `interfaces -> application -> domain <- infrastructure`
- 컨텍스트 간 상태 변경: 직접 테이블 접근 금지, 이벤트/포트 기반
- 정산 확정: Spring Batch 경로로만 수행

### 4-1. 개선 목표
- DDD 개념을 코드/패키지 구조로 표현한다.
- Hexagonal Architecture를 Input/Output Port 기준으로 구현한다.
- DIP(의존성 역전 원칙)를 구조로 강제한다.

### 4-2. 개선 필요성
- Domain 순수성 보장: 프레임워크/기술 의존성 제거
- Port 기반 접근 제어: 계층 경계 명확화
- 공통 응답 구조: API 일관성 확보

### 4-3. 계층 책임 계약
- Presentation(`interfaces`)
  - Application을 통해서만 Domain에 접근한다.
  - Controller, Request/Response DTO, ExceptionHandler를 둔다.
  - 예외를 공통 응답 형태로 변환한다.
- Application(`application`)
  - Domain에만 의존한다.
  - UseCase, Input Port, Output Port를 정의한다.
  - 여러 도메인 객체 협업 흐름을 오케스트레이션한다.
- Domain(`domain`)
  - 순수 비즈니스 규칙만 포함한다.
  - 어떤 계층에도 의존하지 않는다.
  - Spring/JPA 의존을 금지한다.
- Infrastructure(`infrastructure`)
  - Application의 Output Port를 구현한다(Adapter).
  - DB/JPA/외부 시스템 연동, Config를 담당한다.

### 4-4. Web 요청 흐름 목표
`Client -> Filter -> FilterChain -> Interceptor -> Controller -> Application Service -> Domain -> Response Mapper`

## 5. 품질 게이트
- 백엔드 변경: `./gradlew test`, `./gradlew check`
- 보안 변경: 401/403 실패 케이스 테스트 포함
- 배치/정산 변경: 재실행 시 중복 반영 없음 검증

## 6. 문서 맵
- 실행 체크: `docs/WEEKLY_CHECKLIST.md`
- 학습 로드맵: `docs/JUNIOR_LEARNING_PATH.md`
- 프레젠테이션 매퍼 규칙: `docs/PRESENTATION_MAPPER_CONVENTION.md`
- 테스트 템플릿 가이드: `docs/TEST_TEMPLATE_GUIDE.md`
- 주차별 학습 포인트: `docs/learning/week*/`

## 7. 변경 이력
- v2.0 (2026-03-01): 주니어 학습 중심으로 재구성, 1포인트 1문서 원칙 반영
- v2.1 (2026-03-14): Presentation Mapper 위치/네이밍/책임 규칙 문서 링크 추가
- v2.2 (2026-03-15): 계층별 테스트 템플릿/fixture 가이드 링크 추가
