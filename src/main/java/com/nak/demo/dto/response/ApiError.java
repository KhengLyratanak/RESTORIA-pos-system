package com.nak.demo.dto.response;

public record ApiError(String code, String message, String description, long timestamp) {

    public static ApiError of(int httpStatus, String description) {
        return new ApiError(String.valueOf(httpStatus), "fail", description, System.currentTimeMillis());
    }
}
