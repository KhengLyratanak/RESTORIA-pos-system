package com.nak.demo.dto.order;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelOrderDto {
    @Size(max = 255, message = "reason must be at most 255 charecters")
    private String reason;
}

