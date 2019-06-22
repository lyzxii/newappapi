package com.caiyi.lottery.tradesystem.usercenter.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.usercenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.usercenter.util.DecryptUtil;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.service.*;
import com.caiyi.lottery.tradesystem.usercenter.service.impl.UserCenterServiceImpl;
import constant.UserConstants;
import dto.UserBasicDTO;
import dto.UserLoginDTO;
import dto.UserRegistDTO;
import org.apache.catalina.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.*;
import util.UserUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;

/**
 * 用户中心——用户
 */
@RestController
public class UserController {
    @Autowired
    @Qualifier("userCenterService")
    private UserCenterService userCenterService;
    @Autowired
    private UserRecordService userRecordService;
    @Autowired
    private PersonalInfoService personalInfoService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private ModifyUserInfoService modifyUserInfoService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;

    private Logger logger = LoggerFactory.getLogger(UserController.class);
    @RequestMapping(value = "/user/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("用户中心user-center启动运行正常");
        return response;
    }
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/user/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_user");
        redisClient.exists(cacheBean,logger, SysCodeConstant.USERCENTER);
        dualMapper.getAgentNextval();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("用户中心服务运行正常");
        return response;
    }

    @Deprecated
    @RequestMapping(value = "/user/mobile_register_check.api")
    public UserRegistResp mobileRegisterCheck(@RequestBody UserBean bean) {
        UserRegistResp userRegistResp = new UserRegistResp();
        registerService.mobileRegisterCheck(bean);
        userRegistResp.setCode(bean.getBusiErrCode() + "");
        userRegistResp.setDesc(bean.getBusiErrDesc());
        UserRegistDTO userRegistDTO = new UserRegistDTO();
        userRegistDTO.setTemporaryId(bean.getTemporaryId());
        userRegistResp.setData(userRegistDTO);
        return userRegistResp;
    }

    /**
     * 手机注册
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/mobile_register.api")
    public UserRegistResp mobileRegister(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserRegistResp userRegistResp = new UserRegistResp();
        try {
            //手机号,密码解密
            Map<String, String> map = DecryptUtil.decryptByAesBase64(bean.getPwd(), null, bean.getMobileNo(), null, null);
            if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.MOBILENO_KEY))) {
                bean.setPwd(map.get(UserConstants.PWD_KEY));
                bean.setMobileNo(map.get(UserConstants.MOBILENO_KEY));
            } else {
                userRegistResp.setCode(ErrorCode.USER_PARAMDECODE_ERROR);
                userRegistResp.setDesc("注册参数失败错误");
                logger.info("注册参数解密失败-pwd-{},mobileno-{}", bean.getPwd(), bean.getMobileNo());
                return userRegistResp;
            }
            registerService.setBaseData(bean);
            registerService.checkYzm(bean);
            if (BusiCode.SUCCESS.equals(String.valueOf(bean.getBusiErrCode()))) {
                registerService.queryagentid(bean);
                registerService.checkminsRegister(bean);
                if (BusiCode.SUCCESS.equals(String.valueOf(bean.getBusiErrCode()))) {
                    registerService.registerUser(bean);
                }
                userRegistResp = registerService.phoneRegisterResult(bean);
            } else {
                userRegistResp.setCode(String.valueOf(bean.getBusiErrCode()));
                userRegistResp.setDesc(bean.getBusiErrDesc());
            }
        } catch (Exception e) {
            logger.error("手机注册报错-{}", bean.getMobileNo(), e);
            userRegistResp.setCode(BusiCode.FAIL);
            userRegistResp.setDesc("手机注册失败");
        }

        return userRegistResp;
    }


    @RequestMapping(value = "/user/get_user_whitelist_grade.api")
    public UserPersonalInfoResq getUserWhitelistGrade(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserPersonalInfoResq userPersonalInfoResq = personalInfoService.getUserWhitelistGrade(bean);
        return userPersonalInfoResq;
    }

    @RequestMapping(value = "/user/personal_center_info.api")
    public UserPersonalInfoResq personalCenterInfo(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserPersonalInfoResq userPersonalInfoResq = personalInfoService.personalCenterInfo(bean);
        return userPersonalInfoResq;
    }

    @RequestMapping(value = "/user/mobile_login.api")
    public UserLoginResq mobileLogin(@RequestBody BaseReq<UserBean> baseReq) throws Exception {
        UserBean bean = baseReq.getData();
        String uid = URLDecoder.decode(bean.getUid(), "utf-8");
        bean.setUid(uid);
        //basebean没有flag参数，使用pn代替
        bean.setPn(bean.getFlag());
        String imei = bean.getImei(); //imei 号
        logger.info("usercenter login,imei=" + imei);
        if (CheckUtil.isNullString(imei)) {
            imei = "";
        }
        UserLoginResq userLoginResq = new UserLoginResq();
        try {
            boolean loginParamCorrect = loginService.checkLoginParam(bean);
            if (!loginParamCorrect) {
                userLoginResq.setCode(BusiCode.USER_LOGIN_PARAM_ERROR);
                userLoginResq.setDesc("登录参数不正确");
                return userLoginResq;
            }
            UserBean tempBean = new UserBean();
            tempBean.setUid(bean.getUid());
            tempBean.setPwd(bean.getPwd());
            tempBean.setNewpwd(bean.getNewpwd());
            tempBean.setIpAddr(bean.getIpAddr());
            tempBean.setImei(imei);
            tempBean.setSource(bean.getSource());
            //走密码登录使用flag
            tempBean.setFlag(bean.getPn());
            loginService.login(tempBean);
            if (tempBean.getBusiErrCode() == 0) {
                logger.info("登录成功,uid=" + tempBean.getUid());
                //TODO 暂时不做session登入 app全是token登入
                //   login_result(tempBean, request);
                modifyUserInfoService.saveimei(bean);
                bean.setUid(tempBean.getUid());
                bean.setPwd(tempBean.getPwd());
                bean.setCuserId(tempBean.getCuserId());
                bean.setBusiErrDesc("登录成功");
            } else {
                logger.info("登录失败,uid=" + tempBean.getUid() + ",errDesc=" + tempBean.getBusiErrDesc());
                bean.setBusiErrCode(tempBean.getBusiErrCode());
                bean.setBusiErrDesc(tempBean.getBusiErrDesc());
            }
            userLoginResq = loginService.afterLogin(bean);
        } catch (Exception e) {
            logger.error("登录出现异常,uid-{}", bean.getUid(), e);
            userLoginResq.setCode(BusiCode.FAIL);
            userLoginResq.setDesc("登录失败");
            return userLoginResq;
        }
        return userLoginResq;
    }


    /**
     * @Author: tiankun
     * @Description: 修改用户信息 （根据flag来决定修改内容）
     * @Date: 13:43 2017/11/30
     */
    @RequestMapping(value = "/user/modify_user_info.api")
    public BaseResp modifyUserInfo(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        Result result = new Result<>();
        try {
            int flag = bean.getFlag();
            logger.info("flag=" + bean.getFlag());
            // 修改登录密码和绑定身份证的流程迁移到新类中.
            if (flag == UserUtil.UPDATE_PASS) {
                //修改用户登录密码
                logger.info("修改用户登录密码,用户名=" + bean.getUid());
                result = userCenterService.modifyLoginPwd(bean, result);
                baseResp.setCode(result.getCode());
                baseResp.setDesc(result.getDesc());
                return baseResp;
            } else if (flag == UserUtil.UPDATE_NAME) {
                //绑定身份证
                logger.info("绑定身份证,用户名=" + bean.getUid());
                result = userCenterService.bindIdcard(bean, result);
                baseResp.setCode(result.getCode());
                baseResp.setDesc(result.getDesc());
                return baseResp;
            }
            //删除token缓存
            if (result.getCode().equals("0") && bean.getFlag() == UserUtil.UPDATE_PASS) {
                loginService.loginout(bean);
            }
            // TODO 此接口flag暂时只有2和7
            //result = userCenterService.modifyUser(bean,result);
        } catch (Exception e) {
            baseResp.setCode(FAIL);
            baseResp.setDesc("修改用户信息程序异常");
            logger.error("修改用户信息发生异常", e);
        }
        return baseResp;
    }

    /**
     * 退出登录
     *
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/loginout.api")
    BaseResp loginout(@RequestBody BaseReq<BaseBean> baseReq) {
        BaseBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            loginService.loginout(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.error("退出异常：[uid:{},appid:[]]", bean.getUid(), bean.getAppid());
            baseResp.setCode(ErrorCode.USER_LOGINOUT_PROCESS_ERROR);
            baseResp.setDesc("退出异常");
        }
        return baseResp;
    }

    /**
     * @Author: tiankun
     * @Description: 用户检测网络统计错误信息
     * @Date: 17:29 2017/12/6
     */
    @RequestMapping(value = "/user/calc_userping_neterror.api")
    public Response calcUserpingNeterror(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Response resp = new Response();
        try {
            resp = userRecordService.calcUserpingNeterror(bean, resp);
        } catch (Exception e) {
            logger.error("用户检测网络统计错误信息发生异常", e);
            resp.setCode(String.valueOf(BusiCode.FAIL));
            resp.setDesc("插入用户检测网络统计信息执行失败");
        }
        return resp;
    }

    /**
     * @Author: tiankun
     * @Description: 统计网络错误信息
     * @Date: 17:29 2017/12/6
     */
    @RequestMapping(value = "/user/calculate_net_error.api")
    public Response calculateNeterror(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Response resp = new Response();
        try {
            resp = userRecordService.calculateNeterror(bean, resp);
        } catch (Exception e) {
            logger.error("统计网络错误信息发生异常", e);
            resp.setCode(String.valueOf(BusiCode.FAIL));
            resp.setDesc("统计网络错误信息发生异常");
        }
        return resp;
    }

    /**
     * 用户名注册
     *
     * @param baseReq
     * @return
     * @mender 571
     * @create 2017-11-28 16:55:48
     */
    @RequestMapping(value = "/user/user_register.api")
    public UserRegistResp userRegister(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserRegistResp rsp = new UserRegistResp();
        UserRegistDTO userRegistDTO = new UserRegistDTO();
        int baseData;
        try {
            //密码,手机号解密
            int decryptResp = decryptPwdAndMobileNo(bean);
            if (0 == decryptResp) {
                rsp.setCode(bean.getBusiErrCode() + "");
                rsp.setDesc(bean.getBusiErrDesc());
                return rsp;
            }
            int flag = registerService.checkParamByVerifySms(bean);
            if (1 == flag) {
                baseData = registerService.setBaseData(bean);
                if (1 == baseData) {
                    try {
                        //验证短信信息
                        registerService.verifyMobCode(bean, bean.getMobileNo(), bean.getYzm(), bean.getTid(), true);
                    } catch (Exception e) {
                        bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKMSM_ERROR));
                        bean.setBusiErrDesc("验证手机短信号码失败");
                        logger.error("验证手机短信号码失败,手机号:" + bean.getMobileNo(), e);
                    }
                    if (BusiCode.SUCCESS.equals(String.valueOf(bean.getBusiErrCode()))) {
                        registerService.registerSourceUser(bean);
                    }
                    userRegistDTO.setUid(bean.getUid());
                    userRegistDTO.setPwd(bean.getPwd());
                    userRegistDTO.setLogintype(bean.getLogintype());
                    registerService.registerResult(bean, userRegistDTO);
                }
            }
        } catch (Exception e) {
            logger.info("注册发生异常,{}", e);
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REGISTEXCEPTION_ERROR));
            bean.setBusiErrDesc("注册发生异常");
        }
        rsp.setData(userRegistDTO);
        rsp.setCode(bean.getBusiErrCode() + "");
        rsp.setDesc(bean.getBusiErrDesc());
        return rsp;
    }

    /**
     * 发送短信验证码(新)
     *
     * @param baseReq
     * @return
     * @mender 571
     */
    @RequestMapping(value = "/user/send_mob_sms.api")
    public UserResp sendMobSms(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserResp userResp = new UserResp();
        try {
            int ret = userCenterService.checkParamByMobSms(bean);
            if (1 == ret) {
                userCenterService.sendMobMsg(bean);
            }
            UserBasicDTO userBasicDTO = new UserBasicDTO();
            userBasicDTO.setClear(true);
            userBasicDTO.setMobileNo(bean.getMobileNo());
            userResp.setData(userBasicDTO);
            userResp.setCode(bean.getBusiErrCode() + "");
            userResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.error("发送短信验证码发生异常:", e);
            userResp.setCode(ErrorCode.USER_SENDSMS_ERROR);
            userResp.setDesc("发送短信验证码发生异常");
        }
        return userResp;
    }

    /**
     * 设置新密码
     *
     * @param baseReq
     * @return
     * @mender 571
     * @create 2017-11-27 11:55:30
     */
    @RequestMapping(value = "/user/set_new_pwd.api")
    public Response setNewPwd(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Response rsp = new Response();
        int flag = userCenterService.checkParamBySet(bean);
        if (1 == flag) {
            try {
                userCenterService.setNewPwd(bean);
                loginService.loginout(bean);
                if ("注销成功".equals(bean.getBusiErrDesc())) {
                    bean.setBusiErrDesc("设置密码成功");
                }
            } catch (Exception e) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("设置密码失败");
                logger.error("设置密码失败,用户名:" + bean.getUid() + ",手机号:" + bean.getMobileNo(), e);
            }
        }
        rsp.setCode(bean.getBusiErrCode() + "");
        rsp.setDesc(bean.getBusiErrDesc());
        return rsp;
    }

    /**
     * 用户忘记密码
     *
     * @param baseReq
     * @return
     * @mender 571
     * @create 2017-11-27 09:52:24
     */
    @RequestMapping(value = "/user/forget_pwd.api")
    public Response forgetPwd(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Response rsp = new UserResp();
        try {
            int decryptResp = decryptMobileNo(bean);
            if (0 == decryptResp) {
                rsp.setCode(bean.getBusiErrCode() + "");
                rsp.setDesc(bean.getBusiErrDesc());
                return rsp;
            }
            int flag = userCenterService.checkParamByForget(bean);
            if (1 == flag) {
                userCenterService.matchUidAndMobile(bean);
            }
            rsp.setCode(bean.getBusiErrCode() + "");
            rsp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.error("用户忘记密码发生异常:", e);
            rsp.setCode(ErrorCode.USER_FORGETPWD_EXCEPTION);
            rsp.setDesc("用户忘记密码发生异常");
        }
        return rsp;
    }

    /**
     * 手机号解密
     *
     * @param bean
     * @return
     * @throws Exception
     */
    private int decryptMobileNo(UserBean bean) throws Exception {
        int flag = 1;
        Map<String, String> map = DecryptUtil.decryptByAesBase64(null, null, bean.getMobileNo(), null, null);
        if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.MOBILENO_KEY))) {
            bean.setMobileNo(map.get(UserConstants.MOBILENO_KEY));
        } else {
            logger.info("手机号解密失败");
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
            bean.setBusiErrDesc("参数解密失败");
            flag = 0;
        }
        return flag;
    }

    /**
     * 验证短信验证码
     *
     * @param baseReq
     * @return
     * @mender 571
     * @create 2017-11-28 15:11:36
     */
    @RequestMapping(value = "/user/verify_sms.api")
    public Response verifySms(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Response rsp = new UserResp();
        try {
            int decryptResp = decryptMobileNo(bean);
            if (0 == decryptResp) {
                rsp.setCode(bean.getBusiErrCode() + "");
                rsp.setDesc(bean.getBusiErrDesc());
                return rsp;
            }
            int flag = registerService.checkParamByVerifySms(bean);
            if (1 == flag) {
                registerService.verifyMobCode(bean, bean.getMobileNo(), bean.getYzm(), bean.getTid(), true);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKMSM_ERROR));
            bean.setBusiErrDesc("验证手机短信号码失败");
            logger.error("验证手机短信号码失败,手机号:" + bean.getMobileNo(), e);
        }
        rsp.setCode(bean.getBusiErrCode() + "");
        rsp.setDesc(bean.getBusiErrDesc());
        return rsp;
    }

    /**
     * 检测用户名
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/check_user_nick.api")
    public Response checkUserNick(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Response rsp = new UserResp();
        int checkUserNick = userCenterService.checkParamByCheckUserNick(bean);
        if (Integer.parseInt(BusiCode.SUCCESS) == checkUserNick) {
            userCenterService.checkUserNick(bean);
        }
        rsp.setCode(bean.getBusiErrCode() + "");
        rsp.setDesc(bean.getBusiErrDesc());
        return rsp;
    }

    /**
     * 查询用户密码状态
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/query_default_pwd.api")
    public UserResp queryUserDefaultPwd(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserResp userResp = new UserResp();
        UserBasicDTO user = new UserBasicDTO();
        try {
            String flag = userCenterService.queryUserDefaultPwd(bean);
            user.setFlag(flag);
        } catch (Exception e) {
            logger.info("查询用户密码出错出错,用户名:" + bean.getUid(), e);
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_QUERYUSERPWD_ERROR));
            bean.setBusiErrDesc("查询用户密码出错");
        }
        userResp.setCode(bean.getBusiErrCode() + "");
        userResp.setDesc(bean.getBusiErrDesc());
        userResp.setData(user);
        return userResp;
    }

    @RequestMapping(value = "/user/web_login.api")
    public UserLoginResq webLogin(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        UserLoginResq result = new UserLoginResq();
        UserLoginDTO dto = new UserLoginDTO();
        loginService.login(bean);
//            dto.setNotice(noticeFlag);
        dto.setUid(bean.getUid());
        dto.setUserid(bean.getCuserId());
        dto.setAppid(bean.getAppid());
        dto.setAccesstoken(bean.getAccesstoken());
//            dto.setWhitelist(whitelist);
        dto.setHasVip(bean.getHasVip());
        if (bean.getBusiErrCode() == 0) {
            result.setCode(bean.getBusiErrCode() + "");
            result.setDesc("登陆成功");
            result.setData(dto);
        } else {
            result.setCode(bean.getBusiErrCode() + "");
            result.setDesc("登陆失败");
            result.setData(dto);
        }
        return result;
    }

    @RequestMapping(value = "/user/check_bank_card.api")
    public Result checkBankCard(@RequestBody BaseReq<UserBean> baseReq) {
        UserBean bean = baseReq.getData();
        Result result = userCenterService.checkBankCard(bean);
        logger.info("身份证验证结果，" + result.toJson());
        return result;
    }

    /**
     * 银行卡鉴权和申请修改
     *
     * @return
     * @mender 571
     */
    @RequestMapping(value = "/user/apply_modify_bankcard.api")
    public BaseResp authenticAndApplyModifyBankCard(@RequestBody UserBean bean) {
        BaseResp rsp = new BaseResp();
        //密码,手机号,银行卡解密
        try {

            int dcreptRsp = decryptBankCard(bean);
            if (0 == dcreptRsp) {
                logger.info("解密银行卡卡号失败");
                rsp.setCode(bean.getBusiErrCode() + "");
                rsp.setDesc(bean.getBusiErrDesc());
                return rsp;
            }
            logger.info("银行卡鉴权:uid==" + bean.getUid() + "bankCard==" + bean.getBankCard() + "mobileNo==" + bean.getMobileNo());
            boolean flag = userCenterService.authenticBankCard(bean);
            if (false == flag) {
                logger.info("银行卡鉴权失败");
                rsp.setCode(bean.getBusiErrCode() + "");
                rsp.setDesc(bean.getBusiErrDesc());
                return rsp;
            }
            logger.info("银行卡换绑申请：uid==" + bean.getUid());
            userCenterService.applyModifyBankCard(bean);
            rsp.setCode(bean.getBusiErrCode() + "");
            rsp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.info("申请银行修改发生异常");
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("申请银行修改发生异常");
        }
        return rsp;
    }

    /**
     * 解密银行卡
     *
     * @param bean
     * @return
     * @throws Exception
     */
    private int decryptBankCard(UserBean bean) throws Exception {
        int flag = 1;
        Map<String, String> map = DecryptUtil.decryptByAesBase64(null, bean.getBankCard(), bean.getMobileNo(), null, null);
        if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.BANKCARD_KEY)) && !StringUtils.isEmpty(map.get(UserConstants.MOBILENO_KEY))) {
            bean.setBankCard(map.get(UserConstants.BANKCARD_KEY));
            bean.setMobileNo(map.get(UserConstants.MOBILENO_KEY));
        }else if(map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.BANKCARD_KEY))){
            bean.setBankCard(map.get(UserConstants.BANKCARD_KEY));
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
            bean.setBusiErrDesc("申请修改银行卡参数解密失败");
            flag = 0;
        }
        return flag;
    }

    /**
     * 解密密码/银行卡/手机号
     *
     * @param bean
     */
    private int decryptPwdBankCardMobile(UserBean bean) throws Exception {
        int flag = 1;
        Map<String, String> map = DecryptUtil.decryptByAesBase64(bean.getPwd(), bean.getBankCard(), bean.getMobileNo(), null, null);
        if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.PWD_KEY)) && !StringUtils.isEmpty(map.get(UserConstants.BANKCARD_KEY))) {
            bean.setPwd(map.get(UserConstants.PWD_KEY));
            bean.setBankCard(map.get(UserConstants.BANKCARD_KEY));
            bean.setMobileNo(map.get(UserConstants.MOBILENO_KEY));
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
            bean.setBusiErrDesc("申请修改银行卡参数解密失败");
            flag = 0;
        }
        return flag;
    }


    /**
     * 忘记密码：校验验证码后发送手机验证码
     *
     * @param bean
     * @return
     * @throws UnsupportedEncodingException
     */
    @Deprecated
    @PostMapping("/user/forget_pwd_sendSMS.api")
    public Response forgetPWDYzm(@RequestBody UserBean bean) throws UnsupportedEncodingException {
        //汉字转码
        String uid = URLDecoder.decode(bean.getUid(), "utf-8");
        bean.setUid(uid);
        Response res = new Response();
        userCenterService.CheckYZM(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            res.setCode(bean.getBusiErrCode() + "");
            res.setDesc(bean.getBusiErrDesc());

        } else {
            userCenterService.forgetPwdCheckPreCondition(bean);
            res.setCode(bean.getBusiErrCode() + "");
            res.setDesc(bean.getBusiErrDesc());
        }

        logger.info("忘记密码-发送短信验证码：uid:" + bean.getUid() + ",mobile:" + bean.getMobileNo() + ",code:" + res.getCode() + ",desc:" + res.getDesc());
        return res;
    }

    /**
     * lilei
     * 忘记密码：校验手机号验证码重置密码
     */
    @Deprecated
    @PostMapping("/user/forget_pwd_resetPwd.api")
    public Response resetPassword(@RequestBody UserBean bean) throws Exception {
        return userCenterService.forgetPwdRestPwd(bean);
    }

    /**
     * 解密密码和手机号
     *
     * @param bean
     */
    private int decryptPwdAndMobileNo(UserBean bean) throws Exception {
        int flag = 1;
        Map<String, String> map = DecryptUtil.decryptByAesBase64(bean.getPwd(), null, bean.getMobileNo(), null, null);
        if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.MOBILENO_KEY))) {
            bean.setPwd(map.get(UserConstants.PWD_KEY));
            bean.setMobileNo(map.get(UserConstants.MOBILENO_KEY));
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
            bean.setBusiErrDesc("用户名注册参数错误");
            logger.info("用户名注册参数解密失败");
            flag = 0;
        }
        return flag;
    }

    /**
     * 查询短信验证码
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/query_sms_authcode.api")
    public BaseResp querySmsAuthCode(@RequestBody BaseReq<UserBean> baseReq) {
        BaseResp baseResp = new BaseResp();
        if (null != baseReq.getData() && !StringUtils.isEmpty(baseReq.getData().getMobileNo())) {
            baseResp = userCenterService.querySmsAuthCode(baseReq.getData());
        } else {
            baseResp.setCode(BusiCode.USER_PARAM_NULL);
            baseResp.setDesc("输入手机号为空");
        }
        return baseResp;
    }
}
