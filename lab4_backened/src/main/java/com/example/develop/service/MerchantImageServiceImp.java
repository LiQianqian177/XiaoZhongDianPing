package com.example.develop.service;

import com.example.develop.entity.Merchant;
import com.example.develop.entity.MerchantImage;
import com.example.develop.repository.MerchantImageRepository;
import com.example.develop.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class MerchantImageServiceImp implements MerchantImageService {
    private final MerchantImageRepository imageRepository;
    private final MerchantRepository merchantRepository;

    @Value("${image.upload-dir}")
    private String uploadDir;

    public MerchantImage uploadImage(Long merchantId,
                                     MultipartFile file,
                                     String description,
                                     String type) throws IOException {
        // 1. 验证商家存在
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new EntityNotFoundException("商家不存在"));
        // 2. 生成唯一文件名
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        // 3. 保存文件
        Files.createDirectories(filePath.getParent());
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        // 4. 保存数据库记录
        MerchantImage image = new MerchantImage();
        image.setImageUrl(fileName);  // 存储相对路径
        image.setDescription(description);
        image.setType(type);
        image.setMerchant(merchant);

        return imageRepository.save(image);
    }
    public List<MerchantImage> findByMerchantId(Long merchantId) {
        if (merchantId == null || merchantId <= 0) {
            throw new IllegalArgumentException("Invalid merchant ID");
        }
        return imageRepository.findByMerchantId(merchantId);
    }
}
