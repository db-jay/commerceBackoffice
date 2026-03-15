package com.commerce.backoffice.application.auth.command;

/*
 * 토큰 재발급 유스케이스 입력 모델.
 */
public record RefreshTokenCommand(
    String refreshToken
) {
}
