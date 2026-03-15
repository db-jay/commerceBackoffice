package com.commerce.backoffice.application.auth.model;

/*
 * 로그인/재발급 결과 토큰 묶음.
 */
public record AuthTokenPair(
    String accessToken,
    String refreshToken,
    String tokenType,
    long accessTokenExpiresIn,
    long refreshTokenExpiresIn
) {
}
