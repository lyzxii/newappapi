package com.caiyi.lottery.tradesystem.util;

import java.util.Calendar;

/**
 * 
 * 唯一字符串创建工具类.
当前的年月日十分表毫秒组成的字符串，加上前缀字符，这些数字加起来，只有18位，
生成的最短为32位，剩下14位字符随机生成，而且每生成一个字符，会在前面字符中间找一个随机位置插入进去，这样不断的来回操作，直到位数达到32位为止
比如：U2013T1KU220KJ03CE23N1O3X7HRAJ63  这个字符串，前缀字符 U，中间的数字不难看出是2013年12月20日03时23分13秒763毫秒，
										剩下的字符都是随机生成，且随机插入进去的，我试着运行了很多遍，每一毫秒最多生成20个这样的字符串，
										但是这儿是个字符串中要想其他随机生成的字符完全一样，且排序一样，
										而且随机插入的位置还要一样，这个概率机会可以完全忽略。
**/

public class UniqueStrCreator {
    

    public static final char PREFIX = 'U';
    
    private UniqueStrCreator(){}

	/**
	 * 创建唯一标识字符串.
	 * 
	 * @param prefixStr
	 *            前缀字符串
	 * @return
	 */
    public static String createUniqueString(String prefixStr) {
        if (prefixStr != null && prefixStr.trim().length() > 0) {
            StringBuffer str = new StringBuffer(prefixStr);
            str.append(mixString());
            return str.toString();
        } else {
            return createUniqueString();
        }

    }

	/**
	 * 创建唯一标识字符串.
	 * 
	 * @param prefix
	 *            字符串前缀字符
	 * @return
	 */
    public static String createUniqueString(char prefix) {
        if (prefix == ' '){
            return createUniqueString();
        }
        StringBuffer str = new StringBuffer(String.valueOf(prefix));
        str.append(mixString());
        return str.toString().toUpperCase();
    }

	/**
	 * 创建唯一标识字符串,默认前缀为U.
	 * 
	 * @return
	 */
    public static String createUniqueString() {
        StringBuffer str = new StringBuffer(String.valueOf(PREFIX));
        str.append(mixString());
        return str.toString().toUpperCase();
    }

    private static synchronized String mixString() {
        StringBuffer buffer = new StringBuffer(createTimeStr());
        int len = 31 - buffer.length();
        int len2 = buffer.length();
        for (int i = 0; i < len; i++) {
            buffer.insert(getRandomPos(len2), getRandomChar());
            len2 = buffer.length();
        }
        return buffer.toString();
    }

    private static int getRandomPos(int len) {
        if (len == 0){
            len = 1;
        }
        int pos = 0;
        pos = (int) (Math.random() * 1000);
        return pos % len;
    }

    public static synchronized String createTimeStr() {
        StringBuffer buffer = new StringBuffer("");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int h = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int ms = calendar.get(Calendar.MILLISECOND);
        buffer.append(year);
        if (month < 10) {
            buffer.append(0);
        }
        buffer.append(month);
        if (day < 10) {
            buffer.append(0);
        }
        buffer.append(day);
        if (h < 10) {
            buffer.append(0);
        }
        buffer.append(h);
        if (min < 10) {
            buffer.append(0);
        }
        buffer.append(min);
        if (second < 10) {
            buffer.append(0);
        }
        buffer.append(second);
        if (ms < 10) {
            buffer.append("00");
        } else if (ms >= 10 && ms < 100) {
            buffer.append(0);
        }
        buffer.append(ms);
        return buffer.toString();
    }

    public static char getRandomChar() {
        char c = 0;
        int b = (int) (Math.random() * 1000) % 26;
        c = (char) (b + 65);
        return c;
    }

}
