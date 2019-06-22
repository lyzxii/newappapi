package com.caiyi.lottery.tradesystem.ordercenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.service.ChaseNumberService;
import lombok.extern.slf4j.Slf4j;
import order.bean.ChaseNumberPage;
import order.bean.OrderBean;
import order.pojo.ComplexPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * controller
 *
 * @author GJ
 * @create 2017-12-21 15:50
 **/
@RestController
@Slf4j
public class ChaseNumberController {

    @Autowired
    private ChaseNumberService chaseNumberService;
    /**
     *追号详情
     * @param baseReq
     * @return
     * /user/queryrecord.go
     */
    @RequestMapping(value = "/order/get_chasenumber_record.api")
    public BaseResp<ChaseNumberPage> getChaseNumberRecord(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        ChaseNumberPage chaseNumberPage = chaseNumberService.getChaseNumberRecord(bean);
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        baseResp.setData(chaseNumberPage);
        return baseResp;
    }

}
