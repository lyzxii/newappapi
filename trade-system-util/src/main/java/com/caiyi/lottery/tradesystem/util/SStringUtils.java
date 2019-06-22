package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by tiankun on 2017/12/27.
 */
public class SStringUtils {
    public static boolean endwith(String str, String suffix) {
        if ((str == null) || (suffix == null)) {
            return (str == null) && (suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(true, strOffset, suffix, 0, suffix.length());
    }
    public static int getmatchCnt(String xmlpath) {
        File file = new File(xmlpath);
        if (file == null || !file.exists()) {
            return 0;
        }
        JXmlWrapper jxml = JXmlWrapper.parse(file);
        String yhcode = jxml.getStringValue("row.@code");
        String[] yhcodeList = yhcode.split("\\;");
        return yhcodeList.length;
    }
    //过关方式排序
    public static String sortedGuoGuan(String str) {
        try {
            String[] arrs=str.split(",");
            if(arrs.length>1){
                Arrays.sort(arrs,new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        int i=Integer.valueOf(o1.substring(0, o1.lastIndexOf("*")));
                        int j=Integer.valueOf(o2.substring(0, o2.lastIndexOf("*")));
                        return i>j?1:i<j?-1:0;
                    }
                });
                str=Arrays.asList(arrs).toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
                return str;
            }
        } catch (Exception e) {
            //
        }
        return str;
    }

    public static void main(String[] args) {
        System.out.println(sortedGuoGuan("6*1,8*1,7*1"));
        System.out.println(sortedGuoGuan("2*1"));
        System.out.println(sortedGuoGuan(""));
    }
}
