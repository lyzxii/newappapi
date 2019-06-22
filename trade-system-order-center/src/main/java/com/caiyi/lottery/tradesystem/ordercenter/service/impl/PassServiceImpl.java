package com.caiyi.lottery.tradesystem.ordercenter.service.impl;

import bean.TokenBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.dao.VProjBuyerAppMapper;
import com.caiyi.lottery.tradesystem.ordercenter.dao.VProjMapper;
import com.caiyi.lottery.tradesystem.ordercenter.service.PassService;
import com.caiyi.lottery.tradesystem.ordercenter.utils.GuoGuanUtil;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.LotteryLogoUtil;
import com.caiyi.lottery.tradesystem.util.proj.ProjUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.util.comparable.ComparableUtil;
import constant.UserConstants;
import order.bean.GetGuoGuanProject;
import order.bean.GuoGuanBean;
import order.bean.OrderBean;
import order.constant.OrderConstants;
import order.dto.ProjDTO;
import order.pojo.ProjPojo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 过关实现类
 * @author A-0146
 * @create 2018-1-5 16:55:33
 */
@Service
public class PassServiceImpl implements PassService{

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private VProjMapper vProjMapper;
    @Autowired
    private VProjBuyerAppMapper vProjBuyerAppMapper;

    private Logger logger = LoggerFactory.getLogger(PassServiceImpl.class);

    // 同步hashmap（彩种_期号_查询类型，期次对应的过关统计数据）
    private ConcurrentHashMap<String, List<GuoGuanBean>> maps = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, List<GuoGuanBean>> getMaps() {
        return maps;
    }

    public static java.text.DecimalFormat df =new java.text.DecimalFormat("#0.00");
    public static String df(double v){
        return df.format(v);
    }

    // 竞彩彩种编号
    static Map<String, String> jcIDMaps = new HashMap<>();
    static {// 只显示中奖彩种编号
        // 竞彩
        jcIDMaps.put("70", "70");
        jcIDMaps.put("72", "72");
        jcIDMaps.put("90", "90");
        jcIDMaps.put("91", "91");
        jcIDMaps.put("92", "92");
        jcIDMaps.put("93", "93");
        jcIDMaps.put("900", "900");

        // 北单
        jcIDMaps.put("84", "84");
        jcIDMaps.put("85", "85");
        jcIDMaps.put("86", "86");
        jcIDMaps.put("87", "87");
        jcIDMaps.put("88", "88");
        jcIDMaps.put("89", "89");
        jcIDMaps.put("850", "850");
    }

    static Map<String, String> bdIDMaps = new HashMap<>();
    static {// 北单彩种
        bdIDMaps.put("84", "84");
        bdIDMaps.put("85", "85");
        bdIDMaps.put("86", "86");
        bdIDMaps.put("87", "87");
        bdIDMaps.put("88", "88");
        bdIDMaps.put("89", "89");
        bdIDMaps.put("850", "850");
    }

    @Override
    public BaseResp statPass(OrderBean bean) throws Exception {
        BaseResp rsp = new BaseResp();
        Map map;
        //白名单检测
        boolean iopen = checkWhiteAuth(bean);
//        iopen = true;//-------------------------------测试用，后期删除
        if (iopen) {
            String pid = bean.getPid();
            int pn = bean.getPn() == 0 ? 1 : bean.getPn();
            String logoUrl = devideLogo(bean);
            if (ProjUtils.SZMaps.containsKey(bean.getGid())||ProjUtils.zCMaps.containsKey(bean.getGid())) {
                //慢频数字彩,老足彩
                map = szGuoguan(bean, pid, 1,logoUrl);
            } else {
                if (GuoGuanUtil.bd.containsKey(bean.getGid())) {
                    //北京单场,北单胜负过关
                    map = bdStatData(bean, pid, rsp, logoUrl);
                } else {
                    //竞彩足球、篮球
                    map = loadNewJcGuoguan(bean, pn, logoUrl, pid);
                }
            }
            if (null == map) {
                rsp.setCode(BusiCode.ORDER_NODATA);
                rsp.setData("暂无数据");
                return rsp;
            }
            rsp.setData(map);
            rsp.setCode(BusiCode.SUCCESS);
            rsp.setDesc("查询成功");
        } else {
            //白名单不开放返回空文件
            rsp.setCode(BusiCode.ORDRE_UNAUTHORIZED_ACCESS);
            rsp.setDesc("白名单未放开");
        }
        return rsp;
    }

    /**
     * 区分彩种logo
     * @param bean
     * @return
     * @throws Exception
     */
    private String devideLogo(OrderBean bean) throws Exception {
        String gid;
        if("900".equals(bean.getGid())) {
            gid = "70";
        }else if("940".equals(bean.getGid())){
            gid = "71";
        }else if("850".equals(bean.getGid())){
            gid = "89";
        }else{
            gid = bean.getGid();
        }
        String logoUrl = getLogoUrl(gid);
        return logoUrl;
    }

