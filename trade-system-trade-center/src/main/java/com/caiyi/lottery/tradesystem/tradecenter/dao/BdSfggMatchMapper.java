package com.caiyi.lottery.tradesystem.tradecenter.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



//对应tb_match表
@Mapper
public interface BdSfggMatchMapper {

	//获取场次列表     特殊使用返回Map
	@Select("select cperiodid expect,imatchid mid,cmname hn,csname gn,to_char(cbegintime,'yyyy-mm-dd hh24:mi:ss') bt,"
			+ "to_char(cendtime,'yyyy-mm-dd hh24:mi:ss') et,cbet3 b3,cbet1 b1,cbet0 b0,cmscore ms, "
			+ "csscore ss, cresult rs,close,cmatchname mname,csf sf,ccolor cl from tb_bd_sfgg_match "
			+ "where cperiodid=#{pid} and isale=0 and istatus=0 order by imatchid")
	List<Map<String, Object>> bdSfggMatchList(@Param("pid")String pid);
	
}
