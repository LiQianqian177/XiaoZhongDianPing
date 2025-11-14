package com.example.develop.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Data
public class PaymentRequest {
    private Long orderId;
    private BigDecimal discount;
    private Long userCouponId;
}
