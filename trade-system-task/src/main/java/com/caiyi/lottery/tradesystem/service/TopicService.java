package com.caiyi.lottery.tradesystem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.TopicConfigBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.ordercenter.client.GodShareInterface;
import com.caiyi.lottery.tradesystem.util.ParseGeneralRulesUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.TimeUtil;
import com.caiyi.lottery.tradesystem.utils.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专题服务
 * @author wxy
 * @create 2018-03-28 17:03
 **/
@Slf4j
@Service
public class TopicService {
    @Autowired
    private GodShareInterface godShareInterface;

    private static final String SUBGROUP_FILE_URL = "http://mobile.9188.com/qtjsbf/topic/subGroup/subGroup.json";
    private static final String ONEROUND_FILE_URL = "http://mobile.9188.com/qtjsbf/jc/oneroundtitledata/";
    private static final String GYJ_FILE_PATCH = "/opt/export/data/app/gyj/18001/";
    private static final String GJ_FILE_NAME = "gj.json";
    private static final String GYJ_FILE_NAME = "gyj.json";
    private static final String JCZQ_HH_FILE_PATH = "/opt/export/data/app/jczq/";
    private static final String JCZQ_HH_FILE_NAME = "new_jczq_hh.xml";
    private static final String TOPIC_HOMEPAGE_FILE = "/opt/export/data/app/topic/topicHomePage.json";

    private static final String CONFIG_FILE = "/opt/export/www/cms/news/ad/topic_operation.xml";
    private static final String MATCHDATA_PATH = "/opt/export/data/app/topic/matchData/";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
    private static final Map teammap = new HashMap();
    static {
        teammap.put("巴西","778");
        teammap.put("德国","650");
        teammap.put("西班牙","772");
        teammap.put("阿根廷","766");
        teammap.put("法国","649");
        teammap.put("比利时","645");
        teammap.put("葡萄牙","765");
        teammap.put("英格兰","744");
        teammap.put("乌拉圭","767");
        teammap.put("哥伦比亚","775");
        teammap.put("克罗地亚","768");
        teammap.put("俄罗斯","746");
        teammap.put("墨西哥","819");
        teammap.put("波兰","637");
        teammap.put("瑞士","648");
        teammap.put("丹麦","638");
        teammap.put("塞尔维亚","642");
        teammap.put("瑞典","644");
        teammap.put("秘鲁","774");
        teammap.put("日本","903");
        teammap.put("尼日利亚","789");
        teammap.put("塞内加尔","815");
        teammap.put("埃及","735");
        teammap.put("冰岛","756");
        teammap.put("突尼斯","823");
        teammap.put("澳大利亚","913");
        teammap.put("摩洛哥","813");
        teammap.put("韩国","898");
        teammap.put("伊朗","783");
        teammap.put("哥斯达黎加","914");
        teammap.put("巴拿马","798");
        teammap.put("沙特","891");
    }
    private Map<String, TopicConfigBean> configBeanMap;

