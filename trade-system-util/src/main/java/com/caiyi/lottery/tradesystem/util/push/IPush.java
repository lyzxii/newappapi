package com.caiyi.lottery.tradesystem.util.push;


import com.caiyi.lottery.tradesystem.util.push.bean.PushResult;

import java.util.List;

public interface IPush {
	//查询用户tag
	public List<String> queryTag(String id, String packageName);
	//根据标签推送
	public PushResult pushByTag(List<String> tagList, String content, String messageType, String condition);
	//根据用户名推送
	public PushResult pushByUserName(String userName, String content, String messageType);
	//根据用户名推送
	public PushResult pushByUserNames(List<String> userNames, String content, String messageType);
//	//设置tag
//	public PushResult setTag(String id,List<String> tagList);
//	//删除tag
//	public PushResult deleteTag(String id,String tagName);
//	//添加tag
//	public PushResult addTag(String id,String tagName);
}
