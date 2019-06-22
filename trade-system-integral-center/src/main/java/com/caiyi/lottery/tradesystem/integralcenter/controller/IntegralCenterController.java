package com.caiyi.lottery.tradesystem.integralcenter.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.integralcenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.integralcenter.service.IntegralCenterQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import integral.bean.IntegralBean;



@RestController
public class IntegralCenterController {

    private Logger logger = LoggerFactory.getLogger(IntegralCenterController.class);

    @Autowired
    IntegralCenterQueryService integralCenterQueryService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;

    @RequestMapping(value = "/integral/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("积分中心integral-center启动运行正常");
        return response;
    }
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/integral/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_integral");
        redisClient.exists(cacheBean,logger, SysCodeConstant.INTEGRALCENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("积分中心服务运行正常");
        return response;
    }

    /**
     * 积分中心页面数据
     */
    @RequestMapping(value = "/integral/query_vip_point_info.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<IntegralBean> getIntegralCenterInfo(@RequestBody BaseReq<UserBean> bean) {
        BaseResp<IntegralBean> result = new BaseResp<>();
        UserBean userBean = bean.getData();
        try {
            logger.info("积分中心--> 积分中心开始查询用户" + userBean.getUid());
            result = integralCenterQueryService.integralCenterImage(userBean);
            if (result != null) {
                logger.info("积分中心--> 用户["+ userBean.getUid() +"]积分中心数据查询结束，code==" + result.getCode() + ",desc==" + result.getDesc());
                return result;
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询失败");
            logger.error("积分中心用户" + userBean.getUid()+"数据查询error", e);
        }
        return result;
    }

    /**
     * 用户手动签到
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/integral/click_to_sign.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<IntegralBean> clickToSign(@RequestBody BaseReq<UserBean> bean) {
        BaseResp<IntegralBean> result = new BaseResp<>();
        UserBean userBean = bean.getData();
        try {
            logger.info("积分中心--> 用户["+ userBean.getUid() +"]开始签到");
            result = integralCenterQueryService.sign(userBean);
            logger.info("积分中心--> 用户["+ userBean.getUid() +"]签到结束，code==" + result.getCode());
            if (result != null && StringUtil.isEmpty(result.getCode())) {
                result.setCode(userBean.getBusiErrCode() + "");
                result.setDesc(userBean.getBusiErrDesc());
                return result;
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("签到天数+1失败");
            logger.error("积分中心用户" + userBean.getUid()+"手动签到error", e);
        }
        return result;
    }

    /**
     * 获取积分
     *
     * @param bean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/integral/click_to_get_points.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<IntegralBean> clickToGetPoints(@RequestBody BaseReq<UserBean> bean) throws Exception {
        BaseResp result = new BaseResp();
        UserBean userBean = bean.getData();
        try {
            result = integralCenterQueryService.getUserPoints(userBean);
            logger.info("积分中心--> 用户[" + userBean.getUid() + "]身份证/银行卡绑定积分获取结束，code==" + result.getCode() +",msg=="+result.getDesc());
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("身份证/银行卡绑定积分获取失败");
            logger.error("积分中心用户" + userBean.getUid()+"身份证/银行卡绑定积分获取error", e);
        }
        return result;
    }

    /**
     * 会员中心数据
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/integral/query_vip_user_info.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<IntegralBean> queryVipUserInfo(@RequestBody BaseReq<UserBean> bean) {
        BaseResp result = new BaseResp();
        UserBean userBean = bean.getData();
        IntegralBean integralBean = integralCenterQueryService.queryVipUserInfo(userBean);
        String code = integralBean.getCode();
        if (StringUtil.isEmpty(code)) {
            result.setCode(Result.SUCCESS);
            result.setDesc("查询成功");
        }
        result.setData(integralBean);
        return result;
    }

    /**
     * 经验明细
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/integral/experience_detail.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<Page> experienceDetail(@RequestBody BaseReq<UserBean> bean) {
        BaseResp response = new BaseResp();
        UserBean userBean = bean.getData();
        try {
            response = integralCenterQueryService.getExperienceDetail(userBean);
            if (response != null) {
                return response;
            }
        } catch (Exception e) {
            response.setCode(BusiCode.FAIL);
            response.setDesc("身份证/银行卡绑定积分获取失败");
            logger.error("积分中心用户[" + userBean.getUid()+"]积分明细error", e);
        }

        return response;
    }

    /**
     * 积分明细
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/integral/points_detail.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<Page> pointsDetail(@RequestBody BaseReq<UserBean> bean) {
        BaseResp response = new BaseResp();
        UserBean userBean = bean.getData();
        try {
            response = integralCenterQueryService.getPointsDetail(userBean);
            if (response != null) {
                return response;
            }
        } catch (Exception e) {
            response.setCode(BusiCode.FAIL);
            response.setDesc("身份证/银行卡绑定积分获取失败");
            logger.error("积分中心用户" + userBean.getUid()+"积分明细error", e);
        }
        return response;
    }
    
}
