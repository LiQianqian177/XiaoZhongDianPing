package com.example.develop.controller;

import com.example.develop.DTO.*;
import com.example.develop.entity.Coupon;
import com.example.develop.entity.Order;
import com.example.develop.entity.User;
import com.example.develop.entity.UserCoupon;
import com.example.develop.repository.UserCouponRepository;
import com.example.develop.repository.UserRepository;
import com.example.develop.service.CouponClaimService;
import com.example.develop.service.CouponCreatService;
import com.example.develop.service.CouponUseService;
import com.example.develop.service.OrderService;
import com.google.errorprone.annotations.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    @Autowired
    private CouponClaimService couponClaimService;

    @Autowired
    private CouponCreatService couponCreatService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/couponClaim")
    public ResponseEntity<String> claimCoupon(@RequestBody CouponClaimRequest request) {
        try {
            couponClaimService.issueCouponToUser(request.userId(), request.couponId());
            return ResponseEntity.ok("Coupon claimed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/couponCreat")
    public ResponseEntity<String> createCoupon(@RequestBody CouponRequest request) {
        try {
            couponCreatService.generateCoupon(request);
            return ResponseEntity.ok("Coupon created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/orderCreat")
    public ResponseEntity<String> orderCoupon(@RequestBody OrderRequest request) {
        try {
            orderService.createOrder(request.userId(), request.groupbuyID());
            return ResponseEntity.ok("Order created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/couponUse")
    public ResponseEntity<String> couponUse(@RequestBody CouponUseRequest request) {
        try {
            couponUseService.applyCouponToOrder(request.orderId(), request.userCouponId());
            BigDecimal discount = couponUseService.applyCouponToOrder(request.orderId(), request.userCouponId());
            return ResponseEntity.ok("Discount amount: " + discount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/userCoupon")
    public ResponseEntity<?> userCoupon(@RequestBody UserCouponRequest request) {
        try {
            User user = userRepository.findById(request.userId()).orElseThrow(() -> new Exception("User not found"));
            List<UserCoupon> userCoupons = userCouponRepository.findUserCouponByUser(user);
            if (userCoupons.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user coupons found for user with ID: " + request.userId());
            }
            // 使用 Stream 操作抽取 Coupon 对象，并封装到 UserCouponResponse 中
            List<UserCouponResponse> userCouponResponses = userCoupons.stream()
                    .map(userCoupon -> new UserCouponResponse(userCoupon.getId(), userCoupon.getCoupon(),userCoupon.getStatus()))
                    .collect(Collectors.toList());

            // 返回封装后的列表
            return ResponseEntity.ok(userCouponResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }
    }
}
