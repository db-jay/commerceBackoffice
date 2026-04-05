package com.commerce.backoffice.presentation.delivery;

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

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class DeliveryFlowIntegrationTest extends ApiIntegrationTestTemplate {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_delivery_test")
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
    void createAndGetDelivery_shouldStartWithReadyStatus() throws Exception {
        String bearerToken = bearerToken();
        Long orderId = createOrderAndGetId(bearerToken);

        Long deliveryId = extractLongData(
            mockMvc.perform(post("/api/deliveries")
                    .header("Authorization", bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {"orderId": %d}
                        """.formatted(orderId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.status").value("READY"))
                .andReturn(),
            "id"
        );

        mockMvc.perform(get("/api/deliveries/{deliveryId}", deliveryId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(deliveryId))
            .andExpect(jsonPath("$.data.status").value("READY"));
    }

    @Test
    void registerTrackingNumber_shouldSucceedOnlyInReadyStatus() throws Exception {
        String bearerToken = bearerToken();
        Long deliveryId = createDeliveryAndGetId(bearerToken);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/tracking-number", deliveryId)
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {"trackingNumber":"TRACK-2026-0001"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.trackingNumber").value("TRACK-2026-0001"));

        changeStatus(bearerToken, deliveryId, "SHIPPED");

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/tracking-number", deliveryId)
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {"trackingNumber":"TRACK-2026-0002"}
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DELIVERY_TRACKING_NUMBER_NOT_ALLOWED"));
    }

    @Test
    void changeStatus_shouldFollowAllowedSequence() throws Exception {
        String bearerToken = bearerToken();
        Long deliveryId = createDeliveryAndGetId(bearerToken);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/tracking-number", deliveryId)
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {"trackingNumber":"TRACK-2026-0001"}
                    """))
            .andExpect(status().isOk());

        changeStatus(bearerToken, deliveryId, "SHIPPED")
            .andExpect(jsonPath("$.data.status").value("SHIPPED"));
        changeStatus(bearerToken, deliveryId, "IN_DELIVERY")
            .andExpect(jsonPath("$.data.status").value("IN_DELIVERY"));
        changeStatus(bearerToken, deliveryId, "DELIVERED")
            .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }

    @Test
    void changeStatus_shouldFailWhenTrackingNumberIsMissing() throws Exception {
        String bearerToken = bearerToken();
        Long deliveryId = createDeliveryAndGetId(bearerToken);

        changeStatus(bearerToken, deliveryId, "SHIPPED")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DELIVERY_TRACKING_NUMBER_REQUIRED"));
    }

    @Test
    void changeStatus_shouldFailWhenTransitionIsInvalid() throws Exception {
        String bearerToken = bearerToken();
        Long deliveryId = createDeliveryAndGetId(bearerToken);

        mockMvc.perform(patch("/api/deliveries/{deliveryId}/tracking-number", deliveryId)
                .header("Authorization", bearerToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {"trackingNumber":"TRACK-2026-0001"}
                    """))
            .andExpect(status().isOk());

        changeStatus(bearerToken, deliveryId, "DELIVERED")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DELIVERY_INVALID_STATUS_TRANSITION"));
    }

    private org.springframework.test.web.servlet.ResultActions changeStatus(
        String bearerToken,
        Long deliveryId,
        String status
    ) throws Exception {
        return mockMvc.perform(patch("/api/deliveries/{deliveryId}/status", deliveryId)
            .header("Authorization", bearerToken)
            .contentType(APPLICATION_JSON)
            .content("""
                {"status":"%s"}
                """.formatted(status)));
    }

    private Long createDeliveryAndGetId(String bearerToken) throws Exception {
        Long orderId = createOrderAndGetId(bearerToken);
        return extractLongData(
            mockMvc.perform(post("/api/deliveries")
                    .header("Authorization", bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {"orderId": %d}
                        """.formatted(orderId)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }

    private Long createOrderAndGetId(String bearerToken) throws Exception {
        Long memberId = createMemberAndGetId();
        Long productId = createProductAndGetId("delivery-orange", 1000, 20);

        return extractLongData(
            mockMvc.perform(post("/api/orders")
                    .header("Authorization", bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "memberId": %d,
                          "orderLines": [
                            {"productId": %d, "quantity": 2, "unitPrice": 1000}
                          ]
                        }
                        """.formatted(memberId, productId)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }

    private Long createMemberAndGetId() throws Exception {
        return extractLongData(
            mockMvc.perform(post("/api/members")
                    .header("Authorization", bearerToken())
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {"email":"delivery-member@test.com", "name":"delivery-member"}
                        """))
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
