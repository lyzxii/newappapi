package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;

import pojo.Acct_UserPojo;
import pojo.UserAcctPojo;
import pojo.UserPojo;

/**
 * 用户表基础信息查询
 * @author A-0205
 *
 */
public interface UserBasicInfoService {
	/**
	 * 查询用户白名单等级
	 * @param uid
	 * @return
	 */
	public String queryUserWhiteGrade(BaseBean bean);

	/**
	 * 查询用户基本信息
	 * @param uid
	 * @return
	 */
	public UserPojo queryUserInfo(BaseBean bean);


	UserPojo queryUserInfoForCardCharge(BaseBean bean);

	/**
	 * 获取用户积分
	 * @param bean
	 * @return
	 */
	UserAcctPojo queryUserPoint(BaseBean bean);

	/**
	 * 更新用户积分
	 * @param bean
	 * @return
	 */
	int updateUserPoint(UserBean bean) throws Exception;

	/**
	 * 统计用户充值次数
	 * @param bean
	 * @return
	 */
	Integer countUserCharge(BaseBean bean);

	/**
	 * 查询用户vip数量
	 * @param bean
	 * @return
	 */
	Integer queryUserVipAgentCount(BaseBean bean);

	/**
	 * 查询appagentid
	 * @param bean
	 * @return
	 */
	String queryAppAgentId(BaseBean bean);

	/**
	 * 更新appAgentid
	 * @param bean
	 * @return
	 */
	int updateAgentid(UserBean bean);

	/**
	 * 查询用户vip和白名单等级
	 * @param cnickid
	 * @return
	 */
	UserPojo queryUserVipAndWhitelistLevel(String cnickid);

	/**
	 * 查询一年内的消费次数
	 * @param bean
	 * @return
	 * @throws Exception
	 */
    Integer countOutByNickidInAYear(BaseBean bean) throws Exception;

	/**
	 * 查询是否新用户
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	Integer isNewUser(BaseBean bean) throws Exception;

    Acct_UserPojo queryUserAccountInfo(BaseBean bean);

	Integer countSelfBuy(BaseBean bean);
}
