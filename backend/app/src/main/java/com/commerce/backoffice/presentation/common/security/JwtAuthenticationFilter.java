package com.commerce.backoffice.presentation.common.security;

import com.commerce.backoffice.application.auth.port.out.TokenPort;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.domain.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * [역할]
 * - 모든 보호된 API 요청에서 Access 토큰을 읽고 인증 정보를 SecurityContext에 넣는다.
 *
 * [왜 필요한가]
 * - Controller나 Service에서 매번 JWT를 직접 읽으면 코드가 흩어진다.
 * - 인증은 필터에서 한 번만 처리하고, 이후 계층은 "이미 인증된 사용자"를 전제로 움직이는 편이 깔끔하다.
 *
 * [흐름]
 * 1) Authorization 헤더에서 Bearer 토큰 찾기
 * 2) 토큰이 없으면 그냥 다음 필터로 넘기기
 * 3) 토큰이 있으면 Access 토큰 검증
 * 4) 성공 시 SecurityContext에 subject/role 등록
 * 5) 실패 시 401 공통 에러 응답 작성
 *
 * [주의할 점]
 * - 이 필터는 Access 토큰만 처리한다.
 * - Refresh 토큰은 /api/auth/refresh API에서 별도로 검증한다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenPort tokenPort;
    private final SecurityResponseWriter securityResponseWriter;

    public JwtAuthenticationFilter(TokenPort tokenPort, SecurityResponseWriter securityResponseWriter) {
        this.tokenPort = tokenPort;
        this.securityResponseWriter = securityResponseWriter;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 아예 없으면 인증을 시도하지 않고 다음 단계로 넘긴다.
        // 이후 SecurityConfig 규칙에 따라 401/permitAll 여부가 결정된다.
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            AuthenticatedOperator operator = tokenPort.parseAccessToken(token);
            setAuthentication(operator, request);
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException ex) {
            // 토큰이 잘못되었으면 이후 흐름으로 넘기지 않고 즉시 401 응답을 쓴다.
            securityResponseWriter.writeError(response, HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN);
        }
    }

    private void setAuthentication(AuthenticatedOperator operator, HttpServletRequest request) {
        // Spring Security는 권한 문자열 앞에 ROLE_ 접두사를 붙인 형태를 자주 사용한다.
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            operator.subject(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_" + operator.role()))
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
