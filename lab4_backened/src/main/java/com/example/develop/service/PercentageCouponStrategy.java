package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

import java.math.BigDecimal;

public class PercentageCouponStrategy implements CouponStrategy {
    @Override
    public BigDecimal calculateDiscount(Order order, Coupon coupon) {
        // 计算订单金额的百分比折扣
        BigDecimal discount = order.getTotalAmount().multiply(new BigDecimal("1").subtract(coupon.getAmount().divide(BigDecimal.valueOf(100))));

        return discount;
    }
}
