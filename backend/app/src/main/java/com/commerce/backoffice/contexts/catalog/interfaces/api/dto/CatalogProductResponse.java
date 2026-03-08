package com.commerce.backoffice.contexts.catalog.interfaces.api.dto;

import com.commerce.backoffice.contexts.catalog.domain.Product;
import java.math.BigDecimal;

/*
 * 상품 응답 DTO.
 *
 * Controller는 도메인 객체를 그대로 외부에 노출하지 않고,
 * 응답 전용 모델로 변환해서 반환한다.
 */
public record CatalogProductResponse(
    Long id,
    String name,
    BigDecimal price,
    int stockQuantity,
    String status
) {

    public static CatalogProductResponse from(Product product) {
        return new CatalogProductResponse(
            product.id(),
            product.name(),
            product.price(),
            product.stockQuantity(),
            product.status().name()
        );
    }
}
