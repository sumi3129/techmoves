package com.example.techmoves.exception;

public class OrderAlreadyTakenException extends RuntimeException {
    public OrderAlreadyTakenException(Long id) {
        super("Order with id " + id + " is already taken");
    }
}
