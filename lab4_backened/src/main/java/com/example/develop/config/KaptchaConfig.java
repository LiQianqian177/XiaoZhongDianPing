package com.example.develop.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.util.Config;
import java.io.ObjectInputFilter;
import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha kaptcha() {
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties props = new Properties();
        // 验证码宽度、高度、字符数
        props.put("kaptcha.image.width", "120");
        props.put("kaptcha.image.height", "40");
        props.put("kaptcha.textproducer.char.length", "4");
        // 字体样式
        props.put("kaptcha.textproducer.font.names", "Arial,Courier");
        // 字符颜色
        props.put("kaptcha.textproducer.font.color", "black");
        // 干扰线配置
        props.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        Config config = new Config(props);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}


