package com.example.develop.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "groupbuy_id", nullable = false)
    private Groupbuy groupbuy;

    //团购套餐标题
    @Column(name = "groupbuy_title", nullable = false)
    private String groupbuyTitle;


    //适用门店
    @Column(name = "store_name", nullable = false)
    private String storeName;

    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    //16位券码
    @Column(name = "voucher_code", nullable = true , unique = true, length = 16)
    private String voucherCode;

    //优惠券码
    @Column(name = "coupon_id", nullable = true)
    private Long userCouponId;

    //订单状态
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = true)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private BigDecimal totalAmount; // 应该为减去优惠券金额后

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

}