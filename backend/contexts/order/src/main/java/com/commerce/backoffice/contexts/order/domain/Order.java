package com.commerce.backoffice.contexts.order.domain;

import java.math.BigDecimal;
import java.util.List;

/*
 * Order 컨텍스트의 주문 애그리거트 초안.
 * - 상태 전이(CREATED -> CONFIRMED, CREATED/CONFIRMED -> CANCELED) 규칙을 보관한다.
 */
public class Order {

    private final Long id;
    private final Long memberId;
    private final List<OrderLine> orderLines;
    private OrderStatus status;
    private BigDecimal totalAmount;

    private Order(Long id, Long memberId, List<OrderLine> orderLines) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id는 양수여야 합니다.");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("memberId는 양수여야 합니다.");
        }
        if (orderLines == null || orderLines.isEmpty()) {
            throw new IllegalArgumentException("orderLines는 1개 이상이어야 합니다.");
        }
        this.id = id;
        this.memberId = memberId;
        this.orderLines = List.copyOf(orderLines);
        this.status = OrderStatus.CREATED;
        this.totalAmount = calculateTotalAmount(orderLines);
    }

    public static Order create(Long id, Long memberId, List<OrderLine> orderLines) {
        return new Order(id, memberId, orderLines);
    }

    public void confirm() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("CREATED 상태에서만 CONFIRMED로 전이할 수 있습니다.");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.status = OrderStatus.CANCELED;
    }

    private BigDecimal calculateTotalAmount(List<OrderLine> lines) {
        return lines.stream()
            .map(OrderLine::lineAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long id() {
        return id;
    }

    public Long memberId() {
        return memberId;
    }

    public List<OrderLine> orderLines() {
        return orderLines;
    }

    public OrderStatus status() {
        return status;
    }

    public BigDecimal totalAmount() {
        return totalAmount;
    }
}

