package com.caiyi.lottery.tradesystem.ordercenter.service;

import com.caiyi.lottery.tradesystem.base.BaseResp;
import order.bean.OrderBean;

/**
 * 过关
 * @author 571
 * @create 2018-1-5 16:49:44
 */
public interface PassService {

    /**
     * 过关统计
     * @param bean
     */
    BaseResp statPass(OrderBean bean) throws Exception;
}
