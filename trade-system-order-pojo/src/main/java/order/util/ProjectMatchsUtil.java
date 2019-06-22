package order.util;

import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.ConcurrentSafeDateUtil;
import com.caiyi.lottery.tradesystem.util.SecurityTool;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.proj.LiveBfUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import order.dto.*;
import order.pojo.QueryProjAppPojo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.ParseException;
import java.util.*;

/**
 * 对阵util
 *
 * @author GJ
 * @create 2018-01-08 19:34
 **/
public class ProjectMatchsUtil {
    private static final String day_pattern = "yyyy-MM-dd";
    private static final String pattern = "MM-dd";
    private static final String pattern2 = "MM-dd HH:mm";
    private static final String patternDatabase = "yyyy-MM-dd HH:mm:ss";
    private static final String[] wk = { "日", "一", "二", "三", "四", "五", "六" };
    private static final String[] yhfslist = { "中奖优先", "平稳盈利", "奖金优先" };

    public static Logger logger = LoggerFactory.getLogger(ProjectMatchsUtil.class);

    /**
     * 竞彩篮球各玩法id.
     */
    private static List<String> gidJCLQ = new ArrayList<String>();
    /**
     * 竞彩足球各玩法id.
     */
    private static List<String> gidJCZQ = new ArrayList<String>();
    static {
        gidJCLQ.add("71");
        gidJCLQ.add("94");
        gidJCLQ.add("95");
        gidJCLQ.add("96");
        gidJCLQ.add("97");

        gidJCZQ.add("70");
        gidJCZQ.add("72");
        gidJCZQ.add("90");
        gidJCZQ.add("91");
        gidJCZQ.add("92");
        gidJCZQ.add("93");
    }

    public static HashMap<String, String> SPF = new HashMap<String, String>();
    static {
        SPF.put("3", "0");
        SPF.put("1", "1");
        SPF.put("0", "2");
    }

    public static HashMap<String, String> JQS = new HashMap<String, String>();
    static {
        JQS.put("0", "0");
        JQS.put("1", "1");
        JQS.put("2", "2");
        JQS.put("3", "3");
        JQS.put("4", "4");
        JQS.put("5", "5");
        JQS.put("6", "6");
        JQS.put("7", "7");
    }

    public static HashMap<String, String> CBF = new HashMap<String, String>();
    static {

        CBF.put("1:0", "0");
        CBF.put("2:0", "1");
        CBF.put("2:1", "2");
        CBF.put("3:0", "3");
        CBF.put("3:1", "4");
        CBF.put("3:2", "5");
        CBF.put("4:0", "6");
        CBF.put("4:1", "7");
        CBF.put("4:2", "8");
        CBF.put("5:0", "9");
        CBF.put("5:1", "10");
        CBF.put("5:2", "11");
        CBF.put("9:0", "12");// 胜其它
        CBF.put("0:0", "13");
        CBF.put("1:1", "14");
        CBF.put("2:2", "15");
        CBF.put("3:3", "16");
        CBF.put("9:9", "17");// 平其它
        CBF.put("0:1", "18");
        CBF.put("0:2", "19");
        CBF.put("1:2", "20");
        CBF.put("0:3", "21");
        CBF.put("1:3", "22");
        CBF.put("2:3", "23");
        CBF.put("0:4", "24");
        CBF.put("1:4", "25");
        CBF.put("2:4", "26");
        CBF.put("0:5", "27");
        CBF.put("1:5", "28");
        CBF.put("2:5", "29");
        CBF.put("0:9", "30");// 负其它
    }

    public static HashMap<String, String> BQC = new HashMap<String, String>();
    static {
        BQC.put("3-3", "0");
        BQC.put("3-1", "1");
        BQC.put("3-0", "2");
        BQC.put("1-3", "3");
        BQC.put("1-1", "4");
        BQC.put("1-0", "5");
        BQC.put("0-3", "6");
        BQC.put("0-1", "7");
        BQC.put("0-0", "8");
    }


    /**
     * 竞彩对阵详情. 修改这里面对于的get***()方法
     * 必须相应的修改tradebeanImpl里showSharedProj()方法中调用的load***()方法。
     *
     * @param flag 1表示从中奖排行榜查看详情,0表示其它
     * @param fromShareRank
     * @return
     * @throws Exception
     */
    public static void loadInfo(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO,  int flag, int grade, int yczsFlag,
                                   boolean fromShareRank) throws Exception {
        String source = projectInfoDTO.getSource();
        String gid = projectInfoDTO.getGid();
        boolean visible = Boolean.valueOf(projectInfoDTO.getVisible());

        if ("6".equals(source)) {// 6 奖金优化对阵 7一场致胜（单关配） 11 单关 12 单关(奖金优化)
             getJJYHDuizhen(gamesProjectDTO, projectInfoDTO, flag, visible, grade, fromShareRank);
             return;
        } else if ("7".equals(source)) {
            getWinDuizhen(gamesProjectDTO, projectInfoDTO, flag, visible, grade);
            return ; // 无法显示及时比分
        } else if ("11".equals(source) || "12".equals(source)) {
            // 走单关配的单关投注详情（不支持混投）
            getNewDanDuizhen(gamesProjectDTO, projectInfoDTO, flag, visible, grade);
            return;
        } else if ("13".equals(source)) {// 纯单关投注 //&& counter(codes,"|") > 3
            // 复试投注-单关投注详情

            if (!"70".equals(gid)) { // SPF|141023001=3|1*1_10;SPF|141023001=3|1*1_10;SPF|141023001=3|1*1_10
                getDanDuizhenByCodes(gamesProjectDTO, projectInfoDTO, flag, visible, grade);
                return;
            } else { // HH|130828021>RQSPF=1|1*1_10;HH|130828021>SPF=1|1*1_10
                getHhDanDuizhenByCodes(gamesProjectDTO, projectInfoDTO, flag, visible, grade);
                return;
            }
        } else if ("14".equals(source)) {
            getJJYHDuizhen2xuan1(gamesProjectDTO, projectInfoDTO, flag, visible, grade, fromShareRank);
            return;
        } else if ("15".equals(source)) {// 一场致胜
            getYczsJJYHDuizhen(yczsFlag,gamesProjectDTO,projectInfoDTO,flag,visible,grade);
            return;
        }
        if (!"70".equals(gid)) {
            getDuizhen(gamesProjectDTO,projectInfoDTO,flag,visible,grade);
            return;
        } else {
            getHhDuizhen(gamesProjectDTO,projectInfoDTO,flag,visible,grade);
            return;
        }
    }

    public static void loadInfoBeiDan(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag,int grade)throws Exception {
        getBdDuizhen(gamesProjectDTO, projectInfoDTO, flag, grade);
    }

