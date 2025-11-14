package com.example.develop.DTO;

public record LoginResponse(
        boolean success,
        String message,
        String token
) {
}