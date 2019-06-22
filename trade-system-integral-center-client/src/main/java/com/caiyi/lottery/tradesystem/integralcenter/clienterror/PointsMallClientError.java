package com.caiyi.lottery.tradesystem.integralcenter.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.integralcenter.client.PointsMallClient;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import integral.bean.PointsMallBean;
import integral.pojo.ExchangeStatus;
import integral.pojo.PointsExchangeResult;
import integral.pojo.PointsMallGoods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Component
@Slf4j
public class PointsMallClientError implements PointsMallClient{
    @Override
    public BaseResp<ExchangeStatus> getExchangeGoodStatus(BaseReq<PointsMallBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心getExchangeGoodStatus调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 获取积分商城所有物品
     * 必传参数： cnickid 用户名
     *
     * @param cnickid
     */
    @Override
    public BaseResp<PointsMallGoods> queryJFMallGoods(String cnickid) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心queryJFMallGoods调用失败,cnickid:"+cnickid);
        return resp;
    }

    /**
     * 积分中心获取用户兑换记录
     * 必传参数： PointsMallBean 中 uid 、ps、pn 属性
     *
     * @param req
     */
    @Override
    public BaseResp<Page> queryExchangeRecord(BaseReq<PointsMallBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心queryExchangeRecord调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 积分兑换物品
     * 必传参数：PointsMallBean 中 uid、ex_goods_id为必传参数
     *
     * @param req
     */
    @Override
    public BaseResp<PointsExchangeResult> exchangeGoods(BaseReq<PointsMallBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心exchangeGoods调用失败,req:"+req.toJson());
        return resp;
    }
}
