package com.caiyi.lottery.tradesystem.tradecenter.service;

import java.util.HashMap;

import com.caipiao.plugin.helper.CodeFormatException;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import trade.bean.TradeBean;

//投注内容业务类
public interface CodeService {
	//统计投注内容所需的钱
	int countCodesMoney(TradeBean bean) throws Exception;
	//保存投注内容到文件
	void saveCastCodeToFile(TradeBean bean) throws Exception;
	//检测投注时间
	void checkCodeCount(TradeBean bean) throws Exception;
	//获取游戏插件
	public GamePluginAdapter getGamePluginAdapter(TradeBean bean);
	//检测竞彩投注号码
	String checkJcCode(TradeBean bean) throws Exception;
	//快频慢频30s内是否有相同投注内容
	boolean getSameTicketKm(TradeBean bean);
	//足彩篮彩30s内是否有相同投注内容
	boolean getSameTicketZl(TradeBean bean);
	//投注检测codes值是否正确
	public boolean checkGame(TradeBean bean);
	//检测投注场次
	public void checkItem(String gid, JXmlWrapper xml, HashMap<String, Long> cvals, String gg) throws Exception;
	//检测竞彩足球奖金优化code
	public boolean checkZqOptimizeCode(TradeBean bean, FilterResult result);
	//刷新竞彩投注内容
	public void refreshJcNewCodes(TradeBean bean);
	//刷新篮彩投注内容
	public void refreshLcNewCodes(TradeBean bean);
	//检测篮彩奖金优化code
	public boolean checkLqOptimizeCode(TradeBean bean, FilterResult result);
}
