package com.commerce.backoffice.presentation.member;

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
 * Member 컨텍스트 통합 테스트.
 */
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=",
    "spring.flyway.enabled=true"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class MemberFlowIntegrationTest extends ApiIntegrationTestTemplate {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("commerce_member_test")
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
    void createAndGetMember_shouldWorkWithLayerFlow() throws Exception {
        String bearerToken = bearerToken();

        Long memberId = extractLongData(
            mockMvc.perform(post("/api/members")
                    .header("Authorization", bearerToken)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "email": "member@test.com",
                          "name": "tester"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.email").value("member@test.com"))
                .andExpect(jsonPath("$.data.name").value("tester"))
                .andReturn(),
            "id"
        );

        mockMvc.perform(get("/api/members/{memberId}", memberId)
                .header("Authorization", bearerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(memberId))
            .andExpect(jsonPath("$.data.email").value("member@test.com"))
            .andExpect(jsonPath("$.data.name").value("tester"));
    }

    @Test
    void getById_shouldReturnConflictWhenMemberNotFound() throws Exception {
        mockMvc.perform(get("/api/members/{memberId}", 999999L)
                .header("Authorization", bearerToken()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"));
    }

    @Test
    void updateMember_shouldChangeNameAndGrade() throws Exception {
        Long memberId = createMemberAndGetId("update-member@test.com", "old-name");

        mockMvc.perform(patch("/api/members/{memberId}", memberId)
                .header("Authorization", bearerToken())
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "name": "new-name",
                      "grade": "VIP"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.name").value("new-name"))
            .andExpect(jsonPath("$.data.grade").value("VIP"));
    }

    @Test
    void changeStatus_shouldUpdateMemberStatus() throws Exception {
        Long memberId = createMemberAndGetId("status-member@test.com", "status-member");

        mockMvc.perform(patch("/api/members/{memberId}/status", memberId)
                .header("Authorization", bearerToken())
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "status": "DORMANT"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.status").value("DORMANT"));
    }

    @Test
    void create_shouldReturnValidationErrorWhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/members")
                .header("Authorization", bearerToken())
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "email": "not-email",
                      "name": "tester"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    private Long createMemberAndGetId(String email, String name) throws Exception {
        return extractLongData(
            mockMvc.perform(post("/api/members")
                    .header("Authorization", bearerToken())
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "email": "%s",
                          "name": "%s"
                        }
                        """.formatted(email, name)))
                .andExpect(status().isOk())
                .andReturn(),
            "id"
        );
    }
}
