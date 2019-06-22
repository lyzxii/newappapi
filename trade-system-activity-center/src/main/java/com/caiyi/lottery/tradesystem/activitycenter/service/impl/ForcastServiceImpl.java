package com.caiyi.lottery.tradesystem.activitycenter.service.impl;

import activity.bean.ActivityBean;
import activity.dto.ForcastUserDTO;
import activity.pojo.ForcastMatchPojo;
import activity.dto.ForcastDTO;
import activity.pojo.ForcastPojo;
import activity.pojo.ForcastShareUserPojo;
import activity.pojo.ForcastUserPojo;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.activitycenter.dao.ForcastMapper;
import com.caiyi.lottery.tradesystem.activitycenter.service.ForcastService;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.util.MatchUtils;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 拉新活动-预测比分Service
 *
 * @author GJ
 * @create 2018-04-23 10:09
 **/
@Slf4j
@Service
public class ForcastServiceImpl implements ForcastService {

    @Autowired
    private ForcastMapper forcastMapper;
    @Autowired
    private RedisClient redisClient;

    private final static String IMGPATH = "/data/qtjsbf/topic/pic/team/";
    private final static String IMG_SUFFIX = ".png";

    @Override
    public void shareForcast(ActivityBean activityBean) {
        int count = forcastMapper.getShareUserCount(activityBean.getUid());
        if (count == 0) {
            ForcastShareUserPojo forcastShareUserPojo = new ForcastShareUserPojo();
            if (!StringUtil.isEmpty(activityBean.getUid())) {
                forcastShareUserPojo.setNickid(activityBean.getUid());
                forcastShareUserPojo.setAddtime(new Date());
                forcastShareUserPojo.setAppclient("9188彩票");
                forcastShareUserPojo.setIpaddress(activityBean.getIpAddr());
                forcastShareUserPojo.setMobiletype(activityBean.getMobiletype());
                forcastShareUserPojo.setSource(activityBean.getSource() + "");
                forcastMapper.addShareUser(forcastShareUserPojo);
            }else {
                activityBean.setBusiErrDesc("用户名为空,不能发起分享");
                activityBean.setBusiErrCode(Integer.valueOf(BusiCode.ACTIVITY_FORCAST_UID_EMPTY));
            }
        }
    }
    @Override
    public ForcastDTO forcastShareUserPage(ActivityBean activityBean) {
        ForcastDTO forcastDTO = new ForcastDTO();
        ForcastMatchPojo forcastMatchPojo = forcastMapper.getCurrnetForcastMatch();
        if (forcastMatchPojo != null) {
            forcastDTO.setItemid(forcastMatchPojo.getItemid());
            forcastDTO.setHomeTeamName(forcastMatchPojo.getMname());
            forcastDTO.setAwayTeamName(forcastMatchPojo.getSname());
            forcastDTO.setMatchTime(forcastMatchPojo.getMatchtime());
        }
        if (!StringUtil.isEmpty(activityBean.getUid())) {
            CacheBean cacheBean = new CacheBean();
            cacheBean.setKey(activityBean.getUid() + "_user_photo");
            String photo = redisClient.getString(cacheBean, log, SysCodeConstant.ACTIVITYCENTER);
            if (StringUtil.isEmpty(photo)) {
                photo = forcastMapper.getShareUserImg(activityBean.getUid());
            }
            forcastDTO.setUserImgUrl(photo);
            ForcastPojo forcastPojo = forcastMapper.getForcastNum(activityBean.getUid());
            Integer totaluser = forcastPojo.getForcastNum() == null ? 0 : forcastPojo.getForcastNum();
            Integer totalaward = forcastPojo.getForcastAwardNum() == null ? 0 : forcastPojo.getForcastAwardNum();
            Integer totalonway = (totaluser - totalaward) * 5;

            forcastDTO.setTotaluser(String.valueOf(totaluser));
            forcastDTO.setTotalaward(String.valueOf(totalaward));
            forcastDTO.setTotalonway(String.valueOf(totalonway));

            List<ForcastUserDTO> forcastUserDTOList = new ArrayList<>();

            List<ForcastUserPojo> forcastUserPojoList = forcastMapper.queryForcastUser(activityBean.getUid());
            if (forcastUserPojoList == null || forcastUserPojoList.isEmpty()) {
                activityBean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                activityBean.setBusiErrDesc("暂时没人哦，快去邀请吧～");
                return forcastDTO;
            }
            for (ForcastUserPojo forcastUserPojo : forcastUserPojoList) {
                ForcastUserDTO forcastUserDTO = new ForcastUserDTO();
                forcastUserDTO.setNickName(forcastUserPojo.getNickName());
                forcastUserDTO.setUserImgUrl(forcastUserPojo.getUserImgUrl());
                forcastUserDTO.setAddTime(TimeUtil.customDateTime(TimeUtil.parserDateTime(forcastUserPojo.getAddTime()), "MM-dd HH:mm:ss"));
                String forcast = forcastUserPojo.getForcastContent();
                String itemid = forcastUserPojo.getItemId();
                String[] names = MatchUtils.getTeamName(itemid, log);
                if ("3".equals(forcast)) {
                    forcastUserDTO.setForcastcontent(names[0]);
                    forcastUserDTO.setForcast("3");
                } else if ("1".equals(forcast)) {
                    forcastUserDTO.setForcastcontent("");
                    forcastUserDTO.setForcast("1");
                } else {
                    forcastUserDTO.setForcastcontent(names[1]);
                    forcastUserDTO.setForcast("0");
                }
                forcastUserDTOList.add(forcastUserDTO);
            }
            forcastDTO.setUserList(forcastUserDTOList);
            }
        return forcastDTO;
    }


