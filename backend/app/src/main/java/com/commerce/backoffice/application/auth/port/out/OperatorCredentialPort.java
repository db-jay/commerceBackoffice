package com.commerce.backoffice.application.auth.port.out;

import com.commerce.backoffice.domain.auth.AuthenticatedOperator;
import java.util.Optional;

/*
 * 운영자 자격 검증 포트.
 *
 * application은 "검증이 필요하다"만 알고,
 * 실제 저장소/정책(메모리/DB/외부 IdP)은 infrastructure에서 구현한다.
 */
public interface OperatorCredentialPort {

    Optional<AuthenticatedOperator> authenticate(String username, String rawPassword);
}
