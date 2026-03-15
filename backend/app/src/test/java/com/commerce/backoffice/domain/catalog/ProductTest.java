package com.commerce.backoffice.domain.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
import org.junit.jupiter.api.Test;

/*
 * [역할]
 * - Product 도메인 객체의 핵심 재고 규칙을 검증한다.
 *
 * [왜 필요한가]
 * - 재고 부족/증가 같은 규칙이 가장 먼저 깨지기 쉬운 곳은 Domain이다.
 * - Domain 테스트가 있으면 API/DB가 없어도 규칙을 빠르게 확인할 수 있다.
 */
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

    @Test
    void releaseStock_shouldIncreaseStock() {
        Product product = TestFixtureFactory.product(1L, "apple", 1000, 1);

        product.releaseStock(4);

        assertEquals(5, product.stockQuantity());
    }
}
