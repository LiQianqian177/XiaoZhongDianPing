package com.example.develop.entity;

import com.example.develop.utils.PinyinUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "merchant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "商家名称不能为空")
    @Column(nullable = false)
    private String name;


    @Setter
    @Column(name = "name_pinyin") // 新增拼音字段
    private String namePinyin;
    // 在保存或更新时自动生成拼音
    @PrePersist
    @PreUpdate
    private void generatePinyin() {
        this.namePinyin = PinyinUtils.toFuzzyPinyin(this.name);
        this.addressPinyin = PinyinUtils.toFuzzyPinyin(this.address);
    }

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "地址不能为空")
    private String address;


    @Setter
    @Column(name = "address_pinyin") // 新增拼音字段
    private String addressPinyin;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @NotNull
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double rating;

    @NotNull
    @Column(name = "is_open", nullable = false)
    private Boolean isOpen;
    @NotBlank(message = "营业时间不能为空")
    private String businessHours;
    @NotNull
    @Column(name = "averageCost", nullable = false)
    private BigDecimal averageCost;
    @NotNull
    @Column(name = "minPrice", nullable = false)
    private BigDecimal minPrice;
    @NotNull
    @Column(name = "maxPrice", nullable = false)
    private BigDecimal maxPrice;
    @OneToMany(
            mappedBy = "merchant",
            cascade = CascadeType.ALL,
            orphanRemoval = true// 自动删除无关联的子项
    )
    @Builder.Default  // 关键：保留初始化的值
    private List<MerchantImage> images = new ArrayList<>();

    // 添加菜品列表
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dish> dishes;

    // 添加团购套餐列表
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Groupbuy> groupbuys = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 增加与订单的联系
    //@OneToMany(mappedBy = "Merchant", cascade = CascadeType.ALL)
    //private List<Order> orders;

}
