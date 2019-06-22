package com.caiyi.lottery.tradesystem.util;



import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import org.jdom.Attribute;
import org.jdom.Element;
import org.slf4j.Logger;

import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import com.caiyi.lottery.tradesystem.BaseBean;

public class ParseGeneralRulesUtil {
	public static boolean parseGeneralRules(JXmlWrapper generalRules, BaseBean bean) {
		//获取content下的source，进行解析
		JXmlWrapper source = generalRules.getXmlNode("source");
		boolean sourceFlag = parseSource(source,bean);
		//根据手机平台进行分类
		int mtype = bean.getMtype();
		String mobileType = "andriod";
		if(1==mtype){
			mobileType = "andriod";
		}else if(2==mtype){
			mobileType = "ios";
		}else if(4==mtype){
			mobileType = "h5";
		}
		//对app版本进行解析
		JXmlWrapper appversion = generalRules.getXmlNode(mobileType+"-appversion"); 
		boolean appversionFlag = parseAppversion(appversion,bean);
		
		//对系统版本进行解析
		JXmlWrapper osversion = generalRules.getXmlNode(mobileType+"-osversion");
		boolean osversionFlag = parseOsversion(osversion,bean);
		
		//对白名单进行解析
		JXmlWrapper whiteList = generalRules.getXmlNode("whitelist");
		boolean whiteListFlag = parseWhiteList(whiteList,bean);
		
		//对ip进行解析
		JXmlWrapper ip = generalRules.getXmlNode("ip");	
//		String remoteIp = IPUtils.getIpAddr(request);新项目remoteIp在web设置
//		bean.setIpAddr(remoteIp);
//		logger.info("请求的设备类型为:"+mobileType+" ip==:"+bean.getIpAddr()+"  uid=="+bean.getUid());
		boolean ipFlag = parseIp(ip,bean);
		
		boolean showflag = sourceFlag & appversionFlag & osversionFlag & whiteListFlag & ipFlag;
		
		return showflag;
	}
	
	//不带request的解析
	public static boolean parseGeneralRulesNew(JXmlWrapper generalRules, BaseBean bean, Logger logger) {
		//获取content下的source，进行解析
		JXmlWrapper source = generalRules.getXmlNode("source");
		boolean sourceFlag = parseSource(source,bean);
		//根据手机平台进行分类
		int mtype = bean.getMtype();
		String mobileType = "andriod";
		if(1==mtype){
			mobileType = "andriod";
		}else if(2==mtype){
			mobileType = "ios";
		}else if(4==mtype){
			mobileType = "h5";
		}
		//对app版本进行解析
		JXmlWrapper appversion = generalRules.getXmlNode(mobileType+"-appversion"); 
		boolean appversionFlag = parseAppversion(appversion,bean);
		
		//对系统版本进行解析
		JXmlWrapper osversion = generalRules.getXmlNode(mobileType+"-osversion");
		boolean osversionFlag = parseOsversion(osversion,bean);
		
		//对白名单进行解析
		JXmlWrapper whiteList = generalRules.getXmlNode("whitelist");
		boolean whiteListFlag = parseWhiteList(whiteList,bean);
		
		//对ip进行解析
		JXmlWrapper ip = generalRules.getXmlNode("ip");	
//		logger.info("请求的设备类型为:"+mobileType+" ip==:"+bean.getIpAddr()+"  uid=="+bean.getUid());
		boolean ipFlag = parseIp(ip,bean);
		
		boolean showflag = sourceFlag & appversionFlag & osversionFlag & whiteListFlag & ipFlag;
		
		return showflag;
	}
	
	/*
	 * 对单个条件进行判断
	 */
	public static boolean parseSingleRule(JXmlWrapper generalRules, BaseBean bean, HttpServletRequest request, Logger logger, String singleRule) {
		//根据手机平台进行分类
		int mtype = bean.getMtype();
		String mobileType = "andriod";
		if(1==mtype){
			mobileType = "andriod";
		}else if(2==mtype){
			mobileType = "ios";
		}else if(4==mtype){
			mobileType = "h5";
		}
		if("source".equals(singleRule)){
			//获取content下的source，进行解析
			JXmlWrapper source = generalRules.getXmlNode("source");
			boolean sourceFlag = parseSource(source,bean);
			return sourceFlag;
		}else if("appversion".equals(singleRule)){
			//对app版本进行解析
			JXmlWrapper appversion = generalRules.getXmlNode(mobileType+"-appversion"); 
			boolean appversionFlag = parseAppversion(appversion,bean);
			return appversionFlag;
		}else if("osversion".equals(singleRule)){
			//对系统版本进行解析
			JXmlWrapper osversion = generalRules.getXmlNode(mobileType+"-osversion");
			boolean osversionFlag = parseOsversion(osversion,bean);
			return osversionFlag;
		}else if("whitelist".equals(singleRule)){
			//对白名单进行解析
			JXmlWrapper whiteList = generalRules.getXmlNode("whitelist");
			boolean whiteListFlag = parseWhiteList(whiteList,bean);
			return whiteListFlag;
		}else if("ip".equals(singleRule)){
			//对ip进行解析
			JXmlWrapper ip = generalRules.getXmlNode("ip");	
			String remoteIp = IPUtils.getIpAddr(request);
			bean.setIpAddr(remoteIp);
			logger.info("请求的设备类型为:"+mobileType+" ip==:"+bean.getIpAddr()+"  uid=="+bean.getUid());
			boolean ipFlag = parseIp(ip,bean);
			return ipFlag;
		}

		return false;
	}
	
