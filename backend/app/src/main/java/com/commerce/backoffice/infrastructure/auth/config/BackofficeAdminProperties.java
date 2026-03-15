package com.commerce.backoffice.infrastructure.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * 학습용 백오피스 관리자 계정 설정.
 *
 * 주의:
 * - 현재는 학습 목적의 단순 구현(설정 파일 기반)이다.
 * - 운영 환경에서는 DB/외부 IAM 연동으로 교체해야 한다.
 */
@Component
@ConfigurationProperties(prefix = "backoffice.security.admin")
public class BackofficeAdminProperties {

    private String username;
    private String password;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
