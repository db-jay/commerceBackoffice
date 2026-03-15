package com.commerce.backoffice.presentation.auth.api.mapper;

import com.commerce.backoffice.application.auth.command.LoginCommand;
import com.commerce.backoffice.application.auth.command.RefreshTokenCommand;
import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.presentation.auth.api.dto.AuthTokenResponse;
import com.commerce.backoffice.presentation.auth.api.dto.LoginRequest;
import com.commerce.backoffice.presentation.auth.api.dto.RefreshTokenRequest;

/*
 * Auth 프레젠테이션 변환 계약.
 */
public interface AuthPresentationMapper {

    LoginCommand toLoginCommand(LoginRequest request);

    RefreshTokenCommand toRefreshCommand(RefreshTokenRequest request);

    AuthTokenResponse toResponse(AuthTokenPair tokenPair);
}
