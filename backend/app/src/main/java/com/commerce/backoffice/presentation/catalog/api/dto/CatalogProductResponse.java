package com.commerce.backoffice.presentation.catalog.api.dto;

import java.math.BigDecimal;

/*
 * 상품 응답 DTO.
 *
 * 주의:
 * - 이 DTO는 "응답 데이터 구조"만 표현한다.
 * - Domain -> DTO 변환은 presentation/mapper 계층이 담당한다.
 */
public record CatalogProductResponse(
    Long id,
    String name,
    BigDecimal price,
    int stockQuantity,
    String status
) {
}
