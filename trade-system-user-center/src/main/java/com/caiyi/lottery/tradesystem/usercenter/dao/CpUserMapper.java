package com.caiyi.lottery.tradesystem.usercenter.dao;

import bean.AlipayLoginBean;
import org.apache.ibatis.annotations.Mapper;

import bean.UserBean;
import pojo.CpUserPojo;
import pojo.UserPojo;

@Mapper
public interface CpUserMapper {

	/**
	 * cpuser.sp_user_allyregister
	 <in-parm>
	 <parm name="是否新用户" property="isNew"/>
	 <parm name="授权唯一标识" property="merchantacctid"/>
	 <parm name="登录类型" property="type"/>
	 <parm name="用户类型" property="usertype"/>
	 <parm name="用户编号" property="uid"/>
	 <parm name="登录密码" property="pwd"/>
	 <parm name="用户来源" property="comeFrom"/>
	 <parm name="注册IP" property="ipAddr"/>
	 <parm name="电子邮件" property="mailAddr"/>
	 <parm name="手机号码" property="mobileNo"/>
	 <parm name="域名" property="host"/>
	 <parm name="备注" property="memo"/>
	 <parm name="用户类型 支付宝用" property="hztype"/>
	 <parm name="用户惟一序列ID" property="cuserId"/>
	 <parm name="订单推送key" property="partner"/>
	 <parm name="渠道值" property="source"/>
	 <parm name="手机IMEI" property="imei"/>
	 <parm name="加密私钥" property="privateKey"></parm>
	 </in-parm>

	 <out-parm>
	 <parm name="错误编号" property="busiErrCode"/>
	 <parm name="错误描叙" property="busiErrDesc"/>
	 </out-parm>
	 @param alipayLoginBean
	 */
	void allyRegister(AlipayLoginBean alipayLoginBean);
    /**
     * cpuser.sp_user_bind_yz存储过程调用
     * <parameter property="uid" jdbcType="VARCHAR" mode="IN"/>
     * <parameter property="flag" jdbcType="INTEGER" mode="IN"/>
     * <parameter property="verificationCode" jdbcType="VARCHAR" mode="IN"/>

     * <parameter property="code" jdbcType="INTEGER" mode="OUT"/>
     * <parameter property="decs" jdbcType="VARCHAR" mode="OUT"/>
     * @param pojo
     */
    void userBindCheck(CpUserPojo pojo);

    /**
     * cpuser.sp_user_mobregister_send_yzm存储过程调用
     * <parameter property="mobileNo" jdbcType="VARCHAR" mode="IN"/>
     <parameter property="flag" jdbcType="INTEGER" mode="IN"/>
     <parameter property="yzm" jdbcType="VARCHAR" mode="IN"/>
     <parameter property="ipAddr" jdbcType="VARCHAR" mode="IN"/>
     <parameter property="source" jdbcType="VARCHAR" mode="IN"/>

     <parameter property="busiErrCode" jdbcType="INTEGER" mode="OUT"/>
     <parameter property="busiErrDesc" jdbcType="VARCHAR" mode="OUT"/>
     <parameter property="temporaryId" jdbcType="VARCHAR" mode="OUT"/>
     * @param pojo
     */
    void userSendMsg(CpUserPojo pojo);

    /**
     * 
	 * cpuser.sp_user_getpwd_yz(?,?,?,?,?,?)
		<parameterMap id="userSendMobSms" type="bean.UserBean">
	        <parameter property="uid" jdbcType="VARCHAR" mode="IN"/>
	        <parameter property="flag" jdbcType="INTEGER" mode="IN"/>
	        <parameter property="yzm" jdbcType="VARCHAR" mode="IN"/>
	        <parameter property="mobileNo" jdbcType="VARCHAR" mode="IN"/>
	        <parameter property="mailAddr" jdbcType="VARCHAR" mode="IN"/>
	        <parameter property="comeFrom" jdbcType="VARCHAR" mode="IN"/>
	
	        <parameter property="busiErrCode" jdbcType="INTEGER" mode="OUT"/>
	        <parameter property="busiErrDesc" jdbcType="VARCHAR" mode="OUT"/>
    	</parameterMap>
     * @param bean
     */
	void userSendMobSms(UserBean bean);
	
	
	/**
	 * 
	call cpuser.sp_user_getpwd_yz(?,?,?,?,?,?)
	<parameterMap type="bean.UserBean" id="userGetPwdyzMap">
		<parameter property="uid" mode="IN" jdbcType="VARCHAR"/>
		<parameter property="flag" mode="IN" jdbcType="INTEGER"/>
		<parameter property="newValue" mode="IN" jdbcType="VARCHAR"/>
		<parameter property="yzm" mode="IN" jdbcType="VARCHAR"/>
		<parameter property="busiErrCode" mode="OUT" jdbcType="INTEGER"/>
		<parameter property="busiErrDesc" mode="OUT" jdbcType="VARCHAR"/>
	</parameterMap>
	 */
	void forgetPwdVerifyYzm(UserBean bean);

	/**
	 * <parm name="授权用户唯一标识" property="openid"/>
	 * <parm name="用户统一标识" property="unionid"/>
	 * <parm name="用户名" property="uid"/>
	 * <parm name="登录地址" property="ipAddr"/>

	 * <parm name="错误编号" property="busiErrCode"/>
	 * <parm name="错误描叙" property="busiErrDesc"/>
	 * <parm name="用户名" property="uid"/>
	 * <parm name="用户密码" property="pwd"/>
	 * @param pojo
	 */
	void wechatLogin(CpUserPojo pojo);


	void cpuserRegister(UserPojo pojo);
}
