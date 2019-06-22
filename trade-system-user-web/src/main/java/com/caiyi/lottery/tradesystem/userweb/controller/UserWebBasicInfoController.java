package com.caiyi.lottery.tradesystem.userweb.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;


@Slf4j
@RestController
public class UserWebBasicInfoController {
	
	@Autowired
	UserBaseInterface userCenterBaseInterface;
	@Autowired
	UserBasicInfoInterface userBasicInfoInterface;
	
	/**
	 * 用户提款银行卡绑定
	 * @param bean
	 * @return
	 */
	@CheckLogin(sysCode = SysCodeConstant.USERWEB)
	@RequestMapping(value="/user/bind_bankcard.api",produces={"application/json;charset=UTF-8"})
	public Result<BaseBean> bankCardBind(UserBean bean){
		BaseReq<UserBean> baseReq = new BaseReq(SysCodeConstant.USERWEB);
		baseReq.setData(bean);
    	Result<BaseBean> result = new Result<>();
    	BaseResp<BaseBean> resp = userBasicInfoInterface.bankCardBind(baseReq);
    	result.setCode(resp.getCode());
    	result.setDesc(resp.getDesc());
		return result;
	}
	
	 /**
	   * @Author: tiankun
	   * @Description: 统计APP崩溃错误信息
	   * @Date: 19:36 2017/12/11
	   */
	@RequestMapping(value = "/user/calculate_breakdown_error.api", produces = {"application/json;charset=UTF-8"})
	public Result calcUserpingNeterror(UserBean bean) {
		BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
		baseReq.setData(bean);
		Result result =new Result();
		try {
			BaseResp<BaseBean> bbr = userBasicInfoInterface.calculate_breakdown_error(baseReq);
			result.setCode(bbr.getCode());
			result.setDesc(bbr.getDesc());
		} catch (Exception e) {
			result.setCode(FAIL);
			result.setDesc("统计APP崩溃错误信息抛出异常");
			log.error("calcUserpingNeterror",e);
		}
		return result;
	}

	 /**
	   * @Author: tiankun
	   * @Description: 检查用户名是否存在
	   * @Date: 19:40 2017/12/11
	   */
	@RequestMapping(value = "/user/checkIsExist.api",produces={"application/json;charset=UTF-8"})
	public Result checkIsExist(UserBean bean) {
		BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
		baseReq.setData(bean);
		Result result =new Result();
		try {
			BaseResp<String> sbr = userBasicInfoInterface.checkIsExist(baseReq);
			result.setCode(sbr.getCode());
			result.setDesc(sbr.getDesc());
		} catch (Exception e) {
			result.setCode(FAIL);
			result.setDesc("检查用户名是否存在抛出异常");
			log.error("checkIsExist",e);
		}
		return result;
	}
	
	
}
