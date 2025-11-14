package com.example.develop.service;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.entity.Dish;
import com.example.develop.entity.Merchant;
import com.example.develop.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantSortingImpl implements MerchantSorting {
    private final DishRepository dishRepository;
    public MerchantSortingImpl(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<MerchantDTO> sortMerchant(List<MerchantDTO> merchant,MerchantFilterAndSortRequest sortingRequest) {
        List<Merchant> merchants = convertToEntities(merchant);
        // 根据评分排序(从高到低)
        if (sortingRequest.isSortByRating()) {
            merchants = sortByRating(merchants);
        }

        // 根据人均消费排序(从低到高)
        if (sortingRequest.isSortByPrice()) {
            System.out.println("Is sort by price!");
            merchants = sortByPrice(merchants);
        }

        // 根据菜品价格排序
        if (sortingRequest.isSortByDishPrice()) {
            merchants = sortByDishPrice(merchants);
        }

        // 综合排序
        if (sortingRequest.isSortByOverall()) {
            merchants = sortByOverall(merchants);
        }

        return convertToDTOs(merchants);
    }

    // 根据评分排序
    public List<Merchant> sortByRating(List<Merchant> merchants) {
        merchants.sort(Comparator.comparingDouble(Merchant::getRating).reversed());
        return merchants;
    }

    // 根据人均消费排序
    public List<Merchant> sortByPrice(List<Merchant> merchants) {
        // 使用Stream遍历并替换空值（直接修改原对象）
        List<Merchant> merchantsWithDefault = merchants.stream()
                .peek(m -> {
                    if (m.getAverageCost() == null) {
                        m.setAverageCost(BigDecimal.ZERO); // 替换空值为零
                    }
                })
                .collect(Collectors.toList());

        merchantsWithDefault.sort(Comparator.comparing(Merchant::getAverageCost));
        return merchantsWithDefault;
    }

    // 根据菜品价格排序
    public List<Merchant> sortByDishPrice(List<Merchant> merchants) {
        // 获取所有商家的菜品
        List<Dish> dishes = dishRepository.findAll();

        // 按商家分组，计算每个商家的最低菜品价格
        return merchants.stream()
                .sorted(Comparator.comparing(merchant ->
                        dishes.stream()
                                .filter(dish -> dish.getMerchant().getId().equals(merchant.getId()))
                                .map(Dish::getPrice)
                                .min(BigDecimal::compareTo)
                                .orElse(BigDecimal.valueOf(Double.MAX_VALUE))))
                .collect(Collectors.toList());
    }

    // 根据综合排序
    public List<Merchant> sortByOverall(List<Merchant> merchants) {
        // 综合排序：先按评分降序，再按人均消费升序，再按菜品价格升序
        merchants.sort(Comparator
                .comparingDouble(Merchant::getRating).reversed()
                .thenComparing(Merchant::getAverageCost)
                .thenComparing(Merchant::getMinPrice));
        return merchants;
    }

    // 将单个 MerchantDTO 转换为 Merchant
    public Merchant convertToEntity(MerchantDTO merchantDTO) {
        Merchant merchant = new Merchant();
        merchant.setId(merchantDTO.getId());
        merchant.setName(merchantDTO.getName());
        merchant.setDescription(merchantDTO.getDescription());
        merchant.setAddress(merchantDTO.getAddress());
        merchant.setPhone(merchantDTO.getPhone());
        merchant.setRating(merchantDTO.getRating());
        merchant.setAverageCost(merchantDTO.getAvgCost());
        merchant.setIsOpen(merchantDTO.getIsOpen());
        merchant.setBusinessHours(merchantDTO.getBusinessHours());
        // 如果需要，可以在这里填充 images 和 dishes
        return merchant;
    }

    // 将 List<MerchantDTO> 转换为 List<Merchant>
    public List<Merchant> convertToEntities(List<MerchantDTO> merchantDTOs) {
        return merchantDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    // 将单个 Merchant 转换为 MerchantDTO
    public MerchantDTO convertToDTO(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setDescription(merchant.getDescription());
        dto.setAddress(merchant.getAddress());
        dto.setPhone(merchant.getPhone());
        dto.setRating(merchant.getRating());
        dto.setAvgCost(merchant.getAverageCost());
        dto.setIsOpen(merchant.getIsOpen());
        dto.setBusinessHours(merchant.getBusinessHours());
        // 如果需要，可以在这里填充 images 和 dishes
        return dto;
    }

    // 将 List<Merchant> 转换为 List<MerchantDTO>
    public List<MerchantDTO> convertToDTOs(List<Merchant> merchants) {
        return merchants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
