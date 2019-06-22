package com.caiyi.lottery.tradesystem.ordercenter.service;

import order.bean.ChaseNumberPage;
import order.bean.OrderBean;

/**
 * 追号相关Service
 *
 * @author GJ
 * @create 2017-12-27 14:04
 **/
public interface ChaseNumberService {

    /**
     * 追号记录详情
     */
     ChaseNumberPage getChaseNumberRecord(OrderBean bean);
}
