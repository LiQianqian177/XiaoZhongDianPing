package com.example.develop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "groupbuy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Groupbuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "套餐标题不能为空")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "套餐价格不能为空")
    @Column(nullable = false)
    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "套餐内容不能为空")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @NotNull
    @Column(name = "sales", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer sales;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    // 团购套餐的有效期开始时间
    @Column(name = "valid_from", nullable = true)
    private LocalDateTime validFrom;

    // 团购套餐的有效期结束时间
    @Column(name = "valid_to", nullable = true)
    private LocalDateTime validTo;

    // 是否上架
    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

}