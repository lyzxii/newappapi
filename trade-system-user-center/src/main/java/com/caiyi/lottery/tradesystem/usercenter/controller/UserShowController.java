package com.caiyi.lottery.tradesystem.usercenter.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.service.UserCenterShowService;
import dto.MyLotteryDTO;
import dto.UserAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//客户端展示页面
@RestController
public class UserShowController {
	private Logger logger = LoggerFactory.getLogger(UserShowController.class);
	@Autowired
	UserCenterShowService userCenterShowService;

	/**
	 *
	 * @param baseReq
	 * @return
	 */
	@RequestMapping(value="/user/mlottery.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<MyLotteryDTO> mlottery(@RequestBody BaseReq<UserBean> baseReq){
		UserBean bean = baseReq.getData();
		BaseResp<MyLotteryDTO> resp = new BaseResp<>();
		MyLotteryDTO dto = userCenterShowService.queryMyLotteryData(bean);
		if(bean.getBusiErrCode()==0){
			logger.info("查询我的彩票返回数据:"+dto.toJsonString());
			resp.setData(dto);
			resp.setCode(BusiCode.SUCCESS);
			resp.setDesc("查询成功");
		}else{
			resp.setCode(bean.getBusiErrCode()+"");
			resp.setDesc("查询失败");
		}
		return resp;
	}

	/**
	 *
	 * @param baseReq
	 * @return
	 */
	@RequestMapping(value = "/user/query_account.api")
	public BaseResp queryAccount(@RequestBody BaseReq<UserBean> baseReq) {
		UserBean bean = baseReq.getData();
		BaseResp baseResp = new BaseResp();
		Page<List<UserAccountDTO>> page = new Page<>();
		try {
			page = userCenterShowService.queryAccount(bean);
			baseResp.setCode(bean.getBusiErrCode() + "");
			baseResp.setDesc(bean.getBusiErrDesc());
			baseResp.setData(page);
		} catch (Exception e) {
			logger.error("账户明细查询失败：[uid:{},flag:{}]", bean.getUid(), bean.getFlag(), e);
			baseResp.setCode(ErrorCode.USER_QUERYACCOUNT_PROCESS_ERROR);
			baseResp.setDesc("账户明细查询失败");
		}
		return baseResp;
	}
}
