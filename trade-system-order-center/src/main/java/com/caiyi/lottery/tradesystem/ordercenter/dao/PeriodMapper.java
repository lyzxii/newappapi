package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.PeriodPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * tb_period
 *
 * @author GJ
 * @create 2018-01-11 19:58
 **/
@Mapper
public interface PeriodMapper {

    /**
     * 快频
     * @return
     */
    @Select("select cgameid gid,cperiodid pid,cawardcode acode from tb_period where cgameid in (06,08,54,56,57,04,20) and cawardtime >= add_months(sysdate,-1)")
    List<PeriodPojo> queryHfAwardcodes();

    @Select("select cgameid gid,cperiodid pid,cawardcode acode from tb_period where cgameid in (01,50,03,07,51,52,53) and cawardtime >= add_months(sysdate,-3)\n")
    List<PeriodPojo> queryLfAwardcodes();

    @Select("select decode(cgameid,'01',decode(length(cawardcode),23,substr(cawardcode, 0, 20) ,cawardcode),cawardcode ) acode from tb_period where cgameid = #{gid} and cperiodid = #{pid} and icodeaudit =1")
    List<String> queryAwardcodes(@Param("gid") String gid, @Param("pid") String pid);

}
