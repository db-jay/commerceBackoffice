package com.commerce.backoffice.presentation.catalog.api;

import com.commerce.backoffice.application.catalog.port.in.CatalogProductUseCase;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.presentation.catalog.api.dto.CatalogProductResponse;
import com.commerce.backoffice.presentation.catalog.api.dto.ChangeCatalogProductStatusRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.CreateCatalogProductRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.ReserveCatalogProductStockRequest;
import com.commerce.backoffice.presentation.catalog.api.dto.UpdateCatalogProductRequest;
import com.commerce.backoffice.presentation.catalog.api.mapper.CatalogProductPresentationMapper;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.commerce.backoffice.presentation.common.response.ResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Catalog 상품 API Controller (presentation 계층).
 *
 * 책임:
 * - HTTP 요청/응답 처리
 * - DTO 검증
 * - UseCase 호출
 *
 * 비책임:
 * - SQL/저장소 접근
 * - 도메인 규칙 직접 구현
 */
@RestController
@RequestMapping("/api/catalog/products")
public class CatalogProductController {

    private final CatalogProductUseCase catalogProductUseCase;
    private final ResponseMapper responseMapper;
    private final CatalogProductPresentationMapper presentationMapper;

    public CatalogProductController(
        CatalogProductUseCase catalogProductUseCase,
        ResponseMapper responseMapper,
        CatalogProductPresentationMapper presentationMapper
    ) {
        this.catalogProductUseCase = catalogProductUseCase;
        this.responseMapper = responseMapper;
        this.presentationMapper = presentationMapper;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CatalogProductResponse>> create(
        @Valid @RequestBody CreateCatalogProductRequest request
    ) {
        Product created = catalogProductUseCase.create(presentationMapper.toCreateCommand(request));

        return responseMapper.ok(presentationMapper.toResponse(created));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<CatalogProductResponse>> getById(@PathVariable long productId) {
        Product product = catalogProductUseCase.getById(productId);
        return responseMapper.ok(presentationMapper.toResponse(product));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<BaseResponse<CatalogProductResponse>> update(
        @PathVariable long productId,
        @Valid @RequestBody UpdateCatalogProductRequest request
    ) {
        Product product = catalogProductUseCase.update(
            productId,
            presentationMapper.toUpdateCommand(request)
        );
        return responseMapper.ok(presentationMapper.toResponse(product));
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<BaseResponse<CatalogProductResponse>> changeStatus(
        @PathVariable long productId,
        @Valid @RequestBody ChangeCatalogProductStatusRequest request
    ) {
        Product product = catalogProductUseCase.changeStatus(
            productId,
            presentationMapper.toChangeStatusCommand(request)
        );
        return responseMapper.ok(presentationMapper.toResponse(product));
    }

    @PatchMapping("/{productId}/stock/reserve")
    public ResponseEntity<BaseResponse<CatalogProductResponse>> reserveStock(
        @PathVariable long productId,
        @Valid @RequestBody ReserveCatalogProductStockRequest request
    ) {
        Product product = catalogProductUseCase.reserveStock(productId, request.quantity());
        return responseMapper.ok(presentationMapper.toResponse(product));
    }
}
