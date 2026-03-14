package com.commerce.backoffice.application.order.port.out;

import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderLine;
import java.util.List;
import java.util.Optional;

/*
 * Order 영속화 아웃바운드 포트.
 */
public interface OrderPersistencePort {

    Order save(Long memberId, List<OrderLine> orderLines);

    Optional<Order> findById(long orderId);
}

