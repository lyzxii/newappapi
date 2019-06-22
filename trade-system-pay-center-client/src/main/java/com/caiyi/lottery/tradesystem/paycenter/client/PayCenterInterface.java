package com.caiyi.lottery.tradesystem.paycenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;

import com.caiyi.lottery.tradesystem.base.Response;
import pay.bean.PayBean;
import pay.dto.*;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 用户中心客户端接口
 */
@FeignClient(name = "tradecenter-system-paycenter-center")
public interface PayCenterInterface {
	/**
	 * 服务检查
	 * @return
	 */
	@RequestMapping(value = "/pay/checkhealth.api")
	Response checkHealth() ;
	@RequestMapping(value = "/pay/addmoney.api")
	BaseResp<RechDto> addmoney(@RequestBody BaseReq<PayBean> req);

	@RequestMapping(value = "/pay/agree_consume.api")
	BaseResp<RechDto> agreeConsume(@RequestBody BaseReq<PayBean> req); 
	
	@RequestMapping(value = "/pay/recharge_route.api")
	BaseResp<RechRouteDto> rechargeRoute(@RequestBody BaseReq<PayBean> req); 
	
	@RequestMapping(value = "/pay/single_card_route.api")
	BaseResp<RechRouteDto> singleCardRoute(@RequestBody BaseReq<PayBean> req); 
	
	@RequestMapping(value = "/pay/user_recharge_card_list.api")
	BaseResp<UserRechCardDto> userRechargeCardList(@RequestBody BaseReq<PayBean> req);
	
	@RequestMapping(value = "/pay/query_order_status.api")
	BaseResp<OrderStatusDto> queryOrderStatus(@RequestBody BaseReq<PayBean> req);

	@RequestMapping(value = "/pay/query_bankid.api")
	BaseResp<PayBean> queryBankId(@RequestBody BaseReq<PayBean> req);


	@RequestMapping(value = "/pay/delete_recharge_card_info.api")
	BaseResp<List<BindCardInfoDto>> deleteRechargeCard(@RequestBody BaseReq<PayBean> req);

}
