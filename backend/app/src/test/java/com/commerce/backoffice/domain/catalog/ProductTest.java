package com.commerce.backoffice.domain.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void reserveStock_shouldDecreaseStock() {
        Product product = TestFixtureFactory.product(1L, "apple", 1000, 10);

        product.reserveStock(3);

        assertEquals(7, product.stockQuantity());
    }

    @Test
    void reserveStock_shouldThrowWhenStockIsInsufficient() {
        Product product = TestFixtureFactory.product(1L, "apple", 1000, 1);

        assertThrows(IllegalStateException.class, () -> product.reserveStock(2));
    }
}

