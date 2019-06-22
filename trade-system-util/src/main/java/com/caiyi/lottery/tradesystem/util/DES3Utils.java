package com.caiyi.lottery.tradesystem.util;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * DES3 加解密工具类
 */
public class DES3Utils {

    private static byte[] keyiv = "12345678".getBytes();

    /**
     * CBC加密
     *
     * @param key
     *            密钥
     * @param data
     *            明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static String des3EncodeCBC(String key, String data) {
        try {
            DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyiv);
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] bOut = cipher.doFinal(data.getBytes("UTF-8"));
            return new BASE64Encoder().encode(bOut);
        } catch (Exception e) {
            throw new RuntimeException("加密出错,key:" + key + ",data:" + data, e);
        }
    }

    /**
     * CBC解密
     *
     * @param key
     *            密钥
     * @param data
     *            Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static String des3DecodeCBC(String key, String data) {
        try {
            DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyiv);
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
            byte[] bOut = cipher.doFinal(new BASE64Decoder().decodeBuffer(data));
            return new String(bOut, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("解密出错, key:" + key + ",data:" + data, e);
        }
    }

}
