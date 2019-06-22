package com.caiyi.lottery.tradesystem.integralcenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.integralcenter.clienterror.PointsMallClientError;
import integral.bean.PointsMallBean;
import integral.pojo.ExchangeStatus;
import integral.pojo.PointsExchangeResult;
import integral.pojo.PointsMallGoods;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "tradecenter-system-integralcenter-center",fallback = PointsMallClientError.class)
@RequestMapping("/integral")
public interface PointsMallClient {

    @RequestMapping(value = "/get_exgood_detail.api",method = RequestMethod.POST)
    BaseResp<ExchangeStatus> getExchangeGoodStatus(@RequestBody BaseReq<PointsMallBean> req);

    /**
     *获取积分商城所有物品
     *必传参数： cnickid 用户名
     */
    @RequestMapping("/query_jfmall_goods.api")
    BaseResp<PointsMallGoods> queryJFMallGoods(@RequestParam("cnickid") String cnickid);


    /**
     * 积分中心获取用户兑换记录
     * 必传参数： PointsMallBean 中 uid 、ps、pn 属性
     */
    @RequestMapping(value = "/query_exchange_record.api",method = RequestMethod.POST)
    BaseResp<Page> queryExchangeRecord(@RequestBody BaseReq<PointsMallBean> req);


    /**
     *积分兑换物品
     *必传参数：PointsMallBean 中 uid、ex_goods_id为必传参数
     */
    @RequestMapping(value = "/exchange_goods.api",method = RequestMethod.POST)
    BaseResp<PointsExchangeResult> exchangeGoods(@RequestBody BaseReq<PointsMallBean> req);

}
