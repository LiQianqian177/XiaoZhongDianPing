package com.example.develop.entity;

import com.example.develop.DTO.DishDTO;
import com.example.develop.utils.PinyinUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Setter
    @Column(name = "name_pinyin") // 新增拼音字段
    private String namePinyin;
    // 在保存或更新时自动生成拼音
    @PrePersist
    @PreUpdate
    private void generatePinyin() {
        this.namePinyin = PinyinUtils.toFuzzyPinyin(this.name);
        this.categoryPinyin = PinyinUtils.toFuzzyPinyin(this.category);
    }

    private String description;

    @NotNull
    private BigDecimal price;

    private String category;
    @Column(name = "category_pinyin")
    private String categoryPinyin;  // 菜品类别拼音
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    // 图片URL
    private String imageUrl;

    // 是否上架
    private Boolean isAvailable;

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
        dto.setMerchantId(dish.getMerchant() != null ? dish.getMerchant().getId() : null);

        return dto;
    }

}