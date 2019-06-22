package com.caiyi.lottery.tradesystem.ordercenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.clienterror.OrderBasicInterfaceError;
import order.bean.OrderBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 其它中心访问用
 * @author wxy
 * @create 2018-01-18 13:49
 **/
@FeignClient(name = "tradecenter-system-ordercenter-center",fallback = OrderBasicInterfaceError.class)
public interface OrderBasicInterface {

    /**
     * 查询投注人数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_bet_num")
    BaseResp<Integer> queryBetNum(@RequestBody BaseReq<OrderBean> baseReq);
}
