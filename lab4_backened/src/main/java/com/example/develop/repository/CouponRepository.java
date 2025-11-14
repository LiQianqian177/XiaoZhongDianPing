package com.example.develop.repository;

import com.example.develop.entity.Coupon;
import com.example.develop.entity.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByIdAndIsNewUserCoupon(Long couponId, boolean isNewUserCoupon);

    boolean existsByIsNewUserCouponTrue();
}