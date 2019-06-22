package com.caiyi.lottery.tradesystem.paycenter.service;

import com.caiyi.lottery.tradesystem.BaseBean;

public interface PayBasicService {
    /**
     * 查询首次充值是否小于20
     * @param bean
     * @return
     * @throws Exception
     */
    Integer queryFirstIsLower20(BaseBean bean) throws Exception;

    Integer queryWhitelistStatus(BaseBean bean);
}
