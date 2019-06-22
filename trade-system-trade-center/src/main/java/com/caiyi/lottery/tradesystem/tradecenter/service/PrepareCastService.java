package com.caiyi.lottery.tradesystem.tradecenter.service;


import trade.bean.TradeBean;
import trade.dto.PrepareCastDto;

public interface PrepareCastService {
    PrepareCastDto prepare4Pay(TradeBean bean) throws Exception;
}
