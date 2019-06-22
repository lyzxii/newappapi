package com.caiyi.lottery.tradesystem.redpacketcenter.dao;


import redpacket.bean.Card_CardType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 *tb_card tb_card_type表
 */
@Mapper
public interface Card_CardTypeMapper {

    @Select("select t.iactive,to_char(t.cexpireddate,'yyyy-MM-dd HH24:mi:ss') cexpireddate,t.crpdiedate,t.itype,t.imoney,t.crpid,c.cctname,c.inums,c.isource,c.istate from tb_card t,tb_card_type c where t.itype=c.icid " +
            "and ccardid=#{ccardid} and ccardpwd=#{ccardpwd}")
    Card_CardType queryCard_CardType(@Param("ccardid") String ccardid, @Param("ccardpwd")String ccardpwd);

}
