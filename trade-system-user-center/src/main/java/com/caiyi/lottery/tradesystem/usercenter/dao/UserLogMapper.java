package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pojo.UserLogPojo;

/**
 * 用户日志记录Dao
 *
 * @author GJ
 * @create 2017-11-24 19:25
 **/
@Mapper
public interface UserLogMapper {
    @Insert("insert into tb_user_log (irecid,cnickid,cmemo,cipaddr,ctype) values (seq_user_log.nextval,#{cnickid},#{cmemo},#{cipaddr},#{ctype})")
    int insertIntoUserLog(UserLogPojo userLogPojo);
}
