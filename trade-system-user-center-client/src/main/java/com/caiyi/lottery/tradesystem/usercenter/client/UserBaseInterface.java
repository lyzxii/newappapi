package com.caiyi.lottery.tradesystem.usercenter.client;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;

import com.caiyi.lottery.tradesystem.usercenter.clienterror.UserBaseInterfaceError;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户中心客户端接口
 */
@FeignClient(name = "tradecenter-system-usercenter-center")
public interface UserBaseInterface {

	/**
	 * 检测登录
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/base/check_login.api")
	BaseResp<BaseBean> checkLogin(@RequestBody BaseReq<BaseBean> bean);
	
	/**
	 * 设置用户基础信息
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/base/set_user_data.api")
	BaseResp<BaseBean> setUserData(@RequestBody BaseReq<BaseBean> bean);

	/**
	 * 获取客服电话
	 *
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/get_service_hot_line.api")
	BaseResp getServiceHotLine(@RequestBody BaseReq<UserBean> bean);

	/**
	 * 用户头像反馈
	 *
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/reback_user_photo_status.api")
	BaseResp rebackUserPhotoStatus(BaseReq<UserBean> bean);

	/**
	 * 产品操作记录
	 */
	@RequestMapping("/user/product_opertion_info.api")
	BaseResp<UserBean> productOperationInfo(@RequestBody BaseReq<UserBean> bean);

	/**
	 * 产品反馈
	 */
	@RequestMapping("/user/product_feedback_info.api")
	BaseResp<UserBean> check_login_feedback_multipart(@RequestBody BaseReq<UserBean> bean);

	/**
	 * 产品反馈token查询
	 */
	@RequestMapping("/user/query_user_token.api")
	public void queryUserToken(@RequestBody BaseReq<BaseBean> bean);


	/**
	 * 银行卡鉴权和申请修改
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/apply_modify_bankcard.api")
	BaseResp authenticAndApplyModifyBankCard(UserBean bean);

}
