package com.caiyi.lottery.tradesystem.ordercenter.service.impl;

import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.dao.*;
import com.caiyi.lottery.tradesystem.ordercenter.service.GodShareService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.*;
import order.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trade.constants.TradeConstants;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.*;
import static com.caiyi.lottery.tradesystem.util.DateTimeUtil.isToday;
import static com.caiyi.lottery.tradesystem.util.SStringUtils.getmatchCnt;


/**
 * Created by tiankun on 2017/12/27.
 * 神单
 */
@Service
@Slf4j
public class GodShareServiceImpl implements GodShareService {

    private static String factor = "000000000000_";

    @Autowired
    private ShareListMapper shareListMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ShareUserListMapper shareUserListMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShareUserDetailMapper shareUserDetailMapper;
    @Autowired
    private FollowlistMapper followlistMapper;
    @Autowired
    private ShareUserStatMapper shareUserStatMapper;
    @Autowired
    private ShareUserList_ShareUserDetailMapper shareUserList_shareUserDetailMapper;
    @Autowired
    private ProjMapper projMapper;
    @Autowired
    private ProjTaskMapper projTaskMapper;
    @Autowired
    private Tb_share_sharelist_followlistMapper shareAndFollowListMapller;
    @Autowired
    TbUserFollowlistMapper tbUserFollowlistMapper;

    /**
     * 查看大神其余神单
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public List<HashMap> queryOtherItem(OrderBean bean) throws Exception {
        String hid = bean.getHid();
        // 查询方案的基本信息
        ShareListPojo shareListPojo = shareListMapper.queryShareProjStatus(hid);
        log.info("查询该神单的方案编号,projid=" + hid + " 分享人,nickid=" + shareListPojo.getNickid());
        List<HashMap> list = new ArrayList<>();
        if (shareListPojo != null) {
            String nickid = shareListPojo.getNickid();
            List<ShareListPojo> slpListPojo = new ArrayList<>();
            if (bean.getMtype() == 4) {// H5传此参数,代表显示满额的单子
                slpListPojo = shareListMapper.queryOtherItemAll(nickid, hid);
            } else {
                slpListPojo = shareListMapper.queryOtherItem(nickid, hid);
            }
            log.info("查询出其他神单共 " + slpListPojo.size() + " 条");
            if (slpListPojo != null && !slpListPojo.isEmpty()) {
                for (int i = 0; i < slpListPojo.size(); i++) {
                    ShareListPojo Pojo = slpListPojo.get(i);
                    HashMap shareItem = organizeShareItem(Pojo);
                    list.add(shareItem);
                }
            }
        }
        return list;
    }

    /**
     * 神单详情信息
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public GodShareDetailDTO godShareDetail(OrderBean bean) throws Exception {
        GodShareDetailDTO dto = new GodShareDetailDTO();
        if (StringUtil.isEmpty(bean.getHid())) {
            bean.setBusiErrCode(Integer.valueOf(ORDER_FANGAN_NOEMPTY));
            bean.setBusiErrDesc("方案详情编号不可为空");
            return dto;
        }
        ShareListPojo pojo = shareListMapper.queryShareProjStatus(bean.getHid());
        if (pojo != null) {
            log.info("根据方案编号查询彩种状态,方案编号projid= " + bean.getHid());
            ItemDetailDTO itemDetailDTO = appendGodItemDetail(pojo, bean);
            GodDetailDTO godDetailDTO = appendGodData(pojo, bean);
            ResultDTO resultDTO = appendGodItemResult(pojo, bean);
            dto.setItemdetail(itemDetailDTO);
            dto.setGodDetail(godDetailDTO);
            dto.setResult(resultDTO);
        } else {
            bean.setBusiErrCode(Integer.valueOf(ORDER_FANGAN_NOEXIST));
            bean.setBusiErrDesc("未查询到神单信息");
            log.info("未查询到神单信息,方案编号:" + bean.getHid() + " 用户名:" + bean.getUid());
        }
        return dto;
    }


    @Override
    public HashMap<String, Object> godShareItem(OrderBean bean) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        List<HashMap<String, Object>> list1 = appendGodRankBanner(bean);
        ArrayList<HashMap<String, Object>> list3 = appendGodShareList(bean);
        map.put("bannerlist", list1);
        // List<HashMap<String, Object>> list2 = appendGodProfitRank(bean);
        // map.put("godlist", list2); 老版竞彩大神页面不需要返回godlist
        map.put("sharelist", list3);
        return map;
    }

    // 组织分享列表数据
    private HashMap organizeShareItem(ShareListPojo pojo) throws Exception {
        HashMap map = new HashMap();
        if (pojo != null) {
            String nickid = pojo.getNickid();
            String encryted = CaiyiEncrypt.encryptStr(nickid).replaceAll("\\+", "\\*");
            map.put("realUid", factor + encryted);
            String projid = pojo.getProjid();
            map.put("projid", projid);
            Date endtime = pojo.getEndtime();//方案截止时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endtime);
            calendar.add(Calendar.MINUTE, -5);// 跟投时间比方案截止时间提前5分钟
            Calendar now = Calendar.getInstance();
            int nowDate = now.get(Calendar.DAY_OF_YEAR);
            int endtimeDate = calendar.get(Calendar.DAY_OF_YEAR);
            if (nowDate == endtimeDate) {// 如果是当天只显示时和分
                Date realEndDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String realEndTime = sdf.format(realEndDate);
                map.put("endtime", realEndTime);
            } else {
                Date realEndDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                String realEndTime = sdf.format(realEndDate);
                map.put("endtime", realEndTime);
            }
            String tmoney = pojo.getTmoney();
            map.put("tmoney", tmoney);
            int mulity = pojo.getMulity();
            int averageMoney = Integer.parseInt(tmoney) / mulity;
            Integer wrate = pojo.getWrate();
            map.put("wrate", wrate + "%");
            String matches = pojo.getMatches();
            String[] matchesArr = matches.split(",");
            int matchNum = 0;
            for (String match : matchesArr) {
                if (StringUtil.isEmpty(match)) {
                    continue;
                }
                matchNum = matchNum + 1;
            }
            map.put("matchNum", String.valueOf(matchNum));
            String codes = pojo.getCodes();
            String yhfile = codes.replace("_n.txt", "_yh.xml");
            String xmlpath = "/opt/export/data/pupload/" + pojo.getGameid() + "/" + pojo.getPeriod() + "/"
                    + yhfile;
            double tm = 0.0;
            if (!StringUtil.isEmpty(String.valueOf(tmoney))) {
                tm = Double.valueOf(tmoney);
            }
            boolean b = SStringUtils.endwith(codes, "txt");
            boolean yczsFlag = "15".equals(pojo.getExtendtype());
            String yhmoney = averageMoney + "";
            if (b && tm > 100 && !yczsFlag) {
                int itemcnt = getmatchCnt(xmlpath);
                if (itemcnt != 0 && itemcnt * 10 < tm) {
                    yhmoney = itemcnt * 10 + "";
                }
            }
            map.put("averageMoney", String.valueOf(averageMoney));
            map.put("yhMoney", yhmoney);
            String yczs = "0";
            String extendtype = String.valueOf(pojo.getExtendtype());
            if ("15".equals(extendtype)) {
                yczs = "1";
            }
            map.put("yczs", yczs);
            String guoguan = pojo.getGuoguan();
            guoguan = guoguan.replace("*", "串");
            map.put("guoguan", guoguan);
            Integer usernum = pojo.getUsernum();
            map.put("usernum", String.valueOf(usernum));
            nickid = CheckUtil.checkNum(nickid);
            map.put("nickid", nickid);
        }
        return map;
    }

    // 添加神单基础数据
    private ItemDetailDTO appendGodItemDetail(ShareListPojo pojo, OrderBean bean) throws Exception {
        ItemDetailDTO dto = new ItemDetailDTO();
        if (pojo != null) {
            String gameid = pojo.getGameid();
            dto.setGameid(gameid);
            bean.setGid(gameid);
            dto.setProjid(bean.getHid());
            dto.setWrate(pojo.getWrate() + "%");
            Date endtime = pojo.getEndtime();//方案截止时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endtime);
            calendar.add(Calendar.MINUTE, -5);// 跟投时间比方案截止时间提前5分钟
            Calendar now = Calendar.getInstance();
            int nowDate = now.get(Calendar.DAY_OF_YEAR);
            int endtimeDate = calendar.get(Calendar.DAY_OF_YEAR);
            if (nowDate == endtimeDate) {// 如果是当天只显示时和分
                Date realEndDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String realEndTime = sdf.format(realEndDate);
                dto.setEndtime(realEndTime);
            } else {
                Date realEndDate = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                String realEndTime = sdf.format(realEndDate);
                dto.setEndtime(realEndTime);
            }
            if (endtime.compareTo(now.getTime()) < 0) {
                dto.setProjState("0");
            } else {
                dto.setProjState("1");
            }
            String tmoney = pojo.getTmoney();
            dto.setTmoney(tmoney);
            int limitMoney = Integer.parseInt(tmoney) * 10;
            dto.setLimitMoney(String.valueOf(limitMoney));
            Integer followmoney = pojo.getFollowmoney();
            int progress = followmoney * 100 / limitMoney;
            if (progress > 100) {
                dto.setProgress("100");
            } else {
                dto.setProgress(String.valueOf(progress));
            }
            Integer mulity = pojo.getMulity();
            int averageMoney = Integer.parseInt(tmoney) / mulity;
            // 优化初始投资金额
            String matchs = pojo.getMatches().replaceAll(",", "");
            int length = matchs.split(",").length;
            boolean b = SStringUtils.endwith(pojo.getCodes(), "txt");
            double tm = 0.0;
            String yhfile = pojo.getCodes().replace("_n.txt", "_yh.xml");
            String xmlpath = "/opt/export/data/pupload/" + gameid + "/" + pojo.getPeriod() + "/" + yhfile;
            length = getmatchCnt(xmlpath);
            String yhMoney = averageMoney + "";
            boolean yczsFlag = "15".equals(pojo.getExtendtype());
            if (!StringUtils.isEmpty(tmoney)) {
                tm = Double.valueOf(tmoney);
            }
            if (b && tm > 100 && !yczsFlag) {
                if (length != 0 && length * 10 < tm) {
                    yhMoney = length * 10 + "";
                }
            }
            dto.setAverageMoney(String.valueOf(averageMoney));
            dto.setYhMoney(yhMoney);
            dto.setUserNum(String.valueOf(pojo.getUsernum()));
            dto.setFinish(String.valueOf(pojo.getFinish()));
        }
        return dto;
    }

    // 增加大神数据
    private GodDetailDTO appendGodData(ShareListPojo pojo, OrderBean bean) throws Exception {
        GodDetailDTO dto = new GodDetailDTO();
        if (pojo != null) {
            String nickid = pojo.getNickid();
            if (nickid.equals(bean.getUid()) || isAgent(bean.getUid()) || bean.getXzflag() == 1) {
                dto.setIsOwner("1");//1==true
            } else {
                dto.setIsOwner("0");//0==false
            }
            String encrypted = CaiyiEncrypt.encryptStr(nickid).replaceAll("\\+", "\\*");
            dto.setRealUid(factor + encrypted);
            String checkId = CheckUtil.checkNum(nickid);// 检测用户名是否包含五位数字
            dto.setNickid(checkId);
            // 查询该用户是否是大神
            ShareUserListPojo sulPojo = shareUserListMapper.queryGodStatus(nickid);
            if (sulPojo != null) {
                String usertype = sulPojo.getUsertype();
                if ("1".equals(usertype)) {// 是大神用户,显示大神用户指定类型的命中
                    dto.setIsGod("1");
                    String uptype = sulPojo.getUptype();
                    dto.setUptype(uptype);//几日数据
                    String rank = String.valueOf(sulPojo.getRank());
                    dto.setRank(rank);//排名
                    List<ShareUserDetailPojo> sudListPojo = shareUserDetailMapper.queryGodHitData(nickid, uptype);
                    if (sudListPojo.size() > 0) {//测试pojo数据类型
                        ShareUserDetailPojo sudPojo = sudListPojo.get(0);
                        String allnum = String.valueOf(sudPojo.getAllnum());
                        dto.setAllnum(allnum);
                        String rednum = String.valueOf(sudPojo.getRednum());
                        dto.setRednum(rednum);
                        String shootrate = sudPojo.getShootrate();
                        dto.setShootrate(shootrate);
                        String returnrate = sudPojo.getReturnrate();
                        dto.setReturnrate(returnrate);
                        String buymoney = String.valueOf(sudPojo.getBuymoney());
                        dto.setBuymoney(buymoney);
                        String winmoney = String.valueOf(sudPojo.getWinmoney());
                        dto.setWinmoney(winmoney);
                        DecimalFormat df = new DecimalFormat("######0.00");
                        int userNum = shareListMapper.shareUserNumByDay("7");
                        if (userNum <= 1) {
                            dto.setWinrate("-");
                        } else {
                            double winrate = (userNum - Double.parseDouble(String.valueOf(rank))) / (userNum - 1) * 100;
                            String winrateStr = df.format(winrate);
                            if ("100.00".equals(winrateStr)) {
                                winrateStr = "100";
                            }
                            dto.setWinrate(winrateStr + "%");
                        }
                        String imgUrlTmp = getUserPhoto(nickid);
                        String newImgUrl = "";
                        // 相对地址时
                        if (!StringUtil.isEmpty(imgUrlTmp) && !imgUrlTmp.startsWith("http://")) {
                            newImgUrl = imgUrlTmp;
                        }
                        // 绝对地址时
                        if (imgUrlTmp != null && imgUrlTmp.startsWith("http://")) {
                            newImgUrl = imgUrlTmp.substring(22);
                        }
                        dto.setNewimgUrl(newImgUrl);
                    }
                } else {
                    dto.setIsGod("0");
                    String imgUrlTmp = getUserPhoto(nickid);
                    String newImgUrl = "";
                    // 相对地址时
                    if (!StringUtil.isEmpty(imgUrlTmp) && !imgUrlTmp.startsWith("http://")) {
                        newImgUrl = imgUrlTmp;
                    }
                    // 绝对地址时
                    if (imgUrlTmp != null && imgUrlTmp.startsWith("http://")) {
                        newImgUrl = imgUrlTmp.substring(22);
                    }
                    dto.setNewimgUrl(newImgUrl);
                }

            } else {
                dto.setIsGod("0");
                String imgUrlTmp = getUserPhoto(nickid);
                String newImgUrl = "";
                // 相对地址时
                if (!StringUtil.isEmpty(imgUrlTmp) && !imgUrlTmp.startsWith("http://")) {
                    newImgUrl = imgUrlTmp;
                }
                // 绝对地址时
                if (imgUrlTmp != null && imgUrlTmp.startsWith("http://")) {
                    newImgUrl = imgUrlTmp.substring(22);
                }
                dto.setNewimgUrl(newImgUrl);
            }
            // ========================================================
            // 新版本数据
            List<ShareUserStatPojo> list = queryGodData(nickid, "30");
            if (list != null && list.size() > 0) {
                ShareUserStatPojo susPojo = list.get(0);
                String returnRate = susPojo.getCreturnrate();
                int iReturnRate = Integer.parseInt(returnRate.substring(0, returnRate.length() - 1));
                if (iReturnRate > 100) {
                    dto.setShowData("1");
                    String winmoney30 = String.valueOf(susPojo.getIwinmoney());
                    dto.setWinmoney30(winmoney30);
                    String followUser30 = String.valueOf(susPojo.getIfollowusers());
                    dto.setFollowUser30(followUser30);
                    String followMoney30 = String.valueOf(susPojo.getIfollowmoney());
                    dto.setFollowMoney30(followMoney30);
                    String combo = String.valueOf(susPojo.getIcontinurednum());
                    dto.setCombo(combo);
                    buildAllPeriodData(dto, nickid);
                } else {
                    dto.setShowData("0");
                }
            } else {
                dto.setShowData("0");
            }

        }
        return dto;
    }

    //添加神单完成数据
    private ResultDTO appendGodItemResult(ShareListPojo pojo, OrderBean bean) throws Exception {
        ResultDTO dto = new ResultDTO();
        if (pojo != null) {
            Double bonus = pojo.getBonus();
            if (bonus > 0) {
                dto.setFlag("1");
                dto.setBonus(String.valueOf(bonus));
                FollowlistPojo flPojo = followlistMapper.queryFollowResult(bean.getHid());
                if (flPojo != null) {
                    String followBonus = String.valueOf(flPojo.getBonus());
                    String reward = String.valueOf(flPojo.getReward());
                    dto.setFollowBonus(followBonus);
                    dto.setReward(reward);
                }
            } else {
                dto.setFlag("0");
            }
        }
        return dto;
    }

    /**
     * 我的详情/大神详情
     *
     * @param bean
     */
    public HashMap<String, Object> shareUserDetailsNew(OrderBean bean) throws Exception {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        //newvalue  表示查询详情人用户名
        log.info("我的详情 uid==" + bean.getUid() + ",newvalue==" + bean.getNewValue()+",qtype="+bean.getQtype());
        if (bean.getNewValue().length() >= 37) {
            String dencryptStr = AlipayUtil.dencryptStr(bean.getNewValue());
            bean.setNewValue(dencryptStr);
        }
        HashMap<String, Object> map1 = appendUserInfo(bean);
        map.put("userinfo", map1);
        HashMap<String, Object> map2 = appendGodList(bean);
        map.put("godlist", map2);
        List<HashMap<String, Object>> rowlist = appendShareProjRecordNew(bean);
        map.put("datas", rowlist);
        //返回分页结果给前端
        map.put("totalRecords", bean.getTr());
        map.put("totalPages", bean.getTp()==0?1:bean.getTp());
        map.put("pageSize", bean.getPs());
        map.put("pageNumber", bean.getPn());
        bean.setBusiErrCode(0);
        bean.setBusiErrDesc("查询成功");
        return map;
    }

