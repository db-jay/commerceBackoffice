package com.commerce.backoffice.presentation.display;

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

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class DisplayEventFlowIntegrationTest extends ApiIntegrationTestTemplate {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_display_test")
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
    void createAndGetExposure_shouldReturnTrueWithinActivePeriod() throws Exception {
        String bearerToken = bearerToken();
        Long productId = createProductAndGetId("display-orange", 1200, 30);

        mockMvc.perform(post("/api/displays/events")
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "spring-sale",
                      "status": "ACTIVE",
                      "startAt": "2026-03-30T00:00:00",
                      "endAt": "2026-03-31T23:59:59",
                      "productIds": [%d]
                    }
                    """.formatted(productId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.status").value("ACTIVE"))
            .andExpect(jsonPath("$.data.productIds[0]").value(productId));

        mockMvc.perform(get("/api/displays/products/{productId}/exposure", productId)
                .header("Authorization", bearerToken)
                .param("at", "2026-03-30T12:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.productId").value(productId))
            .andExpect(jsonPath("$.data.exposed").value(true))
            .andExpect(jsonPath("$.data.eventName").value("spring-sale"));
    }

    @Test
    void getExposure_shouldReturnFalseWhenEventIsInactive() throws Exception {
        String bearerToken = bearerToken();
        Long productId = createProductAndGetId("inactive-orange", 1200, 30);
        createDisplayEvent(bearerToken, "inactive-sale", "INACTIVE", productId);

        mockMvc.perform(get("/api/displays/products/{productId}/exposure", productId)
                .header("Authorization", bearerToken)
                .param("at", "2026-03-30T12:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.exposed").value(false))
            .andExpect(jsonPath("$.data.eventId").doesNotExist())
            .andExpect(jsonPath("$.data.eventName").doesNotExist());
    }

    @Test
    void getExposure_shouldReturnFalseWhenOutsidePeriod() throws Exception {
        String bearerToken = bearerToken();
        Long productId = createProductAndGetId("outside-orange", 1200, 30);
        createDisplayEvent(bearerToken, "outside-sale", "ACTIVE", productId);

        mockMvc.perform(get("/api/displays/products/{productId}/exposure", productId)
                .header("Authorization", bearerToken)
                .param("at", "2026-04-01T00:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.exposed").value(false));
    }

    @Test
    void getExposure_shouldReturnFalseWhenProductIsNotTargeted() throws Exception {
        String bearerToken = bearerToken();
        Long targetedProductId = createProductAndGetId("targeted-orange", 1200, 30);
        Long otherProductId = createProductAndGetId("other-orange", 1300, 30);
        createDisplayEvent(bearerToken, "targeted-sale", "ACTIVE", targetedProductId);

        mockMvc.perform(get("/api/displays/products/{productId}/exposure", otherProductId)
                .header("Authorization", bearerToken)
                .param("at", "2026-03-30T12:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.productId").value(otherProductId))
            .andExpect(jsonPath("$.data.exposed").value(false));
    }

    @Test
    void getExposure_shouldReturnTrueOnBoundaryTimes() throws Exception {
        String bearerToken = bearerToken();
        Long productId = createProductAndGetId("boundary-orange", 1200, 30);
        createDisplayEvent(bearerToken, "boundary-sale", "ACTIVE", productId);

        mockMvc.perform(get("/api/displays/products/{productId}/exposure", productId)
                .header("Authorization", bearerToken)
                .param("at", "2026-03-30T00:00:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.exposed").value(true));

        mockMvc.perform(get("/api/displays/products/{productId}/exposure", productId)
                .header("Authorization", bearerToken)
                .param("at", "2026-03-31T23:59:59"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.exposed").value(true));
    }

    private void createDisplayEvent(String bearerToken, String name, String status, Long productId) throws Exception {
        mockMvc.perform(post("/api/displays/events")
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "%s",
                      "status": "%s",
                      "startAt": "2026-03-30T00:00:00",
                      "endAt": "2026-03-31T23:59:59",
                      "productIds": [%d]
                    }
                    """.formatted(name, status, productId)))
            .andExpect(status().isOk());
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
