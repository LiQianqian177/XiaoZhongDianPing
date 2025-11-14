package com.example.develop.controller;

import com.example.develop.DTO.*;
import com.example.develop.entity.User;
import com.example.develop.exception.InvalidTokenException;
import com.example.develop.exception.TokenNotProvidedException;
import com.example.develop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final CaptchaController CaptchaController;

    public UserController(
            UserService userService,
            CaptchaController captchaController  // 通过构造函数注入
    ) {
        this.userService = userService;
        this.CaptchaController = captchaController;
    }
    //用户注册
    @PostMapping("/register")
    public UserResponse registerUserCheckCaptcha(@RequestBody NewUserRequest request, HttpServletRequest httpRequest) {
        // 直接使用注入的 captchaController
        if (!CaptchaController.verifyCaptcha(request.verificationCode(), httpRequest)) {
            throw new RuntimeException("验证码错误!");
        }
        return userService.registerUser(request);
    }

    //用户登录
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            String token = userService.login(request);
            // 创建 HttpOnly Cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true); // 禁止前端 JavaScript 访问
            cookie.setSecure(true);   // 仅在 HTTPS 传输
            cookie.setPath("/");      // 适用于整个应用
            cookie.setMaxAge(3600);   // 1小时过期
            response.addCookie(cookie);
            return new LoginResponse(true, "登录成功", token);
        } catch (RuntimeException e) {
            return new LoginResponse(false, e.getMessage(), null);
        }
    }

    //对于已经登录的用户，返回用户信息
    @GetMapping("/loggedUser")
    public ResponseEntity<LoggedUserResponse> getLoggedUser(HttpServletRequest request) {
        try {
            // 从 Token 中获取用户名
            String name = userService.getUsernameFromToken(request);
            Long userId = userService.getUserIdByName(name);
            return ResponseEntity.ok(new LoggedUserResponse(true, name, userId));
        } catch (TokenNotProvidedException | InvalidTokenException e) {
            // 返回 401 Unauthorized 状态
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoggedUserResponse(false, null,null));
        }
    }

    //注销
    @GetMapping("/logout")
    public LogoutResponse logout(HttpServletRequest request, HttpServletResponse response) {
        String username = userService.logout(request, response);
        String message = username != null ? "用户 " + username + " 注销成功" : "注销成功";
        return new LogoutResponse(true, message);
    }
    // 新增在UserController类中

    @GetMapping("/auth-status")
    public ResponseEntity<AuthStatusResponse> checkAuthStatus(
            HttpServletRequest request,
            @CookieValue(value = "token", required = false) String token) {

        try {
            if (token == null || token.isEmpty()) {
                throw new TokenNotProvidedException("未找到认证令牌");
            }

            // 验证token有效性
            User authenticatedUser = userService.validateToken(token);

            // 构建响应数据
            AuthStatusResponse response = new AuthStatusResponse(
                    true,
                    authenticatedUser.getName(),
                    userService.getTokenExpiration(token)
            );

            return ResponseEntity.ok(response);

        } catch (TokenNotProvidedException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthStatusResponse(false, null, null));
        }
    }

}

