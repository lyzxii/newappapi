package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;

/**
 * 更新用户信息Service
 * 修改密码，忘记密码,....
 *
 * @author GJ
 * @create 2017-12-04 20:28
 **/
public interface ModifyUserInfoService {

    /**
     * 开启手机号登陆
     * @param bean
     * @return
     * @throws Exception
     */
    int openMobilenoLogin(UserBean bean) throws Exception ;

    /**
     * 保存设备号
     * @param bean
     */
    void saveimei(BaseBean bean);

    
    /**
     * 绑定用户提款银行卡
     * @param bean
     * @throws Exception 
     */
    void bindUserBankCard(UserBean bean) throws Exception;

    /**
     * 更换手机号检查
     * @param bean
     */
    void changeMobileCheck(UserBean bean) throws Exception;
}
