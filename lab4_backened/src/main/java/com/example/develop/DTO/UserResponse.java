package com.example.develop.DTO;

import com.example.develop.entity.User;

public record UserResponse(
        Long id,
        String name
) {
    public UserResponse(User user){
        this(user.getId(), user.getName());
    }
}
