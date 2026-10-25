package com.nak.demo.dto.response;

import java.math.BigDecimal;

public record PayableOrderResponse(Long id, String number, BigDecimal amount, String currency) {}
