package com.commerce.backoffice.application.order.port.in;

import com.commerce.backoffice.application.order.command.CreateOrderCommand;
import com.commerce.backoffice.domain.order.Order;

/*
 * Order 인바운드 포트(UseCase).
 */
public interface OrderUseCase {

    Order create(CreateOrderCommand command);

    Order getById(long orderId);
}

