package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import java.io.File;
import java.util.List;

/**
 * 获取彩种logo
 * 每次获取logourl都要解析文件，存缓存操作放到业务代码中
 * @create 2018-1-4 16:47:09
 */
public class LotteryLogoUtil {

    private static final String logoPath="/opt/export/www/cms/news/ad/lottery_logo.xml";

    public static String addLogo(String xml, String gid) throws Exception {
            String url = getLotteryLogo(gid);
            JXmlWrapper xmlw= JXmlWrapper.parse(xml);
            if (!StringUtil.isEmpty(url)) {
                xmlw.addStringValue("@logoUrl",url);
            }
            return xmlw.toXmlString("UTF-8");
    }

    /**
     * 从配置文件获取彩种logo url
     * @param gid 彩种id
     * @return
     */
    public static String getLotteryLogo(String gid) throws Exception {
            String logoUrl;
            JXmlWrapper xml = JXmlWrapper.parse(new File(logoPath));
            JXmlWrapper logoList = xml.getXmlNode("logoList");
            String visiable = logoList.getStringValue("@visiable");
            String id;
            if ("Y".equals(visiable)) {
                List<JXmlWrapper> list = logoList.getXmlNodeList("logo");
                for (JXmlWrapper row : list) {
                    visiable = row.getStringValue("@visiable");
                    if ("Y".equals(visiable)) {
                        id = row.getStringValue("@gid");
                        if (id.trim().equals(gid.trim())) {
                            logoUrl=row.getStringValue("@linkimg");
                            return logoUrl;
                        }
                    }
                }
            } else {
                // logger.info("配置文件 lottery_logo.xml 中 logoList 节点 visiable 设置为
                // N,不显示所有彩种logo");
                return logoList.getStringValue("@defaulturl");
            }
        return null;
    }
}
