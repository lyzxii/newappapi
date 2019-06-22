package com.caiyi.lottery.tradesystem.redis.util;

import org.slf4j.Logger;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;

//缓存使用工具类
public class CacheUtil {
	public static boolean setString(CacheBean bean, Logger log, RedisInterface redisInterface, String syscode){
		BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
		BaseResp<CacheBean> resp = redisInterface.setString(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			return true;
		}else{
			log.info("设置缓存至缓存中心失败,key:"+bean.getKey()+" value:"+bean.getValue()+
					" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return false;
		}
	}
	
	public static String getString(CacheBean bean, Logger log, RedisInterface redisInterface, String syscode){
		BaseReq<CacheBean> req = new BaseReq<>(bean, syscode);
		BaseResp<CacheBean> resp = redisInterface.getString(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			CacheBean cacheBean = resp.getData();
			if(cacheBean == null){
				log.info("getString 缓存key为:"+bean.getKey()+"的缓存值为空");
				return "";
			}else{
				return cacheBean.getValue();
			}
		}else{
			log.info("从缓存中心获取String失败,key:"+bean.getKey()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return "";
		}
	}
	
	public static String getJsonString(CacheBean bean, Logger log, RedisInterface redisInterface, String syscode){
		BaseReq<CacheBean> req = new BaseReq<>(bean, syscode);
		BaseResp<CacheBean> resp = redisInterface.getJsonString(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			CacheBean cacheBean = resp.getData();
			if(cacheBean == null){
				log.info("getJsonString 缓存key为:"+bean.getKey()+"的缓存值为空");
				return "";
			}else{
				return cacheBean.getValue();
			}
		}else{
			log.info("从缓存中心获取JsonString失败,key:"+bean.getKey()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return "";
		}
	}
	
	public static String getXmlString(CacheBean bean, Logger log, RedisInterface redisInterface, String syscode){
		BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
		BaseResp<CacheBean> resp = redisInterface.getXmlString(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			CacheBean cacheBean = resp.getData();
			if(cacheBean == null){
				log.info("getXmlString 缓存key为:"+bean.getKey()+"的缓存值为空");
				return "";
			}else{
				return cacheBean.getValue();
			}
		}else{
			log.info("从缓存中心获取XmlString失败,key:"+bean.getKey()+" value:"+bean.getValue()+
					" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return "";
		}
	}
	
	public static boolean delete(CacheBean bean, Logger log, RedisInterface redisInterface, String syscode){
		BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
		BaseResp<CacheBean> resp = redisInterface.delete(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			return true;
		}else{
			log.info("缓存中心删除key:"+bean.getKey()+"失败  code:"+resp.getCode()+" desc:"+resp.getDesc());
			return false;
		}
	}
	
	public static boolean exists(CacheBean bean, Logger log, RedisInterface redisInterface, String syscode){
		BaseReq<CacheBean> req = new BaseReq<>(bean,syscode);
		BaseResp<CacheBean> resp = redisInterface.getString(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			return true;
		}else{
			log.info("缓存中心判断key是否存在失败,key:"+bean.getKey()+"失败 code:"+resp.getCode()+" desc:"+resp.getDesc());
			return false;
		}
	}
}