	/**
	 * 
	 * @param content 需要写入builder的xmlNode
	 * @param builder 
	 * @param keyArr 需要写入的属性值的key
	 */
	public static void writeToBuilder(JXmlWrapper content, String nodeName, StringBuilder builder, String[] keyArr) {
	    Element element = content.getXmlRoot();
	    builder.append("<"+nodeName+" ");
	    for(String key : keyArr){
	    	Attribute attribute = element.getAttribute(key);
	    	if(null == attribute){
	    		continue;
	    	}
	    	String attrName = attribute.getName();
	    	String attrVal = attribute.getValue();
	    	XmlUtil.append(builder, attrName, attrVal);
	    }
	    builder.append(" />");
	}
	
	/**
	 * 
	 * @param content 需要写入builder的xmlNode
	 * @param nodeName 生成的节点名称
	 * @param builder
	 * @param keyArr 需要写入的属性值的key
	 * @param extras 额外需要写入的内容
	 */
	public static void writeToBuilder(JXmlWrapper content, String nodeName, StringBuilder builder, String[] keyArr, Map<String,String> extras) {
	    Element element = content.getXmlRoot();
	    builder.append("<"+nodeName+" ");
	    for(String key : keyArr){
	    	Attribute attribute = element.getAttribute(key);
	    	if(null == attribute){
	    		continue;
	    	}
	    	String attrName = attribute.getName();
	    	String attrVal = attribute.getValue();
	    	XmlUtil.append(builder, attrName, attrVal);
	    }
	    for(String key : extras.keySet()){
	    	String value = extras.get(key);
	    	XmlUtil.append(builder, key, value);
	    }
	    builder.append(" />");
	}
	
	public static void writeToBuilder(JXmlWrapper content, String nodeName, StringBuilder builder) {
	    Element element = content.getXmlRoot();
	    List<Attribute> attrList = element.getAttributes();
	    builder.append("<"+nodeName+" ");
	   for(Attribute attr : attrList){
		   String attrName = attr.getName();
		   String attrValue = attr.getValue();
		   XmlUtil.append(builder, attrName, attrValue);
	   }
	    builder.append(" />");
	}

	private static boolean parseIp(JXmlWrapper ip, BaseBean bean) {
		String interval = ip.getStringValue("@interval");
		String single = ip.getStringValue("@single");
		
	 	String visible = ip.getStringValue("@visible");
		
		if(StringUtil.isEmpty(interval.trim())&&StringUtil.isEmpty(single.trim())){
			//判断是否是可显示的
		 	if("N".equals(visible.trim())){
		 		return false;
		 	}
			return true;
		}
		
		String ipVal = bean.getIpAddr();
		
		boolean intervalFlag = parseIntervalIp(interval,ipVal);
		boolean singleFlag = parseSingleIp(single,ipVal);
		
		boolean flag = intervalFlag | singleFlag;
		
		//判断是否是可显示的
	 	if("N".equals(visible.trim())){
	 		flag = !flag;
	 	}
		
		return flag;
		
	}
	
	
	private static boolean parseWhiteList(JXmlWrapper whiteList, BaseBean bean) {
		String interval = whiteList.getStringValue("@interval");
		String single = whiteList.getStringValue("@single");
		
		String visible = whiteList.getStringValue("@visible");
		
		if(StringUtil.isEmpty(interval.trim())&&StringUtil.isEmpty(single.trim())){
			if("N".equals(visible.trim())){
		 		return false;
		 	}
			return true;
		}
		
		int whiteListVal = bean.getWhitelistGrade();
		
		boolean intervalFlag = parseInterval(interval, String.valueOf(whiteListVal));
		boolean singleFlag = parseSingle(single, String.valueOf(whiteListVal));
		boolean flag = intervalFlag | singleFlag;
		
		//判断是否是可显示的
	 	if("N".equals(visible.trim())){
	 		flag = !flag;
	 	}
		
		return flag;
	}