    //###################

    /**
     * 查询邀请历史
     * @param bean
     * @return
     */
    @Override
    public List<ForcastDTO> queryInvitationHistory(ActivityBean bean) {
        List<ForcastDTO> forcastDTOList = new ArrayList<>();
        ForcastDTO forcastDTO;
        if (StringUtil.isEmpty(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_THIRD_GAME_PARAM_ERROR));
            bean.setBusiErrDesc("用户名为空");
            log.error("查询邀请历史失败，用户名为空");
            return forcastDTOList;
        }

        // 查询历史邀请场次
        List<ForcastPojo> matchList = forcastMapper.queryMatchesByNickid(bean.getUid());
        if (matchList == null || matchList.isEmpty()) {
            log.info("[{}] 无历史邀请场次", bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("暂无历史邀请场次");
            return forcastDTOList;
        }

        for (ForcastPojo forcast : matchList) {
            forcastDTO = new ForcastDTO();
            forcastDTO.setHomeTeamName(forcast.getHomeTeamName());
            forcastDTO.setAwayTeamName(forcast.getAwayTeamName());
            // 未开奖
            if (forcast.getMatchResult() == null) {
                forcastDTO.setState(0);
                forcastDTOList.add(forcastDTO);
                continue;
            }

            forcastDTO.setHomeScore(forcast.getHomeScore());
            forcastDTO.setAwayScore(forcast.getAwayScore());
            // 红包在路上
            if (forcast.getForcastNum() != null && forcast.getForcastAwardNum() != null && forcast.getForcastAwardNum() != forcast.getForcastNum()) {
                forcastDTO.setState(1);
                forcastDTO.setAward((forcast.getForcastNum() - forcast.getForcastAwardNum()) * 5);
                forcastDTOList.add(forcastDTO);
                continue;
            }

            if (forcast.getForcastNum() != null) {
                forcastDTO.setState(2);
                forcastDTO.setInvitationNum(forcast.getForcastNum());
                forcastDTO.setAward(forcast.getHaveAward());
                forcastDTOList.add(forcastDTO);
            }
        }

        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
        return forcastDTOList;
    }

