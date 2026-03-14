package com.commerce.backoffice.presentation.common.response;

import java.time.Instant;

/*
 * 모든 API 응답이 공통으로 따르는 표준 구조.
 * - code: 결과 코드 (예: SUCCESS, VALIDATION_ERROR)
 * - message: 사람이 읽을 메시지
 * - timestamp: 응답 생성 시각
 * - data: 실제 데이터(payload)
 */
public record BaseResponse<T>(
    String code,
    String message,
    Instant timestamp,
    T data
) {

    public static <T> BaseResponse<T> success(T data) {
        // 성공 응답 기본 포맷
        return new BaseResponse<>("SUCCESS", "요청이 성공했습니다.", Instant.now(), data);
    }

    public static <T> BaseResponse<T> failure(String code, String message, T data) {
        // 실패 응답 기본 포맷
        return new BaseResponse<>(code, message, Instant.now(), data);
    }
}
