package com.caiyi.lottery.tradesystem.tradecenter.service.impl;


import bean.UserBean;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.FollowlistMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.ProjMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.SharelistMapper;
import com.caiyi.lottery.tradesystem.tradecenter.service.BaseService;
import com.caiyi.lottery.tradesystem.tradecenter.service.CastService;
import com.caiyi.lottery.tradesystem.tradecenter.service.FollowCastService;
import com.caiyi.lottery.tradesystem.tradecenter.util.FollowCastItemCheck;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.constants.TradeJC;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.Util;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import trade.bean.CodeBean;
import trade.bean.TradeBean;
import trade.dto.JcCastDto;
import trade.pojo.ProjPojo;
import trade.pojo.SharelistPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 神单更买投注
 */
@Service
@Slf4j
public class FollowCastServiceImpl implements FollowCastService {

    @Autowired
    ProjMapper projMapper;

    @Autowired
    FollowlistMapper followlistMapper;

    @Autowired
    SharelistMapper sharelistMapper;

    @Autowired
    UserBasicInfoInterface userBasicInfoInterface;

    @Autowired
    CastService castService;

    @Autowired
    BaseService baseService;


    private Map<String, GamePluginAdapter> mapsPlugin = new ConcurrentHashMap<>();


    @Override
    public JcCastDto fgpcast(TradeBean bean) throws Exception {
        if (!baseService.checkBanActivity(bean)) {
            return null;
        }
        return followcast(bean);
    }

    @Override
    public JcCastDto followcast(TradeBean bean) throws Exception {
        log.info("用户名:" + bean.getUid() + " 跟投方案:" + bean.getHid() + " 跟投倍数:" + bean.getMuli() +
                " 跟投金额:" + bean.getMoney() + " 彩种id:" + bean.getGid() + " 投注选项:" + bean.getCodes());
        String gid = bean.getGid(); // 游戏编号
        String hid = bean.getHid(); // 分享单认购编号
        String tzcodes = bean.getCodes(); //优化之后的投注选项
        boolean isyh = "1".equals(bean.getTzyh());
        //分享神单方案信息
        ProjPojo projPojo = projMapper.queryProjectInfo(bean);
        String cguoguan=projPojo.getGuoguan();
        String codes=projPojo.getCodes();
        String appversion=bean.getAppversion();
        //安卓470 471版本不能跟买 需要升级到最新的版本
        if("470".equals(appversion)||"471".equals(appversion)){
            if(1==bean.getMtype()){//安卓
                if(isyh||("1*1,".equals(cguoguan)&&codes.endsWith("txt"))){//投注优化或者为单关优化
                    log.info("用户:{},版本号:{},mtype:{},ccodes:{},ccguoguan:{}",bean.getUid(),bean.getAppversion(),
                    bean.getMtype(),codes,cguoguan);
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("请升级到最新版本!");
                    return null;
                }

            }
        }
        checkGodOrderCast(bean, projPojo); //神单投注前置检查
        if (bean.getBusiErrCode() != 0) {
            return null;
        }
        String pid = projPojo.getPid();
        bean.setPid(projPojo.getPid()); // 期次
        bean.setGuoguan(projPojo.getGuoguan());// 过关
        bean.setZid(projPojo.getMatchs());// 对阵列表(,隔开)
        //String codes = projPojo.getCodes();// 投注内容
        String ifile = projPojo.getIfile();// 是否文件上传
        bean.setFflag(Integer.parseInt(ifile));// 是否文件上传
        JXmlWrapper yczsNewFile = null;
        if ("0".equals(ifile)) {
            bean.setCodes(codes); // 普通投注
        } else { // 奖金优化 一场致胜 文件投注  投注选项优化
            String yhfile = codes.replace("_n.txt", "_yh.xml");
            //找到原始投注文件
            String xmlpath = "/opt/export/data/pupload/" + gid + "/" + pid + "/" + yhfile;
            JXmlWrapper jxml = JXmlWrapper.parse(new File(xmlpath));
            String yhcode = "";
            //神单奖金优化投注,codes直接传
            if (isyh) {
                yhcode = tzcodes;
            } else {
                yhcode = jxml.getStringValue("row.@code");
            }
            StringBuilder followYhcode = getCastCodes(bean, isyh, jxml, yhcode);
            if (followYhcode == null) return null;
            yhcode = followYhcode.toString();
            GamePluginAdapter plugin = mapsPlugin.get(gid);
            if (plugin == null) {
                try {
                    plugin = (GamePluginAdapter) Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.plugin.GamePlugin_" + gid).newInstance();
                    mapsPlugin.putIfAbsent(gid, plugin);
                    log.info("加载游戏插件成功 game=" + gid);
                } catch (Exception e) {
                    log.error("加载游戏插件失败 game=" + gid, e);
                }
            }
            String filename = "";
            String str = "3=3,1=1,0=0"; //自定义选项
            FilterResult result = new FilterResult();
            CodeBean codebean = new CodeBean();
            codebean.setCodeitems(str); //自定义
            codebean.setPlaytype(TradeJC.ds_playid.get(gid));
            codebean.setLottype(Integer.parseInt(gid));

            String[] codd = yhcode.split(";");
            for (int i = 0; i < codd.length; i++) {
                checkCodes(result, codebean, codd[i]);
            }
            //生成优化原单
            yczsNewFile = mkYhFile(bean, projPojo, jxml, yhcode, filename, result);
        }
        if (exeCast(bean, projPojo, yczsNewFile)) {
            return null;
        }
        //插入跟买记录
        bean.setZid(hid); //分享单方案编号
        String fuid = projPojo.getUid(); // 分享人
        bean.setFuid(fuid); //分享人
        saveFollowRecord(bean);
        if (bean.getBusiErrCode() != 0) {
            log.info("插入跟买记录投注失败：" + bean.getBusiErrDesc() + " 用户名:" + bean.getUid() + " 跟投方案:" + bean.getHid());
        }
        JcCastDto dto = new JcCastDto();
        dto.setProjid(bean.getHid());
        dto.setGid(bean.getGid());
        return dto;
    }

