package com.caiyi.lottery.tradesystem.redpacketweb.service;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketClientInterface;
import com.caiyi.lottery.tradesystem.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redpacket.bean.RedPacketBean;

import javax.servlet.http.HttpServletRequest;

@Service
public class RedPacketWebService {

    @Autowired
    private RedPacketClientInterface redPacketClientInterface;

    @Autowired
    private HttpServletRequest request;

    public BaseResp CardChargeRedpacket(RedPacketBean bean){
        String host = request.getHeader("Host");
//        String ip=IPUtils.getRealIp(request);
//        bean.setIpAddr(ip);
        if (host.endsWith("iphone.shanghaicaiyi.com")||host.endsWith("iphone.9188.com")) {
            bean.setComeFrom("ios");
        } else if (host.endsWith("mobile.shanghaicaiyi.com")||host.endsWith("mobile.9188.com")) {
            bean.setComeFrom("android");
        }
        BaseResp resp=redPacketClientInterface.CardChargeRedpacket(new BaseReq<>(bean, SysCodeConstant.REDPACKETWEB));
        return resp;
    }
}
