package com.commerce.backoffice.presentation.security;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.commerce.backoffice.application.auth.port.out.TokenPort;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
import com.commerce.backoffice.support.template.ApiIntegrationTestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * JWT 인증/인가 통합 테스트.
 *
 * 이 테스트는 "API 통합 테스트 템플릿" 예시이기도 하다.
 * - 공개 엔드포인트(login/refresh)
 * - 인증 실패(401)
 * - 인가 실패(403)
 * 를 한 곳에서 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest extends ApiIntegrationTestTemplate {

    @Autowired
    private TokenPort tokenPort;

    @Test
    void login_shouldReturnAccessAndRefreshToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "username": "admin",
                      "password": "admin1234"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void login_shouldReturnUnauthorizedWhenCredentialIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "username": "admin",
                      "password": "wrong-password"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }

    @Test
    void refresh_shouldIssueNewTokenPair() throws Exception {
        String refreshToken = extractStringData(
            mockMvc.perform(post("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "username": "admin",
                          "password": "admin1234"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn(),
            "refreshToken"
        );

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "refreshToken": "%s"
                    }
                    """.formatted(refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void refresh_shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "refreshToken": "invalid-refresh-token"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
    }

    @Test
    void protectedApi_shouldReturnUnauthorizedWhenTokenIsMissing() throws Exception {
        mockMvc.perform(patch("/api/members/{memberId}/status", 1L)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "status": "DORMANT"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
    }

    @Test
    void protectedApi_shouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(patch("/api/members/{memberId}/status", 1L)
                .header("Authorization", "Bearer invalid-token")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "status": "DORMANT"
                    }
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_TOKEN"));
    }

    @Test
    void adminOnlyApi_shouldReturnForbiddenWhenRoleIsInsufficient() throws Exception {
        String mdAccessToken = tokenPort.issueTokenPair(TestFixtureFactory.operator("md-user", "MD")).accessToken();

        mockMvc.perform(patch("/api/members/{memberId}/status", 1L)
                .header("Authorization", "Bearer " + mdAccessToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "status": "DORMANT"
                    }
                    """))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }
}
