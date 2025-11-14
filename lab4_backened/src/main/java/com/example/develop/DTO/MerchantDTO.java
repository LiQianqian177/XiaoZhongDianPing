package com.example.develop.DTO;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.develop.entity.Merchant;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDTO {
    private Long id;

    @NotBlank(message = "商家名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "地址信息不能为空")
    private String address;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @NotNull(message = "评分不能为空")
    @Builder.Default
    private Double rating = 0.0; // 默认值

    @NotNull(message = "平均消费不能为空")
    private BigDecimal avgCost;

    @NotNull(message = "营业状态不能为空")
    private Boolean isOpen;
    @NotBlank(message = "营业时间不能为空")
    private String businessHours;
    // 使用延迟加载策略时，DTO应避免直接暴露关联集合
    // 可根据业务需求决定是否返回图片列表
    private List<MerchantImageDTO> images;
    private List<DishDTO> dishes;
    // 可选：添加关键字段的简化视图（如用于列表展示）
    public String getSimpleInfo() {
        return String.format("%s (%s)", name, phone);
    }
    private List<GroupbuyDTO> groupbuys;// 团购套餐列表

    // 静态方法：将 Merchant 转换为 MerchantDTO
    public static MerchantDTO fromEntity(Merchant merchant, Boolean isReturningImages,Boolean isReturningDishes, Boolean isReturningGroupbuys) {
        if (merchant == null) {
            return null;
        }

        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setDescription(merchant.getDescription());
        dto.setAddress(merchant.getAddress());
        dto.setPhone(merchant.getPhone());
        dto.setRating(merchant.getRating());
        dto.setBusinessHours(merchant.getBusinessHours());
        dto.setAvgCost(merchant.getAverageCost());
        dto.setIsOpen(merchant.getIsOpen());

        // 转换图片列表
        if (isReturningImages && merchant.getImages() != null) {
            dto.setImages(merchant.getImages().stream()
                    .map(MerchantImageDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        // 转换团购列表
        if (isReturningGroupbuys && merchant.getGroupbuys() != null) {
            dto.setGroupbuys(merchant.getGroupbuys().stream()
                    .map(GroupbuyDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        // 转换菜品列表（可选）
        if (isReturningDishes && merchant.getDishes() != null) {
            dto.setDishes(merchant.getDishes().stream()
                    .map(DishDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
