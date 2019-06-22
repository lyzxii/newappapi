package com.caiyi.lottery.tradesystem.ordercenter.clienterror;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import order.bean.ChaseNumberPage;
import order.bean.OrderBean;
import order.dto.*;
import order.response.XmlResp;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Component
public class OrderInterfaceError implements OrderInterface{
    @Override
    public Response checkHealth() {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        return resp;
    }

    @Override
    public BaseResp<ChaseNumberPage> getChaseNumberRecord(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心getChaseNumberRecord调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp hideZhuihaoDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心hideZhuihaoDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<XmlResp> awarddetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心awarddetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp hideBuyRecord(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心hideBuyRecord调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp queryCastDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心queryCastDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp ranking(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心ranking调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp statPass(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心statPass调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp queryLotteryDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心queryLotteryDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<List> lotteryInfoNew(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心lotteryInfoNew调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<GamesProjectDTO> getAthleticsProjectDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心getAthleticsProjectDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<FigureGamesDTO> getDigitaProjectDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心getDigitaProjectDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<ZucaiMatchProDTO> getZucaiProjectDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心getZucaiProjectDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<ZuCaiMatchVSDTO> getZucaiMatch(BaseReq<OrderBean> baseReq) {
        BaseResp<ZuCaiMatchVSDTO> resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心getZucaiMatch调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<Integer> queryUserUnbeginNum(BaseReq<BaseBean> baseReq) {
        BaseResp<Integer> resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心queryUserUnbeginNum调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<List<String>> getMatrixInfos(BaseReq<OrderBean> baseReq) {
        BaseResp<List<String>> resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心getMatrixInfos调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    @Override
    public BaseResp<NewTicketDetailDTO> queryLsDetail(@RequestBody BaseReq<OrderBean> baseReq) {
        BaseResp<NewTicketDetailDTO> resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("queryLsDetail,请求req:"+baseReq.toJson());
        return resp;
    }
}
