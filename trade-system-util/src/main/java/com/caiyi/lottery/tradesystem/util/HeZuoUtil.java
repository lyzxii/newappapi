package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.bean.SiteBean;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class HeZuoUtil {
    private static HashMap<String, String> maps = new HashMap<String, String>();
    private static HashMap<String, SiteBean> siteMaps = new HashMap<String, SiteBean>();

    public static void put(String key, String value) {
        maps.put(key, value);
    }

    public static String get(String key) {
        return maps.get(key);
    }

    public static void putSite(String key, SiteBean value) {
        siteMaps.put(key, value);
    }

    public static SiteBean getSite(String key) {
        return siteMaps.get(key);
    }


    public static void getSite() {
        String site = getResource();
        JXmlWrapper sXml = JXmlWrapper.parse(site);

        List<JXmlWrapper> slist = sXml.getXmlNodeList("sites.site");
        try {
            for (JXmlWrapper sxml : slist) {
                SiteBean sb = new SiteBean();
                sb.setHost(sxml.getStringValue("@host"));
                sb.setRegfrom(sxml.getStringValue("@regfrom"));
                sb.setName(sxml.getStringValue("@name"));
                putSite(sb.getHost(), sb);
            }
        } catch (Exception e) {
            log.error("getSite Exception",e);
        }
    }
    public  static  String getResource(){
        ClassLoader classLoader = SiteBean.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream("site.xml");
        String message ="";

        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line = null;
            while((line=br.readLine())!=null){
                message += line;
            }
        } catch (Exception e) {
            log.error("getResource Exception",e);
        }
        return message;
    }


    public static void main(String[] args) throws Exception {
        Integer.valueOf("-1");
        getSite();
    }


}