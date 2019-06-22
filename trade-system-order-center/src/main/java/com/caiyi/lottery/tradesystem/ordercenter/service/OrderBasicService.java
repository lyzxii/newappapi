package com.caiyi.lottery.tradesystem.ordercenter.service;

import order.bean.OrderBean;

public interface OrderBasicService {
    /**
     * 查询投注人数
     * @param bean
     * @return
     */
    Integer queryBetNum(OrderBean bean);
}
