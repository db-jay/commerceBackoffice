package com.commerce.backoffice.presentation.catalog.api.dto;

import jakarta.validation.constraints.Positive;

/*
 * 재고 예약 요청 DTO.
 */
public record ReserveCatalogProductStockRequest(
    @Positive(message = "예약 수량은 1 이상이어야 합니다.")
    int quantity
) {
}
