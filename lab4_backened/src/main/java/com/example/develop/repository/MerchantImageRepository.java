package com.example.develop.repository;

import com.example.develop.entity.MerchantImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
// src/main/java/com/example/develop/repository/MerchantImageRepository.java
public interface MerchantImageRepository extends JpaRepository<MerchantImage, Long> {

    List<MerchantImage> findByMerchantId(Long merchantId);

    @Query("SELECT mi FROM MerchantImage mi WHERE mi.merchant.id = :merchantId AND mi.type = :type")
    List<MerchantImage> findByMerchantAndType(@Param("merchantId") Long merchantId,
                                              @Param("type") String type);
}
