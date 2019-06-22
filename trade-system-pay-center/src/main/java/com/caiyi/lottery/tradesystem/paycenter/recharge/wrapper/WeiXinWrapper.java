package com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IWeiXinRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.dto.RechDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("WeiXinWrapper")
public class WeiXinWrapper {
	@Autowired
	RedisClient redisClient;
	@Autowired
	NotifyService notifyService;
	
	@SuppressWarnings("unchecked")
	public RechDto addmoney(PayBean bean, IWeiXinRech weiXinRech){
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(bean.getUid()+"_RechRecordMap");
		Map<String, Map<String, String>> userRechRecordMap = (Map<String, Map<String, String>>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if(null == userRechRecordMap){
			userRechRecordMap = new HashMap<>();
		}
		Map<String, String> rechargeCache = new HashMap<String, String>();
		rechargeCache.put("flag", "0");
		rechargeCache.put("bankid", bean.getBankid()+"");
		rechargeCache.put("channel", bean.getChannel()+bean.getProduct()+bean.getKey());
		userRechRecordMap.put(bean.getUid()+"_weixin", rechargeCache);
		cacheBean.setKey(bean.getUid()+"_RechRecordMap");
		cacheBean.setValue(JSONObject.toJSONString(userRechRecordMap));
		cacheBean.setTime(10*Constants.TIME_MINUTE);
		redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
		return weiXinRech.addmoney(bean);
	}

	public void backNotify(PayBean bean, IWeiXinRech weiXinRech){
		weiXinRech.backNotify(bean);
		notifyService.defaultRechNotify(bean);
	}
}
