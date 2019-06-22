package com.caiyi.lottery.tradesystem.paycenter.service;

import pay.bean.PayBean;

/**
 * 回调服务
 * @author A-0205
 *
 */
public interface NotifyService {
	//账户加款操作
	void applyAccountSuc(PayBean bean);
	//更新银行卡信息
	void updateBankCardInfo(PayBean bean) throws Exception;

	//查询bankid
	void queryBankId(PayBean bean);

	/**
	 * 更新支付宝微信充值缓存
	 *
	 * @param bean
	 */
	void updateWXAndZfbPayInfo(PayBean bean);

	void bindCard(PayBean bean) throws Exception;

	void updateRechargeCard(PayBean bean);

	void defaultBankCardNotify(PayBean bean) throws Exception;

	void defaultRechNotify(PayBean bean);

	void basicNotifyService(PayBean bean);
}
