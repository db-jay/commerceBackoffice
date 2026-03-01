-- W1 스키마 초안:
-- catalog / member / order 컨텍스트의 최소 테이블을 정의한다.
-- 목적은 "완성형 설계"가 아니라 이후 주차에서 확장 가능한 시작점 만들기.

create table if not exists members (
    id bigserial primary key,
    email varchar(255) not null unique,
    name varchar(100) not null,
    grade varchar(30) not null default 'BASIC',
    status varchar(30) not null default 'ACTIVE',
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create table if not exists products (
    id bigserial primary key,
    name varchar(255) not null,
    price numeric(12,2) not null,
    stock_quantity integer not null,
    status varchar(30) not null default 'ACTIVE',
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp
);

create table if not exists orders (
    id bigserial primary key,
    member_id bigint not null,
    order_status varchar(30) not null default 'CREATED',
    total_amount numeric(12,2) not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    constraint fk_orders_member
        foreign key (member_id) references members(id)
);

create table if not exists order_items (
    id bigserial primary key,
    order_id bigint not null,
    product_id bigint not null,
    quantity integer not null,
    unit_price numeric(12,2) not null,
    created_at timestamp not null default current_timestamp,
    constraint fk_order_items_order
        foreign key (order_id) references orders(id),
    constraint fk_order_items_product
        foreign key (product_id) references products(id)
);

create index if not exists idx_orders_member_id on orders(member_id);
create index if not exists idx_order_items_order_id on order_items(order_id);
create index if not exists idx_order_items_product_id on order_items(product_id);

