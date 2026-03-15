package com.commerce.backoffice.presentation.auth.api.mapper;

import com.commerce.backoffice.application.auth.command.LoginCommand;
import com.commerce.backoffice.application.auth.command.RefreshTokenCommand;
import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.presentation.auth.api.dto.AuthTokenResponse;
import com.commerce.backoffice.presentation.auth.api.dto.LoginRequest;
import com.commerce.backoffice.presentation.auth.api.dto.RefreshTokenRequest;
import org.springframework.stereotype.Component;

/*
 * Auth 프레젠테이션 매퍼 구현체.
 */
@Component
public class AuthPresentationMapperImpl implements AuthPresentationMapper {

    @Override
    public LoginCommand toLoginCommand(LoginRequest request) {
        return new LoginCommand(request.username(), request.password());
    }

    @Override
    public RefreshTokenCommand toRefreshCommand(RefreshTokenRequest request) {
        return new RefreshTokenCommand(request.refreshToken());
    }

    @Override
    public AuthTokenResponse toResponse(AuthTokenPair tokenPair) {
        return new AuthTokenResponse(
            tokenPair.tokenType(),
            tokenPair.accessToken(),
            tokenPair.accessTokenExpiresIn(),
            tokenPair.refreshToken(),
            tokenPair.refreshTokenExpiresIn()
        );
    }
}
