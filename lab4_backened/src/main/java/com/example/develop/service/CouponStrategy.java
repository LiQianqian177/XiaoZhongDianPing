package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

import java.math.BigDecimal;

public interface CouponStrategy {
    BigDecimal calculateDiscount(Order order, Coupon coupon);
}
