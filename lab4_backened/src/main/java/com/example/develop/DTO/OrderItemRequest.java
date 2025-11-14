package com.example.develop.DTO;

public record OrderItemRequest(
        Long dishId, // 菜品ID
        Integer quantity // 购买数量
) {
}
