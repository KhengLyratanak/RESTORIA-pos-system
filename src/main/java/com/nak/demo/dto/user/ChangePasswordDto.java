package com.nak.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {

    @NotBlank(message = "old password is required")
    @JsonAlias("old_password")
    private String oldPassword;

    @NotBlank(message = "new password is required")
    @Size(min = 8, max = 20, message = "password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "password must contain at least one letter and one number")
    @JsonAlias("new_password")
    private String newPassword;

    @NotBlank(message = "confirm password is required")
    @JsonAlias("confirm_password")
    private String confirmPassword;

    }


