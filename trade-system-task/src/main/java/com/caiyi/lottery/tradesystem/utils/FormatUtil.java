package com.caiyi.lottery.tradesystem.utils;

import java.text.DecimalFormat;

/**
 * @author wxy
 * @create 2018-04-03 17:17
 **/
public class FormatUtil {
    /**
     * 格式化赔率
     *  赔率规则：1.赔率小于100时，保留两位小数；2.赔率大于等于100，小于1000时，保留一位小数；3.赔率大于等于1000时，不保留小数
     * @param sp
     * @return
     */
    public static String formatSP(String sp) {
        DecimalFormat format;
        String spStr = "";
        double spNumber = Double.parseDouble(sp);
        if (spNumber < 100) {
            format = new DecimalFormat("#.00");
            spStr = format.format(spNumber);
        } else if (spNumber >= 100 && spNumber < 1000) {
            format = new DecimalFormat("#.0");
            spStr = format.format(spNumber);
        } else {
            format = new DecimalFormat("#");
            spStr = format.format(spNumber);
        }
        return spStr;
    }
}
