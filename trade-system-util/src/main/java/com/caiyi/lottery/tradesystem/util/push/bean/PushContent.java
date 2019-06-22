package com.caiyi.lottery.tradesystem.util.push.bean;


import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class PushContent {
	private String messageId;
	private String messageType;
	private String title;
	private String content;
	private String action;
	private List<String> tags;
	private Date time;
	private String notify = PushConstant.PUSH_NOTIFY;
	private Map<String, Object> extras;
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getNotify() {
		return notify;
	}
	public void setNotify(String notify) {
		this.notify = notify;
	}
	public Map<String, Object> getExtras() {
		return extras;
	}
	public void setExtras(Map<String, Object> extras) {
		this.extras = extras;
	}
	
	public String getExtrasJson(){
		if(this.extras==null){
			this.extras = new HashMap<>();
		}
		JSONObject json = new JSONObject(this.extras);
		return json.toJSONString();
	}
	
	public String getJsonData(){
		JSONObject json = new JSONObject();
		json.put("id", messageId==null?"":messageId);
		json.put("type", messageType==null?"":messageType);
		json.put("title", title==null?"":title);
		json.put("content", content==null?"":content);
		json.put("action", action==null?"":action);
		json.put("tags", tags==null?new ArrayList<>():tags);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(null==time){
			time = new Date();
		}
		json.put("time", sdf.format(time));
		json.put("notify", notify);
		json.put("extras", extras==null?new HashMap<>():extras);
		return json.toJSONString();
	}
	public String getTagsString(){
		StringBuilder builder = new StringBuilder();
		if(tags==null){
			tags = new ArrayList<>();
		}
		for(int i=0;i<tags.size();i++){
			if(i==tags.size()-1){
				builder.append(tags.get(i));
			}else{
				builder.append(tags.get(i)).append(",");
			}
		}
		return builder.toString();
	}
}
