package com.caiyi.lottery.tradesystem.util.push;


import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.push.bean.PushChannel;
import com.caiyi.lottery.tradesystem.util.push.bean.PushConstant;
import com.caiyi.lottery.tradesystem.util.push.bean.PushResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class PushUtil {
	
	private Logger logger;
	
	public PushUtil() {
		logger = LoggerFactory.getLogger("pushUtil");
	}
	
	//查询tag
	public Set<String> queryTag(Map<String, String> idMap, String packageName){
		Set<String> tagSet = new HashSet<>();
		logger.info("查询用户Tag,idMap:"+idMap);
		PushChannel[] pushChannelArr = PushChannel.values();
		for(PushChannel pushChannel:pushChannelArr){
			String key = pushChannel.getKey();
			IPush push = pushChannel.getPush();
			String id = idMap.get(key);
			if(StringUtil.isEmpty(id)){
				continue;
			}
			List<String> tagList = push.queryTag(id,packageName);
			tagSet.addAll(tagList);
			logger.info("添加推送tag,key:"+key+" id:"+id+" tagList:"+tagList.toString());
		}
		logger.info("查询用户tag idMap:"+idMap+" tagSet:"+tagSet.toString());
		return tagSet;
	}
	
	//根据tag推送
	public Map<String, PushResult> pushByTag(List<String> tagList, String content, String messageType, String condition){
		logger.info("根据Tag进行推送,tagList:"+tagList.toString()+" content:"+content);
		Map<String, PushResult> channelResult = new HashMap<>();
		PushChannel[] pushChannelArr = PushChannel.values();
		for(PushChannel pushChannel:pushChannelArr){
			String key = pushChannel.getKey();
			IPush push = pushChannel.getPush();
			PushResult pushResult = push.pushByTag(tagList, content, messageType, condition);
			channelResult.put(key, pushResult);
			logger.info("根据tag推送,key:"+key+" content:"+content+" tagList:"+tagList.toString()+" result:"+pushResult.getResult());
		}
		return channelResult;
	}
	
	//根据用户名推送
	public Map<String, PushResult> pushByUserName(String userName, String content, String messageType){
		logger.info("根据用户名进行推送,用户名:"+userName+" 推送内容:"+content);
		Map<String, PushResult> channelResult = new HashMap<>();
		PushChannel[] pushChannelArr = PushChannel.values();
		for(PushChannel pushChannel:pushChannelArr){
			String key = pushChannel.getKey();
			IPush push = pushChannel.getPush();
			PushResult pushResult = push.pushByUserName(userName, content, messageType);
			channelResult.put(key, pushResult);
			logger.info("根据用户名推送,key:"+key+" content:"+content+" userName:"+userName+" result:"+pushResult.getResult());
		}
		return channelResult;
	}
	
	public static void main(String[] args) {
		PushUtil pushUtil = new PushUtil();
		pushUtil.pushByTag(new ArrayList<String>(), "{123}", PushConstant.MESSAGE_LOTTERY_NOTICE, "0");
	}
	
	//设置tag
//	public boolean setTag(Map<String, String> idMap, List<String> tagList){
//		logger.info("设置推送Tag,idMap:"+idMap+" tagList:"+tagList.toString());
//		boolean flag = false;
//		PushChannel[] pushChannelArr = PushChannel.values();
//		for(PushChannel pushChannel:pushChannelArr){
//			String key = pushChannel.getKey();
//			IPush push = pushChannel.getPush();
//			String id = idMap.get(key);
//			if(StringUtil.isEmpty(id)){
//				continue;
//			}
//			PushResult pushResult = push.setTag(id, tagList);
//			if(PushResult.SUCCESS.equalsIgnoreCase(pushResult.getResult())){//有一个设置成功即为成功
//				flag = true;
//			}
//			logger.info("推送设置tag,key:"+key+" id:"+id+" result:"+pushResult.getResult());
//		}
//		return flag;
//	}
//	
//	//删除tag
//	public boolean deleteTag(Map<String, String> idMap, String tagName){
//		logger.info("删除推送Tag,idMap:"+idMap+" tagName:"+tagName.toString());
//		boolean flag = false;
//		PushChannel[] pushChannelArr = PushChannel.values();
//		for(PushChannel pushChannel:pushChannelArr){
//			String key = pushChannel.getKey();
//			IPush push = pushChannel.getPush();
//			String id = idMap.get(key);
//			if(StringUtil.isEmpty(id)){
//				continue;
//			}
//			PushResult pushResult = push.deleteTag(id, tagName);
//			if(PushResult.SUCCESS.equalsIgnoreCase(pushResult.getResult())){
//				flag = true;
//			}
//			logger.info("删除推送tag,key:"+key+" id:"+id+" result:"+pushResult.getResult()+" tagName:"+tagName);
//		}
//		return flag;
//	}
//	
//	//添加tag
//	public boolean addTag(Map<String, String> idMap, String tagName){
//		logger.info("添加推送Tag,idMap:"+idMap+" tagName:"+tagName.toString());
//		boolean flag = false;
//		PushChannel[] pushChannelArr = PushChannel.values();
//		for(PushChannel pushChannel:pushChannelArr){
//			String key = pushChannel.getKey();
//			IPush push = pushChannel.getPush();
//			String id = idMap.get(key);
//			if(StringUtil.isEmpty(id)){
//				continue;
//			}
//			PushResult pushResult = push.addTag(id, tagName);
//			if(PushResult.SUCCESS.equals(pushResult.getResult())){
//				flag = true;
//			}
//			logger.info("添加推送tag,key:"+key+" id:"+id+" result:"+pushResult.getResult()+" tagName:"+tagName);
//		}
//		return flag;
//	}
}
	