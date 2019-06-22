package util;

import bean.UserBean;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.util.StringUtil;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtil {
	
	public final static int IDCARD_ERROR = -102;//身份证信息错误
	private static final int[] weight = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
	// 校验码
	private static final int[] checkDigit = new int[] { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };

	public final static int UPDATE_BASE = 1;//修改基本信息
	public final static int UPDATE_PASS = 2;//修改密码
	public final static int UPDATE_SAFE = 3;//设置及修改密保问题
	public final static int UPDATE_BANK = 4;//设置及修改银行卡信息
	public final static int UPDATE_MOBIL = 5;
	public final static int UPDATE_EMAIL = 6;
	public final static int UPDATE_NAME = 7;//用户实名
	public final static int UPDATE_AUTOBUY = 8;//修改自动跟单状态
	public final static int UPDATE_JOIN = 16;//参与活动
	public final static int UPDATE_NOJOIN = 17;//不参与

	public final static int INFO_DIND = 3000;
	public final static int INFO_DINDYZ=3001;
	public final static int INFO_RGISTER = 2000;
	public final static int INFO_LOGIN = 1000;

	/**
	 * 检查注册用户名是否符合要求
	 * @author s
	 * @return
	 */
	public static boolean checkUserStr(String s){
		String str = "[A-Za-z0-9_|\u4e00-\u9fa5]*";
		Pattern pattern = Pattern.compile(str);
		Matcher matcher = pattern.matcher(s);
		if(!matcher.matches()){
			return false;
		}
		
		str = "9188|习近平|李克强|法轮功";
		pattern = Pattern.compile(str);
		matcher = pattern.matcher(s);
		
		if(matcher.find()){
			return false;
		}
		return true;
	}
	
	/**
	 * 验证身份证是否符合格式
	 * 
	 * @param idcard
	 * @return
	 */
	public static boolean verifyIDCard(String idcard) {
		if (idcard.length() == 15) {
			//15 位的身份证不能带X等字母
			Pattern pattern = Pattern.compile("[0-9]{1,}");
			if (pattern.matcher(idcard).matches()) {
				idcard = update2eighteen(idcard);
			} else {
				return false;
			}
		}
		if (idcard.length() != 18) {
			return false;
		}
		// 获取输入身份证上的最后一位，它是校验码
		String checkDigit = idcard.substring(17, 18);
		// 比较获取的校验码与本方法生成的校验码是否相等
		if (checkDigit.equals(getCheckDigit(idcard))) {
			return true;
		}
		return false;
	}
	
	/**
	 * 计算18位身份证的校验码
	 * 
	 * @param eighteenCardID
	 *            18位身份证
	 * @return
	 */
	private static String getCheckDigit(String eighteenCardID) {
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

	/**
	 * 将15位身份证升级成18位身份证号码
	 * 
	 * @param fifteenCardID
	 * @return
	 */
	private static String update2eighteen(String fifteenCardID) {
		// 15位身份证上的生日中的年份没有19，要加上
		String eighteenCardID = fifteenCardID.substring(0, 6) + "19" + fifteenCardID.substring(6, 15);
		eighteenCardID = eighteenCardID + getCheckDigit(eighteenCardID);
		return eighteenCardID;
	}
	
	
	/**
	    * 获取字符串的长度，如果有中文，则每个中文字符计为2位
	    * 
	    * @param value 指定的字符串
	    * @return 字符串的长度
	    */
	 public static int length(String value) {
//	       int valueLength = 0;
//	       String chinese = "[\u0391-\uFFE5]";
//	       /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
//	       for (int i = 0; i < value.length(); i++) {
//	           /* 获取一个字符 */
//	           String temp = value.substring(i, i + 1);
//	           /* 判断是否为中文字符 */
//	           if (temp.matches(chinese)) {
//	               /* 中文字符长度为2 */
//	               valueLength += 2;
//	           } else {
//	               /* 其他字符长度为1 */
//	               valueLength += 1;
//	           }
//	       }
//	       return valueLength;
		   return value.length();
	   }
	 
	 /**
	  * 验证身份证是否合法,用户是否已年满18周岁
	  */
	 public static void isLeaglIdcard(UserBean bean) {
		 if (CheckUtil.isNullString(bean.getIdCardNo())) {
			 bean.setBusiErrCode(IDCARD_ERROR);
			 bean.setBusiErrDesc("身份证号不能为空");
		 	return;
		 }
		 bean.setIdCardNo(bean.getIdCardNo().toUpperCase());
		 String idcard = bean.getIdCardNo();
		 if (idcard.length() != 18) {
			 bean.setBusiErrCode(IDCARD_ERROR);
			 bean.setBusiErrDesc("身份证号格式错误");
			 return;
		 }
		 // 获取输入身份证上的最后一位，它是校验码
		 String checkDigit = idcard.substring(17, 18);
		 // 比较获取的校验码与本方法生成的校验码是否相等
		 if (!checkDigit.equals(getCheckDigit(idcard))) {
			 bean.setBusiErrCode(IDCARD_ERROR);
			 bean.setBusiErrDesc("身份证号不合法");
			 return;
		 }
		 int birthYear = Integer.parseInt(idcard.substring(6, 10));
		 int birthMonth = Integer.parseInt(idcard.substring(10, 12));
		 int birthDay = Integer.parseInt(idcard.substring(12, 14));
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
		 //黑名单身份证
		 String hcidcard="330724199807195416,320502199407032510,350181199804121655,522124199312254431,653221198004210738";
		 if(hcidcard.indexOf(bean.getIdCardNo())!=-1){
			 bean.setBusiErrDesc("您的身份信息已实名过，不能重复实名");
			 return;
		 }
		 if (age < 18) {
			 bean.setBusiErrCode(IDCARD_ERROR);
			 bean.setBusiErrDesc("您未满18周岁,本站不向未成年人出售彩票");
		 } else {
			 bean.setBusiErrCode(0);
		 }
	 }	 /**
	  * 验证身份证是否合法,用户是否已年满18周岁,返回Result
	  */
	 public static Result isLeaglIdcardRes(UserBean bean, Result result) {
		 if (CheckUtil.isNullString(bean.getIdCardNo())) {
			 result.setCode(String.valueOf(IDCARD_ERROR));
			 result.setDesc("身份证号不能为空");
		 	return result;
		 }
		 bean.setIdCardNo(bean.getIdCardNo().toUpperCase());
		 String idcard = bean.getIdCardNo();
		 if (idcard.length() != 18) {
			 result.setCode(String.valueOf(IDCARD_ERROR));
			 result.setDesc("身份证号格式错误");
			 return result;
		 }
		 // 获取输入身份证上的最后一位，它是校验码
		 String checkDigit = idcard.substring(17, 18);
		 // 比较获取的校验码与本方法生成的校验码是否相等
		 if (!checkDigit.equals(getCheckDigit(idcard))) {
			 result.setCode(String.valueOf(IDCARD_ERROR));
			 result.setDesc("身份证号不合法");
			 return result;
		 }
		 int birthYear = Integer.parseInt(idcard.substring(6, 10));
		 int birthMonth = Integer.parseInt(idcard.substring(10, 12));
		 int birthDay = Integer.parseInt(idcard.substring(12, 14));
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
			 result.setCode(String.valueOf(IDCARD_ERROR));
			 result.setDesc("您未满18周岁,本站不向未成年人出售彩票");
			 return result;
		 } else {
			 result.setCode("0");
			 return result;
		 }
	 }


	public static int check(UserBean userBean,int flagnum) {
		int ret = 0;
		userBean.setBusiErrCode(0);

		if(GameContains.canNotUse(userBean.getGid())){
			userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
			userBean.setBusiErrDesc("不支持的彩种");
		}

		if ( CheckUtil.isNullString(userBean.getComeFrom()) ) {
			userBean.setComeFrom("main");
		}

		switch ( flagnum ) {
			case UPDATE_JOIN: {//参与活动
				break ;
			}
			case UPDATE_NOJOIN: {//取消参与活动
				break ;
			}
			case INFO_DIND: {//绑定发送
				if ( CheckUtil.isNullString(userBean.getUid()) ) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				if ( userBean.getFlag()!=0 &&  userBean.getFlag()!=1 ) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("不支持的绑定类型");
				}
				break ;
			}
			case INFO_DINDYZ: {//绑定验证
				if ( CheckUtil.isNullString(userBean.getUid()) ) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				if (  userBean.getFlag()!=0 &&  userBean.getFlag()!=1 ) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("不支持的绑定类型");
				}
				break ;
			}
			case INFO_RGISTER: {//注册
				if ( CheckUtil.isNullString(userBean.getUid()) ) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}else{
					//if (!UserUtil.checkUserStr(uid)) { // 检查用户名合法性
					//	userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					//	userBean.setBusiErrDesc("用户名不合法，可由中英文、数字、下划线组成");
					//}
					if (!CheckUtil.CheckUserName(userBean.getUid())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("用户名不合法，可由中英文、数字、下划线组成");
					}
					Pattern pattern = Pattern.compile("习近平|李克强|法轮功");
					Matcher matcher = pattern.matcher(userBean.getUid());
					while (matcher.find()) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("用户名不合法，用户名不能包含敏感词语");
					}
					pattern = Pattern.compile("QQ|qq|9188|微信");
					matcher = pattern.matcher(userBean.getUid());
					while (matcher.find()) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("用户名不合法，用户名不能包含QQ、qq、9188、微信等禁用词语");
					}
					pattern = Pattern.compile("\\d{7,}");
					matcher = pattern.matcher(userBean.getUid());
					while (matcher.find()) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("用户名不合法，用户名里面不能包含6个以上连续的数字");
					}
				}
