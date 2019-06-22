package com.caiyi.lottery.tradesystem.util;

/**
 * 十六进制工具
 * 
 * @author sunaolin
 * 
 */
public abstract class HexUtils {
    /**
     * 把{@code Byte}数组转成十六进制格式的字符串
     * 
     * @param value 需要转换的字节数组
     * 
     * @return 返回转换后的{@code String}对象
     */
    public static String toHexString(byte[] value) {
        if (value == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer(value.length * 2);
        for (int i = 0; i < value.length; i++) {
            sb.append(toHexString(value[i]));
        }
        return sb.toString();
    }

    /**
     * 把{@code Byte}类型转成十六进制格式的字符串
     * 
     * @param value 需要转换的值
     * 
     * @return 返回转换后的{@code String}对象
     */
    public static String toHexString(byte value) {
        String hex = Integer.toHexString(value & 0xFF);

        return padZero(hex, 2);
    }

    /**
     * 使用"0"左补齐字符串
     * 
     * @param hex 十六进制字符串
     * 
     * @param length 字符串的固定长度
     * 
     * @return 返回补齐后的十六进制字符串
     */
    private static String padZero(String hex, int length) {
        for (int i = hex.length(); i < length; i++) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }
}