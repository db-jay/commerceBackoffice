package com.commerce.backoffice.application.catalog.port.out;

import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.catalog.ProductStatus;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    /*
     * 여러 상품을 한 번에 조회해 주문 생성/취소 시 불필요한 반복 조회를 줄인다.
     */
    Map<Long, Product> findByIds(Set<Long> productIds);

    void updateBasicInfo(long productId, String name, BigDecimal price);

    void updateStatus(long productId, ProductStatus status);

    /*
     * [역할]
     * - 재고 변경을 DB에 반영하면서 version도 함께 확인한다.
     *
     * [왜 boolean을 반환하나]
     * - rows updated = 0 이면 "상품이 없거나, version이 이미 바뀌었거나" 둘 중 하나다.
     * - 이 프로젝트에서는 findById를 먼저 했으므로, 보통은 version 충돌로 해석한다.
     */
    boolean updateStock(Product product);
}
