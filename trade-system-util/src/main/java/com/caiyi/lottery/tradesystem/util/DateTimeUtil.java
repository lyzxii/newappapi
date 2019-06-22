package com.caiyi.lottery.tradesystem.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateTimeUtil {
	/** 年-月-日 */
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	/** 年-月-日 时:分:秒 */
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** 月-日 */
	public static final String DATE_MD_FORMAT = "MM-dd";

	/** 月-日 时:分 */
	public static final String DATE_MDHM_FORMAT = "MM-dd HH:mm";

	/** 时:分 */
	public static final String DATE_HM_FORMAT = "HH:mm";

	public static Timestamp[] todayPeriod() {
		Timestamp[] timeMap = new Timestamp[2];
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		Timestamp startTime = new Timestamp(now.getTimeInMillis());
		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 999);
		Timestamp endTime = new Timestamp(now.getTimeInMillis());
		timeMap[0] = startTime;
		timeMap[1] = endTime;
		return timeMap;
	}

	public static Timestamp[] yesterdayPeriod() {
		Timestamp[] timeMap = new Timestamp[2];
		Calendar now = Calendar.getInstance();
		now.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) - 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		Timestamp startTime = new Timestamp(now.getTimeInMillis());
		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		now.set(Calendar.MILLISECOND, 999);
		Timestamp endTime = new Timestamp(now.getTimeInMillis());
		timeMap[0] = startTime;
		timeMap[1] = endTime;
		return timeMap;
	}

	/**
	 * 将日期格式化为指定格式的日期字符串
	 * 
	 * @author sjq
	 * @param date
	 *            日期
	 * @param format
	 *            格式化模型,如："yyyy-MM-dd HH:mm:ss"
	 * @return 格式化后的日期字符串
	 */
	public static String formatDate(Date date, String format) {
		if (date == null) {
			return "";
		}
		DateFormat df = new SimpleDateFormat(format);
		String dateString = df.format(date);
		return dateString;
	}

	/**
	 * 将日期字符串转换成指定格式的日期
	 * 
	 * @author sjq
	 * @param dateString
	 *            日期字符串
	 * @param format
	 *            格式化模型,如："yyyy-MM-dd HH:mm:ss"
	 * @return date 指定格式的日期
	 */
	public static Date parseDate(String dateString, String format) {
		Date date = null;
		try {
			DateFormat df = new SimpleDateFormat(format);
			date = df.parse(dateString);
		} catch (ParseException e) {
			log.error("解析时间转换格式,dateString:"+ dateString+" format:"+format);
		}
		return date;
	}

	/**
	 * 获取两个日期间相差的天数
	 * 
	 * @author sjq
	 * @param calendar1
	 *            日期1
	 * @param calendar2
	 *            日期2
	 * @return 两个日期间相差的天数
	 */
	public static int getDaysBetween(Calendar calendar1, Calendar calendar2) {
		if (calendar1.after(calendar2)) {
			Calendar calendar = calendar1;
			calendar1 = calendar2;
			calendar2 = calendar;
		}
		int days = calendar2.get(Calendar.DAY_OF_YEAR) - calendar1.get(Calendar.DAY_OF_YEAR);
		int y2 = calendar2.get(Calendar.YEAR);
		if (calendar1.get(Calendar.YEAR) != y2) {
			calendar1 = (Calendar) calendar1.clone();
			do {
				days += calendar1.getActualMaximum(6);
				calendar1.add(1, 1);
			} while (calendar1.get(1) != y2);
		}
		return days;
	}

	public static boolean checkDate(String date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date d = null;
		boolean flag = true;
		try {
			d = df.parse(date);
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static String getDateTime(long paramLong, String paramString) {
		try {
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString);
			return localSimpleDateFormat.format(new Date(paramLong));
		} catch (Exception localException) {
		}
		return "";
	}
	/**
	 * 获取前N天日期
	 */
	public static String getBeforeXDayTime(String time,long miltime){
		String format = "yyyy-MM-dd";
		String result = "";
		try {
			Date now = new SimpleDateFormat(format).parse(time);
			Date now_10 = new Date(now.getTime() - miltime*24*60*60*1000);
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			result = dateFormat.format(now_10);
		} catch (ParseException e) {
			log.error("getBeforeXDayTime Exception,time:"+time+" miltime:"+miltime);
		}
		return result;
	}


	/**
	 * 计算时间差
	 * @param jzTime
	 * @param curTime
	 * @return
	 */
	public static int getDateInterval(String jzTime, String curTime) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		int days = 0 ;
		try {
			Date dt1 = df.parse(jzTime);
			Date dt2 = df.parse(curTime);
			days= (int)(dt1.getTime() - dt2.getTime())/(1000 * 60 * 60 * 24);
		} catch (Exception exception) {
			log.error("getDateInterval Exception,jzTime:"+jzTime+" curTime:"+curTime);
		}
		int ceil = (int)Math.ceil(days);
		return ceil;
	}

	/**
	 * 判断当前日期是否为星期一
	 */
	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if(c.get(Calendar.DAY_OF_WEEK) == 1){
			dayForWeek = 7;
		}else{
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	/**
	 * 获取上周指定时间(0-周一，……，6-周日)
	 */
	public static String getDayOfLastWeek(int num) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, -1);
		cal.add(Calendar.DATE, -1 * cal.get(Calendar.DAY_OF_WEEK) + 2 + num);
		return sf.format(cal.getTime());
	}
	
	/**
	 * 获取X分钟前时间
	 */
    public static String getBeforeXminTime(String time,long miltime){
		String format = "yyyy-MM-dd HH:mm:ss";
		String result = "";
		try {
			Date now = new SimpleDateFormat(format).parse(time);
			Date now_10 = new Date(now.getTime() - miltime); //10分钟前的时间
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			result = dateFormat.format(now_10);
		} catch (ParseException e) {
			log.error("getBeforeXminTime Exception,time:"+time+" miltime:"+miltime);
		}
		return result;
	}
	public static Date getBeforeXminTimeToDate(Date time,long miltime){
	//	String format = "yyyy-MM-dd HH:mm:ss";
		Date result = null;
		try {
			//Date now = new SimpleDateFormat(format).parse(time);
			result = new Date(time.getTime() - miltime*60*1000); //10分钟前的时间
		} catch (Exception e) {
			log.error("getBeforeXminTimeToDate Exception,time:"+time+" miltime:"+miltime);
		}
		return result;
	}

	/**
	 * 判断是否为当日
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		int year1 = c1.get(Calendar.YEAR);
		int month1 = c1.get(Calendar.MONTH)+1;
		int day1 = c1.get(Calendar.DAY_OF_MONTH);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		int year2 = c2.get(Calendar.YEAR);
		int month2 = c2.get(Calendar.MONTH)+1;
		int day2 = c2.get(Calendar.DAY_OF_MONTH);
		if(year1 == year2 && month1 == month2 && day1 == day2){
			return true;
		}
		return false;
	}

	public static String getCurrentFormatDate(String var0) {
		try {
			SimpleDateFormat var1 = new SimpleDateFormat(var0);
			return var1.format(new Date());
		} catch (Exception var2) {
			return "";
		}
	}
	public static Date parserDateTime(String var0) {
		Date var1 = null;

		try {
			SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			var1 = var2.parse(var0);
		} catch (Exception var3) {
			log.error("getBeforeXminTimeToDate Exception,var0:"+var0);
			var1 = null;
		}

		return var1;
	}

	public static String getCurrentDate() {
		return getCurrentFormatDate("yyyy-MM-dd");
	}

	public static String getCurrentDateTime() {
		return getCurrentFormatDate("yyyy-MM-dd HH:mm:ss");
	}
}