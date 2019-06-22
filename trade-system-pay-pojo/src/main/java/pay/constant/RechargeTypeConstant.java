package pay.constant;

//充值类型常量类
public class RechargeTypeConstant {
    public static final String RECHARGETYPE_BANKCARD = "bankCard";   //银行卡充值
    public static final String RECHARGETYPE_WEIXIN = "weixin";   //微信充值
    public static final String RECHARGETYPE_ALIPAY = "alipay";   //支付宝充值
    public static final String RECHARGETYPE_TENPAY = "tenpay";   //QQ支付
    public static final String RECHARGETYPE_OTHER = "other";   //其他

    public static final String[] RECHARGETYPE_ARR = {RECHARGETYPE_BANKCARD, RECHARGETYPE_WEIXIN, RECHARGETYPE_ALIPAY, RECHARGETYPE_TENPAY, RECHARGETYPE_OTHER};
}
