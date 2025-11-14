package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

import java.time.LocalDateTime;

public class CouponExpirationChecker implements CouponChecker {
    @Override
    public void check(Order order, Coupon coupon) throws CouponCheckException {
        if (!coupon.getValidFrom().isBefore(LocalDateTime.now()) || !coupon.getValidTo().isAfter(LocalDateTime.now())) {
            throw new CouponCheckException("Coupon is not valid.");
        }
    }
}
