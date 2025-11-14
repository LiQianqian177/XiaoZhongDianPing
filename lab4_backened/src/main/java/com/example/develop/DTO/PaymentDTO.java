package com.example.develop.dto;

import java.math.BigDecimal;

/**
 * 支付请求数据传输对象
 */
public class PaymentDTO {

    private Long orderId;
    private String orderNumber;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;

    // 默认构造函数
    public PaymentDTO() {
    }

    // 带参数的构造函数
    public PaymentDTO(Long orderId, String orderNumber, BigDecimal amount, String currency, String paymentMethod) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
    }

    // Getter 和 Setter
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}