package com.caiyi.lottery.tradesystem.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CheckUtil {
	private static final int[] weight = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
	private static final int[] checkDigit = new int[] { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };
	
	public final static boolean isNullString(String s) {
		if ( s != null && s.length() > 0 && s.trim().length() > 0) {
			return false ;
		} else {
			return true ;
		}
	}
	
	/**
	 * 生成6位随机码
	 * @author xhs
	 * @return
	 */
	public final static String randomNum() {
//		char[] codeSequence = {'0', '1', '2', '3', '4', '5', '6',
//				'7', '8', '9','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
//				'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U',
//				'V', 'W', 'X', 'Y', 'Z'};
		char[] codeSequence = {'0', '1', '2', '3', '4', '5', '6',
				'7', '8', '9'};
		Random random = new Random();
		String strRand = "";
		StringBuffer randomCode = new StringBuffer("");
		for (int i = 0; i < 6; i++) {
			strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length-1)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
	
	/**
	 * 邮箱地址验证
	 * @author xhs
	 * @return boolean
	 */
	public final static boolean isEmail(String email) {
		String str = "^([a-z0-9A-Z]+[-_|.]*)+[a-z0-9A-Z]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	/**
	 * 手机号码验证
	 * @author xhs
	 * @return boolean
	 */
	public final static boolean isMobilephone(String mobiles){
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(166)|(17[0-9])|(18[0-9])|(19[8-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 用户名格式验证
	 * @author xhs
	 * @return boolean
	 */
	public final static boolean CheckUserName(String s){
		String str = "[A-Za-z0-9_\u4e00-\u9fa5]*";
		Pattern pattern = Pattern.compile(str);
		Matcher matcher = pattern.matcher(s);
		if(!matcher.matches()){
			return false;
		}
		
		str = "习近平|李克强|法轮功";
		pattern = Pattern.compile(str);
		matcher = pattern.matcher(s);
		
		if(matcher.find()){
			return false;
		}
		return true;
	}
	
	/**
	 * 用户名格式验证
	 * @author xhs
	 * @return boolean
	 */
	public final static boolean CheckRealName(String s){
		String str = "[\u4e00-\u9fa5·•●.]+";
		Pattern pattern = Pattern.compile(str);
		Matcher matcher = pattern.matcher(s);
		if(!matcher.matches()){
			return false;
		}
		
		str = "习近平|李克强|法轮功";
		pattern = Pattern.compile(str);
		matcher = pattern.matcher(s);
		
		if(matcher.find()){
			return false;
		}
		return true;
	}
	
	/**
	 * 过滤用户名中的特殊字符
	 * @author xhs
	 * @return boolean
	 */
	public final static String FilterUserName(String s){
		String pattern = "[^A-Za-z0-9_|^\u4e00-\u9fa5]*";
		String filterName = s.replaceAll(pattern, "");
		return filterName;
	}
	
	/**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     * 
     * @param value 指定的字符串
     * @return 字符串的长度
     */
	public static int length(String value) {
		int valueLength = 0;
	    String chinese = "[\u0391-\uFFE5]";
	    /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
	    for (int i = 0; i < value.length(); i++) {
	        /* 获取一个字符 */
	        String temp = value.substring(i, i + 1);
	        /* 判断是否为中文字符 */
	        if (temp.matches(chinese)) {
	        	/* 中文字符长度为2 */
	            valueLength += 2;
	        } else {
	            /* 其他字符长度为1 */
	            valueLength += 1;
	        }
	    }
	    return valueLength;
	}
	
	public static String checkNum(String content){
		String[] numArr = new String[]{"0","1","2","3","4","5","6","7","8","9","零","一","二","三","四","五","六","七","八","九"};
		List<String> numList = new ArrayList<String>(Arrays.asList(numArr));
		int count = 0;
		for(int i = 0;i<content.length();i++){
			String index = content.substring(i, i+1);
			if(numList.contains(index)){
				count++;
			}
		}
		if(count>=5){
			content = content.substring(0,4)+"***";
		}
		String reg = "[a-zA-Z0-9]{5}";
		Pattern pattern = Pattern.compile(reg);
		Matcher match = pattern.matcher(content);
		if(match.find()){
			content = content.substring(0,4)+"***";
		}
		return content;
	}
	
	/**
	 * 验证身份证是否合法,用户是否已年满18周岁
	 */
	public static boolean checkIdcard(String idCard) {
		if (CheckUtil.isNullString(idCard)) {
			return false;
		}
		if (idCard.length() != 18) {
			return false;
		}
		// 获取输入身份证上的最后一位，它是校验码
		String checkDigit = idCard.substring(17, 18);
		// 比较获取的校验码与本方法生成的校验码是否相等
		if (!checkDigit.equals(getCheckDigit(idCard))) {
			return false;
		}
		int birthYear = Integer.parseInt(idCard.substring(6, 10));
		int birthMonth = Integer.parseInt(idCard.substring(10, 12));
		if(birthMonth>12){
			return false;
		}
		int birthDay = Integer.parseInt(idCard.substring(12, 14));
		if(birthDay>31){
			return false;
		}
		Calendar now = Calendar.getInstance();
		int nowYear = now.get(Calendar.YEAR);
		int nowMonth = now.get(Calendar.MONTH) + 1;
		int nowDay = now.get(Calendar.DAY_OF_MONTH);
		int age = nowYear - birthYear -1;
		if (birthMonth < nowMonth) {
			age += 1;
		}else if(birthMonth == nowMonth && birthDay <= nowDay){
			age += 1;
		}
		if (age < 18) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 计算18位身份证的校验码
	 */
	protected static String getCheckDigit(String eighteenCardID) {
		int remaining = 0;
		if (eighteenCardID.length() == 18) {
			eighteenCardID = eighteenCardID.substring(0, 17);
		}

		if (eighteenCardID.length() == 17) {
			int sum = 0;
			int[] a = new int[17];
			// 先对前17位数字的权求和
			for (int i = 0; i < 17; i++) {
				String k = eighteenCardID.substring(i, i + 1);
				a[i] = Integer.parseInt(k);
			}
			for (int i = 0; i < 17; i++) {
				sum = sum + weight[i] * a[i];
			}
			// 再与11取模
			remaining = sum % 11;
			a = null;
		}
		return remaining == 2 ? "X" : String.valueOf(checkDigit[remaining]);
	}
	
	public static void main(String[] args){
		System.out.println(CheckUserName("我的用户名|asdas"));
		System.out.println(isEmail("3-_.d.-7.7110p@1.com"));
		System.out.println(checkNum("a11汉11字aa"));
		System.out.println(!CheckRealName("好好地]"));
	}
	
}