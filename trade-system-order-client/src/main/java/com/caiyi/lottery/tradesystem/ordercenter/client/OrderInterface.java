package com.caiyi.lottery.tradesystem.ordercenter.client;


import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.ordercenter.clienterror.OrderInterfaceError;
import order.bean.ChaseNumberPage;
import order.bean.OrderBean;
import order.dto.*;
import order.response.XmlResp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 *
 * @author GJ
 * @create 2017-12-19 18:24
 **/
@FeignClient(name = "tradecenter-system-ordercenter-center",fallback = OrderInterfaceError.class)
public interface OrderInterface {

    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/order/checkhealth.api")
     Response checkHealth() ;
    /**
     * 追号详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_chasenumber_record.api")
    BaseResp<ChaseNumberPage> getChaseNumberRecord(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 隐藏追号记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/hide_zhuihao_detail.api")
    BaseResp hideZhuihaoDetail(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 查询出票明细
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/ai_ticket_detail.api")
    BaseResp<XmlResp> awarddetail(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 隐藏投注记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/hide_buy_record.api")
    BaseResp hideBuyRecord(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 查询投注记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_cast_detail.api")
    BaseResp queryCastDetail(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 快频排行
     * @param baseReq
     * @return
     */
    @RequestMapping(value ="/order/ranking.api")
    BaseResp ranking(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 过关统计
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/stat_pass.api")
    BaseResp statPass(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 查询购彩/追号/投注记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_lottery_detail.api")
    BaseResp queryLotteryDetail(BaseReq<OrderBean> baseReq);

    /**
     * 带提示检测追号的期次限制
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/lottery_info_new.api")
    BaseResp<List> lotteryInfoNew(BaseReq<OrderBean> baseReq);

    /**
     * 竞技彩方案详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_athletics_projectdetail.api")
     BaseResp<GamesProjectDTO> getAthleticsProjectDetail(@RequestBody BaseReq<OrderBean> baseReq);


    /**
     * 数字彩方案详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_digita_projectdetail.api")
     BaseResp<FigureGamesDTO> getDigitaProjectDetail(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 胜负彩任九
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_zucai_projectdetail.api")
     BaseResp<ZucaiMatchProDTO> getZucaiProjectDetail(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 胜负彩任九
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_zucai_match.api")
    BaseResp<ZuCaiMatchVSDTO> getZucaiMatch(@RequestBody BaseReq<OrderBean> baseReq);
    
    /**
     * 查询用户未开奖订单数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_unbegin_num.api")
    BaseResp<Integer> queryUserUnbeginNum(@RequestBody BaseReq<BaseBean> baseReq);

    @RequestMapping(value = "/order/query_matrixinfos.api")
    BaseResp<List<String>> getMatrixInfos(@RequestBody BaseReq<OrderBean> baseReq);

    @RequestMapping(value = "/order/query_lsdetail.api")
    BaseResp<NewTicketDetailDTO> queryLsDetail(@RequestBody BaseReq<OrderBean> baseReq);

}
