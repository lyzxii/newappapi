package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.bean.SiteBean;
import com.caiyi.lottery.tradesystem.constants.BaseConstant;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IPUtils {
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("X-Real-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 获取配置文件中的IP信息
	 * 
	 * @param path
	 * @param xmlFile
	 * @return
	 */
	public static Map<String, List<String>> getXmlInfo(String path, String xmlFile) {
		Map<String, List<String>> ipMap = new HashMap<String, List<String>>();
		List<String> proxyIpAddrs = new ArrayList<String>();
		List<String> ipHeaders = new ArrayList<String>();
		List<String> localareaIps = new ArrayList<String>();
		String str = "addr,header,local";
		List<Element> nodes = getTable(path, xmlFile, str);

		for (Element node : nodes) {
			proxyIpAddrs.add(node.getTextTrim());
		}

		for (Element node : nodes) {
			ipHeaders.add(node.getTextTrim());
		}

		for (Element node : nodes) {
			localareaIps.add(node.getTextTrim());
		}

		ipMap.put("proxyIpAddrs", proxyIpAddrs);
		ipMap.put("ipHeaders", ipHeaders);
		ipMap.put("localareaIps", localareaIps);
		return ipMap;
	}

	public static String getRealIp(HttpServletRequest request){
		String path = FileConstant.IP_PROXY_FILE_PATH;
		return getRealIp(request, path, null);
	}
	public static String getRealIp(HttpServletRequest request, String path){
		return getRealIp(request, path, null);
	}

	/**
	 * 获取IP地址(匹配配置文件内容,有则匹配,无则获取当前访问IP)
	 * 
	 * @param request
	 * @return
	 */
	public static String getRealIp(HttpServletRequest request, String path, String xmlFile) {
		try {
			Map<String, List<String>> xmlInfo = getXmlInfo(path, xmlFile);
			List<String> proxyIpAddrs = xmlInfo.get("proxyIpAddrs");
			List<String> ipHeaders = xmlInfo.get("ipHeaders");
			List<String> localareaIps = xmlInfo.get("localareaIps");

			String ipStr = null;
			String[] ipAddrs = null;
			String clientIp = "";

			for (String ipHeader : ipHeaders) {
				ipStr = request.getHeader(ipHeader);
				if (isUnknownIp(ipStr)) {
					continue;
				}
				System.out.println(ipHeader + "=" + ipStr);
				ipAddrs = ipStr.split(",");
				for (String ipAddr : ipAddrs) {
					// 第一个ip就是代理ip则不再使用这个头信息
					if (proxyIpAddrs.contains(ipAddr)) {
						clientIp = "";
						break;
					}
					clientIp = ipAddr;
					// ip是局域网ip则读取同一头信息里的下一个ip
					for (String localareaIp : localareaIps) {
						if (clientIp.startsWith(localareaIp)) {
							clientIp = "";
							break;
						}
					}
					if (!isUnknownIp(clientIp)) {
						break;
					}
				}
				if (!isUnknownIp(clientIp)) {
					break;
				}
			}
			if (isUnknownIp(clientIp)) {
				if (!proxyIpAddrs.contains(request.getRemoteAddr())) {
					clientIp = request.getRemoteAddr();
				}
			}
			return clientIp;
		} catch (Exception e) {
			log.error("ip配置文件地址"+path+xmlFile,e);
			return "";
		}

	}

	/**
	 * 判断是否是未知IP
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isUnknownIp(String ip) {
		return StringUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || "null".equalsIgnoreCase(ip);
	}


	/**
	 * 读取配置文件,获取配置文件中的所有结点
	 * 
	 * @param path
	 *            配置文件存放路径
	 * @param xmlFile
	 *            配置文件名
	 * @param str
	 *            关键词之间用逗号隔开
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getTable(String path, String xmlFile, String str) {
		File file;
		if (StringUtil.isEmpty(xmlFile)) {
			file = new File(path);
		}else {
			file = new File(path, xmlFile);
		}

		if (file != null && file.exists()) {
			try {
				SAXReader reader = new SAXReader();
				Document document = reader.read(file);
				Element root = document.getRootElement();
				String[] strs = str.split(",");
				List<Element> nodes = null;
				for (int i = 0; i < strs.length; i++) {// 同类型标签
					nodes = root.elements(strs[i]);
				}
				return nodes;
			} catch (Exception e) {
				log.error("getTable Exception,path:"+path+" xmlFile:"+xmlFile+" str:"+str,e);
			}
		}
		return null;
	}



	public static int readMaxAccessTimes(){
		String path = FileConstant.IP_PROXY_FILE_PATH;
		return readMaxAccessTimes(path);
	}

	public static int readMaxAccessTimes(String path) {
		File file = new File(path);
		int maxAccessTimes = 0;
		if (file != null && file.exists()) {
			JXmlWrapper proxyIpConfig = JXmlWrapper.parse(file);
			@SuppressWarnings("unchecked")
			List<Element> nodes = proxyIpConfig.getXmlRoot().getChildren("maxAccessTimes");
			if (nodes != null && nodes.size() > 0) {
				maxAccessTimes = Integer.parseInt(nodes.get(0).getTextTrim());
			}
		}
		return maxAccessTimes;
	}

	/**
	 * 获取来源
	 * @param request
	 * @param log
	 * @return
	 */
    public static String getComeFrom(HttpServletRequest request, Logger log) {
        if (request.getSession().getAttribute(BaseConstant.COMFROM) != null) {
			if (log != null) {
				log.info("uid:" + request.getParameter("uid") + " comfrom:" + "" + request.getSession().getAttribute("comfrom"));
			}
			return "" + request.getSession().getAttribute("comfrom");
        }
        String regfrom = null;

        //根据域名写入代理商，没有则按原有流程走
        String host = request.getHeader("Host");
        log.info("uid:" + request.getParameter("uid") + " host:" + host);
        SiteBean sBean = HeZuoUtil.getSite(host);
        if (sBean == null) {
            Cookie cookies[] = request.getCookies();
            Cookie sCookie = null;
            String svalue = null;
            String sname = null;
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    sCookie = cookies[i];
                    svalue = sCookie.getValue();
                    sname = sCookie.getName();
                    if (sname != null && sname.equalsIgnoreCase("regfrom")) {
                        regfrom = svalue;
                    }
                }
            }
        } else {
            if ("cp.duba.com".equals(host)) {
                Cookie cookies[] = request.getCookies();
                Cookie sCookie = null;
                String svalue = null;
                String sname = null;
                if (cookies != null) {
                    for (int i = 0; i < cookies.length; i++) {
                        sCookie = cookies[i];
                        svalue = sCookie.getValue();
                        sname = sCookie.getName();
                        if (sname != null && sname.equalsIgnoreCase("regfrom")) {
                            regfrom = svalue;
                        }
                    }
                }
                if (!"dubaad".equals(regfrom)) {
                    regfrom = sBean.getRegfrom();
                }

            } else {
                regfrom = sBean.getRegfrom();
            }
        }
        if (host.endsWith(".qq.shanghaicaiyi.com") || host.endsWith(".vip.shanghaicaiyi.com") || host.endsWith(".agent.shanghaicaiyi.com") || host.endsWith(".v5.shanghaicaiyi.com")
                || host.endsWith(".qq.9188.com") || host.endsWith(".vip.9188.com") || host.endsWith(".agent.9188.com") || host.endsWith(".v5.9188.com")) {
            String agent = host.replace(".qq.shanghaicaiyi.com", "").replace(".vip.shanghaicaiyi.com", "").replace(".agent.shanghaicaiyi.com", "").replace(".v5.shanghaicaiyi.com", "")
                    .replace(".qq.9188.com", "").replace(".vip.9188.com", "").replace(".agent.9188.com", "").replace(".v5.9188.com", "");
            agent = agent.lastIndexOf(".") > -1 ? agent.substring(agent.lastIndexOf(".") + 1) : agent;
            if (host.endsWith(".v5.shanghaicaiyi.com") || host.endsWith(".v5.9188.com")) {
                log.info("代理商id:" + agent + " 用户名:" + request.getParameter("uid") + " refrom:" + regfrom);
            }
            if (agent.matches("[0-9a-zA-Z]+")) {
                regfrom = agent;
            }
        }

        if (regfrom == null || regfrom.length() == 0) {
            regfrom = "normal";
        }
        return regfrom;
    }


	public static void main(String[] args) {
		getRealIp(null);
	}
}
