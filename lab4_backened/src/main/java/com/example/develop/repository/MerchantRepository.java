package com.example.develop.repository;

import com.example.develop.entity.Merchant;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long>, JpaSpecificationExecutor<Merchant> {
    // 基本查询方法
    Optional<Merchant> findByName(String name);

    List<Merchant> findByRatingGreaterThanEqual(Double rating);

    // 添加更多实用的查询方法
    List<Merchant> findByIsOpenTrue();

    List<Merchant> findByAddressContaining(String addressKeyword);

    // 价格范围查询
    List<Merchant> findByAverageCostBetween(BigDecimal minCost, BigDecimal maxCost);

    // 组合条件查询
    List<Merchant> findByIsOpenTrueAndRatingGreaterThanEqual(Double rating);

    // 分页查询
    Page<Merchant> findByRatingGreaterThanEqual(Double rating, Pageable pageable);

    // 使用JPQL的自定义查询
    @Query("SELECT m FROM Merchant m WHERE m.rating >= :rating AND m.isOpen = true ORDER BY m.rating DESC")
    List<Merchant> findTopRatedOpenMerchants(@Param("rating") Double rating);

    // 使用原生SQL的查询示例
    @Query(value = "SELECT * FROM merchant WHERE business_hours LIKE %:pattern%", nativeQuery = true)
    List<Merchant> findByBusinessHoursPattern(@Param("pattern") String pattern);

    // 计数查询
    long countByIsOpenTrue();

    // 存在性查询
    boolean existsByName(String name);

    // 删除查询
    void deleteByRatingLessThan(Double rating);

    // 查询带有菜品的商家
    @Query("SELECT DISTINCT m FROM Merchant m JOIN m.dishes d")
    List<Merchant> findMerchantsWithDishes();

    // 查询带有图片的商家
    @Query("SELECT DISTINCT m FROM Merchant m JOIN m.images i")
    List<Merchant> findMerchantsWithImages();

    // 按平均消费排序
    List<Merchant> findByIsOpenTrueOrderByAverageCostAsc();

    List<Merchant> findByIsOpenTrueOrderByAverageCostDesc();

    // 按创建时间查询最新商家
    List<Merchant> findTop10ByOrderByCreatedAtDesc();
    default Page<Merchant> filterAndSortWithImages(Specification<Merchant> spec, Pageable pageable) {
        // 合并原有 Specification 和 JOIN FETCH 逻辑
        Specification<Merchant> fetchSpec = (root, query, cb) -> {
            root.fetch("images", JoinType.LEFT); // 强制加载图片
            query.distinct(true); // 去重
            return spec.toPredicate(root, query, cb);
        };
        return findAll(fetchSpec, pageable);
    }


}

