package com.caiyi.lottery.tradesystem.util.push.impl;

import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.push.IPush;
import com.caiyi.lottery.tradesystem.util.push.bean.PushConstant;
import com.caiyi.lottery.tradesystem.util.push.bean.PushContent;
import com.caiyi.lottery.tradesystem.util.push.bean.PushResult;
import com.gexin.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.base.uitls.AppConditions.OptType;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GtPush implements IPush {
	
    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    private static final String appId = "4FBbrNHcuH8ndnTh0j5W78";
    private static final String appKey = "ikOFXqs3yK8MAUXugSGdV1";
    private static final String masterSecret = "lrsY0NDz3M9NIoNMTTvuI9";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
    
	private Logger logger = LoggerFactory.getLogger("GtPush");
    
	@Override
	public List<String> queryTag(String id, String packageName) {
		IGtPush push = new IGtPush(host, appKey, masterSecret);
	    IPushResult result = push.getUserTags(appId, id);
	    Map<String, Object> respRet = result.getResponse();
	    String tags = (String) respRet.get("tags");
	    if(StringUtil.isEmpty(tags)){
	    	tags = "";
	    }
	    String[] tagArr = tags.split("\\s");
	    LinkedList<String> tagList = new LinkedList<>();
	    Collections.addAll(tagList, tagArr);
	    logger.info("GTPush--queryTag id:"+id+" resultMap:"+respRet);
	    return tagList;
	}

	@Override
	public PushResult pushByTag(List<String> tagList, String content, String messageType, String condition) {
        TransmissionTemplate template = transmissionTemplate(content);
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setOffline(true);
        int offlineExpireTime = PushConstant.MESSAGE_TIME_MAP.get(messageType);
        //离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(offlineExpireTime);
        
        List<String> appIdList = new ArrayList<String>();
        appIdList.add(appId);
        message.setAppIdList(appIdList);
        
        //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions();
        OptType optType = OptType.and;
        if(condition.equals(PushConstant.OR)){
        	optType = OptType.or;
        }
        cdt.addCondition(AppConditions.TAG, tagList,optType);
        message.setConditions(cdt);
        
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        IPushResult ret = push.pushMessageToApp(message);
        Map<String, Object> resultMap = ret.getResponse();
	    logger.info("GTPush--pushByTag tagList:"+tagList+"content:"+content+" resultMap:"+resultMap);
        String result = (String) resultMap.get("result");
        PushResult pushResult = new PushResult();
        if("ok".equalsIgnoreCase(result)){
        	pushResult.setResult(PushResult.SUCCESS);
        	String taskId = (String)resultMap.get("contentId");
            pushResult.setTaskId(taskId);
        }else{
        	pushResult.setResult(PushResult.FAIL);
            pushResult.setReason(result);
        }
		return pushResult;
	}
	
	@Override
	public PushResult pushByUserName(String userName, String content, String messageType) {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        TransmissionTemplate template =  transmissionTemplate(content);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        int offlineExpireTime = PushConstant.MESSAGE_TIME_MAP.get(messageType);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(offlineExpireTime);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0); 
        Target target = new Target();
        target.setAppId(appId);
        target.setAlias(userName);
        IPushResult ret = push.pushMessageToSingle(message, target);
        Map<String, Object> resultMap = ret.getResponse();
        String result = (String) resultMap.get("result");
        String taskid = (String) resultMap.get("taskId");
	    logger.info("GTPush--pushByUserName userName:"+userName+"content:"+content+" resultMap:"+resultMap);
	    PushResult pushResult = new PushResult();
        if("ok".equalsIgnoreCase(result)){
        	pushResult.setResult(PushResult.SUCCESS);
            pushResult.setTaskId(taskid);
        }else{
        	pushResult.setResult(PushResult.FAIL);
            pushResult.setReason(result);
        }
	    return pushResult;
	}
	
	@Override
	public PushResult pushByUserNames(List<String> userNames, String content, String messageType) {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        TransmissionTemplate template =  transmissionTemplate(content);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        int offlineExpireTime = PushConstant.MESSAGE_TIME_MAP.get(messageType);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(offlineExpireTime);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0); 
        List<Target> targetList = new ArrayList<>();
        for(String userName:userNames){
        	Target target = new Target();
            target.setAppId(appId);
            target.setAlias(userName);
            targetList.add(target);
        }
        IPushResult ret = push.pushMessageToList(content, targetList);
        Map<String, Object> resultMap = ret.getResponse();
        String result = (String) resultMap.get("result");
        String taskid = (String) resultMap.get("taskId");
	    logger.info("GTPush--pushByUserNames userNames:"+userNames+"content:"+content+" resultMap:"+resultMap);
	    PushResult pushResult = new PushResult();
        if("ok".equalsIgnoreCase(result)){
        	pushResult.setResult(PushResult.SUCCESS);
            pushResult.setTaskId(taskid);
        }else{
        	pushResult.setResult(PushResult.FAIL);
            pushResult.setReason(result);
        }
	    return pushResult;
	}

	public static TransmissionTemplate transmissionTemplate(String content) {
	    TransmissionTemplate template = new TransmissionTemplate();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
	    template.setTransmissionType(2);
	    template.setTransmissionContent(content);
	    APNPayload payload = new APNPayload();
	    payload.setAlertMsg(getDictionaryAlertMsg(content));
	    payload.addCustomMsg("9188other", content);
	    template.setAPNInfo(payload);
	    return template;
	}
	
	private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(String content){
	    APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
	    JSONObject json = JSONObject.parseObject(content);
	    String body = json.getString("content");
	    String title = json.getString("title");
	    alertMsg.setBody(body);
	    // iOS8.2以上版本支持
	    alertMsg.setTitle(title);
	    return alertMsg;
	}
	
	
	public static void main(String[] args){
		PushContent content = new PushContent();
		content.setAction(PushConstant.OPERATION_OPEN_EXPLORER);
		content.setContent("test");
		content.setMessageType(PushConstant.MESSAGE_AWARD_MORE);
		List<String> tags = new ArrayList<>();
		tags.add("android");
		tags.add("ios");
		content.setTags(tags);
		content.setTitle("title");
		Map<String, Object> extras = new HashMap<>();
		extras.put("userName", "xcw168");
		extras.put("link", "123456");
		content.setExtras(extras);
		System.out.println("extrasJson:"+content.getExtrasJson());
		System.out.println("message:"+content.getJsonData());
		System.out.println("tags:"+content.getTagsString());
		GtPush push = new GtPush();
		PushResult result = push.pushByTag(tags, content.getJsonData(), "22", "0");
		System.out.println(result.toString());
//		System.out.println("extrasJson:"+content.getExtrasJson());
//		System.out.println("message:"+content.getJsonData());
//		System.out.println("tags:"+content.getTagsString());
	}

