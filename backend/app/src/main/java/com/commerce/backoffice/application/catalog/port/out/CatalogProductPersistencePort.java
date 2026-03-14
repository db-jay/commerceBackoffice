package com.commerce.backoffice.application.catalog.port.out;

import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.catalog.ProductStatus;
import java.math.BigDecimal;
import java.util.Optional;

/*
 * Catalog 상품 영속화(Output Port).
 *
 * 핵심 포인트:
 * - application은 "저장/조회가 필요하다"는 계약만 정의한다.
 * - 실제 JDBC/JPA 구현은 infrastructure가 담당한다.
 */
public interface CatalogProductPersistencePort {

    Product save(String name, BigDecimal price, int stockQuantity);

    Optional<Product> findById(long productId);

    void updateBasicInfo(long productId, String name, BigDecimal price);

    void updateStatus(long productId, ProductStatus status);

    void updateStockQuantity(long productId, int stockQuantity);
}
