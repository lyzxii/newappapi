package com.caiyi.lottery.tradesystem.usercenter.service.impl;


import bean.UserBean;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import constant.UserConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.service.UserBasicInfoService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pojo.Acct_UserPojo;
import pojo.UserAcctPojo;
import pojo.UserPojo;

@Slf4j
@Service
public class UserBasicInfoImpl implements UserBasicInfoService{

	@Autowired
	UserMapper userMapper;

	@Autowired
	UserAcctMapper userAcctMapper;

	@Autowired
	UserChargeMapper userChargeMapper;

	@Autowired
	Agent_UserMapper agent_userMapper;

	@Autowired
	AppagentMapper appagentMapper;

	@Autowired
	Charge_UserMapper chargeUserMapper;

	@Autowired
	AgentMapper agentMapper;

	@Autowired
	AllyMapper allyMapper;

	@Autowired
	OrderInterface orderInterface;

	@Autowired
	Acct_UserMapper acct_userMapper;
	/**
	 * 查询用户白名单等级
	 */
	@Override
	public String queryUserWhiteGrade(BaseBean bean) {
		Integer whiteGrade = userMapper.queryUserWhitelistGrade(bean.getUid());
		if(null == whiteGrade){
			return null;
		}
		return whiteGrade+"";
	}

