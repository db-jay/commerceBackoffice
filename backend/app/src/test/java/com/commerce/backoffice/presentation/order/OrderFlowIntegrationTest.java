package com.commerce.backoffice.presentation.order;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.commerce.backoffice.support.template.ApiIntegrationTestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
 * Order 컨텍스트 통합 테스트.
 */
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class OrderFlowIntegrationTest extends ApiIntegrationTestTemplate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_order_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Test
    void createAndGetOrder_shouldWorkWithLayerFlow() throws Exception {
        String bearerToken = bearerToken();
        Long memberId = createMemberAndGetId("order-member@test.com", "order-member");
        Long productId1 = createProductAndGetId("orange", 1000, 50);
        Long productId2 = createProductAndGetId("grape", 3000, 50);

        Long orderId = extractLongData(
            mockMvc.perform(post("/api/orders")
                    .header("Authorization", bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": %d,
                          "orderLines": [
                            {"productId": %d, "quantity": 2, "unitPrice": 1000},
                            {"productId": %d, "quantity": 1, "unitPrice": 3000}
                          ]
                        }
                        """.formatted(memberId, productId1, productId2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.data.totalAmount").value(5000))
                .andExpect(jsonPath("$.data.orderLines.length()").value(2))
                .andReturn(),
            "id"
        );

        mockMvc.perform(get("/api/catalog/products/{productId}", productId1)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stockQuantity").value(48));

        mockMvc.perform(get("/api/catalog/products/{productId}", productId2)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stockQuantity").value(49));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(orderId))
            .andExpect(jsonPath("$.data.memberId").value(memberId))
            .andExpect(jsonPath("$.data.totalAmount").value(5000));
    }

    @Test
    void getById_shouldReturnConflictWhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", 999999L)
                .header("Authorization", bearerToken()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
    }

    @Test
    void create_shouldReturnConflictWhenStockIsInsufficient() throws Exception {
        String bearerToken = bearerToken();
        Long memberId = createMemberAndGetId("low-stock-member@test.com", "low-stock-member");
        Long productId = createProductAndGetId("limited-orange", 1500, 1);

        mockMvc.perform(post("/api/orders")
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "memberId": %d,
                      "orderLines": [
                        {"productId": %d, "quantity": 2, "unitPrice": 1500}
                      ]
                    }
                    """.formatted(memberId, productId)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("INSUFFICIENT_STOCK"));
    }

    @Test
    void cancel_shouldRestoreStockWhenOrderIsCreated() throws Exception {
        String bearerToken = bearerToken();
        Long memberId = createMemberAndGetId("cancel-created@test.com", "cancel-created");
        Long productId = createProductAndGetId("cancel-created-orange", 1000, 10);
        Long orderId = createOrderAndGetId(bearerToken, memberId, productId, 3, 1000);

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.status").value("CANCELED"));

        mockMvc.perform(get("/api/catalog/products/{productId}", productId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stockQuantity").value(10));
    }

    @Test
    void cancel_shouldRestoreStockWhenOrderIsConfirmed() throws Exception {
        String bearerToken = bearerToken();
        Long memberId = createMemberAndGetId("cancel-confirmed@test.com", "cancel-confirmed");
        Long productId = createProductAndGetId("cancel-confirmed-orange", 1000, 10);
        Long orderId = createOrderAndGetId(bearerToken, memberId, productId, 2, 1000);

        jdbcTemplate.update(
            "update orders set order_status = ? where id = ?",
            "CONFIRMED",
            orderId
        );

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.status").value("CANCELED"));

        mockMvc.perform(get("/api/catalog/products/{productId}", productId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.stockQuantity").value(10));
    }

    @Test
    void cancel_shouldReturnConflictWhenOrderIsAlreadyCanceled() throws Exception {
        String bearerToken = bearerToken();
        Long memberId = createMemberAndGetId("cancel-again@test.com", "cancel-again");
        Long productId = createProductAndGetId("cancel-again-orange", 1000, 10);
        Long orderId = createOrderAndGetId(bearerToken, memberId, productId, 1, 1000);

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                .header("Authorization", bearerToken))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ORDER_ALREADY_CANCELED"));
    }

    private Long createMemberAndGetId(String email, String name) throws Exception {
        return extractLongData(
            mockMvc.perform(post("/api/members")
                    .header("Authorization", bearerToken())
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {"email":"%s", "name":"%s"}
                        """.formatted(email, name)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }

    private Long createProductAndGetId(String name, int price, int stockQuantity) throws Exception {
        return extractLongData(
            mockMvc.perform(post("/api/catalog/products")
                    .header("Authorization", bearerToken())
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {"name":"%s", "price":%d, "stockQuantity":%d}
                        """.formatted(name, price, stockQuantity)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }

    private Long createOrderAndGetId(String bearerToken, Long memberId, Long productId, int quantity, int unitPrice) throws Exception {
        return extractLongData(
            mockMvc.perform(post("/api/orders")
                    .header("Authorization", bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": %d,
                          "orderLines": [
                            {"productId": %d, "quantity": %d, "unitPrice": %d}
                          ]
                        }
                        """.formatted(memberId, productId, quantity, unitPrice)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }
}
