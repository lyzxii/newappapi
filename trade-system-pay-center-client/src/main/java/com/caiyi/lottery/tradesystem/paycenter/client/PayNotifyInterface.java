package com.caiyi.lottery.tradesystem.paycenter.client;


import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pay.bean.PayBean;

@FeignClient(name = "tradecenter-system-paycenter-center")
public interface PayNotifyInterface {

    @RequestMapping(value = "/pay/basic_notify.api")
    BaseResp<PayBean> basicNotify(@RequestBody BaseReq<PayBean> req);
}
