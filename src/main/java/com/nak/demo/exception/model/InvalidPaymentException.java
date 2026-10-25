package com.nak.demo.exception.model;


public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) { super(message); }
}
