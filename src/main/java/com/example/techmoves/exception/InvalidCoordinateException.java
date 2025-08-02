package com.example.techmoves.exception;

public class InvalidCoordinateException extends RuntimeException {
    public InvalidCoordinateException(String message) {
        super(message);
    }
}