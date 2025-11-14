package com.example.develop.controller;

import com.example.develop.DTO.MerchantDTO;

import com.example.develop.DTO.MerchantFilterAndSortRequest;
import com.example.develop.service.MerchantFilter;
import com.example.develop.service.MerchantService;
import com.example.develop.service.MerchantSorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MerchantController 类是一个 RESTful 控制器，负责处理与商家相关的 HTTP 请求。
 * 该类依赖于 MerchantService、MerchantFilter 和 MerchantSorting 服务，
 * 提供了搜索商家、根据 ID 获取商家信息以及对商家进行过滤和排序的功能。
 */
@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    private final MerchantService merchantService;
    private final MerchantFilter merchantFilter;
    private final MerchantSorting merchantSorting;

    /**
     * 构造函数，使用 Spring 的依赖注入机制初始化所需的服务。
     *
     * @param merchantService 处理商家业务逻辑的服务
     * @param merchantFilter  过滤商家数据的服务
     * @param merchantSorting 对商家数据进行排序的服务
     */
    @Autowired
    public MerchantController(MerchantService merchantService,
                              MerchantFilter merchantFilter,
                              MerchantSorting merchantSorting) {
        this.merchantService = merchantService;
        this.merchantFilter = merchantFilter;
        this.merchantSorting = merchantSorting;
    }

    /**
     * 根据给定的关键词列表搜索商家。
     * 该方法接收一个关键词列表作为请求参数，调用 MerchantService 的 searchMerchants 方法进行搜索。
     *
     * @param keywords 用于搜索商家的关键词列表
     * @return 符合搜索条件的商家 DTO 列表
     */
    @GetMapping("/search")
    public List<MerchantDTO>searchMerchantsByKeywords(@RequestParam List<String> keywords) {
        return merchantService.searchMerchants(keywords);
    }

    /**
     * 根据商家的 ID 获取商家的具体信息。
     * 该方法接收一个商家 ID 作为路径变量，调用 MerchantService 的 getMerchantById 方法获取商家信息。
     *
     * @param id 要获取信息的商家的 ID
     * @return 对应 ID 的商家 DTO
     */
    @GetMapping("/{id}")
    public MerchantDTO getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }


    /**
     * 对商家数据进行过滤和排序。
     * 该方法接收一个包含过滤和排序请求信息的对象，
     * 首先调用 MerchantFilter 的 filterMerchants 方法对商家数据进行过滤，
     * 然后调用 MerchantSorting 的 sortMerchant 方法对过滤后的商家数据进行排序。
     *
     * @param request 包含过滤和排序请求信息的对象
     * @return 经过过滤和排序后的商家 DTO 列表
     */
    @PostMapping("/filterAndSort")
    public List<MerchantDTO> filterAndSortMerchants(@RequestBody MerchantFilterAndSortRequest request) {
        List<MerchantDTO> filtered = merchantFilter.filterMerchants(request);
        return merchantSorting.sortMerchant(filtered, request);
    }
}
