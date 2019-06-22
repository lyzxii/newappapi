package com.caiyi.lottery.tradesystem.usercenter.util;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;

import java.util.*;

/**
 * 用户账户操作基础类.
 */
public class UserActionBase {
	public final static String DEFAULT_MD5_KEY = "http://www.9188.com/";

	/**
     * 安卓平台代号.
     */
	public static final int CAIYI_ANDROID = 1;
    /**
     * 苹果平台代号.
     */
	public static final int CAIYI_IOS = 2;
    /**
     * WP平台代号.
     */
	public static final int CAIYI_WP = 3;
    /**
     * 触屏平台代号.
     */
	public static final int CAIYI_TOUCH = 4;
	public final static String LOGO_9188 = "http://mobile.9188.com/img/9188.png";
	public final static String LOGO_HSK = "http://mobile.9188.com/img/huishuaka.png";
	public final static String LOGO_AIDUOBAO = "";
	public final static String LOGO_LICAIDI = "";
	public final static String LOGO_GJJ = "";
	public final static String _9188 = "9188cp";
	public final static String _HSK = "hsk";
	public final static String _AIDUOBAO = "aiduobao";
	public final static String _LICAIDI = "licaidi";
	public final static String _GJJ = "gongjijing";
	public static Map<String, String> logoMap = new HashMap<String, String>();
	static {
		logoMap.put(_9188, LOGO_9188);
		logoMap.put(_AIDUOBAO, LOGO_AIDUOBAO);
		logoMap.put(_GJJ, LOGO_GJJ);
		logoMap.put(_HSK, LOGO_HSK);
		logoMap.put(_LICAIDI, LOGO_LICAIDI);
	}
	public static List<String> CAIYI_PRODUCTS = new ArrayList<String>();
	static {
		CAIYI_PRODUCTS.add(_9188);
		CAIYI_PRODUCTS.add(_HSK);
		CAIYI_PRODUCTS.add(_LICAIDI);
		CAIYI_PRODUCTS.add(_GJJ);
		CAIYI_PRODUCTS.add(_AIDUOBAO);
	}


    /**
     * 使用彩票加密串加密用户登录密码,并设置加密串到bean对象中.
     * @param bean 
     * @param plainPwd 登录密码原文
     * @return 加密后的密码密文
     * @throws Exception
     */
    public String encryptPwd(BaseBean bean, String plainPwd) throws Exception {
    	String privateKey = DEFAULT_MD5_KEY;
    	bean.setPrivateKey(privateKey);
    	return MD5Util.compute(plainPwd + privateKey);
    }

	/**
	 * 判断source值是否在指定的范围内,大于或等于min,小于或等于max
	 * @param source 目标source值
	 * @param min 最小值
	 * @param max 最大值
	 * @return
	 */
	public static boolean isSourceInRange(int source, int min, int max) {
		return source >= min && source <= max;
	}
	
	/**
	 * 根据source判断是否是主站彩票用户
	 */
	public static boolean isWebsiteLotteryUser(BaseBean bean) {
		return bean.getMtype()<=0 && bean.getSource() == 0;
	}
	
	/**
	 * 根据source判断是否是安卓彩票用户
	 */
	public static boolean isAndriodLotteryUser(BaseBean bean) {
		return bean.getMtype() == 1 || isSourceInRange(bean.getSource(), 1000, 1999);
	}
	
	/**
	 * 根据source判断是否是iOS彩票用户
	 */
	public static boolean isIOSLotteryUser(BaseBean bean) {
		return bean.getMtype() == 2 || isSourceInRange(bean.getSource(), 2000, 2999);
	}
	
	/**
	 * 根据source判断是否是触屏彩票用户
	 */
	public static boolean isTouchUser(BaseBean bean) {
		return bean.getMtype() == 4 || isSourceInRange(bean.getSource(), 3000, 3999);
	}
	
