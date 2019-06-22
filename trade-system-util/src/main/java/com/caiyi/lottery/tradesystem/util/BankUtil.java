package com.caiyi.lottery.tradesystem.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class BankUtil {
	
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
        digest.update(getContentBytes(message, charset));

        byte[] signed = digest.digest();

        return HexUtils.toHexString(signed);
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
     * Returns a MessageDigest for the given <code>algorithm</code>.
     *
     *            The MessageDigest algorithm name.
     * @return An MD5 digest instance.
     * @throws RuntimeException
     *             when a {@link NoSuchAlgorithmException} is
     *             caught
     */

    static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     * 
     * @param data
     *            Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(byte[] data) {
        return getDigest().digest(data);
    }

    /**
     * Calculates the MD5 digest and returns the value as a 16 element
     * <code>byte[]</code>.
     * 
     * @param data
     *            Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(String data) {
        return md5(data.getBytes());
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     * 
     * @param data
     *            Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(byte[] data) {
        return toHexString(md5(data));
    }

    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex
     * string.
     * 
     * @param data
     *            Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(String data) {
        return toHexString(md5(data));
    }
   
    /**
     * Converts a byte array to hex string.
     * 
     * @param b -
     *            the input byte array
     * @return hex string representation of b.
     */
    
    public static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_CHARS.charAt(b[i] >>> 4 & 0x0F));
            sb.append(HEX_CHARS.charAt(b[i] & 0x0F));
        }
        return sb.toString();
    }

    /**
     * Converts a hex string into a byte array.
     * 
     * @param s -
     *            string to be converted
     * @return byte array converted from s
     */
    public static byte[] toByteArray(String s) {
        byte[] buf = new byte[s.length() / 2];
        int j = 0;
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) ((Character.digit(s.charAt(j++), 16) << 4) | Character
                    .digit(s.charAt(j++), 16));
        }
        return buf;
    }

    private static final String HEX_CHARS = "0123456789abcdef";
 
	
    public static String appendParam(String returnStr, String paramId, String paramValue) {
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			if (!paramValue.equals("")) {
				returnStr = paramId + "=" + paramValue;
			}
		}
		return returnStr;
	}

	
	public static String appendParam_all(String returnStr, String paramId, String paramValue) {
		if (!returnStr.equals("")) {
			returnStr = returnStr + "&" + paramId + "=" + paramValue;
		} else {
			returnStr = paramId + "=" + paramValue;
		}
		return returnStr;
	}
	
	public static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Not support:" + charset, ex);
        }
    }
	
	/** 银行代号 */
    public static Map<String, String> bankInfoMap = new HashMap<String, String>();
    static {
        bankInfoMap.put("1","招商银行");
        bankInfoMap.put("2","工商银行");
        bankInfoMap.put("13","农业银行");
        bankInfoMap.put("3","建设银行");
        bankInfoMap.put("4","中国银行");
        bankInfoMap.put("6","交通银行");
        bankInfoMap.put("8","中信银行");
        bankInfoMap.put("9","兴业银行");
        bankInfoMap.put("10","光大银行");
        bankInfoMap.put("11","华夏银行");
        bankInfoMap.put("12","民生银行");
        bankInfoMap.put("25","邮储银行");
        bankInfoMap.put("1000","广发银行");
        bankInfoMap.put("1001","深圳发展银行");
        bankInfoMap.put("4000","浦发银行");
        bankInfoMap.put("15","农村信用合作社");
        bankInfoMap.put("16","农村商业银行");
        bankInfoMap.put("17","农村合作银行");
        bankInfoMap.put("18","城市商业银行");
        bankInfoMap.put("19","城市信用合作社");
        bankInfoMap.put("23","平安银行");
        bankInfoMap.put("4001","上海银行");
        bankInfoMap.put("2000","北京银行");
        bankInfoMap.put("22","恒丰银行");
        bankInfoMap.put("24","渤海银行");
        bankInfoMap.put("1002","广州银行");
        bankInfoMap.put("1003","珠海南通银行");
        bankInfoMap.put("3000","天津银行");
        bankInfoMap.put("5000","浙商银行");
        bankInfoMap.put("5001","浙江商业银行");
        bankInfoMap.put("5002","宁波国际银行");
        bankInfoMap.put("5003","宁波银行");
        bankInfoMap.put("5004","温州银行");
        bankInfoMap.put("6000","南京银行");
        bankInfoMap.put("6001","常熟农村商业银行");
        bankInfoMap.put("7000","福建亚洲银行");
        bankInfoMap.put("7001","福建兴业银行");
        bankInfoMap.put("7002","徽商银行");
        bankInfoMap.put("7003","厦门国际银行");
        bankInfoMap.put("8000","青岛市商业银行");
        bankInfoMap.put("8001","济南市商业银行");
        bankInfoMap.put("9000","重庆银行");
        bankInfoMap.put("10000","成都市商业银行");
        bankInfoMap.put("11000","哈尔滨银行");
        bankInfoMap.put("12000","包头市商业银行");
        bankInfoMap.put("13000","南昌市商业银行");
        bankInfoMap.put("14000","贵阳商业银行");
        bankInfoMap.put("15000","兰州市商业银行");
    }
    
    /**
     * 支持提款的银行图标
     */
    public static Map<String, String> bankImageMap = new HashMap<String, String>();
    static {
    	bankImageMap.put("1","/image/zs_yh_icon@3x.png");//招商银行
    	bankImageMap.put("2","/image/gs_yh_icon@3x.png");//工商银行
    	bankImageMap.put("13","/image/ny_yh_icon@3x.png");//农业银行
    	bankImageMap.put("3","/image/js_yh_icon@3x.png");//建设银行
    	bankImageMap.put("4","/image/zg_yh_icon@3x.png");//中国银行
    	bankImageMap.put("6","/image/jt_yh_icon@3x.png");//交通银行
    	bankImageMap.put("8","/image/zx_yh_icon@3x.png");//中信银行
    	bankImageMap.put("9","/image/xy_yh_icon@3x.png");//兴业银行
    	bankImageMap.put("10","/image/gd_yh_icon@3x.png");//光大银行
    	bankImageMap.put("11","/image/hx_yh_icon@3x.png");//华夏银行
    	bankImageMap.put("12","/image/ms_yh_icon@3x.png");//中国民生银行
    	bankImageMap.put("25","/image/yz_yh_icon@3x.png");//中国储蓄银行
    	bankImageMap.put("1000","/image/gf_yh_icon@3x.png");//广东发展银行
    	bankImageMap.put("4000","/image/pf_yh_icon@3x.png");//上海浦东发展银行
    	bankImageMap.put("23","/image/pa_yh_icon@3x.png");//平安银行
    }

    // 将变量值不为空的参数组成字符串
    public static void appendParam(StringBuilder str, String key, Object value) throws Exception {
        if (str != null && !StringUtil.isEmpty(key)) {
            if (str.length() == 0) {
                str.append(key);
                str.append("=");
                str.append(value);
            } else {
                str.append("&");
                str.append(key);
                str.append("=");
                str.append(value);
            }
        } else {
            throw new Exception("字符串拼接异常,字符串对象或key值为空,str builder=" + str + ",key=" + key);
        }
    }
    
}