package com.caiyi.lottery.tradesystem.userweb.controller;

import bean.AlipayLoginBean;
import bean.PushBean;
import bean.UserBean;
import bean.WeChatBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.RealIP;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.usercenter.client.UserInterface;
import com.caiyi.lottery.tradesystem.userweb.service.UploadService;
import com.caiyi.lottery.tradesystem.userweb.service.UserWebService;
import com.caiyi.lottery.tradesystem.util.*;
import constant.UserConstants;
import dto.IdcardBindingDTO;
import dto.UserPhotoDTO;
import dto.WeChatDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import response.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;


/**
 * 用户中心请求
 * /user/uploadUserPhoto.go 用户上传头像
 * /user/userBindCheck.go 用户绑定验证
 *
 * @author wxy
 * @create 2017-11-27 9:46
 **/
@RestController
public class UserWebController {

    private Logger logger = LoggerFactory.getLogger(UserWebController.class);
    @Autowired
    private UserBaseInterface userCenterBaseInterface;

    @Autowired
    private UserInterface userCenterInterface;
    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserWebService userWebService;

    @Autowired
    private HttpServletRequest request;
    @RequestMapping(value = "/user/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("用户中心user-web启动运行正常");
        return response;
    }

    @RequestMapping(value = "/user/checkhealth.api")
    public Result checkHealth(){
        Response response = userCenterInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        logger.info("=====检测用户中心服务=====");
        return result;
    }
    /**
     * 手机注册资格校验
     *
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/mobile_register_check.api")
    public Result mobileRegisterCheck(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 合作登入不检测图片验证码
        if (bean.getHztype() != 1) {
            result = userWebService.checkPicYzm(bean, request);
            if (!result.getCode().equals(BusiCode.SUCCESS)) {
                return result;
            }
        }
        UserRegistResp userRegistResp = userCenterInterface.mobileRegisterCheck(baseReq);
        userWebService.clearPicYzm(bean, request);
        result.setCode(userRegistResp.getCode());
        result.setDesc(userRegistResp.getDesc());
        result.setData(userRegistResp.getData());
        return result;

    }

    /**
     * 加减乘除校验码图片
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/user/image_code.api")
    public void getImagecode(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        HttpSession localHttpSession = request.getSession(true);
        VerificationCodeTool vct = new VerificationCodeTool();
        BufferedImage image = vct.drawVerificationCodeImage();
        try {
            ServletOutputStream localServletOutputStream = response.getOutputStream();
            localHttpSession.setAttribute(UserConstants.SESSION_YZM, vct.getXyresult() + "");
            ImageIO.write(image, "JPEG", localServletOutputStream);
            localServletOutputStream.flush();
            localServletOutputStream.close();
            logger.info("验证码生成成功,内容:" + vct.getRandomString() + " 结果:" + vct.getXyresult());
        } catch (IOException e) {
            logger.error("创建验证码失败", e);
        }
    }

    /**
     * 数字字母校验码图片
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/user/number_image_code.api")
    public void getNumberImagecode(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        VerificationCodeTool vct = new VerificationCodeTool();
        BufferedImage image = vct.getnumberImage();
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(300);
        }
        logger.info("生成图片验证码时sessionid={},session={}", session.getId(), session.toString());
        try {
            ServletOutputStream localServletOutputStream = response.getOutputStream();
            session.setAttribute(UserConstants.SESSION_YZM, vct.getNumber() + "");
            ImageIO.write(image, "JPEG", localServletOutputStream);
            localServletOutputStream.flush();
            localServletOutputStream.close();
            logger.info("验证码生成成功--,内容:" + vct.getNumber());
        } catch (IOException e) {
            logger.error("创建验证码失败", e);
        }
    }

    /**
     * 验证图片验证码
     *
     * @param bean
     * @param request
     */
    @RequestMapping(value = "/user/check_pic_yzm.api")
    public Result checkPicYzm(UserBean bean, HttpServletRequest request) {
        return userWebService.checkPicYzm(bean, request);
    }

    /**
     * 手机注册
     *
     * @param bean
     * @return
     */
    @RealIP
    @RequestMapping(value = "/user/mobile_register.api", method = RequestMethod.POST)
    public Result<UserRegistResp> mobileRegister(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        UserRegistResp userRegistResp = userCenterInterface.mobileRegister(baseReq);
        result.setCode(userRegistResp.getCode());
        result.setDesc(userRegistResp.getDesc());
        result.setData(userRegistResp.getData());
        return result;
    }

