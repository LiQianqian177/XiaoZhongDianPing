package com.example.develop.service;

import com.example.develop.entity.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付单
     * @param orderId 订单ID
     * @param orderNumber 订单号
     * @param amount 金额
     * @param currency 货币类型
     * @param paymentMethod 支付方式
     * @return 创建的支付单
     */
    Payment createPayment(Long orderId, String orderNumber, BigDecimal amount,
                          String currency, String paymentMethod);

    /**
     * 根据ID查询支付单
     * @param id 支付单ID
     * @return 支付单信息
     */
    Optional<Payment> getPaymentById(Long id);

    /**
     * 根据支付单号查询支付单
     * @param paymentNumber 支付单号
     * @return 支付单信息
     */
    Optional<Payment> getPaymentByPaymentNumber(String paymentNumber);

    /**
     * 根据订单ID查询支付单列表
     * @param orderId 订单ID
     * @return 支付单列表
     */
    List<Payment> getPaymentsByOrderId(Long orderId);

    /**
     * 根据订单号查询支付单列表
     * @param orderNumber 订单号
     * @return 支付单列表
     */
    List<Payment> getPaymentsByOrderNumber(String orderNumber);

    /**
     * 处理支付
     * @param paymentNumber 支付单号
     */
    void processPayment(String paymentNumber);

    /**
     * 完成支付
     * @param paymentNumber 支付单号
     * @param transactionId 第三方交易号
     * @return 更新后的支付单
     */
    Payment completePayment(String paymentNumber, String transactionId);

    /**
     * 支付失败处理
     * @param paymentNumber 支付单号
     * @param failReason 失败原因
     * @return 更新后的支付单
     */
    Payment failPayment(String paymentNumber, String failReason);

    /**
     * 取消支付
     * @param paymentNumber 支付单号
     * @return 更新后的支付单
     */
    Payment cancelPayment(String paymentNumber);

    /**
     * 根据状态查询支付单列表
     * @param status 支付状态
     * @return 支付单列表
     */
    List<Payment> getPaymentsByStatus(String status);

    /**
     * 生成唯一支付单号
     * @return 支付单号
     */
    String generatePaymentNumber();
}