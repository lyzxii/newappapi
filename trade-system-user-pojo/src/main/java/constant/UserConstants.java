package constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户中心常量类
 *
 * @author GJ
 * @create 2017-11-24 17:45
 **/
public class UserConstants {

    public static final String IP_PROXY_FILE_PATH = "/opt/export/config/";
    public static final String IP_PROXY_FILE_NAME = "proxy-ip-config.xml";
    public static final String EIGHTYSEVEN_PATH = "/opt/export/www/cms/news/ad/87.xml";
    public static final String LOGOCACHEKEYPRE = "9k88lottery_logo_";
    public static String DATA_DIR = "/opt/export/data/";
    public static final String UID_KEY = "uid";
    public static final String PWD_KEY = "pwd";
    public final static String BANKCARD_KEY = "bankCard";
    public final static String MOBILENO_KEY = "mobileNo";
    public final static String IDCARD_KEY = "idCard";
    public final static String REALNAME_KEY = "realName";

    public final static String ADD_KEY = "add";

    public static final String ENCODING = "UTF-8";
    public static final String COMFROM = "comfrom";
    public static final String PHONEFLAG = "phoneflag";
    public static final Integer ONEWEEK_EXPIRESTIME = 604800;//7天
    public static final Integer TENDAY_EXPIRESTIME = 864000000;//10天
    public static final String INITVECTOR = "1234567812345678";//初始化向量IV
    public static final String AESKEY = "70d72abe3ad9dc3e";
    public final static String DEFAULTPWD = "888888";
    public static final String MD5_KEY = "AKSj12SAQ18AQNWzqQPPX56QLCMZQqmSCxa08931";
	public static final String OPENUSER = "ouser";

    public static final String ACTIVE_APP_FILE_PATH = "/opt/export/data/app/huodong/";
    public static final String ACTIVE_360_FILE_NAME = "huodong_360.xml";

    public static final String ACTIVE_FILE_PATH = "/opt/export/data/huodong/";
    public static final String ACTIVE_HD_FILE_NAME = "redpacket_hd.xml";

    public static final String PUPLOAD_PATH = "/opt/export/data/pupload/";
    public static final String USERPHOTO_SUB_PATH = "userphoto/";
    public static final String USERPHOTO_SUB_IDCARDPATH = "idcardphoto/";

    public static final String USERPHOTO= "userPhoto";
    public static final String USERBANK_FRONT = "frontPhoto";
    public static final String USERBANK_BACKPHOTO = "backPhoto";
    public static final String SYSPAY = "syspay";

    public final static String ENCRYPT_KEY = "A9FK25RHT487ULMI";

    public static final String[] SERVICE_HOTLINE_KEY = {"desc", "phoneNo", "url"};

    public final static int QUERY_GOUCAI = 10;//购彩记录
    public final static int QUERY_ZHUIHAO = 11;//追号记录
    public final static int QUERY_AUTOBUY = 12;//定制跟单
    public final static int QUERY_ACCOUNT = 13;//账户明细
    public final static int QUERY_PAY = 14;//充值记录
    public final static int QUERY_CASH = 15;//提现记录
    public final static int QUERY_KTKMONEY = 23;//查询可提款金额
    public final static int QUERY_WZFGOUCAI = 33;//购彩记录
    public final static int QUERY_WZFZHUIHAO = 34;//追号记录

    public final static int QUERY_ZHUIHAO_DETAIL = 44;//追号记录明细

    public final static int WEB_SOURCE = 1141;//主站新版专业版
    public final static int VERSION = 2;//新版
    public final static String AUTO_BIND_PHONE = "auto_bind_phone";

    public final static String DEFAULT_MD5_KEY = "http://www.9188.com/";
    public final static String HSK_MD5_KEY = "http://www.huishuaka.com/";
    public static final String SMS_CONFIG = "/opt/export/www/cms/news/ad/sendsms.xml";
    public final static String MOBILE_URL = "http://mobile.9188.com";
    public final static String RANK_PATH = "/opt/export/data/guoguan/paihang/";

