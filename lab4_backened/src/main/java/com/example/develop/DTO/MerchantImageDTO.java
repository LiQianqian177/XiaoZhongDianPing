package com.example.develop.DTO;

import com.example.develop.entity.MerchantImage;
import com.example.develop.service.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

// 商家图片DTO
@Data
@AllArgsConstructor
public class MerchantImageDTO {

    private Long id;
    @JsonSerialize(using = ImageUrlSerializer.class)
    private String imageUrl;
    private String description;
    private String type;

    // 静态方法：将 MerchantImage 转换为 MerchantImageDTO
    public static MerchantImageDTO fromEntity(MerchantImage image) {
        if (image == null) {
            return null;
        }

        MerchantImageDTO dto = new MerchantImageDTO(
                image.getId(),
                image.getImageUrl(),
                image.getDescription(),
                image.getType()
        );
        return dto;
    }
}
