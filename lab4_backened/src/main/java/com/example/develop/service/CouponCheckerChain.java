package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class CouponCheckerChain {
    // 创建一个校验器链，将多个校验器组合在一起。
    private List<CouponChecker> checkers = new ArrayList<>();

    public void addChecker(CouponChecker checker) {
        checkers.add(checker);
    }

    public void check(Order order, Coupon coupon) throws CouponCheckException {
        for (CouponChecker checker : checkers) {
            checker.check(order, coupon);
        }
    }
}
