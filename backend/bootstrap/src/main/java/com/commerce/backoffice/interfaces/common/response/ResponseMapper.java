package com.commerce.backoffice.interfaces.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {
    /*
     * Controller가 응답 형태를 직접 조립하지 않도록 도와주는 클래스.
     * 응답 포맷을 한 곳에서 만들면 API 일관성을 유지하기 쉽다.
     */

    public <T> ResponseEntity<BaseResponse<T>> ok(T data) {
        // HTTP 200 + 공통 성공 응답
        return ResponseEntity.ok(BaseResponse.success(data));
    }

    public ResponseEntity<BaseResponse<Object>> error(HttpStatus status, String code, String message) {
        // 지정한 HTTP 상태코드 + 공통 실패 응답
        return ResponseEntity.status(status).body(BaseResponse.failure(code, message, null));
    }
}
