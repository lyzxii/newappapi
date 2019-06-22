package com.caiyi.lottery.tradesystem.safecenter.clientwrapper;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import dto.RechargeCardDTO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("SafeCenterWrapper")
public class SafeCenterWrapper {
	
	@Autowired
	SafeCenterInterface safeCenterInterface;
	
	public SafeBean getUserTable(SafeBean safeBean, Logger log, String syscode){
		BaseReq<SafeBean> req = new BaseReq<>(safeBean,syscode);
		BaseResp<SafeBean> resp = safeCenterInterface.getUserTable(req);
		SafeBean bean = resp.getData();
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			log.info("调用安全中心查询用户信息成功,code:"+resp.getCode()+" desc:"+resp.getDesc()+" 用户名:"+safeBean.getNickid());
			return bean;
		}else{
			log.info("调用安全中心查询用户信息失败,code:"+resp.getCode()+" desc:"+resp.getDesc()+" 用户名:"+safeBean.getNickid());
			return null;
		}
	}
	
	public List<SafeBean> queryRechargeByRechargeId(RechargeCardDTO rechCardDto, Logger log, String syscode){
		BaseReq<RechargeCardDTO> req = new BaseReq<>(rechCardDto,syscode);
		BaseResp<List<SafeBean>> resp = safeCenterInterface.queryRechargeByRechargeId(req);
		List<SafeBean> cardList = resp.getData();
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			log.info("调用安全中心查询用户充值卡列表成功,code:"+resp.getCode()+" desc:"+resp.getDesc()+" 用户名:"+rechCardDto.getNickid());
			return cardList;
		} else {
			log.info("调用安全中心查询用户充值卡列表失败,code:"+resp.getCode()+" desc:"+resp.getDesc()+" 用户名:"+rechCardDto.getNickid());
			return null;
		}
	}

	public boolean addUserTable(SafeBean bean, Logger log, String syscode) {
		BaseReq<SafeBean> req = new BaseReq<SafeBean>(bean,syscode);
		BaseResp<SafeBean> resp = safeCenterInterface.addUserTable(req);
		if (BusiCode.SUCCESS.equals(resp.getCode())) {
			log.info("调用安全中心添加用户表成功,code:" + resp.getCode() + " desc:" + resp.getDesc() + " 用户名:" + bean.getNickid());
			return true;
		} else {
			log.info("调用安全中心添加用户表失败,code:"+resp.getCode()+" desc:"+resp.getDesc()+" 用户名:"+bean.getNickid());
			return false;
		}
	}
}
