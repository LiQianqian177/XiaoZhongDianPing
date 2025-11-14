package com.example.develop.DTO;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.CouponStatus;

public record UserCouponResponse(
        Long userCouponId,
        Coupon coupon,
        CouponStatus status
) {
}
