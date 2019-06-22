package com.caiyi.lottery.tradesystem.userweb.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * token生成器.
 * @author huzhiqiang
 *
 */
public class TokenGenerator {
    public static Logger logger = LoggerFactory.getLogger("token_info");
    // 24位密钥
    public static final String  SECRET = "a60e73ec8c752ac89b521777";
	
	/**
	 * 检查密钥长度,如不足24位,则加0x00补齐.
	 * 
	 * @param keyString
	 * @return
	 */
    public static byte[] getKeyBytes(String keyString) {
        byte[] b = new byte[24];
        byte[] key = keyString.getBytes();
        int count = keyString.getBytes().length;
        for (int i = 0; i < count; i++) {
            b[i] = key[i];
        }
        for (int i = count; i < 24; i++) {
            b[i] = 0x20;
        }
        return b;
    }
	
	
	/***
	 * RTUAPID加密.
	 * 
	 * @param src
	 * @return
	 */
    public static String createDigest(String src) {
        String ret = "";
        try {
			// Hash算法
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(src.getBytes());
			// Base64加密
            ret = new BASE64Encoder().encode(sha.digest());
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return ret;
    }

	
	/***
	 * 验证认证码.
	 * 
	 * @param authentication
	 * @return
	 */
    public static String[] authToken(String authentication, String appid) {
		// 输出参数 
        String[] str = new String[2];
		// 拆分认证码验证

        try {
			// 24字节密钥key,3倍DES密钥长度
            byte[] tripleKey = getKeyBytes(SECRET);

			// 生成密钥
            SecretKey secretKey = new SecretKeySpec(tripleKey, "DESede");

            String transformation = "DESede/CBC/PKCS5Padding";
            Cipher cipher = Cipher.getInstance(transformation);

			// CBC方式的初始化向量
            byte[] iv = new byte[] { 93, 81, (byte) 122, (byte) 233, 47, 50, 17, (byte) 103 };
            IvParameterSpec ivparam = new IvParameterSpec(iv);
		
			// 解密
            byte[] auth = new BASE64Decoder().decodeBuffer(authentication);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparam);
            String text = new String(cipher.doFinal(auth));

			// 验证
            String[] textValue = text.split("\\$");
            if (textValue.length == 2) {
				// 拆分值并存入Map中
                HashMap<String, String> map = new HashMap<String, String>();
                String[] tokenValue = textValue[0].split(";");
                for (int i = 0; i < tokenValue.length; i++) {
                    String[] value = tokenValue[i].split("=");
                    map.put(value[0], value[1]);
                }
				// 判断值是否相等
                if ("9188".equals(map.get("company")) && "LOTTERY".equals(map.get("appType"))
                                && appid.equals(map.get("appid"))) {
                    str[0] = "1";
                    str[1] = "验证成功!";
                } else {
                    str[0] = "0";
                    str[1] = "验证失败,数据不匹配!";
                    logger.info("company:"+map.get("company")+" apptype:"+map.get("appType")+" 解密appid:"+map.get("appid")+" 原appid:"+appid+"  authentication:"+authentication);
                }
            } else {
                str[0] = "0";
                str[1] = "验证失败,格式错误!";
            }
        } catch (Exception ex) {
            str[0] = "-1";
            str[1] = "验证失败,出异常!";
            logger.info("验证失败,出异常  authentication:"+authentication, ex);
        }
        return str;
    }

	
	/**
	 * 认证码.
	 * 
	 * @return
	 * @throws Exception
	 */
    public static String createToken(String appid) {
        //String authenticator = "access_token:";
        String authenticator = "";
        try {
			// 当前时间
            Date now = new Date();
            SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// Digest
            String digest = createDigest(appid);

            String orgToken = "company=9188;appType=LOTTERY;appid=" + appid
					+ ";currentTime=" + d1.format(now)
					+ "$" + digest;

			// 24字节密钥key,3倍DES密钥长度
            byte[] tripleKey = getKeyBytes(SECRET);

			// 生成密钥
            SecretKey secretKey = new SecretKeySpec(tripleKey, "DESede");

            String transformation = "DESede/CBC/PKCS5Padding";   // DES,DESede,Blowfish，
			
            Cipher cipher = Cipher.getInstance(transformation);

			// CBC方式的初始化向量
            byte[] iv = new byte[] { 93, 81, (byte) 122, (byte) 233, 47, 50, 17, (byte) 103 };
            IvParameterSpec ivparam = new IvParameterSpec(iv);

			// 加密
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparam);
			
            byte[] encriptText = cipher.doFinal(orgToken.getBytes("utf-8"));

			// 加密
            authenticator += new BASE64Encoder().encode(encriptText);

			//去掉token中的空格和换行符
            authenticator = getStringNoBlank(authenticator);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return authenticator;
    }
	
