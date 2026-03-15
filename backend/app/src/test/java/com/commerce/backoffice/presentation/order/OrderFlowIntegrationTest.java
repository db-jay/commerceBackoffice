package com.commerce.backoffice.presentation.order;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.commerce.backoffice.support.template.ApiIntegrationTestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
}
