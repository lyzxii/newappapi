package com.caiyi.lottery.tradesystem.ordercenter.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.client.GodShareInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.FollowListDTO;
import order.dto.GodShareDetailDTO;
import order.pojo.ShareGodUserPojo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class GodShareInterfaceError implements GodShareInterface{
    /**
     * 查询该方案大神其余进行中的神单
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<List<HashMap>> queryOtherItem(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心queryOtherItem调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 查看神单详情
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<GodShareDetailDTO> godShareDetail(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心godShareDetail调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 竞彩大神页面
     *
     * @param baseReq
     */
    @Override
    public BaseResp<HashMap<String, Object>> godShareItem(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心godShareItem调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 新版大神详情
     *
     * @param baseReq
     */
    @Override
    public BaseResp<HashMap<String, Object>> shareUserDetailsNew(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心shareUserDetailsNew调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 神单列表
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<HashMap<String, Object>> godProjList(BaseReq<OrderBean> baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心godProjList调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 大神榜
     *
     * @param baseReq
     * @return
     */
    @Override
    public BaseResp<List<ShareGodUserPojo>> share_user_data_list(BaseReq baseReq) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心share_user_data_list调用失败,请求req:"+baseReq.toJson());
        return resp;
    }

    /**
     * 查询跟买人
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<FollowListDTO> queryGodFollowList(BaseReq<OrderBean> req) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心queryGodFollowList调用失败,请求req:"+req.toJson());
        return resp;
    }

    /**
     * 分享神单
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<OrderBean> shareGodProj(BaseReq<OrderBean> req) {
        BaseResp resp = new BaseResp<>();
        resp.setCode(ErrorCode.ORDER_REMOTE_INVOKE_ERROR);
        resp.setDesc("订单中心调用失败");
        log.info("订单中心shareGodProj调用失败,请求req:"+req.toJson());
        return resp;
    }
}
