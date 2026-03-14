package com.commerce.backoffice.presentation.catalog.api.mapper;

import com.commerce.backoffice.application.catalog.command.ChangeCatalogProductStatusCommand;
import com.commerce.backoffice.application.catalog.command.CreateCatalogProductCommand;
import com.commerce.backoffice.application.catalog.command.UpdateCatalogProductCommand;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.presentation.catalog.api.dto.CatalogProductResponse;
import com.commerce.backoffice.presentation.catalog.api.dto.ChangeCatalogProductStatusRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.CreateCatalogProductRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.UpdateCatalogProductRequest;
import org.springframework.stereotype.Component;

/*
 * Catalog 프레젠테이션 매퍼 구현체.
 * - 지금은 수동 매핑을 사용한다(입문자 관점에서 디버깅/추적이 쉬움).
 */
@Component
public class CatalogProductPresentationMapperImpl implements CatalogProductPresentationMapper {

    @Override
    public CreateCatalogProductCommand toCreateCommand(CreateCatalogProductRequest request) {
        return new CreateCatalogProductCommand(request.name(), request.price(), request.stockQuantity());
    }

    @Override
    public UpdateCatalogProductCommand toUpdateCommand(UpdateCatalogProductRequest request) {
        return new UpdateCatalogProductCommand(request.name(), request.price());
    }

    @Override
    public ChangeCatalogProductStatusCommand toChangeStatusCommand(ChangeCatalogProductStatusRequest request) {
        return new ChangeCatalogProductStatusCommand(request.status());
    }

    @Override
    public CatalogProductResponse toResponse(Product product) {
        return new CatalogProductResponse(
            product.id(),
            product.name(),
            product.price(),
            product.stockQuantity(),
            product.status().name()
        );
    }
}

