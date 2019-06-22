package com.caiyi.lottery.tradesystem.activitycenter.service.impl;

import activity.bean.ActivityBean;
import activity.dto.GetBonusDTO;
import activity.dto.TtfqDetailDTO;
import activity.dto.TtfqHomePageDTO;
import activity.dto.TtfqPage;
import activity.pojo.CpenginePojo;
import activity.pojo.ProjQvodPojo;
import activity.pojo.QvodTtfqPojo;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.activitycenter.dao.CpEngineMapper;
import com.caiyi.lottery.tradesystem.activitycenter.dao.ProjQvodMapper;
import com.caiyi.lottery.tradesystem.activitycenter.dao.QvodTtfqAcctMapper;
import com.caiyi.lottery.tradesystem.activitycenter.dao.QvodTtfqMapper;
import com.caiyi.lottery.tradesystem.activitycenter.service.ActivityTtfqService;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.UserPojo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 天天分钱
 * @author wxy
 * @create 2017-12-28 15:40
 **/
@Slf4j
@Service
public class ActivityTtfqServiceImpl implements ActivityTtfqService {
    @Autowired
    private UserBasicInfoWrapper userBasicInfoWrapper;
    @Autowired
    private CpEngineMapper cpEngineMapper;
    @Autowired
    private UserBaseInterface userBaseInterface;
    @Autowired
    private ProjQvodMapper projQvodMapper;
    @Autowired
    private QvodTtfqMapper qvodTtfqMapper;
    @Autowired
    private QvodTtfqAcctMapper qvodTtfqAcctMapper;
    // 开奖时间 key:彩种 value:时间
    private static Map<String, String> timeMap = new HashMap<>();
    static {
        timeMap.put("01", "21:45");
        timeMap.put("50", "20:45");
        timeMap.put("51", "20:45");
    }

