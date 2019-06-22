package com.caiyi.lottery.tradesystem.redpacketcenter.service;

import com.caiyi.lottery.tradesystem.bean.Page;
import redpacket.bean.RedPacketBean;

public interface RedPacketDetailService {

    /**
     *查询红包列表
     */
    Page queryRedPacketDetail(RedPacketBean redPacketBean) throws  Exception;

    /**
     * 采用新的统一查询接口查询投注时可用红包
     */
    Page queryRedpacketBeforeCast(RedPacketBean bean)  throws  Exception;
    
    /**
     * 检测用户交易红包
     * @param bean
     */
    void checkTradeRedpacket(RedPacketBean bean);
}
