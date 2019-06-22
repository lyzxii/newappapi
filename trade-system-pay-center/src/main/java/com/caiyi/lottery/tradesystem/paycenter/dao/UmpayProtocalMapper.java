package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pay.pojo.UmpayProtocalPojo;

import java.util.List;

/**
 * 对应表  TB_UMPAY_PROTOCOL
 */
@Mapper
public interface UmpayProtocalMapper {
    @Select("select distinct ccardno,cstatus from TB_UMPAY_PROTOCOL where cnickid = #{cnickid} and (cuserpayid is not null or cauthentication = '1') order by cstatus desc")
    List<UmpayProtocalPojo> findUmpayByNickid(String cnickid);
}
