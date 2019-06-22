package com.caiyi.lottery.tradesystem.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 安全工具
 * 
 * @author 9188
 *
 */
@Slf4j
public class SecurityTool {
	
	public static Logger logger = LoggerFactory.getLogger("SecurityTool");
	// h5密钥
	private final static String h5secretKey = "9188123123123345";
	// 加解密统一使用的编码方式
	private final static String encoding = "UTF-8";
	// 密钥
	private final static String secretKey = "umpay2015#12add7";

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    
    

	/**
     * MD5加密
     */
    public static String getMD5Str(String source) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] byteArray = messageDigest.digest(source.getBytes(encoding));
        StringBuffer md5StrBuff = new StringBuffer();
        int length = byteArray.length;
        for (int i = 0; i < length; i++) {
        	if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
        		md5StrBuff.append("0").append(
        				Integer.toHexString(0xFF & byteArray[i]));
        	} else {
        		md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        	}
        }
        return md5StrBuff.toString();
    }
    

	/**
	 * IOS  AES加密
	 * @param value
	 * @return
	 */
	public static String iosencrypt(String value){
		try {
			byte[] temp = AESUtil.encrypt(secretKey,secretKey, value.getBytes(encoding));
			return Base64.encode(temp, encoding).trim();
		} catch (UnsupportedEncodingException e) {
			logger.error("ios加密错误，内容:"+value,e);
		} catch (Exception e) {
			logger.error("ios加密错误，内容:"+value,e);
		}
		return "";
	}

	/**
	 * IOS AES解密
	 * @param value
	 * @return
	 */
	public static String iosdecrypt(String value){
		try {
			byte[] temp = AESUtil.decrypt(secretKey,secretKey, Base64.decode(value.getBytes(encoding)));
			return new String(temp, encoding).trim();
		} catch (UnsupportedEncodingException e) {
			logger.error("ios解密错误，内容:"+value,e);
		} catch (Exception e) {
			logger.error("ios解密错误，内容:"+value,e);
		}
		return "";
	}


	/**
	 * IOS  AES加密
	 * @param value
	 * @return
	 */
	public static String h5encrypt(String value){
		try {
			byte[] temp = AESUtil.encrypt(h5secretKey,h5secretKey, value.getBytes(encoding));
			return Base64.encode(temp, encoding).trim();
		} catch (UnsupportedEncodingException e) {
			logger.error("h5加密错误，内容:"+value,e);
		} catch (Exception e) {
			logger.error("h5加密错误，内容:"+value,e);
		}
		return "";
	}

	/**
	 * IOS AES解密
	 * @param value
	 * @return
	 */
	public static String h5decrypt(String value){
		try {
			byte[] temp = AESUtil.h5Decrypt(h5secretKey,h5secretKey, Base64.decode(value.getBytes(encoding)));
			return new String(temp, encoding).trim();
		} catch (UnsupportedEncodingException e) {
			logger.error("h5解密错误，内容:"+value,e);
		} catch (Exception e) {
			logger.error("h5解密错误，内容:"+value,e);
		}
		return "";
	}


	public static String sign(String content, String privateKey) throws  Exception{
		String charset = "utf-8";

		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
				ZfbBase64.decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		Signature signature = Signature
				.getInstance(SIGN_ALGORITHMS);

		signature.initSign(priKey);
		signature.update(content.getBytes(charset));

		byte[] signed = signature.sign();

		return ZfbBase64.encode(signed);


	}

	
	/**
     *发送短信前进行校验
     */
    public static boolean stopSMSbomb(String signmsg,String mobileNo,String time)  {
    	boolean flag = false;
    	if(StringUtil.isEmpty(signmsg)){
	        return flag;
    	}
    	//生成服务器签名串
        StringBuilder sb = new StringBuilder();
        sb.append("imNo");
        sb.append("=");
        sb.append(mobileNo);
        sb.append("&");
        sb.append("timestamp");
        sb.append("=");
        sb.append(time);
        sb.append("&");
        sb.append("key");
        sb.append("=");
        sb.append("1.0^adhfjkas565a4sdf36a4s6df46^");//随机字符串
        String serverSignMsg = null;
		try {
			serverSignMsg = getMD5Str(sb.toString());
		} catch (NoSuchAlgorithmException e) {
			logger.error("忘记密码-验签异常",e);
		} catch (UnsupportedEncodingException e) {
			logger.error("忘记密码-验签异常",e);
		}
        //校验
		if (serverSignMsg.equals(signmsg)){//验签成功
			flag = true;
	    }
		return flag;
	}


    public static void main(String[] args) throws Exception{

        String mingwen = "18117374695";
        String miwen = "wzw3uTcImPKunrwALz6eJw==";
		String res = MD5Helper.md5Hex("13761493867");
		String encode = "wzw3uTcImPKunrwALz6eJw==";
		iosdecrypt(encode);
		System.out.println("md5加密="+res);
		System.out.println("ab8a6c07afb4df30bd9cc69b1cf62581");
		try {
           	String encrypt = iosencrypt(mingwen);
            System.out.println("加密 ：" + encrypt);
            String iosdecrypt = iosdecrypt(miwen);
            System.out.println("解密 ：" + iosdecrypt);
        } catch (Exception e) {

		}
    }
}
