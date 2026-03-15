package com.commerce.backoffice.presentation.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestFlowInterceptor implements HandlerInterceptor {
    /*
     * Interceptor는 Spring MVC 레벨에서 동작한다.
     * - Filter: 서블릿 레벨 (더 바깥)
     * - Interceptor: Controller 호출 전/후 (더 안쪽)
     *
     * 이 클래스는 요청 전/후 로그를 남겨 흐름을 추적한다.
     */

    private static final Logger log = LoggerFactory.getLogger(RequestFlowInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Controller 진입 직전에 실행.
        // true를 반환해야 요청이 계속 진행된다.
        log.info("interceptor preHandle. method={}, uri={}, requestId={}",
            request.getMethod(), request.getRequestURI(), MDC.get("requestId"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Controller 및 View 처리(또는 예외 처리)까지 끝난 뒤 실행.
        log.info("interceptor afterCompletion. method={}, uri={}, status={}, requestId={}",
            request.getMethod(), request.getRequestURI(), response.getStatus(), MDC.get("requestId"));
    }
}
