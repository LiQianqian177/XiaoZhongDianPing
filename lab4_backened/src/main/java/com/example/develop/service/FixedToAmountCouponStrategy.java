package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

import java.math.BigDecimal;

public class FixedToAmountCouponStrategy implements CouponStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, Coupon coupon) {
        // 减至固定金额优惠券，订单金额减至固定金额
        return order.getTotalAmount().subtract(coupon.getAmount());
    }
}