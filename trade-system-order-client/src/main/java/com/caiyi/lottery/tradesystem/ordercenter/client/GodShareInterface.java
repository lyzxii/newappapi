package com.caiyi.lottery.tradesystem.ordercenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.clienterror.GodShareInterfaceError;
import order.bean.OrderBean;
import order.dto.FollowListDTO;
import order.dto.GodShareDetailDTO;
import order.pojo.ShareGodUserPojo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tiankun on 2017/12/28.
 */
@FeignClient(name = "tradecenter-system-ordercenter-center",fallback = GodShareInterfaceError.class)
public interface GodShareInterface {

    /**
     * 查询该方案大神其余进行中的神单
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/queryOtherItem.api")
    BaseResp<List<HashMap>> queryOtherItem(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     *  查看神单详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/godShareDetail.api")
    BaseResp<GodShareDetailDTO> godShareDetail(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 竞彩大神页面
     * @param baseReq
     */
    @RequestMapping(value = "/order/godShareItem.api")
    BaseResp<HashMap<String,Object>> godShareItem(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 新版大神详情
     *
     * @param baseReq
     */
    @RequestMapping(value = "/order/share_user_details_new.api")
    BaseResp<HashMap<String, Object>> shareUserDetailsNew(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     *神单列表
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/god_proj_list.api")
    BaseResp<HashMap<String, Object>> godProjList(@RequestBody BaseReq<OrderBean> baseReq);

    /**
     * 大神榜
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/share_user_data_list.api")
    BaseResp<List<ShareGodUserPojo>> share_user_data_list(BaseReq baseReq);

    /**
     * 查询跟买人
     * @param req
     * @return
     */
    @RequestMapping(value = "/order/query_god_follow_list.api")
    BaseResp<FollowListDTO> queryGodFollowList(BaseReq<OrderBean> req);

    /**
     * 分享神单
     * @return
     */
    @RequestMapping(value = "/order/share_god_proj_buy.api")
    public BaseResp<OrderBean> shareGodProj(@RequestBody BaseReq<OrderBean> req);
}
