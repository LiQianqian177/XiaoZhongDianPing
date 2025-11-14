package com.example.develop.DTO;

import com.example.develop.entity.Order;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO 用于返回订单列表中的基本信息
 */
@Data
public class OrderListItemResponse {
    private Long orderId;         // 订单ID
    private String groupbuyTitle; // 团购标题
    private Long groubuyId;
    private LocalDateTime orderTime; // 下单时间
    private String storeName;     // 使用门店
    private Long storeId;
    private String status;

    /**
     * 将订单实体转换为响应对象
     * @param order 订单实体
     * @return 转换后的响应对象
     */
    public static OrderListItemResponse fromEntity(Order order) {
        var response = new OrderListItemResponse();
        response.setOrderId(order.getId());
        response.setGroupbuyTitle(order.getGroupbuyTitle());
        response.setGroubuyId(order.getGroupbuy().getId());
        response.setOrderTime(order.getOrderDate());
        response.setStoreName(order.getStoreName());
        response.setStoreId(order.getMerchant().getId());
        response.setStatus(order.getStatus().toString());
        return response;
    }
}