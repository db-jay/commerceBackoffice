package com.commerce.backoffice.domain.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void reserveStock_shouldDecreaseStock() {
        Product product = new Product(1L, "apple", BigDecimal.valueOf(1000), 10, ProductStatus.ACTIVE);

        product.reserveStock(3);

        assertEquals(7, product.stockQuantity());
    }

    @Test
    void reserveStock_shouldThrowWhenStockIsInsufficient() {
        Product product = new Product(1L, "apple", BigDecimal.valueOf(1000), 1, ProductStatus.ACTIVE);

        assertThrows(IllegalStateException.class, () -> product.reserveStock(2));
    }
}

