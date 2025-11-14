package com.example.develop.DTO;

import java.math.BigDecimal;

import com.example.develop.entity.Dish;
import lombok.Data;
// DishDTO
@Data
public class DishDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
    private Long merchantId;  // 关联的商家ID

    // getter, setter, constructor等
    // 静态方法：将 Dish 转换为 DishDTO
    public static DishDTO fromEntity(Dish dish) {
        if (dish == null) {
            return null;
        }

        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setDescription(dish.getDescription());
        dto.setPrice(dish.getPrice());
        dto.setCategory(dish.getCategory());
        dto.setImageUrl(dish.getImageUrl());
        dto.setIsAvailable(dish.getIsAvailable());

        if (dish.getMerchant() != null) {
            dto.setMerchantId(dish.getMerchant().getId());
        }

        return dto;
    }
}


