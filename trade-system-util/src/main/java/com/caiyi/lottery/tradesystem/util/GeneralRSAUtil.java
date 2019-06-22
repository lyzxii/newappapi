package com.caiyi.lottery.tradesystem.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


public class GeneralRSAUtil {
	  
    /** *//** 
     * 加密算法RSA 
     */  
    public static final String KEY_ALGORITHM = "RSA";  
      
    /** *//** 
     * 签名算法 
     */  
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";  
  
    /** *//** 
     * 获取公钥的key 
     */  
    private static final String PUBLIC_KEY = "RSAPublicKey";  
      
    /** *//** 
     * 获取私钥的key 
     */  
    private static final String PRIVATE_KEY = "RSAPrivateKey";  
      
    /** *//** 
     * RSA最大加密明文大小 
     */  
    private static final int MAX_ENCRYPT_BLOCK = 117;  
      
    /** *//** 
     * RSA最大解密密文大小 
     */  
    private static final int MAX_DECRYPT_BLOCK = 128;  
    
    public static final String SMS_PUB_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3P6B3b1orfaI4"
    		+ "vGJAbGKH4Dqd4e0YZmJanPxBJiZNXAELFre9ZNmYBOZGSohb3YTpuD31s6mWI8bMZaePsBQzgIUic9tXgkZS"
    		+ "NY0A5BmgPmrQQDRY4beQBdG6sDTWGIM1Kxujp9voTMYgIJqlhaS5WNb1A/zv2XBhrbFRbtJ1JQIDAQAB";
    public static final String SMS_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGB"
    		+ "ALc/oHdvWit9oji8YkBsYofgOp3h7RhmYlqc/EEmJk1cAQsWt71k2ZgE5kZKiFvdhOm4PfWzqZYjxsxlp4+w"
    		+ "FDOAhSJz21eCRlI1jQDkGaA+atBANFjht5AF0bqwNNYYgzUrG6On2+hMxiAgmqWFpLlY1vUD/O/ZcGGtsVFu"
    		+ "0nUlAgMBAAECgYEAlCKQYtyWZP/7yqenOXMkt7ixSf5gP1BNA/lSBoyDfBf7E+66fBVxvW2AhaQs1S6fCp0R"
    		+ "IXWqix/CzmsC6KoruRNXDoquagMMcU22kiT6pmsMcfr5f17yc0GCOAKB9VN7qf2ItDmaYQ1lN4jg0IagQClw"
    		+ "DV7Qg/oy6GN/EzeJsSECQQDqHH7rSbjhMtCL2VjPdJ90syfRIYx8+uJOapp0juOes3OcwOiw+Kyt7Z13PQFT"
    		+ "fbx0KmWVuZ7A/IZkgEJ9BYYdAkEAyGG4RPBdOrI8C8jP7GsUKA9f/mkfTPMO3vJMMmFioHatxcGGWxaJeLqT"
    		+ "azpjpYPXs7qJIB5lS4r+o/SjntzcqQJBAINYo3icrwZh/RFJSwNxmfjefwtdODXyiD+YnbmhZWFnqwiaTrLj"
    		+ "gfY/JyZ76p+OHspJ8x6SutfplfkS4LrF+50CQHKp6IYafV7fKxkbOk/xHkGmOqocgXrJA1N0l0GlFIj6IIov"
    		+ "KbDp/pSFP2J2sRq/jSkdruYgPpqx2PD9RrvbarECQF+40YmMjd4oa44fNhwW4YblufYkPzFA5cQEx6IpbHhM"
    		+ "+MOxgzJrmM+DpE9E7dBDRJmKN8Quu9LqpYproifUBOY=";
  
    /** *//** 
     * <p> 
     * 生成密钥对(公钥和私钥) 
     * </p> 
     *  
     * @return 
     * @throws Exception 
     */  
    public static Map<String, Object> genKeyPair() throws Exception {  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        keyPairGen.initialize(1024);  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        keyMap.put(PUBLIC_KEY, publicKey);  
        keyMap.put(PRIVATE_KEY, privateKey);  
        return keyMap;  
    }

    public static String signMd5WithRSA(String content, PrivateKey privateKey, String charset) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

            signature.initSign(privateKey);
            signature.update(BankUtil.getContentBytes(content, charset));

