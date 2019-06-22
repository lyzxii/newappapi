package com.caiyi.lottery.tradesystem.usercenter.clienterror;

import bean.AlipayLoginBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.AllyInterface;
import dto.AlipayLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import response.AlipayLoginResq;

@Slf4j
@Component
public class AllyInterfaceError implements AllyInterface{
    @Override
    public AlipayLoginResq alipayBind(BaseReq<AlipayLoginBean> baseReq) {
        AlipayLoginResq resp = new AlipayLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心alipayBind调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public AlipayLoginResq alipayLogin(BaseReq<AlipayLoginBean> baseReq) {
        AlipayLoginResq resp = new AlipayLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心alipayLoginm调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public AlipayLoginResq getAuthInfo(BaseReq<AlipayLoginBean> baseReq) {
        AlipayLoginResq resp = new AlipayLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心getAuthInfo调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<AlipayLoginDTO> bindmobileno2caiyi(BaseReq<AlipayLoginBean> baseReq) {
        AlipayLoginResq resp = new AlipayLoginResq();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心bindmobileno2caiyi调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<AlipayLoginDTO> zfbgetcaiyiaccount(BaseReq<AlipayLoginBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心zfbgetcaiyiaccount调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<AlipayLoginDTO> zfbbind2caiyi(BaseReq<AlipayLoginBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心zfbbind2caiyi调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp Upatepwd(BaseReq<AlipayLoginBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心Upatepwd调用失败,请求req:"+baseReq.toJson());
        return resp;
    }
}
