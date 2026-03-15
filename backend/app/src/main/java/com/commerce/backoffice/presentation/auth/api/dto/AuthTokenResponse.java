package com.commerce.backoffice.presentation.auth.api.dto;

/*
 * 로그인/재발급 응답 DTO.
 */
public record AuthTokenResponse(
    String tokenType,
    String accessToken,
    long accessTokenExpiresIn,
    String refreshToken,
    long refreshTokenExpiresIn
) {
}
