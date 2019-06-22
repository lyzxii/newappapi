package com.caiyi.lottery.tradesystem.integralcenter.client;


import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.integralcenter.clienterror.IntegralCenterInterfaceError;
import integral.bean.IntegralBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 积分中心客户端接口
 */
@FeignClient(name = "tradecenter-system-integralcenter-center",fallback = IntegralCenterInterfaceError.class)
public interface IntegralCenterInterface {
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/integral/checkhealth.api")
    Response checkHealth() ;

    /**
     * @param bean
     * @return
     */
    @RequestMapping(value = "/integral/click_to_sign.api")
    public BaseResp<IntegralBean> clickToSign(BaseReq<UserBean> bean);

    @RequestMapping(value = "/integral/query_vip_point_info.api")
    public BaseResp<IntegralBean> queryVipPointInfo(BaseReq<UserBean> bean);

    @RequestMapping(value = "/integral/click_to_get_points.api")
    public BaseResp<IntegralBean> clickToGetPoints(BaseReq<UserBean> bean);

    @RequestMapping(value = "/integral/points_detail.api")
    public BaseResp<Page> pointsDetail(BaseReq<UserBean> bean);

    @RequestMapping(value = "/integral/experience_detail.api")
    public BaseResp<Page> experienceDetail(BaseReq<UserBean> bean);

    @RequestMapping(value = "/integral/query_vip_user_info.api")
    public BaseResp<IntegralBean> queryVipUserInfo(BaseReq<UserBean> bean);


}
