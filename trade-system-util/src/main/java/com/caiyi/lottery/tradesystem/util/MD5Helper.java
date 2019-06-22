package com.caiyi.lottery.tradesystem.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5摘要算法
 *
 * @author liyunlong
 * @date 2017/12/6 17:11
 */
public class MD5Helper {

    private static final String HEX_CHARS = "0123456789abcdef";

    private static final String DEFAULT_FACTOR = "http://www.9188.com/";

    private MD5Helper() {
        throw new UnsupportedOperationException("Instantiation operation is not supported.");
    }

    static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var1) {
            throw new RuntimeException(var1);
        }
    }

    public static byte[] md5(byte[] data) {
        return getDigest().digest(data);
    }

    public static byte[] md5(String data) {
        return md5(data.getBytes());
    }

    public static String md5Hex(byte[] data) {
        return toHexString(md5(data));
    }

    public static String md5Hex(String data) {
        return md5Hex(data,null);
    }

    public static String md5Hex(String data,String privateKey){
        if (privateKey == null) {
            privateKey = DEFAULT_FACTOR;
        }
        return toHexString(md5(data+privateKey));
    }
    
    public static String sign(String data, String key) {
        return toHexString(md5(data+key));
    }

    public static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < b.length; ++i) {
            sb.append("0123456789abcdef".charAt(b[i] >>> 4 & 15));
            sb.append("0123456789abcdef".charAt(b[i] & 15));
        }

        return sb.toString();
    }

    public static byte[] toByteArray(String s) {
        byte[] buf = new byte[s.length() / 2];
        int j = 0;

        for (int i = 0; i < buf.length; ++i) {
            buf[i] = (byte) (Character.digit(s.charAt(j++), 16) << 4 | Character.digit(s.charAt(j++), 16));
        }

        return buf;
    }

    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        } else if (!paramValue.equals("")) {
            returnStr = paramId + "=" + paramValue;
        }

        return returnStr;
    }

    public static String appendParam_all(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            returnStr = returnStr + "&" + paramId + "=" + paramValue;
        } else {
            returnStr = paramId + "=" + paramValue;
        }

        return returnStr;
    }
}
