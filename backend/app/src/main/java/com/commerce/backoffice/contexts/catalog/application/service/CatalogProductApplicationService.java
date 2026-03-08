package com.commerce.backoffice.contexts.catalog.application.service;

import com.commerce.backoffice.contexts.catalog.application.command.CreateCatalogProductCommand;
import com.commerce.backoffice.contexts.catalog.application.port.in.CatalogProductUseCase;
import com.commerce.backoffice.contexts.catalog.application.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.contexts.catalog.domain.Product;
import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import org.springframework.stereotype.Service;

/*
 * Catalog 유스케이스 구현체(Application Service).
 *
 * 이 클래스가 하는 일:
 * 1) 요청 흐름을 조합한다.
 * 2) 도메인 규칙을 실행한다.
 * 3) Output Port를 통해 저장/조회를 수행한다.
 *
 * 이 클래스가 하지 않는 일:
 * - SQL 직접 작성
 * - HTTP 응답 포맷 조립
 */
@Service
public class CatalogProductApplicationService implements CatalogProductUseCase {

    private final CatalogProductPersistencePort catalogProductPersistencePort;

    public CatalogProductApplicationService(CatalogProductPersistencePort catalogProductPersistencePort) {
        this.catalogProductPersistencePort = catalogProductPersistencePort;
    }

    @Override
    public Product create(CreateCatalogProductCommand command) {
        // 1차 검증은 DTO(@Valid)에서 수행하고, 여기서는 도메인 생성까지 연결한다.
        return catalogProductPersistencePort.save(
            command.name(),
            command.price(),
            command.stockQuantity()
        );
    }

    @Override
    public Product getById(long productId) {
        return catalogProductPersistencePort.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public Product reserveStock(long productId, int quantity) {
        Product product = catalogProductPersistencePort.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        try {
            // 핵심 비즈니스 규칙: 재고 예약은 도메인 객체가 판단한다.
            product.reserveStock(quantity);
        } catch (IllegalStateException ex) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }

        catalogProductPersistencePort.updateStockQuantity(product.id(), product.stockQuantity());
        return product;
    }
}
