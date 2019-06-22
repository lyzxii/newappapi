package com.caiyi.lottery.tradesystem.usercenter.service;


import com.caiyi.lottery.tradesystem.BaseBean;

import bean.UserBean;

/**
 * 用户中心-Token接口(TokenManage)
 * @author 571
 * @create 2017-11-27 14:25:59
 */
public interface TokenManageService {


    /**
     * 注册token入库.
     * @param bean
     */
    void registerToken(UserBean bean);

    /**
     * 更新token登录传递的密码.
     * @param bean
     */
    void updateTokenPassword(BaseBean bean, String newPwd) throws Exception;

	/**
	 * 更新用户token
	 * @param viplevel
	 * @param whitegrade
	 * @param bean
	 */
	void updateToken(String viplevel, String whitegrade, BaseBean bean);

	/**
	 * token检测登录
	 * @param bean
	 * @return 
	 */
	BaseBean checkLogin(BaseBean bean);

	/**
	 * token登录
	 * @param bean
	 */
	void tokenLogin(BaseBean bean);
	
	/**
	 * 查询用户token信息
	 * @param bean
	 */
	void queryUserToken(BaseBean bean);

}
