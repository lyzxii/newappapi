package com.caiyi.lottery.tradesystem.tradecenter.service;

import trade.bean.TradeBean;

//基础通用
public interface BaseService {
	//检测是否禁止充值购买
	boolean checkBanActivity(TradeBean bean);
	//购买前检测
	boolean checkBeforeBuy(TradeBean bean);
	//获取用户白名单
	public boolean getUserWhiteGrade(TradeBean bean);
	//检测用户的红包是否可用
	public boolean checkUserRedpacket(TradeBean bean);
	//获取用户代理商id
	public boolean getUserAgentId(TradeBean bean);

	String setRequestUrl(TradeBean bean, String sessionId1, String sessionId2, String requestUrl) throws Exception;

    String setJjyhRequestUrl(TradeBean bean, String sessionId1, String sessionId2, String requestUrl) throws Exception;
}
