package com.caiyi.lottery.tradesystem.tradecenter.service;

import com.alibaba.fastjson.JSONObject;
import trade.bean.TradeBean;
import trade.dto.CastDto;

import java.util.Map;

//交易业务大类
public interface TradeService {
	//发起数字彩投注
	CastDto pcast(TradeBean bean);
	
	//发起竞彩投注
	CastDto jcast(TradeBean bean);
	
	//竞彩足球奖金优化
	CastDto jczq_optimize_proj(TradeBean bean);
	
	//竞彩篮球奖金优化
	CastDto jclq_optimize_proj(TradeBean bean);

	String encodeBetInfo(TradeBean bean);

	String encodeJjyhBetInfo(TradeBean bean);

	void hmzhremind(TradeBean bean);

    String zcancel(TradeBean bean);

    void zcastnew(TradeBean bean);

	int checkBeforeBuy(TradeBean bean);

    Map<String,String> decodeBetInfo(TradeBean bean);

    Map<String,String> decodeJjyhBetInfo(TradeBean bean);
}
