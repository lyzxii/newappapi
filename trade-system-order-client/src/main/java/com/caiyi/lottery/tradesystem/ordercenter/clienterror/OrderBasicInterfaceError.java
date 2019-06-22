package com.caiyi.lottery.tradesystem.ordercenter.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderBasicInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import org.springframework.stereotype.Component;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class OrderBasicInterfaceError implements OrderBasicInterface{
    /**
     * 查询投注人数
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<Integer> queryBetNum(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心queryBetNum调用失败,请求req:"+baseReq.toJson());
        return resp;
    }
}
