package com.commerce.backoffice.interfaces.common.filter;

import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestIdFilter extends OncePerRequestFilter {
    /*
     * 요청 추적용 필터.
     * - 클라이언트가 X-Request-Id를 보내면 재사용
     * - 없으면 서버에서 새로 생성
     * - MDC에 저장해서 로그마다 같은 requestId를 남김
     * - 응답 헤더에도 넣어 클라이언트와 서버 로그를 연결
     */

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // 1) 클라이언트가 이미 보낸 요청 ID 확인
        String existingRequestId = request.getHeader(REQUEST_ID_HEADER);
        // 2) 있으면 재사용, 없으면 새 UUID 생성
        String requestId = StringUtils.hasText(existingRequestId) ? existingRequestId : UUID.randomUUID().toString();

        // 3) 로그 추적을 위해 MDC에 저장
        MDC.put(MDC_KEY, requestId);
        // 4) 응답 헤더에도 같은 ID를 넣어 클라이언트가 확인 가능하게 함
        response.setHeader(REQUEST_ID_HEADER, requestId);
        try {
            // 다음 필터/핸들러로 요청 전달
            filterChain.doFilter(request, response);
        } finally {
            // 매우 중요:
            // 스레드가 재사용될 수 있으므로 요청 종료 후 MDC를 반드시 정리.
            // 정리하지 않으면 다른 요청 로그에 이전 requestId가 섞일 수 있다.
            MDC.remove(MDC_KEY);
        }
    }
}
