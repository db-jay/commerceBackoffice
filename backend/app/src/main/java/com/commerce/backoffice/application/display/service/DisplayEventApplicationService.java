package com.commerce.backoffice.application.display.service;

import com.commerce.backoffice.application.catalog.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.application.display.command.CreateDisplayEventCommand;
import com.commerce.backoffice.application.display.model.ProductExposureResult;
import com.commerce.backoffice.application.display.port.in.DisplayEventUseCase;
import com.commerce.backoffice.application.display.port.out.DisplayEventPersistencePort;
import com.commerce.backoffice.domain.display.DisplayEvent;
import com.commerce.backoffice.domain.display.DisplayEventStatus;
import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * display/event 컨텍스트 유스케이스 구현체.
 *
 * 핵심 포인트:
 * - 상품 존재 여부 검증은 Application에서 조합한다.
 * - 실제 기간/활성 여부 판단은 DisplayEvent 도메인 객체가 담당한다.
 */
@Service
public class DisplayEventApplicationService implements DisplayEventUseCase {

    private final DisplayEventPersistencePort displayEventPersistencePort;
    private final CatalogProductPersistencePort catalogProductPersistencePort;

    public DisplayEventApplicationService(
        DisplayEventPersistencePort displayEventPersistencePort,
        CatalogProductPersistencePort catalogProductPersistencePort
    ) {
        this.displayEventPersistencePort = displayEventPersistencePort;
        this.catalogProductPersistencePort = catalogProductPersistencePort;
    }

    @Override
    @Transactional
    public DisplayEvent create(CreateDisplayEventCommand command) {
        Set<Long> productIds = normalizeProductIds(command.productIds());
        validateProductsExist(productIds);

        DisplayEventStatus status = DisplayEventStatus.valueOf(command.status());
        return displayEventPersistencePort.save(
            command.name(),
            status,
            command.startAt(),
            command.endAt(),
            productIds
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductExposureResult getProductExposure(long productId, LocalDateTime evaluatedAt) {
        validateProductsExist(Set.of(productId));

        List<DisplayEvent> displayEvents = displayEventPersistencePort.findByProductId(productId);
        return displayEvents.stream()
            .filter(event -> event.isExposed(productId, evaluatedAt))
            .findFirst()
            .map(event -> ProductExposureResult.exposed(productId, evaluatedAt, event.id(), event.name()))
            .orElseGet(() -> ProductExposureResult.hidden(productId, evaluatedAt));
    }

    private Set<Long> normalizeProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("productIds는 1개 이상이어야 합니다.");
        }
        return new LinkedHashSet<>(productIds);
    }

    private void validateProductsExist(Set<Long> productIds) {
        if (catalogProductPersistencePort.findByIds(productIds).size() != productIds.size()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }
}
