package com.example.develop.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Map;

import com.example.develop.service.CaptchaService;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private DefaultKaptcha kaptcha;
    private CaptchaService captchaService;

    // 生成图形验证码
    @GetMapping
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        LocalDateTime lastGenTime = (LocalDateTime) session.getAttribute("lastGenTime");

        // 60秒内不允许重复生成
        if (lastGenTime != null && LocalDateTime.now().isBefore(lastGenTime.plusSeconds(3))) {
            response.sendError(429, "验证码请求过于频繁");
            return;
        }
        // 生成验证码文本和图片
        String code = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(code);
        System.out.println("生成的验证码：" + code);
        // 存储到 Session（关键代码）
        request.getSession().setAttribute("captcha", code);
        request.getSession().setAttribute("captchaGeneratedTime", LocalDateTime.now());
        // 输出图片到响应流
        response.setContentType("image/jpeg");
        try (OutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "jpg", out);
        }
        session.setAttribute("lastGenTime", LocalDateTime.now());
    }

    // 验证图形验证码
    @PostMapping("/verify")
    public boolean verifyCaptcha(@RequestParam String userInput, HttpServletRequest request) {
        // 从 Session 获取验证码
        String storedCode = (String) request.getSession().getAttribute("captcha");
        return storedCode != null && storedCode.equalsIgnoreCase(userInput);
    }
}