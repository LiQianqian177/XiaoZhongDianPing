package com.example.develop.DTO;

import java.util.List;
import java.util.Optional;

public record OrderRequest(
        Long userId, // 用户ID
        Long groupbuyID
        /*Optional<Long> couponId,
        List<OrderItemRequest> items // 订单项列表，下次的购物车可以用*/
) {
}
