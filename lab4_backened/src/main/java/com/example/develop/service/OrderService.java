package com.example.develop.service;

import com.example.develop.DTO.OrderItemRequest;
import com.example.develop.entity.*;
import com.example.develop.event.OrderPaidEvent;
import com.example.develop.exception.*;
import com.example.develop.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.example.develop.entity.OrderStatus.PAID;
import static com.example.develop.entity.OrderStatus.UNPAID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final GroupbuyRepository groupbuyRepository;
    private final MerchantRepository merchantRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher; // Spring 的事件发布器
    private final CouponRepository couponRepository; // 优惠券仓库
    private final UserCouponRepository userCouponRepository; // 用户优惠券仓库
    private final DishRepository dishRepository;
    private final CouponUseService couponUseService;

    Random random = new Random();
    private String generateRandom16DigitCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private String generateUniqueVoucherCode() {
        int maxAttempts = 5;
        for (int i = 0; i < maxAttempts; i++) {
            String code = generateRandom16DigitCode();
            if (!orderRepository.existsByVoucherCode(code)) {
                return code;
            }
        }
        throw new OrderProcessingException("Failed to generate unique voucher code after " + maxAttempts + " attempts");
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with ID " + orderId + " not found"));
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional(rollbackOn = Exception.class)
    public Order createOrder(
            Long userId,
            Long groupbuyId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        Groupbuy groupbuy = groupbuyRepository.findById(groupbuyId)
                .orElseThrow(() -> new NotFoundException("Groupbuy not found with ID: " + groupbuyId));

        Long merchantId = groupbuy.getMerchant().getId();
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new NotFoundException("Merchant not found with ID: " + merchantId));

        // 初始化订单对象
        Order order = Order.builder()
                .user(user)
                .groupbuy(groupbuy)
                .groupbuyTitle(groupbuy.getTitle())
                .storeName(merchant.getName())
                .merchant(merchant)
                .status(UNPAID)
                .totalAmount(groupbuy.getPrice())
                .build();

        return orderRepository.save(order);
    }



    public Order payOrder(Long orderId, BigDecimal discount, Long userCouponId) {
        // 查找订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new IllegalStateException("Order is already paid or not eligible for payment.");
        }

        // 更新订单状态和生成唯一券码
        String voucherCode = generateUniqueVoucherCode();
        order.setVoucherCode(voucherCode);
        order.setStatus(OrderStatus.PAID);

        // 记录下单时间和价格
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(order.getTotalAmount().subtract(discount));

        // 保存订单
        orderRepository.save(order);


        eventPublisher.publishEvent(new OrderPaidEvent(
                        this, order.getId(), order.getGroupbuy().getId(), discount, userCouponId
                ));


        return order;
    }


}