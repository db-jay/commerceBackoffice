package com.commerce.backoffice.application.order.service;

import com.commerce.backoffice.application.order.command.CreateOrderCommand;
import com.commerce.backoffice.application.order.command.CreateOrderCommand.CreateOrderLineCommand;
import com.commerce.backoffice.application.order.port.in.OrderUseCase;
import com.commerce.backoffice.application.order.port.out.OrderPersistencePort;
import com.commerce.backoffice.domain.exception.BusinessException;
import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderLine;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Order 유스케이스 구현체.
 * - 주문 생성/조회 흐름을 오케스트레이션한다.
 */
@Service
public class OrderApplicationService implements OrderUseCase {

    private final OrderPersistencePort orderPersistencePort;

    public OrderApplicationService(OrderPersistencePort orderPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    @Transactional
    public Order create(CreateOrderCommand command) {
        List<OrderLine> orderLines = command.orderLines()
            .stream()
            .map(this::toOrderLine)
            .toList();

        return orderPersistencePort.save(command.memberId(), orderLines);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getById(long orderId) {
        return orderPersistencePort.findById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private OrderLine toOrderLine(CreateOrderLineCommand command) {
        return new OrderLine(command.productId(), command.quantity(), command.unitPrice());
    }
}

