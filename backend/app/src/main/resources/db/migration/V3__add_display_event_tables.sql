-- W5 display/event 컨텍스트:
-- 전시 이벤트의 기본 정보와 상품 대상 연결 테이블을 추가한다.

create table if not exists display_events (
    id bigserial primary key,
    name varchar(255) not null,
    status varchar(30) not null,
    start_at timestamp not null,
    end_at timestamp not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create table if not exists display_event_products (
    id bigserial primary key,
    event_id bigint not null,
    product_id bigint not null,
    created_at timestamp not null default current_timestamp,
    constraint fk_display_event_products_event
        foreign key (event_id) references display_events(id),
    constraint fk_display_event_products_product
        foreign key (product_id) references products(id)
);

create index if not exists idx_display_event_products_event_id on display_event_products(event_id);
create index if not exists idx_display_event_products_product_id on display_event_products(product_id);
