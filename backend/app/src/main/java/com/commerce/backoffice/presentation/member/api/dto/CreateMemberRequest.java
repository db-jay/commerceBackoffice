package com.commerce.backoffice.presentation.member.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * 회원 생성 요청 DTO.
 * - HTTP 입력 검증 1차 관문 역할
 */
public record CreateMemberRequest(
    @NotBlank(message = "email은 필수입니다.")
    @Email(message = "email 형식이 올바르지 않습니다.")
    String email,

    @NotBlank(message = "name은 필수입니다.")
    String name
) {
}

