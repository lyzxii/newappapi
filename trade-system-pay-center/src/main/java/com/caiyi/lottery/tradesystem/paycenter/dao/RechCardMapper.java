package com.caiyi.lottery.tradesystem.paycenter.dao;


import java.util.List;

import org.apache.ibatis.annotations.*;

import pay.bean.PayBean;
import pay.pojo.RechCardPojo;


//对应tb_recharge_card
@Mapper
public interface RechCardMapper {
	//用户充值银行卡是否存在
	@Select("select count(1) from tb_recharge_card where csafekey = #{safeKey} and cnickid = #{uid}")
	public int userCardExsit(@Param("safeKey")String safeKey, @Param("uid")String uid);
	
	//是否存在没有手机号的银行卡
	@Select("select count(1) from tb_recharge_card where csafekey = #{safeKey} and cnickid = #{uid} and cmobile is null")
	public int userCardExsitNoMobie(@Param("safeKey")String safeKey, @Param("uid")String uid);
	
	//插入用户充值银行卡
	public int insertRechCard(PayBean bean);
	
	//根据安全中心充值卡key查询卡信息
	public RechCardPojo queryCardByKey(String safekey, String uid);
	
	//更新充值卡信息手机号
	@Update("update tb_recharge_card set cmobile = #{mobileNo} where cnickid = #{uid} and csafekey = #{safeKey}")
	public int updateRechCardMobile(PayBean bean);
	
	//更新充值卡信息手机号
	@Update("update tb_recharge_card set cbankcode = #{bankCode},cbankname = #{bankName},ccardtype = #{cardtype},"
			+ "ccardname = #{cardName} where cnickid = #{uid} and csafekey = #{safeKey}")
	public int updateRechCardInfo(PayBean bean);

	//查询用户显示的所有银行卡
	public List<RechCardPojo> queryUserVisibleCard(String uid);

	//更新充值卡信息cstatus
	@Update("update tb_recharge_card set cstatus = '1' where cnickid = #{uid} and csafekey = #{safeKey}")
	public int updateRechCstatus(@Param("uid") String uid, @Param("safeKey") String safeKey);

	//查询bankcode
	@Select("select cbankcode bankCode,ccardtype cardtype from tb_recharge_card where cnickid = #{uid} and csafekey = #{safeKey}")
	public RechCardPojo getBankCode(@Param("uid") String uid, @Param("safeKey") String safeKey);

	@Select("select distinct a.ccardno cardNo,a.cstatus status,a.csafekey safeKey from tb_recharge_card a,tb_recharge_card_channel b where a.cnickid = #{cnickid} and (b.cuserpayid is not null or a.cauthentication = '1') order by a.cstatus desc")
	public List<RechCardPojo> findRechCardByNickid(String cnickid);

	@Delete("update tb_recharge_card set cstatus='0' where cnickid=#{uid} and csafekey=#{safeKey}")
	int updateRechCardStatus(PayBean bean);

	@Delete("update tb_recharge_card set cbankcode=#{bankCode},cbankname=#{bankName},ccardtype=#{cardtype},ccardname=#{cardName} " +
			"where cnickid=#{uid} and csafekey=#{safeKey}")
	int updateRechCard(PayBean bean);
}
