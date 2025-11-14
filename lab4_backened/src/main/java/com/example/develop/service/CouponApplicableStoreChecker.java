package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

public class CouponApplicableStoreChecker implements CouponChecker {
    @Override
    public void check(Order order, Coupon coupon) throws CouponCheckException {
        if (coupon.getApplicableStore() != null) {
            boolean applicable = order.getOrderItems().stream()
                    .anyMatch(item -> coupon.getApplicableStore().equals(item.getDish().getMerchant().getName()));
            if (!applicable) {
                throw new CouponCheckException("Coupon is not applicable to this store.");
            }
        }
    }
}