    /**
     * 天天分钱领取奖金
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public GetBonusDTO getBonus(ActivityBean bean) throws Exception {
        GetBonusDTO getBonusDTO = new GetBonusDTO();
        if (StringUtil.isEmpty(bean.getProjId())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_TTFQ_NOT_PROJID));
            bean.setBusiErrDesc("非法访问,未传入方案编号");
            return null;
        }

        // 通过用户中心取得用户基础数据
        UserPojo userPojo = userBasicInfoWrapper.queryUserInfo(bean, log, SysCodeConstant.ACTIVITYCENTER);
        if (userPojo != null ) {
            String idCard = userPojo.getIdcard();
            String realName = userPojo.getRealName();
            if(StringUtil.isEmpty(idCard)||StringUtil.isEmpty(realName)){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_TTFQ_NOT_BIND_IDCARD));
                bean.setBusiErrDesc("绑定身份证后才能参与该活动");
                return null;
            }
        }else{
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_TTFQ_NOT_BIND_IDCARD));
            bean.setBusiErrDesc("绑定身份证后才能参与该活动");
            return null;
        }

        CpenginePojo cpenginePojo = new CpenginePojo();
        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, cpenginePojo);
        cpEngineMapper.qvodttfqReturn(cpenginePojo);
        if (!BusiCode.SUCCESS.equals(cpenginePojo.getBusiErrCode())) {
            log.info("用户："+bean.getUid() +" 领取方案："+bean.getProjId()+"   奖金失败########" + cpenginePojo.getBusiErrDesc());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_TTFQ_GET_BONUS_FAIL));
            bean.setBusiErrDesc("天天分钱用户领奖失败");
            return null;
        }
        BeanUtilWrapper.copyPropertiesIgnoreNull(cpenginePojo, getBonusDTO);
        // 格式化金额
        getBonusDTO.setBonus(Double.parseDouble(getBonusDTO.getBonus()) + "");
        getBonusDTO.setBalance(Double.parseDouble(getBonusDTO.getBalance()) + "");
        getBonusDTO.setTotalBonus(Double.parseDouble(getBonusDTO.getTotalBonus()) + "");
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("领取成功");
        return getBonusDTO;
    }

    /**
     * 天天分钱首页
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public TtfqPage<List<TtfqHomePageDTO>> ttfqHomePage(ActivityBean bean) throws Exception {
        TtfqPage<List<TtfqHomePageDTO>> ttfqResultPage = new TtfqPage<>();
        List<TtfqHomePageDTO> ttfqHomePageDTOList = new ArrayList<>();
        TtfqHomePageDTO ttfqHomePageDTO;

        // 检查用户是否登录
        int isLogin = isLogin(bean);

        // 天天分钱方案分页查询，默认每页10条
        bean.setPs(10);
        TtfqPage<List<ProjQvodPojo>> ttfqPage = queryTtfqOrders(bean);

        // 查询天天分钱活动我的参与记录
        List<QvodTtfqPojo> myJoidList = qvodTtfqMapper.queryMyJoin(bean.getUid());
        Map<String, QvodTtfqPojo> myJoinMap = new HashMap<>();
        for (QvodTtfqPojo pojo : myJoidList) {
            myJoinMap.put(pojo.getProjId(), pojo);
        }

        Double totalMyBonus = null;
        Double totalBonus = null;

        // 只有第一页的时候查询总金额
        if (bean.getPn() == 1) {
            // 查询天天分钱活动方案我的总奖金
            // Double totalMyBonus = qvodTtfqMapper.queryTotalMyBonus(bean.getUid());
            totalMyBonus = qvodTtfqAcctMapper.queryByNickid(bean.getUid());

            // 查询天天分钱活动累计总奖金
            totalBonus = projQvodMapper.queryTotalBonus();
        }

        BeanUtilWrapper.copyPropertiesIgnoreNull(ttfqPage, ttfqResultPage);
        ttfqResultPage.setTotalBonus(totalBonus == null ? 0.0 : totalBonus);
        ttfqResultPage.setTotalMyBonus(totalMyBonus == null ? 0.0 : totalMyBonus);
        ttfqResultPage.setIsLogin(isLogin);

        long timeMills = Calendar.getInstance().getTimeInMillis();
        for (ProjQvodPojo pojo : ttfqPage.getDatas()) {
            if ("2".equals(pojo.getAward().toString()) && 0 == pojo.getBonus().doubleValue()) {//已派奖且奖金为0
                pojo.setBonus(1.0);
            }
            ttfqHomePageDTO = new TtfqHomePageDTO();
            BeanUtilWrapper.copyPropertiesIgnoreNull(pojo, ttfqHomePageDTO);

            QvodTtfqPojo ttfqPojo = myJoinMap.get(pojo.getProjId());

            ttfqHomePageDTO.setMyBonus(ttfqPojo == null ? 0.0 : ttfqPojo.getMoney());
            ttfqHomePageDTO.setDate(TimeUtil.customDateTime(TimeUtil.parserDateTime(pojo.getActivityDate()),"yyyyMMddHHmmss"));

            Integer status = 1000;
            if(ttfqPojo != null) {
                status = ttfqPojo.getStatus();
            } else {
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(TimeUtil.parserDateTime(pojo.getEndTime()));
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(TimeUtil.parserDateTime(pojo.getActivityDate()));
                endTime.set(Calendar.HOUR_OF_DAY, 19);
                endTime.set(Calendar.MINUTE, 0);
                endTime.set(Calendar.SECOND, 0);
                endTime.set(Calendar.MILLISECOND, 0);
                long end = endTime.getTimeInMillis();
                beginTime.set(Calendar.HOUR_OF_DAY, 19);
                beginTime.set(Calendar.MINUTE, 0);
                beginTime.set(Calendar.SECOND, 0);
                beginTime.set(Calendar.MILLISECOND, 0);
                beginTime.add(Calendar.DAY_OF_MONTH, -1);
                long begin = beginTime.getTimeInMillis();
                if(timeMills < end && timeMills >= begin) {
                    status = 1000;
                } else {
                    status = 6000;
                }
            }
            // ttfqHomePageDTO.setStatus(status);
            ttfqHomePageDTO.setAwardtime(timeMap.get(pojo.getGameId()));
            ttfqHomePageDTO.setStatus(isLogin == 0 ? 2000 : status);
            ttfqHomePageDTO.setMyBonus(isLogin == 0 ? 0.0 : ttfqHomePageDTO.getMyBonus());

            ttfqHomePageDTOList.add(ttfqHomePageDTO);
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
        ttfqResultPage.setDatas(ttfqHomePageDTOList);
        return ttfqResultPage;
    }

    /**
     * 检查用户是否登录
     * @param bean
     * @return 0 未登录，1 登录
     */
    private int isLogin(ActivityBean bean) {
        int isLogin = 0;
        BaseReq<BaseBean> userReq = new BaseReq<>(bean, SysCodeConstant.ACTIVITYCENTER);
        BaseResp<BaseBean> userResp = userBaseInterface.checkLogin(userReq);

        if (BusiCode.SUCCESS.equals(userResp.getCode())) {
            isLogin = 1;
        }

        return isLogin;
    }

    /**
     * 查询天天分钱活动方案
     * @param bean
     * @return
     */
    private TtfqPage<List<ProjQvodPojo>> queryTtfqOrders(ActivityBean bean) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        if(hour >= 19) {
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        now.set(Calendar.HOUR_OF_DAY, 19);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);

