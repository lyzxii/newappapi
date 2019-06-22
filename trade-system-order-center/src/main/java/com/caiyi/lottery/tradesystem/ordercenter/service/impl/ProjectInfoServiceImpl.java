package com.caiyi.lottery.tradesystem.ordercenter.service.impl;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.dao.*;
import com.caiyi.lottery.tradesystem.ordercenter.service.OrderService;
import com.caiyi.lottery.tradesystem.ordercenter.service.ProjectInfoService;
import com.caiyi.lottery.tradesystem.ordercenter.utils.GuoGuanUtil;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.code.CountCodeUtil;
import com.caiyi.lottery.tradesystem.util.proj.LiveBfUtil;
import com.caiyi.lottery.tradesystem.util.proj.ProjUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.*;
import order.pojo.GameFilterPojo;
import order.pojo.ProjXzjzPojo;
import order.pojo.QueryProjAppPojo;
import order.util.BaketMatchsUtil;
import order.util.ProjectMatchsUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 方案详情
 *
 * @author GJ
 * @create 2018-01-05 16:29
 **/
@Slf4j
@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UserBasicInfoInterface userBasicInfoInterface;
    @Autowired
    private ProjMapper projMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ShareListMapper shareListMapper;
    @Autowired
    private GameFilterMapper gameFilterMapper;
    @Autowired
    private PeriodMapper periodMapper;
    @Autowired
    private ProjXzjzMapper projXzjzMapper;
    @Autowired
    private MatchMapper matchMapper;

    private static String factor = "000000000000_";

    DecimalFormat df = new DecimalFormat("###.####");

    @Override
    public ZuCaiMatchVSDTO queryZucai(OrderBean bean) {
        ZuCaiMatchVSDTO zuCaiMatchVSDTO = new ZuCaiMatchVSDTO();
        String pid = bean.getPid();
        String xmlpath =FileConstant.FILE_80;
        JXmlWrapper sxml = JXmlWrapper.parse(new File(xmlpath));
        if (pid == null || "".equals(pid)) {
            pid = sxml.getStringValue("row[0].@pid");
        }
        xmlpath=FileConstant.FILE_ZC+pid+".xml";
        File file = new File(xmlpath);
        if (file.exists()) {
            JXmlWrapper xml = JXmlWrapper.parse(file);
            // 读取客户端请求域名
            getZucaiPids(zuCaiMatchVSDTO,sxml,xml);
        }
        bean.setBusiErrDesc("查询成功");
        return zuCaiMatchVSDTO;
    }

    private void getZucaiPids( ZuCaiMatchVSDTO zuCaiMatchVSDTO,JXmlWrapper sxml, JXmlWrapper xml) {
        StringBuilder pids = new StringBuilder();
        int count = sxml.countXmlNodes("row");
        if (count < 1) {
            return ;
        }
        List<JXmlWrapper> rows = sxml.getXmlNodeList("row");
        for (JXmlWrapper row : rows) {
            pids.append(row.getStringValue("@pid"));
            if (count > 1) {
                pids.append(",");
            }
            count--;
        }

        JXmlWrapper resXml = xml;
        zuCaiMatchVSDTO.setPid(resXml.getStringValue("@pid"));
        zuCaiMatchVSDTO.setEt(resXml.getStringValue("@et"));
        zuCaiMatchVSDTO.setFet(resXml.getStringValue("@fet"));
        zuCaiMatchVSDTO.setSale(resXml.getStringValue("@sale"));
        zuCaiMatchVSDTO.setPids(pids.toString());
        List<ZuCaiMatchVsInfoDTO> zuCaiMatchVsInfoDTOList = new ArrayList<>();
        List<JXmlWrapper> rows1 = xml.getXmlNodeList("row");
        for (JXmlWrapper row : rows1) {
            ZuCaiMatchVsInfoDTO zuCaiMatchVsInfoDTO = new ZuCaiMatchVsInfoDTO();
            zuCaiMatchVsInfoDTO.setXid(row.getStringValue("@xid"));
            zuCaiMatchVsInfoDTO.setHn(row.getStringValue("@hn"));
            zuCaiMatchVsInfoDTO.setGn(row.getStringValue("@gn"));
            zuCaiMatchVsInfoDTO.setMname(row.getStringValue("@mname"));
            zuCaiMatchVsInfoDTO.setCl(row.getStringValue("@cl"));
            zuCaiMatchVsInfoDTO.setMtime(row.getStringValue("@mtime"));
            zuCaiMatchVsInfoDTO.setHm(row.getStringValue("@hm"));
            zuCaiMatchVsInfoDTO.setGm(row.getStringValue("@gm"));
            zuCaiMatchVsInfoDTO.setHtn(row.getStringValue("@htn"));
            zuCaiMatchVsInfoDTO.setGtn(row.getStringValue("@gtn"));
            zuCaiMatchVsInfoDTO.setOh(row.getStringValue("@oh"));
            zuCaiMatchVsInfoDTO.setOd(row.getStringValue("@od"));
            zuCaiMatchVsInfoDTO.setOa(row.getStringValue("@oa"));
            zuCaiMatchVsInfoDTO.setHtid(row.getStringValue("@htid"));
            zuCaiMatchVsInfoDTO.setGtid(row.getStringValue("@gtid"));
            zuCaiMatchVsInfoDTOList.add(zuCaiMatchVsInfoDTO);
        }
        zuCaiMatchVSDTO.setMatchs(zuCaiMatchVsInfoDTOList);

    }

    @Override
    public GamesProjectDTO queryDuiZhenDetail(boolean isShareGod, OrderBean bean) {
        GamesProjectDTO gamesProjectDTO = new GamesProjectDTO();
        int gid = Integer.valueOf(bean.getGid());
        int lottery = 0;// 1=竞彩足球,2=竞彩篮球
        switch (gid) {
            case 90: // 竞彩足球-让球胜平负
            case 91: // 竞彩足球-比分
            case 92: // 竞彩足球-半全场
            case 93: // 竞彩足球-总进球数
            case 70: // 竞彩足球-混合过关
            case 72: // 竞彩足球-胜平负
                lottery = 1;
                if (isShareGod) {
                    bean.setXzflag(1);
                }
                gamesProjectDTO = matchGames(isShareGod, lottery, bean);
                break;
            case 94: // 竞彩篮球-胜负
            case 95: // 竞彩篮球-让分胜负
            case 96: // 竞彩篮球-胜分差
            case 97: // 竞彩篮球-大小分
            case 71: // 竞彩篮球-混合过关
            {
                lottery = 2;
                gamesProjectDTO = matchGames(isShareGod, lottery, bean);
                break;
            }
            case 84: // 北单-胜负过关
            {
                lottery = 3;
                gamesProjectDTO = matchGames(isShareGod, lottery, bean);
                break;
            }
            case 85: // 北单-让球胜平负
            case 86: // 北单-比分
            case 87: // 北单-半全场
            case 88: // 北单-上下单双
            case 89: // 北单-总进球数
            {
                lottery = 3;
                gamesProjectDTO = matchGames(isShareGod, lottery, bean);
                break;
            }
            case 98: // 猜冠亚军
            case 99: // 猜冠亚军
            {
                lottery = 4;
                gamesProjectDTO = matchGames(isShareGod, lottery, bean);
                break;
            }
            default:
                break;
        }
        return gamesProjectDTO;
    }

    public GamesProjectDTO getGuessChampion(ProjectInfoDTO projectInfoDTO,OrderBean bean) {
        GamesProjectDTO gamesProjectDTO = new GamesProjectDTO();
        String gid = projectInfoDTO.getGid();
        String pid = projectInfoDTO.getPid();
        //TODO 猜冠军
        if (ProjUtils.GUANYJMaps.containsKey(gid)) {
            GuessChampionDTO guessChampionDTO = new GuessChampionDTO();
            List<Map<String, String>> items = new ArrayList<>();
            String projid = projectInfoDTO.getHid();
            String xmlpath = FileConstant.GUOGUAN_DIR + gid + "/" + pid + "/proj/" + projid.toLowerCase() + ".xml";
            JXmlWrapper pxml = JXmlWrapper.parse(new File(xmlpath));
            String gyjNamePath = FileConstant.GYJ_NAME;
            JXmlWrapper gyjNameXml = JXmlWrapper.parse(new File(gyjNamePath));
            String gyjname = gyjNameXml.getStringValue("@name");
            guessChampionDTO.setGameName(gyjname);
            String resultJ = "";
            int count = pxml.countXmlNodes("item");
            for (int i = 0; i < count; i++) {
                String id = pxml.getStringValue("item[" + i + "].@id");
                String name = pxml.getStringValue("item[" + i + "].@name");
                String spvalue = pxml.getStringValue("item[" + i + "].@spvalue");//出票sp
                String result = pxml.getStringValue("item[" + i + "].@result");
                String cancel = pxml.getStringValue("item[" + i + "].@cancel");
                String audit = pxml.getStringValue("item[" + i + "].@audit");
                if ("".equals(cancel)) {
                    cancel = "0";
                }
                if (!"0".equals(cancel)) {
                    result = "取消";
                    spvalue = "1.00";
                } else {
                    if ("1".equals(audit) && !"".equals(result)) {
                        if ("3".equals(result)) {
                            resultJ = name;
                        }
                    }
                }
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("name", name);
                map.put("spvalue", spvalue);
                items.add(map);
            }
            gamesProjectDTO.setItems(items);
            guessChampionDTO.setResult(resultJ);
            gamesProjectDTO.setGuessChampion(guessChampionDTO);
            try {
                ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO,  bean.getOflag());
                gamesProjectDTO.setProcessInfo(processDTO);

            } catch (Exception e ) {
                log.error("猜冠军获取进度点失败,hid={}.uid={}",bean.getHid(),bean.getUid(),e);
            }
            gamesProjectDTO.setProjectInfo(projectInfoDTO);
        }
        return gamesProjectDTO;
    }

    @Override
    public ZucaiMatchProDTO zuCaiMatch(OrderBean bean) {
        ZucaiMatchProDTO zucaiMatchProDTO = new ZucaiMatchProDTO();
        ProjectInfoDTO projectInfoDTO = matchGamesProjectInfo(bean);
        if (projectInfoDTO == null || bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(bean.getBusiErrCode());
            bean.setBusiErrDesc(bean.getBusiErrDesc());
            return zucaiMatchProDTO;
        }
        try {
            getZucaiMatchPro(bean, zucaiMatchProDTO, projectInfoDTO);
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败了");
            log.error("胜负彩方案详情查询出错,hid={}.uid={}",bean.getHid(),bean.getUid(), e);
        }
        return zucaiMatchProDTO;
    }

    @Override
    public FigureGamesDTO figureGames(OrderBean bean) {
        FigureGamesDTO figureGamesDTO = new FigureGamesDTO();
        ProjectInfoDTO projectInfoDTO = matchGamesProjectInfo(bean);
        if (projectInfoDTO == null || bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(bean.getBusiErrCode());
            bean.setBusiErrDesc(bean.getBusiErrDesc());
            return figureGamesDTO;
        }
        try {
            getFigureGames(bean, figureGamesDTO, projectInfoDTO);
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败了");
            log.error("数字彩方案详情错误,hid={}.uid={}",bean.getHid(),bean.getUid(), e);
        }
        return figureGamesDTO;
    }

    private void getZucaiMatchPro(OrderBean bean, ZucaiMatchProDTO figureGamesDTO, ProjectInfoDTO projectInfoDTO) throws Exception {
        String ifile = projectInfoDTO.getIfile();
        if (!"1".equals(ifile)) {
            String ccodes = projectInfoDTO.getCcodes();
            String pid = projectInfoDTO.getPid();
            String carr[] = null;
            String darr[] = null;
            if (ccodes.indexOf("$") > -1) {//胆投
                String cc = ccodes.substring(0, ccodes.length() - 4);
                carr = cc.split("\\$")[1].split(",");//投注
                darr = cc.split("\\$")[0].split(",");//胆
            } else {
                if (!StringUtil.isEmpty(ccodes)) {
                    carr = ccodes.substring(0, ccodes.length() - 4).split(",");
                }
            }
            if (!StringUtil.isEmpty(ccodes)) {//公开方案
                int type = 1;    //默认为胜负彩/任九
                String gid = bean.getGid();
                if ("82".equals(gid)) {     //进球彩
                    type = 2;
                } else if ("83".equals(gid)) {      //半全场
                    type = 3;
                }
                List<ZucaiMatchDTO> matchPojoList = matchMapper.queryMatchList(type, pid);

                List<ZucaiMatchDTO> matchPojoList1 = new ArrayList<>();
                for (int i = 0; i < matchPojoList.size(); i++) {
                    if (!"#".equals(carr[i])) {
                        matchPojoList.get(i).setCode(carr[i]);
                        matchPojoList.get(i).setIsdan("0");
                        matchPojoList1.add(matchPojoList.get(i));
                        continue;
                    }
                    if (darr != null) {
                        if (!"#".equals(darr[i])) {
                            matchPojoList.get(i).setCode(darr[i]);
                            matchPojoList.get(i).setIsdan("1");
                            matchPojoList1.add(matchPojoList.get(i));
                        }
                    }
                }
                ZucaiMatchInfoDTO zucaiMatchInfoDTO = new ZucaiMatchInfoDTO();
                zucaiMatchInfoDTO.setMatchs(matchPojoList1);
                figureGamesDTO.setMatchInfo(zucaiMatchInfoDTO);
            }
        }
        figureGamesDTO.setProjectInfo(projectInfoDTO);
        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, bean.getOflag());
        String url = LotteryLogoUtil.getLotteryLogo(bean.getGid());
        projectInfoDTO.setLogo(url);
        figureGamesDTO.setProcessInfo(processDTO);

    }

    private void getFigureGames(OrderBean bean, FigureGamesDTO figureGamesDTO, ProjectInfoDTO projectInfoDTO) throws Exception {
        int grade = bean.getOflag();
        String pid = projectInfoDTO.getPid();
        String gid = projectInfoDTO.getGid();
        String hid = projectInfoDTO.getHid();
        String source = projectInfoDTO.getSource();
        if ("11".equals(source) || "12".equals(source)) {//单关
            projectInfoDTO.setIfile("0");
        }

        if ("2".equals(projectInfoDTO.getAward())) {
            //每元派送金额
            double nums = StringUtil.getNullDouble(projectInfoDTO.getNums());
            double tax = StringUtil.getNullDouble(projectInfoDTO.getTax());
            double owins = StringUtil.getNullDouble(projectInfoDTO.getOwins());
            double bonus = StringUtil.getNullDouble(projectInfoDTO.getRmoney());
            double one = (tax - owins) / nums;
            String wininfo = projectInfoDTO.getWininfo();

            if (bonus > 0) {
                String wininfostr = GuoGuanUtil.getwininfo(bean.getGid(), wininfo);
                projectInfoDTO.setWininfostr(wininfostr);
                projectInfoDTO.setAvg(GuoGuanUtil.df(one));
            }
        }

        String acode = null;//开奖号码
        //数字彩查询开奖号码
        if (ProjUtils.SZMaps.containsKey(gid) || ProjUtils.SSCMaps.containsKey(gid) || ProjUtils.X5Maps.containsKey(gid) || ProjUtils.K3Maps.containsKey(gid)) {
            bean.setPid(pid);
            acode = queryAwardCode(bean);
            if ("01".equals(gid)) {
                if (!StringUtil.isEmpty(acode)) {
                    acode = acode.substring(0, 20);
                }
            }
            projectInfoDTO.setAcode(acode);

            if ("9".equals(source)) {//旋转矩阵投注
                bean.setGid(gid);
                bean.setHid(hid);
                List<ProjXzjzPojo> projXzjzPojoList = matrixQueryProjectInfo(bean);
                if (projXzjzPojoList != null && !projXzjzPojoList.isEmpty()) {
                    ProjXzjzPojo projXzjzPojo = projXzjzPojoList.get(0);
                    String xzjzcodes = projXzjzPojo.getCcodes();
                    String filename = projectInfoDTO.getCcodes();
                    if (filename.endsWith(".txt")) {
                        String basePath = FileConstant.BASE_PATH + bean.getGid() + "/" + bean.getPid() + "/";
                        String ccodes = handleXzjzFile(xzjzcodes, filename, basePath, bean);
                        projectInfoDTO.setCcodes(ccodes);

                    }
                }
            }
        }
        //文件投注读取分行显示
        String codes = projectInfoDTO.getCcodes();
        if (!StringUtil.isEmpty(gid) && !StringUtil.isEmpty(codes)) {
            if (codes.endsWith("txt")) {
                if (ProjUtils.SZMaps.containsKey(gid) || ProjUtils.SSCMaps.containsKey(gid) || ProjUtils.X5Maps.containsKey(gid) || ProjUtils.K3Maps.containsKey(gid)) {
                    String path = FileConstant.BASE_PATH + bean.getGid() + "/" + bean.getPid();
                    try {
                        String ccodespath = GuoGuanUtil.LineDisplay(path, codes);
                        if (!StringUtil.isEmpty(ccodespath)) {
                            projectInfoDTO.setCcodes(ccodespath);
                        }
                    } catch (Exception e) {
                        log.info("大乐透分行写入文件错误,hid={}.uid={}",bean.getHid(),bean.getUid(), e);
                    }
                }

            }
        }

        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, grade);
        //增加快频开奖时间(未开奖)
        if (ProjUtils.DELAYMAPS.containsKey(gid) && StringUtil.isEmpty(acode)) {
            String xmlpath = FileConstant.PHOT + bean.getGid() + "/c.xml";
            JXmlWrapper cxml = JXmlWrapper.parse(new File(xmlpath));

            int counts = cxml.countXmlNodes("row");

            String at = null;
            for (int i = 0; i < counts; i++) {
                if (pid.equals(cxml.getStringValue("row[" + i + "].@pid"))) {
                    at = cxml.getStringValue("row[" + i + "].@at");
                    break;
                }
            }

            if (!StringUtil.isEmpty(at)) {
                java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Calendar c1 = java.util.Calendar.getInstance();

                try {
                    c1.setTime(df.parse(at));
                } catch (Exception e) {
                    log.error("格式化时间错误！hid={}.uid={}",bean.getHid(),bean.getUid(),e);
                }
                c1.add(Calendar.SECOND, ProjUtils.DELAYMAPS.get(bean.getGid()));
                processDTO.setAt(df.format(c1.getTime()));
            } else {
                processDTO.setAt("");
            }
        } else {
            processDTO.setAt("");
        }

        String url = LotteryLogoUtil.getLotteryLogo(bean.getGid());
        projectInfoDTO.setLogo(url);

        figureGamesDTO.setProcessInfo(processDTO);
        figureGamesDTO.setProjectInfo(projectInfoDTO);


    }

    @Override
    public GamesProjectDTO matchGames(boolean fromShareRank, int lottery, OrderBean bean) {
        GamesProjectDTO gamesProjectDTO = new GamesProjectDTO();
        // flag=1表示是从排行榜查看对阵详情
        int flag = bean.getFflag();

        ProjectInfoDTO projectInfoDTO = matchGamesProjectInfo(bean);
        if (projectInfoDTO == null || bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(bean.getBusiErrCode());
            bean.setBusiErrDesc(bean.getBusiErrDesc());
            return gamesProjectDTO;
        }
        CacheBean cacheBean = new CacheBean();
        LiveBfUtil liveBfUtil = new LiveBfUtil();

        if (lottery == 1) {//竞彩投注对阵详情
            liveBfUtil.clearJcData();
            cacheBean.setKey(Constants.CP_JC);
            Object jc_data = redisClient.getObject(cacheBean,Map.class, log, SysCodeConstant.ORDERCENTER);
            LiveBfUtil.jc_data = (Map<String, Map<String, String>>) jc_data;
            gamesProjectDTO = matchGamesMatchs(bean, projectInfoDTO, flag, fromShareRank);
        } else if (lottery == 2) {//篮彩投注对阵详情
            gamesProjectDTO = basketmatchGamesMatchs(bean, projectInfoDTO, flag);
        } else if (lottery == 3) {//北单投注对阵详情
            liveBfUtil.clearBdData();
            cacheBean.setKey(Constants.CP_BD);
            Object bd_data = redisClient.getObject(cacheBean,Map.class, log, SysCodeConstant.ORDERCENTER);
            LiveBfUtil.bd_data=(Map<String, Map<String, String>>) bd_data;
            gamesProjectDTO = beidanmatchGamesMatchs(bean, projectInfoDTO, flag);
        }else if (lottery == 4) {//猜冠亚军
            gamesProjectDTO = getGuessChampion( projectInfoDTO,bean);
        }
        try {
            String url = LotteryLogoUtil.getLotteryLogo(bean.getGid());
            projectInfoDTO.setLogo(url);
        } catch (Exception e) {
            log.error("获取彩种logo出错，hid={}.uid={}",bean.getHid(),bean.getUid(),e);
        }
        String showcode = gamesProjectDTO.getProjectInfo().getShowCodes();
        if (fromShareRank && "false".equals(showcode)) {
            gamesProjectDTO.getProjectInfo().setCcodes("");
        }
        return gamesProjectDTO;
    }

    @Override
    public GamesProjectDTO matchGamesMatchs(OrderBean bean, ProjectInfoDTO projectInfoDTO, int flag, boolean fromShareRank) {
        GamesProjectDTO gamesProjectDTO = new GamesProjectDTO();
        try {
            ProjectMatchsUtil.loadInfo(gamesProjectDTO, projectInfoDTO, flag, bean.getOflag(), bean.getXzflag(), fromShareRank);
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败了");
            log.error("竞技彩方案详情出错，hid={}.uid={}",bean.getHid(),bean.getUid(), e);
        }
        return gamesProjectDTO;
    }

    @Override
    public GamesProjectDTO basketmatchGamesMatchs(OrderBean bean, ProjectInfoDTO projectInfoDTO, int flag) {
        GamesProjectDTO gamesProjectDTO = new GamesProjectDTO();
        try {
            BaketMatchsUtil.loadInfo(gamesProjectDTO, projectInfoDTO, bean.getGid(), flag, bean.getOflag());
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败了");
            log.error("篮彩方案详情出错，hid={}.uid={}",bean.getHid(),bean.getUid(), e);
        }
        return gamesProjectDTO;

    }

    @Override
    public GamesProjectDTO beidanmatchGamesMatchs(OrderBean bean, ProjectInfoDTO projectInfoDTO, int flag) {
        GamesProjectDTO gamesProjectDTO = new GamesProjectDTO();
        try {
            ProjectMatchsUtil.loadInfoBeiDan(gamesProjectDTO, projectInfoDTO, flag, bean.getOflag());
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败了");
            log.error("北单方案详情出错，hid={}.uid={}",bean.getHid(),bean.getUid(), e);
        }
        return gamesProjectDTO;
    }

    /**
     * 将dto转为map
     *
     * @param dto
     * @return
     */
    @Override
    public HashMap<String, Object> changeDtoToMap(GamesProjectDTO dto, OrderBean bean) {
        HashMap<String, Object> rowsmap = new HashMap<>();
        if (dto != null) {
            ProjectInfoDTO projectInfo = dto.getProjectInfo();
            if (projectInfo != null) {
                rowsmap.put("showCode", projectInfo.getShowCodes());
                rowsmap.put("cast", projectInfo.getCast());
                rowsmap.put("istate", projectInfo.getIstate());
                rowsmap.put("mulity", projectInfo.getMulity());
                rowsmap.put("tmoney", projectInfo.getTmoney());
                rowsmap.put("rmoney", projectInfo.getRmoney());
                rowsmap.put("tax", projectInfo.getTax());
                rowsmap.put("award", projectInfo.getAward());
                rowsmap.put("rpmoney", projectInfo.getRpmoney());
                rowsmap.put("btime", projectInfo.getBtime());
                rowsmap.put("imoneyrange", projectInfo.getImoneyrange());
                rowsmap.put("source", projectInfo.getSource());
                PassInfoDTO passInfo = dto.getPassInfo();
                if (passInfo != null) {
                    rowsmap.put("gg", passInfo.getGg() == null ? "" : passInfo.getGg());
                } else {
                    rowsmap.put("gg", "");
                }
                rowsmap.put("minRatio", projectInfo.getMinRatio());
                rowsmap.put("ipay", projectInfo.getIpay());
                rowsmap.put("upay", projectInfo.getUpay());
                rowsmap.put("shareGod", projectInfo.getShareGod());
                rowsmap.put("sharedNickid", projectInfo.getSharedNickid());
                rowsmap.put("hideSharedNickid", projectInfo.getHideSharedNickid());
                rowsmap.put("visible", projectInfo.getVisible());
                rowsmap.put("ctime", projectInfo.getCtime());

            }
            //拼装row
            List<HashMap<String, Object>> rlist = new ArrayList<>();
            if (dto.getMatchInfo() != null) {
                List<MatchDTO> rowlist = dto.getMatchInfo().getMatchs();
                if (rowlist != null && rowlist.size() > 0) {
                    for (MatchDTO mDTO : rowlist) {
                        HashMap<String, Object> rowmap = new HashMap<>();
                        if (mDTO != null) {
                            rowmap.put("id", mDTO.getId());
                            rowmap.put("name", mDTO.getName());
                            rowmap.put("hn", mDTO.getHn());
                            rowmap.put("gn", mDTO.getGn());
                            rowmap.put("hs", mDTO.getHs());
                            rowmap.put("gs", mDTO.getGs());
                            rowmap.put("hhs", mDTO.getHhs());
                            rowmap.put("hgs", mDTO.getHgs());
                            rowmap.put("lose", mDTO.getClose());//未定
                            rowmap.put("isdan", mDTO.getIsdan());
                            rowmap.put("jsbf", mDTO.getJsbf());
                            rowmap.put("ccodes", mDTO.getCcodes());
                            rowmap.put("isForward", mDTO.getIsForward());
                            rowmap.put("qc", mDTO.getQc());
                            rowmap.put("sort", mDTO.getSort());
                            rowmap.put("roundItemId", mDTO.getRoundItemId());
                            rowmap.put("rid", mDTO.getRid());
                            rowmap.put("sid", mDTO.getSid());
                            rowmap.put("isEncrypt", mDTO.getIsEncrypt());
                        }
                        rlist.add(rowmap);
                    }
                    rowsmap.put("row", rlist);
                }
            }
            ProcessDTO processInfo = dto.getProcessInfo();
            if (processInfo != null) {
                HashMap<String, Object> jindumap = new HashMap<>();
                jindumap.put("node", processInfo.getNode());
                jindumap.put("percent", processInfo.getPercent());
                jindumap.put("paint", processInfo.getPaint());
                jindumap.put("kjtime", processInfo.getKjtime());
                jindumap.put("pjtime", processInfo.getPjtime());
                jindumap.put("isflag", processInfo.getIsflag());
                rowsmap.put("jindu", jindumap);
            }
        }
        return rowsmap;
    }


    @Override
    public ProjectInfoDTO matchGamesProjectInfo(OrderBean bean) {
        ProjectInfoDTO projectInfoDTO = null;
        if (!CheckUtil.isNullString(bean.getGid()) && !CheckUtil.isNullString(bean.getHid())) {
            projectInfoDTO = new ProjectInfoDTO();
            int grade = getGrade(bean);
            bean.setOflag(grade);
            QueryProjAppPojo queryProjAppPojo = projMapper.queryProjectinfo(bean.getGid(), bean.getHid());
            if (queryProjAppPojo != null) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(queryProjAppPojo, projectInfoDTO);
                int shareGod = 0;
                if ("2".equals(queryProjAppPojo.getType())) {
                    shareGod = 1;// 该单为分享神单
                }
                if ("3".equals(queryProjAppPojo.getType())) {
                    shareGod = 2;// 该单为跟买神单
                }
                if (shareGod == 0) { // 投注时间已截止
                    Date date = DateTimeUtil.parseDate(queryProjAppPojo.getEndtime(), DateTimeUtil.DATETIME_FORMAT);
                    if (System.currentTimeMillis() >= date.getTime()) {
                        shareGod = 3;
                    }
                }
                String uid = bean.getUid();
                boolean self = false;
                // 本用户
                if (queryProjAppPojo.getCnickid().equalsIgnoreCase(uid)) {
                    self = true;
                }
                // 代购方案非本人查看 并且不是分享神单
                if ("0".equals(queryProjAppPojo.getType()) && !self) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.ORDER_ERR_DAIGOU_VIEW));
                    bean.setBusiErrDesc("抱歉，该方案是代购方案，您不是该方案的发起人，不能查看。");
                } else {
                    boolean flag = false;
                    if ("1".equals(queryProjAppPojo.getType())) {

                        boolean isJointPurchase = isJointPurchase(queryProjAppPojo, self);
                        if (!isJointPurchase) {
                            flag = true;
                            projectInfoDTO.setCcodes("");
                        }
                        projectInfoDTO.setVisible("true");
                        projectInfoDTO.setShowCodes("true");
                    } else {
                        flag = distinguish(bean, shareGod, self, projectInfoDTO, queryProjAppPojo);
                    }
                    projectInfoDTO.setShieldNickid(StringUtil.shield(projectInfoDTO.getCnickid()));
                    //    projectInfoDTO.setCname(CodesUtil.filteText(projectInfoDTO.getCname()));
                    //    projectInfoDTO.setCdesc(CodesUtil.filteText(projectInfoDTO.getCdesc()));
                    projectInfoDTO.setShareGod(String.valueOf(shareGod));
                    //获取打赏比例
                    getFollowListWrate(shareGod, projectInfoDTO);
                    //在自购详情里显示分享人的用户名
                    getShareUser(shareGod, projectInfoDTO);
                    //获取最终奖金
                    getBouns(shareGod, projectInfoDTO);
                    flag &= false;
                    if (flag) {
                        filter(projectInfoDTO);
                    }
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("获取成功");
                }
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.ORDER_SCHEME_NOT_EXITS));
                bean.setBusiErrDesc("方案不存在");
            }
        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.ORDER_PARAMETER_ERROR));
            bean.setBusiErrDesc("输入参数不正确");
        }
        projectInfoDTO.setOwins(StringUtil.isEmpty(projectInfoDTO.getOwins())?"": df.format(Double.parseDouble(projectInfoDTO.getOwins())) + "");
        projectInfoDTO.setTax(StringUtil.isEmpty(projectInfoDTO.getTax())?"": df.format(Double.parseDouble(projectInfoDTO.getTax()))+"");
        projectInfoDTO.setReward(StringUtil.isEmpty(projectInfoDTO.getReward())?"": df.format(Double.parseDouble(projectInfoDTO.getReward()))+"");
        projectInfoDTO.setDueMoney(StringUtil.isEmpty(projectInfoDTO.getDueMoney())?"": df.format(Double.parseDouble(projectInfoDTO.getDueMoney()))+"");

        //   projectInfoDTO.setRpmoney(StringUtil.isEmpty(projectInfoDTO.getRpmoney())?"":Double.parseDouble(projectInfoDTO.getRpmoney())+"");
    //    projectInfoDTO.setTmoney(StringUtil.isEmpty(projectInfoDTO.getTmoney())?"":Double.parseDouble(projectInfoDTO.getTmoney())+"");
    //    projectInfoDTO.setRmoney(StringUtil.isEmpty(projectInfoDTO.getRmoney())?"":Double.parseDouble(projectInfoDTO.getRmoney())+"");
        //     projectInfoDTO.setYhMoney(StringUtil.isEmpty(projectInfoDTO.getYhMoney())?"":Double.parseDouble(projectInfoDTO.getYhMoney())+"");
       boolean flag=true;
       if ("50".equals(bean.getGid())){
           try {
               String lsPath = FileConstant.LSQC;
               JXmlWrapper lsXml = JXmlWrapper.parse(new File(lsPath));
               Long begin = lsXml.getLongValue("@begin");
               Long end = lsXml.getLongValue("@end");
               Long qc=Long.valueOf(projectInfoDTO.getPid());
               flag=!(qc>end||qc<begin);
           } catch (Exception e) {
               log.error("获取大乐透乐善活动期次错误",e);
           }
       }
        Boolean isleshan = false;
        if (projectInfoDTO.getCcodes().endsWith("txt")) {
            String path = FileConstant.BASE_PATH + bean.getGid() + "/" + projectInfoDTO.getPid();
            try {
                String codestr= FileUtils.readFileToString(new File(path,projectInfoDTO.getCcodes()), "UTF-8");
                String[] ccodes=codestr.split(";");
                for(String ccode:ccodes){
                    if (ccode.contains(":2:1")) {
                        isleshan = true;
                    }
                    break;
                }
            } catch (Exception e) {
                log.error("处理乐善彩文件投注出错",e);
            }
        }
        //大乐透乐善彩中奖处理
        if (flag&&"50".equals(bean.getGid())&&(projectInfoDTO.getCcodes().contains(":2:1")||isleshan)) {
            projectInfoDTO.setIsaddreward("1");
            if (!StringUtil.isEmpty(projectInfoDTO.getLsmoney()) && "2".equals(projectInfoDTO.getLsaward()) && Double.parseDouble(projectInfoDTO.getLsmoney()) > 0d) {
                /*double d1 = StringUtil.isEmpty(projectInfoDTO.getRmoney()) ? 0d : Double.parseDouble(projectInfoDTO.getRmoney());
                double d2 = Double.parseDouble(projectInfoDTO.getLsmoney());
                projectInfoDTO.setRmoney(df.format(d1 + d2));*/
                projectInfoDTO.setIscontainls("1");

               /* double d3 = StringUtil.isEmpty(projectInfoDTO.getTax()) ? 0d : Double.parseDouble(projectInfoDTO.getTax());
                double d4 = Double.parseDouble(projectInfoDTO.getLsmoney());
                projectInfoDTO.setTax(df.format(d3 + d4));*/
            }
        }
        return projectInfoDTO;
    }

    private void filter(ProjectInfoDTO projectInfoDTO) {
        GameFilterPojo gameFilterPojo = gameFilterMapper.queryFilter(projectInfoDTO.getGid(), projectInfoDTO.getHid());
        if (gameFilterPojo != null) {
            File file = new File(
                    FileConstant.BASE_PATH + File.separator + projectInfoDTO.getGid() + File.separator + projectInfoDTO.getPid(),
                    gameFilterPojo.getFC());
            if (file.exists()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String tmp = null;
                    StringBuilder fsb = new StringBuilder();
                    while ((tmp = br.readLine()) != null) {
                        fsb.append(tmp);
                    }
                    String fc = fsb.toString();
                    if (!StringUtil.isEmpty(fc)) {
                        projectInfoDTO.setFc(fc);
                    }
                } catch (Exception e) {
                    log.error("读取文件错误",e);
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            log.error("读取文件关闭错误", e);
                        }

                    }
                }
            }
        }
    }

    /**
     * 获取打赏奖金
     */
    private void getBouns(int shareGod, ProjectInfoDTO projectInfoDTO) {
        Double moneny = StringUtil.isEmpty(projectInfoDTO.getRmoney()) ? 0.0 : Double.valueOf(projectInfoDTO.getRmoney());
        if (moneny > 0 && shareGod == 2) { // 方案详情是跟买用户并且已中奖
            QueryProjAppPojo queryProjAppPojo = shareListMapper.queryFollowUserBouns(projectInfoDTO.getCnickid(), projectInfoDTO.getHid());
            computeBouns(queryProjAppPojo, projectInfoDTO, true);
        } else if (moneny > 0 && shareGod == 1) {
            QueryProjAppPojo queryProjAppPojo = shareListMapper.queryShareUserBous(projectInfoDTO.getCnickid(), projectInfoDTO.getHid());
            computeBouns(queryProjAppPojo, projectInfoDTO, false);
        }

    }

    /**
     * 计算金额
     *
     * @param queryProjAppPojo
     */
    private void computeBouns(QueryProjAppPojo queryProjAppPojo, ProjectInfoDTO projectInfoDTO, boolean flag) {
        if (queryProjAppPojo != null) {
            String rmoney = StringUtil.isEmpty(queryProjAppPojo.getRmoney()) ? "0" : queryProjAppPojo.getRmoney();
            String owins = StringUtil.isEmpty(queryProjAppPojo.getOwins()) ? "0" : queryProjAppPojo.getOwins();
            String dueMoney = "";
            if (flag) {
                dueMoney = new BigDecimal(rmoney).subtract(new BigDecimal(owins))
                        .toPlainString();
            } else {
                dueMoney = new BigDecimal(rmoney).add(new BigDecimal(owins)).toPlainString();
            }
            projectInfoDTO.setReward(owins);// 打赏金额
            projectInfoDTO.setDueMoney(dueMoney);// 中奖金额减去打赏金额

        }
    }


    /**
     * 在自购详情里显示分享人的用户名
     *
     * @param shareGod
     * @param projectInfoDTO
     */
    private void getShareUser(int shareGod, ProjectInfoDTO projectInfoDTO) {
        if (shareGod == 2) {//跟买
            String nickid = shareListMapper.queryNickid(projectInfoDTO.getHid());
            if (nickid != null) {
                getHideUser(nickid, projectInfoDTO);
            }
        } else if (shareGod == 1) {//分享
            getHideUser(projectInfoDTO.getCnickid(), projectInfoDTO);
        }
    }

    private void getHideUser(String nickid, ProjectInfoDTO projectInfoDTO) {
        String encrypted = CaiyiEncrypt.encryptStr(nickid).replaceAll("\\+", "\\*");
        String showUid = CheckUtil.checkNum(nickid);
        projectInfoDTO.setSharedNickid(showUid);
        projectInfoDTO.setHideSharedNickid(factor + encrypted);
    }


    /**
     * 获取打赏比例
     *
     * @param shareGod
     * @param projectInfoDTO
     */
    private void getFollowListWrate(int shareGod, ProjectInfoDTO projectInfoDTO) {
        if (shareGod == 2) {
            String rate = shareListMapper.queryFollowListWrate(projectInfoDTO.getHid());
            projectInfoDTO.setMinRatio(rate);
        } else if (shareGod == 1) {
            projectInfoDTO.setMinRatio("0");
        }
    }

    private boolean distinguish(OrderBean bean, int shareGod, boolean self, ProjectInfoDTO projectInfoDTO, QueryProjAppPojo queryProjAppPojo) {
        Date date = DateTimeUtil.parseDate(queryProjAppPojo.getEndtime(), DateTimeUtil.DATETIME_FORMAT);
        boolean deadline = CountCodeUtil.checkOpenEndTime(queryProjAppPojo.getGid(), queryProjAppPojo.getPid(),
                queryProjAppPojo.getHid(), date, Integer.valueOf(queryProjAppPojo.getIfile())); // 判断方案是否截止
        boolean rightBool = false;
        // 本人 并且是分享神单者
        if ((shareGod == 1 && self)) {
            projectInfoDTO.setVisible("true");
            projectInfoDTO.setShowCodes("true");
        } else if (shareGod == 2 && self) {// 本人 并且是神单跟买者
            Boolean isshow = orderService.checkItemLastDate(bean.getHid(), true);
            projectInfoDTO.setVisible("true");
            projectInfoDTO.setShowCodes(isshow + "");
        } else if (shareGod == 1 && !self) {// 不是本人 但这个方案是分享神单
            Boolean isshow = orderService.checkItemLastDate(bean.getHid(), false);
            projectInfoDTO.setVisible("true");
            projectInfoDTO.setShowCodes(isshow + "");
        } else if (shareGod == 2 && !self) {// 不是本人 但这个方案是神单跟买
            Boolean isshow = orderService.checkItemLastDate(bean.getHid(), true);
            projectInfoDTO.setVisible("true");
            projectInfoDTO.setShowCodes(isshow + "");
        } else if ((shareGod == 0 || shareGod == 3) && self) {  // 不是神单分享方案 也不是神单跟买方案 但这个方案是本人自己查看
            projectInfoDTO.setVisible("true");
            projectInfoDTO.setShowCodes("true");
        } else {
            rightBool = permissViewProj(Integer.valueOf(queryProjAppPojo.getIopen()), deadline, 0);
            projectInfoDTO.setVisible(rightBool + "");
            projectInfoDTO.setShowCodes("true");
        }
        return rightBool;
    }

    private boolean permissViewProj(int iopen, boolean deadline, int projNum) {
        boolean b = false;
        if (iopen == 0) { // 对所有人公开
            b = true;
        } else if (iopen == 1 && deadline) { // 截止后公开
            b = true;
        } else if (iopen == 2) { // 对跟买人公开
            b = projNum > 0;
        } else if (iopen == 3 && deadline) { // 截止后对跟买人公开
            b = projNum > 0;
        } else if (iopen == 4) { // 在网站购买晒单 在客户端查看的情况
            b = true;
        } else {
            b = false;
        }

        return b;
    }

    private boolean isJointPurchase(QueryProjAppPojo queryProjAppPojo, boolean isself) {
        Date date = DateTimeUtil.parseDate(queryProjAppPojo.getEndtime(), DateTimeUtil.DATETIME_FORMAT);
        if (!isself) {
            if ("0".equals(queryProjAppPojo.getIopen())) {// 公开
                isself = true;
            } else {
                if ("1".equals(queryProjAppPojo.getIopen())) {// 截止后公开
                    if (Integer.valueOf(queryProjAppPojo.getIstate()) > 1 && CountCodeUtil.checkOpenEndTime(queryProjAppPojo.getGid(), queryProjAppPojo.getPid(),
                            queryProjAppPojo.getHid(), date, Integer.valueOf(queryProjAppPojo.getIfile()))) {
                        isself = true;
                    }
                } else if ("2".equals(queryProjAppPojo.getIopen())) {// 对参与人员公开
                } else if ("3".equals(queryProjAppPojo.getIopen())) {// 截止后对参与人员公开
                    if (Integer.valueOf(queryProjAppPojo.getIstate()) > 1 && CountCodeUtil.checkOpenEndTime(queryProjAppPojo.getGid(), queryProjAppPojo.getPid(),
                            queryProjAppPojo.getHid(), date, Integer.valueOf(queryProjAppPojo.getIfile()))) {
                    }
                }
            }
        }
        return isself;
    }

    /**
     * 获取用户等级
     *
     * @param bean
     * @return
     */
    private int getGrade(OrderBean bean) {
        int grade = 0;
        String key = "queryUserWhiteList_" + bean.getUid();
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        String gradeStr = redisClient.getString(cacheBean, log, SysCodeConstant.ORDERCENTER);
        if (!StringUtil.isEmpty(gradeStr)) {
            grade = Integer.valueOf(gradeStr);
        } else {
            BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
            baseReq.setData(bean);
            BaseResp<String> userResp = userBasicInfoInterface.queryUserWhiteGrade(baseReq);
            if (userResp != null && !StringUtil.isEmpty(userResp.getData())) {
                grade = Integer.valueOf(userResp.getData());
                cacheBean.setValue(userResp.getData());
                // 设置有效期为1天
                cacheBean.setTime(86400000);
                boolean result = redisClient.setString(cacheBean, log, SysCodeConstant.ORDERCENTER);
                if (!result) {
                    log.info("uid:{},等级:{},白名单加入缓存成功", bean.getUid(),grade);
                }

            } else {
                log.info("查询用户中心uid:{}白名单为空", bean.getUid());
            }
        }
        return grade;
    }

    /**
     * 开奖号码，没有查缓存
     *
     * @param bean
     */
    public String queryAwardCode(OrderBean bean) {
        String acode = "";
        try {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("获取失败");

            CacheBean cacheBean = new CacheBean();
         //   cacheBean.setKey(bean.getGid()+"_"+bean.getPid());
            cacheBean.setKey("acode_"+bean.getGid());
            Map<String, String> acodeMap = (Map<String, String>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.ORDERCENTER);
            if(acodeMap==null||!acodeMap.containsKey(bean.getPid())){
                List<String> acodeList = periodMapper.queryAwardcodes(bean.getGid(), bean.getPid());
                if (acodeList != null && !acodeList.isEmpty()) {
                    acode = acodeList.get(0);
                }
            }else {
                acode = acodeMap.get(bean.getPid());
            }
            if (!StringUtil.isEmpty(acode)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("OK");
            } else {
                acode = "";
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂无开奖号码");
            }
        } catch (Exception e) {
            log.error("获取开奖结果错误,hid={},uid={}",bean.getHid(),bean.getUid(), e);
        }
        return acode;
    }

    public List<ProjXzjzPojo> matrixQueryProjectInfo(OrderBean bean) {
        if (!StringUtil.isEmpty(bean.getGid()) && !StringUtil.isEmpty(bean.getHid())) {
            try {
                List<ProjXzjzPojo> projXzjzPojoList = projXzjzMapper.queryMatrix(bean.getGid(), bean.getHid());
                if (projXzjzPojoList == null || projXzjzPojoList.size() == 0) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.ORDRE_MATRIX));
                    bean.setBusiErrDesc("非旋转矩阵方式投注");
                } else {
                    bean.setBusiErrCode(0);
                }
                return projXzjzPojoList;
            } catch (Exception e) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("暂时无法获取该方案信息");
                log.error("获取旋转矩阵失败,hid={},uid={}",bean.getHid(),bean.getUid(), e);
            }
        }
        return null;
    }

    //处理旋转矩阵文件
    private String handleXzjzFile(String xzjzCode, String fileName, String basePath, OrderBean bean) {
        try {
            String xzjzFilePath = basePath + "xzjz_" + fileName;
            File file = new File(xzjzFilePath);
            if (file.exists()) {
                return "xzjz_" + fileName;
            } else {
                String content = FileUtils.readFileToString(new File(basePath + fileName));
                String[] codes = content.split(";");
                StringBuilder builder = new StringBuilder();
                builder.append("旋转前号码:").append("\n")
                        .append(xzjzCode).append("\n").append("\n")
                        .append("旋转后号码：")
                        .append("\n");
                for (String code : codes) {
                    String[] codeArr = code.split(":");
                    builder.append(codeArr[0]).append("\n");
                }
                FileUtils.writeStringToFile(new File(xzjzFilePath), builder.toString());
                return "xzjz_" + fileName;
            }
        } catch (IOException e) {
            log.error("旋转矩阵文件处理异常,gid:" + bean.getGid() + " hid:" + bean.getHid(), e);
        }
        return fileName;
    }

}
