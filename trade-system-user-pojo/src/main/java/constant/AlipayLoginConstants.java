package constant;

/**
 * 支付宝快捷登入常量
 *
 * @author GJ
 * @create 2017-12-14 16:23
 **/
public class AlipayLoginConstants {
    /**
     * 支付宝公共接口网关
     */
    public static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    /**
     * 客户端APPID
     */
    public static final String APP_ID = "2015010600023767";
    /**
     * 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
     */
    public static final String PARTNER = "2088701983728597";
    /**
     * 商户（RSA）私钥
     */
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJyOC2uoM+tMv1ZQCeq4U4He+If51d6MZdZopAImcsVI9Yh1Zc5nstzZvsZHPvrT6aogtes6UB4hVLVejSeiwQ1CwevDeVBCOo+dnbl+wqOj6KLTtS8ZhB+6r+BsAoxwv4VfYNzlGL7hrx21h5FDwahtha5jJSBVQQTbhtV7DrkVAgMBAAECgYBsV3jb1hmoGwLHDrjuMQXJeK7yGxnA29SWHvBxdH40vZr+BpCf2O/VGvOFcETLfN/WFrEOytorH9HpejehWGbUW1Fk30SGikBZRow9NNSLL3UIBZwqGtWT1vdNNRgcn+L86kZMmYainfz+6REUHGqof6ZEcxxdBLGvTOYhAUstgQJBAM2IRmqHFV+Ftgfa10B8fUvqKe8r3DLAua0euzSb7L7bjDlcdoLLfYALfpz99p0r0Qm5E0ppGopbc56ztAQrsnECQQDC/w4YFUtuSiBt9YcIm4/iYmHwSYUqYtFUOr/5hMFy7D5Xv+HaOpUnQTjCzyM0eNibeN0bU4rfTaEM1qoqcrrlAkEApxb1oB8Hmiua69HWkp9iQXgx9TWkA0K48Gv+Z2un0RWtbuijk/uYoKM3oKu6dNYtUAk9DI5bvEVSvp97ZrSbkQJAUYWeV9U3RvG4ox/+B0w6GFQ3S+UAxlqv1z4EmoW09p3r5nWzL7BEQTgUSeWde0d2j3E1R0JjEb57sUlhtQNoPQJAUgXrxmgOI/yfb8THaOK7eKPYcSzq4pU1Tr57LkOzgDDrKlrKyxk1GmPtXSgQlAWHOFTZkv0K1osCrRhzQNRGpg==";

    /**
     * 返回结果格式：xml、json;
     */
    public static final String FORMAT = "json";

    /**
     * 字符集格式
     */
    public static final String CHARSET = "utf-8";

    /**
     * AUTHORIZATION_CODE
     */
    public static final String AUTHORIZATION_CODE = "authorization_code";
    /**
     * 请求类型
     */
    public static final String PRODUCT_CODE ="WAP_FAST_LOGIN";


}