    /**
     * 查询邀请详细
     * @param bean
     * @return
     */
    @Override
    public ForcastDTO queryInvitationDetail(ActivityBean bean) {
        ForcastDTO forcastDTO = new ForcastDTO();
        List<ForcastUserDTO> forcastUserDTOList = new ArrayList<>();
        ForcastUserDTO forcastUserDTO;
        if (StringUtil.isEmpty(bean.getUid()) || bean.getMatchId() == null) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_THIRD_GAME_PARAM_ERROR));
            bean.setBusiErrDesc("参数错误");
            log.error("查询邀请历史失败，用户名{}，比赛id{}", bean.getUid(), bean.getMatchId());
            return forcastDTO;
        }
        // 查询详细数据
        ForcastPojo forcastPojo = forcastMapper.queryMatchByNickidAndMatchid(bean.getUid(), bean.getMatchId());

        if (forcastPojo == null) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_FORCAST_QUERY_DETAIL_FAIL));
            bean.setBusiErrDesc("查询邀请详细失败");
            log.error("查询邀请详细失败，用户名{}，比赛id{}", bean.getUid(), bean.getMatchId());
            return forcastDTO;
        }
        forcastDTO.setHomeTeamName(forcastPojo.getHomeTeamName());
        forcastDTO.setAwayTeamName(forcastPojo.getAwayTeamName());
        forcastDTO.setHomeScore(forcastPojo.getHomeScore());
        forcastDTO.setAwayScore(forcastPojo.getAwayScore());
        forcastDTO.setInvitationNum(forcastPojo.getForcastAwardNum());
        forcastDTO.setAward(forcastPojo.getHaveAward());
        forcastDTO.setUngetAward((forcastPojo.getForcastNum() - forcastPojo.getForcastAwardNum()) * 5);
        forcastDTO.setOutOfDateAward(forcastPojo.getOutOfDateAward());

        String teamIds[] = MatchUtils.getTeamId(forcastPojo.getItemId().toString(), log);
        if (teamIds == null || teamIds.length != 2) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_FORCAST_GET_TEAMID_FAIL));
            bean.setBusiErrDesc("球队id获取失败");
            log.error("球队id获取失败，用户名{}，比赛id{}，期次id{}", bean.getUid(), bean.getMatchId(), forcastPojo.getItemId());
            return forcastDTO;
        }
        forcastDTO.setHomeTeamImgUrl(IMGPATH + teamIds[0] + IMG_SUFFIX);
        forcastDTO.setAwayTteamImgUrl(IMGPATH + teamIds[1] + IMG_SUFFIX);

        List<ForcastUserPojo> forcastUserPojoList = forcastMapper.queryForcastUser(bean.getUid());
        if (forcastUserPojoList == null || forcastUserPojoList.isEmpty()) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("暂时没人哦，快去邀请吧～");
            return forcastDTO;
        }
        for (ForcastUserPojo forcastUserPojo : forcastUserPojoList) {
            forcastUserDTO = new ForcastUserDTO();
            forcastUserDTO.setNickName(forcastUserPojo.getNickName());
            forcastUserDTO.setUserImgUrl(forcastUserPojo.getUserImgUrl());
            forcastUserDTO.setAddTime(TimeUtil.customDateTime(TimeUtil.parserDateTime(forcastUserPojo.getAddTime()),"MM-dd HH:mm:ss"));

            if (forcastUserPojo.getIsNew() == 0) {
                forcastUserDTO.setState(2);
            } else if (Math.abs(TimeUtil.timeDiff(forcastUserPojo.getAddTime())) > 3 ) {
                forcastUserDTO.setState(3);
            } else if (forcastUserPojo.getIsLogin() == 1) {
                forcastUserDTO.setState(0);
            } else {
                forcastUserDTO.setState(1);
            }

            forcastUserDTOList.add(forcastUserDTO);
        }
        forcastDTO.setUserList(forcastUserDTOList);

        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
        return forcastDTO;
    }

    /**
     * 预测首页
     * @param bean
     * @return
     */
    @Override
    public ForcastDTO forcast(ActivityBean bean) {
        ForcastDTO forcastDTO = new ForcastDTO();
        ForcastMatchPojo forcastMatchPojo = forcastMapper.getCurrnetForcastMatch();
        if (forcastMatchPojo != null) {
            forcastDTO.setItemid(forcastMatchPojo.getItemid());
            forcastDTO.setHomeTeamName(forcastMatchPojo.getMname());
            forcastDTO.setAwayTeamName(forcastMatchPojo.getSname());
            forcastDTO.setMatchTime(forcastMatchPojo.getMatchtime());
            forcastDTO.setEndTime(forcastMatchPojo.getEndTime());

            setTeamImg(forcastDTO, bean);
        }
        return forcastDTO;
    }

    /**
     * 设置球队图片
     * @param forcastDTO
     */
    private void setTeamImg(ForcastDTO forcastDTO, BaseBean bean) {
        String teamIds[] = MatchUtils.getTeamId(forcastDTO.getItemid(), log);
        if (teamIds == null || teamIds.length != 2) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_FORCAST_GET_TEAMID_FAIL));
            bean.setBusiErrDesc("球队id获取失败");
            log.error("球队id获取失败，用户名{}，期次id{}", bean.getUid(), forcastDTO.getItemid());
            return;
        }
        forcastDTO.setHomeTeamImgUrl(IMGPATH + teamIds[0] + IMG_SUFFIX);
        forcastDTO.setAwayTteamImgUrl(IMGPATH + teamIds[1] + IMG_SUFFIX);
    }
}
