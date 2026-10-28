package com.nak.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.nak.demo.common.config.BakongProperties;
import com.nak.demo.exception.model.BakongException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/** VERIFY the path below against the official Bakong API documentation before going live. */
@Component
public class BakongClient {

    private static final Logger log = LoggerFactory.getLogger(BakongClient.class);

    private final RestTemplate http;
    private final BakongProperties props;

    public BakongClient(BakongProperties props) {
        this.props = props;
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(8_000);
        this.http = new RestTemplate(factory);
    }

    /** Raw call: returns the full Bakong JSON body. */
    public JsonNode checkTransactionByMd5(String md5) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.token());   // the token stays on the server

        String url = props.baseUrl() + "/v1/check_transaction_by_md5";
        try {
            JsonNode body = http.postForObject(url,
                    new HttpEntity<>(Map.of("md5", md5), headers), JsonNode.class);
            log.info("Bakong check md5={} -> {}", md5, body);
            if (body == null) throw new BakongException("Empty response from Bakong");
            return body;
        } catch (RestClientException e) {
            log.error("Bakong request failed url={} md5={}: {}", url, md5, e.getMessage());
            throw new BakongException("Bakong request failed: " + e.getMessage(), e);
        }
    }

    /**
     * True only when Bakong says the transaction exists.
     * Success looks like: {"responseCode":0, "data":{ "hash":"...", ... }}
     * Not found looks like: {"responseCode":1, "errorCode":1, ...}
     */
    public boolean isPaid(String md5) {
        JsonNode body = checkTransactionByMd5(md5);
        JsonNode code = body.get("responseCode");
        JsonNode data = body.get("data");
        return code != null && code.asInt(-1) == 0
                && data != null && !data.isNull();
    }
}
