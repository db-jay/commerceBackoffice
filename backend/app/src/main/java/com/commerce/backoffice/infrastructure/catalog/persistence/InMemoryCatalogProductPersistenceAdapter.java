package com.commerce.backoffice.infrastructure.catalog.persistence;

import com.commerce.backoffice.application.catalog.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.catalog.ProductStatus;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/*
 * DB 미사용 환경(예: 일부 테스트)용 In-Memory Adapter.
 *
 * 왜 필요한가?
 * - 기존 HealthController 테스트는 DataSource를 비활성화해서 빠르게 실행된다.
 * - Catalog UseCase 빈이 추가되면서 Output Port 구현이 없으면 컨텍스트 로딩이 실패한다.
 * - 이 어댑터는 "JDBC 구현이 없을 때만" 임시 구현으로 등록되어 테스트 안정성을 유지한다.
 */
@Component
public class InMemoryCatalogProductPersistenceAdapter implements CatalogProductPersistencePort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(String name, BigDecimal price, int stockQuantity) {
        long id = sequence.getAndIncrement();
        Product product = new Product(id, name, price, stockQuantity, ProductStatus.ACTIVE);
        products.put(id, product);
        return product;
    }

    @Override
    public Optional<Product> findById(long productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public void updateBasicInfo(long productId, String name, BigDecimal price) {
        Product current = products.get(productId);
        if (current == null) {
            return;
        }

        Product replaced = new Product(
            current.id(),
            name,
            price,
            current.stockQuantity(),
            current.status()
        );
        products.put(productId, replaced);
    }

    @Override
    public void updateStatus(long productId, ProductStatus status) {
        Product current = products.get(productId);
        if (current == null) {
            return;
        }

        Product replaced = new Product(
            current.id(),
            current.name(),
            current.price(),
            current.stockQuantity(),
            status
        );
        products.put(productId, replaced);
    }

    @Override
    public void updateStockQuantity(long productId, int stockQuantity) {
        Product current = products.get(productId);
        if (current == null) {
            return;
        }

        Product replaced = new Product(
            current.id(),
            current.name(),
            current.price(),
            stockQuantity,
            current.status()
        );
        products.put(productId, replaced);
    }
}
