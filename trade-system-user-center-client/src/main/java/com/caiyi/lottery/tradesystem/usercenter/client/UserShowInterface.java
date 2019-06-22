package com.caiyi.lottery.tradesystem.usercenter.client;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.usercenter.clienterror.UserShowInterfaceError;
import dto.MyLotteryDTO;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户中心客户端接口
 */
@FeignClient(name = "tradecenter-system-usercenter-center")
public interface UserShowInterface {

    /**
     * 我的彩票页面
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/user/mlottery.api")
    BaseResp<MyLotteryDTO> mlottery(@RequestBody BaseReq<UserBean> bean);

}
