package com.commerce.backoffice.infrastructure.delivery.persistence;

import com.commerce.backoffice.application.delivery.port.out.DeliveryPersistencePort;
import com.commerce.backoffice.domain.delivery.Delivery;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryDeliveryPersistenceAdapter implements DeliveryPersistencePort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, Delivery> deliveries = new ConcurrentHashMap<>();

    @Override
    public Delivery save(long orderId) {
        long id = sequence.getAndIncrement();
        Delivery delivery = Delivery.create(id, orderId);
        deliveries.put(id, copy(delivery));
        return copy(delivery);
    }

    @Override
    public Optional<Delivery> findById(long deliveryId) {
        return Optional.ofNullable(deliveries.get(deliveryId))
            .map(this::copy);
    }

    @Override
    public void update(Delivery delivery) {
        deliveries.put(delivery.id(), copy(delivery));
    }

    private Delivery copy(Delivery delivery) {
        return Delivery.restore(
            delivery.id(),
            delivery.orderId(),
            delivery.status(),
            delivery.trackingNumber()
        );
    }
}
