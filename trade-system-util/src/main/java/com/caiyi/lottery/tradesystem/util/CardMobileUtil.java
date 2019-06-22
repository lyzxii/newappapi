package com.caiyi.lottery.tradesystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 銀行卡手机号加解密工具
 * @author A-0205
 *
 */
public class CardMobileUtil {
	private static Logger logger = LoggerFactory.getLogger(CardMobileUtil.class);
	/**
	 * 解密银行卡号
	 * @param bean
	 * @param pool
	 */
	public static String decryptCard(String bankCard) {
		String decrypt = "";
		try {
			String regex = "[0-9]*";
			if(!StringUtil.isEmpty(bankCard) && !bankCard.matches(regex) && !"(null)".equals(bankCard)){
				String cardno = bankCard.contains("%2B") ? bankCard.replaceAll("%2B", "+") : bankCard;
				decrypt = SecurityTool.iosdecrypt(bankCard);
				logger.info("解密银行卡号：cardno密文："+cardno+"  明文："+decrypt);
				return decrypt;
			}
		} catch (Exception e) {
			logger.error("解密银行卡出错,银行卡号:"+bankCard,e);
			return "";
		}
		return decrypt; 
	}
	
	/**
	 * 解密银行卡号
	 * @param bean
	 * @param pool
	 */
	public static String decryptMobile(String mobile) {
		String decrypt = "";
		try {
			String regex = "[0-9]*";
			if(!StringUtil.isEmpty(mobile) && !mobile.matches(regex) && !"(null)".equals(mobile)){
				String mobileNo = mobile.contains("%2B") ? mobile.replaceAll("%2B", "+") : mobile;
				decrypt = SecurityTool.iosdecrypt(mobile);
				logger.info("解密银行卡号：手机号密文："+mobileNo+"  明文："+decrypt);
				return decrypt;
			}
		} catch (Exception e) {
			logger.error("解密银行卡出错,银行卡号:"+mobile,e);
			return "";
		}
		return decrypt; 
	}
	
    //检测银行卡号是否合法
    public static boolean checkBankCard(String bankCard){
    	String regex = "[0-9]*";
    	if(!bankCard.matches(regex)){
    		return false;
    	}
    	Luhn luhn = new Luhn(bankCard);
    	if(luhn.check()){
    		return true;
    	}else{
    		return false;
    	}
    }
}
