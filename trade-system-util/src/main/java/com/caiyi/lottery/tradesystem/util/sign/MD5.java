package com.caiyi.lottery.tradesystem.util.sign;


import com.caiyi.lottery.tradesystem.util.BankUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 使用{@code MD5} 方式加签和验签
 * 
 * @author sunaolin
 * 
 */
public class MD5 {

    public static final String SIGN_ALGORITHMS = "MD5";

    /**
     * 使用{@code MD5}方式对字符串进行签名
     * 
     * @param text 需要加签名的数据
     * @param key 对需要签名的的数据进行加盐
     * @param charset 数据的编码方式
     * @return 返回签名信息
     */
    public static String sign(String text, String key, String charset) {
        String message = text + key;

        MessageDigest digest = getDigest(SIGN_ALGORITHMS);
        digest.update(BankUtil.getContentBytes(message, charset));

        byte[] signed = digest.digest();

        return toHexString(signed);
    }

    /**
     * 使用{@code MD5}方式对签名信息进行验证
     * 
     * @param text 需要加签名的数据
     * @param sign 签名信息
     * @param key 对需要签名的的数据进行加盐
     * @param charset 数据的编码方式
     * @return 是否验证通过。{@code True}表示通过
     */
    public static boolean verify(String text, String sign, String key, String charset) {
        String mysign = sign(text, key, charset);

        if (mysign.equals(sign)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 返回实现指定摘要算法的 {@code MessageDigest} 对象。
     * 
     * @param algorithm 信息摘要算法名称
     * 
     * @return 返回摘要算法对象
     */
    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Not support:" + algorithm, ex);
        }
    }
    
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