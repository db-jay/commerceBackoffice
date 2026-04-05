package com.commerce.backoffice.domain.delivery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DeliveryTest {

    @Test
    void registerTrackingAndChangeStatus_shouldFollowAllowedSequence() {
        Delivery delivery = Delivery.create(1L, 100L);

        delivery.registerTrackingNumber("TRACK-1234");
        delivery.markShipped();
        delivery.startDelivery();
        delivery.completeDelivery();

        assertEquals(DeliveryStatus.DELIVERED, delivery.status());
        assertEquals("TRACK-1234", delivery.trackingNumber());
    }

    @Test
    void markShipped_shouldFailWithoutTrackingNumber() {
        Delivery delivery = Delivery.create(1L, 100L);

        assertThrows(IllegalStateException.class, delivery::markShipped);
    }

    @Test
    void completeDelivery_shouldFailWhenStatusOrderIsBroken() {
        Delivery delivery = Delivery.create(1L, 100L);
        delivery.registerTrackingNumber("TRACK-1234");

        assertThrows(IllegalStateException.class, delivery::completeDelivery);
    }
}
