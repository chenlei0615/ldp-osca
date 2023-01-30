package com.meicloud.paas.osca.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author chenlei140
 * @className AppConfig
 * @description app拦截器注册
 * @date 2022/9/8 17:24
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Bean
    public HandlerInterceptor authInterceptor() {
        return new AppInterceptor();
    }

}
