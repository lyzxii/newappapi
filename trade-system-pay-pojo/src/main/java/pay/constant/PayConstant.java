package pay.constant;

import pay.pojo.BankCard;

import java.util.HashMap;
import java.util.Map;

public class PayConstant {
    public static String RETURN_HOST = "http://mobile.9188.com";// 通知返回的HOST
    //public static String RETURN_HOST = "http://gs.9188.com:18080";// 通知返回的HOST
    //public static String RETURN_HOST = "http://sss.9188.com:8080";// 通知返回的HOST
    //public static String  NOTIFY_HOST = "http://gs.9188.com:18080";
//	测试环境
    //public static String NOTIFY_HOST = "http://t2015.9188.com";// 通知返回的HOST
    //	public static String  H5_CALL_BACKURL = "http://t2015.9188.com";
//	public static String  NOTIFY_HOST = "http://mobile.gs.9188.com";// 通知返回的HOST
    //正式环境
	//public static String  NOTIFY_HOST = "http://mopay.9188.com";// 通知返回的HOST
    public static String  NOTIFY_HOST = "http://mobilev2.9188.com";// 通知返回的HOST
    public static String H5_CALL_BACKURL = "http://5.9188.com";

    //连连回调充值银行卡信息
    public static Map<String, BankCard> llBankCard = new HashMap<>();
    public static Map<String, String> llCardType = new HashMap<>();

    static {
        llBankCard.put("04031000", new BankCard("BCCB", "北京银行"));
        llBankCard.put("01020000", new BankCard("ICBC", "工商银行"));
        llBankCard.put("03030000", new BankCard("CEB", "光大银行"));
        llBankCard.put("03060000", new BankCard("GDB", "广发银行"));
        llBankCard.put("64135810", new BankCard("GZCB", "广州银行"));
        llBankCard.put("04233310", new BankCard("HCCB", "杭州银行"));
        llBankCard.put("03040000", new BankCard("HXB", "华夏银行"));
        llBankCard.put("04403600", new BankCard("HSBANK", "徽商银行"));
        llBankCard.put("01050000", new BankCard("CCB", "建设银行"));
        llBankCard.put("05083000", new BankCard("JSB", "江苏银行"));
        llBankCard.put("03010000", new BankCard("COMM", "交通银行"));
        llBankCard.put("03050000", new BankCard("CMBC", "民生银行"));
        llBankCard.put("04243010", new BankCard("NJCB", "南京银行"));
        llBankCard.put("04083320", new BankCard("NBCB", "宁波银行"));
        llBankCard.put("01030000", new BankCard("ABC", "农业银行"));
        llBankCard.put("03070000", new BankCard("SZPAB", "平安银行"));
        llBankCard.put("03100000", new BankCard("SPDB", "浦发银行"));
        llBankCard.put("04012900", new BankCard("BOS", "上海银行"));
        llBankCard.put("03090000", new BankCard("CIB", "兴业银行"));
        llBankCard.put("01000000", new BankCard("PSBC", "邮储银行"));
        llBankCard.put("03080000", new BankCard("CMB", "招商银行"));
        llBankCard.put("01040000", new BankCard("BOC", "中国银行"));
        llBankCard.put("03020000", new BankCard("CITIC", "中信银行"));

        llCardType.put("BCCB", "北京银行");
        llCardType.put("ICBC", "工商银行");
        llCardType.put("CEB", "光大银行");
        llCardType.put("GDB", "广发银行");
        llCardType.put("GZCB", "广州银行");
        llCardType.put("HCCB", "杭州银行");
        llCardType.put("HXB", "华夏银行");
        llCardType.put("HSBANK", "徽商银行");
        llCardType.put("CCB", "建设银行");
        llCardType.put("JSB", "江苏银行");
        llCardType.put("COMM", "交通银行");
        llCardType.put("CMBC", "民生银行");
        llCardType.put("NJCB", "南京银行");
        llCardType.put("NBCB", "宁波银行");
        llCardType.put("ABC", "农业银行");
        llCardType.put("SZPAB", "平安银行");
        llCardType.put("SPDB", "浦发银行");
        llCardType.put("BOS", "上海银行");
        llCardType.put("CIB", "兴业银行");
        llCardType.put("PSBC", "邮储银行");
        llCardType.put("CMB", "招商银行");
        llCardType.put("BOC", "中国银行");
        llCardType.put("CITIC", "中信银行");
    }


    public static Map<String, String> llBankCardType = new HashMap<>();

    static {
        llBankCardType.put("2", "借记卡");
        llBankCardType.put("3", "信用卡");
    }
}
