package com.caiyi.lottery.tradesystem.integralweb.service.impl;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import integral.bean.IntegralBean;
import com.caiyi.lottery.tradesystem.integralcenter.client.IntegralCenterInterface;
import com.caiyi.lottery.tradesystem.integralweb.service.IntergralCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegralCenterServiceImpl implements IntergralCenterService {
    @Autowired
    IntegralCenterInterface integralCenterInterface;

    @Override
    public BaseResp<IntegralBean> queryVipPointInfo(BaseReq<UserBean> bean) {
        return integralCenterInterface.queryVipPointInfo(bean);
    }

    @Override
    public BaseResp<IntegralBean> clickToSign(BaseReq<UserBean> bean) {
        return integralCenterInterface.clickToSign(bean);
    }

    @Override
    public BaseResp<IntegralBean> clickToGetPoints(BaseReq<UserBean> bean) {
        return integralCenterInterface.clickToGetPoints(bean);
    }

    @Override
    public BaseResp<Page> pointsDetail(BaseReq<UserBean> bean) {
        return integralCenterInterface.pointsDetail(bean);
    }

    @Override
    public BaseResp<Page> experienceDetail(BaseReq<UserBean> bean) {
        return integralCenterInterface.experienceDetail(bean);
    }

    @Override
    public BaseResp<IntegralBean> queryVipUserInfo(BaseReq<UserBean> bean) {
        return integralCenterInterface.queryVipUserInfo(bean);
    }
}
