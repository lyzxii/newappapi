package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import redpacket.bean.RedPacketBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 *tb_redpacket表
 */
@Mapper
public interface RedpacketMapper {

    @Select("select imoney,itype from tb_redpacket where crpid=#{crpid}")
    RedPacketBean queryRpInfo(String crpid);

}
