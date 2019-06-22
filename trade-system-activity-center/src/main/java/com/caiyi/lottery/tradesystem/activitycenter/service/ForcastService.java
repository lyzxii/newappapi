package com.caiyi.lottery.tradesystem.activitycenter.service;

import activity.bean.ActivityBean;

import activity.dto.ForcastDTO;

import java.util.List;

/**
 * 拉新活动-预测比分Service
 *
 * @author GJ
 * @create 2018-04-23 10:05
 **/
public interface ForcastService {

    ForcastDTO forcastShareUserPage(ActivityBean activityBean);

    void shareForcast(ActivityBean activityBean);
    //###################
    /**
     * 查询邀请历史
     * @param bean
     * @return
     */
    List<ForcastDTO> queryInvitationHistory(ActivityBean bean);

    /**
     * 查询邀请详细
     * @param bean
     * @return
     */
    ForcastDTO queryInvitationDetail(ActivityBean bean);

    /**
     * 预测首页
     * @param bean
     * @return
     */
    ForcastDTO forcast(ActivityBean bean);
}