    /**
     * 数据统计-北京单场,北单胜负过关
     * @param bean
     * @param pid
     * @param rsp
     * @param logoUrl
     */
    private Map bdStatData(OrderBean bean, String pid, BaseResp rsp, String logoUrl) {
        String xmlpath;
        List<Map<String, Object>> list;
        Map map = new TreeMap<>();
        if (StringUtils.isEmpty(pid)) {
            String gid = bean.getGid().length() > 2 ? bean.getGid().substring(0, 2):bean.getGid();//北单850→85
            xmlpath = UserConstants.DATA_DIR + "phot" + File.separator + gid + File.separator + "c.xml";
            JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));
            int count = xml.countXmlNodes("row");
            for(int i = 0; i < count; i++){
                int st = xml.getIntValue("row[" + i + "].@st");
                if (st > 1) {
                    pid = xml.getStringValue("row[" + i + "].@pid");
                    break;
                }
            }
        }
        //新版北单中奖排行榜数据
        list = loadNewBdGuoguan(bean, bean.getGid(), pid, "bjfs", 1);
        if(null == list || 0 == list.size()){
            rsp.setCode(BusiCode.ORDER_NODATA);
            rsp.setData("暂无数据");
            return null;
        }
        map.put("datas",list);
        map.put("logoUrl",logoUrl);
        map.put("pageNumber",1);//当前页
        map.put("pid",pid);
        return map;
    }

    /**
     * 检测白名单
     * @param bean
     */
    private boolean checkWhiteAuth(OrderBean bean) {
        //白名单开关
        boolean iopen = false;
        Object obj = redisGetCacheByTokenBean(bean.getAppid());
        if (null != obj) {
            String ouser = parseObject(obj);
            if (!StringUtils.isEmpty(ouser) && 100 == Integer.parseInt(ouser)) {
                iopen = true;
            }
        }
        return iopen;
    }

    /**
     * 数字彩,任九,胜负彩排行榜数据
     * @param bean
     * @param pid
     * @param i
     * @param logoUrl
     * @return
     */
    private Map<String,Object> szGuoguan(OrderBean bean, String pid, int i, String logoUrl) {
        Map<String,Object> map = new HashMap<>();
        logger.info("查询数字彩,或任九,或胜负彩排行榜数据gid=" + bean.getGid());
        // 如果客户端没有传入期次,从期次文件中读取
        String newPid = pid;
        if (StringUtils.isEmpty(newPid)) {
            newPid = GuoGuanUtil.getPid(bean.getGid());
        }
        // 如果期次id为空,报错
        if (StringUtils.isEmpty(newPid)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ORDER_GETCURRENTISSUE_FAIL));
            bean.setBusiErrDesc("获取当前期次信息失败");
            logger.info("未能取得当前期次pid,gid=" + bean.getGid());
            return null;
        }
        List<Map<String, Object>> list = loadNewSzcGuoguan(bean, newPid, "as", i);
        map.put("datas",list);
        if (list.size() <= 0) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ORDER_GETCURRENTISSUE_FAIL));
            bean.setBusiErrDesc("获取当前期次信息失败");
            logger.info("未能取得当前期中奖排行榜统计数据gid=" + bean.getGid() + ",pid=" + newPid);
            return null;
        }
        map.put("logoUrl",logoUrl);
        map = parseXmlSetData(map, bean, newPid);
        return map;
    }

    /**
     * 解析xml设置数据
     * @param map
     * @param bean
     * @param newPid
     * @return
     */
    private Map<String, Object> parseXmlSetData(Map<String, Object> map, OrderBean bean, String newPid) {
        String xmlpath = UserConstants.DATA_DIR + "guoguan" + File.separator + bean.getGid() + File.separator + newPid;
        File file = new File(xmlpath, newPid + ".xml");
        if (null == file || !file.exists()) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ORDER_GETCURRENTISSUE_FAIL));
            bean.setBusiErrDesc("获取当前期次信息失败");
            logger.info("未能取得当前期中奖统计数据gid=" + bean.getGid() + ",pid=" + newPid);
            return null;
        }
        JXmlWrapper xml = JXmlWrapper.parse(file);
        String rspid = xml.getXmlRoot().getAttributeValue("pid");
        map.put("pid",rspid);
        if ("01".equals(bean.getGid()) && Integer.valueOf(rspid) > 2014143) {
            //2014143期幸运篮球
            String code = xml.getXmlRoot().getAttributeValue("code");
            if (!StringUtils.isEmpty(code) && code.length() > 20) {
                code = code.substring(0, 20);
            }
            map.put("code",code);
        } else {
            map.put("code",xml.getXmlRoot().getAttributeValue("code"));
        }
        map.put("gsale",xml.getXmlRoot().getAttributeValue("gsale"));
        map.put("atime",xml.getXmlRoot().getAttributeValue("atime"));
        map.put("total",1);//TODO pageSize
        map.put("totalPages",1);
        map.put("pageNumber",1);
        // 大乐透
        if ("50".equals(bean.getGid())) {
            dltGuoguan(map, xml);
        } else {
            map.put("ginfo",xml.getXmlRoot().getAttributeValue("ginfo"));
            map.put("ninfo",xml.getXmlRoot().getAttributeValue("ninfo"));
        }
        map.put("pid",newPid);
        // 胜负彩或任九专属中奖排行榜数据
        if ("80".equals(bean.getGid()) || "81".equals(bean.getGid())) {
            r9SfcGuoguan(map, xml);
        }
        return map;
    }

    /**
     * 数字彩,胜负彩,任九中奖排行榜数据.
     */
    public List loadNewSzcGuoguan(OrderBean bean, String expect, String type, int pn){
        List list = new ArrayList<>();
        Map<String,String> map;
        List tlist;
        String lotid = bean.getGid();
        String xmlpath = UserConstants.DATA_DIR + "guoguan" + File.separator + lotid + File.separator + expect;
        File file = new File(xmlpath, type + "_" + pn + ".xml");
        if (file == null || !file.exists()) {
            return list;
        }
        JXmlWrapper xml = JXmlWrapper.parse(file);
        List<JXmlWrapper> rows = xml.getXmlNodeList("row");
        String uid;
        String sinfo;
        String[] info;
        String xid;
        String hid;
        String buyid = null;
        int index = 1;
        for (JXmlWrapper row : rows) {
            tlist = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            map = new TreeMap<>();
            uid = row.getStringValue("@uid");
            sinfo = row.getStringValue("@info");
            double bonus = row.getDoubleValue("@bonus");
            if (bonus == 0 && !"80".equals(lotid) && !"81".equals(lotid)) {
                continue;
            }
            if (index > 20) {
                continue;
            }
            double betmoney = row.getDoubleValue("@betnum");
            hid = row.getStringValue("@hid");

            boolean isdg = false;
            if (uid.indexOf("*****") > 0 || hid.indexOf("DG") > 0) {
                isdg = true;
            }
            xid = String.valueOf((pn * 1 - 1) * 25 + index);
            //排名
            map.put("xid",xid);
            //昵称
            if (isdg){
                map.put("uid",uid);
            } else {
                map.put("uid",GuoGuanUtil.shield(uid));
            }
            map.put("sit",sinfo);
            int jj = 0;
            info = sinfo.split(",");
            if (info.length >= 1 && !StringUtils.isEmpty(info[0])){
                jj = GuoGuanUtil.parsePrize(lotid, expect, info, sb,tlist);
            }
            String prizeStr = null;
            if(!StringUtils.isEmpty(sb.toString())){
                String prize = sb.toString().split("=")[1].trim().replace("\"","");
                map.put("prize",prize);
            }else if(tlist.size() > 0){
                if("80".equals(lotid)){
                    if("0".equals(tlist.get(0)) && "0".equals(tlist.get(1))){
                        prizeStr = "命中"+tlist.get(2)+"场";
                    }else{
                        prizeStr = "一等奖"+tlist.get(0)+"注,二等奖"+tlist.get(1)+"注";
                    }
                }else if("81".equals(lotid)){
                    if("0".equals(tlist.get(0))){
                        prizeStr = "命中"+tlist.get(2)+"场";
                    }else{
                        prizeStr = "一等奖"+tlist.get(0)+"注";
                    }
                }
                map.put("prize",prizeStr);
            }
            int pid = Integer.valueOf(expect);
            if ("50".equals(lotid) && pid <= 2014153 && jj > 0){
                map.put("jj",String.valueOf(jj));
            } else if ("53".equals(lotid) && 2013328 <= pid && pid < 2014230 && jj > 0){
                map.put("jj",String.valueOf(jj));
            }
            map.put("money", df(bonus));
            // 如果返回的方案中包含6个连续星号,表示方案不公开
            boolean isOpen = hid.indexOf("*") < 0;
            // 是否盈利
            boolean isEarn = bonus > betmoney;
            if (isOpen && isdg && isEarn) {
                buyid = getBuyid(bean, hid);
            }
            // 如果代购方案不盈利,不返回方案编号,表示不能查看方案详情
            if (isOpen && (!isdg || isEarn)) {
                map.put("hid",hid);
            }
            // 自购方案需要buyid用于记录删除
            if (isOpen && isdg && isEarn) {
                map.put("buyid",buyid);
            }
            index++;
            list.add(map);
        }
        return list;
    }

    /**
     * 获取购买id
     * @param bean
     * @param projid
     * @return
     */
    private String getBuyid(OrderBean bean, String projid) {
        String buyid="";
        bean.setDid(projid);
        List<ProjDTO> vlist = vProjBuyerAppMapper.queryBuyid(bean.getDid());
        if(null == vlist ||  vlist.size() < 0){
            logger.info("查询购买id暂无数据");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_NODATA));
            bean.setBusiErrDesc("暂无数据");
            return buyid;
        }
        ProjDTO projDTO = vlist.get(0);
        buyid = projDTO.getIbuyid();
        return buyid;
    }

    /**
     * 大乐透专属中奖排行榜数据.
     * @param map
     * @param xml
     */
    private void dltGuoguan(Map<String,Object> map, JXmlWrapper xml) {
        StringBuilder gsb = new StringBuilder();
        StringBuilder nsb = new StringBuilder();
        String[] ginfo = xml.getXmlRoot().getAttributeValue("ginfo").split(",");
        String[] ninfo = xml.getXmlRoot().getAttributeValue("ninfo").split(",");
        judgeRecycle(gsb,ginfo);
        judgeRecycle(nsb,ninfo);
        map.put("ginfo",gsb.toString());
        map.put("ninfo",nsb.toString());
    }

    private void judgeRecycle(StringBuilder sb, String[] info) {
        int length = info.length;
        for (int i = 0; i < length; i++) {
            if (i == 8) {
                continue;
            } else {
                if (i == length - 1) {
                    sb.append(info[i]);
                } else {
                    sb.append(info[i]).append(",");
                }
            }
        }
    }

    /**
     * 胜负彩或任九专属中奖排行榜数据.
     */
    private void r9SfcGuoguan(Map<String,Object> map, JXmlWrapper xml) {
        List clist = new ArrayList<>();
        Map<String,Object> cmap;
        List<JXmlWrapper> rows = xml.getXmlNodeList("row");
        for (JXmlWrapper row : rows) {
            cmap = new TreeMap<>();
            cmap.put("@id",row.getStringValue("@id"));
            cmap.put("@hn",row.getStringValue("@hn"));
            cmap.put("@vn",row.getStringValue("@vn"));
            cmap.put("@hs",row.getStringValue("@hs"));
            cmap.put("@vs",row.getStringValue("@vs"));
            cmap.put("@rs",row.getStringValue("@result"));
            clist.add(cmap);
        }
        map.put("r",clist);
    }

    /**
     * 新版北单中奖排行榜数据,支持点击榜单查看方案详情.
     * @param bean
     * @param lotid
     * @param expect
     * @param type
     * @param pn
     * @return
     */
    public List<Map<String,Object>> loadNewBdGuoguan(OrderBean bean, String lotid, String expect, String type, int pn){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map;
        String xmlpath;
        if ("850".equals(lotid)) {
            //北单全部
            xmlpath  = UserConstants.DATA_DIR + "guoguan" + File.separator + lotid + "/" + expect + "/all" + type + "_" + pn + ".xml";
        } else {
            xmlpath = UserConstants.DATA_DIR + "guoguan" + File.separator + lotid + File.separator + expect + File.separator + type + "_" + pn + ".xml";
        }
        File file = new File(xmlpath);
        if (file == null || !file.exists()) {
            return null;
        }
        JXmlWrapper xml = JXmlWrapper.parse(file);
        List<JXmlWrapper> nodes = xml.getXmlNodeList("row");
        int index = 1;
        StringBuilder rows = new StringBuilder();
        rows.append("<rows>");
        for (JXmlWrapper node : nodes) {
            map = new TreeMap<>();
            if (index > 20) {
                continue;
            }
            map.put("xid",String.valueOf((pn * 1 - 1) * 25 + index));
            String uid = node.getStringValue("@uid");
            double betmoney = node.getDoubleValue("@betnum");
            double bonus = node.getDoubleValue("@bonus");
            String hid = node.getStringValue("@hid");
            String buyid = node.getStringValue("@buyid");
            String gnames = node.getStringValue("@gnames");
            // 如果返回的方案中包含星号,表示方案不公开
            boolean isOpen = hid.indexOf("*") < 0;
            // 是否盈利
            boolean isEarn = bonus > betmoney;
            // 是否代购
            boolean isDg = hid.indexOf("DG") > 0;
            // 自购方案需要buyid用于记录删除,如果缓存中没有buyid,从数据库读取
            if (isOpen && isDg && isEarn && StringUtils.isEmpty(buyid)) {
                buyid = getBuyid(bean, hid);
            }
            if (isDg && !isEarn){
                // 如果代购方案不盈利,隐藏用户名
                uid = uid.substring(0, 1).concat("*****");
            }
            map.put("uid",GuoGuanUtil.shield(uid));
            map.put("betmoney",df(betmoney));
            map.put("money", df(bonus));
            String gstr = splitData(gnames);
            map.put("gg",gstr.replaceAll("([,]+)1串1", ",单关").replaceAll("^1串1", "单关"));
            // 如果代购方案不盈利,不返回方案编号,表示不能查看方案详情
            if (isOpen && (!isDg || isEarn)) {
                map.put("hid",hid);
            }
            // 自购方案需要buyid用于记录删除
            if (isOpen && isDg && isEarn) {
                map.put("buyid",buyid);
            }
            index++;
            list.add(map);
        }
        return list;
    }

    /**
     * 新版竞彩足球/篮球中奖排行榜数据,支持点击榜单查看方案详情.
     * @param bean
     * @param pn
     * @param logoUrl
     * @return
     */
    public Map loadNewJcGuoguan(OrderBean bean, int pn,String logoUrl,String pid){
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar c = java.util.Calendar.getInstance();
        bean.setBid(df.format(c.getTime()));
        c.add(Calendar.DAY_OF_YEAR, -1);
        bean.setPid(df.format(c.getTime()));
        bean.setcType("jcfs");
        bean.setPn(1);
        bean.setPs(25);
        bean.setFsort("bonus");
        bean.setDsort("descending");
        List tlist = newQueryGuoguanList(bean, logoUrl);
        if(null == tlist || null == tlist.get(0) || null == tlist.get(1)){
            return null;
        }
        Map map = (Map) tlist.get(0);
        map.put("pageSize",20);
        map.put("totalPages",1);
        map.put("pageNumber",1);
        //当前页
        java.text.DateFormat df2 = new java.text.SimpleDateFormat("yyMMdd");
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        String etime=df2.format(c2.getTime());//今天
        c2.add(Calendar.DAY_OF_YEAR, -1); // 昨天
        String stime=df2.format(c2.getTime());
        pid=stime+"至"+etime;
        map.put("pid",pid);
        List rlist = new ArrayList();
        List arrayList = (ArrayList) tlist.get(1);
        int index = 1;
        for(int i = 0; i< arrayList.size(); i++) {
            if (index > 20) {
                continue;
            }
            Map treeMap = (TreeMap) arrayList.get(i);
            treeMap.put("xid",String.valueOf((pn * 1 - 1) * 25 + index));
            String uid = String.valueOf(treeMap.get("uid"));
            double bonus = Double.parseDouble(treeMap.get("bonus").toString());
            double betmoney = Double.parseDouble(treeMap.get("betnum").toString());
            String hid = String.valueOf(treeMap.get("hid"));
            String buyid = String.valueOf(treeMap.get("buyid"));
            String gnames = String.valueOf(treeMap.get("gnames"));
            // 如果返回的用户名中包含星号,表示方案不公开
            boolean isOpen = uid.indexOf("*****") < 0;
            // 是否盈利
            boolean isEarn = bonus > betmoney;
            // 是否代购
            boolean isDg = hid.indexOf("DG") > 0;
            // 自购方案需要buyid用于记录删除,如果缓存中没有buyid,从数据库读取
            if (isOpen && isDg && isEarn && StringUtils.isEmpty(buyid)) {
                buyid = getBuyid(bean,hid);
            }
            if (isDg && !isEarn) {
                // 如果代购方案不盈利,隐藏用户名
                uid = uid.substring(0, 1).concat("*****");
            }
            treeMap.put("uid",GuoGuanUtil.shield(uid));
            treeMap.put("betmoney", df(betmoney));
            treeMap.put("money",df(bonus));
            String gstr = splitData(gnames);
            treeMap.put("gg",gstr.replaceAll("([,]+)1串1", ",单关").replaceAll("^1串1", "单关"));
            // 如果代购方案不盈利,不返回方案编号,表示不能查看方案详情
            if (isOpen && (!isDg || isEarn)) {
                treeMap.put("hid",hid);
            }
            // 自购方案需要buyid用于记录删除
            if (isOpen && isDg && isEarn) {
                treeMap.put("buyid", buyid);
            }
            rlist.add(treeMap);
            index++;
        }
            map.put("datas",rlist);
            return map;
        }

    private String splitData(String gnames) {
        String gstr="";
        if(gnames.split(",").length>1){
            gstr=gnames.split(",")[0].replaceAll("\\*","串")+","+gnames.split(",")[1].replaceAll("\\*","串");
            if(gnames.split(",").length>2){gstr=gstr+"..";}
        }else{
            gstr=gnames.replaceAll("\\*","串");
        }
        return  gstr;
    }

    /**
     * 查询过关统计数据,用户名后台隐藏
     * @param bean
     * @param logoUrl
     */
    public List newQueryGuoguanList(OrderBean bean, String logoUrl) {
        List list = null;
        String gameID;
        if(!StringUtils.isEmpty(bean.getGid())){
            gameID = bean.getGid();
        }else{
            logger.info("过关统计数据，传递gameID为空");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_PARAM_NULL));
            bean.setBusiErrDesc("传递参数为空");
            return null;
        }
        long start = System.currentTimeMillis();
        bean.setBusiErrCode(-1);
        bean.setBusiErrDesc("获取失败");
        GetGuoGuanProject gp;
        try {
            gp = new GetGuoGuanProject();
            gp.setPageNo(bean.getPn());
            gp.setPageSize(bean.getPs());
            gp.setNickID(bean.getUid());
            gp.setGid(bean.getGid());
            gp.setPid(bean.getPid());
            gp.setEndpid(bean.getBid());
            gp.setGgtype(bean.getcType());

            if (!StringUtils.isEmpty(bean.getFsort())) {
                gp.setSort(bean.getFsort());
            }
            if (!StringUtils.isEmpty(bean.getDsort())) {
                gp.setSortType(bean.getDsort());
            }
            String[] pidinterval = GuoGuanUtil.getPidsInterval(gp.getPid(), gp.getEndpid());
            if (null != pidinterval && pidinterval.length > 3) {
                bean.setBusiErrCode(3);
                bean.setBusiErrDesc("仅支持同时查看三天以内的数据");
                return null;
            }
            logger.info("竞彩准备查询参数共耗时：" + (System.currentTimeMillis() - start) / 1000 + "s");

            long querytimes = System.currentTimeMillis();

            list = new_select_GuoGuanPage(gameID, gp, pidinterval, logoUrl);
            logger.info("竞彩查询数据共耗时：" + (System.currentTimeMillis() - querytimes) / 1000 + "s");
            long printtimes = System.currentTimeMillis();
            if (null != list && list.size() > 0) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("调用成功");
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("查询无数据");
            }
            logger.info("竞彩返回数据共耗时：" + (System.currentTimeMillis() - printtimes) / 1000 + "s");
        } catch (Exception e) {
            logger.error("newQueryGuoguanList", e);
        }
        return list;
    }

    /**
     * 查询过关统计,用户名后台隐藏
     * @param gameID
     * @param gp
     * @param periods
     * @param logoUrl
     * @return
     */
    public List new_select_GuoGuanPage(String gameID, GetGuoGuanProject gp, String[] periods, String logoUrl) {
        long startq = System.currentTimeMillis();
        if (periods != null && periods.length > 0) {
            List<GuoGuanBean> allList;
            // 直接查询缓存3天的键值数据
            if(3 == periods.length){
                String llkey1 = GuoGuanUtil.getGuoGuanKey(gameID,periods[0] + "_" + periods[2], gp.getGgtype());
                String cachekey = GuoGuanUtil.getGuoGuanPageKey(llkey1, gp.getSort(), gp.getPageNo());
                //取缓存
                Object object = redisGetList(cachekey);
                    if(null != object){
                    long start = System.currentTimeMillis();
                    allList = (List<GuoGuanBean>)object;
                    String totalStr = redisGetStr(cachekey + "_total");// 获取总记录数
                    int cacheTotal;
                    List result = null;
                    if(!StringUtils.isEmpty(totalStr)){
                        cacheTotal = Integer.parseInt(totalStr);
                        result = newCachePageToXml(allList, gp, cacheTotal,logoUrl);
                        logger.info("过关统计，取3天键缓存，key=" + cachekey + ", 结果条数=" + allList.size());
                        logger.info("过关统计，取3天键缓存，共耗时 " + (System.currentTimeMillis()-start)/1000 + "s");
                    }
                    return result;
                }
            }

            allList = new ArrayList<>();
            // 将期次区间内的所有期次数据相加并排序
            for (String period : periods) {
                String[] queryKeys = GuoGuanUtil.getNewVsGuoGuanKey(gameID,period, gp.getGgtype());
                if(null != queryKeys){
                    for(String queryKey : queryKeys){
                        // memcache中查询
                        long time1 = System.currentTimeMillis();
                        int ks = 0;
                        for(int fl = 1; fl < 12; fl++){ // 预估最大合买列表不会超过60000条
                            String cakey = queryKey + "_" + fl;
                            Object object = redisGetList(cakey);
                            if(null != object){
                                List<GuoGuanBean> lb = (List<GuoGuanBean>)object;
                                allList.addAll(lb);
                                ks += lb.size();
                            }else{
                                break;//按顺序取
                            }
                        }

                        if(ks > 0){
                            logger.info("过关统计，取单天键缓存，key=" + queryKey + ", 结果条数=" + ks);
                            logger.info("过关统计，取单天键缓存，共耗时 " + (System.currentTimeMillis()-time1)/1000 + "s");
                        }else{
                            // 内存中查询
                            if (!getMaps().containsKey(queryKey)) {//内存中不存
                                long time2 = System.currentTimeMillis();
                                if (!bdIDMaps.containsKey(gp.getGid())) {
                                    queryPeriodCacheData(gameID, period, Boolean.TRUE);//竞彩
                                } else {
                                    queryBDPeriodCacheData(gameID, period, Boolean.TRUE);//北单
                                }
                                logger.info("过关统计，查询库，期次period=" + period);
                                logger.info("过关统计，查询库，共耗时 " + (System.currentTimeMillis() - time2) / 1000 + "s");
                            }

                            // 查库后新的map中有数据
                            if(getMaps().containsKey(queryKey)){
                                long time3 = System.currentTimeMillis();
                                List<GuoGuanBean> lcb = getMaps().get(queryKey);
                                allList.addAll(lcb);
                                logger.info("过关统计 取内存缓存，key=" + queryKey + ", 结果条数=" + lcb.size());
                                logger.info("过关统计 取内存缓存，共耗时 " + (System.currentTimeMillis()-time3)/1000 + "s");
                            }
                        }
                    }
                }
            }

            String sortSrt = "bonus desc, rrate desc";
            if(!StringUtils.isEmpty(gp.getSort()) && "rate".equalsIgnoreCase(gp.getSort())){
                sortSrt = "rrate desc, bonus desc";
                // 回报率排序（代购不公开方案、回报率小于0的方案不参与排序）
                for(int k=0; k<allList.size(); k++){
                    GuoGuanBean bean = allList.get(k);
                    if(bean.getNickID().indexOf("*****") > 0 || bean.getRrate() == 0){
                        allList.remove(k);// 不满足条件的数据不参与排序
                        --k;
                    }
                }
            }

            ComparableUtil.sort(allList, sortSrt);
            logger.info("过关统计，取所有数据，共耗时 " + (System.currentTimeMillis()-startq)/1000 + "s");
            List list = GuoGuanUtil.newToXml(allList, gp, logoUrl);//--------------------------------------------------------------this
            return list;
        }
        return null;
    }

    /**
     * 北单过关统计查询数据库
     * @param gameID
     * @param period
     * @param flag
     */
    private void queryBDPeriodCacheData(String gameID, String period, Boolean flag) {
        ArrayList<String> array = new ArrayList<>();
        array.add(period);
        List<ProjPojo> jrsgg;
        // 输出日志
        try {
            // 根据期次查询过关数据
            for (String perid : array) {
                String ggSql;
                Object[] obj;
                if (OrderConstants.client_bd_all.equals(gameID)) {
                    // 前端查询条件为“全部”，则查询视图
                    jrsgg = vProjMapper.selectByDiffBdType(perid, OrderConstants.server_bd_all);
                } else {
                    // 前端查询条件为某一个“彩种”，则查询方案表
                    jrsgg = vProjMapper.selectByBdGameid(gameID,perid);
                }
                // 查询过关统计
                if (jrsgg != null && jrsgg.size() > 0) {
                    logger.info("本次任务共更新北单彩种" + gameID + ",期次" + perid + "的过关数据 " + jrsgg.size() + " 条");
                    handleBDPeriodData(gameID,perid, jrsgg, flag);
                }
            }
        } catch (Exception e) {
            // 输出错误日志
            logger.error("查询过关统计异常：" + e.getMessage(),e);
        }

    }

    /**
     * 处理北单期次对应的过关统计数据
     * @param gameID
     * @param pid
     * @param jrsgg
     * @param flag
     */
    private void handleBDPeriodData(String gameID, String pid, List<ProjPojo> jrsgg, Boolean flag) {
        List<GuoGuanBean> lstFinishSuccess = new ArrayList<>();// 已经结束成功方案
        List<GuoGuanBean> lstFinishFailure = new ArrayList<>();// 已经结束流产方案
        for (ProjPojo pojo : jrsgg) {
            GuoGuanBean bean = null;
            try {
                bean = new GuoGuanBean();
                bean.setProjID(pojo.getCprojid());
                bean.setBetNum(pojo.getItmoney()); // 总金额
                bean.setAgNum(pojo.getIagnum()); // 银星个数
                bean.setAuNum(pojo.getIaunum()); // 金星个数
                bean.setAddDate(pojo.getCadddate());
                bean.setGid(pojo.getCgameid()); // 彩种
                bean.setCuserid(pojo.getUserid());

                String nickid = pojo.getCnickid();
                int type = pojo.getItype();    // 方案类型
                int open = pojo.getIopen();    //是否公开

                if (1 == type) {    // 合买
                    bean.setNickID(nickid);
                } else if (0 == type && 4 == open) {    // 代购  公开方案
                    bean.setNickID(nickid);
                } else if (0 == type && 4 != open) {    //代购 不公开方案
                    bean.setNickID(nickid.substring(0, 1) + "*****");
                }

                // 出票标志
                if (3 == pojo.getIcast()) { // 已出票
                    bean.setState(1);
                } else {
                    Integer istate = pojo.getIstate();
                    if (istate <= 2 && istate > 0) {
                        bean.setState(1);
                    } else {// 已撤销
                        bean.setState(0);
                    }
                }

                // 总奖金
                Double sb = pojo.getIbonus();
                if (null != sb) {
                    bean.setBonus(0);
                } else {
                    bean.setBonus(sb);
                }

                // 中奖信息
                String wininfo = pojo.getCwininfo();
                String[] swin = org.apache.commons.lang3.StringUtils.split(wininfo, "|");
                bean.setNumInfo(wininfo);
                if (swin.length >= 3) {
                    try {
                        bean.setBnums(Integer.parseInt(swin[0]));
                        bean.setMnums(Integer.parseInt(swin[1]));
                        bean.setGupguans(swin[2]);
                    } catch (Exception e) {
                        // 写日志(拆分中奖场次信息异常swin)
                    }
                }

                // 回报率（包括代购公开方案、合买方案）
                int roi = 0;
                int ratio = (int) Math.ceil(((bean.getBonus() - bean.getBetNum()) / bean.getBetNum()) * 100);
                if (ratio > 0) {
                    roi = ratio;
                }
                bean.setRrate(roi);

                // 计奖标志
                int award = pojo.getIaward();
                if (bean.getState() == 1) {// 成功
                    if (award == 2) {
                        if (jcIDMaps.containsKey(gameID)) {
                            if(bean.getBnums() > 0){
                                lstFinishSuccess.add(bean);
                            }
                        }else{
                            lstFinishSuccess.add(bean);
                        }
                    }
                } else {
                    if (award == 2) {
                        if (jcIDMaps.containsKey(gameID)) {
                            if(bean.getBnums() > 0){
                                lstFinishFailure.add(bean);
                            }
                        }else{
                            lstFinishFailure.add(bean);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("handleBDPeriodData 北单彩种为" + bean.getGid() + "，方案编号：" + bean.getProjID() + "处理过关查询处理异常");
                logger.error("异常原因：" + e.getMessage());
            }
        }
        // 将过关统计数据写入缓存 key(gid + "_" + pid + "_" + ggtype)
        String key1 = getGuoGuanKey(gameID, pid, OrderConstants.cachekey1);
        String key3 = getGuoGuanKey(gameID, pid, OrderConstants.cachekey3);
        if (flag) { //查库
            logger.info("=======query database(bd period[" + pid + "])");
            getMaps().put(key1, lstFinishSuccess);
            getMaps().put(key3, lstFinishFailure);
        } else { //入缓存
            if (isUpdateCache(key1, lstFinishSuccess)) {
                logger.info("=======memcache lstFinishSuccess(bd period[" + pid + "]): " + lstFinishSuccess.size());
                saveGgBigMemcache(key1, lstFinishSuccess);
            } else {
                logger.info("=======memcache lstFinishSuccess(bd period[" + pid + "]) no change...");
            }

            if (isUpdateCache(key3, lstFinishFailure)) {
                logger.info("=======memcache lstFinishFailure(bd period[" + pid + "]): " + lstFinishFailure.size());
                saveGgBigMemcache(key3, lstFinishFailure);
            } else {
                logger.info("=======memcache lstFinishFailure(bd period[" + pid + "]) no change...");
            }
        }
    }

    /**
     * 过关统计查询数据库
     * @param gameID
     * @param period
     * @param flag
     */
    private void queryPeriodCacheData(String gameID, String period, Boolean flag) {
        ArrayList<String> array = new ArrayList<>();
        array.add(period);
        // 输出日志
        List<ProjPojo> jrsgg;
        try {
            // 根据期次查询过关数据
            List<String> islist = new ArrayList<>();
            for (String perid : array) {
                String ggSql = "";
                Object[] obj = null;
                if (OrderConstants.client_jc_all.equals(gameID) || OrderConstants.client_lc_all.equals(gameID)) {
                    // 前端查询条件为“全部”，则查询视图
                    if (OrderConstants.client_jc_all.equals(gameID)) {
                        jrsgg = vProjMapper.selectByDiffType(perid,OrderConstants.server_jc_all);
                    } else {
                        jrsgg = vProjMapper.selectByDiffType(perid,OrderConstants.server_lc_all);
                    }
                } else {
                    // 前端查询条件为某一个“彩种”，则查询方案表
                    jrsgg = vProjMapper.selectByGameid(gameID, perid);
                }

                // 查询过关统计
                if (jrsgg != null && jrsgg.size() > 0) {
                    logger.info("本次任务共更新彩种" + gameID + ",期次" + perid + "的过关数据 " + jrsgg.size() + " 条");
                    islist.addAll(handlePeriodData(gameID,perid, jrsgg,flag));
                }
            }

            if(islist.size() == 0){
                return;
            }

            // 操作存储期次范围缓存(最近3天)
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            String end = format.format(calendar.getTime());
            calendar.add(Calendar.DATE, -2);
            String start = format.format(calendar.getTime());
            String[] rangePeriods = GuoGuanUtil.getPidsInterval(start, end); //最近3天缓存键

            boolean iskey1Update = false;
            boolean iskey3Update = false;
            for(String rps : rangePeriods){
                String ckey1 = getGuoGuanKey(gameID, rps, OrderConstants.cachekey1);
                String ckey3 = getGuoGuanKey(gameID, rps, OrderConstants.cachekey3);
                if(!iskey1Update && islist.contains(ckey1)){
                    iskey1Update = true;
                }
                if(!iskey3Update && islist.contains(ckey3)){
                    iskey3Update = true;
                }
            }

            if(iskey1Update){
                setRangCacheUpdate(gameID, rangePeriods, OrderConstants.cachekey1);
            }
            if(iskey3Update){
                setRangCacheUpdate(gameID, rangePeriods, OrderConstants.cachekey3);
            }
        } catch (Exception e) {
            // 输出错误日志
            logger.error("查询过关统计异常：" + e.getMessage(), e);
        }
    }

    /**
     * 设置or更新期次范围缓存（3天为键值）
     * @param gameID
     * @param rangePeriods
     * @param key
     */
    private void setRangCacheUpdate(String gameID, String[] rangePeriods, String key) {
        List<GuoGuanBean> allList = new ArrayList<>();
        for(String pid : rangePeriods){
            String queryKey = getGuoGuanKey(gameID, pid, key);
            // memcache中查询
            int ks = 0;
            for(int fl = 1; fl < 12; fl++){ // 预估最大合买列表不会超过60000条
                String cakey = queryKey + "_" + fl;
                Object object = redisGetList(cakey);
                if(null != object){
                    List<GuoGuanBean> lb = (List<GuoGuanBean>)object;
                    allList.addAll(lb);
                    ks += lb.size();
                }else{
                    break;//按顺序取
                }
            }

            if(ks == 0){
                // 内存中查询
                if (!getMaps().containsKey(queryKey)) {
                    //查库 -- readonly只读库
                    queryPeriodCacheData(gameID,pid,Boolean.FALSE);//竞彩//TODO 原为TRUE
                }
                // 查库后新的map中有数据
                if(getMaps().containsKey(queryKey)){
                    allList.addAll(getMaps().get(queryKey));
                }
            }
        }

        if(allList.size() == 0){
            return;
        }

        // 分页存储
        String llkey1 = getGuoGuanKey(gameID,rangePeriods[0] + "_" + rangePeriods[2], key);
        logger.info("=======memcache threekey(" + llkey1 + ") " + gameID + " size：" + allList.size());
        GuoGuanUtil.toCachePage(allList,redisClient,llkey1);
        allList.clear();
    }

    /**
     * 生成过关键值
     *
     * @param gameID
     * @param pid
     * @param ggtype
     * @return
     */
    public String getGuoGuanKey(String gameID, String pid, String ggtype) {
        return gameID + "_" + pid + "_" + ggtype;
    }

    /**
     * 处理期次对应的过关统计数据
     *
     * @param gameID
     * @param pid
     * @param jrsgg
     * @param flag
     * @return
     */
    private List<String> handlePeriodData(String gameID, String pid, List<ProjPojo> jrsgg, boolean flag) {
        List<GuoGuanBean> lstFinishSuccess = new ArrayList<>();// 已经结束成功方案
        List<GuoGuanBean> lstFinishFailure = new ArrayList<>();// 已经结束流产方案
        List<String> uplist = new ArrayList<>();
        for(ProjPojo pojo : jrsgg){
            GuoGuanBean bean = null;
            try{
                bean = new GuoGuanBean();
                bean.setProjID(pojo.getCprojid());
                bean.setBetNum(pojo.getItmoney()); // 总金额
                bean.setAgNum(pojo.getIagnum()); // 银星个数
                bean.setAuNum(pojo.getIaunum()); // 金星个数
                bean.setAddDate(pojo.getCadddate());
                bean.setGid(pojo.getCgameid()); // 彩种
                bean.setCuserid(pojo.getUserid());
                String nickid = pojo.getCnickid();
                int type = pojo.getItype();	// 方案类型
                int open = pojo.getIopen();	//是否公开
                if (1 == type) { 	// 合买
                    bean.setNickID(nickid);
                } else if (0 == type && 4 == open) { // 代购  公开方案
                    bean.setNickID(nickid);
                } else if (0 == type  && 4 != open) { //代购 不公开方案
                    bean.setNickID(nickid.substring(0, 1) + "*****");
                } else if (2 == type || 3 == type) {
                    bean.setNickID(nickid.substring(0, 1) + "*****");
                }

                // 出票标志
                if (3 == pojo.getIcast()) { // 已出票
                    bean.setState(1);
                } else {
                    int istate = pojo.getIstate();
                    if (istate <= 2 && istate > 0) {
                        bean.setState(1);
                    } else {// 已撤销
                        bean.setState(0);
                    }
                }

                // 总奖金
                Double sb = pojo.getIbonus();
                if (null == sb) {
                    bean.setBonus(0);
                } else {
                    bean.setBonus(sb);
                }

                // 中奖信息
                String wininfo = pojo.getCwininfo();
                String[] swin = org.apache.commons.lang.StringUtils.split(wininfo, "|");
                bean.setNumInfo(wininfo);
                if (swin.length >= 3) {
                    try {
                        bean.setBnums(Integer.parseInt(swin[0]));
                        bean.setMnums(Integer.parseInt(swin[1]));
                        bean.setGupguans(swin[2]);
                    } catch (Exception e) {
                        // 写日志(拆分中奖场次信息异常swin)
                    }
                }

                // 回报率（包括代购公开方案、合买方案）
                int roi = 0;
                int ratio = (int)Math.ceil(((bean.getBonus()-bean.getBetNum())/bean.getBetNum())*100);
                if (ratio > 0) {
                    roi = ratio;
                }
                bean.setRrate(roi);

                // 计奖标志
                int award = pojo.getIaward();
                if (bean.getState() == 1) {// 成功
                    if (award == 2) {
                        if (jcIDMaps.containsKey(gameID)) {
                            if(bean.getBnums() > 0){
                                lstFinishSuccess.add(bean);
                            }
                        }else{
                            lstFinishSuccess.add(bean);
                        }
                    }
                } else {
                    if (award == 2) {
                        if (jcIDMaps.containsKey(gameID)) {
                            if(bean.getBnums() > 0){
                                lstFinishFailure.add(bean);
                            }
                        }else{
                            lstFinishFailure.add(bean);
                        }
                    }
                }
            }catch(Exception e){
                logger.error("handlePeriodData 彩种为" + bean.getGid() + "，方案编号：" +bean.getProjID()+ "处理过关查询处理异常,异常原因:{}"+e);
            }
        }
        // 将过关统计数据写入缓存 key(gid + "_" + pid + "_" + ggtype)
        String key1 = getGuoGuanKey(gameID, pid, OrderConstants.cachekey1);
        String key3 = getGuoGuanKey(gameID, pid, OrderConstants.cachekey3);
        if(flag){ //查库
            logger.info("=======query database(period["+ pid +"])");
            getMaps().put(key1, lstFinishSuccess);
            getMaps().put(key3, lstFinishFailure);
        }else{ //入缓存
            if(isUpdateCache(key1, lstFinishSuccess)){
                logger.info("=======memcache lstFinishSuccess(period["+ pid +"]): " + lstFinishSuccess.size());
                //cache.set(key1, lstFinishSuccess, cacheTimes);
                saveGgBigMemcache(key1, lstFinishSuccess);
                uplist.add(key1);
            }else{
                logger.info("=======memcache lstFinishSuccess(period["+ pid +"]) no change...");
            }

            if(isUpdateCache(key3, lstFinishFailure)){
                logger.info("=======memcache lstFinishFailure(period["+ pid +"]): " + lstFinishFailure.size());
                //cache.set(key3, lstFinishFailure, cacheTimes);
                saveGgBigMemcache(key3, lstFinishFailure);
                uplist.add(key3);
            }else{
                logger.info("=======memcache lstFinishFailure(period["+ pid +"]) no change...");
            }
        }

        return uplist;
    }

    /**
     * 过关统计大缓存分页存储
     * @param key
     * @param list
     */
    private void saveGgBigMemcache(String key, List<GuoGuanBean> list) {
        // 合买大缓存
        if (org.apache.commons.lang3.StringUtils.isEmpty(key) || list == null || list.size() == 0) {
            logger.info("过关大缓存写入 数据为空...");
            return;
        }

        if(list != null && list.size() > 0){
            // 由于memcache单个键值最大存取1M，固将其按照5000条为单位分页存储
            int length = list.size();
            // 总页数
            int tp = length / OrderConstants.bigCacheLimit;
            if (length % OrderConstants.bigCacheLimit != 0) {
                tp += 1;
            }

            // 盲清除老的键值
            for(int ln = 0; ln < 12; ln++){
                redisDelete(key + "_" + ln);
            }

            for(int indx = 1; indx <= tp; indx++){
                String cakey = key + "_" + indx;
                int fromIndex = (indx - 1) * OrderConstants.bigCacheLimit;
                int toIndex = fromIndex + OrderConstants.bigCacheLimit;
                // 结束下标是否大于总行数
                if (toIndex > length) {
                    toIndex = length;
                }

                List<GuoGuanBean> calist = new ArrayList<>();
                for(int h = fromIndex; h < toIndex; h++){
                    calist.add(list.get(h));
                }
                logger.info("过关大缓存pageKey：" + cakey);
                redisSetCache(cakey,calist.toString(), Constants.TIME_DAY * 9);
            }
        }
    }


    /**
     * 判断是否需要缓存更新
     * @param cachekey
     * @param lst
     * @return
     */
    private boolean isUpdateCache(String cachekey, List<GuoGuanBean> lst) {
        if(org.apache.commons.lang3.StringUtils.isEmpty(cachekey) && (lst == null || lst.size() < 0)){
            return false;
        }
        int ks = 0;
        for(int fl = 1; fl < 12; fl++){ // 预估最大合买列表不会超过60000条
            String cakey = cachekey + "_" + fl;
            redisDelete(cakey);
            Object object = redisGetList(cakey);
            if(null != object){
                List<GuoGuanBean> lb = (List<GuoGuanBean>)object;
                ks += lb.size();
            }else{
                break;//按顺序取
            }
        }
        if(ks == 0 || ks < lst.size()){
            return true;
        }
        return false;
    }

    /**
     * 存
     * @param key
     * @param value
     * @param time
     */
    private boolean redisSetCache(String key, String value, int time) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        cacheBean.setValue(value);
        cacheBean.setTime(time);
        boolean rsp = redisClient.setString(cacheBean, logger, SysCodeConstant.ORDERCENTER);
        return rsp;
    }


    /**
     * 删
     * @param key
     */
    private boolean redisDelete(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        boolean delete = redisClient.delete(cacheBean, logger, SysCodeConstant.ORDERCENTER);
        return delete;
    }

    /**
     * 取-String
     * @param key
     */
    private String redisGetStr(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        String string = redisClient.getString(cacheBean,logger, SysCodeConstant.ORDERCENTER);
        return string;
    }

    /**
     * 取-Object
     * @param key
     */
    private Object redisGetList(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        Object object = redisClient.getObject(cacheBean, List.class, logger, SysCodeConstant.ORDERCENTER);
        return object;
    }

    /**
     * 将分页存储的缓存数据list<GuoGuanBean>生成xml字符串
     * @param list
     * @param gp
     * @param logoUrl
     * @return
     */
    public List newCachePageToXml(List<GuoGuanBean> list, GetGuoGuanProject gp, int cacheTotal, String logoUrl){
        List slist = new ArrayList();
        Map<String,String> tmap;
        if (null == list || 0 == list.size()) {
            logger.info("数据为空");
            return null;
        }
        int beginId = 0; // 当前页开始下标
        int endId = list.size(); // 当前页结束下标
        int total = cacheTotal; // 总行数
        int tp = total / gp.getPageSize(); // 总页数
        if (total % gp.getPageSize() != 0) {
            tp += 1;
        }
        // 生产返回json数据
        tmap = new TreeMap<>();
        tmap.put("total",total+"");
        tmap.put("ps",gp.getPageSize() + "");
        tmap.put("tp",tp+"");
        tmap.put("logoUrl",logoUrl);
        slist.add(tmap);
        List tlist = new ArrayList();
        for (int x = beginId; x < endId; x++) {
            tmap = new TreeMap<>();
            GuoGuanBean bean = list.get(x);
            if(null == bean.getCuserid() || "".equals(bean.getCuserid())){
                tmap.put("uid",bean.getNickID());
            } else {
                tmap.put("uid",GuoGuanUtil.changeCnickid(bean.getNickID()));
                tmap.put("cuserid",bean.getCuserid() + "");
            }
            tmap.put("ag",bean.getAgNum() + "");
            tmap.put("au",bean.getAuNum() + "");
            tmap.put("info",bean.getNumInfo() + "");
            tmap.put("bonus",bean.getBonus() + "");
            tmap.put("betnum",bean.getBetNum() + "");
            tmap.put("mnums",bean.getMnums() + "");
            tmap.put("bnus",bean.getBnums() + "");
            tmap.put("gnames",bean.getGupguans() + "");
            tmap.put("hid",bean.getProjID() + "");
            tmap.put("rrate",bean.getRrate() + "");
            tmap.put("addtime",bean.getAddDate() + "");
            tmap.put("gid",bean.getGid() + "");
            tlist.add(tmap);
        }
        slist.add(tlist);
        return slist;
    }

    /**
     * 获取彩种logourl
     * @param gid
     * @return
     * @throws Exception
     */
    private String getLogoUrl(String gid) throws Exception {
        if(StringUtils.isEmpty(gid)){
            return null;
        }
        String lotteryLogo = redisClientGetLogoUrl(gid);
        if(StringUtils.isEmpty(lotteryLogo)){
            lotteryLogo = LotteryLogoUtil.getLotteryLogo(gid);
        }
        if(!"null".equals(lotteryLogo) && !StringUtils.isEmpty(lotteryLogo)){
            redisClientSetLogoUrl(gid,lotteryLogo);
        }
        return lotteryLogo;
    }

    /**
     * 取-彩种logo
     * @param gid
     * @return
     */
    private String redisClientGetLogoUrl(String gid) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(UserConstants.LOGOCACHEKEYPRE+gid.trim());
        String logoUrl = redisClient.getString(cacheBean, logger, SysCodeConstant.ORDERCENTER);
        return logoUrl;
    }

    /**
     * 存-彩种logo
     * @param gid
     * @param logoUrl
     */
    private void redisClientSetLogoUrl(String gid,String logoUrl) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(UserConstants.LOGOCACHEKEYPRE+gid.trim());
        cacheBean.setValue(logoUrl);
        cacheBean.setTime(7* Constants.TIME_DAY);
        redisClient.setString(cacheBean,logger, SysCodeConstant.ORDERCENTER);
    }

    /**
     * 取-String
     * @param key
     */
    private Object redisGetCacheByTokenBean(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        Object object = redisClient.getObject(cacheBean, TokenBean.class, logger, SysCodeConstant.ORDERCENTER);
        return object;
    }

    private String parseObject(Object obj) {
        String ouser="";
        TokenBean tokenBean = (TokenBean) obj;
        if (!StringUtils.isEmpty(tokenBean.getParamJson())) {
            JSONObject jsObj = JSON.parseObject(tokenBean.getParamJson());
            Object object = jsObj.get(UserConstants.OPENUSER);
            if (null != object) {
                ouser = object.toString();
            }
        }
        return ouser;
    }
}
