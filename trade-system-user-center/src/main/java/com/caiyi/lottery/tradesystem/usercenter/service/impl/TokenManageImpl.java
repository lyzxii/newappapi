package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.TokenBean;
import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.BaseConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.pojo.TbTokenPojo;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.redis.util.CacheUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.dao.TokenManageMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.UserMapper;
import com.caiyi.lottery.tradesystem.usercenter.service.TokenManageService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserRecordService;
import com.caiyi.lottery.tradesystem.usercenter.util.TokenGenerator;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import constant.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.UserPojo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户中心-Token实现类
 * 
 * @create 2017-11-27 14:34:03
 */
@Slf4j
@Service("tokenManageImpl")
public class TokenManageImpl implements TokenManageService {

	@Autowired
	private TokenManageMapper tokenManageMapper;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRecordService userLogRecordService;
	@Autowired
	private RedisInterface redisInterface;
	
	@Override
	public void registerToken(UserBean bean) {
		log.info(
				"注册新token,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token=" + bean.getAccesstoken());
		try {
			TbTokenPojo tbTokenPojo = new TbTokenPojo();
			tbTokenPojo.setAccesstoken(bean.getAccesstoken());
			tbTokenPojo.setExpiresin(UserConstants.ONEWEEK_EXPIRESTIME);
			tbTokenPojo.setMobiletype(bean.getMtype());
			tbTokenPojo.setCnickid(bean.getUid());
			tbTokenPojo.setCpassword(bean.getPwd());
			tbTokenPojo.setAppid(bean.getAppid());
			tbTokenPojo.setParamjson(bean.getParamJson());
			int res = tokenManageMapper.saveTokenInDB(tbTokenPojo);
			if (res != 1) {
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("注册token失败");
				throw new Exception("注册token失败");
			} else {
				log.info("注册新token成功：uid=" + bean.getUid() + ",appid=" + bean.getAppid());
			}
		} catch (Exception e) {
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("注册token失败");
			log.info("注册新token失败,uid=" + bean.getUid(), e);
		}
	}

	/**
	 * 更新token登录传递的密码.
	 * @param bean
	 * @param newPwd
	 * @throws Exception
	 */
	@Override
	public void updateTokenPassword(BaseBean bean, String newPwd) throws Exception {
		log.info("更新token password,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token=" + bean.getAccesstoken());
		if (StringUtil.isEmpty(bean.getAppid()) || StringUtil.isEmpty(bean.getAccesstoken())) {
			log.info("客户端没有传递token,直接从数据库查询最新可用token,nickid=" + bean.getUid());
			List<TokenBean> tbs = tokenManageMapper.selectLatestTokenByNickid(bean.getUid());
			if (tbs != null && tbs.size() > 0) {
				TokenBean token = tbs.get(0);
				bean.setAppid(token.getAppid());
				bean.setAccesstoken(token.getAccessToken());
			}
			// 更新Token
			int ret = tokenManageMapper.updateTokenPwd(bean.getAppid(), bean.getAccesstoken(), newPwd);
			if (1 != ret) {
				throw new Exception("更新token password失败");
			} else {
				log.info("更新token password参数成功：uid=" + bean.getUid() + ",appid=" + bean.getAppid());
			}
		}
	}

