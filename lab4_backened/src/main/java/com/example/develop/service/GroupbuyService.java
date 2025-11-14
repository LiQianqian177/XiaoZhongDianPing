package com.example.develop.service;

import com.example.develop.DTO.GroupbuyDTO;
import com.example.develop.entity.Groupbuy;
import com.example.develop.repository.GroupbuyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupbuyService {

    @Autowired
    private GroupbuyRepository groupbuyRepository;

    /**
     * 根据商家ID查询商家团购
     * @param merchantId 商家ID
     * @return 团购列表
     */
    public List<GroupbuyDTO> getGroupbuysByMerchantId(Long merchantId) {
        List<Groupbuy> groupbuys = groupbuyRepository.findByMerchantId(merchantId);
        return groupbuys.stream()
                .map(GroupbuyDTO::fromEntity) // 使用 DTO 的静态方法进行转换
                .collect(Collectors.toList());
    }

    /**
     * 根据团购ID查询团购详情
     * @param groupbuyId 团购ID
     * @return 团购详情
     */
    public GroupbuyDTO getGroupbuyDetailsById(Long groupbuyId) {
        Groupbuy groupbuy = groupbuyRepository.findById(groupbuyId)
                .orElseThrow(() -> new RuntimeException("团购信息不存在"));
        return GroupbuyDTO.fromEntity(groupbuy); // 使用 DTO 的静态方法进行转换
    }
}