    public void createTopicHomePage() throws Exception {
        JXmlWrapper configXml = null;
        JSONObject object = new JSONObject();
        JSONObject topicHomePageDataObj = new JSONObject();
        JSONObject subGroupDataObj;
        JSONObject gjDataObj;
        JSONObject gyjDataObj;

        // 读取世界杯小组赛的数据
        String subGroupDataStr = retrieveDataByUrl(SUBGROUP_FILE_URL);
        String gjDataStr = null;
        String gyjDataStr = null;
        String jczqDataStr = null;
        try {
            configXml = JXmlWrapper.parse(new File(CONFIG_FILE));
            gjDataStr = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(new File(GYJ_FILE_PATCH + GJ_FILE_NAME)), "utf-8"));
            gyjDataStr = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(new File(GYJ_FILE_PATCH + GYJ_FILE_NAME)), "utf-8"));
            jczqDataStr = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(new File(FileConstant.JC_TOPIC_FILE)), "utf-8"));
        } catch (FileNotFoundException e) {
            log.error("文件不存在", e);
        }
        if (configXml == null) {
            log.error("{}配置文件载入失败", CONFIG_FILE);
            return;
        }
        if (StringUtil.isEmpty(subGroupDataStr)) {
            log.error("{}文件内容为空", SUBGROUP_FILE_URL);
            return;
        }
        if (StringUtil.isEmpty(gjDataStr)) {
            log.info("{}内容为空", GYJ_FILE_PATCH + GJ_FILE_NAME);
        }
        if (StringUtil.isEmpty(gyjDataStr)) {
            log.info("{}内容为空", GYJ_FILE_PATCH + GYJ_FILE_NAME);
        }
        if (StringUtil.isEmpty(jczqDataStr)) {
            log.info("{}内容为空", FileConstant.JC_TOPIC_FILE);
        }
        // 载入配置文件
        loadConfig(configXml);

        gjDataObj = JSONObject.parseObject(gjDataStr);
        gyjDataObj = JSONObject.parseObject(gyjDataStr);

        // 世界杯战况
        subGroupDataObj = (JSONObject) JSON.toJSON(configBeanMap.get("subGroup"));
        if (subGroupDataObj != null) {
            subGroupDataObj.put("subGroupData", JSONObject.parseObject(subGroupDataStr));
            topicHomePageDataObj.put("subGroupData", subGroupDataObj);
        }

        // 赛事详情快速导航模块
        topicHomePageDataObj.put("navigationList", getIcon(configXml));

        // 运营下发
        topicHomePageDataObj.put("operationList", getOperation(configXml));

        // 竞彩世界杯
        JSONObject worlCupMatchObj = (JSONObject) JSON.toJSON(configBeanMap.get("worldCupMatches"));
        if (worlCupMatchObj != null) {
            worlCupMatchObj.put("worldCupMatches", getJCWorldCup(jczqDataStr));
            topicHomePageDataObj.put("worldCupMatches", worlCupMatchObj);
        }

        // 冠军游戏
        JSONObject championGame = (JSONObject)JSON.toJSON(configBeanMap.get("championGame"));
        if (championGame != null) {
            championGame.put("championGame", getGJGameData(gjDataObj));
            topicHomePageDataObj.put("championGame", championGame);
        }

        // 冠亚军游戏
        JSONObject championAndSecondGame = (JSONObject) JSON.toJSON(configBeanMap.get("championAndSecondGame"));
        if (championAndSecondGame != null) {
            championAndSecondGame.put("championAndSecondGame", getGJGameData(gyjDataObj));
            topicHomePageDataObj.put("championAndSecondGame", championAndSecondGame);
        }

        // 精选神单
        JSONObject godShare = (JSONObject) JSON.toJSON(configBeanMap.get("godShare"));
        if (godShare != null) {
            godShare.put("godShare", getGodShareData());
            topicHomePageDataObj.put("godShare", godShare);
        }

        // 聚焦世界杯
        topicHomePageDataObj.put("worldCupTopic", getTopic(configXml));

        object.put("code", "0");
        object.put("desc", "查询成功");
        object.put("data", topicHomePageDataObj);

        // FileCopyUtils.copy(object.toJSONString(), new FileWriter(new File(TOPIC_HOMEPAGE_FILE)));
        FileCopyUtils.copy(object.toJSONString(), new OutputStreamWriter(new FileOutputStream(new File(TOPIC_HOMEPAGE_FILE)), "utf-8"));

    }

    /**
     * 获取聚焦世界杯的数据
     * @param configXml
     * @return
     */
    private JSONObject getTopic(JXmlWrapper configXml) {
        JSONObject topicListObject = new JSONObject();
        JXmlWrapper topicListNode = configXml.getXmlNode("topicList");
        topicListObject.put("order", topicListNode.getStringValue("@order"));
        topicListObject.put("haveSpace", topicListNode.getStringValue("@haveSpace"));
        topicListObject.put("haveTitle", topicListNode.getStringValue("@haveTitle"));
        topicListObject.put("titleAdSrc", topicListNode.getStringValue("@titleAdSrc"));
        topicListObject.put("titleIOSsrc", topicListNode.getStringValue("@titleIOSsrc"));
        topicListObject.put("titleIOSsrc2X", topicListNode.getStringValue("@titleIOSsrc2X"));
        topicListObject.put("titleIOSsrc3X", topicListNode.getStringValue("@titleIOSsrc3X"));
        topicListObject.put("titleHeightAD", topicListNode.getStringValue("@titleHeightAD"));
        topicListObject.put("titleHeightIOS", topicListNode.getStringValue("@titleHeightIOS"));
        topicListObject.put("adImgWidth", topicListNode.getStringValue("@adImgWidth"));
        topicListObject.put("adImgHeight", topicListNode.getStringValue("@adImgHeight"));
        List<JXmlWrapper> topicNodeList = topicListNode.getXmlNodeList("topic");
        JSONArray topicArray = new JSONArray();
        JSONObject topicObj;
        for (JXmlWrapper topicNode : topicNodeList) {
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(topicNode.getXmlNode("general-rules"), new BaseBean(), log);
            if (!flag) {
                continue;
            }
            topicObj = new JSONObject();
            topicObj.put("evid", topicNode.getStringValue("@evid"));
            topicObj.put("title", topicNode.getStringValue("@title"));
            topicObj.put("adsrc", topicNode.getStringValue("@adsrc"));
            topicObj.put("iossrc", topicNode.getStringValue("@iOSsrc"));
            topicObj.put("iossrc2X", topicNode.getStringValue("@iOSsrc2X"));
            topicObj.put("iossrc3X", topicNode.getStringValue("@iOSsrc3X"));
            topicObj.put("linkUrl", topicNode.getStringValue("@linkUrl"));
            topicArray.add(topicObj);
        }
        topicListObject.put("topic", topicArray);
        return topicListObject;
    }

    /**
     * 获取运营下发位的数据
     * @param configXml
     * @return
     */
    private JSONArray getOperation(JXmlWrapper configXml) {
        JSONObject operationObject;
        JSONArray operationStyleArray = new JSONArray();
        JSONArray operationArray;
        JSONObject operationObj;
        List<JXmlWrapper> operationNodeList;
        List<JXmlWrapper> operationStyleNodeList = configXml.getXmlNode("operation-list").getXmlNodeList("operation-style");
        for (JXmlWrapper operationStyleNode : operationStyleNodeList) {
            operationObject = new JSONObject();
            operationObject.put("style", operationStyleNode.getStringValue("@style"));
            operationObject.put("order", operationStyleNode.getStringValue("@order"));
            operationObject.put("haveSpace", operationStyleNode.getStringValue("@haveSpace"));
            operationObject.put("haveTitle", operationStyleNode.getStringValue("@haveTitle"));
            operationObject.put("titleAdSrc", operationStyleNode.getStringValue("@titleAdSrc"));
            operationObject.put("titleIOSsrc2X", operationStyleNode.getStringValue("@titleIOSsrc2X"));
            operationObject.put("titleIOSsrc3X", operationStyleNode.getStringValue("@titleIOSsrc3X"));
            operationObject.put("titleHeightAD", operationStyleNode.getStringValue("@titleHeightAD"));
            operationObject.put("titleHeightIOS", operationStyleNode.getStringValue("@titleHeightIOS"));
            operationObject.put("adImgHeight", operationStyleNode.getStringValue("@adImgHeight"));
            operationObject.put("adImgWidth", operationStyleNode.getStringValue("@adImgWidth"));
            operationObject.put("iosimgHeight", operationStyleNode.getStringValue("@iOSImgHeight"));

            operationNodeList = operationStyleNode.getXmlNodeList("operation");
            operationArray = new JSONArray();
            for (JXmlWrapper operationNode : operationNodeList) {
                boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(operationNode.getXmlNode("general-rules"), new BaseBean(), log);
                if (!flag) {
                    continue;
                }
                operationObj = new JSONObject();
                operationObj.put("evid", operationNode.getStringValue("@evid"));
                operationObj.put("adsrc", operationNode.getStringValue("@adsrc"));
                operationObj.put("iossrc2X", operationNode.getStringValue("@iOSsrc2X"));
                operationObj.put("iossrc3X", operationNode.getStringValue("@iOSsrc3X"));
                operationObj.put("adlink", operationNode.getStringValue("@adlink"));
                operationObj.put("ioslink", operationNode.getStringValue("@iOSlink"));
                operationObj.put("title", operationNode.getStringValue("@title"));

                operationArray.add(operationObj);
            }
            if (operationArray.isEmpty()) {
                continue;
            }
            operationObject.put("operation", operationArray);
            operationStyleArray.add(operationObject);
        }

        return operationStyleArray;
    }

    /**
     * 取得当行模块数据
     * @param configXml
     * @return
     */
    private JSONObject getIcon(JXmlWrapper configXml) {
        JSONObject iconObj = new JSONObject();
        JSONArray iconArray = new JSONArray();
        JSONObject icon;
        JXmlWrapper iconListNode = configXml.getXmlNode("navigationList");
        iconObj.put("order", iconListNode.getStringValue("@order"));
        iconObj.put("haveSpace", iconListNode.getStringValue("@haveSpace"));

        List<JXmlWrapper> iconNodeList = iconListNode.getXmlNodeList("navigation");
        for (JXmlWrapper iconNode : iconNodeList) {
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(iconNode.getXmlNode("general-rules"), new BaseBean(), log);
            if (!flag) {
                continue;
            }
            icon = new JSONObject();
            icon.put("evid", iconNode.getStringValue("@evid"));
            icon.put("adsrc", iconNode.getStringValue("@adsrc"));
            icon.put("iossrc", iconNode.getStringValue("@iOSsrc"));
            icon.put("title", iconNode.getStringValue("@title"));
            icon.put("adlink", iconNode.getStringValue("@adlink"));
            icon.put("ioslink", iconNode.getStringValue("@iOSlink"));

            iconArray.add(icon);
        }
        iconObj.put("navigation", iconArray);

        return iconObj;
    }

    /**
     * 获取神单列表
     * @return
     */
    private JSONArray getGodShareData() {
        JSONArray shareArray = new JSONArray();
        BaseReq baseReq = new BaseReq("task");
        baseReq.setData(new BaseBean());
        BaseResp<HashMap<String, Object>> resp = godShareInterface.godShareItem(baseReq);
        HashMap<String, Object> godShareData = resp.getData();
        if (godShareData == null) {
            log.error("神单数据为空");
            return shareArray;
        }
        JSONArray shareDataArray = (JSONArray) JSON.toJSON(godShareData.get("sharelist"));

        return shareDataArray;
    }

    /**
     * 处理冠军数据
     * @param gjDataObj
     * @return
     */
    private JSONArray getGJGameData(JSONObject gjDataObj) {
        JSONArray gjArray = gjDataObj.getJSONArray("data");
        JSONArray gjDataAraay = new JSONArray();
        for (int i = 0; i < 6; i++) {
            JSONObject object = gjArray.getJSONObject(i);
            String name = object.getString("name");
            String sale = object.getString("isale");
            String rs = object.getString("rs");
            if (name.contains("—")) {
                String[] names = name.split("—");
                String logoA = teammap.get(names[0]) + ".png";
                String logoB = teammap.get(names[1]) + ".png";
                if ("1".equals(sale) || "0".equals(rs)) {
                    logoA = teammap.get(names[0]) + "_1.png";
                    logoB = teammap.get(names[1]) + "_1.png";
                }
                object.put("teamAlogo", "/qtjsbf/topic/pic/gyj/" + logoA);
                object.put("teamBlogo", "/qtjsbf/topic/pic/gyj/" + logoB);
            } else {
                String logo = teammap.get(object.getString("name")) + ".png";
                if ("1".equals(sale) || "0".equals(rs)) {
                    logo = teammap.get(object.getString("name")) + "_1.png";
                }
                object.put("teamLogo","/qtjsbf/topic/pic/gyj/" + logo);
            }
            object.put("sp", FormatUtil.formatSP(object.getString("sp")));
            gjDataAraay.add(object);
        }

        return gjDataAraay;
    }

    /**
     * 竞彩世界杯
     * @param contentStr
     * @return
     */
    private JSONArray getJCWorldCup(String contentStr) {
        JSONObject object = JSONObject.parseObject(contentStr);
        JSONArray matchDataArray = new JSONArray();
        JSONObject matchObj;
        JSONArray matchArray = object.getJSONObject("data").getJSONArray("matchData");

        for (int i = 0; i < matchArray.size(); i++) {
            if (i > 4) {
                break;
            }
            matchObj = new JSONObject();
            matchObj.put("mname", matchArray.getJSONObject(i).getString("mname"));
            matchObj.put("itemid", matchArray.getJSONObject(i).getString("itemId"));
            matchObj.put("rid", matchArray.getJSONObject(i).getString("matchId"));
            matchObj.put("hn", matchArray.getJSONObject(i).getString("hn"));
            matchObj.put("gn", matchArray.getJSONObject(i).getString("gn"));

            matchObj.put("et", dateFormat.format(TimeUtil.parserDateTime(matchArray.getJSONObject(i).getString("et"))));
            matchObj.put("hm", matchArray.getJSONObject(i).getString("hm"));
            matchObj.put("gm", matchArray.getJSONObject(i).getString("gm"));
            matchObj.put("spf", matchArray.getJSONObject(i).getString("spf"));
            matchObj.put("spfscale", matchArray.getJSONObject(i).getString("spfscale"));
            matchObj.put("info", getInfo(matchArray.getJSONObject(i).getString("itemId")));

            setTeamId(matchObj);
            matchDataArray.add(matchObj);
        }
        return matchDataArray;
    }

    private void setTeamId(JSONObject matchObj) {
        String rul = ONEROUND_FILE_URL + matchObj.getString("itemid") + ".xml";
        try {
            JXmlWrapper xml = JXmlWrapper.parseUrl(rul);
            // todo 测试保留
            matchObj.put("homeTeamId", xml.getStringValue("@hid"));
            matchObj.put("awayTeamId", xml.getStringValue("@gid"));
            // matchObj.put("homeTeamId", "637");
            // matchObj.put("awayTeamId", "638");
        } catch (Exception e) {
            log.error("读取{}文件出错", rul);
        }
    }


    private String retrieveDataByUrl(String url) throws Exception {
        HttpClient client = new HttpClient();
        GetMethod mothod = new GetMethod(url);
        mothod.getParams().setContentCharset("utf-8");

        String respStr = "";
        try {
            long startTime = System.currentTimeMillis();
            client.executeMethod(mothod); // 发送http请求
            respStr = mothod.getResponseBodyAsString();
            long endTime = System.currentTimeMillis();

            log.info("文件{}访问成功,用时{}毫秒",url,(endTime - startTime));
        } catch (Exception e) {
            log.error("文件{}访问失败", url);
        } finally {
            mothod.releaseConnection();
        }
        return respStr;
    }

    private void loadConfig(JXmlWrapper configXml) {
        TopicConfigBean topicConfigBean;
        JXmlWrapper moduleNode;
        JXmlWrapper generalRules;
        List<Element> elementList = configXml.getXmlRoot().getChildren();
        configBeanMap = new HashMap<>();
        for (Element element : elementList) {
            String nodeName = element.getName();

            if ("operation-list".equals(nodeName) || "navigationList".equals(nodeName) || "topicList".equals(nodeName)) {
                continue;
            }
            moduleNode = configXml.getXmlNode(nodeName);
            if (moduleNode != null) {
                generalRules = moduleNode.getXmlNode("general-rules");
                boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, new BaseBean(), log);
                if (flag) {
                    topicConfigBean = new TopicConfigBean();
                    topicConfigBean.setEvid(moduleNode.getStringValue("@evid"));
                    topicConfigBean.setOrder(moduleNode.getStringValue("@order"));
                    topicConfigBean.setLinkUrl(moduleNode.getStringValue("@linkUrl"));
                    topicConfigBean.setHaveSpace(moduleNode.getStringValue("@haveSpace"));
                    topicConfigBean.setHaveTitle(moduleNode.getStringValue("@haveTitle"));
                    topicConfigBean.setTitleAdSrc(moduleNode.getStringValue("@titleAdSrc"));
                    topicConfigBean.setTitleIOSsrc2X(moduleNode.getStringValue("@titleIOSsrc2X"));
                    topicConfigBean.setTitleIOSsrc3X(moduleNode.getStringValue("@titleIOSsrc3X"));
                    topicConfigBean.setTitleHeightAD(moduleNode.getStringValue("@titleHeightAD"));
                    topicConfigBean.setTitleHeightIOS(moduleNode.getStringValue("@titleHeightIOS"));
                    topicConfigBean.setAdImgWidth(moduleNode.getStringValue("@adImgWidth"));
                    topicConfigBean.setAdImgHeight(moduleNode.getStringValue("@adImgHeight"));
                    topicConfigBean.setPath(moduleNode.getStringValue("@path"));
                    configBeanMap.put(nodeName, topicConfigBean);
                }
            }
        }
    }

    private String getInfo(String itemId) {
        String info = "";
        String jsonStr;
        try {
            jsonStr = FileCopyUtils.copyToString(new FileReader(new File(MATCHDATA_PATH + itemId + ".json")));
            JSONObject matchDataObj = JSONObject.parseObject(jsonStr);
            JSONArray intelligences = matchDataObj.getJSONObject("data").getJSONArray("intelligences");
            if (intelligences == null || intelligences.size() < 1) {
                return info;
            }
            info = ((JSONObject)intelligences.get(0)).getString("content");
        } catch (IOException e) {
            log.error("读取{}文件失败", MATCHDATA_PATH + itemId + ".json");
            return info;
        }
        return info;
    }
}
