package com.commerce.backoffice.contexts.order.domain;

import java.math.BigDecimal;

/*
 * 주문 항목 값 객체 초안.
 * - 주문 단가 * 수량으로 라인 금액을 계산한다.
 */
public class OrderLine {

    private final Long productId;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderLine(Long productId, int quantity, BigDecimal unitPrice) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("productId는 양수여야 합니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("unitPrice는 0 이상이어야 합니다.");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal lineAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long productId() {
        return productId;
    }

    public int quantity() {
        return quantity;
    }

    public BigDecimal unitPrice() {
        return unitPrice;
    }
}

