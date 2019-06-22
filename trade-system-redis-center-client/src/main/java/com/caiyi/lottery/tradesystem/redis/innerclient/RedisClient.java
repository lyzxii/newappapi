package com.caiyi.lottery.tradesystem.redis.innerclient;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Redis客户端封装接口
 *
 * @author wjy
 * @create 2017-12-15 16:41
 */
@Component("RedisClient")
public class RedisClient {

    @Autowired
    private RedisInterface redisInterface;

    /**
     * 存储字符串
     * 使用fastjson，转化为字符串例子：
     * 1、字符串直接存储
     * 2、XXXBean转化为字符串: bean.toJsonString()
     * 3、Map转化为字符串: JSONObject.toJSONString(map);
     * 4、List转化为字符串: JSONObject.toJSONString(list);
     *
     * @param bean bean.setValue(-1)为永不过期、bean.setValue()不设置默认过期时间为30天
     * @param log
     * @return
     */
    public boolean setString(CacheBean bean, Logger log, String syscode) {
        boolean flag = false;
        try {
            BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
            BaseResp<CacheBean> resp = redisInterface.setString(req);
            if (resp != null && resp.getCode() != null && BusiCode.SUCCESS.equals(resp.getCode())) {
                flag = true;
            } else {
                flag = false;
                log.info("设置缓存至缓存中心失败,key=" + bean.getKey() + " value=" + bean.getValue() +
                        " code=" + resp.getCode() + " desc=" + resp.getDesc());
            }
        } catch (Exception e) {
            log.error("异常信息", e);
        }
        return flag;
    }

    /**
     * 根据指定key获取字符串
     *
     * @param bean
     * @param log
     * @return
     */
    public String getString(CacheBean bean, Logger log,String syscode) {
        String result = "";
        try {
            BaseReq<CacheBean> req = new BaseReq<>(bean, syscode);
            BaseResp<CacheBean> resp = redisInterface.getString(req);
            if (BusiCode.SUCCESS.equals(resp.getCode())) {
                CacheBean cacheBean = resp.getData();
                if (cacheBean == null || cacheBean.getValue() == null) {
                    log.info("getString 缓存key为:" + bean.getKey() + "的缓存值为空");
                } else {
                    result = cacheBean.getValue();
                }
            } else {
                log.info("从缓存中心获取String失败,key=" + bean.getKey() + " code=" + resp.getCode() + " desc=" + resp.getDesc());
            }
        } catch (Exception e) {
            log.error("异常信息", e);
        }
        return result;
    }

    /**
     * 根据指定key获取Object对象
     * 返回值Object可强制转化为指定对象（XXXBean、Map、List）例子：
     * 调用方式：
     * UserBean bean = (UserBean)redisClient.getObject(cacheBean, UserBean.class, logger);
     * Map<String, UserBean> maps = (Map)redisClient.getObject(cacheBean,Map.class, logger);
     * Map<String, Map<String, UserBean>> maps = (Map) redisClient.getObject(cacheBean, Map.class, logger);
     * List<String> list = (List) redisClient.getObject(cacheBean, List.class, logger);
     * List<Map<String, UserBean>> list = (List) redisClient.getObject(cacheBean, List.class, logger);
     *
     * @param bean
     * @param clazz 预期返回的对象类型：XXXBean.class、Map.class、List.class
     * @param log
     * @return
     */
    public Object getObject(CacheBean bean, Class clazz, Logger log, String syscode) {
        Object object = null;
        try {
            BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
            BaseResp<CacheBean> resp = redisInterface.getJsonString(req);
            if (BusiCode.SUCCESS.equals(resp.getCode())) {
                CacheBean cacheBean = resp.getData();
                if (cacheBean == null || cacheBean.getValue() == null) {
                    log.info("getJsonString 缓存key为:" + bean.getKey() + "的缓存值为空");
                } else {
                    object = JSONObject.parseObject(cacheBean.getValue(), clazz);
                }
            } else {
                log.info("从缓存中心获取JsonString失败,key=" + bean.getKey() + " code=" + resp.getCode() + " desc=" + resp.getDesc());
            }
        } catch (Exception e) {
            log.error("异常信息", e);
        }
        return object;
    }

    /**
     * 根据指定key获取JXmlWrapper对象
     *
     * @param bean
     * @param log
     * @return
     */
    public JXmlWrapper getXmlString(CacheBean bean, Logger log, String syscode) {
        JXmlWrapper xml = null;
        try {
            BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
            BaseResp<CacheBean> resp = redisInterface.getXmlString(req);
            if (BusiCode.SUCCESS.equals(resp.getCode())) {
                CacheBean cacheBean = resp.getData();
                if (cacheBean == null || cacheBean.getValue() == null) {
                    log.info("getXmlString 缓存key为:" + bean.getKey() + "的缓存值为空");
                } else {
                    xml = JXmlWrapper.parse(cacheBean.getValue());
                }
            } else {
                log.info("从缓存中心获取XmlString失败,key=" + bean.getKey() + " value=" + bean.getValue() +
                        " code=" + resp.getCode() + " desc=" + resp.getDesc());
            }
        } catch (Exception e) {
            log.error("异常信息", e);
        }
        return xml;
    }

    /**
     * 删除指定key的数据
     *
     * @param bean
     * @param log
     * @return
     */
    public boolean delete(CacheBean bean, Logger log, String syscode) {
        boolean flag = false;
        try {
            BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
            BaseResp<CacheBean> resp = redisInterface.delete(req);
            if (BusiCode.SUCCESS.equals(resp.getCode())) {
                flag = true;
            } else {
                log.error("缓存中心删除key=" + bean.getKey() + "失败  code=" + resp.getCode() + " desc=" + resp.getDesc());
            }
        } catch (Exception e) {
            log.error("异常信息", e);
        }
        return flag;
    }

    /**
     * 判断指定key是否存在
     *
     * @param bean
     * @param log
     * @return
     */
    public boolean exists(CacheBean bean, Logger log, String syscode) {
        boolean flag = false;
        try {
            BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
            BaseResp<CacheBean> resp = redisInterface.getString(req);
            if (BusiCode.SUCCESS.equals(resp.getCode())) {
                flag = true;
            } else {
                log.info("缓存中心判断key是否存在失败,key=" + bean.getKey() + "失败 code=" + resp.getCode() + " desc=" + resp.getDesc());
            }
        } catch (Exception e) {
            log.error("异常信息", e);
        }
        return flag;
    }

}