	/**
	 * 查询用户基本信息(手机,真实姓名,身份证非明文)
	 */
	@Override
	public UserPojo queryUserInfo(BaseBean bean) {
		UserPojo user = userMapper.queryUserInfo(bean.getUid());
		if(user==null){
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_QUERY_SAFEINFO_ERROR));
			bean.setBusiErrDesc("查询用户基础信息失败");
			return null;
		}
		return user;
	}

	@Override
	public UserPojo queryUserInfoForCardCharge(BaseBean bean) {
		UserPojo user = userMapper.queryUserInfoForCardCharge(bean.getUid());
		if(user==null){
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_QUERY_SAFEINFO_ERROR));
			bean.setBusiErrDesc("查询用户基础信息失败");
			return null;
		}
		return user;
	}


	@Override
	public UserAcctPojo queryUserPoint(BaseBean bean){
		UserAcctPojo userAcct= null;
		try {
			userAcct = userAcctMapper.getUserPoint(bean.getUid());
			if(userAcct!=null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("查询用户账户信息成功");
                return userAcct;
            }
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_QUERY_SAFEINFO_ERROR));
			bean.setBusiErrDesc("查询用户账户信息异常");
		}
		return userAcct;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateUserPoint(UserBean bean) throws Exception{
		int flag=bean.getFlag();//增加或者减少标记
		try {
			int cnt;
			if(flag==1){//增加
				cnt=userAcctMapper.addUserPoint(bean.getPoint(), bean.getUid());
			}else{//减少
                cnt=userAcctMapper.decreaseUserPoint(bean.getPoint(),bean.getUid());
			}
			if(cnt!=1){
				throw new Exception("更新积分错误");
			}
			return 1;
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_POINT_UPDATE_ERROR));
			bean.setBusiErrDesc("更新用户积分异常");
			log.error("更新用户积分异常，uid:{}",bean.getUid(),e);
		}
		return 0;
	}

	@Override
	public Integer countUserCharge(BaseBean bean) {
		try {
			return userChargeMapper.countUserCharge(bean.getUid());
		} catch (Exception e) {
			bean.setBusiErrCode(-1);
			log.error("查询用户充值次数异常，uid:{}",bean.getUid(),e);
		}
		return 0;
	}

	/**
	 * 判断用户是否是vip
	 * @param bean
	 * @return
	 */
	@Override
	public Integer queryUserVipAgentCount(BaseBean bean) {
		try {
			return  agent_userMapper.queryUserVipAgentCount(bean.getUid());
		} catch (Exception e) {
			bean.setBusiErrCode(-1);
		}
		return 0;
	}

	@Override
	public String queryAppAgentId(BaseBean bean) {
		try {
			return appagentMapper.queryAgentId(bean.getSource());
		} catch (Exception e) {
			log.error("查询用户agentid错误,uid:{}",bean.getUid());
		}
		return null;
	}

	/**
	 * 更新token
	 * @param bean
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateAgentid(UserBean bean) {
		try {
			int flag=userMapper.updateAgentid(bean.getAgentid(),bean.getUid());
			if(flag!=1){
                throw new RuntimeException("更新appAgentid出错");
			}
			return flag;
		} catch (Exception e) {
			bean.setBusiErrCode(-1);
			log.error("更新appAgentid出错，uid:{}",bean.getUid(),e);
		}
		return 0;
	}

	/**
	 * 查询用户白名单等级
	 * @param cnickid
	 * @return
	 */
	@Override
	public UserPojo queryUserVipAndWhitelistLevel(String cnickid) {
		log.info("查询用户vip级别和白名单等级,用户名=" + cnickid);
		UserPojo user=agent_userMapper.queryUserVipAndWhitelistLevel(cnickid);
		if(user!=null){
			return user;
		}else{
			log.error("查询用户vip级别和白名单等级错误,uid:{}",cnickid);
			return null;
		}
	}

	/**
	 * 查询一年内的消费次数
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	@Override
	public Integer countOutByNickidInAYear(BaseBean bean) throws Exception {
		return userChargeMapper.countOutByNickidInOneYear(bean.getUid());
	}

	/**
	 * 查询是否新用户
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	@Override
	public Integer isNewUser(BaseBean bean) throws Exception {
		return chargeUserMapper.queryIsNewUser(bean.getMd5Mobile(), bean.getMd5IdCard());
	}

	@Override
	public Acct_UserPojo queryUserAccountInfo(BaseBean bean){
		try {
			bean.setBusiErrDesc("查询用户账户信息成功");
			return acct_userMapper.queryUserAccountInfo(bean.getUid());
		} catch (Exception e) {
			log.error("查询用户账户信息失败,uid:{}",bean.getUid(),e);
			bean.setBusiErrDesc("查询用户账户信息失败");
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
		}
		return null;
	}

	@Override
	public Integer countSelfBuy(BaseBean bean) {
		try {
			bean.setBusiErrDesc("查询用户合买次数成功");
			return userChargeMapper.countSelfBuy(bean.getUid());
		} catch (Exception e) {
			bean.setBusiErrDesc("查询用户合买次数失败");
			log.error("查询用户合买次数失败,uid:{}",bean.getUid(),e);
		}
		return null;
	}

	private void queryStepFirst(String key, String ckey, UserBean bean, long threadId) {
		long l = 0;
		if (ckey != null && ckey.length() > 0) {
			if (bean.getFlag() == 30 || bean.getFlag() == 27) {
				// 根据cnickid查询代理商ID
				String agentid = agentMapper.queryAgentId(bean.getUid());
				//bean.setCuserId(jr.get("cagentid")); TODO
				bean.setAgentid(agentid);
			}

			if (bean.getTr() == 0) {// 需要进行分页查询
				l = System.currentTimeMillis();
				//TODO

			}
			log.info("线程ID=" + threadId + ",queryUserInfo分页查询(ckey=" + ckey + ")记录总数耗时：" + (System.currentTimeMillis() - l) / 1000 + "s");
		} else {// 不需要分页
			if (bean.getFlag() == UserConstants.QUERY_KTKMONEY) {
				l = System.currentTimeMillis();
				//select type from tb_ally where nickid=?
				String type = allyMapper.queryTypeAlly(bean.getUid());
				if ("6".equals(type) || "10".equals(type) || "17".equals(type) || "18".equals(type) || "19".equals(type) || "20".equals(type)){
//					key = "u_query_" + bean.getFlag() + "_shyfk"; 查询可提款金额上海导购用户
					UserAcctPojo userAcctPojo = userAcctMapper.getAvilable(bean.getUid());
				}else{

				}

				log.info("线程ID=" + threadId + ",queryUserInfo查询可提款金额耗时：" + (System.currentTimeMillis() - l) / 1000 + "s");
			}
			l = System.currentTimeMillis();

//			log.info("线程ID=" + threadId + ",queryUserInfo不分页查询(key=" + key + ")记录信息(总数=" + jrs.size() + ")耗时：" + (System.currentTimeMillis() - l) / 1000 + "s");

		}
	}

}
