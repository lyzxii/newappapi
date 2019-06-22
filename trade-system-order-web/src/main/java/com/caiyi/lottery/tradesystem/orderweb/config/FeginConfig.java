package com.caiyi.lottery.tradesystem.orderweb.config;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FeginConfig配置
 *
 * @author GJ
 * @create 2017-12-21 10:03
 **/
//@Configuration
public class FeginConfig {
    //禁止重试
    @Bean
    public Retryer feignRetryer(){
        // 第一个参数period是请求重试的间隔算法参数，第二个参数maxPeriod 是请求间隔最大时间，第三个参数是重试的次数。
        // 覆盖配置文件配置
    //    return new Retryer.Default(100,  TimeUnit.SECONDS.toMillis(10), 3) {
        return new  Retryer(){
            /**
             * 该方法是进行重试的关键方法
             * @param e
             */
            @Override
            public void continueOrPropagate(RetryableException e) {
                //直接抛出异常不进行重试
                throw e;
            }

            @Override
            public Retryer clone() {
                return this;
            }
        };
    }

    /**
     * 配置fegin超时时间重试时间，覆盖ribbon的超时时间，提前进行重试，然后抛出异常终止重试。
     * @return
     */
    @Bean
    public Request.Options feginOption(){
        Request.Options option = new Request.Options(3000,5000);
        return option;
    }
}
