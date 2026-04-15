# Week 7 Issue Pack (Processing vs Learning)

이 디렉터리는 7주차 이슈를 `처리(구현)`와 `학습` 트랙으로 분리해 발행하기 위한 패키지다.

## 생성 이슈 구성
1. `[W7][진행관리] 7주차 실행 컨트롤타워`
2. `[W7][처리] claim 컨텍스트 1차 구현`
3. `[W7][학습] claim 컨텍스트와 환불 연계 이해`

## 이번 주가 큰 커머스 구조에서 가지는 의미
- W7은 `delivery`로 주문 이후 fulfillment 흐름을 분리한 다음, 그 뒤에 따라오는 `claim` 운영 흐름으로 넘어가는 주차다.
- 즉 "보내기(delivery)" 다음 단계로 "되돌리기/조정하기(claim)"를 배우며, 취소/반품/교환과 환불 연계를 별도 컨텍스트로 이해하는 주차다.

## 운영 규칙
- 처리 트랙 이슈는 코드/테스트/증빙 중심으로 완료한다.
- 학습 트랙 이슈는 체크리스트를 두지 않고, 주니어가 읽고 숙지할 핵심 내용 중심으로 작성한다.
- 이슈 생성 시 라벨을 반드시 부착한다.
  - 공통: `week:7`
  - 처리: `track:processing`
  - 학습: `track:learning`
  - 진행관리: `track:control`

## 발행 방법
```bash
bash docs/issues/week7/create_issues.sh
```
