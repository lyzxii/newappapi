package com.caiyi.lottery.tradesystem.redpacketcenter.service;

import redpacket.bean.MyRedPacketPage;
import redpacket.bean.RedPacketBean;


public interface MyRedPacketService {
    MyRedPacketPage queryMyRedPacket(RedPacketBean bean) throws Exception;

    void sendRedpacket(RedPacketBean bean);
}
