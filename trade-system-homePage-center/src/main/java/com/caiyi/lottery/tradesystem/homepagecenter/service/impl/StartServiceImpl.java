package com.caiyi.lottery.tradesystem.homepagecenter.service.impl;

import bean.HomePageBean;
import bean.VersionBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.homepagecenter.service.StartService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.BaseUtil;
import com.caiyi.lottery.tradesystem.util.ParseGeneralRulesUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxy
 * @create 2018-01-18 15:26
 **/
@Slf4j
@Service
public class StartServiceImpl implements StartService {
    @Autowired
    private UserBasicInfoWrapper userBasicInfoWrapper;
    @Override
    public StartUpDTO startup(HomePageBean bean) throws Exception {
        StartUpDTO startUpDTO = new StartUpDTO();
        StringBuilder builder = new StringBuilder();
        builder.append("<Resp code=\"0\" desc=\"\">");
        appendLotteryReminder(bean, builder);
        appendGodItemControl(bean, builder);
        appendReadMode(bean, builder);
        appendUserWhiteGrade(bean, builder);
        appendServiceHotline(bean, builder);
        appendStartImg(bean, builder);
        appendBanActivityAndroid(bean, builder);
        appendBanKeyWord(bean, builder);
        appendCheckStatus(bean, builder);
        appendItemListDesc(builder);
        appendSmsControl(builder);
        builder.append(" </Resp>");
        startUpDTO = praseToDTO(bean, builder.toString());
        appendWorldCupFlag(startUpDTO);
        //appendRedpacketRemindFlag(startUpDTO);
        return startUpDTO;
    }