    /**
     * 获取白名单
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/get_user_whitelist_grade.api", method = RequestMethod.POST)
    Result<UserPersonalInfoResq> getUserWhitelistGrade(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        if (!BusiCode.SUCCESS.equals(String.valueOf(bean.getBusiErrCode()))) {  // 检测登录不通过
            result.setCode(String.valueOf(bean.getBusiErrCode()));
            result.setDesc(bean.getBusiErrDesc());
            return result;
        }
        UserPersonalInfoResq userPersonalInfoResq = userCenterInterface.getUserWhitelistGrade(baseReq);
        result.setCode(userPersonalInfoResq.getCode());
        result.setDesc(userPersonalInfoResq.getDesc());
        result.setData(userPersonalInfoResq.getData());
        return result;
    }

    /**
     * 获取个人中心数据
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/personal_center_info.api", method = RequestMethod.POST)
    public Result<UserPersonalInfoResq> personalCenterInfo(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        if (!BusiCode.SUCCESS.equals(String.valueOf(bean.getBusiErrCode()))) {  // 检测登录不通过
            result.setCode(String.valueOf(bean.getBusiErrCode()));
            result.setDesc(bean.getBusiErrDesc());
            return result;
        }
        UserPersonalInfoResq userPersonalInfoResq = userCenterInterface.personalCenterInfo(baseReq);
        result.setCode(userPersonalInfoResq.getCode());
        result.setDesc(userPersonalInfoResq.getDesc());
        result.setData(userPersonalInfoResq.getData());
        return result;
    }

    /**
     * 登入操作
     *
     * @param bean
     * @return
     */

    @RealIP
    @RequestMapping(value = "/user/mobile_login.api", method = RequestMethod.POST)
    public Result<UserLoginResq> login(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        UserLoginResq userLoginResq = userCenterInterface.login(baseReq);
        Result result = new Result();
        result.setCode(userLoginResq.getCode());
        result.setDesc(userLoginResq.getDesc());
        result.setData(userLoginResq.getData());
        return result;
    }

    /**
     * 用户头像上传
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/upload_user_photo.api", produces = {"application/json;charset=UTF-8"})
    public String uploadUserPhoto(UserBean bean) {

        UserPhotoDTO userPhotoDTO = new UserPhotoDTO();
        BaseResp baseResp = new BaseResp();

        try {
            baseResp = new BaseResp<>();
            baseResp = uploadService.photoMultipart(request);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                return baseResp.toJson();
            }
            userPhotoDTO.setPhotoPath(((UserPhotoDTO) baseResp.getData()).getPhotoPath());
            userPhotoDTO.setUid(bean.getUid());
            baseResp = new BaseResp<>();
            BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
            baseReq.setData(userPhotoDTO);
            baseResp = userCenterInterface.uploadUserPhoto(baseReq);
            return baseResp.toJson();
        } catch (Exception e) {
            logger.error("用户上传头像请求异常", e);
            baseResp.setCode(ErrorCode.USER_UPLOADPHOTO_REMOTE_ERROR);
            baseResp.setDesc("上传头像调用远程服务异常");
        }

        return baseResp.toJson();
    }

    /**
     * 用户绑定验证
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/user_bind_check.api")
    public Result userBindCheck(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = userCenterInterface.userBindCheck(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            logger.error("用户绑定验证异常", e);
            result.setCode(ErrorCode.USER_BINDCHECK_REMOTE_ERROR);
            result.setDesc("绑定验证服务异常");
        }
        return result;
    }

    /**
     * 发送短信(新)
     *
     * @param bean
     * @return
     */
    @RealIP
    @RequestMapping(value = "/user/send_mob_sms.api")
    public Result<UserResp> sendMobSms(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        String encryptData = request.getParameter("data");
        Result result = new Result();
        bean.setData(encryptData);

        HttpSession session = request.getSession(true);
        String yzm = (String) session.getAttribute(UserConstants.SESSION_YZM);
        logger.info("前端验证码为-{},后台session中的验证码为", bean.getYzm(), yzm);
        if (!StringUtil.isEmpty(bean.getYzm())) {
            logger.info("发送短信超过2次,校验图片验证码时sessionid={},session={}，图片验证码={}", session.getId(), session.toString(), yzm);
        }
        if (!StringUtils.isEmpty(yzm)) {
            //服务端验证码
            bean.setCode(yzm);
        }
        UserResp userResp = userCenterInterface.sendMobSms(baseReq);
        Boolean clear = userResp.getData().getClear();
        if (clear) {
            userWebService.clearAuthCode(session);
        }
        userResp.getData().setMobileNo(bean.getMobileNo());
        result.setData(userResp.getData());
        result.setCode(userResp.getCode());
        result.setDesc(userResp.getDesc());
        return result;
    }

