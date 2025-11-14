package com.example.develop.controller;

import com.example.develop.entity.Payment;
import com.example.develop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 创建支付单
     */
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Map<String, Object> paymentRequest) {
        try {
            Long orderId = Long.valueOf(paymentRequest.get("orderId").toString());
            String orderNumber = (String) paymentRequest.get("orderNumber");
            BigDecimal amount = new BigDecimal(paymentRequest.get("amount").toString());
            String currency = (String) paymentRequest.get("currency");
            String paymentMethod = (String) paymentRequest.get("paymentMethod");

            Payment payment = paymentService.createPayment(orderId, orderNumber, amount, currency, paymentMethod);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 获取支付单详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable("id") Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 根据支付单号获取支付单
     */
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<Payment> getPaymentByNumber(@PathVariable("paymentNumber") String paymentNumber) {
        Optional<Payment> payment = paymentService.getPaymentByPaymentNumber(paymentNumber);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 根据订单ID获取支付单列表
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderId(@PathVariable("orderId") Long orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    /**
     * 根据订单号获取支付单列表
     */
    @GetMapping("/orderNumber/{orderNumber}")
    public ResponseEntity<List<Payment>> getPaymentsByOrderNumber(@PathVariable("orderNumber") String orderNumber) {
        List<Payment> payments = paymentService.getPaymentsByOrderNumber(orderNumber);
        return ResponseEntity.ok(payments);
    }

    /**
     * 处理支付请求
     */
    @PostMapping("/{paymentNumber}/process")
    public ResponseEntity<Map<String, Object>> processPayment(@PathVariable("paymentNumber") String paymentNumber) {
        try {
            paymentService.processPayment(paymentNumber);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "支付处理中");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 完成支付
     */
    @PostMapping("/{paymentNumber}/complete")
    public ResponseEntity<Payment> completePayment(
            @PathVariable("paymentNumber") String paymentNumber,
            @RequestBody Map<String, String> request) {
        String transactionId = request.get("transactionId");
        try {
            Payment payment = paymentService.completePayment(paymentNumber, transactionId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 标记支付失败
     */
    @PostMapping("/{paymentNumber}/fail")
    public ResponseEntity<Payment> failPayment(
            @PathVariable("paymentNumber") String paymentNumber,
            @RequestBody Map<String, String> request) {
        String failReason = request.get("failReason");
        try {
            Payment payment = paymentService.failPayment(paymentNumber, failReason);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 取消支付
     */
    @PostMapping("/{paymentNumber}/cancel")
    public ResponseEntity<Payment> cancelPayment(@PathVariable("paymentNumber") String paymentNumber) {
        try {
            Payment payment = paymentService.cancelPayment(paymentNumber);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 根据支付状态查询支付单
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable("status") String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }
}