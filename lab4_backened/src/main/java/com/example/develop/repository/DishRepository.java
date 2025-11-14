package com.example.develop.repository;
import com.example.develop.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    // 根据商家ID查询菜品
    List<Dish> findByMerchantId(Long merchantId);

    // 根据分类查询菜品
    List<Dish> findByCategory(String category);

    // 根据价格范围查询菜品
    List<Dish> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);

    // 查询可用的菜品
    List<Dish> findByIsAvailableTrue();
}
