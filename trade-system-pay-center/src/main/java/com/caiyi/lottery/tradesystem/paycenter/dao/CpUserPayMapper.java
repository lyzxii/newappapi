package com.caiyi.lottery.tradesystem.paycenter.dao;


import com.caiyi.lottery.tradesystem.BaseBean;
import org.apache.ibatis.annotations.*;

import pay.bean.PayBean;


@Mapper
public interface CpUserPayMapper {
	//创建用户订单
	void createPayOrder(PayBean bean);

	/**
	 * 充值成功调用
	 * @param bean
	 */
	void addmoneysuc(PayBean bean);

	/**
	 * 代理商转款
	 */
	void agentTransfer(BaseBean bean);
}
