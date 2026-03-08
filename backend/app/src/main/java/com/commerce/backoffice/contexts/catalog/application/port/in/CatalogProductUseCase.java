package com.commerce.backoffice.contexts.catalog.application.port.in;

import com.commerce.backoffice.contexts.catalog.application.command.CreateCatalogProductCommand;
import com.commerce.backoffice.contexts.catalog.domain.Product;

/*
 * Catalog 상품 유스케이스(Input Port).
 *
 * 규칙:
 * - interfaces 계층은 이 인터페이스만 바라본다.
 * - 구현체가 어떻게 DB를 쓰는지는 interfaces가 알 필요가 없다.
 */
public interface CatalogProductUseCase {

    Product create(CreateCatalogProductCommand command);

    Product getById(long productId);

    Product reserveStock(long productId, int quantity);
}
