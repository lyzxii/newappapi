package com.caiyi.lottery.tradesystem.ordercenter.clientwrapper;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderBasicInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import order.bean.OrderBean;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查询投注人数
 * @author wxy
 * @create 2018-01-18 14:07
 **/
@Component("OrderBasicWrapper")
public class OrderBasicWrapper {
    @Autowired
    private OrderBasicInterface orderBasicInterface;
    public Integer queryBetNum(OrderBean bean, Logger log, String syscode){
        BaseReq<OrderBean> req = new BaseReq<OrderBean>(bean,syscode);
        BaseResp<Integer> resp = orderBasicInterface.queryBetNum(req);
        bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
        bean.setBusiErrDesc(resp.getDesc());
        if(BusiCode.SUCCESS.equals(resp.getCode())&&resp.getData()!=null){
            return resp.getData();
        }else{
            log.info("查询投注人数失败,用户名:{},彩种:{},期次:{}code:{},desc:{}", bean.getUid(), bean.getGid(), bean.getPid(), resp.getCode(), resp.getDesc());
            return null;
        }
    }
}
