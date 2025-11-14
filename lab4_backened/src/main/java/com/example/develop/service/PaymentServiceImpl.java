package com.example.develop.service;

import com.example.develop.entity.Payment;
import com.example.develop.repository.PaymentRepository;
import com.example.develop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 支付服务实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Payment createPayment(Long orderId, String orderNumber, BigDecimal amount,
                                 String currency, String paymentMethod) {
        String paymentNumber = generatePaymentNumber();

        Payment payment = new Payment(paymentNumber, orderId, orderNumber, amount, currency, paymentMethod);
        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Optional<Payment> getPaymentByPaymentNumber(String paymentNumber) {
        return paymentRepository.findByPaymentNumber(paymentNumber);
    }

    @Override
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Payment> getPaymentsByOrderNumber(String orderNumber) {
        return paymentRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional
    public void processPayment(String paymentNumber) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNumber(paymentNumber);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.processing();
            paymentRepository.save(payment);
        } else {
            throw new RuntimeException("支付单不存在: " + paymentNumber);
        }
    }

    @Override
    @Transactional
    public Payment completePayment(String paymentNumber, String transactionId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNumber(paymentNumber);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.paymentSuccess(transactionId);
            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("支付单不存在: " + paymentNumber);
        }
    }

    @Override
    @Transactional
    public Payment failPayment(String paymentNumber, String failReason) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNumber(paymentNumber);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.paymentFailed(failReason);
            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("支付单不存在: " + paymentNumber);
        }
    }

    @Override
    @Transactional
    public Payment cancelPayment(String paymentNumber) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaymentNumber(paymentNumber);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.cancel();
            return paymentRepository.save(payment);
        } else {
            throw new RuntimeException("支付单不存在: " + paymentNumber);
        }
    }

    @Override
    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status);
    }

    @Override
    public String generatePaymentNumber() {
        // 生成格式：PAY + 年月日时分秒 + 4位随机数
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeStr = now.format(formatter);

        // 生成4位随机数
        int randomNum = 1000 + random.nextInt(9000); // 1000-9999之间的随机数

        return "PAY" + timeStr + randomNum;
    }
}