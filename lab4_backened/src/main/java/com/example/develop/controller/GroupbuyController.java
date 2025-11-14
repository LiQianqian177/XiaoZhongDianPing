package com.example.develop.controller;

import com.example.develop.DTO.GroupbuyDTO;
import com.example.develop.service.GroupbuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groupbuys")
public class GroupbuyController {

    @Autowired
    private GroupbuyService groupbuyService;

    /**
     * 根据商家ID查询商家团购
     * @param merchantId 商家ID
     * @return 商家的团购列表
     */
    @GetMapping(    "/merchant/{merchantId}")
    public ResponseEntity<List<GroupbuyDTO>> getGroupbuysByMerchant(@PathVariable Long merchantId) {
        List<GroupbuyDTO> groupbuys = groupbuyService.getGroupbuysByMerchantId(merchantId);
        return ResponseEntity.ok(groupbuys);
    }

    /**
     * 根据团购ID查询团购详情
     * @param groupbuyId 团购ID
     * @return 团购的详细信息
     */
    @GetMapping("/{groupbuyId}")
    public ResponseEntity<GroupbuyDTO> getGroupbuyDetails(@PathVariable Long groupbuyId) {
        GroupbuyDTO groupbuyDetails = groupbuyService.getGroupbuyDetailsById(groupbuyId);
        return ResponseEntity.ok(groupbuyDetails);
    }
}