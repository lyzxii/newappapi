package com.caiyi.lottery.tradesystem.usercenter.util;

import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;

import bean.UserBean;

public class UserBeanCheck {
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

	 
	public final static int QUERY_GOUCAI  = 10;//购彩记录
	public final static int QUERY_ZHUIHAO = 11;//追号记录
	public final static int QUERY_AUTOBUY = 12;//定制跟单
	public final static int QUERY_ACCOUNT = 13;//账户明细
	public final static int QUERY_PAY  = 14;//充值记录
	public final static int QUERY_CASH = 15;//提现记录
	public final static int QUERY_KTKMONEY = 23;//查询可提款金额
	public final static int QUERY_WZFGOUCAI  = 33;//购彩记录
	public final static int QUERY_WZFZHUIHAO = 34;//追号记录
	
	public final static int QUERY_ZHUIHAO_DETAIL = 44;//追号记录明细
	
	public final static int INFO_DIND = 3000;
	public final static int INFO_DINDYZ=3001;
	public final static int INFO_RGISTER = 2000;
	public final static int INFO_LOGIN = 1000;
	
	public final static String ENCRYPT_KEY = "A9FK25RHT487ULMI";
	
	public static void check(int flagnum, UserBean bean) {
		bean.setBusiErrCode(0);

		if (CheckUtil.isNullString(bean.getComeFrom())) {
			bean.setComeFrom("main");
		}

		switch (flagnum) {
			case UPDATE_BANK: {// 银行信息
				if (CheckUtil.isNullString(bean.getPwd())) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_LOGIN_PASSWORD_ERROR));
					bean.setBusiErrDesc("密码不能为空");
				}
				if (CheckUtil.isNullString(bean.getDrawBankCode())) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_DRAWCARD_CODE_ERROR));
					bean.setBusiErrDesc("提款银行卡代码不能为空");
				}
				if (CheckUtil.isNullString(bean.getProvid())) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_LOCATION_ERROR));
					bean.setBusiErrDesc("银行省份不能为空");
				}
				if (CheckUtil.isNullString(bean.getCityid())) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_LOCATION_ERROR));
					bean.setBusiErrDesc("银行城市不能为空");
				}
				if (CheckUtil.isNullString(bean.getBankCard())) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_ERROR));
					bean.setBusiErrDesc("银行卡号不能为空");
				}
	
				if (!bean.getBankCard().trim().matches("\\d+")) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_ERROR));
					bean.setBusiErrDesc("银行卡号非法");
				}
				break;
			}
			default: {
				bean.setBusiErrCode(1000);
				bean.setBusiErrDesc("未知的检查类型");
				break;
			}
		}
		return;
	}
}
