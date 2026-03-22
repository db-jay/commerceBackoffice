package com.commerce.backoffice.infrastructure.order.persistence;

import com.commerce.backoffice.application.order.port.out.OrderPersistencePort;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderLine;
import com.commerce.backoffice.domain.order.OrderStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/*
 * DB 미사용 환경 대비용 In-Memory 주문 어댑터.
 */
@Component
public class InMemoryOrderPersistenceAdapter implements OrderPersistencePort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Long memberId, List<OrderLine> orderLines) {
        long id = sequence.getAndIncrement();
        Order order = Order.create(id, memberId, orderLines);
        orders.put(id, copy(order));
        return copy(order);
    }

    @Override
    public Optional<Order> findById(long orderId) {
        return Optional.ofNullable(orders.get(orderId))
            .map(this::copy);
    }

    @Override
    public void updateStatus(long orderId, OrderStatus status) {
        Order current = orders.get(orderId);
        if (current == null) {
            return;
        }
        orders.put(orderId, Order.restore(current.id(), current.memberId(), current.orderLines(), status));
    }

    private Order copy(Order order) {
        return Order.restore(order.id(), order.memberId(), order.orderLines(), order.status());
    }
}
