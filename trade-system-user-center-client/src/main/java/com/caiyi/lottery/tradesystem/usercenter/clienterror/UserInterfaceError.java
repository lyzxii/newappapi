package com.caiyi.lottery.tradesystem.usercenter.clienterror;

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
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserInterface;
import dto.*;
import integral.bean.IntegralParamBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pojo.Acct_UserPojo;
import pojo.UserPojo;
import pojo.UserRecordPojo;
import response.*;

import java.util.List;

@Slf4j
@Component
public class UserInterfaceError implements UserInterface {
    @Override
    public Response checkHealth() {
        Response resp = new Response();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心checkHealth调用失败");
        return resp;
    }

    @Override
    public UserRegistResp mobileRegisterCheck(BaseReq<UserBean> baseReq) {
        UserRegistResp resp = new UserRegistResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心mobileRegisterCheck调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserRegistResp mobileRegister(BaseReq<UserBean> baseReq) {
        UserRegistResp resp = new UserRegistResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心mobileRegister调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserPersonalInfoResq getUserWhitelistGrade(BaseReq<UserBean> baseReq) {
        UserPersonalInfoResq resp = new UserPersonalInfoResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心getUserWhitelistGrade调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserPersonalInfoResq personalCenterInfo(BaseReq<UserBean> baseReq) {
        UserPersonalInfoResq resp = new UserPersonalInfoResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心personalCenterInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserLoginResq login(BaseReq<UserBean> baseReq) {
        UserLoginResq resp = new UserLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心login调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp uploadUserPhoto(BaseReq<UserPhotoDTO> baseReq) throws Exception {
        UserLoginResq resp = new UserLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心uploadUserPhoto调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp userBindCheck(BaseReq<UserBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心userBindCheck调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Response forgetPwd(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心forgetPwd调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserResp sendMobSms(BaseReq<UserBean> baseReq) {
        UserResp resp = new UserResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心sendMobSms调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserRegistResp userRegister(BaseReq<UserBean> baseReq) {
        UserRegistResp resp = new UserRegistResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心userRegister调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Response setNewPwd(BaseReq<UserBean> baseReq) {
        Response resp = new Response();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心setNewPwd调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Response verifySms(BaseReq<UserBean> baseReq) {
        Response resp = new Response();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心verifySms调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp updateWinAndChaseNumberSwitch(BaseReq<UserBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心updateWinAndChaseNumberSwitch调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp saveActiveDate(BaseReq<UserBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心saveActiveDate调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp modifyUserInfo(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心modifyUserInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp loginout(BaseReq<BaseBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心loginout调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp checkApplyEligible(String uid) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心checkApplyEligible调用失败,请求req:" + uid);
        return resp;
    }

    @Override
    public Response checkUserNick(BaseReq<UserBean> baseReq) {
        Response resp = new Response();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心checkUserNick调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserResp queryUserDefaultPwd(BaseReq<UserBean> baseReq) {
        UserResp resp = new UserResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserDefaultPwd调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Response calcUserpingNeterror(BaseReq<UserBean> baseReq) {
        Response resp = new Response();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心calcUserpingNeterror调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Page<List<UserAccountDTO>>> queryAccount(BaseReq<UserBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryAccount调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<PushBean> queryGtTag(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryGtTag调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Response calculateNeterror(BaseReq<UserBean> baseReq) {
        Response resp = new Response();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心calculateNeterror调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public UserLoginResq webLogin(BaseReq<UserBean> baseReq) {
        UserLoginResq resp = new UserLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心webLogin调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp changeMobileCheck(BaseReq<UserBean> baseReq) throws Exception {
        UserLoginResq resp = new UserLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心changeMobileCheck调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Result checkBankCard(BaseReq<UserBean> baseReq) {
        Result resp = new Result();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心checkBankCard调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<IdBankBindingDTO> queryIdBankBinding(BaseReq<UserBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryIdBankBinding调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<IdcardBindingDTO> queryUserInfoBind(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserInfoBind调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<AlipayLoginResq> bindmobileno2caiyi(BaseReq<AlipayLoginBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bindmobileno2caiyi调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public Response forgetPWDSendSMS(UserBean bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心forgetPWDSendSMS调用失败,请求req:" + bean.toJsonString());
        return resp;
    }

    @Override
    public Response forgetPwdRestPwd(UserBean bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心forgetPwdRestPwd调用失败,请求req:" + bean.toJsonString());
        return resp;
    }

    @Override
    public BaseResp zfbGetCaiyi(AlipayLoginBean bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心zfbGetCaiyi调用失败,请求req:" + bean.toJsonString());
        return resp;
    }

    @Override
    public Response activationCallback(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心activationCallback调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatDTO> getMobileBindAccountWechat(BaseReq<WeChatBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心getMobileBindAccountWechat调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<AlipayLoginResq> zfbbind2caiyi(BaseReq<AlipayLoginBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心zfbbind2caiyi调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp beforeWechatRegister(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心beforeWechatRegister调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatBean> getWechatUserInfo(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心getWechatUserInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatBean> registerUser(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心registerUser调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatDTO> loginAfterBind(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心loginAfterBind调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatDTO> wechatLogin(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心wechatLogin调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp bindWechatParamCheck(BaseReq baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bindWechatParamCheck调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatBean> bind9188UserId2WXAppId(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bind9188UserId2WXAppId调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp bindWechatMobilenoParamCheck(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bindWechatMobilenoParamCheck调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<WeChatBean> bindMobilenoToCaiyi(BaseReq<WeChatBean> baseReq) throws Exception {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bindMobilenoToCaiyi调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp querySmsAuthCode(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心querySmsAuthCode调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Acct_UserPojo> integralQueryBasicInfo(BaseReq<String> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心integralQueryBasicInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserPojo> integralQueryIdBankBinding(BaseReq<String> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心integralQueryIdBankBinding调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> cannotSign(BaseReq<String> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心cannotSign调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> clickToGetPoints(BaseReq<IntegralParamBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心clickToGetPoints调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserRecordPojo> queryVipUserInfo(BaseReq<String> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryVipUserInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> queryLevelExper(BaseReq<String> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryLevelExper调用失败,请求req:" + baseReq.toJson());
        return resp;
    }
}
