package com.commerce.backoffice.infrastructure.order.persistence;

import com.commerce.backoffice.application.order.port.out.OrderPersistencePort;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderLine;
import com.commerce.backoffice.domain.order.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/*
 * Order 아웃바운드 포트의 JDBC 구현체.
 * - 주문 헤더(orders) + 주문 라인(order_items)을 함께 저장/조회한다.
 */
@Component
@Primary
@ConditionalOnBean(JdbcTemplate.class)
public class JdbcOrderPersistenceAdapter implements OrderPersistencePort {

    private static final RowMapper<OrderRow> ORDER_ROW_MAPPER = (rs, rowNum) -> new OrderRow(
        rs.getLong("id"),
        rs.getLong("member_id"),
        OrderStatus.valueOf(rs.getString("order_status"))
    );

    private static final RowMapper<OrderLine> ORDER_LINE_ROW_MAPPER = (rs, rowNum) -> new OrderLine(
        rs.getLong("product_id"),
        rs.getInt("quantity"),
        rs.getBigDecimal("unit_price")
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcOrderPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Order save(Long memberId, List<OrderLine> orderLines) {
        BigDecimal totalAmount = orderLines.stream()
            .map(OrderLine::lineAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String insertOrderSql = """
            insert into orders (member_id, order_status, total_amount)
            values (?, ?, ?)
            returning id
            """;

        Long orderId = jdbcTemplate.queryForObject(
            insertOrderSql,
            Long.class,
            memberId,
            OrderStatus.CREATED.name(),
            totalAmount
        );

        if (orderId == null) {
            throw new IllegalStateException("주문 저장 후 ID를 반환받지 못했습니다.");
        }

        String insertOrderItemSql = """
            insert into order_items (order_id, product_id, quantity, unit_price)
            values (?, ?, ?, ?)
            """;

        for (OrderLine line : orderLines) {
            jdbcTemplate.update(
                insertOrderItemSql,
                orderId,
                line.productId(),
                line.quantity(),
                line.unitPrice()
            );
        }

        return Order.create(orderId, memberId, orderLines);
    }

    @Override
    public Optional<Order> findById(long orderId) {
        String selectOrderSql = """
            select id, member_id, order_status
            from orders
            where id = ?
            """;

        Optional<OrderRow> orderRow = jdbcTemplate.query(selectOrderSql, ORDER_ROW_MAPPER, orderId)
            .stream()
            .findFirst();

        if (orderRow.isEmpty()) {
            return Optional.empty();
        }

        String selectOrderItemsSql = """
            select product_id, quantity, unit_price
            from order_items
            where order_id = ?
            order by id
            """;

        List<OrderLine> orderLines = jdbcTemplate.query(selectOrderItemsSql, ORDER_LINE_ROW_MAPPER, orderId);
        if (orderLines.isEmpty()) {
            return Optional.empty();
        }

        OrderRow row = orderRow.get();
        return Optional.of(Order.restore(row.id(), row.memberId(), orderLines, row.status()));
    }

    @Override
    public void updateStatus(long orderId, OrderStatus status) {
        String sql = """
            update orders
            set order_status = ?,
                updated_at = current_timestamp
            where id = ?
            """;
        jdbcTemplate.update(sql, status.name(), orderId);
    }

    private record OrderRow(
        Long id,
        Long memberId,
        OrderStatus status
    ) {
    }
}
