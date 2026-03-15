package com.commerce.backoffice.application.auth.service;

import com.commerce.backoffice.application.auth.command.LoginCommand;
import com.commerce.backoffice.application.auth.command.RefreshTokenCommand;
import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.application.auth.port.in.AuthUseCase;
import com.commerce.backoffice.application.auth.port.out.OperatorCredentialPort;
import com.commerce.backoffice.application.auth.port.out.TokenPort;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import com.commerce.backoffice.domain.exception.ErrorCode;
import com.commerce.backoffice.domain.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

/*
 * [역할]
 * - 인증 UseCase의 실제 흐름을 오케스트레이션한다.
 *
 * [왜 필요한가]
 * - Controller는 웹 처리만 해야 하고,
 * - Infrastructure는 JWT 발급/검증이나 자격 확인 같은 기술 세부사항만 담당해야 한다.
 * - 그 중간에서 "무슨 순서로 무엇을 호출할지"를 정리하는 계층이 Application Service다.
 *
 * [흐름]
 * - login
 *   1) 자격 검증 포트 호출
 *   2) 실패 시 UnauthorizedException(401용) 발생
 *   3) 성공 시 토큰 발급 포트 호출
 * - refresh
 *   1) Refresh 토큰 파싱/검증
 *   2) 토큰의 주체(sub, role) 추출
 *   3) 새 토큰 쌍 발급
 *
 * [주의할 점]
 * - 이 서비스는 JWT 라이브러리 코드를 직접 사용하지 않는다.
 * - 토큰 구현 세부사항은 TokenPort 뒤로 숨긴다.
 */
@Service
public class AuthApplicationService implements AuthUseCase {

    private final OperatorCredentialPort operatorCredentialPort;
    private final TokenPort tokenPort;

    public AuthApplicationService(
        OperatorCredentialPort operatorCredentialPort,
        TokenPort tokenPort
    ) {
        this.operatorCredentialPort = operatorCredentialPort;
        this.tokenPort = tokenPort;
    }

    @Override
    public AuthTokenPair login(LoginCommand command) {
        // 운영자 자격이 맞는지 먼저 확인한다.
        AuthenticatedOperator operator = operatorCredentialPort
            .authenticate(command.username(), command.password())
            .orElseThrow(() -> new UnauthorizedException(ErrorCode.AUTHENTICATION_FAILED));

        // 검증이 끝난 운영자 정보로 Access/Refresh 토큰을 만든다.
        return tokenPort.issueTokenPair(operator);
    }

    @Override
    public AuthTokenPair refresh(RefreshTokenCommand command) {
        // Refresh 토큰이 유효한지 먼저 확인하고,
        // 그 토큰이 표현하는 운영자(subject/role)를 복원한다.
        AuthenticatedOperator operator = tokenPort.parseRefreshToken(command.refreshToken());

        // 복원된 운영자 정보로 새 토큰 쌍을 발급한다.
        return tokenPort.issueTokenPair(operator);
    }
}