    /**
     * 用户名注册
     *
     * @param bean
     * @return
     */
    @RealIP
    @RequestMapping(value = "/user/user_register.api", method = RequestMethod.POST)
    public Result<UserRegistResp> userRegister(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        UserRegistResp UserRegistResp;
        HttpSession session = request.getSession();
        Result result = new Result();
        UserRegistResp = userCenterInterface.userRegister(baseReq);
        userWebService.handleSession(UserRegistResp, session);
        result.setCode(UserRegistResp.getCode());
        result.setDesc(UserRegistResp.getDesc());
        result.setData(UserRegistResp.getData());

        return result;
    }

    /**
     * 设置新密码
     *
     * @return
     */
    @RequestMapping(value = "/user/set_new_pwd.api", method = RequestMethod.POST)
    public Result setNewPwd(UserBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
//        BaseReq baseReq2 = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
//        baseReq2.setData(bean);
        Response rsp = userCenterInterface.setNewPwd(baseReq);
        HttpSession session = request.getSession();
        if (session != null) {
            userWebService.deleteCacheAfterSetNewPwd(bean, session);
        }
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        //调用退出服务
//        try {
//            baseReq.setData(bean);
//            BaseResp loginout = userCenterInterface.loginout(baseReq);
//             BeanUtilWrapper.copyPropertiesIgnoreNull(loginout, result);
//        }catch (Exception e){
//            logger.error("用户退出异常", e);
//            result.setCode(ErrorCode.USER_LOGINOUT_REMOTE_ERROR);
//            result.setDesc("退出服务异常");
//        }
        return result;
    }

