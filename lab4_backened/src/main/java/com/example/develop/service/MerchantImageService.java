package com.example.develop.service;

import com.example.develop.entity.MerchantImage;
import io.lettuce.core.dynamic.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MerchantImageService {

    public MerchantImage uploadImage(Long merchantId, MultipartFile file, String description, String type) throws IOException;

    List<MerchantImage> findByMerchantId(Long merchantId);
}
