package com.caiyi.lottery.tradesystem.util;

import java.security.MessageDigest;

/**
 * md5util
 *
 * @author GJ
 * @create 2017-12-12 20:52
 **/
public class MD5Util {
    public static char[] hexChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static String compute(String var0) throws Exception {
        MessageDigest var1 = MessageDigest.getInstance("MD5");
        char[] var2 = var0.toCharArray();
        byte[] var3 = new byte[var2.length];

        for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = (byte)var2[var4];
        }

        byte[] var8 = var1.digest(var3);
        StringBuffer var5 = new StringBuffer();

        for(int var6 = 0; var6 < var8.length; ++var6) {
            int var7 = var8[var6] & 255;
            if(var7 < 16) {
                var5.append("0");
            }

            var5.append(Integer.toHexString(var7));
        }

        return var5.toString();
    }

    /**
     * MD5方法
     *
     * @param text 明文
     * @param charset 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5WithCharSet(String text, String charset) throws Exception {
        if(charset == null || charset.length()==0)
            charset = "UTF-8";

        byte[] bytes = text.getBytes(charset);

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(bytes);
        bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < bytes.length; i ++)
        {
            if((bytes[i] & 0xff) < 0x10)
            {
                sb.append("0");
            }

            sb.append(Long.toString(bytes[i] & 0xff, 16));
        }

        return sb.toString().toLowerCase();
    }

    public static void main(String[] var0) throws Exception {
        System.out.println(compute("1234wq56789rt0"+"http://www.9188.com/"));
        System.out.println(MD5Helper.md5Hex("1234wq56789rt0"));
        System.out.println(MD5Helper.md5Hex("1234wq56789rt0",null));
    }
}
