package com.commerce.backoffice.infrastructure.catalog.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.commerce.backoffice.application.catalog.port.out.CatalogProductPersistencePort;
import com.commerce.backoffice.domain.catalog.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
 * [역할]
 * - JDBC 기반 재고 저장이 version 값을 사용해 낙관적 락을 수행하는지 검증한다.
 *
 * [왜 필요한가]
 * - 동시성 충돌은 API 성공 테스트만으로는 잘 드러나지 않는다.
 * - 같은 상품을 두 번 읽고, 먼저 저장한 뒤, 나중 저장이 실패하는지 확인해야
 *   "version 충돌 감지"가 실제로 동작한다고 볼 수 있다.
 */
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@Testcontainers(disabledWithoutDocker = true)
class CatalogProductPersistenceConcurrencyIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_catalog_lock_test")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private CatalogProductPersistencePort catalogProductPersistencePort;

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Test
    void updateStock_shouldFailWhenVersionAlreadyChanged() {
        Product saved = catalogProductPersistencePort.save("apple", BigDecimal.valueOf(1000), 10);

        Product firstReader = catalogProductPersistencePort.findById(saved.id()).orElseThrow();
        Product secondReader = catalogProductPersistencePort.findById(saved.id()).orElseThrow();

        firstReader.reserveStock(3);
        secondReader.reserveStock(2);

        assertTrue(catalogProductPersistencePort.updateStock(firstReader));
        assertFalse(catalogProductPersistencePort.updateStock(secondReader));

        Product latest = catalogProductPersistencePort.findById(saved.id()).orElseThrow();
        assertEquals(7, latest.stockQuantity());
        assertEquals(1L, latest.version());
    }
}
