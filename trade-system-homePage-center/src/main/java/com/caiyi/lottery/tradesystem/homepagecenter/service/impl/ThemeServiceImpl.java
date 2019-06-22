package com.caiyi.lottery.tradesystem.homepagecenter.service.impl;

import bean.HomePageBean;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.homepagecenter.service.ThemeService;
import com.caiyi.lottery.tradesystem.ordercenter.clientwrapper.OrderBasicWrapper;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.ParseGeneralRulesUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.proj.LiveBfUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import dto.*;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wxy
 * @create 2018-01-18 11:00
 **/
@Slf4j
@Service
public class ThemeServiceImpl implements ThemeService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrderBasicWrapper orderBasicWrapper;

    @Override
    public ThemDTO themeStart(HomePageBean bean) {
        log.info("主题启动页启动,启动类型:" + bean.getThemeType());
        StringBuilder builder = new StringBuilder();
        String bannerFile = "";
        if ("1".equals(bean.getThemeType())) {
            bannerFile = FileConstant.FOOTBALL_BANNER_FILE;
        } else if ("2".equals(bean.getThemeType())) {
            bannerFile = FileConstant.BASKETBALL_BANNER_FILE;
        } else if ("3".equals(bean.getThemeType())) {
            bannerFile = FileConstant.WELFARE_BANNER_FILE;
        } else {
            log.info("未选择主题包,版本号:" + bean.getAppversion() + " mtype:" + bean.getMtype());
            return null;
        }
        JXmlWrapper xml = JXmlWrapper.parse(new File(bannerFile));
        builder.append("<Resp code=\"0\" desc=\"\">");
        // 首页banner信息加载
        appendThemeBanner(builder, bean, xml);
        // 快捷登录信息加载
        appendQuickEnter(builder, bean, xml);
        // 添加主体数据
        appendThemeData(builder, bean, xml);

        builder.append(" </Resp>");
        ThemDTO themDTO = praseToDTO(bean, builder.toString());
        return themDTO;
    }

    /**
     * 转成对象
     * @param bean
     * @param xmlStr
     * @return
     */
    private ThemDTO praseToDTO(HomePageBean bean, String xmlStr) {
        ThemDTO themDTO = new ThemDTO();
        List<BannerDTO> bannerDTOList = new ArrayList<>();
        BannerDTO bannerDTO;
        List<QuickEnterDTO> quickEnterDTOList = new ArrayList<>();
        QuickEnterDTO quickEnterDTO;
        MatchListDTO matchListDTO = new MatchListDTO();
        List<MatchDTO> matchDTOList = new ArrayList<>();
        MatchDTO matchDTO = new MatchDTO();
        List<LotteryDTO> lotteryDTOList = new ArrayList<>();
        LotteryDTO lotteryDTO;

        JXmlWrapper xml = JXmlWrapper.parse(xmlStr);
        if (xml == null) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("数据转换失败");
            return null;
        }

        JXmlWrapper bannersNode = xml.getXmlNode("banners");
        List<JXmlWrapper> bannerNodeList = bannersNode.getXmlNodeList("banner");
        for (JXmlWrapper bannerNode : bannerNodeList) {
            bannerDTO = new BannerDTO();
            bannerDTO.setAdsrc(bannerNode.getStringValue("@newsrcad"));
            bannerDTO.setIOSsrc(bannerNode.getStringValue("@newsrcios5"));
            bannerDTO.setIOSsrc6(bannerNode.getStringValue("@newsrcios6"));
            bannerDTO.setIOSsrc6p(bannerNode.getStringValue("@newsrcios6p"));
            bannerDTO.setLink(bannerNode.getStringValue("@newlink"));
            bannerDTO.setTitle(bannerNode.getStringValue("@title"));
            bannerDTO.setEvid(bannerNode.getStringValue("@evid"));

            bannerDTOList.add(bannerDTO);
        }

        List<JXmlWrapper> quickNodeList = xml.getXmlNode("quickenters").getXmlNodeList("quickenter");
        for (JXmlWrapper quickNode : quickNodeList) {
            quickEnterDTO = new QuickEnterDTO();
            quickEnterDTO.setEvid(quickNode.getStringValue("@evid"));
            quickEnterDTO.setTitle(quickNode.getStringValue("@title"));
            quickEnterDTO.setSrc(quickNode.getStringValue("@newsrc"));
            quickEnterDTO.setLink(quickNode.getStringValue("@newlink"));
            quickEnterDTO.setColor(quickNode.getStringValue("@color"));
            quickEnterDTO.setDesc(quickNode.getStringValue("@desc"));

            quickEnterDTOList.add(quickEnterDTO);
        }

        JXmlWrapper matchsNode = xml.getXmlNode("data");
        matchListDTO.setLogoUrl(matchsNode.getStringValue("@newlogourl"));

        if ("1".equals(bean.getThemeType())) {
            List<JXmlWrapper> matchNodeList = matchsNode.getXmlNodeList("match");
            for (JXmlWrapper matchNode : matchNodeList) {
                matchDTO = new MatchDTO();
                matchDTO.setRid(matchNode.getStringValue("@rid"));
                matchDTO.setSid(matchNode.getStringValue("@sid"));
                matchDTO.setLn(matchNode.getStringValue("@ln"));
                matchDTO.setTime(matchNode.getStringValue("@time"));
                matchDTO.setHtime(matchNode.getStringValue("@htime"));
                matchDTO.setHn(matchNode.getStringValue("@hn"));
                matchDTO.setGn(matchNode.getStringValue("@gn"));
                matchDTO.setHid(matchNode.getStringValue("@hid"));
                matchDTO.setGid(matchNode.getStringValue("@gid"));
                matchDTO.setHsc(matchNode.getStringValue("@hsc"));
                matchDTO.setGsc(matchNode.getStringValue("@asc"));
                matchDTO.setHalfScore(matchNode.getStringValue("@halfsc"));
                matchDTO.setType(matchNode.getStringValue("@type"));
                matchDTO.setJn(matchNode.getStringValue("@jn"));
                matchDTO.setRoundItemId(matchNode.getStringValue("@roundItemId"));
                matchDTO.setExpect(matchNode.getStringValue("@qc"));
                matchDTO.setSort(matchNode.getStringValue("sort"));
                matchDTO.setLive(matchNode.getStringValue("@tvlive"));
                matchDTO.setFollow(matchNode.getStringValue("@follow"));
                matchDTO.setViewer(matchNode.getStringValue("@viewer"));

                matchDTOList.add(matchDTO);
            }

            matchListDTO.setMatchs(matchDTOList);
            themDTO.setMatches(matchListDTO);
        } else if ("2".equals(bean.getThemeType())) {
            List<JXmlWrapper> matchNodeList = matchsNode.getXmlNodeList("match");
            for (JXmlWrapper matchNode : matchNodeList) {
                matchDTO = new MatchDTO();
                matchDTO.setSt(matchNode.getStringValue("@st"));
                matchDTO.setHsc(matchNode.getStringValue("@hsc"));
                matchDTO.setGsc(matchNode.getStringValue("@gsc"));
                matchDTO.setJn(matchNode.getStringValue("@jcn"));
                matchDTO.setDown(matchNode.getStringValue("@down"));
                matchDTO.setLn(matchNode.getStringValue("@mn"));
                matchDTO.setHid(matchNode.getStringValue("@hid"));
                matchDTO.setGid(matchNode.getStringValue("@gid"));
                matchDTO.setHfn(matchNode.getStringValue("@hfn"));
                matchDTO.setGfn(matchNode.getStringValue("@gfn"));
                matchDTO.setHn(matchNode.getStringValue("@hn"));
                matchDTO.setGn(matchNode.getStringValue("@gn"));
                matchDTO.setType(matchNode.getStringValue("@status"));
                matchDTO.setTime(matchNode.getStringValue("@mtime"));
                matchDTO.setLive(matchNode.getStringValue("@live"));
                matchDTO.setViewer(matchNode.getStringValue("@visits"));
                matchDTO.setZid(matchNode.getStringValue("@zid"));
                matchDTO.setMid(matchNode.getStringValue("@mid"));
                matchDTO.setExpect(matchNode.getStringValue("@expect"));
                matchDTO.setSort(matchNode.getStringValue("sort"));
                matchDTO.setFollow(matchNode.getStringValue("@follow"));

                matchDTOList.add(matchDTO);
            }

            matchListDTO.setMatchs(matchDTOList);
            themDTO.setMatches(matchListDTO);
        } else {
            List<JXmlWrapper> lotterNodeList = matchsNode.getXmlNodeList("row");
            for (JXmlWrapper lotteryNode : lotterNodeList) {
                lotteryDTO = new LotteryDTO();
                lotteryDTO.setTryCode(lotteryNode.getStringValue("@trycode"));
                lotteryDTO.setAwardTime(lotteryNode.getStringValue("@awardtime"));
                lotteryDTO.setCode(lotteryNode.getStringValue("@code"));
                lotteryDTO.setPid(lotteryNode.getStringValue("@pid"));
                lotteryDTO.setGid(lotteryNode.getStringValue("@gid"));
                lotteryDTO.setPools(lotteryNode.getStringValue("@pools"));
                lotteryDTO.setEndTime(lotteryNode.getStringValue("@endtime"));
                lotteryDTO.setNumber(lotteryNode.getStringValue("@num"));

                lotteryDTOList.add(lotteryDTO);
            }
            themDTO.setLotteries(lotteryDTOList);
        }

        themDTO.setBanners(bannerDTOList);
        themDTO.setQuickEnters(quickEnterDTOList);
        return themDTO;
    }

    // 添加主题应用的banner页面
    private void appendThemeBanner(StringBuilder builder, HomePageBean bean, JXmlWrapper file) {
        JXmlWrapper banners = file.getXmlNode("banners");
        builder.append("<banners>");
        List<JXmlWrapper> bannerList = banners.getXmlNodeList("banner");
        for (JXmlWrapper banner : bannerList) {
            JXmlWrapper generalRules = banner.getXmlNode("general-rules");
            // 通用规则解析
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                ParseGeneralRulesUtil.writeToBuilder(banner, "banner", builder);
            }
        }
        builder.append("</banners>");
    }

    // 添加首页快捷入口
    private void appendQuickEnter(StringBuilder builder, HomePageBean bean, JXmlWrapper file) {
        JXmlWrapper quickEnters = file.getXmlNode("quickenters");
        builder.append("<quickenters>");
        List<JXmlWrapper> quickEnterList = quickEnters.getXmlNodeList("quickenter");
        for (JXmlWrapper quickEnter : quickEnterList) {
            JXmlWrapper generalRules = quickEnter.getXmlNode("general-rules");
            // 通用规则解析
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
            if (flag) {
                ParseGeneralRulesUtil.writeToBuilder(quickEnter, "quickenter", builder);
            }
        }
        builder.append("</quickenters>");
    }

    // 添加主题热门数据
    private void appendThemeData(StringBuilder builder, HomePageBean bean, JXmlWrapper xml) {
        if ("1".equals(bean.getThemeType())) {
            appendFootballMatch(builder, xml);
        } else if ("2".equals(bean.getThemeType())) {
            appendBasketballMatch(builder, xml);
        } else if ("3".equals(bean.getThemeType())) {
            appendWelfareData(builder);
        }
    }

    // 添加足球主题数据
    private void appendFootballMatch(StringBuilder builder, JXmlWrapper xml) {
        // 获取所有竞彩未完赛的对阵信息
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(LiveBfUtil.getPostUrl("footmatch") + "70" + "0");

        JXmlWrapper matchList = redisClient.getXmlString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
        String logoUrlTmp = xml.getXmlNode("logourl").getStringValue("@value");
        String logoUrl = "";
        String newLogoUrl = "";
        //取得相对地址时
        if(!StringUtil.isEmpty(logoUrlTmp) && !logoUrlTmp.startsWith("http://")){
            logoUrl = "http://www.9188.com" + logoUrlTmp;
            newLogoUrl = logoUrlTmp;
        }
        //相对绝对地址时
        if(logoUrlTmp!=null && logoUrlTmp.startsWith("http://")){
            logoUrl = logoUrlTmp;
            newLogoUrl = logoUrlTmp.substring(19);
        }
        builder.append("<data logourl='"+logoUrl +"' newlogourl='"+newLogoUrl+"' >");
        if (matchList == null) {
            log.info("未查询到足球未完赛的对阵信息");
        } else {
            appendHotMatchListData(builder, matchList, "viewer");
        }
        builder.append("</data>");
    }

    // 添加篮球主题数据
    private void appendBasketballMatch(StringBuilder builder, JXmlWrapper xml) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey("basketunfinish");
        JXmlWrapper matchList = redisClient.getXmlString(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
        String logoUrlTmp = xml.getXmlNode("logourl").getStringValue("@value");
        String logoUrl = "";
        String newLogoUrl = "";
        //取得相对地址时
        if(!StringUtil.isEmpty(logoUrlTmp) && !logoUrlTmp.startsWith("http://")){
            logoUrl = "http://mobile.9188.com" + logoUrlTmp;
            newLogoUrl = logoUrlTmp;
        }
        //相对绝对地址时
        if(logoUrlTmp!=null && logoUrlTmp.startsWith("http://")){
            logoUrl = logoUrlTmp;
            newLogoUrl = logoUrlTmp.substring(22);
        }
        builder.append("<data logourl='" + logoUrl +"' newlogourl='" + newLogoUrl + "' >");
        if(matchList == null){
            log.info("未查询到篮球未完赛的对阵信息");
        }else{
            appendHotMatchListData(builder, matchList, "visits");
        }
        builder.append("</data>");

    }

    // 提价福利彩票主题数据
    private void appendWelfareData(StringBuilder builder) {
        String path_result = "/opt/export/data/app/lottery_results.xml";
        String path_indexdesc = "/opt/export/data/app/index_desc.xml";
        String path = "/opt/export/data/phot/";
        JXmlWrapper lottery_results = JXmlWrapper.parse(new File(path_result));
        JXmlWrapper index_desc = JXmlWrapper.parse(new File(path_indexdesc));
        builder.append("<data>");
        String ssq=lottery_results.getStringValue("row[0].@awardtime");
        String dlt=lottery_results.getStringValue("row[1].@awardtime");

        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar c1=java.util.Calendar.getInstance();
        java.util.Calendar c2=java.util.Calendar.getInstance();
        try {
            c1.setTime(df.parse(ssq));
            c2.setTime(df.parse(dlt));
            int index=0;
            if (c2.compareTo(c1)<0) {//大乐透最新开奖时间在双色球之前,则显示大乐透内容
                index=1;
            }
            builder.append("<row ");
            String trycode = lottery_results.getStringValue("row["+index+"].@trycode");
            XmlUtil.append(builder, "trycode", trycode);
            String awardtime = lottery_results.getStringValue("row["+index+"].@awardtime");
            XmlUtil.append(builder, "awardtime", awardtime);
            String code = lottery_results.getStringValue("row["+index+"].@code");
            XmlUtil.append(builder, "code", code);
            String pid = lottery_results.getStringValue("row["+index+"].@pid");
            XmlUtil.append(builder, "pid", pid);
            String gid = lottery_results.getStringValue("row["+index+"].@gid");
            XmlUtil.append(builder, "gid", gid);
            List<JXmlWrapper> rowList = index_desc.getXmlNodeList("row");
            for(JXmlWrapper row : rowList){
                if(row.getStringValue("@gid").equals(gid)){
                    String pools = row.getStringValue("@pools");
                    XmlUtil.append(builder, "pools", pools);
                    break;
                }
            }
            String latestPath = path+gid+"/s.xml";
            JXmlWrapper latestXml = JXmlWrapper.parse(new File(latestPath));
            String endtime = latestXml.getStringValue("row[0].@et");
            SimpleDateFormat parseSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatSdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = DateUtil.getCurrentFormatDate("yyyy-MM-dd");
            String et = "";
            if(now.equals(formatSdf.format(parseSdf.parse(endtime)))){
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                et = "今日 "+sdf1.format(parseSdf.parse(endtime));
            }else{
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm");
                et = sdf2.format(parseSdf.parse(endtime));
            }
            XmlUtil.append(builder, "endtime", et);

            OrderBean orderBean = new OrderBean();
            orderBean.setGid(gid);
            orderBean.setPid(pid);
            Integer num = orderBasicWrapper.queryBetNum(orderBean, log, SysCodeConstant.HOMEPAGECENTER);
            XmlUtil.append(builder, "num", num);
            builder.append(" />");
        } catch (ParseException e) {
            log.error("时间格式转化错误,双色球:"+ssq+" 大乐透:"+dlt, e);
        }
        builder.append("</data>");
    }

    private void appendHotMatchListData(StringBuilder builder, JXmlWrapper matchList, String watchNumField) {
        List<JXmlWrapper> dayMatchList = matchList.getXmlNodeList("rows");
        int daycount = 0;
        TreeMap<Double, JXmlWrapper> sortedMap = new TreeMap<Double, JXmlWrapper>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o2.compareTo(o1);  // 降序排列
            }
        });
        for(JXmlWrapper dayMatch : dayMatchList){
            daycount ++;
            if(daycount>2){//只取两天内的比赛场次
                break;
            }else{
                List<JXmlWrapper> dayMatches = dayMatch.getXmlNodeList("row");
                for(JXmlWrapper match:dayMatches){
                    double viewer = match.getDoubleValue("@"+watchNumField+"");
                    while(sortedMap.containsKey(viewer)){
                        viewer = viewer + 0.1;
                    }
                    sortedMap.put(viewer, match);
                }
            }
        }
        Set<Double> viewerKey = sortedMap.keySet();
        int matchNum = 0;
        for(Double key : viewerKey){
            matchNum ++;
            if(matchNum>5){
                break;
            }
            JXmlWrapper match = sortedMap.get(key);
            ParseGeneralRulesUtil.writeToBuilder(match, "match", builder);
        }
    }

}
