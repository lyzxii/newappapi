package com.caiyi.lottery.tradesystem.tradecenter.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



//对应tb_match表
@Mapper
public interface MatchMapper {

	//获取场次列表     特殊使用返回Map
	@Select("select cperiodid expect,imatchid mid,cmname hn,csname gn,to_char(cbegintime,'yyyy-mm-dd hh24:mi:ss') bt,"
			+ "to_char(cendtime,'yyyy-mm-dd hh24:mi:ss') et,cbet3 b3,cbet1 b1,cbet0 b0,"
			+ "cmscore ms, csscore ss, cresult rs,close,cmatchname mname,cspf spf,cbqc bqc, ccbf cbf, cjqs jqs,csxp sxp,ccolor cl  "
			+ "from tb_match where itype = #{type} and cperiodid = #{pid} and isale=0 order by imatchid")
	List<Map<String, Object>> matchList(@Param("type")String type, @Param("pid")String pid);
	
	//获取场次列表     特殊使用返回Map
	@Select("select cperiodid expect,imatchid mid,cmname hn,csname gn,to_char(cbegintime,'yyyy-mm-dd hh24:mi:ss') bt,"
			+ "to_char(cendtime,'yyyy-mm-dd hh24:mi:ss') et,cbet3 b3,cbet1 b1,cbet0 b0,"
			+ "cmscore ms, csscore ss, cresult rs,close,cmatchname mname,cspf spf,cbqc bqc, ccbf cbf, cjqs jqs,csxp sxp,ccolor cl  "
			+ "from tb_match where itype = #{type} and cperiodid = #{pid} and isale>0 order by imatchid")
	List<Map<String, Object>> bdMatchList(@Param("type")String type, @Param("pid")String pid);
}
