package com.caiyi.lottery.tradesystem.activitycenter.controller;

import activity.bean.ActivityBean;
import activity.dto.ForcastDTO;
import com.caiyi.lottery.tradesystem.activitycenter.service.ForcastService;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 拉新活动-预测比分
 *
 * @author GJ
 * @create 2018-04-23 10:03
 **/
@Slf4j
@RestController
public class ActivityForcastController {
    @Autowired
    private ForcastService forcastService;


    //###################

    /**
     * 历史邀请
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/invitation_history.api")
    public BaseResp<List<ForcastDTO>> invitationHistory(@RequestBody BaseReq<ActivityBean> activityReq) {
        ActivityBean bean = activityReq.getData();
        BaseResp<List<ForcastDTO>> baseResp = new BaseResp<>();
        List<ForcastDTO> forcastDTOList = forcastService.queryInvitationHistory(bean);
        baseResp.setCode(String.valueOf(bean.getBusiErrCode()));
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.SUCCESS)) {
            baseResp.setData(forcastDTOList);
        }
        return baseResp;
    }

    /**
     * 邀请详细
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/invitation_detail.api")
    public BaseResp<ForcastDTO> invitationDetail(@RequestBody BaseReq<ActivityBean> activityReq) {
        ActivityBean bean = activityReq.getData();
        BaseResp<ForcastDTO> baseResp = new BaseResp<>();
        ForcastDTO forcastDTO = forcastService.queryInvitationDetail(bean);
        baseResp.setCode(String.valueOf(bean.getBusiErrCode()));
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.SUCCESS)) {
            baseResp.setData(forcastDTO);
        }
        return baseResp;
    }

    /**
     * 预测首页
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/forcast.api")
    BaseResp<ForcastDTO> forcast(@RequestBody BaseReq<ActivityBean> activityReq) {
        ActivityBean bean = activityReq.getData();
        BaseResp<ForcastDTO> baseResp = new BaseResp<>();
        ForcastDTO forcastDTO = forcastService.forcast(bean);
        baseResp.setCode(String.valueOf(bean.getBusiErrCode()));
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.SUCCESS)) {
            baseResp.setData(forcastDTO);
        }
        return baseResp;
    }
}
