package com.example.develop.repository;

import com.example.develop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 支付数据访问层
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 根据支付单号查询支付信息
     * @param paymentNumber 支付单号
     * @return 支付信息
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    /**
     * 根据订单ID查询支付信息列表
     * @param orderId 订单ID
     * @return 支付信息列表
     */
    List<Payment> findByOrderId(Long orderId);

    /**
     * 根据订单号查询支付信息列表
     * @param orderNumber 订单号
     * @return 支付信息列表
     */
    List<Payment> findByOrderNumber(String orderNumber);

    /**
     * 根据支付状态查询支付信息列表
     * @param paymentStatus 支付状态
     * @return 支付信息列表
     */
    List<Payment> findByPaymentStatus(String paymentStatus);

    /**
     * 根据支付方式查询支付信息列表
     * @param paymentMethod 支付方式
     * @return 支付信息列表
     */
    List<Payment> findByPaymentMethod(String paymentMethod);

    /**
     * 根据第三方交易号查询支付信息
     * @param transactionId 第三方交易号
     * @return 支付信息
     */
    Optional<Payment> findByTransactionId(String transactionId);
}