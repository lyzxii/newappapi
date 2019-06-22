package com.caiyi.lottery.tradesystem.util;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseUtil {

    /**
     * 判断source值是否在指定的范围内,大于或等于min,小于或等于max
     *
     * @param source 目标source值
     * @param min    最小值
     * @param max    最大值
     * @return
     */
    public static boolean isSourceInRange(int source, int min, int max) {
        return source >= min && source <= max;
    }

    /**
     * 根据source判断是否是主站彩票用户
     */
    public static boolean isWebsiteLotteryUser(BaseBean bean) {
        return bean.getMtype() <= 0 && bean.getSource() == 0;
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
        return isSourceInRange(source, 10001, 11499);
    }

    /**
     * 根据source判断是否是记账用户
     */
    public static boolean isFinancialManageUser(int source) {
        return isSourceInRange(source, 11500, 12999);
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
     * 读取东方网用户source值.
     */
    public static List<String> getEastdaySource(String path) {
        List<String> sourceList = new ArrayList<String>();
        File file = new File(path);
        String sourceStr = null;
        if (file != null && file.exists()) {
            JXmlWrapper xml = JXmlWrapper.parse(file);
            sourceStr = xml.getStringValue("row.@source");
        }
        if (!StringUtil.isEmpty(sourceStr)) {
            sourceList.addAll(Arrays.asList(sourceStr.split(",")));
        }
        return sourceList;
    }
    
    /**
     * 判断请求来源是不是华龙网包
     * @param source 华龙网安卓source为1398和1389,iOS为2214
     */
    public static boolean isHualong(int source) {
		return source == 1398 || source == 1389 || source == 2214;
	}

    public   static  boolean isH5User(int source) {
        return isSourceInRange(source, 3000, 3999);
    }
    
    
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
	
	
    
    /**
     * 判断客户端版本
     * @param bean
     * @param controlId
     * @return
     */
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

    public static Boolean checkTimeOrCode(BaseBean bean) {
        if (GameContains.canNotUse(bean.getGid())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("不支持的彩种");
            return false;
        }
        if (!StringUtil.isEmpty(bean.getStime())) {
            if (!DateTimeUtil.checkDate(bean.getStime(), "yyyy-MM-dd")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("起始日期格式错误");
                return false;
            }
        }
        if (!StringUtil.isEmpty(bean.getEtime())) {
            if (!DateTimeUtil.checkDate(bean.getEtime(), "yyyy-MM-dd")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("终止日期格式错误");
                return false;
            }
        }
        return true;
    }
}
