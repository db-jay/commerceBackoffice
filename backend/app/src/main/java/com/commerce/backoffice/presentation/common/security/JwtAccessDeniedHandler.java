package com.commerce.backoffice.presentation.common.security;

import com.commerce.backoffice.domain.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/*
 * 인가 실패(403) 응답 처리기.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityResponseWriter securityResponseWriter;

    public JwtAccessDeniedHandler(SecurityResponseWriter securityResponseWriter) {
        this.securityResponseWriter = securityResponseWriter;
    }

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        securityResponseWriter.writeError(response, HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED);
    }
}
