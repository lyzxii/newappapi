package com.caiyi.lottery.tradesystem.usercenter.client;

import bean.AlipayLoginBean;
import bean.PushBean;
import bean.UserBean;

import bean.WeChatBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.usercenter.clienterror.UserInterfaceError;
import integral.bean.IntegralParamBean;
import dto.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.Acct_UserPojo;
import pojo.UserPojo;
import pojo.UserRecordPojo;
import response.*;

import java.util.List;


/**
 * 用户中心客户端接口
 */
@FeignClient(name = "tradecenter-system-usercenter-center")
public interface UserInterface {


    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/user/checkhealth.api")
    public Response checkHealth() ;
    /**
     * 手机资格注册校验
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/mobile_register_check.api")
     UserRegistResp mobileRegisterCheck(@RequestBody BaseReq<UserBean> bean);

    /**
     * 手机注册
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/mobile_register.api")
    UserRegistResp mobileRegister(@RequestBody BaseReq<UserBean> bean);

    /**
     * 获取用户白名单
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/get_user_whitelist_grade.api")
    UserPersonalInfoResq getUserWhitelistGrade(@RequestBody BaseReq<UserBean> bean);
    /**
     * 个人中心
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/personal_center_info.api")
    UserPersonalInfoResq personalCenterInfo(@RequestBody BaseReq<UserBean> bean);


    /**
     * 登入接口
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/mobile_login.api")
    UserLoginResq login(@RequestBody BaseReq<UserBean> bean);

    /**
     * 用户头像上传 入库
     * @param userPhotoDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/upload_user_photo.api")
    BaseResp uploadUserPhoto(@RequestBody  BaseReq<UserPhotoDTO> userPhotoDTO) throws Exception;

    /**
     * 绑定验证
     * @param bean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/user_bind_check.api")
    BaseResp userBindCheck(@RequestBody BaseReq<UserBean> bean) throws Exception;

    /**
     * 忘记密码
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/forget_pwd.api")
    Response forgetPwd(@RequestBody BaseReq<UserBean> bean);

    /**
     * 发送短信(新)
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/send_mob_sms.api")
    UserResp sendMobSms(@RequestBody BaseReq<UserBean> bean);

    /**
     * 带验证码的用户注册
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/user_register.api")
    UserRegistResp userRegister(@RequestBody BaseReq<UserBean> bean);

    /**
     * 设置新密码
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/set_new_pwd.api")
    Response setNewPwd(@RequestBody BaseReq<UserBean> bean);

    /**
     * 验证短信验证码
     * @param bean
     */
    @RequestMapping(value = "/user/verify_sms.api")
    Response verifySms(@RequestBody BaseReq<UserBean> bean);

    /**
     * 用户中奖追号推送开关设置
     * @param bean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/update_win_chase_switch.api")
    BaseResp updateWinAndChaseNumberSwitch(@RequestBody BaseReq<UserBean> bean) throws Exception;

    /**
     * 保存用户激活数据
     * @param bean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/save_active_data.api")
    BaseResp saveActiveDate(@RequestBody BaseReq<UserBean> bean) throws Exception;


    /**
     * @Author: tiankun
     * @Description: 修改用户信息 （根据flag来决定修改内容）
     * @Date: 13:43 2017/11/30
     */
    @RequestMapping(value = "/user/modify_user_info.api")
    BaseResp modifyUserInfo(@RequestBody BaseReq<UserBean> resp);

    /**
     * 退出登录
     * @param bean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/loginout.api")
    BaseResp loginout(@RequestBody BaseReq<BaseBean> bean) throws Exception;

    /**
     * 提交银行卡号修改申请前，查看是否有提交资格
     * @param uid 用户昵称
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/check_apply_eligible.api")
    BaseResp checkApplyEligible(@RequestBody String uid) throws Exception;

    /**
     * 检测用户名是否使用
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/check_user_nick.api")
    Response checkUserNick(@RequestBody  BaseReq<UserBean> bean);

    /**
     * 查询用户密码状态
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/query_default_pwd.api")
    UserResp queryUserDefaultPwd(@RequestBody  BaseReq<UserBean> bean);

    /**
     * @Author: tiankun
     * @Description: 用户检测网络统计错误信息
     * @Date: 17:29 2017/12/6
     */
    @RequestMapping(value = "/user/calc_userping_neterror.api")
    Response calcUserpingNeterror(@RequestBody  BaseReq<UserBean> bean);

    /**
     * 查询账户明细
     * @param bean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/query_account.api")
    BaseResp<Page<List<UserAccountDTO>>> queryAccount(@RequestBody  BaseReq<UserBean> bean) throws Exception;

    /**
     * 查询个推tag
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/query_gt_tag.api")
    BaseResp<PushBean> queryGtTag(@RequestBody BaseReq<UserBean> bean);

    /**
     * @Author: tiankun
     * @Description: 统计网络错误信息
     * @Date: 17:29 2017/12/6
     */
    @RequestMapping(value = "/user/calculate_net_error.api")
    Response calculateNeterror(@RequestBody  BaseReq<UserBean> bean);


    @RequestMapping(value = "/user/web_login.api")
    UserLoginResq webLogin(@RequestBody  BaseReq<UserBean> bean);

