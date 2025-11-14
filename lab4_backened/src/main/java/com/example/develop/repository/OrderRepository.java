package com.example.develop.repository;

import com.example.develop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    Optional<Order> findById(Long userId);
    List<Order> findByUserId(Long userId);
    boolean existsByVoucherCode(String voucherCode);
}
