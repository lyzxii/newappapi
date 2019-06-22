package com.caiyi.lottery.tradesystem.paycenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.paycenter.service.PayService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechRouteService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;

import lombok.extern.slf4j.Slf4j;
import pay.bean.PayBean;
import pay.dto.*;

import java.util.List;

@Slf4j
@RestController
public class PayController {
	
	@Autowired
	PayService payService;
	@Autowired
	UserBasicInfoInterface userBasicInfoInterface;
	@Autowired
	RechRouteService rechRouteService;
	@Autowired
	RechService rechService;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private DualMapper dualMapper;

	@RequestMapping(value = "/pay/checklocalhealth.api")
	public Response checkLocalHealth() {
		Response response = new Response();
		response.setCode(BusiCode.SUCCESS);
		response.setDesc("支付中心pay-center启动运行正常");
		return response;
	}
	/**
	 * 服务检查
	 * @return
	 */
	@RequestMapping(value = "/pay/checkhealth.api")
	public Response checkHealth() {
		CacheBean cacheBean= new CacheBean();
		cacheBean.setKey("checkhealth_pay");
		redisClient.exists(cacheBean,log, SysCodeConstant.PAYCENTER);
		dualMapper.check();
		Response response = new Response();
		response.setCode(BusiCode.SUCCESS);
		response.setDesc("支付中心服务运行正常");
		return response;
	}
	
	@RequestMapping(value = "/pay/addmoney.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<RechDto> addmoney(@RequestBody BaseReq<PayBean> req){
		BaseResp<RechDto> resp = new BaseResp<>();
		PayBean bean = req.getData();
		RechDto rech = payService.addmoney(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode()==0){
			resp.setData(rech);
		}
		log.info("充值返回信息,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" resp:"+resp.toJson());
		return resp;
	}
	
	@RequestMapping(value = "/pay/agree_consume.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<RechDto> agreeConsume(@RequestBody BaseReq<PayBean> req){
		BaseResp<RechDto> resp = new BaseResp<>();
		PayBean bean = req.getData();
		RechDto rech = payService.agreeConsume(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode()==0){
			resp.setData(rech);
		}
		log.info("确认支付返回信息,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid());
		return resp;
	}
	
	@RequestMapping(value = "/pay/recharge_route.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<RechRouteDto> rechargeRoute(@RequestBody BaseReq<PayBean> req){
		BaseResp<RechRouteDto> resp = new BaseResp<>();
		PayBean bean = req.getData();
		RechRouteDto rechRoute = rechRouteService.distributeRoute(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode() == 0){
			resp.setData(rechRoute);
		}
		return resp;
	}
	
	@RequestMapping(value = "/pay/single_card_route.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<RechRouteDto> singleCardRoute(@RequestBody BaseReq<PayBean> req){
		BaseResp<RechRouteDto> resp = new BaseResp<>();
		PayBean bean = req.getData();
		RechRouteDto rechRoute = rechRouteService.singleCardRoute(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode() == 0){
			resp.setData(rechRoute);
		}
		return resp;
	}
	
	@RequestMapping(value = "/pay/user_recharge_card_list.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<UserRechCardDto> userRechargeCardList(@RequestBody BaseReq<PayBean> req){
		BaseResp<UserRechCardDto> resp = new BaseResp<>();
		PayBean bean = req.getData();
		UserRechCardDto userRechCardDto = payService.userRechCardList(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode() == 0){
			resp.setData(userRechCardDto);
		}
		return resp;
	}
	
	@RequestMapping(value = "/pay/query_order_status.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<OrderStatusDto> queryOrderStatus(@RequestBody BaseReq<PayBean> req){
		BaseResp<OrderStatusDto> resp = new BaseResp<>();
		PayBean bean = req.getData();
		OrderStatusDto orderStatusDto = rechService.queryOrderStatus(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode() == 0){
			resp.setData(orderStatusDto);
		}
		return resp;
	}


	@RequestMapping(value = "/pay/delete_recharge_card_info.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<List<BindCardInfoDto>> deleteRechargeCard(@RequestBody BaseReq<PayBean> req){
		PayBean bean = req.getData();
		BaseResp<List<BindCardInfoDto>> baseResp=new BaseResp<>();
		List<BindCardInfoDto> bindCardInfoDtoList=payService.deleteRechargeCard(bean);
		baseResp.setCode(BusiCode.SUCCESS);
		baseResp.setDesc("删除绑定银行卡信息成功");
		if(bindCardInfoDtoList!=null&&bindCardInfoDtoList.size()!=0){
			baseResp.setData(bindCardInfoDtoList);
		}
		return baseResp;
	}
}
