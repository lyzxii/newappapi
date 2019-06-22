package com.caiyi.lottery.tradesystem.usercenter.clientwrapper;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.StringUtil;

import pojo.UserPojo;

@Component("UserBasicInfoWrapper")
public class UserBasicInfoWrapper {
	@Autowired
	private UserBasicInfoInterface userBasicInfoInterface;
	
	public String queryUserWhiteGrade(BaseBean bean, Logger log, String syscode){
		BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean, syscode);
		BaseResp<String> resp = userBasicInfoInterface.queryUserWhiteGrade(req);
		bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
		bean.setBusiErrDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())&&!StringUtil.isEmpty(resp.getData())){
			return resp.getData();
		}else{
			log.info("查询用户白名单失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return null;
		}
	}
	
	public UserPojo queryUserInfo(BaseBean bean, Logger log, String syscode){
		BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean, syscode);
		BaseResp<UserPojo> resp = userBasicInfoInterface.queryUserInfo(req);
		bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
		bean.setBusiErrDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
			return resp.getData();
		}else{
			log.info("查询用户基本信息失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return null;
		}
	}

	public Integer countOutByNickidInAYear(BaseBean bean, Logger log, String syscode) {
		BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean, syscode);
		BaseResp<Integer> resp = userBasicInfoInterface.countOutByNickidInAYear(req);
		bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
		bean.setBusiErrDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
			return resp.getData();
		}else{
			log.info("查询用户一年内消费次数失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return 0;
		}
	}

	public Integer isNewUser(BaseBean bean, Logger log, String syscode) {
		BaseReq<BaseBean> req = new BaseReq<>(bean, syscode);
		BaseResp<Integer> resp = userBasicInfoInterface.isNewUser(req);
		bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
		bean.setBusiErrDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())&&null!=resp.getData()){
			return resp.getData();
		}else{
			log.info("查询是否新用户失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return 0;
		}
	}
}
