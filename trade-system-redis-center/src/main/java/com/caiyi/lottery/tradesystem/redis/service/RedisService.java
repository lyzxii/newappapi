package com.caiyi.lottery.tradesystem.redis.service;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis业务层
 *
 * @author wjy
 * @create 2017-12-13 10:05
 */
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private Logger logger = LoggerFactory.getLogger(RedisService.class);

    /**
     * set方法
     *
     * @param baseReq
     * @return
     */
    public BaseResp<CacheBean> set(BaseReq<CacheBean> baseReq) {
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            if (checkKeyValue(baseReq, baseResp)) {
                CacheBean bean = baseReq.getData();
                if (bean.getTime() > 0) {
                    stringRedisTemplate.opsForValue().set(bean.getKey(), bean.getValue(), bean.getTime(), TimeUnit.MILLISECONDS);
                } else if (bean.getTime() == 0) {
                    stringRedisTemplate.opsForValue().set(bean.getKey(), bean.getValue(), 30, TimeUnit.DAYS);
                } else {
                    stringRedisTemplate.opsForValue().set(bean.getKey(), bean.getValue());
                }
                baseResp.setCode(BusiCode.SUCCESS);
                baseResp.setDesc("成功");
            }
        } catch (Exception e) {
            baseResp.setCode(ErrorCode.CACHE_EXCEPTION_ERROR);
            baseResp.setDesc("缓存服务异常");
            logger.error("异常信息", e);
        }
        return baseResp;
    }

    /**
     * 根据指定key获取String
     *
     * @param baseReq
     * @return
     */
    public BaseResp<CacheBean> get(BaseReq<CacheBean> baseReq) {
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            if (checkKey(baseReq, baseResp)) {
                CacheBean bean = baseReq.getData();
                String value = stringRedisTemplate.opsForValue().get(bean.getKey());
                bean.setValue(value);
                baseResp.setData(bean);
                baseResp.setCode(BusiCode.SUCCESS);
                baseResp.setDesc("成功");
            }
        } catch (Exception e) {
            baseResp.setCode(ErrorCode.CACHE_EXCEPTION_ERROR);
            baseResp.setDesc("缓存服务异常");
            logger.error("异常信息", e);
        }
        return baseResp;
    }

    /**
     * 删除对应的value
     *
     * @param baseReq
     */
    public BaseResp<CacheBean> delete(BaseReq<CacheBean> baseReq) {
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            if (checkKey(baseReq, baseResp)) {
                CacheBean bean = baseReq.getData();
                if ((BusiCode.SUCCESS).equals(exists(baseReq).getCode())) {
                    stringRedisTemplate.delete(bean.getKey());
                }
                baseResp.setCode(BusiCode.SUCCESS);
                baseResp.setDesc("成功");
            }
        } catch (Exception e) {
            baseResp.setCode(ErrorCode.CACHE_EXCEPTION_ERROR);
            baseResp.setDesc("缓存服务异常");
            logger.error("异常信息", e);
        }
        return baseResp;
    }

    /**
     * 判断缓存中是否有对应的key
     *
     * @param baseReq
     * @return
     */
    public BaseResp<CacheBean> exists(BaseReq<CacheBean> baseReq) {
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            if (checkKey(baseReq, baseResp)) {
                CacheBean bean = baseReq.getData();
                boolean flag = stringRedisTemplate.hasKey(bean.getKey());
                if (flag) {
                    baseResp.setCode(BusiCode.SUCCESS);
                    baseResp.setDesc("成功");
                } else {
                    baseResp.setCode(ErrorCode.CACHE_SERVER_KEY_NULL_ERROR);
                    baseResp.setDesc("缓存服务器中键不存在");
                }
            }
        } catch (Exception e) {
            baseResp.setCode(ErrorCode.CACHE_EXCEPTION_ERROR);
            baseResp.setDesc("缓存服务异常");
            logger.error("异常信息", e);
        }
        return baseResp;
    }

    /**
     * 判断传入参数中键是否为空
     *
     * @param baseReq
     * @param baseResp
     * @return
     */
    private boolean checkKey(BaseReq<CacheBean> baseReq, BaseResp<CacheBean> baseResp) {
        CacheBean bean = baseReq.getData();
        if (bean == null || bean.getKey() == null || bean.getKey().trim().length() == 0) {
            baseResp.setCode(ErrorCode.CACHE_PARAM_KEY_NULL_ERROR);
            baseResp.setDesc("传入参数键为空");
            return false;
        }
        return true;
    }

    /**
     * 判断传入参数中键值是否为空
     *
     * @param baseReq
     * @param baseResp
     * @return
     */
    private boolean checkKeyValue(BaseReq<CacheBean> baseReq, BaseResp<CacheBean> baseResp) {
        CacheBean bean = baseReq.getData();
        if (bean == null || bean.getKey() == null || bean.getKey().trim().length() == 0) {
            baseResp.setCode(ErrorCode.CACHE_PARAM_KEY_NULL_ERROR);
            baseResp.setDesc("传入参数键为空");
            return false;
        }
        if (bean.getValue() == null || bean.getValue().trim().length() == 0) {
            baseResp.setCode(ErrorCode.CACHE_PARAM_VALUE_NULL_ERROR);
            baseResp.setDesc("传入参数值为空");
            return false;
        }
        return true;
    }
}
