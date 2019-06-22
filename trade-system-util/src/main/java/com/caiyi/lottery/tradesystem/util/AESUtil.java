package com.caiyi.lottery.tradesystem.util;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.Security;

/**
 * AES 算法工具类。
 */
public final class AESUtil {
    
	/** private constructor. */
	private AESUtil() {
	}

	/** the name of the transformation to create a cipher for. */
	private static final String TRANSFORMATION = "AES/ECB/PKCS7Padding";

	/** 算法名称 */
	private static final String ALGORITHM_NAME = "AES";

	/**
	 * aes 加密，AES/CBC/PKCS5Padding
	 * 
	 * @param key
	 *            密钥字符�? 此处使用AES-128-CBC加密模式，key�?���?6�?
	 * @param content
	 *            要加密的内容
	 * @param cbcIv
	 *            初始化向�?CBC模式必须使用) 使用CBC模式，需要一个向量iv，可增加加密算法的强�?
	 * @return 加密后原始二进制字符�?
	 * @throws Exception
	 *             Exception
	 */
	public static byte[] encrypt(String cbcIv, String key, byte[] content)
			throws Exception {

		SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(),
				ALGORITHM_NAME);
		//
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");

		// IvParameterSpec iv = new IvParameterSpec(cbcIv.getBytes());

		cipher.init(Cipher.ENCRYPT_MODE, sksSpec);

		byte[] encrypted = cipher.doFinal(content);
//		for (int i = 0; i < encrypted.length; i++) {
//			System.out.printf("%x", encrypted[i]);
//		}
//		System.out.println();
		return encrypted;
	}

	/**
	 * aes 解密，AES/CBC/PKCS5Padding
	 * 
	 * @param key
	 *            密钥, 此处使用AES-128-CBC加密模式，key�?���?6�?
	 * @param encrypted
	 *            密文
	 * @param cbcIv
	 *            初始化向�?CBC模式必须使用) 使用CBC模式，需要一个向量iv，可增加加密算法的强�?
	 * @return 明文
	 * @throws Exception
	 *             异常
	 */
	public static byte[] decrypt(String cbcIv, String key, byte[] encrypted)
			throws Exception {

		SecretKeySpec skeSpect = new SecretKeySpec(key.getBytes(),
				ALGORITHM_NAME);

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");

		// IvParameterSpec iv = new IvParameterSpec(cbcIv.getBytes());

		cipher.init(Cipher.DECRYPT_MODE, skeSpect);

		byte[] decrypted = cipher.doFinal(encrypted);

		return decrypted;
	}

	public static byte[] h5Decrypt(String cbcIv, String key, byte[] encrypted) throws Exception {
		SecretKeySpec skeSpect = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(cbcIv.getBytes());
		cipher.init(2, skeSpect, iv);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
    
    // test
    public static void main(String[] args) {
        String content = "888888";
        
        String key = "umpay20151207813";
        
        String miwen  ="F774715CD8175AF43670068C1AD69C99";
        
        try {
            byte[] encrypted = AESUtil.encrypt(key,key,content.getBytes());
            System.out.println(encrypted.length);
            System.out.println("加密  ：" + byteArrToString(encrypted));
            
            System.out.println(miwen.toCharArray().length);
            byte[] decrypt = AESUtil.decrypt(key,key,encrypted);
            System.out.println("解密 ：" + byteArrToString(decrypt));
            
        } catch (Exception e) {

        }
    }
    
    
    private static String byteArrToString(byte[] arr)  
    {  
        StringBuffer sb = new StringBuffer();  
  
        for (int i = 0; i < arr.length; i++)  
        {  
  
            String s = Integer.toString(arr[i] + 128, 16);  
            if (s.length() == 1)  
            {  
                s = "0" + s;  
            }  
  
            sb.append(s);  
        }  
  
        return sb.toString().toUpperCase();  
    }  
}