    /**
     * 用户忘记密码
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/forget_pwd.api", method = RequestMethod.POST)
    public Result forgetPwd(UserBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Response rsp = userCenterInterface.forgetPwd(baseReq);
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 验证手机短信信息
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/verify_sms.api", method = RequestMethod.POST)
    public Result verifySms(UserBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Response rsp = userCenterInterface.verifySms(baseReq);
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 设置中奖追号推送开关
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/update_win_chase_switch.api")
    public Result updateWinAndChaseNumberSwitch(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = userCenterInterface.updateWinAndChaseNumberSwitch(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            logger.error("用户中奖追号推送开关更新服务异常", e);
            result.setCode(ErrorCode.USER_WINANDCHASENUMBERSWITCH_REMOTE_ERROR);
            result.setDesc("中奖追号推送开关更新服务异常");
        }
        return result;
    }

    /**
     * 保存激活数据
     *
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/save_active_data.api", produces = {"application/json;charset=UTF-8"})
    public Result saveActiveData(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = userCenterInterface.saveActiveDate(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            logger.error("保存激活数据远程调用失败,uid={}", bean.getUid(), e);
            result.setCode(ErrorCode.USER_SAVEACTIVE_REMOTE_ERROR);
            result.setDesc("保存激活数据远程调用失败");
        }
        return result;
    }

    /**
     * 退出
     *
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/loginout.api")
    public Result loginout(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        try {
            // todo session
            // request.getSession().removeAttribute(UserConstants.UID_KEY);
            // request.getSession().removeAttribute(UserConstants.PWD_KEY);
            // request.getSession().invalidate();
            baseResp = userCenterInterface.loginout(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);

        } catch (Exception e) {
            logger.error("用户退出异常", e);
            result.setCode(ErrorCode.USER_LOGINOUT_REMOTE_ERROR);
            result.setDesc("退出服务异常");
        }
        return result;
    }

    /**
     * @Author: tiankun
     * @Description: 修改用户信息
     * @Date: 10:08 2017/12/5
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/modify.api", produces = {"application/json;charset=UTF-8"})
    public Result modify(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        try {
            Response response = userCenterInterface.modifyUserInfo(baseReq);
            result.setCode(response.getCode());
            result.setDesc(response.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("修改用户信息发生异常");
            logger.error("修改用户信息发生异常:", e);
        }
        return result;
    }

    /**
     * @Author: tiankun
     * @Description: 用户检测网络统计错误信息
     * @Date: 17:22 2017/12/6
     */
    @RequestMapping(value = "/user/calc_userping_neterror.api", produces = {"application/json;charset=UTF-8"})
    public Result calcUserpingNeterror(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        try {
            Response response = userCenterInterface.calcUserpingNeterror(baseReq);
            result.setCode(response.getCode());
            result.setDesc(response.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("存储用户检测网络信息抛出异常");
            logger.error("存储用户检测网络信息抛出异常",e);
        }
        return result;
    }

    /**
     * @Author: tiankun
     * @Description: 统计网络错误信息
     * @Date: 17:22 2017/12/6
     */
    @RequestMapping(value = "/user/calculate_net_error.api", produces = {"application/json;charset=UTF-8"})
    public Result calculateNeterror(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        try {
            Response response = userCenterInterface.calculateNeterror(baseReq);
            result.setCode(response.getCode());
            result.setDesc(response.getDesc());

        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("存储用户检测网络信息抛出异常");
            logger.error("存储用户检测网络信息抛出异常",e);
        }
        return result;
    }


    /**
     * 检测用户是否可以提交银行卡修改申请
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/check_apply_eligible.api")
    public Result checkApplyEligible(UserBean bean) {
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        try {

            baseResp = userCenterInterface.checkApplyEligible(bean.getUid());
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);

        } catch (Exception e) {
            logger.error("验证服务异常：[uid:{}]", ((BaseBean) baseResp.getData()).getUid());
            result.setCode(ErrorCode.USER_CHECKAPPLYELIGIBLE_REMOTE_ERROR);
            result.setDesc("验证服务异常");
        }
        return result;
    }

    /**
     * 检测用户名是否已使用
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/check_user_nick.api", method = RequestMethod.POST)
    public Result checkUserNick(UserBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Response rsp = userCenterInterface.checkUserNick(baseReq);
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 查询用户密码状态
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/query_default_pwd.api", method = RequestMethod.POST)
    public Result<UserResp> queryUserDefaultPwd(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        UserResp userResp = userCenterInterface.queryUserDefaultPwd(baseReq);
        result.setCode(userResp.getCode());
        result.setDesc(userResp.getDesc());
        result.setData(userResp.getData());
        return result;
    }

    /**
     * 查询个推tag
     *
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/query_gt_tag.api", method = RequestMethod.POST)
    public Result<PushBean> queryGtTag(UserBean bean) {
//        BaseReq<BaseBean> baseReq = new BaseReq<>(SysCodeConstant.USERWEB);
//        baseReq.setData(bean);
        Result result = new Result();
//        BaseResp<BaseBean> resp = new BaseResp<>();
//        resp = userCenterBaseInterface.checkLogin(baseReq);
//        BeanUtilWrapper.copyPropertiesIgnoreNull(resp.getData(), bean);
//        if (!BusiCode.SUCCESS.equals(resp.getCode())) {
//            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
//        } else {
//            bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
//        }
        BaseReq<UserBean> reqBean = new BaseReq<>(SysCodeConstant.USERWEB);
        reqBean.setData(bean);
        BaseResp<PushBean> baseResp = userCenterInterface.queryGtTag(reqBean);
        if ("0".equals(baseResp.getCode())) {
            logger.info("查询个推调用成功，code==" + baseResp.getCode());
            result.setCode(BusiCode.SUCCESS);
            result.setDesc(baseResp.getDesc());
            result.setData(baseResp.getData());
        } else {
            result.setCode(BusiCode.FAIL);
            result.setDesc("调用失败");
            result.setData(BusiCode.FAIL);
        }
        return result;
    }

    /**
     * 查询账户明细
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/query_account.api")
    public Result queryAccount(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        try {
            // 触屏不显示账户收支明细,购彩明细,派奖明细,返款明细
            int source = bean.getSource();
            int flag = bean.getFlag();
            if ((source < 1000 || source >= 5000) && (flag != 14 && flag != 15)) {
                logger.info("触屏不显示账户收支明细,购彩明细,派奖明细,返款明细");
                result.setCode(BusiCode.USER_QUERYACCOUNT_NOT_QUERY);
                result.setDesc("没有该用户的相关记录");
                return result;
            }

            if (flag == 0) {// 默认为交易明细
                flag = 13;
                bean.setFlag(flag);
            }
            baseResp = userCenterInterface.queryAccount(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
            // 暂无数据前端要求code=0
            if (BusiCode.USER_QUERYACCOUNT_NODATA.equals(result.getCode())) {
                result.setCode(BusiCode.SUCCESS);
            }
        } catch (Exception e) {
            logger.error("账户查询服务调用异常", e);
            result.setCode(ErrorCode.USER_QUERYACCOUNT_REMOTE_ERROR);
            result.setDesc("查询失败");
        }
        return result;
    }

    /**
     * 获取客服电话
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/get_service_hot_line.api", produces = {"application/xml;charset=UTF-8"})
    public String getServiceHotline(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp result = userCenterBaseInterface.getServiceHotLine(baseReq);
        return result.getData().toString();
    }

    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/reback_user_photo_status.api")
    public BaseResp rebackUserPhotoStatus(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        return userCenterBaseInterface.rebackUserPhotoStatus(baseReq);
    }

    /**
     * 申请修改银行卡卡号
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/apply_modify_bankcard.api", method = RequestMethod.POST)
    public Result applyModifyBankCard(UserBean bean) {
        Result result = new Result();
        //上传图片
        BaseResp rsp = uploadService.checkLoginAndMutilPart(request);
        if (BusiCode.SUCCESS.equals(rsp.getCode())) {
            UserPhotoDTO data = (UserPhotoDTO) rsp.getData();
            bean.setBankCardFrontUrl(data.getPhotoPath());
            bean.setIdCardFrontUrl(data.getFrontPath());
            bean.setIdCardBackUrl(data.getBackPath());

            //鉴权和申请银行卡修改
            bean.setRealBankCode(request.getParameter("realBankCode"));
            BaseResp baseResp = userCenterBaseInterface.authenticAndApplyModifyBankCard(bean);
            result.setCode(baseResp.getCode());
            result.setDesc(baseResp.getDesc());
        } else {
            result.setCode(rsp.getCode());
            result.setDesc(rsp.getDesc());
        }
        return result;
    }

    /**
     * touch版用户登录
     *
     * @param bean
     * @return
     */
    @RealIP
    @RequestMapping(value = "/user/web_login.api", method = RequestMethod.POST)
    public Result<UserLoginResq> webLogin(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        if (bean.getSource() == 0) {
            bean.setSource(1141); //主站新版专业版
        }
        int source = bean.getSource();
        if (BaseUtil.isHskUser(source) || BaseUtil.isAiduobaoUser(source)
                || BaseUtil.isLicaidiUser(source) || BaseUtil.isGongjijingUser(source)
                || BaseUtil.isFinancialManageUser(source)) {
            //是慧刷卡及理财 渠道不需要设置IP
            bean.setIpAddr("");
        }
        UserLoginResq userLoginResq = userCenterInterface.webLogin(baseReq);
        Result result = new Result();
        result.setCode(userLoginResq.getCode());
        result.setDesc(userLoginResq.getDesc());
        result.setData(userLoginResq.getData());
        return result;
    }

    /**
     * 更换手机号检测
     *
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/change_mobile_check.api")
    public Result changeMobileCheck(UserBean bean) {
        Result result = new Result();
        BaseResp baseResp = new BaseResp();
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        try {
            baseResp = userCenterInterface.changeMobileCheck(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            logger.error("更换手机号服务调用失败，uid:{}", bean.getUid(), e);
            result.setCode(ErrorCode.USER_CHANGE_MOBILE_CHECK_REMOTE_ERROR);
            result.setDesc("验证服务调用失败");
        }
        return result;
    }

    /**
     * 查询身份证银行卡绑定信息
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/query_idbank_binding.api")
    public Result queryIdBankBinding(UserBean bean) {
        Result result = new Result();
        BaseResp baseResp = new BaseResp<>();
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        try {
            baseResp = userCenterInterface.queryIdBankBinding(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            logger.error("查询身份证银行卡绑定信息异常，[uid:{}]", bean.getUid(), e);
            result.setCode(ErrorCode.USER_IDBANK_BINDING_REMOTE_ERROR);
            result.setDesc("查询服务调用失败");
        }
        return result;
    }

    /**
     * 银行卡号校验
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/check_bank_card.api")
    public Result checkBankCard(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        Result result = userCenterInterface.checkBankCard(baseReq);
        return result;
    }

    /**
     * 查询身份证、手机号绑定状态
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping("/user/query_userinfo_bind.api")
    public Result<IdcardBindingDTO> queryUserInfoBind(BaseBean bean) {
        BaseResp resp = userCenterInterface.queryUserInfoBind(new BaseReq<>(bean,SysCodeConstant.USERWEB));
        Result<IdcardBindingDTO> result = new Result<>();
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp, result);
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/bind_mobileno2caiyi.api")
    public Result<AlipayLoginResq> bindmobileno2caiyi(AlipayLoginBean bean) {
        BaseReq<AlipayLoginBean> baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp<AlipayLoginResq> result = userCenterInterface.bindmobileno2caiyi(baseReq);
        Result<AlipayLoginResq> response = new Result<>();
        response.setCode(result.getCode());
        response.setDesc(response.getDesc());
        response.setData(result.getData());
        return response;
    }

    /**
     * 忘记密码-发送短信验证码
     *
     * @param bean
     * @return
     */
    @Deprecated
    @PostMapping(value = "/user/forget_pwd_sendSMS.api")
    public Result forgetPWDSendSMS(UserBean bean, HttpSession session) {
        Result result = new Result();
        String rand = (String) session.getAttribute(UserConstants.SESSION_YZM);
        logger.info("session中保存的验证码：" + rand);
        if (StringUtils.isBlank(rand)) {
            result.setCode(ErrorCode.USER_PICAUTH_ERROR);
            result.setDesc("验证码错误");
            return result;
        }
        bean.setRand(rand);
        Response res = userCenterInterface.forgetPWDSendSMS(bean);
        result.setCode(res.getCode());
        result.setDesc(res.getDesc());
        return result;
    }

    /**
     * lilei
     * 校验短信验证码，重置密码
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @Deprecated
    @PostMapping(value = "/user/forget_pwd_resetPwd.api")
    public Result resetPassword(UserBean bean) throws Exception {
        Result result = new Result();
        Response response = userCenterInterface.forgetPwdRestPwd(bean);
        result.setCode(response.getCode());
        if (response.getCode().equals("0")) {
            result.setDesc(response.getDesc() + "新密码为" + bean.getPwd());
            return result;
        } else {
            result.setDesc(response.getDesc());
            return result;
        }
    }

    @RequestMapping(value = "/user/zfb_get_caiyi_account.api")
    public Result zfbGetCaiyi(AlipayLoginBean bean) {
        BaseResp baseResp = userCenterInterface.zfbGetCaiyi(bean);
        Result result = new Result();
        result.setCode(baseResp.getCode());
        result.setDesc(baseResp.getDesc());
        result.setData(baseResp.getData());
        return result;
    }


    /**
     * 激活回调
     *
     * @return
     */
    @RealIP
    @RequestMapping("/user/invoke.api")
    public Result activationCallback(UserBean bean) {
        bean.setAid(bean.getIdfa());
        logger.info("imei:" + bean.getImei());
        BaseReq<UserBean> res = new BaseReq(SysCodeConstant.USERWEB);
        res.setData(bean);
        Response response = userCenterInterface.activationCallback(res);
        Result resp = new Result();
        resp.setCode(response.getCode());
        resp.setDesc(response.getDesc());

        return resp;
    }

    /**
     * 微信快登校验短信验证码，查询手机号绑定彩亿账户列表
     */
    @RequestMapping("/user/wechat_get_mobilebind_account.api")
    public Result getMobileBindAccountWechat(WeChatBean bean) {
        bean.setMphone(CardMobileUtil.decryptMobile(bean.getMphone()));
        BaseResp resp = userCenterInterface.getMobileBindAccountWechat(new BaseReq<>(bean,SysCodeConstant.USERWEB));
        Result result = new Result<>();
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp, result);
        return result;

    }