	private static boolean parseAppversion(JXmlWrapper appversion, BaseBean bean) {
		String interval = appversion.getStringValue("@interval").replace(".", "");
		String single = appversion.getStringValue("@single").replace(".", "");
		
		String visible = appversion.getStringValue("@visible");
		
		//两个范围都为空时，表示所有条件都符合,只有一个条件有值时，只根据有条件的进行判断
		if(StringUtil.isEmpty(interval.trim())&&StringUtil.isEmpty(single.trim())){
			if("N".equals(visible.trim())){
		 		return false;
		 	}
			return true;
		}
		
		String appversionVal = bean.getAppversion().replace(".", "");
		
		boolean intervalFlag = parseInterval(interval, appversionVal);
		boolean singleFlag = parseSingle(single, appversionVal);
		boolean flag = intervalFlag | singleFlag;
		
		//判断是否是可显示的
	 	if("N".equals(visible.trim())){
	 		flag = !flag;
	 	}
		
		return flag;
	}
	
	private static boolean parseOsversion(JXmlWrapper osversion, BaseBean bean) {
		String interval = osversion.getStringValue("@interval").replace(".", "");
		String single = osversion.getStringValue("@single").replace(".", "");
		
		String visible = osversion.getStringValue("@visible");
		
		if(StringUtil.isEmpty(interval.trim())&&StringUtil.isEmpty(single.trim())){
			if("N".equals(visible.trim())){
		 		return false;
		 	}
			return true;
		}
		
		String osversionVal = bean.getOsversion().replace(".", "");
		//如果系统版本号非纯数字,直接通过
		if(!isNum(osversionVal)){
			return true;
		}
		
		boolean intervalFlag = parseInterval(interval, osversionVal);
		boolean singleFlag = parseSingle(single, osversionVal);
		boolean flag = intervalFlag | singleFlag;
		
		//判断是否是可显示的
	 	if("N".equals(visible.trim())){
	 		flag = !flag;
	 	}
		
		return flag;
	}

	public static boolean parseSource(JXmlWrapper source,BaseBean bean) {
		//获取判断条件
		String interval = source.getStringValue("@interval");
		String single = source.getStringValue("@single");
		
		String visible = source.getStringValue("@visible");
		
		if(StringUtil.isEmpty(interval.trim())&&StringUtil.isEmpty(single.trim())){
			if("N".equals(visible.trim())){
		 		return false;
		 	}
			return true;
		}
		
		//获取需判断值
		String sourceStr = String.valueOf(bean.getSource());
		
		
		boolean intervalFlag = parseInterval(interval,sourceStr);
	 	boolean singleFlag = parseSingle(single,sourceStr);
	 	//是否在范围内
	 	boolean flag = intervalFlag | singleFlag;
		
	 	//判断是否是可显示的
	 	if("N".equals(visible.trim())){
	 		flag = !flag;
	 	}
	 	
	 	return flag;
	}
	
	private static boolean parseSingle(String single, String source) {
		if(StringUtil.isEmpty(single)){
			return false;
		}
		String[] singleArr = single.split(",");
		for(int i=0 ;i<singleArr.length ;i++){
			if(singleArr[i].trim().equals(source.trim())){
				return true;
			}
		}
		
		return false;
	}

	private static boolean parseInterval(String interval, String source) {
		//判断区间是否为空，如果为空则不通过
		if(StringUtil.isEmpty(interval)){
			return false;
		}
		String[] intervalArr = interval.split(",");
		for(int i=0;i<intervalArr.length;i++){
			if(intervalArr[i].contains("~")){
				//只要为真即通过
				if(parseBetween(intervalArr[i], source)){
					return true;
				}
			}else if(intervalArr[i].contains(">=")){
				
				int result = checkLessOrGreater(intervalArr[i]);
				//0表示实际为<=,1表示为实际是>=
				if(result == 0){
					if(parseLessEquals(intervalArr[i], source)){
						return true;
					}
				}else{
					if(parseGreaterEquals(intervalArr[i], source)){
						return true;
					}
				}
			}else{
				return false;
			}
		}
		return false;
	}
	
	private static boolean parseSingleIp(String single, String realIp) {
		if(StringUtil.isEmpty(single)){
			return false;
		}
		long ipVal = convertIp(realIp);
		String[] ipArr = single.split(",");
		for(int i=0;i<ipArr.length;i++){
			long ipCondition = convertIp(ipArr[i]);
			if(ipVal==ipCondition){
				return true;
			}
		}
		return false;
	}


