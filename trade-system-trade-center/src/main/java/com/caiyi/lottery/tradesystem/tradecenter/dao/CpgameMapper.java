package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Mapper;

import trade.bean.TradeBean;

//对应cpgame存储过程
@Mapper
public interface CpgameMapper {
	void t_proj_cast(TradeBean bean);

	void t_cast_zh(TradeBean bean);
}
