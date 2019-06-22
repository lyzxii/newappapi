package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


//对应TB_PROJ_STATS_TASK表
@Mapper
public interface ProjStatsTaskMapper {
	@Insert("insert into TB_PROJ_STATS_TASK (CPROJID, CGAMEID) values (#{hid},#{gid})")
	int insertProjStatsTask(@Param("hid")String hid, @Param("gid")String gid);
}
