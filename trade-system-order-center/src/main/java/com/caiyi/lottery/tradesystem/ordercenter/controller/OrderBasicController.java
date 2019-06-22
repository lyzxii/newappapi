package com.caiyi.lottery.tradesystem.ordercenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.service.OrderBasicService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部调用接口
 * @author wxy
 * @create 2018-01-18 13:46
 **/
@Slf4j
@RestController
public class OrderBasicController {
    @Autowired
    private OrderBasicService orderBasicService;
    /**
     * 查询投注人数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_bet_num")
    BaseResp<Integer> queryBetNum(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        BaseResp<Integer> baseResp = new BaseResp<>();
        try {
            Integer num = orderBasicService.queryBetNum(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(num);
        } catch (Exception e) {
            log.error("查询投注人数失败，[彩种：{},期次：{}]", bean.getGid(), bean.getPid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询投注人数失败");
        }
        return baseResp;
    }
}