	private static boolean parseIntervalIp(String interval, String realIp) {
		if(StringUtil.isEmpty(interval)){
			return false;
		}
		long ipVal = convertIp(realIp);
		String[] ipArr = interval.split(",");
		for(int i=0;i<ipArr.length;i++){
			if(ipArr[i].contains("~")){
				String ipInterval[] = ipArr[i].split("~");
				long minIp = convertIp(ipInterval[0]);
				long maxIp = convertIp(ipInterval[1]);
				if(checkBetween(minIp, maxIp, ipVal)){
					return true;
				}
			}else if(ipArr[i].contains(">=")){
				int index = ipArr[i].trim().indexOf(">=");
				if(index>0){//表示>=前面含有数字，实际表示<=
					String maxIpStr = ipArr[i].substring(0,index);
					long maxIp = convertIp(maxIpStr);
					if(checkLessEquals(maxIp , ipVal)){
						return true;
					}
				}else{
					String minIpStr = ipArr[i].substring(index+2);
					long minIp = convertIp(minIpStr);
					if(checkGreaterEquals(minIp , ipVal)){
						return true;
					}
				}
			}else{
				return false;
			}
		}
		return false;
	}

	private static int checkLessOrGreater(String interval) {
		int index = interval.trim().indexOf(">=");
		//角标大于0表示>=前面还有数字，实际表示为<=
		if(index > 0){
			return 0;
		}
		return 1;
	}
	
	public static boolean checkLessEquals(long max,long val){
		if(val<=max){
			return true;
		}
		return false;
	}
	
	public static boolean parseLessEquals(String interval,String content){
		int index = interval.indexOf(">=");
		String intervalStr = interval.substring(0,index);
		long intervalVal = Long.parseLong(intervalStr.trim());
		if(StringUtil.isEmpty(content.trim())){
			return false;
		}
		long contentVal = Long.parseLong(content.trim());
		
		return checkLessEquals(intervalVal, contentVal);
	}
	
	public static boolean checkGreaterEquals(long min,long val){
		if(val>=min){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean parseGreaterEquals(String interval,String content){
		int index = interval.indexOf(">=");
		String intervalStr = interval.substring(index+2);
		long intervalVal = Long.parseLong(intervalStr.trim());
		//如果不是所有全部通过,内容为空,则不返回
		if(StringUtil.isEmpty(content.trim())){
			return false;
		}
		long contentVal = Long.parseLong(content.trim());
		return checkGreaterEquals(intervalVal, contentVal);
	}
	
	public static boolean checkBetween(long min, long max, long val){
		
		if(min<=val && val<=max){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean parseBetween(String interval,String content){
		String[] range = interval.split("~");
		String minStr = range[0];
		long min = Long.parseLong(minStr.trim()); 
		String maxStr = range[1];
		long max = Long.parseLong(maxStr.trim());
		
		if(StringUtil.isEmpty(content.trim())){
			return false;
		}
		long contentVal = Long.parseLong(content.trim());
		return checkBetween(min, max, contentVal);
	}
	
	private static long convertIp(String ip) {
		String ipParse = ip.trim();
		String[] ipArr = ipParse.split("\\.");
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<ipArr.length;i++){
			while(ipArr[i].trim().length()!=3){
				ipArr[i] = 0 + ipArr[i];
			}
			builder.append(ipArr[i]);
		}
		return Long.parseLong(builder.toString());
	}	
	
	public static boolean parseGeneralRulesH5(JXmlWrapper generalRules, BaseBean bean, HttpServletRequest request, Logger logger) {
		//获取content下的source，进行解析
		JXmlWrapper source = generalRules.getXmlNode("source");
		boolean sourceFlag = parseSource(source,bean);
		
		//对白名单进行解析
		JXmlWrapper whiteList = generalRules.getXmlNode("whitelist");
		boolean whiteListFlag = parseWhiteList(whiteList,bean);
		
		//对ip进行解析
		JXmlWrapper ip = generalRules.getXmlNode("ip");	
		String remoteIp = IPUtils.getIpAddr(request);
		bean.setIpAddr(remoteIp);
		logger.info("请求的设备类型为:H5 ip==:"+bean.getIpAddr()+"  uid=="+bean.getUid());
		boolean ipFlag = parseIp(ip,bean);
		
		boolean showflag = sourceFlag  & whiteListFlag & ipFlag;
		
		return showflag;
	}
	
	//判断是否是数字
	public final static boolean isNum(String content) {
		String str = "^[0-9]*$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(content);
		return m.matches();
	}
}