    /**
     * 北单投注详情详情
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param grade
     * @throws Exception
     */
    private static void getBdDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag,int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        String pid = projectInfoDTO.getPid();
        String gid = projectInfoDTO.getGid();
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if (flag == 1) {
            //todo 是否是方案所有人查看方案, same=1 是, same=0 不是
        }
        String ifile =projectInfoDTO.getIfile();
        String ccodes = projectInfoDTO.getCcodes();
        if(!"1".equals(ifile)){
            setGg("0", projectInfoDTO);
            setWinzs(projectInfoDTO);
            Map<String, String> dmap = new  HashMap<String, String>();//设胆map
            Map<String, String> map = new  HashMap<String, String>();//key 131124020 vaule 3/1
            setDan("0",map,dmap,ccodes);

            String xmlpath = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" + projectInfoDTO.getHid().toLowerCase() + ".xml";
            JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));


            int count = xml.countXmlNodes("item");
            MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
            List<MatchDTO> matchDTOList = new ArrayList<>();
            Date date1 = new Date();
            Map<String, Map<String, String>> zqDataMap = new HashMap<>();
            Map<String, String> jsbfmap = LiveBfUtil.jsbf(projectInfoDTO.getGid(), pid, zqDataMap, true);
            Date date2 = new Date();
            logger.info("订单号"+projectInfoDTO.getHid()+"北单获取资料库数据时间"+(date2.getTime()-date1.getTime())+"ms");

            for (int i = 0; i < count; i++) {
                //场次编号
                String id=xml.getStringValue("item["+i+"].@id");
                String[] ms = new String[1];
                ms[0] = id;
                String[] minfoarr = new String[12];
                minfoarr[0] = null;
                minfoarr[1] = xml.getStringValue("item[" + i + "].@hn");
                minfoarr[2] = xml.getStringValue("item[" + i + "].@lose");
                minfoarr[3] = xml.getStringValue("item[" + i + "].@vn");
                minfoarr[4] = xml.getStringValue("item[" + i + "].@hs");
                minfoarr[5] = xml.getStringValue("item[" + i + "].@vs");
                minfoarr[6] = xml.getStringValue("item[" + i + "].@hhs");
                minfoarr[7] = xml.getStringValue("item[" + i + "].@hvs");
                minfoarr[8] = xml.getStringValue("item[" + i + "].@bet0");
                minfoarr[9] = xml.getStringValue("item[" + i + "].@bet1");
                minfoarr[10] = xml.getStringValue("item[" + i + "].@bet3");
                minfoarr[11] = xml.getStringValue("item[" + i + "].@spvalue");
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, jsbfmap, zqDataMap);
                matchDTO.setIsdan(isDan(dmap, id));
                String prefix=null;
                switch (Integer.valueOf(gid)) {
                    case 84: //北单-胜负过关
                        prefix = "SF";
                        break;
                    case 85: // 北单-(让球)胜平负
                        prefix="SPF";
                        break;
                    case 86: // 北单-比分
                        prefix="CBF";
                        break;
                    case 87: // 北单-半全场
                        prefix="BQC";
                        break;
                    case 88: // 北单-上下单双
                        prefix="SXP";
                        break;
                    case 89: // 北单-总进球数
                        prefix="JQS";
                        break;

                    default:
                        break;
                }

                String cod = "";
                String tz=map.get(id);
                if (!StringUtil.isEmpty(tz)) {
                    String [] d=tz.split("/");
                    for (int j = 0; j < d.length; j++) {
                        if ("".equals(cod)) {
                            cod=prefix+"|"+d[j];
                        }else {
                            cod+=","+d[j];
                        }
                    }
                }
                matchDTO.setCcodes(cod);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);
        }
        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    /**
     * 竞彩混投对阵
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getHhDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible,int grade) throws Exception {
        getMatchForDanDuizhenByCodes("1",gamesProjectDTO, projectInfoDTO, flag, visible,true);
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    /**
     * 非竞彩混投对阵
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible,int grade) throws Exception {
        getMatchForDanDuizhenByCodes("0",gamesProjectDTO, projectInfoDTO, flag, visible,true);
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }


    /**
     *  一场致胜（奖金优化）
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getYczsJJYHDuizhen(int yczsgodFlag,GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible, int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        setWinzs(projectInfoDTO);
        if (flag == 1 ||visible) {
            if (yczsgodFlag == 1) {//是否是神单优化
                getSpAndMatch(gamesProjectDTO, projectInfoDTO, true, false, false, true, true);

            } else {
                getSpAndMatch(gamesProjectDTO, projectInfoDTO, true, false,false,true,false);
            }
        }
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
        gamesProjectDTO.setProcessInfo(processDTO);

    }

    /**
     * 竞彩2选1奖金优化对阵.
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @param fromShareRank
     * @throws Exception
     */
    private static void getJJYHDuizhen2xuan1(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible, int grade,  boolean fromShareRank) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        projectInfoDTO.setJjyh("1");
        if (flag == 1) {
            // 是否是方案所有人查看方案, same=1 是, same=0 不是
            //todo 暂时没有flag=1  见cp-appapi项目JinCaiUtil
        }
        setWinzs(projectInfoDTO);

        if (visible) {
            getSpAndMatch(gamesProjectDTO, projectInfoDTO, fromShareRank, true,true,false,false);
        }
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
        gamesProjectDTO.setProcessInfo(processDTO);
    }

    /**
     * 复试单关投注显示详情(混投)
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getHhDanDuizhenByCodes(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible,int grade) throws Exception {
        getMatchForDanDuizhenByCodes("",gamesProjectDTO, projectInfoDTO, flag, visible,true);
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    /**
     *复试投注单关显示详情.
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getDanDuizhenByCodes(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible, int grade) throws Exception {
        getMatchForDanDuizhenByCodes("",gamesProjectDTO, projectInfoDTO, flag, visible,false);
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    /**
     *  单关投注详情,走奖金优化接口投注的.
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getNewDanDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible, int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        projectInfoDTO.setGg("单关");
        if (flag == 1) {
            // 是否是方案所有人查看方案, same=1 是, same=0 不是
            //todo 暂时没有flag=1  见cp-appapi项目JinCaiUtil
        }
        setWinzs(projectInfoDTO);
        if (flag == 1 || visible) {
            getSpAndMatchForNewDanDuizhen(gamesProjectDTO, projectInfoDTO);
        }
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    /**
     * 一场制胜 单关配对阵详情
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @return
     * @throws Exception
     */
    private static void getWinDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible, int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        Boolean showCode = Boolean.valueOf(projectInfoDTO.getShowCodes());
        String dgpfile = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + projectInfoDTO.getHid() + "_dgp.xml";
        JXmlWrapper jxml = JXmlWrapper.parse(new File(dgpfile));
        JXmlWrapper rowsXml = jxml.getXmlNode("rows");

        if (flag == 1 || visible) {
            Map<String, String> matchdata = new HashMap<String, String>();
            String xmlpath =  FileConstant.GUOGUAN_DIR +  projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" +  projectInfoDTO.getHid() + ".xml";
            JXmlWrapper pxml = JXmlWrapper.parse(new File(xmlpath));
            int count = pxml.countXmlNodes("item");
            for (int i = 0; i < count; i++) {
                String id = pxml.getStringValue("item[" + i + "].@id");
                String hs = pxml.getStringValue("item[" + i + "].@hs");
                String gs = pxml.getStringValue("item[" + i + "].@vs");
                String hhs = pxml.getStringValue("item[" + i + "].@hhs");
                String hvs = pxml.getStringValue("item[" + i + "].@hvs");
                matchdata.put(id, hhs + ":" + hvs + "," + hs + ":" + gs);
            }

            String id1 = rowsXml.getStringValue("@id1");// 配对的号 +配对
            String id2 = rowsXml.getStringValue("@id2").split("-")[0]; // 场次号
            String rs1 = matchdata.get(id1);
            String rs2 = matchdata.get(id2);
            projectInfoDTO.setRs1(rs1);
            projectInfoDTO.setRs2(rs2);
        }
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if(!showCode){
            projectInfoDTO.setCcodes("");
        }
        if (flag == 1) {
            // 是否是方案所有人查看方案, same=1 是, same=0 不是
            //todo 暂时没有flag=1  见cp-appapi项目JinCaiUtil
        }
        setWinzs(projectInfoDTO);
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
        gamesProjectDTO.setProcessInfo(processDTO);

    }

    /**
     *  奖金优化对阵
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param visible
     * @param grade
     * @param fromShareRank
     * @return
     * @throws Exception
     */
    private static void getJJYHDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible, int grade,  boolean fromShareRank) throws Exception {
        ProcessDTO processDTO = getProcess(projectInfoDTO, grade);
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        int shareGod = Integer.valueOf(projectInfoDTO.getShareGod());// shareGod 0  普通  1  分享神单  2  神单跟买
        logger.info("方案编号:"+projectInfoDTO.getHid().toLowerCase()+" 期次id:"+projectInfoDTO.getPid()+" shareGod:"+shareGod+" ccodes:"+projectInfoDTO.getCcodes()+" fromShareRank:"+fromShareRank);
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        String yhmoney = projectInfoDTO.getTmoney();
        String ccodes = projectInfoDTO.getCcodes();
        String yhfile = ccodes.replace("_n.txt", "_yh.xml");
        String originxmlpath = FileConstant.BASE_PATH + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + yhfile;
        double tm = Double.valueOf(StringUtils.isEmpty(yhmoney) ? "0.0" : yhmoney);
        //1、奖金优化单子 2、tm>100  3、tm>优化金额
        boolean b = true;
        if (tm > 100 && fromShareRank) {
            int matchCnt = getmatchCnt(originxmlpath);
            if(matchCnt!=0){
                if(matchCnt * 10<tm){
                    yhmoney= matchCnt * 10 + "";
                }else{
                    //优化金额>自买金额 不显示ccode 赔率 按正常神单处理
                    b=false;
                }
            }
        }
        projectInfoDTO.setYhMoney(yhmoney);
        // 奖金优化标识
        projectInfoDTO.setJjyh("1");
        if (flag == 1) {
            //todo 暂时没有flag=1  见cp-appapi项目JinCaiUtil
        }
        setWinzs(projectInfoDTO);
        if (visible) {
            getSpAndMatch(gamesProjectDTO, projectInfoDTO, fromShareRank, b,false,false,false);
        }
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
        gamesProjectDTO.setProcessInfo(processDTO);
    }


    private static void  getMatchForDanDuizhenByCodes(String matchTye,GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag, boolean visible,boolean isHh) throws  Exception{
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if(StringUtil.isEmpty(matchTye)){
            projectInfoDTO.setGg("单关");
        }
        if (flag == 1) {
            // 是否是方案所有人查看方案, same=1 是, same=0 不是
            //todo 暂时没有flag=1  见cp-appapi项目JinCaiUtil
        }

        String ccodes = projectInfoDTO.getCcodes();
        String gid = projectInfoDTO.getGid();
        String pid = projectInfoDTO.getPid();
        String hid = projectInfoDTO.getHid();
        String ifile = projectInfoDTO.getIfile();
        if (!"1".equals(ifile)) {
            if (!StringUtil.isEmpty(matchTye)){
                setGg(matchTye,projectInfoDTO);
            }
            setWinzs(projectInfoDTO);
            if (flag == 1 || visible) {
                Map<String, String> map = new HashMap<>(); // key 131124020, vaule 3/1
                Map<String, String> dmap = new HashMap<>();

                if (!StringUtil.isEmpty(matchTye)){
                    setDan(matchTye,map,dmap, ccodes);
                }else {
                    if (isHh) {
                        getHhMultiple(map, ccodes);
                    } else {
                        getMultiple(map, ccodes);
                    }
                }
                String xmlpath = FileConstant.GUOGUAN_DIR + gid + "/" + pid + "/proj/" + hid + ".xml";
                JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));
                int count = xml.countXmlNodes("item");
                String start = xml.getStringValue("item[" + 0 + "].@id").substring(0, 6); // 场次编号
                String end = xml.getStringValue("item[" + (count - 1) + "].@id").substring(0, 6); // 场次编号
                String expect = start;
                if (!start.equals(end)) {
                    expect = start + "-" + end;
                }
                MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
                List<MatchDTO> matchDTOList = new ArrayList<>();
                Map<String, Map<String, String>> zqDataMap = new HashMap<>();
                long l1 = System.currentTimeMillis();
                Map<String, String> jsbfmap = LiveBfUtil.jsbf(projectInfoDTO.getGid(), expect, zqDataMap, true);
                long l2 = System.currentTimeMillis();
                logger.info("订单号：{}，访问资料库即时比分耗时：{}ms",projectInfoDTO.getHid(),(l2-l1));
                Calendar c = Calendar.getInstance();
                for (int i = 0; i < count; i++) {
                    String id = xml.getStringValue("item[" + i + "].@id"); // 场次编号
                    String tdate = "20" + id.substring(0, 2) + "-" + id.substring(2, 4) + "-" + id.substring(4, 6);
                    c.setTime(ConcurrentSafeDateUtil.parse(tdate, day_pattern));
                    String name = "周" + wk[c.get(Calendar.DAY_OF_WEEK) - 1] + "" + id.substring(6, 9);
                    String[] ms = new String[1];
                    ms[0] = id;
                    String[] minfoarr = new String[8];
                    minfoarr[0] = name;
                    minfoarr[1] = xml.getStringValue("item[" + i + "].@hn");
                    minfoarr[2] = xml.getStringValue("item[" + i + "].@lose");
                    minfoarr[3] = xml.getStringValue("item[" + i + "].@vn");
                    minfoarr[4] = xml.getStringValue("item[" + i + "].@hs");
                    minfoarr[5] = xml.getStringValue("item[" + i + "].@vs");
                    minfoarr[6] = xml.getStringValue("item[" + i + "].@hhs");
                    minfoarr[7] = xml.getStringValue("item[" + i + "].@hvs");
                    MatchDTO matchDTO = getMatchDTO(ms, minfoarr, jsbfmap, zqDataMap);
                    if (!StringUtil.isEmpty(matchTye)){
                        String spvalue = xml.getStringValue("item[" + i + "].@spvalue");
                        matchDTO.setIsdan(isDan(dmap, id));
                        if ("1".equals(matchTye)) {
                            getHhCcode(Boolean.valueOf(projectInfoDTO.getShowCodes()),spvalue, matchDTO, map, id);
                        } else if ("0".equals(matchTye)) {
                            getCcodes("0",Boolean.valueOf(projectInfoDTO.getShowCodes()),gid,spvalue, matchDTO, map, id);
                        }
                    }else {
                        if (isHh) {
                            String spvalues = xml.getStringValue("item[" + i + "].@spvalue");
                            getHhCcodes(Boolean.valueOf(projectInfoDTO.getShowCodes()),spvalues, matchDTO, map, id);
                        } else {
                            String spvalue = xml.getStringValue("item[" + i + "].@spvalue");
                            getCcodes("",Boolean.valueOf(projectInfoDTO.getShowCodes()),gid,spvalue, matchDTO, map, id);
                        }
                    }
                    matchDTOList.add(matchDTO);
                }
                matchInfoDTO.setMatchs(matchDTOList);
                gamesProjectDTO.setMatchInfo(matchInfoDTO);
            }
        }

    }

    private static void getSpAndMatchForNewDanDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO)throws Exception{
        Map<String, String> matchdata = new HashMap<>();
        Map<String, String> spmap = new HashMap<>();
        SpValueDTO spValueDTO = new SpValueDTO();
        String expect = getSpValue(spValueDTO, projectInfoDTO, matchdata, spmap);
        Map<String, Map<String, String>> zqDataMap = new HashMap<>();
        long l1 = System.currentTimeMillis();
        Map<String, String> jsbfmap = LiveBfUtil.jsbf(projectInfoDTO.getGid(), expect, zqDataMap, true);
        long l2 = System.currentTimeMillis();
        logger.info("订单号：{}，访问资料库即时比分耗时：{}ms",projectInfoDTO.getHid(),(l2-l1));

        String yhfile = projectInfoDTO.getCcodes().replace("_n.txt", "_yh.xml");
        String xmlpath =  FileConstant.BASE_PATH + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + yhfile;
        JXmlWrapper jxml = JXmlWrapper.parse(new File(xmlpath));
        String yhmatchs = jxml.getStringValue("row.@matchs");
        String yhcode = jxml.getStringValue("row.@code");
        Map<String, String> map = new HashMap<String, String>(); // key 131124020, vaule 3/1
        getMultiple(map, yhcode);
        MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
        List<MatchDTO> matchDTOList = new ArrayList<>();

        if (!"70".equals(projectInfoDTO.getGid())) {
            // 竞彩足球胜平负（72），让球胜平负（90）
            String[] yhmatchsList = yhmatchs.split("\\/");
            for (int i = 0; i < yhmatchsList.length; i++) {
                String[] ms = yhmatchsList[i].replace("]", "").split("\\[", -1);
                String minfo = matchdata.get(ms[0]);
                String[] minfoarr = minfo.split("\\_", -1);
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, jsbfmap, zqDataMap);
                matchDTO.setIsdan("0");//是否是胆
                String spvalue = spmap.get(ms[0]);
                String id = ms[0];
                getCcodes("",Boolean.valueOf(projectInfoDTO.getShowCodes()),projectInfoDTO.getGid(),spvalue, matchDTO, map, id);
                matchDTOList.add(matchDTO);

            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);
        }
    }

    private  static void getHhCcode(boolean showCodes,String spvalue, MatchDTO matchDTO,Map<String, String> map,String id){
        String[] spvalues = spvalue.split("\\|", -1); //
        String[] spf = spvalues[4].split(",");
        String[] rqspf = spvalues[0].split(",");
        String[] cbf = spvalues[1].split(",");
        String[] bqc = spvalues[2].split(",");
        String[] jqs = spvalues[3].split(",");
        String[] sp = null;

        String cod = "";
        String tz = map.get(id);
        if (!StringUtil.isEmpty(tz)) {
            String[] d = tz.split("\\+");
            for (int j = 0; j < d.length; j++) {
                // 混合70HM2013112541848575
                // HH|131125001>JQS=4+CBF=3:1/3:2+BQC=1-1,131125002>BQC=1-1/1-0|2*1"
                int index = 0;
                String[] arr = d[j].split("\\=");
                String wf = arr[0];
                String[] tarra = arr[1].split("\\/");
                for (int k = 0; k < tarra.length; k++) {
                    if ("JQS".equals(wf)) {
                        index = Integer.valueOf(JQS.get(tarra[k]));
                        sp = jqs;
                    } else if ("BQC".equals(wf)) {
                        index = Integer.valueOf(BQC.get(tarra[k]));
                        sp = bqc;
                    } else if ("CBF".equals(wf)) {
                        index = Integer.valueOf(CBF.get(tarra[k]));
                        sp = cbf;
                    } else if ("SPF".equals(wf)) {
                        index = Integer.valueOf(SPF.get(tarra[k]));
                        sp = spf;
                    } else {
                        index = Integer.valueOf(SPF.get(tarra[k]));
                        sp = rqspf;
                    }

                    if ("".equals(cod)) {
                        cod = "HH|" + wf + "=" + tarra[k] + "_" + sp[index];
                    } else {
                        cod += "," + wf + "=" + tarra[k] + "_" + sp[index];
                    }

                }
            }
        }
        if (showCodes) {
            matchDTO.setCcodes(cod);
        } else {
            matchDTO.setCcodes("");
        }
    }

    private static void getHhCcodes(boolean showCodes,String spvalue, MatchDTO matchDTO,Map<String, String> map,String id){
        String[] spvalues = spvalue.split("\\|", -1); //
        String[] spf = spvalues[4].split(",");
        String[] rqspf = spvalues[0].split(",");
        String[] cbf = spvalues[1].split(",");
        String[] bqc = spvalues[2].split(",");
        String[] jqs = spvalues[3].split(",");
        String[] sp = null;

        String cod = "";
        String tz = map.get(id);
        if (!StringUtil.isEmpty(tz)) {
            String[] d = tz.split("\\+");
            for (int j = 0; j < d.length; j++) { // 混合70HM2013112541848575
                // //HH|131125001>JQS=4+CBF=3:1/3:2+BQC=1-1,131125002>BQC=1-1/1-0|2*1"
                int index = 0;
                String[] arr = d[j].split("\\=");
                String wf = arr[0];
                if ("".equals(cod)) {
                    cod = wf;
                } else {
                    cod += "+" + wf;
                }
                String[] tarra = arr[1].split("\\/");
                for (int k = 0; k < tarra.length; k++) {
                    if ("JQS".equals(wf)) {
                        index = Integer.valueOf(JQS.get(tarra[k].split("\\_")[0]));
                        sp = jqs;
                    } else if ("BQC".equals(wf)) {
                        index = Integer.valueOf(BQC.get(tarra[k].split("\\_")[0]));
                        sp = bqc;
                    } else if ("CBF".equals(wf)) {
                        index = Integer.valueOf(CBF.get(tarra[k].split("\\_")[0]));
                        sp = cbf;
                    } else if ("SPF".equals(wf)) {
                        index = Integer.valueOf(SPF.get(tarra[k].split("\\_")[0]));
                        sp = spf;
                    } else {
                        index = Integer.valueOf(SPF.get(tarra[k].split("\\_")[0]));
                        sp = rqspf;
                    }

                    if (k == 0) {
                        cod += "|" + tarra[k].split("\\_")[0] + "_" + sp[index] + "_"
                                + Integer.valueOf(tarra[k].split("\\_")[1]) * 2;
                    } else {
                        cod += "," + tarra[k].split("\\_")[0] + "_" + sp[index] + "_"
                                + Integer.valueOf(tarra[k].split("\\_")[1]) * 2;
                    }
                }

            }
        }
        if (showCodes) {
            matchDTO.setCcodes(cod);
        } else {
            matchDTO.setCcodes("");
        }

    }

    private static void setGg(String matchType,ProjectInfoDTO projectInfoDTO){
        String ccodes = projectInfoDTO.getCcodes(); // 投注号码
        // SPF|131122001=3,131122003=3,131122006=3|2*1,3*1
        // SPF|131129002=3,131129005=3$131129003=3,131129006=3|3*1(胆)
        String[] codarr = ccodes.split("\\|");
        if (codarr.length >= 3) {
            if (!"1".equals(matchType)) {
                if ("1*1".equals(codarr[2])) {
                    codarr[2] = "单关";
                }
            }

            String gstr = codarr[2].replace("*", "串");
            projectInfoDTO.setGg(gstr.replaceAll("([,]+)1串1", ",单关").replaceAll("^1串1", "单关"));// 串关方式
        }
    }

    private static void getCcodes(String matchType,boolean showCodes,String gid,String spvalue, MatchDTO matchDTO,Map<String, String> map,String id){
        String[] sparr = spvalue.split("\\|", -1); // spvalue//赔率
        // jc_rqspf.xml|jc_cbf.xml|jc_bqc.xml|jc_jqs.xml|jc_spf.xml"
        String[] sp = null;
        Map<String, String> m = null;
        String prefix = null;
        switch (Integer.valueOf(gid)) {
            case 90: // 竞彩足球-让球胜平负 sp 2.50,3.35,2.21
                sp = sparr[0].split(",");
                m = SPF;
                prefix = "RQSPF";
                break;
            case 91: // 竞彩足球-比分
                sp = sparr[1].split(",");
                m = CBF;
                prefix = "CBF";
                break;
            case 92: // 竞彩足球-半全场
                sp = sparr[2].split(",");
                m = BQC;
                prefix = "BQC";
                break;
            case 93: // 竞彩足球-总进球数
                sp = sparr[3].split(",");
                m = JQS;
                prefix = "JQS";
                break;
            case 72: // 竞彩足球-胜平负
                sp = sparr[4].split(",");
                m = SPF;
                prefix = "SPF";
                break;
            default:
                break;
        }

        String cod = "";
        String tz = map.get(id);
        if (StringUtil.isEmpty(matchType)) {
            if (!StringUtil.isEmpty(tz)) {
                String[] d = tz.split("\\/");
                for (int j = 0; j < d.length; j++) {
                    String[] tb = d[j].split("\\_");
                    int index = Integer.valueOf(m.get(tb[0]));
                    if ("".equals(cod)) {
                        cod = prefix + "|" + tb[0] + "_" + sp[index] + "_" + tb[1]; // 加上倍数
                    } else {
                        cod += "," + tb[0] + "_" + sp[index] + "_" + tb[1]; // 加上倍数
                    }
                }
            }
        }else {
            if (!StringUtil.isEmpty(tz)) {
                String[] d = tz.split("/");
                for (int j = 0; j < d.length; j++) {
                    int index = Integer.valueOf(m.get(d[j]));
                    if ("".equals(cod)) {
                        cod = prefix + "|" + d[j] + "_" + sp[index];
                    } else {
                        cod += "," + d[j] + "_" + sp[index];
                    }
                }
            }
        }

        if (showCodes) {
            matchDTO.setCcodes(cod);
        } else {
            matchDTO.setCcodes("");
        }
    }

    private static String getSpValue(SpValueDTO spValueDTO, ProjectInfoDTO projectInfoDTO, Map<String, String> matchdata, Map<String, String> spmap) throws Exception {
        String xmlpath = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" + projectInfoDTO.getHid().toLowerCase() + ".xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));
        int count = xml.countXmlNodes("item");
        String start = xml.getStringValue("item[" + 0 + "].@id").substring(0, 6); // 场次编号
        String end = xml.getStringValue("item[" + (count - 1) + "].@id").substring(0, 6); // 场次编号
        String expect = start;
        if (!start.equals(end)) {
            expect = start + "-" + end;
        }
        Calendar c = Calendar.getInstance();
        spValueDTO.setCount(count + "");
        List<Map<String, String>> spList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, String> tem = new HashMap<>();
            String id = xml.getStringValue("item[" + i + "].@id");
            String hn = xml.getStringValue("item[" + i + "].@hn");
            String lose = xml.getStringValue("item[" + i + "].@lose");
            String vn = xml.getStringValue("item[" + i + "].@vn");
            String spvalue = xml.getStringValue("item[" + i + "].@spvalue");
            String hs = xml.getStringValue("item[" + i + "].@hs");
            String gs = xml.getStringValue("item[" + i + "].@vs");
            String hhs = xml.getStringValue("item[" + i + "].@hhs");
            String hvs = xml.getStringValue("item[" + i + "].@hvs");

            tem.put("id", id);
            tem.put("value", spvalue);
            spList.add(tem);

            String tdate = "20" + id.substring(0, 2) + "-" + id.substring(2, 4) + "-" + id.substring(4, 6);
            c.setTime(ConcurrentSafeDateUtil.parse(tdate, day_pattern));
            String mid = "周" + wk[c.get(Calendar.DAY_OF_WEEK) - 1] + "" + id.substring(6, 9);
            matchdata.put(id, mid + "_" + hn + "_" + lose + "_" + vn + "_" + hs + "_" + gs + "_" + hhs + "_" + hvs);
            spmap.put(id, spvalue);
        }
        spValueDTO.setSpList(spList);
        return expect;
    }

    private static void getSpAndMatch(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO,boolean fromShareRank, boolean b,boolean is2x1,boolean isYczs,boolean isYczsgod)  throws Exception{
        Map<String, String> matchdata = new HashMap<>();
        Map<String, String> spmap = new HashMap<>();
        SpValueDTO spValueDTO = new SpValueDTO();
        String expect = getSpValue(spValueDTO, projectInfoDTO, matchdata, spmap);

        Map<String, Map<String, String>> zqDataMap = new HashMap<>();
        long l1 = System.currentTimeMillis();
        Map<String, String> jsbfmap = LiveBfUtil.jsbf(projectInfoDTO.getGid(), expect, zqDataMap, true);
        long l2 = System.currentTimeMillis();
        logger.info("订单号：{}，访问资料库即时比分耗时：{}ms",projectInfoDTO.getHid(),(l2-l1));

        if (isYczs) {
            String yczsfile = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + projectInfoDTO.getHid() + "_yczs.xml";
            getYczsJjyhGod(gamesProjectDTO, projectInfoDTO,yczsfile, matchdata, jsbfmap, spmap, isYczsgod, zqDataMap);
        } else {
            getJjyhstr(gamesProjectDTO, projectInfoDTO, matchdata, jsbfmap, spmap, fromShareRank && b, zqDataMap, is2x1);
        }
        if (fromShareRank&&b) {
            gamesProjectDTO.setSpvalueInfo(spValueDTO);
        }
    }
    private static void getYczsJjyhGod(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO,String yczsfilePath, Map<String, String> matchdata,
                                   Map<String, String> jsbfmap, Map<String, String> spmap,boolean shareGod, Map<String, Map<String, String>> jcDataMap) {
        String ccode = projectInfoDTO.getCcodes();
        String yhfile = ccode.replace("_n.txt", "_yh.xml");
        String originxmlpath = FileConstant.BASE_PATH + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + yhfile;
        File file = new File(originxmlpath);
        if (file == null || !file.exists()) {
            return ;
        }
        JXmlWrapper jxml = JXmlWrapper.parse(file);
        int yhfs = Integer.parseInt(jxml.getStringValue("row.@yhfs"));
        String yhmatchs = jxml.getStringValue("row.@matchs");
        String yhcode = jxml.getStringValue("row.@code");
        int missmatch = 0;
        String missstr = jxml.getStringValue("row.@missmatch");
        if (!StringUtil.isEmpty(missstr)) {
            missmatch = Integer.parseInt(missstr);
        }

        MatchInfoDTO matchInfoDTO = new MatchInfoDTO();

        List<YCZSMatchInfoDTO> yczsMatchInfoDTOList = new ArrayList<>();
        matchInfoDTO.setFs(yhfslist[yhfs]);
        matchInfoDTO.setType("yczs");
        File yczsfile = new File(yczsfilePath);
        if (yczsfile == null||!yczsfile.exists()) {
            return ;
        }
        List<MatchDTO> matchDTOList = new ArrayList<>();
        Boolean showcode = Boolean.valueOf(projectInfoDTO.getShowCodes());
        String gid = projectInfoDTO.getGid();
        if ("70".equals(gid)) {// 竞彩足球混投
            yhmatchs = yhmatchs.substring(3); // 去掉HH|
            String[] yhmatchsLists = yhmatchs.split("\\,");
            JXmlWrapper yczsXml = JXmlWrapper.parse(yczsfile);
            List<JXmlWrapper> xmlNodeList = yczsXml.getXmlNodeList("row");
            for (JXmlWrapper row : xmlNodeList) {
                YCZSMatchInfoDTO yczsMatchInfoDTO = new YCZSMatchInfoDTO();
                JXmlWrapper zxitem = row.getXmlNode("zxitem");
                String zid = zxitem.getStringValue("@id");
                String zccodes = zxitem.getStringValue("@ccodes");
                String zxidTem = zid + ">" + zccodes;
                MatchDTO matchDTO=  appendYczsZxAndPpTem( yhmatchsLists,zxidTem, matchdata, jsbfmap, spmap, showcode, jcDataMap);
                yczsMatchInfoDTO.setZxitem(matchDTO);
                matchDTOList.add(matchDTO);
                if (!shareGod){
                     matchDTO = appendYczsZxAndPpTem(yhmatchsLists, zxidTem, matchdata, jsbfmap, spmap, showcode, jcDataMap);
                    yczsMatchInfoDTO.setZxitem(matchDTO);
                    JXmlWrapper ppitem = row.getXmlNode("ppitem");
                    String pid = ppitem.getStringValue("@id");
                    String pccodes = ppitem.getStringValue("@ccodes");
                    String ppidTem = pid + ">" + pccodes;
                    matchDTO = appendYczsZxAndPpTem(yhmatchsLists, ppidTem, matchdata, jsbfmap, spmap, showcode, jcDataMap);
                    yczsMatchInfoDTO.setPpitem(matchDTO);
                }
                yczsMatchInfoDTOList.add(yczsMatchInfoDTO);
            }

            if (shareGod){
                matchInfoDTO.setMatchs(matchDTOList);
            }else {
                matchInfoDTO.setYczsmatchs(yczsMatchInfoDTOList);
            }
            gamesProjectDTO.setMatchInfo(matchInfoDTO);

            PassInfoDTO passInfoDTO = new PassInfoDTO();
            List<PassDTO> passDTOList = new ArrayList<>();
            String gg = getGgInfo(yhcode, matchdata, passDTOList);
            projectInfoDTO.setGg(gg);
            passInfoDTO.setGg(gg);
            passInfoDTO.setMissmatch(String.valueOf(missmatch));
            passInfoDTO.setPassinfo(passDTOList);
            gamesProjectDTO.setPassInfo(passInfoDTO);
        }
    }

    private static MatchDTO appendYczsZxAndPpTem( String[] yhmatchsLists, String zxidTem, Map<String, String> matchdata,
                                             Map<String, String> jsbfmap, Map<String, String> spmap, boolean showcode, Map<String, Map<String, String>> jcDataMap) {
        MatchDTO matchDTO =new MatchDTO();
        List<String> list = new ArrayList<>();
        for (String str : yhmatchsLists) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        String[] yhmatchsList = list.toArray(new String[1]);
        for (int i = 0; i < yhmatchsList.length; i++) {
            if (!yhmatchsList[i].equals(zxidTem)) {
                continue;
            }
            String[] ms = yhmatchsList[i].split("\\>");
            String minfo = matchdata.get(ms[0]);
            String[] minfoarr = minfo.split("\\_", -1);
            matchDTO = getMatchDTO(ms, minfoarr, jsbfmap, jcDataMap);
            String[] spvalues = spmap.get(ms[0]).split("\\|", -1);

            String toustr = getjjyhCcodes(spvalues, ms[1]);
            String ccodes = toustr.substring(0, toustr.length() - 1);
            getCcodesforjjyh(matchDTO, showcode, ccodes);
        }
        return matchDTO;
    }


    private static void getJjyhstr(GamesProjectDTO gamesProjectDTO ,ProjectInfoDTO projectInfoDTO, Map<String, String> matchdata,
                                     Map<String, String> jsbfmap, Map<String, String> spmap,boolean shareGod, Map<String, Map<String, String>> jcDataMap,boolean is2x1) {
        String ccode = projectInfoDTO.getCcodes();
        String yhfile = ccode.replace("_n.txt", "_yh.xml");
        String originxmlpath = FileConstant.BASE_PATH + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + yhfile;
        File file = new File(originxmlpath);
        if (file == null || !file.exists()) {
            return ;
        }
        String gid = projectInfoDTO.getGid();
        Boolean showcode = Boolean.valueOf(projectInfoDTO.getShowCodes());
        JXmlWrapper jxml = JXmlWrapper.parse(file);
        int yhfs = Integer.parseInt(jxml.getStringValue("row.@yhfs"));
        String yhmatchs = jxml.getStringValue("row.@matchs");
        String yhcode = jxml.getStringValue("row.@code");
        int missmatch = 0;
        String missstr = jxml.getStringValue("row.@missmatch");
        if (!StringUtil.isEmpty(missstr)) {
            missmatch = Integer.parseInt(missstr);
        }
        MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
        List<MatchDTO> matchDTOList = new ArrayList<>();

        PassInfoDTO passInfoDTO = new PassInfoDTO();
        List<PassDTO> passDTOList = new ArrayList<>();
        matchInfoDTO.setFs(yhfslist[yhfs]);
        if ("70".equals(gid)||is2x1) {// 竞彩足球混投
            yhmatchs = yhmatchs.substring(3); // 去掉HH|
            String[] yhmatchsList = yhmatchs.split("\\,");
            for (int i = 0; i < yhmatchsList.length; i++) {
                String[] ms = yhmatchsList[i].split("\\>");
                String minfo = matchdata.get(ms[0]);
                String[] minfoarr = minfo.split("\\_", -1);
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, jsbfmap, jcDataMap);
                String[] spvalues = spmap.get(ms[0]).split("\\|", -1);
                String toustr="";
                if (is2x1) {
                    toustr = getjjyh2x1Ccodes(spvalues, ms[1]);
                } else {
                    toustr = getjjyhCcodes(spvalues, ms[1]);
                }
                String ccodes = toustr.substring(0, toustr.length() - 1);
                getCcodes(matchDTO, showcode, ccodes, shareGod);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);


            String gg="";
            if (is2x1) {
                gg = get2x1GgInfo(yhcode, matchdata, passDTOList);
            } else {
                gg = getGgInfo(yhcode, matchdata, passDTOList);
            }
            projectInfoDTO.setGg(gg);
            passInfoDTO.setGg(gg);
            passInfoDTO.setMissmatch(String.valueOf(missmatch));
            passInfoDTO.setPassinfo(passDTOList);
            gamesProjectDTO.setPassInfo(passInfoDTO);

        }else {
            // 竞彩足球胜平负（72），让球胜平负（90）
            String[] yhmatchsList = yhmatchs.split("\\/");
            for (int i = 0; i < yhmatchsList.length; i++) {
                String[] ms = yhmatchsList[i].replace("]", "").split("\\[", -1);
                String minfo = matchdata.get(ms[0]);
                String[] minfoarr = minfo.split("\\_", -1);
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, jsbfmap, jcDataMap);
                String spvalue = spmap.get(ms[0]);
                String[] sparr = spvalue.split("\\|", -1);
                String[] mid = ms[1].split("\\,", -1); // RQSPF=3+SPF=0
                String toustr = "";
                int length = mid.length;
                for (int m = 0; m < length; m++) {
                    if ("90".equals(gid)) {
                        String[] sp = sparr[0].split(",");
                        toustr += "RSPF" + "_" + mid[m] + "_" + sp[Integer.valueOf(SPF.get(mid[m]))] + ",";
                    } else if ("72".equals(gid)) {
                        String[] sp = sparr[4].split(",");
                        toustr += "SPF" + "_" + mid[m] + "_" + sp[Integer.valueOf(SPF.get(mid[m]))] + ",";
                    } else if ("91".equals(gid)) {
                        String[] sp = sparr[1].split(",");
                        toustr += "CBF" + "_" + mid[m] + "_" + sp[Integer.valueOf(CBF.get(mid[m]))] + ",";
                    } else if ("92".equals(gid)) {
                        String[] sp = sparr[2].split(",");
                        toustr += "BQC" + "_" + mid[m] + "_" + sp[Integer.valueOf(BQC.get(mid[m]))] + ",";
                    } else if ("93".equals(gid)) {
                        String[] sp = sparr[3].split(",");
                        toustr += "JQS" + "_" + mid[m] + "_" + sp[Integer.valueOf(JQS.get(mid[m]))] + ",";
                    } else {
                        // 错误彩种id
                    }
                }
                String ccodes = toustr.substring(0, toustr.length() - 1);
                getCcodes(matchDTO, showcode, ccodes, shareGod);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);

            StringBuffer sb = new StringBuffer();
            String gg = ""; // 过关方式
            String[] yhcodeList = yhcode.split(";");
            for (int i = 0; i < yhcodeList.length; i++) {
                String[] mcarr = yhcodeList[i].split("\\|");
                String[] marr = mcarr[1].split("\\,");
                String mstr = "";
                for (int j = 0; j < marr.length; j++) {
                    String[] mt = marr[j].split("\\=");
                    String minfo = matchdata.get(mt[0]);
                    String[] minfoarr = minfo.split("\\_");
                    if ("90".equals(gid)) {
                        mstr += minfoarr[1] + "(让" + getspfsel(mt[1]) + "),";
                    } else if ("91".equals(gid)) {
                        mstr += minfoarr[1] + "(" + getcbf(mt[1]) + "),";
                    } else if ("93".equals(gid)) {
                        mstr += minfoarr[1] + "(" + mt[1] + "球),";
                    } else {
                        mstr += minfoarr[1] + "(" + getspfsel(mt[1]) + "),";
                    }
                }

                String[] yhggbs = mcarr[2].split("\\_");
                PassDTO passDTO = new PassDTO();
                passDTO.setStr(mstr.substring(0, mstr.length() - 1));
                passDTO.setBs(yhggbs[1]);
                passDTOList.add(passDTO);
                if (gg.indexOf(yhggbs[0]) != -1) {
                } else {
                    gg = gg + yhggbs[0] + ",";
                }
            }
            projectInfoDTO.setGg(gg);
            passInfoDTO.setGg(gg);
            passInfoDTO.setMissmatch(String.valueOf(missmatch));
            passInfoDTO.setPassinfo(passDTOList);
            gamesProjectDTO.setPassInfo(passInfoDTO);
        }
    }

    private static String get2x1GgInfo(String yhcode,Map<String, String> matchdata, List<PassDTO> passDTOList){
        String gg = ""; // 过关方式
        String[] yhcodeList = yhcode.split("\\;");
        StringBuilder mstr = null;

        for (int i = 0; i < yhcodeList.length; i++) {
            String[] mcarr = yhcodeList[i].split("\\|"); // HH|141209001>RQSPF=0,141209007>RQSPF=3|2*1_2
            String[] marr = mcarr[1].split("\\,"); // 141209001>RQSPF=0,141209007>RQSPF=3
            int length2 = marr.length;
            mstr = new StringBuilder();
            for (int j = 0; j < length2; j++) {
                String[] mt = marr[j].split("\\>"); // 141209001>RQSPF=0
                String minfo = matchdata.get(mt[0]);
                String[] minfoarr = minfo.split("\\_", -1);
                mstr.append(minfoarr[1]);
                mstr.append("(");
                mstr.append(get2xuan1(mt[1]));
                mstr.append("),");
            }

            String[] yhggbs = mcarr[2].split("\\_");
            PassDTO passDTO = new PassDTO();
            passDTO.setStr(mstr.substring(0, mstr.length() - 1));
            passDTO.setBs(yhggbs[1]);
            if (gg.indexOf(yhggbs[0]) != -1) {
            } else {
                gg = gg + yhggbs[0] + ",";
            }
            passDTOList.add(passDTO);
        }
        return gg;
    }

    private static String  getGgInfo(String yhcode,Map<String, String> matchdata, List<PassDTO> passDTOList){
        String gg = ""; // 过关方式
        String[] yhcodeList = yhcode.replaceAll("\n","").replaceAll(" ","").replaceAll("\r","").split("\\;"); // HH|130828003>RQSPF=3,130828004>SPF=0,130828005>SPF=0,130828006>SPF=3|4*1_1

        for (int i = 0; i < yhcodeList.length; i++) {
            String[] mcarr = yhcodeList[i].split("\\|");
            String[] marr = mcarr[1].split("\\,"); // 130828003>RQSPF=3,130828004>SPF=0
            String mstr = "";
            for (int j = 0; j < marr.length; j++) {
                String[] mt = marr[j].split("\\>");
                String minfo = matchdata.get(mt[0]);
                String[] minfoarr = minfo.split("\\_", -1);
                mstr += minfoarr[1] + "(";
                String[] mid = mt[1].split("\\+"); // RQSPF=3+SPF=0

                for (int m = 0; m < mid.length; m++) {
                    String key = mid[m].split("\\=")[0]; // RQSPF SPF
                    String val = mid[m].split("\\=")[1]; // 0/3 3
                    String[] values = val.split("\\/");
                    for (int k = 0; k < values.length; k++) {
                        if ("RQSPF".equals(key)) {
                            mstr += "让" + getspfsel(values[k]);
                        } else if ("SPF".equals(key)) {
                            mstr += getspfsel(values[k]);
                        } else if ("BQC".equals(key)) {
                            mstr += getspfsel(values[k]);
                        } else if ("JQS".equals(key)) {
                            mstr += values[k] + "球";
                        } else if ("CBF".equals(key)) {
                            mstr += getcbf(values[k]);
                        }
                    }
                }

                mstr += "),";
            }
            String[] yhggbs = mcarr[2].split("\\_");
            PassDTO passDTO = new PassDTO();
            passDTO.setStr(mstr.substring(0, mstr.length() - 1));
            passDTO.setBs(yhggbs[1]);

            if (gg.indexOf(yhggbs[0]) != -1) {
            } else {
                gg = gg + yhggbs[0] + ",";
            }
            passDTOList.add(passDTO);
        }
        return gg;
    }

    private static void getCcodes(MatchDTO matchDTO,boolean showcode,String ccodes,boolean shareGod){
        if (showcode) {
            matchDTO.setCcodes(ccodes);
            matchDTO.setIsEncrypt("0");
        }else {
            // 此处ccodes需要加密显示 ios android 原加密方式不同 现在统一
            matchDTO.setCcodes(shareGod ? SecurityTool.iosencrypt(ccodes) : "");
            matchDTO.setIsEncrypt("1");
        }
    }

    private static void getCcodesforjjyh(MatchDTO matchDTO,boolean showcode,String ccodes){
        if (showcode) {
            matchDTO.setCcodes(ccodes);
        }else {
            matchDTO.setCcodes( "");
        }
    }

    private static String isDan(Map<String, String> map, String key) {
        return map.get(key) == null ? "0" : "1";
    }

    /**
     * 设胆map
     * @param dmap
     */
    private static void setDan(String matchType,Map<String, String> map, Map<String, String> dmap,String ccodes){
        String str= "=";
        if ("1".equals(matchType)) {
            str= ">";
        }
        String[] codarr = ccodes.split("\\|");
        if (codarr.length >= 3) {
            if (codarr[1].indexOf("$") > 0) {
                String dan = codarr[1].split("\\$")[0];
                String[] darr = dan.split(",");
                String[] da = null;
                for (int i = 0; i < darr.length; i++) {

                    da = darr[i].split(str);
                    dmap.put(da[0], da[1]);
                }
            }
        }

        if (codarr.length >= 3) {
            String[] tarr = codarr[1].replaceAll("\\$", ",").split(",");
            String[] xz = null;
            for (int i = 0; i < tarr.length; i++) {
                xz = tarr[i].split(str);
                map.put(xz[0], xz[1]);
            }
        }
    }

    /**
     * 混投倍数
     * @param map
     * @param ccodes
     */
    private static void getHhMultiple( Map<String, String> map, String ccodes){
        String dgcodes = formatCodeByJinCai(ccodes); // 150105302>SFC=11_5/12_5,150105303>SFC=12_5+DXF=12_5
        String[] tarr = dgcodes.split("\\,");

        String[] xz = null;
        for (int i = 0; i < tarr.length; i++) {
            xz = tarr[i].split("\\>");
            map.put(xz[0], xz[1]);
        }
    }

    private static void getMultiple( Map<String, String> map,String codeStr){
        String[] tarr = codeStr.split(";");
         // key 131124020, vaule 3/1
        String[] xz = null;
        String[] iz = null;
        for (int i = 0; i < tarr.length; i++) {
            xz = tarr[i].split("\\|");
            iz = xz[1].split("=");
            String v = map.get(iz[0]);
            String value = iz[1] + "_" + (Integer.valueOf(xz[2].split("\\_")[1]) * 2); // 增加倍数
            if (!StringUtil.isEmpty(v)) {
                value = v + "/" + iz[1] + "_" + (Integer.valueOf(xz[2].split("\\_")[1]) * 2);
            }
            map.put(iz[0], value);
        }
    }

    private static void setWinzs(ProjectInfoDTO projectInfoDTO){
        if (projectInfoDTO.getWininfo() != null) {
            String[] winarr = projectInfoDTO.getWininfo().split("\\|");
            if (winarr.length >= 3) {
                // 中奖注数
                projectInfoDTO.setWinzs(winarr[0]);
            }
        }
    }

    private static MatchDTO getMatchDTO( String[] ms, String[] minfoarr, Map<String, String> jsbfmap,Map<String, Map<String, String>> jcDataMap){
        MatchDTO matchDTO = new MatchDTO();
        try {
            matchDTO.setId(ms[0]);
            matchDTO.setName(minfoarr[0]);
            matchDTO.setHn(minfoarr[1]);
            matchDTO.setGn(minfoarr[3]);
            matchDTO.setClose(minfoarr[2]);
            matchDTO.setHs(minfoarr[4]);
            matchDTO.setGs(minfoarr[5]);
            matchDTO.setHhs(minfoarr[6]);
            matchDTO.setHgs(minfoarr[7]);
            if (minfoarr.length > 8) {
                matchDTO.setBet0(minfoarr[8]);
                matchDTO.setBet1(minfoarr[9]);
                matchDTO.setBet3(minfoarr[10]);
                matchDTO.setSpvalue(minfoarr[11]);
            }
            if (jsbfmap!=null&&!jsbfmap.isEmpty()) {
                matchDTO.setJsbf(jsbfmap.get(ms[0]));
            }
            //添加对应资料库相关数据
            Map<String, String> jcData = jcDataMap.get(ms[0]);
            if (null == jcData) {
                matchDTO.setIsForward("0");
            } else {
                matchDTO.setIsForward("1");
                matchDTO.setQc(jcData.get("qc"));
                matchDTO.setSort(jcData.get("sort"));
                matchDTO.setRoundItemId(jcData.get("roundItemId"));
                matchDTO.setRid(jcData.get("rid"));
                matchDTO.setSid(jcData.get("sid"));
            }
        } catch (Exception e) {
            logger.error("组装资料库数据错误",e);
        }

        return matchDTO;
    }

    /**
     * 获取非竞彩混投对阵
     * @param gamesProjectDTO
     * @param flag
     * @param visible
     * @param grade
     * @throws Exception
     */
    private static void getDuizhen(GamesProjectDTO gamesProjectDTO, int flag, boolean visible, int grade) throws Exception {
        ProjectInfoDTO projectInfoDTO = gamesProjectDTO.getProjectInfo();
        if (!"1".equals(projectInfoDTO.getIfile())) {

        }
    }

    private static String getjjyh2x1Ccodes(String[] spvalues,String ccodes){
        String[] spf = spvalues[4].split(",");
        String[] rqspf = spvalues[0].split(",");
        String[] sp = null;
        String[] touzhuarr = ccodes.split("\\+"); // RQSPF=0+SPF=3
        int length3 = touzhuarr.length;
        StringBuilder toustr = new StringBuilder();
        for (int m = 0; m < length3; m++) {
            String key = touzhuarr[m].split("\\=")[0]; // RQSPF SPF
            String val = touzhuarr[m].split("\\=")[1]; // 0 3
            String[] values = val.split("\\/");
            int length2 = values.length;
            for (int k = 0; k < length2; k++) {
                int index = Integer.valueOf(SPF.get(values[k]));
                if ("SPF".equals(key)) {
                    sp = spf;
                } else {
                    sp = rqspf;
                }
                toustr.append(key).append("_");
                toustr.append(values[k]).append("_");
                toustr.append(sp[index]).append(",");
            }
        }
        return toustr.toString();
    }
    private static String getjjyhCcodes(String[] spvalues,String ccodes) {
        String[] spf = spvalues[4].split(",");
        String[] rqspf = spvalues[0].split(",");
        String[] cbf = spvalues[1].split(",");
        String[] bqc = spvalues[2].split(",");
        String[] jqs = spvalues[3].split(",");
        String[] sp = null;
        String[] touzuarr = ccodes.split("\\+"); // RQSPF=3+SPF=0
        String toustr = "";
        for (int j = 0; j < touzuarr.length; j++) {
            String key = touzuarr[j].split("\\=")[0]; // RQSPF SPF
            String val = touzuarr[j].split("\\=")[1]; // 0/3 3
            String[] values = getcbfsel(val).split("\\/");
            for (int k = 0; k < values.length; k++) {
                int index = 0;
                if ("JQS".equals(key)) {
                    index = Integer.valueOf(JQS.get(values[k]));
                    sp = jqs;
                } else if ("BQC".equals(key)) {
                    index = Integer.valueOf(BQC.get(values[k]));
                    sp = bqc;
                } else if ("CBF".equals(key)) {
                    index = Integer.valueOf(CBF.get(values[k]));
                    sp = cbf;
                } else if ("SPF".equals(key)) {
                    index = Integer.valueOf(SPF.get(values[k]));
                    sp = spf;
                } else {
                    index = Integer.valueOf(SPF.get(values[k]));
                    sp = rqspf;
                }
                toustr += key + "_" + values[k] + "_" + sp[index] + ",";
            }
        }
        return toustr;
    }

    public static int getmatchCnt(String xmlpath) {
        File file = new File(xmlpath);
        if (file == null || !file.exists()) {
            return 0;
        }
        JXmlWrapper jxml = JXmlWrapper.parse(file);
        String yhcode = jxml.getStringValue("row.@code");
        String[] yhcodeList = yhcode.split("\\;");
        return yhcodeList.length;
    }

    /**
     * 获取进度
     * @return
     */
     public static ProcessDTO getProcess(QueryProjAppPojo projectInfoDTO, int grade)throws ParseException{
         return mark(projectInfoDTO, grade);
     }

    public static ProcessDTO mark(QueryProjAppPojo projectInfoDTO, int grade) throws ParseException {
        Integer award=Integer.valueOf(projectInfoDTO.getAward());// 计奖标志（0 未计奖 1 正在计奖 2 已计奖)
        Integer istate=Integer.valueOf(projectInfoDTO.getIstate());// 状态(-1未支付 0 禁止认购 1 认购中,2 已满员 3过期未满撤销 4主动撤销 5已出票 6 已派奖)
        Integer icast=Integer.valueOf(projectInfoDTO.getCast());// 出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）
        Integer ireturn=Integer.valueOf(projectInfoDTO.getIreturn());// 是否派奖（0 未派奖 1 正在派 2 已派奖）

        String awarddate=projectInfoDTO.getAwarddate();// 计奖时间
        String endtimes=projectInfoDTO.getEndtime();// 截止时间
        String retdate=projectInfoDTO.getRetdate();// 派奖时间
        String gid=projectInfoDTO.getGid();// 游戏编号

        String kjtime="",pjtime="";
        if("01".equals(gid)||"07".equals(gid)){
            if(StringUtil.isEmpty(awarddate)){
                kjtime="(预计:"+ ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:45)";
                pjtime="(预计:"+ ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 22:00)";
            }else if((StringUtil.isEmpty(retdate))&&(!StringUtil.isEmpty(awarddate))){
                kjtime="("+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern2)+")";
                pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 22:00)";
            }else if(!StringUtil.isEmpty(retdate)){
                kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
                pjtime="("+ConcurrentSafeDateUtil.convert(retdate, patternDatabase, pattern2)+")";
            }
        }else if("03".equals(gid)){
            if(StringUtil.isEmpty(awarddate)){
                kjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:15)";
                pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:50)";
            }else if((StringUtil.isEmpty(retdate))&&(!StringUtil.isEmpty(awarddate))){
                kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
                pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:50)";
            }else if(!StringUtil.isEmpty(retdate)){
                kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
                pjtime="("+ConcurrentSafeDateUtil.convert(retdate, patternDatabase, pattern2)+")";
            }
        }else if("50".equals(gid)||"51".equals(gid)||"52".equals(gid)||"53".equals(gid)){
            if(StringUtil.isEmpty(awarddate)){
                kjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 20:40)";
                pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:00)";
            }else if((StringUtil.isEmpty(retdate))&&(!StringUtil.isEmpty(awarddate))){
                kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
                pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:00)";
            }else if(!StringUtil.isEmpty(retdate)){
                kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
                pjtime="("+ConcurrentSafeDateUtil.convert(retdate, patternDatabase, pattern2)+")";
            }
        } else if (gidJCZQ.contains(gid) || gidJCLQ.contains(gid)) {
            // 获取竞彩篮球,竞彩足球方案预计开奖时间
            String pid =projectInfoDTO.getPid();
            String hid = projectInfoDTO.getHid().toLowerCase();
            String xmlpath = FileConstant.GUOGUAN_DIR + gid + File.separator + pid + File.separator + "proj";
            File file = new File(xmlpath, hid + ".xml");
            if (file.exists()) {
                String t = getKjTimeForJincai(file, gid);
                if (!StringUtil.isEmpty(t)) {
                    kjtime = "(预计:" + ConcurrentSafeDateUtil.convert(t, patternDatabase, pattern2) + ")";
                }
            }
        }

        int kj = award >= 2 ? 1:0;//计奖标志（0 未计奖 1 正在计奖 2 已计奖)

        int isflg = 0;
        if(istate>0){
            isflg = (icast == 3) ? (istate > 2 ? 1 : 5) : (istate > 2 && istate<6) ? 1 : (icast == 2) ?  2 : 3; //出票状态5
            isflg = (kj == 1) ? ((isflg == 5) ? 6 : isflg ) : isflg; //开奖状态6
            isflg = (award == 2) ? ((isflg == 6) ? 7 : isflg ) : isflg;//计奖状态7
            isflg = (ireturn == 2) ? ((isflg == 7) ? 12 : isflg) : (ireturn == 1) ? ((isflg == 7)? 8 : isflg) : isflg; // 派奖中、已派奖
        }else{
            if(istate==0){
                isflg = 14;
            }else{
                isflg = 13;
            }
        }
        String phase = null;
        String percent = null;
        String state = null;
        switch (isflg) {
            case 1://撤单
                phase = "1";
                percent="30";
                state="已撤单";
                break;
            case 2://出票中2
                phase = "1";
                percent="80";
                state="出票中";
                break;
            case 3://等待出票3
                phase = "1";
                percent="70";
                state="等待出票";
                break;
            case 5://出票成功5
                phase = "1";
                percent="100";
                state="出票成功";
                break;
            case 6://已开奖6
                phase = "2";
                percent="100";
                state="已开奖";
                break;
            case 7://已计奖7
                phase = "3";
                percent="60";
                state="已计奖";
                break;
            case 8://派奖中8
                phase = "3";
                percent="80";
                state="派奖中";
                break;
            case 12://已派奖12
                phase = "3";
                percent="100";
                state="已派奖";
                break;
            case 13://未支付
                phase = "1";
                percent="10";
                state="未支付";
                break;
            case 14://处理中
                phase = "1";
                percent="15";
                state="处理中";
                break;
            default://发起0
                phase = "1";
                percent="20";
                state="已发起";
                break;
        }
        if (grade >= 1) {
            if ("出票中".equals(state) || "等待出票".equals(state)) {
                state = "约单中";
            } else if ("已撤单".equals(state)){
                state = "约单失败";
            } else if ("已发起".equals(state)) {
                state = "发起约单";
            } else if ("出票成功".equals(state)){
                state = "约单成功";
            }
        }
        return new ProcessDTO(phase, percent, state, kjtime, pjtime,(isflg + ""));
    }

    /**
     * 计算竞技彩开奖时间
     * @param file
     * @param gid
     * @return
     * @throws ParseException
     */
    public static String getKjTimeForJincai(File file, String gid) throws ParseException{
        String time = "";
        JXmlWrapper  xml = JXmlWrapper.parse(file);
        List<JXmlWrapper> items = xml.getXmlNodeList("item");
        if (items != null && items.size() > 0) {
            long maxTimes = 0L;
            Calendar ca = Calendar.getInstance();
            for (JXmlWrapper item : items) {
                long beginTimes = ConcurrentSafeDateUtil.parse(item.getStringValue("@bt"), patternDatabase).getTime();
                if (beginTimes > maxTimes) {
                    maxTimes = beginTimes;
                }
            }
            ca.setTimeInMillis(maxTimes);
            if (gidJCZQ.contains(gid)) {
                // 竞彩足球后推2小时20分钟
                ca.add(Calendar.HOUR_OF_DAY, 2);
                ca.add(Calendar.MINUTE, 40);
            } else if (gidJCLQ.contains(gid)) {
                // 竞彩篮球后推3小时
                ca.add(Calendar.HOUR_OF_DAY, 3);
            }
            time = ConcurrentSafeDateUtil.format(ca.getTime(), patternDatabase);
        }

        return time;
    }


    public static String formatCodeByJinCai(String dgCodes) {

        Map<String, String> spfMap = new HashMap<String, String>();
        Map<String, String> rqspfMap = new HashMap<String, String>();
        Map<String, String> cbfMap = new HashMap<String, String>();
        Map<String, String> jqsMap = new HashMap<String, String>();
        Map<String, String> bqcMap = new HashMap<String, String>();

        List<String> keylist = new ArrayList<String>();

        String[] tarr = dgCodes.split(";");
        String[] xz = null;
        String[] iz = null;
        String bs = null;
        for (int i = 0; i < tarr.length; i++) {
            xz = tarr[i].split("\\|");
            iz = xz[1].split(">");
            bs = xz[2].split("\\_")[1];

            if (!keylist.contains(iz[0])) {
                keylist.add(iz[0]);
            }

            if (iz[1].indexOf("RQSPF") > -1) {
                String v = rqspfMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    rqspfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    rqspfMap.put(iz[0], iz[1] + "_" + bs);
                }

            } else if (iz[1].indexOf("BQC") > -1) {
                String v = bqcMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    bqcMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    bqcMap.put(iz[0], iz[1] + "_" + bs);
                }

            } else if (iz[1].indexOf("JQS") > -1) {
                String v = jqsMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    jqsMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    jqsMap.put(iz[0], iz[1] + "_" + bs);
                }

            } else if (iz[1].indexOf("CBF") > -1) {
                String v = cbfMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    cbfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    cbfMap.put(iz[0], iz[1] + "_" + bs);
                }
            } else { // 胜平负
                String v = spfMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    spfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    spfMap.put(iz[0], iz[1] + "_" + bs);
                }

            }
        }

        StringBuilder sb = new StringBuilder();

        boolean flag = false;
        for (String key : keylist) {

            if (flag) {
                sb.append(",");
            } else {
                flag = true;
            }

            boolean start = true;

            if (rqspfMap.size() > 0) {
                String value = rqspfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (jqsMap.size() > 0) {
                String value = jqsMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (bqcMap.size() > 0) {
                String value = bqcMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (cbfMap.size() > 0) {
                String value = cbfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (spfMap.size() > 0) {
                String value = spfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
        }

        return sb.toString();
    }

    public static String getspfsel(String sel) {
        return sel.replace("0", "负").replace("1", "平").replace("3", "胜");
    }

    public static String getcbfsel(String sel) {
        return sel.replace("胜其它", "9:0").replace("平其它", "9:9").replace("负其它", "0:9");
    }

    public static String getcbf(String sel) {
        return sel.replace("9:0", "胜其它").replace("9:9", "平其它").replace("0:9", "负其它");
    }

    public static String get2xuan1(String sel) {
        return sel.replace("RQSPF=3", "主不败").replace("RQSPF=0", "客不败").replace("SPF=0", "客胜").replace("SPF=3", "主胜");
    }


}
