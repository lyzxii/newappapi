package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * allylogmapper
 *
 * @author GJ
 * @create 2017-12-21 18:14
 **/
@Mapper
public interface AllyLogMapper {
    @Insert("insert into tb_ally_log(cnickid,addtime,itype,host) values(#{uid},to_char(sysdate,'yyyy-MM-dd'),#{type},#{host})")
    int insertIntoTbAllyLog(@Param("uid") String uid, @Param("type") String type, @Param("host") String host);
}
