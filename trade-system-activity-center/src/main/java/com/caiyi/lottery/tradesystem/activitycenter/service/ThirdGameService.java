package com.caiyi.lottery.tradesystem.activitycenter.service;

import activity.bean.ActivityBean; /**
 * @author wxy
 * @create 2018-01-03 10:29
 **/
public interface ThirdGameService {
    /**
     * 记录第三方游戏登陆
     * @param bean
     */
    void gameRecordLogin(ActivityBean bean) throws Exception;
}
