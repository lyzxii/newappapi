package com.caiyi.lottery.tradesystem.payweb.controller;

import com.caiyi.lottery.tradesystem.annotation.RealIP;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import com.caiyi.lottery.tradesystem.util.CheckBannedIp;
import com.caiyi.lottery.tradesystem.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.paycenter.client.PayCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;

import lombok.extern.slf4j.Slf4j;
import pay.bean.PayBean;
import pay.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
public class PayController {
	@Autowired
	private PayCenterInterface payCenterInterface;

	@Autowired
	private HttpServletRequest request;

	@RequestMapping(value = "/pay/checklocalhealth.api")
	public Response checkLocalHealth() {
		Response response = new Response();
		response.setCode(BusiCode.SUCCESS);
		response.setDesc("支付中心pay-web启动运行正常");
		return response;
	}

	@RequestMapping(value = "/pay/checkhealth.api")
	public Result checkHealth(){
		Response response = payCenterInterface.checkHealth();
		Result result = new Result();
		result.setCode(response.getCode());
		result.setDesc(response.getDesc());
		log.info("=====检测支付中心服务=====");
		return result;
	}
	
	@CheckLogin(sysCode = SysCodeConstant.PAYWEB)
	@RealIP
	@RequestMapping(value = "/pay/addmoney.api", produces = {"application/json;charset=UTF-8"})
	public Result<RechDto> addmoney(PayBean bean){
		bean.setClientIp(bean.getIpAddr());
		BaseReq<PayBean> req = new BaseReq<>(bean,SysCodeConstant.PAYWEB);
		BaseResp<RechDto> resp = payCenterInterface.addmoney(req);
		Result<RechDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("充值失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
	}

	@CheckLogin(sysCode = SysCodeConstant.PAYWEB)
	@RealIP
	@RequestMapping(value = "/pay/agree_consume.api", produces = {"application/json;charset=UTF-8"})
	public Result<RechDto> agreeConsume(PayBean bean){

		bean.setClientIp(bean.getIpAddr());
		BaseReq<PayBean> req = new BaseReq<>(bean,SysCodeConstant.PAYWEB);
		BaseResp<RechDto> resp = payCenterInterface.agreeConsume(req);
		Result<RechDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("确认支付失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
	}

	@CheckLogin(sysCode = SysCodeConstant.PAYWEB)
	@RealIP
	@RequestMapping(value = "/pay/recharge_route.api", produces = {"application/json;charset=UTF-8"})
	public Result<RechRouteDto> rechargeRoute(PayBean bean){
		Result<RechRouteDto> result = new Result<>();
		if(CheckBannedIp.checkBannedIp(bean)){
			result.setCode(String.valueOf(bean.getBusiErrCode()));
			result.setDesc(bean.getBusiErrDesc());
			return result;
		}
		BaseReq<PayBean> req = new BaseReq<>(bean,SysCodeConstant.PAYWEB);
		BaseResp<RechRouteDto> resp = payCenterInterface.rechargeRoute(req);
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("充值路由失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
	}

	@CheckLogin(sysCode = SysCodeConstant.PAYWEB)
	@RequestMapping(value = "/pay/single_card_route.api", produces = {"application/json;charset=UTF-8"})
	public Result<RechRouteDto> singleCardRoute(PayBean bean){
		BaseReq<PayBean> req = new BaseReq<>(bean,SysCodeConstant.PAYWEB);
		BaseResp<RechRouteDto> resp = payCenterInterface.singleCardRoute(req);
		Result<RechRouteDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("充值路由失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
	}

	@CheckLogin(sysCode = SysCodeConstant.PAYWEB)
	@RequestMapping(value = "/pay/user_recharge_card_list.api", produces = {"application/json;charset=UTF-8"})
	public Result<UserRechCardDto> userRechargeCardList(PayBean bean){
		BaseReq<PayBean> req = new BaseReq<>(bean,SysCodeConstant.PAYWEB);
		BaseResp<UserRechCardDto> resp = payCenterInterface.userRechargeCardList(req);
		Result<UserRechCardDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("查询用户银行卡列表失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
	}
	
	@RequestMapping(value = "/pay/query_order_status.api", produces = {"application/json;charset=UTF-8"})
	public Result<OrderStatusDto> queryOrderStatus(PayBean bean){
		BaseReq<PayBean> req = new BaseReq<>(bean,SysCodeConstant.PAYWEB);
		BaseResp<OrderStatusDto> resp = payCenterInterface.queryOrderStatus(req);
		Result<OrderStatusDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("查询订单信息失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
	}


	/**
	 * 用户删卡
	 */
	@CheckLogin(sysCode = SysCodeConstant.PAYWEB)
	@RequestMapping(value = "/pay/delete_recharge_card_info.api",produces = {"application/json;charset=UTF-8"})
	public Result deleteRechargeCard(PayBean bean){
		Result result=new Result();
		BaseResp<List<BindCardInfoDto>> resp=payCenterInterface.deleteRechargeCard(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
		BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
		return result;
	}
}
