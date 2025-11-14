package com.example.develop.DTO;

import com.example.develop.entity.Groupbuy;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class GroupbuyDTO {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String content;
    private Integer sales;
    private Boolean isAvailable;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    // 静态方法：将 Groupbuy 转换为 GroupbuyDTO
    public static GroupbuyDTO fromEntity(Groupbuy groupbuy) {
        if (groupbuy == null) {
            return null;
        }

        return GroupbuyDTO.builder()
                .id(groupbuy.getId())
                .title(groupbuy.getTitle())
                .price(groupbuy.getPrice())
                .description(groupbuy.getDescription())
                .content(groupbuy.getContent())
                .sales(groupbuy.getSales())
                .isAvailable(groupbuy.getIsAvailable())
                .validFrom(groupbuy.getValidFrom())
                .validTo(groupbuy.getValidTo())
                .build();
    }
}