            return GeneralBase64Utils.encode(signature.sign());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /** *//** 
     * <p> 
     * 用私钥对信息生成数字签名 
     * </p> 
     *  
     * @param data 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     *  
     * @return 
     * @throws Exception 
     */  
    public static String sign(byte[] data, String privateKey) throws Exception {  
        byte[] keyBytes = GeneralBase64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(privateK);  
        signature.update(data);  
        return GeneralBase64Utils.encode(signature.sign());  
    }  
  
    /** *//** 
     * <p> 
     * 校验数字签名 
     * </p> 
     *  
     * @param data 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @param sign 数字签名 
     *  
     * @return 
     * @throws Exception 
     *  
     */  
    public static boolean verify(byte[] data, String publicKey, String sign)  
            throws Exception {  
        byte[] keyBytes = GeneralBase64Utils.decode(publicKey);  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        PublicKey publicK = keyFactory.generatePublic(keySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(publicK);  
        signature.update(data);  
        return signature.verify(GeneralBase64Utils.decode(sign));  
    }  
  
    /** *//** 
     * <P> 
     * 私钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)  
            throws Exception {  
        byte[] keyBytes = GeneralBase64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, privateK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥解密 
     * </p> 
     *  
     * @param encryptedData 已加密数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)  
            throws Exception {  
        byte[] keyBytes = GeneralBase64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.DECRYPT_MODE, publicK);  
        int inputLen = encryptedData.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段解密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {  
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_DECRYPT_BLOCK;  
        }  
        byte[] decryptedData = out.toByteArray();  
        out.close();  
        return decryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 公钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param publicKey 公钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)  
            throws Exception {  
        byte[] keyBytes = GeneralBase64Utils.decode(publicKey);  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key publicK = keyFactory.generatePublic(x509KeySpec);  
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, publicK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 私钥加密 
     * </p> 
     *  
     * @param data 源数据 
     * @param privateKey 私钥(BASE64编码) 
     * @return 
     * @throws Exception 
     */  
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)  
            throws Exception {  
        byte[] keyBytes = GeneralBase64Utils.decode(privateKey);  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
        cipher.init(Cipher.ENCRYPT_MODE, privateK);  
        int inputLen = data.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(data, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return encryptedData;  
    }  
  
    /** *//** 
     * <p> 
     * 获取私钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public static String getPrivateKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PRIVATE_KEY);  
        return GeneralBase64Utils.encode(key.getEncoded());  
    }  
  
    /** *//** 
     * <p> 
     * 获取公钥 
     * </p> 
     *  
     * @param keyMap 密钥对 
     * @return 
     * @throws Exception 
     */  
    public static String getPublicKey(Map<String, Object> keyMap)  
            throws Exception {  
        Key key = (Key) keyMap.get(PUBLIC_KEY);  
        return GeneralBase64Utils.encode(key.getEncoded());  
    }  
    
    public static void main(String[] args) throws Exception {
//    	Map<String, Object> keyMap = genKeyPair();
//    	String privateKey = getPrivateKey(keyMap);
//    	String publicKey = getPublicKey(keyMap);
//    	System.out.println("privateKey:"+privateKey);
//    	System.out.println("publicKey:"+publicKey);
//    	String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALc/oHdvWit9oji8YkBsYofgOp"
//    			+ "3h7RhmYlqc/EEmJk1cAQsWt71k2ZgE5kZKiFvdhOm4PfWzqZYjxsxlp4+wFDOAhSJz21eCRlI1jQDkGaA+at"
//    			+ "BANFjht5AF0bqwNNYYgzUrG6On2+hMxiAgmqWFpLlY1vUD/O/ZcGGtsVFu0nUlAgMBAAECgYEAlCKQYtyWZP"
//    			+ "/7yqenOXMkt7ixSf5gP1BNA/lSBoyDfBf7E+66fBVxvW2AhaQs1S6fCp0RIXWqix/CzmsC6KoruRNXDoquag"
//    			+ "MMcU22kiT6pmsMcfr5f17yc0GCOAKB9VN7qf2ItDmaYQ1lN4jg0IagQClwDV7Qg/oy6GN/EzeJsSECQQDqHH"
//    			+ "7rSbjhMtCL2VjPdJ90syfRIYx8+uJOapp0juOes3OcwOiw+Kyt7Z13PQFTfbx0KmWVuZ7A/IZkgEJ9BYYdAk"
//    			+ "EAyGG4RPBdOrI8C8jP7GsUKA9f/mkfTPMO3vJMMmFioHatxcGGWxaJeLqTazpjpYPXs7qJIB5lS4r+o/Sjnt"
//    			+ "zcqQJBAINYo3icrwZh/RFJSwNxmfjefwtdODXyiD+YnbmhZWFnqwiaTrLjgfY/JyZ76p+OHspJ8x6Sutfplf"
//    			+ "kS4LrF+50CQHKp6IYafV7fKxkbOk/xHkGmOqocgXrJA1N0l0GlFIj6IIovKbDp/pSFP2J2sRq/jSkdruYgPp"
//    			+ "qx2PD9RrvbarECQF+40YmMjd4oa44fNhwW4YblufYkPzFA5cQEx6IpbHhM+MOxgzJrmM+DpE9E7dBDRJmKN8"
//    			+ "Quu9LqpYproifUBOY=";
//    	String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3P6B3b1orfaI4vGJAbGKH4Dqd4e0YZmJanPx"
//    			+ "BJiZNXAELFre9ZNmYBOZGSohb3YTpuD31s6mWI8bMZaePsBQzgIUic9tXgkZSNY0A5BmgPmrQQDRY4beQBdG"
//    			+ "6sDTWGIM1Kxujp9voTMYgIJqlhaS5WNb1A/zv2XBhrbFRbtJ1JQIDAQAB";
//        System.out.println("公钥加密——私钥解密");  
//        String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？11111111111111111111111111111111111111"
//        		+ "111111111111111asxsaxsaxsaxxxxxxxxxasxsxsaxsaxsax請求請求群群群群群群群群群群所尋奧奧奧奧奧奧奧奧"
//        		+ "奧奧奧奧奧奧奧奧奧奧奧所尋尋尋尋尋尋尋尋尋尋尋尋尋尋尋奧奧奧奧奧奧奧奧奧奧奧奧奧請求請求群群群群群群群群群群群群群"
//        		+ "11111111111111111惺惺惜惺惺尋尋尋尋尋尋尋尋尋請問啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊"
//        		+ "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊群群群群群群群重中之重做做做做做做做做做做做做"
//        		+ "做做做做做做做做做做做做啛啛喳喳錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯錯啛"
//        		+ "啛喳喳錯錯錯錯錯錯錯錯錯錯錯錯";  
//        System.out.println("\r加密前文字：\r\n" + source);  
//        byte[] data = source.getBytes();  
//        byte[] encodedData = encryptByPublicKey(data, SMS_PUB_KEY);  
//        String transfer = GeneralBase64Utils.encode(encodedData);
//        System.out.println("加密后文字：\r\n" + transfer);  
//        byte[] decodedData = decryptByPrivateKey(GeneralBase64Utils.decode(transfer), SMS_PRIVATE_KEY);  
//        String target = new String(decodedData);  
//        System.out.println("解密后文字: \r\n" + target); 
//    	byte[] decodedData = decryptByPrivateKey(GeneralBase64Utils.decode("CqsxO5r3tjPKOUi5l0FKSUJzlMcxttdVaYp5Gj+VlqvnLn/3DJAJYkxqjUP5mZWsF9Jkt49fd6pA"
//    			+ "GxwYjsxH+/PM0Mwe8IhDiGM6WxRV5ViOrsnasZvUwSo4LjoYmCPV0hH4+ZNPaz+EOfxxP7YpYrvu"
//    			+ "JtFuY6adJq07M6CaP1U="), SMS_PRIVATE_KEY);  
//    	System.out.println(new String(decodedData));
    	String json = "{'osversion':'7.0.0','imei':'QzQ2QzQ5M0MyOEVBM0U2MTgwMzM1M0M5RDMyQjQ1OEQ=',"
    			+ "'logintype':'1','signmsg':'2a8f6687c2cb99659422d12c7cd8fa26','rversion':'4.6.0',"
    			+ "'appversion':'460','stime':'1510800250061','uid':'xcw168','mtype':'1',"
    			+ "'source':'1003','mobileNo':'13348093857','flag':'1'}";
    	byte[] byteArr = encryptByPublicKey(json.getBytes(), SMS_PUB_KEY);
    	System.out.println(GeneralBase64Utils.encode(byteArr));
	}
}
