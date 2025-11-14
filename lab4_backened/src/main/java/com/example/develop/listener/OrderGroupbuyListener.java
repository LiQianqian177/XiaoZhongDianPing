package com.example.develop.listener;

import com.example.develop.entity.Groupbuy;
import com.example.develop.event.OrderPaidEvent;
import com.example.develop.repository.GroupbuyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderGroupbuyListener {

    private final GroupbuyRepository groupbuyRepository;

    @EventListener
    public void onOrderPaid(OrderPaidEvent event) {
        Long groupbuyId = event.getGroupbuyId();
        if(groupbuyRepository.existsById(groupbuyId)) {
            Groupbuy groupbuy = groupbuyRepository.findById(groupbuyId).get();
            Integer sales = groupbuy.getSales();
            sales += 1;
            groupbuy.setSales(sales);
        }
    }
}