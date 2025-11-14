package com.example.develop.DTO;

import com.example.develop.entity.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest(
        Long userId, // 用户ID，可选 ,为空表示这张优惠券是通用的 ，不为空就是给某个人的
        String name, // 优惠券名称
        String description,
        CouponType type,
        /*
        * amount含义：
        * 固定金额优惠券：优惠金额
        * 折扣优惠券：折扣率
        * 减至固定金额：这个固定金额
        * */
        BigDecimal amount, //
        BigDecimal usageThreshold,// 使用门槛
        BigDecimal maxDiscountAmount,
        String applicableCategory, // 适用品类
        String applicableStore, // 适用店铺
        LocalDateTime validFrom,
        LocalDateTime validTo,
        Integer totalQuantity, // 发放总量
        Integer quantityPerUser, // 每个用户可领取的数量
        boolean isNewUserCoupon
) {
}
