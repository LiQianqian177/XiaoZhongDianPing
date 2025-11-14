package com.example.develop.DTO;

public record CouponUseRequest(
        Long userCouponId,
        Long orderId
) {
}
