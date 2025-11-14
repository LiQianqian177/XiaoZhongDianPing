package com.example.develop.DTO;

import java.util.Date;

// 新增在DTO包中
public record AuthStatusResponse(
        boolean isAuthenticated,
        String username,
        Date expiration
) {}

