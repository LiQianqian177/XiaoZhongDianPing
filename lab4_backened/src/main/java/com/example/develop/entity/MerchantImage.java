package com.example.develop.entity;

import com.example.develop.DTO.MerchantImageDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchant_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "image_type")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    @JsonIgnore
    private Merchant merchant;

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
