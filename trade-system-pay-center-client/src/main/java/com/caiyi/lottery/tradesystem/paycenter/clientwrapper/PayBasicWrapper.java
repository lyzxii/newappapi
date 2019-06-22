package com.caiyi.lottery.tradesystem.paycenter.clientwrapper;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.paycenter.client.PayBasicInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author wxy
 * @create 2017-12-27 19:47
 **/
@Component("PayBasicWrapper")
public class PayBasicWrapper {
    @Autowired
    private PayBasicInterface payBasicInterface;

    public Integer queryFirstIsLower20(BaseBean bean, Logger log, String syscode) {
        BaseReq<BaseBean> req = new BaseReq<>(bean,syscode);
        BaseResp<Integer> resp = payBasicInterface.queryFirstIsLower20(req);
        bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
        bean.setBusiErrDesc(resp.getDesc());
        if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
            return resp.getData();
        }else{
            log.info("查询用户首次充值是否小于20失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
            return 0;
        }
    }
}
