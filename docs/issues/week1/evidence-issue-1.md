# W1 Processing Issue #1 Evidence

## Branch
- `codex/w1-processing-foundation-1`

## Checklist Evidence
- Java 21 확인
  - `openjdk version "21.0.10" 2026-01-20`
- 빌드/검증 성공
  - `./gradlew test`: BUILD SUCCESSFUL
  - `./gradlew check`: BUILD SUCCESSFUL
- PostgreSQL 연결 확인
  - `pg_isready -h localhost -p 5432`: `accepting connections`
- Health API 확인
  - `GET /health` -> 200, body: `{"status":"UP"}`
  - `GET /actuator/health` -> 200

## Notes
- `gradle` 계열 명령은 샌드박스 제약으로 권한 상승 실행이 필요했다.
- Spring Boot 최소 스켈레톤 + `/health` 엔드포인트를 이번 이슈 범위에서 추가했다.

