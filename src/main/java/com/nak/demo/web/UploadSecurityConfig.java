package com.nak.demo.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * Product photos (GET /uploads/**) are public: browsers load <img> tags without the login token, so these files
 * must skip the JWT check. Only GET is opened; uploading still goes through the secured /api/v1/products/{id}/image.
 */
@Configuration
public class UploadSecurityConfig {

    @Bean
    public WebSecurityCustomizer publicUploads() {
        return web -> web.ignoring().requestMatchers(HttpMethod.GET, "/uploads/**");
    }
}
