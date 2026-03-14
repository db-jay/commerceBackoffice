package com.commerce.backoffice.presentation.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
 * Catalog 컨텍스트 통합 테스트.
 *
 * 검증 목표:
 * - Controller -> Application(UseCase) -> Domain -> Infrastructure(JDBC Adapter)
 *   흐름이 실제로 연결되어 동작하는지 확인한다.
 * - 공통 응답 포맷(code/message/timestamp/data)이 유지되는지 확인한다.
 *
 * 주의:
 * - 테스트 전용 PostgreSQL 컨테이너를 사용하므로 로컬 DB를 오염시키지 않는다.
 */
@SpringBootTest(properties = {
    // test/resources/application.yml의 자동설정 제외값을 이 테스트에서는 해제한다.
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class CatalogProductFlowIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_catalog_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        // Spring DataSource/Flyway가 테스트 컨테이너 DB를 사용하도록 연결한다.
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
    void createAndGetProduct_shouldWorkWithLayerFlow() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/catalog/products")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "banana",
                      "price": 2500,
                      "stockQuantity": 10
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.name").value("banana"))
            .andExpect(jsonPath("$.data.stockQuantity").value(10))
            .andReturn();

        Long productId = extractProductId(createResult);

        mockMvc.perform(get("/api/catalog/products/{productId}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(productId))
            .andExpect(jsonPath("$.data.name").value("banana"))
            .andExpect(jsonPath("$.data.price").value(2500));
    }

    @Test
    void reserveStock_shouldDecreaseStock() throws Exception {
        Long productId = createProductAndGetId("apple", 1000, 5);

        mockMvc.perform(patch("/api/catalog/products/{productId}/stock/reserve", productId)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "quantity": 2
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.stockQuantity").value(3));
    }

    @Test
    void updateProduct_shouldChangeNameAndPrice() throws Exception {
        Long productId = createProductAndGetId("melon", 3000, 9);

        mockMvc.perform(patch("/api/catalog/products/{productId}", productId)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "premium-melon",
                      "price": 3500
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.name").value("premium-melon"))
            .andExpect(jsonPath("$.data.price").value(3500));
    }

    @Test
    void changeStatus_shouldUpdateProductStatus() throws Exception {
        Long productId = createProductAndGetId("pear", 2200, 9);

        mockMvc.perform(patch("/api/catalog/products/{productId}/status", productId)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "status": "INACTIVE"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    void create_shouldReturnValidationErrorWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/catalog/products")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "",
                      "price": 1000,
                      "stockQuantity": 1
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getById_shouldReturnConflictWhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/catalog/products/{productId}", 999999L))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    private Long createProductAndGetId(String name, int price, int stockQuantity) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/catalog/products")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "%s",
                      "price": %d,
                      "stockQuantity": %d
                    }
                    """.formatted(name, price, stockQuantity)))
            .andExpect(status().isOk())
            .andReturn();

        return extractProductId(result);
    }

    @SuppressWarnings("unchecked")
    private Long extractProductId(MvcResult mvcResult) throws Exception {
        Map<String, Object> root = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        Number productId = (Number) data.get("id");
        assertThat(productId).isNotNull();
        return productId.longValue();
    }
}
