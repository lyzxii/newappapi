package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import constant.CodeDict;
import response.UserLoginResq;

/**
 * 登入和注册相关接口
 *
 * @author GJ
 * @create 2017-12-04 20:14
 **/
public interface LoginService {
    /**
     * 用户登入后操作
     * @param bean
     */
    UserLoginResq afterLogin(UserBean bean);


    /**
     * 检查登入参数正确性
     * @param bean
     * @return
     * @throws Exception
     */
    boolean checkLoginParam(BaseBean bean)throws  Exception;

    /**
     * 登入
     * @param bean
     */
    void login(UserBean bean);

    /**
     * 生成token
     * @param bean
     * @param codeDict
     */
     void generateNewToken(UserBean bean, CodeDict codeDict);

    /**
     * 根据用户状态获取加密串,加密用户登录密码,并设置加密串到bean对象中.
     * @param bean
     * @param plainPwd
     * @return
     * @throws Exception
     */
     String encryptPwd(BaseBean bean, String plainPwd) throws Exception;

    /**
     * 根据用户状态获取加密串 不查库
     * @param bean
     * @param plainPwd
     * @return
     * @throws Exception
     */
     String encryptPwdNoSql(BaseBean bean, String plainPwd) throws Exception;

    /**
     * 退出登录
     * @param bean
     * @return
     * @throws Exception
     */
     String loginout(BaseBean bean) throws Exception;

}
