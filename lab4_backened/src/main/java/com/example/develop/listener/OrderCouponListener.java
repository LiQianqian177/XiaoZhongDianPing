package com.example.develop.listener;

import com.example.develop.entity.CouponStatus;
import com.example.develop.entity.Order;
import com.example.develop.entity.UserCoupon;
import com.example.develop.event.OrderPaidEvent;
import com.example.develop.repository.OrderRepository;
import com.example.develop.repository.UserCouponRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderCouponListener {

    private final OrderRepository orderRepository;
    private final UserCouponRepository userCouponRepository;

    public OrderCouponListener(OrderRepository orderRepository, UserCouponRepository userCouponRepository) {
        this.orderRepository = orderRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @EventListener
    @Transactional(rollbackOn = Exception.class)
    public void handleOrderPaidEvent(OrderPaidEvent event) {
        // 更新优惠券状态
        if (event.getDiscount().compareTo(BigDecimal.ZERO) > 0 && event.getUserCouponId() != null) {
            UserCoupon userCoupon = userCouponRepository.findById(event.getUserCouponId())
                    .orElseThrow(() -> new RuntimeException("UserCoupon not found with ID: " + event.getUserCouponId()));
            userCoupon.setStatus(CouponStatus.USED);
            userCouponRepository.save(userCoupon);

            // 更新订单的 userCoupon 字段
            Order order = orderRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + event.getOrderId()));
            order.setUserCouponId(event.getUserCouponId());
            orderRepository.save(order);
        }
    }
}