package com.commerce.backoffice.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
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
                TestFixtureFactory.orderLine(10L, 2, 1000),
                TestFixtureFactory.orderLine(20L, 1, 3000)
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
            List.of(TestFixtureFactory.orderLine(10L, 1, 1000))
        );

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.status());
    }

    @Test
    void confirm_shouldFailWhenStatusIsNotCreated() {
        Order order = Order.create(
            1L,
            100L,
            List.of(TestFixtureFactory.orderLine(10L, 1, 1000))
        );
        order.cancel();

        assertThrows(IllegalStateException.class, order::confirm);
    }
}