	//正则表达式替换空格和换行符
    public static String getStringNoBlank(String str) {      
        if (str != null && !"".equals(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");      
            Matcher m = p.matcher(str);      
            String strNoBlank = m.replaceAll("");      
            return strNoBlank;      
        } else {      
            return str;      
        }           
    }     
    
    public static void main(String[] args) {
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K7WdguII3mwmESgrenI6CkS04m29e6D0dRiNKHxoK+6dRYJFUoHN75FIHOrz9e7Zo5LhpnrxlM54uTlhffTQGOnIi91dL3Rh+UKqXFQHtFzQeOQXUx50s6aBt+ZSch7Uaw==";
    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtMZoxMqTU4aSRL/ca5/bMLr9NJkwVOCQtgvw+2CvpamE0w6l4HEw+piPz1mEm+H65MOIlOnWJJnYBkCtsU2c+AN7OivK6MS2vZ0IR7MzFFLLMwbO+HVO4sKLUYXsIhHN9oTkmo5Q2h6Q==&";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuPX25D6oPE7Lz5SEa6kLelWgycK0AUaVdYli8NU4giunJ0FNmoGU1t9CxxCtTWrzn0oSgzOM7+f01D8xardMaqgmyo4El65XjsUI6vEL0CI0q1GvXQ78dAuIAAFC+tIflN31uF5KHsXQ==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtu/BPj2x+Ucv4q5XGsre6Huqfug2EFoeYtLPsYinDk9X34QFMRNTwB7Vmm84BiT6KNurg6jlP603Ny1+dSoJtD5FFW+ab2gneSa2UeqXGmO5FdDTekwQ0aJz6TLT9Y+tDXK+jicYsABg==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvJEi7AGWS/dyX/EEQo0+tToUdcj5pL9JKgGSr9yiRafALD1jMSGgeh1RBZracPxsW/iPC3ZCDy7Xdx0yFs0TpxC6540L7Ny+iG+9+DvKZafKqfvR7SwgojnCns/1oMdYKMTqa3FUPpwA==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtlEBnj0ueKFNtqyKbKuhRxr/zYYaNWgmQjxyOWWJD9SGQ1mnvCCb/Bqm+1pEu0VLZQgKjeCPanGuj21LGrfYAlmSHao0PssV7jr2bSJiZnAGCmSDw4ne1cp/+39hxZG3cIJsNTenuChQ==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvs8omV0zK5N59jz2xp9drKnuoNf25NfF41s5HZepny5PWiu5W+UBz+m+tcpq/mDDc2YDPulfBCFO7L3e98yWYB3YQMKUfr8VdnK8a0SJGu3u9oq7d6d0gkun+vJfMg6v9vCSx9foJg9Lw==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvvLUedDw+0ZapxIXeedQxNqYADsfedl2Os7MP8lT62knWy2oxw5o7s0CeDZYbyvCyE9Weh/azSr4gRkLGxK5uvNl5PaFb2f8Xrpi4TWvau2lAcx1Tdxc1qvXp8HqdEaxjjzVI4ZMrufw==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvJEi7AGWS/d5bHl7YeiLPgIu7IVGzRnd94vooHJ1QE4G21zITsli4eMBfIaQT4YkI2yhZ18eu16F7a/ulDo/iSaP+FuDbkXN4aDHg9eIXEU/gRDcpQEhvO2BxQI3lKQGKgkneunDy7Sg==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtxJq0JkKSdQojruHdOvZBBQ5PtoHw0tvxIEsXFZ4TVs4Ed34jT+EN1eLKMGTjZrUdf5YRY+ts7FRdB6V8c1S/HoNfGHpiH20APOsGB/UM6xywcHY2LFU0+23DHMPQMoyCJaMdCloimWw==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvtydv9zibe61HIE+AZPPiaASCZa7gBOKsANGoYEZYa3mbtIKdj+5CA3Z8j/2g5uZaOjw4/ZguGviq2AsT01fZtssOGLVaB1JG+yolqPbfe2YIEaXd8Bl9W266XQm7jUPckT0KF6WPWHrQ==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvDAiNNNPkA6zxJ4zkfpJJTEQa/TZF410VqyzqS1qHbiE/XbALoMWgAk7feEpmxBLntAA2rpX2P4O6c/Hizb/XI14bmZdxPF3KuNaewaRRY/BNNPdaedO4KVbeNhXxnFmAkmTsbfgh5Xg==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvqgdrY0caI8ulJMKS1ImpzW4xCynAfj6qRAfHtgu/4std4a1XNZ0AsYuKnWFrDDf8snRnwBMEr3XYkmdj9s5jqGIKRMbsHuluhyRMIB5QhofrzRgQQHxvVFiiGS3nCLfbSfEHzI3gGPg==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvu8aBRGX9FLbW/OqYyK/lngvRdXUhg++F97Cs/fDyYjksN7IboJR4hXlsnbzovGR0jRCH09dnyez9Wv099d2Q6XlEqH1e0kVreAD80bZE1zUmwu0akcoqxAdJR68qVjmFLy8tJuqy7pg==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuWhbZkQq4FvZ5HizFq5++JCmqu026uGmoxUgkykw5Uce33a3kLo4V11BB9Ll11wlDAMkA3F+SgK3sRCxVEUznTYyacKHX7cAYW7sCxf84kA9JIZTJoU++fxMdK5YUj2bgtppskUuqvDQ==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K+cSBchyPxUDaJbjM6m6l4xBWNJr1teIhdsvXxWpCoHSQvxfProC8q0F9O91CqiASGgpFH1cMPjyHZ/FklSwIWtbz8M2jsqVP+W2jt9GcgBjsngJFrc/EyqokQ9Oimgb4w==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K+mI4q+fHCypQSlF5tBLStWBuVbzb2tIn6VSD5YX58wXTictrf393KRzDMsK327SHvQPidpnE3/8YaeuCempVuVgNQDsfbTsEQHSKgCrfVzDdXJSP+Eft4RikLaUCqqAYA==";
//    	String aa ="+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K4HcDmmdFfZO7X2KFiNkK4fWD9pbpdcQj5v6NrV3qlcnZ2vOW7H8KQUvgUbb+Nmth6AX35SXK2uuCuwr0xPQCnveD6JzEttenrtEWl139cC1dUWduShiCDxrdZOom2qcLQ==";
    	String appid = "lt2KR016NQX092JB211WQDW33Q45M53N5";
    	String[] authToken = authToken(aa, appid);
    	for (String string : authToken) {
    		System.out.println(string);
		}
	}
	
}
