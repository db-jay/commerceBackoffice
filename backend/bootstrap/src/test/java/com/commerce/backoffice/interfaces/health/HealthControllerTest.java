package com.commerce.backoffice.interfaces.health;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {
    /*
     * 이 테스트 클래스는 "요구사항이 실제 동작하는지"를 검증한다.
     * 핵심 검증 범위:
     * 1) 공통 응답 구조(code/message/timestamp/data)
     * 2) RequestId 필터 동작(생성/재사용)
     * 3) 전역 예외 처리(Business/Runtime/Validation)
     */

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsHealthUp() throws Exception {
        // /health 정상 호출 시 공통 응답 포맷 + request id 헤더가 내려오는지 확인
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(header().exists("X-Request-Id"))
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.message").value("요청이 성공했습니다."))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void reusesRequestIdWhenHeaderExists() throws Exception {
        // 클라이언트가 X-Request-Id를 보내면 서버가 새로 만들지 않고 그대로 재사용해야 함
        mockMvc.perform(get("/health").header("X-Request-Id", "client-request-id"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Request-Id", "client-request-id"));
    }

    @Test
    void handlesBusinessException() throws Exception {
        // Application Service에서 BusinessException이 발생하면
        // ControllerAdvice가 CONFLICT + 공통 에러 응답으로 변환해야 함
        mockMvc.perform(get("/demo/business-error"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DEMO_BUSINESS_ERROR"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handlesRuntimeException() throws Exception {
        // 처리되지 않은 런타임 예외는 INTERNAL_SERVER_ERROR 코드로 표준화되어야 함
        mockMvc.perform(get("/demo/runtime-error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handlesValidationException() throws Exception {
        // @Valid 검증 실패 시 BAD_REQUEST + VALIDATION_ERROR로 내려와야 함
        mockMvc.perform(post("/demo/validation")
                .contentType(APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
            .andExpect(jsonPath("$.message").value("name은 필수입니다."))
            .andExpect(jsonPath("$.timestamp").exists());
    }
}
