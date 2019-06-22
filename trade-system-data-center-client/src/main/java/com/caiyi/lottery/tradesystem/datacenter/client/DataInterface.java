package com.caiyi.lottery.tradesystem.datacenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import data.bean.DataBean;
import data.dto.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient(name = "tradecenter-system-data-center")
public interface DataInterface {
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/data/checkhealth.api")
    Response checkHealth() ;

    @RequestMapping(value = "/data/getMatchFollowList.api")
    BaseResp<FollowMatchInfoDTO> getMatchFollow(@RequestBody BaseReq<DataBean> baseReq);

    /**
     * 查询情报详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/query_intelligence_details.api")
    String queryIntelligenceDetails(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 对情报详情进行评论
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/comment_to_intelligence.api")
    String commentToIntelligence(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 举报评论
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/report_comment.api")
    String reportComment(@RequestBody BaseReq<DataBean> request) throws Exception;

    /**
     * 对球评进行评论
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/reply_to_comment.api")
    String replyToComment(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 评论点赞
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/praise_to_comment.api")
    String praiseToComment(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 查询球评详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/query_comment_details.api")
    String queryCommentDetails(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 评论、回复
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/reply.api")
    String replay(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 球评列表
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/query_comments.api")
    String queryComments(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 查询球评点赞用户
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/query_user_praise.api")
    String queryUserPraise(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 足球头部数据
     * @param req
     * @return
     */
    @RequestMapping(value = "/data/foot_title.api")
    BaseResp footballTitleData(@RequestBody BaseReq<DataBean> req);

    /**
     * 篮球单场即时比分
     * @param req
     * @return
     */
    @RequestMapping(value = "/data/basket_title.api")
    BaseResp basketScore(@RequestBody BaseReq<DataBean> req);

    /**
     * 关注/取消关注篮球对阵
     * @param req
     * @return
     */
    @RequestMapping(value = "/data/match_follow.api")
    BaseResp matchFollow(@RequestBody BaseReq<DataBean> req);

    /**
     * 查询足球直播源
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/foot_live.api")
    BaseResp<List<LiveSourceDTO>> footLive(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 查询篮球直播源
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/basket_live.api")
    BaseResp<List<LiveSourceDTO>> basketLive(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 查询赔率
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/odds.api")
    BaseResp<OddsDTO> odds(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 查询球队近10场比赛
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/query_team_match_history.api")
    BaseResp<List<QueryTeamDTO>> queryTeamMatchHistory(@RequestBody BaseReq<DataBean> baseReq) throws Exception;

    /**
     * 查询指定场次比赛实况数据
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/query_round_info.api")
    BaseResp<RoundInfoDTO> queryRoundInfo(@RequestBody BaseReq<DataBean> baseReq) throws Exception;
}
