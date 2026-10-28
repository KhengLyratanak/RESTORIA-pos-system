package com.nak.demo.mapper;


import com.fasterxml.jackson.databind.JsonNode;
import com.nak.demo.dto.response.BakongTransactionResponse;
import com.nak.demo.entity.BakongTransactionLog;
import com.nak.demo.exception.model.BakongException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BakongMapper {

    /** Converts the 'data' object of Bakong's response into our own DTO. */
    public BakongTransactionResponse toTransaction(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new BakongException(
                    "Invalid Bakong transaction data"
            );
        }

        JsonNode amountNode = data.get("amount");
        JsonNode currencyNode = data.get("currency");
        JsonNode hashNode = data.get("hash");
        JsonNode toAccountNode = data.get("toAccountId");

        if (amountNode == null || !amountNode.isNumber()
                || currencyNode == null || !currencyNode.isTextual()
                || currencyNode.asText().isBlank()
                || hashNode == null || !hashNode.isTextual()
                || hashNode.asText().isBlank()
                || toAccountNode == null || !toAccountNode.isTextual()
                || toAccountNode.asText().isBlank()) {
            throw new BakongException(
                    "Bakong transaction is missing required fields"
            );
        }

        return new BakongTransactionResponse(
                hashNode.asText(),
                amountNode.decimalValue(),
                currencyNode.asText(),
                data.path("fromAccountId").asText(null),
                toAccountNode.asText()
        );
    }
    public BakongTransactionLog toLog(UUID paymentId, String md5, BakongTransactionResponse tx, Instant now) {
        return new BakongTransactionLog(paymentId, md5, tx.hash(), tx.amount(), tx.currency().toUpperCase(),
                tx.fromAccountId(), tx.toAccountId(), now);
    }
}