    /**
     * 更换手机号检查
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/change_mobile_check.api")
    BaseResp changeMobileCheck(@RequestBody  BaseReq<UserBean> bean) throws Exception;

    /**
     *银行卡号校验
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/check_bank_card.api")
    Result checkBankCard(@RequestBody BaseReq<UserBean> bean);

    /**
     * 查询身份证银行卡绑定信息
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/query_idbank_binding.api")
    BaseResp<IdBankBindingDTO> queryIdBankBinding(@RequestBody BaseReq<UserBean> baseReq) throws Exception;

    /**
     * 得宝查询是否绑定身份证
     */
    @RequestMapping("/user/query_userinfo_bind.api")
    BaseResp<IdcardBindingDTO> queryUserInfoBind(@RequestBody BaseReq<BaseBean> req);
    /**
     * 绑定手机号到已有彩亿账号
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_mobileno2caiyi.api")
    BaseResp<AlipayLoginResq> bindmobileno2caiyi(BaseReq<AlipayLoginBean> baseReq);

    /**
     * 忘记密码-发送短信验证码
     * @param bean
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/user/forget_pwd_sendSMS.api")
	Response forgetPWDSendSMS(@RequestBody UserBean bean);
    
    /**
     * 忘记密码-校验短信验证码重置密码
     * @param bean
     * @return
     */
    @Deprecated
    @RequestMapping(value="/user/forget_pwd_resetPwd.api")
    Response forgetPwdRestPwd(@RequestBody UserBean bean);

    /**
     * 新版app支付宝便捷登录-校验短信验证码，查询已存在彩亿账号
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/zfb_get_caiyi_account.api")
    BaseResp zfbGetCaiyi(AlipayLoginBean bean);

    /**
     * 回调激活
     * @param res
     * @return
     */
    @RequestMapping(value="/user/invoke.api")
	Response activationCallback(@RequestBody BaseReq<UserBean> res);

    /**
     *微信 校验短信验证码，查询手机号绑定彩亿账户列表
     */
    @RequestMapping("/user/get_mobilebind_account_wechat.api")
    BaseResp<WeChatDTO> getMobileBindAccountWechat(@RequestBody BaseReq<WeChatBean> req);

    /**
     *绑定支付宝到已有彩亿账号
     *
     * @param baseReq
     * @return
     */
    @RequestMapping("/user/zfb_bind2caiyi.api")
    BaseResp<AlipayLoginResq> zfbbind2caiyi(@RequestBody BaseReq<AlipayLoginBean> baseReq);

    /**
     * 微信开发平台注册
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/wechat_before_register.api")
    BaseResp beforeWechatRegister(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     * 获取微信用户信息
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/wechat_get_user_info.api")
    BaseResp<WeChatBean> getWechatUserInfo(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     * 微信注册并绑定9188账号
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/wechat_register_user.api")
    BaseResp<WeChatBean> registerUser(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     *
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/wechat_login_after_bind.api")
    BaseResp<WeChatDTO> loginAfterBind(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     * 通过微信code登录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/wechat_login.api")
    BaseResp<WeChatDTO> wechatLogin(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     * 绑定微信账户到彩亿账户前检测接口参数
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/bind_wechat_param_check.api")
    BaseResp bindWechatParamCheck(@RequestBody BaseReq baseReq) throws Exception;

    /**
     * 绑定9188ID到微信AppID
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_9188userid_to_wxappid.api")
    BaseResp<WeChatBean> bind9188UserId2WXAppId(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     * 绑定手机号到彩亿账户前检测接口参数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/bind_mobileno_param_check.api")
    BaseResp bindWechatMobilenoParamCheck(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    /**
     * 校验短信验证码，绑定手机号到9188账号并登录
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/bind_mobileno_to_caiyi.api")
    BaseResp<WeChatBean> bindMobilenoToCaiyi(@RequestBody BaseReq<WeChatBean> baseReq) throws Exception;

    @RequestMapping(value = "/user/query_sms_authcode.api")
    BaseResp querySmsAuthCode(@RequestBody BaseReq<UserBean> baseReq);

    /**
     * //查询头像、等级、当前积分值
     * @return
     */
    @RequestMapping(value = "/user/integral_query_basic_info.api")
    BaseResp<Acct_UserPojo> integralQueryBasicInfo(@RequestBody BaseReq<String> uid);

    @RequestMapping(value = "/user/integral_idBank_binding.api")
    BaseResp<UserPojo> integralQueryIdBankBinding(@RequestBody BaseReq<String> uid);

    /**
     * 查询签到资格
     * @param req
     * @return
     */
    @RequestMapping(value = "/user/cannot_sign.api")
    BaseResp<String> cannotSign(BaseReq<String> req);

    /**
     * 获取积分
     * @param req
     * @return
     */
    @RequestMapping(value = "/user/click_toGet_points.api")
    BaseResp<Integer> clickToGetPoints(BaseReq<IntegralParamBean> req);

    /**
     * 获取会员中心信息
     * @param req
     * @return
     */
    @RequestMapping(value = "/user/query_vip_user_info.api")
    BaseResp<UserRecordPojo> queryVipUserInfo(BaseReq<String> req);

    /**
     * 查询等级及对应经验
     * @param req
     * @return
     */
    @RequestMapping(value = "/user/query_level_exper.api")
    BaseResp<String> queryLevelExper(BaseReq<String> req);
}
