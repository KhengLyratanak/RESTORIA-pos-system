package com.nak.demo.common.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.HashMap;

@Configuration
@ConfigurationProperties(prefix = "config")
@Getter
@Setter
public class ApplicationConfiguration {
    private Security security;
    private Pagination pagination;

    @Getter
    @Setter
    public static class Security{
        private String secret;
        private long expiration;
        private long refreshTokenExpiration; // refresh-token-expiration
    }

    @Getter
    @Setter
    public static class Pagination{
        private String baseUrl;
        private HashMap<String,String> uri;

        public String getUrlByResource(String resource){
            return baseUrl.concat(uri.getOrDefault(resource,""));
        }

    }
}
