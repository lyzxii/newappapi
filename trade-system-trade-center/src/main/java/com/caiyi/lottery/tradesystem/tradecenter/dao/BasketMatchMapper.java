package com.caiyi.lottery.tradesystem.tradecenter.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;



//对应tb_basket_match表
@Mapper
public interface BasketMatchMapper {

	//获取场次列表   特殊使用返回Map
	@Select("select citemid itemid,imatchid mid,cmname hn,csname gn,to_char(cendtime,'yyyy-mm-dd hh24:mi:ss') et, "
			+ "to_char(cmatchtime,'yyyy-mm-dd hh24:mi:ss') mt,cbet3 b3,cbet0 b0,cmscore ms, csscore ss, "
			+ "cresult rs,csf,crfsf,csfc,cdxf,close,zclose,cmatchname mname,cname name,ccolor cl,isale from tb_basket_match "
			+ "where istate = 0 and isale>0 order by citemid")
	List<Map<String,Object>> basketMatchList();
}