	/**
	 * 根据source判断是否是彩票用户
	 */
	public static boolean isLotteryUser(int source) {
		return isSourceInRange(source, 0, 4999);
	}
	
	/**
	 * 根据source判断是否是惠刷卡用户
	 */
	public static boolean isHskUser(int source) {
		return isSourceInRange(source, 5000, 6999);
	}
	
	/**
	 * 根据source判断是否是惠刷卡安卓用户
	 */
	public static boolean isHskAndroidUser(int source) {
		return isSourceInRange(source, 5000, 5999);
	}
	
	/**
	 * 根据source判断是否是惠刷卡iOS用户
	 */
	public static boolean isHskIOSUser(int source) {
		return isSourceInRange(source, 6000, 6499);
	}
	
	/**
	 * 根据source判断是否是惠刷卡PC或H5用户
	 */
	public static boolean isHskPCH5User(int source) {
		return isSourceInRange(source, 6500, 6999);
	}
	
	/**
	 * 根据source判断是否是爱夺宝用户
	 */
	public static boolean isAiduobaoUser(int source) {
		return isSourceInRange(source, 7000, 8999);
	}
	
	/**
	 * 根据source判断是否是理财帝用户
	 */
	public static boolean isLicaidiUser(int source) {
		return isSourceInRange(source, 9000, 9999);
	}
	
	/**
	 * 根据source判断是否是公积金用户
	 */
	public static boolean isGongjijingUser(int source) {
		return (isSourceInRange(source, 10001, 11499) || isSourceInRange(source, 13000, 13100));
	}
	
	/**
	 * 根据source判断是否是记账用户
	 */
	public static boolean isFinancialManageUser(int source) {
		return isSourceInRange(source, 11500, 12999);
	}
	

	public static boolean isNewApp(BaseBean bean, String controlId) {
        boolean isNew = false;
        switch (bean.getMtype()) {
            case CAIYI_ANDROID : {
                isNew = isNewApp(bean.getAppversion(), controlId, "android");
                break;
            }
            case CAIYI_IOS : {
                isNew = isNewApp(bean.getAppversion(), controlId, "ios");
                break;
            }
            case CAIYI_WP : {
                isNew = isNewApp(bean.getAppversion(), controlId, "wp");
                break;
            }
            case CAIYI_TOUCH : {
                isNew = isNewApp(controlId, "touch");
                break;
            }
            default : {
                isNew = false;
            }
        }
        return isNew;
    }
    
    public static boolean isNewApp(String appversion, String controlId, String type) {
		JXmlWrapper node = XmlUtil.getRow("/opt/export/www/cms/news/ad/57.xml", controlId);
        if (node == null || StringUtil.isEmpty(appversion)) {
            return false;
        }
        String baseVersion = node.getStringValue("@" + type);
        boolean isNew = false;
        if ("android".equals(type) && baseVersion.indexOf("~") > 1) {
        	String[] newVersionArr = baseVersion.split(",");
        	for (String newVersion : newVersionArr) {
        		int index = newVersion.indexOf("~");
        		if (index < 1) {
        			continue;
        		}
        		int min = Integer.parseInt(newVersion.substring(0, index));
        		int max = Integer.parseInt(newVersion.substring(index + 1));
        		if (Integer.parseInt(appversion) >= min && Integer.parseInt(appversion) <= max) {
        			isNew = true;
        			break;
        		}
        	}
        } else {
	        if (baseVersion.indexOf(".") > 0) {
	            isNew = appversion.compareTo(baseVersion) >= 0;
	        } else {
	            isNew = Integer.parseInt(appversion) - Integer.parseInt(baseVersion) >= 0;
	        }
        }
        return isNew;
    }
    
    public static boolean isNewApp(String controlId, String type) {
		JXmlWrapper node = XmlUtil.getRow("/opt/export/www/cms/news/ad/57.xml", controlId);
        String flag = node.getStringValue("@" + type);
        return "1".equals(flag);
    }

}
