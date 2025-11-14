package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

public class CouponApplicableCategoryToGroupbuyChecker implements CouponChecker{
    @Override
    public void check(Order order, Coupon coupon) throws CouponCheckException {
        if (coupon.getApplicableCategory() != null) {
            boolean applicable = order.getGroupbuy().getContent().contains(coupon.getApplicableCategory());
            boolean applicable1 = order.getMerchant().getName().contains(coupon.getApplicableCategory());
            if (!(applicable || applicable1)) {
                throw new CouponCheckException("Coupon is not applicable to this Category.");
            }
        }
    }
}
