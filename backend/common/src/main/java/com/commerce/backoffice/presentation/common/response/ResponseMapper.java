package com.commerce.backoffice.presentation.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/*
 * API 응답 변환 포트(인터페이스).
 *
 * 왜 인터페이스로 바꾸나?
 * - Controller/ExceptionHandler는 "응답을 어떻게 만든다"는 구현 상세를 몰라도 된다.
 * - 구현체 교체(예: 버전별 응답 포맷, 멀티 모듈 정책 분기)가 쉬워진다.
 *
 * 즉, presentation 계층에서도 DIP(의존성 역전)를 적용하기 위한 계약이다.
 */
public interface ResponseMapper {

    <T> ResponseEntity<BaseResponse<T>> ok(T data);

    ResponseEntity<BaseResponse<Object>> error(HttpStatus status, String code, String message);
}
