-- W3 재고 동시성 제어:
-- products 테이블에 version 컬럼을 추가해 낙관적 락의 기준값으로 사용한다.
-- 저장 시 "내가 읽었던 version과 DB의 version이 같은가?"를 비교해서
-- 동시에 들어온 재고 변경 충돌을 감지한다.

alter table products
    add column if not exists version bigint not null default 0;
