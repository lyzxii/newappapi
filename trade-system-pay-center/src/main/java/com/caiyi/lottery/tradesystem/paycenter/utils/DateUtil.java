package com.caiyi.lottery.tradesystem.paycenter.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tiankun on 2018/1/3.
 */
public class DateUtil {
    public DateUtil() {
    }

    public static String getCurrentDate() {
        try {
            SimpleDateFormat var0 = new SimpleDateFormat("yyyy-MM-dd");
            return var0.format(new Date());
        } catch (Exception var1) {
            return "";
        }
    }

    public static String getCurrentFormatDate(String var0) {
        try {
            SimpleDateFormat var1 = new SimpleDateFormat(var0);
            return var1.format(new Date());
        } catch (Exception var2) {
            return "";
        }
    }

    public static String getCurrentTime1(int var0, int var1) {
        try {
            SimpleDateFormat var2 = new SimpleDateFormat("HH:mm:ss");
            Calendar var3 = Calendar.getInstance();
            var3.add(var0, var1);
            return var2.format(var3.getTime());
        } catch (Exception var4) {
            return "";
        }
    }

    public static String getCurrentDate(int var0) {
        try {
            SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd");
            Calendar var2 = Calendar.getInstance();
            var2.add(5, var0);
            return var1.format(var2.getTime());
        } catch (Exception var3) {
            return "";
        }
    }

    public static String getDateTime(int var0, int var1) {
        try {
            SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar var3 = Calendar.getInstance();
            var3.add(var0, var1);
            return var2.format(var3.getTime());
        } catch (Exception var4) {
            return "";
        }
    }

    public static String getCurrentDate(String var0) {
        try {
            SimpleDateFormat var1 = new SimpleDateFormat("yyyy-MM-dd");
            Calendar var2 = Calendar.getInstance();
            var2.add(5, Integer.parseInt(var0));
            return var1.format(var2);
        } catch (Exception var3) {
            return "";
        }
    }

    public static String getCurrentTime() {
        try {
            SimpleDateFormat var0 = new SimpleDateFormat("HH:mm:ss");
            return var0.format(new Date());
        } catch (Exception var1) {
            return "";
        }
    }

    public static String getCurrentDateTime() {
        try {
            SimpleDateFormat var0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return var0.format(new Date());
        } catch (Exception var1) {
            return "";
        }
    }

    public static String ConvertToTime(String var0) {
        String var1 = "";

        try {
            if(var0.length() == 5) {
                var1 = "0" + var0.substring(0, 1) + ":" + var0.substring(1, 3) + ":" + var0.substring(3);
            } else {
                var1 = var0.substring(0, 2) + ":" + var0.substring(2, 4) + ":" + var0.substring(4);
            }
        } catch (Exception var3) {
            var1 = "";
        }

        return var1;
    }

    public static String ConvertToTime1(String var0) {
        String var1 = "";

        try {
            if(var0.length() == 5) {
                var1 = "0" + var0.substring(0, 1) + "时" + var0.substring(1, 3) + "分" + var0.substring(3) + "秒";
            } else {
                var1 = var0.substring(0, 2) + "时" + var0.substring(2, 4) + "分" + var0.substring(4, 6) + "秒";
            }
        } catch (Exception var3) {
            var1 = "";
        }

        return var1;
    }

    public static String ConvertToDate(String var0) {
        String var1 = "";

        try {
            if(var0.length() == 6) {
                var0 = "20" + var0;
            }

            if(var0.length() == 8) {
                var1 = var0.substring(0, 4) + "-" + var0.substring(4, 6) + "-" + var0.substring(6);
            }
        } catch (Exception var3) {
            var1 = "";
        }

        return var1;
    }

    public static String ConvertToDate1(String var0) {
        String var1 = "";

        try {
            if(var0.length() == 6) {
                var0 = "20" + var0;
            }

            if(var0.length() == 8) {
                var1 = var0.substring(0, 4) + "年" + var0.substring(4, 6) + "月" + var0.substring(6) + "日";
            }
        } catch (Exception var3) {
            var1 = "";
        }

        return var1;
    }

    public static String getCurrentDate1() {
        try {
            SimpleDateFormat var0 = new SimpleDateFormat("yyyy年MM月dd日");
            return var0.format(new Date());
        } catch (Exception var1) {
            return "";
        }
    }

    public static Date parserDate(String var0) {
        Date var1 = null;

        try {
            SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd");
            var1 = var2.parse(var0);
        } catch (Exception var3) {
            var1 = null;
        }

        return var1;
    }

    public static Date parserDateTime(String var0) {
        Date var1 = null;

        try {
            SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var1 = var2.parse(var0);
        } catch (Exception var3) {
            var3.printStackTrace();
            var1 = null;
        }

        return var1;
    }

    public static String getDateTime(long var0) {
        try {
            SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return var2.format(new Date(var0));
        } catch (Exception var3) {
            return "";
        }
    }

    public static String getDateTime(long var0, String var2) {
        try {
            SimpleDateFormat var3 = new SimpleDateFormat(var2);
            return var3.format(new Date(var0));
        } catch (Exception var4) {
            return "";
        }
    }
}
