package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRedpacketMapper {
    @Select("select count(1) from tb_user_redpacket where crpid=#{rpid} and cnickid=#{nickid}")
    Integer countWithRpidAndNickId(@Param("rpid") Integer rpid, @Param("nickid") String nickid);
}
