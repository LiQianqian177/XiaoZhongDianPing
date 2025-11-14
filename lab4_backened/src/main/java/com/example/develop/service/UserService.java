package com.example.develop.service;

import com.example.develop.DTO.LoginRequest;
import com.example.develop.DTO.NewUserRequest;
import com.example.develop.DTO.UserResponse;
import com.example.develop.entity.User;
import com.example.develop.entity.UserLoginRecord;
import com.example.develop.exception.InvalidTokenException;
import com.example.develop.exception.TokenNotProvidedException;
import com.example.develop.repository.UserLoginRecordRepository;
import com.example.develop.repository.UserRepository;
import com.example.develop.utils.JwtTokenUtil;
import com.example.develop.utils.PasswordUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserLoginRecordRepository userLoginRecordRepository;
    public UserService(
            UserRepository userRepository,
            RedisTemplate<String, String> redisTemplate,
            JwtTokenUtil jwtTokenUtil,
            UserLoginRecordRepository userLoginRecordRepository  // 新增参数
    ) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.jwtTokenUtil = jwtTokenUtil;  // 初始化
        this.userLoginRecordRepository = userLoginRecordRepository;
    }
    //分开各个功能至不同函数中，方便拼接
    public UserResponse registerUser(NewUserRequest request) {
        // 1. 校验用户名格式
        validateUsernameFormat(request.name());

        // 2. 检查用户名是否存在
        checkUsernameExistence(request.name());

        // 3. 校验密码格式
        validatePasswordFormat(request.password());

        // 4. 加密密码
        String encryptedPassword = PasswordUtil.encryptPassword(request.password());

        // 5. 创建并保存用户
        User user = createAndSaveUser(request.name(), encryptedPassword);

        // 6. 返回响应
        return buildUserResponse(user);
    }
    private void validateUsernameFormat(String username) {
        if (!username.matches("^[a-zA-Z0-9_]{4,20}$")) {
            throw new IllegalArgumentException("用户名只能包含数字、字母、下划线，长度4-20个字符");
        }
    }

    // 辅助方法：检查用户名是否存在
    private void checkUsernameExistence(String username) {
        if (userRepository.existsByName(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
    }

    // 辅助方法：校验密码格式
    private void validatePasswordFormat(String password) {
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,20}$")) {
            throw new IllegalArgumentException("密码必须包含数字和字母，长度6-20个字符");
        }
    }

    // 辅助方法：创建并保存用户
    private User createAndSaveUser(String username, String encryptedPassword) {
        User user = new User();
        user.setName(username);
        user.setEncryptedPassword(encryptedPassword);
        return userRepository.save(user);
    }

    // 辅助方法：构建响应对象
    private UserResponse buildUserResponse(User user) {
        System.out.println("用户注册成功：" + user.getName());
        return new UserResponse(user.getId(), user.getName());
    }
    public String login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByName(request.name());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (PasswordUtil.checkPassword(request.password(), user.getEncryptedPassword())) {
                // 调用 JwtTokenUtil 生成 Token
                String token = jwtTokenUtil.generateToken(user.getName());
                UserLoginRecord record = new UserLoginRecord(user.getName(), token, new Date());
                userLoginRecordRepository.save(record);
                return token;
            }
        }
        throw new RuntimeException("用户名或密码错误");
    }

    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtTokenUtil.getTokenFromRequest(request);

        if (token == null) {
            throw new RuntimeException("未提供有效的 Token");
        }

        try {
            // 解析 token，确认有效性
            String username = jwtTokenUtil.getUsername(token);
            Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);

            // 如果 Token 已经过期，直接返回
            if (jwtTokenUtil.isTokenExpired(token)) {
                throw new RuntimeException("Token 已经过期");
            }
            //记录登出时间
            Optional<UserLoginRecord> recordOptional = userLoginRecordRepository.findByToken(token);
            if (recordOptional.isPresent()) {
                UserLoginRecord record = recordOptional.get();
                record.setLogoutTime(new Date());
                userLoginRecordRepository.save(record);
            }


            // 删除 HttpOnly Cookie
            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0); // 立即过期
            response.addCookie(cookie);
            return  username ;
        } catch (Exception e) {
            throw new RuntimeException("无效的 Token: " + e.getMessage());
        }

    }

    public String getUsernameFromToken(HttpServletRequest request) {
        String token = jwtTokenUtil.getTokenFromRequest(request);

        if (token == null) {
            throw new TokenNotProvidedException("未提供有效的 Token");
        }

        try {
            // 解析 Token 获取用户名
            String username = jwtTokenUtil.getUsername(token);

            // 检查 Token 是否过期
            if (jwtTokenUtil.isTokenExpired(token)) {
                throw new InvalidTokenException("Token 已过期");
            }

            // 检查用户是否已注销
            if (!userLoginRecordRepository.existsByTokenAndLogoutTimeIsNull(token)) {
                throw new InvalidTokenException("Token 无效，用户已注销");
            }

            return username;
        } catch (JwtException e) {
            // 捕获 JWT 解析异常
            throw new InvalidTokenException("解析 Token 失败: " + e.getMessage());
        }
    }

    public List<UserLoginRecord> getLoggedInUsers() {
        return userLoginRecordRepository.findByLogoutTimeIsNull();
    }


    public Date getTokenExpiration(String token) {
        // 参数合法性检查
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }

        try {
            // 解析Token基本信息
            Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);

            // 验证Token有效性（新增校验逻辑）
            validateTokenState(token, expiration);

            return expiration;
        } catch (JwtException ex) {
            throw new InvalidTokenException("Token解析失败: " + ex.getMessage());
        }
    }
    private void validateTokenState(String token, Date expiration) {
        // 检查数据库中的登录记录
        Optional<UserLoginRecord> record = userLoginRecordRepository.findByToken(token);

        // 记录不存在或已注销
        if (record.isEmpty() || record.get().getLogoutTime() != null) {
            throw new InvalidTokenException("Token已被注销");
        }

        // 检查Redis黑名单
        if (redisTemplate.hasKey("invalid_tokens:" + token)) {
            throw new InvalidTokenException("Token已被加入黑名单");
        }
    }
    public User validateToken(String token) {
        // 防御性参数检查
        if (token == null || token.trim().isEmpty()) {
            throw new TokenNotProvidedException("认证令牌缺失");
        }

        try {
            // JWT基础验证
            String username = jwtTokenUtil.getUsername(token);
            Date expiration = jwtTokenUtil.getExpirationDateFromToken(token);

            // 用户存在性验证
            User user = userRepository.findByName(username)
                    .orElseThrow(() -> new InvalidTokenException("用户不存在"));

            // 登录记录状态验证
            validateLoginRecord(token);

            // 黑名单检查
            checkTokenBlacklist(token);

            return user;

        } catch (ExpiredJwtException ex) {
            throw new InvalidTokenException("令牌已过期");
        } catch (JwtException ex) {
            throw new InvalidTokenException("令牌格式异常");
        }
    }

    private void validateLoginRecord(String token) {
        Optional<UserLoginRecord> record = userLoginRecordRepository.findByToken(token);
        if (record.isEmpty() || record.get().getLogoutTime() != null) {
            throw new InvalidTokenException("令牌已注销");
        }
    }

    private void checkTokenBlacklist(String token) {
        Boolean isBlacklisted = redisTemplate.opsForValue()
                .getOperations()
                .hasKey("token_blacklist:" + token);

        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new InvalidTokenException("令牌已被禁用");
        }
    }
    public Long getUserIdByName(String name) {
        Optional<User> userOptional = userRepository.findByName(name);
        if (userOptional.isPresent()) {
            return userOptional.get().getId();
        } else {
            throw new RuntimeException("用户不存在");
        }
    }
}