    /**
     * 绑定支付宝到已有彩亿账号
     *
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/zfb_bind2caiyi.api")
    public Result<AlipayLoginResq> zfbbind2caiyi(AlipayLoginBean bean) {
        BaseReq<AlipayLoginBean> baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp<AlipayLoginResq> result = userCenterInterface.zfbbind2caiyi(baseReq);
        Result<AlipayLoginResq> response = new Result<>();
        response.setCode(result.getCode());
        response.setDesc(response.getDesc());
        response.setData(result.getData());
        return response;
    }

    /**
     * 微信开发平台注册
     *
     * @param bean
     * @return
     */
    @RealIP
    @RequestMapping(value = "/user/wechat_register_user.api")
    public Result registerWechatUser(WeChatBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.USERWEB);
        BaseResp baseResp = new BaseResp();
        try {
            bean.setRequestURI(request.getRequestURI());
            baseResp = userCenterInterface.beforeWechatRegister(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                return result;
            }

            baseResp = userCenterInterface.getWechatUserInfo(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                result.setData(null);
                return result;
            }

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp.getData(), bean);
            baseResp = userCenterInterface.registerUser(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                result.setData(null);
                return result;
            }

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp.getData(), bean);
            baseResp = userCenterInterface.loginAfterBind(baseReq);

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);

        } catch (Exception e) {
            logger.error("微信开发平台注册失败,[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("微信开发平台注册失败");
        }
        return result;
    }

    /**
     * 通过微信code登录
     *
     * @param bean
     * @return
     */
    @RealIP
    @RequestMapping(value = "/user/wechat_login.api")
    public Result wechatLogin(WeChatBean bean) {
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.USERWEB);
        BaseResp baseResp = new BaseResp();
        Result result = new Result();
        try {
            baseResp = userCenterInterface.wechatLogin(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            logger.error("通过微信code登录异常，[uid:{}]", bean.getUid(), e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("微信登录失败");
        }
        return result;
    }

    /**
     * 绑定9188ID到微信AppID
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/wechat_bind_caiyiid.api")
    public Result wechatBindCaiyiId(WeChatBean bean) {
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.USERWEB);
        BaseResp baseResp = new BaseResp();
        Result result = new Result();
        try {
            bean.setPwd(SecurityTool.iosdecrypt(bean.getPwd()));
            baseResp = userCenterInterface.bindWechatParamCheck(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                return result;
            }

            baseResp = userCenterInterface.getWechatUserInfo(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                result.setData(null);
                return result;
            }
            baseResp = userCenterInterface.bind9188UserId2WXAppId(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                result.setData(null);
                return result;
            }

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp.getData(), bean);
            bean.setPwd(MD5Helper.md5Hex(bean.getPwd()));
            baseResp = userCenterInterface.loginAfterBind(baseReq);

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("绑定9188ID到微信AppID失败");
            logger.error("绑定9188ID到微信AppID异常，[uid:{}]", bean.getUid(), e);
        }
        return result;
    }

    /**
     * 校验短信验证码，绑定手机号到9188账号并登录
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/wechat_bind_mobileno.api")
    public Result<WeChatDTO> bindmobileno2caiyi(WeChatBean bean) {
        BaseReq baseReq = new BaseReq(bean,SysCodeConstant.USERWEB);
        BaseResp baseResp = new BaseResp();
        Result result = new Result();
        try {
            baseResp = userCenterInterface.bindWechatMobilenoParamCheck(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                return result;
            }

            baseResp = userCenterInterface.getWechatUserInfo(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                result.setData(null);
                return result;
            }
            baseResp = userCenterInterface.bindMobilenoToCaiyi(baseReq);
            if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
                result.setData(null);
                return result;
            }

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp.getData(), bean);
            baseResp = userCenterInterface.loginAfterBind(baseReq);

            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("校验短信验证码，绑定手机号到9188账号并登录失败");
            logger.error("校验短信验证码，绑定手机号到9188账号并登录异常，[uid:{}]", bean.getUid(), e);
        }
        return result;
    }

    /**
     * 查询短信验证码,临时使用
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/query_sms_authcode.api")
    public Result querySmsAuthCode(UserBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        Result result = new Result();
        try {
            baseReq.setData(bean);
            BaseResp baseResp = userCenterInterface.querySmsAuthCode(baseReq);
            result.setCode(baseResp.getCode());
            result.setDesc(baseResp.getDesc());
            if (null != baseResp.getData()) {
                result.setData(baseResp.getData());
            }
        } catch (Exception e) {
            logger.info("查询短信验证码发生异常");
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询短信验证码发生异常");
        }
        return result;
    }

}
