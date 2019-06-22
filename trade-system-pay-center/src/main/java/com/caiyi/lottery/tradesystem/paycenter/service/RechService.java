package com.caiyi.lottery.tradesystem.paycenter.service;

import com.caiyi.lottery.tradesystem.paycenter.dao.RechCardChannelMapper;
import pay.bean.PayBean;
import pay.bean.PaySftBean;
import pay.dto.OrderStatusDto;
import pay.pojo.RechCardChannelPojo;

/**
 * 充值服务
 * @author A-0205
 *
 */
public interface RechService {
	//记录银行卡信息
	public void recordBankCardInfo(PayBean bean) throws Exception;
	//检测充值卡信息
	public boolean checkRechargeInfo(PayBean bean);
	//保存错误信息
	public void saveUserPayErrorInfo(PayBean bean);
	//查询订单状态
	public OrderStatusDto queryOrderStatus(PayBean bean);

	void updateUserPayDealid(PayBean bean);

	//保存盛付通协议信息
	int saveShengpayOrderInfo(PaySftBean sftBean);
}
