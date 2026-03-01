package com.commerce.backoffice.domain.exception;

/*
 * 공통 에러 코드 열거형.
 * - code: 시스템/클라이언트가 분기할 때 사용하는 값
 * - message: 사람이 읽는 설명
 */
public enum ErrorCode {
    DEMO_BUSINESS_ERROR("DEMO_BUSINESS_ERROR", "비즈니스 규칙 위반입니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "요청 값이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        // 예: 응답 JSON의 code 필드
        return code;
    }

    public String message() {
        // 예: 응답 JSON의 message 필드
        return message;
    }
}
