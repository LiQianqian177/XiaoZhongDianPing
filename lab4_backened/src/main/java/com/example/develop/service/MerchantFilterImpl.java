package com.example.develop.service;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.entity.Dish;
import com.example.develop.entity.Merchant;
import com.example.develop.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantFilterImpl implements MerchantFilter {
    private final DishRepository dishRepository;
    private final MerchantService merchantService;
    private final MerchantSorting merchantSorting;

    public MerchantFilterImpl(DishRepository dishRepository,
                              MerchantService merchantService,
                              MerchantSorting merchantSorting) {
        this.dishRepository = dishRepository;
        this.merchantService = merchantService;
        this.merchantSorting = merchantSorting;
    }

    public List<MerchantDTO> filterMerchants(MerchantFilterAndSortRequest requests) {

        // 对request一些为空的地方进行处理
        MerchantFilterAndSortRequest request = requests.withDefaults();
        // 获取商家
        List<Merchant> merchants = merchantSorting.convertToEntities(merchantService.searchMerchants(request.keywords()));

        // 根据评分筛选商家
        merchants = filterByRating(merchants, request.minRating(), request.maxRating());

        // 根据人均消费筛选商家
        merchants = filterBycostRange(merchants, request.minAvgCost(), request.maxAvgCost());

        // 根据菜品价格筛选商家
        merchants = filterByDishPrice(merchants, request.minPrice(), request.maxPrice());

        return merchantSorting.convertToDTOs(merchants);
    }

    // 依据评分筛选商家
    public List<Merchant> filterByRating(List<Merchant> merchants, double minRating, double maxRating) {
        return merchants.stream()
                .filter(m -> m.getRating() >= minRating && m.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

    // 根据人均消费筛选商家
    public List<Merchant> filterBycostRange(List<Merchant> merchants, BigDecimal minAvgcost, BigDecimal maxAvgcost) {
        // 使用Stream遍历并替换空值（直接修改原对象）
        List<Merchant> merchantsWithDefault = merchants.stream()
                .peek(m -> {
                    if (m.getAverageCost() == null) {
                        m.setAverageCost(BigDecimal.ZERO); // 替换空值为零
                    }
                })
                .collect(Collectors.toList());
        return merchantsWithDefault.stream()
                .filter(m -> m.getAverageCost().compareTo(minAvgcost) >= 0
                        && m.getAverageCost().compareTo(maxAvgcost) <= 0)
                .collect(Collectors.toList());
    }

    // 根据菜品价格筛选商家
    public List<Merchant> filterByDishPrice(List<Merchant> merchants, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Dish> dishes = dishRepository.findAll();
        // 筛选出菜品价格在指定区间内的商家
        return merchants.stream()
                .filter(merchant -> dishes.stream()
                        .anyMatch(dish -> dish.getMerchant().getId().equals(merchant.getId())
                                && dish.getPrice().compareTo(minPrice) >= 0
                                && dish.getPrice().compareTo(maxPrice) <= 0))
                .collect(Collectors.toList());
    }

}