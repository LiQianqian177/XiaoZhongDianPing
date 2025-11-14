package com.example.develop.service;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.CouponStatus;
import com.example.develop.entity.User;
import com.example.develop.entity.UserCoupon;
import com.example.develop.repository.CouponRepository;
import com.example.develop.repository.UserCouponRepository;
import com.example.develop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CouponClaimService {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void issueCouponToUser(Long userId, Long couponId) {
        // 获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 获取优惠券
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // 检查是不是领取新人优惠券
        // 检查用户是否为新用户
        if (isCouponNewUserCoupon(couponId) && !isNewUser(user)) {
            throw new RuntimeException("User is not a new user");
        }

        // 只能领取一次新人优惠券
        if (isCouponNewUserCoupon(couponId) && hasUserClaimedNewUserCoupon(userId)) {
            throw new RuntimeException("User has already claimed a new user coupon");
        }

        // 检查优惠券是否还有剩余
        if (coupon.getTotalQuantity() <= 0) {
            coupon.setFullyClaimed(true);
            couponRepository.save(coupon);
            throw new RuntimeException("Coupon is fully claimed");
        }

        // 检查用户是否已经领取过该优惠券
        long userCouponCount = userCouponRepository.countByUserAndCoupon(user, coupon);

        // 检查是否超过最大领取数量
        if (userCouponCount >= coupon.getQuantityPerUser()) {
            throw new RuntimeException("You have already claimed the maximum number of this coupon.");
        }

        // 创建用户优惠券关系
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCoupon.setClaimAt(LocalDateTime.now());
        userCouponRepository.save(userCoupon);

        // 更新优惠券的剩余数量
        coupon.setTotalQuantity(coupon.getTotalQuantity() - 1);
        couponRepository.save(coupon);
    }

    private boolean isNewUser(User user) {
        // 实现检查用户是否为新用户的逻辑
        return user.getOrders().isEmpty();
    }

    public boolean isCouponNewUserCoupon(Long couponId) {
        return couponRepository.findByIdAndIsNewUserCoupon(couponId, true).isPresent();
    }

    public boolean hasUserClaimedNewUserCoupon(Long userId) {
        return userCouponRepository.existsByUser_IdAndCoupon_IsNewUserCoupon(userId, true);
    }
}
