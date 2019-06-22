package com.caiyi.lottery.tradesystem.usercenter.controller;

import bean.PushBean;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import integral.bean.IntegralParamBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.service.*;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import dto.IdcardBindingDTO;
import dto.UserPhotoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.Acct_UserPojo;
import pojo.UserPojo;
import pojo.UserRecordPojo;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户中心边缘接口
 *
 * @author GJ
 * @create 2017-11-24 16:43
 **/
@RestController
public class UserOtherController {

    private static Logger logger = LoggerFactory.getLogger(UserOtherController.class);

    @Autowired
    @Qualifier("userCenterService")
    private UserCenterService userCenterService;
    @Autowired
    private PersonalInfoService personalInfoService;
    @Autowired
    private ModifyUserInfoService modifyUserInfoService;
    
    @Autowired
    private UserActivationCallbackService userActivationCallbackService;

    @Autowired
    private WeChatService weChatService;

    /**
     * 头像上传入库
     * @param userPhotoDTOBaseReq
     * @return
     */
    @RequestMapping(value = "/user/upload_user_photo.api")
    public BaseResp uploadUserPhoto(@RequestBody BaseReq<UserPhotoDTO> userPhotoDTOBaseReq) {
        UserPhotoDTO userPhotoDTO = userPhotoDTOBaseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            return userCenterService.upLoadUserPhoto(userPhotoDTO);

        } catch (Exception e) {
            logger.error("头像上传失败", e);
            baseResp.setCode(ErrorCode.USER_UPLOADPHOTO_PROCESS_ERROR);
            baseResp.setDesc("头像上传失败");
        }
        return baseResp;
    }

    /**
     * 用户绑定校验
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/user_bind_check.api")
    BaseResp userBindCheck(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        BaseResp<UserBean> baseResp = new BaseResp<>();
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("验证通过");
        try {
            if (bean.getFlag() == 0) {//邮箱

            } else if (bean.getFlag() == 1) {
                if (bean.getLogintype() != 1) {
                    // todo 非app登录方式session未处理
                    // HttpSession session = request.getSession();
                    // String uid = (String) session.getAttribute(UserConstants.UID_KEY);
                    // bean.setUid(uid);
                }
            } else {
                logger.error("绑定类型不支持:[uid:{}]", bean.getUid());
                baseResp.setCode(BusiCode.USER_BINDCHECK_FLAG_ERROR);
                baseResp.setDesc("绑定类型不支持");
            }

            if (bean.getBusiErrCode() == 0) {
                userCenterService.bindUserCheck(bean);
            }
            baseResp.setCode(bean.getBusiErrCode()+"");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.error("绑定验证处理异常：[uid:{}]",bean.getUid(), e);
            baseResp.setCode(ErrorCode.USER_BINDCHECK_PROCESS_ERROR);
            baseResp.setDesc("绑定验证处理异常");
        }
        return baseResp;
    }

    /**
     * 设置中奖追号推送开关
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/update_win_chase_switch.api")
    public BaseResp updateWinAndChaseNumberSwitch(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            if(bean.getWinSwitch() == null && bean.getChaseSwitch() == null){
                baseResp.setCode(BusiCode.USER_WINANDCHASENUMBERSWITCH_PARAM_NULL);
                baseResp.setDesc("开关设置参数不可全为空");
                return baseResp;
            }
            int rc = userCenterService.updateWinAndChaseNumberSwitch(bean);
            if(rc != 0||bean.getBusiErrCode()!=0){
                baseResp.setCode(BusiCode.USER_WINANDCHASENUMBERSWITCH_SAVE_ERROR);
                baseResp.setDesc("开关设置参数保存出错");
                return baseResp;
            }
            baseResp.setCode(BusiCode.SUCCESS);
            baseResp.setDesc("开关设置参数保存成功");
        } catch (Exception e) {
            logger.error("用户中奖追号推送开关设置异常", e);
            baseResp.setCode(ErrorCode.USER_WINANDCHASENUMBERSWITCH_PROCESS_ERROR);
            baseResp.setDesc("中奖追号推送开关设置异常");
        }
        return baseResp;
    }

    /**
     * 保存用户激活数据
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/save_active_data.api")
    BaseResp saveActiveDate(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            if(bean.getImei().contains(",")){
                String[] imeiArr = bean.getImei().split(",");
                bean.setImei(imeiArr[0]);
            }
            userCenterService.saveActiveData(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.error("保存激活数据处理过程异常,uid={}", bean.getUid(), e);
            baseResp.setCode(ErrorCode.USER_SAVEACTIVE_PROCESS_ERROR);
            baseResp.setDesc("保存激活数据处理过程异常");
        }
        return baseResp;
    }

    /**
     * 检测用户是否可以提交银行卡修改申请
     * @param uid 用户昵称
     * @return
     */
    @RequestMapping(value = "/user/check_apply_eligible.api")
    BaseResp checkApplyEligible(@RequestBody String uid) {
        logger.info("检测当前用户是否可以提交银行卡修改申请：[nickid:{}]", uid);
        BaseResp baseResp = new BaseResp();
        BaseBean baseBean = new BaseBean();
        baseBean.setUid(uid);
        try {
            boolean proceed = personalInfoService.checkBeforeSubmit(baseBean);
            if (proceed) {
                baseResp.setCode(BusiCode.SUCCESS);
                baseResp.setDesc("继续填写新银行卡信息");
                return baseResp;
            }

            baseResp.setCode(baseBean.getBusiErrCode() + "");
            baseResp.setDesc(baseBean.getBusiErrDesc());

        } catch (Exception e) {
            logger.error("检测用户是否可以修改银行卡时出错:[nickid:{}]", uid, e);
            baseResp.setCode(ErrorCode.USER_CHECKAPPLYELIGIBLE_PROCESS_ERROR);
            baseResp.setDesc("检测失败");
        }
        return baseResp;
    }


    /**
     * 查询个推tag
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/query_gt_tag.api")
    public BaseResp<PushBean> queryGtTag(@RequestBody BaseReq<UserBean> baseReq){
        UserBean bean = baseReq.getData();
        //查询openKey
        PushBean resbean = userCenterService.queryGtTag(bean);
        //查询tag
        PushBean result = userCenterService.queryGtTagDetail(resbean,bean);
        try {
            userCenterService.insertIntoData(bean);
        } catch (Exception e) {
            logger.error("查询tag出错");
            BaseResp baseResp = new BaseResp();
            baseResp.setCode(Result.FAIL);
            baseResp.setDesc("查询出错");
            return baseResp;
        }
        PushBean res= userCenterService.setData(result, bean);
        BaseResp baseResp = new BaseResp();
        baseResp.setCode(Result.SUCCESS);
        baseResp.setDesc("查询结束");
        baseResp.setData(res);
        return baseResp;
    }

    /**
     * 获取客服电话
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/get_service_hot_line.api")
    public BaseResp getServiceHotline(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        logger.info("获取客服电话");
        String result = "";
        result = userCenterService.getHotLineString(bean);
        BaseResp baseResp = new BaseResp();
        baseResp.setCode(Result.SUCCESS);
        baseResp.setData(result);
        return baseResp;
    }

    @RequestMapping(value = "/user/reback_user_photo_status.api")
    public BaseResp rebackUserPhotoStatus(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            logger.info("用户中心--> 用户头像反馈，uid==" + bean.getUid());
            int i = userCenterService.rebackUserPhotoStatus(bean);
            if (i <= 0) {
                baseResp.setCode(Result.FAIL);
                baseResp.setDesc("反馈失败");
                logger.info("头像审核失败，已经提示用户");
                return baseResp;
            }
            baseResp.setCode(Result.SUCCESS);
            baseResp.setDesc("反馈成功");
        } catch (Exception e) {
            baseResp.setCode(Result.FAIL);
            baseResp.setDesc("反馈出错");
            logger.error("头像反馈接口出错",e);
        }
        return baseResp;
    }

    /**
     * 更换手机号检查
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/change_mobile_check.api")
    public BaseResp changeMobileCheck(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            modifyUserInfoService.changeMobileCheck(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.error("更换手机号检查失败，uid:{}",bean.getUid(), e);
            baseResp.setCode(ErrorCode.USER_CHANGE_MOBILE_CHECK_PROCESS_ERROR);
            baseResp.setDesc("验证失败");
        }
        return baseResp;
    }

    /**
     * 查询身份证银行卡绑定信息
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/query_idbank_binding.api")
    BaseResp queryIdBankBinding(@RequestBody BaseReq<UserBean> baseReq) {
        BaseResp baseResp = new BaseResp();
        BaseBean bean = baseReq.getData();
        try {
            baseResp = userCenterService.queryIdBankBinding(bean);
        } catch (Exception e) {
            logger.error("查询身份证银行卡绑定信息异常，[uid:{}]",bean.getUid(), e);
            baseResp.setCode(ErrorCode.USER_IDBANK_BINDING_PROCESS_ERROR);
            baseResp.setDesc("绑定信息查询失败");
        }
        return baseResp;
    }

    /**
     * 查询身份证、手机号绑定状态
     */
    @RequestMapping("/user/query_userinfo_bind.api")
    public BaseResp<IdcardBindingDTO> queryReaInfoBind(@RequestBody BaseReq<BaseBean> req){
        BaseResp<IdcardBindingDTO> resp=new BaseResp<>();
        BaseBean bean=req.getData();
        IdcardBindingDTO dto=personalInfoService.queryUserInfoBind(bean);
        resp.setCode(bean.getBusiErrCode()+"");
        resp.setDesc(bean.getBusiErrDesc());
        resp.setData(dto);
        return resp;
    }
    
    /**
     * 激活回调
     * @return
     */
    @RequestMapping("/user/invoke.api")
    public Response activationCallback(@RequestBody  BaseReq<UserBean> beanReq) {
    	UserBean bean = beanReq.getData();
        try {
            userActivationCallbackService.adInvoke(bean);
        } catch (Exception e) {
            logger.error("激活回调出错",e);
        }
    	Response res = new Response();
    	res.setCode(bean.getBusiErrCode()+"");
    	res.setDesc(bean.getBusiErrDesc());
    	return res;
    }

    @RequestMapping("/user/integral_query_basic_info.api")
    public BaseResp<Acct_UserPojo> integralQueryBasicInfo(@RequestBody BaseReq<String> req){
        String uid = req.getData();
        Acct_UserPojo bean = new Acct_UserPojo();
        bean = userCenterService.integralQueryBasicInfo(uid);
        BaseResp<Acct_UserPojo> baseResp = new BaseResp<>();
        if(null != bean){
            baseResp.setCode(BusiCode.SUCCESS);
            baseResp.setDesc("查询头像积分等级成功");
        }else{
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询头像积分等级成功");
        }
        baseResp.setData(bean);
        return baseResp;
    };

    @RequestMapping("/user/integral_idBank_binding.api")
    public BaseResp<UserPojo> integralQueryIdBankBinding(@RequestBody BaseReq<String> req){
        String uid = req.getData();
        UserPojo pojo = userCenterService.integralQueryIdBankBinding(uid);
        BaseResp<UserPojo> response = new BaseResp<>();
        if(null != pojo){
            response.setCode(BusiCode.FAIL);
            response.setDesc("查询银行卡身份证绑定状态失败");
        }else{
            response.setCode(BusiCode.SUCCESS);
            response.setDesc("查询成功");
        }
        response.setData(pojo);
        return response;
    }

    @RequestMapping(value = "/user/cannot_sign.api")
    public BaseResp<String>  cannotSign(@RequestBody BaseReq<String> req){
        String uid = req.getData();
        String total = userCenterService.cannotSign(uid);
        BaseResp<String> response = new BaseResp<>();
        if(StringUtil.isEmpty(total)){
            response.setCode(BusiCode.FAIL);
            response.setDesc("查询签到资格状态失败");
        }else{
            response.setCode(BusiCode.SUCCESS);
            response.setDesc("查询成功");
        }
        response.setData(total);
        return response;

    }

    @RequestMapping(value = "/user/click_toGet_points.api")
    public BaseResp<Integer>  clickToGetPoints(@RequestBody BaseReq<IntegralParamBean> req){
        IntegralParamBean params = req.getData();
        int num = userCenterService.clickToGetPoints(params);
        BaseResp<Integer> response = new BaseResp<>();
        if(1 != num){
            response.setCode(BusiCode.FAIL);
            response.setDesc("获取积分失败");
        }else{
            response.setCode(BusiCode.SUCCESS);
            response.setDesc("查询成功");
        }
        response.setData(num);
        return response;

    }

    @RequestMapping(value = "/user/query_vip_user_info.api")
    BaseResp<UserRecordPojo> queryVipUserInfo(@RequestBody BaseReq<String> req){
        String uid = req.getData();
        UserRecordPojo pojo = userCenterService.queryVipUserInfo(uid);
        BaseResp<UserRecordPojo> response = new BaseResp<>();
        if(null != pojo){
            response.setCode(BusiCode.FAIL);
            response.setDesc("获取会员中心失败");
        }else{
            response.setCode(BusiCode.SUCCESS);
            response.setDesc("查询成功");
        }
        response.setData(pojo);
        return response;
    }

    @RequestMapping(value = "/user/query_level_exper.api")
    BaseResp<String> queryLevelExper(@RequestBody BaseReq<String> req){
        BaseResp<String> resp = new BaseResp<>();
        String level = req.getData();
        String exper =  userCenterService.queryLevelExper(level);
        if(StringUtil.isEmpty(exper)){
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("查询下一级经验失败");
        }else{
            resp.setCode(BusiCode.SUCCESS);
            resp.setDesc("查询下一级经验成功");
        }
        resp.setData(level);
        return resp;
    }
}
