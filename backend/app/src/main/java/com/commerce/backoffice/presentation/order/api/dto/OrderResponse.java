package com.commerce.backoffice.presentation.order.api.dto;

import java.math.BigDecimal;
import java.util.List;

/*
 * 주문 응답 DTO.
 * - 응답 스키마만 정의한다.
 * - Domain -> DTO 변환은 OrderPresentationMapper가 담당한다.
 */
public record OrderResponse(
    Long id,
    Long memberId,
    String status,
    BigDecimal totalAmount,
    List<OrderLineResponse> orderLines
) {

    public record OrderLineResponse(
        Long productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineAmount
    ) {
    }
}
