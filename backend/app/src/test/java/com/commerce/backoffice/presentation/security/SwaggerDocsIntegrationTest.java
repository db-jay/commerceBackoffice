package com.commerce.backoffice.presentation.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.commerce.backoffice.support.template.ApiIntegrationTestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * Swagger/OpenAPI 공개 여부 검증.
 *
 * - `/swagger-ui.html` 진입 URL이 유지되는지
 * - 정적 Swagger 페이지가 제공되는지
 * - OpenAPI 문서에 display API가 포함되는지
 * 를 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SwaggerDocsIntegrationTest extends ApiIntegrationTestTemplate {

    @Test
    void swaggerUiShortcut_shouldRedirectToStaticPage() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    void swaggerUiStaticPage_shouldBePublic() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("text/html"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Commerce Backoffice Swagger UI")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("/v3/api-docs")));
    }

    @Test
    void openApiDocs_shouldContainDisplayExposurePathWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith("application/json"))
            .andExpect(jsonPath("$.info.title").value("Commerce Backoffice API"))
            .andExpect(jsonPath("$.paths['/api/displays/events']").exists())
            .andExpect(jsonPath("$.paths['/api/displays/products/{productId}/exposure']").exists());
    }
}
