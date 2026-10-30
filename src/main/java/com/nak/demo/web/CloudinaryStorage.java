package com.nak.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores menu photos on Cloudinary so they survive server restarts (Render's free disk is wiped on every restart).
 * Turned on by the CLOUDINARY_URL environment variable, which looks like cloudinary://API_KEY:API_SECRET@CLOUD_NAME
 * (copy it from the Cloudinary dashboard). Without it, photos are saved on the local disk as before.
 */
@Component
public class CloudinaryStorage {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryStorage.class);
    private static final String FOLDER = "pos-menu";
    private static final Pattern PUBLIC_ID = Pattern.compile("/image/upload/(?:v\\d+/)?(.+?)(?:\\.[a-zA-Z0-9]+)?$");

    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;
    private final RestClient http = RestClient.create();

    public CloudinaryStorage(@Value("${app.cloudinary-url:}") String cloudinaryUrl) {
        String name = null, key = null, secret = null;
        if (cloudinaryUrl != null && !cloudinaryUrl.isBlank()) {
            try {
                URI uri = URI.create(cloudinaryUrl.trim());
                String[] userInfo = uri.getRawUserInfo().split(":", 2);
                key = URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8);
                secret = URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8);
                name = uri.getHost();
            } catch (RuntimeException e) {
                log.error("CLOUDINARY_URL is set but not in the form cloudinary://API_KEY:API_SECRET@CLOUD_NAME; photos will be saved on local disk");
                name = key = secret = null;
            }
        }
        this.cloudName = name;
        this.apiKey = key;
        this.apiSecret = secret;
        log.info(enabled() ? "Menu photos are stored on Cloudinary (cloud '{}')" : "Menu photos are stored on local disk (CLOUDINARY_URL not set)", cloudName);
    }

    public boolean enabled() {
        return cloudName != null && apiKey != null && apiSecret != null;
    }

    /** Uploads the image and returns its permanent https URL. */
    public String upload(byte[] bytes, String fileName, String publicIdWithoutFolder) {
        long timestamp = System.currentTimeMillis() / 1000;
        String publicId = FOLDER + "/" + publicIdWithoutFolder;
        String signature = sign("public_id=" + publicId + "&timestamp=" + timestamp);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        form.add("public_id", publicId);
        form.add("timestamp", String.valueOf(timestamp));
        form.add("api_key", apiKey);
        form.add("signature", signature);

        try {
            Map<?, ?> body = http.post()
                    .uri("https://api.cloudinary.com/v1_1/{cloud}/image/upload", cloudName)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(form)
                    .retrieve()
                    .body(Map.class);
            Object url = body == null ? null : body.get("secure_url");
            if (url == null) throw new IllegalStateException("Cloudinary did not return an image URL");
            return url.toString();
        } catch (RestClientException e) {
            throw new IllegalStateException("could not upload the image to Cloudinary: " + e.getMessage(), e);
        }
    }

    /** Deletes a photo previously uploaded here. Failures are only logged: an old photo left behind is harmless. */
    public void delete(String imageUrl) {
        if (!isCloudinaryUrl(imageUrl)) return;
        Matcher m = PUBLIC_ID.matcher(URI.create(imageUrl).getPath());
        if (!m.find()) return;
        String publicId = m.group(1);
        long timestamp = System.currentTimeMillis() / 1000;

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("public_id", publicId);
        form.add("timestamp", String.valueOf(timestamp));
        form.add("api_key", apiKey);
        form.add("signature", sign("public_id=" + publicId + "&timestamp=" + timestamp));
        try {
            http.post()
                    .uri("https://api.cloudinary.com/v1_1/{cloud}/image/destroy", cloudName)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Could not delete old Cloudinary photo {}: {}", publicId, e.getMessage());
        }
    }

    public boolean isCloudinaryUrl(String imageUrl) {
        return enabled() && imageUrl != null
                && imageUrl.startsWith("https://res.cloudinary.com/" + cloudName + "/image/upload/");
    }

    /** Cloudinary signature: SHA-1 of the sorted parameters followed by the API secret, in hex. */
    private String sign(String sortedParams) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(sha1.digest((sortedParams + apiSecret).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