	/**
	 * 更新token
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateToken(String viplevel, String whitegrade, BaseBean bean) {
		if (bean.getLogintype() == 1) {
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey(bean.getAppid());
			String tokenBean = CacheUtil.getJsonString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
			if (!StringUtil.isEmpty(tokenBean)) {
				JSONObject tokenBeanJson = JSONObject.parseObject(tokenBean);
				String tokenParamJson = ""; // memcache 缓存中 代理等级 和 白名单 数据
				String currentParamJson = "";// 用户最新 代理等级 和 白名单值
				if (!StringUtil.isEmpty(tokenBeanJson.getString("paramJson"))) {// 已经有附加参数
					JSONObject jsObj = JSONObject.parseObject(bean.getParamJson());
					jsObj.put(BaseConstant.VLEVEL, viplevel);
					jsObj.put(BaseConstant.OPENUSER, whitegrade);
					tokenParamJson = jsObj.toString();
					tokenBeanJson.put("paramJson", tokenParamJson);
				} else {
					Map<String, String> map = new HashMap<String, String>();
					map.put(BaseConstant.VLEVEL, viplevel);
					map.put(BaseConstant.OPENUSER, whitegrade);
					JSONObject jsObj = JSONObject.parseObject(JSONObject.toJSONString(map));
					currentParamJson = jsObj.toString();
					tokenBeanJson.put("paramJson",currentParamJson);
				}

				// 更新到缓存
				cacheBean.setKey(bean.getAppid());
				cacheBean.setValue(tokenBeanJson.toJSONString());
				cacheBean.setTime(Constants.TIME_DAY);// 1天
				CacheUtil.setString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
				// 更新数据到数据库
				bean.setParamJson(tokenBeanJson.getString("paramJson"));
				System.out.println("bean.paramJson:" + tokenBeanJson.getString("paramJson"));
				// 因为updateToken调用地方比较多 有的地方没有查询用户级别和白名单 导致currentParamJson为空
				if (!currentParamJson.equals(tokenParamJson) && !StringUtil.isEmpty(currentParamJson)) {
					log.info("------>currentParamJson:" + currentParamJson + "  tokenParamJson:" + tokenParamJson
							+ "---> call updateTokenParam !!!");
					log.info("更新token param json,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token="
							+ bean.getAccesstoken());
					int ret = tokenManageMapper.updateTokenParam(bean.getAppid(), bean.getAccesstoken(),
							bean.getParamJson());
					if (ret != 1) {
						log.info("更新token的paramJson失败,用户名:" + bean.getUid() + " appid:" + bean.getAppid()
								+ " accesstoken:" + bean.getAccesstoken());
					}
				}
			}
		}
	}

	/**
	 * token登录检测
	 */
	@Override
	public BaseBean checkLogin(BaseBean bean) {
		if (bean.getLogintype() == 1) { // token登录
			if (StringUtil.isEmpty(bean.getAccesstoken())||StringUtil.isEmpty(bean.getAppid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_UNLOGIN));
				bean.setBusiErrDesc("用户未登录");
			}else {
				String[] result = TokenGenerator.authToken(bean.getAccesstoken(), bean.getAppid());
				if ("1".equals(result[0])) {
					tokenLogin(bean);
				} else {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_TOKEN_AUTH_FAIL));
					bean.setBusiErrDesc(result[1]);
				}
			}

		} else {
        	bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_UNLOGIN));
        	bean.setBusiErrDesc("用户未登录");
        }
		return bean;
	}

	/**
	 * token登录
	 */
	@Override
	public void tokenLogin(BaseBean bean) {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(bean.getAppid());
		String tokenBean = CacheUtil.getJsonString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
		if (!StringUtil.isEmpty(tokenBean)) {
			JSONObject tokenBeanJson = JSONObject.parseObject(tokenBean);
			bean.setUid(tokenBeanJson.getString("uid"));
			bean.setPwd(tokenBeanJson.getString("pwd"));
			bean.setParamJson(tokenBeanJson.getString("paramJson"));
			return;
		} else {
			UserBean userbean = new UserBean();
			userbean.setAccesstoken(bean.getAccesstoken());
			userbean.setAppid(bean.getAppid());
			userbean.setIpAddr(bean.getIpAddr());
			queryUserToken(bean);
			if(bean.getBusiErrCode()==0){
				TokenBean token = new TokenBean();
                token.setUid(bean.getUid());
                token.setPwd(bean.getPwd());
                token.setAccessToken(bean.getAccesstoken());
                token.setAppid(bean.getAppid());
                token.setParamJson(bean.getParamJson());
                cacheBean.setKey(bean.getAppid());
                cacheBean.setValue(token.toJson());
                cacheBean.setTime(Constants.TIME_DAY);
                boolean flag = CacheUtil.setString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER); //放入缓存中1天
                log.info("token登录重新加入缓存 =" +bean.getAppid()+"  结果:"+ flag);
			} else {
	            log.info("token登录失败:appid:" + bean.getAppid() + "  desc:" +bean.getBusiErrDesc()+" busiErrcode:"+
	            		bean.getBusiErrCode() + " time =" + DateTimeUtil.formatDate(new Date(),DateTimeUtil.DATETIME_FORMAT)+" uid:"+bean.getUid());
			}
		}
	}

	@Override
	public void queryUserToken(BaseBean bean) {
		try {
			log.info("查询token登录用户的账号密码和状态信息,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token="
					+ bean.getAccesstoken());
			
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("token信息已获取");
			
			List<TbTokenPojo> tokenList = tokenManageMapper.findByAccesstokenAppid(bean.getAccesstoken(), bean.getAppid());
			TbTokenPojo token = new TbTokenPojo();
			if (tokenList != null && tokenList.size() > 0) {
				token = tokenList.get(0);
				if (0 != token.getIstate()) {
					log.info("token登录信息已注销,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_TOKEN_DISABLE));
					bean.setBusiErrDesc("token已注销");
					return;
				}
				// 判断是否超时

				Date now1 = new Date();
				Date now2 = token.getLasttime();
				// 比较两者时间差
				long between = 0;
				if (now1.getTime() > now2.getTime()) {
					between = (now1.getTime() - now2.getTime()) / 1000; // 除以1000是为了转换成秒
				} else if (now1.getTime() < now2.getTime()) {
					between = (now2.getTime() - now1.getTime()) / 1000; // 除以1000是为了转换成秒
				}
				if (between >= token.getExpiresin()) {
					log.info("token登录信息已失效,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_TOKEN_EXPIRE));
					bean.setBusiErrDesc("账户登录信息失效，请重新登录");
					return;
				}
				
				UserPojo user = userMapper.querysByNickid(token.getCnickid());
				if(!token.getCpassword().equals(user.getPwd())){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_PWD_CHANGE));
					bean.setBusiErrDesc("密码已修改,请重新登录");
					log.info("用户密码已修改请重新登陆,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
					return;
				}
                if (0!=user.getState()) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_PWD_CHANGE)); 
                    bean.setBusiErrDesc("账户已禁用");
                    log.info("用户账户已禁用,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
                    return;
                }
                
                int rs = 0;
                //更新token最后使用时间和状态
                int state = bean.getBusiErrCode() == 0 ? 0 : 1;
                if (state == 0 && ((between * 1000 ) > Constants.TIME_HALFHOUR)) { //半小时内只更新一次最后活跃时间，防止memcache挂掉
                    rs = tokenManageMapper.updateByAccesstokenAppid(bean.getAccesstoken(), bean.getAppid());
                } else {
                    rs = tokenManageMapper.updateByAccesstokenAppid1(state, bean.getBusiErrDesc(), bean.getAccesstoken(), bean.getAppid());
                }
               
                if(rs == 1) {
                    log.info("token信息更新成功,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
                }else{
                	log.info("token信息更新失败,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
                    throw new Exception("token信息更新失败");
                }
                if (bean.getBusiErrCode() ==0) {
                    bean.setUid(token.getCnickid());//设置用户名和密码
                    bean.setPwd(token.getCpassword());
                    bean.setParamJson(token.getParamjson());
                }
			} else {
				log.info("未查到相关token记录,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid());
				bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_TOKEN_UNFIND));
				bean.setBusiErrDesc("未查到相关token记录");
				return;
			}
		    if (bean.getBusiErrCode() ==0) {
		    	UserBean userbean = new UserBean();
				userLogRecordService.addUserOperLog(userbean, "token用户登录", "[成功]");
            }
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_TOKEN_ERROR));
			bean.setBusiErrDesc("查询token信息出错");
			log.info("token信息更新失败,appid:"+bean.getAppid()+" accesstoken:"+bean.getAccesstoken()+" uid:"+bean.getUid(), e);
			UserBean userbean = new UserBean();
			userLogRecordService.addUserOperLog(userbean, "[失败]："+bean.getBusiErrDesc(), "[成功]");
		}
	}
}
