package com.caiyi.lottery.tradesystem.userweb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import redis.clients.jedis.JedisPoolConfig;

//这个类用配置redis服务器的连接
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 1800)
@Configuration
public class SessionConfig {

	/*@Value("${spring.redis.host}")
	String HostName;
	@Value("${spring.redis.port}")
	int Port;

	@Bean
	public JedisConnectionFactory connectionFactory() {
		JedisConnectionFactory connection = new JedisConnectionFactory();
		connection.setPort(Port);
		connection.setHostName(HostName);
		return connection;
	}*/

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
		cf.setTimeout(Integer.valueOf(redisConnBean.getTimeout()));
		return cf;
	}
}
