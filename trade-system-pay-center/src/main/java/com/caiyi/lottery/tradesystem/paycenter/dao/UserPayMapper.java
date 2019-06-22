package com.caiyi.lottery.tradesystem.paycenter.dao;


import org.apache.ibatis.annotations.*;
import pay.bean.PayBean;
import pay.pojo.UserPayPojo;


//对应tb_user_pay
@Mapper
public interface UserPayMapper {
	@Update("update tb_user_pay set csafekey = #{safekey} where capplyid = #{applyid}")
	int updateUerPaySafeKey(@Param("safekey")String safekey, @Param("applyid") String applyid);

	@Select("select t.cbankid bankid,t.csafekey safeKey, t.cnickid \"uid\",t.cconfirmid confirmid,capplyinfo merchantId,imoney addmoney,isuccess isSuccess "
			+ "from tb_user_pay t where t.capplyid = #{applyid}")
	UserPayPojo queryPayInfo(@Param("applyid") String applyid);

	@Select("select csafekey from tb_user_pay where capplyid = #{applyid}")
	String querySafeKey(@Param("applyid") String applyid);

	@Update("update tb_user_pay set cconfirmid=#{dealid} where cnickid=#{uid} and capplyid=#{applyid}")
	int updateUserPayDealid(PayBean bean);

	@Select("select count(1) from tb_user_pay t where t.istate=2 and t.imoney<20 and t.capplydate>to_date('2017-03-28','yyyy-MM-dd') and t.cnickid=#{nickid}")
	int queryFirstIsLower20(@Param("nickid") String nickid);
	
	@Update("update tb_user_pay set capplyinfo=#{merchantId} where cnickid=#{uid} and capplyid=#{applyid}")
	int updateUserMerchantId(PayBean bean);
	
	@Select("select capplyinfo from tb_user_pay where capplyid = #{applyid}")
	String queryMerchantId(@Param("applyid") String applyid);
}
