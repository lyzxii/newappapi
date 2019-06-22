package com.caiyi.lottery.tradesystem.usercenter.util;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.util.MD5Util;

/**
 * @author wxy
 * @create 2017-12-18 21:04
 **/
public class WeChatActionBase {
    public final static String DEFAULT_MD5_KEY = "http://www.9188.com/";
    /**
     * 使用彩票加密串加密用户登录密码,并设置加密串到bean对象中.
     * @param bean
     * @param plainPwd 登录密码原文
     * @return 加密后的密码密文
     * @throws Exception
     */
    public String encryptPwd(BaseBean bean, String plainPwd) throws Exception {
        String privateKey = DEFAULT_MD5_KEY;
        bean.setPrivateKey(privateKey);
        return MD5Util.compute(plainPwd + privateKey);
    }
}
