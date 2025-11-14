package com.example.develop.exception;

// 订单处理失败异常
public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message) {
        super(message);
    }
}