package com.caiyi.lottery.tradesystem.tradecenter.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



//对应tb_top_match表
@Mapper
public interface TopMatchMapper {

	//获取场次列表   特殊使用返回Map
	@Select("select cmatchid matchid,cmid mid,cindex cindex,teamname name,istate state,isale isale,to_char(cmatchtime,'yyyy-mm-dd hh24:mi:ss') matchtime,"
			+ "to_char(cendtime,'yyyy-mm-dd hh24:mi:ss') endtime,sp sp,zcl zcl,gl gl,result result,icancel cancel,data data from tb_top_match "
			+ "where itype = #{type} and cmatchid = #{matchid} and  istate = 0 and isale=0 order by cindex ")
	List<Map<String,Object>> topMatchList(@Param("type")String type,@Param("matchid")String matchid);
}
