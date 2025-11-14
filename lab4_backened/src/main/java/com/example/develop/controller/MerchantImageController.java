package com.example.develop.controller;

import com.example.develop.entity.MerchantImage;
import com.example.develop.service.MerchantImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

/**
 * MerchantImageController 是一个 RESTful 控制器，负责处理与商家图片相关的 HTTP 请求。
 * 该控制器提供了上传商家图片和获取指定商家所有图片的功能。
 * 它依赖于 MerchantImageService 来执行具体的业务逻辑。
 */
// src/main/java/com/example/develop/controller/MerchantImageController.java
@RestController
@RequestMapping("/api/merchants/{merchantId}/images")
@RequiredArgsConstructor
public class MerchantImageController {
    private final MerchantImageService imageService;

    /**
     * 上传商家图片的 HTTP POST 请求处理方法。
     * 此方法接收一个多部分表单数据请求，包含图片文件、可选的描述和图片类型。
     * 它调用 MerchantImageService 的 uploadImage 方法来处理图片上传，并返回上传成功后的 MerchantImage 对象。
     *
     * @param merchantId  商家的唯一标识符，从路径变量中获取。
     * @param file        要上传的图片文件，通过请求参数 "file" 传递。
     * @param description 图片的可选描述信息，通过请求参数传递，可为空。
     * @param type        图片的类型，通过请求参数传递，默认值为 "GENERAL"。
     * @return 若图片上传成功，返回包含上传后 MerchantImage 对象的 ResponseEntity，状态码为 200 OK；
     *         若上传过程中发生 IOException，抛出状态码为 500 INTERNAL_SERVER_ERROR 的异常。
     * @throws ResponseStatusException 当文件上传失败时，抛出此异常，状态码为 500 INTERNAL_SERVER_ERROR。
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MerchantImage> uploadImage(
            @PathVariable Long merchantId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "GENERAL") String type
    ) {
        try {
            return ResponseEntity.ok(
                    imageService.uploadImage(merchantId, file, description, type)
            );
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "文件上传失败");
        }
    }

    /**
     * 获取指定商家所有图片的 HTTP GET 请求处理方法。
     * 此方法接收商家的唯一标识符作为路径变量，调用 MerchantImageService 的 findByMerchantId 方法来获取该商家的所有图片。
     *
     * @param merchantId 商家的唯一标识符，从路径变量中获取。
     * @return 包含指定商家所有 MerchantImage 对象列表的 ResponseEntity，状态码为 200 OK。
     */
    // 正确：为 GET 请求定义方法
    @GetMapping
    public ResponseEntity<List<MerchantImage>> getImages(@PathVariable Long merchantId) {
        List<MerchantImage> images = imageService.findByMerchantId(merchantId);
        return ResponseEntity.ok(images);
    }
}

