package com.commerce.backoffice.presentation.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/*
 * Order 컨텍스트 통합 테스트.
 *
 * 검증 목표:
 * - Order Controller -> Application -> Domain -> Infrastructure(JDBC) 흐름 연결 확인
 * - 공통 응답 포맷(code/message/timestamp/data) 유지 확인
 */
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class OrderFlowIntegrationTest {

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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndGetOrder_shouldWorkWithLayerFlow() throws Exception {
        Long memberId = createMemberAndGetId("order-member@test.com", "order-member");
        Long productId1 = createProductAndGetId("orange", 1000, 50);
        Long productId2 = createProductAndGetId("grape", 3000, 50);

        MvcResult createResult = mockMvc.perform(post("/api/orders")
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
            .andReturn();

        Long orderId = extractId(createResult, "id");

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(orderId))
            .andExpect(jsonPath("$.data.memberId").value(memberId))
            .andExpect(jsonPath("$.data.totalAmount").value(5000));
    }

    @Test
    void getById_shouldReturnConflictWhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", 999999L))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"));
    }

    private Long createMemberAndGetId(String email, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content("""
                    {"email":"%s", "name":"%s"}
                    """.formatted(email, name)))
            .andExpect(status().isOk())
            .andReturn();

        return extractId(result, "id");
    }

    private Long createProductAndGetId(String name, int price, int stockQuantity) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/catalog/products")
                .contentType(APPLICATION_JSON)
                .content("""
                    {"name":"%s", "price":%d, "stockQuantity":%d}
                    """.formatted(name, price, stockQuantity)))
            .andExpect(status().isOk())
            .andReturn();

        return extractId(result, "id");
    }

    @SuppressWarnings("unchecked")
    private Long extractId(MvcResult mvcResult, String key) throws Exception {
        Map<String, Object> root = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        Number id = (Number) data.get(key);
        assertThat(id).isNotNull();
        return id.longValue();
    }
}

