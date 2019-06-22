package com.caiyi.lottery.tradesystem.redis.client;


import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;

import com.caiyi.lottery.tradesystem.redis.clienterror.RedisInterfaceError;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redis客户端接口
 *
 * @author wjy
 * @create 2017-12-13 11:43
 */
@FeignClient(name = "tradecenter-system-redis",fallback = RedisInterfaceError.class)
public interface RedisInterface {

    /**
     * 存储字符串
     * 使用fastjson，转化为字符串例子：
     * 1、字符串直接存储
     * 2、XXXBean转化为字符串: bean.toJsonString()
     * 3、Map转化为字符串: JSONObject.toJSONString(map);
     * 4、List转化为字符串: JSONObject.toJSONString(list);
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/setString.api")
    BaseResp<CacheBean> setString(@RequestBody BaseReq<CacheBean> baseReq);

    /**
     * 根据指定key获取字符串
     *
     * @return
     */
    @RequestMapping(value = "/redis/getString.api")
    BaseResp<CacheBean> getString(@RequestBody BaseReq<CacheBean> baseReq);

    /**
     * 根据指定key获取json字符串
     * 使用fastjson，返回值字符串转化为指定对象（XXXBean、Map、List）例子：
     * 返回值字符串 value = baseResp.getData().getValue();
     * UserBean bean = JSONObject.parseObject(value, UserBean.class);
     * Map<String, Object> maps = JSONObject.parseObject(value, Map.class);
     * Map<String, Map<String, Object>> maps = JSONObject.parseObject(value, Map.class);
     * List list = JSONObject.parseArray(value, String.class);// List里放的String
     * List list = JSONObject.parseArray(value, Map.class);// List里放的Map
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/getJsonString.api")
    BaseResp<CacheBean> getJsonString(@RequestBody BaseReq<CacheBean> baseReq);

    /**
     * 根据指定key获取xml字符串
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/getXmlString.api")
    BaseResp<CacheBean> getXmlString(@RequestBody BaseReq<CacheBean> baseReq);

    /**
     * 删除指定key的数据
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/delete.api")
    BaseResp<CacheBean> delete(@RequestBody BaseReq<CacheBean> baseReq);

    /**
     * 判断指定key是否存在
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/exists.api")
    BaseResp<CacheBean> exists(@RequestBody BaseReq<CacheBean> baseReq);
}
