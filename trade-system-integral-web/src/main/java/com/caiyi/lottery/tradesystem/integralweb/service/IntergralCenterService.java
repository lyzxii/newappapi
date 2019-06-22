package com.caiyi.lottery.tradesystem.integralweb.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import integral.bean.IntegralBean;

public interface IntergralCenterService {

    BaseResp<IntegralBean> queryVipPointInfo(BaseReq<UserBean> bean);

    BaseResp<IntegralBean> clickToSign(BaseReq<UserBean> bean);

    BaseResp<IntegralBean> clickToGetPoints(BaseReq<UserBean> bean);

    BaseResp<Page> pointsDetail(BaseReq<UserBean> bean);

    BaseResp<Page> experienceDetail(BaseReq<UserBean> bean);

    BaseResp<IntegralBean> queryVipUserInfo(BaseReq<UserBean> bean);

}
