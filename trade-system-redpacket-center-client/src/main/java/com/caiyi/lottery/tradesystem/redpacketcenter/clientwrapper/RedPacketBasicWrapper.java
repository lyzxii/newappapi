package com.caiyi.lottery.tradesystem.redpacketcenter.clientwrapper;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("QueryRedPacketTaskWrapper")
public class RedPacketBasicWrapper {
    @Autowired
    private RedPacketCenterInterface redPacketCenterInterface;

    public Integer isGetRedPacket(BaseBean bean, Logger log, String syscode){
        BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean,syscode);
        BaseResp<Integer> resp = redPacketCenterInterface.isGetRedPacket(req);
        bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
        bean.setBusiErrDesc(resp.getDesc());
        if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
            return resp.getData();
        }else{
            log.info("查询用户是否领取失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
            return 0;
        }
    }

    public Integer havenRedPacket(BaseBean bean, Logger log, String syscode){
        BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean,syscode);
        BaseResp<Integer> resp = redPacketCenterInterface.havenRedPacket(req);
        bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
        bean.setBusiErrDesc(resp.getDesc());
        if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
            return resp.getData();
        }else{
            log.info("查询用户以其他身份获取红包失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
            return 0;
        }
    }

    public Integer getRedPacketStateByNickid(BaseBean bean, Logger log, String syscode) {
        BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean,syscode);
        BaseResp<Integer> resp = redPacketCenterInterface.getRedPacketStateByNickid(req);
        bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
        bean.setBusiErrDesc(resp.getDesc());
        if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
            return resp.getData();
        }else{
            log.info("查询用户红包状态失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
            return null;
        }
    }
}
