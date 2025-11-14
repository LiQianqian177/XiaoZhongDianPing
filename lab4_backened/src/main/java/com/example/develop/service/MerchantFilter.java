package com.example.develop.service;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.entity.Merchant;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public interface MerchantFilter {
    public List<Merchant> filterByRating(List<Merchant> merchants, double minRating, double maxRating);
    public List<Merchant> filterBycostRange(List<Merchant> merchants, BigDecimal minAvgcost, BigDecimal maxAvgcost);
    public List<MerchantDTO> filterMerchants(MerchantFilterAndSortRequest request);
    public List<Merchant> filterByDishPrice(List<Merchant> merchants, BigDecimal minPrice, BigDecimal maxPrice);

}
