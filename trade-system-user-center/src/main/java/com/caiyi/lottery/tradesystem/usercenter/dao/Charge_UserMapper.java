package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用于 tb_user_charge 和 tb_user表的联合查询
 */
@Mapper
public interface Charge_UserMapper {
    @Select("select count(1) from tb_user_charge t,tb_user t1 where t.cnickid=t1.cnickid and t.itype=1 and t.cadddate>sysdate-365 and (t1.CMOBILENOMD5=#{mobileNoMD5} or t1.CIDCARDMD5=#{idCardMD5})")
    int queryIsNewUser(@Param("mobileNoMD5") String mobileNoMD5, @Param("idCardMD5") String idCardMd5);
}
