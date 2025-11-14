package com.example.develop.exception;

public class TokenNotProvidedException  extends RuntimeException{
    public TokenNotProvidedException(String message) {
        super(message);
    }
}
