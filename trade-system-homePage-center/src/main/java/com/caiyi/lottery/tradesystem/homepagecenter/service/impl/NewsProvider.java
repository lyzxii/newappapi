package com.caiyi.lottery.tradesystem.homepagecenter.service.impl;

import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;

/**
 * @author wxy
 * @create 2018-01-15 19:17
 **/
@Slf4j
public class NewsProvider {
    @Autowired
    private RedisClient redisClient;
    /**
     * html5预测页面获取预测和资讯
     *
     * @param pn
     *            页码
     * @return
     */
    public static String getHtml5ForeCastsAndNews(int pn) {
        StringBuilder resp = new StringBuilder();
        resp.append(XmlUtil.XML_HEAD);
        StringBuilder xmlPath = null;
        StringBuilder rows = new StringBuilder();
        for (String lot : XmlUtil.YUCELOT_HTML5) {
            xmlPath = new StringBuilder();
            xmlPath.append(XmlUtil.YUCEROOT);
            xmlPath.append(lot);
            xmlPath.append("/list_");
            xmlPath.append(XmlUtil.LOTTID.get(lot));
            xmlPath.append("_");
            xmlPath.append(pn);
            xmlPath.append(".xml");
            rows.append(readListXmlFirst(xmlPath));
        }

        StringBuilder hotNews = new StringBuilder();
        hotNews.append(XmlUtil.HOTNEWSROOT);
        hotNews.append("list_114_");
        hotNews.append(pn);
        hotNews.append(".xml");
        rows.append(readListXmlFirst(hotNews));

        if (rows.indexOf("<row") > -1) {
            resp.append("<Resp code=\"0\" desc=\"获取成功\">");
            resp.append("<rows>");
            resp.append(rows);
            resp.append("</rows>");
        } else {
            resp.append("<Resp code=\"-1\" desc=\"获取失败\">");
        }
        resp.append("</Resp>");
        return resp.toString();
    }
    /**获取各彩种最新一条预测文章标题.
     * @param pn 页码
     * @param source 客户端source值
     * @return
     */
    public static String getAllForecast(int pn, int source) {
        // 华龙彩票不显示双色球分析预测文章
        boolean isHualong = isHualong(source);
        StringBuilder resp = new StringBuilder();
        resp.append(XmlUtil.XML_HEAD);
        StringBuilder xmlPath = null;
        StringBuilder rows = new StringBuilder();
        for (String lot : XmlUtil.YUCELOT) {
            if (isHualong && "ssq".equals(lot)) {
                continue;
            }
            xmlPath = new StringBuilder();
            xmlPath.append(XmlUtil.YUCEROOT);
            xmlPath.append(lot);
            xmlPath.append("/list_");
            xmlPath.append(XmlUtil.LOTTID.get(lot));
            xmlPath.append("_");
            xmlPath.append(pn);
            xmlPath.append(".xml");
            rows.append(readLatestArticleList(xmlPath.toString()));
        }
        if (rows.indexOf("<row") > -1) {
            resp.append("<Resp code=\"0\" desc=\"获取成功\">");
            resp.append("<rows>");
            resp.append(rows);
            resp.append("</rows>");
        } else {
            resp.append("<Resp code=\"-1\" desc=\"获取失败\">");
        }
        resp.append("</Resp>");
        return resp.toString();
    }

    private static String readLatestArticleList(String path) {
        File xmlFile = new File(path);
        if (xmlFile == null || !xmlFile.exists()) {
            return "";
        }
        JXmlWrapper wapper = JXmlWrapper.parse(xmlFile);
        Element root = wapper.getXmlRoot();
        @SuppressWarnings("rawtypes")
        List eles = root.getChildren();
        Element ele = null;
        StringBuilder row = new StringBuilder();
        for (Object obj : eles) {
            ele = (Element) obj;
            if ("rows".equals(ele.getName())) {
                Element firstRow = (Element) ele.getChildren().get(0);
                String title = firstRow.getAttributeValue("ntitle");
                String gid = XmlUtil.GIDMAP.get(firstRow
                                                        .getAttributeValue("gid"));
                String lotteryName = XmlUtil.CATEGORY.get(gid);
                row.append("<row name=\"");
                row.append(lotteryName);
                row.append("\" gid=\"");
                row.append(gid);
                row.append("\" title=\"");
                row.append(title);
                row.append("\" />");
                break;
            }
        }
        return row.toString();
    }

