package com.caiyi.lottery.tradesystem.paycenter.recharge.inter;


import pay.bean.PayBean;
import pay.dto.RechDto;

public interface IRecharge {
	//充值接口
	public RechDto addmoney(PayBean bean);
	//回调接口
	public void backNotify(PayBean bean);
}
