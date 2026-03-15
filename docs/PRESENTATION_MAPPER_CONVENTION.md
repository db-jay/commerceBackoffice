# Presentation Mapper 규칙 (입문자용)

## 1) 왜 Mapper를 두나요?
Controller가 아래 두 가지를 동시에 하면 코드가 빨리 복잡해집니다.
- HTTP 처리(요청 받기, 응답 내리기)
- 데이터 변환(DTO ↔ Command, Domain ↔ Response)

그래서 역할을 분리합니다.
- Controller: "흐름 제어"
- Mapper: "변환 규칙"

이렇게 나누면 읽기/테스트/교체가 쉬워집니다.

---

## 2) 현재 프로젝트의 표준

### 2-1. 위치 규칙
각 API 컨텍스트에서 Mapper는 아래 경로를 사용합니다.

- `presentation/{context}/api/mapper`

예시:
- `presentation/catalog/api/mapper/CatalogProductPresentationMapper`
- `presentation/member/api/mapper/MemberPresentationMapper`
- `presentation/order/api/mapper/OrderPresentationMapper`

### 2-2. 네이밍 규칙
- 인터페이스: `{도메인/리소스}PresentationMapper`
- 구현체: `{도메인/리소스}PresentationMapperImpl`

예시:
- `CatalogProductPresentationMapper`
- `CatalogProductPresentationMapperImpl`

### 2-3. 의존 규칙
- Controller는 **인터페이스** 타입에만 의존한다.
- 구현체는 Spring Bean(`@Component`)으로 주입받는다.

즉, Controller는 "어떻게 매핑하는지"는 몰라도 됩니다.

---

## 3) Mapper가 담당하는 일

### 입력 매핑
- Request DTO → Application Command

### 출력 매핑
- Domain Entity/Model → Response DTO

> 원칙: Response DTO는 가능하면 "데이터 구조"만 가지게 하고,
> 변환 로직은 Mapper에 둡니다.

---

## 4) 코드 템플릿

```java
public interface MemberPresentationMapper {
    CreateMemberCommand toCreateCommand(CreateMemberRequest request);
    MemberResponse toResponse(Member member);
}
```

```java
@Component
public class MemberPresentationMapperImpl implements MemberPresentationMapper {
    @Override
    public CreateMemberCommand toCreateCommand(CreateMemberRequest request) {
        return new CreateMemberCommand(request.email(), request.name());
    }

    @Override
    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
            member.id(),
            member.email(),
            member.name(),
            member.grade().name(),
            member.status().name()
        );
    }
}
```

---

## 5) 왜 지금은 수동 매핑(manual mapping)인가?
학습 단계에서는 아래 장점이 큽니다.
- 디버깅 시 값 흐름을 한 줄씩 추적하기 쉽다.
- 어떤 필드가 어디서 바뀌는지 명확하다.

필드 수가 커지고 반복이 많아지면,
그때 `MapStruct` 같은 자동 매핑 도입을 검토합니다.

---

## 6) 체크리스트 (PR 전에 확인)
- [ ] Controller에 `new XxxCommand(...)`가 남아있지 않은가?
- [ ] Controller에 `Response.from(...)` 같은 변환 로직이 남아있지 않은가?
- [ ] Mapper 인터페이스/구현체가 `api/mapper` 경로에 있는가?
- [ ] 변환 규칙 변경 시 관련 통합 테스트가 통과하는가?

---

## 7) 한 줄 요약
"Controller는 흐름만, Mapper는 변환만"을 지키면
코드가 단순해지고, 유지보수가 쉬워집니다.
