package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import trade.pojo.PeriodPojo;

//对应tb_period表
@Mapper
public interface PeriodMapper {
	
	@Select("select cendtime endtime,cfendtime fendtime from tb_period where cgameid = #{gid} and cperiodid = #{pid}")
	PeriodPojo queryEndTime(@Param("gid")String gid, @Param("pid")String pid);
	
	PeriodPojo queryNomarlEndState(String stime, String gid, String pid);
}
