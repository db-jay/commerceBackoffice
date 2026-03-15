package com.commerce.backoffice.application.auth.port.out;

import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;

/*
 * JWT 토큰 발급/검증 포트.
 */
public interface TokenPort {

    AuthTokenPair issueTokenPair(AuthenticatedOperator operator);

    AuthenticatedOperator parseAccessToken(String accessToken);

    AuthenticatedOperator parseRefreshToken(String refreshToken);
}
