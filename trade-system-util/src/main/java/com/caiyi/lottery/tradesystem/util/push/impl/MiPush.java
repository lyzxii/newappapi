package com.caiyi.lottery.tradesystem.util.push.impl;

import com.caiyi.lottery.tradesystem.util.push.IPush;
import com.caiyi.lottery.tradesystem.util.push.bean.PushConstant;
import com.caiyi.lottery.tradesystem.util.push.bean.PushResult;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;
import com.xiaomi.push.sdk.ErrorCode;
import com.xiaomi.xmpush.server.*;
import com.xiaomi.xmpush.server.Sender.BROADCAST_TOPIC_OP;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

//小米推送只对安卓设备使用,message只构建了安卓，如需添加ios需添加使用ios的消息构建
public class MiPush implements IPush {
	
    private static final String AppID = "2000240";
    private static final String AppKey = "340200049240";
    private static final String AppSecret = "mU+LoTd2diMGpHGz4W9qbw==";
    
	private Logger logger = LoggerFactory.getLogger("MiPush");
    

	@Override
	public List<String> queryTag(String id, String packageName) {
		LinkedList<String> tagList = new LinkedList<>();
		DevTools tools = new DevTools(AppSecret);
		String response;
		try {
			response = tools.getTopicsOf(packageName, id, 3);
			logger.info("MiPush--queryTag id:"+id+" resposne:"+response);
		    JSONObject json = JSONObject.parseObject(response);
		    String result = json.getString("result");
		    if("ok".equalsIgnoreCase(result)){
		    	JSONObject data = json.getJSONObject("data");
		    	JSONArray tagListJson = data.getJSONArray("list");
		    	Object[] tagArr = tagListJson.toArray();
		    	for(Object tagObj : tagArr){
		    		String tag = (String)tagObj;
		    		tagList.add(tag);
		    	}
		    }
		} catch (IOException e) {
			logger.error("MiPush查询标签出错,id:"+id,e);
		}
	    return tagList;
	}

	@Override
	public PushResult pushByTag(List<String> tagList, String content, String messageType, String condition) {
		PushResult pushResult = new PushResult();
		try {
			Constants.useOfficial();
			Message message = constructMessage(content,messageType);
			Sender sender = new Sender(AppSecret);
			Result miResult = null;
			if(tagList.size()==0){//推送全部
				miResult = sender.broadcastAll(message, 3);
			}else if(tagList.size()==1){//单个推送
				String tag = tagList.get(0);
				miResult = sender.broadcast(message, tag, 3);
			}else{//多个推送
				BROADCAST_TOPIC_OP option = BROADCAST_TOPIC_OP.INTERSECTION;
				if(condition.equals(PushConstant.OR)){
					option = BROADCAST_TOPIC_OP.UNION;
				}
				miResult = sender.multiTopicBroadcast(message, tagList, option, 3);
			}
			logger.info("MiPush--pushByTag tagList:"+tagList+"content:"+content+" result:"+miResult.toString());
			ErrorCode errorCode = miResult.getErrorCode();
			int code = errorCode.getValue();
			if(0==code){
				pushResult.setResult(PushResult.SUCCESS);
				String messageId = miResult.getMessageId();
				pushResult.setTaskId(messageId);
			}else{
				pushResult.setResult(PushResult.FAIL);
				pushResult.setReason(miResult.getReason());
			}
		} catch (IOException | ParseException e) {
			logger.error("Mipush根据Tag发送推送失败,内容:"+content+" tagList:"+tagList+" messageType:"+messageType+" condition:"+condition,e);
		}
		return pushResult;
	}
	
	@Override
	public PushResult pushByUserName(String userName, String content, String messageType) {
		PushResult pushResult = new PushResult();
		try {
			Constants.useOfficial();
			Message message = constructMessage(content,messageType);
			Sender sender = new Sender(AppSecret);
			Result miResult = sender.sendToAlias(message, userName, 3);
			logger.info("MiPush--pushByUserName userName:"+userName+"content:"+content+" result:"+miResult.toString());
			ErrorCode errorCode = miResult.getErrorCode();
			int code = errorCode.getValue();
			if(0==code){
				pushResult.setResult(PushResult.SUCCESS);
				String messageId = miResult.getMessageId();
				pushResult.setTaskId(messageId);
			}else{
				pushResult.setResult(PushResult.FAIL);
				pushResult.setReason(miResult.getReason());
			}
		} catch (IOException | ParseException e) {
			logger.error("Mipush根据用户名发送推送失败,内容:"+content+" username:"+userName+" messageType:"+messageType,e);
		}
	    return pushResult;
	}
	
	@Override
	public PushResult pushByUserNames(List<String> userNames, String content, String messageType) {
		PushResult pushResult = new PushResult();
		try {
			Constants.useOfficial();
			Message message = constructMessage(content,messageType);
			Sender sender = new Sender(AppSecret);
			Result miResult = sender.sendToAlias(message, userNames, 3);
			logger.info("MiPush--pushByUserNames userNames:"+userNames+"content:"+content+" result:"+miResult.toString());
			ErrorCode errorCode = miResult.getErrorCode();
			int code = errorCode.getValue();
			if(0==code){
				pushResult.setResult(PushResult.SUCCESS);
				String messageId = miResult.getMessageId();
				pushResult.setTaskId(messageId);
			}else{
				pushResult.setResult(PushResult.FAIL);
				pushResult.setReason(miResult.getReason());
			}
		} catch (IOException | ParseException e) {
			logger.error("Mipush根据用户名列表发送推送失败,内容:"+content+" usernames:"+userNames+" messageType:"+messageType,e);
		}
	    return pushResult;
	}

	//构造推送消息对象(只构造安卓消息)
	private Message constructMessage(String content, String messageType) {
		int offlineExpireTime = PushConstant.MESSAGE_TIME_MAP.get(messageType);
		Message message = new Message.Builder().
				payload(content).
				passThrough(1).
				timeToLive(offlineExpireTime).
				build();
		return message;
	}
}
