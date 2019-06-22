package com.caiyi.lottery.tradesystem.activitycenter.service;

import activity.bean.ActivityBean;
import activity.dto.GetBonusDTO;
import activity.dto.TtfqDetailDTO;
import activity.dto.TtfqHomePageDTO;
import activity.dto.TtfqPage;

import java.util.List;

/**
 * @author wxy
 * @create 2017-12-28 15:40
 **/
public interface ActivityTtfqService {
    /**
     * 天天分钱活动领取奖金
     * @param bean
     * @return
     */
    GetBonusDTO getBonus(ActivityBean bean) throws Exception;

    /**
     * 天天分钱首页
     * @param bean
     * @return
     * @throws Exception
     */
    TtfqPage<List<TtfqHomePageDTO>> ttfqHomePage(ActivityBean bean) throws Exception;

    /**
     * 天天分钱方案详情
     * @param bean
     * @return
     * @throws Exception
     */
    TtfqDetailDTO ttfqDetail(ActivityBean bean) throws Exception;

    /**
     * 参与天天分钱
     * @param bean
     */
    void ttfeJoin(ActivityBean bean) throws Exception;
}
