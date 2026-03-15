package com.commerce.backoffice.domain.auth;

/*
 * 인증된 백오피스 운영자 모델.
 *
 * 왜 Domain 모델로 두나?
 * - JWT claim(sub/role)을 application/presentation에서 문자열로 직접 다루면
 *   오타/규칙 누락이 생기기 쉽다.
 * - "인증 주체"를 하나의 명시적 타입으로 다루면 흐름 추적이 쉬워진다.
 */
public record AuthenticatedOperator(
    String subject,
    String role
) {
}
