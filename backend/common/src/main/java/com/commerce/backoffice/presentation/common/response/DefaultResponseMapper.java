package com.commerce.backoffice.presentation.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/*
 * ResponseMapper 기본 구현체.
 *
 * 초급자 포인트:
 * - 인터페이스(ResponseMapper): "무엇을 할지" 계약
 * - 구현체(DefaultResponseMapper): "어떻게 할지" 실제 코드
 *
 * Controller는 계약만 의존하므로,
 * 나중에 응답 포맷 정책이 바뀌어도 구현체만 바꾸면 된다.
 */
@Component
public class DefaultResponseMapper implements ResponseMapper {

    @Override
    public <T> ResponseEntity<BaseResponse<T>> ok(T data) {
        // HTTP 200 + 공통 성공 응답
        return ResponseEntity.ok(BaseResponse.success(data));
    }

    @Override
    public ResponseEntity<BaseResponse<Object>> error(HttpStatus status, String code, String message) {
        // 지정한 HTTP 상태코드 + 공통 실패 응답
        return ResponseEntity.status(status).body(BaseResponse.failure(code, message, null));
    }
}
