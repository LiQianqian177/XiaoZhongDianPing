package com.example.develop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_coupons")
@Getter
@Setter
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Getter
    @Setter
    @Column(nullable = false)
    private LocalDateTime claimAt;

    @Getter
    @Setter
    @Column(nullable = true)
    @JoinColumn(name = "used_at")
    private LocalDateTime usedAt;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status; //优惠券状态

    public UserCoupon() {
        this.usedAt = LocalDateTime.now();
        this.status = CouponStatus.UNUSED; // 设置默认值
    }

}
