package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import org.slf4j.Logger;

/**
 * @author wxy
 * @create 2018-04-23 17:55
 **/
public class MatchUtils {
    private static final String ONEROUND_FILE_URL = "http://mobile.9188.com/qtjsbf/jc/oneroundtitledata/";

    /**
     * 通过itemid取得主客队球探的比赛id
     * @param itemId
     * @param log
     * @return
     */
    public static String[] getTeamId(String itemId, Logger log) {
        String rul = ONEROUND_FILE_URL + itemId + ".xml";
        String teamIds[] = new String[2];
        try {
            JXmlWrapper xml = JXmlWrapper.parseUrl(rul);
            teamIds[0] = xml.getStringValue("@hid");
            teamIds[1] = xml.getStringValue("@gid");
        } catch (Exception e) {
            log.error("读取{}文件出错", rul);
        }
        return teamIds;
    }

    /**
     * 通过itemid取得主客队球探的比赛名字
     * @param itemId
     * @param log
     * @return
     */
    public static String[] getTeamName(String itemId, Logger log) {
        String rul = ONEROUND_FILE_URL + itemId + ".xml";
        String teamNames[] = new String[2];
        try {
            JXmlWrapper xml = JXmlWrapper.parseUrl(rul);
            teamNames[0] = xml.getStringValue("@hn");
            teamNames[1] = xml.getStringValue("@gn");
        } catch (Exception e) {
            log.error("读取{}文件出错名字", rul);
        }
        return teamNames;
    }
}
