package com.example.develop.filter;

import com.example.develop.utils.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.Cookie;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // 合并两个依赖项
    public JwtFilter(JwtTokenUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = jwtUtil.getTokenFromRequest(request);

        if (token != null) {
            // 保留黑名单检查
            if (redisTemplate.hasKey("blacklist:" + token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 使用 JwtUtil 的验证逻辑
            if (jwtUtil.validateToken(token)) {
                setSecurityContextAuthentication(token);
            }
        }

        chain.doFilter(request, response);
    }


    // 新增 Spring Security 上下文设置
    private void setSecurityContextAuthentication(String token) {
        String username = jwtUtil.getUsername(token);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
