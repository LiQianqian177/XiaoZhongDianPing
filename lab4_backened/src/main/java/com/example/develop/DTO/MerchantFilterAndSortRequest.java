package com.example.develop.DTO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public record MerchantFilterAndSortRequest(
        List<String> keywords,
        Double minRating,
        Double maxRating,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minAvgCost,
        BigDecimal maxAvgCost,
        boolean isSortByOverall,
        boolean isSortByRating,
        boolean isSortByPrice,
        boolean isSortByDishPrice
) {
    public MerchantFilterAndSortRequest withDefaults() {
        List<String> k = keywords != null ? keywords : Collections.emptyList();
        Double minR = minRating != null ? minRating : 0.0;
        Double maxR = maxRating != null ? maxRating : 5.0;
        BigDecimal minP = minPrice != null ? minPrice : BigDecimal.ZERO;
        BigDecimal maxP = maxPrice != null ? maxPrice : new BigDecimal("1000");
        BigDecimal minC = minAvgCost != null ? minAvgCost : BigDecimal.ZERO;
        BigDecimal maxC = maxAvgCost != null ? maxAvgCost : new BigDecimal("1000");

        return new MerchantFilterAndSortRequest(
                k, minR, maxR, minP, maxP, minC, maxC,
                isSortByOverall, isSortByRating, isSortByPrice, isSortByDishPrice
        );
    }
}
