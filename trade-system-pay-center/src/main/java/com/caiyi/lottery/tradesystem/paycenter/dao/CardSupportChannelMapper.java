package com.caiyi.lottery.tradesystem.paycenter.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pay.pojo.CardSupportChannelPojo;

import java.util.List;



//对应tb_bankcard_support_channel
@Mapper
public interface CardSupportChannelMapper {
	@Select("select cbankcode bankcode,ccardtype cardtype,cchannel channel,cproduct product,ckey as \"key\",cminlimit minlimit,"
			+ "cmaxlimit maxlimit,cdaylimit daylimit,copenflag openflag,cbindidcard bindidcard,iorder as \"order\" "
			+ "from tb_bankcard_support_channel "
			+ "where cbankcode = #{bankCode} and ccardtype = #{cardtype} order by iorder desc")
	List<CardSupportChannelPojo> queryBankSupportChannel(@Param("bankCode")String bankCode, @Param("cardtype")int cardType);
	
	@Select("select cchannel channel,cproduct product,ckey as \"key\""
			+ "from tb_bankcard_support_channel "
			+ "where cbankcode = #{bankCode} and ccardtype = #{cardtype} order by iorder desc")
	List<CardSupportChannelPojo> queryBankSupportChannelOpen(@Param("bankCode")String bankCode, @Param("cardtype")int cardType);
}
