package com.nak.demo.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.nak.demo.common.config.BakongProperties;
import com.nak.demo.exception.model.BakongException;
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

    private final RestTemplate http;
    private final BakongProperties props;

    public BakongClient(BakongProperties props) {
        this.props = props;
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(8_000);
        this.http = new RestTemplate(factory);
    }

    public JsonNode checkTransactionByMd5(String md5) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(props.token());   // the token stays on the server

        try {
            JsonNode body = http.postForObject(props.baseUrl() + "/v1/check_transaction_by_md5",
                    new HttpEntity<>(Map.of("md5", md5), headers), JsonNode.class);
            if (body == null) throw new BakongException("Empty response from Bakong");
            return body;
        } catch (RestClientException e) {
            throw new BakongException("Bakong request failed", e);
        }
    }
}