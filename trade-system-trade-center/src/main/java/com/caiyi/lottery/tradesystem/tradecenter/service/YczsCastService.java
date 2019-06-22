package com.caiyi.lottery.tradesystem.tradecenter.service;


import trade.bean.TradeBean;
import trade.dto.JcCastDto;

public interface YczsCastService {
    JcCastDto project_yczs_create(TradeBean bean) throws Exception;

    JcCastDto yczs_cast(TradeBean bean) throws Exception;
}