    public HashMap<String, Object> appendUserInfo(OrderBean bean) throws Exception {
        HashMap<String, Object> userinfoMap = new HashMap<>();
        String isowner = isOwner(bean);
        ShareGodUserPojo godUser = new ShareGodUserPojo();
        godUser.setNickid(CheckUtil.checkNum(bean.getNewValue()));
        //userinfo --> 用户名，用户头像，累计打赏金额
        godUser.setUserphoto(getUserPhotoSetCache(bean.getNewValue()));
        godUser.setRewardall(getProjRewardall(bean.getNewValue()));
        log.info("拼接用户信息  uid==" + bean.getNewValue());
        userinfoMap.put("nickid", godUser.getNickid());
        String userPhotoTmp = godUser.getUserphoto();
        String userPhoto = "";
        String newUserPhoto = "";
        //相对地址时
        if (!StringUtil.isEmpty(userPhotoTmp) && !userPhotoTmp.startsWith("http://")) {
            userPhoto = "http://mobile.9188.com" + userPhotoTmp;
            newUserPhoto = userPhotoTmp;
        }
        //绝对地址时
        if (userPhotoTmp != null && userPhotoTmp.startsWith("http://")) {
            userPhoto = userPhotoTmp;
            newUserPhoto = userPhotoTmp.substring(22);
        }
        userinfoMap.put("newuserphoto", newUserPhoto);
        userinfoMap.put("rewardall", String.valueOf(godUser.getRewardall()));
        userinfoMap.put("isowner", isowner);
        return userinfoMap;
    }

    public HashMap<String, Object> appendGodList(OrderBean bean) throws Exception {
        HashMap<String, Object> godMap = new HashMap<>();
        godMap.put("realUid", AlipayUtil.encryptStr(bean.getNewValue()));
        List<ShareUserStatPojo> jrs4 = queryGodData(bean.getNewValue(), "30");
        if (jrs4 != null && jrs4.size() > 0) {
            ShareUserStatPojo pojo = jrs4.get(0);
            String returnRate = pojo.getCreturnrate();
            int iReturnRate = Integer.parseInt(returnRate.substring(0, returnRate.length() - 1));
            if (iReturnRate > 100) {
                godMap.put("showData", "1");
                String winmoney30 = String.valueOf(pojo.getIwinmoney());
                godMap.put("winmoney30", winmoney30);
                String followUser30 = String.valueOf(pojo.getIfollowusers());
                godMap.put("follownum30", followUser30);
                String followMoney30 = String.valueOf(pojo.getIfollowmoney());
                godMap.put("followmoney30", followMoney30);
                String combo = String.valueOf(pojo.getIcontinurednum());
                godMap.put("combo", combo);
                buildAllPeriodData(godMap, bean.getNewValue());
            } else {
                godMap.put("showData", "0");
            }
        } else {
            godMap.put("showData", "0");
        }
        return godMap;
    }

