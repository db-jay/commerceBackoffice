package com.commerce.backoffice.application.delivery.port.out;

import com.commerce.backoffice.domain.delivery.Delivery;
import java.util.Optional;

public interface DeliveryPersistencePort {

    Delivery save(long orderId);

    Optional<Delivery> findById(long deliveryId);

    void update(Delivery delivery);
}