    //添加世界杯开售标识和乐善彩标识
    private void appendWorldCupFlag(StartUpDTO startUpDTO) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.GOIDITEM_SWITCH_FILE));
        JXmlWrapper worldCup = xml.getXmlNode("worldCup");
        String worldCupFlag = worldCup.getStringValue("@value");
        startUpDTO.setWorldCupFlag(worldCupFlag);
        JXmlWrapper leShanNode = xml.getXmlNode("leshan");
        if (leShanNode != null) {
            startUpDTO.setLeShanFlag(leShanNode.getStringValue("@value"));
        }
    }

    /**
     * 转成返回对象
     * @param bean
     * @param xmlStr
     * @return
     */
    private StartUpDTO praseToDTO(HomePageBean bean, String xmlStr) {
        StartUpDTO startUpDTO = new StartUpDTO();
        LotteryReminderDTO lotteryReminderDTO = new LotteryReminderDTO();
        HotlineDTO hotlineDTO = new HotlineDTO();
        RechargeMessageDTO rechargeMessageDTO = new RechargeMessageDTO();
        StartImgDTO startImgDTO = new StartImgDTO();
        RecordBottomHintDTO recordBottomHintDTO = new RecordBottomHintDTO();
        ReviewStateDTO reviewStateDTO = new ReviewStateDTO();

        if (StringUtil.isEmpty(xmlStr)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("配置文件加载失败");
            return null;
        }
        JXmlWrapper xml = JXmlWrapper.parse(xmlStr);

        JXmlWrapper lotteryReminderNode = xml.getXmlNode("lotteryReminder");
        if (lotteryReminderNode != null) {
            lotteryReminderDTO.setAll(lotteryReminderNode.getStringValue("@switch"));
            lotteryReminderDTO.setSsq(lotteryReminderNode.getStringValue("@ssq"));
            lotteryReminderDTO.setDlt(lotteryReminderNode.getStringValue("@dlt"));
            lotteryReminderDTO.setFc3d(lotteryReminderNode.getStringValue("@fc3d"));
            lotteryReminderDTO.setQxc(lotteryReminderNode.getStringValue("@qxc"));
            lotteryReminderDTO.setQlc(lotteryReminderNode.getStringValue("@qlc"));
            lotteryReminderDTO.setPl3(lotteryReminderNode.getStringValue("@pl3"));
            lotteryReminderDTO.setPl5(lotteryReminderNode.getStringValue("@pl5"));
        }

        JXmlWrapper hotlineNode = xml.getXmlNode("hotline");
        if (hotlineNode != null) {
            hotlineDTO.setDesc(hotlineNode.getStringValue("@desc"));
            hotlineDTO.setPhoneNo(hotlineNode.getStringValue("@phoneNo"));
            hotlineDTO.setUrl(hotlineNode.getStringValue("@newurl"));
        }

        JXmlWrapper rechargeMessageNode = xml.getXmlNode("rechargeMessage");
        if (rechargeMessageNode != null) {
            rechargeMessageDTO.setTopMessage(rechargeMessageNode.getStringValue("@topMessage"));
            rechargeMessageDTO.setMaxMoney(rechargeMessageNode.getStringValue("@maxMoney"));
            rechargeMessageDTO.setMaxMessage(rechargeMessageNode.getStringValue("@maxMessage"));
        }

        JXmlWrapper startImgNode = xml.getXmlNode("startImg");
        if (startImgNode != null) {
            startImgDTO.setAdsrc(startImgNode.getStringValue("@newadsrc"));
            startImgDTO.setIOSsrc(startImgNode.getStringValue("@newiOSsrc"));
            startImgDTO.setIOSsrc6(startImgNode.getStringValue("@newiOSsrc6"));
            startImgDTO.setIOSsrc6p(startImgNode.getStringValue("@newiOSsrc6p"));
            startImgDTO.setIOSsrcIPhoneX(startImgNode.getStringValue("@newiOSsrcIPhoneX"));
            startImgDTO.setAdlink(startImgNode.getStringValue("@newadlink"));
            startImgDTO.setIOSlink(startImgNode.getStringValue("@newiOSlink"));
        }

        JXmlWrapper recordBottomHintNode = xml.getXmlNode("itemlist");
        if (recordBottomHintNode != null) {
            recordBottomHintDTO.setAll(recordBottomHintNode.getStringValue("@all"));
            recordBottomHintDTO.setZh(recordBottomHintNode.getStringValue("@zh"));
        }

        JXmlWrapper reviewStateNode = xml.getXmlNode("version");
        if (reviewStateNode != null) {
            reviewStateDTO.setCode(reviewStateNode.getStringValue("@code"));
            reviewStateDTO.setState(reviewStateNode.getStringValue("@state"));
        } else {
            reviewStateDTO.setCode(bean.getRversion());
            reviewStateDTO.setState("0");
        }

        startUpDTO.setGodItemControl(xml.getXmlNode("godItemControl").getStringValue("@switch"));
        startUpDTO.setReadMode(xml.getXmlNode("readMode").getStringValue("@switch"));
        startUpDTO.setWhiteGrade(xml.getXmlNode("whiteGrade").getStringValue("@value"));
        startUpDTO.setRechargeControl(xml.getXmlNode("adBanActivity").getStringValue("@switch"));
        startUpDTO.setBanWord(xml.getXmlNode("banWord").getStringValue("@value"));
        startUpDTO.setImageCaptchaControl(xml.getXmlNode("sms").getStringValue("@value"));
        RedpacketRemindDTO rpdto=new RedpacketRemindDTO();
        rpdto.setRpMoney(xml.getXmlNode("rpRemind").getIntValue("@remindMoney"));
        rpdto.setRpImg(xml.getXmlNode("rpRemind").getStringValue("@remindImg"));
        startUpDTO.setRpRemind(rpdto);

        startUpDTO.setLotteryReminder(lotteryReminderDTO);
        startUpDTO.setHotline(hotlineDTO);
        startUpDTO.setRechargeMessage(rechargeMessageDTO);
        startUpDTO.setStartImg(startImgDTO);
        startUpDTO.setRecordBottomHint(recordBottomHintDTO);
        startUpDTO.setReviewState(reviewStateDTO);

        return startUpDTO;
    }

    private void appendLotteryReminder(HomePageBean bean, StringBuilder builder) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.LOTTERY_REMINDER_FILE));
        JXmlWrapper itemList = xml.getXmlNode("reminder");
        builder.append("<lotteryReminder ");
        XmlUtil.append(builder, "switch", itemList.getShortValue("@open"));

        List<JXmlWrapper> nodes = itemList.getXmlNodeList("key");
        for(JXmlWrapper row : nodes){
            XmlUtil.append(builder,row.getStringValue("@val"),row.getStringValue("@flag"));
        }
        builder.append(" />");
    }

    //添加大神分享开关
    private void appendGodItemControl(HomePageBean bean, StringBuilder builder) {
        builder.append("<godItemControl ");
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.GOIDITEM_SWITCH_FILE));
        List<JXmlWrapper> nodeList = xml.getXmlNodeList("goditem");
        for(JXmlWrapper node : nodeList){
            String openFlag =  node.getXmlNode("business-rules").getXmlNode("open").getStringValue("@flag");
            if("1".equals(openFlag)){
                if(ParseGeneralRulesUtil.parseGeneralRulesNew(node.getXmlNode("general-rules"), bean, log)){
                    XmlUtil.append(builder, "switch", "1");//神单功能开启
                    builder.append(" />");
                    return;
                }
            }
        }
        XmlUtil.append(builder, "switch", "0");//神单功能关闭
        builder.append(" />");
    }

    //添加阅读模式
    private void appendReadMode(HomePageBean bean, StringBuilder builder) {
        builder.append("<readMode ");
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.GOIDITEM_SWITCH_FILE));
        List<JXmlWrapper> nodeList = xml.getXmlNodeList("readMode");
        for(JXmlWrapper node : nodeList){
            String openFlag =  node.getXmlNode("business-rules").getXmlNode("open").getStringValue("@flag");
            if("1".equals(openFlag)){
                if(ParseGeneralRulesUtil.parseGeneralRulesNew(node.getXmlNode("general-rules"), bean, log)){
                    XmlUtil.append(builder, "switch", "1");//阅读功能开启
                    builder.append(" />");
                    return;
                }
            }
        }
        XmlUtil.append(builder, "switch", "0");//阅读功能关闭
        builder.append(" />");
    }

    //添加用户白名单
    private void appendUserWhiteGrade(HomePageBean bean, StringBuilder builder) {
        builder.append("<whiteGrade ");
        //查询用户白名单
        String whiteGrade = null;
        if(!StringUtil.isEmpty(bean.getUid())){
            whiteGrade = userBasicInfoWrapper.queryUserWhiteGrade(bean, log, SysCodeConstant.HOMEPAGECENTER);
            if(StringUtil.isEmpty(whiteGrade)){
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("白名单获取失败 用户名:" + bean.getUid());
            }
        }
        if(StringUtil.isEmpty(whiteGrade)){
            XmlUtil.append(builder, "value", 0);
        }else{
            XmlUtil.append(builder, "value", whiteGrade);
        }
        builder.append(" />");
    }

    //添加在线客服热线
    private void appendServiceHotline(HomePageBean bean, StringBuilder builder) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.SERVICE_HOTLINE_FILE));
        List<JXmlWrapper> hotlineList = xml.getXmlNodeList("hotline");
        //将XML文件下的每个content进行解析
        for(int i=0;i<hotlineList.size();i++){
            JXmlWrapper generalRules = hotlineList.get(i).getXmlNode("general-rules");
            //通用规则解析
            boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules,bean,log);
            if(flag){
                ParseGeneralRulesUtil.writeToBuilder(hotlineList.get(i) ,"hotline" ,builder);
            }
        }
        List<JXmlWrapper> rechargelist = xml.getXmlNodeList("rechargeMessage");
        ParseGeneralRulesUtil.writeToBuilder(rechargelist.get(0) ,"rechargeMessage" ,builder);
    }

    //添加启动页
    private void appendStartImg(HomePageBean bean, StringBuilder builder) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.START_IMG_FILE));
        List<JXmlWrapper> nodeList = xml.getXmlNodeList("startImg");
        for(JXmlWrapper node : nodeList){
            if(ParseGeneralRulesUtil.parseGeneralRulesNew(node.getXmlNode("general-rules"), bean, log)){
                ParseGeneralRulesUtil.writeToBuilder(node, "startImg", builder);
                return;
            }
        }
    }

    //添加安卓禁止充值状态
    private void appendBanActivityAndroid(HomePageBean bean, StringBuilder builder) {
        builder.append("<adBanActivity ");
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.BAN_ACTIVITY_FILE));
        List<JXmlWrapper> banNodeList = xml.getXmlNodeList("ban-activity");
        for(JXmlWrapper banNode : banNodeList){
            String openFlag =  banNode.getXmlNode("business-rules").getXmlNode("open").getStringValue("@flag");
            if("1".equals(openFlag)){
                if(ParseGeneralRulesUtil.parseGeneralRulesNew(banNode.getXmlNode("general-rules"), bean, log)){
                    XmlUtil.append(builder, "switch", 1);
                    builder.append(" />");
                    return;
                }
            }
        }
        XmlUtil.append(builder, "switch", 0);
        builder.append(" />");
    }

    //添加禁止关键字
    private void appendBanKeyWord(HomePageBean bean, StringBuilder builder) {
        builder.append("<banWord ");
        XmlUtil.append(builder, "value", "like,insert,delete,drop,create,update,select");
        builder.append(" />");
    }

    //添加审核状态
    private void appendCheckStatus(HomePageBean bean, StringBuilder builder) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.NEW_CHECK_STATUS_FILE));
        List<JXmlWrapper> rowList = xml.getXmlNodeList("row");
        for(JXmlWrapper row : rowList){
            String source = row.getStringValue("@source");
            if(source.equals(bean.getSource()+"")){
                List<JXmlWrapper> versionList = row.getXmlNodeList("versions");
                for(JXmlWrapper version : versionList){
                    String code = version.getStringValue("@code").replace(".", "");
                    if(code.equals(bean.getAppversion().replace(".", ""))){
                        builder.append("<version ");
                        XmlUtil.append(builder, "code", version.getStringValue("@code"));
                        XmlUtil.append(builder, "state", version.getStringValue("@state"));
                        builder.append(" />");
                        return;
                    }
                }
            }
        }
    }

    //添加方案列表全部和追号的描述
    private void appendItemListDesc(StringBuilder builder) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.NEW_CHECK_STATUS_FILE));
        JXmlWrapper itemlist = xml.getXmlNode("itemlist");
        String all = itemlist.getStringValue("@all");
        String zh = itemlist.getStringValue("@zh");
        builder.append("<itemlist ");
        XmlUtil.append(builder, "all", all);
        XmlUtil.append(builder, "zh", zh);
        builder.append(" />");
    }

    //添加短信控制
    private void appendSmsControl(StringBuilder builder) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.SMS_CONFIG_FILE));
        JXmlWrapper itemList = xml.getXmlNode("sms");
        builder.append("<sms ");
        XmlUtil.append(builder, "value", itemList.getStringValue("@value"));
        builder.append(" />");
        JXmlWrapper rpRemindNode=xml.getXmlNode("rpRemind");
        builder.append("<rpRemind ");
        XmlUtil.append(builder, "remindMoney", rpRemindNode.getStringValue("@remindMoney"));
        XmlUtil.append(builder, "remindImg", rpRemindNode.getStringValue("@remindImg"));
        builder.append(" />");
    }

    /**
     * 加载配置文件
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public ConfigDTO loadMainConfig(HomePageBean bean) throws Exception {
        String baseconfigUrl = FileConstant.BASE_CONFIG_URL;
        String gameconfigUrl = FileConstant.GAME_CONFIG_URL;
        String telPhoneUrl = FileConstant.TELPHONE_URL;
        String launchimageUrl = FileConstant.LAUNCH_IMAGE_URL;
        String xrlbUrl = FileConstant.XRLB_URL;

        ConfigDTO configDTO = new ConfigDTO();
        List<BaseConfigDTO> versionList = new ArrayList<>();
        BaseConfigDTO versions;
        List<BaseConfigDTO> bizTypeList = new ArrayList<>();
        BaseConfigDTO bizType;
        List<BaseConfigDTO> addMoneyList = new ArrayList<>();
        BaseConfigDTO addMoney;
        List<BaseConfigDTO> bandIdList = new ArrayList<>();
        BaseConfigDTO bankId;
        List<BaseConfigDTO> addAwardList = new ArrayList<>();
        BaseConfigDTO addAward;
        List<BaseConfigDTO> gameList = new ArrayList<>();
        BaseConfigDTO game;
        List<BaseConfigDTO> hotLineList = new ArrayList<>();
        BaseConfigDTO tellPhone;
        HotlineDTO hotlineDTO;
        List<BaseConfigDTO> launchImagList = new ArrayList<>();
        BaseConfigDTO launchImag;
        BaseConfigDTO xrlb;

        String come = bean.getSource() + ""; // 客户端来源
        String ver = bean.getRversion(); //String ver="2.1.32";//客户端当前版本
        BaseConfigDTO app = checkVersion(come, ver);  //检测新版本
        configDTO.setApp(app);

        StringBuilder sb = new StringBuilder();

        String filePart = bean.getChannelId();
        if (filePart != null && !"".equals(filePart)) {
            String otherbaseconfigUrl = "/opt/export/www/mobile/app/ios/base/baseconfig_" + filePart + ".xml";
            try {
                JXmlWrapper xmlWapper = JXmlWrapper.parse(new File(otherbaseconfigUrl));
                List<JXmlWrapper> verList = xmlWapper.getXmlNodeList("versions");
                String code = "";
                for (JXmlWrapper xml : verList) {
                    code = xml.getStringValue("@code");
                    if (strCompare(code, ver)) {
                        String s = xml.toXmlString("ISO-8859-1");
                        s = s.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                        s = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                        sb.append(s);
                        break;
                    }
                }
            } catch (Exception e) {
                //有的版本可能没有这个文件 这里就在没有这个文件的时候忽略
            }
        }

        if (filePart != null && !"".equals(filePart)) {
            String newbaseconfigUrl = FileConstant.NEW_BASE_CONFIGURL;
            try {
                JXmlWrapper xmlWapper = JXmlWrapper.parse(new File(newbaseconfigUrl));
                List<JXmlWrapper> rows = xmlWapper.getXmlNodeList("row");
                for (JXmlWrapper row : rows) {
                    if (strCompare(filePart, row.getStringValue("@source"))) {
                        List<JXmlWrapper> versionsNodeList = row.getXmlNodeList("versions");
                        for (JXmlWrapper version : versionsNodeList) {
                            if (strCompare(version.getStringValue("@code"), ver)) {
                                versions = new BaseConfigDTO();
                                versions.setCode(version.getStringValue("@code"));
                                versions.setTarget(version.getStringValue("@target"));
                                versions.setState(version.getStringValue("@state"));
                                versionList.add(versions);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //有的版本可能没有这个文件 这里就在没有这个文件的时候忽略
            }
        }

        // <biztype> <addmoneytype> <bankid> <huodongjiajian>
        File baseconfigFile = new File(baseconfigUrl);
        JXmlWrapper baseconfigXml = JXmlWrapper.parse(baseconfigFile);

        JXmlWrapper bizTypeNode = baseconfigXml.getXmlNode("biztype");
        if (bizTypeNode != null) {
            List<JXmlWrapper> rowNodeList = bizTypeNode.getXmlNodeList("row");
            for (JXmlWrapper rowNode : rowNodeList) {
                bizType = new BaseConfigDTO();
                bizType.setKey(rowNode.getStringValue("@key"));
                bizType.setValue(rowNode.getStringValue("@value"));
                bizType.setType(rowNode.getStringValue("@type"));

                bizTypeList.add(bizType);
            }
            configDTO.setBiztype(bizTypeList);
        }

        JXmlWrapper addMoneysNode = baseconfigXml.getXmlNode("addmoneytype");
        if (addMoneysNode != null) {
            List<JXmlWrapper> addMoneyNodeList = addMoneysNode.getXmlNodeList("row");
            for (JXmlWrapper addMoneyNode : addMoneyNodeList) {
                addMoney = new BaseConfigDTO();
                addMoney.setKey(addMoneyNode.getStringValue("@key"));
                addMoney.setValue(addMoneyNode.getStringValue("@value"));

                addMoneyList.add(addMoney);
            }
            configDTO.setAddmoneytype(addMoneyList);
        }

        JXmlWrapper bankIdsNode = baseconfigXml.getXmlNode("bankid");
        if (bankIdsNode != null) {
            List<JXmlWrapper> bankIdNodeList = baseconfigXml.getXmlNodeList("row");
            for (JXmlWrapper bankidNode : bankIdNodeList) {
                bankId = new BaseConfigDTO();
                bankId.setKey(bankidNode.getStringValue("@key"));
                bankId.setValue(bankidNode.getStringValue("@value"));

                bandIdList.add(bankId);
            }

            configDTO.setBankid(bandIdList);
        }

        JXmlWrapper addAwardsNode = baseconfigXml.getXmlNode("huodongjiajian");
        if (addAwardsNode != null) {
            List<JXmlWrapper> addAwardNodeList = addAwardsNode.getXmlNodeList("row");
            for (JXmlWrapper addAwardNode : addAwardNodeList) {
                addAward = new BaseConfigDTO();
                addAward.setKey(addAwardNode.getStringValue("@key"));
                addAward.setValue(addAwardNode.getStringValue("@value"));

                addAwardList.add(addAward);
            }

            configDTO.setHuodongjiajian(addAwardList);
        }

        // gameconfig
        try {
            JXmlWrapper gameconfigXML = JXmlWrapper.parse(new File(gameconfigUrl));
            if (gameconfigXML != null) {
                List<JXmlWrapper> gameNodeList = gameconfigXML.getXmlNodeList("row");
                for (JXmlWrapper gameNode : gameNodeList) {
                    game = new BaseConfigDTO();
                    game.setGid(gameNode.getStringValue("@gid"));
                    game.setName(gameNode.getStringValue("@name"));

                    gameList.add(game);
                }
                configDTO.setGame(gameList);
            }
        } catch (Exception e) {
            //gameconfig.xml文件以后可以会移除 所以这里就在没有这个文件的时候忽略
        }

        // tellPhone
        JXmlWrapper telPhoneXML = JXmlWrapper.parse(new File(telPhoneUrl));
        if (telPhoneXML != null) {
            HotlineDTO hotline = new HotlineDTO();
            List<JXmlWrapper> tellPhoneNodeList = telPhoneXML.getXmlNodeList("row");
            for (JXmlWrapper tellPhoneNode : tellPhoneNodeList) {
                tellPhone = new BaseConfigDTO();
                tellPhone.setKey(tellPhoneNode.getStringValue("@key"));
                tellPhone.setValue(tellPhoneNode.getStringValue("@value"));

                hotLineList.add(tellPhone);
            }
            hotline.setHotLine(hotLineList);

            hotline.setStopsale(telPhoneXML.getXmlNode("isstop").getStringValue("@stopsale"));
            hotline.setInweb(telPhoneXML.getXmlNode("showTVInWeb").getStringValue("@inweb"));
            configDTO.setTelphone(hotline);
        }

        // launchimage
        launchImag = getLaunchImage(launchimageUrl, ver);
        launchImagList.add(launchImag);
        configDTO.setLaunchimage(launchImagList);

        JXmlWrapper xrlbXML = JXmlWrapper.parse(new File(xrlbUrl));
        List<JXmlWrapper> xrlbList = xrlbXML.getXmlNodeList("xrlb");
        String source = "";
        for (JXmlWrapper xrlbNode : xrlbList) {
            source = xrlbNode.getStringValue("@source");
            if (strCompare(source, come)) {
                xrlb = new BaseConfigDTO();
                xrlb.setSource(xrlbNode.getStringValue("@source"));
                xrlb.setValue(xrlbNode.getStringValue("@value"));
                configDTO.setXrlb(xrlb);
                break;
            }
        }

        return configDTO;
    }

    private ConfigDTO praseToConfigDTO(HomePageBean bean, String xmlStr) {
        ConfigDTO configDTO = new ConfigDTO();
        BaseConfigDTO app = new BaseConfigDTO();
        List<BaseConfigDTO> versionList = new ArrayList<>();
        BaseConfigDTO versions;
        List<BaseConfigDTO> bizTypeList = new ArrayList<>();
        BaseConfigDTO bizType;
        List<BaseConfigDTO> addMoneyList = new ArrayList<>();
        BaseConfigDTO addMoney;
        List<BaseConfigDTO> bandIdList = new ArrayList<>();
        BaseConfigDTO bankId;
        List<BaseConfigDTO> addAwardList = new ArrayList<>();
        BaseConfigDTO addAward;
        List<BaseConfigDTO> gameList = new ArrayList<>();
        BaseConfigDTO game;
        List<BaseConfigDTO> hotLineList = new ArrayList<>();
        BaseConfigDTO tellPhone;
        HotlineDTO hotlineDTO;
        List<BaseConfigDTO> launchImagList = new ArrayList<>();
        BaseConfigDTO launchImag;
        BaseConfigDTO xrlb;

        JXmlWrapper xml = JXmlWrapper.parse(xmlStr);
        JXmlWrapper appNode = xml.getXmlNode("app");
        if (appNode != null) {
            app.setIsup(appNode.getStringValue("@isup"));
            app.setContent(appNode.getStringValue("@content"));
            app.setUrl(appNode.getStringValue("@url"));
            app.setAversion(appNode.getStringValue("@aversion"));
            configDTO.setApp(app);
        }

        JXmlWrapper newversionNode = xml.getXmlNode("newversion");
        if (newversionNode != null) {
            List<JXmlWrapper> versionsNodeList = newversionNode.getXmlNodeList("versions");
            for (JXmlWrapper versionsNode : versionsNodeList) {
                versions = new BaseConfigDTO();
                versions.setCode(versionsNode.getStringValue("@code"));
                versions.setTarget(versionsNode.getStringValue("@target"));
                versions.setState(versionsNode.getStringValue("@state"));

                versionList.add(versions);
            }
            configDTO.setVersion(versionList);
        }

        JXmlWrapper bizTypeNode = xml.getXmlNode("biztype");
        if (bizTypeNode != null) {
            List<JXmlWrapper> rowNodeList = bizTypeNode.getXmlNodeList("row");
            for (JXmlWrapper rowNode : rowNodeList) {
                bizType = new BaseConfigDTO();
                bizType.setKey(rowNode.getStringValue("@key"));
                bizType.setValue(rowNode.getStringValue("@value"));
                bizType.setType(rowNode.getStringValue("@type"));

                bizTypeList.add(bizType);
            }
            configDTO.setBiztype(bizTypeList);
        }

        JXmlWrapper addMoneysNode = xml.getXmlNode("addmoneytype");
        if (addMoneysNode != null) {
            List<JXmlWrapper> addMoneyNodeList = addMoneysNode.getXmlNodeList("row");
            for (JXmlWrapper addMoneyNode : addMoneyNodeList) {
                addMoney = new BaseConfigDTO();
                addMoney.setKey(addMoneyNode.getStringValue("@key"));
                addMoney.setValue(addMoneyNode.getStringValue("@value"));

                addMoneyList.add(addMoney);
            }
            configDTO.setAddmoneytype(addMoneyList);
        }

        JXmlWrapper bankIdsNode = xml.getXmlNode("bankid");
        if (bankIdsNode != null) {
            List<JXmlWrapper> bankIdNodeList = xml.getXmlNodeList("row");
            for (JXmlWrapper bankidNode : bankIdNodeList) {
                bankId = new BaseConfigDTO();
                bankId.setKey(bankidNode.getStringValue("@key"));
                bankId.setValue(bankidNode.getStringValue("@value"));

                bandIdList.add(bankId);
            }

            configDTO.setBankid(bandIdList);
        }

        JXmlWrapper addAwardsNode = xml.getXmlNode("huodongjiajian");
        if (addAwardsNode != null) {
            List<JXmlWrapper> addAwardNodeList = addAwardsNode.getXmlNodeList("row");
            for (JXmlWrapper addAwardNode : addAwardNodeList) {
                addAward = new BaseConfigDTO();
                addAward.setKey(addAwardNode.getStringValue("@key"));
                addAward.setValue(addAwardNode.getStringValue("@value"));

                addAwardList.add(addAward);
            }

            configDTO.setHuodongjiajian(addAwardList);
        }

        JXmlWrapper gamesNode = xml.getXmlNode("game");
        if (gamesNode != null) {
            List<JXmlWrapper> gameNodeList = gamesNode.getXmlNodeList("row");
            for (JXmlWrapper gameNode : gameNodeList) {
                game = new BaseConfigDTO();
                game.setGid(gameNode.getStringValue("@gid"));
                game.setName(gameNode.getStringValue("@name"));

                gameList.add(game);
            }
            configDTO.setGame(gameList);
        }

        JXmlWrapper tellPhonesNode = xml.getXmlNode("telphone");
        if (tellPhonesNode != null) {
            HotlineDTO hotline = new HotlineDTO();
            List<JXmlWrapper> tellPhoneNodeList = tellPhonesNode.getXmlNodeList("row");
            for (JXmlWrapper tellPhoneNode : tellPhoneNodeList) {
                tellPhone = new BaseConfigDTO();
                tellPhone.setKey(tellPhoneNode.getStringValue("@key"));
                tellPhone.setValue(tellPhoneNode.getStringValue("@value"));

                hotLineList.add(tellPhone);
            }
            hotline.setHotLine(hotLineList);

            hotline.setStopsale(tellPhonesNode.getXmlNode("isstop").getStringValue("@stopsale"));
            hotline.setInweb(tellPhonesNode.getXmlNode("showTVInWeb").getStringValue("@inweb"));
            configDTO.setTelphone(hotline);
        }

        JXmlWrapper launchImagsNode = xml.getXmlNode("launchimage");
        if (launchImagsNode != null) {
            List<JXmlWrapper> launchImagNodeList = launchImagsNode.getXmlNodeList("newrow");
            for (JXmlWrapper launchImagNode : launchImagNodeList) {
                launchImag = new BaseConfigDTO();
                launchImag.setSrc1(launchImagNode.getStringValue("@src1"));
                launchImag.setSrc2(launchImagNode.getStringValue("@src2"));
                launchImag.setSrc3(launchImagNode.getStringValue("@src3"));
                launchImag.setSrc4(launchImagNode.getStringValue("@src4"));

                launchImagList.add(launchImag);
            }
            configDTO.setLaunchimage(launchImagList);
        }

        JXmlWrapper xrlbNode = xml.getXmlNode("xrlb");
        if (xrlbNode != null) {
            xrlb = new BaseConfigDTO();
            xrlb.setSource(xrlbNode.getStringValue("@source"));
            xrlb.setValue(xrlbNode.getStringValue("@value"));
            configDTO.setXrlb(xrlb);
        }

        return configDTO;
    }

    /**
     * ios检测版本.
     * @param come
     * @param ver
     * @return
     */
    private BaseConfigDTO checkVersion(String come, String ver) {
        VersionBean verbean = getVersionBeanForIOS(come);
        BaseConfigDTO baseConfigDTO = new BaseConfigDTO();
        if (verbean == null) {
            log.info("IOS渠道号={} >>>>>>>没有配置相关信息", come);
            return baseConfigDTO;
        }
        boolean u = false;
        if (!StringUtil.isEmpty(verbean.getAversion())) {
            String version  = verbean.getAversion(); //String version="2.1.33";//服务器版本
            String[] arr1 = version.split("\\."); // 服务器版本
            String[] arr2 = ver.split("\\."); // 用户所传版本

            for (int i = 0; i < arr1.length; i++) {
                if (i < arr2.length) {
                    int result = Integer.valueOf(arr1[i]) - Integer.valueOf(arr2[i]);
                    if (result > 0) {
                        u = true;
                        break;
                    } else if (result == 0) {
                        continue;
                    } else if (result < 0) {
                        u = false;
                        break;
                    }
                }
            }
        } else {
            log.info("IOS渠道号={} >>>>>>>没有传人版本信息", come);
        }
        StringBuilder sb = new StringBuilder();
        if (u) {
            String url = verbean.getUrl();
            url = url.replace("^", "&");
            baseConfigDTO.setIsup("1");
            baseConfigDTO.setType(verbean.getType());
            baseConfigDTO.setContent(verbean.getContent());
            baseConfigDTO.setUrl(url);
            baseConfigDTO.setAversion(verbean.getAversion());
        } else {
            baseConfigDTO.setIsup("0");
        }

        return baseConfigDTO;
    }
    public boolean strCompare(String cs1, String cs2) {
        if (cs1 == null || cs2 == null) {
            return false;
        } else {
            return cs1.equals(cs2);
        }
    }

    private BaseConfigDTO getLaunchImage(String path, String appversion) {
        BaseConfigDTO baseConfigDTO = new BaseConfigDTO();
        JXmlWrapper xml = JXmlWrapper.parse(new File(path));
        JXmlWrapper newrow = xml.getXmlNode("newrow");
        baseConfigDTO.setSrc1(newrow.getStringValue("@src1"));
        baseConfigDTO.setSrc2(newrow.getStringValue("@src2"));
        baseConfigDTO.setSrc3(newrow.getStringValue("@src3"));
        baseConfigDTO.setSrc4(newrow.getStringValue("@src4"));
        if (BaseUtil.isNewApp(appversion, "launchimagelink", "ios")) {
            baseConfigDTO.setLink(newrow.getStringValue("@link"));
        }
        return baseConfigDTO;
    }

    public VersionBean getVersionBeanForIOS(String channel){
        String path="/opt/export/www/cms/news/ad/iosbbgxpz.xml";
        File file = new File(path);
        JXmlWrapper xml = JXmlWrapper.parse(file);
        int count=xml.countXmlNodes("app");

        VersionBean vb = new VersionBean();
        for (int i = 0; i < count; i++) {
            if (xml.getStringValue("app[" + i + "].@channel").equals(channel)) {
                vb.setAnum(new Integer(xml.getStringValue("app["+i+"].@anum").replace(".", "")));
                vb.setContent(xml.getStringValue("app["+i+"].@content"));
                vb.setType(xml.getStringValue("app["+i+"].@type"));
                vb.setUrl(xml.getStringValue("app["+i+"].@url"));
                vb.setPath(xml.getStringValue("app["+i+"].@path"));
                vb.setAversion(xml.getStringValue("app["+i+"].@anum"));
                break;
            }
        }
        return vb;
    }

    @Override
    public void checkBanActivity(HomePageBean bean) throws Exception {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.BAN_ACTIVITY_FILE));
        List<JXmlWrapper> banNodeList = xml.getXmlNodeList("ban-activity");
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("正常");
        for(JXmlWrapper banNode : banNodeList){
            String openFlag =  banNode.getXmlNode("business-rules").getXmlNode("open").getStringValue("@flag");
            if("1".equals(openFlag)){
                if(ParseGeneralRulesUtil.parseGeneralRulesNew(banNode.getXmlNode("general-rules"), bean, log)){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_BAN));
                    bean.setBusiErrDesc("系统升级中~暂停销售~");
                    break;
                }
            }
        }
    }
}
