package com.example.develop.DTO;

public record LoggedUserResponse(
        boolean loggedIn,
        String name,
        Long userID
) {
}