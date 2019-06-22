package com.caiyi.lottery.tradesystem.datacenter.service.impl;

import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.datacenter.dao.MatchFollowMapper;
import com.caiyi.lottery.tradesystem.datacenter.service.MatchFollowSerivce;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import data.bean.DataBean;
import data.dto.FollowMatchInfoDTO;
import data.dto.MatchDTO;
import data.dto.MatchDetailDTO;
import data.pojo.MatchFollowPojo;
import data.utils.DataConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author GJ
 * @create 2018-01-18 10:37
 **/
@Slf4j
@Service
public class MatchFollowSerivceImpl implements MatchFollowSerivce {
    @Autowired
    private MatchFollowMapper matchFollowMapper;
    @Autowired
    private RedisClient redisClient;
    @Override
    public FollowMatchInfoDTO getMatchFollw(DataBean bean) {
        FollowMatchInfoDTO followMatchInfoDTO = new FollowMatchInfoDTO();
        List<MatchFollowPojo> matchFollowPojoList = matchFollowMapper.queryMatchFollow(bean.getUid());
        //关键参数isall 区分关注是关注列表的，还是对阵中的关注比赛 isall=1 关注列表，isall=0 对阵中的关注
        if (StringUtils.isNotEmpty(bean.getGameId())) {
            JXmlWrapper newzlk_finish_xml = null;
            JXmlWrapper newzlk_unfinish_xml = null;
            CacheBean cacheBean = new CacheBean();
            //足球
            if ("0".equals(bean.getGameId())) {
                //竞彩
                if (bean.getGameType().intValue()==70) {
                    cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_JC_UNFINISH);
                    newzlk_unfinish_xml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.DATACENTER);
                    cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_JC_FINISH);
                    newzlk_finish_xml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.DATACENTER);
                }
                //北单
                else if (bean.getGameType().intValue() == 85) {
                    cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_BD_UNFINISH);
                    newzlk_unfinish_xml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.DATACENTER);
                    cacheBean.setKey(DataConstants.NEWZLK_FOOTBALL_BD_FINISH);
                    newzlk_finish_xml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.DATACENTER);
                }
            }
            //篮球
            else if ("1".equals(bean.getGameId())) {
                cacheBean.setKey(DataConstants.NEWZLK_BASKETBALL_UNFINISH);
                newzlk_unfinish_xml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.DATACENTER);
                cacheBean.setKey(DataConstants.NEWZLK_BASKETBALL_FINISH);
                newzlk_finish_xml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.DATACENTER);
            }
            List<String> matchIdList = new ArrayList<>();
            if (matchFollowPojoList != null) {
                for (MatchFollowPojo matchFollowPojo : matchFollowPojoList) {
                    //注意篮球要给Gtype值
                    if (matchFollowPojo.getIgameid().equals(bean.getGameId())&&matchFollowPojo.getIgametype().equals(String.valueOf(bean.getGameType()))) {
                        matchIdList.add(matchFollowPojo.getImatchid());
                    }
                }
            }
            if (bean.getIsAll() ==null){
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("参数错误");
                return null;
            }
            List<MatchDetailDTO> matchDetailDTOList = new ArrayList<>();
            if (bean.getIsAll() == 0) {
                MatchDetailDTO matchDetailDTO = new MatchDetailDTO();
                List<MatchDTO> matchDTOList = new ArrayList<>();
                for (String id : matchIdList) {
                    MatchDTO matchDTO = new MatchDTO();
                    matchDTO.setRid(id);
                    matchDTOList.add(matchDTO);
                }
                matchDetailDTO.setMatchs(matchDTOList);
                matchDetailDTOList.add(matchDetailDTO);
            }else {
                List<JXmlWrapper> jXmlWrapperList_unfinish = newzlk_unfinish_xml.getXmlNodeList("rows");
                List<MatchDetailDTO> matchDetailDTOList_unfinish = xml2Obj(matchIdList,bean, jXmlWrapperList_unfinish);

                List<JXmlWrapper> jXmlWrapperList_finish = newzlk_finish_xml.getXmlNodeList("rows");
                List<MatchDetailDTO> matchDetailDTOList_finish = xml2Obj(matchIdList,bean, jXmlWrapperList_finish);


                Map<String, List<MatchDTO>> listMap = new LinkedHashMap<>();
                for (MatchDetailDTO matchDetailDTO : matchDetailDTOList_finish) {
                    Collections.reverse(matchDetailDTO.getMatchs());
                    listMap.put(matchDetailDTO.getDesc(), matchDetailDTO.getMatchs());
                }
                for (MatchDetailDTO matchDetailDTO : matchDetailDTOList_unfinish) {
                    if (listMap.containsKey(matchDetailDTO.getDesc())) {
                        List<MatchDTO> tem = listMap.get(matchDetailDTO.getDesc());
                        tem.addAll(matchDetailDTO.getMatchs());
                        listMap.put(matchDetailDTO.getDesc(), tem);
                    } else {
                        listMap.put(matchDetailDTO.getDesc(),  matchDetailDTO.getMatchs());
                    }
                }
                for (Map.Entry<String, List<MatchDTO>> entry : listMap.entrySet()) {
                    MatchDetailDTO matchDetailDTO = new MatchDetailDTO();
                    matchDetailDTO.setDesc(entry.getKey());
                    matchDetailDTO.setMatchs(entry.getValue());
                    matchDetailDTOList.add(matchDetailDTO);
                }
            }
            followMatchInfoDTO.setLogourl("0".equals(bean.getGameId()) ? "/newzlk/img/team/" : "/lqzlk/img/team/mobile/");
            followMatchInfoDTO.setTitle("1".equals(bean.getGameId()) ? "篮球" : (bean.getGameType() == 70 ? "竞彩足球" : "北单"));
            followMatchInfoDTO.setTime(DateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            followMatchInfoDTO.setMatchdetail(matchDetailDTOList);
        }
        bean.setBusiErrDesc("查询成功");
        return followMatchInfoDTO;
    }

    private List<MatchDetailDTO> xml2Obj( List<String> matchIdList,DataBean bean,List<JXmlWrapper> jXmlWrapperList) {
        List<MatchDetailDTO> matchDetailDTOList = new ArrayList<>();
        for (JXmlWrapper jXmlWrapper : jXmlWrapperList) {
            MatchDetailDTO matchDetailDTO = new MatchDetailDTO();
            List<MatchDTO> matchDTOList = new ArrayList<>();
            List<JXmlWrapper> unfinish = jXmlWrapper.getXmlNodeList("row");
            for (JXmlWrapper jXmlWrapper1 : unfinish) {
                MatchDTO matchDTO = setMatchDTO(bean.getGameId(), jXmlWrapper1);
                if (matchIdList.contains(matchDTO.getRid())) {
                    matchDTOList.add(matchDTO);
                }
            }
            if (!matchDTOList.isEmpty()) {
                matchDetailDTO.setDesc(jXmlWrapper.getStringValue("@desc"));
                matchDetailDTO.setMatchs(matchDTOList);
                matchDetailDTOList.add(matchDetailDTO);
            }
        }
        return matchDetailDTOList;
    }

    private MatchDTO setMatchDTO(String gameid,JXmlWrapper jXmlWrapper){
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setRid(jXmlWrapper.getStringValue("@rid"));
        if ("0".equals(gameid)) {
            matchDTO.setSid(jXmlWrapper.getStringValue("@sid"));
        } else {
            matchDTO.setSid(jXmlWrapper.getStringValue("@mid"));
        }
        matchDTO.setLid(jXmlWrapper.getStringValue("@lid"));
        matchDTO.setLn(jXmlWrapper.getStringValue("@ln"));
        matchDTO.setTime(jXmlWrapper.getStringValue("@time"));
        matchDTO.setHtime(jXmlWrapper.getStringValue("@htime"));
        matchDTO.setHn(jXmlWrapper.getStringValue("@hn"));
        matchDTO.setGn(jXmlWrapper.getStringValue("@gn"));
        matchDTO.setHomeRank(jXmlWrapper.getStringValue("@homeRank"));
        matchDTO.setGuestRank(jXmlWrapper.getStringValue("@guestRank"));
        matchDTO.setHid(jXmlWrapper.getStringValue("@hid"));
        matchDTO.setGid(jXmlWrapper.getStringValue("@gid"));
        matchDTO.setHsc(jXmlWrapper.getStringValue("@hsc"));
        matchDTO.setAsc(jXmlWrapper.getStringValue("@asc"));
        matchDTO.setHalfsc(jXmlWrapper.getStringValue("@halfsc"));
        matchDTO.setType(jXmlWrapper.getStringValue("@type"));
        matchDTO.setJn(jXmlWrapper.getStringValue("@jn"));
        matchDTO.setRoundItemId(jXmlWrapper.getStringValue("@roundItemId"));
        matchDTO.setQc(jXmlWrapper.getStringValue("@qc"));
        matchDTO.setSort(jXmlWrapper.getStringValue("@sort"));
        matchDTO.setTvlive(jXmlWrapper.getStringValue("@tvlive"));
        matchDTO.setIsfriendly(jXmlWrapper.getStringValue("@isfriendly"));
        matchDTO.setIsfiveleague(jXmlWrapper.getStringValue("@isfiveleague"));
        if ("0".equals(gameid)) {
            matchDTO.setCg(jXmlWrapper.getStringValue("@cg"));
        } else {
            matchDTO.setCg(jXmlWrapper.getStringValue("@sg"));

        }
        matchDTO.setOdds(jXmlWrapper.getStringValue("@odds"));
        matchDTO.setRq(jXmlWrapper.getStringValue("@rq"));
        matchDTO.setIaudit(jXmlWrapper.getStringValue("@iaudit"));

        matchDTO.setSt(jXmlWrapper.getStringValue("@st"));
        matchDTO.setDown(jXmlWrapper.getStringValue("@down"));
        matchDTO.setDx(jXmlWrapper.getStringValue("@dx"));
        matchDTO.setRf(jXmlWrapper.getStringValue("@rf"));

        return matchDTO;
    }
}
