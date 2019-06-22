package com.caiyi.lottery.tradesystem.usercenter.clienterror;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserBaseInterfaceError implements UserBaseInterface {
    @Override
    public BaseResp<BaseBean> checkLogin(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心checkLogin调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<BaseBean> setUserData(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心setUserData调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp getServiceHotLine(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心getServiceHotLine调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp rebackUserPhotoStatus(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心rebackUserPhotoStatus调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserBean> productOperationInfo(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心productOperationInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserBean> check_login_feedback_multipart(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心check_login_feedback_multipart调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public void queryUserToken(BaseReq<BaseBean> baseReq) {

    }

    @Override
    public BaseResp authenticAndApplyModifyBankCard(UserBean baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
//        log.info("用户中心alipayBind调用失败,请求req:"+baseReq.toJson());
        log.info("用户中心authenticAndApplyModifyBankCard调用失败,请求req:" + baseReq.toJsonString());
        return resp;
    }
}
