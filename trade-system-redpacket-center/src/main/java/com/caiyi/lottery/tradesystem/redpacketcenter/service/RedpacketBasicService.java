package com.caiyi.lottery.tradesystem.redpacketcenter.service;


import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import redpacket.bean.RedPacketBean;

import java.util.List;


public interface RedpacketBasicService {

    /**
     * 插入红包活动表
     *
     */
    int insertIntoRedpacketActivity(RedPacketBean bean) throws Exception;

    /**
     * 查询用户滚动打码
     * @param bean
     * @throws Exception
     */
    List<String> queryRolingCode(RedPacketBean bean) throws Exception;

    /**
     * 查询红包详情
     */
    RedPacketBean queryRepacketDetail(RedPacketBean bean);

    /**
     * 是否领取红包
     * @param bean
     * @return
     */
    Integer isGetRedPacket(BaseBean bean) throws Exception;

    /**
     * 查询是否以其他身份取得过红包
     * @param bean
     * @return
     * @throws Exception
     */
    Integer havenRedPacket(BaseBean bean) throws Exception;

    /**
     * 通过用户名查询红包状态
     * @param bean
     * @return
     */
    BaseResp getRedPacketStateByNickid(BaseBean bean) throws Exception;

    Integer countGroupBuy(BaseBean bean);
}
