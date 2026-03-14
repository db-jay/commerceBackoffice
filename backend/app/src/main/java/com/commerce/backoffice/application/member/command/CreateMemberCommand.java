package com.commerce.backoffice.application.member.command;

/*
 * 회원 생성 유스케이스 입력 커맨드.
 *
 * 왜 record를 쓰나?
 * - "입력 데이터 전달" 목적이 분명해서 DTO처럼 간단히 유지할 수 있다.
 * - 불변(immutable)이라 흐름 추적이 쉽다.
 */
public record CreateMemberCommand(
    String email,
    String name
) {
}

