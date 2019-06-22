package com.caiyi.lottery.tradesystem.redis.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 建立redis连接bean
 *
 * @author wjy
 * @create 2017-12-13 12:23
 */
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisConnBean {

    private String database;// 数据库索引（默认为0）
    private String host;// 服务器地址
    private String port;// 服务器端口
    private String poolMaxActive;// 连接池最大连接数，默认值为8，使用负值表示没有限制
    private String poolMaxWait;// 连接池最大阻塞等待时间，单位毫秒，默认值为-1，表示永不超时
    private String poolMaxIdle;// 连接池中的最大空闲连接，默认值为8
    private String poolMinIdle;// 连接池中的最小空闲连接
    private String timeout;// 连接超时时间（毫秒）
    private String testOnBorrow;//是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个

    private List<String> nodes;//集群所用 节点以逗号分隔 ip:port,ip:port
    private String maxAttempts; //重试次数

    public String getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(String testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public String getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(String maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPoolMaxActive() {
        return poolMaxActive;
    }

    public void setPoolMaxActive(String poolMaxActive) {
        this.poolMaxActive = poolMaxActive;
    }

    public String getPoolMaxWait() {
        return poolMaxWait;
    }

    public void setPoolMaxWait(String poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }

    public String getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public void setPoolMaxIdle(String poolMaxIdle) {
        this.poolMaxIdle = poolMaxIdle;
    }

    public String getPoolMinIdle() {
        return poolMinIdle;
    }

    public void setPoolMinIdle(String poolMinIdle) {
        this.poolMinIdle = poolMinIdle;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}