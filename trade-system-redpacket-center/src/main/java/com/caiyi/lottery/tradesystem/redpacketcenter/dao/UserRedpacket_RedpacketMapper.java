package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import org.apache.ibatis.annotations.Select;
import redpacket.bean.RedPacketBean;
import redpacket.bean.UserRedpacket;
import redpacket.pojo.Rp_UserRpPojo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * tb_user_redpacket tb_redpacket表关联查询
 */
@Mapper
public interface UserRedpacket_RedpacketMapper {

    /**
     *查询用户可使用红包
     */
    List<RedPacketBean> queryredpacketUseabel(String cnickid);

    /**
     *查询失效红包
     */
    List<RedPacketBean> queryredpacketUseless(String cnickid);

    /**
     *查询待派发的红包
     */
    List<RedPacketBean> queryredpacketWait(String cnickid);


    /**
     * 查询用户的红包 android 投注时获取可用红包
     * @param bean
     * @return
     */
    List<UserRedpacket> query_cast_redpacket(RedPacketBean bean);


    /**
     * 调用存储过程发红包
     * @param redPacket
     */
    void sendRedPacket(RedPacketBean redPacket);
    
    /**
     * 查询用户交易红包
     */
    
    Rp_UserRpPojo queryUserTradeRedpacket (RedPacketBean bean);


    /**
     * 查询正常使用红包数量
     * @param cnickid
     * @return
     */
    @Select("select count(1) from tb_user_redpacket t,tb_redpacket v where t.crpid = v.crpid and t.istate = 1 and t.cnickid = #{cnickid}")
    int querytitleNumU(String cnickid);

    /**
     *查询未发放、已派发未激活红包数量
     * @param cnickid
     * @return
     */
    @Select("select count(1) from tb_user_redpacket t,tb_redpacket v where t.crpid = v.crpid and (t.istate = 0 or t.istate=5) and t.cnickid = #{cnickid}")
    int querytitleNumW(String cnickid);
}
