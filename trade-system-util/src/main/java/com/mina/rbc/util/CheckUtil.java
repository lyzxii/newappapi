package com.mina.rbc.util;

public class CheckUtil {
	public static final boolean isNullString(String paramString) {
		return (paramString == null) || (paramString.length() <= 0);
	}

	public static final boolean isNullObject(Object paramObject) {
		if (paramObject == null)
			return true;
		if ((paramObject instanceof String))
			return paramObject.toString().length() == 0;
		return false;
	}
}
