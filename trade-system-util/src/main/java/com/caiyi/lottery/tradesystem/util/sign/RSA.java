package com.caiyi.lottery.tradesystem.util.sign;



import com.caiyi.lottery.tradesystem.util.BankUtil;
import com.caiyi.lottery.tradesystem.util.GeneralBase64Utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * 使用{@code RAS} 方式加签和验签
 * 
 * @author sunaolin
 * 
 */
public class RSA {

    public static final String SIGN_ALGORITHMS = "MD5withRSA";

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
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(GeneralBase64Utils.decode(privateKey));

            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(keySpec);

            return sign(content, priKey, charset);
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
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(privateKey);
            signature.update(BankUtil.getContentBytes(content, charset));

            return GeneralBase64Utils.encode(signature.sign());
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
    public static boolean verify(String content, String sign, String publicKey, String charset) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(GeneralBase64Utils.decode(publicKey));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            return verify(content, sign, pubKey, charset);
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
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(publicKey);
            signature.update(BankUtil.getContentBytes(content, charset));

            return signature.verify(GeneralBase64Utils.decode(sign));
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
