package com.caiyi.lottery.tradesystem.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by tiankun on 2017/12/27.
 */
public class CaiyiEncrypt {
    public CaiyiEncrypt() {
    }
    public static String encryptStr(String value) {
        try {
            byte[] temp = AESUtil.encrypt("9188123123123345", "9188123123123345", value.getBytes("utf-8"));
            return Base64.encode(temp, "utf-8").trim();
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        return "";
    }
    public static String dencryptStr(String value) {
        try {
            byte[] temp = AESUtil.decrypt("9188123123123345", "9188123123123345", Base64.decode(value.getBytes("utf-8")));
            return (new String(temp, "utf-8")).trim();
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return "";
    }

}
