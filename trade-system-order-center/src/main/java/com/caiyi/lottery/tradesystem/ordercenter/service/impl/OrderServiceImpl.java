package com.caiyi.lottery.tradesystem.ordercenter.service.impl;


import bean.TokenBean;
import bean.UserBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.dao.*;
import com.caiyi.lottery.tradesystem.ordercenter.service.OrderService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserInterface;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.code.JinDuUtil;
import com.caiyi.lottery.tradesystem.util.matrix.MatrixConstants;
import com.caiyi.lottery.tradesystem.util.matrix.MatrixUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import constant.UserConstants;
import order.bean.*;
import order.dto.*;
import order.pojo.*;
import order.response.XmlResp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import response.UserPersonalInfoResq;
import trade.constants.TradeConstants;
import util.UserErrCode;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.*;

/**
 * @author tiankun
 * @date 2017/12/21
 */
@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static HashMap<String, RankingBean> ranks = new HashMap<>();

    @Autowired
    private ZhuiHaoMapper zhuiHaoMapper;
    @Autowired
    private ProjMapper projMapper;
    @Autowired
    private ShareListMapper shareListMapper;
    @Autowired
    private ProjBuyMapper projBuyMapper;
    @Autowired
    private Proj_ProjBuyMapper proj_projBuyMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ZhDetailMapper zhDetailMapper;
    @Autowired
    private VZhuiHaoMapper vZhuiHaoMapper;
    @Autowired
    private VAllProjBuyerAppMapper vAllProjBuyerAppMapper;
    @Autowired
    private VProjBuyerAppMapper vProjBuyerAppMapper;
    @Autowired
    private NewTicketDetailMapper newTicketDetailMapper;

    //冠亚军对阵匹配
    private static final String regex = ".?[/|=](\\d{1,2})\\(";
    private static final String regex1 = "([GYJ]+)";

    @Autowired
    UserInterface userInterface;

    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");

    @Override
    public List<String> getMatrixCodesList(OrderBean bean) {
        try {
            if (!StringUtil.isEmpty(bean.getCodes())){

                //投注号码,红球在前,蓝球在后,红球与蓝球之间用|隔开,只支持单个号码组合
                String tzcodes = bean.getCodes().substring(0, bean.getCodes().indexOf("-"));
                //旋转类型,中用S替代,保用E替代,如：中6保5,则该值为S6E5,中6保4,则该值为S6E4,中5保5,则该值为S5E5
                String xztype =  bean.getCodes().substring(bean.getCodes().indexOf("-") + 1);

                List<String> codeList = MatrixUtils.getMatrixCodes(bean.getGid(), tzcodes, xztype);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("查询成功");
                return codeList;
            }
        } catch (Exception e) {
            logger.error("获取旋转矩阵错误",e);
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询失败");
        }
        return null;
    }

    /**
     * @param bean
     * @Description: 查询出票明细xml
     * @Date: 11:25 2017/12/22
     * @return:
     */
    @Override
    public XmlResp awarddetail(OrderBean bean) throws Exception {
        XmlResp xmlResp = new XmlResp();
        if (!CheckUtil.isNullString(bean.getGid()) && !CheckUtil.isNullString(bean.getHid())) {
            if (!(GameContains.isFootball(bean.getGid()) || GameContains.isBasket(bean.getGid())
                    || GameContains.isGYJ(bean.getGid()))) {
                xmlResp.setCode(ORDER_NO_TICKET_DETAIL);
                xmlResp.setDesc("该彩种无出票明细");
                return xmlResp;
            }
            boolean blnMy = false;
            QueryProjPojo projPojo = projMapper.queryPinfo(bean.getGid(), bean.getHid());
            if (projPojo != null) {
                xmlResp.setCode(SUCCESS);
                Date castdate = projPojo.getCastdate();
                String nid = projPojo.getCnickid();
                String gid = projPojo.getGameid();
                Integer icast = projPojo.getIcast();
                Integer iopen = projPojo.getIopen();
                Integer istate = projPojo.getIstate();
                Integer itype = projPojo.getItype();
                Integer pid = projPojo.getPeriodid();
                boolean bln = false;
                if (nid.equalsIgnoreCase(bean.getUid())) {// 本用户
                    bln = true;
                }
                if (itype == 3) {// 该单为跟单
                    if (!checkItemLastDate(bean.getHid(), true)) {
                        xmlResp.setCode(ORDER_GENMAI_NO_CHECK);
                        xmlResp.setDesc("该方案为跟买方案,暂不能查看");
                        return xmlResp;
                    }
                }
                if (itype == 0 && !bln) {// 代购方案非本人查看
                    xmlResp.setCode(ORDER_ERR_DAIGOU_VIEW);
                    xmlResp.setDesc("抱歉，该方案是代购方案，您不是该方案的发起人，不能查看");
                } else {
                    int num = projBuyMapper.queryProjNumByUid(bean.getGid(), bean.getUid());
                    if (num > 0) {
                        blnMy = true;
                    }
                    if (!bln) {
                        if (iopen == 0) {// 公开
                            bln = true;
                        } else if (iopen == 1) {// 截止后公开
                            if (istate > 1) {
                                bln = true;
                            }
                        } else if (iopen == 2) {// 对参与人员公开
                            if (blnMy) {
                                bln = true;
                            }
                        } else if (iopen == 3) {// 截止后对参与人员公开
                            if (istate > 1 && blnMy) {
                                bln = true;
                            }
                        }
                    }
                    if (!bln) {
                        xmlResp.setCode(ORDER_NO_RIGHT);
                        xmlResp.setDesc("出票明细无权查看");
                        return xmlResp;
                    }
                    if (icast != 3) {
                        xmlResp.setCode(ORDER_NO_TICKET_OUT);
                        xmlResp.setDesc("尚未出票，请稍候");
                        return xmlResp;
                    }
                    Calendar cal = Calendar.getInstance();
                    if (GameContains.isFootball(gid)) {
                        cal.set(2012, 10 - 1, 25, 0, 0, 0);
                    } else if (GameContains.isBasket(gid)) {
                        cal.set(2012, 11 - 1, 2, 0, 0, 0);
                    } else if (GameContains.isGYJ(gid)) {
                        cal.set(2012, 11 - 1, 2, 0, 0, 0);
                    } else {
                        xmlResp.setCode(ORDER_NOT_CHECK_SP);
                        xmlResp.setDesc("该彩种不支持查看出票sp值");
                        return xmlResp;
                    }
                    if (cal.getTime().after(castdate)) {
                        xmlResp.setCode(ORDER_HISTORY_NO_CHECK);
                        xmlResp.setDesc("出票明细为新功能,历史方案不支持查看");
                        return xmlResp;
                    }
                    File file = new File("/opt/export/data/guoguan/" + bean.getGid() + "/" + pid + "/pass",
                            bean.getHid() + ".xml");
                    if (file.exists()) {
                        JXmlWrapper xml = JXmlWrapper.parse(file);
                        String busiXml = xml.toXmlString().replaceAll("<\\?.+?\\?>", "");
                        logger.info("拼接前出票明细xml:" + busiXml);
                        String newbusiXml= getMatchName(busiXml, gid, pid);
                        logger.info("投注选项改成对阵场次:" + newbusiXml);
                        XmlDTO projDTO = new XmlDTO();
                        projDTO.setBusiXml(newbusiXml);
                        xmlResp.setData(projDTO);
                        xmlResp.setCode(SUCCESS);
                        xmlResp.setDesc("获取成功");

                    } else {
                        xmlResp.setCode(ORDER_GUOGUAN_NO_CREATE);
                        xmlResp.setDesc("过关文件尚未生成，请稍候");
                    }
                }
            } else {
                xmlResp.setCode(ORDER_SCHEME_NOT_EXITS);
                xmlResp.setDesc("方案不存在");
            }

        } else {
            xmlResp.setCode(ORDER_PARAMETER_ERROR);
            xmlResp.setDesc("输入参数不正确");
        }
        return xmlResp;
    }

    /**
     * 将投注选项翻译成对阵
     * @param busiXml
     * @param gid
     * @param pid
     * @return
     */
    private String getMatchName(String busiXml, String gid, Integer pid) {
        StringBuilder sb = new StringBuilder();
        try {
            String filename = "98".equals(gid) ? "gj" : "gyj";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(busiXml);

            File file1 = new File(FileConstant.TOPIC_FOOTBALL_MATCH + pid,
                    filename + ".xml");
            JXmlWrapper xml1 = JXmlWrapper.parse(file1);
            int count = xml1.countXmlNodes("row");
            int end = 0;
            while (matcher.find()) {
                String s1 = matcher.group(1);
                for (int i = 0; i < count; i++) {
                    String cid = xml1.getStringValue("row[" + i + "].@cindex");
                    String name = xml1.getStringValue("row[" + i + "].@name");
                    if (s1.equals(cid)) {
                        int start = matcher.start(1);
                        sb.append(busiXml.substring(end, start));
                        end = matcher.end(1);
                        sb.append(name);
                        break;
                    }
                }
            }
            sb.append(busiXml.substring(end, busiXml.length()));
        } catch (Exception e) {
            sb.append(busiXml);
            logger.error("冠亚军出票明细投注翻译成对阵出错",e);
        }
        return sb.toString();
    }

    /**
     * @param hid
     * @param isFollow
     * @Description: 检查神单是否到显示的时间
     * @Date: 14:28 2017/12/22
     * @return:
     */
    @Override
    public Boolean checkItemLastDate(String hid, boolean isFollow) {
        Date lastDate;
        if (isFollow) {// 是跟投的单子
            lastDate = shareListMapper.queryShareListAndFollowListByHid(hid);
        } else {// 是分享的单子
            lastDate = shareListMapper.queryShareListByHid(hid);
        }
        if (lastDate != null) {
            Date now = new Date();
            if (now.getTime() > lastDate.getTime()) {// 已过最后一场比赛的开始时间
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void hideZhuihaoDetail(OrderBean bean) {
        logger.info("开始隐藏追号记录uid=" + bean.getUid() + ",gid=" + bean.getGid() + ",zhid=" + bean.getPid());
        if (StringUtils.isEmpty(bean.getGid()) || StringUtils.isEmpty(bean.getPid())) {
            logger.info("传入参数为空,游戏编号：{}，期次编号：{}", bean.getGid(), bean.getPid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_PARAM_NULL));
            bean.setBusiErrDesc("参数为空");
            return;
        }
        List<ZhuihaoDTO> zhList = zhuiHaoMapper.selectZhuiHaoDetail(bean.getGid(), bean.getPid());
        if (null == zhList || zhList.size() < 1) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ORDER_AFFILIATION_FAIL));
            bean.setBusiErrDesc("查询追号记录所属用户昵称失败");
        }

        for (ZhuihaoDTO zhdto : zhList) {
            int rsp = checkZhuihaoRsp(zhdto, bean);
            if (0 != rsp) {
                return;
            }
        }
        //TODO 隐藏追号能同时隐藏多个号？
        int count = zhuiHaoMapper.updateZhDetail(bean.getGid(), bean.getPid());
        if (1 == count) {
            logger.info("隐藏追号记录成功uid=" + bean.getUid() + ",gid=" + bean.getGid() + ",zhid=" + bean.getPid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("隐藏成功");
        } else {
            logger.info("隐藏失败,隐藏记录行数count=" + count);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("隐藏失败");
        }
    }

    /**
     * 处理追号回值
     *
     * @param zhdto
     * @param bean
     * @return
     */
    private int checkZhuihaoRsp(ZhuihaoDTO zhdto, OrderBean bean) {
        int flag = -1;
        Integer ihide = zhdto.getIhide();
        if (null == ihide) {
            ihide = 0;
        } else if (1 == ihide) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_REPEAT_OPERATION));
            bean.setBusiErrDesc("该追号记录已经被隐藏,不能重复隐藏");
            logger.info("该追号记录已经被隐藏,不能重复隐藏");
            return flag;
        }

        String nickid = zhdto.getCnickid();
        if (StringUtils.isEmpty(nickid) || !nickid.equals(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_DATA_MISMATCHING));
            bean.setBusiErrDesc("该追号记录不属于当前用户,不能隐藏");
            logger.info("该追号记录不属于当前用户 nickid-->" + nickid);
            return flag;
        }
        Integer ireason = zhdto.getIreason();
        Integer isreturn = zhdto.getIsreturn();
        if (isreturn < 2 && ireason < 1) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_UNFINISH_OPERATION));
            bean.setBusiErrDesc("该追号记录还未完成派奖,不能隐藏");
            logger.info("该追号记录还未完成派奖,不能隐藏");
            return flag;
        }
        flag = 0;
        return flag;
    }

    @Override
    public void hideBuyRecord(OrderBean bean) {
        if (StringUtils.isEmpty(bean.getDid()) || StringUtils.isEmpty(bean.getGid())) {
            logger.info("传入参数为空,游戏编号：{}，期次编号：{},认购/方案编号:{}", bean.getGid(), bean.getPid(), bean.getDid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_PARAM_NULL));
            bean.setBusiErrDesc("参数为空");
            return;
        }
        List<ProjDTO> buyList;
        if(!StringUtils.isEmpty(bean.getHid())){
            buyList = proj_projBuyMapper.selectBuyStatusByHid(bean.getGid(), bean.getHid());
        }else{
            buyList = proj_projBuyMapper.selectBuyStatus(bean.getGid(), bean.getBid());
        }
        if (null != buyList && buyList.size() > 0 && null != buyList.get(0)) {
            int rsp = checkBuyRsp(buyList.get(0), bean);
            if (0 != rsp) {
                return;
            }
        } else {
            logger.info("无效记录!");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_INVALID_RECODE));
            bean.setBusiErrDesc("无效记录!");
            return;
        }

        int count;
        if(!StringUtils.isEmpty(bean.getHid())){
            count = projBuyMapper.updateProjBuyByHid(bean.getGid(), bean.getHid());
        }else{
            count = projBuyMapper.updateProjBuy(bean.getGid(), bean.getBid());
        }
        if (1 == count) {
            logger.info("隐藏投注记录成功!");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("隐藏投注记录成功!");
        } else {
            logger.info("隐藏失败,隐藏记录行数count=" + count);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("隐藏失败");
        }
    }

    /**
     * 处理投注回值
     *
     * @param bean
     * @param pojo
     */
    private int checkBuyRsp(ProjDTO pojo, OrderBean bean) {
        int flag = -1;
        if (!bean.getUid().equals(pojo.getCnickid())) {
            logger.info("不能隐藏他人记录!");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_BIN_OPERATION));
            bean.setBusiErrDesc("不能隐藏他人记录!");
            return flag;
        } else if (null != pojo.getIhide() && 1 == pojo.getIhide()) {
            logger.info("该记录已被隐藏,请刷新!");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_FINISH_OPERATION));
            bean.setBusiErrDesc("该记录已被隐藏,请刷新!");
            return flag;
        } else if (0 == pojo.getIcancel() && pojo.getIreturn() <= 1) {
            logger.info("投注记录未派奖,无法隐藏!");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_BIN_OPERATION));
            bean.setBusiErrDesc("投注记录未派奖,无法隐藏!");
            return flag;
        }
        flag = 0;
        return flag;
    }

    @Override
    public List queryCastDetail(OrderBean bean) throws Exception {
        List<Object> list = new ArrayList<>();
        logger.info("查询购彩记录(新版快频),用户名：{}，游戏编号：{}，{}",bean.getGid(), bean.getUid(), bean.getTid());
        List<ProjZhPojo> projList = projMapper.queryProjDetail(bean.getGid(), bean.getUid(), bean.getTid());
        if (null != projList && projList.size() > 0) {
            for (ProjZhPojo projPojo : projList) {
                if (!StringUtils.isEmpty(projPojo.getProjid())) {
                    list.add(projPojo);
                }
            }
        }

        logger.info("追号记录(新版快频),用户名：{}，游戏编号：{}，{}",bean.getGid(), bean.getUid(), bean.getTid());
        List<ProjZhPojo> zhList = zhuiHaoMapper.queryZhDetail(bean.getGid(), bean.getUid(), bean.getTid());
        if (null != zhList && zhList.size() > 0) {
            for (ProjZhPojo zhuihaoPojo : zhList) {
                if (!StringUtils.isEmpty(zhuihaoPojo.getProjid())) {
                    list.add(zhuihaoPojo);
                }
            }
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("获取成功");
        return list;
    }

    public List queryProjDetail(OrderBean bean) throws Exception {
        if (GameContains.canNotUse(bean.getGid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("不支持的彩种");
            return null;
        }

        List<Object> list = new ArrayList<>();
        logger.info("查询购彩记录(新版快频),用户名：{}，游戏编号：{}，{}",bean.getGid(), bean.getUid(), bean.getTid());
        List<ZhProjPojo> projList = projMapper.queryCastDetail(bean.getGid(), bean.getUid(), bean.getTid());
        if (null != projList && projList.size() > 0) {
            for (ZhProjPojo projPojo : projList) {
                list.add(projPojo);
            }
        }

        logger.info("追号记录(新版快频),用户名：{}，游戏编号：{}，{}",bean.getGid(), bean.getUid(), bean.getTid());
        List<ZhProjPojo> zhList = zhuiHaoMapper.queryZhList(bean.getGid(), bean.getUid(), bean.getTid());
        if (null != zhList && zhList.size() > 0) {
            for (ZhProjPojo zhuihaoPojo : zhList) {
                list.add(zhuihaoPojo);
            }
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("获取成功");
        return list;
    }

    @Override
    public Map ranking(OrderBean bean) throws Exception {
        List<Object> list = new ArrayList<>();
        Map<String,Object> map = null;
        boolean iopen = false;//白名单开关
        Object obj = redisGetCacheByTokenBean(bean.getAppid());
        if (null != obj) {
            String ouser = parseObject(obj);
            if (!StringUtils.isEmpty(ouser) && 100 == Integer.parseInt(ouser)) {
                iopen = true;
            }
        }
        String key = bean.getFind() + "_" + bean.getName();
        RankingBean rb = ranks.get(key);
        JXmlWrapper xml = readXml(key, rb, bean);
        if (null == xml) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ORDER_GETXML_FAIL));
            bean.setBusiErrDesc("xml读取错误");
        } else {
            if (iopen) {
                int count = xml.countXmlNodes("row");
                count = count > 15 ? 15 : count;
                //获取彩种logourl
                String logoUrl = getLogoUrl(bean.getFind());
                map = new TreeMap<>();
                map.put("gid",bean.getFind());
                map.put("logo",logoUrl);
                for (int i = 0; i < count; i++) {
                    Map tmap = new TreeMap();
                    if (!xml.getStringValue("row[" + i + "].@cnickid").equals(bean.getUid())) {
                        tmap.put("rank",i+1+"");
                        tmap.put("imoney",xml.getStringValue("row[" + i + "].@imoney"));
                        tmap.put("cnickid",xml.getStringValue("row[" + i + "].@cnickid").substring(0, 2) + "*****");
                    }else{
                        tmap.put("rank",i+1+"");
                        tmap.put("imoney",xml.getStringValue("row[" + i + "].@imoney"));
                        tmap.put("cnickid",xml.getStringValue("row[" + i + "].@cnickid"));
                    }
                    list.add(tmap);
                }
                map.put("datas",list);
            }
            if(map.size() >0){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("查询成功");
            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_NODATA));
                bean.setBusiErrDesc("暂无数据");
            }
        }
        return map;
    }

    @Override
    public List lotteryInfoNew(OrderBean bean) throws Exception {
        String gid = bean.getGid();
        List list = new ArrayList();
        if (TradeConstants.DELAY_GID_MAP.containsKey(gid)) {
            list = delayLotteryInfoNew(bean);
            logger.info("lotteryInfoNew获取结束,uid==" + bean.getUid() + ",gid==" + bean.getGid() + "，result==");
        } else {
            logger.info("获取开奖历史和遗漏值,gid=" + gid + " 彩种已停售");
            bean.setBusiErrCode(120);
            bean.setBusiErrDesc("彩种已停售");
            return list;
        }
        return list;
    }

    private List delayLotteryInfoNew(OrderBean bean) throws Exception {
        String periodFileDir = "/opt/export/data/phot/" + bean.getGid();

        JXmlWrapper xml = XmlUtil.readLocalXml(periodFileDir, "qc.xml");
        int size = xml.countXmlNodes("row");
        int availablePeriodCount = getAvailablePeriodCount(bean.getGid());
        //最后一期期次
        String lastPid=xml.getStringValue("rowc.@lastPid");
        logger.info("uid=="+bean.getUid()+",lastPid=="+lastPid);
        String pPid=xml.getStringValue("rowc.@p");
        logger.info("uid=="+bean.getUid()+",pPid=="+pPid);
        List<BetRecordDTO> castList=queryCastListNew(bean, pPid,lastPid, xml);
        Map<String, Integer> bestRecord = getBestRecord4Period(castList);
        logger.info(bean.getGid()+" bestRecord==>"+bestRecord.toString());

        Calendar awardTime = Calendar.getInstance();
        //版本控制
        List list =reAppendResultNew(bean, null, xml, 0, size, availablePeriodCount, bestRecord, awardTime);
        return list;
    }

    private List reAppendResultNew(OrderBean bean, StringBuilder info,
                                         JXmlWrapper xml, int startIndex, int endIndex,
                                         int availablePeriodCount, Map<String, Integer> bestRecord,
                                         Calendar awardTime) throws ParseException {
        String prefix;
        String pid;
        String tn;
        JSONObject json = new JSONObject();
        List<JXmlWrapper> list = xml.getXmlNodeList("row");
        List resList = new ArrayList();
        for (int j = startIndex; j <= endIndex; j++) {
            if (j == startIndex) {
                Map<String, String> rowcMap = new HashMap<>();
                pid = xml.getStringValue("rowc.@p");
                rowcMap.put("tn", getBestRecord4Period(bestRecord, pid) + "");
                rowcMap.put("p", pid);
                resList.add(rowcMap);
            } else {
                JXmlWrapper e = list.get(j - 1);
                pid = e.getStringValue("@p");
                Map<String, String> rowMap = new HashMap<>();
                rowMap.put("tn", getBestRecord4Period(bestRecord, pid) + "");
                rowMap.put("p", pid);
                resList.add(rowMap);
            }
        }
        return resList;
    }

    class Test{
        String p;
        String tn;
    }

    /**
     * 判断当前期次用户最佳投注结果
     * @return
     */
    private int getBestRecord4Period(Map<String, Integer> bestRecord, String currentPid){
        if (bestRecord.containsKey(currentPid)) {
            return bestRecord.get(currentPid);
        }
        return 0;
    }

    private Map<String, Integer> getBestRecord4Period(List<BetRecordDTO> betRecords) {
        Map<String, Integer> bestRecord = new HashMap<String, Integer>();
        if (betRecords == null || betRecords.size() <= 0) {
            return bestRecord;
        }
        for (BetRecordDTO record : betRecords) {
            if (bestRecord.containsKey(record.getPid())) {
                if (record.getResult() > bestRecord.get(record.getPid())) {
                    bestRecord.put(record.getPid(), record.getResult());
                }
            } else {
                bestRecord.put(record.getPid(), record.getResult());
            }
        }
        return bestRecord;
    }

    /**
     * 查询投注记录(投注+追号)的投注号码 （挖金矿）
     * @param currentPid 当前期次
     * @throws Exception
     */
    private List<BetRecordDTO> queryCastListNew(OrderBean orderBean, String pPid,String currentPid, JXmlWrapper periodXml) throws Exception {
        String uid = orderBean.getUid();
        if (StringUtil.isEmpty(uid)) {
            orderBean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            orderBean.setBusiErrDesc("用户名为空");
            return null;
        }
        String gid = orderBean.getGid();
        orderBean.setTid(currentPid);
        List<ZhProjPojo> list = queryProjDetail(orderBean);

        if (list == null || list.size() <= 0) {
            return null;
        }
        List<BetRecordDTO> records = new ArrayList<BetRecordDTO>();
        String salePid = periodXml.getXmlRoot().getAttributeValue("pid");//当前销售期
        List<JXmlWrapper> missXmlRows = null;
        if (GameContains.szc.containsKey(gid)) {
            missXmlRows= periodXml.getXmlNodeList("rowp");
        } else {
            missXmlRows = periodXml.getXmlNodeList("row");
        }
        for (ZhProjPojo zpj : list) {
            BetRecordDTO record = new BetRecordDTO();
            record.setGid(gid);
            record.setBetType(zpj.getType());
            record.setPid(zpj.getPid());
            record.setBonus(Double.parseDouble(zpj.getBonus()));
            int st = zpj.getSt();
            int tn;
            int ost=1;//拉金矿的时候是0 不拉是1

            if (!GameContains.szc.containsKey(gid)) {
                for (JXmlWrapper missRow : missXmlRows) {
                    if (pPid.equals(record.getPid())) {//投注期等于当前在售期
                        ost=0;
                        break;
                    }

                    if (record.getPid().equals(missRow.getStringValue("@p"))) {
                        if (StringUtil.isEmpty(missRow.getStringValue("@c"))) {
                            ost=0;
                        }
                        break;
                    }
                }
            }


            if (st == 1) {
                if (ost == 1) {
                    if (record.getBonus() > 0) { // 成功且中奖
                        tn = 10;
                    } else {// 成功未中奖
                        tn = 9;
                    }
                } else { // 成功未开奖
                    tn = 6;
                }
            } else {
                if (ost == 1) {
                    if (record.getBonus() > 0) { // 失败且中奖
                        tn = 8;
                    } else { // 失败未中奖
                        tn = 7;
                    }
                } else { // 失败未开奖
                    tn = 5;
                }
            }
            record.setResult(tn);
            records.add(record);
        }
        return records;
    }

    /**
     * 获取最多可追号期次数
     * @param gid
     * @return
     * @throws IOException
     * @throws ParseException
     */
    protected int getAvailablePeriodCount(String gid) throws IOException, ParseException{
        JXmlWrapper xml = XmlUtil.readLocalXml("/opt/export/data/phot/" + gid, "s.xml");

        List<JXmlWrapper> rows = xml.getXmlNodeList("row");
        int periodCount = 0;
        String endTime = null;
        for (JXmlWrapper row : rows) {
            //截止时间
            endTime = row.getStringValue("@et");
            long endTimeMills = ConcurrentSafeDateUtil.parse(endTime, JinDuUtil.patternDatabase).getTime();
            long now = System.currentTimeMillis();
            if (now < endTimeMills) {
                periodCount++;
            }
        }
        return periodCount;
    }

    private String parseObject(Object obj) {
        String ouser="";
        TokenBean tokenBean = (TokenBean) obj;
        if (!StringUtils.isEmpty(tokenBean.getParamJson())) {
            JSONObject jsObj = JSON.parseObject(tokenBean.getParamJson());
            Object object = jsObj.get(UserConstants.OPENUSER);
            if (object != null) {
                ouser = object.toString();
            }
        }
        return ouser;
    }

    private JXmlWrapper readXml(String key, RankingBean rb, OrderBean bean) {
        JXmlWrapper xml = null;
        File file;
        if (rb == null || rb.needUpdate()) {
            if ("win_all".equals(bean.getFind())) {
                file = new File(UserConstants.RANK_PATH + "win_all.xml");
            } else if ("lastest_win".equals(bean.getFind())) {
                file = new File(UserConstants.RANK_PATH + "lastest_win.xml");
            } else {
                file = new File(UserConstants.RANK_PATH + bean.getFind() + "_" + bean.getName() + ".xml");
            }
            if (file.exists()) {
                xml = JXmlWrapper.parse(file);
                if (rb == null) {
                    rb = new RankingBean();
                }
                rb.setXml(xml);
                rb.setTime(System.currentTimeMillis());
                ranks.put(key, rb);
            }
        } else {
            xml = rb.getXml();
        }
        return xml;
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
     * 取-String
     * @param key
     */
    private Object redisGetCacheByTokenBean(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        Object Object = redisClient.getObject(cacheBean, TokenBean.class, logger, SysCodeConstant.ORDERCENTER);
        return Object;
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

    @Override
    public Map queryLotteryDetail(OrderBean bean) {
        Map map = null;
        if(1 == bean.getFlag()){
            //查询用户购彩记录
            map = queryGoucaiRecord(bean);
        }else if(2 == bean.getFlag()){
            //查询追号记录
            map = queryZhuiHaoRecord(bean);
        }else if(3 == bean.getFlag()){
            //查询所有投注记录
            map = queryAllRecord(bean);
        }
        return map;
    }

    /**
     * 查询所有投注记录
     * @param bean
     */
    private Map queryAllRecord(OrderBean bean) {
        Map map = null;
        Calendar now = Calendar.getInstance();
        bean.setEtime(ConcurrentSafeDateUtil.format(now.getTime(), "yyyy-MM-dd"));

        //目前限制为只查询最近3个月的投注记录
        now.add(Calendar.MONTH, -3);
        bean.setStime(ConcurrentSafeDateUtil.format(now.getTime(), "yyyy-MM-dd"));
        String gidCondition = getGidCondition(bean.getGid());
        if (null == gidCondition) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDRE_UNSUPPORT_LOTTERYTYPE));
            bean.setBusiErrDesc("不支持的彩种");
            return null;
        }
        try {
            Page<AllRecordBean> allBuyRecord = queryAllBuyRecord(bean, gidCondition);
            if (null == allBuyRecord || allBuyRecord.size() == 0) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_NODATA));
                bean.setBusiErrDesc("您没有投注记录~");
                if(bean.getSource() >= 4000 && bean.getSource() < 5000)
                {
                    bean.setBusiErrDesc("您没有相关记录~");
                }
            } else {
                map = new TreeMap();
                int grade = queryUserWhitelistGrade(bean);
                map.put("totalRecords", allBuyRecord.getTotal());
                map.put("totalPages", allBuyRecord.getPages());
                map.put("pageSize", allBuyRecord.getPageSize());
                map.put("pageNumber", allBuyRecord.getPageNum());
                List detailist = appendAllRecord(allBuyRecord, grade);
                judgeStatus(map,bean,detailist);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
            logger.info("查询用户投注记录出现异常,用户名=" + bean.getUid(), e);
        }
        return map;
    }

    private List appendAllRecord(Page<AllRecordBean> allBuyRecord, int grade) throws Exception {
        List tlist = new ArrayList();
        Map tmap;
        if (allBuyRecord != null && allBuyRecord.size() > 0) {
            String pattern = "yyyy-MM-dd HH:mm:ss";
            for (AllRecordBean record : allBuyRecord) {
                tmap = new TreeMap<>();
                tmap.put("gid",record.getGid()+"");
                tmap.put("logoUrl",getLogoUrl(record.getGid())+"");
                tmap.put("projid", record.getProjid()+"");
                tmap.put("money", decimalFormat.format(record.getBuyMoney())+"");
                tmap.put("buydate", ConcurrentSafeDateUtil.format(record.getBuyDate(), pattern)+"");
                tmap.put("iszh", record.getIsZh()+"");
                tmap.put("rmoney", decimalFormat.format(record.getAwardMoney())+"");
                if(0==record.getIsZh()){
                    tmap.put("pid", record.getPid()+"");
                    tmap.put("buyid", record.getBuyid()+"");
                    tmap.put("cancel", record.getCancelFlag()+"");
                    tmap.put("return", record.getReturnFlag()+"");
                    tmap.put("award", record.getAwardFlag()+"");
                    tmap.put("ty", record.getTy()+"");
                    tmap.put("istate", record.getBuyState()+"");
                    tmap.put("icast", record.getCastFlag()+"");
                    tmap.put("status", record.getStateDesc()+"");
                    tmap.put("shareGod", record.getShareGod()+"");
                    tmap.put("state", getState(record, grade, pattern)+"");
                    tmap.put("iend", ConcurrentSafeDateUtil.format(record.getEndtime(), pattern)+"");
                }else if(1==record.getIsZh()){
                    tmap.put("pnums", record.getPnum()+"");
                    tmap.put("finish", record.getFinishFlag()+"");
                    tmap.put("success", record.getSuccessFlag()+"");
                    tmap.put("casts", record.getCasts()+"");
                    tmap.put("reason", record.getReason()+"");
                    tmap.put("failure", record.getFailureFlag()+"");
                    tmap.put("zhflag", record.getZhflag()+"");
                    tmap.put("zhtype", record.getZhtype()+"");
                }
                tlist.add(tmap);
            }
        }
        return tlist;
    }

    /**
     * 查询用户购彩记录
     * @param bean
     * @param gidCondition
     * @return
     */
    private Page<AllRecordBean> queryAllBuyRecord(OrderBean bean, String gidCondition) {
        // 停售期间只有安卓/iOS/WP客户端显示投注记录
        int source = bean.getSource();
        if (source < 1000 || source >= 5000) {
            return null;
        }
        PageHelper.startPage(bean.getPn(), bean.getPs());
        List<AllRecordBean> buyerRecord = vAllProjBuyerAppMapper.queryAllRecord(bean.getStime(),bean.getEtime(),bean.getUid(),gidCondition);
        Page<AllRecordBean> recode = (Page<AllRecordBean>)buyerRecord;
        return recode;
    }

    /**
     * 查询追号记录
     * @param bean
     */
    private Map queryZhuiHaoRecord(OrderBean bean) {
        Map map = new TreeMap();
        Calendar now = Calendar.getInstance();
        bean.setEtime(ConcurrentSafeDateUtil.format(now.getTime(), "yyyy-MM-dd"));

        //目前限制为只查询最近2个月的投注记录
//        now.add(Calendar.DAY_OF_MONTH, -62); //近62天数据
        //限制为只查询最近6个月的投注记录
        now.add(Calendar.MONTH, -6);
        bean.setStime(ConcurrentSafeDateUtil.format(now.getTime(), "yyyy-MM-dd"));
        try {
            Page<ZhuihaoRecordBean> zhRecord = queryUserZhuihaoRecord(bean);
            if (zhRecord == null || zhRecord.size() == 0) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_NODATA));
                bean.setBusiErrDesc("您没有追号记录~");
                if(bean.getSource() >= 4000 && bean.getSource() < 5000)
                {
                    bean.setBusiErrDesc("您没有相关记录~");
                }
            } else {
                map.put("totalRecords", zhRecord.getTotal());
                map.put("totalPages", zhRecord.getPages());
                map.put("pageSize", zhRecord.getPageSize());
                map.put("pageNumber", zhRecord.getPageNum());

                if (null != zhRecord && zhRecord.size() > 0) {
                    List tlist = appendZhuiHaoRecord(zhRecord);
                    map.put("datas",tlist);
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("查询成功");
                } else {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("查询失败");
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(UserErrCode.ERR_EXCEPTION);
            bean.setBusiErrDesc("查询失败");
            logger.info("查询用户投注记录出现异常,用户名=" + bean.getUid(), e);
        }

        return map;
    }

    private List appendZhuiHaoRecord(Page<ZhuihaoRecordBean> zhRecord) throws Exception {
        Map tmap;
        List tlist = new ArrayList();
        if (null != zhRecord && zhRecord.size() > 0) {
            String pattern = "yyyy-MM-dd HH:mm:ss";
            for (ZhuihaoRecordBean record : zhRecord) {
                tmap = new TreeMap();
                tmap.put("gid",record.getGid()+"");
                tmap.put("logoUrl",getLogoUrl(record.getGid())+"");
                tmap.put("zhid",record.getZhuihaoId()+"");
                tmap.put("pnums",record.getTotalPeriod()+"");
                tmap.put("finish",record.getFinishFlag()+"");
                tmap.put("tmoney",decimalFormat.format(record.getTotalMoney())+"");
                tmap.put("adddate",ConcurrentSafeDateUtil.format(record.getAddDate(), pattern)+"");
                tmap.put("success",record.getSuccessPeriod()+"");
                tmap.put("bonus",decimalFormat.format(record.getTotalBonus())+"");
                tmap.put("casts",decimalFormat.format(record.getCastMoney())+"");
                tmap.put("reason",record.getStopReason()+"");
                tmap.put("failure",record.getFailPeriod()+"");
                tmap.put("zhflag",record.getZhFlag()+"");
                tmap.put("zhtype",record.getZhType()+"");
                tlist.add(tmap);
            }
        }
        return tlist;
    }

    /**
     * 查询用户追号或套餐记录.
     * @param bean
     * @return
     */
    private Page<ZhuihaoRecordBean> queryUserZhuihaoRecord(OrderBean bean) {
        // 停售期间只有安卓/iOS/WP客户端显示追号记录
        int source = bean.getSource();
        if (source < 1000 || source >= 5000) {
            return null;
        }
        PageHelper.startPage(bean.getPn(), bean.getPs());
        List<ZhuihaoRecordBean> zhRecord = vZhuiHaoMapper.queryZhuihaoRecord(bean.getStime(),bean.getEtime(),bean.getUid(),bean.getQtype(),bean.getNewValue());
        Page<ZhuihaoRecordBean> recode = (Page<ZhuihaoRecordBean>)zhRecord;
        return recode;
    }

    /**
     * 查询用户购彩记录
     * @param bean
     */
    private Map queryGoucaiRecord(OrderBean bean) {
        Map map = new TreeMap();
        Calendar now = Calendar.getInstance();
        bean.setEtime(ConcurrentSafeDateUtil.format(now.getTime(), "yyyy-MM-dd"));
        //目前限制为只查询最近3个月的投注记录
        now.add(Calendar.MONTH, -3);
        bean.setStime(ConcurrentSafeDateUtil.format(now.getTime(), "yyyy-MM-dd"));
        String gidCondition = getGidCondition(bean.getGid());
        if (null == gidCondition) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDRE_UNSUPPORT_LOTTERYTYPE));
            bean.setBusiErrDesc("不支持的彩种");
            return null;
        }

        try {
            Page<BetRecordBean> buyRecord = queryUserBetRecord(bean,gidCondition);
            if (null == buyRecord || 0 == buyRecord.size()) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.ORDER_NODATA));
                bean.setBusiErrDesc("您没有投注记录~");
                if(bean.getSource() >= 4000 && bean.getSource() < 5000)
                {
                    bean.setBusiErrDesc("您没有相关记录~");
                }
            } else{
                int grade = queryUserWhitelistGrade(bean);
                map.put("totalRecords", buyRecord.getTotal());
                map.put("totalPages", buyRecord.getPages());
                map.put("pageSize", buyRecord.getPageSize());
                map.put("pageNumber", buyRecord.getPageNum());
                List detailist = getDetailData(buyRecord, grade);
                judgeStatus(map,bean,detailist);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("系统异常，请稍后再试~");
            logger.info("查询用户投注记录出现异常,用户名=" + bean.getUid(), e);
        }
        return map;
    }

    private void judgeStatus(Map map, OrderBean bean, List detailist) {
        if (null != detailist && detailist.size() > 0) {
            map.put("datas", detailist);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("查询成功");
        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
        }
    }

    private List getDetailData(Page<BetRecordBean> recordList, int grade) throws Exception {
        Map tmap;
        List tlist = null;
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if(null != recordList && recordList.size() > 0) {
            tlist = new ArrayList();
            for (BetRecordBean record : recordList) {
                tmap = new TreeMap();
                tmap.put("gid", record.getGid()+"");
                tmap.put("logoUrl", getLogoUrl(record.getGid())+"");
                tmap.put("pid", record.getPid()+"");
                tmap.put("projid", record.getProjid()+"");
                tmap.put("money", decimalFormat.format(record.getBuyMoney())+"");
                tmap.put("buydate", ConcurrentSafeDateUtil.format(record.getBuyDate(), pattern)+"");
                tmap.put("buyid", record.getBuyid()+"");
                tmap.put("cancel",record.getCancelFlag()+"");
                tmap.put("award", record.getAwardFlag()+"");
                tmap.put("return", record.getReturnFlag()+"");
                tmap.put("rmoney", decimalFormat.format(record.getAwardMoney())+"");
                tmap.put("ty", record.getTy()+"");
                tmap.put("istate", record.getBuyState()+"");
                tmap.put("icast", record.getCastFlag()+"");
                tmap.put("status",record.getStateDesc()+"");
                tmap.put("shareGod", record.getShareGod()+"");
                tmap.put("state", getState(record, grade, pattern)+"");
                tmap.put("iend", ConcurrentSafeDateUtil.format(record.getEndtime(), pattern)+"");
                tlist.add(tmap);
            }
        }
        return tlist;
    }

    private String getState(BetRecordBean record, int grade, String pattern) throws ParseException {
        int award = record.getAwardFlag();// 计奖标志（0 未计奖 1 正在计奖 2 已计奖)
        int istate = record.getBuyState();// 状态(-1未支付 0 禁止认购 1 认购中,2 已满员 3过期未满撤销 4主动撤销 5已出票 6 已派奖)
        int icast = record.getCastFlag();// 出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）
        int ireturn = record.getReturnFlag();// 是否派奖（0 未派奖 1 正在派 2 已派奖）
        String gid = record.getGid();// 游戏编号
        String jindu = record.getJindu();// 进度
        int ty = record.getTy();// 购买类型	0 自购 1发起合买 2 合买跟单
        String status = record.getStateDesc();
        double rmoney = record.getAwardMoney();// 中奖金额

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

        String state="";
        if (rmoney == 0) {
            if ("未中奖".equals(status)) {
                state="未中奖";
            } else if ("未结算".equals(status)) {
                if (ty != 0 && !"100".equals(jindu)) {// 合买&进度<100
                    state = "认购中(" + jindu + "%)";
                } else {
                    String endtimes = ConcurrentSafeDateUtil.format(record.getEndtime(), pattern);// 截止时间
                    if ("01".equals(gid) || "07".equals(gid)) {
                        state = ConcurrentSafeDateUtil.convertTimeToChinese(endtimes) + " 21:45开奖";
                    } else if ("03".equals(gid)) {
                        state = ConcurrentSafeDateUtil.convertTimeToChinese(endtimes) + " 21:15开奖";
                    } else if ("50".equals(gid) || "51".equals(gid)|| "52".equals(gid) || "53".equals(gid)) {
                        state = ConcurrentSafeDateUtil.convertTimeToChinese(endtimes) + " 20:40开奖";
                    } else {
                        switch (isflg) {
                            case 1:
                                state="已撤单";
                                break;
                            case 2:
                                state="出票中";
                                break;
                            case 3:
                                state="等待出票";
                                break;
                            case 5:
                                state="出票成功";
                                break;
                            case 6:// 已开奖
                                state = "已开奖";
                                break;
                            case 7:// 已计奖
                                state = "已计奖";
                                break;
                            case 8:// 派奖中
                                state = "派奖中";
                                break;
                            default:
                                state = "等待开奖";
                                break;
                        }
                    }

                }
            } else if (istate == -1) {
                state = status;
            }else {
                state = "已撤单";
            }
        }else {
            state=status;	//已中奖
        }
        if (grade >= 1) {
            if ("本人撤单".equals(state) || "系统撤单".equals(state) || "已撤单".equals(state)) {
                state = "约单失败";
            } else if ("出票成功".equals(state) || "已开奖".equals(state) || "已计奖".equals(state) || "派奖中".equals(state)) {
                state = "约单成功";
            } else if ("等待开奖".equals(state) || "出票中".equals(state) || "等待出票".equals(state)) {
                state = "约单中";
            }
        }
        return state;
    }


    /**
     * 查询用户白名单等级
     * @param bean
     * @return
     */
    private int queryUserWhitelistGrade(OrderBean bean) {
        int grade = 0;
        BaseReq<UserBean> req = new BaseReq<>(SysCodeConstant.ORDERCENTER);
        UserBean userBean = new UserBean();
        userBean.setUid(bean.getUid());
        req.setData(userBean);
        UserPersonalInfoResq rsp = userInterface.getUserWhitelistGrade(req);
        if(null != rsp && null != rsp.getData() && !StringUtils.isEmpty(rsp.getData().getWhitelistGrade())){
            grade = Integer.parseInt(rsp.getData().getWhitelistGrade());
        }
        return grade;
    }

    private String getGidCondition(String gid) {
        StringBuilder gids = new StringBuilder();
        gids.append(" and (");
        if("800".equals(gid)) {	//老足彩  and(cgameid=80 or cgameid=81 or cgameid=82 or cgameid=83 );
            appendGids(gids, "80", true);
            appendGids(gids, "81", false);
            appendGids(gids, "82", false);
            appendGids(gids, "83", false);
        } else if ("850".equals(gid)) {	//足球单场
            appendGids(gids, "84", true);
            appendGids(gids, "85", false);
            appendGids(gids, "86", false);
            appendGids(gids, "87", false);
            appendGids(gids, "88", false);
            appendGids(gids, "89", false);
        } else if ("940".equals(gid)) {	//竞彩篮球
            appendGids(gids, "94", true);
            appendGids(gids, "95", false);
            appendGids(gids, "96", false);
            appendGids(gids, "97", false);
            appendGids(gids, "71", false);
        } else if ("900".equals(gid)) {	//竞彩足球
            appendGids(gids, "90", true);
            appendGids(gids, "91", false);
            appendGids(gids, "92", false);
            appendGids(gids, "93", false);
            appendGids(gids, "70", false);
            appendGids(gids, "72", false);
            appendGids(gids, "98", false);
            appendGids(gids, "99", false);
        } else {
            if (gid.length() > 0) {
                if (GameContains.canNotUse(gid)) {
                    return null;
                } else {
                    appendGids(gids, gid, true);
                }
            } else {
                return "";
            }
        }
        gids.append(")");
        return gids.toString();
    }

    private void appendGids(StringBuilder gids, String gid, boolean isFirst) {
        if (!isFirst) {
            gids.append(" or ");
        }
        gids.append("cgameid='");
        gids.append(gid);
        gids.append("'");
    }

    /**
     * 查询用户购彩记录
     * @param bean
     * @param gidCondition
     * @return
     * @throws Exception
     */
    public Page<BetRecordBean> queryUserBetRecord(OrderBean bean, String gidCondition) throws Exception {
        // 停售期间只有安卓/iOS/WP客户端显示投注记录
        int source = bean.getSource();
        if (source < 1000 || source >= 5000) {
            return null;
        }
        PageHelper.startPage(bean.getPn(), bean.getPs());
        List<BetRecordBean> buyRecode = proj_projBuyMapper.queryBuyByLotid(bean.getStime(),bean.getEtime(),bean.getUid(),bean.getAid(),bean.getRid(),bean.getNewValue(),bean.getTid(),gidCondition);
        Page<BetRecordBean> recode = (Page<BetRecordBean>)buyRecode;
        return recode;
    }
    
    /**
     * 查询未开奖订单数
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public int queryUserUnbeginNum(BaseBean bean) {
    	int count = vProjBuyerAppMapper.countUnbeginNum(bean.getUid());
    	return count;
    }

    @Override
    public NewTicketDetailDTO queryLsDetail(OrderBean bean) {
        NewTicketDetailDTO newTicketDetailDTO = new NewTicketDetailDTO();
        List<NewTicketDetailPojo> ticketDetailPojoList = newTicketDetailMapper.getTicketDetail(bean.getHid());
        if (ticketDetailPojoList == null||ticketDetailPojoList.size()==0) {
            bean.setBusiErrDesc("暂未出票,请稍后查看");
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            List<NewTicketDetailPojo> tem = new ArrayList<>();
            newTicketDetailDTO.setTickets(tem);
            return newTicketDetailDTO;
        }
        Integer total=0;
        for (NewTicketDetailPojo newTicketDetailPojo : ticketDetailPojoList) {
            int per = StringUtil.isEmpty(newTicketDetailPojo.getLsmoney()) ? 0 : Integer.valueOf(newTicketDetailPojo.getLsmoney());
            newTicketDetailPojo.setLsmoney(per == 0 ? "" : (per + ""));
            total += per;
        }
        newTicketDetailDTO.setTickets(ticketDetailPojoList);
        newTicketDetailDTO.setTotalLsBous(total == 0 ? "" : (total + ""));
        bean.setBusiErrCode(0);
        bean.setBusiErrDesc("查询乐善中奖明细成功");
        return newTicketDetailDTO;
    }
}

