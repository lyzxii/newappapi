package com.caiyi.lottery.tradesystem.redpacketcenter.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketClientInterface;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redpacket.bean.MyRedPacketPage;
import redpacket.bean.RedPacketBean;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class RedPacketClientInterfaceError implements RedPacketClientInterface{
    /**
     * 服务检查
     *
     * @return
     */
    @Override
    public Response checkHealth() {
        return null;
    }

    /**
     * 获取红包详情
     *
     * @param redPacketBean 中 cupacketid（红包id） pn(分页起始页) ps（分页大小）三个属性为必传参数
     */
    @Override
    public BaseResp<Page> queryRedPacketDetail(BaseReq<RedPacketBean> redPacketBean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心queryRedPacketDetail调用失败,req:"+redPacketBean.toJson());
        return resp;
    }

    /**
     * 查询我的红包
     *
     * @param redPacketBean 中 state、pn(分页起始页) ps（分页大小）三个属性为必传参数
     *                      state 1:可用红包 2：过期红包 3：待派发的红包
     */
    @Override
    public BaseResp<MyRedPacketPage> queryMyRedPacket(BaseReq<RedPacketBean> redPacketBean) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心queryMyRedPacket调用失败,req:"+redPacketBean.toJson());
        return resp;
    }

    /**
     * 卡密充值送红包
     *
     * @param req
     * @return
     */
    @Override
    public BaseResp CardChargeRedpacket(BaseReq<RedPacketBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.REDPACKET_REMOTE_INVOKE_ERROR);
        resp.setDesc("红包中心调用失败");
        log.info("红包中心CardChargeRedpacket调用失败,req:"+req.toJson());
        return resp;
    }
}
