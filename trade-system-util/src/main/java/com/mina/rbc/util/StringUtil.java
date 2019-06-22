package com.mina.rbc.util;

import org.springframework.util.StringUtils;

import java.util.Hashtable;

public class StringUtil {
	public static boolean isEmpty(String paramString) {
		return (paramString == null) || ("".equals(paramString)) || (paramString.trim().length() == 0);
	}

	public static String getStringNoNull(String paramString) {
		if (paramString == null) {
			return "";
		}
		return paramString;
	}

	public StringUtil() {
	}

	public static String getNullString(String paramString) {
		return paramString == null ? "" : paramString.trim();
	}

	public static String getNullString(String paramString1, String paramString2) {
		return paramString1 == null ? paramString2 : paramString1.trim();
	}

	public static String getNullString(Object paramObject) {
		return paramObject == null ? "" : paramObject.toString().trim();
	}

	public static int getNullInt(String paramString) {
		try {
			return getNullString(paramString).length() == 0 ? 0 : Integer.parseInt(paramString.trim());
		} catch (Exception localException) {
		}
		return 0;
	}

	public static int getNullInt(Object paramObject) {
		try {
			return paramObject == null ? 0 : Integer.parseInt(paramObject.toString().trim());
		} catch (Exception localException) {
		}
		return 0;
	}

	public static long getNullLong(String paramString) {
		try {
			return getNullString(paramString).length() == 0 ? 0L : Long.parseLong(paramString.trim());
		} catch (Exception localException) {
		}
		return 0L;
	}

	public static long getNullLong(Object paramObject) {
		try {
			return paramObject == null ? 0L : Long.parseLong(paramObject.toString().trim());
		} catch (Exception localException) {
		}
		return 0L;
	}

	public static double getNullDouble(String paramString) {
		try {
			return getNullString(paramString).length() == 0 ? 0.0D : Double.parseDouble(paramString.trim());
		} catch (Exception localException) {
		}
		return 0.0D;
	}

	public static double getNullDouble(Object paramObject) {
		try {
			return paramObject == null ? 0.0D : Double.parseDouble(paramObject.toString().trim());
		} catch (Exception localException) {
		}
		return 0.0D;
	}

	public static boolean getNullBoolean(String paramString) {
		return (getNullString(paramString).length() > 0) && ((paramString.toString().equalsIgnoreCase("True"))
				|| (paramString.toString().equalsIgnoreCase("1")));
	}

	public static boolean getNullBoolean(Object paramObject) {
		return paramObject == null ? false : ((Boolean) paramObject).booleanValue();
	}

	public static String getValueFromHashtable(Hashtable paramHashtable, String paramString) {
		String str = "";
		try {
			str = (String) paramHashtable.get(paramString);
			if (str == null) {
				str = "";
			}
		} catch (Exception localException) {
			str = "";
		}
		return str;
	}

	public static String replaceString(String paramString1, String paramString2, String paramString3) {
		String str = paramString1;
		StringBuffer localStringBuffer = new StringBuffer();
		if ((str != null) && (paramString2 != null) && (paramString3 != null)) {
			int i;
			while ((i = str.indexOf(paramString2)) != -1) {
				localStringBuffer.append(str.substring(0, i));
				localStringBuffer.append(paramString3);
				str = str.substring(i + paramString2.length());
			}
			localStringBuffer.append(str);
			return new String(localStringBuffer);
		}
		return paramString1;
	}

	public static String replaceStringNoCase(String paramString1, String paramString2, String paramString3) {
		StringBuffer localStringBuffer = new StringBuffer();
		if ((paramString1 != null) && (paramString2 != null) && (paramString3 != null)) {
			String str1 = paramString1;
			String str2 = paramString1.toLowerCase();
			String str3 = paramString2.toLowerCase();
			int i;
			while ((i = str2.indexOf(str3)) != -1) {
				localStringBuffer.append(str1.substring(0, i));
				localStringBuffer.append(paramString3);
				str2 = str2.substring(i + str3.length());
				str1 = str1.substring(i + paramString2.length());
			}
			localStringBuffer.append(str1);
			return new String(localStringBuffer);
		}
		return paramString1;
	}

	public static String replaceChar(String paramString1, char paramChar, String paramString2) {
		String str = "" + paramChar;
		return replaceString(paramString1, str, paramString2);
	}

	public static final String CreateUniqID(String paramString) {
		String str = System.currentTimeMillis() + "";
		return paramString + str;
	}

	public static String LeftPad(String paramString1, String paramString2, int paramInt) {
		int i = paramInt - paramString1.getBytes().length;
		String str = paramString1;
		for (int j = 0; j < i; j++) {
			str = paramString2 + str;
		}
		return str;
	}

	public static String RightPad(String paramString1, String paramString2, int paramInt) {
		int i = paramInt - paramString1.getBytes().length;
		String str = paramString1;
		for (int j = 0; j < i; j++) {
			str = str + paramString2;
		}
		return str;
	}

	public static int[] SplitterInt(String paramString1, String paramString2) {
		int i = CountStrNum(paramString1, paramString2);
		return SplitterInt(paramString1, paramString2, i);
	}

	public static int[] SplitterInt(String paramString1, String paramString2, int paramInt) {
		int i = -1;
		int j = 0;
		int k = 0;
		int[] arrayOfInt = new int[paramInt];
		for (k = 0; (i = paramString1.indexOf(paramString2, i + 1)) != -1; k++) {
			arrayOfInt[k] = Integer.parseInt(paramString1.substring(j, i));
			j = i + 1;
		}
		arrayOfInt[k] = Integer.parseInt(paramString1.substring(j, paramString1.length()));
		return arrayOfInt;
	}

	public static String[] splitter(String paramString1, String paramString2) {
		int i = CountStrNum(paramString1, paramString2);
		return splitter(paramString1, paramString2, i);
	}

	public static String[] splitter(String paramString1, String paramString2, int paramInt) {
		int i = -1;
		int j = 0;
		int k = 0;
		String[] arrayOfString = new String[paramInt];
		for (k = 0; (i = paramString1.indexOf(paramString2, i + 1)) != -1; k++) {
			arrayOfString[k] = paramString1.substring(j, i);
			j = i + 1;
		}
		arrayOfString[k] = paramString1.substring(j, paramString1.length());
		return arrayOfString;
	}

	public static int CountStrNum(String paramString1, String paramString2) {
		int i = -1;
		int j = 0;
		int k = 1;
		while ((i = paramString1.indexOf(paramString2, j)) >= 0) {
			k++;
			j = i + 1;
		}
		return k;
	}

	public static void appendParam(StringBuilder str, String key, Object value) throws Exception {
		if (str != null && !StringUtils.isEmpty(key)) {
			if (str.length() == 0) {
				str.append(key);
				str.append("=");
				str.append(value);
			} else {
				str.append("&");
				str.append(key);
				str.append("=");
				str.append(value);
			}
		} else {
			throw new Exception("字符串拼接异常,字符串对象或key值为空,str builder=" + str + ",key=" + key);
		}
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	public static void main(String[] paramArrayOfString) {
		String[] strArr = splitter("@host", ".");
		for(String str : strArr){
			System.out.println(str);
		}
			
	}
}
