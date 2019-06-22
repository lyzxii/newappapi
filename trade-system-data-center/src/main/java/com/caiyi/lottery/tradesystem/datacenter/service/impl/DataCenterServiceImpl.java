package com.caiyi.lottery.tradesystem.datacenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.datacenter.dao.MatchFollowMapper;
import com.caiyi.lottery.tradesystem.datacenter.service.DataCenterService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.HttpClientUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.proj.LiveBfUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import data.bean.DataBean;
import data.constant.DataConstants;
import data.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.UserPojo;

import java.util.*;

/**
 * @author wxy
 * @create 2018-01-16 17:08
 **/
@Slf4j
@Service
public class DataCenterServiceImpl implements DataCenterService {
    public static final String ONLINE_VIEWER = "onlineviewer";
    @Autowired
    private UserBasicInfoWrapper userBasicInfoWrapper;

    @Autowired
    private MatchFollowMapper matchFollowMapper;

    @Autowired
    private RedisClient redisClient;

    @Override
    public String queryIntelligenceDetails(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("userid", bean.getUserId());
        paramMap.put("matchId", bean.getMatchId());
        paramMap.put("intelligenceId", bean.getIntelligenceId());
        paramMap.put("commentId", bean.getCommentId());
        paramMap.put("mtype", bean.getMtype()+"");
        Long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("intelligenceDetails"), paramMap);
        Long end = System.currentTimeMillis();
        log.info("请求情报详情信息,[响应时间：{},情报id:{},场次id:{},返回结果:{}]", end-start, bean.getIntelligenceId(), bean.getMatchId(), result);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "没有查到相关的情报详情");
        }else{
            return result.replace("msg", "desc");
        }
    }

    /**
     * 发表对情报详情的评论
     * @param bean
     * @return
     */
    @Override
    public String commentToIntelligence(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("userId", bean.getUserId());
        paramMap.put("content", bean.getContent());
        paramMap.put("intelligenceId", bean.getIntelligenceId());
        paramMap.put("ip", bean.getIpAddr());
        paramMap.put("mtype", bean.getMtype()+"");
        if(1==bean.getMtype()){
            paramMap.put("idfa", bean.getImei());
        }else{
            paramMap.put("idfa", bean.getIdfa());
        }
        paramMap.put("source", bean.getSource()+"");
        paramMap.put("appversion", bean.getAppversion());
        Long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("intelligenceComment"), paramMap);
        Long end = System.currentTimeMillis();
        log.info("请求情报详情信息,[响应时间：{}ms,情报id:{},场次id:{},返回结果:{}]", end-start, bean.getIntelligenceId(), bean.getMatchId(), result);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "没有查到相关的情报详情");
        }else{
            return result.replace("msg", "desc");
        }
    }

    //举报评论
    @Override
    public String reportComment(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("userId", bean.getUserId());
        paramMap.put("commentId", bean.getCommentId());
        paramMap.put("commentType", bean.getCommentType());
        paramMap.put("reportType", bean.getReportType());
        long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("reportComment"), paramMap);
        long end = System.currentTimeMillis();

        log.info("举报评论用时{}ms", end - start);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "举报失败");
        }else{
            return result.replace("msg", "desc");
        }
    }

    //对球评就行评论
    @Override
    public String replyToComment(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("userId", bean.getUserId());
        paramMap.put("comment", bean.getContent());
        paramMap.put("followUserId", bean.getFollowUserId());
        paramMap.put("matchId", bean.getMatchId());
        paramMap.put("ip", bean.getIpAddr());
        paramMap.put("mtype", bean.getMtype()+"");
        if(1==bean.getMtype()){
            paramMap.put("idfa", bean.getImei());
        }else{
            paramMap.put("idfa", bean.getIdfa());
        }
        paramMap.put("source", bean.getSource()+"");
        paramMap.put("appversion", bean.getAppversion());
        long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("replyComment"), paramMap);
        long end = System.currentTimeMillis();
        log.info("对球评进行评论用时{}ms", end - start);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "评论失败");
        }else{
            return result.replace("msg", "desc");
        }
    }

    //点赞评论
    @Override
    public String praiseToComment(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("userId", bean.getUserId());
        paramMap.put("commentId", bean.getCommentId());
        paramMap.put("commentType", bean.getCommentType());
        String result = "";
        long start = System.currentTimeMillis();
        if("1".equals(bean.getCommentType())){
            result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("praiseUser"), paramMap);
        }else{
            result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("praiseComment"), paramMap);
        }
        long end = System.currentTimeMillis();
        log.info("点赞评论用时{}ms", end - start);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "点赞失败");
        }else{
            return result.replace("msg", "desc");
        }
    }

    //查询评论详细信息
    @Override
    public String queryCommentDetails(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("queryUserId", bean.getUserId());
        paramMap.put("userId", bean.getCuserId());
        paramMap.put("commentId", bean.getCommentId());
        paramMap.put("matchId", bean.getMatchId());
        long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("commentDetails"), paramMap);
        long end = System.currentTimeMillis();
        log.info("查询评论详情用时{}ms", end - start);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "查询评论详情失败");
        }else{
            result = result.replaceAll("http://mobile\\.9188\\.com", "").replaceAll("http://t2015\\.9188\\.com", "");
            result = result.replace("msg", "desc");
            return result;
        }
    }

    //查询评论点赞用户
    @Override
    public String queryUserPraise(DataBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", bean.getUid());
        paramMap.put("queryUserId", bean.getUserId());
        paramMap.put("userId", bean.getCuserId());
        paramMap.put("matchId", bean.getMatchId());
        long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_Map(LiveBfUtil.getPostUrl("queryPraiseUser"), paramMap);
        long end = System.currentTimeMillis();
        log.info("查询球评点赞用户用时{}ms", end - start);
        if(StringUtil.isEmpty(result)){
            return errorResult("-1", "查询球评点赞用户失败");
        }else{
            result = result.replaceAll("http://mobile\\.9188\\.com", "").replaceAll("http://t2015\\.9188\\.com", "");
            result = result.replace("msg", "desc");
            return result;
        }
    }

    /**
     * 评论、回复
     * @param bean
     * @return
     */
    @Override
    public String replay(DataBean bean) throws Exception {
        String content = bean.getContent();//球评内容
        String option_spf = bean.getOptionSPF();//胜平负预测结果，多个预测用逗号分隔
        String option_rqspf = bean.getOptionRQSPF();//胜平负预测结果，多个预测用逗号分隔
        String lotterytype = bean.getLotteryType();//彩种，区分篮球足球球评，0，北单，1竞彩，2篮球
        String rid = bean.getMatchId();//比赛id
        String userid = bean.getUserId();//用户id
        String nickname= bean.getUid();//用户名
        String ip = bean.getIpAddr();//用户设备ip
        String rq = bean.getRqNum();//让球数
        String rversion = bean.getAppversion();//移动端版本号
        String source = bean.getSource() + "";//source值
        String mtype = bean.getMtype() + "";//移动端类型 1 android，2 IOS
        String idfa = bean.getIdfa();//用户设备关联串
        if (StringUtils.isNotEmpty(mtype)) {
            if (mtype.equals("1")) {
                idfa = bean.getImei();//用户设备关联串
            }
        }

        // 如果球评内容和预测选项都为空,提示不能为空
        if (StringUtil.isEmpty(option_rqspf)&&StringUtil.isEmpty(option_spf) && StringUtil.isEmpty(content)) {
            return errorResult("001", "发表内容为空");
        }

        if ((!StringUtil.isEmpty(option_spf)
                     && (!(option_spf.contains("0") || option_spf.contains("1") || option_spf.contains("3"))))
                    ||(!StringUtil.isEmpty(option_rqspf)
                               && (!(option_rqspf.contains("0") || option_rqspf.contains("1") || option_rqspf.contains("3"))))) {
            return errorResult("006", "非法预测选项");
        }

        int res = checkUserBeforePostComment(bean, rid);
        if (res != 0) {
            return bean.getBusiXml();
        }

        Map<String, String> parammap = new HashMap<String, String>();
        content = StringUtil.isEmpty(content) ? "" : content;
        parammap.put("option_spf", StringUtil.isEmpty(option_spf)?"":option_spf);
        parammap.put("option_rqspf", StringUtil.isEmpty(option_rqspf)?"":option_rqspf);
        parammap.put("content", content); // 评论或回复内容
        parammap.put("userid", StringUtil.isEmpty(userid)?"":userid); // 用户名
        parammap.put("rid", StringUtil.isEmpty(rid)?"":rid); // 比赛id
        parammap.put("lotterytype",  StringUtil.isEmpty(lotterytype)?"":lotterytype); // 区分篮球足球
        parammap.put("nickname",  StringUtil.isEmpty(nickname)?"":nickname); // 用户名
        parammap.put("rq", StringUtil.isEmpty(rq)?"":rq); //盘口

        parammap.put("idfa", StringUtil.isEmpty(idfa)?"":idfa); //设备号
        parammap.put("ip", StringUtil.isEmpty(ip)?"":ip); //ip
        parammap.put("rversion", StringUtil.isEmpty(rversion)?"":rversion); //版本号
        parammap.put("source", StringUtil.isEmpty(source)?"":source); //source值
        parammap.put("mtype", StringUtil.isEmpty(mtype)?"":mtype); //source值

        long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_String(LiveBfUtil.getPostUrl("newcommentreply"), JSON.toJSONString(parammap));
        long end = System.currentTimeMillis();
        log.info("评论、回复用时{}ms", end - start);
        if(StringUtil.isEmpty(result)){
            return errorResult("006", "发送失败~请重试~");
        }else{
            return result.replace("msg", "desc");
        }
    }

    /**
     * 球评内容检测.
     */
    private int checkUserBeforePostComment(DataBean bean, String rid) {
        int status = 0;
        checkUserBind(bean);
        if (bean.getBusiErrCode() == 0) {
            status = 0;
        } else {
            status = bean.getBusiErrCode();
        }
        switch (status) {
            case 2: {
                bean.setBusiXml(errorResult("009", "请先实名认证~"));
                break;
            }
            case 3: {
                bean.setBusiXml(errorResult("007", "请先绑定手机号~"));
                break;
            }
            case 5: {
                bean.setBusiXml(errorResult("011","请先绑定手机号并实名认证~"));
                break;
            }
            default: {
                bean.setBusiXml(errorResult("006", "发送失败啦~请重发~"));
            }
        }
        return status;
    }

    /**
     * 检测用户手机号绑定和实名信息.
     * @param bean
     */
    private void checkUserBind(DataBean bean) {
        UserPojo user = userBasicInfoWrapper.queryUserInfo(bean, log, SysCodeConstant.DATACENTER);
        int bindflag = 0;
        if (user != null ) {
            String mobileno = user.getMobileNo();
            String idcard = user.getIdcard();
            int mobbind = user.getMobileBind();
            if (StringUtil.isEmpty(mobileno) || mobbind != 1) {
                bindflag += 3;
            }
            if (StringUtil.isEmpty(idcard)) {
                bindflag += 2;
            }
        } else {
            bindflag = 5;
        }
        bean.setBusiErrCode(bindflag);
        switch (bindflag) {
            case 2 : {
                bean.setBusiErrDesc("身份证未绑定");
                bean.setBusiErrCode(2);
                log.info("未实名认证uid=" + bean.getUid());
                break;
            }
            case 3 : {
                bean.setBusiErrDesc("手机号未绑定");
                bean.setBusiErrCode(3);
                log.info("未绑定手机号uid=" + bean.getUid());
                break;
            }
            case 5 : {
                bean.setBusiErrDesc("手机号和身份证未绑定");
                bean.setBusiErrCode(5);
                log.info("未绑定手机号和实名认证uid=" + bean.getUid());
                break;
            }
            default : {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("已绑定");
            }
        }
    }

    /**
     * 球评列表
     * @param bean
     * @return
     */
    @Override
    public String queryComments(DataBean bean) throws Exception {
        String commentStr = newQueryFromZlk(bean);
        if (StringUtil.isEmpty(commentStr)) {
            log.info("从资料库查询球评数据为空 资料库id为{}", bean.getMatchId());
            return errorResult("006", "查询失败");
        } else {
            commentStr = commentStr.replaceAll("http://mobile\\.9188\\.com", "").replaceAll("http://t2015\\.9188\\.com", "");
            JSONObject commentObj = JSONObject.parseObject(commentStr);
            if (!commentObj.containsKey("code") && "006".equals(commentObj.getString("code"))) {
                return errorResult("006", "查询失败");
            }else{
                return commentStr;
            }
        }
    }

    private String  newQueryFromZlk(DataBean bean) throws Exception {
        Map<String, String> parammap = new HashMap<String, String>();
        if (StringUtils.isEmpty(bean.getMatchId())
                    ||StringUtils.isEmpty(bean.getOptionType())
                    ||StringUtils.isEmpty(bean.getLotteryType())){
            String res = errorResult("006","查询失败");
            return res;
        }
        parammap.put("rid", StringUtils.isEmpty(bean.getMatchId())?"":bean.getMatchId());
        parammap.put("username", StringUtils.isEmpty(bean.getUid())?"":bean.getUid());
        parammap.put("opType", StringUtils.isEmpty(bean.getOptionType())?"":bean.getOptionType()); // 操作类型 (初始化 1，分页 2)
        parammap.put("cmtUserId", StringUtils.isEmpty(bean.getCmtUserId())?"":bean.getCmtUserId()); // 分页最后一条评论的用户id
        //    parammap.put("ip", request.getParameter("ip")); // ip
        String idfa = bean.getIdfa();//用户设备关联串
        String mtype = bean.getMtype() + "";//移动端类型 1 android，2 IOS
        if (StringUtils.isNotEmpty(mtype)) {
            if (mtype.equals("1")) {
                idfa = bean.getImei();//用户设备关联串
            }
        }

        parammap.put("idfa", StringUtils.isEmpty(idfa)?"":idfa); // 设备号
        parammap.put("mtype", StringUtils.isEmpty(mtype)?"":mtype);
        parammap.put("lotterytype", StringUtils.isEmpty(bean.getLotteryType())?"":bean.getLotteryType()); // 彩种 0北单，1竞彩，2篮彩
        Long start = System.currentTimeMillis();
        String result = HttpClientUtil.callHttpPost_String(LiveBfUtil.getPostUrl("newcommentquery"), JSON.toJSONString(parammap));
        Long end = System.currentTimeMillis();
        log.info("获取球评列表用时{}ms,结果{}",end - start, result);
        return result.replace("msg", "desc");
    }

    /**
     * 错误结果
     * @param code
     * @param msg
     * @return
     */
    private String errorResult(String code, String msg){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("desc", msg);
        return json.toString();
    }

    @Override
    public BaseResp getFootballTitleData(DataBean bean) throws Exception {
        BaseResp rsp = new BaseResp<>();
        Map map = new TreeMap();
        String gtype = bean.getGameType()+"";
        String roundItemId = bean.getRoundItemId();
        String qc = bean.getPeriod();
        String sort = bean.getSort();
        String appversion = bean.getAppversion();
        String kind = "70".equals(gtype)?"jc":("85".equals(gtype)?"bd":"");
        String name = kind.equals("jc") ? roundItemId : (kind.equals("bd") ? (qc + "_" + sort) : "");
        String url = DataConstants.QTJSBF_DATA_PATH + kind + DataConstants.ONEROUNDDATA_PATH + name + ".xml";
        if (appversion!=null&&!StringUtils.isEmpty(appversion)) {
            String reversion = appversion.replace(".", "");
            Integer ver = Integer.valueOf(reversion);
            //新版本添加了取消状态
            if (ver > 460) {
                url = DataConstants.QTJSBF_DATA_PATH + kind + DataConstants.ONEROUNDTITLEDATA_PATH + name + ".xml";
            }
        }
        JXmlWrapper xml = JXmlWrapper.parseUrl(url, "", DataConstants.UTF8, 10);
        //是否已关注比赛
        bean.setFollow(false);
        if (bean.getCheckLogin()) {
            isFollowMatch(bean);
        }
        Element row = xml.getXmlRoot();
        setFootData(bean,row,map,kind);
        rsp.setData(map);
        rsp.setCode(BusiCode.SUCCESS);
        rsp.setDesc("查询成功");
        return rsp;
    }

    /**
     * 组装足球数据
     * @param bean
     * @param row
     * @param map
     * @param kind
     */
    private void setFootData(DataBean bean, Element row, Map map, String kind) {
        if (bean.getFollow()) {
            map.put("follow","1");
        } else {
            map.put("follow","0");
        }
        map.put("sid",row.getAttribute("sid").getValue());
        if (kind.equals("jc")) {
            map.put("itemId",row.getAttribute("itemId").getValue());
        }else if(kind.equals("bd")){
            map.put("itemId",row.getAttribute("sort").getValue());
        }
        map.put("rid",row.getAttribute("rid").getValue());
        map.put("hn",row.getAttribute("hn").getValue());
        map.put("gn",row.getAttribute("gn").getValue());
        map.put("hid",row.getAttribute("hid").getValue());
        map.put("gid",row.getAttribute("gid").getValue());
        map.put("homeRank",row.getAttribute("homeRank").getValue());
        map.put("guestRank",row.getAttribute("guestRank").getValue());
        map.put("time",row.getAttribute("time").getValue());
        map.put("htime",row.getAttribute("htime").getValue());
        map.put("hsc",row.getAttribute("hsc").getValue());
        map.put("asc",row.getAttribute("asc").getValue());
        map.put("type",row.getAttribute("type").getValue());
        map.put("serverTime",row.getAttribute("serverTime").getValue());
        map.put("leaguename",row.getAttribute("leaguename").getValue());
        map.put("roundnum",row.getAttribute("roundnum").getValue());
        map.put("logourl",row.getAttribute("logourl").getValue());
//        map.put("follow",row.getAttribute("follow").getValue());
        map.put("live",row.getAttribute("live").getValue());
        map.put("halfsc",row.getAttribute("halfsc").getValue());
    }

    @Override
    public BaseResp getBasketScore(DataBean bean) throws Exception {
        BaseResp rsp = new BaseResp();
        Map map = new TreeMap();
            JXmlWrapper xml = JXmlWrapper.parseUrl(DataConstants.QTJSBF_DATA_MATCH_PATH + bean.getPeriod() + ".xml", "", DataConstants.UTF8, 10);
            int xmlcount = xml.countXmlNodes("r");
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("查询失败");
            for (int i = 0; i < xmlcount; i++) {
                String sStr = xml.getStringValue("r[" + i + "]");
                if (bean.getMatchId().equals(sStr.substring(0, sStr.indexOf('^')))) {
                    String[] score = sStr.split("\\^");
                    JXmlWrapper live = JXmlWrapper.parseUrl(DataConstants.LIVE_URL + "flag=2&id=" + bean.getMatchId(), "", DataConstants.UTF8, 10);
                    bean.setFollow(false);
                    if (bean.getCheckLogin()) {
                        bean.setMatchId(score[0]);
                        //	bean.setId(score[1]);//查询关注使用mid
                        isFollowMatch(bean);
                    }
                    setBasketData(bean,map,score,live);
                    rsp.setData(map);
                    rsp.setCode(BusiCode.SUCCESS);
                    rsp.setDesc("查询成功");
                    break;
                }
            }
        return rsp;
    }

    /**
     * 组装篮球数据
     * @param bean
     * @param map
     * @param score
     * @param live
     */
    private void setBasketData(DataBean bean, Map map, String[] score, JXmlWrapper live) {
        map.put("zid",StringUtil.isEmpty(score[0]) ? "" : score[0]);
        map.put("mid",StringUtil.isEmpty(score[1]) ? "" : score[1]);
        map.put("st","NCAA".equals(score[3])?1:0);
        map.put("ln",StringUtil.isEmpty(score[3]) ? "" : score[3]);
        map.put("hn",StringUtil.isEmpty(score[7]) ? "" : score[7]);
        map.put("gn",StringUtil.isEmpty(score[8]) ? "" : score[8]);
        map.put("hsc",StringUtil.isEmpty(score[15]) ? "" : score[15]);
        map.put("gsc",StringUtil.isEmpty(score[16]) ? "" : score[16]);
        map.put("mtime",StringUtil.isEmpty(score[4].substring(5, 16)) ? "" : score[4].substring(5, 16));
        map.put("status",StringUtil.isEmpty(score[13]) ? "" : score[13]);
        map.put("follow", bean.getFollow() ? "1" : "0");
        map.put("live", StringUtil.isEmpty(live.getStringValue("@count")) ? "" : live.getStringValue("@count"));
        map.put("hlogo","http://www.9188.com/lqzlk/img/team/mobile/" + (StringUtil.isEmpty(score[9]) ? "" : score[9]) + ".png");
        map.put("glogo","http://www.9188.com/lqzlk/img/team/mobile/" + (StringUtil.isEmpty(score[10]) ? "" : score[10]) + ".png");
    }

    /**
     * 是否已关注比赛
     * @param bean
     */
    public void isFollowMatch(DataBean bean) {
        try {
            int count;
            bean.setFollow(false);
            if(StringUtils.isEmpty(bean.getGameId())){
                count = matchFollowMapper.queryFocusBaskMatch(bean.getUid(), bean.getMatchId(), bean.getGameType());
            }else{
                count = matchFollowMapper.queryFocusMatch(bean.getUid(), bean.getMatchId(), bean.getGameId(), bean.getGameType());
            }
            if (0 != count) {
                bean.setFollow(true);
            }
        } catch (Exception e) {
            log.info("查询是否已关注比赛异常：" + e.getMessage(), e);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询是否已关注比赛异常");
        }
    }

    @Override
    public BaseResp matchFollow(DataBean bean) {
        BaseResp rsp = new BaseResp<>();
        if (0 == bean.getFtype()) {
            delMatchFollow(bean);
        } else if (1 == bean.getFtype()) {
            addMatchFollow(bean);
        }
        rsp.setCode(bean.getBusiErrCode()+"");
        rsp.setDesc(bean.getBusiErrDesc());
        return rsp;
    }

    /**
     * 关注比赛
     * @param bean
     */
    private void addMatchFollow(DataBean bean) {
        try {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            isFollowMatch(bean);
            if (bean.getFollow()) {
                bean.setBusiErrDesc("重复关注");
            } else {
                int count = matchFollowMapper.insertFocus(bean.getUid(), bean.getMatchId(), bean.getGameId(), bean.getPeriod(), bean.getSort(), bean.getGameType());
                if (count <= 0) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("关注失败");
                    log.info("关注比赛失败--> cnickid:{},mid:{}", bean.getUid(), bean.getMatchId());
                } else {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.MATCH_FOLLOW));
                    bean.setBusiErrDesc("关注成功");
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("关注发生异常");
            log.info("关注比赛异常：", e);
        }
    }

    /**
     * 取消关注(用户[delete])
     * @param bean
     */
    private void delMatchFollow(DataBean bean) {
        try {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            if(StringUtil.isEmpty(bean.getMatchId()) || "(null)".equals(bean.getMatchId()) || "null".equals(bean.getMatchId())){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("操作失败");
                return;
            }
            int count = matchFollowMapper.deletFocus(bean.getGameId(), bean.getMatchId(), bean.getUid());
            if (count <= 0) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("操作失败");
                log.info("取消关注比赛失败--> cnickid:{},mid:{}", bean.getUid(), bean.getMatchId());
            } else {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.MATCH_UNFOLLOW));
                bean.setBusiErrDesc("取消关注成功");
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("操作异常");
            log.info("取消关注比赛异常：" + e.getMessage(), e);
        }
    }

    /**
     * 查询足球直播源
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public List<LiveSourceDTO> footLive(DataBean bean) throws Exception {
        List<LiveSourceDTO> liveSourceDTOList = new ArrayList<>();
        LiveSourceDTO liveSourceDTO;

        String result = HttpClientUtil.callHttpGet(LiveBfUtil.getPostUrl("queryFootLive") + "?rid=" + bean.getMatchId());
        if (StringUtil.isEmpty(result)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询足球直播源失败");
            return null;
        }

        JXmlWrapper xml = JXmlWrapper.parse(result);
        bean.setBusiErrCode(xml.getIntValue("@code"));
        bean.setBusiErrDesc(xml.getStringValue("@desc"));

        if (bean.getBusiErrCode() != 0) {
            return null;
        }

        List<JXmlWrapper> datasNodeList = xml.getXmlNodeList("row");
        for (JXmlWrapper datasNode : datasNodeList) {
            liveSourceDTO = new LiveSourceDTO();
            liveSourceDTO.setLiveFrom(datasNode.getStringValue("@liveFrom"));
            liveSourceDTO.setLiveUrl(datasNode.getStringValue("@liveUrl"));
            liveSourceDTO.setVideoUrl(datasNode.getStringValue("@videoUrl"));

            liveSourceDTOList.add(liveSourceDTO);
        }

        return liveSourceDTOList;
    }


    /**
     * 查询篮球直播源
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public List<LiveSourceDTO> basketLive(DataBean bean) throws Exception {
        List<LiveSourceDTO> liveSourceDTOList = new ArrayList<>();
        LiveSourceDTO liveSourceDTO;

        String result = HttpClientUtil.callHttpGet(LiveBfUtil.getPostUrl("queryBasketLive") + "?flag=1&id=" + bean.getMatchId());
        if (StringUtil.isEmpty(result)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询篮球直播源失败");
            return null;
        }

        JXmlWrapper xml = JXmlWrapper.parse(result);
        bean.setBusiErrCode(xml.getIntValue("@code"));
        bean.setBusiErrDesc(xml.getStringValue("@desc"));

        if (bean.getBusiErrCode() != 0) {
            return null;
        }

        List<JXmlWrapper> datasNodeList = xml.getXmlNodeList("row");
        for (JXmlWrapper datasNode : datasNodeList) {
            liveSourceDTO = new LiveSourceDTO();
            liveSourceDTO.setLiveFrom(datasNode.getStringValue("@name"));
            liveSourceDTO.setLiveUrl(datasNode.getStringValue("@url"));

            liveSourceDTOList.add(liveSourceDTO);
        }

        return liveSourceDTOList;
    }

    /**
     * 查询赔率数据
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public OddsDTO odds(DataBean bean) throws Exception {
        OddsDTO oddsDTO = new OddsDTO();

        String result = HttpClientUtil.callHttpGet(LiveBfUtil.getPostUrl("queryFootOdds") + "?matchid=" + bean.getMatchId());
        if (StringUtil.isEmpty(result)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询足球赔率失败");
            return null;
        }

        JXmlWrapper xml = JXmlWrapper.parse(result);
        bean.setBusiErrCode(xml.getIntValue("@code"));
        bean.setBusiErrDesc(xml.getStringValue("@desc"));

        if (bean.getBusiErrCode() != 0) {
            return null;
        }
        JXmlWrapper datasNode = xml.getXmlNode("row");
        if (datasNode != null) {
            oddsDTO.setHomeTeamName(datasNode.getStringValue("@homeTeamName"));
            oddsDTO.setGuestTeamName(datasNode.getStringValue("@guestTeamName"));
            oddsDTO.setScore("null".equals(datasNode.getStringValue("@score")) ? "" : datasNode.getStringValue("@score"));
            oddsDTO.setHalfScore("null".equals(datasNode.getStringValue("@halfscore")) ? "" : datasNode.getStringValue("@halfscore"));
            oddsDTO.setJcsg("null".equals(datasNode.getStringValue("@jcsg")) ? "" : datasNode.getStringValue("@jcsg"));
            oddsDTO.setJcodds("null".equals(datasNode.getStringValue("@jcodds")) ? "" : datasNode.getStringValue("@jcodds"));
            oddsDTO.setOcOldOdds("null".equals(datasNode.getStringValue("@ocOldOdds")) ? "" : datasNode.getStringValue("@ocOldOdds"));
            oddsDTO.setOcNewOdds("null".equals(datasNode.getStringValue("@ocNewOdds")) ? "" : datasNode.getStringValue("@ocNewOdds"));
            oddsDTO.setAvgNew("null".equals(datasNode.getStringValue("@avgNew")) ? "" : datasNode.getStringValue("@avgNew"));
            oddsDTO.setAvgOld("null".equals(datasNode.getStringValue("@avgOld")) ? "" : datasNode.getStringValue("@avgOld"));
            oddsDTO.setProfit("null".equals(datasNode.getStringValue("@profit")) ? "" : datasNode.getStringValue("@profit"));
            oddsDTO.setVolume("null".equals(datasNode.getStringValue("@volume")) ? "" : datasNode.getStringValue("@volume"));
        }
        return oddsDTO;
    }

    @Override
    public List<QueryTeamDTO> queryTeamMatchHistory(DataBean bean) throws Exception {
        List<QueryTeamDTO> queryTeamDTOList = new ArrayList<>();
        QueryTeamDTO queryTeamDTO;
        String url = "http://www.9188.com/zlk/projbf/historymatch/"+bean.getTeamId()+".xml";

        JXmlWrapper xml = JXmlWrapper.parseUrl(url ,"", "UTF-8", 10);
        if (xml == null) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
            return queryTeamDTOList;
        }

        int count = xml.countXmlNodes("r");
        if (count == 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("暂无数据");
            return queryTeamDTOList;
        }

        for (int i = 0; i < count; i++) {
            queryTeamDTO = new QueryTeamDTO();
            String mtime=xml.getStringValue("r["+i+"].@mtime")+"000";//比赛时间 = *1000
            long sd=new Long(mtime);
            Date dat=new Date(sd);
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MM-dd HH:mm");
            String mt=format.format(dat);

            //联赛名称
            queryTeamDTO.setLeagueName(xml.getStringValue("r["+i+"].@ln"));
            //主队名称
            queryTeamDTO.setHomeTeamName(xml.getStringValue("r["+i+"].@hteam"));
            //客队名称
            queryTeamDTO.setAwayTeamName(xml.getStringValue("r["+i+"].@ateam"));
            //主队tid
            queryTeamDTO.setHomeTeamId(xml.getStringValue("r["+i+"].@htid"));
            //客队tid
            queryTeamDTO.setAwayTeamId(xml.getStringValue("r["+i+"].@atid"));
            //主队比分
            queryTeamDTO.setHomeTeamScore(xml.getStringValue("r["+i+"].@hscore"));
            //客队比分
            queryTeamDTO.setAwayTeamScore(xml.getStringValue("r["+i+"].@ascore"));
            //比赛时间
            queryTeamDTO.setMatchTime(mt);

            queryTeamDTOList.add(queryTeamDTO);
        }
        return queryTeamDTOList;
    }

    /**
     * 查询指定场次比赛实况数据
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public RoundInfoDTO queryRoundInfo(DataBean bean) throws Exception {
        RoundInfoDTO roundInfoDTO = new RoundInfoDTO();
        List<MatchDTO> matchDTOList = new ArrayList<>();
        MatchDTO matchDTO;
        int gid = 70;
        String url = LiveBfUtil.getPostUrl("jsbflive") + bean.getIds();
        String flag = bean.getFlag();
        if (!StringUtil.isEmpty(flag)) {
            url += "&flag=";
            url += flag;
            gid = 85;
        }
        String str = HttpClientUtil.callHttpGet(url);
        if (StringUtil.isEmpty(str)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败");
            return roundInfoDTO;
        }

        JXmlWrapper xml = JXmlWrapper.parse(str);
        List<JXmlWrapper> rows = xml.getXmlNodeList("row");
        roundInfoDTO.setUrl(LiveBfUtil.getPostUrl("teamimg"));
        roundInfoDTO.setNewUrl(LiveBfUtil.getUrl("teamimg", "newurl"));

        for (JXmlWrapper row : rows) {
            matchDTO = new MatchDTO();
            matchDTO.setRid(row.getStringValue("@rid"));
            matchDTO.setSid(row.getStringValue("@sid"));
            matchDTO.setLn(row.getStringValue("@ln"));
            matchDTO.setHn(row.getStringValue("@hn"));
            matchDTO.setGn(row.getStringValue("@gn"));
            matchDTO.setHid(row.getStringValue("@hid"));
            matchDTO.setGid(row.getStringValue("@gid"));
            matchDTO.setTime(row.getStringValue("@time"));
            matchDTO.setHtime(row.getStringValue("@htime"));
            matchDTO.setHsc(row.getStringValue("@hsc"));
            matchDTO.setAsc(row.getStringValue("@asc"));
            matchDTO.setHalfsc(row.getStringValue("@halfsc"));
            matchDTO.setType(row.getStringValue("@type"));
            matchDTO.setJn(row.getStringValue("@jn"));
            matchDTO.setRoundItemId(row.getStringValue("@roundItemId"));
            matchDTO.setQc(row.getStringValue("@qc"));
            matchDTO.setSort(row.getStringValue("@sort"));
            matchDTO.setSwapTeam(row.getStringValue("@swapTeam"));
            matchDTO.setTvlive(row.getStringValue("@tvlive"));
            String key = getMatchVisitsKey(gid, row.getStringValue("@sort"), row.getStringValue("@qc"));
            int viewer = getMatchVisits(key, 7);
            matchDTO.setViewer(viewer + "");

            matchDTOList.add(matchDTO);
        }
        roundInfoDTO.setMatches(matchDTOList);
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
        return roundInfoDTO;
    }

    /**
     * 获取足球访问量缓存key
     * @param gid
     * @param sort
     * @param expect
     * @return
     */
    public static String getMatchVisitsKey(int gid, String sort, String expect) {
        String key = null;
        if (gid == 70) {
            key = ONLINE_VIEWER + sort;
        } else if (gid == 85) {
            key = ONLINE_VIEWER + expect + sort;
        }
        return key;
    }

    /**
     * 获取对阵访问量
     * @param key
     * @param day 缓存不存在时，新缓存天数
     * @return
     */
    public int getMatchVisits(String key, int day) {
        int viewer;
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        cacheBean.setTime(Constants.TIME_DAY * day);

        String value = redisClient.getString(cacheBean, log, SysCodeConstant.DATACENTER);
        if (StringUtil.isEmpty(value)) {
            viewer = RandomUtils.nextInt(100) + 1;
            cacheBean.setValue(viewer + "");
            redisClient.setString(cacheBean, log, SysCodeConstant.DATACENTER);
        } else {
            viewer = Integer.parseInt(value.toString());
        }
        return viewer;
    }
}
