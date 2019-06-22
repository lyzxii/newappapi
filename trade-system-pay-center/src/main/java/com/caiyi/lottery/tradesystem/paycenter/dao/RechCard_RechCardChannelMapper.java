package com.caiyi.lottery.tradesystem.paycenter.dao;


import java.util.List;

import org.apache.ibatis.annotations.*;

import pay.bean.PayBean;
import pay.pojo.RechCardPojo;


//对应tb_recharge_card和tb_recharge_card_channel
@Mapper
public interface RechCard_RechCardChannelMapper {
	//查询用户显示的所有银行卡
	public List<RechCardPojo> queryUserChannelVisibleCard(PayBean bean);


	@Select("select t1.cbankcode bankCode,t1.ccardtype cardtype from tb_recharge_card t1,tb_recharge_card_channel t2 where t1.cnickid=t2.cnickid " +
			"and t1.csafekey=t2.csafekey and t1.cnickid=#{uid} and t1.csafekey=#{safekey}\n" +
			"and cstatus = '1' and cuserpayid is not null")
	List<RechCardPojo> queryBankCardInfo(@Param("uid") String uid, @Param("safekey") String safekey);

}
