package com.caiyi.lottery.tradesystem.redis.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.redis.service.RedisService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Redis控制层
 *
 * @author wjy
 * @create 2017-12-13 11:26
 */
@RestController
public class RedisController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisService redisService;

    private Logger logger = LoggerFactory.getLogger(RedisController.class);
    @RequestMapping(value = "/redis/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("缓存中心redis启动运行正常");
        return response;
    }


    @RequestMapping(value = "/redis/test.api")
    public Response checkLocalHealth1(String pa) {
        stringRedisTemplate.opsForValue().set("test", pa, 1000, TimeUnit.SECONDS);
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("测试数据");
        return response;
    }

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
    public BaseResp<CacheBean> setString(@RequestBody BaseReq<CacheBean> baseReq) {
        long startTime = System.currentTimeMillis();
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            baseResp = redisService.set(baseReq);
            if ((BusiCode.SUCCESS).equals(baseResp.getCode())) {
                logger.info("存储数据成功 key=" + baseReq.getData().getKey());
            } else {
                logger.error("存储数据失败 " + baseReq.toJson());
            }
        } catch (Exception e) {
            baseResp.setCode(ErrorCode.CACHE_EXCEPTION_ERROR);
            baseResp.setDesc("存取缓存异常");
            logger.error("存储数据失败 key=" + baseReq.getData().getKey(), e);
        }
        long endTime = System.currentTimeMillis();
        float excTime=endTime-startTime;
        logger.info("setString 执行时间:"+excTime+"ms key:"+baseReq.getData().getKey());
        return baseResp;
    }

    /**
     * 根据指定key获取字符串
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/getString.api")
    public BaseResp<CacheBean> getString(@RequestBody BaseReq<CacheBean> baseReq) {
        return get(baseReq);
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
    @RequestMapping(value = "/redis/getJsonString.api")
    public BaseResp<CacheBean> getJsonString(@RequestBody BaseReq<CacheBean> baseReq) {
        return get(baseReq);
    }

    /**
     * 根据指定key获取xml字符串
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/getXmlString.api")
    public BaseResp<CacheBean> getXmlString(@RequestBody BaseReq<CacheBean> baseReq) {
        return get(baseReq);
    }

    /**
     * 删除指定key的数据
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/delete.api")
    public BaseResp<CacheBean> delete(@RequestBody BaseReq<CacheBean> baseReq) {
        long startTime = System.currentTimeMillis();
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            baseResp = redisService.delete(baseReq);
            if ((BusiCode.SUCCESS).equals(baseResp.getCode())) {
                logger.info("删除数据成功 key=" + baseReq.getData().getKey());
            } else {
                logger.error("删除数据失败 " + baseReq.toJson());
            }
        } catch (Exception e) {
            logger.error("删除数据失败 key=" + baseReq.getData().getKey(), e);
        }
        long endTime = System.currentTimeMillis();
        float excTime=endTime-startTime;
        logger.info("delete 执行时间:"+excTime+"ms key:"+baseReq.getData().getKey());
        return baseResp;
    }

    /**
     * 判断指定key是否存在
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/redis/exists.api")
    public BaseResp<CacheBean> exists(@RequestBody BaseReq<CacheBean> baseReq) {
        long startTime = System.currentTimeMillis();
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            baseResp = redisService.exists(baseReq);
            if ((BusiCode.SUCCESS).equals(baseResp.getCode())) {
                logger.info("键存在 key=" + baseReq.getData().getKey());
            } else {
                logger.info("键不存在 key=" + baseReq.getData().getKey());
            }
        } catch (Exception e) {
            logger.error("判断键存在异常 key=" + baseReq.getData().getKey(), e);
        }
        long endTime = System.currentTimeMillis();
        float excTime=endTime-startTime;
        logger.info("exists 执行时间:"+excTime+"ms key:"+baseReq.getData().getKey());
        return baseResp;
    }

    /**
     * 根据指定key获取字符串
     *
     * @param baseReq
     * @return
     */
    private BaseResp<CacheBean> get(BaseReq<CacheBean> baseReq) {
        long startTime = System.currentTimeMillis();
        BaseResp<CacheBean> baseResp = new BaseResp<CacheBean>();
        try {
            baseResp = redisService.get(baseReq);
            if ((BusiCode.SUCCESS).equals(baseResp.getCode())) {
                logger.info("取数据成功 key=" + baseReq.getData().getKey());
            } else {
                logger.error("取数据失败 " + baseReq.toJson());
            }
        } catch (Exception e) {
            baseResp.setCode(ErrorCode.CACHE_EXCEPTION_ERROR);
            baseResp.setDesc("取数据异常");
            logger.error("取数据失败 key=" + baseReq.getData().getKey(), e);
        }
        long endTime = System.currentTimeMillis();
        float excTime=endTime-startTime;
        logger.info("get 执行时间:"+excTime+"ms key:"+baseReq.getData().getKey());
        return baseResp;
    }
}
