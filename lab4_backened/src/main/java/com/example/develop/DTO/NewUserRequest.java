package com.example.develop.DTO;

public record NewUserRequest(
        String name,
        String password,
        String verificationCode
) {
}
