package com.commerce.backoffice.presentation.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/*
 * 상품 기본정보 수정 요청 DTO.
 */
public record UpdateCatalogProductRequest(
    @NotBlank(message = "상품명은 필수입니다.")
    String name,

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    BigDecimal price
) {
}

