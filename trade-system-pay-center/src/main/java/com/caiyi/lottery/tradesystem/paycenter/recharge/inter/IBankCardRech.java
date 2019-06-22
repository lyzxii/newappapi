package com.caiyi.lottery.tradesystem.paycenter.recharge.inter;


import pay.bean.PayBean;
import pay.dto.RechDto;

public interface IBankCardRech extends IRecharge{
	//同意付款
	public RechDto agreePay(PayBean bean) throws Exception;

}
