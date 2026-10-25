package com.nak.demo.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(BakongProperties.class)
public class PaymentConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