    public static final String HOST = "mobile.9188.com";

    public final static String CACHEKEY_OWNER_MAP = "_zjzh_switch";//个人map的key(使用时在前面拼接用户名)
    public final static String CACHEKEY_OWNER_MAP_AWARD = "zj_switch";//个人中奖key
    public final static String CACHEKEY_OWNER_MAP_ZHUIHAO = "zh_switch";//个人追号key


    public final static String MOBREGISTER = "mobRegister";
    public final static String NORMAL = "normal";
    public final static String PLATFORM = "9188";
    public final static String VOICE = "voice";
    public final static String IDFALIST = "00000000-0000-0000-0000-000000000000";
    public final static String SESSION_YZM = "rand";//session中的验证码
    public final static int MAXCOUNT = 5;//手机号注册最大次数
    public final static int SIZETHRESHOLD = 5242880;
    public final static String CONTENT_TYPE = "content-type";
    public final static String MULTIPART_FORMDATA = "multipart/form-data";
    public static String PATH = "";

    public final static String SIGN_TYPE = "MD5"; //签名方式
    public final static String MER_ID = "220160422000606";//正式商户号
    public final static String SIGN_KEY = "dAEPVJMUVJyAmjPXyGauWsx7CUCRKZnf";//签名密钥
    public final static String[] base64Keys = new String[] { "subject", "body", "remark" };// 对内容做Base64加密
    public final static String[] base64JsonKeys = new String[] { "customerInfo", "accResv", "riskRateInfo", "billQueryInfo","billDetailInfo" };// 对内容做Base64加密， 所有子域采用json数据格式
    //正式环境
    public final static String JNEW_URL = "http://api.chinagpay.com/bas/BgTrans";
    public final static String URL_PARAM_CONNECT_FLAG = "&";

    public final static int PROCEDURES_ERROR=9999;//存储过程错误
    public final static int PROCEDURES_CODE=1003;//同一IP地址绑定已超过每天限制次数(1000次)/同一手机号绑定已超过每天限制次数(3次)


    public final static String SIGNTYPE_RSA = "RSA";

    public final static String LOGO_9188 = "http://mobile.9188.com/img/9188.png";

    public final static String[] games = { "80", "81", "82", "83", "50", "51", "53", "52", "01", "03", "07","04","54","56","57","58","59","20","55","05","06","08","09","10","85","86","87","88","89","90","91","92","93","94","95","96","97","70","72","71"};



    /**
     * 快登相关
     */
    //start
    public static final int ALIPAY = 1; // 支付宝便捷登录
    //end





    //免登录通道
    public static Map<String, String> loginhezuo = new HashMap<String, String>();

    static {
        loginhezuo.put("110808001", "A5FK25RHT987ULMI");//大师
        loginhezuo.put("130313002", "A9FK25RHT487ULMI");//Android
        loginhezuo.put("130313003", "A9FK25RHT487ULMI");//IOS
        loginhezuo.put("130313004", "A9FK25RHT487ULMI");//wp
    }

    public static final String ROLLBACK_ALLYREGISTER = "allyRegister";
    public static final String ROLLBACK_BINDALIPAY = "bindAlipay";
    public static final String ROLLBACK_BINDIDCARD = "bindIdcard";
    public static final String ROLLBACK_WECHATREGISTER = "wechatRegister";
    public static final String ROLLBACK_WECHATBINDMOBILENO = "wechatBindmobileno";
    public static final String ROLLBACK_BINDWECHAT = "bindwechat";
    public static final String ROLLBACK_REGISTER = "register";
    public static final String ROLLBACK_USERBINDCHECK = "userbindcheck";
    public static final String ROLLBACK_BANKCARDINFO = "bankcardinfo";
    public static final String ROLLBACK_MODIFYBANKCARD = "modifybankcard";
}
