package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

public class CouponThresholdChecker implements CouponChecker {
    @Override
    public void check(Order order, Coupon coupon) throws CouponCheckException {
        if (order.getTotalAmount().compareTo(coupon.getUsageThreshold()) < 0) {
            throw new CouponCheckException("Order amount does not meet the threshold.");
        }
    }
}

/**
 * 应该不用检查优惠券是否已使用，因为用户只能选择到可以使用的
 */