//			if ( CheckUtil.isNullString(mailAddr) ) {
//				userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
//				userBean.setBusiErrDesc("电子邮件不能为空");
//			}
				if ( CheckUtil.isNullString(userBean.getPwd()) ) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("密码不能为空");
				}
				break ;
			}
			case UPDATE_BASE: {//基本信息
				if ( CheckUtil.isNullString(userBean.getProvid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("省份不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getCityid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("城市不能为空");
				}
				break ;
			}

			case UPDATE_SAFE: {//密保信息
				if (CheckUtil.isNullString(userBean.getTid())) {//设置密保
					if ( CheckUtil.isNullString(userBean.getRid())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("密保问题编号不能够为空");
					}
					if ( CheckUtil.isNullString(userBean.getAid())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("答案不能为空");
					}
				}else{//修改密保
					if ( CheckUtil.isNullString(userBean.getRid())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("新密保问题编号不能够为空");
					}
					if ( CheckUtil.isNullString(userBean.getAid())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("新答案不能为空");
					}
					if ( CheckUtil.isNullString(userBean.getNewValue())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("旧答案不能为空");
					}
				}
				break ;
			}
			case UPDATE_PASS: {//密码
				if ( CheckUtil.isNullString(userBean.getPwd())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("老密码不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getNewValue())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("新密码不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getUid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				break ;
			}
			case UPDATE_BANK: {//银行信息
				if ( CheckUtil.isNullString(userBean.getPwd())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("密码不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getBankCode())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("银行代码不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getProvid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("银行省份不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getCityid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("银行城市不能为空");
				}
				if (CheckUtil.isNullString(userBean.getTid())) {//设置银行卡信息
					if ( CheckUtil.isNullString(userBean.getBankCard())) {
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("银行卡号不能为空");
					}

					if(!userBean.getBankCard().trim().matches("\\d+")){
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("银行卡号非法");
					}
				}else{

				}
				break ;
			}
			case UPDATE_MOBIL: {
				if ( CheckUtil.isNullString(userBean.getMobileNo())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("老手机号码不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getNewValue())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("新手机不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getUid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				break ;
			}
			case UPDATE_EMAIL: {
				if ( CheckUtil.isNullString(userBean.getMailAddr())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("老电子邮件不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getNewValue())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("新电子邮件不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getUid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				break;
			}
			case UPDATE_NAME:{//实名
				if ( CheckUtil.isNullString(userBean.getRealName())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("真实姓名不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getIdCardNo())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("身份证号不能为空");
				}else{
					userBean.setIdCardNo(userBean.getIdCardNo().toUpperCase());
					if (!UserUtil.verifyIDCard(userBean.getIdCardNo())) { // 检查身份证合法性
						userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
						userBean.setBusiErrDesc("身份证输入有误");
					}
				}
				if ( CheckUtil.isNullString(userBean.getPwd())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("密码不能为空");
				}
				break;
			}
			case INFO_LOGIN: {
				if ( CheckUtil.isNullString(userBean.getUid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getPwd())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("密码不能为空");
				}
				break ;
			}
			case UPDATE_AUTOBUY: {
				if ( CheckUtil.isNullString(userBean.getUid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("用户名不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getGid())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("彩种不能为空");
				}
				if ( CheckUtil.isNullString(userBean.getOwner())) {
					userBean.setBusiErrCode(UserErrCode.ERR_CHECK);
					userBean.setBusiErrDesc("发起人不能为空");
				}
				break ;
			}
			default: {
				ret = 2000 ;
				userBean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHECK_UNKNOW));
				userBean.setBusiErrDesc("未知的检查类型");
				break ;
			}
		}
		return ret;
	}
	public static String verifyCaiyiNickid(String uid) {
		if (CheckUtil.isNullString(uid)) {
			return "用户名不能为空";
		}
		int length = uid.length();
		if (length < 4 || length > 16) {
			return "用户名长度必须为4-16个字符";
		}
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(uid);
		if (matcher.find()) {
			return "用户昵称不能包含空格";
		}
		if (!CheckUtil.CheckUserName(uid)) {
			return "用户名不合法，只可由中英文、数字、下划线组成";
		}
		pattern = Pattern.compile("QQ|qq|9188");
		matcher = pattern.matcher(uid);
		while (matcher.find()) {
			return "用户名不合法，用户名不能包含QQ、qq、9188等禁用词语";
		}
		pattern = Pattern.compile("\\d{7,}");
		matcher = pattern.matcher(uid);
		while (matcher.find()) {
			return "用户名不合法，用户名里面不能包含6个以上连续的数字";
		}
		return null;
	}

	public static String verifyLoginPwd(String pwd) {
		if (CheckUtil.isNullString(pwd)) {
			return "密码不能为空";
		}
//    	int length = pwd.length();
//    	if (length < 6 || length > 20) {
//    		return "密码长度为6-20个字符";
//    	}
		return null;
	}

	public static int verifyCaiyiNickidAndPwd(BaseBean bean) {
		int ret = 0;
		String errDesc = verifyCaiyiNickid(bean.getUid());
		if (StringUtil.isEmpty(errDesc)) {
			errDesc = verifyLoginPwd(bean.getPwd());
			if (StringUtil.isEmpty(errDesc)) {
				ret = 1;
			} else {
				bean.setBusiErrCode(UserErrCode.ERR_CHECK);
				bean.setBusiErrDesc(errDesc);
			}
		} else {
			bean.setBusiErrCode(UserErrCode.ERR_CHECK);
			bean.setBusiErrDesc(errDesc);
		}
		return ret;
	}
}
