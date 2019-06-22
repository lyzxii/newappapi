package com.caiyi.lottery.tradesystem.util.sign;


import com.caiyi.lottery.tradesystem.util.sign.snk.SNKReader;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 使用{@code SNK} 方式加签和验签
 * 
 * @author sunaolin
 * 
 */
public class SNKRSA {

    /**
     * 使用{@code RSA}方式对字符串进行签名
     * 
     * @param content 需要加签名的数据
     * @param privateKey {@code RSA}的私钥
     * @param charset 数据的编码方式
     * @return 返回签名信息
     */
    public static String sign(String content, String privateKey, String charset) {
        try {
            return sign(content, SNKReader.getPrivateKey(privateKey), charset);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 使用{@code RSA}方式对字符串进行签名
     * 
     * @param content 需要加签名的数据
     * @param privateKey {@code RSA}的私钥
     * @param charset 数据的编码方式
     * @return 返回签名信息
     */
    public static String sign(String content, PrivateKey privateKey, String charset) {

        return RSA.sign(content, privateKey, charset);
    }

    /**
     * 使用{@code RSA}方式对签名信息进行验证
     * 
     * @param content 需要加签名的数据
     * @param sign 签名信息
     * @param publicKey {@code RSA}的公钥
     * @param charset 数据的编码方式
     * @return 是否验证通过。{@code True}表示通过
     */
    public static boolean verify(String content, String sign, String publicKey, String charset) {
        try {
            return verify(content, sign, SNKReader.getPublicKey(publicKey), charset);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 使用{@code RSA}方式对签名信息进行验证
     * 
     * @param content 需要加签名的数据
     * @param sign 签名信息
     * @param publicKey {@code RSA}的公钥
     * @param charset 数据的编码方式
     * @return 是否验证通过。{@code True}表示通过
     */
    public static boolean verify(String content, String sign, PublicKey publicKey, String charset) {

        return RSA.verify(content, sign, publicKey, charset);
    }
}