    public List<HashMap<String, Object>> appendShareProjRecordNew(OrderBean bean) throws Exception {
        List<HashMap<String, Object>> list = new ArrayList<>();
        String uid = bean.getUid();
        String newValue = bean.getNewValue();
        String qtype = bean.getQtype();
        boolean isSelf = false;
        List<ShareGodProjPojo> recordList = null;
        //分页
        int pn = bean.getPn();
        int ps = bean.getPs();
        PageHelper.startPage(pn, ps);
        if (!uid.equals(newValue) && "0".equals(qtype)) {
            List<ShareListPojo> listPojos = shareListMapper.queryShareGodProj_0(newValue, qtype);
            PageInfo<ShareListPojo> pageInfo = new PageInfo<>(listPojos);
            Page<List<ShareListPojo>> page = new Page<>(ps, pn, pageInfo.getPages(),
                    pageInfo.getTotal(), listPojos);
            bean.setTr((int) pageInfo.getTotal());
            bean.setTp(pageInfo.getPages());
            recordList = queryShareGodProjList(page.getDatas());
        } else {
            List<ShareListPojo> listPojos = shareListMapper.queryShareGodProj(newValue, qtype);
            PageInfo<ShareListPojo> pageInfo = new PageInfo<>(listPojos);
            Page<List<ShareListPojo>> page = new Page<>(ps, pn, pageInfo.getPages(),
                    pageInfo.getTotal(), listPojos);
            bean.setTr((int) pageInfo.getTotal());
            bean.setTp(pageInfo.getPages());
            recordList = queryShareGodProjList(page.getDatas());
        }
        if (recordList != null && recordList.size() > 0) {
            //双层list分页插件不能识别出总页数和总条数
            for (ShareGodProjPojo shareGodUser : recordList) {
                HashMap<String, Object> rowMap = new HashMap<>();
                rowMap.put("projid", shareGodUser.getProjid());
                rowMap.put("realuid", AlipayUtil.encryptStr(shareGodUser.getNickid()));
                rowMap.put("nickid", CheckUtil.checkNum(shareGodUser.getNickid()));
                rowMap.put("tmoney", String.valueOf(shareGodUser.getTmoney()));
                rowMap.put("mintmoney", String.valueOf(shareGodUser.getMintmoney()));
                String yhmoney = (int) shareGodUser.getMintmoney() + "";
                String yhtmoney = shareGodUser.getYhmoney();
                if (!StringUtil.isEmpty(yhtmoney)) {
                    if (!"0".equals(yhtmoney)) {
                        if (Double.valueOf(yhtmoney) < shareGodUser.getTmoney()) {
                            yhmoney = shareGodUser.getYhmoney();
                        }
                    }
                }
                rowMap.put("yhMoney", yhmoney);
                rowMap.put("wrate", Integer.valueOf((int) (shareGodUser.getWrate())) + "%");
                rowMap.put("matchnum", String.valueOf(shareGodUser.getMatchnum()));
                rowMap.put("guoguan", shareGodUser.getGuoguan());
                rowMap.put("follownums", String.valueOf(shareGodUser.getFollownums()));
                rowMap.put("endtime", shareGodUser.getEndtime());
                if (shareGodUser.getTmoney() < shareGodUser.getBonus()) {
                    rowMap.put("bonus", shareGodUser.getBonus());
                    rowMap.put("flag", "1");
                } else {
                    rowMap.put("bonus", "0");
                    rowMap.put("flag", "0");
                }
                if (!StringUtil.isEmpty(shareGodUser.getYczs())) {
                    rowMap.put("yczs", "1");
                }
                rowMap.put("followrate", shareGodUser.getFollowrate());
                list.add(rowMap);
            }
        }
        return list;
    }

