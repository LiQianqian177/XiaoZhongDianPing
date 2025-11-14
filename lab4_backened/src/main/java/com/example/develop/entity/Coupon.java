package com.example.develop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type; // 优惠券类型

    @Column(nullable = false)
    private BigDecimal amount; // 优惠券折扣详情

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validTo;

    @Column(nullable = false)
    private BigDecimal usageThreshold; // 使用门槛

    @Column(nullable = true)
    private BigDecimal maxDiscountAmount; // 最大优惠金额

    @Column(nullable = true)
    private String applicableCategory; // 适用品类

    @Column(nullable = true)
    private String applicableStore; // 适用店铺

    @Column(nullable = true) // 空表示不限量
    private Integer totalQuantity; // 发放总量

    @Column(nullable = false)
    private Integer quantityPerUser; // 每个用户可领取的数量

    @Column(nullable = false)
    private String name; // 优惠券标题

    @Column(nullable = false)
    private String description; // 优惠券描述

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isNewUserCoupon; // 默认为 false

    @Column(nullable = false)
    private boolean isFullyClaimed; // 优惠券有没有领完

    // 私有构造函数，防止直接实例化

    public Coupon() {

    }

    // 静态内部类作为 Builder
    public static class Builder {
        private String name;
        private String description;
        private CouponType type;
        private BigDecimal amount;
        private LocalDateTime validFrom;
        private LocalDateTime validTo;
        private BigDecimal usageThreshold;
        private BigDecimal maxDiscountAmount;
        private String applicableCategory;
        private String applicableStore;
        private Integer totalQuantity;
        private Integer quantityPerUser;
        private boolean isNewUserCoupon;
        private  boolean isForever;
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setType(CouponType type) {
            this.type = type;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setValidFrom(LocalDateTime validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public Builder setValidTo(LocalDateTime validTo) {
            this.validTo = validTo;
            return this;
        }

        public Builder setUsageThreshold(BigDecimal usageThreshold) {
            this.usageThreshold = usageThreshold;
            return this;
        }
        public Builder setIsForever(boolean isForever) {
            this.isForever = isForever;
            return this;
        }
        public Builder setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
            this.maxDiscountAmount = maxDiscountAmount;
            return this;
        }

        public Builder setApplicableCategory(String applicableCategory) {
            this.applicableCategory = applicableCategory;
            return this;
        }

        public Builder setApplicableStore(String applicableStore) {
            this.applicableStore = applicableStore;
            return this;
        }

        public Builder setTotalQuantity(Integer totalQuantity) {
            this.totalQuantity = totalQuantity;
            return this;
        }

        public Builder setQuantityPerUser(Integer quantityPerUser) {
            this.quantityPerUser = quantityPerUser;
            return this;
        }

        public Builder setNewUserCoupon(boolean isNewUserCoupon) {
            this.isNewUserCoupon = isNewUserCoupon;
            return this;
        }

        public Coupon build() {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null");
            }
            if (type == null) {
                throw new IllegalArgumentException("Type cannot be null");
            }
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
            if (validFrom == null) {
                throw new IllegalArgumentException("ValidFrom cannot be null");
            }
            if (validTo == null) {
                throw new IllegalArgumentException("ValidTo cannot be null");
            }
            if (usageThreshold == null) {
                throw new IllegalArgumentException("UsageThreshold cannot be null");
            }
            if (quantityPerUser == null) {
                throw new IllegalArgumentException("QuantityPerUser cannot be null");
            }
            Coupon coupon = new Coupon();
            coupon.name = name;
            coupon.description = description;
            coupon.type = type;
            coupon.amount = amount;
            coupon.validFrom = validFrom;
            coupon.validTo = validTo;
            coupon.usageThreshold = usageThreshold;
            coupon.maxDiscountAmount = maxDiscountAmount;
            coupon.applicableCategory = applicableCategory;
            coupon.applicableStore = applicableStore;
            coupon.totalQuantity = totalQuantity;
            coupon.quantityPerUser = quantityPerUser;
            coupon.isNewUserCoupon = isNewUserCoupon;
            coupon.createdAt = LocalDateTime.now();
            return coupon;
        }
    }
}