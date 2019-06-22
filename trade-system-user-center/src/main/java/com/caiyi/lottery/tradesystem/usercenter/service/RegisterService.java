package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import dto.UserRegistDTO;
import response.UserRegistResp;

/**
 * 注册相关接口
 *
 * @author GJ
 * @create 2017-12-04 21:22
 **/
public interface RegisterService {
    /**
     * 手机号注册资格检测
     * @param bean
     */
    @Deprecated
    void mobileRegisterCheck(UserBean bean);
    /**
     * 手机注册结果
     * @param bean
     */
    UserRegistResp phoneRegisterResult(UserBean bean);

    /**
     * 手机注册检查20分钟短信校验成功，和1分钟重复注册
     * @param bean
     */
    void checkminsRegister(UserBean bean);
    /**
     * 用户注册
     * @param bean
     */
     void registerUser(UserBean bean);

    /**
     * 手机注册，设置setFunc
     * @param bean
     */
     void checkYzm(UserBean bean);

    /**
     * 发送短信验证码前检测接口参数
     * @return
     * @param bean
     */
    int checkParamByVerifySms(UserBean bean);

    /**
     * 手机校验
     * @param bean
     * @param permitEmpty
     * @return
     */
     int verifyMobileno(BaseBean bean,String mobileNo, boolean permitEmpty);


    /**
     * 用户数据组装
     * @param bean
     * @return
     */
    int setBaseData(UserBean bean);

    /**
     * 带验证码的用户名注册
     * @param bean
     */
    void registerSourceUser(UserBean bean);


    /**
     * 校验短信验证码
     * @param bean
     * @param mobileNo
     * @param tzm
     * @param type
     * @param isAddCache
     * @throws Exception
     */
    void verifyMobCode(BaseBean bean,String mobileNo,String tzm,String type,boolean isAddCache) throws Exception;

    /**
     * 注册结果处理
     * @param bean
     * @param rsp
     * @paramponse
     * @return
     */
    int registerResult(UserBean bean, UserRegistDTO rsp);
    /**
     * 查询APP代理商接口
     * @param bean
     */
    void queryagentid(UserBean bean);

}
