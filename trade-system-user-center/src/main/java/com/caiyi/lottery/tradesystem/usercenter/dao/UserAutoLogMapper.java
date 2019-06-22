package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pojo.UserAutoLogPojo;

/**
 * Created by tiankun on 2017/12/1.
 */
@Mapper
public interface UserAutoLogMapper {

    @Insert("insert into tb_user_auto_log (id,cnickid,cgameid,cowner,ctype,ilimit,iminmoney,imaxmoney,ibmoney,itype,irate,ibuy,itimes,ctime,cdes)values (seq_user_auto_log.nextval,#{cnickid},#{cgameid},#{cowner},#{ctype},#{ilimit},#{iminmoney},#{imaxmoney},#{ibmoney},#{itype},#{irate},#{ibuy},#{itimes},sysdate,#{cdes})")
    int insertUserAutoLog(UserAutoLogPojo userAutoLogPojo);
}
