package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Select;
import trade.bean.TradeBean;
import trade.pojo.ProjPojo;

//对应tb_proj_{gid}表
@Mapper
public interface ProjMapper {

	//30s内查询相同方案
	int countSameProjKm(TradeBean bean);
	//30s内查询相同方案
	int countSameProjZL(TradeBean bean);
	//查询方案信息
	@Select("select cnickid \"uid\",icast \"cast\",to_char(cendtime,'yyyy-MM-dd HH24:mi:ss') endtime,cperiodid pid,cguoguan guoguan,cmatchs matchs," +
	"ccodes codes,ifile,decode(extendtype,6,6,7,7,8,8,9,9,11,11,13,13,12,12,14,14,15,15,isource) source," +
	"decode(sign(cendtime-sysdate),-1,0,decode(istate,-1,1,0)) pay from tb_proj_${gid} where cprojid = #{hid}")
	ProjPojo queryProjectInfo(TradeBean bean);
}
