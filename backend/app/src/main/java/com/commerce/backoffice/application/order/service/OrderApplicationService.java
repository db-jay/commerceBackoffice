package com.commerce.backoffice.application.order.service;

import com.commerce.backoffice.application.catalog.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.application.order.command.CreateOrderCommand;
import com.commerce.backoffice.application.order.command.CreateOrderCommand.CreateOrderLineCommand;
import com.commerce.backoffice.application.order.port.in.OrderUseCase;
import com.commerce.backoffice.application.order.port.out.OrderPersistencePort;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderLine;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * [역할]
 * - 주문 생성/조회 흐름을 오케스트레이션하는 Application Service다.
 *
 * [왜 여기서 재고를 다루나]
 * - "주문을 저장하기 전에 재고를 먼저 줄여야 한다"는 흐름 조합은 Application 책임이다.
 * - 실제 재고 충분 여부 판단은 Product 도메인 객체가 수행한다.
 *
 * [흐름]
 * 1. 요청 DTO가 CreateOrderCommand로 변환되어 들어온다.
 * 2. 주문 라인을 OrderLine으로 바꾼다.
 * 3. 상품별 총 주문 수량을 계산한다.
 * 4. 각 상품의 재고를 reserveStock()으로 차감한다.
 * 5. version 충돌이 없으면 주문을 저장한다.
 *
 * [주의할 점]
 * - 재고 부족과 동시성 충돌은 다른 실패다.
 *   - 재고 부족: 비즈니스 규칙 위반
 *   - 동시성 충돌: 누군가 먼저 수정함
 */
@Service
public class OrderApplicationService implements OrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final CatalogProductPersistencePort catalogProductPersistencePort;

    public OrderApplicationService(
        OrderPersistencePort orderPersistencePort,
        CatalogProductPersistencePort catalogProductPersistencePort
    ) {
        this.orderPersistencePort = orderPersistencePort;
        this.catalogProductPersistencePort = catalogProductPersistencePort;
    }

    @Override
    @Transactional
    public Order create(CreateOrderCommand command) {
        List<OrderLine> orderLines = command.orderLines()
            .stream()
            .map(this::toOrderLine)
            .toList();

        reserveOrderStock(orderLines);
        return orderPersistencePort.save(command.memberId(), orderLines);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getById(long orderId) {
        return getOrder(orderId);
    }

    @Override
    @Transactional
    public Order cancel(long orderId) {
        Order order = getOrder(orderId);

        try {
            order.cancel();
        } catch (IllegalStateException ex) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELED);
        }

        releaseOrderStock(order.orderLines());
        orderPersistencePort.updateStatus(order.id(), order.status());
        return order;
    }

    private OrderLine toOrderLine(CreateOrderLineCommand command) {
        return new OrderLine(command.productId(), command.quantity(), command.unitPrice());
    }

    private void reserveOrderStock(List<OrderLine> orderLines) {
        /*
         * 같은 상품이 주문 라인에 여러 번 등장할 수 있으므로
         * 상품별 총 수량으로 한 번만 재고를 줄인다.
         */
        Map<Long, Integer> reserveQuantityByProduct = aggregateQuantityByProduct(orderLines);
        Map<Long, Product> products = loadProducts(reserveQuantityByProduct.keySet());

        for (Map.Entry<Long, Integer> entry : reserveQuantityByProduct.entrySet()) {
            Product product = products.get(entry.getKey());
            try {
                product.reserveStock(entry.getValue());
            } catch (IllegalStateException ex) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
            updateProductStock(product);
        }
    }

    private void releaseOrderStock(List<OrderLine> orderLines) {
        Map<Long, Integer> releaseQuantityByProduct = aggregateQuantityByProduct(orderLines);
        Map<Long, Product> products = loadProducts(releaseQuantityByProduct.keySet());

        for (Map.Entry<Long, Integer> entry : releaseQuantityByProduct.entrySet()) {
            Product product = products.get(entry.getKey());
            product.releaseStock(entry.getValue());
            updateProductStock(product);
        }
    }

    private Order getOrder(long orderId) {
        return orderPersistencePort.findById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private Map<Long, Integer> aggregateQuantityByProduct(List<OrderLine> orderLines) {
        return orderLines.stream()
            .collect(Collectors.groupingBy(
                OrderLine::productId,
                Collectors.summingInt(OrderLine::quantity)
            ));
    }

    private Map<Long, Product> loadProducts(Set<Long> productIds) {
        Map<Long, Product> products = catalogProductPersistencePort.findByIds(productIds);
        if (products.size() == productIds.size()) {
            return products;
        }

        Long missingProductId = productIds.stream()
            .filter(productId -> !products.containsKey(productId))
            .findFirst()
            .orElse(null);
        if (missingProductId != null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return products;
    }

    private void updateProductStock(Product product) {
        boolean updated = catalogProductPersistencePort.updateStock(product);
        if (!updated) {
            throw new BusinessException(ErrorCode.PRODUCT_STOCK_CONFLICT);
        }
    }
}
