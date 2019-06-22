package com.caiyi.lottery.tradesystem.usercenter.clienterror;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pojo.Acct_UserPojo;
import pojo.UserAcctPojo;
import pojo.UserPojo;

@Slf4j
@Component
public class UserBasicInfoInterfaceError implements UserBasicInfoInterface {
    @Override
    public BaseResp<BaseBean> bankCardBind(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bankCardBind调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<BaseBean> calculate_breakdown_error(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心calculate_breakdown_error调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> checkIsExist(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心checkIsExist调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> queryUserWhiteGrade(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserWhiteGrade调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserPojo> queryUserInfo(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserPojo> queryUserInfoForCardCharge(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserInfoForCardCharge调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<UserAcctPojo> getUserPoint(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心getUserPoint调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp updateUserPoint(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心updateUserPoint调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> countUserCharge(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心countUserCharge调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> queryUserVipAgentCount(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserVipAgentCount调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> queryAppagentId(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryAppagentId调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp updateAgentId(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心updateAgentId调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp check_level(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心check_level调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> countOutByNickidInAYear(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心countOutByNickidInAYear调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> isNewUser(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心isNewUser调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Acct_UserPojo> queryUserAccountInfo(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryUserAccountInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> countSelfBuy(BaseReq<BaseBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心countSelfBuy调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> queryAgentId(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心queryAgentId调用失败,请求req:" + baseReq.toJson());
        return resp;
    }
}
