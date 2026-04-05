-- W6 delivery 컨텍스트:
-- 주문 이후 fulfillment 흐름을 위한 최소 배송 테이블을 추가한다.

create table if not exists deliveries (
    id bigserial primary key,
    order_id bigint not null,
    delivery_status varchar(30) not null default 'READY',
    tracking_number varchar(100),
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    constraint fk_deliveries_order
        foreign key (order_id) references orders(id)
);

create index if not exists idx_deliveries_order_id on deliveries(order_id);
