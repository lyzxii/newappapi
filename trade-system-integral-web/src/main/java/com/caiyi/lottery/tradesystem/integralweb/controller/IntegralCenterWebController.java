package com.caiyi.lottery.tradesystem.integralweb.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import integral.bean.IntegralBean;
import com.caiyi.lottery.tradesystem.integralcenter.client.IntegralCenterInterface;
import com.caiyi.lottery.tradesystem.integralweb.service.IntergralCenterService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegralCenterWebController {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(IntegralCenterWebController.class);

    @Autowired
    IntergralCenterService intergralCenterService;
    @Autowired
    private UserBaseInterface userCenterBaseInterface;
    @Autowired
    private IntegralCenterInterface integralCenterInterface;
    @RequestMapping(value = "/integral/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("积分中心integral-web启动运行正常");
        return response;
    }
    @RequestMapping(value = "/integral/checkhealth.api")
    public Result checkHealth(){
        Response response = integralCenterInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        logger.info("=====检测积分中心服务=====");
        return result;
    }

    /**
     * 积分中心数据
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping(value = "/integral/query_vip_point_info.api", produces = {"application/json;charset=UTF-8"})
    public Result<IntegralBean> queryVipPointInfo(UserBean bean) {
        BaseReq<UserBean> baseReq = new BaseReq(SysCodeConstant.INTEGRALWEB);
        BaseResp<IntegralBean> result = new BaseResp();
        Result<IntegralBean> integralBeanResult = new Result<>();
        baseReq.setData(bean);
        result = intergralCenterService.queryVipPointInfo(baseReq);
        integralBeanResult.setCode(result.getCode());
        integralBeanResult.setDesc(result.getDesc());
        integralBeanResult.setData(result.getData());
        return integralBeanResult;
    }

    /**
     * 用户签到
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping(value = "/integral/click_to_sign.api", produces = {"application/json;charset=UTF-8"})
    public Result<IntegralBean> clickToSign(UserBean bean) {
        BaseReq<UserBean> baseReq = new BaseReq(SysCodeConstant.INTEGRALWEB);
        Result<IntegralBean> integralBeanResult = new Result<>();
        baseReq.setData(bean);
        BaseResp<IntegralBean> res = intergralCenterService.clickToSign(baseReq);
        logger.info("用户[" + bean.getUid() + "]积分中心处理结果：code==" + res.getCode() + ",msg==" + res.getDesc());
        integralBeanResult.setCode(res.getCode());
        integralBeanResult.setDesc(res.getDesc());
        integralBeanResult.setData(res.getData());
        return integralBeanResult;
    }

    /**
     * 获取银行卡 身份证绑定积分
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping(value = "/integral/click_to_get_points.api", produces = {"application/json;charset=UTF-8"})
    public Result<IntegralBean> clickToGetPoints(UserBean bean) {
        BaseReq<UserBean> baseReq = new BaseReq(SysCodeConstant.INTEGRALWEB);
        Result<IntegralBean> integralBeanResult = new Result<>();
        baseReq.setData(bean);
        BaseResp<IntegralBean> res = intergralCenterService.clickToGetPoints(baseReq);
        logger.info("用户[" + bean.getUid() + "]积分中心处理结果：code==" + res.getCode() + ",msg==" + res.getDesc());
        integralBeanResult.setCode(res.getCode());
        integralBeanResult.setDesc(res.getDesc());
        integralBeanResult.setData(res.getData());
        return integralBeanResult;
    }

    /**
     * 获经验明细
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping(value = "/integral/experience_detail.api", produces = {"application/json;charset=UTF-8"})
    public Result<Page> experienceDetail(UserBean bean) {
        Result<Page> pageResult = new Result<>();
        BaseReq req = new BaseReq(SysCodeConstant.INTEGRALWEB);
        req.setData(bean);
        BaseResp<Page> res = intergralCenterService.experienceDetail(req);
        logger.info("用户[" + bean.getUid() + "]积分中心处理结果：code==" + res.getCode() + ",msg==" + res.getDesc());
        if ("0".equals(res.getCode())){
            pageResult.setCode(res.getCode());
            pageResult.setDesc(res.getDesc());
            pageResult.setData(res.getData());
            return pageResult;
        }
        pageResult.setCode(res.getCode());
        pageResult.setDesc(res.getDesc());
        pageResult.setData(res.getData());
        return pageResult;
    }

    /**
     * 获取积分明细
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping(value = "/integral/points_detail.api", produces = {"application/json;charset=UTF-8"})
    public Result<Page> pointsDetail(UserBean bean) {
        Result<Page> pageResult = new Result<>();
        BaseReq req = new BaseReq(SysCodeConstant.INTEGRALWEB);
        req.setData(bean);
        BaseResp<Page> res = intergralCenterService.pointsDetail(req);
        logger.info("用户[" + bean.getUid() + "]积分中心处理结果：code==" + res.getCode() + ",msg==" + res.getDesc());
        if ("0".equals(res.getCode())){
            pageResult.setCode(res.getCode());
            pageResult.setDesc(res.getDesc());
            pageResult.setData(res.getData());
            return pageResult;
        }
        pageResult.setCode(res.getCode());
        pageResult.setDesc(res.getDesc());
        pageResult.setData(res.getData());
        return pageResult;
    }

    /**
     * 会员中心数据
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping(value = "/integral/query_vip_user_info.api", produces = {"application/json;charset=UTF-8"})
    public Result<IntegralBean> queryVipUserInfo(UserBean bean) {
        BaseReq<UserBean> req = new BaseReq<>(SysCodeConstant.INTEGRALWEB);
        Result<IntegralBean> integralBeanResult = new Result<>();
        req.setData(bean);
        BaseResp<IntegralBean> res = intergralCenterService.queryVipUserInfo(req);
        logger.info("用户[" + bean.getUid() + "]积分中心处理结果：code==" + res.getCode() + ",msg==" + res.getDesc());
        if(!StringUtil.isEmpty(res.getCode())){
            integralBeanResult.setCode(res.getCode());
            integralBeanResult.setDesc(res.getDesc());
            integralBeanResult.setData(res.getData());
            return integralBeanResult;
        }
        integralBeanResult.setCode(BusiCode.FAIL);
        integralBeanResult.setDesc("查询失败");
        integralBeanResult.setData(res.getData());
        return integralBeanResult;
    }
}
