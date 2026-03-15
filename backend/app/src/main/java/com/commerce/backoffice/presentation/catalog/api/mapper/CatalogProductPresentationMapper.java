package com.commerce.backoffice.presentation.catalog.api.mapper;

import com.commerce.backoffice.application.catalog.command.ChangeCatalogProductStatusCommand;
import com.commerce.backoffice.application.catalog.command.CreateCatalogProductCommand;
import com.commerce.backoffice.application.catalog.command.UpdateCatalogProductCommand;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.presentation.catalog.api.dto.CatalogProductResponse;
import com.commerce.backoffice.presentation.catalog.api.dto.ChangeCatalogProductStatusRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.CreateCatalogProductRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.UpdateCatalogProductRequest;

/*
 * Catalog 프레젠테이션 매퍼 계약.
 *
 * 왜 인터페이스를 두나?
 * - Controller가 "변환 규칙 구현체"에 직접 묶이지 않게 하기 위해서다.
 * - 나중에 매핑 방식(MapStruct/수동 매핑)을 바꿔도 Controller는 영향이 거의 없다.
 */
public interface CatalogProductPresentationMapper {

    CreateCatalogProductCommand toCreateCommand(CreateCatalogProductRequest request);

    UpdateCatalogProductCommand toUpdateCommand(UpdateCatalogProductRequest request);

    ChangeCatalogProductStatusCommand toChangeStatusCommand(ChangeCatalogProductStatusRequest request);

    CatalogProductResponse toResponse(Product product);
}

