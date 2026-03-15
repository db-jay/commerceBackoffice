package com.commerce.backoffice.presentation.member.api.dto;

/*
 * 회원 응답 DTO.
 * - 필드 정의만 담당한다.
 * - 변환 규칙은 MemberPresentationMapper에서 관리한다.
 */
public record MemberResponse(
    Long id,
    String email,
    String name,
    String grade,
    String status
) {
}
