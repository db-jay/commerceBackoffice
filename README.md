# Food Commerce Backoffice (Learning Project)

## 프로젝트 개요
- 기간: 2026-02-16 ~ 2026-05-16 (3개월)
- 목표: 식품 이커머스 백오피스 도메인을 직접 설계/구현하며 백엔드 핵심 역량(도메인 모델링, 트랜잭션, 동시성, 성능, 안정성)을 학습한다.
- 범위: Front는 최소 React 화면이 준비되었다고 가정하고, Backend API 중심으로 진행한다.

## 주요 기능
- 상품 관리: 상품, 카테고리, 가격, 재고, 유통기한 관리
- 전시 관리: 배너, 기획전, 노출 순서, 전시 기간 관리
- 이벤트 관리: 쿠폰, 프로모션, 할인 정책, 적용 조건 관리
- 회원 관리: 회원 정보, 등급, 권한, 휴면 상태 관리
- 공급사 관리: 공급사 정보, 계약, 공급가 정책 관리
- 주문 관리: 주문 생성, 결제 결과 반영, 주문 상태 전이
- 클레임 관리: 취소, 반품, 교환 접수/처리
- 배송 관리: 출고, 송장, 배송 상태 추적
- 발주 관리: 발주 생성, 입고 처리, 재고 반영
- 정산 관리: 주문/클레임 기준 정산 금액 계산 및 지급 상태 관리
- 감사 로그: 주요 상태 변경 이력, 운영자 액션 추적

## 기술 스택
- Backend: Java 21, Spring Boot, Spring Data JPA, Spring Validation
- Database: PostgreSQL
- Cache/Lock: Redis (분산락/캐시 필요 구간에 한정 적용)
- Messaging (선택): AWS SQS/SNS 또는 LocalStack 기반 이벤트 실험
- Frontend: React (최소 화면, API 연동 중심)
- Infra: AWS EC2 (프리티어 우선) 또는 Local Docker + LocalStack
- CI/CD: AWS CodePipeline, AWS CodeBuild, AWS CodeDeploy
- Artifact/Image: Amazon ECR
- Test: JUnit5, Mockito, Testcontainers (PostgreSQL)

## 아키텍처(Backend)
- 아키텍처 선택: `Layered + Hexagonal 규칙`의 하이브리드
- 이유: 학습 초반 생산성은 레이어드로 확보하고, 의존성 역전과 도메인 순수성은 헥사고날 원칙으로 강제한다.
- 스타일: DDD 기반 모듈형 모놀리스

### 레이어 구성
- interfaces (Inbound Adapter): Controller, Request/Response DTO, Validation
- application (Use Case): 유스케이스, 트랜잭션 경계, 인가 체크, Port 인터페이스
- domain (Core): Entity, Value Object, Domain Service, Domain Event
- infrastructure (Outbound Adapter): JPA/Redis/외부 API/메시징 구현체

### 의존성 규칙(DIP)
- interfaces -> application -> domain
- infrastructure -> application/domain의 Port 구현
- domain은 Spring, JPA, AWS SDK 등 프레임워크/인프라에 의존하지 않는다.

### Bounded Context
- catalog, display, event, member, supplier, order, claim, delivery, purchase, settlement

### 트랜잭션/동시성 원칙
- 애그리거트 단위 트랜잭션 경계 유지
- 재고/수량 변경은 낙관적 락(version) 기본 적용
- 고경합 구간은 비관적 락 또는 Redis 분산락을 제한적으로 적용
- 멱등성 키(idempotency key)로 중복 요청 방지

## 데이터 플로우
1. 운영자(MD)가 상품/가격/전시 정보를 등록한다.
2. 고객 주문 생성 시 주문 항목 기준 재고 예약을 수행한다.
3. 결제 결과를 반영해 주문 상태를 확정한다.
4. 출고 지시 후 배송 상태를 갱신한다.
5. 취소/반품/교환 발생 시 클레임 정책에 따라 환불/재고 복원/재정산을 처리한다.
6. 정산 배치에서 주문/클레임/수수료를 집계해 공급사 정산 금액을 확정한다.
7. 모든 상태 변경 이벤트는 감사 로그와 이력 테이블에 기록한다.

## 로드맵

## 1개월차 (기초 설계 + 핵심 도메인)
### 1주차
- 프로젝트 멀티모듈 구조 세팅
- 공통 예외/응답 포맷/로깅 규격 정의
- PostgreSQL 스키마 초안 및 마이그레이션 환경 구성
- catalog/member/order 도메인 모델 초안 작성

### 2주차
- 상품/회원 API 구현 (CRUD + 기본 검증)
- 주문 생성 API 구현 (주문 상태 모델 포함)
- 단위/통합 테스트 템플릿 구축

### 3주차
- 재고 예약/차감 로직 구현
- 낙관적 락 기반 동시성 제어 1차 적용
- 주문 생성 시나리오 통합 테스트 작성

### 4주차
- 주문 취소(결제 전/후 분기) 구현
- N+1 점검 및 조회 성능 개선
- 1개월차 회고: 모델/트랜잭션 경계 재정의

## 2개월차 (운영 도메인 확장)
### 1주차
- display/event 컨텍스트 구현
- 전시 기간/이벤트 적용 조건 규칙 반영

### 2주차
- delivery 컨텍스트 구현
- 출고/송장/배송 상태 전이 API 구현

### 3주차
- claim 컨텍스트 구현
- 취소/반품/교환 처리 플로우 및 환불 연계

### 4주차
- supplier/purchase 컨텍스트 구현
- 발주 생성/입고 처리/재고 반영
- 운영자 권한(RBAC) 1차 적용

## 3개월차 (정산 + 품질 고도화)
### 1주차
- settlement 컨텍스트 구현
- 주문/클레임 기반 정산 계산 로직 구현

### 2주차
- 정산 배치/집계 API 구현
- 정산 이력/지급 상태 관리

### 3주차
- 보안 강화(입력 검증, 민감정보 마스킹, 권한 세분화)
- 장애 대응(재시도/타임아웃/예외 정책) 정리

### 4주차
- 성능 점검(슬로우 쿼리, 락 경합, 병목 분석)
- 문서화(ADR, 트러블슈팅, 운영 가이드)
- 최종 데모 및 학습 회고

## CI/CD 파이프라인
1. Git push (GitHub/CodeCommit)
2. CodePipeline 트리거
3. CodeBuild에서 빌드/테스트
4. Docker 이미지 빌드 후 ECR 푸시 (도커 전략 사용 시)
5. CodeDeploy로 EC2 배포

## 완료 기준 (Definition of Done)
- 핵심 API에 단위/통합 테스트가 존재한다.
- 주문/재고/클레임/정산 도메인 규칙을 코드와 문서로 설명할 수 있다.
- 동시성/성능/예외 처리 관점의 리뷰 체크리스트를 통과한다.
