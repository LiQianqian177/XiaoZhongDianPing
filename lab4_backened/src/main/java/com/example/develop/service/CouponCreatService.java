package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.DTO.CouponRequest;
import com.example.develop.entity.CouponStatus;
import com.example.develop.entity.User;
import com.example.develop.entity.UserCoupon;
import com.example.develop.repository.CouponRepository;
import com.example.develop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CouponCreatService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void generateCoupon(CouponRequest request) {
        // 创建优惠券模板
        Coupon coupon = new Coupon.Builder()
                .setName(request.name())
                .setDescription(request.description())
                .setType(request.type())
                .setAmount(request.amount())
                .setValidFrom(request.validFrom())
                .setValidTo(request.validTo())
                .setUsageThreshold(request.usageThreshold())
                .setMaxDiscountAmount(request.maxDiscountAmount())
                .setApplicableCategory(request.applicableCategory())
                .setApplicableStore(request.applicableStore())
                .setTotalQuantity(request.totalQuantity())
                .setQuantityPerUser(request.quantityPerUser())
                .setNewUserCoupon(request.isNewUserCoupon())
                .build();

        coupon = couponRepository.save(coupon);

        // 如果是用户专属优惠券，创建用户优惠券关系
        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUser(user);
            userCoupon.setCoupon(coupon);
            couponRepository.save(coupon);
        }
    }
}