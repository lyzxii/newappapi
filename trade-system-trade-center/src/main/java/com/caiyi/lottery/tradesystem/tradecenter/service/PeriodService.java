package com.caiyi.lottery.tradesystem.tradecenter.service;

import trade.bean.TradeBean;

//彩种期次业务
public interface PeriodService {
	//查询数字彩期次信息
	public void querySZCPeriodEndTime(TradeBean bean); 
	//设置默认北单期次id
	public void setDefaultBeidanPid(TradeBean bean);
	//慢频截止日期提醒
	public String mpEndTimeReminder(TradeBean bean);
}	
