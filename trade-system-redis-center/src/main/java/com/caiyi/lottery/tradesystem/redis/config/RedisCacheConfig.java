package com.caiyi.lottery.tradesystem.redis.config;

import com.caiyi.lottery.tradesystem.redis.bean.RedisConnBean;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;


/**
 * redis配置类
 *
 * @author wjy
 * @create 2017-12-13 15:01
 */
//@Configuration
//@EnableCaching
//@AutoConfigureAfter(RedisConnBean.class)
public class RedisCacheConfig extends CachingConfigurerSupport{

    @Autowired
    private RedisConnBean redisConnBean;

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        //设置缓存过期时间
        //rcm.setDefaultExpiration(60);//秒
        return rcm;
    }

    @Bean
    public RedisConnectionFactory redisCF() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.valueOf(redisConnBean.getPoolMaxActive()));
        poolConfig.setMaxIdle(Integer.valueOf(redisConnBean.getPoolMaxIdle()));
        poolConfig.setMaxWaitMillis(Integer.valueOf(redisConnBean.getPoolMaxWait()));
        poolConfig.setMinIdle(Integer.valueOf(redisConnBean.getPoolMinIdle()));
        poolConfig.setTestOnBorrow(Boolean.valueOf(redisConnBean.getTestOnBorrow()));
        JedisConnectionFactory cf = new JedisConnectionFactory(poolConfig);
        cf.setDatabase(Integer.valueOf(redisConnBean.getDatabase()));
        cf.setHostName(String.valueOf(redisConnBean.getHost()));
        cf.setPort(Integer.valueOf(redisConnBean.getPort()));
        cf.setTimeout(Integer.valueOf(redisConnBean.getTimeout()));
        return cf;
    }
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}