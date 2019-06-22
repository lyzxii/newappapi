package com.caiyi.lottery.tradesystem.activityweb.controller;

import activity.bean.ActivityBean;
import com.caiyi.lottery.tradesystem.activitycenter.client.ActivityInterface;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 活动中心
 * @author wxy
 * @create 2017-12-22 11:54
 **/
@Slf4j
@RestController
public class ActivityWebController {
    @Autowired
    private RedPacketCenterInterface redPacketCenterInterface;
    @Autowired
    private ActivityInterface activityInterface;

    @RequestMapping(value = "/activity/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("活动中心activity-web启动运行正常");
        return response;
    }

    @RequestMapping(value = "/activity/checkhealth.api")
    public Result checkHealth(){
        Response response = activityInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        log.info("=====检测活动中心服务=====");
        return result;
    }

    /**
     * 展示用户滚动打码
     * @param bean
     * @return
     */
    @RequestMapping(value = "/activity/query_rolling_code.api")
    public Result queryRollCode(ActivityBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.ACTIVITYWEB);
        try {
            BaseResp<List<String>> resp = redPacketCenterInterface.queryRollingCode(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(resp, result);
        } catch (Exception e) {
            log.error("展示用户滚动打码异常", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询失败");
        }
        return result;
    }

    /**
     * 获取新用户88元活动红包验证接口
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/get_eighty_redpacket_check.api")
    public Result getEightyRedPacketCheck(ActivityBean bean) {
        Result result = new Result();
        BaseReq<ActivityBean> activityReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYWEB);
        BaseResp activityResp;
        try {
            activityResp = activityInterface.getEightyRedPacketCheck(activityReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(activityResp,result);
        } catch (Exception e) {
            log.error("获取新用户88元活动红包验证接口异常，[uid:{}]", bean.getUid() ,e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("验证失败");
        }
        return result;
    }

    /**
     * 天天分钱首页
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/ttfq_home_page.api")
    public Result ttfqHomePage(ActivityBean bean) {
        Result result = new Result();
        BaseReq<ActivityBean> activityReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYWEB);
        BaseResp activityResp;
        try {
            activityResp = activityInterface.ttfqHomePage(activityReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(activityResp, result);
        } catch (Exception e) {
            log.error("天天分钱首页异常", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("首页加载失败");
        }
        return result;
    }

    /**
     * 天天分钱领奖接口
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/ttfq_get_bonus.api")
    public Result ttfqGetBonus(ActivityBean bean) {
        Result result = new Result();
        BaseReq<ActivityBean> activityReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYWEB);
        BaseResp activityResp;
        try {
            activityResp = activityInterface.getBonus(activityReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(activityResp, result);
        } catch (Exception e) {
            log.error("天天分钱领奖接口异常，[uid:{},projid:{}]", bean.getUid(), bean.getProjId(), e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("领取失败");
        }
        return result;
    }

    /**
     * 查看快播活动天天分钱方案
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/ttfq_detail.api")
    public Result ttfqDetail(ActivityBean bean) {
        Result result = new Result();
        BaseReq<ActivityBean> activityReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYWEB);
        BaseResp activityResp;
        try {
            activityResp = activityInterface.ttfqDetail(activityReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(activityResp, result);
        } catch (Exception e) {
            log.error("查看快播活动天天分钱方案异常，[projId:{},gameId:{}]", bean.getProjId(), bean.getGameId(), e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询失败");
        }
        return result;
    }

    /**
     * 天天分钱参与
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/ttfq_join.api")
    public Result ttfqJoin(ActivityBean bean) {
        Result result = new Result();
        BaseReq<ActivityBean> activityReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYWEB);
        BaseResp activityResp = new BaseResp();
        try {
            activityResp = activityInterface.ttfqJoin(activityReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(activityResp, result);
        } catch (Exception e) {
            log.error("参与天天分钱异常，[uid:{},projId:{},gameId:{}]", bean.getUid(), bean.getProjId(), bean.getGameId(), e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("参与天天分钱活动失败");
        }
        return result;
    }

    /**
     * 记录第三方游戏登陆
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ACTIVITYWEB)
    @RequestMapping(value = "/activity/game_record_login.api")
    public Result gameRecordLogin(ActivityBean bean) {
        Result result = new Result();
        BaseReq<ActivityBean> activityReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYWEB);
        BaseResp activityResp;
        try {
            activityResp = activityInterface.gameRecordLogin(activityReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(activityResp, result);
        } catch (Exception e) {
            log.error("记录第三方有心登陆失败，[uid:{},gameId:{}]", bean.getUid(), bean.getGameId(), e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("记录第三方游戏登陆失败");
        }
        return result;
    }
}
