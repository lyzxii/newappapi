package com.caiyi.lottery.tradesystem.usercenter.util;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.SecurityTool;
import constant.UserConstants;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务-解密工具类
 * @create 2017-12-8 17:32:51
 */
public class DecryptUtil {
    public final static String DEFAULT_MD5_KEY = "http://www.9188.com/";
    /**
     * 解密 AES-Base64
     * @param pwd 密码
     * @param bankCard 银行卡号
     * @param mobileNo 手机号
     * @param idCard 身份证号
     * @param append 附加字段
     */
    public static Map<String,String> decryptByAesBase64(String pwd, String bankCard, String mobileNo, String idCard,String append) throws Exception{
        //传进来是+变成了空格
        Map<String,String> map = new HashMap<>();
        if(!StringUtils.isEmpty(pwd)){
            String decreptPwd = SecurityTool.iosdecrypt(pwd.replaceAll(" ", "+"));
            map.put(UserConstants.PWD_KEY,decreptPwd);
        }
        if(!StringUtils.isEmpty(bankCard)){
            String decreptBid = SecurityTool.iosdecrypt(bankCard.replaceAll(" ", "+"));
            map.put(UserConstants.BANKCARD_KEY,decreptBid);
        }
        if(!StringUtils.isEmpty(mobileNo)){
            String decreptMob = SecurityTool.iosdecrypt(mobileNo.replaceAll(" ", "+"));
            map.put(UserConstants.MOBILENO_KEY,decreptMob);
        }
        if(!StringUtils.isEmpty(idCard)){
            String decreptId = SecurityTool.iosdecrypt(idCard.replaceAll(" ", "+"));
            map.put(UserConstants.IDCARD_KEY,decreptId);
        }
        if(!StringUtils.isEmpty(append)){
            String decreptAdd = SecurityTool.iosdecrypt(append);
            map.put(UserConstants.ADD_KEY,decreptAdd);
        }
        return map;
    }

    /**
     * 使用彩票加密串加密用户登录密码,并设置加密串到bean对象中.
     * @param bean
     * @param plainPwd 登录密码原文
     * @return 加密后的密码密文
     * @throws Exception
     */
    public static String encryptPwd(BaseBean bean, String plainPwd) throws Exception {
        String privateKey = DEFAULT_MD5_KEY;
        bean.setPrivateKey(privateKey);
        return MD5Util.compute(plainPwd + privateKey);
    }
}
