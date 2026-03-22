package com.commerce.backoffice.domain.exception;

/*
 * 공통 에러 코드 열거형.
 * - code: 시스템/클라이언트가 분기할 때 사용하는 값
 * - message: 사람이 읽는 설명
 */
public enum ErrorCode {
    DEMO_BUSINESS_ERROR("DEMO_BUSINESS_ERROR", "비즈니스 규칙 위반입니다."),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELED("ORDER_ALREADY_CANCELED", "이미 취소된 주문입니다."),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "재고가 부족합니다."),
    PRODUCT_STOCK_CONFLICT("PRODUCT_STOCK_CONFLICT", "다른 요청이 먼저 재고를 변경했습니다. 다시 시도해주세요."),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "인증에 실패했습니다."),
    INVALID_TOKEN("INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    ACCESS_DENIED("ACCESS_DENIED", "접근 권한이 없습니다."),
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
