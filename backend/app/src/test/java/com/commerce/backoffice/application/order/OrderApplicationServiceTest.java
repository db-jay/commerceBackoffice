package com.commerce.backoffice.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.commerce.backoffice.application.catalog.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.application.order.command.CreateOrderCommand;
import com.commerce.backoffice.application.order.command.CreateOrderCommand.CreateOrderLineCommand;
import com.commerce.backoffice.application.order.port.out.OrderPersistencePort;
import com.commerce.backoffice.application.order.service.OrderApplicationService;
import com.commerce.backoffice.domain.catalog.Product;
import com.commerce.backoffice.domain.order.Order;
import com.commerce.backoffice.domain.order.OrderStatus;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderPersistencePort orderPersistencePort;

    @Mock
    private CatalogProductPersistencePort catalogProductPersistencePort;

    @Test
    void create_shouldLoadProductsInBatchBeforeSavingOrder() {
        OrderApplicationService service = new OrderApplicationService(orderPersistencePort, catalogProductPersistencePort);
        Product apple = TestFixtureFactory.product(1L, "apple", 1000, 10);
        Product banana = TestFixtureFactory.product(2L, "banana", 2000, 10);
        CreateOrderCommand command = new CreateOrderCommand(
            100L,
            List.of(
                new CreateOrderLineCommand(1L, 2, java.math.BigDecimal.valueOf(1000)),
                new CreateOrderLineCommand(2L, 1, java.math.BigDecimal.valueOf(2000))
            )
        );

        when(catalogProductPersistencePort.findByIds(Set.of(1L, 2L))).thenReturn(Map.of(1L, apple, 2L, banana));
        when(catalogProductPersistencePort.updateStock(any(Product.class))).thenReturn(true);
        when(orderPersistencePort.save(eq(100L), any())).thenReturn(
            Order.create(
                99L,
                100L,
                List.of(
                    TestFixtureFactory.orderLine(1L, 2, 1000),
                    TestFixtureFactory.orderLine(2L, 1, 2000)
                )
            )
        );

        Order createdOrder = service.create(command);

        assertEquals(99L, createdOrder.id());
        verify(catalogProductPersistencePort, times(1)).findByIds(Set.of(1L, 2L));
        verify(catalogProductPersistencePort, times(2)).updateStock(any(Product.class));
        verify(orderPersistencePort, times(1)).save(eq(100L), any());
    }

    @Test
    void cancel_shouldLoadProductsInBatchWhenRestoringStock() {
        OrderApplicationService service = new OrderApplicationService(orderPersistencePort, catalogProductPersistencePort);
        Order confirmedOrder = Order.restore(
            77L,
            100L,
            List.of(
                TestFixtureFactory.orderLine(1L, 2, 1000),
                TestFixtureFactory.orderLine(2L, 1, 2000)
            ),
            OrderStatus.CONFIRMED
        );
        Product apple = TestFixtureFactory.product(1L, "apple", 1000, 8);
        Product banana = TestFixtureFactory.product(2L, "banana", 2000, 9);

        when(orderPersistencePort.findById(77L)).thenReturn(Optional.of(confirmedOrder));
        when(catalogProductPersistencePort.findByIds(Set.of(1L, 2L))).thenReturn(Map.of(1L, apple, 2L, banana));
        when(catalogProductPersistencePort.updateStock(any(Product.class))).thenReturn(true);

        Order canceledOrder = service.cancel(77L);

        assertEquals(OrderStatus.CANCELED, canceledOrder.status());
        verify(catalogProductPersistencePort, times(1)).findByIds(Set.of(1L, 2L));
        verify(catalogProductPersistencePort, times(2)).updateStock(any(Product.class));
        verify(orderPersistencePort, times(1)).updateStatus(77L, OrderStatus.CANCELED);
    }
}
