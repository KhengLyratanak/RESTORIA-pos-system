package com.nak.demo.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenResponeDto extends AuthResponseDto {
    @JsonProperty("token_type")
    private String tokenType;

    public RefreshTokenResponeDto(String accessToken, String refreshToken,String tokenType) {
        super(accessToken, refreshToken);
        setTokenType(tokenType ==null ? "Bearer" : tokenType.trim());
    }
}
