package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import pay.pojo.RechDayLimitPojo;

//tb_rechargeroute_daylimit表
@Mapper
public interface RechDayLimitMapper {

    @Select("select rechargemoney from tb_rechargeroute_daylimit where statday =#{statday} " +
            "and channel = #{channel} and product = #{product} and csafekey =#{csafekey}")
    String queryRechMoney(RechDayLimitPojo rechlimit);


    @Select("select count(1) from tb_rechargeroute_daylimit where statday = #{statday} " +
            "and channel = #{channel} and product = #{product} and csafekey =#{csafekey}")
    int countRechDayLimit(RechDayLimitPojo rechlimit);


    @Select("select count(1) from tb_rechargeroute_daylimit where instr(applyids,#{applyid})> 0 and statday = #{statday} " +
            "and channel = #{channel} and product = #{product} and csafekey =#{csafekey}")
    int countRechDayLimitWithApplyIds(RechDayLimitPojo rechlimit);


    @Update("update tb_rechargeroute_daylimit set applyids = applyids ||#{applyids} , rechargemoney = rechargemoney + #{addmoney}, rechargenum = rechargenum + 1" +
            ",updatedate = sysdate where statday = #{statday} and channel = #{channel} and product = #{product} and csafekey =#{csafekey}")
    int updateRechDayLimit(RechDayLimitPojo rechlimit);


    @Insert("insert into tb_rechargeroute_daylimit(id,statday,channel,product,bankcode,cardno,cardtype,rechargemoney,rechargenum,applyids,updatedate,csafekey)" +
            "values(seq_rechargeroute_daylimit.nextval,#{statday},#{channel},#{product},#{bankcode},#{cardno},#{cardtype},#{rechargemoney},1,#{applyids},sysdate,#{csafekey})")
    int insertRechDayLimit(RechDayLimitPojo rechlimit);


}