//	@Override
//	public PushResult setTag(String id, List<String> tagList) {
//        IGtPush push = new IGtPush(host, appKey, masterSecret);
//        IQueryResult ret = push.setClientTag(appId, id, tagList);
//        Map<String, Object> resultMap = ret.getResponse();
//        logger.info("GTPush--setTag id:"+id+" tagList:"+tagList+" resultMap:"+resultMap);
//        String result = (String) resultMap.get("result");
//        PushResult pushResult = new PushResult();
//        if("ok".equalsIgnoreCase(result)){
//        	pushResult.setResult(PushResult.SUCCESS);
//        }else{
//        	pushResult.setResult(PushResult.FAIL);
//        	pushResult.setReason(result);
//        }
//		return pushResult;
//	}
//
//	@Override
//	public PushResult deleteTag(String id, String tagName) {
//		List<String> tagList = queryTag(id);
//		tagList.remove(tagName);
//		PushResult pushResult = setTag(id, tagList);
//        logger.info("GTPush--deleteTag id:"+id+" tagName:"+tagName+" result:"+pushResult.getResult());
//		return pushResult;
//	}
//
//	@Override
//	public PushResult addTag(String id, String tagName) {
//		List<String> tagList = queryTag(id);
//		tagList.add(tagName);
//		PushResult pushResult = setTag(id, tagList);
//		logger.info("GTPush--addTag id:"+id+" tagName:"+tagName+" result:"+pushResult.getResult());
//		return pushResult;
//	}
}
