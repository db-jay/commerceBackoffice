package com.commerce.backoffice.infrastructure.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * JWT 보안 설정값.
 */
@Component
@ConfigurationProperties(prefix = "backoffice.security.jwt")
public class JwtSecurityProperties {

    private String secret;
    private long accessTokenExpirySeconds;
    private long refreshTokenExpirySeconds;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpirySeconds() {
        return accessTokenExpirySeconds;
    }

    public void setAccessTokenExpirySeconds(long accessTokenExpirySeconds) {
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
    }

    public long getRefreshTokenExpirySeconds() {
        return refreshTokenExpirySeconds;
    }

    public void setRefreshTokenExpirySeconds(long refreshTokenExpirySeconds) {
        this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
    }
}
