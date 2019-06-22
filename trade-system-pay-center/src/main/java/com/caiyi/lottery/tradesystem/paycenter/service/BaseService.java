package com.caiyi.lottery.tradesystem.paycenter.service;

import java.util.List;
import java.util.Map;

import bean.SafeBean;
import pay.bean.PayBean;
import pay.pojo.RechCardPojo;

/**
 * 基础服务
 * @author A-0205
 *
 */
public interface BaseService {
	//获取用户身份信息
	public boolean getUserIdenInfo(PayBean bean);
	//获取用户白名单
	public boolean getUserWhiteGrade(PayBean bean);
	//获取银行卡信息列表
	public Map<String,String> getBankCardMap(String bankCode);

	/**
	 *订单号applyid获取：cardNo,uid,safeKey
	 *
	 * @param bean
	 */
	public void getCardNoByApplyid(PayBean bean);
	
	/**
	 * 根据用户名和银行卡安全中心key查询用户该这些银行卡信息
	 * @param cardList
	 * @param bean
	 * @return
	 */
	public List<SafeBean> getRealCardList(List<RechCardPojo> cardList, PayBean bean);
}
