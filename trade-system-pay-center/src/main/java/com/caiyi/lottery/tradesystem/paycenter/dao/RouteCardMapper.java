package com.caiyi.lottery.tradesystem.paycenter.dao;


import org.apache.ibatis.annotations.*;

import pay.pojo.RouteCardPojo;



//对应tb_rechargeroute_bankcard
@Mapper
public interface RouteCardMapper {
	@Select("select cbankcode bankCode,ccardtype cardtype,cbankname bankName,cbanstatus banStatus,cvisible as \"visible\", cbancontent banContent, openFlag "
			+ "from tb_rechargeroute_bankcard where cbankcode = #{bankCode} and ccardtype = #{cardtype}")
	RouteCardPojo queryRouteCard(@Param("bankCode")String bankCode, @Param("cardtype")int cardType);
}
