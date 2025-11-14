package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

public class CouponApplicableCategoryChecker implements CouponChecker {
    @Override
    public void check(Order order, Coupon coupon) throws CouponCheckException {
        if (coupon.getApplicableCategory() != null) {
            boolean applicable = order.getOrderItems().stream()
                    .anyMatch(item -> coupon.getApplicableCategory().equals(item.getDish().getCategory()));
            if (!applicable) {
                throw new CouponCheckException("Coupon is not applicable to this store.");
            }
        }
    }
}
