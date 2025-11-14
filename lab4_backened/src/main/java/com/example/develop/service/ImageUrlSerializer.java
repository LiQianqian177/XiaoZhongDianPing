package com.example.develop.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

// 自定义序列化器
public class ImageUrlSerializer extends JsonSerializer<String> {
    @Value("${image.base-url}")
    private String baseUrl;

    @Override
    public void serialize(String value,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {
        gen.writeString(baseUrl + value);
    }
}

