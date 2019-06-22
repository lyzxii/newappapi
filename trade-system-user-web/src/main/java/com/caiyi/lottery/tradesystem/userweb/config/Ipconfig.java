package com.caiyi.lottery.tradesystem.userweb.config;

import com.caiyi.lottery.tradesystem.userweb.filter.IpFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * Ipconfig
 *
 * @author GJ
 * @create 2017-12-04 19:31
 **/
//不配置ip过滤器用切面完成
//@Configuration
public class Ipconfig {

    /**
     * 配置过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(ipFilter());
        registration.addUrlPatterns("/*");
        registration.setName("ipFilter");
        return registration;
    }

    /**
     * 创建一个bean
     * @return
     */
    @Bean(name = "ipFilter")
    public Filter ipFilter() {
        return new IpFilter();
    }
}
