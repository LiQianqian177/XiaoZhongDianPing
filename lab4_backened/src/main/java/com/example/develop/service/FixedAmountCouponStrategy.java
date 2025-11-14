package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

import java.math.BigDecimal;

public class FixedAmountCouponStrategy implements CouponStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, Coupon coupon) {
        return coupon.getAmount();
    }
}
