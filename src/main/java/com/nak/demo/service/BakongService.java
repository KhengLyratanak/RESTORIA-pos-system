package com.nak.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nak.demo.client.BakongClient;
import com.nak.demo.dto.response.BakongHealthResponse;
import com.nak.demo.dto.response.BakongTransactionResponse;
import com.nak.demo.exception.model.BakongException;
import com.nak.demo.mapper.BakongMapper;
import com.nak.demo.repository.BakongTransactionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class BakongService {

    private static final Logger log = LoggerFactory.getLogger(BakongService.class);
    private static final String HEALTH_MD5 = "00000000000000000000000000000000";

    private final BakongClient client;
    private final BakongMapper mapper;
    private final BakongTransactionLogRepository logRepository;
    private final Clock clock;

    public BakongService(BakongClient client, BakongMapper mapper,
                         BakongTransactionLogRepository logRepository, Clock clock) {
        this.client = client;
        this.mapper = mapper;
        this.logRepository = logRepository;
        this.clock = clock;
    }

    public Optional<BakongTransactionResponse> findTransaction(String md5) {
        JsonNode body = client.checkTransactionByMd5(md5);

        int code = body.path("responseCode").asInt(-1);
        int errorCode = body.path("errorCode").asInt(-1);
        String message = body.path("responseMessage").asText("?");

        log.info("Bakong reply for md5 {}: responseCode={}, errorCode={}, message={}",
                md5, code, errorCode, message);

        if (code == 0 && body.hasNonNull("data")) {
            return Optional.of(mapper.toTransaction(body.get("data")));
        }

        if (code == 1 && isNotFound(errorCode, message)) {
            return Optional.empty();
        }

        throw new BakongException("Bakong rejected the request: errorCode=" + errorCode
                + ", message=" + message);
    }

    private boolean isNotFound(int errorCode, String message) {
        String m = message == null ? "" : message.toLowerCase();
        return errorCode == 3 || m.contains("not found") || m.contains("could not be found");
    }

    @Transactional
    public boolean recordTransaction(UUID paymentId, String md5, BakongTransactionResponse tx) {
        if (tx.hash() != null && logRepository.existsByBakongHash(tx.hash())) {
            return logRepository.existsByBakongHashAndPaymentId(tx.hash(), paymentId);
        }
        logRepository.save(mapper.toLog(paymentId, md5, tx, clock.instant()));
        return true;
    }

    public BakongHealthResponse health() {
        try {
            findTransaction(HEALTH_MD5);
            return new BakongHealthResponse(true, "Bakong is reachable and accepted the token");
        } catch (BakongException e) {
            return new BakongHealthResponse(false, e.getMessage());
        }
    }
}