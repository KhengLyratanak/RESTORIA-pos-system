package com.nak.demo.dto.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDto {
    @NotNull(message = "username is required")
    private String username;
    @NotNull(message = "password is required")
    @NotBlank(message = "password must not be blank")
    @Size(min = 8,max = 20,message = "password = must be between 8-20 charector")
    private String password;
}
