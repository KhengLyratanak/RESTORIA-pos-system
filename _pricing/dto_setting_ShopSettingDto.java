package com.nak.demo.dto.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShopSettingDto {
    @JsonProperty("vat_rate")
    @NotNull(message = "vat rate is required")
    @DecimalMin(value = "0", message = "vat rate cannot be negative")
    @DecimalMax(value = "100", message = "vat rate must be at most 100")
    private BigDecimal vatRate;

    @JsonProperty("service_rate")
    @NotNull(message = "service rate is required")
    @DecimalMin(value = "0", message = "service rate cannot be negative")
    @DecimalMax(value = "100", message = "service rate must be at most 100")
    private BigDecimal serviceRate;
}
