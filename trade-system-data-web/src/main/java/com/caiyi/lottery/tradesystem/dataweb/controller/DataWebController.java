package com.caiyi.lottery.tradesystem.dataweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.datacenter.client.DataInterface;
import com.caiyi.lottery.tradesystem.dataweb.service.DataService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import data.bean.DataBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author wxy
 * @create 2018-01-16 14:43
 **/
@Slf4j
@RestController
public class DataWebController {
    @Autowired
    private DataInterface dataInterface;
    @Autowired
    private UserBaseInterface userBaseInterface;
    @Autowired
    private DataService dataService;


    @RequestMapping(value = "/data/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("数据中心data-web启动运行正常");
        return response;
    }

    @RequestMapping(value = "/data/checkhealth.api")
    public Result checkHealth() {
        Response response = dataInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        log.info("=====检测数据中心服务=====");
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.DATAWEB)
    @RequestMapping(value = "/data/getMatchFollowList.api",method = RequestMethod.POST)
    public Result getMatchFollowList(DataBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.INTEGRALWEB);
        baseReq.setData(bean);
        BaseResp rsp = dataInterface.getMatchFollow(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 查询情报详情
     *
     * @param bean
     * @return
     */
    // @SetUserData
    @RequestMapping(value = "/data/query_intelligence_details.api")
    public String queryIntelligenceDetails(DataBean bean) {
        String result;
        try {
            dataService.setUserData(bean);
            BaseReq<DataBean> request = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.queryIntelligenceDetails(request);
        } catch (Exception e) {
            log.error("查询情报详情失败，[uid:{},matchId:{},intelligenceId:{}]", bean.getUid(), bean.getMatchId(), bean.getIntelligenceId(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "查询情报详情失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 对情报详情进行评论
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/comment_to_intelligence.api")
    public String commentToIntelligence(DataBean bean) {
        String result;
        try {
            result = dataService.checkLogin(bean);
            if (result != null) {
                return result;
            }

            BaseReq<DataBean> request = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.commentToIntelligence(request);

        } catch (Exception e) {
            log.error("情报详情进行评论失败[uid:{}]", bean.getUid(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "评论失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 举报评论
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/report_comment.api")
    public String reportComment(DataBean bean) {
        String result;
        try {
            result = dataService.checkLogin(bean);
            if (result != null) {
                return result;
            }
            BaseReq<DataBean> request = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.reportComment(request);
        } catch (Exception e) {
            log.error("评论失败，[uid:{}]", bean.getUid(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "评论失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 对球评进行评论
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/reply_to_comment.api")
    public String replyToComment(DataBean bean) {
        String result;
        try {
            result = dataService.checkLogin(bean);
            if (result != null) {
                return result;
            }
            BaseReq<DataBean> baseReq = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.replyToComment(baseReq);
        } catch (Exception e) {
            log.error("球评评论失败，[uid:{}]", bean.getUid(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "评论失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 评论点赞
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/praise_to_comment.api")
    public String praiseToComment(DataBean bean) {
        String result;
        try {
            result = dataService.checkLogin(bean);
            if (result != null) {
                return result;
            }
            BaseReq<DataBean> baseReq = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.praiseToComment(baseReq);
        } catch (Exception e) {
            log.error("点在失败，[uid:{}]", bean.getUid(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "点赞失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 查询球评详情
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/query_comment_details.api")
    public String queryCommentDetails(DataBean bean) {
        String result;
        try {
            dataService.setUserData(bean);
            BaseReq<DataBean> baseReq = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.queryCommentDetails(baseReq);
        } catch (Exception e) {
            log.error("查询球评详情失败，[uid:{},mathcId:{}]", bean.getUid(), bean.getMatchId(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "查询球评详情失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 评论/回复
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/reply.api")
    public String replay(DataBean bean) {
        String result;
        try {
            result = dataService.checkLogin(bean);
            if (result != null) {
                return result;
            }
            BaseReq<DataBean> baseReq = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.replay(baseReq);
        } catch (Exception e) {
            log.error("评论回复错误，[uid:{}]", bean.getUid(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "评论回复失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 球评列表
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/query_comments.api")
    public String queryComments(DataBean bean) {
        String result;
        try {
            dataService.setUserData(bean);
            BaseReq<DataBean> baseReq = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.queryComments(baseReq);
        } catch (Exception e) {
            log.error("球评列表获取失败，[uid:{},lotteryType:{},matchId:{}]", bean.getUid(), bean.getLotteryType(), bean.getMatchId(), e);
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "球评列表获取失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 查询球评点赞用户
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/query_user_praise.api")
    public String queryUserPraise(DataBean bean) {
        String result;
        try {
            dataService.setUserData(bean);
            BaseReq<DataBean> baseReq = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            result = dataInterface.queryUserPraise(baseReq);
        } catch (Exception e) {
            log.error("查询球评点赞用户失败，");
            JSONObject json = new JSONObject();
            json.put("code", BusiCode.FAIL);
            json.put("desc", "查询球评点赞用户失败");
            result = json.toString();
        }
        return result;
    }

    /**
     * 足球头部数据
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/foot_title.api")
    public Result footballTitleData(DataBean bean) {
        Result result = new Result<>();
        try {
            String status = dataService.checkLogin(bean);
            bean.setCheckLogin(false);
            if (null == status) {
                bean.setCheckLogin(true);
            }
            BaseReq<DataBean> req = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            BaseResp rsp = dataInterface.footballTitleData(req);
            if(null != rsp){
                result.setData(rsp.getData());
            }
            result.setCode(rsp.getCode());
            result.setDesc(rsp.getDesc());
        } catch (Exception e) {
            log.error("查询足球头部数据异常,{}",e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("系统异常,请稍后重试~");
        }
        return result;
    }

    /**
     * 篮球单场即时比分
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/basket_title.api")
    public Result basketScore(DataBean bean) {
        Result result = new Result<>();
        try {
            String status = dataService.checkLogin(bean);
            bean.setCheckLogin(false);
            if (null == status) {
                bean.setCheckLogin(true);
            }
            BaseReq<DataBean> req = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
            BaseResp rsp = dataInterface.basketScore(req);
            if(null != rsp){
                result.setData(rsp.getData());
            }
            result.setCode(rsp.getCode());
            result.setDesc(rsp.getDesc());
        } catch (Exception e) {
            log.error("查询篮球单场即时比分失败,{}",e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("系统异常,请稍后重试~");
        }
        return result;
    }

    /**
     * 关注/取消关注对阵
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.DATAWEB)
    @RequestMapping(value = "/data/match_follow.api")
    public Result matchFollow(DataBean bean) {
        Result result = new Result<>();
        BaseReq<DataBean> req = new BaseReq<>(bean, SysCodeConstant.DATAWEB);
        BaseResp rsp = dataInterface.matchFollow(req);
        if(null != rsp){
            result.setData(rsp.getData());
        }
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 查询足球直播源
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/foot_live.api")
    public Result footLive(DataBean bean) {
        Result result = new Result();
        BaseReq<DataBean> baseReq = new BaseReq(bean, SysCodeConstant.DATAWEB);
        BaseResp baseResp;
        try {
            baseResp = dataInterface.footLive(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("查询失败，[matchId:{}]", bean.getMatchId());
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询直播源失败");
        }
        return result;
    }

    /**
     * 查询篮球直播源
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/basket_live.api")
    public Result basketLive(DataBean bean) {
        Result result = new Result();
        BaseReq<DataBean> baseReq = new BaseReq(bean, SysCodeConstant.DATAWEB);
        BaseResp baseResp;
        try {
            baseResp = dataInterface.basketLive(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("查询失败，[matchId:{}]", bean.getMatchId());
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询直播源失败");
        }
        return result;
    }

    /**
     * 查询赔率
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/odds.api")
    public Result odds(DataBean bean) {
        Result result = new Result();
        BaseReq<DataBean> baseReq = new BaseReq(bean, SysCodeConstant.DATAWEB);
        BaseResp baseResp;
        try {
            baseResp = dataInterface.odds(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("查询失败，[matchId:{}]", bean.getMatchId());
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询赔率失败");
        }
        return result;
    }

    /**
     * 查询球队近10场的比赛
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/query_team_match_history.api")
    public Result queryTeamMatchHistory(DataBean bean) {
        Result result = new Result();
        BaseReq<DataBean> baseReq = new BaseReq(bean, SysCodeConstant.DATAWEB);
        BaseResp baseResp;
        try {
            baseResp = dataInterface.queryTeamMatchHistory(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("查询失败，[teamId:{}]", bean.getTeamId());
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询失败");
        }
        return result;
    }
    /**
     * 查询指定场次比赛实况数据
     * @param bean
     * @return
     */
    @RequestMapping(value = "/data/query_round_info.api")
    public Result queryRoundInfo(DataBean bean) {
        Result result = new Result();
        BaseReq<DataBean> baseReq = new BaseReq(bean, SysCodeConstant.DATAWEB);
        BaseResp baseResp;
        try {
            baseResp = dataInterface.queryRoundInfo(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("查询失败，[ids:{}]", bean.getIds());
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询失败");
        }
        return result;
    }
}
