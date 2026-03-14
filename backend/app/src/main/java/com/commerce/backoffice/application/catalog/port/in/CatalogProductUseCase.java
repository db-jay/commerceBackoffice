package com.commerce.backoffice.application.catalog.port.in;

import com.commerce.backoffice.application.catalog.command.ChangeCatalogProductStatusCommand;
import com.commerce.backoffice.application.catalog.command.CreateCatalogProductCommand;
import com.commerce.backoffice.application.catalog.command.UpdateCatalogProductCommand;
import com.commerce.backoffice.domain.catalog.Product;

/*
 * Catalog 상품 유스케이스(Input Port).
 *
 * 규칙:
 * - presentation 계층은 이 인터페이스만 바라본다.
 * - 구현체가 어떻게 DB를 쓰는지는 presentation이 알 필요가 없다.
 */
public interface CatalogProductUseCase {

    Product create(CreateCatalogProductCommand command);

    Product getById(long productId);

    Product update(long productId, UpdateCatalogProductCommand command);

    Product changeStatus(long productId, ChangeCatalogProductStatusCommand command);

    Product reserveStock(long productId, int quantity);
}
