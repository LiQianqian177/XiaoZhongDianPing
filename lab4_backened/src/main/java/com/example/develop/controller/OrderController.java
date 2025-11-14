package com.example.develop.controller;

import com.example.develop.DTO.*;
import com.example.develop.entity.Order;
import com.example.develop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单相关接口控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单接口
     *
     * @param createOrderRequest 包含用户ID、团购套餐ID、商家ID，以及可选的优惠券ID
     * @return 返回订单详情，包括订单ID、券码、团购标题、门店名称、订单时间和金额
     */
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@Valid  @RequestBody CreateOrderRequest createOrderRequest) {
        // 调用服务层创建订单
        var order = orderService.createOrder(
                createOrderRequest.getUserId(),
                createOrderRequest.getGroupbuyId()
        );

        // 返回订单信息
        return ResponseEntity.ok(OrderResponse.fromEntity(order,"订单创建成功"));
    }

    /**
     * 查询用户的所有订单接口
     *
     * @param userId 用户ID
     * @return 返回用户的所有订单列表
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderListItemResponse>> getMyOrders(@RequestParam Long userId) {
        // 调用服务层获取用户的所有订单
        var orders = orderService.findByUserId(userId);

        // 转换为响应对象并返回
        var response = orders.stream()
                .map(OrderListItemResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * 查询订单券码详情接口
     *
             * @param orderId 订单ID
     * @return 返回券码详情
     */
    @GetMapping("/{orderId}/details")
    public ResponseEntity<PaymentResponse> getOrderDetails(@PathVariable Long orderId) {
        // 调用服务层获取订单详情
        var order = orderService.findById(orderId);

        // 返回订单详情
        return ResponseEntity.ok(PaymentResponse.fromEntity(order,"返回订单详情成功"));
    }

    /**
     * 订单支付接口
     *
     * @param paymentRequest 支付请求（包含订单ID、折扣金额和用户优惠券ID）
     * @return 返回支付结果
     */
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> payOrder(@RequestBody PaymentRequest paymentRequest) {
        // 调用服务层处理支付逻辑
        var order = orderService.payOrder(paymentRequest.getOrderId(),
                paymentRequest.getDiscount(),
                paymentRequest.getUserCouponId());

        // 返回支付后的订单详情
        return ResponseEntity.ok(PaymentResponse.fromEntity(order,"订单支付成功"));
    }


    }