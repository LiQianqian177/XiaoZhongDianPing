package com.example.develop.repository;

import com.example.develop.entity.Groupbuy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupbuyRepository extends JpaRepository<Groupbuy, Long> {
    // 根据商家ID查询该商家所有的团购套餐
    List<Groupbuy> findByMerchantId(Long merchantId);
}