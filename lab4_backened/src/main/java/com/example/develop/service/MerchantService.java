package com.example.develop.service;

import com.example.develop.DTO.MerchantDTO;
import com.example.develop.DTO.MerchantImageDTO;
import com.example.develop.entity.Merchant;
import com.example.develop.entity.MerchantImage;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public interface MerchantService {
    MerchantDTO getMerchantById(@NotNull Long id);
    List<MerchantImageDTO> getMerchantImagesById(@NotNull Long merchantId);
    List<MerchantDTO> getAllMerchants();
    List<MerchantDTO> searchMerchants(List<String> keywords);
    MerchantDTO getMerchantWithImages(Long merchantId);
}
