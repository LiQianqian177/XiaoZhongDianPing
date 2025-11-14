package com.example.develop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 支付实体类
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                     // 支付ID

    @Column(nullable = false, unique = true)
    private String paymentNumber;        // 支付单号

    @Column(nullable = false)
    private Long orderId;                // 关联订单ID

    @Column(nullable = false)
    private String orderNumber;          // 关联订单号

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;           // 支付金额

    @Column(length = 3, nullable = false)
    private String currency;             // 货币类型 (CNY, USD等)

    @Column(length = 20, nullable = false)
    private String paymentMethod;        // 支付方式 (支付宝, 微信, 信用卡等)

    @Column(length = 20, nullable = false)
    private String paymentStatus;        // 支付状态 (待支付, 支付中, 支付成功, 支付失败等)

    @Column(length = 64)
    private String transactionId;        // 第三方支付平台交易号

    @Column
    private LocalDateTime paymentTime;   // 支付时间

    @Column(nullable = false)
    private LocalDateTime createTime;    // 创建时间

    @Column(nullable = false)
    private LocalDateTime updateTime;    // 更新时间

    @Column(length = 255)
    private String remark;               // 备注信息

    // 默认构造函数
    public Payment() {
    }

    // 带参数的构造函数
    public Payment(String paymentNumber, Long orderId, String orderNumber,
                   BigDecimal amount, String currency, String paymentMethod) {
        this.paymentNumber = paymentNumber;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "PENDING"; // 默认为待支付状态
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.updateTime = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // 业务逻辑方法

    /**
     * 支付处理中
     */
    public void processing() {
        this.paymentStatus = "PROCESSING";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 支付成功
     * @param transactionId 第三方支付平台交易号
     */
    public void paymentSuccess(String transactionId) {
        this.paymentStatus = "SUCCESS";
        this.transactionId = transactionId;
        this.paymentTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 支付失败
     * @param remark 失败原因
     */
    public void paymentFailed(String remark) {
        this.paymentStatus = "FAILED";
        this.remark = remark;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 取消支付
     */
    public void cancel() {
        this.paymentStatus = "CANCELLED";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 检查支付是否成功
     * @return 是否支付成功
     */
    public boolean isPaymentSuccessful() {
        return "SUCCESS".equals(this.paymentStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) &&
                Objects.equals(paymentNumber, payment.paymentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentNumber);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentNumber='" + paymentNumber + '\'' +
                ", orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentTime=" + paymentTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}