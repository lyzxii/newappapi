package com.caiyi.lottery.tradesystem.tradecenter.service;


import trade.bean.TradeBean;
import trade.dto.JcCastDto;

public interface FollowCastService {

    JcCastDto followcast(TradeBean bean) throws Exception;

    JcCastDto fgpcast(TradeBean bean) throws Exception;
}
