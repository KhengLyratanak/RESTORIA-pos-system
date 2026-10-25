package com.nak.demo.exception;

import com.nak.demo.controller.CashPaymentController;
import com.nak.demo.controller.PaymentController;
import com.nak.demo.dto.response.ApiError;
import com.nak.demo.exception.model.InvalidPaymentException;
import com.nak.demo.exception.model.PaymentNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Applies only to the two payment controllers and runs before any global handler you already have,
 * so payment errors return a clear 4xx instead of a generic "unexpected error".
 */
@RestControllerAdvice(assignableTypes = {PaymentController.class, CashPaymentController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiError> notFound(PaymentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of(404, e.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiError> invalid(InvalidPaymentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of(400, e.getMessage()));
    }
}
