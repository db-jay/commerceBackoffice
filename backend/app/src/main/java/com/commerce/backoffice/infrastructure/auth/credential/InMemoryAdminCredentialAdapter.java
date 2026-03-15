package com.commerce.backoffice.infrastructure.auth.credential;

import com.commerce.backoffice.application.auth.port.out.OperatorCredentialPort;
import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * 학습용 운영자 인증 어댑터.
 *
 * 현재 전략:
 * - 설정값(환경변수/application.yml)의 단일 관리자 계정과 비교
 */
@Component
public class InMemoryAdminCredentialAdapter implements OperatorCredentialPort {

    private final String adminUsername;
    private final String adminPassword;
    private final String adminRole;

    public InMemoryAdminCredentialAdapter(
        @Value("${backoffice.security.admin.username:admin}") String adminUsername,
        @Value("${backoffice.security.admin.password:admin1234}") String adminPassword,
        @Value("${backoffice.security.admin.role:ADMIN}") String adminRole
    ) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminRole = adminRole;
    }

    @Override
    public Optional<AuthenticatedOperator> authenticate(String username, String rawPassword) {
        boolean matched = adminUsername.equals(username) && adminPassword.equals(rawPassword);

        if (!matched) {
            return Optional.empty();
        }

        return Optional.of(new AuthenticatedOperator(adminUsername, adminRole));
    }
}
