package com.example.develop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射图片存储路径到 URL
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/"); // 假设图片存储在根目录的 uploads 文件夹
    }
}

