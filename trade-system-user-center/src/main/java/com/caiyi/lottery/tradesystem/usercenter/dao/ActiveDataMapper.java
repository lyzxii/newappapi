package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.ActiveDataPojo;

/**
 * 用于 tb_active_data 表
 * @author wxy
 * @create 2017-12-04 14:41
 **/
@Mapper
public interface ActiveDataMapper {
    @Select("select count(1) from tb_active_data where cidfa = #{idfa}")
    int countByIdfa(@Param("idfa") String idfa);

    @Select("select count(*) from tb_active_data where cimei = #{imei}")
    int countByImei(@Param("imei") String imei);

    @Insert("insert into tb_active_data(iid,cidfa,cimei,cosversion,ccity,cmobiletype,cipaddress,cappversion,csource,cappname,coperator) values(seq_active_data.nextval,#{idfa},#{imei},#{phoneSys},#{cityid},#{phoneModel},#{ipAddr},#{appversion},#{source},#{clientName},#{operator})")
    int insert(ActiveDataPojo activeData);
}
