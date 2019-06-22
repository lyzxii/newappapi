package com.caiyi.lottery.tradesystem.activitycenter.controller;

import activity.bean.ActivityBean;
import activity.dto.GetBonusDTO;
import activity.dto.TtfqDetailDTO;
import activity.dto.TtfqHomePageDTO;
import activity.dto.TtfqPage;
import com.caiyi.lottery.tradesystem.activitycenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.activitycenter.service.ActivityRedPacketService;
import com.caiyi.lottery.tradesystem.activitycenter.service.ActivityTtfqService;
import com.caiyi.lottery.tradesystem.activitycenter.service.ThirdGameService;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wxy
 * @create 2017-12-22 12:00
 **/
@Slf4j
@RestController
public class ActivityCenterController {
    @Autowired
    private ActivityRedPacketService activityRedPacketService;
    @Autowired
    private ActivityTtfqService activityTtfqService;
    @Autowired
    private ThirdGameService thirdGameService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;

    @RequestMapping(value = "/activity/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("活动中心activity-center启动运行正常");
        return response;
    }

    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/activity/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_activity");
        redisClient.exists(cacheBean,log, SysCodeConstant.ACTIVITYCENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("活动中心服务运行正常");
        return response;
    }
    /**
     * 获取新用户88元活动红包验证接口
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/get_eighty_redpacket_check.api")
    BaseResp getEightyRedPacketCheck(@RequestBody BaseReq<ActivityBean> activityReq) {
        BaseResp activityResp = new BaseResp();
        ActivityBean bean = activityReq.getData();
        try {
            activityRedPacketService.getEightyRedPacketCheck(bean);
            activityResp.setCode(bean.getBusiErrCode() + "");
            activityResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("获取新用户88元活动红包验证接口服务异常，[uid:{}]", bean.getUid(), e);
            activityResp.setCode(BusiCode.FAIL);
            activityResp.setDesc("验证失败");
        }
        return activityResp;
    }

    /**
     * 天天分钱首页
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_home_page.api")
    BaseResp<TtfqPage<List<TtfqHomePageDTO>>> ttfqHomePage(@RequestBody BaseReq<ActivityBean> activityReq) {
        ActivityBean bean = activityReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            TtfqPage<List<TtfqHomePageDTO>> data = activityTtfqService.ttfqHomePage(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("天天分钱首页异常", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }

    /**
     * 天天分钱领取奖金
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_get_bonus.api")
    BaseResp<GetBonusDTO> getBonus(@RequestBody BaseReq<ActivityBean> activityReq) {
        BaseResp baseResp = new BaseResp();
        ActivityBean bean = activityReq.getData();
        try {
            GetBonusDTO bonusDto = activityTtfqService.getBonus(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(bonusDto);
        } catch (Exception e) {
            log.error("天天分钱领取奖金异常，[uid:{},projid:{}]", bean.getUid(), bean.getProjId(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("奖金领取失败");
        }
        return baseResp;
    }

    /**
     * 天天分钱方案详情
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_detail.api")
    BaseResp<TtfqDetailDTO> ttfqDetail(@RequestBody BaseReq<ActivityBean> activityReq) {
        BaseResp baseResp = new BaseResp();
        ActivityBean bean = activityReq.getData();
        try {
            TtfqDetailDTO detailDTO = activityTtfqService.ttfqDetail(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(detailDTO);
        } catch (Exception e) {
            log.error("天天分钱方案详情查询失败，[projId:{},gameId:{}]", bean.getProjId(), bean.getGameId(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }

    /**
     * 参与天天分钱
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/ttfq_join.api")
    BaseResp ttfqJoin(@RequestBody BaseReq<ActivityBean> activityReq) {
        ActivityBean bean = activityReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            activityTtfqService.ttfeJoin(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("参与天天分钱处理异常，[uid:{},projId:{},gameId:{}]", bean.getUid(), bean.getProjId(), bean.getGameId(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("参与天天分钱活动失败");
        }
        return baseResp;
    }

    /**
     * 记录第三方登陆的游戏
     * @param activityReq
     * @return
     */
    @RequestMapping(value = "/activity/game_record_login.api")
    BaseResp gameRecordLogin(@RequestBody BaseReq<ActivityBean> activityReq) {
        ActivityBean bean = activityReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            thirdGameService.gameRecordLogin(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("记录第三方游戏登陆处理异常，[uid:{},gameId:{}]", bean.getUid(), bean.getGameId(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("记录第三方游戏登陆失败");
        }
        return baseResp;
    }
}
