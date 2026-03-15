package com.commerce.backoffice.application.auth.command;

/*
 * 로그인 유스케이스 입력 모델.
 */
public record LoginCommand(
    String username,
    String password
) {
}
