package com.nak.demo.dto.response;

import java.math.BigDecimal;

public record BakongTransactionResponse(
        String hash,
        BigDecimal amount,
        String currency,
        String fromAccountId,
        String toAccountId) {}
