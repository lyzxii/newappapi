package com.caiyi.lottery.tradesystem.activitycenter.client;

import activity.bean.ActivityBean;
import activity.dto.*;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "tradecenter-system-activity-center")
public interface ActivityInterface {
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/activity/checkhealth.api")
    Response checkHealth() ;
    /**
     * 获取新用户88元活动红包验证接口
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/get_eighty_redpacket_check.api")
    BaseResp getEightyRedPacketCheck(@RequestBody BaseReq<ActivityBean> activityReq) throws Exception;

    /**
     * 天天分钱领取奖金
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_get_bonus.api")
    BaseResp<GetBonusDTO> getBonus(@RequestBody BaseReq<ActivityBean> activityReq) throws Exception;

    /**
     * 天天分钱首页
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_home_page.api")
    BaseResp<TtfqPage<List<TtfqHomePageDTO>>> ttfqHomePage(@RequestBody BaseReq<ActivityBean> activityReq) throws Exception;

    /**
     * 天天分钱方案详情
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_detail.api")
    BaseResp<TtfqDetailDTO> ttfqDetail(@RequestBody BaseReq<ActivityBean> activityReq) throws Exception;

    /**
     * 参与天天分钱
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_join.api")
    BaseResp ttfqJoin(@RequestBody BaseReq<ActivityBean> activityReq) throws Exception;

    /**
     * 记录第三方游戏登陆
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/game_record_login.api")
    BaseResp gameRecordLogin(@RequestBody BaseReq<ActivityBean> activityReq) throws Exception;

    /**
     * 邀请历史
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/invitation_history.api")
    BaseResp<List<ForcastDTO>> invitationHistory(@RequestBody BaseReq<ActivityBean> activityReq);

    /**
     * 邀请详情
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/invitation_detail.api")
    BaseResp<ForcastDTO> invitationDetail(@RequestBody BaseReq<ActivityBean> activityReq);

    /**
     * 预测首页
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/activity/forcast.api")
    BaseResp<ForcastDTO> forcast(@RequestBody BaseReq<ActivityBean> activityReq);
}
