package com.caiyi.lottery.tradesystem.activityweb.controller;

import activity.bean.ActivityBean;
import activity.dto.ForcastDTO;
import com.caiyi.lottery.tradesystem.activitycenter.client.ActivityInterface;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分享预测拉新活动
 * @author wxy
 * @create 2018-04-23 10:16
 **/
@Slf4j
@RestController
public class ActivityWebForcastController {
    @Autowired
    private ActivityInterface activityInterface;

    /**
     * 邀请历史
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/invitation_history.api")
    public Result invitationHistory(ActivityBean bean) {
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.ACTIVITYWEB);
        Result result = new Result();
        BaseResp<List<ForcastDTO>> baseResp = activityInterface.invitationHistory(baseReq);
        BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        return result;
    }

    /**
     * 邀请详情
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/invitation_detail.api")
    public Result invitationDetail(ActivityBean bean) {
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.ACTIVITYWEB);
        Result result = new Result();
        BaseResp<ForcastDTO> baseResp = activityInterface.invitationDetail(baseReq);
        BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        return result;
    }

    /**
     * 预测首页
     * @param bean
     * @return
     */
    @RequestMapping(value = "/activity/forcast.api")
    public Result forcast(ActivityBean bean) {
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.ACTIVITYWEB);
        Result result = new Result();
        BaseResp<ForcastDTO> baseResp = activityInterface.forcast(baseReq);
        BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        return result;
    }
}
