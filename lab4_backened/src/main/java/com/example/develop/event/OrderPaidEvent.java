package com.example.develop.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class OrderPaidEvent extends ApplicationEvent {

    private final Long orderId;
    private final Long groupbuyId;
    private final BigDecimal discount;
    private final Long userCouponId;

    public OrderPaidEvent(Object source, Long orderId, Long groupbuyId, BigDecimal discount, Long userCouponId) {
        super(source);
        this.orderId = orderId;
        this.groupbuyId = groupbuyId;
        this.discount = discount;
        this.userCouponId = userCouponId;
    }
}