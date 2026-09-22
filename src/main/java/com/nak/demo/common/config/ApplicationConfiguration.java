package com.nak.demo.common.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.security.Security;

@Configuration
@ConfigurationProperties(prefix = "config")
@Getter
@Setter
public class ApplicationConfiguration {
    private Security security;

    @Getter
    @Setter
    public static class Security{
        private String secret;
        private long expiration;
        private long refreshTokenExpiration; // refresh-token-expiration
    }
}
