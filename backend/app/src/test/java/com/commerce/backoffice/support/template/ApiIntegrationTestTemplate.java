package com.commerce.backoffice.support.template;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/*
 * [역할]
 * - API 통합 테스트에서 공통으로 쓰는 도구를 모아 둔 템플릿이다.
 *
 * [왜 필요한가]
 * - MockMvc/ObjectMapper 주입, 관리자 로그인, 응답 data 추출 코드가
 *   API 테스트마다 반복되기 쉽다.
 * - 이 반복을 줄이면 테스트가 "무엇을 검증하는지"에 더 집중할 수 있다.
 *
 * [제공 기능]
 * - bearerToken()      : 관리자 access token을 Bearer 형식으로 반환
 * - adminAccessToken() : 관리자 로그인 후 access token만 반환
 * - extractStringData(): 응답 JSON의 data.{key} 문자열 값 추출
 * - extractLongData()  : 응답 JSON의 data.{key} 숫자 값 추출
 *
 * [주의할 점]
 * - DB가 필요한 테스트는 이 템플릿을 상속한 뒤,
 *   각 테스트 클래스에서 Testcontainers와 DynamicPropertySource를 추가해야 한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
public abstract class ApiIntegrationTestTemplate {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String bearerToken() throws Exception {
        return "Bearer " + adminAccessToken();
    }

    @SuppressWarnings("unchecked")
    protected String adminAccessToken() throws Exception {
        // 테스트용 기본 관리자 계정으로 로그인해서 access token을 얻는다.
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "username": "admin",
                      "password": "admin1234"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();

        Map<String, Object> root = objectMapper.readValue(loginResult.getResponse().getContentAsString(), Map.class);
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        return (String) data.get("accessToken");
    }

    @SuppressWarnings("unchecked")
    protected String extractStringData(MvcResult mvcResult, String key) throws Exception {
        Map<String, Object> root = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        return (String) data.get(key);
    }

    @SuppressWarnings("unchecked")
    protected Long extractLongData(MvcResult mvcResult, String key) throws Exception {
        Map<String, Object> root = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        Number value = (Number) data.get(key);
        return value.longValue();
    }
}
