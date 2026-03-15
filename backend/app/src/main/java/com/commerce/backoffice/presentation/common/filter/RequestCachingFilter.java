package com.commerce.backoffice.presentation.common.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCachingFilter extends OncePerRequestFilter {
    /*
     * 이 필터의 목적:
     * 1) 요청 본문(Request Body)을 캐싱해서
     * 2) 예외가 났을 때도 본문을 로그로 확인할 수 있게 만드는 것.
     *
     * 왜 가장 먼저 실행하나?
     * - 뒤에서 다른 필터/인터셉터/컨트롤러가 본문을 읽더라도
     *   캐싱된 wrapper를 계속 사용할 수 있게 하기 위함.
     */

    private static final Logger log = LoggerFactory.getLogger(RequestCachingFilter.class);

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // 원본 request를 ContentCachingRequestWrapper로 감싼다.
        // 이미 감싸져 있다면 재사용한다.
        ContentCachingRequestWrapper wrapper = request instanceof ContentCachingRequestWrapper
            ? (ContentCachingRequestWrapper) request
            : new ContentCachingRequestWrapper(request);

        try {
            // 필수: 다음 필터로 흐름을 넘겨야 전체 요청이 계속 진행된다.
            // 이 호출이 없으면 Controller까지 요청이 도달하지 않는다.
            filterChain.doFilter(wrapper, response);
        } catch (RuntimeException | ServletException | IOException ex) {
            // 예외가 발생한 경우, 디버깅을 위해 요청 본문을 로그로 남긴다.
            logCachedBody(wrapper);
            throw ex;
        }
    }

    private void logCachedBody(ContentCachingRequestWrapper request) {
        byte[] body = request.getContentAsByteArray();
        if (body.length == 0) {
            // 바디가 없거나 아직 읽히지 않은 요청일 수 있다.
            log.warn("request body cache is empty. uri={}", request.getRequestURI());
            return;
        }
        // 캐시된 바이트 배열을 UTF-8 문자열로 변환해 로그에 출력.
        String payload = new String(body, StandardCharsets.UTF_8);
        log.warn("request body cached for error tracing. uri={}, body={}", request.getRequestURI(), payload);
    }
}
