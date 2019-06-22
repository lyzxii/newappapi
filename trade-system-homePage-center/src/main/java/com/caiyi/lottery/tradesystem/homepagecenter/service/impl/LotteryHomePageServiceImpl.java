package com.caiyi.lottery.tradesystem.homepagecenter.service.impl;

import bean.HomePageBean;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.homepagecenter.service.LotteryHomePageService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.ParseGeneralRulesUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.TimeUtil;
import dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author wxy
 * @create 2018-01-09 20:24
 **/
@Slf4j
@Service
public class LotteryHomePageServiceImpl implements LotteryHomePageService {
    //福彩3D截止时间（暂定）
    private static final String FC3D_TIME = "19:30:00";
    @Autowired
    private UserBasicInfoWrapper userBasicInfoWrapper;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH:mm");

    @Override
    public LotteryHomePageDTO lotteryHomePage(HomePageBean bean) throws Exception {
        LotteryHomePageDTO lotteryHomePageDTO = new LotteryHomePageDTO();
        log.info("获取主页内容信息,用户名:" + bean.getUid() + " source:" + bean.getSource());
        // 根据配置文件获取指定的配置文件
        String fileName = getSpecifiedFile(bean);
        if (StringUtil.isEmpty(fileName)) {
            log.info("未找到指定的主页配置文件,[source:{},用户名:{}]", bean.getSource(), bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("服务器出现了一些小问题，请稍后重试。首页问题码:1");
            return null;
        }

        // 设置白名单等级
        if (!StringUtil.isEmpty(bean.getUid())) {
            String whiteGrade = userBasicInfoWrapper.queryUserWhiteGrade(bean, log, SysCodeConstant.HOMEPAGECENTER);
            bean.setWhitelistGrade(StringUtil.isEmpty(whiteGrade) ? 0 : Integer.parseInt(whiteGrade));
        }

        appendBannerContent(bean, lotteryHomePageDTO, fileName);
        appendOperationContent(bean, lotteryHomePageDTO, fileName);
        appendLotteryContent(bean, lotteryHomePageDTO, fileName);
        return lotteryHomePageDTO;
    }

    private void appendLotteryContent(HomePageBean bean, LotteryHomePageDTO lotteryHomePageDTO, String fileName) {
        String operationFile = FileConstant.HOME_PAGE_CONFIG_PATH + fileName + "_lottery_new.xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(operationFile));
        // appendLotteryNodeContent(bean, xml, "recommend");
        LotteryListDTO lotteryListDTO = appendLotteryNode(bean, xml, "normal", "lottery");
        lotteryHomePageDTO.setLotteryList(lotteryListDTO);
        
        HotBetsDTO hotBetsDTO = appendHotBetsNode(bean, xml, "hotBets");
        lotteryHomePageDTO.setHotBets(hotBetsDTO);

        FocusEventListDTO focusEventListDTO = appendFocusEventNode(bean, xml, "focusEvent");
        lotteryHomePageDTO.setFocusEventList(focusEventListDTO);

        MatchListDTO matchListDTO = appendWorldCupMatchesNode(bean, xml, "worldCupMatches");

        lotteryHomePageDTO.setWorldCupMatches(matchListDTO);

        FloatImgDTO floatImgDTO=appendFloatImgNode(bean, xml, "FloatImg");

        lotteryHomePageDTO.setFloatImg(floatImgDTO);//首页浮标

        FloatImgDTO floatWindowDTO=appendFloatImgNode(bean, xml, "FloatWindow");

        lotteryHomePageDTO.setFloatWindow(floatWindowDTO);//首页浮窗
    }

    private FloatImgDTO appendFloatImgNode(HomePageBean bean, JXmlWrapper xml, String nodeName) {
         FloatImgDTO dto=new FloatImgDTO();
         JXmlWrapper node = xml.getXmlNode(nodeName);
         if (node == null) {
            return dto;
        }
        Date beginDate=null;
        if(!StringUtil.isEmpty(node.getStringValue("@begindate"))){
            beginDate=node.getDateValue("@begindate");
        }
        Date endDate=null;
        if(!StringUtil.isEmpty(node.getStringValue("@enddate"))){
            endDate=node.getDateValue("@enddate");
        }
        boolean dflag=true;

        if(beginDate!=null&&endDate!=null){//都不为空
            dflag=beginDate.compareTo(new Date())<0&&endDate.compareTo(new Date())>0;
        }else if(beginDate==null&&endDate!=null){
            dflag=endDate.compareTo(new Date())>0;
        }else if(beginDate!=null){
            dflag=beginDate.compareTo(new Date())<0;
        }
        JXmlWrapper generalRules = node.getXmlNode("general-rules");
        // 通用规则检查
        boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
        if(flag&&dflag){
            dto.setImgUrl(node.getStringValue("@imgUrl"));
            dto.setJumpUrl(node.getStringValue("@jumpUrl"));
            dto.setYmId(node.getStringValue("@ymId"));
            dto.setTitle(node.getStringValue("@title"));
            if("FloatWindow".equals(nodeName)){
                dto.setId(node.getStringValue("@id"));
            }
            if("FloatImg".equals(nodeName)){
                dto.setHeight(node.getStringValue("@height"));
                dto.setWidth(node.getStringValue("@width"));
            }
        }
        return dto;

    }

    /**
     * 加载竞彩世界杯
     * @param bean
     * @param xml
     * @param nodeName
     * @return
     */
    private MatchListDTO appendWorldCupMatchesNode(HomePageBean bean, JXmlWrapper xml, String nodeName) {
        MatchListDTO matchListDTO = new MatchListDTO();
        List<MatchDTO> matchDTOList = new ArrayList<>();
        MatchDTO matchDTO;
        JSONObject matchData;

        JXmlWrapper node = xml.getXmlNode(nodeName);
        if (node == null) {
            return matchListDTO;
        }
        JXmlWrapper generalRules = node.getXmlNode("general-rules");
        // 通用规则检查
        boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
        if (flag) {
            matchListDTO.setHaveSpace(node.getStringValue("@haveSpace"));
            matchListDTO.setEvid(node.getStringValue("@evid"));
            matchListDTO.setOrder(node.getStringValue("@order"));
            matchListDTO.setHaveTitle(node.getStringValue("@haveTitle"));
            matchListDTO.setTitleAdSrc(node.getStringValue("@titleAdSrc"));
            matchListDTO.setTitleIOSsrc2X(node.getStringValue("@titleIOSsrc2X"));
            matchListDTO.setTitleIOSsrc3X(node.getStringValue("@titleIOSsrc3X"));
            matchListDTO.setTitleHeightAD(node.getStringValue("@titleHeightAD"));
            matchListDTO.setTitleHeightIOS(node.getStringValue("@titleHeightIOS"));
            matchListDTO.setPath(node.getStringValue("@path"));
            String jczqDataStr = null;
            try {
                jczqDataStr = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(new File(FileConstant.JC_TOPIC_FILE)), "utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (StringUtil.isEmpty(jczqDataStr)) {
                log.info("{}内容为空", FileConstant.JC_TOPIC_FILE);
                return matchListDTO;
            }

            JSONArray matchArray = JSONObject.parseObject(jczqDataStr).getJSONObject("data").getJSONArray("matchData");

            for (int i = 0; i < matchArray.size(); i++) {
                if (i > 4) {
                    break;
                }
                matchData = matchArray.getJSONObject(i);
                matchDTO = new MatchDTO();
                matchDTO.setMname(matchData.getString("mname"));
                matchDTO.setItemid(matchData.getString("itemId"));
                matchDTO.setRid(matchData.getString("matchId"));
                matchDTO.setHn(matchData.getString("hn"));
                matchDTO.setGn(matchData.getString("gn"));
                matchDTO.setEt(dateFormat.format(TimeUtil.parserDateTime(matchData.getString("et"))));
                matchDTO.setSpf(matchData.getString("spf"));
                matchDTO.setSpfscale(matchData.getString("spfscale"));
                matchDTO.setInfo(getInfo(matchData.getString("itemId")));

                setTeamId(matchDTO);
                matchDTOList.add(matchDTO);
            }
            matchListDTO.setMatchs(matchDTOList);
        }
        return matchListDTO;
    }
    private String getInfo(String itemId) {
        String info = "";
        String jsonStr;
        try {
            jsonStr = FileCopyUtils.copyToString(new InputStreamReader(new FileInputStream(new File(FileConstant.MATCHDATA_PATH + itemId + ".json")), "utf-8"));
            if (StringUtil.isEmpty(jsonStr)) {
                log.info("情报内容为空");
                return info;
            }
            JSONObject matchDataObj = JSONObject.parseObject(jsonStr);
            JSONArray intelligences = matchDataObj.getJSONObject("data").getJSONObject("matchData").getJSONArray("intelligences");
            if (intelligences == null || intelligences.size() < 1) {
                log.info("情报内容为空");
                return info;
            }
            info = ((JSONObject)intelligences.get(0)).getString("content");
        } catch (IOException e) {
            log.error("读取{}文件失败", FileConstant.MATCHDATA_PATH + itemId + ".json");
            return info;
        }
        return info;
    }
    private void setTeamId(MatchDTO matchDTO) {
        String rul = "http://mobile.9188.com/qtjsbf/jc/oneroundtitledata/" + matchDTO.getItemid() + ".xml";
        try {
            JXmlWrapper xml = JXmlWrapper.parseUrl(rul);
            // todo 测试保留
            matchDTO.setHomeTeamId(xml.getStringValue("@hid"));
            matchDTO.setAwayTeamId(xml.getStringValue("@gid"));
            // matchDTO.setHomeTeamId("637");
            // matchDTO.setAwayTeamId("638");
        } catch (Exception e) {
            log.error("读取{}文件出错", rul);
        }
    }

    private FocusEventListDTO appendFocusEventNode(HomePageBean bean, JXmlWrapper xml, String nodeName) {
        FocusEventListDTO focusEventListDTO = new FocusEventListDTO();
        List<FocusEventDTO> focusEventDTOList = new ArrayList<>();
        FocusEventDTO focusEventDTO;
        JXmlWrapper node = xml.getXmlNode(nodeName);
        JXmlWrapper generalRules = node.getXmlNode("general-rules");
        boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
        if (flag) {
            focusEventListDTO.setHaveSpace(node.getStringValue("@haveSpace"));
            focusEventListDTO.setGameType(node.getStringValue("@gameType"));
            focusEventListDTO.setEvid(node.getStringValue("@evid"));
            focusEventListDTO.setOrder(node.getStringValue("@order"));
            focusEventListDTO.setHaveTitle(node.getStringValue("@haveTitle"));
            focusEventListDTO.setTitleAdSrc(node.getStringValue("@titleAdSrc"));
            focusEventListDTO.setTitleIOSsrc2X(node.getStringValue("@titleIOSsrc2X"));
            focusEventListDTO.setTitleIOSsrc3X(node.getStringValue("@titleIOSsrc3X"));
            focusEventListDTO.setTitleHeightAD(node.getStringValue("@titleHeightAD"));
            focusEventListDTO.setTitleHeightIOS(node.getStringValue("@titleHeightIOS"));

            focusEventDTOList = appendEvents(node.getStringValue("@gameType"));
        }
        focusEventListDTO.setFocusEvent(focusEventDTOList);
        return focusEventListDTO;
    }

    private List<FocusEventDTO> appendEvents(String gameType) {
        List<FocusEventDTO> focusEventDTOList = new ArrayList<>();
        FocusEventDTO focusEventDTO;
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.FOOTBALL_FOCUS_EVENT_FILE));
        if ("6".equals(gameType)) {
            xml = JXmlWrapper.parse(new File(FileConstant.BASKETBALL_FOCUS_EVENT_FILE));
        }
        List<JXmlWrapper> eventList = xml.getXmlNodeList("row");
        for (JXmlWrapper event : eventList) {
            focusEventDTO = new FocusEventDTO();
            focusEventDTO.setDesc(event.getStringValue("@reason"));
            focusEventDTO.setItemId(event.getStringValue("@itemid"));
            focusEventDTO.setLeagueName(event.getStringValue("@mname"));
            focusEventDTO.setJcName(event.getStringValue("@name"));
            focusEventDTO.setHomeName(event.getStringValue("@hn"));
            focusEventDTO.setHomeId(event.getStringValue("@hnId"));
            focusEventDTO.setHomeLogo(event.getStringValue("@hnLogo"));
            focusEventDTO.setAwayName(event.getStringValue("@gn"));
            focusEventDTO.setAwayId(event.getStringValue("@gnId"));
            focusEventDTO.setAwayLogo(event.getStringValue("@gnLogo"));
            focusEventDTO.setEndTime(event.getStringValue("@et"));
            focusEventDTO.setEndDesc(event.getStringValue("@endTime"));
            focusEventDTO.setLinkUrl("");

            focusEventDTOList.add(focusEventDTO);
        }
        return focusEventDTOList;
    }

    private HotBetsDTO appendHotBetsNode(HomePageBean bean, JXmlWrapper xml, String nodeName) {
        HotBetsDTO hotBetsDTO = new HotBetsDTO();
        LotteryDTO lotteryDTO;
        JXmlWrapper node = xml.getXmlNode(nodeName);
        JXmlWrapper generalRules = node.getXmlNode("general-rules");
        boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
        String week = TimeUtil.weekOfDay();

        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        String[] endTime = node.getStringValue("@endTime").split(":");
        int endHours = Integer.parseInt(endTime[0]);
        int endMinutes = Integer.parseInt(endTime[1]);
        if ("五".equals(week) || (hours * 60 + minutes > endHours * 60 + endMinutes)) {
            flag = false;
        }
        if (flag) {
            hotBetsDTO.setHaveSpace(node.getStringValue("@haveSpace"));
            hotBetsDTO.setEndTime("19:30");
            hotBetsDTO.setEvid(node.getStringValue("@evid"));
            hotBetsDTO.setOrder(node.getStringValue("@order"));
            hotBetsDTO.setHaveTitle(node.getStringValue("@haveTitle"));
            hotBetsDTO.setTitleAdSrc(node.getStringValue("@titleAdSrc"));
            hotBetsDTO.setTitleIOSsrc2X(node.getStringValue("@titleIOSsrc2X"));
            hotBetsDTO.setTitleIOSsrc3X(node.getStringValue("@titleIOSsrc3X"));
            hotBetsDTO.setTitleHeightAD(node.getStringValue("@titleHeightAD"));
            hotBetsDTO.setTitleHeightIOS(node.getStringValue("@titleHeightIOS"));
            hotBetsDTO.setImgSrc(node.getStringValue("@imgSrc"));
            hotBetsDTO.setEndTime(node.getStringValue("@endTime"));

            String pools;
            if ("一三六".contains(week)) {
                lotteryDTO = new LotteryDTO();
                appendPoolAndAwardState(lotteryDTO, "50");
                pools = lotteryDTO.getPools();
                if (lotteryDTO.getPools().contains(":")) {
                    pools = lotteryDTO.getPools().replace(":","");
                }
                hotBetsDTO.setLotteryName("大乐透");
                hotBetsDTO.setPools(pools);
                hotBetsDTO.setLinkUrl("http://mobile.9188.com/app?pagetype=Lottery&pageid=50&pagetab=0&pageextend=");
            }
            if ("二四日".contains(week)) {
                hotBetsDTO.setLotteryName("双色球");
                lotteryDTO = new LotteryDTO();
                appendPoolAndAwardState(lotteryDTO, "01");
                pools = lotteryDTO.getPools();
                if (lotteryDTO.getPools().contains(":")) {
                    pools = lotteryDTO.getPools().replace(":", "");
                }
                hotBetsDTO.setPools(pools);
                hotBetsDTO.setLinkUrl("http://mobile.9188.com/app?pagetype=Lottery&pageid=01&pagetab=0&pageextend=");
            }

        }
        return hotBetsDTO;
    }

    private LotteryListDTO appendLotteryNode(HomePageBean bean, JXmlWrapper xml, String nodeName, String childNodeName) {
        LotteryListDTO lotteryListDTO = new LotteryListDTO();
        List<LotteryDTO> lotteryDTOList = new ArrayList<>();
        LotteryDTO lotteryDTO;
        List<LotteryDTO> childLotteryDTOList;
        LotteryDTO childLotteryDTO;

        JXmlWrapper node = xml.getXmlNode(nodeName);
        List<JXmlWrapper> lotteryList = node.getXmlNodeList(childNodeName);
        lotteryListDTO.setOrder(node.getStringValue("@order"));
        lotteryListDTO.setShowNum(node.getStringValue("@showNum"));
        lotteryListDTO.setHaveSpace(node.getStringValue("@haveSpace"));

        for (JXmlWrapper lottery : lotteryList) {
            JXmlWrapper generalRules = lottery.getXmlNode("general-rules");
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                lotteryDTO = new LotteryDTO();
                appendLotteryAttr(lottery, "lottery", lotteryDTO);
                JXmlWrapper businessRules = lottery.getXmlNode("business-rules");
                List<JXmlWrapper> childLotteryList = businessRules.getXmlNodeList("childLottery");
                if(childLotteryList.size()>0){
                    childLotteryDTOList = new ArrayList<>();
                    for(JXmlWrapper childLottery:childLotteryList){
                        childLotteryDTO = new LotteryDTO();
                        appendLotteryAttr(childLottery, "childLottery", childLotteryDTO);

                        childLotteryDTOList.add(childLotteryDTO);
                    }
                    lotteryDTO.setChildLottery(childLotteryDTOList);
                }
                lotteryDTOList.add(lotteryDTO);
            }
        }
        lotteryListDTO.setLottery(lotteryDTOList);
        return lotteryListDTO;
    }

    //添加彩种属性
    private void appendLotteryAttr(JXmlWrapper node, String nodeName, LotteryDTO lotteryDTO) {
        String gid = node.getStringValue("@gid");
        if (gid != null) {
            lotteryDTO.setGid(node.getStringValue("@gid"));
            lotteryDTO.setEvid(node.getStringValue("@evid"));
            lotteryDTO.setDesc(node.getStringValue("@desc"));
            lotteryDTO.setLotteryName(node.getStringValue("@lotteryName"));
            lotteryDTO.setStreamer(node.getStringValue("@streamer"));
            lotteryDTO.setImgUrl(node.getStringValue("@imgUrl"));
            lotteryDTO.setImgUrl2X(node.getStringValue("@imgUrl2X"));
            lotteryDTO.setImgUrl3X(node.getStringValue("@imgUrl3X"));
            lotteryDTO.setAdlink(node.getStringValue("@adlink"));
            lotteryDTO.setIOSlink(node.getStringValue("@iOSlink"));
            lotteryDTO.setShowSale(node.getStringValue("@showSale"));
            lotteryDTO.setAddAward(node.getStringValue("@addAward"));
            lotteryDTO.setStyle(node.getStringValue("@style"));

            if("01".equals(gid)){//双色球
                appendPoolAndAwardState(lotteryDTO,gid);
            }else if("50".equals(gid)){//大乐透
                appendPoolAndAwardState(lotteryDTO,gid);
            }else if("70".equals(gid)){//竞彩足球
                appendRemainMatch(lotteryDTO,gid);
            }else if("71".equals(gid)){//竞彩篮球
                appendRemainMatch(lotteryDTO,gid);
            }else if("03".equals(gid)){//福彩3D
                appendTryCode(lotteryDTO,gid);
            }
        }
    }

    //添加奖池和今日开奖信息
    private void appendPoolAndAwardState(LotteryDTO lotteryDTO, String gid) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.INDEX_DESC_FILE));
        List<JXmlWrapper> rowList = xml.getXmlNodeList("row");
        for(JXmlWrapper row : rowList){
            if(row.getStringValue("@gid").equals(gid)){
                String pools = row.getStringValue("@pools");
                if(StringUtil.isEmpty(pools)||"null".equals(pools)){
                    lotteryDTO.setPools("");
                }else{
                    pools = pools.replace("元", "");
                    lotteryDTO.setPools("奖池:" + pools);
                }
                String day = row.getStringValue("@day");
                if("1".equals(day)){
                    lotteryDTO.setDay("1");
                }
                break;
            }
        }
    }
    //添加可投注场次
    private void appendRemainMatch(LotteryDTO lotteryDTO, String gid) {
        JXmlWrapper xml = null;
        if("70".equals(gid)){
            xml = JXmlWrapper.parse(new File(FileConstant.FOOTBALL_MATCH_FILE));
            getMatchCountAll(lotteryDTO,xml);
        }else if("71".equals(gid)){
            xml = JXmlWrapper.parse(new File(FileConstant.BASKETBALL_MATCH_FILE));
            getMatchCount(lotteryDTO,xml,"NBA");
        }
    }
    //获取所有的场次数
    private void getMatchCountAll(LotteryDTO lotteryDTO, JXmlWrapper xml) {
        int count = 0;
        List<JXmlWrapper> rowsList = xml.getXmlNodeList("rows");
        for(JXmlWrapper rows:rowsList){
            List<JXmlWrapper> rowList = rows.getXmlNodeList("row");
            count += rowList.size();
        }
        if(count>0){
            if(StringUtil.isEmpty(lotteryDTO.getStreamer())){
                lotteryDTO.setStreamer(count + "场在售");
            }
            //lotteryDTO.setRemainMatch(count + "场比赛在售");
        }
    }

    //获取指定联赛的在售场次数
    private void getMatchCount(LotteryDTO lotteryDTO, JXmlWrapper xml, String mname) {
        int count = 0;
        List<JXmlWrapper> rowsList = xml.getXmlNodeList("rows");
        for(JXmlWrapper rows:rowsList){
            List<JXmlWrapper> rowList = rows.getXmlNodeList("row");
            for(JXmlWrapper row:rowList){
                String matchName = row.getStringValue("@mname");
                if(matchName.equals(mname)||matchName.equals("美职篮")){
                    count++;
                }
            }
        }
        if(count>0){
            if(StringUtil.isEmpty(lotteryDTO.getStreamer())){
                lotteryDTO.setStreamer(count + "场" + mname);
            }
            //lotteryDTO.setRemainMatch(count + "场" + mname + "比赛在售");
        }
    }
    //福彩3D添加试机号
    private void appendTryCode(LotteryDTO lotteryDTO, String gid) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.INDEX_DESC_FILE));
        List<JXmlWrapper> rowList = xml.getXmlNodeList("row");
        for(JXmlWrapper row : rowList){
            if(row.getStringValue("@gid").equals(gid)){
                String trycode = row.getStringValue("@trycode");
                if(StringUtil.isEmpty(trycode)){
                    lotteryDTO.setTryCode("");
                }else{
                    //开奖时间后福彩3D的不展示试机号
                    if("03".equals(gid) && afterAward()){
                        lotteryDTO.setTryCode("");
                    }else{
                        lotteryDTO.setTryCode("试机号:" + trycode);
                    }
                }
                break;
            }
        }
    }
    private static boolean afterAward() {
        SimpleDateFormat sim1 = new SimpleDateFormat("HH:mm:ss");
        Date date1;
        try {
            date1 = sim1.parse(sim1.format(new Date()));
            if(date1.after(sim1.parse(FC3D_TIME))){ //开奖时间
                return true;
            }else {
                return false;
            }
        } catch (ParseException e) {
            log.error("开奖时间解析错误", e);
        }
        return false;
    }

    // 添加banner内容

    private void appendBannerContent(HomePageBean bean, LotteryHomePageDTO lotteryHomePageDTO, String fileName) {
        String bannnerFile = FileConstant.HOME_PAGE_CONFIG_PATH + fileName + "_banner_new.xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(bannnerFile));

        BannerListDTO bannerListDTO = appendBannerNode(bean, xml,"bannerlist", "banner");
        lotteryHomePageDTO.setBannerList(bannerListDTO);

        NoticeListDTO noticeListDTO = appendNoticeNode(bean, xml, "noticelist", "notice");
        lotteryHomePageDTO.setNoticeList(noticeListDTO);

        TopicListDTO topicListDTO = appendTopicNode(bean, xml, "topicList", "topic");
        lotteryHomePageDTO.setWorldCupTopic(topicListDTO);
    }

    private void appendOperationContent(HomePageBean bean, LotteryHomePageDTO lotteryHomePageDTO, String fileName) {
        String operationFile = FileConstant.HOME_PAGE_CONFIG_PATH + fileName + "_operation_new.xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(operationFile));

        List<OperationStyleDTO> operationStyleDTOList = appendOperationNode(bean, xml, "operation-list", "operation-style", "operation");
        lotteryHomePageDTO.setOperationList(operationStyleDTOList);

        List<BottomIconDTO> bottomIconDTOList = appendBottomIconNode(bean, xml, "icon-bottom", "icon");
        lotteryHomePageDTO.setBottomIconList(bottomIconDTOList);
    }

    private List<OperationStyleDTO> appendOperationNode(HomePageBean bean, JXmlWrapper xml, String nodeName, String childNodeName, String grandChildNodeName) {
        List<OperationStyleDTO> operationStyleDTOList = new ArrayList<>();
        OperationStyleDTO operationStyleDTO;
        List<OperationDTO> operationDTOList;
        OperationDTO operationDTO;

        JXmlWrapper node = xml.getXmlNode(nodeName);
        List<JXmlWrapper> childNodeList = node.getXmlNodeList(childNodeName);

        for (JXmlWrapper childNode : childNodeList) {
            operationStyleDTO = new OperationStyleDTO();

            operationStyleDTO.setHaveSpace(childNode.getStringValue("@haveSpace"));
            operationStyleDTO.setStyle(childNode.getStringValue("@style"));
            operationStyleDTO.setOrder(childNode.getStringValue("@order"));
            operationStyleDTO.setHaveTitle(childNode.getStringValue("@haveTitle"));
            operationStyleDTO.setTitleAdSrc(childNode.getStringValue("@titleAdSrc"));
            operationStyleDTO.setTitleIOSsrc2X(childNode.getStringValue("@titleIOSsrc2X"));
            operationStyleDTO.setTitleIOSsrc3X(childNode.getStringValue("@titleIOSsrc3X"));
            operationStyleDTO.setTitleHeightAD(childNode.getStringValue("@titleHeightAD"));
            operationStyleDTO.setTitleHeightIOS(childNode.getStringValue("@titleHeightIOS"));
            operationStyleDTO.setAdImgHeight(childNode.getStringValue("@adImgHeight"));
            operationStyleDTO.setAdImgWidth(childNode.getStringValue("@adImgWidth"));
            operationStyleDTO.setIOSImgHeight(childNode.getStringValue("@iOSImgHeight"));

            List<JXmlWrapper> grandChildNodeList = childNode.getXmlNodeList(grandChildNodeName);
            operationDTOList = new ArrayList<>();
            for(JXmlWrapper granChildNode:grandChildNodeList){
                JXmlWrapper generalRules = granChildNode.getXmlNode("general-rules");
                boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
                if(flag){
                    operationDTO = new OperationDTO();
                    operationDTO.setEvid(granChildNode.getStringValue("@evid"));
                    operationDTO.setAdsrc(granChildNode.getStringValue("@adsrc"));
                    operationDTO.setIOSsrc2X(granChildNode.getStringValue("@iOSsrc2X"));
                    operationDTO.setIOSsrc3X(granChildNode.getStringValue("@iOSsrc3X"));
                    operationDTO.setAdlink(granChildNode.getStringValue("@adlink"));
                    operationDTO.setIOSlink(granChildNode.getStringValue("@iOSlink"));
                    operationDTO.setTitle(granChildNode.getStringValue("@title"));

                    operationDTOList.add(operationDTO);
                }
            }
            operationStyleDTO.setOperation(operationDTOList);
            // 修改ios的显示bug(在内容为空时还保留内容位置)
            if (operationDTOList.isEmpty()) {
                continue;
            }
            operationStyleDTOList.add(operationStyleDTO);
        }
        return operationStyleDTOList;
    }

    private List<BottomIconDTO> appendBottomIconNode(HomePageBean bean, JXmlWrapper xml, String nodeName, String childNodeName) {
        List<BottomIconDTO> bottomIconDTOList = new ArrayList<>();
        BottomIconDTO bottomIconDTO;
        JXmlWrapper node = xml.getXmlNode(nodeName);
        List<JXmlWrapper> childNodeList = node.getXmlNodeList(childNodeName);
        for (JXmlWrapper childNode : childNodeList) {
            JXmlWrapper generalRules = childNode.getXmlNode("general-rules");
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                bottomIconDTO = new BottomIconDTO();
                bottomIconDTO.setLabel(childNode.getStringValue("@label"));
                bottomIconDTO.setTitle(childNode.getStringValue("@title"));
                bottomIconDTO.setColor(childNode.getStringValue("@color"));
                bottomIconDTO.setClickColor(childNode.getStringValue("@click_color"));
                bottomIconDTO.setAdsrc(childNode.getStringValue("@ad_src"));
                bottomIconDTO.setClickAdSrc(childNode.getStringValue("@click_ad_src"));
                bottomIconDTO.setIos2xSrc(childNode.getStringValue("@ios2x_src"));
                bottomIconDTO.setClickIOS2xSrc(childNode.getStringValue("@click_ios2x_src"));
                bottomIconDTO.setIos1xSrc(childNode.getStringValue("@ios1x_src_src"));
                bottomIconDTO.setClickIOS1xSrc(childNode.getStringValue("@click_ios1x_src_src"));

                bottomIconDTOList.add(bottomIconDTO);
            }
        }
        return bottomIconDTOList;
    }

    private TopicListDTO appendTopicNode(HomePageBean bean, JXmlWrapper xml, String nodeName, String childNodeName){
        TopicListDTO topicListDTO = new TopicListDTO();
        BannerDTO topicDTO;
        List<BannerDTO> topicList = new ArrayList<>();
        JXmlWrapper node = xml.getXmlNode(nodeName);
        List<JXmlWrapper> childNodeList = node.getXmlNodeList(childNodeName);
        topicListDTO.setOrder(node.getStringValue("@order"));
        topicListDTO.setHaveSpace(node.getStringValue("@haveSpace"));
        topicListDTO.setHaveTitle(node.getStringValue("@haveTitle"));
        topicListDTO.setTitleAdSrc(node.getStringValue("@titleAdSrc"));
        topicListDTO.setTitleIOSsrc(node.getStringValue("@titleIOSsrc"));
        topicListDTO.setTitleIOSsrc2X(node.getStringValue("@titleIOSsrc2X"));
        topicListDTO.setTitleIOSsrc3X(node.getStringValue("@titleIOSsrc3X"));
        topicListDTO.setTitleHeightAD(node.getStringValue("@titleHeightAD"));
        topicListDTO.setTitleHeightIOS(node.getStringValue("@titleHeightIOS"));
        topicListDTO.setAdImgWidth(node.getStringValue("@adImgWidth"));
        topicListDTO.setAdImgHeight(node.getStringValue("@adImgHeight"));

        for (JXmlWrapper childNode : childNodeList) {
            JXmlWrapper generalRules = childNode.getXmlNode("general-rules");
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                topicDTO = new BannerDTO();

                topicDTO.setEvid(childNode.getStringValue("@evid"));
                topicDTO.setTitle(childNode.getStringValue("@title"));
                topicDTO.setAdsrc(childNode.getStringValue("@adsrc"));
                topicDTO.setIOSsrc(childNode.getStringValue("@iOSsrc"));
                topicDTO.setIOSsrc2X(childNode.getStringValue("@iOSsrc2X"));
                topicDTO.setIOSsrc3X(childNode.getStringValue("@iOSsrc3X"));
                topicDTO.setLinkUrl(childNode.getStringValue("@linkUrl"));


                topicList.add(topicDTO);
            }
        }
        topicListDTO.setTopic(topicList);

        return topicListDTO;
    }
    private NoticeListDTO appendNoticeNode(HomePageBean bean, JXmlWrapper xml, String nodeName, String childNodeName) {
        JXmlWrapper node = xml.getXmlNode(nodeName);
        List<JXmlWrapper> childNodeList = node.getXmlNodeList(childNodeName);
        NoticeListDTO noticeListDTO = new NoticeListDTO();
        List<NoticeDTO> noticeDTOList = new ArrayList<>();
        NoticeDTO noticeDTO;
        noticeListDTO.setOrder(node.getStringValue("@order"));
        noticeListDTO.setAuto(node.getStringValue("@auto"));
        noticeListDTO.setHaveSpace(node.getStringValue("@haveSpace"));

        if ("1".equals(node.getStringValue("@auto"))) {
            noticeDTOList = appendLatestNotice(2);
        } else {
            for (JXmlWrapper childNode : childNodeList) {
                JXmlWrapper generalRules = childNode.getXmlNode("general-rules");
                boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
                if (flag) {
                    noticeDTO = new NoticeDTO();
                    noticeDTO.setAdlink(childNode.getStringValue("@adlink"));
                    noticeDTO.setDesc(childNode.getStringValue("@desc"));
                    noticeDTO.setEvid(childNode.getStringValue("@evid"));
                    noticeDTO.setIOSlink(childNode.getStringValue("@iOSlink"));
                    noticeDTO.setNewiOSlink(childNode.getStringValue("@newiOSlink"));

                    noticeDTOList.add(noticeDTO);
                }
            }
        }
        noticeListDTO.setNotice(noticeDTOList);
        return noticeListDTO;
    }
    //添加最近几天的公告内容
    private List<NoticeDTO> appendLatestNotice(int day) {
        List<NoticeDTO> noticeDTOList = new ArrayList<>();
        NoticeDTO noticeDTO;
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.APP_NOTICE_FILE));
        List<JXmlWrapper> noticeList = xml.getXmlNodeList("row");
        for(JXmlWrapper notice:noticeList){
            long ndate = notice.getLongValue("@ndate")*1000;
            long now = new Date().getTime();
            double period = (now-ndate)/(1000*3600*24);
            if(day>period){
                noticeDTO = new NoticeDTO();
                noticeDTO.setAdlink(notice.getStringValue("@arcurl"));
                noticeDTO.setDesc(notice.getStringValue("@ntitle"));
                noticeDTO.setEvid("xxgg");
                noticeDTO.setIOSlink(notice.getStringValue("@arcurl"));
                noticeDTOList.add(noticeDTO);
            }else{
                continue;
            }
        }
        return noticeDTOList;
    }
    //將指定节点内容按新的nodeName写入builder
    private BannerListDTO appendBannerNode(HomePageBean bean, JXmlWrapper xml, String nodeName, String childNodeName){
        JXmlWrapper node = xml.getXmlNode(nodeName);
        List<JXmlWrapper> childNodeList = node.getXmlNodeList(childNodeName);
        BannerListDTO bannerListDTO = new BannerListDTO();
        List<BannerDTO> bannerDTOList = new ArrayList<>();
        BannerDTO bannerDTO;
        bannerListDTO.setOrder(node.getStringValue("@order"));
        bannerListDTO.setHaveSpace(node.getStringValue("@haveSpace"));
        for (JXmlWrapper childNode : childNodeList) {
            JXmlWrapper generalRules = childNode.getXmlNode("general-rules");
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                bannerDTO = new BannerDTO();
                bannerDTO.setAdlink(childNode.getStringValue("@adlink"));
                bannerDTO.setAdsrc(childNode.getStringValue("@adsrc"));
                bannerDTO.setEvid(childNode.getStringValue("@evid"));
                bannerDTO.setIOSlink(childNode.getStringValue("@iOSlink"));
                bannerDTO.setIOSsrc(childNode.getStringValue("@iOSsrc"));
                bannerDTO.setIOSsrc6(childNode.getStringValue("@iOSsrc6"));
                bannerDTO.setIOSsrc6p(childNode.getStringValue("@iOSsrc6p"));
                bannerDTO.setIossrciphonex(childNode.getStringValue("@iOSsrcIPhoneX"));
                bannerDTO.setParamAd(childNode.getStringValue("@paramAd"));
                bannerDTO.setTitle(childNode.getStringValue("@title"));

                bannerDTOList.add(bannerDTO);
            }
        }
        bannerListDTO.setBanner(bannerDTOList);
        return bannerListDTO;
    }

    // 获取符合规则的指定的文件名
    private String getSpecifiedFile(HomePageBean bean) {
        JXmlWrapper homePageConfig = JXmlWrapper.parse(new File(FileConstant.HOME_PAGE_CONFIG));
        List<JXmlWrapper> categoryList = homePageConfig.getXmlNodeList("category");
        for (JXmlWrapper category : categoryList) {
            JXmlWrapper generalRules = category.getXmlNode("general-rules");
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                String fileName = category.getStringValue("@fileName");
                return fileName;
            }
        }
        return null;
    }
}
