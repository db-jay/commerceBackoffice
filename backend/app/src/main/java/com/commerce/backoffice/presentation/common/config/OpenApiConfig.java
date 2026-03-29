package com.commerce.backoffice.presentation.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Swagger/OpenAPI 문서 메타데이터 설정.
 *
 * 왜 필요한가?
 * - Swagger UI에 프로젝트 이름/설명/버전을 보여주면
 *   주니어가 "지금 어떤 API 문서를 보고 있는지" 더 쉽게 이해할 수 있다.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI backofficeOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Commerce Backoffice API")
                .description("주차별로 확장하는 백오피스 학습용 API 문서")
                .version("v1"));
    }
}