    private boolean exeCast(TradeBean bean, ProjPojo projPojo, JXmlWrapper yczsNewFile) {
        UserBean ubean = new UserBean();
        ubean.setUid(bean.getUid());
        ubean.setSource(bean.getSource());
        BaseResp<String> resp = userBasicInfoInterface.queryAgentId(new BaseReq<>(ubean, SysCodeConstant.TRADECENTER));
        //TODO
        if (!StringUtil.isEmpty(resp.getData())) {
            bean.setComeFrom(resp.getData());
        }
        //投注
        bean.setType(3);//跟投方案类型
        bean.setBnum(1);//设置购买份数为1
        bean.setPnum(0);//设置保底份数为0
        bean.setTnum(1);//设置方案总分数
        int iplay = projPojo.getPlay(); //玩法
        bean.setPlay(iplay);//设置玩法
        bean.setOflag(4); //公开标识 跟买自购方案 自购方案为0,开赛后对参与人公开
        bean.setComeFrom("normal");
        bean.setExtendtype(projPojo.getSource());
        bean.setIsfollow(1);
        castService.proj_cast_app(bean);//调用存储过程
        if (bean.getBusiErrCode() != 0) {
            String errdesc = StringUtil.isEmpty(bean.getBusiErrDesc()) ? "投注异常:请查看投注记录确认是否投注成功" : "投注失败:" + bean.getBusiErrDesc();
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.TRADE_GODORDER_CAST_ERROR));
            bean.setBusiErrDesc(errdesc);
            return true;
        }
        if (15 == bean.getExtendtype()) {
            //一场致胜保存yczs文件
            String newpath = "/opt/export/data/guoguan/" + bean.getGid() + "/" + bean.getPid() + "/" + bean.getHid().toLowerCase() + "_yczs.xml";
            boolean flag = Util.SaveFile(yczsNewFile.toXmlString(), newpath, "utf-8");
            if (!flag) {
                log.error(newpath + "：存储失败  用户名:" + bean.getUid() + " 跟投方案:" + bean.getHid() + " 文件名:" + bean.getHid().toLowerCase() + "_yczs.xml");
            }
        }
        return false;
    }

    private JXmlWrapper mkYhFile(TradeBean bean, ProjPojo projPojo, JXmlWrapper jxml, String yhcode, String filename, FilterResult result) throws Exception {
        String gid = bean.getGid();
        String hid = bean.getHid();
        String pid = bean.getPid();
        long time = System.currentTimeMillis();
        String name = bean.getUid() + gid + time + pid;

        filename = MD5Util.compute(name);
        File dir = new File(FileConstant.BASE_PATH + File.separator + gid + File.separator + pid);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, gid + "_" + filename + "_n.txt");
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(result.getAllCodeToFile().getBytes());
        fout.close();
        StringBuilder yhxml = new StringBuilder();
        JXmlWrapper yczsNewFile = null;

        yhxml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        yhxml.append("<xml>");
        yhxml.append("<row ").append(JXmlUtil.createAttrXml("code", yhcode));
        yhxml.append(" ").append(JXmlUtil.createAttrXml("matchs", jxml.getStringValue("row.@matchs")));
        yhxml.append(" ").append(JXmlUtil.createAttrXml("yhfs", jxml.getStringValue("row.@yhfs")));
        yhxml.append(" />");
        yhxml.append("</xml>");
        boolean flag = Util.SaveFile(yhxml.toString(), FileConstant.BASE_PATH + File.separator + gid + File.separator + pid, gid + "_" + filename + "_yh.xml", "utf-8");
        if (!flag) {
            log.error(filename + "_yh.xml" + "：存储失败 用户名:" + bean.getUid() + " 文件名:" + gid + "_" + filename + "_yh.xml" + " 跟投方案:" + bean.getHid());
        }

        bean.setCodes(gid + "_" + filename + "_n.txt"); // 投注号码（文件投注的文件名）
        bean.setMuli(1); // 投注倍数
        bean.setFflag(1); // 文件标志（0 是号码 1 是文件）
        //一场致胜生成文件
        if (15 == projPojo.getSource()) {
            String yczsPath = "/opt/export/data/guoguan/" + gid + "/" + pid + "/" + hid.toLowerCase() + "_yczs.xml";
            File yczsFile = new File(yczsPath);
            yczsNewFile = JXmlWrapper.parse(yczsFile);
        }
        return yczsNewFile;
    }

    private void checkCodes(FilterResult result, CodeBean codebean, String code) throws Exception {
        codebean.setItemType(CodeBean.HAVEITEM);
        codebean.setCode(code);
        codebean.setGuoguan("");
        String[] codestring = code.split("_");
        int bs = 1; //单式解析倍数
        int len = codestring.length;
        if (len == 2) {
            if (StringUtil.getNullInt(codestring[1].trim()) > 0) {
                bs = Integer.parseInt(codestring[1].trim());
            } else {
                throw new Exception("投注格式中倍数异常,code=" + code);
            }
        } else {
            throw new Exception("投注格式异常,code=" + code);
        }
        FilterBase.doFilterJc(codebean, result);
        if (result.getCurrentCode().contains("=")) {
            for (int n = 1; n < bs; n++) {
                result.addCode(result.getCurrentCode());
            }
        }
    }

    private StringBuilder getCastCodes(TradeBean bean, boolean isyh, JXmlWrapper jxml, String yhcode) throws Exception {
        StringBuilder followYhcode = new StringBuilder();
        String[] yhcodes = yhcode.split(";");
        List<String> tzList = new ArrayList<String>();//原始投注选项
        List<String> matchList = new ArrayList<String>();//优化之后的投注选项
        String[] originCodes = jxml.getStringValue("row.@code").split(";");
        //截取原始投注选项 注意玩法可能有多种 HH RQSPF SPF CBF
        for (int t = 0; t < originCodes.length && isyh; t++) {
            String code = FollowCastItemCheck.sort(originCodes[t]);
            tzList.add(code);
        }
        for (int i = 0; i < yhcodes.length; i++) {
            String[] codeContent = yhcodes[i].split("_");
            int bs = 1; // 单式解析倍数 grh
            int len = codeContent.length;
            if (len == 2) {
                if (StringUtil.getNullInt(codeContent[1].trim()) > 0) {
                    bs = Integer.parseInt(codeContent[1].trim());
                } else {
                    throw new Exception("投注格式中倍数异常,code=" + yhcodes[i]);
                }
            } else {
                throw new Exception("投注格式异常,code=" + yhcodes[i]);
            }
            followYhcode.append(codeContent[0]).append("_").append(bs * bean.getMuli());
            if (i != yhcodes.length - 1) {
                followYhcode.append(";");
            }
            //获得优化过后的投注选项
            if (isyh) {
                String ycode = FollowCastItemCheck.sort(yhcodes[i]);
                matchList.add(ycode);
            }
        }
        //对优化之后的投注选项进行校验
        if (isyh) {
            log.info("对优化之后的投注选项进行校验,优化之后的code:{}", yhcode);
            if (!FollowCastItemCheck.doCheck(tzList, matchList)) {
                bean.setBusiErrCode(Integer.valueOf(ErrorCode.TRADE_CAST_ITEM_MATCH_ERROR));
                bean.setBusiErrDesc("投注选项不正确");
                log.info("原始投注选项--->" + tzList.toString());
                log.info("匹配投注选项--->" + matchList.toString());
                return null;
            }
        }
        return followYhcode;
    }


    //神单投注前置检查
    private void checkGodOrderCast(TradeBean bean, ProjPojo projPojo) throws ParseException {
        if (projPojo == null) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.TRADE_QUERYPROJ_ERROR));
            bean.setBusiErrDesc("查询分享方案信息出错");
            return;
        }
        if (projPojo.getUid().equals(bean.getUid())) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_FOLLOWBUY_SELFORDER));
            bean.setBusiErrDesc("当前方案是你发起的，不能进行跟买哦~");
            log.info("该方案是发起人发起的,发起人不能进行跟买,方案编号:" + bean.getHid() + " 发起人名称:" + projPojo.getUid() + " 跟买人名称:" + bean.getUid());
            return;
        }
        int cast = projPojo.getCast();
        if (cast < 3) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_ORDER_NO_CAST));
            bean.setBusiErrDesc("发单人的方案正在出票，请稍候进行跟买~");
            log.info("该方案是还未出票,不能进行跟买,方案编号:" + bean.getHid() + " 发起人:" + projPojo.getUid() + " 跟买人名称:" + bean.getUid());
            return;
        }
        checkFollowLimit(bean);
        if (bean.getBusiErrCode() != 0) {
            if (!StringUtil.isEmpty(bean.getAccesstoken())) {// 客户端跟投
                if (bean.getBusiErrCode() == 1) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_MONEY_OUT_OF_LIMIT));
                    bean.setBusiErrDesc("该神单的跟投已达上限不能跟投");
                    log.info("该神单的跟投金额已达上限,不能进行跟投,神单号:" + bean.getHid());
                    return;
                }
            }
            return;
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        // 判断截止时间
        String endtime = projPojo.getEndtime(); // 方案截止时间
        /* 跟投截止时间提前5分钟 */
        Date followEnddate = new Date(df.parse(endtime).getTime() - 5 * 60 * 1000);
        c1.setTime(df.parse(DateUtil.getCurrentDateTime()));
        c2.setTime(followEnddate);

        int rst = c1.compareTo(c2);
        // 判断是否查过截止时间
        if (rst > 0) { // 截止时间小于系统时间
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_GODORDER_OUT_OF_ENDTIME));
            bean.setBusiErrDesc("方案已超出截止投注时间，不能进行跟买了哦~");
            log.info("方案已超出截止投注时间,方案编号:" + bean.getHid() + " 用户名:" + bean.getUid() + " 截止时间:" + endtime + " 跟投截止时间:"
                    + followEnddate.toString());
            return;
        }
        checkRepeatFollow(bean);
        if (bean.getBusiErrCode() != 0) {
            if (bean.getBusiErrCode() == 3) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_FOLLOWBUY_AGAIN_IN_ONE_MIN));
                log.info("一分钟内重复下单,用户名:" + bean.getUid() + " 跟单单号:" + bean.getHid());
                return;
            }
            log.info("检测是否一分钟内重复下单失败,用户名:" + bean.getUid() + " 跟单单号:" + bean.getHid());
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.TRADE_CHECK_FOLLOWBUY_ONEMIN_ERROR));
            bean.setBusiErrDesc("同一方案订单，一分钟内不能重复跟单哦~");
        }

    }

    // 检查跟投神单的限制
    private void checkFollowLimit(TradeBean bean) {
        try {
            SharelistPojo sharelistPojo = sharelistMapper.queryShareProjStatus(bean);
            if (sharelistPojo != null) {
                int tmoney = sharelistPojo.getTmoney();
                int followMoney = sharelistPojo.getFollowmoney();
                if (tmoney * 10 <= followMoney) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("跟投金额已经超过神单上线金额");
                    log.info("跟投金额已经超过神单上线金额,神单上线金额:" + tmoney + " 跟投总金额:" + followMoney + " 神单编号:" + bean.getHid());
                }
            } else {
                bean.setBusiErrCode(2);
                bean.setBusiErrDesc("未查询到该神单信息");
                log.info("跟投金额已经超过神单上线金额,神单编号:" + bean.getHid());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("检查神单跟投金额异常");
            log.error("检查神单跟投金额错误 用户名:" + bean.getUid() + "方案编号:" + bean.getHid(), e);
        }
    }

    // 检测是否重复跟单
    private void checkRepeatFollow(TradeBean bean) {
        try {
            List<String> cadddateList = followlistMapper.queryLatestFollowTime(bean);
            if (cadddateList != null && cadddateList.size() > 0) {
                String cadddate = cadddateList.get(0);
                log.info("检测是否重复跟单,最近一次下单时间:{}", cadddate);
                Date addDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cadddate);
                Date now = new Date();
                double minute = (now.getTime() - addDate.getTime()) / 60000;
                log.info("检测是否重复跟单,跟单单号:" + bean.getHid() + " 间隔时间:" + minute + " 上一次的添加时间:" + addDate + " 当前时间"
                        + now + " 用户名:" + bean.getUid());
                if (minute <= 1) {
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("同一方案订单，一分钟内不能重复跟单哦~");
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("检测是否重复跟单出错");
            log.info("检测是否重复跟单出错,分享单号:" + bean.getHid() + " 用户名:" + bean.getUid() + " 错误信息:" + e);
        }
    }

    private void saveFollowRecord(TradeBean bean){
        try {
            // 查询分享列表中的打赏比例
            int wrate = sharelistMapper.queryShareWrate(bean);
            bean.setWrate(wrate);
            // 插入跟投表数据
            int rs = followlistMapper.insertFollowRecord(bean);
            if (rs == 1) {// 插入跟投表数据成功
                log.info("插入分享神单活动-跟买投注插入跟买信息成功！用户名:" + bean.getUid() + "分享方案编号:" + bean.getZid() + " 跟投编号:"
                        + bean.getHid());
                rs = sharelistMapper.updateShareFollowData(bean);
                if (rs == 1) {// 更新分享表跟投份数成功
                    log.info("更新分享列表的跟投份数成功,分享方案编号:" + bean.getZid() + " 用户名:" + bean.getUid());
                    // 更新分享人数
                    int count = followlistMapper.queryExist(bean);
                    if (count <= 1) {// 该用户还未跟投过,更新跟投人数,1是刚刚插入的数据
                        rs = sharelistMapper.updateFollowUserNum(bean);
                        if (rs != 1) {// 更新失败
                            log.info("更新分享列表的跟投人数失败,分享方案编号:" + bean.getZid() + " 用户名:" + bean.getUid());
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
                        }
                    }
                } else {
                    log.info("更新分享列表的跟投份数失败,分享方案编号:" + bean.getZid() + " 用户名:" + bean.getUid());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
                }
            } else {
                log.error("插入分享神单活动-跟买投注插入跟买信息失败！ uid:" + bean.getUid() + " cprojid:" + bean.getHid() + " sprojid:"
                        + bean.getZid());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();;//手动回滚
            }
        } catch (Exception e) {
            log.error("保存更买记录异常，uid:" + bean.getUid() + " cprojid:" + bean.getHid() + " sprojid:"
                    + bean.getZid());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
        }
    }
}
