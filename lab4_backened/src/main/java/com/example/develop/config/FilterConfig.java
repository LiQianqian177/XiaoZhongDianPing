package com.example.develop.config;

import com.example.develop.filter.JwtFilter;
import com.example.develop.utils.JwtTokenUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

// 配置过滤器
@Configuration
public class FilterConfig {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenUtil jwtTokenUtil; // 改为注入已存在的 Bean

    // 通过构造器注入 Spring 管理的 Bean
    public FilterConfig(
            RedisTemplate<String, String> redisTemplate,
            JwtTokenUtil jwtTokenUtil // 确保 JwtTokenUtil 是 Spring Bean
    ) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();

        // 使用注入的 Bean 代替手动创建
        registrationBean.setFilter(new JwtFilter(jwtTokenUtil, redisTemplate));

        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1); // 明确设置过滤器顺序

        return registrationBean;
    }
}
