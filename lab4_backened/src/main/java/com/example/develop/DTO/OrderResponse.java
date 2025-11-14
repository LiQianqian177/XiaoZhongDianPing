package com.example.develop.DTO;

import com.example.develop.entity.Order;
import lombok.Data;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO 用于返回订单的详细信息
 */
@Data
@Setter
public class OrderResponse {
    private Long orderId;         // 订单ID
    private String groupbuyTitle; // 团购标题
    private String storeName;     // 使用门店
    private BigDecimal totalPrice;    // 总金额
    private String status;        // 订单状态

    /**
     * 将订单实体转换为响应对象
     *
     * @param order 订单实体
     * @return 转换后的响应对象
     */
    public static OrderResponse fromEntity(Order order) {
        var response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setGroupbuyTitle(order.getGroupbuyTitle());
        response.setStoreName(order.getStoreName());
        response.setTotalPrice(order.getTotalAmount());
        response.setStatus(String.valueOf(order.getStatus()));
        return response;
    }

    public static OrderResponse fromEntity(Order order, String message) {
        var response = fromEntity(order); // 复用已有方法
        response.setMessage(message);     // 设置传入的 message
        return response;
    }

    private String message;
    public static OrderResponse fromError(String message) {
        OrderResponse response = new OrderResponse();
        response.message = message; // 直接赋值
        return response;
    }
}