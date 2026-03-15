package com.commerce.backoffice.presentation.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

/*
 * 로그인 요청 DTO.
 */
public record LoginRequest(
    @NotBlank(message = "username은 필수입니다.")
    String username,

    @NotBlank(message = "password는 필수입니다.")
    String password
) {
}
