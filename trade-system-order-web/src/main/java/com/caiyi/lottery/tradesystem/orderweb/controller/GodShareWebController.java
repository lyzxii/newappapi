package com.caiyi.lottery.tradesystem.orderweb.controller;

import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.client.GodShareInterface;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderBasicInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.FollowListDTO;
import order.dto.GodShareDetailDTO;
import order.dto.XmlDTO;
import order.pojo.ShareGodUserPojo;
import order.response.XmlResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;

/**
 * Created by tiankun on 2017/12/28.
 */
@RestController
@Slf4j
public class GodShareWebController {

    @Autowired
    private GodShareInterface godShareInterface;

    /**
     * 查询该方案大神其余进行中的神单
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/order/queryOtherItem.api")
    public Result<List<HashMap>> queryOtherItem(OrderBean bean) {
        Result<List<HashMap>> result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        try {
            BaseResp<List<HashMap>> resp = godShareInterface.queryOtherItem(baseReq);
            log.info("查询其他神单Result结果:" + resp.toJson());
            result.setData(resp.getData());
            result.setCode(resp.getCode());
            result.setDesc(resp.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("系统异常,请稍后重试");
            log.error("查询该方案大神其余进行中的神单程序异常", e);
        }
        return result;
    }

    /**
     * 查看神单详情
     *
     * @param
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/godShareDetail.api")
    public Result<GodShareDetailDTO> godShareDetail(OrderBean bean) {
        Result<GodShareDetailDTO> result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        try {
            BaseResp<GodShareDetailDTO> resp = godShareInterface.godShareDetail(baseReq);
            result.setData(resp.getData());
            result.setCode(resp.getCode());
            result.setDesc(resp.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("系统异常,请稍后重试");
            log.error("查看神单详情程序异常", e);
        }
        return result;
    }

    /**
     * 竞彩大神页面
     *
     * @param
     */
    @RequestMapping(value = "/order/godShareItem.api")
    public Result<HashMap<String, Object>> godShareItem(OrderBean bean) {
        Result<HashMap<String, Object>> result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        try {
            BaseResp<HashMap<String, Object>> resp = godShareInterface.godShareItem(baseReq);
            result.setData(resp.getData());
            result.setCode(resp.getCode());
            result.setDesc(resp.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("系统异常,请稍后重试");
            log.error("竞彩大神页面程序异常", e);
        }
        return result;
    }

    /**
     * 新版大神详情
     *
     * @param
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/share_user_details_new.api")
    public Result<HashMap<String, Object>> shareUserDetailsNew(OrderBean bean){
        Result<HashMap<String, Object>> result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        try {
            BaseResp<HashMap<String, Object>> resp = godShareInterface.shareUserDetailsNew(baseReq);
            result.setData(resp.getData());
            result.setCode(resp.getCode());
            result.setDesc(resp.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("系统异常,请稍后重试");
            log.error("新版大神详情程序异常", e);
        }
        return result;
    }

    /**
     * 神单列表
     * @param bean
     * @return
     */
    @RequestMapping(value = "/order/god_proj_list.api")
    public Result<HashMap<String, Object>> godProjList(OrderBean bean){
        Result<HashMap<String, Object>> result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        try {
            BaseResp<HashMap<String, Object>> resp = godShareInterface.godProjList(baseReq);
            result.setData(resp.getData());
            result.setCode(resp.getCode());
            result.setDesc(resp.getDesc());
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("系统异常,请稍后重试");
            log.error("查询分享神单列表程序异常", e);
        }
        return result;
    }

    /**
     * 分享神单
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/share_god_proj_buy.api", produces = {"application/json;charset=UTF-8"})
    public Result shareGodProj(OrderBean bean) {
        Result result = new Result();
        BaseReq<OrderBean> req = new BaseReq<>(SysCodeConstant.ORDERWEB);
        req.setData(bean);
        BaseResp<OrderBean> baseResp = godShareInterface.shareGodProj(req);
        bean = baseResp.getData();
        if(bean != null){
            result.setCode(String.valueOf(bean.getBusiErrCode()));
            result.setDesc(bean.getBusiErrDesc());
            return result;
        }
        result.setCode(BusiCode.FAIL);
        result.setDesc("请求结果为空");
        return result;
    }
    /**
     * 大神榜
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/order/share_user_data_list.api")
    public Result<List<ShareGodUserPojo>> shareUserDataList(OrderBean bean) {
        Result<List<ShareGodUserPojo>> result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp<List<ShareGodUserPojo>> resp = godShareInterface.share_user_data_list(baseReq);
        result.setData(resp.getData());
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        return result;
    }

    @RequestMapping(value = "/order/query_god_follow_list.api")
    public Result<FollowListDTO> queryGodFollowList(OrderBean bean){
        Result<FollowListDTO> response = new Result();
        BaseReq<OrderBean> req = new BaseReq<>(SysCodeConstant.ORDERWEB);
        req.setData(bean);
        BaseResp<FollowListDTO> result = godShareInterface.queryGodFollowList(req);
        if(result != null){
            response.setCode(result.getCode());
            response.setDesc(result.getDesc());
            response.setData(result.getData());
            return response;
        }
        response.setCode(BusiCode.FAIL);
        response.setDesc("请求结果为空");
        return response;
    }
}
