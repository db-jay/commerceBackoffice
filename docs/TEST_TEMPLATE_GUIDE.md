# 테스트 템플릿 가이드 (Week 2)

## 왜 템플릿이 필요한가?
테스트를 기능마다 제각각 작성하면,
나중에 새 기능을 만들 때 어디서부터 시작해야 할지 헷갈립니다.

그래서 이 프로젝트는 계층별로 "출발점 템플릿"을 둡니다.

- Domain 테스트: 규칙 자체를 빠르게 검증
- Application 테스트: Port를 Mock으로 바꿔 유스케이스 흐름 검증
- API 테스트: MockMvc + 실제 스프링 컨텍스트로 HTTP 흐름 검증

---

## 1. Domain 테스트 템플릿
위치 예시:
- `backend/app/src/test/java/com/commerce/backoffice/domain/catalog/ProductTest.java`
- `backend/app/src/test/java/com/commerce/backoffice/domain/order/OrderTest.java`

목표:
- 순수 규칙 검증
- Spring/DB 없이 빠르게 실행

패턴:
- given: 도메인 객체 생성
- when: 규칙 실행
- then: 상태/예외 검증

---

## 2. Application 테스트 템플릿
공통 템플릿:
- `ApplicationServiceTestTemplate`

예시:
- `AuthApplicationServiceTest`

목표:
- Application Service가 Port를 올바르게 호출하는지 검증
- 인프라 없이 UseCase 흐름만 검증

패턴:
- `@Mock`: Output Port
- `@InjectMocks`: Application Service
- 성공/실패 흐름 둘 다 검증

---

## 3. API 통합 테스트 템플릿
공통 템플릿:
- `ApiIntegrationTestTemplate`

예시:
- `CatalogProductFlowIntegrationTest`
- `MemberFlowIntegrationTest`
- `OrderFlowIntegrationTest`
- `AuthSecurityIntegrationTest`

목표:
- Controller → Application → Domain → Infrastructure 연결 확인
- 공통 응답 포맷, 인증/인가, validation 확인

제공 헬퍼:
- `bearerToken()`
- `adminAccessToken()`
- `extractLongData()`
- `extractStringData()`

---

## 4. Fixture 최소 규칙
공통 Fixture:
- `TestFixtureFactory`

역할:
- 테스트 기본 데이터를 한 곳에서 빠르게 생성

현재 제공 예시:
- `product(...)`
- `member(...)`
- `orderLine(...)`
- `operator(...)`
- `tokenPair(...)`

원칙:
- 처음에는 읽기 쉬운 정적 메서드로 유지
- Fixture가 너무 복잡해지면 Builder 패턴으로 확장

---

## 5. 신규 기능 만들 때 시작 순서
1. Domain 규칙이 있으면 Domain 테스트 먼저 작성
2. UseCase 흐름이 있으면 Application 테스트 작성
3. API가 열리면 API 통합 테스트 추가
4. 반복 데이터는 `TestFixtureFactory`에 올릴지 검토

---

## 6. 한 줄 요약
"규칙은 Domain 테스트, 흐름은 Application 테스트, 실제 요청은 API 테스트"로 나누면
초보자도 테스트 목적을 구분해서 작성할 수 있습니다.
