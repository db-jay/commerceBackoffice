# Week 6 Issue Pack (Processing vs Learning)

    이 디렉터리는 6주차 이슈를 `처리(구현)`와 `학습` 트랙으로 분리해 발행하기 위한 패키지다.

## 생성 이슈 구성
1. `[W6][진행관리] 6주차 실행 컨트롤타워`
2. `[W6][처리] delivery 컨텍스트 1차 구현 + 출고/송장/배송 상태 전이 API`
3. `[W6][학습] delivery 상태 전이와 출고 흐름 이해`

## 이번 주가 큰 커머스 구조에서 가지는 의미
- W6는 `display/event`로 전시 정책을 분리한 다음, 실제 주문 이후 운영 흐름을 담당하는 `delivery` 컨텍스트로 넘어가는 주차다.
- 즉 "보여주기(display)" 다음 단계로 "보내기(delivery)"를 배우며, 주문 이후 fulfillment 관점의 책임을 분리해서 이해하는 주차다.

    ## 운영 규칙
    - 처리 트랙 이슈는 코드/테스트/증빙 중심으로 완료한다.
    - 학습 트랙 이슈는 체크리스트를 두지 않고, 주니어가 읽고 숙지할 핵심 내용 중심으로 작성한다.
    - 이슈 생성 시 라벨을 반드시 부착한다.
      - 공통: `week:6`
      - 처리: `track:processing`
      - 학습: `track:learning`
      - 진행관리: `track:control`

    ## 발행 방법
    ```bash
    bash docs/issues/week6/create_issues.sh
    ```
