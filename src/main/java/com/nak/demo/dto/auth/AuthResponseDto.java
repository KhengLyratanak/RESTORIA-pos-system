package com.nak.demo.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.nak.demo.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    private UserResponseDto user;

    public AuthResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthResponseDto(String accessToken, String refreshToken, UserResponseDto user) {
        this(accessToken, refreshToken);
        this.user = user;
    }
}
