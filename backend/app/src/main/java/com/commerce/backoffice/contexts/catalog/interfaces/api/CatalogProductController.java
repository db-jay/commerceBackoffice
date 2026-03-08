package com.commerce.backoffice.contexts.catalog.interfaces.api;

import com.commerce.backoffice.contexts.catalog.application.command.CreateCatalogProductCommand;
import com.commerce.backoffice.contexts.catalog.application.port.in.CatalogProductUseCase;
import com.commerce.backoffice.contexts.catalog.domain.Product;
import com.commerce.backoffice.contexts.catalog.interfaces.api.dto.CatalogProductResponse;
import com.commerce.backoffice.contexts.catalog.interfaces.api.dto.CreateCatalogProductRequest;
import com.commerce.backoffice.contexts.catalog.interfaces.api.dto.ReserveCatalogProductStockRequest;
import com.commerce.backoffice.interfaces.common.response.BaseResponse;
import com.commerce.backoffice.interfaces.common.response.ResponseMapper;
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
 * Catalog 상품 API Controller (interfaces 계층).
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

    public CatalogProductController(
        CatalogProductUseCase catalogProductUseCase,
        ResponseMapper responseMapper
    ) {
        this.catalogProductUseCase = catalogProductUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CatalogProductResponse>> create(
        @Valid @RequestBody CreateCatalogProductRequest request
    ) {
        Product created = catalogProductUseCase.create(
            new CreateCatalogProductCommand(request.name(), request.price(), request.stockQuantity())
        );

        return responseMapper.ok(CatalogProductResponse.from(created));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<CatalogProductResponse>> getById(@PathVariable long productId) {
        Product product = catalogProductUseCase.getById(productId);
        return responseMapper.ok(CatalogProductResponse.from(product));
    }

    @PatchMapping("/{productId}/stock/reserve")
    public ResponseEntity<BaseResponse<CatalogProductResponse>> reserveStock(
        @PathVariable long productId,
        @Valid @RequestBody ReserveCatalogProductStockRequest request
    ) {
        Product product = catalogProductUseCase.reserveStock(productId, request.quantity());
        return responseMapper.ok(CatalogProductResponse.from(product));
    }
}
