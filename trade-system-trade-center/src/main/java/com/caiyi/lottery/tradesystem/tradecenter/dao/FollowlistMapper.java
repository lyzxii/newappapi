package com.caiyi.lottery.tradesystem.tradecenter.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import trade.bean.TradeBean;

import java.util.List;

/**
 * tb_followlist
 */
@Mapper
public interface FollowlistMapper {

    @Select("select to_char(cadddate,'yyyy-MM-dd HH24:mi:ss') from tb_followlist where cshareprojid = #{hid} and cnickid = #{uid} and imulity = #{muli} order by cadddate desc")
    List<String> queryLatestFollowTime(TradeBean bean);


    @Insert("insert into TB_FOLLOWLIST (CPROJID, CNICKID ,CGAMEID ,CPERIODID,CCODES,IMULITY,ITMONEY,CENDTIME,IWRATE,CSHAREPROJID,CSHARENICKID) " +
            "values (#{hid},#{uid},#{gid},#{pid},#{codes},#{muli},#{money}, to_date(#{endTime}, 'yyyy-mm-dd hh24:mi:ss'),#{wrate},#{zid},#{fuid})")
    int insertFollowRecord(TradeBean bean);


    @Select("select count(1) from tb_followlist where cnickid = #{uid} and cshareprojid = #{zid}")
    Integer queryExist(TradeBean bean);



}
