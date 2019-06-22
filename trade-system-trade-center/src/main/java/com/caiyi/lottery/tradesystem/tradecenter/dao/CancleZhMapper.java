package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Mapper;
import trade.bean.TradeBean;

@Mapper
public interface CancleZhMapper {

    void cancle_zhuihao(TradeBean bean);
}
