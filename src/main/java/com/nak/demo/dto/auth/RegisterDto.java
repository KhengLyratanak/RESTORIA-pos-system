package com.nak.demo.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "username is required")
    @Size(min = 4, max = 30, message = "username must be between 4 and 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$",
            message = "username may only contain letters, numbers, dot, underscore and dash")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Size(max = 100, message = "email must be at most 100 characters")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 20, message = "password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "password must contain at least one letter and one number")
    private String password;

    @NotBlank(message = "confirm password is required")
    @JsonProperty("confirm_password")
    private String confirmPassword;

    @NotNull(message = "age is required")
    @Min(value = 18, message = "age must be at least 18")
    @Max(value = 120, message = "age must be at most 120")
    private Integer age;

    @NotBlank(message = "address is required")
    @Size(min = 5, max = 50, message = "address must be between 5 and 50 characters")
    private String address;
}