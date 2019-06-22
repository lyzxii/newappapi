package com.caiyi.lottery.tradesystem.tradecenter.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.tradecenter.client.TradeInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import trade.bean.TradeBean;
import trade.dto.CastDto;
import trade.dto.JcCastDto;
import trade.dto.PrepareCastDto;
import trade.dto.SelectMatchDto;

import java.util.List;

@Slf4j
@Component
public class TradeInterfaceError implements TradeInterface {
    @Override
    public Response checkHealth() {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心checkHealth调用失败");
        return resp;
    }

    @Override
    public BaseResp<String> encodeBetInfo(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心encodeBetInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> encodeJjyhBetInfo(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心encodeJjyhBetInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> hmzhremind(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心hmzhremind调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<CastDto> pcast(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心pcast调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<CastDto> jcast(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心jcast调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<CastDto> jczq_optimize_proj(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心jczq_optimize_proj调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<CastDto> jclq_optimize_proj(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心jclq_optimize_proj调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<String> zcancel(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心zcancel调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<TradeBean> zcastnew(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心zcastnew调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<PrepareCastDto> prepare4Pay(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心prepare4Pay调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<List<SelectMatchDto>> select_match_dz(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心select_match_dz调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<JcCastDto> fgpcast(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心fgpcast调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<JcCastDto> project_yczs_cast(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心project_yczs_cast调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<TradeBean> decodeBetInfo(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心decodeBetInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<TradeBean> decodeJjyhBetInfo(BaseReq<TradeBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("交易中心调用失败");
        log.info("交易中心decodeJjyhBetInfo调用失败,请求req:" + baseReq.toJson());
        return resp;
    }
}
