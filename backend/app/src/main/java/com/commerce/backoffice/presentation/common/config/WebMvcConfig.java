package com.commerce.backoffice.presentation.common.config;

import com.commerce.backoffice.presentation.common.interceptor.RequestFlowInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /*
     * WebMvcConfigurer를 통해 Interceptor를 Spring MVC 체인에 등록한다.
     * 등록하지 않으면 Interceptor 클래스가 있어도 실행되지 않는다.
     */

    private final RequestFlowInterceptor requestFlowInterceptor;

    public WebMvcConfig(RequestFlowInterceptor requestFlowInterceptor) {
        this.requestFlowInterceptor = requestFlowInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 모든 요청 경로에 RequestFlowInterceptor 적용
        registry.addInterceptor(requestFlowInterceptor);
    }
}
