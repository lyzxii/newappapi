package com.caiyi.lottery.tradesystem.util;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AES 加解密工具类
 */
@Slf4j
public class AESUtils {

    public static final String algorithmStr = "SHA1PRNG";//"AES/ECB/PKCS5Padding";//AES/CBC/PKCS5Padding

    public static final String CHAR_ENCODING = "UTF-8";

    public static final String AES_ALGORITHM = "AES";
    /*
     * 加密
     */
     public static String aesEncode(String encodeRules,String content){
        try {
            SecretKey key = getSecretKey(encodeRules);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte [] byte_encode=content.getBytes(CHAR_ENCODING);
            byte [] byte_AES=cipher.doFinal(byte_encode);
            String AES_encode=Base64.encode(byte_AES,CHAR_ENCODING);
            return AES_encode;
        } catch (Exception e) {
            log.error("aesEncode Exception encodeRules:"+encodeRules+" content:"+content,e);
        }
        return null;
    }

    /*
     * 解密
     */
    public static String aesDncode(String encodeRules,String content){
        try {
            SecretKey key = getSecretKey(encodeRules);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte [] byte_content = Base64.decode(content.getBytes(CHAR_ENCODING));
            byte [] byte_decode=cipher.doFinal(byte_content);
            String AES_decode=new String(byte_decode,CHAR_ENCODING);
            return AES_decode;
        } catch (Exception e) {
            log.error("aesDncode Exception encodeRules:"+encodeRules+" content:"+content,e);
        }
        return null;
    }

    private static SecretKey getSecretKey(String encodeRules) throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance(AES_ALGORITHM);
        //SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        SecureRandom random = SecureRandom.getInstance(algorithmStr);
        random.setSeed(encodeRules.getBytes());
        //keygen.init(128, new SecureRandom(encodeRules.getBytes()));
        keygen.init(128, random);
        SecretKey original_key = keygen.generateKey();
        byte [] raw = original_key.getEncoded();
        return new SecretKeySpec(raw, AES_ALGORITHM);
    }

    public static void main(String[] args) {
        String p = "13761493867";
        String pp="Fp0men2EHLWv/G0bgmdB9Q==";
        String encodeRules = "l8i9KqIw4AN0gj3ihny7OVnG";
        String encptycontent = "13761498888";
        String decptycontent = "eiUOkJe81QkRGV9L4L/0vQ==";

        String p1 = aesEncode(encodeRules, p);
        String p2 = aesDncode(encodeRules, pp);
        System.out.println(p1);
        System.out.println(p2);
        /*
         * 加密
         */
        System.out.println("使用AES对称加密，请输入加密的规则:"+ encodeRules);
        System.out.println("请输入要加密的内容:"+ encptycontent);
        System.out.println("根据输入的规则"+encodeRules+"加密后的密文是:"+AESUtils.aesEncode(encodeRules, encptycontent));

        /*
         * 解密
         */
        System.out.println("使用AES对称解密，请输入加密的规则：(须与加密相同)"+ encodeRules);
        System.out.println("请输入要解密的内容（密文）:"+decptycontent);
        System.out.println("根据输入的规则"+encodeRules+"解密后的明文是:"+AESUtils.aesDncode(encodeRules, decptycontent));
    }
}
