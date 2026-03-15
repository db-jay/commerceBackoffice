package com.commerce.backoffice.presentation.catalog;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
 * Catalog 컨텍스트 통합 테스트.
 *
 * 템플릿 활용 포인트:
 * - 관리자 로그인 헬퍼(bearerToken)
 * - 응답 data 추출 헬퍼(extractLongData)
 */
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class CatalogProductFlowIntegrationTest extends ApiIntegrationTestTemplate {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_catalog_test")
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
    void createAndGetProduct_shouldWorkWithLayerFlow() throws Exception {
        String bearerToken = bearerToken();

        Long productId = extractLongData(
            mockMvc.perform(post("/api/catalog/products")
                    .header("Authorization", bearerToken)
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
                .andReturn(),
            "id"
        );

        mockMvc.perform(get("/api/catalog/products/{productId}", productId)
                .header("Authorization", bearerToken))
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
                .header("Authorization", bearerToken())
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
                .header("Authorization", bearerToken())
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
                .header("Authorization", bearerToken())
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
                .header("Authorization", bearerToken())
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
        mockMvc.perform(get("/api/catalog/products/{productId}", 999999L)
                .header("Authorization", bearerToken()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    private Long createProductAndGetId(String name, int price, int stockQuantity) throws Exception {
        return extractLongData(
            mockMvc.perform(post("/api/catalog/products")
                    .header("Authorization", bearerToken())
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "%s",
                          "price": %d,
                          "stockQuantity": %d
                        }
                        """.formatted(name, price, stockQuantity)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }
}
