package com.caiyi.lottery.tradesystem.tradecenter.service;

import trade.bean.TradeBean;
import trade.dto.CastDto;

//投注相关
public interface CastService {
	//app端发起方案投注
	public CastDto proj_cast_app(TradeBean bean);
	//app端发起竞彩方案投注
	public CastDto jproj_cast_app(TradeBean bean);
	//竞彩足球奖金优化投注
	public CastDto project_optimize_zq(TradeBean bean);
	//竞彩篮球奖金优化投注
	public CastDto project_optimize_lq(TradeBean bean);
}
