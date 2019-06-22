package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用于 tb_redpacket_new_task 表相关操作
 */
@Mapper
public interface RedpacketNewTaskMapper {
    @Select("select (substr(t.cnickid,0,2)|| '*****') cnickid from tb_redpacket_new_task t where istate = 1 and rownum < 31 order by t.cfinishtime desc ")
    List<String> queryRollingCode();

    @Select("select count(1) num from tb_redpacket_new_task t where t.istate=1 and t.cnickid=#{nickid}")
    int countByNickidAndState1(@Param("nickid") String nickid);

    @Select("select count(1) num from tb_redpacket_new_task t where t.cnickid != #{nickid} and (t.cmobilenomd5=#{mobileNoMD5} or t.cidcardmd5=#{idCardMD5})")
    int countByNickidAndMobileOrIdcard(@Param("nickid") String nickid, @Param("mobileNoMD5") String mobileNoMD5, @Param("idCardMD5") String idCardMD5);

    @Select("select istate from tb_redpacket_new_task t where t.cnickid=#{nickid}")
    Integer getStateByNickid(@Param("nickid") String nickid);
}
