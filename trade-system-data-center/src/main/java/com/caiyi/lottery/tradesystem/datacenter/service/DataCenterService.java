package com.caiyi.lottery.tradesystem.datacenter.service;

import com.caiyi.lottery.tradesystem.base.BaseResp;
import data.bean.DataBean;
import data.dto.LiveSourceDTO;
import data.dto.OddsDTO;
import data.dto.QueryTeamDTO;
import data.dto.RoundInfoDTO;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-16 17:08
 **/
public interface DataCenterService {
    /**
     * 查询情报详情
     * @param bean
     * @return
     */
    String queryIntelligenceDetails(DataBean bean);

    /**
     * 对情报详情进行评论
     * @param bean
     * @return
     */
    String commentToIntelligence(DataBean bean);

    /**
     * 举报评论
     * @param bean
     * @return
     */
    String reportComment(DataBean bean);

    /**
     * 对球评就行评论
     * @param bean
     * @return
     */
    String replyToComment(DataBean bean);

    /**
     * 点赞评论
     * @param bean
     * @return
     */
    String praiseToComment(DataBean bean);

    /**
     * 查询评论详细信息
     * @param bean
     * @return
     */
    String queryCommentDetails(DataBean bean);

    /**
     * 查询评论点赞用户
     * @param bean
     * @return
     */
    String queryUserPraise(DataBean bean);

    /**
     * 评论、回复
     * @param bean
     * @return
     */
    String replay(DataBean bean) throws Exception;

    /**
     * 球评列表
     * @param bean
     * @return
     */
    String queryComments(DataBean bean) throws Exception;

    /**
     * 获取足球头部信息
     * @param bean
     * @return
     */
    BaseResp getFootballTitleData(DataBean bean) throws Exception;

    /**
     * 获取篮球单场即时比分
     * @param data
     * @return
     */
    BaseResp getBasketScore(DataBean data) throws Exception;

    /**
     * 关注/取消关注篮球比赛
     * @param data
     * @return
     */
    BaseResp matchFollow(DataBean data);

    /**
     * 查询足球直播源
     * @param bean
     * @return
     */
    List<LiveSourceDTO> footLive(DataBean bean) throws Exception;

    /**
     * 查询篮球直播源
     * @param bean
     * @return
     * @throws Exception
     */
    List<LiveSourceDTO> basketLive(DataBean bean) throws Exception;

    /**
     * 查询赔率
     * @param bean
     * @return
     * @throws Exception
     */
    OddsDTO odds(DataBean bean) throws Exception;

    /**
     * 查询球队近10场比赛
     * @param bean
     * @return
     * @throws Exception
     */
    List<QueryTeamDTO> queryTeamMatchHistory(DataBean bean) throws Exception;

    /**
     * 查询指定场次比赛实况数据
     * @param bean
     * @return
     * @throws Exception
     */
    RoundInfoDTO queryRoundInfo(DataBean bean) throws Exception;
}
