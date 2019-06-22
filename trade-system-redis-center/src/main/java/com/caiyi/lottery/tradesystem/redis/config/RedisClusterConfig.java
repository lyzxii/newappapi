package com.caiyi.lottery.tradesystem.redis.config;

import com.caiyi.lottery.tradesystem.redis.bean.RedisConnBean;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/** 生成JedisCluster对象
 * @ClassName: JedisClusterConfig   
 **/
@Configuration
@AutoConfigureAfter(RedisConnBean.class)
public class RedisClusterConfig {
  
    @Autowired
    private RedisConnBean redisConnBean;

    @Bean
    public RedisClusterConfiguration getRedisCluster() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisConnBean.getNodes());
        redisClusterConfiguration.setMaxRedirects(Integer.valueOf(redisConnBean.getMaxAttempts()));
        return redisClusterConfiguration;
    }

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.valueOf(redisConnBean.getPoolMaxActive()));
        poolConfig.setMaxIdle(Integer.valueOf(redisConnBean.getPoolMaxIdle()));
        poolConfig.setMaxWaitMillis(Integer.valueOf(redisConnBean.getPoolMaxWait()));
        poolConfig.setMinIdle(Integer.valueOf(redisConnBean.getPoolMinIdle()));
        poolConfig.setTestOnBorrow(Boolean.valueOf(redisConnBean.getTestOnBorrow()));

        JedisConnectionFactory cf = new JedisConnectionFactory(getRedisCluster(),poolConfig);
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