package com.commerce.backoffice.contexts.catalog.interfaces.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/*
 * 상품 생성 요청 DTO.
 *
 * 1차 검증 위치:
 * - HTTP 요청 형식/기본 범위 검증은 여기서 처리한다.
 * - 도메인 규칙(재고 예약/상태 전이)은 domain 객체에서 처리한다.
 */
public record CreateCatalogProductRequest(
    @NotBlank(message = "상품명은 필수입니다.")
    String name,

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    BigDecimal price,

    @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
    int stockQuantity
) {
}
