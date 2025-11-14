package com.example.develop.service;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.entity.Dish;
import com.example.develop.entity.Merchant;
import com.example.develop.repository.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MerchantFilterTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private MerchantService merchantService;

    @InjectMocks
    private MerchantFilterImpl merchantFilter;

    private Merchant merchant1, merchant2, merchant3;
    private Dish dish1, dish2, dish3;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        merchant1 = new Merchant();
        merchant1.setId(1L);

        merchant2 = new Merchant();
        merchant2.setId(2L);

        merchant3 = new Merchant();
        merchant3.setId(3L);

        dish1 = new Dish();
        dish1.setMerchant(merchant1);
        dish1.setPrice(BigDecimal.valueOf(50));

        dish2 = new Dish();
        dish2.setMerchant(merchant2);
        dish2.setPrice(BigDecimal.valueOf(300));

        dish3 = new Dish();
        dish3.setMerchant(merchant3);
        dish3.setPrice(BigDecimal.valueOf(180));
    }




    // 对MerchantFilter.filterBycostRange()方法进行单元测试



    // 1. 未使用任何过滤
    @Test
    void testNoFilter() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
            List.of("测试"), null, null,
            null, null, null, null,
            false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
            List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                    4.5, new BigDecimal(100), true, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(1, result.size());
    }


    // 2. 只使用评分过滤
    @Test
    void testRatingFilter() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("测试"), 3.0, 4.0,
                null, null, null, null,
                false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
                List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(100), true, "8:00-22:00", null, null),
                        new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                        3.2, new BigDecimal(250), false, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1, dish2));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }


    // 3. 只使用人均消费过滤
    @Test
    void testAvgCostFilter() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("测试"), null, null,
                null, null, new BigDecimal(100), new BigDecimal(300),
                false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
                List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(100), true, "8:00-22:00", null, null),
                        new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                        3.2, new BigDecimal(250), false, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1, dish2));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(2, result.size());
    }

    // 4. 只使用菜品价格过滤
    @Test
    void testDishPriceFilter() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("测试"), null, null,
                new BigDecimal(100), new BigDecimal(200), null, null,
                false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
                List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(100), true, "8:00-22:00", null, null),
                        new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                        3.2, new BigDecimal(250), false, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1, dish2));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(0, result.size());
    }


    // 5. 使用所有过滤
    @Test
    void testAllFilters() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("测试"), 3.0, 5.0,
                new BigDecimal(100), new BigDecimal(400), new BigDecimal(120), new BigDecimal(250),
                false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
            List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                    4.5, new BigDecimal(100), true, "8:00-22:00", null, null),
                    new MerchantDTO(2L, "商家2", "描述2", "地址2", "17700000000",
                    3.2, new BigDecimal(250), false, "8:00-22:00", null, null),
                    new MerchantDTO(3L, "商家3", "描述3", "地址3", "18800000000",
                    4.0, new BigDecimal(300), true, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1, dish2, dish3));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }


    // 6. 非法请求区间（返回空列表）
    @Test
    void testInvalidRange() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("测试"), 5.0, 3.0,
                new BigDecimal(400), new BigDecimal(100), new BigDecimal(250), new BigDecimal(120),
                false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
                List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(100), true, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(0, result.size());
    }


    // 7. 合法请求但符合的商家列表为空，正常返回空列表
    @Test
    void testNoMerchantsFound() {
        MerchantFilterAndSortRequest request = new MerchantFilterAndSortRequest(
                List.of("测试"), 3.0, 5.0,
                new BigDecimal(100), new BigDecimal(400), new BigDecimal(120), new BigDecimal(250),
                false, true, false, false
        );

        when(merchantService.searchMerchants(any())).thenReturn(
                List.of(new MerchantDTO(1L, "商家1", "描述1", "地址1", "13900000000",
                        4.5, new BigDecimal(100), true, "8:00-22:00", null, null))
        );
        when(dishRepository.findAll()).thenReturn(List.of(dish1));

        List<MerchantDTO> result = merchantFilter.filterMerchants(request);
        assertEquals(0, result.size());
    }

}
