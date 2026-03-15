package com.commerce.backoffice.domain.exception;

/*
 * 인증 실패(401)를 표현하는 공통 예외.
 *
 * 왜 BusinessException과 분리하나?
 * - BusinessException은 현재 409(CONFLICT) 응답으로 내려가고,
 * - 인증 실패는 HTTP 401로 내려야 의미가 정확하기 때문이다.
 */
public class UnauthorizedException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public String code() {
        return errorCode.code();
    }

    public String errorMessage() {
        return errorCode.message();
    }
}
