package com.example.develop.exception;

// 资源找不到异常
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
