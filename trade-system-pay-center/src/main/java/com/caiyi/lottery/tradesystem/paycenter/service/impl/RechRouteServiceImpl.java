package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import bean.SafeBean;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.CardSupportChannelMapper;
import com.caiyi.lottery.tradesystem.paycenter.dao.RechCardMapper;
import com.caiyi.lottery.tradesystem.paycenter.dao.RechargeWayMapper;
import com.caiyi.lottery.tradesystem.paycenter.dao.RouteCardMapper;
import com.caiyi.lottery.tradesystem.paycenter.service.BaseService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechRouteService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.safecenter.clientwrapper.SafeCenterWrapper;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pay.bean.PayBean;
import pay.constant.RechRouteConstant;
import pay.dto.RechRouteDto;
import pay.pojo.CardSupportChannelPojo;
import pay.pojo.RechCardPojo;
import pay.pojo.RechargeWayPojo;
import pay.pojo.RouteCardPojo;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class RechRouteServiceImpl implements RechRouteService {

	@Autowired
	RedisClient redisClient;
	@Autowired
	SafeCenterWrapper safeCenterWrapper;
	@Autowired
	UserBasicInfoWrapper userBasicInfoWrapper;
	@Autowired
	BaseService baseService;
	@Autowired
	RechCardMapper rechCardMapper;
	@Autowired
	RouteCardMapper routeCardMapper;
	@Autowired
	CardSupportChannelMapper cardSupportChannelMapper;
	@Autowired
	RechargeWayMapper rechargeWayMapper;

	@Override
	public RechRouteDto distributeRoute(PayBean bean) {
		log.info("路由分配充值渠道,用户名:" + bean.getUid() + " 充值金额:" + bean.getAddmoney());
		// 从缓存中获取配置文件内容,如果没有则读取文件,并将文件内容放入缓存中
		JXmlWrapper config = JXmlWrapper.parse(new File(FileConstant.RECHARGE_ROUTE));
		Map<String, Integer> routeParamMap = new HashMap<String, Integer>();
		routeParamMap.put("maxLimit", 0);// 最大限制金额(可用来根据用户动态分配最大金额)
		RechRouteDto rechRouteDto = new RechRouteDto();
		appendUserInfo(bean, rechRouteDto);
		if (!baseService.getUserWhiteGrade(bean)) {
			bean.setWhitelistGrade(0);
			log.info("充值路由查询用户白名单等级失败,用户名:" + bean.getUid());
		}
		rechargeRoute(bean, config, routeParamMap, rechRouteDto);
		Integer maxLimit = routeParamMap.get("maxLimit");
		if (bean.getAddmoney() > 50000) {// 如果充值金额超过5w检测可用最大金额
			if (bean.getAddmoney() > maxLimit) {
				bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_OUT_MONEY_LIMIT));
				bean.setBusiErrDesc("单笔充值金额超过" + maxLimit + "元，请登录官网www.9188.com使用网银支付或通过小额分批进行充值。");
				return null;
			}
		}
		return rechRouteDto;
	}

	// 充值路由
	private void rechargeRoute(PayBean bean, JXmlWrapper config, Map<String, Integer> routeParamMap,
			RechRouteDto rechRouteDto) {
		Map<String, Map<String, Map<String, String>>> orderRechargeAllMap = getOrderRechargeAllMap();
		Map<String, Map<String, String>> channelOrderAllMap = getChannelOrderAllMap();
		Map<String, String> rechargeConfigAllMap = getRechargeConfigAllMap();
		Map<String, Map<String, String>> rechargeDataBaseAllMap = getRechargeDataBaseAllMap();
		Map<String, Map<String, String>> userRechRecordMap = getUserRechRecordMap(bean.getUid());
		List<JXmlWrapper> rechargeList = config.getXmlNodeList("rechargeWay");
		for (JXmlWrapper rechargeWay : rechargeList) {
			String visible = rechargeWay.getStringValue("@visible");
			if ("N".equals(visible)) {
				continue;
			}
			String id = rechargeWay.getStringValue("@id");
			if (!("1".equals(id))) {
				String banStatus = rechargeWay.getStringValue("@banStatus");
				if ("Y".equals(banStatus)) {
					JSONObject rechJson = writeToJson(rechargeWay);
					rechRouteDto.getRechargeWay().add(rechJson);
					continue;
				}
			}
			if ("1".equals(id)) {// 表示获取已绑定的银行卡信息
				JSONObject rechJson = writeToJson(rechargeWay);
				rechargeRouteBankCard(bean, rechJson, routeParamMap, orderRechargeAllMap, channelOrderAllMap,
						rechargeConfigAllMap, rechargeDataBaseAllMap, config, userRechRecordMap);
				rechRouteDto.getRechargeWay().add(rechJson);
			} else if ("2".equals(id)) {// 支付宝充值方式
				JSONObject rechJson = new JSONObject();
				Map<String, String> channelContent = putRechargeXmlIntoMap(rechargeWay);
				appendRechargeWay(channelContent, bean, rechJson, "alipay", routeParamMap, orderRechargeAllMap,
						channelOrderAllMap, rechargeConfigAllMap, rechargeDataBaseAllMap, config, userRechRecordMap);
				rechRouteDto.getRechargeWay().add(rechJson);
			} else if ("3".equals(id)) {// 微信充值方式
				JSONObject rechJson = new JSONObject();
				Map<String, String> channelContent = putRechargeXmlIntoMap(rechargeWay);
				appendRechargeWay(channelContent, bean, rechJson, "weixin", routeParamMap, orderRechargeAllMap,
						channelOrderAllMap, rechargeConfigAllMap, rechargeDataBaseAllMap, config, userRechRecordMap);
				rechRouteDto.getRechargeWay().add(rechJson);
			} else if ("4".equals(id)) {// 使用银行卡没有子节点，直接显示
				JSONObject rechJson = writeToJson(rechargeWay);
				rechRouteDto.getRechargeWay().add(rechJson);
			} else if ("20".equals(id)) {// QQ充值方式
				JSONObject rechJson = new JSONObject();
				Map<String, String> channelContent = putRechargeXmlIntoMap(rechargeWay);
				appendRechargeWay(channelContent, bean, rechJson, "tenpay", routeParamMap, orderRechargeAllMap,
						channelOrderAllMap, rechargeConfigAllMap, rechargeDataBaseAllMap, config, userRechRecordMap);
				rechRouteDto.getRechargeWay().add(rechJson);
			} else {// 其余单类充值方式
				JSONObject rechJson = new JSONObject();
				Map<String, String> channelContent = putRechargeXmlIntoMap(rechargeWay);
				Map<String, Integer> rechargeWayResult = getOtherSingleRechargeWay(channelContent, bean, rechJson,
						routeParamMap, rechargeWay, rechargeConfigAllMap, rechargeDataBaseAllMap, config);
				addRechargeWayByResult(channelContent, rechJson, rechargeWayResult, routeParamMap);
				rechRouteDto.getRechargeWay().add(rechJson);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, String>> getUserRechRecordMap(String uid) {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(uid+"_RechRecordMap");
		Map<String, Map<String, String>> userRechRecordMap = (Map<String, Map<String, String>>) redisClient
				.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if (null == userRechRecordMap) {
			userRechRecordMap = new HashMap<>();
		}
		return userRechRecordMap;
	}

	// 获取所有渠道的数据库配置
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, String>> getRechargeDataBaseAllMap() {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey("rechargeDataBaseAll");
		Map<String, Map<String, String>> rechargeDataBaseAllMap = (Map<String, Map<String, String>>) redisClient
				.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if (null == rechargeDataBaseAllMap) {
			rechargeDataBaseAllMap = new HashMap<>();
		}
		return rechargeDataBaseAllMap;
	}

	// 获取所有渠道的配置缓存
	@SuppressWarnings("unchecked")
	private Map<String, String> getRechargeConfigAllMap() {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey("rechargeConfigAll");
		Map<String, String> rechargeConfigAllMap = (Map<String, String>) redisClient.getObject(cacheBean, Map.class,
				log, SysCodeConstant.PAYCENTER);
		if (null == rechargeConfigAllMap) {
			rechargeConfigAllMap = new HashMap<>();
		}
		return rechargeConfigAllMap;
	}

	// 获取所有渠道与充值渠道顺序的对应关系
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, String>> getChannelOrderAllMap() {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey("channelOrderAll");
		Map<String, Map<String, String>> channelOrderAllMap = (Map<String, Map<String, String>>) redisClient
				.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if (null == channelOrderAllMap) {
			channelOrderAllMap = new HashMap<>();
		}
		return channelOrderAllMap;
	}

	// 获取所有渠道的顺序与充值渠道的对应关系
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Map<String, String>>> getOrderRechargeAllMap() {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey("orderRechargeAll");
		Map<String, Map<String, Map<String, String>>> orderRechargeAllMap = (Map<String, Map<String, Map<String, String>>>) redisClient
				.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if (null == orderRechargeAllMap) {
			orderRechargeAllMap = new HashMap<>();
		}
		return orderRechargeAllMap;
	}

	// 添加其他渠道的单个方式
	private Map<String, Integer> getOtherSingleRechargeWay(Map<String, String> channelContent, PayBean bean,
			JSONObject rechJson, Map<String, Integer> routeParamMap, JXmlWrapper rechargeWay,
			Map<String, String> rechargeConfigAllMap, Map<String, Map<String, String>> rechargeDataBaseAllMap,
			JXmlWrapper config) {
		Map<String, Integer> rechargeWayResult = new HashMap<String, Integer>();
		rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		List<JXmlWrapper> rechargeList = rechargeWay.getXmlNodeList("recharge");
		for (JXmlWrapper recharge : rechargeList) {
			String channel = recharge.getStringValue("@channel");
			String product = recharge.getStringValue("@product");
			String key = recharge.getStringValue("@key");
			Map<String, String> otherChannelContent = getOtherChannelContent(channel, product, key,
					rechargeDataBaseAllMap);
			if (null == otherChannelContent) {
				return rechargeWayResult;
			}
			boolean result = singleChannelCheck(otherChannelContent, channelContent, bean, rechargeWayResult, rechJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		Integer result = rechargeWayResult.get("result");
		if (result == null) {// 如果走到这一步且结果没有，表示没有符合条件的充值渠道，关闭该渠道
			rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		}
		return rechargeWayResult;
	}

	// 添加路由充值渠道
	private void appendRechargeWay(Map<String, String> channelContent, PayBean bean, JSONObject rechJson,
			String cacheKey, Map<String, Integer> routeParamMap,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap,
			Map<String, Map<String, String>> channelOrderAllMap, Map<String, String> rechargeConfigAllMap,
			Map<String, Map<String, String>> rechargeDataBaseAllMap, JXmlWrapper config, Map<String, Map<String, String>> userRechRecordMap) {
		// 获取最后一次充值的bankid和状态
		Map<String, String> lastRechargeMap = userRechRecordMap.get(bean.getUid() + "_" + cacheKey);
		if (lastRechargeMap == null) {// 没有最后一次的充值状态,按优先级进行充值
			log.info(bean.getUid() + "没有上一次的充值记录,cacheKey:"+cacheKey);
			lastRechargeMap=getChannelByWeight(bean,cacheKey);
			Map<String, Integer> rechargeWayResult;
			if(null==lastRechargeMap){//根据权重分配渠道失败
               rechargeWayResult = getRechargeWayDefault(channelContent, bean, rechJson, cacheKey,
					routeParamMap, rechargeConfigAllMap, config, orderRechargeAllMap);
			}else{
				rechargeWayResult = getRechargeWay(bean, rechJson, channelContent, lastRechargeMap,
						cacheKey, routeParamMap, rechargeConfigAllMap, config, orderRechargeAllMap, channelOrderAllMap);
			}
			addRechargeWayByResult(channelContent, rechJson, rechargeWayResult, routeParamMap);
		} else {
			log.info(bean.getUid() + "的上一次充值记录的channel为" + lastRechargeMap.get("channel"));
			Map<String, Integer> rechargeWayResult = getRechargeWay(bean, rechJson, channelContent, lastRechargeMap,
					cacheKey, routeParamMap, rechargeConfigAllMap, config, orderRechargeAllMap, channelOrderAllMap);
			addRechargeWayByResult(channelContent, rechJson, rechargeWayResult, routeParamMap);
		}
	}

	// 按最后一次的充值渠道进行排序筛选
	private Map<String, Integer> getRechargeWay(PayBean bean, JSONObject rechJson, Map<String, String> channelContent,
			Map<String, String> lastRechargeMap, String cacheKey, Map<String, Integer> routeParamMap,
			Map<String, String> rechargeConfigAllMap, JXmlWrapper config,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap, Map<String, Map<String, String>> channelOrderAllMap) {
		Map<String, Integer> defaultRechargeWayResult = new HashMap<String, Integer>();
		defaultRechargeWayResult.put("result", RechRouteConstant.CLOSE);
		// 获取渠道顺序
		Map<String, String> channelOrderMap = getChannelOrderFromCacheNotCard(bean, cacheKey, channelOrderAllMap);
		if (channelOrderMap == null) {
			return defaultRechargeWayResult;
		}
		Map<String, Map<String, String>> orderRechargeMap = getOrderRechargeMapFromCacheNotCard(bean, cacheKey,
				orderRechargeAllMap);
		if (orderRechargeMap == null) {
			return defaultRechargeWayResult;
		}
		String channel = lastRechargeMap.get("channel");
		String orderStr = channelOrderMap.get(channel);
		Integer order = (StringUtil.isEmpty(orderStr)?null:Integer.parseInt(orderStr));
		if (order == null) {
			Map<String, Integer> rechargeWayResult = getRechargeWayDefault(channelContent, bean, rechJson, cacheKey,
					routeParamMap, rechargeConfigAllMap, config, orderRechargeAllMap);
			return rechargeWayResult;
		} else {
			String flag = lastRechargeMap.get("flag");
			if ("1".equals(flag)) {// 上次充值成功,继续从上次充值开始向下
				Map<String, Integer> rechargeWayResult = getRechargeWayOrder(order, channelContent, orderRechargeMap,
						rechJson, bean, routeParamMap, rechargeConfigAllMap, config);
				return rechargeWayResult;
			} else {// 上次未成功
				order = order + 1;
				if (orderRechargeMap.get(String.valueOf(order)) == null) {// 上次充值渠道为最后一个充值渠道
					Map<String, Integer> rechargeWayResult = getRechargeWayDefault(channelContent, bean, rechJson,
							cacheKey, routeParamMap, rechargeConfigAllMap, config, orderRechargeAllMap);
					return rechargeWayResult;
				} else {
					Map<String, Integer> rechargeWayResult = getRechargeWayOrder(order, channelContent,
							orderRechargeMap, rechJson, bean, routeParamMap, rechargeConfigAllMap, config);
					return rechargeWayResult;
				}
			}
		}
	}

	// 根据指定顺序开始循环找到第一个符合条件的充值渠道
	private Map<String, Integer> getRechargeWayOrder(Integer order, Map<String, String> channelContent,
			Map<String, Map<String, String>> orderRechargeMap, JSONObject rechJson, PayBean bean,
			Map<String, Integer> routeParamMap, Map<String, String> rechargeConfigAllMap, JXmlWrapper config) {
		Map<String, Integer> rechargeWayResult = new HashMap<String, Integer>();
		for (int i = order; i < orderRechargeMap.size(); i++) {// 从指定顺序到最后
			Map<String, String> singleChannel = orderRechargeMap.get(String.valueOf(i));
			boolean result = singleChannelCheck(singleChannel, channelContent, bean, rechargeWayResult, rechJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		for (int i = 0; i < order; i++) {// 从最开始到指定顺序
			Map<String, String> singleChannel = orderRechargeMap.get(String.valueOf(i));
			boolean result = singleChannelCheck(singleChannel, channelContent, bean, rechargeWayResult, rechJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		Integer result = rechargeWayResult.get("result");
		if (result == null) {// 如果走到这一步且结果没有，表示没有符合条件的充值渠道，关闭该渠道
			rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		}
		return rechargeWayResult;
	}

	// 从缓存中获取通道与顺序的映射关系
	private Map<String, String> getChannelOrderFromCacheNotCard(PayBean bean, String cacheKey, Map<String, Map<String, String>> channelOrderAllMap) {
		Map<String, String> channelOrderMap = channelOrderAllMap.get("channelOrderNewMap_" + cacheKey);
		if(channelOrderMap != null){
			log.info("channelOrderNewMap=====>" + channelOrderMap.toString() + " 用户名:" + bean.getUid() + " key:"
					+ "channelOrderNewMap_" + cacheKey + " formMap");
			return channelOrderMap;
		}
		channelOrderMap = putChannelOrderIntoCacheNotCard(cacheKey, channelOrderAllMap);
		if (null == channelOrderMap) {
			return null;
		}
		log.info("channelOrderNewMap=====>" + channelOrderMap.toString() + " 用户名:" + bean.getUid() + " key:"
				+ "channelOrderNewMap_" + cacheKey + " fromDB");
		return channelOrderMap;
	}

	// 将通道与顺序的映射关系存入缓存
	private Map<String, String> putChannelOrderIntoCacheNotCard(String cacheKey, Map<String, Map<String, String>> channelOrderAllMap) {
		Map<String, String> channelOrderMap = new HashMap<String, String>();
		List<RechargeWayPojo> rechargeWayList = rechargeWayMapper.queryOpenRechWayByCategory(cacheKey);
		if (null != rechargeWayList && rechargeWayList.size() > 0) {
			int order = 0;
			for (RechargeWayPojo rechargeWay : rechargeWayList) {
				String channel = rechargeWay.getChannel();
				String product = rechargeWay.getProduct();
				String key = rechargeWay.getKey();
				channelOrderMap.put(channel + product + key, String.valueOf(order));
				order++;
			}
			channelOrderAllMap.put("channelOrderNewMap_" + cacheKey, channelOrderMap);
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey("channelOrderAll");
			cacheBean.setValue(JSONObject.toJSONString(channelOrderAllMap));
			cacheBean.setTime(Constants.TIME_DAY * 7);
			redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
			return channelOrderMap;
		}
		return null;
	}

	// 根据默认的优先级获取可使用的充值渠道
	private Map<String, Integer> getRechargeWayDefault(Map<String, String> channelContent, PayBean bean,
			JSONObject rechJson, String cacheKey, Map<String, Integer> routeParamMap,
			Map<String, String> rechargeConfigAllMap, JXmlWrapper config,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap) {
		Map<String, Integer> rechargeWayResult = new HashMap<String, Integer>();
		rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		Map<String, Map<String, String>> orderRechargeMap = getOrderRechargeMapFromCacheNotCard(bean, cacheKey,
				orderRechargeAllMap);
		if (null == orderRechargeMap) {
			return rechargeWayResult;
		}
		for (int i = 0; i < orderRechargeMap.size(); i++) {
			Map<String, String> singleChannel = orderRechargeMap.get(String.valueOf(i));
			boolean result = singleChannelCheck(singleChannel, channelContent, bean, rechargeWayResult, rechJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		Integer result = rechargeWayResult.get("result");
		if (result == null) { // 如果走到这一步且结果没有，表示没有符合条件的充值渠道，关闭该渠道
			rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		}
		return rechargeWayResult;
	}

	// 从缓存中获取顺序与充值方式的映射map (QQ,微信,支付宝)
	private Map<String, Map<String, String>> getOrderRechargeMapFromCacheNotCard(PayBean bean, String cacheKey,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap) {
		Map<String, Map<String, String>> orderRechargeMap = orderRechargeAllMap.get("orderRechargeNewMap_" + cacheKey);
		if (orderRechargeMap != null) {
			log.info("orderRechargeNewMap=====>" + orderRechargeMap.toString() + "用户名:" + bean.getUid() + " key:"
					+ "orderRechargeNewMap_" + cacheKey + " fromMap");
			return orderRechargeMap;
		}
		orderRechargeMap = putOrderRechargeMapIntoCacheNotCard(cacheKey, orderRechargeAllMap);
		if (null == orderRechargeMap) {
			return null;
		}
		log.info("orderRechargeNewMap=====>" + orderRechargeMap.toString() + "用户名:" + bean.getUid() + " key:"
				+ "orderRechargeNewMap_" + cacheKey + " fromDB");
		return orderRechargeMap;
	}

	// 将顺序与映射的充值关系放入缓存中
	private Map<String, Map<String, String>> putOrderRechargeMapIntoCacheNotCard(String cacheKey,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap) {
		Map<String, Map<String, String>> orderRechargeMap = new HashMap<String, Map<String, String>>();
		List<RechargeWayPojo> rechargeWayList = rechargeWayMapper.queryOpenRechWayByCategory(cacheKey);
		if (null != rechargeWayList && rechargeWayList.size() > 0) {
			int order = 0;
			for (RechargeWayPojo rechargeWay : rechargeWayList) {
				Map<String, String> channelContent = new HashMap<String, String>();
				channelContent.put("channel", rechargeWay.getChannel());
				channelContent.put("product", rechargeWay.getProduct());
				channelContent.put("key", rechargeWay.getKey());
				channelContent.put("minlimit", rechargeWay.getMinlimit());
				channelContent.put("maxlimit", rechargeWay.getMaxlimit());
				channelContent.put("daylimit", rechargeWay.getDaylimit());
				channelContent.put("openflag", rechargeWay.getOpenflag());
				channelContent.put("bindIdCard", rechargeWay.getBindIdcard());
				orderRechargeMap.put(String.valueOf(order), channelContent);
				order++;
			}
			orderRechargeAllMap.put("orderRechargeNewMap_" + cacheKey, orderRechargeMap);
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey("orderRechargeAll");
			cacheBean.setValue(JSONObject.toJSONString(orderRechargeAllMap));
			cacheBean.setTime(Constants.TIME_DAY * 7);
			redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
			return orderRechargeMap;
		}
		log.info("没有支持" + cacheKey + "的orderRechargeNewMap缓存");
		return null;
	}

	private Map<String, String> putRechargeXmlIntoMap(JXmlWrapper rechargeWay) {
		Map<String, String> channelContent = new HashMap<String, String>();
		Element element = rechargeWay.getXmlRoot();
		@SuppressWarnings("unchecked")
		List<Attribute> attrList = element.getAttributes();
		for (Attribute attr : attrList) {
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			channelContent.put(attrName, attrValue);
		}
		return channelContent;
	}

	// 路由充值银行卡
	private void rechargeRouteBankCard(PayBean bean, JSONObject rechJson, Map<String, Integer> routeParamMap,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap,
			Map<String, Map<String, String>> channelOrderAllMap, Map<String, String> rechargeConfigAllMap,
			Map<String, Map<String, String>> rechargeDataBaseAllMap, JXmlWrapper config, Map<String, Map<String, String>> userRechRecordMap) {
		log.info("加载用户银行卡列表 uid==" + bean.getUid());
		JSONArray cardArray = new JSONArray();
		List<RechCardPojo> cardList = rechCardMapper.queryUserVisibleCard(bean.getUid());
		List<SafeBean> realCardNoList = baseService.getRealCardList(cardList, bean);
		if (null == cardList || null == realCardNoList) {
			rechJson.put("card", cardArray);
			return;
		}
		Map<String, Map<String, String>> bankCardChannelMap = getBankCardChannelMapFromCache();
		for (RechCardPojo rechCard : cardList) {// 数据库中的银行卡信息
			for (SafeBean safeBean : realCardNoList) {// 安全中心银行卡信息
				if (rechCard.getSafeKey().equals(safeBean.getRechargeCardId())) {
					JSONObject cardJson = new JSONObject();
					bean.setBankCode(rechCard.getBankCode());
					bean.setCardtype(rechCard.getCardtype());
					bean.setCardNo(rechCard.getSafeKey());
					Map<String, String> channelContent = getBankCardChannelContentFromCache(bean, bankCardChannelMap);
					if (null == channelContent) {
						log.info("没有" + bean.getBankCode() + bean.getCardtype() + "该渠道的数据库记录,用户名:" + bean.getUid());
						continue;
					}
					channelContent.put("bankcode", bean.getBankCode());
					channelContent.put("cardtype", bean.getCardtype() + "");
					appendCardInfo(safeBean, channelContent);
					String visible = channelContent.get("visible");
					if ("N".equals(visible)) {
						return;
					}
					String banStatus = channelContent.get("banStatus");
					if ("Y".equals(banStatus)) {
						writeChannelIntoResult(cardJson, channelContent);
						cardArray.add(cardJson);
						continue;
					}
					// 获取最后一次的充值状态
					Map<String, String> lastRechargeMap = userRechRecordMap.get(bean.getUid() + "_" + bean.getBankCode() + bean.getCardtype());
					if (null == lastRechargeMap) {
						log.info(bean.getUid() + "没有上一次的充值记录 bankCode:" + bean.getBankCode() + " cardType:"
								+ bean.getCardtype());
						lastRechargeMap=getChannelByWeight(bean,"bankcard");
						Map<String, Integer> rechargeWayResult;
						if(null==lastRechargeMap){//根据权重分配路由失败
							rechargeWayResult = getBankCardRechargeWayByDefault(bean, cardJson,
								channelContent, routeParamMap, orderRechargeAllMap, rechargeConfigAllMap,
								rechargeDataBaseAllMap, config);// 按默认顺序进行排序*/
						}else {
							rechargeWayResult = getBankCardRechargeWay(bean, cardJson, channelContent,
									lastRechargeMap, routeParamMap, orderRechargeAllMap, channelOrderAllMap,
									rechargeConfigAllMap, rechargeDataBaseAllMap, config);
						}
						addRechargeWayByResult(channelContent, cardJson, rechargeWayResult, routeParamMap);
					} else {
						log.info(bean.getUid() + "的上一次充值记录的bankid为" + lastRechargeMap.get("bankid") + " 充值渠道:"
								+ lastRechargeMap.get("channel") + " bankCode:" + bean.getBankCode() + " cardType:"
								+ bean.getCardtype());
						Map<String, Integer> rechargeWayResult = getBankCardRechargeWay(bean, cardJson, channelContent,
								lastRechargeMap, routeParamMap, orderRechargeAllMap, channelOrderAllMap,
								rechargeConfigAllMap, rechargeDataBaseAllMap, config);// 按上次充值渠道进行排序筛选
						addRechargeWayByResult(channelContent, cardJson, rechargeWayResult, routeParamMap);
					}
					// 将银行卡信息添加到卡列表中
					cardArray.add(cardJson);
				}
			}
		}
		rechJson.put("card", cardArray);
		bean.setCardNo("");// 银行卡号设为空
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, String>> getBankCardChannelMapFromCache() {
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey("bankCardChannel");
		Map<String, Map<String, String>> bankCardChannelMap = (Map<String, Map<String, String>>) redisClient
				.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if (null == bankCardChannelMap) {
			bankCardChannelMap = new HashMap<>();
		}
		return bankCardChannelMap;
	}

	// 按最后一次的充值渠道进行排序筛选
	private Map<String, Integer> getBankCardRechargeWay(PayBean bean, JSONObject cardJson,
			Map<String, String> channelContent, Map<String, String> lastRechargeMap, Map<String, Integer> routeParamMap,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap,
			Map<String, Map<String, String>> channelOrderAllMap, Map<String, String> rechargeConfigAllMap,
			Map<String, Map<String, String>> rechargeDataBaseAllMap, JXmlWrapper config) {
		Map<String, Integer> defaultRechargeWayResult = new HashMap<String, Integer>();
		defaultRechargeWayResult.put("result", RechRouteConstant.CLOSE);
		// 获取渠道顺序
		Map<String, String> channelOrderMap = getBankCardChannelOrderFromCache(bean, channelOrderAllMap);
		if (channelOrderMap == null) {
			return defaultRechargeWayResult;
		}
		Map<String, Map<String, String>> orderRechargeMap = getBankCardOrderRechargeMapFromCache(bean,
				orderRechargeAllMap);
		if (orderRechargeMap == null) {
			return defaultRechargeWayResult;
		}
		String channel = lastRechargeMap.get("channel");
		String orderStr = channelOrderMap.get(channel);
		Integer order = (StringUtil.isEmpty(orderStr)?null:Integer.parseInt(orderStr));
		if (order == null) {
			Map<String, Integer> rechargeWayResult = getBankCardRechargeWayByDefault(bean, cardJson, channelContent,
					routeParamMap, orderRechargeAllMap, rechargeConfigAllMap, rechargeDataBaseAllMap, config);
			return rechargeWayResult;
		} else {
			String flag = lastRechargeMap.get("flag");
			if ("1".equals(flag)) {// 上次充值成功,继续从上次充值开始向下
				Map<String, Integer> rechargeWayResult = getBankCardRechargeWayOrder(order, channelContent,
						orderRechargeMap, cardJson, bean, routeParamMap, orderRechargeAllMap, rechargeConfigAllMap,
						rechargeDataBaseAllMap, config);
				return rechargeWayResult;
			} else {// 上次未成功
				order = order + 1;
				if (orderRechargeMap.get(String.valueOf(order)) == null) {// 上次充值渠道为最后一个充值渠道
					Map<String, Integer> rechargeWayResult = getBankCardRechargeWayByDefault(bean, cardJson,
							channelContent, routeParamMap, orderRechargeAllMap, rechargeConfigAllMap,
							rechargeDataBaseAllMap, config);
					return rechargeWayResult;
				} else {
					Map<String, Integer> rechargeWayResult = getBankCardRechargeWayOrder(order, channelContent,
							orderRechargeMap, cardJson, bean, routeParamMap, orderRechargeAllMap, rechargeConfigAllMap,
							rechargeDataBaseAllMap, config);
					return rechargeWayResult;
				}
			}
		}
	}

	// 根据指定顺序开始循环找到第一个符合条件的充值渠道
	private Map<String, Integer> getBankCardRechargeWayOrder(Integer order, Map<String, String> channelContent,
			Map<String, Map<String, String>> orderRechargeMap, JSONObject cardJson, PayBean bean,
			Map<String, Integer> routeParamMap, Map<String, Map<String, Map<String, String>>> orderRechargeAllMap,
			Map<String, String> rechargeConfigAllMap, Map<String, Map<String, String>> rechargeDataBaseAllMap,
			JXmlWrapper config) {
		Map<String, Integer> rechargeWayResult = new HashMap<String, Integer>();
		for (int i = order; i < orderRechargeMap.size(); i++) {// 从指定顺序到最后
			Map<String, String> bankCardChannel = orderRechargeMap.get(String.valueOf(i));
			String channel = bankCardChannel.get("channel");
			String product = bankCardChannel.get("product");
			String key = bankCardChannel.get("key");
			Map<String, String> supportSingleChannelContent = getOtherChannelContent(channel, product, key,
					rechargeDataBaseAllMap);
			if (null == supportSingleChannelContent) {
				continue;
			}
			boolean supportResult = checkRecharge(supportSingleChannelContent, bean, rechargeConfigAllMap, config);
			if (!supportResult) {// 单个渠道不支持
				log.info(channel + "_" + product + "_" + key + "的通用规则不符合条件,用户名:" + bean.getUid() + " 银行卡:"
						+ bean.getBankCode() + bean.getCardtype());
				continue;
			}
			boolean result = singleChannelCheck(bankCardChannel, channelContent, bean, rechargeWayResult, cardJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		for (int i = 0; i < order; i++) {// 从最开始到指定顺序
			Map<String, String> bankCardChannel = orderRechargeMap.get(String.valueOf(i));
			String channelNow = bankCardChannel.get("channel");
			String product = bankCardChannel.get("product");
			String key = bankCardChannel.get("key");
			Map<String, String> supportSingleChannelContent = getOtherChannelContent(channelNow, product, key,
					rechargeDataBaseAllMap);
			boolean supportResult = checkRecharge(supportSingleChannelContent, bean, rechargeConfigAllMap, config);
			if (!supportResult) {// 单个渠道不支持
				log.info(channelNow + "_" + product + "_" + key + "的通用规则不符合条件,用户名:" + bean.getUid() + " 银行卡:"
						+ bean.getBankCode() + bean.getCardtype());
				continue;
			}
			boolean result = singleChannelCheck(bankCardChannel, channelContent, bean, rechargeWayResult, cardJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		Integer result = rechargeWayResult.get("result");
		if (result == null) {// 如果走到这一步且结果没有，表示没有符合条件的充值渠道，关闭该渠道
			rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		}
		return rechargeWayResult;
	}

	// 从缓存中获取通道与顺序的映射关系
	private Map<String, String> getBankCardChannelOrderFromCache(PayBean bean,
			Map<String, Map<String, String>> channelOrderAllMap) {
		Map<String, String> channelOrderMap = channelOrderAllMap
				.get("channelOrderNewMap_" + bean.getBankCode() + bean.getCardtype());
		if (channelOrderMap != null) {
			log.info("channelOrderNewMap=====>" + channelOrderMap.toString() + " 用户名:" + bean.getUid() + " key:"
					+ "channelOrderNewMap_" + bean.getBankCode() + bean.getCardtype() + " formMap");
			return channelOrderMap;
		}
		channelOrderMap = putBankCardChannelOrderIntoCache(bean, channelOrderAllMap);
		if (null == channelOrderMap) {
			return null;
		}
		log.info("channelOrderNewMap=====>" + channelOrderMap.toString() + " 用户名:" + bean.getUid() + " key:"
				+ "channelOrderNewMap_" + bean.getBankCode() + bean.getCardtype() + " formDB");
		return channelOrderMap;
	}

	// 将通道与顺序的映射关系存入缓存
	private Map<String, String> putBankCardChannelOrderIntoCache(PayBean bean,
			Map<String, Map<String, String>> channelOrderAllMap) {
		Map<String, String> channelOrderMap = new HashMap<String, String>();
		List<CardSupportChannelPojo> channelList = cardSupportChannelMapper
				.queryBankSupportChannelOpen(bean.getBankCode(), bean.getCardtype());
		if (channelList != null && channelList.size() > 0) {
			int order = 0;
			for (CardSupportChannelPojo channelPojo : channelList) {
				String channel = channelPojo.getChannel();
				String product = channelPojo.getProduct();
				String key = channelPojo.getKey();
				channelOrderMap.put(channel + product + key, String.valueOf(order));
				order++;
			}
			channelOrderAllMap.put("channelOrderNewMap_" + bean.getBankCode() + bean.getCardtype(), channelOrderMap);
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey("channelOrderAll");
			cacheBean.setValue(JSONObject.toJSONString(channelOrderAllMap));
			cacheBean.setTime(Constants.TIME_DAY * 7);
			redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
			return channelOrderMap;
		}
		log.info("没有" + bean.getBankCode() + bean.getCardtype() + "的channelOrderNewMap缓存");
		return null;
	}

	// 根据结果添加充值结果
	private void addRechargeWayByResult(Map<String, String> channelContent, JSONObject channelRet,
			Map<String, Integer> rechargeWayResult, Map<String, Integer> routeParamMap) {
		Integer result = rechargeWayResult.get("result");
		if (result == RechRouteConstant.OK) {
			return;
		}
		if (result == RechRouteConstant.OUT_OF_LIMIT) {
			Integer maxRecharge = rechargeWayResult.get("maxRecharge");
			channelContent.put("banStatus", "Y");
			channelContent.put("banContent", "单笔交易上限为" + maxRecharge + "元");
			writeChannelIntoResult(channelRet, channelContent);
			Integer maxLimit = routeParamMap.get("maxLimit");
			if (maxRecharge > maxLimit) {
				routeParamMap.put("maxLimit", maxRecharge);
			}
		}
		if (result == RechRouteConstant.OUT_OF_DAYLIMIT) {
			Integer daylimit = rechargeWayResult.get("daylimit");
			channelContent.put("banStatus", "Y");
			channelContent.put("banContent", "已达单日限额" + daylimit + "元");
			writeChannelIntoResult(channelRet, channelContent);
		}
		if (result == RechRouteConstant.CLOSE) {
			channelContent.put("banStatus", "Y");
			channelContent.put("banContent", "渠道升级中,暂不可用");
			writeChannelIntoResult(channelRet, channelContent);
		}
		// TODO 可以添加其余禁止原因
	}

	// 按默认顺序进行银行卡路由
	private Map<String, Integer> getBankCardRechargeWayByDefault(PayBean bean, JSONObject cardJson,
			Map<String, String> channelContent, Map<String, Integer> routeParamMap,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap,
			Map<String, String> rechargeConfigAllMap, Map<String, Map<String, String>> rechargeDataBaseAllMap,
			JXmlWrapper config) {
		Map<String, Integer> rechargeWayResult = new HashMap<String, Integer>();
		Map<String, Map<String, String>> orderRechargeMap = getBankCardOrderRechargeMapFromCache(bean,
				orderRechargeAllMap);
		if (orderRechargeMap == null) {
			rechargeWayResult.put("result", RechRouteConstant.CLOSE);
			log.info("没有支持" + bean.getBankCode() + bean.getCardtype() + "的银行卡渠道");
			return rechargeWayResult;
		}
		for (int i = 0; i < orderRechargeMap.size(); i++) {
			Map<String, String> bankCardChannel = orderRechargeMap.get(String.valueOf(i));
			String channel = bankCardChannel.get("channel");
			String product = bankCardChannel.get("product");
			String key = bankCardChannel.get("key");
			Map<String, String> supportSingleChannel = getOtherChannelContent(channel, product, key,
					rechargeDataBaseAllMap);
			if (null == supportSingleChannel) {
				continue;
			}
			boolean supportResult = checkRecharge(supportSingleChannel, bean, rechargeConfigAllMap, config);
			if (!supportResult) {// 单个渠道不支持
				log.info(channel + "_" + product + "_" + key + "的通用规则不符合条件,用户名:" + bean.getUid() + " 银行卡:"
						+ bean.getBankCode() + bean.getCardtype());
				continue;
			}
			boolean result = singleChannelCheck(bankCardChannel, channelContent, bean, rechargeWayResult, cardJson,
					routeParamMap, rechargeConfigAllMap, config);
			if (result) {
				return rechargeWayResult;
			}
		}
		Integer result = rechargeWayResult.get("result");
		if (result == null) { // 如果走到这一步且结果没有，表示没有符合条件的充值渠道，关闭该渠道
			rechargeWayResult.put("result", RechRouteConstant.CLOSE);
		}
		return rechargeWayResult;
	}

	// 单个渠道检测
	private boolean singleChannelCheck(Map<String, String> singleChannel, Map<String, String> channelContent,
			PayBean bean, Map<String, Integer> rechargeWayResult, JSONObject rechJson,
			Map<String, Integer> routeParamMap, Map<String, String> rechargeConfigAllMap, JXmlWrapper config) {
		// 检测该渠道是否可用
		boolean flag = checkRecharge(singleChannel, bean, rechargeConfigAllMap, config);
		if (flag) {// 该通道可用
			boolean passFlag = true;
			// 检测单日限额
			passFlag = rechargeDayLimitCheck(singleChannel, rechargeWayResult, bean) & passFlag;
			if (!passFlag) {// 单日限额检测通过
				return false;
			}
			// 检测充值渠道金额检测
			passFlag = rechargeLimitCheck(singleChannel, rechargeWayResult, bean) & passFlag;
			if (!passFlag) {
				return false;
			}
			// 将银行卡数据库信息,通道信息数据库信息,配置文件渠道信息放入JxmlWrapper
			JXmlWrapper recharge = mergeAllContent(channelContent, singleChannel, rechargeConfigAllMap, config);
			if (null == recharge) {// 没有生成指定的文件
				return false;
			}
			writeRetToJson(recharge, rechJson);
			Integer maxLimit = routeParamMap.get("maxLimit");
			Integer maxRecharge = Integer.parseInt(singleChannel.get("maxlimit"));
			if (maxRecharge > maxLimit) {
				routeParamMap.put("maxLimit", maxRecharge);
			}
			rechargeWayResult.put("result", RechRouteConstant.OK);
			return true;
		}
		log.info("通用规则不符合,用户名:" + bean.getUid() + " 充值渠道名称:" + singleChannel.get("channel") + "_"
				+ singleChannel.get("product") + "_" + singleChannel.get("key") + " source:" + bean.getSource()
				+ " appversion:" + bean.getAppversion());
		return false;
	}

	// 将结果写入至Json
	private void writeRetToJson(JXmlWrapper recharge, JSONObject rechJson) {
		Element element = recharge.getXmlRoot();
		@SuppressWarnings("unchecked")
		List<Attribute> attrList = element.getAttributes();
		for (Attribute attr : attrList) {
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			rechJson.put(attrName, attrValue);
		}
	}

	// 整合所有数据
	private JXmlWrapper mergeAllContent(Map<String, String> channelContent, Map<String, String> singleChannel,
			Map<String, String> rechargeConfigAllMap, JXmlWrapper config) {
		JXmlWrapper bankCardChanneXml = getRechargeXmlFromCache(singleChannel, rechargeConfigAllMap, config);
		if (null == bankCardChanneXml) {
			return null;
		}
		Set<String> channelKey = channelContent.keySet();
		for (String key : channelKey) {
			String value = channelContent.get(key);
			if(StringUtil.isEmpty(value)){
				value = "";
			}
			if (null == bankCardChanneXml.getStringValue("@" + key)) {
				bankCardChanneXml.addValue("@" + key, value);
			} else {
				bankCardChanneXml.setValue("@" + key, value);
			}
		}
		Set<String> bankCardKey = singleChannel.keySet();
		for (String key : bankCardKey) {
			String value = singleChannel.get(key);
			if(StringUtil.isEmpty(value)){
				value = "";
			}
			if (null == bankCardChanneXml.getStringValue("@" + key)) {
				bankCardChanneXml.addValue("@" + key, value);
			} else {
				bankCardChanneXml.setValue("@" + key, value);
			}
		}
		return bankCardChanneXml;
	}

	// 充值方式限额检测,true未通过,false不通过
	private boolean rechargeLimitCheck(Map<String, String> singleChannel, Map<String, Integer> rechargeWayResult,
			PayBean bean) {
		if (checkRechargeAddmoney(singleChannel, bean)) {// 检测充值金额
			return true;
		} else {
			Integer result = rechargeWayResult.get("result");
			if (null == result || result < RechRouteConstant.OUT_OF_LIMIT) {
				rechargeWayResult.put("result", RechRouteConstant.OUT_OF_LIMIT);
				// 使用resultParam字段,不同的错误原因所需的错误参数，在外层显示时，都取该字段
				rechargeWayResult.put("maxRecharge", Integer.parseInt(singleChannel.get("maxlimit").trim()));
			} else if (result == RechRouteConstant.OUT_OF_LIMIT) {
				int resultParam = rechargeWayResult.get("maxRecharge");
				int nowResultParam = Integer.parseInt(singleChannel.get("maxlimit").trim());
				if (nowResultParam > resultParam) {
					rechargeWayResult.put("maxRecharge", nowResultParam);
				}
			}
			return false;
		}
	}

	// 检测该大渠道的充值金额,返回true:检测通过 返回false:检测不通过
	private boolean checkRechargeAddmoney(Map<String, String> singleChannel, PayBean bean) {
		int maxRecharge = Integer.parseInt(singleChannel.get("maxlimit").trim());
		int minRecharge = Integer.parseInt(singleChannel.get("minlimit").trim());
		if (minRecharge <= bean.getAddmoney() && bean.getAddmoney() <= maxRecharge) {
			return true;
		}
		return false;
	}

	// 充值限额检测
	private boolean rechargeDayLimitCheck(Map<String, String> singleChannel, Map<String, Integer> rechargeWayResult,
			PayBean bean) {
		if (checkRechargeDaylimit(singleChannel, bean)) {
			return true;
		} else {
			Integer result = rechargeWayResult.get("result");
			if (null == result || result < RechRouteConstant.OUT_OF_DAYLIMIT) {
				rechargeWayResult.put("result", RechRouteConstant.OUT_OF_DAYLIMIT);
				rechargeWayResult.put("daylimit", Integer.parseInt(singleChannel.get("daylimit")));
			} else if (result == RechRouteConstant.OUT_OF_DAYLIMIT) {
				int resultParam = rechargeWayResult.get("daylimit");
				int nowResultParam = Integer.parseInt(singleChannel.get("daylimit"));
				if (nowResultParam > resultParam) {
					rechargeWayResult.put("daylimit", nowResultParam);
				}
			}
			return false;
		}
	}

	// 检测充值日限额
	@SuppressWarnings("unchecked")
	private boolean checkRechargeDaylimit(Map<String, String> singleChannel, PayBean bean) {
		if (StringUtil.isEmpty(bean.getCardNo())) {// 非银行卡不检测单日限额
			return true;
		}
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(DateTimeUtil.getCurrentDate() + "_" + singleChannel.get("channel") + "_"
				+ singleChannel.get("product") + "_" + bean.getCardNo());
		Map<String, String> userdaylimitMap = (Map<String, String>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
		if (null == userdaylimitMap) {// 没有限额
			return true;
		} else {
			// 已充值金额
			String addmoney = userdaylimitMap.get("addmoney");
			// 每日限额
			String daylimit = singleChannel.get("daylimit");
			double addmoneyD = Double.parseDouble(addmoney);
			double daylimitD = Double.parseDouble(daylimit);
			if (addmoneyD + bean.getAddmoney() > daylimitD) {
				return false;
			} else {
				return true;
			}
		}
	}

	// 检测单个渠道是否可用
	private boolean checkRecharge(Map<String, String> singleChannel, PayBean bean,
			Map<String, String> rechargeConfigAllMap, JXmlWrapper config) {
		// 检测该渠道是否打开
		String openFlag = singleChannel.get("openflag");
		if (!"1".equals(openFlag)) {
			return false;// 该通道关闭
		}
		String bindIdCard = singleChannel.get("bindIdCard");
		if ("2".equals(bindIdCard)) {
			if (StringUtil.isEmpty(bean.getIdcard())) {
				return false;// 该用户未绑定身份证
			}
		}
		JXmlWrapper channeXml = getRechargeXmlFromCache(singleChannel, rechargeConfigAllMap, config);
		if (null == channeXml) {
			log.info("recharge_route_new中没有该充值渠道:" + singleChannel.get("channel") + "_" + singleChannel.get("product")
					+ "_" + singleChannel.get("key"));
			return false;// 配置文件中没有该充值渠道
		}
		if (ParseGeneralRulesUtil.parseGeneralRulesNew(channeXml.getXmlNode("general-rules"), bean, log)) {
			return true;
		} else {
			return false;
		}
	}

	// 获取充值的xml
	private JXmlWrapper getRechargeXmlFromCache(Map<String, String> singleChannel,
			Map<String, String> rechargeConfigAllMap, JXmlWrapper config) {
		String channel = singleChannel.get("channel");
		String product = singleChannel.get("product");
		String key = singleChannel.get("key");
		String rechargeStr = rechargeConfigAllMap.get(channel + "_" + product + "_" + key + "_config");
		if (!StringUtil.isEmpty(rechargeStr)) {
			return JXmlWrapper.parse(rechargeStr);
		}
		JXmlWrapper recharge = putRechargeXmlIntoCache(channel, product, key, rechargeConfigAllMap, config);
		return recharge;
	}

	// 将充值的xml放入缓存
	private JXmlWrapper putRechargeXmlIntoCache(String channel, String product, String key,
			Map<String, String> rechargeConfigAllMap, JXmlWrapper config) {
		List<JXmlWrapper> rechargeWayList = config.getXmlNodeList("rechargeWay");
		for (JXmlWrapper rechargeWay : rechargeWayList) {
			List<JXmlWrapper> rechargeList = rechargeWay.getXmlNodeList("recharge");
			for (JXmlWrapper recharge : rechargeList) {
				String recChannel = recharge.getStringValue("@channel");
				String recProduct = recharge.getStringValue("@product");
				String reckey = recharge.getStringValue("@key");
				if (channel.equals(recChannel) && product.equals(recProduct) && key.equals(reckey)) {
					rechargeConfigAllMap.put(channel + "_" + product + "_" + key + "_config",
							recharge.toXmlString());
					CacheBean cacheBean = new CacheBean();
					cacheBean.setKey("rechargeConfigAll");
					cacheBean.setValue(JSONObject.toJSONString(rechargeConfigAllMap));
					cacheBean.setTime(Constants.TIME_DAY * 7);
					redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
					return recharge;
				}
			}
		}
		log.info("未找到" + channel + "_" + product + "_" + key + "_config的缓存信息");
		return null;
	}

	// 获取单个渠道内容
	private Map<String, String> getOtherChannelContent(String channel, String product, String key,
			Map<String, Map<String, String>> rechargeDataBaseAllMap) {
		Map<String, String> singleChannelContentMap = rechargeDataBaseAllMap
				.get(channel + "_" + product + "_" + key + "_database");
		if (singleChannelContentMap != null) {
			log.info("channelContent====>" + singleChannelContentMap.toString() + " key:" + channel + "_" + product
					+ "_" + key + "_database" + " fromMap");
			return singleChannelContentMap;
		}
		singleChannelContentMap = putOtherChannelContentIntoCache(channel, product, key, rechargeDataBaseAllMap);
		if (null == singleChannelContentMap) {
			return null;
		}
		log.info("channelContent====>" + singleChannelContentMap.toString() + " key:" + channel + "_" + product + "_"
				+ key + "_database" + " fromDB");
		return singleChannelContentMap;
	}

	// 将渠道内容放入缓存中
	private Map<String, String> putOtherChannelContentIntoCache(String channel, String product, String key,
			Map<String, Map<String, String>> rechargeDataBaseAllMap) {
		PayBean bean = new PayBean();
		bean.setChannel(channel);
		bean.setProduct(product);
		bean.setKey(key);
		RechargeWayPojo rechWayPojo = rechargeWayMapper.queryRechWayByPK(bean);
		if (null != rechWayPojo) {
			Map<String, String> otherChannelContent = new HashMap<String, String>();
			otherChannelContent.put("channel", channel);
			otherChannelContent.put("product", product);
			otherChannelContent.put("key", key);
			otherChannelContent.put("minlimit", rechWayPojo.getMinlimit());
			otherChannelContent.put("maxlimit", rechWayPojo.getMaxlimit());
			otherChannelContent.put("daylimit", rechWayPojo.getDaylimit());
			otherChannelContent.put("openflag", rechWayPojo.getOpenflag());
			otherChannelContent.put("bindIdCard", rechWayPojo.getBindIdcard());
			rechargeDataBaseAllMap.put(channel + "_" + product + "_" + key + "_database", otherChannelContent);
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey("rechargeDataBaseAll");
			cacheBean.setValue(JSONObject.toJSONString(rechargeDataBaseAllMap));
			cacheBean.setTime(Constants.TIME_DAY * 7);
			redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
			return otherChannelContent;
		}
		log.info("未找到单个渠道的信息内容:" + channel + "_" + product + "_" + key);
		return null;
	}

	private Map<String, Map<String, String>> getBankCardOrderRechargeMapFromCache(PayBean bean,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap) {
		Map<String, Map<String, String>> orderRechargeMap = orderRechargeAllMap
				.get("orderRechargeNewMap_" + bean.getBankCode() + bean.getCardtype());
		if (orderRechargeMap != null) {
			log.info("orderRechargeNewMap=====>" + orderRechargeMap.toString() + " 用户名:" + bean.getUid() + " key:"
					+ "orderRechargeNewMap_" + bean.getBankCode() + bean.getCardtype() + " fromMap");
			return orderRechargeMap;
		}
		orderRechargeMap = putBankCardOrderRechargeMapIntoCache(bean, orderRechargeAllMap);
		if (null == orderRechargeMap) {
			return null;
		}
		log.info("orderRechargeNewMap=====>" + orderRechargeMap.toString() + " 用户名:" + bean.getUid() + " key:"
				+ "orderRechargeNewMap_" + bean.getBankCode() + bean.getCardtype() + " fromDB");
		return orderRechargeMap;
	}

	// 将顺序与充值的映射关系放入缓存中
	private Map<String, Map<String, String>> putBankCardOrderRechargeMapIntoCache(PayBean bean,
			Map<String, Map<String, Map<String, String>>> orderRechargeAllMap) {
		Map<String, Map<String, String>> orderRechargeMap = new HashMap<String, Map<String, String>>();
		List<CardSupportChannelPojo> channelList = cardSupportChannelMapper.queryBankSupportChannel(bean.getBankCode(),
				bean.getCardtype());
		if (null != channelList && channelList.size() > 0) {
			int order = 0;
			for (CardSupportChannelPojo channel : channelList) {
				Map<String, String> bankCardChannel = new HashMap<String, String>();
				bankCardChannel.put("channel", channel.getChannel());
				bankCardChannel.put("product", channel.getProduct());
				bankCardChannel.put("key", channel.getKey());
				bankCardChannel.put("minlimit", channel.getMinlimit());
				bankCardChannel.put("maxlimit", channel.getMaxlimit());
				bankCardChannel.put("daylimit", channel.getDaylimit());
				bankCardChannel.put("openflag", channel.getOpenflag());
				bankCardChannel.put("bindIdCard", channel.getBindIdCard());
				orderRechargeMap.put(String.valueOf(order), bankCardChannel);
				order++;
			}
			orderRechargeAllMap.put("orderRechargeNewMap_" + bean.getBankCode() + bean.getCardtype(), orderRechargeMap);
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey("orderRechargeAll");
			cacheBean.setTime(Constants.TIME_DAY * 7);
			cacheBean.setValue(JSONObject.toJSONString(orderRechargeAllMap));
			return orderRechargeMap;
		}
		log.info("没有" + bean.getBankCode() + bean.getCardtype() + "的顺序与渠道映射");
		return null;
	}

	// 将渠道情况写入结果
	private void writeChannelIntoResult(JSONObject result, Map<String, String> channelContent) {
		Set<String> keySet = channelContent.keySet();
		for (String key : keySet) {
			String value = channelContent.get(key);
			result.put(key, value);
		}
	}

	// 添加银行卡信息
	private void appendCardInfo(SafeBean safeBean, Map<String, String> channelContent) {
		Map<String, String> bankCardLinkMap = baseService.getBankCardMap(channelContent.get("bankcode"));
		String cardNo = safeBean.getBankcard();
		String mobile = safeBean.getMobileno();
		if (!StringUtil.isEmpty(cardNo)) {
			cardNo = SecurityTool.iosencrypt(cardNo);
		}
		channelContent.put("cardno", cardNo);
		if (!StringUtil.isEmpty(mobile)) {
			mobile = SecurityTool.iosencrypt(mobile);
		} else {
			mobile = "";
		}
		channelContent.put("mobile", mobile);
		channelContent.put("linkimg", bankCardLinkMap.get("linkimg"));
	}

	// 从缓存中获取渠道信息
	private Map<String, String> getBankCardChannelContentFromCache(PayBean bean,
			Map<String, Map<String, String>> bankCardChannelMap) {
		Map<String, String> channelContentMap = bankCardChannelMap
				.get(bean.getBankCode() + bean.getCardtype() + "_channel");
		if (channelContentMap != null) {
			log.info("channelContent=====>" + channelContentMap.toString() + "用户名:" + bean.getUid() + " key:"
					+ bean.getBankCode() + bean.getCardtype() + "_channel fromMap");
			return channelContentMap;
		}
		channelContentMap = putBankCardChannelContentIntoCache(bean, bankCardChannelMap);
		if (null == channelContentMap) {
			return null;
		}
		log.info("channelContent=====>" + channelContentMap.toString() + "用户名:" + bean.getUid() + " key:"
				+ bean.getBankCode() + bean.getCardtype() + "_channel fromDB");
		return channelContentMap;
	}

	// 将channel的内容放入缓存
	private Map<String, String> putBankCardChannelContentIntoCache(PayBean bean,
			Map<String, Map<String, String>> bankCardChannelMap) {
		RouteCardPojo routeCardPojo = routeCardMapper.queryRouteCard(bean.getBankCode(), bean.getCardtype());
		if (null != routeCardPojo) {
			Map<String, String> channelContentMap = new HashMap<String, String>();
			channelContentMap.put("bankName", routeCardPojo.getBankName());
			channelContentMap.put("banStatus", routeCardPojo.getBanStatus());
			channelContentMap.put("banContent", routeCardPojo.getBanContent());
			channelContentMap.put("visible", routeCardPojo.getVisible());
			channelContentMap.put("openflag", routeCardPojo.getOpenflag());
			bankCardChannelMap.put(bean.getBankCode() + bean.getCardtype() + "_channel", channelContentMap);
			CacheBean cacheBean = new CacheBean();
			cacheBean.setKey("bankCardChannel");
			cacheBean.setValue(JSONObject.toJSONString(bankCardChannelMap));
			cacheBean.setTime(Constants.TIME_DAY * 7);
			redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
			return channelContentMap;
		}
		log.info("未找到该银行卡渠道信息:" + bean.getBankCode() + bean.getCardtype() + "_channel");
		return null;
	}

	// 写入json对象
	private JSONObject writeToJson(JXmlWrapper content) {
		JSONObject json = new JSONObject();
		Element element = content.getXmlRoot();
		@SuppressWarnings("unchecked")
		List<Attribute> attrList = element.getAttributes();
		for (Attribute attr : attrList) {
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			json.put(attrName, attrValue);
		}
		return json;
	}

	// 添加用户真实信息
	private void appendUserInfo(PayBean bean, RechRouteDto rechRouteDto) {
		boolean flag = baseService.getUserIdenInfo(bean);
		if (flag) {
			rechRouteDto.setRealName(bean.getRealName());
			String idCard = bean.getIdcard();
			String encryptIdCard = SecurityTool.iosencrypt(idCard);
			rechRouteDto.setIdcard(encryptIdCard);
		} else {
			rechRouteDto.setIdcard("");
			rechRouteDto.setRealName("");
		}
	}

	@Override
	public RechRouteDto singleCardRoute(PayBean bean) {
		log.info("银行卡单卡路由分配充值渠道,用户名:" + bean.getUid() + " 充值金额:" + bean.getAddmoney());
		// 从缓存中获取配置文件内容,如果没有则读取文件,并将文件内容放入缓存中
		JXmlWrapper config = JXmlWrapper.parse(new File(FileConstant.RECHARGE_ROUTE));
		Map<String, Integer> routeParamMap = new HashMap<String, Integer>();
		routeParamMap.put("maxLimit", 0);// 最大限制金额(可用来根据用户动态分配最大金额)
		RechRouteDto rechRouteDto = new RechRouteDto();
		appendUserInfo(bean, rechRouteDto);
		if (!baseService.getUserWhiteGrade(bean)) {
			bean.setWhitelistGrade(0);
			log.info("充值路由查询用户白名单等级失败,用户名:" + bean.getUid());
		}
		singleCardRechargeRoute(bean, rechRouteDto, config, routeParamMap);
		return rechRouteDto;
	}

	// 单卡银行卡路由
	private void singleCardRechargeRoute(PayBean bean, RechRouteDto rechRouteDto, JXmlWrapper config,
			Map<String, Integer> routeParamMap) {
		List<JXmlWrapper> rechargeList = config.getXmlNodeList("rechargeWay");
		for (JXmlWrapper rechargeWay : rechargeList) {
			String visible = rechargeWay.getStringValue("@visible");
			if ("N".equals(visible)) {
				continue;
			}
			String id = rechargeWay.getStringValue("@id");
			if ("1".equals(id)) {// 表示获取已绑定的银行卡信息
				JSONObject rechJson = writeToJson(rechargeWay);
				singleCardRechargeRouteBankCard(bean, rechJson, routeParamMap);
				rechRouteDto.getRechargeWay().add(rechJson);
			}
		}
	}

	//针对首次充值 根据渠道权重分配路由 不再走默认充值渠道
	@SuppressWarnings("unchecked")
	private Map<String, String> getChannelByWeight(PayBean bean, String cacheKey) {
		Map<String, String> lastRechMap = new HashMap<>();
		String key = "RechChannelWeightList_" + cacheKey;//充值渠道权重缓存key
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(key);
		try {
			List<Map<String, String>> weightList = new ArrayList<>();
			if (!"bankcard".equals(cacheKey)) {
                weightList = (List<Map<String, String>>) redisClient.getObject(cacheBean, List.class, log, SysCodeConstant.PAYCENTER);
            } else {
                Map<String, List<Map<String, String>>> bankcardMap = (Map<String, List<Map<String, String>>>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
                if(bankcardMap==null){
                	log.error("根据权重分配到渠道,银行卡权重缓存为空，用户:{}",bean.getUid());
                	return null;
				}
                weightList = bankcardMap.get(bean.getBankCode() + "_" + bean.getCardtype());
            }
            if(weightList==null){
				log.error("根据权重分配到渠道,权重缓存为空，用户:{}",bean.getUid());
				return null;
			}
			log.info("缓存key:{},内容:{}", key, weightList);
			int weightSumValue = 0;//总权重值
			int[] weightArr = new int[weightList.size()];//权重数组
			int i = 0, result = 0;
			for (Map<String, String> weightMap : weightList) {
                weightArr[i++] = Integer.valueOf(weightMap.get("weight"));//初始化权重数组
                weightSumValue += Integer.valueOf(weightMap.get("weight"));
            }
            if(weightSumValue==0){//权重值为0 走默认
				return null;
			}
			for (int m = 0; m < weightArr.length; m++) {
                int randomNum = new Random().nextInt(weightSumValue);
                if (randomNum < weightArr[m]) {
                    result = m;
                    break;
                } else {
                    weightSumValue -= weightArr[m];
                }
            }
			Map<String, String> selectChannelMap = weightList.get(result);//根据权重分配到的充值渠道
			String selectedChannel=selectChannelMap.get("channel");
			if(StringUtil.isEmpty(selectedChannel)){
				log.error("根据权重分配渠道异常,用户:{}",bean.getUid());
				return null;
			}
			lastRechMap.put("bankid", "");
			lastRechMap.put("flag", "1");
			lastRechMap.put("channel",selectedChannel);
			log.info("用户:{},充值金额:{},没有上次充值成功信息,根据权重分配到的渠道,channel:{}", bean.getUid(),bean.getAddmoney(),selectChannelMap.get("channel"));
			return lastRechMap;
		} catch (Exception e) {
			log.error("根据权重分配到的渠道出现异常,用户:{}",bean.getUid(),e);
		}
		return null;
	}

	// 银行卡单卡路由
	private void singleCardRechargeRouteBankCard(PayBean bean, JSONObject rechJson,
			Map<String, Integer> routeParamMap) {
		JSONObject cardJson = new JSONObject();
		String bankCode = bean.getBankCode();
		String cardType = bean.getCardtype() + "";
		if (StringUtil.isEmpty(bankCode)) {
			log.info("单卡路由的银行卡号和银行编码不能为空, 银行编码:" + bankCode);
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_PARAM_ERROR));
			bean.setBusiErrDesc("银行卡号或银行编码不能为空");
			return;
		}
		Map<String, Map<String, String>> bankCardChannelMap = getBankCardChannelMapFromCache();
		Map<String, String> channelContent = getBankCardChannelContentFromCache(bean, bankCardChannelMap);
		if (null == channelContent) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_NO_USEFUL_CHANNEL));
			bean.setBusiErrDesc("暂无该银行卡可用渠道");
			log.info("没有" + bankCode + cardType + "该渠道的数据库记录");
			return;
		}
		String visible = channelContent.get("visible");
		if ("N".equals(visible)) {
			return;
		}
		String banStatus = channelContent.get("banStatus");
		if ("Y".equals(banStatus)) {
			writeChannelIntoResult(cardJson, channelContent);
			return;
		}
		JXmlWrapper config = JXmlWrapper.parse(new File(FileConstant.RECHARGE_ROUTE));
		Map<String, Map<String, Map<String, String>>> orderRechargeAllMap = getOrderRechargeAllMap();
		Map<String, Map<String, String>> channelOrderAllMap = getChannelOrderAllMap();
		Map<String, String> rechargeConfigAllMap = getRechargeConfigAllMap();
		Map<String, Map<String, String>> rechargeDataBaseAllMap = getRechargeDataBaseAllMap();
		Map<String, Map<String, String>> userRechRecordMap = getUserRechRecordMap(bean.getUid());
		// 获取最后一次的充值状态
		Map<String, String> lastRechargeMap = userRechRecordMap.get(bean.getUid() + "_" + bean.getBankCode() + bean.getCardtype());
		if (null == lastRechargeMap) {
			log.info(bean.getUid() + "没有上一次的充值记录 bankCode:" + bean.getBankCode() + " cardType:" + bean.getCardtype());
			lastRechargeMap =getChannelByWeight(bean,"bankcard");
			Map<String, Integer> rechargeWayResult;
			if(null==lastRechargeMap){//根据权重获取渠道异常 按默认顺序进行排序
                 rechargeWayResult = getBankCardRechargeWayByDefault(bean, cardJson, channelContent,
						routeParamMap, orderRechargeAllMap, rechargeConfigAllMap, rechargeDataBaseAllMap, config);// 按默认顺序进行排序
			}else{
				rechargeWayResult = getBankCardRechargeWay(bean, cardJson, channelContent,
						lastRechargeMap, routeParamMap, orderRechargeAllMap, channelOrderAllMap, rechargeConfigAllMap,
						rechargeDataBaseAllMap, config);
			}
			addRechargeWayByResult(channelContent, cardJson, rechargeWayResult, routeParamMap);
		} else {
			log.info(bean.getUid() + "的上一次充值记录的bankid为" + lastRechargeMap.get("bankid") + " 充值渠道:"
					+ lastRechargeMap.get("channel") + " bankCode:" + bean.getBankCode() + " cardType:"
					+ bean.getCardtype());
			Map<String, Integer> rechargeWayResult = getBankCardRechargeWay(bean, cardJson, channelContent,
					lastRechargeMap, routeParamMap, orderRechargeAllMap, channelOrderAllMap, rechargeConfigAllMap,
					rechargeDataBaseAllMap, config);// 按上次充值渠道进行排序筛选
			addRechargeWayByResult(channelContent, cardJson, rechargeWayResult, routeParamMap);
		}
		JSONArray cardArr = new JSONArray();
		cardArr.add(cardJson);
		rechJson.put("card", cardArr);
	}

}
