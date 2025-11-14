package com.example.develop.repository;

import com.example.develop.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    //List<Coupon> findAllByStatus(CouponStatus status);
    long countByUserAndCoupon(User user, Coupon coupon);

    Optional<UserCoupon> findByUserId(Long userId);
    boolean existsByUser_IdAndCoupon_IsNewUserCoupon(Long userId, boolean isNewUserCoupon);

    List<UserCoupon> findUserCouponByUser(User user);
}
