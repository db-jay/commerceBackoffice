package com.commerce.backoffice.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void create_shouldCalculateTotalAmount() {
        Order order = Order.create(
            1L,
            100L,
            List.of(
                new OrderLine(10L, 2, BigDecimal.valueOf(1000)),
                new OrderLine(20L, 1, BigDecimal.valueOf(3000))
            )
        );

        assertEquals(BigDecimal.valueOf(5000), order.totalAmount());
        assertEquals(OrderStatus.CREATED, order.status());
    }

    @Test
    void confirm_shouldChangeStatusFromCreatedToConfirmed() {
        Order order = Order.create(
            1L,
            100L,
            List.of(new OrderLine(10L, 1, BigDecimal.valueOf(1000)))
        );

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.status());
    }

    @Test
    void confirm_shouldFailWhenStatusIsNotCreated() {
        Order order = Order.create(
            1L,
            100L,
            List.of(new OrderLine(10L, 1, BigDecimal.valueOf(1000)))
        );
        order.cancel();

        assertThrows(IllegalStateException.class, order::confirm);
    }
}

