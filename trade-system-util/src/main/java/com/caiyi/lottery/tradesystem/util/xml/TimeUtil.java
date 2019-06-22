package com.caiyi.lottery.tradesystem.util.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class TimeUtil {
	public static final SimpleDateFormat DateFormater = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat TimeFormater = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat DateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Hashtable<String, SimpleDateFormat> DataFormatTab = new Hashtable<>();
	private static final String[] WeekArr = { "日", "一", "二", "三", "四", "五", "六" };
	private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

	public TimeUtil() {
	}

	public static String currentDate() {
		return DateFormater.format(new Date());
	}

	public static String currentTime() {
		return TimeFormater.format(new Date());
	}

	public static String currentDateTime() {
		return DateTimeFormater.format(new Date());
	}

	public static String currentDateTime(String paramString)
	  {
	    try
	    {
	      synchronized (DataFormatTab)
	      {
	        if (!DataFormatTab.containsKey(paramString)) {
	          DataFormatTab.put(paramString, new SimpleDateFormat(paramString));
	        }
	      }
	      SimpleDateFormat sdf = DataFormatTab.get(paramString);
	      return sdf.format(new Date());
	    }
	    catch (Exception localException)
	    {
	      throw new RuntimeException("时间格式(" + paramString + ")错误", localException);
	    }
	  }

	public static String weekOfDay() {
		return weekOfDay(Calendar.getInstance());
	}

	public static String weekOfDay(Calendar paramCalendar) {
		return WeekArr[(paramCalendar.get(7) - 1)];
	}

	public static String customDateTime(Date paramDate) {
		return DateTimeFormater.format(paramDate);
	}

	public static String customDateTime(Date paramDate, String paramString)
	  {
	    try
	    {
	      synchronized (DataFormatTab)
	      {
	        if (!DataFormatTab.containsKey(paramString)) {
	          DataFormatTab.put(paramString, new SimpleDateFormat(paramString));
	        }
	      }
	      SimpleDateFormat sdf = DataFormatTab.get(paramString);
	      return sdf.format(paramDate);
	    }
	    catch (Exception localException)
	    {
	      throw new RuntimeException("日期时间格式(" + paramString + ")错误", localException);
	    }
	  }

	public static String customDateTime(Calendar paramCalendar, String paramString)
	  {
	    try
	    {
	      synchronized (DataFormatTab)
	      {
	        if (!DataFormatTab.containsKey(paramString)) {
	          DataFormatTab.put(paramString, new SimpleDateFormat(paramString));
	        }
	      }
	      SimpleDateFormat sdf = DataFormatTab.get(paramString);
	      return sdf.format(paramCalendar);
	    }
	    catch (Exception localException)
	    {
	      throw new RuntimeException("日期时间格式(" + paramString + ")错误", localException);
	    }
	  }

	public static Date parserDateTime(String paramString) {
		try {
			return DateTimeFormater.parse(paramString);
		} catch (ParseException localParseException) {
			throw new RuntimeException("解析时间错误", localParseException);
		}
	}

	public static Date parserDateTime(String paramString1, String paramString2)
	  {
	    try
	    {
	      synchronized (DataFormatTab)
	      {
	        if (!DataFormatTab.containsKey(paramString2)) {
	          DataFormatTab.put(paramString2, new SimpleDateFormat(paramString2));
	        }
	      }
	      SimpleDateFormat sdf = DataFormatTab.get(paramString2);
	      return sdf.parse(paramString1);
	    }
	    catch (ParseException localParseException)
	    {
	      throw new RuntimeException("解析时间错误", localParseException);
	    }
	  }

	public static String convert(String paramString1, String paramString2) {
		return customDateTime(parserDateTime(paramString1), paramString2);
	}

	public static String convert(String paramString1, String paramString2, String paramString3) {
		return customDateTime(parserDateTime(paramString1, paramString2), paramString3);
	}

	public static long timeDiff(String paramString) {
		return parserDateTime(paramString).getTime() - new Date().getTime();
	}

	public static long timeDiff(String paramString1, String paramString2) {
		return parserDateTime(paramString1, paramString2).getTime() - new Date().getTime();
	}

	public static long timeDiff(String paramString1, String paramString2, String paramString3) {
		return parserDateTime(paramString1, paramString3).getTime()
				- parserDateTime(paramString2, paramString3).getTime();
	}

	public static long timeDiff(String paramString1, String paramString2, String paramString3, String paramString4) {
		return parserDateTime(paramString1, paramString2).getTime()
				- parserDateTime(paramString3, paramString4).getTime();
	}

	static void threadLocal() {
		threadLocal.set(Long.valueOf(System.currentTimeMillis()));
	}

	public static long updateTimer() {
		Long localLong = (Long) threadLocal.get();
		threadLocal.set(Long.valueOf(System.currentTimeMillis()));
		if (localLong == null) {
			return -1L;
		}
		return System.currentTimeMillis() - localLong.longValue();
	}

	public static long fetchTimer() {
		return fetchTimer(true);
	}

	public static long fetchTimer(boolean paramBoolean) {
		Long localLong = (Long) threadLocal.get();
		if (localLong != null) {
			if (paramBoolean) {
				threadLocal.remove();
			}
			return System.currentTimeMillis() - localLong.longValue();
		}
		return -1L;
	}

	public static void main(String[] paramArrayOfString) {
		System.out.println(System.currentTimeMillis());
		System.out.println(new Date().getTime());
	}
}
