package com.commerce.backoffice.presentation.common.security;

import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.presentation.common.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/*
 * [역할]
 * - Filter / EntryPoint / AccessDeniedHandler에서 공통 에러 JSON을 직접 써주는 도우미다.
 *
 * [왜 필요한가]
 * - Controller에서는 ResponseEntity를 쉽게 반환할 수 있지만,
 *   Security 필터 계층에서는 HttpServletResponse에 직접 써야 하는 경우가 많다.
 * - 이때도 API 응답 모양(BaseResponse)을 통일하기 위해 분리했다.
 *
 * [주의할 점]
 * - 인증/인가 실패도 일반 API 응답과 같은 JSON 구조를 유지한다.
 */
@Component
public class SecurityResponseWriter {

    private final ObjectMapper objectMapper;

    public SecurityResponseWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void writeError(HttpServletResponse response, HttpStatus status, ErrorCode errorCode) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), BaseResponse.failure(errorCode.code(), errorCode.message(), null));
    }
}
