package com.caiyi.lottery.tradesystem.task;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.dao.PeriodMapper;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import data.pojo.PeriodPojo;
import data.utils.DataConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author GJ
 * @create 2018-03-29 15:24
 **/
@Slf4j
@Component
public class CacheTask {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private PeriodMapper periodMapper;

    //上一次执行完毕时间点之后5秒再执行
 //   @Scheduled(fixedDelay  = 1000*30)
    public void matchCachejc(){
       log.info("同步资料库竞彩对阵到缓存-开始");
        try {
            String jc_unfinish = retrieveDataByUrl(DataConstants.newzlk_football_jc_unfinish_new);
            CacheBean cacheBean = new CacheBean();
            cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_JC_UNFINISH);
            cacheBean.setTime(1000*60*5);
            cacheBean.setValue(jc_unfinish);
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);

            String jc_finish = retrieveDataByUrl(DataConstants.newzlk_football_jc_finish_new);
            cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_JC_FINISH);
            cacheBean.setTime(1000*60*5);
            cacheBean.setValue(jc_finish);
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);

        } catch (Exception e) {
            log.error("同步资料库竞彩对阵到缓存错误",e);
        }
        log.info("同步资料库竞彩对阵到缓存-结束");
    }

    //上一次执行完毕时间点之后5秒再执行
 //   @Scheduled(fixedDelay  = 1000*30)
    public void matchCachebd(){
        log.info("同步资料库北单对阵到缓存-开始");
        try {
            String bd_unfinish = retrieveDataByUrl(DataConstants.newzlk_football_bd_unfinish_new);
            CacheBean cacheBean = new CacheBean();
            cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_BD_UNFINISH);
            cacheBean.setTime(1000*60*5);
            cacheBean.setValue(bd_unfinish);
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);

            String bd_finish = retrieveDataByUrl(DataConstants.newzlk_football_bd_finish_new);
            cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_BD_FINISH);
            cacheBean.setTime(1000*60*5);
            cacheBean.setValue(bd_finish);
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);

        } catch (Exception e) {
            log.error("同步资料库北单对阵到缓存错误",e);
        }
        log.info("同步资料库北单对阵到缓存-结束");
    }


    //上一次执行完毕时间点之后5秒再执行
 //   @Scheduled(fixedDelay  = 1000*30)
    public void matchCachelc(){
        log.info("同步资料库篮球对阵到缓存-开始");
        try {
            String lc_unfinish = retrieveDataByUrl(DataConstants.newzlk_basketball_unfinish);
            CacheBean cacheBean = new CacheBean();
            cacheBean.setKey(DataConstants.NEWZLK_BASKETBALL_UNFINISH);
            cacheBean.setTime(1000*60*5);
            cacheBean.setValue(lc_unfinish);
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);

            String lc_finish = retrieveDataByUrl(DataConstants.newzlk_basketball_finish);
            cacheBean.setKey(DataConstants.NEWZLK_BASKETBALL_FINISH);
            cacheBean.setTime(1000*60*5);
            cacheBean.setValue(lc_finish);
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);

        } catch (Exception e) {
            log.error("同步资料库篮球对阵到缓存错误",e);
        }
        log.info("同步资料库篮球对阵到缓存-结束");
    }

    // private JXmlWrapper retrieveDataByUrl(String url) throws Exception {
    //     return JXmlWrapper.parseUrl(url, "", "utf-8", 10);
    // }

    private String retrieveDataByUrl(String url) throws Exception {
        HttpClient client = new HttpClient();
        GetMethod mothod = new GetMethod(url);
        mothod.getParams().setContentCharset("utf-8");

        String respStr = "";
        try {
            client.executeMethod(mothod); // 发送http请求
            respStr = mothod.getResponseBodyAsString();

            log.info("文件{}访问成功",url);
        } catch (Exception e) {
            log.error("文件{}访问失败", url);
        } finally {
            mothod.releaseConnection();
        }
        return respStr;
    }


    //上一次执行完毕时间点之后600秒再执行
    @Scheduled(fixedDelay  = 1000*600)
    public void setMpCode(){
        log.info("存储慢频开奖号码到缓存-开始");
        try {
            List<PeriodPojo> periodPojoList = periodMapper.getMpPeriod();
            setcache(periodPojoList);
        } catch (Exception e) {
            log.error("存储慢频开奖号码到缓存出错",e);
        }
        log.info("存储慢频开奖号码到缓存-结束");
    }

    //上一次执行完毕时间点之后30秒再执行
    @Scheduled(fixedDelay  = 1000*30)
    public void setKpCode(){
        log.info("存储快频开奖号码到缓存-开始");
        try {
            List<PeriodPojo> periodPojoList = periodMapper.getKpPeriod();
            setcache(periodPojoList);
        } catch (Exception e) {
            log.error("存储快频开奖号码到缓存出错",e);
        }
        log.info("存储快频开奖号码到缓存-结束");
    }

    /**
     * 保存缓存
     */
    public void  setcache( List<PeriodPojo> periodPojoList){
        CacheBean cacheBean = new CacheBean();
        cacheBean.setTime(1000*25);
        Set<String> gidSet = new HashSet<>();
        Map<String, Map<String, String>> gidMap = new HashMap<>();
        for (PeriodPojo periodPojo : periodPojoList) {
            gidSet.add(periodPojo.getGid());
            if(periodPojo.getAcode()==null){
                continue;
            }
            Map<String, String> pidMap = gidMap.get(periodPojo.getGid());
            if (pidMap == null) {
                pidMap = new HashMap<>();
            }
            pidMap.put(periodPojo.getPid(), periodPojo.getAcode());
            gidMap.put(periodPojo.getGid(), pidMap);
        }
        for (String gid : gidSet) {
            cacheBean.setKey("acode_"+gid);
            Map<String, String> acodeMap = (Map<String, String>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.TASK);
            if (acodeMap == null) {
                acodeMap = new HashMap<>();
            }
            acodeMap.putAll(gidMap.get(gid));
            JSONObject json = (JSONObject) JSONObject.toJSON(acodeMap);
            cacheBean.setValue(json.toJSONString());
            redisClient.setString(cacheBean, log, SysCodeConstant.TASK);
        }
    }
}
