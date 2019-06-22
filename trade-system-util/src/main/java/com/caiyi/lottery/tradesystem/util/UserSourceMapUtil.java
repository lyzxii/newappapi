package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.BaseBean;

public class UserSourceMapUtil {
	/**
	 * 判断source值是否在指定的范围内,大于或等于min,小于或等于max
	 * @param source 目标source值
	 * @param min 最小值
	 * @param max 最大值
	 * @return
	 */
	public static boolean isSourceInRange(int source, int min, int max) {
		return source >= min && source <= max;
	}
	
	/**
	 * 根据source判断是否是主站彩票用户
	 */
	public static boolean isWebsiteLotteryUser(BaseBean bean) {
		return bean.getMtype()<=0 && bean.getSource() == 0;
	}
	
	/**
	 * 根据source判断是否是安卓彩票用户
	 */
	public static boolean isAndriodLotteryUser(BaseBean bean) {
		return bean.getMtype() == 1 || isSourceInRange(bean.getSource(), 1000, 1999);
	}
	
	/**
	 * 根据source判断是否是iOS彩票用户
	 */
	public static boolean isIOSLotteryUser(BaseBean bean) {
		return bean.getMtype() == 2 || isSourceInRange(bean.getSource(), 2000, 2999);
	}
	
	/**
	 * 根据source判断是否是触屏彩票用户
	 */
	public static boolean isTouchUser(BaseBean bean) {
		return bean.getMtype() == 4 || isSourceInRange(bean.getSource(), 3000, 3999);
	}
	
	/**
	 * 根据source判断是否是触屏彩票用户
	 */
	public static boolean isWPUser(BaseBean bean) {
		return bean.getMtype() == 3 || isSourceInRange(bean.getSource(), 4000, 4999);
	}
	
	/**
	 * 根据source判断是否是彩票用户
	 */
	public static boolean isLotteryUser(int source) {
		return isSourceInRange(source, 0, 4999);
	}
}
