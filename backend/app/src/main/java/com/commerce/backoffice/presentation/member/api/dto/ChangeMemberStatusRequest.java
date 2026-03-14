package com.commerce.backoffice.presentation.member.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * 회원 상태 변경 요청 DTO.
 */
public record ChangeMemberStatusRequest(
    @NotBlank(message = "status는 필수입니다.")
    @Pattern(regexp = "ACTIVE|DORMANT|WITHDRAWN", message = "status는 ACTIVE/DORMANT/WITHDRAWN 중 하나여야 합니다.")
    String status
) {
}

