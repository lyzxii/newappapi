package com.caiyi.lottery.tradesystem.redis.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class RedisInterfaceError implements RedisInterface{
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
    @Override
    public BaseResp<CacheBean> setString(BaseReq<CacheBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.CACHE_REMOTE_INVOKE_ERROR);
        resp.setDesc("缓存中心调用失败");
        log.info("缓存中心setString调用失败,req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 根据指定key获取字符串
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<CacheBean> getString(BaseReq<CacheBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.CACHE_REMOTE_INVOKE_ERROR);
        resp.setDesc("缓存中心调用失败");
        log.info("缓存中心getString调用失败,req:"+baseReq.toJson());
        return resp;
    }

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
    @Override
    public BaseResp<CacheBean> getJsonString(BaseReq<CacheBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.CACHE_REMOTE_INVOKE_ERROR);
        resp.setDesc("缓存中心调用失败");
        log.info("缓存中心getJsonString调用失败,req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 根据指定key获取xml字符串
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<CacheBean> getXmlString(BaseReq<CacheBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.CACHE_REMOTE_INVOKE_ERROR);
        resp.setDesc("缓存中心调用失败");
        log.info("缓存中心getXmlString调用失败,req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 删除指定key的数据
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<CacheBean> delete(BaseReq<CacheBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.CACHE_REMOTE_INVOKE_ERROR);
        resp.setDesc("缓存中心调用失败");
        log.info("缓存中心delete调用失败,req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 判断指定key是否存在
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<CacheBean> exists(BaseReq<CacheBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.CACHE_REMOTE_INVOKE_ERROR);
        resp.setDesc("缓存中心调用失败");
        log.info("缓存中心exists调用失败,req:"+baseReq.toJson());
        return resp;
    }
}
