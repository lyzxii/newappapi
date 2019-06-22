package com.caiyi.lottery.tradesystem.paycenter.client;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pay.bean.PayBean;

/**
 * 支付中心互调接口
 */
@FeignClient(name = "tradecenter-system-paycenter-center")
public interface PayBasicInterface {
    @RequestMapping(value = "/pay/query_first_is_lower20.api")
    BaseResp<Integer> queryFirstIsLower20(@RequestBody BaseReq<BaseBean> req);

    @RequestMapping(value = "/pay/query_whitelist_status.api")
    BaseResp<Integer> queryWhitelistStatus(@RequestBody BaseReq<BaseBean> req);

    //查询bankid
    @RequestMapping(value = "/pay/query_bankid.api")
    BaseResp<PayBean> queryBankId(@RequestBody BaseReq<PayBean> req);
}
