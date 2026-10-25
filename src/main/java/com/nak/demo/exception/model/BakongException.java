package com.nak.demo.exception.model;

public class BakongException extends RuntimeException {
    public BakongException(String message) { super(message); }
    public BakongException(String message, Throwable cause) { super(message, cause); }
}
