package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.AlipayLoginBean;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import dto.AccountBindCaiyiDTO;
import dto.UserInfoDTO;
import response.AlipayLoginResq;

import java.util.List;

/**
 * 快捷登入Service
 *
 * @author GJ
 * @create 2017-12-14 16:15
 **/
public interface AlipayLoginService {
    /**
     * 支付宝绑定入库操作
     * @param bean
     * @param userInfo
     * @throws Exception
     */
    void bindAlipay2Caiyi(AlipayLoginBean bean, UserInfoDTO userInfo) throws Exception;

    /**
     * 绑定支付宝
     *
     * @param bean
     */
    UserInfoDTO checkAccountInfo(AlipayLoginBean bean);

    /**
     * 支付宝绑定操作
     *
     * @param bean
     * @throws Exception
     */
    void bindData(AlipayLoginBean bean) throws Exception;

    /**
     * 生成token
     *
     * @param bean
     */
    void generateNewToken(UserBean bean);

    /**
     * 设置登入参数
     *
     * @param bean
     * @param alipayLoginResq
     */
    void setloginData(AlipayLoginBean bean, AlipayLoginResq alipayLoginResq);

    /**
     * 设置   vip
     *
     * @param bean
     * @throws Exception
     */
    void setAlipayUserAsVip(AlipayLoginBean bean) throws Exception;

    /**
     * 支付宝联合登录，绑定用户真实姓名,绑定支付宝账号,设置支付宝用户等级
     *
     * @param bean
     */
    void addAlipayInfo(AlipayLoginBean bean) throws Exception;

    /**
     * 检查支付宝第一次绑定参数
     *
     * @param bean
     */
    void checkFirstBindParam(AlipayLoginBean bean);

    /**
     * 支付宝第一次绑定
     *
     * @param bean
     */
    void alipayFirstBind(AlipayLoginBean bean);

    /**
     * 获取支付宝手机号关联彩亿用户列表
     *
     * @param mobileno
     * @param accountBindCaiyiDTOList
     */
    void getAlipayAccountList(String mobileno, List<AccountBindCaiyiDTO> accountBindCaiyiDTOList);

    /**
     * 支付宝账号登入
     *
     * @param bean
     * @return
     */
    void loginByAlipay(AlipayLoginBean bean, AlipayLoginResq alipayLoginResq);

    /**
     * 支付宝授权信息监测
     */
    AlipayLoginResq alipayAuthCheck(AlipayLoginBean bean);

    /**
     * 获取支付宝用户信息
     *
     * @param bean
     */
    void getAlipayUserInfo(AlipayLoginBean bean, Boolean setMobile);

    /**
     * 支付宝快捷登入获取验签
     *
     * @param bean
     */
    AlipayLoginResq getAuthInfo(AlipayLoginBean bean);

    /**
     * 支付宝快登绑定检测前检测接口参数
     *
     * @param bean
     */
    void checkParam4zfbBindCheck(AlipayLoginBean bean);

    /**
     * 获取支付宝账号信息
     *
     * @param bean
     */
    void getAlipayOauthData(AlipayLoginBean bean);

    /**
     * 新版app支付宝便捷登录-绑定手机号到已有彩亿账号
     *
     * @param userBean
     * @return
     */
    AlipayLoginResq bindmobileno2caiyi(AlipayLoginBean userBean);


    /**
     * 校验短信验证码，查询已存在彩亿账号
     *
     * @param
     * @return
     */
    BaseResp zfbgetcaiyiaccount(AlipayLoginBean bean);

    /**
     * 绑定支付宝到已有彩亿账号
     *
     * @param bean
     * @return
     */
    AlipayLoginResq checkAlipayInfo(AlipayLoginBean bean);

    /**
     * @param bean
     * @Description: 支付宝用户修改默认登录密码
     * @Date: 13:58 2017/12/20
     * @return:
     */
    BaseResp Upatepwd(AlipayLoginBean bean) throws Exception;
}
