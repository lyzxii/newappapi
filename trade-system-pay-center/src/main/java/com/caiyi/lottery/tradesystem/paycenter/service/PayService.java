package com.caiyi.lottery.tradesystem.paycenter.service;

import pay.bean.PayBean;
import pay.dto.BindCardInfoDto;
import pay.dto.RechDto;
import pay.dto.UserRechCardDto;

import java.util.List;

/**
 * 支付服务
 * @author A-0205
 *
 */
public interface PayService {
	
	/*
	 * 用户充值
	 */
	RechDto addmoney(PayBean bean);
	
	/**
	 * 确认支付
	 * @return 
	 */
	RechDto agreeConsume(PayBean bean);
	/**
	 * 用户银行卡列表
	 * @param bean
	 * @return
	 */
	UserRechCardDto userRechCardList(PayBean bean);

	List<BindCardInfoDto> deleteRechargeCard(PayBean bean);
}
