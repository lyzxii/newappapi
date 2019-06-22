package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import redpacket.bean.RedPacketBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * tb_redpacket_huodong表
 */
@Mapper
public interface RedpacketActivityMapper {

    @Insert("insert into tb_redpacket_huodong(cnickid,itype,crpid,crpmoney) values(#{cnickid},#{itype},#{crpid},#{imoney})")
    int insertIntoRedpacket_huodong(RedPacketBean bean);

}
