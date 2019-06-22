package com.caiyi.lottery.tradesystem.redpacketcenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.redpacketcenter.clienterror.RedPacketClientInterfaceError;
import redpacket.bean.MyRedPacketPage;
import redpacket.bean.RedPacketBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 红包中心web层调用接口
 */
@FeignClient(value = "tradecenter-system-redpacket-center",fallback = RedPacketClientInterfaceError.class)
public interface RedPacketClientInterface {
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/redpacket/checkhealth.api")
    Response checkHealth() ;

    /**
     * 获取红包详情
     * @param redPacketBean 中 cupacketid（红包id） pn(分页起始页) ps（分页大小）三个属性为必传参数
     *
     */
    @RequestMapping(value = "/redpacket/query_redpacket_detail.api",method= RequestMethod.POST)
    BaseResp<Page> queryRedPacketDetail(@RequestBody BaseReq<RedPacketBean> redPacketBean);

    /**
     * 查询我的红包
     * @param redPacketBean 中 state、pn(分页起始页) ps（分页大小）三个属性为必传参数
     * state 1:可用红包 2：过期红包 3：待派发的红包
     */
    @RequestMapping(value ="/redpacket/query_my_redpacket.api",method = RequestMethod.POST)
    BaseResp<MyRedPacketPage> queryMyRedPacket(@RequestBody BaseReq<RedPacketBean> redPacketBean);


    /**
     * 卡密充值送红包
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/card_charge_redpacket.api",method = RequestMethod.POST)
    BaseResp CardChargeRedpacket(BaseReq<RedPacketBean> req);
}
