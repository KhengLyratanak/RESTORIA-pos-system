package com.nak.demo.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bakong")
public record BakongProperties(
        String baseUrl,
        String token,
        String accountId,
        String accountInformation,   // NEW: maps to account-information in the YAML
        boolean merchantAccount,
        String merchantId,
        String acquiringBank,
        String merchantName,
        String merchantCity,
        String terminalLabel,
        int qrTtlSeconds) {

    public BakongProperties {
        if (qrTtlSeconds <= 0) qrTtlSeconds = 180;
        if (terminalLabel == null || terminalLabel.isBlank()) terminalLabel = "POS-1";
    }
}