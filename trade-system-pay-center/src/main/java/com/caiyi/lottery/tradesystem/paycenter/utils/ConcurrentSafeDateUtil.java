package com.caiyi.lottery.tradesystem.paycenter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 并发安全的日期处理类,每个线程每种格式的SimpleDateFormat只会创建一次,且线程安全.
 */
public class ConcurrentSafeDateUtil {
	/** 锁对象 */
	private static final Object lockObj = new Object();
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	/**
	 * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf.
	 */
	private static SimpleDateFormat getSdf(final String pattern) {
		ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
		// 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
		if (tl == null) {
			synchronized (lockObj) {
				tl = sdfMap.get(pattern);
				if (tl == null) {
					// 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
					// 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new
					// SimpleDateFormat
					tl = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected SimpleDateFormat initialValue() {
							return new SimpleDateFormat(pattern);
						}
					};
					sdfMap.put(pattern, tl);
				}
			}
		}
		return tl.get();
	}

	/**
	 * 根据指定<code>pattern</code>格式化日期对象为日期字符串.
	 * @param time 被格式化日期对象
	 * @param pattern 日期格式
	 * @return 格式化后的日期字符串
	 */
	public static String format(Date time, String pattern) {
		return getSdf(pattern).format(time);
	}
	
	/**
	 * 根据指定<code>pattern</code>解析日期字符串生成日期对象
	 * @param time 被解析的日期字符串
	 * @param pattern 日期格式
	 * @return 解析得来的日期对象
	 * @throws ParseException
	 */
	public static Date parse(String time, String pattern) throws ParseException {
		return getSdf(pattern).parse(time);
	}
	
	/**
	 * 把<code>fromPattern</code>格式的日期字符串转换成<code>toPattern</code>格式的日期字符串
	 * @param time 原始日期字符串
	 * @param fromPattern 原始日期字符串格式
	 * @param toPattern 目标日期字符串格式
	 * @return 转换后的日期字符串
	 * @throws ParseException
	 */
	public static String convert(String time, String fromPattern, String toPattern) throws ParseException {
		return getSdf(toPattern).format(getSdf(fromPattern).parse(time));
	}
	
	/**
	 * 把日期精度转换为<code>pattern</code>格式的日期
	 * @param time 原始日期对象
	 * @param pattern 目标精度日期格式
	 * @return 精度被调整后的日期对象
	 * @throws ParseException
	 */
	public static Date convert(Date time, String pattern) throws ParseException {
		return getSdf(pattern).parse(getSdf(pattern).format(time));
	}
	
	/**
	  * 将时间转换成今天、明天、后天或13日这样的日期
	  * @param time
	  * @return
	 * @throws ParseException 
	  */
	public static String convertTimeToChinese(String time) throws ParseException {
		String day = convert(time, "yyyy-MM-dd HH:mm:ss", "dd") + "日";
		Calendar today = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		today.setTime(convert(new Date(), "yyyy-MM-dd"));
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		target.setTime(parse(time, "yyyy-MM-dd"));
		target.set(Calendar.HOUR, 0);
		target.set(Calendar.MINUTE, 0);
		target.set(Calendar.SECOND, 0);

		long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
		int xcts = (int) (intervalMilli / 86400000);
		if (xcts >= 0 && xcts <= 2) {
			day = xcts == 0 ? "今天" : xcts == 1 ? "明天" : "后天";
		}
		return day;

	}
}