    public List<ShareGodProjPojo> queryShareGodProjListByCache(List<ShareListPojo> list, OrderBean bean) {
        List<ShareGodProjPojo> recordList = null;
        if (list != null && list.size() > 0) {
            recordList = new ArrayList<>();
            for (ShareListPojo pojo : list) {
                ShareGodProjPojo record = new ShareGodProjPojo();
                record.setNickid(CheckUtil.checkNum(pojo.getNickid()));
                record.setProjid(pojo.getProjid());
                record.setUserphoto(getUserPhotoNoCache(pojo.getNickid()));
                record.setTmoney(Double.valueOf(pojo.getTmoney()));
                record.setWrate(Double.valueOf(pojo.getWrate()));
                String matchs = pojo.getMatches().replaceFirst(",", "");
                int length = matchs.split(",").length;
                record.setMatchnum(length);//比赛场次
                //计算起投金额
                String minitmoney = String.valueOf(pojo.getMintmoney());
                boolean b = SStringUtils.endwith(pojo.getCodes(), "txt");
                String yhfile = pojo.getCodes().replace("_n.txt", "_yh.xml");
                String xmlpath = "/opt/export/data/pupload/" + pojo.getGameid() + "/" + pojo.getPeriod() + "/" + yhfile;
                double tmoney = Double.valueOf(pojo.getTmoney());
                String yhmoney = "";
                boolean yczsFlag = "15".equals(pojo.getExtendtype());
                if (b && tmoney > 100 && !yczsFlag) {
                    yhmoney = SStringUtils.getmatchCnt(xmlpath) * 10 + "";
                }
                record.setYhmoney(yhmoney);
                record.setMintmoney(Double.valueOf(minitmoney));
                String guoguan = pojo.getGuoguan();
                String substring = "";
                if (guoguan.length() > 1) {
                    substring = guoguan.substring(0, guoguan.length() - 1);
                } else {
                    substring = guoguan;
                }
                //过关方式显示排序 从小到大
                substring = SStringUtils.sortedGuoGuan(substring);
                record.setGuoguan(substring);
                record.setFollownums(pojo.getUsernum());
                //发单数，红单数取缓存
                CacheBean cacheBean = new CacheBean();
                cacheBean.setKey(pojo.getNickid() + "_god_detail_cache");
                Object object = redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.ORDERCENTER);
                if (object != null) {
                    Map<String, String> map = (Map<String, String>) object;
                    String projallnum = map.get("projallnum");
                    String projrednum = map.get("projrednum");
                    if (StringUtil.isEmpty(projallnum)) {
                        record.setProjallnum(0);
                    } else {
                        record.setProjallnum(Integer.valueOf(projallnum));
                    }
                    if (StringUtil.isEmpty(projrednum)) {
                        record.setProjrednum(0);
                    } else {
                        record.setProjrednum(Integer.valueOf(projrednum));
                    }
                } else {
                    //查询
                    List<UserListDetailPojo> userProjAllNum = shareUserList_shareUserDetailMapper.getUserProjAllNum(bean.getSort(), pojo.getNickid());
                    if (userProjAllNum != null && userProjAllNum.size() > 0) {
                        UserListDetailPojo detailPojo = userProjAllNum.get(0);
                        if (StringUtil.isEmpty(String.valueOf(detailPojo.getProjallnum()))) {
                            record.setProjallnum(0);
                        } else {
                            record.setProjallnum(detailPojo.getProjallnum());
                        }
                        if (StringUtil.isEmpty(String.valueOf(detailPojo.getProjrednum()))) {
                            record.setProjrednum(0);
                        } else {
                            record.setProjrednum(detailPojo.getProjrednum());
                        }
                    } else {
                        record.setProjallnum(0);
                        record.setProjrednum(0);
                    }
                }
                Date endtime = DateTimeUtil.getBeforeXminTimeToDate(pojo.getEndtime(), 5);
                String time = "";
                if (isToday(endtime)) {//是当日的取 时 分
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    time = sdf.format(endtime);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                    time = sdf.format(endtime);
                }
                record.setEndtime(time);
                String extendtype = String.valueOf(pojo.getExtendtype());
                if (!StringUtil.isEmpty(extendtype) && "15".equals(extendtype)) {
                    record.setYczs("1");
                } else {
                    record.setYczs("0");
                }
                recordList.add(record);
            }
        }
        return recordList;
    }

    public List<ShareGodProjPojo> queryShareGodProjList(List<ShareListPojo> list) {
        List<ShareGodProjPojo> recordList = null;
        if (list != null && list.size() > 0) {
            recordList = new ArrayList<>();
            for (ShareListPojo pojo : list) {
                ShareGodProjPojo record = new ShareGodProjPojo();
                record.setNickid(pojo.getNickid());
                record.setProjid(pojo.getProjid());
                record.setTmoney(Double.valueOf(pojo.getTmoney()));
                record.setWrate(Double.valueOf(pojo.getWrate()));
                String matchs = pojo.getMatches().replaceFirst(",", "");
                int length = matchs.split(",").length;
                record.setMatchnum(length);//比赛场次
                String minitmoney = String.valueOf(pojo.getMintmoney());
                double tm = Double.parseDouble(pojo.getTmoney());
                //起投金额优化
                boolean b = SStringUtils.endwith(pojo.getCodes(), "txt");
                String yhfile = pojo.getCodes().replace("_n.txt", "_yh.xml");
                String xmlpath = "/opt/export/data/pupload/" + pojo.getGameid() + "/" + pojo.getPeriod() + "/" + yhfile;
                String yhmoney = "";
                boolean yczsFlag = "15".equals(pojo.getExtendtype());
                if (b && tm > 100 && !yczsFlag) {
                    yhmoney = SStringUtils.getmatchCnt(xmlpath) * 10 + "";
                }
                record.setYhmoney(yhmoney);
                record.setMintmoney(Double.valueOf(minitmoney));
                String guoguan = pojo.getGuoguan();
                String substring = "";
                if (guoguan.length() > 1) {
                    substring = guoguan.substring(0, guoguan.length() - 1);
                } else {
                    substring = guoguan;
                }
                record.setGuoguan(substring);
                record.setFollownums(pojo.getUsernum());
                record.setBonus(pojo.getBonus());
                //  DateTimeUtil.getBeforeXminTimeToDate(String.valueOf(pojo.getEndtime()),5);
                Date endtime = DateTimeUtil.getBeforeXminTimeToDate(pojo.getEndtime(), 5);
                String time = "";
                if (isToday(endtime)) {//是当日的取 时 分
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    time = sdf.format(endtime);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                    time = sdf.format(endtime);
                }
                record.setEndtime(time);
                if (StringUtil.isEmpty(String.valueOf(pojo.getFollowmoney()))) {
                    record.setFollowmoney(0);
                } else {
                    record.setFollowmoney(pojo.getFollowmoney());
                }
                record.setFollowrate(pojo.getFollowrate());
                String extendtype = String.valueOf(pojo.getExtendtype());
                if (!StringUtil.isEmpty(extendtype) && "15".equals(extendtype)) {
                    record.setYczs("1");
                }
                recordList.add(record);
            }
        }
        return recordList;
    }

    /**
     * 大神 -- 分享单子列表信息
     *
     * @return
     */
    public HashMap<String, Object> queryGodProjListInfo(OrderBean bean) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        log.info("用户查看大神单列表  uid==" + bean.getUid()+",oflag="+bean.getOflag());
        // 1--发单金额（大到小），2--发单金额（小到大），3--起投金额（大到小），4--起投金额（小到大），5--人气（大到小）
        int oflag = bean.getOflag();
        if (oflag < 1 || oflag > 5) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("参数有问题");
            return map;
        }
        // 检测当日数据是否生成，没有生成则查询上一天数据
        String currentDay = checkCurrentDate();
        bean.setSort(currentDay);// 设置查询时间
        List<ShareListPojo> slPojo = new ArrayList<>();
        //分页
        int pn = bean.getPn();
        int ps = bean.getPs();
        PageHelper.startPage(pn, ps);
        if (oflag == 1) {
            slPojo = shareListMapper.query_god_proj_1();
        } else if (oflag == 2) {
            slPojo = shareListMapper.query_god_proj_2();
        } else if (oflag == 3) {
            slPojo = shareListMapper.query_god_proj_3();
        } else if (oflag == 4) {
            slPojo = shareListMapper.query_god_proj_4();
        } else {
            slPojo = shareListMapper.query_god_proj_5();
        }
        PageInfo<ShareListPojo> pageInfo = new PageInfo<>(slPojo);
        Page<List<ShareListPojo>> page = new Page<>(ps, pn, pageInfo.getPages(),
                pageInfo.getTotal(), slPojo);
        bean.setTr((int) pageInfo.getTotal());
        bean.setTp(pageInfo.getPages());
        List<ShareGodProjPojo> recordList = queryShareGodProjListByCache(page.getDatas(), bean);
        if (recordList == null || recordList.size() == 0) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查无大神记录~");
        } else {
            //返回分页信息
            map.put("totalRecords", bean.getTr());
            map.put("totalPages", bean.getTp()==0?1:bean.getTp());
            map.put("pageSize", bean.getPs());
            map.put("pageNumber", bean.getPn());
            List<HashMap<String, Object>> rowlist = appendShareGodProjs(recordList);
            map.put("datas", rowlist);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询成功");
        }
        return map;
    }

    @Override
    public List<ShareGodUserPojo> queryShareUserDataList(OrderBean bean) {
        //flag
        //1-奖金榜，2-人气榜，3-7日回报榜，4-15日回报榜，5-30日回报榜，6-7日命榜中，7-15日命中榜，8-30日命中榜，9-连红榜，10-新秀榜
        //11-可买奖金榜，12-可买人气榜，13-可买7日回报榜，14-可买15日回报榜，15-可买30日回报榜，16-可买7日命榜中，17-可买15日命中榜，18-可买30日命中榜，19-可买连红榜，20-可买新秀榜
        int flag = bean.getFlag();
        //检测当日数据是否生成，没有生成则查询上一天数据
        String currentDay = checkCurrentDateStat();
        bean.setStime(currentDay);
        StringBuilder builder = new StringBuilder();
        List<ShareGodUserPojo> recordList = queryShareUserData(bean);
        if (recordList == null || recordList.size() == 0) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查无榜单记录~");
        } else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询成功");
        }
        return recordList;
    }

    @Override
    public FollowListDTO queryGodFollowList(OrderBean bean) {

        FollowListDTO dto = new FollowListDTO();
        dto.setTotalPages(bean.getTp());
        dto.setPageNumber(bean.getPn());
        dto.setPageSize(bean.getPs());
        if (StringUtil.isEmpty(bean.getNewValue())) {
            bean.setBusiErrCode(2);
            bean.setBusiErrDesc("查询神单跟买人方案不可为空");
            return dto;
        }
        List<UserFollowPojo> followList = queryGodFollowRecord(bean);
        dto.setDatas(followList);
        dto.setTotalPages(0 == bean.getTp()?1:bean.getTp());
        dto.setTotalRecords(bean.getTr());
        //方案的所有跟买金额
        String tmoney = shareListMapper.queryFollowTMoney(bean.getNewValue());
        //方案的所有跟买金额
        ShareListPojo result = shareListMapper.queryFollowData(bean.getNewValue());
        int finish = 0;
        if (result.getBonus() > 0) {
            finish = 1;
        }
        dto.setTMoney(tmoney);
        dto.setFollowNum(result.getUsernum());
        dto.setFinish(finish);

        bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");

        return dto;
    }

    private List queryGodFollowRecord(OrderBean bean) {

        PageHelper.startPage(bean.getPn(), bean.getPs());
        List<UserFollowPojo> pageBean = tbUserFollowlistMapper.queryGodFollowList(bean.getNewValue());
        PageInfo<UserFollowPojo> info = new PageInfo<UserFollowPojo>(pageBean);
        Page<UserFollowPojo> page = new Page(bean.getPs(), bean.getPn(), info.getPages(), info.getTotal(), pageBean);
        List<UserFollowPojo> pageList = (List<UserFollowPojo>) page.getDatas();
        String name = "";
        bean.setTp(page.getTotalPages());
        bean.setTr(page.getTotalRecords().intValue());
        for (UserFollowPojo user : pageList) {
            if (user.getUid().length() > 3) {
                name = user.getUid().substring(0, 3) + "**";
                user.setUid(name);
            } else {
                name = user.getUid() + "**";
                user.setUid(name);
            }
            parsePhoto(user);
        }
        return pageList;
    }

    private void parsePhoto(UserFollowPojo user) {
        String imgUrlTmp = user.getPhoto();
        String newImgUrl = "";
        //相对地址时
        if (!StringUtil.isEmpty(imgUrlTmp) && !imgUrlTmp.startsWith("http://")) {
            newImgUrl = imgUrlTmp;
        }
        //绝对地址时
        if (imgUrlTmp != null && imgUrlTmp.startsWith("http://")) {
            newImgUrl = imgUrlTmp.substring(22);
        }
        user.setNewImgUrl(newImgUrl);
        user.setPhoto("");
    }

    private List<ShareGodUserPojo> queryShareUserData(OrderBean bean) {
        log.info("交易中心-->动态sql查询，stime==" + bean.getStime() + ",flag==" + bean.getFlag());
        List<ShareGodUserPojo> recordList = shareAndFollowListMapller.queryShareUserData(bean.getStime(), bean.getFlag() + "");
        Iterator it = recordList.iterator();
        while (it.hasNext()){
            ShareGodUserPojo share = (ShareGodUserPojo)it.next();
            share.setRealuid(AlipayUtil.encryptStr(share.getNickid()));
            int unfinishnum = share.getUnfinishnum();

            if (bean.getFlag() > 10) {
                if (unfinishnum < 1) {//有可跟买的单子
                    it.remove();
                }
                putPhoto(share, getUserPhotoSetCache(share.getNickid()));
            } else {
                putPhoto(share, getUserPhotoSetCache(share.getNickid()));
            }
            // 用户名连续数字打***
            share.setNickid(CheckUtil.checkNum(share.getNickid()));
        }
        return recordList;
    }

    private void putPhoto(ShareGodUserPojo bean, String userPhototmp) {
        String newUserPhoto = "";
        //相对路径时
        if (!StringUtil.isEmpty(userPhototmp) && !userPhototmp.startsWith("http://")) {
            newUserPhoto = userPhototmp;
        }
        //绝对路径时
        if (userPhototmp != null && userPhototmp.startsWith("http://")) {
            newUserPhoto = userPhototmp.substring(22);
        }
        bean.setNewuserphoto(newUserPhoto);
    }

    public List<HashMap<String, Object>> appendShareGodProjs(List<ShareGodProjPojo> recordList) throws Exception {//大神单
        List<HashMap<String, Object>> rowList = new ArrayList<>();
        if (recordList != null && recordList.size() > 0) {
            for (ShareGodProjPojo shareGodUser : recordList) {
                CacheBean cacheBean = new CacheBean();
                HashMap<String, Object> rowmap = new HashMap<>();
                rowmap.put("projid", shareGodUser.getProjid());
                rowmap.put("nickid", shareGodUser.getNickid());
                String userPhotoTmp = shareGodUser.getUserphoto();
                String userPhoto = "";
                String newUserPhoto = "";
                // 相对地址时
                if (!StringUtil.isEmpty(userPhotoTmp) && !userPhotoTmp.startsWith("http://")) {
                    userPhoto = "http://mobile.9188.com" + userPhotoTmp;
                    newUserPhoto = userPhotoTmp;
                }
                // 绝对地址时
                if (userPhotoTmp != null && userPhotoTmp.startsWith("http://")) {
                    userPhoto = userPhotoTmp;
                    newUserPhoto = userPhotoTmp.substring(22);
                }
                rowmap.put("newuserphoto", newUserPhoto);
                rowmap.put("tmoney", String.valueOf(shareGodUser.getTmoney()));
                rowmap.put("mintmoney", String.valueOf(shareGodUser.getMintmoney()));
                // 优化起投金额
                String yhmoney = (int) shareGodUser.getMintmoney() + "";
                String yhtmoney = shareGodUser.getYhmoney();
                if (!StringUtil.isEmpty(yhtmoney)) {
                    if (!"0".equals(yhtmoney)) {
                        if (Double.valueOf(yhtmoney) < shareGodUser.getTmoney()) {
                            yhmoney = yhtmoney;
                        }
                    }
                }
                rowmap.put("yhMoney", yhmoney);
                // 根据nickid先从缓存中获取是否用户是否存在刷单标记，获取不到，则需要从数据库查询数据计算 ---检查刷单标记
                cacheBean.setKey(shareGodUser.getNickid() + "Buy_Rate");
                String uflag = redisClient.getString(cacheBean, log, SysCodeConstant.ORDERCENTER);
                if (uflag == null || "".equals(uflag)) {
                    double rate = queryBuyRate(shareGodUser.getNickid());
                    uflag = rate > 100 ? "1" : "0";
                    cacheBean.setValue(uflag);
                    cacheBean.setTime(Constants.TIME_DAY);
                    redisClient.setString(cacheBean, log, SysCodeConstant.ORDERCENTER);
                }
                int projallnum = shareGodUser.getProjallnum();
                int projrednum = shareGodUser.getProjrednum();
                double projallnumD = Double.parseDouble(projallnum + "");
                double projrednumD = Double.parseDouble(projrednum + "");

                if ((projrednumD / projallnumD) >= 0.4 && "0".equals(uflag)) {
                    rowmap.put("projallnum", shareGodUser.getProjallnum());
                    rowmap.put("projrednum", shareGodUser.getProjrednum());
                } else {
                    rowmap.put("projallnum", "");
                    rowmap.put("projrednum", "");
                }
                rowmap.put("wrate", Integer.valueOf((int) (shareGodUser.getWrate())) + "%");
                rowmap.put("matchnum", String.valueOf(shareGodUser.getMatchnum()));
                rowmap.put("guoguan", shareGodUser.getGuoguan());
                rowmap.put("follownums", String.valueOf(shareGodUser.getFollownums()));
                rowmap.put("endtime", shareGodUser.getEndtime());
                rowmap.put("yczs", shareGodUser.getYczs());
                rowList.add(rowmap);
            }
        }
        return rowList;
    }


    private String isOwner(OrderBean bean) {
        String isowner = "0";
        if (StringUtil.isEmpty(bean.getUid())) {
            return isowner;
        }
        if (!StringUtil.isEmpty(bean.getUid()) && bean.getNewValue().equals(bean.getUid())) {
            isowner = "1";
        }
        boolean isAgent = isAgent(bean.getUid());
        if (isAgent) {
            isowner = "1";
        }
        return isowner;
    }

    /**
     * 判断该用户是否为大客户
     *
     * @param uid
     * @return
     */
    public boolean isAgent(String uid) {
        String uidPath = "";
        try {
            uidPath = FileConstant.IS_AGENT;
            JXmlWrapper xml = JXmlWrapper.parse(new File(uidPath));
            List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("row");
            for (JXmlWrapper jXmlWapper : xmlNodeList) {
                String nickid = jXmlWapper.getStringValue("@uid");
                if (nickid.equals(uid)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("判断该用户是否为大客户异常", e);
        }
        return false;
    }

    public String getUserPhoto(String nickid) {
        CacheBean cacheBean = new CacheBean();
        String key = nickid + "_user_photo";
        cacheBean.setKey(key);
        //后期在配置文件中添加redis依赖
        String imgUrl = redisClient.getString(cacheBean, log, SysCodeConstant.ORDERCENTER);
        if (imgUrl == null || StringUtil.isEmpty(imgUrl) || "null".equals(imgUrl)) {
            imgUrl = userMapper.queryUserPhoto(nickid);
        }
        return imgUrl;
    }

    public String getUserPhotoNoCache(String nickid) {
        String photo = "";
        String cuserphoto = userMapper.queryUserPhoto(nickid);
        if (StringUtil.isEmpty(cuserphoto)) {
            return "";
        }
        photo = cuserphoto;
        return photo;
    }

    public String getUserPhotoSetCache(String nickid) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(nickid + "_user_photo");
        String photo = redisClient.getString(cacheBean, log, SysCodeConstant.ORDERCENTER);
        if (photo == null || StringUtil.isEmpty(photo) || "null".equals(photo)) {
            String cuserphoto = userMapper.queryUserPhoto(nickid);
            if (StringUtil.isEmpty(cuserphoto)) {
                return "";
            }
            photo = cuserphoto;
            cacheBean.setValue(photo);
            cacheBean.setTime(Constants.TIME_DAY);
            redisClient.setString(cacheBean, log, SysCodeConstant.ORDERCENTER);
        }
        return photo;
    }

    public double getProjRewardall(String nickid) {
        String s = shareListMapper.queryProjRewardall(nickid);
        double returnmoney = 0;
        if (s != null) {
            returnmoney = Double.valueOf(s);
        }
        return returnmoney;
    }


    //查询大神数据
    public List<ShareUserStatPojo> queryGodData(String nickid, String uptype) {
        String day = checkCurrentDate();
        List<ShareUserStatPojo> list = shareUserStatMapper.queryGodData(nickid, uptype, day);
        return list;
    }

    //查询当前时间
    public String checkCurrentDate() {
        String currentDate = DateUtil.getCurrentDate();
        int recordNums = shareUserDetailMapper.queryCountShareDetail(currentDate);
        if (recordNums > 0) {
            return currentDate;
        } else {
            return getBeforeDate();
        }
    }

    //查询当前时间
    public String checkCurrentDateStat() {
        String currentDate = DateUtil.getCurrentDate();
        int recordNums = shareUserStatMapper.queryCount(currentDate);
        if (recordNums > 0) {
            return currentDate;
        } else {
            return getBeforeDate();
        }
    }

    /**
     * 获取前一天的日期
     */
    private String getBeforeDate() {
        Date dNow = new Date();   //当前时间
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        return sdf.format(dBefore);    //格式化前一天
    }

    private void buildAllPeriodData(HashMap<String, Object> map, String nickid) {
        String day = checkCurrentDate();
        List<ShareUserDetailPojo> list = shareUserDetailMapper.queryAllPeriodData(nickid, day);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShareUserDetailPojo jrs = list.get(i);
                String stattype = jrs.getCstattype();
                if ("7".equals(stattype)) {
                    appendPeriodData(map, jrs, "7");
                } else if ("15".equals(stattype)) {
                    appendPeriodData(map, jrs, "15");
                } else if ("30".equals(stattype)) {
                    appendPeriodData(map, jrs, "30");
                } else {
                    map.put("shootrate7", "");
                    map.put("returnrate7", "");
                    map.put("shootrate15", "");
                    map.put("returnrate15", "");
                    map.put("shootrate30", "");
                    map.put("returnrate30", "");
                    return;
                }

            }
        }

    }

    private void buildAllPeriodData(GodDetailDTO dto, String nickid) {
        String day = checkCurrentDate();
        List<ShareUserDetailPojo> list = shareUserDetailMapper.queryAllPeriodData(nickid, day);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ShareUserDetailPojo jrs = list.get(i);
                String stattype = jrs.getCstattype();
                if ("7".equals(stattype)) {
                    appendPeriodData(dto, jrs, "7");
                } else if ("15".equals(stattype)) {
                    appendPeriodData(dto, jrs, "15");
                } else if ("30".equals(stattype)) {
                    appendPeriodData(dto, jrs, "30");
                } else {
                    dto.setShootrate7("");
                    dto.setReturnrate7("");
                    dto.setShootrate15("");
                    dto.setReturnrate15("");
                    dto.setShootrate30("");
                    dto.setReturnrate30("");
                    return;
                }

            }
        }

    }

    // 用户命中率，回报率数据添加
    private void appendPeriodData(HashMap<String, Object> detailMap, ShareUserDetailPojo jrs, String dayType) {
        String allProjNum = String.valueOf(jrs.getAllnum());
        if (StringUtil.isEmpty(allProjNum) || "0".equals(allProjNum)) {
            detailMap.put("shootrate" + dayType, "");
            detailMap.put("returnrate" + dayType, "");
            return;
        } else {
            String shootrate = jrs.getShootrate();
            detailMap.put("shootrate" + dayType, shootrate);
            String returnrate = jrs.getReturnrate();
            detailMap.put("returnrate" + dayType, returnrate);
            return;
        }
    }

    // 用户命中率，回报率数据添加
    private void appendPeriodData(GodDetailDTO dto, ShareUserDetailPojo jrs, String dayType) {
        String allProjNum = String.valueOf(jrs.getAllnum());
        if (dayType.equals("7")) {
            if (StringUtil.isEmpty(allProjNum) || "0".equals(allProjNum)) {
                dto.setShootrate7("");
                dto.setReturnrate7("");
                return;
            } else {
                String shootrate = jrs.getShootrate();
                dto.setShootrate7(shootrate);
                String returnrate = jrs.getReturnrate();
                dto.setReturnrate7(returnrate);
                return;
            }
        }
        if (dayType.equals("15")) {
            if (StringUtil.isEmpty(allProjNum) || "0".equals(allProjNum)) {
                dto.setShootrate15("");
                dto.setReturnrate15("");
                return;
            } else {
                String shootrate = jrs.getShootrate();
                dto.setShootrate15(shootrate);
                String returnrate = jrs.getReturnrate();
                dto.setReturnrate15(returnrate);
                return;
            }
        }
        if (dayType.equals("30")) {
            if (StringUtil.isEmpty(allProjNum) || "0".equals(allProjNum)) {
                dto.setShootrate30("");
                dto.setReturnrate30("");
                return;
            } else {
                String shootrate = jrs.getShootrate();
                dto.setShootrate30(shootrate);
                String returnrate = jrs.getReturnrate();
                dto.setReturnrate30(returnrate);
                return;
            }
        }
    }

    //添加竞彩大神详情页面banner
    private List<HashMap<String, Object>> appendGodRankBanner(OrderBean bean) throws Exception {
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
            JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.GOD_BANNER));
            List<JXmlWrapper> bannerList = xml.getXmlNodeList("banner");
            for (JXmlWrapper banner : bannerList) {
                JXmlWrapper generalRules = banner.getXmlNode("general-rules");
                boolean flag = ParseGeneralRulesUtil.parseGeneralRules(generalRules, bean);
                if (flag) {
                    Element element = banner.getXmlRoot();
                    List<Attribute> attrList = element.getAttributes();
                    HashMap<String, Object> banMap = new HashMap<>();
                    for (Attribute attr : attrList) {
                        String attrName = attr.getName();
                        String attrValue = attr.getValue();
                        if(attrValue.startsWith("http://mobile.9188.com")){
                            attrValue = attrValue.substring(22);
                        }
                        banMap.put(attrName, attrValue);
                    }
                    list.add(banMap);
                }
            }
        } catch (Exception e) {
            log.info("解析banner XML文件异常", e);
        }
        return list;
    }

    //添加竞彩大神盈利rank
    private List<HashMap<String, Object>> appendGodProfitRank(OrderBean bean) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey("godProfitRank");
        String godProfitRank = redisClient.getString(cacheBean, log, SysCodeConstant.ORDERCENTER);
        List<HashMap<String, Object>> list = new ArrayList<>();
        if (godProfitRank == null || StringUtil.isEmpty(godProfitRank) || "null".equals(godProfitRank)) {//先从缓存获取,如果缓存中没有再从数据库查询
            list = queryGodProfitRank(bean);
            if (bean.getBusiErrCode() != 0) {
                log.info("添加竞彩大神盈利rank失败,用户名:" + bean.getUid() + " 错误描述:" + bean.getBusiErrDesc());
            } else {
                log.info("添加竞彩大神盈利rank成功,用户名:" + bean.getUid());
            }
        } else {
            //从缓存中获取,并转为map集合 todo
        }
        return list;
    }

    // 查询盈利大神榜
    public List<HashMap<String, Object>> queryGodProfitRank(OrderBean bean) {
        //HashMap<String, Object> map = new HashMap<>();
        List<HashMap<String, Object>> list = new ArrayList<>();
        try {
            List<ShareUserListPojo> jrs = queryGodRank("10");
            if (jrs != null && jrs.size() > 0) {
                int i = 0;// 记录人数
                Iterator<ShareUserListPojo> iterator = jrs.iterator();
                while (iterator.hasNext() && i < 10) {
                    HashMap<String, Object> godMap = new HashMap<>();
                    i++;
                    ShareUserListPojo pojo = iterator.next();
                    String nickid = pojo.getCnickid();
                    String encrypted = CaiyiEncrypt.encryptStr(nickid).replaceAll("\\+", "\\*");
                    godMap.put("realUid", factor + encrypted);
                    String imgUrlTmp = getUserPhoto(nickid);
                    String imgUrl = "";
                    String newImgUrl = "";
                    // 相对地址时
                    if (!StringUtil.isEmpty(imgUrlTmp) && !imgUrlTmp.startsWith("http://")) {
                        imgUrl = "http://mobile.9188.com" + imgUrlTmp;
                        newImgUrl = imgUrlTmp;
                    }
                    // 绝对地址时
                    if (imgUrlTmp != null && imgUrlTmp.startsWith("http://")) {
                        imgUrl = imgUrlTmp;
                        newImgUrl = imgUrlTmp.substring(22);
                    }
                    godMap.put("imgUrl", imgUrl);
                    godMap.put("newImgUrl", newImgUrl);
                    nickid = CheckUtil.checkNum(nickid);
                    godMap.put("godUid", nickid);
                    String rank = String.valueOf(pojo.getRank());
                    godMap.put("rank", rank);
                    int remainNum = shareListMapper.queryGodGoingProjNum(nickid);
                    godMap.put("remainNum", remainNum);
                    list.add(godMap);

                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(FAIL));
            bean.setBusiErrDesc("查询盈利大神榜出错");
            log.info("查询盈利大神榜出错 用户名:" + bean.getUid() + " 错误信息:" + e);
        }
        return list;
    }

    public List<ShareUserListPojo> queryGodRank(String num) {
        List<ShareUserListPojo> list = null;
        if (!StringUtil.isEmpty(num)) {
            list = shareUserListMapper.queryGodRank1(num);
        } else {
            list = shareUserListMapper.queryGodRank();
        }
        return list;
    }

    // 增加竞彩大神分享神单
    public ArrayList<HashMap<String, Object>> appendGodShareList(OrderBean bean) throws Exception {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey("godShareList");
        String godShareList = redisClient.getString(cacheBean, log, SysCodeConstant.ORDERCENTER);
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        if (godShareList == null || StringUtil.isEmpty(godShareList) || "null".equals(godShareList)) {
            list = queryGodShareList(bean);
            if (bean.getBusiErrCode() != 0) {
                log.info("添加大神分享神单失败,用户名:" + bean.getUid() + " 错误描述:" + bean.getBusiErrDesc());
            } else {
                log.info("添加大神分享神单成功用户名:" + bean.getUid());
            }
        } else {
            //从缓存中获取,并转为map集合 todo
            JXmlWrapper jXmlWrapper = JXmlWrapper.parse(godShareList);
            list = getGodShareListToMap(jXmlWrapper);
        }
        return list;
    }

    private static ArrayList<HashMap<String, Object>> getGodShareListToMap(JXmlWrapper xml) throws Exception {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("shareItem");
        for (JXmlWrapper content : xmlNodeList) {
            Element element = content.getXmlRoot();
            HashMap<String, Object> map = new HashMap<>();
            List<Attribute> attrList = element.getAttributes();
            for (Attribute attr : attrList) {
                String attrName = attr.getName();
                if("userphoto".equals(attrName)){
                    continue;
                }
                String attrValue = attr.getValue();
                map.put(attrName, attrValue);
            }
            list.add(map);
        }
        return list;
    }

    // 查询神单分享列表
    public ArrayList<HashMap<String, Object>> queryGodShareList(OrderBean bean) throws Exception {
        CacheBean cacheBean = new CacheBean();
        ArrayList<String> userList = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        int num = 0;
        // 根据后台优先级查询分享神单
        try {
            List<ShareListPojo> jrs = shareListMapper.queryGodShareByPriority();
            List<ShareListPojo> jrs1 = new ArrayList<>();
            if ("1".equals(bean.getWorldCup())) {
                choiceWorldCupMatches(jrs,jrs1);
            }else {
                jrs1.addAll(jrs);
            }
            list = organizeShareItem(num, jrs1, userList, cacheBean, list);
            num = list.size();
            // 是否已经存在10条数据
            if (num < 10) {
                //根据投注金额查询分享神单（取5个金额最大的,命中率大于20%,起投金额小于5000的投注人(不重复)）
                int firstNum = 0;//大神榜前几位数字
                List<ShareListPojo> listPojos = shareListMapper.queryGodFirst5();
                List<ShareListPojo> jrs2 = new ArrayList<>();
                if ("1".equals(bean.getWorldCup())) {
                    choiceWorldCupMatches(listPojos,jrs2);
                }else {
                    jrs2.addAll(listPojos);
                }

                if (jrs2 != null && jrs2.size() > 0) {
                    Iterator<ShareListPojo> iterator = jrs2.iterator();
                    while (iterator.hasNext()) {
                        ShareListPojo pojo = iterator.next();
                        if(firstNum<10) {
                            String nickid = pojo.getNickid();
                            if(userList.contains(nickid)){//如果包含该用户名则跳过
                                continue;
                            }else {
                                List<ShareListPojo> shareListPojos = shareListMapper.queryMaxItem(nickid);
                                List<ShareListPojo> jrs3 = new ArrayList<>();
                                if ("1".equals(bean.getWorldCup())) {
                                    choiceWorldCupMatches(shareListPojos,jrs3);
                                }else {
                                    jrs3.addAll(shareListPojos);
                                }
                                firstNum++;
                                list = organizeShareItem(num, jrs3, userList, cacheBean, list);
                                num = list.size();
                            }
                        }else {
                            break;
                        }
                    }

                }

 /*               int offset = 0;
                // 根据投注金额查询分享神单（取3个金额最大的投注人(不重复)）
                jrs = shareListMapper.queryShareMaxMoney(10);
                if (jrs != null & jrs.size() > 0) {
                    for (ShareListPojo jr : jrs) {
                        if (userList.contains(jr.getNickid())) {
                            offset++;
                        }
                    }
                }
                jrs = shareListMapper.queryShareMaxMoney(10 + offset);
                if (jrs != null & jrs.size() > 0) {
                    for (ShareListPojo jr : jrs) {
                        String nickid = jr.getNickid();
                        List<ShareListPojo> jrs4 = shareListMapper.queryGodBiggestItem(nickid);
                        list = organizeShareItem(num, jrs4, userList, cacheBean, list);
                        num = list.size();
                    }
                }*/
            }
            // num = (int) map1.get("num");
            // 是否已经存在10条数据
/*            if (num < 10) {
                // 根据排名查询大神
                List<ShareUserListPojo> jrs2 = queryGodRank("10");
                if (jrs2 != null && jrs2.size() > 0) {
                    Iterator<ShareUserListPojo> iterator = jrs2.iterator();
                    while (iterator.hasNext() && num < 10) {
                        ShareUserListPojo pojo = iterator.next();
                        String nickid = pojo.getCnickid();
                        // 取大神最大的一笔分享神单
                        List<ShareListPojo> jrs3 = shareListMapper.queryGodBiggestItem(nickid);
                        list = organizeShareItem(num, jrs3, userList, cacheBean, list);
                        num = list.size();
                    }
                }
            }
            // 如果还未满足10条数据，继续从金额最大的单子中进行筛选
            if (num < 10) {
                jrs = shareListMapper.queryGodShareByMoney(100);//从金额最高的100的单子中筛选
                list = organizeShareItem(num, jrs, userList, cacheBean, list);
            }*/
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(FAIL));
            bean.setBusiErrDesc("查询分享神单出错");
            log.info("查询分享神单出错 用户名:" + bean.getUid() + " 错误信息:" + e);
        }
        return list;
    }

    /**
     * 筛选世界杯比赛
     * @param jrs
     * @param jrsnew
     */
    private void  choiceWorldCupMatches( List<ShareListPojo> jrs,List<ShareListPojo> jrsnew){
        try {
            JXmlWrapper jXmlWrapper = JXmlWrapper.parse(new File(FileConstant.TOPIC_FOOTBALL));
            int count = jXmlWrapper.countXmlNodes("row");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                String items = jXmlWrapper.getStringValue("row[" + i + "].@items");
                stringBuilder.append(items);
                if (i != count - 1) {
                    stringBuilder.append(",");
                }
            }
            String[] itemsArr = stringBuilder.toString().split(",");
            List itemsList = Arrays.asList(itemsArr);
            for (ShareListPojo shareListPojo : jrs) {
                if (StringUtil.isEmpty(shareListPojo.getMatches())) {
                    continue;
                }
                boolean flag = false;
                String[] temArr = shareListPojo.getMatches().split(",");
                for (int j = 0; j < temArr.length; j++) {
                    //购买的单子场次都是世界杯
                    if (!StringUtil.isEmpty(temArr[j])) {
                        if (itemsList.contains(temArr[j])){
                            flag = true;
                        }else {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    jrsnew.add(shareListPojo);
                }
            }
        } catch (Exception e) {
            log.error("筛选世界杯比赛出错",e);
        }

    }

    // 组织分享列表数据
    private ArrayList<HashMap<String, Object>> organizeShareItem(int num, List<ShareListPojo> jrs, List<String> userList, CacheBean cacheBean, ArrayList<HashMap<String, Object>> list) throws Exception {
        if (jrs != null && jrs.size() > 0) {
            Iterator<ShareListPojo> iterator = jrs.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> shareMap = new HashMap<>();
                if (num == 10) {// 如果有10条数据就停止
                    break;
                }
                ShareListPojo pojo = iterator.next();
                String nickid = pojo.getNickid();
                // 根据nickid先从缓存中获取是否用户是否存在刷单标记，获取不到，则需要从数据库查询数据计算 ---检查刷单标记
               /* cacheBean.setKey(nickid + "Buy_Rate");
                String uflag = redisClient.getString(cacheBean, log);
                if (uflag == null || "".equals(uflag)) {
                    double rate = queryBuyRate(nickid);
                    uflag = rate > 100 ? "1" : "0";
                    cacheBean.setValue(uflag);
                    cacheBean.setTime(Constants.TIME_DAY);
                    redisClient.setString(cacheBean, log);
                }*/
                if (userList.contains(nickid)/* || "1".equals(uflag)*/) {// 列表存在该用户的神单或者该用户存在刷单行为均不显示
                    continue;
                } else {
                    userList.add(nickid);
                }
                String encrypted = CaiyiEncrypt.encryptStr(nickid).replaceAll("\\+", "\\*");
                shareMap.put("realUid", factor + encrypted);
                String projid = pojo.getProjid();
                shareMap.put("projid", projid);
                Date endtime = pojo.getEndtime();//方案截止时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endtime);
                calendar.add(Calendar.MINUTE, -5);// 跟投时间比方案截止时间提前5分钟
                Calendar now = Calendar.getInstance();
                int nowDate = now.get(Calendar.DAY_OF_YEAR);
                int endtimeDate = calendar.get(Calendar.DAY_OF_YEAR);
                if (nowDate == endtimeDate) {// 如果是当天只显示时和分
                   // String realEndTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                    Date realEndDate = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String realEndTime = sdf.format(realEndDate);
                    shareMap.put("endtime", realEndTime);
                } else {
                    Date realEndDate = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                    String realEndTime = sdf.format(realEndDate);
                    shareMap.put("endtime", realEndTime);
                }
                String tmoney = pojo.getTmoney();
                shareMap.put("tmoney", tmoney);
                Integer mulity = pojo.getMulity();
                int averageMoney = Integer.parseInt(tmoney) / mulity;
                Integer wrate = pojo.getWrate();
                shareMap.put("wrate", wrate + "%");
                String matches = pojo.getMatches();
                String[] matchesArr = matches.split(",");
                int matchNum = 0;
                for (String match : matchesArr) {
                    if (StringUtil.isEmpty(match)) {
                        continue;
                    }
                    matchNum = matchNum + 1;
                }
                shareMap.put("matchNum", String.valueOf(matchNum));
                // 起投金额优化
                String codes = pojo.getCodes();
                double tm = Double.valueOf(tmoney);
                boolean b = SStringUtils.endwith(codes, "txt");
                String extendtype = String.valueOf(pojo.getExtendtype());
                boolean yczsFlag = "15".equals(extendtype);
                String yhmoney = averageMoney + "";
                if (b && tm > 100 && !yczsFlag) {
                    String yhfile = codes.replace("_n.txt", "_yh.xml");
                    String xmlpath = "/opt/export/data/pupload/" + pojo.getGameid() + "/" + pojo.getPeriod() + "/"
                            + yhfile;
                    int itemcnt = getmatchCnt(xmlpath);
                    if (itemcnt != 0 && 10 * itemcnt < tm) {
                        yhmoney = 10 * itemcnt + "";
                    }
                }
                shareMap.put("yhMoney", yhmoney);
                shareMap.put("averageMoney", String.valueOf(averageMoney));
                String yczs = "0";
                //String extendtype = jrs.get("extendtype");
                if("15".equals(extendtype)){
                    yczs = "1";
                }
                shareMap.put("yczs",yczs);
                String guoguan = pojo.getGuoguan();
                guoguan = guoguan.replace("*", "串");
                shareMap.put("guoguan", guoguan);
                String usernum = String.valueOf(pojo.getUsernum());
                shareMap.put("usernum", usernum);
                if (getMapCacheValue(nickid + "_god_detail_cache", "projallnum")
                        && getMapCacheValue(nickid + "_god_detail_cache", "projrednum")) {
                    CacheBean cacheBean1 = new CacheBean();
                    cacheBean1.setKey(nickid + "_god_detail_cache");
                    Map<String, String> mmp = (Map<String, String>) redisClient.getObject(cacheBean1, Map.class, log, SysCodeConstant.ORDERCENTER);
                    if (mmp != null || mmp.size() > 0) {
                        String allnum = mmp.get("projallnum");
                        //shareMap.put("allnum", allnum);
                        String rednum = mmp.get("projrednum");
                       // shareMap.put("rednum", rednum);
                        if(!(StringUtil.isEmpty(allnum)||StringUtil.isEmpty(rednum))){
                            double allnumD = Double.parseDouble(allnum);
                            double rednumD = Double.parseDouble(rednum);
                            if((rednumD/allnumD)>=0.4){
                                shareMap.put("allnum", allnum);
                                shareMap.put("rednum", rednum);

                            }else{
                                shareMap.put("allnum", "");
                                shareMap.put("rednum", "");
                            }
                        }else{
                            shareMap.put("allnum", "");
                            shareMap.put("rednum", "");
                        }
                    }
                } else {
                    // 查询该用户是否是大神
                    ShareUserListPojo sulPojo = shareUserListMapper.queryGodStatus(nickid);
                    if (sulPojo != null) {
                        String usertype = sulPojo.getUsertype();
                        if ("1".equals(usertype)) {// 是大神用户,显示大神用户指定类型的命中
                            String uptype = sulPojo.getUptype();
                            List<ShareUserDetailPojo> sudListPojo = shareUserDetailMapper.queryGodHitData(nickid, uptype);
                            if (sudListPojo != null && sudListPojo.size() > 0) {
                                ShareUserDetailPojo sudPojo = sudListPojo.get(0);
                                String allnum = String.valueOf(sudPojo.getAllnum());
                                String rednum = String.valueOf(sudPojo.getRednum());
                                if(!(StringUtil.isEmpty(allnum)||StringUtil.isEmpty(rednum))){
                                    double allnumD = Double.parseDouble(allnum);
                                    double rednumD = Double.parseDouble(rednum);
                                    if((rednumD/allnumD)>=0.4){
                                        shareMap.put("allnum", allnum);
                                        shareMap.put("rednum", rednum);
                                    }else{
                                        shareMap.put("allnum", "");
                                        shareMap.put("rednum", "");
                                    }
                                }else{
                                    shareMap.put("allnum", "");
                                    shareMap.put("rednum", "");
                                }
                            }
                        } else {// 非大神用户,根据最近7天的数据进行统计
                            List<ShareUserDetailPojo> sudPojos = shareUserDetailMapper.queryLatestData(nickid, "7");
                            if (sudPojos != null && sudPojos.size() > 0) {
                                ShareUserDetailPojo pojo1 = sudPojos.get(0);
                                String allnum = String.valueOf(pojo1.getAllnum());
                                String rednum = String.valueOf(pojo1.getRednum());
                                if(!(StringUtil.isEmpty(allnum)||StringUtil.isEmpty(rednum))){
                                    double allnumD = Double.parseDouble(allnum);
                                    double rednumD = Double.parseDouble(rednum);
                                    if((rednumD/allnumD)>=0.4){
                                        shareMap.put("allnum", allnum);
                                        shareMap.put("rednum", rednum);
                                    }else{
                                        shareMap.put("allnum", "");
                                        shareMap.put("rednum", "");
                                    }
                                }else{
                                    shareMap.put("allnum", "");
                                    shareMap.put("rednum", "");
                                }

                            }
                        }
                    }
                }
                String cuserphoto = getUserPhoto(nickid);
                String newUserPhoto = "";
                // 绝对地址时
                if (cuserphoto != null && cuserphoto.startsWith("http://")) {
                    newUserPhoto = cuserphoto.substring(22);
                }
                shareMap.put("newuserphoto", newUserPhoto);
                nickid = CheckUtil.checkNum(nickid);
                shareMap.put("nickid", nickid);
                list.add(shareMap);
                num = num + 1;
            }
        }
        return list;
    }

    public double queryBuyRate(String nickid) {
        double rate = 0;
        List<String> list = shareListMapper.queryBuyRate(nickid);
        if (list != null && list.size() > 0) {
            String aDouble = list.get(0);
            rate = Double.valueOf(StringUtil.isEmpty(aDouble) ? "0.0" : aDouble);
        }
        return rate;
    }

    // 缓存中是否有值,true有,false没有
    public boolean getMapCacheValue(String mapkey, String valuekey) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(mapkey);
        Map<String, String> map = (Map<String, String>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.ORDERCENTER);
        if (null == map) {
            return false;
        } else {
            String value = map.get(valuekey);
            if (null == value || StringUtil.isEmpty(value) || "null".equals(value)) {
                return false;
            } else {
                return true;
            }
        }
    }

    //-------------------------------------------------TK END-----------------------------------------------------------------

    /**
     * 分享神单
     *
     * @return
     */
    public void shareGodProj(OrderBean bean) {
        log.info("用户 uid==" + bean.getUid() + " 分享神单==" + bean.getPid());
        try {
            String gid = bean.getGid();
            String hid = bean.getHid();
            if (StringUtil.isEmpty(gid) || StringUtil.isEmpty(hid)) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("彩种编号或方案编号不能为空");
                return;
            }
            if (!TradeConstants.JC_ZQ_MAP.containsKey(gid)) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("暂只支持足球神单分享");
                return;
            }
            // 查询待分享的方案
            List<ProjPojo> projPojos = projMapper.queryProjInfo(gid, hid);
            if (projPojos == null || projPojos.size() == 0) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("方案" + hid + "不存在,不能发起分享");
                return;
            }
            ProjPojo projPojo = projPojos.get(0);
