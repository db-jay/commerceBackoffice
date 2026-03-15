package com.commerce.backoffice.application.member.command;

/*
 * 회원 수정 입력 커맨드.
 */
public record UpdateMemberCommand(
    String name,
    String grade
) {
}

