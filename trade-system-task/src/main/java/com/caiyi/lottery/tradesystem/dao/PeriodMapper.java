package com.caiyi.lottery.tradesystem.dao;

import data.pojo.PeriodPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author GJ
 * @create 2018-03-07 19:24
 **/
@Mapper
public interface PeriodMapper {

    @Select("select cgameid gid,cperiodid pid,cawardcode acode from tb_period where cgameid in (01,50,03,07,51,52,53) and cawardtime >= add_months(sysdate,-3)")
    List<PeriodPojo> getMpPeriod();

    @Select("select cgameid gid,cperiodid pid,cawardcode acode from tb_period where cgameid in (06,08,54,56,57,04,20) and cawardtime >= add_months(sysdate,-1)")
    List<PeriodPojo> getKpPeriod();

}
