package com.caiyi.lottery.tradesystem.integralcenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import integral.bean.IntegralBean;

/**
 * 积分中心查询Service接口
 */
public interface IntegralCenterQueryService {

    public BaseResp<IntegralBean> sign(UserBean bean) throws Exception;

    public BaseResp<IntegralBean> integralCenterImage(UserBean bean) throws Exception;

    public BaseResp<IntegralBean> getUserPoints(UserBean bean) throws Exception;

    public BaseResp<Page> getExperienceDetail(UserBean bean) throws Exception;

    public BaseResp<Page> getPointsDetail(UserBean bean) throws Exception;

    public IntegralBean queryVipUserInfo(UserBean bean);


}
