package com.caiyi.lottery.tradesystem.usercenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.service.TokenManageService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserBasicInfoService;
import com.caiyi.lottery.tradesystem.usercenter.util.TokenGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserBaseController {
	@Autowired
	TokenManageService tokenManageService;
	@Autowired
	UserBasicInfoService userBasicInfoService;
	
	@RequestMapping(value = "/base/check_login.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<BaseBean> checkLogin(@RequestBody BaseReq<BaseBean> baseReq ){
		BaseBean bean = baseReq.getData();
		tokenManageService.checkLogin(bean);
		BaseResp<BaseBean> resp = new BaseResp<>();
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		resp.setData(bean);
 		return resp;
	}
	
	@RequestMapping(value = "/base/set_user_data.api", produces = {"application/json;charset=UTF-8"})
	public BaseResp<BaseBean> setUserData(@RequestBody BaseReq<BaseBean> baseReq){
		BaseBean bean = baseReq.getData();
        if (bean.getLogintype() == 1) { //token登录
            String [] result = TokenGenerator.authToken(bean.getAccesstoken(), bean.getAppid());
            if ("1".equals(result[0])) {
    			tokenManageService.tokenLogin(bean);
    			if(bean.getBusiErrCode()!=0){
    				bean.setBusiErrCode(0);
    				bean.setBusiErrDesc("未获取到用户基础信息");
    				log.info("未获取到用户的基础信息,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken());
    			}
            }
        } else {
        	bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_UNLOGIN));
        	bean.setBusiErrDesc("用户未登录");
        }
		BaseResp<BaseBean> resp = new BaseResp<>();
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		resp.setData(bean);
 		return resp;
	}
}
