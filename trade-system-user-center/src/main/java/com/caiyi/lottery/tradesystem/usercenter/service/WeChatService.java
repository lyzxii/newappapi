package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.WeChatBean;
import com.alibaba.fastjson.JSONObject;
import dto.WeChatDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface WeChatService {

    void checkParam4VerifySmsCodeWechat(WeChatBean bean);

    WeChatDTO getMobileBindAccountWechat(WeChatBean bean)throws Exception;

    /**
     * 微信注册前校验注册信息是否合法
     * @param bean
     * @return
     */
    int beforeWechatRegister(WeChatBean bean) throws Exception;

    /**
     * 校验微信注册用户名和微信openid,unionid是否合法
     * @param bean
     * @param allowEmptyPwd 密码不能为空
     * @param allowEmptyMobileno 手机号不能为空
     * @return
     */
    int checkWechatRegisterInfo(WeChatBean bean, boolean allowEmptyPwd, boolean allowEmptyMobileno);

    /**
     * 获取微信用户信息
     * @param bean
     * @return
     * @throws Exception
     */
    void getWechatUserInfo(WeChatBean bean) throws Exception;

    /**
     * 微信注册并绑定9188账号
     * @param bean
     */
    void registerUser(WeChatBean bean) throws Exception;

    WeChatDTO weChatSetDate(WeChatBean bean) throws Exception;

    /**
     * 通过微信code登录
     * @param bean
     * @return
     * @throws Exception
     */
    WeChatDTO wechatLogin(WeChatBean bean) throws Exception;

    /**
     * 绑定微信账户到彩亿账户前检测接口参数
     * @param bean
     * @throws Exception
     */
    void bindWechatParamCheck(WeChatBean bean) throws Exception;

    /**
     * 绑定9188ID到微信AppID
     * @param bean
     * @throws Exception
     */
    void bind9188UserId2WXAppId(WeChatBean bean) throws Exception;

    /**
     * 绑定手机号到彩亿账户前检测接口参数
     * @param bean
     */
    void bindWechatMobilenoParamCheck(WeChatBean bean) throws Exception;

    /**
     * 校验短信验证码，绑定手机号到9188账号并登录
     * @param bean
     * @throws Exception
     */
    void bindMobilenoToCaiyi(WeChatBean bean) throws Exception;
}
