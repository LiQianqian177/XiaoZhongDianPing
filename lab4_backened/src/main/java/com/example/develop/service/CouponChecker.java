package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

public interface CouponChecker {
    void check(Order order, Coupon coupon) throws CouponCheckException;
}
