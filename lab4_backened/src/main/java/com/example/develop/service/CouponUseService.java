package com.example.develop.service;

import com.example.develop.entity.*;
import com.example.develop.repository.CouponRepository;
import com.example.develop.repository.OrderRepository;
import com.example.develop.repository.UserCouponRepository;
import com.example.develop.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CouponUseService {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final Map<CouponType, CouponStrategy> strategyMap;

    @Autowired
    private UserCouponRepository userCouponRepository;

    public CouponUseService() {
        strategyMap = new HashMap<>();
        strategyMap.put(CouponType.FIXED_AMOUNT, new FixedAmountCouponStrategy());
        strategyMap.put(CouponType.DISCOUNT, new PercentageCouponStrategy());
        strategyMap.put(CouponType.REDUCE_TO_FIXED_AMOUNT, new FixedToAmountCouponStrategy());
    }

    @Transactional
    public BigDecimal applyCouponToOrder(Long orderId, Long userCouponId) throws CouponCheckException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (userCouponId == null) {
            return BigDecimal.ZERO;
        }

        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // 创建校验器链
        CouponCheckerChain checkerChain = new CouponCheckerChain();
        checkerChain.addChecker(new CouponExpirationChecker()); // 过期
        checkerChain.addChecker(new CouponThresholdChecker()); // 门槛
        checkerChain.addChecker(new CouponApplicableCategoryToGroupbuyChecker()); // 指定商品
        checkerChain.addChecker(new CouponApplicableStoreToGroupbuyChecker()); // 指定店铺

        // 执行校验
        checkerChain.check(order, userCoupon.getCoupon());

        if (userCoupon.getStatus() == CouponStatus.USED) {
            throw new RuntimeException("Coupon has already been used");
        }

        // 如果校验通过，计算折扣并更新订单
        CouponStrategy strategy = strategyMap.get(userCoupon.getCoupon().getType());
        if (strategy == null) {
            throw new RuntimeException("Invalid coupon type.");
        }

        BigDecimal discount = strategy.calculateDiscount(order, userCoupon.getCoupon());
        if (userCoupon.getCoupon().getMaxDiscountAmount() != null) {
            discount = discount.min(userCoupon.getCoupon().getMaxDiscountAmount());
        }
        if (discount.compareTo(order.getTotalAmount()) > 0){
            discount = order.getTotalAmount();
        }

        discount = discount.min(order.getTotalAmount());

        // order.setTotalAmount(order.getTotalAmount().subtract(discount));
        // orderRepository.save(order);

        // 更新优惠券状态
        //userCoupon.setStatus(CouponStatus.USED);

        return discount;
    }

    public void CouponStatusChange (Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByUserId(userCouponId)
                .orElseThrow(() -> new RuntimeException("UserCoupon not found"));

        userCoupon.setStatus(CouponStatus.USED);

    }

    public void CheckCouponStatus (Order order) {
        UserCoupon userCoupon = userCouponRepository.findByUserId(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("UserCoupon not found"));

        if (userCoupon.getStatus() == CouponStatus.USED) {
            throw new RuntimeException("Coupon has already been used");
        }
    }
}