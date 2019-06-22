package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pay.pojo.UserLogPojo;

/**
 * Created by XQH on 2017/12/26.
 * 操作 tb_user_log
 */
@Mapper
public interface UserLogMapper {

    @Insert("insert into tb_user_log (irecid,cnickid,cmemo,cipaddr,ctype) values (seq_user_log.nextval,#{cnickid},#{cmemo},#{cipaddr},#{ctype})")
     int addUserLog(UserLogPojo userLogPojo);

}
