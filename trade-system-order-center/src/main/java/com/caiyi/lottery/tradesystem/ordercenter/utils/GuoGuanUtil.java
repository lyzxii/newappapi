package com.caiyi.lottery.tradesystem.ordercenter.utils;

import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.proj.ProjUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.util.comparable.ComparableUtil;
import order.bean.GetGuoGuanProject;
import order.bean.GuoGuanBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuoGuanUtil {

    private final static int pageSize = 25;

    private static Logger logger = LoggerFactory.getLogger("guoguan_cachemange");

    private static String DATA_DIR = "/opt/export/data/";

    public static final String cachekey1 = "jcfs"; // 存放过关统计的键值（已结束的成功方案）
    public static final String cachekey2 = "jcus"; // 存放过关统计的键值（未结束的成功方案）
    public static final String cachekey3 = "jcff"; // 存放过关统计的键值（已结束的流产方案）
    public static final String cachekey4 = "jcuf"; // 存放过关统计的键值（未结束的流产方案）

    public static List<String> prizeDesc = new ArrayList<>();
    static {
        prizeDesc.add("一等奖");
        prizeDesc.add("二等奖");
        prizeDesc.add("三等奖");
        prizeDesc.add("四等奖");
        prizeDesc.add("五等奖");
        prizeDesc.add("六等奖");
        prizeDesc.add("七等奖");
        prizeDesc.add("八等奖");
    }

    public static HashMap<String,String> bd= new HashMap<>();
    static{
        bd.put("850", "全部");
        bd.put("84", "胜负过关");
        bd.put("85", "胜平负");
        bd.put("86", "比分");
        bd.put("89", "总进球");
        bd.put("87", "半全场");
        bd.put("88", "上下单双");
    }

    public static HashMap<String,String> gradeDef= new HashMap<>();
    static{
        gradeDef.put("80", "一等奖,二等奖");
        gradeDef.put("81", "一等奖");
        gradeDef.put("82", "一等奖");
        gradeDef.put("83", "一等奖");

        gradeDef.put("01", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖");
        gradeDef.put("03", "直选,组三,组六");
        gradeDef.put("04", "五星奖,三星奖,二星奖,一星奖,大小单双,二星组选,五星通选一等奖,五星通选二等奖,五星通选三等奖");
        gradeDef.put("05", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("06", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("07", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖,七等奖");
        gradeDef.put("08", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("09", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("10", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");

        gradeDef.put("20", "五星奖,四星一等奖,四星二等奖,三星奖,二星奖,一星奖,大小单双,二星组选,五星通选一等奖,五星通选二等奖,五星通选三等奖,任选一,任选二,三星组三,三星组六");
        //0,0,0,0,1,27,0,0,0,0,0,0,0,0,0,0,0
//		gradeDef.put("50", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖,七等奖,八等奖,生肖乐,追加一等奖,追加二等奖,追加三等奖,追加四等奖,追加五等奖,追加六等奖,追加七等奖,,宝钻一等奖,宝钻二等奖,宝钻三等奖,宝钻四等奖");
        gradeDef.put("50", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖,追加一等奖,追加二等奖,追加三等奖,追加四等奖,追加五等奖,追加六等奖,追加七等奖,,宝钻一等奖,宝钻二等奖,宝钻三等奖,宝钻四等奖");
        gradeDef.put("51", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖");
        gradeDef.put("52", "一等奖");
        gradeDef.put("53", "直选,组三,组六");
        gradeDef.put("54", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("55", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("56", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("57", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("58", "任选一,任选二,任选三,任选四,任选五,任选六,同花,同花顺,顺子,豹子,对子,同花包选,同花顺包选,顺子包选,豹子包选,对子包选");
        gradeDef.put("59", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
    }

    // 同步hashmap（彩种_期号_查询类型，期次对应的过关统计数据）
    private  ConcurrentHashMap<String, List<GuoGuanBean>> maps = new ConcurrentHashMap<>();

    public  ConcurrentHashMap<String, List<GuoGuanBean>> getMaps() {
        return maps;
    }

    public  void setMaps(ConcurrentHashMap<String, List<GuoGuanBean>> maps) {
        this.maps = maps;
    }

    public static java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0.00");
    public static String df(double v){
        return df.format(v);
    }

    public static String [] getgrade(String value){
        for(String key : gradeDef.keySet()){
            if (value.equals(key)) {
                return gradeDef.get(key).split(",") ;
            }
        }
        return null;
    }

    public static String getmax(String[] info){
        int max=0;
        for (int i=0;i<info.length;i++){
            max=i;
            if (Integer.valueOf(info[i])>0){
                break;
            }
        }
        return info.length-max-1+"";
    }

    /**
     * 根据彩种id获取当前期次id.
     */
    public static String getPid(String gid) {
        String pid = null;
        String xmlpath = DATA_DIR + "phot" + File.separator + gid;
        File file = new File(xmlpath, "c.xml");
        if (file == null || !file.exists()) {
            return pid;
        }
        JXmlWrapper xml = JXmlWrapper.parse(file);
        List<JXmlWrapper> rows = xml.getXmlNodeList("row");
        for (JXmlWrapper row : rows) {
            int st = row.getIntValue("@st");
            // 10表示过关统计中
            if (st >= 10) {
                pid = row.getStringValue("@pid");
                break;
            }
        }
        return pid;
    }

    //屏蔽用户手机号和QQ号码
    public static String shield(String str){
        String regEx = "\\d{4}";
        String regEx1 = "\\d{5}\\d*";

        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(str);

        Pattern pat1=Pattern.compile(regEx1);
        Matcher mat1 = pat1.matcher(str);

        if(mat.find() && mat1.find()){
            str = str.replace(mat1.group(),mat.group()+"**");
        }
        return str;
    }

    /**
     * 数字彩,胜负彩,任九中奖排行榜数据.
     */
    public static int parsePrize(String lotid, String expect, String[] info, StringBuilder sb, List tlist) {
        int jj = 0;
        if (ProjUtils.SSZMaps.containsKey(lotid)){
            //数字彩(除胜负彩(80)，任九(81))
            jj = parseSzPrize(lotid, expect, info, sb);
        } else {
            //胜负彩,任9
            //全对(一等奖)
            String all = Integer.valueOf(info[0]) > 0 ? info[0] : "0";
            tlist.add(all);
            //错一注数(二等奖)
            String one = Integer.valueOf(info[1]) > 0 ? info[1] : "0";
            tlist.add(one);
            //正确场次
            tlist.add(getmax(info));
        }
        return jj;
    }

    /**
     * 数字彩中奖排行榜数据.
     */
    private static int parseSzPrize(String lotid, String expect, String[] info, StringBuilder sb) {
        Object[] arr = new Object[2];
        String prize = null;
        int jj = 0;
        if ("50".equals(lotid)){
            arr = parseDltPrize(expect, info);
            jj = Integer.parseInt(arr[0].toString());
            prize = arr[1].toString();
        } else if ("51".equals(lotid) || "01".equals(lotid)){
            prize = parseSsqPrize(info, 6);
        } else if ("07".equals(lotid)) {
            prize = parseSsqPrize(info, 7);
        } else if ("53".equals(lotid) || "03".equals(lotid)) {
            arr = parseP3Prize(lotid, info);
            jj = Integer.parseInt(arr[0].toString());
            prize = arr[1].toString();
        } else if ("52".equals(lotid)) {
            prize = parseP5Prize(info, lotid);
        }
        sb.append(JXmlUtil.createAttrXml("prize", prize.trim()));
        return jj;
    }

    /**
     * 双色球,七星彩中奖排行榜数据.
     */
    private static String parseSsqPrize(String[] info, int length) {
        StringBuilder prize = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (Integer.valueOf(info[i]) <= 0) {
                continue;
            }
            prize.append(prizeDesc.get(i));
            prize.append(info[i]);
            prize.append("注 ");
        }
        return prize.toString();
    }

    /**
     * 大乐透中奖排行榜数据.
     */
    private static Object[] parseDltPrize(String expect, String[] info) {
        int dltjj = 0;
        StringBuilder prize = new StringBuilder();
        Object[] arr = new Object[2];
        int pid = Integer.valueOf(expect);
        for (int i = 0; i < 8; i++) {
            int flag = Integer.valueOf(info[i]);
            if (flag <= 0) {
                continue;
            }
            prize.append(prizeDesc.get(i));
            prize.append(info[i]);
            prize.append("注 ");
            if (Integer.valueOf(expect) < 2014052) {
                switch (i) {
                    case 2: {
                        dltjj = dltjj + (2000 * flag);
                        break;
                    }
                    case 3: {
                        dltjj = dltjj + (1000 * flag);
                        break;
                    }
                    case 4: {
                        dltjj = dltjj + (200 * flag);
                        break;
                    }
                    default: {
                        // 不作任何处理
                    }
                }
            } else if (2014052 <= pid && pid <= 2014153) {
                switch (i) {
                    case 2: {
                        dltjj = dltjj + (500 * flag);
                        break;
                    }
                    case 3: {
                        dltjj = dltjj + (50 * flag);
                        break;
                    }
                    default: {
                        // 不作任何处理
                    }
                }
            }
        }
        arr[0] = Integer.valueOf(dltjj);
        arr[1] = prize.toString();
        return arr;
    }

    /**
     * 排列3,福彩3D中奖排行榜数据.
     */
    private static Object[] parseP3Prize(String lotid, String[] info) {
        int p3jj = 0;
        StringBuilder prize = new StringBuilder();
        Object[] arr = new Object[2];
        int length = getgrade(lotid).length;
        for (int i = 0; i < length; i++) {
            int temp = Integer.valueOf(info[i]);
            if (temp <= 0) {
                continue;
            }
            switch (i) {
                case 0: {
                    prize.append("直选");
                    break;
                }
                case 1: {
                    prize.append("组三");
                    p3jj = p3jj + (13 * temp);
                    break;
                }
                case 2: {
                    prize.append("组六");
                    p3jj = p3jj + (6 * temp);
                    break;
                }
                default: {
                    // 不作任何处理
                }
            }
            prize.append(info[i]);
            prize.append("注 ");
        }
        arr[0] = Integer.valueOf(p3jj);
        arr[1] = prize.toString();
        return arr;
    }

    /**
     * 排列5中奖排行榜数据.
     */
    private static String parseP5Prize(String[] info, String lotid) {
        int length = getgrade(lotid).length;
        StringBuilder prize = new StringBuilder();
        for (int i = 0; i < length; i++){
            if (Integer.valueOf(info[i]) > 0) {
                prize.append("一等奖");
                prize.append(info[i]);
                prize.append("注 ");
            }
        }
        return prize.toString();
    }

    /**
     * 根据期次区间字符串生成所有的期次
     * @param pid
     * 		（yyyy-MM-dd或者yyyyMMdd格式）
     * @param endpid
     * 		（yyyy-MM-dd或者yyyyMMdd格式）
     * @return
     */
    public static String[] getPidsInterval(String pid, String endpid) throws Exception{
        if(StringUtils.isEmpty(pid) || StringUtils.isEmpty(endpid)) {
            return null;
        }
        pid = pid.replaceAll("\\-", "");
        endpid = endpid.replaceAll("\\-", "");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date piddate = format.parse(pid);
        Date endpiddate = format.parse(endpid);
        // 将结束期号加一天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endpiddate);
        calendar.add(Calendar.DATE, 1);

        // 计算时间区间
        int days = (int)(calendar.getTimeInMillis() - piddate.getTime()) / (24*60*60*1000);
        String[] dateInterval = new String[days];
        for(int index = 0; index < days; index ++){
            calendar.setTime(piddate);
            calendar.add(Calendar.DATE, index);
            dateInterval[index] = format.format(calendar.getTime());
        }
        return dateInterval;
    }

    /**
     * 生成分页存放键值
     * @param key1
     * @param sortType
     * @param pageNo
     * @return
     */
    public static String getGuoGuanPageKey(String key1, String sortType, int pageNo) {
        return key1 + "_" + sortType + "_" + pageNo;
    }

    /**
     * 将list<GuoGuanBean>分页存储到redis中
     * @param list
     * @param redisClient
     * @param key
     */
    public static void toCachePage(List<GuoGuanBean> list, RedisClient redisClient, String key) {
        if (list == null || list.size() == 0) {
            logger.info("数据为空");
            return;
        }
        /******按照前端两种排序方式分别存储 **************/
        logger.info("[toCachePage] sortType=bonus key=" + key + " list.size=" + list.size());
        // 1. 税前奖金降序排列
        String sortSrt = "bonus desc, rrate desc";
        ComparableUtil.sort(list, sortSrt);
        savePageToCache(list, redisClient, key, "bonus");

        // 2. 回报率降序排列
        for(int k=0; k<list.size(); k++){
            GuoGuanBean bean = list.get(k);
            if (bean.getNickID() == null) {
                logger.info("[toCachePage] nickID=null");
                list.remove(k);// 不满足条件的数据不参与排序
                --k;
            } else if(bean.getNickID().indexOf("*****") > 0 || bean.getRrate() == 0){
                list.remove(k);// 不满足条件的数据不参与排序
                --k;
            }
        }
        logger.info("[toCachePage] sortType=rate key=" + key + " list.size=" + list.size());
        sortSrt = "rrate desc, bonus desc";
        ComparableUtil.sort(list, sortSrt);
        savePageToCache(list, redisClient, key, "rate");
    }

    /**
     * 执行缓存数据存储
     * @param list
     * @param redisClient
     * @param key
     * @param sortType
     */
    private static void savePageToCache(List<GuoGuanBean> list, RedisClient redisClient, String key, String sortType) {
        int beginId = 0; // 当前页开始下标
        int endId = 0; // 当前页结束下标
        int total = list.size(); // 总行数
        int tp = total / pageSize; // 总页数
        if (total % pageSize != 0) {
            tp += 1;
        }

        String pgkey = getGuoGuanPageKey(key, sortType, total);

        if (tp == 1){
            logger.info("过关统计 写入缓存只有一页pageKey：" + pgkey + "****" + tp);
            endId = total;
            String pageKey = getGuoGuanPageKey(key, sortType, tp);
            List<GuoGuanBean> guoGuanBeans = toSaveCachePage(list, beginId, endId);
            redisSetCachePage(pageKey,guoGuanBeans.toString(), Constants.TIME_DAY,redisClient);
            redisSetCachePage(pageKey + "_total", total+"", Constants.TIME_DAY,redisClient);//记录总记录数

        }else{
            logger.info("过关统计 写入缓存大于一页pageKey：" + pgkey + "****" + tp);
            for(int x=1; x <= tp; x++){
                // 计算开始和结束下标(页码从1开始)
                beginId = (x - 1) * pageSize;
                endId = beginId + pageSize;
                // 结束下标是否大于总行数
                if (endId > total) {
                    endId = total;
                }
                String pageMulKey = getGuoGuanPageKey(key, sortType, x);
                List<GuoGuanBean> guoGuanBeans = toSaveCachePage(list, beginId, endId);
                redisSetCachePage(pageMulKey,guoGuanBeans.toString(),Constants.TIME_DAY,redisClient);
                redisSetCachePage(pageMulKey + "_total",total+"",Constants.TIME_DAY,redisClient);//记录总记录数
            }
        }
    }

    /**
     * 分页数据存储到缓存
     * @param key
     * @param value
     * @param time
     * @param redisClient
     * @return
     */
    private static boolean redisSetCachePage(String key, String value, int time, RedisClient redisClient) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        cacheBean.setValue(value);
        cacheBean.setTime(time);
        boolean rsp = redisClient.setString(cacheBean,logger, SysCodeConstant.ORDERCENTER);
        return  rsp;
    }


    /**
     * 将过关数据进行分页
     * @param list
     * @param begin
     * @param end
     * @return
     */
    private static List<GuoGuanBean> toSaveCachePage(List<GuoGuanBean> list, int begin, int end){
        if (list == null || list.size() == 0) {
            logger.info("数据为空");
            return null;
        }

        List<GuoGuanBean> pageList = new ArrayList<GuoGuanBean>(end-begin);
        for (int x = begin; x < end; x++) {
            GuoGuanBean bean = list.get(x);
            pageList.add(bean);
        }
        return pageList;
    }


    /**
     * 将list<GuoGuanBean>生成xml字符串,用户名后台隐藏
     * @param list
     * @param gp
     * @param logoUrl
     * @return
     */
    public static List newToXml(List<GuoGuanBean> list, GetGuoGuanProject gp, String logoUrl) {
        List slist = new ArrayList();
        List tlist = new ArrayList();
        Map<String,String> tmap;
        if (null == list || 0 == list.size()) {
            logger.info("数据为空");
            return null;
        }

        int beginId = 0; // 当前页开始下标
        int endId = 0; // 当前页结束下标
        int total = list.size(); // 总行数
        int tp = total / gp.getPageSize(); // 总页数
        if (total % gp.getPageSize() != 0) {
            tp += 1;
        }

        // 查询的页码不能大于最大页数
        if (gp.getPageNo() > tp) {
            gp.setPageNo(tp);
        }

        // 计算开始和结束下标(页码从1开始)
        beginId = (gp.getPageNo() - 1) * gp.getPageSize();
        // 有且仅有一页，结束下标等于总行数
        if (tp == 1) {
            endId = total;
        } else {
            endId = beginId + gp.getPageSize();
            // 结束下标是否大于总行数
            if (endId > total) {
                endId = total;
            }
        }
        tmap = new TreeMap<>();
        tmap.put("total",list.size() + "");
        tmap.put("ps",gp.getPageSize() + "");
        tmap.put("tp",tp + "");
        tmap.put("logoUrl",logoUrl);
        slist.add(tmap);
        for (int x = beginId; x < endId; x++) {
            tmap = new TreeMap<>();
            GuoGuanBean bean = list.get(x);
            if(null == bean.getCuserid() || "".equals(bean.getCuserid())){
                tmap.put("uid",bean.getNickID());
            } else {
                tmap.put("uid",changeCnickid(bean.getNickID()));
                tmap.put("cuserid",bean.getCuserid() + "");
            }
            tmap.put("ag",bean.getAgNum() + "");
            tmap.put("au", bean.getAuNum() + "");
            tmap.put("info", bean.getNumInfo());
            tmap.put("bonus", bean.getBonus() + "");
            tmap.put("betnum", bean.getBetNum() + "");
            tmap.put("mnums", bean.getMnums() + "");
            tmap.put("bnums", bean.getBnums() + "");
            tmap.put("gnames", bean.getGupguans() + "");
            tmap.put("hid", bean.getProjID() + "");
            tmap.put("rrate", bean.getRrate() + "");
            tmap.put("addtime", bean.getAddDate() + "");
            tmap.put("gid", bean.getGid() + "");
            tlist.add(tmap);
        }
        slist.add(tlist);
        return slist;
    }

    /**
     * 处理用户昵称中连续的数字
     * @param cnickid
     * @return
     */
    public static String changeCnickid(String cnickid) {
        Pattern pattern  = Pattern.compile("\\d{4}");
        Pattern pattern1 = Pattern.compile("\\d{5}\\d*");

        Matcher matcher = pattern1.matcher(cnickid);
        String s = "", s1 = "";
        if(matcher.find()){
            s1 = matcher.group();
            matcher = pattern.matcher(cnickid);
            matcher.find();
            s = matcher.group();
        }
        if(!"".equals(s1) && !"".equals(s)){
            cnickid = cnickid.replace(s1, s+"**");
        }
        return cnickid;
    }

    /**
     * 生成过关键值
     * @param pid
     * @param ggtype
     * @return
     */
    public static String getGuoGuanKey(String gameID, String pid, String ggtype) {
        return gameID + "_" + pid + "_" + ggtype;
    }

    /**
     * 生成过关键值
     * @param pid
     * @param ggtype
     * @return
     */
    public static String[] getNewVsGuoGuanKey(String gameID, String pid, String ggtype) {
        if(!StringUtils.isEmpty(ggtype)){
            if(cachekey1.equals(ggtype)){
                return new String[]{getGuoGuanKey(gameID, pid, cachekey1)};
            }

            if(cachekey3.equals(ggtype)){
                return new String[]{getGuoGuanKey(gameID, pid, cachekey3)};
            }
        }
        return null;
    }

    public static String getwininfo(String gid,String wininfos){
        String wininfostr="";
        int lotid=Integer.valueOf(gid);
        if (lotid==85||lotid==86||lotid==87||lotid==88||lotid==89
                ||lotid==90||lotid==91||lotid==92||lotid==93||lotid==94
                ||lotid==95||lotid==96||lotid==97||lotid==70||lotid==71||lotid==72||lotid==99||lotid==98){
            String [] wininfo = wininfos.split("\\|");
            if (wininfo.length>=3){
                wininfostr="共"+wininfo[1]+"场,"+(lotid==99||lotid==98 ? "单关":wininfo[2].replaceAll("\\*", "串").replaceAll("([,]+)1串1", ",单关").replaceAll("^1串1", "单关"))+", 中"+wininfo[0] + "注";
                //tmp=new String[][]{{""},{"共"+wininfo[1]+"场, 过关方式："+(lotid==99||lotid==98 ? "单关":wininfo[2].replaceAll("\\*", "串").replaceAll("1串1","单场"))+", 中"+wininfo[0]}};
            }
        }else{
            if(!StringUtil.isEmpty(wininfos)){
                String [] wininfo = wininfos.split(",");
                String [] grade =  GuoGuanUtil.getgrade(gid);
                if (wininfo !=null && grade != null && wininfo.length > 0 && wininfo.length <= grade.length) {
                    for ( int i = 0; i < wininfo.length; i++) {
                        if (Integer.valueOf(wininfo[i]) > 0) {
                            wininfostr += "" + grade[i] +" "+ wininfo[i] + "注";
                        }
                    }
                }
            }
        }
        return wininfostr;
    }

    public static String LineDisplay(String path, String codes) {
        try {
            String codestr= FileUtils.readFileToString(new File(path,codes), "UTF-8");
            String[] ccodes=codestr.split(";");
            StringBuilder data=new StringBuilder();
            for(String ccode:ccodes){
                data.append(ccode);
                data.append("\n");
            }
            String[] names=codes.split("\\.");
            if(StringUtil.isEmpty(names[0])){
                return null;
            }
            File codeFile=new File(path,names[0]+"_line.txt");
            if(!codeFile.exists()){
                FileUtils.writeStringToFile(new File(path,names[0]+"_line.txt"), data.toString());
            }
            return names[0]+"_line.txt";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("文件投注生成换行显示的文件错误",e);
        }
        return null;
    }

}