    private static String readListXmlFirst(StringBuilder xmlPath) {
        File xmlFile = new File(xmlPath.toString());
        if (xmlFile == null || !xmlFile.exists()) {
            return "";
        }
        JXmlWrapper wapper = JXmlWrapper.parse(xmlFile);
        Element root = wapper.getXmlRoot();
        @SuppressWarnings("rawtypes")
        List eles = root.getChildren();
        Element ele = null;
        StringBuilder row = new StringBuilder();
        for (Object obj : eles) {
            ele = (Element) obj;
            if ("rows".equals(ele.getName())) {
                Element firstRow = (Element) ele.getChildren().get(0);
                String title = firstRow.getAttributeValue("ntitle");
                String arcurl = firstRow.getAttributeValue("arcurl");
                String gid = XmlUtil.GIDMAP.get(firstRow
                                                        .getAttributeValue("gid"));
                String lotteryName = XmlUtil.CATEGORY.get(gid);
                row.append("<row name=\"");
                row.append(lotteryName);
                row.append("\" gid=\"");
                row.append(gid);
                row.append("\" title=\"");
                row.append(title);
                row.append("\" arcurl=\"");
                row.append(arcurl);
                row.append("\" />");
                break;
            }
        }
        return row.toString();
    }
    /**
     * 判断请求来源是不是华龙网包
     * @param source 华龙网安卓source为1398和1389,iOS为2214
     */
    public static boolean isHualong(int source) {
        return source == 1398 || source == 1389 || source == 2214;
    }

    /** 获取指定彩种预测分析文章列表
     * @param gid
     * @param pn
     * @return
     */
    public static String getForecastList(String gid, int pn) {
        String lot = XmlUtil.GIDPATH.get(gid);
        StringBuilder xmlPath = new StringBuilder();
        xmlPath.append(XmlUtil.YUCEROOT);
        xmlPath.append(lot);
        xmlPath.append("/list_");
        xmlPath.append(XmlUtil.LOTTID.get(lot));
        xmlPath.append("_");
        xmlPath.append(pn);
        xmlPath.append(".xml");
        return readArticleList(xmlPath.toString());
    }

