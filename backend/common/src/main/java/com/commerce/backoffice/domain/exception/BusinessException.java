package com.commerce.backoffice.domain.exception;

/*
 * 도메인(비즈니스) 규칙 위반을 표현하는 예외.
 * RuntimeException을 상속하지만, ErrorCode를 함께 보관해
 * ControllerAdvice에서 표준 에러 응답으로 변환하기 쉽게 만든다.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        // 상위 예외 메시지에는 사람이 읽을 메시지를 넣는다.
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public String code() {
        // API 응답 code 필드에 사용
        return errorCode.code();
    }

    public String errorMessage() {
        // API 응답 message 필드에 사용
        return errorCode.message();
    }
}
