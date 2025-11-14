package com.example.develop.DTO;

import com.example.develop.entity.OrderItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long userId;       // 用户 ID

    @NotNull
    private Long groupbuyId;   // 团购套餐 IDr

}