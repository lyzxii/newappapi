package com.caiyi.lottery.tradesystem.activitycenter.service;

import activity.bean.ActivityBean;
/**
 * @author wxy
 * @create 2017-12-27 11:45
 **/
public interface ActivityRedPacketService {
    /**
     * 获取新用户88元活动红包验证
     * @param bean
     * @throws Exception
     */
    void getEightyRedPacketCheck(ActivityBean bean) throws Exception;
}
