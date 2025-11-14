package com.example.develop.DTO;

import com.example.develop.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO 用于返回支付结果的详细信息
 */
@Data
public class PaymentResponse {
    private Long orderId;           // 订单ID
    private String voucherCode;     // 券码
    private String groupbuyTitle;   // 团购标题
    private String storeName;       // 使用门店
    private LocalDateTime orderTime; // 下单时间
    private BigDecimal totalPrice;  // 总金额
    private String status;          // 订单状态
    private Long couponCode;      // 优惠券码
    private String message;

    /**
     * 构造方法：包含订单和优惠券码的完整信息
     *
     * @param orderId     订单ID
     * @param voucherCode 券码
     * @param groupbuyTitle 团购标题
     * @param storeName   使用门店
     * @param orderTime   下单时间
     * @param totalPrice  总金额
     * @param status      订单状态
     * @param couponCode  优惠券码
     */
    public PaymentResponse(Long orderId, String voucherCode, String groupbuyTitle, String storeName,
                           LocalDateTime orderTime, BigDecimal totalPrice, String status, Long couponCode,String message) {
        this.orderId = orderId;
        this.voucherCode = voucherCode;
        this.groupbuyTitle = groupbuyTitle;
        this.storeName = storeName;
        this.orderTime = orderTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.couponCode = couponCode;
        this.message = message;
    }

    /**
     * 快捷创建方法：从订单实体创建支付响应
     *
     * @param order 订单实体
     * @return 支付响应对象
     */
    public static PaymentResponse fromEntity(Order order,String message) {
        return new PaymentResponse(
                order.getId(),
                order.getVoucherCode(),
                order.getGroupbuyTitle(),
                order.getStoreName(),
                order.getOrderDate(),
                order.getTotalAmount(),
                String.valueOf(order.getStatus()),
                order.getUserCouponId() // 从 Order 实体中提取 couponCode
                ,message
        );
    }

}