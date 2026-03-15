package com.commerce.backoffice.presentation.member.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * 회원 기본정보 수정 요청 DTO.
 */
public record UpdateMemberRequest(
    @NotBlank(message = "name은 필수입니다.")
    String name,

    @NotBlank(message = "grade는 필수입니다.")
    @Pattern(regexp = "BASIC|VIP|VVIP", message = "grade는 BASIC/VIP/VVIP 중 하나여야 합니다.")
    String grade
) {
}