    public static String readArticleList(String path) {
        char[] buffer = new char[4096];
        File xml = new File(path.toString());
        if (xml == null || !xml.exists()) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Resp code=\"-1\" desc=\"文件不存在\"></Resp>";
        }
        StringBuilder resp = new StringBuilder();
        InputStream inStream = null;
        InputStreamReader inReader = null;
        try {
            inStream = new FileInputStream(xml);
            // 过滤文件bom头
            BOMInputStream bomIn = new BOMInputStream(inStream, false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE);
            inReader = new InputStreamReader(bomIn, "UTF-8");
            int count = inReader.read(buffer);
            while (count > 0) {
                resp.append(buffer, 0, count);
                count = inReader.read(buffer);
            }

        } catch (IOException e) {
            log.error("读取预测文件失败", e);
            resp.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Resp code=\"-1\" desc=\"查询失败\"></Resp>");
        } finally {
            if (inReader != null) {
                try {
                    inReader.close();
                } catch (IOException e) {
                    log.error("文件关闭失败", e);
                }
            }
        }
        return resp.toString();
    }

    /**
     * @param pn 页码
     * @param source 客户端source值
     * @return
     */
    public static String getHotNewsList(int pn, int source) {
        // 华龙彩票不显示双色球分析预测文章
        boolean isHualong = isHualong(source);
        if (isHualong) {
            return new NewsProvider().getHualongHotNewsList(pn);
        } else {
            StringBuilder hotNews = new StringBuilder();
            hotNews.append(XmlUtil.HOTNEWSROOT);
            hotNews.append("list_114_");
            hotNews.append(pn);
            hotNews.append(".xml");
            return readArticleList(hotNews.toString());
        }
    }

    private String getHualongHotNewsList(int pn) {
        String keyPrefix = "hotNewsList_";
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(keyPrefix + "updateTime");
        String value = redisClient.getString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
        long cacheUpdateTime = 0;
        if (value != null) {
            cacheUpdateTime = Long.parseLong(value);
        }
        // 当缓存中没有上次更新文章时间或缓存中文章不是最新文章时从磁盘重新读取文章
        boolean readFromDisk = true;
        if (cacheUpdateTime > 0) {
            long fileUpdateTime = getHotNewsFileUpdateTime();
            if (fileUpdateTime < cacheUpdateTime) {
                readFromDisk = false;
            }
        }
        String resp = null;
        if (readFromDisk) {
            loadHualongHotNewsListFromDisk(keyPrefix);
        }
        resp = getHualongHotNewsListFromCache(keyPrefix, pn);
        return resp;
    }

    private static long getHotNewsFileUpdateTime() {
        File file = new File(XmlUtil.HOTNEWSROOT, "list_114_1.xml");
        return file.lastModified();
    }

    private void loadHualongHotNewsListFromDisk(String keyPrefix) {
        int totalPage = getTotalPage();
        StringBuilder path = null;
        Map<String, List<JXmlWrapper>> newsMap = new HashMap<String, List<JXmlWrapper>>();
        List<JXmlWrapper> newsList = null;
        int newPage = 0;
        int newTotalSize = 0;
        for (int page = 1; page <= totalPage; page++) {
            path = new StringBuilder();
            path.append(XmlUtil.HOTNEWSROOT);
            path.append("list_114_");
            path.append(page);
            path.append(".xml");
            JXmlWrapper xml = JXmlWrapper.parse(new File(path.toString()));
            List<JXmlWrapper> rowList = xml.getXmlNode("rows").getXmlNodeList("row");
            for (JXmlWrapper row : rowList) {
                if ("01".equals(row.getStringValue("@gid"))) {
                    continue;
                }
                newTotalSize++;
                if (newsList == null || newsList.size() == 10) {
                    newPage++;
                    newsList = new ArrayList<JXmlWrapper>(10);
                    newsList.add(row);
                    newsMap.put(keyPrefix + newPage, newsList);
                } else {
                    newsList = newsMap.get(keyPrefix + newPage);
                    newsList.add(row);
                }
            }
        }
        Set<String> keySet = newsMap.keySet();
        StringBuilder newsContent = null;
        CacheBean cacheBean;
        for (String key : keySet) {
            newsContent = new StringBuilder();
            newsList = newsMap.get(key);
            for (JXmlWrapper row : newsList) {
                newsContent.append(row.toXmlString("utf-8").replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "").replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
            }
            cacheBean = new CacheBean();
            cacheBean.setKey(key);
            cacheBean.setValue(newsContent.toString());
            cacheBean.setTime(86400000);
            redisClient.setString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
        }
        cacheBean = new CacheBean();
        cacheBean.setKey(keyPrefix + "totalsize");
        cacheBean.setValue(newTotalSize + "");
        cacheBean.setTime(86400000);
        redisClient.setString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);

        cacheBean = new CacheBean();
        cacheBean.setKey(keyPrefix + "totalpage");
        cacheBean.setValue(newPage + "");
        cacheBean.setTime(86400000);
        redisClient.setString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);

        cacheBean = new CacheBean();
        cacheBean.setKey(keyPrefix + "updateTime");
        cacheBean.setValue(getHotNewsFileUpdateTime() + "");
        cacheBean.setTime(86400000);
        redisClient.setString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
    }

    private String getHualongHotNewsListFromCache(String keyPrefix, int pn) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(keyPrefix + pn);
        String value = redisClient.getString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
        StringBuilder resp = new StringBuilder();
        if (value == null) {
            resp.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Resp code=\"-1\" desc=\"查询失败\"></Resp>");
        } else {
            resp.append("<Resp code=\"0\" desc=\"获取成功\">");
            resp.append("<rows>");
            resp.append(value);
            resp.append("</rows>");
            resp.append("<pagelist ");
            resp.append(JXmlUtil.createAttrXml("pageno", String.valueOf(pn)));
            cacheBean = new CacheBean();
            cacheBean.setKey(keyPrefix + "totalpage");
            resp.append(JXmlUtil.createAttrXml("totalpage", redisClient.getString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER)));

            cacheBean = new CacheBean();
            cacheBean.setKey(keyPrefix + "totalsize");
            resp.append(JXmlUtil.createAttrXml("totalsize", redisClient.getString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER)));
            resp.append("/>");
            resp.append("</Resp>");
        }
        return resp.toString();
    }

    private static int getTotalPage() {
        JXmlWrapper xml = JXmlWrapper.parse(new File(XmlUtil.HOTNEWSROOT, "list_114_1.xml"));
        JXmlWrapper pageList = xml.getXmlNode("pagelist");
        return pageList.getIntValue("@totalpage");
    }
}