//            projJrs.first();
            bean.setPid(projPojo.getCperiodid());
            // 可分享--校验条件
            checkShareCondition(bean, projPojo);

            if (bean.getBusiErrCode() != 0) {
                return;
            }
            String codes = projPojo.getCcodes();
            String nickid = projPojo.getCnickid();
            String periodid = projPojo.getCperiodid();
//            String cmatchs = projPojo.getCmatchs();
            // 获取最后一场比赛的开赛时间
            getLastGameTime(bean);

            if (StringUtil.isEmpty(bean.getGuoguan())) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("分享神单失败,请稍后重试");
                log.info("分享神单获取开赛时间失败,gid:" + bean.getGid() + " pid:" + bean.getPid() + " hid:" + bean.getHid() + " btime:" + bean.getGuoguan());
                return;
            }

            List<ShareListPojo> shareListPojos = shareListMapper.queryComShareList(gid, nickid, periodid);
            modifyShareProj(shareListPojos, projPojo, bean);

        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("分享神单出错");
            log.error("用户分享神单出错", e);
        }
    }

    /**
     * 分享要求检测
     *
     * @param bean
     * @param projPojo
     */
    private void checkShareCondition(OrderBean bean, ProjPojo projPojo) {

        log.info("神单分享前检测开始");
        // 截止时间检测
        Date endtime = projPojo.getCendtime();// 截止时间
        String current = DateUtil.getCurrentFormatDate("yyyy-MM-dd HH:mm:ss");
        String currentTimeBefore5 = getBeforeXminTime(endtime, 15 * 60 * 1000);// 截止时间-15分钟
        boolean compare_date = compare_date(current, currentTimeBefore5);
        if (compare_date) {// 当前时间大于（截止时间-15分钟） 不允许分享
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("有15分钟内截止投注的比赛，不能进行发神单哦~");
            return;
        }
        // 当日发单数不能超过5单
        int recordNums = shareListMapper.queryShareRecordNums(projPojo.getCnickid(), DateUtil.getCurrentDate());
        if (recordNums >= 5) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("一天最多发神单5个方案，明天再来发神单呗");
            return;
        }
        // 理论最小金额检测
        Double itmoney = Double.valueOf(projPojo.getItmoney());
        if (StringUtil.isEmpty(projPojo.getIminrange())) {
            // bean.setBusiErrCode(-1);
            // bean.setBusiErrDesc("神单方案最小奖金须大于投注金额的80%，才可以进行分享");
        } else {
            Double iminrange = Double.valueOf(projPojo.getIminrange() == null ? "0" : projPojo.getIminrange());
            if (iminrange < itmoney) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("神单要求理论奖金的最小金额不得低于投注金额的 1.15 倍");
                // bean.setBusiErrDesc("神单方案最小奖金须大于投注金额，才可以进行分享");
                return;
            }
        }
        Double imaxrange = Double.valueOf(projPojo.getImoneyrange() == null ? "0" : projPojo.getImoneyrange());
        if (imaxrange <= itmoney) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("神单方案最大奖金小于投注金额，不可以分享");
            return;
        }
        // int isource = projJrs.getInt("isource");
        // int extendtype = projJrs.getInt("extendtype");
        // if(isource == 6 || extendtype==6 || isource ==11 ||
        // bean.getExtendtype()==11 || bean.getExtendtype()==13 ||
        // bean.getExtendtype()==14 || isource ==13){
        // bean.setBusiErrCode(-1);
        // bean.setBusiErrDesc("暂不支持奖金优化方案分享");
        // return ;
        // }
        // 有相同场次的不允许分享
        String cmatchs = projPojo.getCmatchs();
        // 同一个用户不允许发相同场次
        int result = getSameItemid(bean, cmatchs);
        if (result >= 1) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("相同场次方案不允许重复分享");
            return;
        }
        // 彩种，上传，串关，奖金优化 检测
        // 理论奖金的最小值为本金的 1.15 倍 iminrange
        double iminrange = Double.valueOf(StringUtils.isEmpty(projPojo.getIminrange()) ? "0.0" : projPojo.getIminrange());
        if (iminrange / itmoney < 1.15) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("神单要求理论奖金的最小金额不得低于投注金额的 1.15 倍");
        }
    }

    /**
     * 获取X分钟前时间
     */
    private static String getBeforeXminTime(Date now, long miltime) {
        String format = "yyyy-MM-dd HH:mm:ss";
        String result = "";
        try {
            Date now_10 = new Date(now.getTime() - miltime); // 10分钟前的时间
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            result = dateFormat.format(now_10);
        } catch (Exception e) {
            log.error("getBeforeXminTime Exception,miltime:"+miltime,e);
        }
        return result;
    }

    /**
     * 比较时间大小
     *
     * @param jzTime
     * @param curTime
     * @return
     */
    public static boolean compare_date(String jzTime, String curTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(jzTime);
            Date dt2 = df.parse(curTime);
            if (dt1.getTime() - dt2.getTime() > 0) {
                return true;
            }
        } catch (Exception exception) {
            log.error("compare_date Exception,jzTime:"+jzTime+" curTime"+curTime,exception);
        }
        return false;
    }

    public int getSameItemid(OrderBean bean, String cmatchs) {
        //查询三天内用户发单所包含的场次ID
        List<ShareListPojo> shareListPojos = shareListMapper.getSameItemid(bean.getUid());
        Map<String, String> map = new HashMap<String, String>();
        if (shareListPojos != null && shareListPojos.size() > 0) {
            for (ShareListPojo shareListPojo : shareListPojos) {
                String gid = shareListPojo.getGameid();
                String hid = shareListPojo.getProjid().toLowerCase();
                String pid = shareListPojo.getPeriod();
                String match = shareListPojo.getMatches();
                String extendtype = String.valueOf(shareListPojo.getExtendtype());
                if ("15".equals(extendtype)) {//一场致胜投注
                    String yczsfile = "/opt/export/data/guoguan/" + gid + "/" + pid + "/" + hid + "_yczs.xml";
                    File file = new File(yczsfile);
                    JXmlWrapper parse = JXmlWrapper.parse(file);
                    List<JXmlWrapper> xmlNodeList = parse.getXmlNodeList("row");
                    StringBuilder builder = new StringBuilder();
                    for (JXmlWrapper row : xmlNodeList) {
                        JXmlWrapper zxitem = row.getXmlNode("zxitem");
                        String zid = zxitem.getStringValue("@id");
                        builder.append(",").append(zid);
                    }
                    match = builder.toString();
                }
                String[] split = match.replaceFirst(",", "").split(",");
                for (String matchid : split) {
                    if (map.containsKey(matchid)) {
                        continue;
                    }
                    map.put(matchid, "");
                }
            }
        }
        //校验场次ID
        if (cmatchs.contains(",")) {
            String[] split = cmatchs.replaceFirst(",", "").split(",");
            for (String matchid : split) {
                if (map.containsKey(matchid)) {
                    return 100;
                }
            }
        }
        return 0;
    }

    private String getLastGameTime(OrderBean bean) throws Exception {
        String gid = bean.getGid();
        String hid = bean.getHid();
        String pid = bean.getPid();
        // 获取最后一场比赛开赛时间
        String xmlpath = "/opt/export/data/guoguan/" + gid + "/" + pid + "/proj/" + hid.toLowerCase() + ".xml";
        Set<String> ppitemSet = new HashSet<>();//一场致胜获取匹配场次
        String yczsPath = FileConstant.GUOGUAN_DIR + gid + "/" + pid + "/" + hid.toLowerCase() + "_yczs.xml";
        if (new File(yczsPath).exists()) {
            JXmlWrapper xml = JXmlWrapper.parse(new File(yczsPath));
            List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("row");
            for (JXmlWrapper row : xmlNodeList) {
                JXmlWrapper ppxml = row.getXmlNode("ppitem");
                ppitemSet.add(ppxml.getStringValue("@id"));
            }
        }
        JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));
        int count = xml.countXmlNodes("item");
        log.info("获取比赛最后一场开赛时间,hid:" + hid + " gid:" + gid + " pid:" + pid + " 比赛数量:" + count);
        String btime = "";
        for (int i = 0; i < count; i++) {
            String itemid=xml.getStringValue("item[" + i + "].@id");
            if(ppitemSet.contains(itemid)){//一场致胜只看自选场次最后截止时间
                continue;
            }
            if (StringUtil.isEmpty(btime)) {
                btime = xml.getStringValue("item[" + i + "].@bt");// 开赛时间
                log.info("获取比赛最后一场开赛时间,第一次时间获得:" + btime + " hid:" + hid + " pid:" + pid + " gid:" + gid);
            } else {
                String newTime = xml.getStringValue("item[" + i + "].@bt");// 开赛时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date btimeD = sdf.parse(btime);
                Date newTimeD = sdf.parse(newTime);
                if (btimeD.before(newTimeD)) {
                    btime = newTime;
                }
            }
        }
        bean.setGuoguan(btime);
        return btime;
    }

    @Transactional
    public void modifyShareProj(List<ShareListPojo> shareListPojos, ProjPojo projPojo, OrderBean bean) {
        String codes = projPojo.getCcodes();
        String gid = bean.getGid();
        String hid = bean.getHid();
        int ret = 0;
        Set<String> codeSet = new HashSet<String>();
        Set<String> fileSet = new HashSet<String>();
        if (shareListPojos != null && shareListPojos.size() > 0) {
            String sharedCodes = "";
            for (ShareListPojo shareListPojo : shareListPojos) {// 缓存之前分享的方案
                sharedCodes = shareListPojo.getCodes();
                if (!sharedCodes.endsWith("txt")) {
                    codeSet.add(sharedCodes);
                } else {
                    fileSet.add(sharedCodes);
                }
            }
            if (!codeSet.contains(codes)) {
                ret = insertShareProj(projPojo, bean);
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("您所选的投注场次、投注选项和过关方式三个都相同，因此该方案不可重复分享");
                return;
            }
        } else { // 之前没有分享过神单
            ret = insertShareProj(projPojo, bean);
        }
        int projRet = 0;
        if (ret == 1) {
            projRet = projMapper.updateProjItype(gid, hid);
            // 分享成功，插入出票后可见的任务
            int insertProjTask = projTaskMapper.insertProjTask(hid, gid, projPojo.getCperiodid());
            if (projRet == 1 && insertProjTask == 1) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("成功");
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("更新方案itype失败:hid=" + hid + "itype=" + 2);
                log.error("投注完成后分享神单失败！");
                throw new RuntimeException("自定义异常用于事务回滚");
            }
        } else {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("分享失败");
            log.error("投注完成后分享神单失败  cprojid==" + hid);
            throw new RuntimeException("自定义异常用于事务回滚");
        }
    }

    public int insertShareProj(ProjPojo projPojo, OrderBean bean) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gid = bean.getGid();
        String hid = bean.getHid();
        //方案信息
        String codes = projPojo.getCcodes();
        String nickid = projPojo.getCnickid();
        String periodid = projPojo.getCperiodid();
        String mulity = String.valueOf(projPojo.getImulity());
        String tmoney = String.valueOf(projPojo.getItmoney());
        String endtime = sdf.format(projPojo.getCendtime());
        String moneyrange = projPojo.getIminrange();
        String extendtype = String.valueOf(projPojo.getExtendtype());
        String cmatchs = projPojo.getCmatchs();
        String cguoguan = projPojo.getCguoguan();
        String iwrate = bean.getIwrate();//分享人设置的打赏比例
        String btime = bean.getGuoguan();
        String imintmoney = "2";
        //计算最小起投金额
        Double doumulity = Double.valueOf(mulity);
        Double doutmoney = Double.valueOf(tmoney);
        if (doumulity > 1) {//倍数大于1倍的
            double ceil = Math.ceil(doutmoney / doumulity);//向上取整
            imintmoney = String.valueOf(ceil);
        } else {
            imintmoney = String.valueOf(doutmoney);
        }
        String yhmoney = imintmoney;
        //奖金优化+金额>100,计算优化起投金额
        if (codes != null) {
            boolean b = codes.endsWith("txt");
            boolean yczsFlag = "15".equals(extendtype);//一场致胜当做普通单子处理
            if (doutmoney > 100 && b && !yczsFlag) {
                String yhfile = codes.replace("_n.txt", "_yh.xml");
                String xmlpath = "/opt/export/data/pupload/" + gid + "/" + periodid + "/" + yhfile;
                int itemcnt = SStringUtils.getmatchCnt(xmlpath);
                if (itemcnt != 0 && itemcnt * 10 < doutmoney) {
                    yhmoney = itemcnt * 10 + "";
                }
            }
        }
        int projRet = shareListMapper.insertShareList(hid, nickid, gid, periodid, codes, mulity, tmoney, "4", endtime, moneyrange, extendtype, iwrate, imintmoney, yhmoney, cmatchs, cguoguan, btime);
        return projRet;
    }
}
























