package com.nak.demo.mapper;


import com.fasterxml.jackson.databind.JsonNode;
import com.nak.demo.dto.response.BakongTransactionResponse;
import com.nak.demo.entity.BakongTransactionLog;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BakongMapper {

    /** Converts the 'data' object of Bakong's response into our own DTO. */
    public BakongTransactionResponse toTransaction(JsonNode data) {
        return new BakongTransactionResponse(
                data.path("hash").asText(null),
                data.path("amount").decimalValue(),
                data.path("currency").asText(""),
                data.path("fromAccountId").asText(null),
                data.path("toAccountId").asText(null));
    }

    public BakongTransactionLog toLog(UUID paymentId, String md5, BakongTransactionResponse tx, Instant now) {
        return new BakongTransactionLog(paymentId, md5, tx.hash(), tx.amount(), tx.currency().toUpperCase(),
                tx.fromAccountId(), tx.toAccountId(), now);
    }
}
