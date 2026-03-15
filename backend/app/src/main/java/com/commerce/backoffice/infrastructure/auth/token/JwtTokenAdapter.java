package com.commerce.backoffice.infrastructure.auth.token;

import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.application.auth.port.out.TokenPort;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.domain.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * [역할]
 * - JWT Access/Refresh 토큰을 실제로 발급하고 검증하는 Infrastructure 어댑터다.
 *
 * [왜 필요한가]
 * - Application은 "토큰을 발급/검증해야 한다"는 사실만 알면 된다.
 * - JJWT 같은 라이브러리 상세 코드는 Infrastructure에만 두어야 교체/테스트가 쉽다.
 *
 * [흐름]
 * - issueTokenPair
 *   1) 현재 시각 기준 만료시각 계산
 *   2) ACCESS / REFRESH 두 종류 토큰 생성
 * - parseAccessToken / parseRefreshToken
 *   1) 서명/형식 검증
 *   2) claim 추출
 *   3) tokenType 확인
 *   4) subject/role을 AuthenticatedOperator로 복원
 *
 * [주의할 점]
 * - 현재는 stateless 방식이다. 즉, Refresh 저장소(Redis/DB)는 아직 없다.
 * - tokenType claim으로 Access/Refresh 혼용을 막는다.
 * - 토큰 파싱 실패는 BusinessException이 아니라 UnauthorizedException으로 바꿔 401로 처리한다.
 */
@Component
public class JwtTokenAdapter implements TokenPort {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";

    private final String secret;
    private final long accessTokenExpirySeconds;
    private final long refreshTokenExpirySeconds;

    public JwtTokenAdapter(
        @Value("${backoffice.security.jwt.secret:backoffice-learning-secret-key-change-me-please-2026}") String secret,
        @Value("${backoffice.security.jwt.access-token-expiry-seconds:1800}") long accessTokenExpirySeconds,
        @Value("${backoffice.security.jwt.refresh-token-expiry-seconds:1209600}") long refreshTokenExpirySeconds
    ) {
        this.secret = secret;
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
        this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
    }

    @Override
    public AuthTokenPair issueTokenPair(AuthenticatedOperator operator) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plusSeconds(accessTokenExpirySeconds);
        Instant refreshExpiry = now.plusSeconds(refreshTokenExpirySeconds);

        String accessToken = issue(operator, now, accessExpiry, ACCESS);
        String refreshToken = issue(operator, now, refreshExpiry, REFRESH);

        return new AuthTokenPair(
            accessToken,
            refreshToken,
            "Bearer",
            accessTokenExpirySeconds,
            refreshTokenExpirySeconds
        );
    }

    @Override
    public AuthenticatedOperator parseAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        validateTokenType(claims, ACCESS);
        return toOperator(claims);
    }

    @Override
    public AuthenticatedOperator parseRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        validateTokenType(claims, REFRESH);
        return toOperator(claims);
    }

    private String issue(AuthenticatedOperator operator, Instant now, Instant expiry, String tokenType) {
        // JWT 안에는 "누구(subject)"와 "무슨 권한(role)"인지,
        // 그리고 ACCESS/REFRESH 중 어떤 토큰인지(tokenType)를 함께 담는다.
        return Jwts.builder()
            .subject(operator.subject())
            .claim(CLAIM_ROLE, operator.role())
            .claim(CLAIM_TOKEN_TYPE, tokenType)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .id(UUID.randomUUID().toString())
            .signWith(signingKey())
            .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            // 토큰이 깨졌거나, 서명이 다르거나, 만료되었거나,
            // 아예 형식이 잘못된 경우 모두 "유효하지 않은 토큰"으로 본다.
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
    }

    private void validateTokenType(Claims claims, String expectedType) {
        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);

        // Access 자리에 Refresh를 넣거나 그 반대를 넣으면 거부한다.
        if (!expectedType.equals(tokenType)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
    }

    private AuthenticatedOperator toOperator(Claims claims) {
        String subject = claims.getSubject();
        String role = claims.get(CLAIM_ROLE, String.class);

        // 최소한 sub, role이 없으면 인증 주체를 복원할 수 없다.
        if (subject == null || role == null || role.isBlank()) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        return new AuthenticatedOperator(subject, role);
    }

    private SecretKey signingKey() {
        // 같은 secret으로 서명/검증해야만 같은 서비스가 발급한 토큰인지 확인할 수 있다.
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
