package com.commerce.backoffice.presentation.common.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Swagger UI 진입 경로 고정.
 *
 * - 팀에서 익숙한 `/swagger-ui.html` 주소를 유지한다.
 * - 실제 정적 페이지는 `/swagger-ui/index.html`에서 제공한다.
 */
@Controller
public class SwaggerUiRedirectController {

    @GetMapping("/swagger-ui.html")
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
