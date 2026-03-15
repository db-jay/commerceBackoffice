package com.commerce.backoffice.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.commerce.backoffice.application.auth.command.LoginCommand;
import com.commerce.backoffice.application.auth.command.RefreshTokenCommand;
import com.commerce.backoffice.application.auth.model.AuthTokenPair;
import com.commerce.backoffice.application.auth.port.out.OperatorCredentialPort;
import com.commerce.backoffice.application.auth.port.out.TokenPort;
import com.commerce.backoffice.application.auth.service.AuthApplicationService;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import com.commerce.backoffice.domain.exception.UnauthorizedException;
import com.commerce.backoffice.support.fixture.TestFixtureFactory;
import com.commerce.backoffice.support.template.ApplicationServiceTestTemplate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/*
 * [역할]
 * - AuthApplicationService를 Spring 없이 빠르게 검증하는 테스트다.
 *
 * [왜 중요한가]
 * - 이 테스트는 "Application 테스트를 앞으로 어떻게 작성할지" 보여주는 예시다.
 * - Port는 Mock으로 바꾸고, Service 흐름만 검증한다.
 *
 * [검증 포인트]
 * - 로그인 성공 시 토큰 발급
 * - 로그인 실패 시 UnauthorizedException 발생
 * - Refresh 성공 시 새 토큰 발급 흐름 연결
 */
class AuthApplicationServiceTest extends ApplicationServiceTestTemplate {

    @Mock
    private OperatorCredentialPort operatorCredentialPort;

    @Mock
    private TokenPort tokenPort;

    @InjectMocks
    private AuthApplicationService authApplicationService;

    @Test
    void login_should_issue_token_pair_when_credential_is_valid() {
        // given: 정상 운영자 정보와 발급될 토큰 쌍 준비
        AuthenticatedOperator operator = TestFixtureFactory.operator("admin", "ADMIN");
        AuthTokenPair tokenPair = TestFixtureFactory.tokenPair("access-token", "refresh-token");
        LoginCommand command = new LoginCommand("admin", "admin1234");

        given(operatorCredentialPort.authenticate("admin", "admin1234")).willReturn(Optional.of(operator));
        given(tokenPort.issueTokenPair(operator)).willReturn(tokenPair);

        // when: 로그인 UseCase 실행
        AuthTokenPair result = authApplicationService.login(command);

        // then: 토큰이 발급되고, 필요한 Port 호출이 일어났는지 확인
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(operatorCredentialPort).authenticate("admin", "admin1234");
        verify(tokenPort).issueTokenPair(operator);
    }

    @Test
    void login_should_throw_unauthorized_exception_when_credential_is_invalid() {
        LoginCommand command = new LoginCommand("admin", "wrong-password");
        given(operatorCredentialPort.authenticate("admin", "wrong-password")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authApplicationService.login(command))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("인증에 실패했습니다.");
    }

    @Test
    void refresh_should_parse_refresh_token_and_issue_new_pair() {
        AuthenticatedOperator operator = TestFixtureFactory.operator("admin", "ADMIN");
        AuthTokenPair refreshedTokenPair = TestFixtureFactory.tokenPair("new-access-token", "new-refresh-token");
        RefreshTokenCommand command = new RefreshTokenCommand("refresh-token");

        given(tokenPort.parseRefreshToken("refresh-token")).willReturn(operator);
        given(tokenPort.issueTokenPair(operator)).willReturn(refreshedTokenPair);

        AuthTokenPair result = authApplicationService.refresh(command);

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        verify(tokenPort).parseRefreshToken("refresh-token");
        verify(tokenPort).issueTokenPair(operator);
    }
}
