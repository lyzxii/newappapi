package trade.util;

import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateConvertUtil {
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
	 * 每个线程只会有一个SimpleDateFormat.
	 */
	public static String format(Date date, String pattern) {
		return getSdf(pattern).format(date);
	}
	
	public static Date format(String date, String pattern) throws ParseException {
		if (StringUtil.isEmpty(date)) {
			return null;
		}
		return getSdf(pattern).parse(date);
	}
	
	public static String format(String date, String inPattern, String outPattern) throws ParseException {
		return getSdf(outPattern).format(getSdf(inPattern).parse(date));
	}
	
	/**
	  * 将日期信息转换成今天、明天、后天、星期
	  * @param date
	  * @return
	 * @throws ParseException
	  */
	public static String getDateDetail(String date) throws ParseException {
		String day = format((Date) Timestamp.valueOf(date), "dd") + "日";
		Calendar today = Calendar.getInstance();
		Calendar target = Calendar.getInstance();
		today.setTime(format(DateUtil.getCurrentDate(), "yyyy-MM-dd"));
		today.set(Calendar.HOUR, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		target.setTime(format(date, "yyyy-MM-dd"));
		target.set(Calendar.HOUR, 0);
		target.set(Calendar.MINUTE, 0);
		target.set(Calendar.SECOND, 0);

		long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
		int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
		if (xcts >= 0 && xcts <= 2) {
			day = xcts == 0 ? "今天" : xcts == 1 ? "明天" : "后天";
		}
		return day;

	}
}
