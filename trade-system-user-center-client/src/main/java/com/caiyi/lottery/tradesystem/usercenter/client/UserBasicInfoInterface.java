package com.caiyi.lottery.tradesystem.usercenter.client;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.usercenter.clienterror.UserBasicInfoInterfaceError;
import org.springframework.web.bind.annotation.RequestMethod;
import pojo.Acct_UserPojo;
import pojo.UserAcctPojo;
import pojo.UserPojo;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户中心客户端接口
 */
@FeignClient(name = "tradecenter-system-usercenter-center")
public interface UserBasicInfoInterface {

	/**
	 * 设置用户基础信息
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/bind_bankcard.api")
	BaseResp<BaseBean> bankCardBind(@RequestBody BaseReq<UserBean> bean);

	/**
	 * 统计APP崩溃信息
     * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/calculate_breakdown_error.api")
	BaseResp<BaseBean> calculate_breakdown_error(@RequestBody BaseReq<UserBean> bean);

	/**
	 * 查询用户是否存在
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/checkIsExist.api")
	BaseResp<String> checkIsExist(@RequestBody BaseReq<UserBean> bean);

	/**
	 * 查询用户白名单等级
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/user_whitegrade.api")
	BaseResp<String> queryUserWhiteGrade(@RequestBody BaseReq<BaseBean> bean);

	/**
	 * 查询用户用户基础信息
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/user/user_basic_info.api")
	BaseResp<UserPojo> queryUserInfo(@RequestBody BaseReq<BaseBean> bean);

	/**
	 * 查询用户基础信息
	 * @return
	 */
	@RequestMapping(value = "/user/query_userinfo_cardCharge.api")
	BaseResp<UserPojo> queryUserInfoForCardCharge(@RequestBody BaseReq<BaseBean> baseReq);

	/**
	 * 获取用户积分
	 */
	@RequestMapping(value = "/user/query_userpoint.api")
	BaseResp<UserAcctPojo> getUserPoint(@RequestBody BaseReq<BaseBean> req);

	/**
	 * 更新用户积分
	 */
	@RequestMapping(value = "/user/update_userpoint.api")
	BaseResp updateUserPoint(@RequestBody BaseReq<UserBean> req);

    /**
	 * 查询用户充值次数
     */
	@RequestMapping(value = "/user/count_user_charge.api")
	BaseResp<Integer> countUserCharge(@RequestBody BaseReq<BaseBean> req);


	/**
	 *查询用户vip数量
	 */
	@RequestMapping(value = "/user/query_uservip_agentcnt.api")
	BaseResp<Integer> queryUserVipAgentCount(@RequestBody BaseReq<BaseBean> req);

	/**
	 * 查询appagentid
	 */
	@RequestMapping(value = "/user/query_app_agentid.api")
	BaseResp<String> queryAppagentId(@RequestBody BaseReq<BaseBean> req);

	/**
	 * 更新agentid
	 */
	@RequestMapping(value = "/user/update_agentid.api")
	BaseResp updateAgentId(@RequestBody BaseReq<UserBean> req);

	/**
	 *代理商转账检测等级
	 */
	@RequestMapping("/user/check_level")
	BaseResp check_level(@RequestBody BaseReq<BaseBean> req);

	/**
	 * 一年内的消费次数
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/user/count_out_nickid_inayear.api")
	BaseResp<Integer> countOutByNickidInAYear(BaseReq<BaseBean> req);

	/**
	 * 查询是否新用户
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/user/is_new_user.api")
	BaseResp<Integer> isNewUser(BaseReq<BaseBean> req);


	/**
	 * 投注查询余额、红包、ios白名单
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/user/query_useraccout_info.api")
	BaseResp<Acct_UserPojo> queryUserAccountInfo(@RequestBody BaseReq<BaseBean> req);

	/**
	 * 查询自买次数
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/user/count_selfbuy.api",method = RequestMethod.POST)
	BaseResp<Integer> countSelfBuy(@RequestBody BaseReq<BaseBean> req);


	@RequestMapping(value = "/user/query_agent_id.api",method = RequestMethod.POST)
	BaseResp<String> queryAgentId(@RequestBody BaseReq<UserBean> req);


}
