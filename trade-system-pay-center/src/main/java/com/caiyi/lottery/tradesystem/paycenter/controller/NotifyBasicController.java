package com.caiyi.lottery.tradesystem.paycenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.bean.PayBean;

@Slf4j
@RestController
public class NotifyBasicController {

    @Autowired
    NotifyService notifyService;

    //center回调通用
    @RequestMapping(value = "/pay/basic_notify.api")
    public BaseResp<PayBean> baseicNotify(@RequestBody BaseReq<PayBean> req) {
        PayBean bean = req.getData();
        notifyService.basicNotifyService(bean);
        BaseResp<PayBean> respBean = new BaseResp<>();
        respBean.setCode(bean.getBusiErrCode() + "");
        respBean.setDesc(bean.getBusiErrDesc());
        respBean.setData(bean);
        return respBean;
    }


}
