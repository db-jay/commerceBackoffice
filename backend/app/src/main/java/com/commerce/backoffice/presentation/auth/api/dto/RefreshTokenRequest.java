package com.commerce.backoffice.presentation.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/*
 * 토큰 재발급 요청 DTO.
 */
public record RefreshTokenRequest(
    @NotBlank(message = "refreshToken은 필수입니다.")
    String refreshToken
) {
}
