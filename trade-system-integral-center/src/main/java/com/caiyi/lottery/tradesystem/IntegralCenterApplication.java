package com.caiyi.lottery.tradesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.caiyi.lottery.tradesystem.integralcenter.dao")
public class IntegralCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntegralCenterApplication.class, args);
    }
}
