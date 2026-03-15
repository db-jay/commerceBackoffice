package com.commerce.backoffice.application.auth.port.in;

import com.commerce.backoffice.application.auth.command.LoginCommand;
import com.commerce.backoffice.application.auth.command.RefreshTokenCommand;
import com.commerce.backoffice.application.auth.model.AuthTokenPair;

/*
 * 인증 유스케이스 진입 포트.
 */
public interface AuthUseCase {

    AuthTokenPair login(LoginCommand command);

    AuthTokenPair refresh(RefreshTokenCommand command);
}
