package com.caiyi.lottery.tradesystem.datacenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.datacenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.datacenter.service.DataCenterService;
import com.caiyi.lottery.tradesystem.datacenter.service.MatchFollowSerivce;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import data.bean.DataBean;
import data.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-16 15:08
 **/
@Slf4j
@RestController
public class DataCenterController {
    @Autowired
    private DataCenterService dataCenterService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;
    @Autowired
    private MatchFollowSerivce matchFollowSerivce;

    @RequestMapping(value = "/data/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("数据中心data-center启动运行正常");
        return response;
    }

    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/data/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_data");
        redisClient.exists(cacheBean,log, SysCodeConstant.DATACENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("数据中心服务运行正常");
        return response;
    }

    /**
     * 获取关注列表
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/getMatchFollowList.api")
    public  BaseResp<FollowMatchInfoDTO> getMatchFollow(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        baseResp.setCode(bean.getBusiErrCode()+"");
        baseResp.setDesc(bean.getBusiErrDesc());
        FollowMatchInfoDTO followMatchInfoDTO = matchFollowSerivce.getMatchFollw(bean);
        baseResp.setData(followMatchInfoDTO);
        return baseResp;
    }

    /**
     * 查询情报详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/query_intelligence_details.api")
    public String queryIntelligenceDetails(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.queryIntelligenceDetails(bean);
    }

    /**
     * 对情报详情进行评论
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/comment_to_intelligence.api")
    public String commentToIntelligence(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.commentToIntelligence(bean);
    }

    /**
     * 举报评论
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/report_comment.api")
    public String reportComment(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.reportComment(bean);
    }

    /**
     * 对球评进行评论
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/reply_to_comment.api")
    public String replyToComment(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.replyToComment(bean);
    }

    /**
     * 评论点赞
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/praise_to_comment.api")
    public String praiseToComment(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.praiseToComment(bean);
    }

    /**
     * 查询球评详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/query_comment_details.api")
    public String queryCommentDetails(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.queryCommentDetails(bean);
    }

    /**
     * 评论、回复
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/reply.api")
    public String replay(@RequestBody BaseReq<DataBean> baseReq) {
        String result;
        try {
            DataBean bean = baseReq.getData();
            result = dataCenterService.replay(bean);
        } catch (Exception e) {
            log.error("球评回复接口调用失败", e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "球评回复接口调用失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 球评列表
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/query_comments.api")
    public String queryComments(@RequestBody BaseReq<DataBean> baseReq) {
        String result;
        try {
            DataBean bean = baseReq.getData();
            result = dataCenterService.queryComments(bean);
        } catch (Exception e) {
            log.error("球评列表获取失败",e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "球评列表获取失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 查询球评点赞用户
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/query_user_praise.api")
    public String queryUserPraise(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        return dataCenterService.queryUserPraise(bean);
    }

    /**
     * 足球头部
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/foot_title.api")
    public BaseResp footballTitleData(@RequestBody BaseReq<DataBean> baseReq){
        BaseResp rsp = null;
        try{
            if(null != baseReq && null != baseReq.getData()){
                rsp = dataCenterService.getFootballTitleData(baseReq.getData());
            }
        } catch (Exception e) {
            log.error("足球头部文件异常，matchID：" + baseReq.getData().getMatchId(), e);
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("查询失败");
        }
        return rsp;
    }

    /**
     * 篮球单场即时比分
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/basket_title.api")
    public BaseResp basketScore(@RequestBody BaseReq<DataBean> baseReq){
        BaseResp rsp = null;
        try {
            if(null != baseReq && null != baseReq.getData()){
                rsp = dataCenterService.getBasketScore(baseReq.getData());
            }
        } catch (Exception e) {
            log.error("获取篮球单场即时比分异常，matchID：" + baseReq.getData().getMatchId(), e);
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("查询失败");
        }
        return rsp;
    }

    /**
     * 关注/取消关注篮球比赛
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/match_follow.api")
    public BaseResp matchFollow(@RequestBody BaseReq<DataBean> baseReq){
        BaseResp rsp = null;
        if(null != baseReq && null != baseReq.getData()){
            rsp = dataCenterService.matchFollow(baseReq.getData());
        }
        return rsp;
    }

    /**
     * 查询足球直播源
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/foot_live.api")
    BaseResp<List<LiveSourceDTO>> footLive(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        BaseResp<List<LiveSourceDTO>> baseResp = new BaseResp<>();
        try {
            List<LiveSourceDTO> data = dataCenterService.footLive(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("查询足球直播源失败，[matchid:{}]", bean.getMatchId());
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询足球直播源失败");
        }
        return baseResp;
    }
    /**
     * 查询篮球直播源
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/basket_live.api")
    BaseResp<List<LiveSourceDTO>> basketLive(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        BaseResp<List<LiveSourceDTO>> baseResp = new BaseResp<>();
        try {
            List<LiveSourceDTO> data = dataCenterService.basketLive(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("查询篮球直播源失败，[matchid:{}]", bean.getMatchId());
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询篮球直播源失败");
        }
        return baseResp;
    }
    /**
     * 查询赔率
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/odds.api")
    BaseResp<OddsDTO> odds(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        BaseResp<OddsDTO> baseResp = new BaseResp<>();
        try {
            OddsDTO data = dataCenterService.odds(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("查询赔率失败，[matchid:{}]", bean.getMatchId());
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询篮球直播源失败");
        }
        return baseResp;
    }

    /**
     * 查询球队近10场比赛
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/data/query_team_match_history.api")
    public BaseResp<List<QueryTeamDTO>> queryTeamMatchHistory(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        BaseResp<List<QueryTeamDTO>> baseResp = new BaseResp<>();
        try {
            List<QueryTeamDTO> data = dataCenterService.queryTeamMatchHistory(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("查询失败，[teamId:{}]", bean.getTeamId());
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }

    /**
     * 查询指定场次比赛实况数据
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/data/query_round_info.api")
    public BaseResp<RoundInfoDTO> queryRoundInfo(@RequestBody BaseReq<DataBean> baseReq) {
        DataBean bean = baseReq.getData();
        BaseResp<RoundInfoDTO> baseResp = new BaseResp<>();
        try {
            RoundInfoDTO data = dataCenterService.queryRoundInfo(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("查询失败，[ids:{}]", bean.getIds());
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }
}
