package com.caiyi.lottery.tradesystem.redpacketcenter.client;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.redpacketcenter.clienterror.RedpacketCenterInterfaceError;
import redpacket.bean.RedPacketBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
  * 红包中心供内部其他中心调用接口
 */
@FeignClient(value = "tradecenter-system-redpacket-center",fallback = RedpacketCenterInterfaceError.class)
public interface RedPacketCenterInterface {

    /**
     * 查询可用红包，交易中心投注的时候会用到 （待测）
     * uid 为必传参数
     */
    @RequestMapping(value = "/redpacket/query_redpacket4pay.api",method = RequestMethod.POST)
    BaseResp<Page> queryRedpacket4Pay(@RequestBody BaseReq<RedPacketBean> redPacketBean);


    /**
     * 发送红包接口  必传参数：
     * crpid、cnickid、cdeaddate、imoney,coperator,igetType,icardid,cmemo,dispatchtime
     *
     */
    @RequestMapping(value = "/redpacket/send_redpacket.api",method = RequestMethod.POST)
    BaseResp<String> sendRedpacket(@RequestBody BaseReq<RedPacketBean> req);

    /**
     *插入红包活动表
     */
    @RequestMapping(value ="/redpacket/insert_rp_huodong.api",method = RequestMethod.POST)
    BaseResp insertIntoRedpacketHuodong(@RequestBody BaseReq<RedPacketBean> req);

    /**
     *获取红包详情 crpid为必传参数
     */
    @RequestMapping(value = "/redpacket/get_redpacket_detail.api",method = RequestMethod.POST)
    BaseResp<RedPacketBean> getRedpacketDetail(@RequestBody BaseReq<RedPacketBean> req);


    /**
     * 查询用户滚动打码
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/query_rolling_code.api",method = RequestMethod.POST)
    BaseResp<List<String>> queryRollingCode(@RequestBody BaseReq<RedPacketBean> req);

    /**
     * 指定用户名是否领取红包
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/is_get_redpacket.api",method = RequestMethod.POST)
    BaseResp<Integer> isGetRedPacket(@RequestBody BaseReq<BaseBean> req);

    /**
     * 是否以其他身份得到过红包
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/haven_redpacket.api",method = RequestMethod.POST)
    BaseResp<Integer> havenRedPacket(@RequestBody BaseReq<BaseBean> req);

    /**
     * 查询红包状态
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/get_redpacket_state_by_nickid.api",method = RequestMethod.POST)
    BaseResp<Integer> getRedPacketStateByNickid(@RequestBody BaseReq<BaseBean> req);

    /**
     * 检测用户交易红包
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/check_trade_redpacket.api",method = RequestMethod.POST)
    BaseResp<BaseBean> checkTradeRedpacket(@RequestBody BaseReq<RedPacketBean> req);

    /**
     * 查询用户合买次数
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/count_groupbuy.api",method = RequestMethod.POST)
    BaseResp<Integer> countGroupBuy(@RequestBody BaseReq<BaseBean> req);
}
