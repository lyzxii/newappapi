package com.caiyi.lottery.tradesystem.usercenter.controller;

import bean.AlipayLoginBean;
import bean.WeChatBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.service.AlipayLoginService;
import com.caiyi.lottery.tradesystem.usercenter.service.ModifyUserInfoService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserRecordService;
import dto.AlipayLoginDTO;
import com.caiyi.lottery.tradesystem.usercenter.service.WeChatService;
import dto.UserInfoDTO;
import dto.WeChatDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.AlipayLoginResq;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.SUCCESS;

/**
 * 快登controller
 *
 * @author GJ
 * @create 2017-12-14 20:31
 **/
@Slf4j
@RestController
public class UserAllyLoginController {
    @Autowired
    private  AlipayLoginService alipayLoginService;
    @Autowired
    private  ModifyUserInfoService modifyUserInfoService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private UserRecordService userRecordService;


    @RequestMapping(value = "/user/alipay_bind.api")
    public AlipayLoginResq alipayBind(@RequestBody BaseReq<AlipayLoginBean> baseReq){
        AlipayLoginResq alipayLoginResq = new AlipayLoginResq();
        AlipayLoginBean alipayLoginBean = baseReq.getData();
        alipayLoginService.alipayFirstBind(alipayLoginBean);
        try {
            alipayLoginService.bindData(alipayLoginBean);
        } catch (Exception e) {
            userRecordService.addUserOperLog(alipayLoginBean, "支付宝用户绑定", "[失败] " + alipayLoginBean.getBusiErrDesc());
            log.info("支付宝用户绑定失败,uid=" + alipayLoginBean.getUid() + ",comform=" + alipayLoginBean.getComeFrom() + ",errDesc=" + alipayLoginBean.getBusiErrDesc());
            alipayLoginBean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_BIND_FAIL));
            alipayLoginBean.setBusiErrDesc(alipayLoginBean.getBusiErrDesc());
            log.info("数据入库失败");
        }
        if (Integer.valueOf(BusiCode.SUCCESS).intValue() != alipayLoginBean.getBusiErrCode()) {
            alipayLoginResq.setCode(alipayLoginBean.getBusiErrCode() + "");
            alipayLoginResq.setDesc(alipayLoginBean.getBusiErrDesc());
            return alipayLoginResq;
        }
        alipayLoginService.setloginData(alipayLoginBean, alipayLoginResq);
        AlipayLoginDTO alipayLoginDTO = alipayLoginResq.getData();
        alipayLoginDTO.setUid(alipayLoginBean.getUid());
        return alipayLoginResq;
    }

    @RequestMapping(value = "/user/alipay_login_check.api")
    public AlipayLoginResq alipayLogin(@RequestBody BaseReq<AlipayLoginBean> baseReq){
        AlipayLoginResq alipayLoginResq = new AlipayLoginResq();
        AlipayLoginBean alipayLoginBean = baseReq.getData();

        alipayLoginService.checkParam4zfbBindCheck(alipayLoginBean);
        if (alipayLoginBean.getBusiErrCode() != Integer.valueOf(BusiCode.SUCCESS).intValue()) {
            alipayLoginResq.setCode(alipayLoginBean.getBusiErrCode() + "");
            alipayLoginResq.setDesc(alipayLoginBean.getBusiErrDesc());
            return alipayLoginResq;
        }
        alipayLoginService.getAlipayOauthData(alipayLoginBean);
        if (alipayLoginBean.getBusiErrCode() != Integer.valueOf(BusiCode.SUCCESS).intValue()) {
            alipayLoginResq.setDesc(alipayLoginBean.getBusiErrDesc());
            alipayLoginResq.setCode(alipayLoginBean.getBusiErrCode() + "");
            return alipayLoginResq;
        }
        alipayLoginResq = alipayLoginService.alipayAuthCheck(alipayLoginBean);
        alipayLoginService.loginByAlipay(alipayLoginBean, alipayLoginResq);
        if (BusiCode.USER_ALLY_CHECK_SUCCESS==alipayLoginResq.getCode()||BusiCode.SUCCESS==alipayLoginResq.getCode()){
            modifyUserInfoService.saveimei(alipayLoginBean);
        }

        return alipayLoginResq;
    }

    @RequestMapping(value = "/user/alipay_authinfo.api")
    public AlipayLoginResq getAuthInfo(@RequestBody BaseReq<AlipayLoginBean> baseReq) {
        AlipayLoginBean alipayLoginBean = baseReq.getData();
        AlipayLoginResq alipayLoginResq  = alipayLoginService.getAuthInfo(alipayLoginBean);
        return alipayLoginResq;
    }

    /**
     * 绑定手机号到已有彩亿账号
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/alipay_bindmobileno2caiyi.api")
    public BaseResp<AlipayLoginDTO> bindmobileno2caiyi(@RequestBody BaseReq<AlipayLoginBean> bean){
        AlipayLoginBean userBean = bean.getData();
        log.info("用户中心--> 支付宝快登绑定手机号到彩亿帐号，uid==[" + userBean.getUid() + "]");
        AlipayLoginResq result = alipayLoginService.bindmobileno2caiyi(userBean);
        BaseResp<AlipayLoginDTO> response = new BaseResp<>();
        response.setCode(result.getCode());
        response.setDesc(result.getDesc());
        response.setData(result.getData());
        return response;
    }

    /**
     * 校验短信验证码，查询已存在彩亿账号
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/alipay_get_caiyi_account.api")
    public BaseResp<AlipayLoginDTO> zfbgetcaiyiaccount(@RequestBody BaseReq<AlipayLoginBean> baseReq) {
        AlipayLoginBean bean = baseReq.getData();
        log.info("用户中心--> 支付宝快登查询已存在彩亿账号，uid==[" + bean.getUid() + "]");
        BaseResp response = alipayLoginService.zfbgetcaiyiaccount(bean);
        return response;
    }

    /**
     * 绑定支付宝到已有彩亿账号
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/alipay_bind2caiyi.api")
    public BaseResp<AlipayLoginDTO> zfbbind2caiyi(@RequestBody BaseReq<AlipayLoginBean> baseReq){
        BaseResp<AlipayLoginDTO> response = new BaseResp<>();

        AlipayLoginBean bean = baseReq.getData();
        log.info("用户中心--> 绑定支付宝到已有彩亿账号,uid==[" + bean.getUid() + "]");
        AlipayLoginResq result = alipayLoginService.checkAlipayInfo(bean);
        UserInfoDTO userInfoDTO = alipayLoginService.checkAccountInfo(bean);
        if (userInfoDTO != null) {
            try {
                alipayLoginService.bindAlipay2Caiyi(bean,userInfoDTO);
            } catch (Exception e) {
                log.error("彩亿账号绑定失败", e);
            }
        }
        if (bean.getBusiErrCode() != 0) {
            response.setCode(bean.getBusiErrCode() + "");
            response.setDesc(bean.getBusiErrDesc());
            return response;
        }
        alipayLoginService.setloginData(bean,result);
        AlipayLoginDTO alipayLoginDTO = result.getData();
        alipayLoginDTO.setUid(bean.getUid());
        response.setCode(result.getCode());
        response.setDesc(result.getDesc());
        response.setData(result.getData());
        return response;
    }

    /**
     * 微信注册前校验注册信息是否合法
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/wechat_before_register.api")
    BaseResp beforeWechatRegister(@RequestBody BaseReq<WeChatBean> baseReq) {
        BaseResp baseResp = new BaseResp();
        WeChatBean bean = baseReq.getData();
        try {
            weChatService.beforeWechatRegister(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("微信注册前校验异常，[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("微信登录失败");
        }
        return baseResp;
    }

    /**
     * 获取微信用户信息
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/wechat_get_user_info.api")
    BaseResp<WeChatDTO> getWechatUserInfo(@RequestBody BaseReq<WeChatBean> baseReq) {
        WeChatBean bean = baseReq.getData();
        BaseResp<WeChatDTO> baseResp = new BaseResp(bean);
        try {
            weChatService.getWechatUserInfo(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("获取微信用户信息异常,[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("获取微信用户信息失败");
        }
        return baseResp;
    }

    /**
     * 微信注册并绑定9188账号
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/wechat_register_user.api")
    BaseResp<WeChatBean> registerUser(@RequestBody BaseReq<WeChatBean> baseReq) {
        BaseResp baseResp = new BaseResp();
        WeChatBean bean = baseReq.getData();
        try {
            weChatService.registerUser(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(bean);
        } catch (Exception e) {
            log.error("微信注册并绑定9188账号异常",bean.getUid() , e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("账号绑定异常");
        }
        return baseResp;
    }

    /**
     *
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/wechat_login_after_bind.api")
    BaseResp loginAfterBind(@RequestBody BaseReq<WeChatBean> baseReq) {
        BaseResp baseResp = new BaseResp();
        WeChatBean bean = baseReq.getData();
        try {
            WeChatDTO weChatDTO = weChatService.weChatSetDate(bean);
            baseResp.setData(weChatDTO);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("微信登录后绑定异常，[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("微信绑定失败");
        }
        return baseResp;
    }

    /**
     * 通过微信code登录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/wechat_login.api")
    BaseResp<WeChatDTO> wechatLogin(@RequestBody BaseReq<WeChatBean> baseReq) {
        WeChatBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        WeChatDTO weChatDTO;
        try {
            weChatDTO = weChatService.wechatLogin(bean);
            modifyUserInfoService.saveimei(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(weChatDTO);
        } catch (Exception e) {
            log.error("通过微信code登录异常，[code:{},appid:{},secret:{}]", bean.getCode(), bean.getAppid(), bean.getSecret());
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("登录失败");
        }
        return baseResp;
    }

    /**
     * @Description:支付宝用户修改默认登录密码
     * @Date: 15:06 2017/12/20
     * @param baseReq
     * @return:
    */
    @RequestMapping(value = "/user/alipay_upatepwd.api")
    BaseResp Upatepwd(@RequestBody BaseReq<AlipayLoginBean> baseReq){
        BaseResp baseResp = new BaseResp();
        AlipayLoginBean bean =  baseReq.getData();
        try {
            BaseResp resp = alipayLoginService.Upatepwd(bean);
            baseResp.setCode(resp.getCode());
            baseResp.setDesc(resp.getDesc());
        } catch (Exception e) {
            log.error("支付宝用户修改默认登录密码出现异常", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("修改登录密码失败");
        }
        return baseResp ;
    }

    /**
     * 绑定微信账户到彩亿账户前检测接口参数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_wechat_param_check.api")
    BaseResp bindWechatParamCheck(@RequestBody BaseReq<WeChatBean> baseReq) {
        WeChatBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            weChatService.bindWechatParamCheck(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("参数检查出错");
            log.error("绑定微信账户到彩亿账户前检测接口参数，[uid:{}]", bean.getUid());
        }
        return baseResp;
    }

    /**
     * 绑定9188ID到微信AppID
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_9188userid_to_wxappid.api")
    BaseResp<WeChatBean> bind9188UserId2WXAppId(@RequestBody BaseReq<WeChatBean> baseReq) {
        WeChatBean bean = baseReq.getData();
        BaseResp<WeChatBean> baseResp = new BaseResp();
        try {
            weChatService.bind9188UserId2WXAppId(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(bean);
        } catch (Exception e) {
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("绑定失败");
            log.error("绑定9188ID到微信AppID,[uid:{}]", bean.getUid(), e);
        }
        return baseResp;
    }

    /**
     * 绑定手机号到彩亿账户前检测接口参数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_mobileno_param_check.api")
    BaseResp bindWechatMobilenoParamCheck(@RequestBody BaseReq<WeChatBean> baseReq) {
        WeChatBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            weChatService.bindWechatMobilenoParamCheck(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("参数检查失败");
            log.error("绑定手机号到彩亿账户前检测异常，[openid:{},wechatToken:{}]", bean.getOpenid(), bean.getWeChatToken(), e);
        }
        return baseResp;
    }

    /**
     * 校验短信验证码，绑定手机号到9188账号并登录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_mobileno_to_caiyi.api")
    BaseResp<WeChatBean> bindMobilenoToCaiyi(@RequestBody BaseReq<WeChatBean> baseReq) {
        WeChatBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            weChatService.bindMobilenoToCaiyi(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(bean);
        } catch (Exception e) {
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("绑定失败");
            log.error("校验短信验证码，绑定手机号到9188账号并登录，[mobile:{},verifycode:{}]", bean.getMphone(), bean.getVerycode(), e);
        }
        return baseResp;
    }
    /**
     * 验短信验证码，查询手机号绑定彩亿账户列表
     * @author wang tao
     * @param req
     * @return
     */
    @RequestMapping("/user/get_mobilebind_account_wechat.api")
    public BaseResp<WeChatDTO> getMobileBindAccountWechat(@RequestBody BaseReq<WeChatBean> req){
        WeChatBean bean=req.getData();
        BaseResp resp=new BaseResp();
        try {
            weChatService.checkParam4VerifySmsCodeWechat(bean);
            if(bean.getBusiErrCode()!=0){//校验出错
                resp.setCode(bean.getBusiErrCode()+"");
                resp.setDesc(bean.getBusiErrDesc());
                return resp;
            }
            WeChatDTO weChatDTO = weChatService.getMobileBindAccountWechat(bean);
            resp.setCode(bean.getBusiErrCode()+"");
            resp.setDesc(bean.getBusiErrDesc());
            resp.setData(weChatDTO);
        } catch (Exception e) {
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("验证绑定失败");
            log.error("验短信验证码，查询手机号绑定彩亿账户列表，[verifycode:{},mobile:{}]", bean.getVerycode() ,bean.getMphone(), e);
        }
        return  resp;
    }
}
