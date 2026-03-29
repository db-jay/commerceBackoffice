package com.commerce.backoffice.presentation.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * [역할]
 * - Spring Security의 전체 보안 규칙을 한 곳에서 설정한다.
 *
 * [왜 필요한가]
 * - 어떤 URL이 공개인지, 어떤 URL이 인증이 필요한지,
 *   어떤 역할이 어떤 API를 호출할 수 있는지를 중앙에서 보여줘야 이해하기 쉽다.
 *
 * [핵심 정책]
 * - 공개 URL: /health, /demo/**, /api/auth/**, Swagger/OpenAPI 문서
 * - 인증 필요: /api/**
 * - 인가 정책:
 *   - 회원 변경 API(PATCH/POST): ADMIN 전용
 *   - 상품 변경 API(POST/PATCH): ADMIN 또는 MD
 *   - 주문 생성 API(POST): ADMIN 또는 MD
 *
 * [주의할 점]
 * - 인증 실패는 401, 인가 실패는 403으로 분리한다.
 * - JWT 인증 필터는 UsernamePasswordAuthenticationFilter보다 먼저 동작해야 한다.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        JwtAuthenticationEntryPoint authenticationEntryPoint,
        JwtAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // JWT 기반 API 서버이므로 세션과 CSRF를 단순화한다.
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/health",
                    "/demo/**",
                    "/actuator/health",
                    "/api/auth/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/members/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/members/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/catalog/**").hasAnyRole("ADMIN", "MD")
                .requestMatchers(HttpMethod.PATCH, "/api/catalog/**").hasAnyRole("ADMIN", "MD")
                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("ADMIN", "MD")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            // JWT 인증을 먼저 끝낸 뒤, 이후 Security가 인가 판단을 하도록 순서를 맞춘다.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
