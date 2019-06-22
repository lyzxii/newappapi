package com.caiyi.lottery.tradesystem.integralcenter.clienterror;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.integralcenter.client.IntegralCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import integral.bean.IntegralBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class IntegralCenterInterfaceError implements IntegralCenterInterface{
    /**
     * 服务检查
     *
     * @return
     */
    @Override
    public Response checkHealth() {
        return null;
    }

    /**
     * @param bean
     * @return
     */
    @Override
    public BaseResp<IntegralBean> clickToSign(BaseReq<UserBean> bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心clickToSign调用失败,req:"+bean.toJson());
        return resp;
    }

    @Override
    public BaseResp<IntegralBean> queryVipPointInfo(BaseReq<UserBean> bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心queryVipPointInfo调用失败,req:"+bean.toJson());
        return resp;
    }

    @Override
    public BaseResp<IntegralBean> clickToGetPoints(BaseReq<UserBean> bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心clickToGetPoints调用失败,req:"+bean.toJson());
        return resp;
    }

    @Override
    public BaseResp<Page> pointsDetail(BaseReq<UserBean> bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心pointsDetail调用失败,req:"+bean.toJson());
        return resp;
    }

    @Override
    public BaseResp<Page> experienceDetail(BaseReq<UserBean> bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心experienceDetail调用失败,req:"+bean.toJson());
        return resp;
    }

    @Override
    public BaseResp<IntegralBean> queryVipUserInfo(BaseReq<UserBean> bean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心queryVipUserInfo调用失败,req:"+bean.toJson());
        return resp;
    }
}
