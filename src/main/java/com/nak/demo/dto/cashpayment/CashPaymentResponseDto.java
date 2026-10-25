package com.nak.demo.dto.cashpayment;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CashPaymentResponseDto {

    Long orderId;
    BigDecimal total;
    BigDecimal received;
    BigDecimal change;
    String status;

}
