package com.meicloud.paas.osca.console.config;

import com.meicloud.paas.common.hepler.LocalConfigHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chenlei140
 * @className AppInterceptor
 * @description app拦截器
 * @date 2022/9/8 17:19
 */
@Configuration
public class AppInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("------ request ended -----");
        LocalConfigHelper.clear();
    }

}
