package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pojo.UserImeiPojo;

/**
 * 用户设备Dao
 *
 * @author GJ
 * @create 2017-11-28 11:08
 **/
@Mapper
public interface UserImeiMapper {
    @Select("select count(*) from tb_cellphone_imei where cnickid =#{nickid}")
    int queryUserImeiCount(String nickid);

    @Insert("insert into tb_cellphone_imei(cnickid,cimei,cagentid,isource) values(#{cnickid},#{cimei},#{cagentid},#{isource})")
    int insertUserImei(UserImeiPojo userImeiPojo);
}
