package com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.dto.RechDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("BankCardWrapper")
public class BankCardWrapper {
	
	@Autowired
	RedisClient redisClient;
	@Autowired
	RechService rechService;
	@Autowired
	NotifyService notifyService;
	
	@SuppressWarnings("unchecked")
	public RechDto addmoney(PayBean bean, IBankCardRech bankCardRech){
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
		userRechRecordMap.put(bean.getUid()+"_"+bean.getBankCode()+bean.getCardtype(), rechargeCache);
		cacheBean.setKey(bean.getUid()+"_RechRecordMap");
		cacheBean.setValue(JSONObject.toJSONString(userRechRecordMap));
		cacheBean.setTime(10*Constants.TIME_MINUTE);//10分钟
		redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
		try {
			rechService.recordBankCardInfo(bean);
			if(bean.getBusiErrCode()!=0){
				return null;
			}
		} catch (Exception e) {
			if(bean.getBusiErrCode()==0){
				bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_ADDMONEY_EXCEPTION));
				bean.setBusiErrDesc("订单异常,请稍后重试");
			}
			log.error("记录保存用户银行卡失败,用户名:"+bean.getUid()+" bankid:"+bean.getBankid()+" 银行卡:"+bean.getCardNo()+" safeKey:"+bean.getSafeKey(),e);
			return null;
		}
		return bankCardRech.addmoney(bean);
	}

	public void backNotify(PayBean bean, IBankCardRech bankCardRech){
		bankCardRech.backNotify(bean);
		try {
			notifyService.defaultBankCardNotify(bean);
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
			bean.setBusiErrDesc("回调出错");
			log.error("类" + bean.getClassName() + "订单号[" + bean.getApplyid() + "]回调出错==", e);
			throw new RuntimeException("回调出错" + e);
		}
	}
}