        PageHelper.startPage(bean.getPn(),bean.getPs());
        List<ProjQvodPojo> projQvodPojoList = projQvodMapper.queryQvodOrders(format.format(now.getTime()));
        PageInfo<ProjQvodPojo> pageInfo = new PageInfo<>(projQvodPojoList);

        TtfqPage<List<ProjQvodPojo>> ttfqPage = new TtfqPage<>();
        ttfqPage.setPageNumber(bean.getPn());
        ttfqPage.setPageSize(bean.getPs());
        ttfqPage.setTotalPages(pageInfo.getPages());
        ttfqPage.setTotalRecords(pageInfo.getTotal());

        ttfqPage.setDatas(projQvodPojoList);

        return ttfqPage;
    }

    /**
     * 天天分钱方案详情
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public TtfqDetailDTO ttfqDetail(ActivityBean bean) throws Exception {
        TtfqDetailDTO detailDTO = new TtfqDetailDTO();
        // 查询参与人数
        Integer joinCounts = qvodTtfqMapper.queryJoinCounts(bean.getProjId());
        // 查询天天分钱活动某一方案详情
        ProjQvodPojo detail = projQvodMapper.queryDetail(bean.getProjId());
        if (detail == null) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_TTFQ_QUERY_FAIL));
            bean.setBusiErrDesc("查询方案详情失败");
            return null;
        }
        BeanUtilWrapper.copyPropertiesIgnoreNull(detail, detailDTO);

        detailDTO.setProjId(bean.getProjId());
        detailDTO.setGameId(bean.getGameId());
        detailDTO.setBeginTime(detail.getAddDate());
        detailDTO.setJoinCounts(joinCounts);
        detailDTO.setAwardTime(TimeUtil.customDateTime(TimeUtil.parserDateTime(detail.getActivityDate()),"MM-dd") + " " + timeMap.get(bean.getGameId()));
        if (2 == detail.getAward().intValue() && 0 == detail.getBonus().intValue()) {
            detailDTO.setBonus(1.0);
            detailDTO.setWinInfo("0,0,0,0,0,0,1");
        }

        String awardCode = "";
        if(detail.getPeriodId() != null) {
            String path = FileConstant.APP_PATH + bean.getGameId() + "/l300.xml";
            JXmlWrapper wapper = JXmlWrapper.parse(new File(path));
            List<JXmlWrapper> nodes = wapper.getXmlNodeList("row");
            for(JXmlWrapper node : nodes) {
                if(detail.getPeriodId().equals(node.getStringValue("@pid"))) {
                    awardCode = node.getStringValue("@acode");
                    break;
                }
            }
        }
        detailDTO.setAwardCode(awardCode);

        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
        return detailDTO;
    }

    /**
     * 参与天天分钱
     * @param bean
     * @throws Exception
     */
    @Transactional
    @Override
    public void ttfeJoin(ActivityBean bean) throws Exception {
        // 身份证检测
        UserPojo userPojo = userBasicInfoWrapper.queryUserInfo(bean, log, SysCodeConstant.ACTIVITYCENTER);
        if (userPojo == null || StringUtil.isEmpty(userPojo.getIdcard()) || StringUtil.isEmpty(userPojo.getRealName())) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_TTFQ_UNBIND_IDCARD));
            bean.setBusiErrDesc("绑定身份证后才能参与该活动");
            return;
        }

        Calendar activityDate = Calendar.getInstance();
        Date endTime = qvodTtfqMapper.queryEndTimeByProjId(bean.getProjId());
        if (endTime != null) {
            activityDate.setTime(endTime);
        }
        activityDate.set(Calendar.HOUR_OF_DAY, 19);
        activityDate.set(Calendar.MINUTE, 0);
        activityDate.set(Calendar.SECOND, 0);
        activityDate.set(Calendar.MILLISECOND, 0);
        long endTimeMillis = activityDate.getTimeInMillis();
        long now = Calendar.getInstance().getTimeInMillis();
        if(now >= endTimeMillis) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_TTFQ_OUTOFDATE));
            bean.setBusiErrDesc("方案已过期，请参加下一期");
            return;
        }

        // 检测用户是否已经参与过某天的分钱活动
        int count = qvodTtfqMapper.queryByNickidAndProjId(bean.getUid(), bean.getProjId());
        if(count > 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_TTFQ_HAVEN_JOIN));
            bean.setBusiErrDesc("不能重复参与哦");
            return;
        }

        // 插入数据
        count = qvodTtfqMapper.insertJoin(bean.getProjId(), bean.getGameId(), bean.getUid());
        if(count == 1) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("参与成功");
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_TTFQ_JOIN_FAIL));
            bean.setBusiErrDesc("参与成功");
        }
    }
}
