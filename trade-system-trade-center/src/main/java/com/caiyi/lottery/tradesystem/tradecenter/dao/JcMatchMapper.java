package com.caiyi.lottery.tradesystem.tradecenter.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import trade.pojo.JcMatchPojo;


//对应tb_jc_match表
@Mapper
public interface JcMatchMapper {

	//获取场次列表   特殊使用返回Map
	@Select("select citemid itemid,imatchid mid,cmname hn,csname gn,to_char(cendtime,'yyyy-mm-dd hh24:mi:ss') et, "
			+ "to_char(cmatchtime,'yyyy-mm-dd hh24:mi:ss') mt,cbet3 b3,cbet1 b1,cbet0 b0,cmscore ms, "
			+ "csscore ss, cresult rs,cspf,ccbf,cjqs,cbqc,cfspf,cfcbf,cfjqs,cfbqc,close,cmatchname mname,cname name,ccolor cl,isale "
			+ "from tb_jc_match where istate = 0 and isale>0 order by citemid")
	List<Map<String,Object>> jcMatchList();


	@Select("SELECT to_char(t.cendtime,'yyyy-MM-dd HH24:mi:ss') endtime  FROM tb_jc_match t where  istate = 0 and isale > 0 and length(cspf) > 12 and length(crqspf) > 12" +
			" group by to_char(t.cendtime,'yyyy-MM-dd HH24:mi:ss') order by to_char(t.cendtime,'yyyy-MM-dd HH24:mi:ss')")
	List<String> queryMatchEndTimeAfter();


	@Select("SELECT to_char(t.cendtime,'yyyy-MM-dd HH24:mi:ss') endtime  FROM tb_jc_match t where  istate = 0 and isale > 0 and length(cspf) > 12 and length(crqspf) > 12" +
			" group by to_char(t.cendtime,'yyyy-MM-dd HH24:mi:ss') order by to_char(t.cendtime,'yyyy-MM-dd HH24:mi:ss') desc")
	List<String> queryMatchEndTimeBefore();

	//匹配场次截止时间往后场次信息
	@Select("select a.citemid itemid,a.close,a.cspf spf,a.crqspf rqspf from tb_jc_match a where a.cendtime >= (select t.cendtime from tb_jc_match t where t.citemid = #{itemid}) and a.cendtime <=to_date(#{endtime},'yyyy-MM-dd HH24:mi:ss') " +
			"and a.CITEMID <> #{itemid} and istate = 0 and isale > 0 and length(cspf) > 12 and length(crqspf) > 12")
	List<JcMatchPojo> getAfterMatchInfo(@Param("itemid") String itemid, @Param("endtime")String endtime);

	//匹配场次截止时间往前场次信息
	@Select("select a.citemid itemid,a.close,a.cspf spf,a.crqspf rqspf from tb_jc_match a where a.cendtime <= (select t.cendtime from tb_jc_match t where t.citemid = #{itemid}) and a.cendtime >=to_date(#{endtime},'yyyy-MM-dd HH24:mi:ss') " +
			"and a.CITEMID <> #{itemid} and istate = 0 and isale > 0 and length(cspf) > 12 and length(crqspf) > 12")
	List<JcMatchPojo> getBeforeMatchInfo(@Param("itemid") String itemid,@Param("endtime")String endtime);

}
