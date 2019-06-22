package com.caiyi.lottery.tradesystem.redpacketcenter.clienterror;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redpacket.bean.RedPacketBean;

import java.util.List;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class RedpacketCenterInterfaceError implements RedPacketCenterInterface{
    /**
     * 查询可用红包，交易中心投注的时候会用到 （待测）
     * uid 为必传参数
     *
     * @param redPacketBean
     */
    @Override
    public BaseResp<Page> queryRedpacket4Pay(BaseReq<RedPacketBean> redPacketBean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心queryRedpacket4Pay调用失败,req:"+redPacketBean.toJson());
        return resp;
    }

    /**
     * 发送红包接口  必传参数：
     * crpid、cnickid、cdeaddate、imoney,coperator,igetType,icardid,cmemo,dispatchtime
     *
     * @param req
     */
    @Override
    public BaseResp<String> sendRedpacket(BaseReq<RedPacketBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心sendRedpacket调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 插入红包活动表
     *
     * @param req
     */
    @Override
    public BaseResp insertIntoRedpacketHuodong(BaseReq<RedPacketBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心insertIntoRedpacketHuodong调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 获取红包详情 crpid为必传参数
     *
     * @param req
     */
    @Override
    public BaseResp<RedPacketBean> getRedpacketDetail(BaseReq<RedPacketBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心getRedpacketDetail调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 查询用户滚动打码
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<List<String>> queryRollingCode(BaseReq<RedPacketBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心queryRollingCode调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 指定用户名是否领取红包
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<Integer> isGetRedPacket(BaseReq<BaseBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心isGetRedPacket调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 是否以其他身份得到过红包
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<Integer> havenRedPacket(BaseReq<BaseBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心havenRedPacket调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 查询红包状态
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<Integer> getRedPacketStateByNickid(BaseReq<BaseBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心getRedPacketStateByNickid调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 检测用户交易红包
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<BaseBean> checkTradeRedpacket(BaseReq<RedPacketBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心checkTradeRedpacket调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 查询用户合买次数
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp<Integer> countGroupBuy(BaseReq<BaseBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心countGroupBuy调用失败,req:"+req.toJson());
        return resp;
    }
}
