package com.caiyi.lottery.tradesystem.userweb.config;

import com.caiyi.lottery.tradesystem.userweb.interceptor.IpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置
 *
 * @author GJ
 * @create 2017-12-04 18:17
 **/
//去掉拦截器
//@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new IpInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
