package com.example.develop.service;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    // 内存存储验证码信息（captchaId -> 验证码文本和过期时间）
    private static final Map<String, CaptchaData> CAPTCHA_STORE = new ConcurrentHashMap<>();

    // 验证码有效期（单位：分钟）
    private static final int CAPTCHA_EXPIRE_MINUTES = 5;

    @Autowired
    private Producer kaptchaProducer;

    // 生成图形验证码（返回验证码ID和图片）
    public BufferedImage generateCaptchaImage(String captchaId) {
        // 生成验证码文本
        String code = kaptchaProducer.createText();

        // 存储到内存
        CAPTCHA_STORE.put(captchaId, new CaptchaData(
                code,
                LocalDateTime.now().plusMinutes(CAPTCHA_EXPIRE_MINUTES)
        ));

        // 生成图片
        return kaptchaProducer.createImage(code);
    }

    // 验证图形验证码
    public boolean validateCaptcha(String captchaId, String userInput) {
        // 获取验证码数据
        CaptchaData captchaData = CAPTCHA_STORE.get(captchaId);
        if (captchaData == null || LocalDateTime.now().isAfter(captchaData.expiresAt)) {
            return false;
        }

        // 验证成功后删除记录
        if (captchaData.code.equalsIgnoreCase(userInput)) {
            CAPTCHA_STORE.remove(captchaId);
            return true;
        }
        return false;
    }

    // 内部类：存储验证码文本和过期时间
    private static class CaptchaData {
        String code;
        LocalDateTime expiresAt;

        CaptchaData(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }
}
