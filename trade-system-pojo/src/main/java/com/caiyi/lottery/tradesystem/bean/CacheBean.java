package com.caiyi.lottery.tradesystem.bean;

/**
 * redis和memchache缓存存储数据bean、取出数据bean
 *
 * @author wjy
 * @create 2017-12-14 13:35
 */
public class CacheBean {

    private String key;// 键
    private String value;// 值
    private long time; // 过期时间，单位毫秒

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
