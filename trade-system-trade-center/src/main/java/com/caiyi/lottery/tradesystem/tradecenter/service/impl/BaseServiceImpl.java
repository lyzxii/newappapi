package com.caiyi.lottery.tradesystem.tradecenter.service.impl;


import bean.TokenBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SourceMap;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.PeriodMapper;
import com.caiyi.lottery.tradesystem.tradecenter.service.BaseService;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import constant.UserConstants;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.UserPojo;
import redpacket.bean.RedPacketBean;
import trade.bean.TradeBean;
import trade.constants.TradeConstants;
import trade.util.FilterUtil;

import java.io.File;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class BaseServiceImpl implements BaseService {
	
	@Autowired
	RedisClient redisClient;
	@Autowired
	UserBasicInfoWrapper userBasicInfoWrapper;
	@Autowired
	RedPacketCenterInterface redPacketCenterInterface;
	@Autowired
	PeriodMapper periodMappper;

	//检测充值购买开关
	@Override
	public boolean checkBanActivity(TradeBean bean) {
		JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.BAN_ACTIVITY));
		List<JXmlWrapper> banNodeList = xml.getXmlNodeList("ban-activity");
		for(JXmlWrapper banNode : banNodeList){
			String openFlag =  banNode.getXmlNode("business-rules").getXmlNode("open").getStringValue("@flag");
			if("1".equals(openFlag)){
				if(ParseGeneralRulesUtil.parseGeneralRules(banNode.getXmlNode("general-rules"), bean)){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
					bean.setBusiErrDesc("系统升级中~暂停销售~");
					return false;
				}
			}
		}
		return true;
	}

	//充值前检测
	@Override
	public boolean checkBeforeBuy(TradeBean bean) {
		log.info("投注前检测开售情况,投注来源和白名单等级,nickid=" + bean.getUid() + ",source=" + bean.getSource() + ",gid=" + bean.getGid() + ",zflag=" + bean.getZflag());
		if ((bean.getSource() == 1400 || bean.getSource() == 1401)
				&& BaseUtil.isNewApp(bean.getAppversion(), "android9188buy", "android")) {
			log.info("用户客户端版本小于最低可投注版本不能购彩,nickid=" + bean.getUid() + ",appversion=" + bean.getAppversion()
					+ ",source=" + bean.getSource());
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
			bean.setBusiErrDesc("系统升级中~暂停销售~");
			return false;
		}
		if ((bean.getSource() == 1436 || bean.getSource() == 1437)
				&& BaseUtil.isNewApp(bean.getAppversion(), "android1436buy", "android")) {
			log.info("用户客户端版本小于最低可投注版本不能购彩,nickid=" + bean.getUid() + ",appversion=" + bean.getAppversion()
					+ ",source=" + bean.getSource());
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
			bean.setBusiErrDesc("系统升级中~暂停销售~");
			return false;
		}
		checkUserWhiteGrade(bean);
		int grade = bean.getWhitelistGrade();
		if (1 == grade || 2 == grade || 3 == grade || 4 == grade || 5 == grade) {
			grade = (1 == grade) ? 1 : 2;
		} else if (grade != 100) {
			grade = 0;
		}
		if(grade == 100){
			return true;
		}
		JXmlWrapper tsxx = JXmlWrapper.parse(new File(FileConstant.GAME_CONFIG));
		List<JXmlWrapper> rows = tsxx.getXmlNodeList("row");
		int isale = -1;
		String gid = null;
		for (JXmlWrapper row : rows) {
			gid = row.getStringValue("@gid");
			if (bean.getGid().equals(gid)) {
				isale = row.getIntValue("@isale");
				break;
			}
		}
		if (isale == 0) {
			// 彩种完全停售,不能投注
			log.info("彩种完全停售不能购彩,nickid=" + bean.getUid() + ",gid=" + bean.getGid());
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
			bean.setBusiErrDesc("系统升级中~暂停销售~");
			return false;
		} else if (isale == 1) {
			// 彩种完全开售,可以投注
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("可以投注");
		} else if (isale == 2) {
			// 彩种只对白名单用户开售,进一步检测白名单等级,投注来源和投注客户端版本
			// TODO 主站,触屏和WP用户停售
			if (bean.getSource() < 1000 || bean.getSource() >= 5000 || SourceMap.noBuySource.containsKey(bean.getSource())) {
				log.info("主站,触屏用户停售不能购彩,nickid=" + bean.getUid() + ",source=" + bean.getSource());
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
				bean.setBusiErrDesc("系统升级中~暂停销售~");
				return false;
			} else {
				boolean isNew = false;
				// 可投注的最低客户端版本号还需要重新确认
				if (UserSourceMapUtil.isAndriodLotteryUser(bean)) {
					isNew = BaseUtil.isNewApp(bean.getAppversion(), "o2o9188buy", "android");
				} else if (UserSourceMapUtil.isWPUser(bean)) {
					isNew =  BaseUtil.isNewApp(bean.getAppversion(), "o2o9188buy", "wp");
				} else if (UserSourceMapUtil.isIOSLotteryUser(bean)) {
					isNew =  BaseUtil.isNewApp(bean.getAppversion(), "o2o9188buy", "ios");
				} else if (UserSourceMapUtil.isTouchUser(bean)) {
					isNew =  BaseUtil.isNewApp(bean.getAppversion(), "o2o9188buy", "touch");
					isNew = true;
				} else {
					log.info("非法source值不能购彩,nickid=" + bean.getUid() + ",source=" + bean.getSource());
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
					bean.setBusiErrDesc("系统升级中~暂停销售~");
					return false;
				}
				// 用户客户端版本小于最低可投注版本时不能投注
				if (!isNew) {
					log.info("用户客户端版本小于最低可投注版本不能购彩,nickid=" + bean.getUid() + ",appversion=" + bean.getAppversion());
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
					bean.setBusiErrDesc("系统升级中~暂停销售~");
					return false;
				}
				if (grade == 2) {
					bean.setBusiErrDesc("可以投注");
				} else {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
					bean.setBusiErrDesc("系统升级中~暂停销售~");
					log.info("白名单等级不足不能购彩,nickid=" + bean.getUid() + ",grade=" + grade);
					return false;
				}
			}
		} else {
			// 彩种开售状态未知,不能投注
			log.info("彩种开售状态未知,nickid=" + bean.getUid() + ",isale=" + isale);
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_BAN_ACTIVITY));
			bean.setBusiErrDesc("系统升级中~暂停销售~");
			return false;
		}
		
		return true;
	}

	//获取用户白名单等级
	@Override
	public boolean getUserWhiteGrade(TradeBean bean) {
		String whiteGrade = "0";
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(bean.getAppid());
		TokenBean tokenBean = (TokenBean) redisClient.getObject(cacheBean, TokenBean.class, log, SysCodeConstant.TRADECENTER);
		if(tokenBean!=null){
			String paramJson = tokenBean.getParamJson();
			if(!StringUtil.isEmpty(paramJson)){
				JSONObject json = JSONObject.parseObject(paramJson);
				whiteGrade = json.getString(UserConstants.OPENUSER);
				if(!StringUtil.isEmpty(whiteGrade)){
					bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
					return true;
				}
			}
		}
		whiteGrade = userBasicInfoWrapper.queryUserWhiteGrade(bean, log, SysCodeConstant.TRADECENTER);
		if(0!=bean.getBusiErrCode()||StringUtil.isEmpty(whiteGrade)){
			return false;
		}
		bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
		return true;
	}

	@Override
	public boolean checkUserRedpacket(TradeBean bean) {
		if(StringUtil.isEmpty(bean.getCupacketid())){
			return true;
		}
		
		UserPojo user = userBasicInfoWrapper.queryUserInfo(bean, log, SysCodeConstant.TRADECENTER);
		if(null == user){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_NOT_FIND_USER));
			bean.setBusiErrDesc("未查询到用户信息");
			log.info("未查询到用户信息,用户名:"+bean.getUid()+" 红包id:"+bean.getCupacketid());
			return false;
		}
		
		RedPacketBean redPacketBean = new RedPacketBean();
		redPacketBean.setUid(bean.getUid());
		redPacketBean.setCupacketid(bean.getCupacketid());
		redPacketBean.setTrade_gameid(bean.getGid());// 投注彩种
		int tradeMoney = bean.getBnum() * 1;// 合买
		if (bean.getTnum() == 1) {// 自购
			tradeMoney = bean.getMoney();
		}
		redPacketBean.setTrade_imoney(String.valueOf(tradeMoney));// 方案总金额(认购金额=认购份数*1)
		redPacketBean.setTrade_redPacket_money(String.valueOf(bean.getRedpacket_money()));// 所使用红包金额
		redPacketBean.setTrade_agent(user.getAgentid());
		redPacketBean.setTrade_isource(String.valueOf(bean.getSource()));
		log.info("UserRedPacket:彩种[" + redPacketBean.getTrade_gameid() + "]，认购金额["
				+ redPacketBean.getTrade_imoney() + "]，" + "使用红包金额[" + bean.getRedpacket_money()
				+ "] ，用户红包关联id[" + redPacketBean.getCupacketid() + "]，过期时间[" + redPacketBean.getCdeaddate()
				+ "]");
		BaseReq<RedPacketBean> req = new BaseReq<>(redPacketBean, SysCodeConstant.TRADECENTER);
		BaseResp<BaseBean> resp = redPacketCenterInterface.checkTradeRedpacket(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			return true;
		}
		bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
		bean.setBusiErrDesc(resp.getDesc());
		log.info("用户当前的红包无法使用,用户名:"+bean.getUid()+" redpacketId:"+bean.getCupacketid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
		return false;
	}

	//获取用户代理商id
	@Override
	public boolean getUserAgentId(TradeBean bean) {
		UserPojo user = userBasicInfoWrapper.queryUserInfo(bean, log, SysCodeConstant.TRADECENTER);
		if(null == user){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_NOT_FIND_USER));
			bean.setBusiErrDesc("未查询到用户信息");
			log.info("未查询到用户信息,用户名:"+bean.getUid()+" 红包id:"+bean.getCupacketid());
			return false;
		}else{
			return true;
		}
	}

	@Override
	public String setRequestUrl(TradeBean bean, String session1, String session2, String requestUrl) throws Exception {
		String message = setRequestMessage(bean);
//		JSONObject json = getExts(bean);
//		message = message + "&session1=" + session1 + "&session2=" + session2 + json.getString("exts");
		message = message + "&session1=" + session1 + "&session2=" + session2;
		byte[] src = message.getBytes();
		byte[] zsrc = FilterUtil.compressBytes(src);

		String outmessage = new String(GeneralBase64Utils.encode(zsrc));
		String outcheckor = MD5Util.compute(outmessage + TradeConstants.MD5KEY).toUpperCase();
		message = URLEncoder.encode(outmessage, "utf-8");
		String requestHeader = getRequestHeader(requestUrl);
//		String url = requestHeader + json.getString("url") + "?checkor=" + outcheckor + "&message=" + message + "&rd=" + Math.random();
		return requestHeader + "/trade/decode_bet_info.api?checkor=" + outcheckor + "&message=" + message + "&rd=" + Math.random();
	}

	@Override
	public String setJjyhRequestUrl(TradeBean bean, String sessionId1, String sessionId2, String requestUrl) throws Exception {
		String message = setJjyhRequestMessage(bean);
		String bdMoney = String.valueOf(bean.getBdMoney());
		String smoney = String.valueOf(bean.getBalance());
		String zs = String.valueOf(bean.getBnum());
		String bs = String.valueOf(bean.getMuli());
		message = message + "&session1=" + sessionId1 + "&session2=" + sessionId2 + "&bdMoney=" + bdMoney + "&smoney="
				+ smoney + "&zs=" + zs + "&bs=" + bs;

		byte[] src = message.getBytes();
		byte[] zsrc = FilterUtil.compressBytes(src);
		String outmessage = new String(GeneralBase64Utils.encode(zsrc));
		String outcheckor = MD5Util.compute(outmessage + TradeConstants.MD5KEY).toUpperCase();

		message = URLEncoder.encode(outmessage, "utf-8");
		String requestHeader = getRequestHeader(requestUrl);
		String url = requestHeader + "/trade/decode_jjyh_bet_info.api?checkor=" + outcheckor + "&message=" + message + "&rd=" + Math.random();
		log.info("生成的URL-------:" + url);
		return url;
	}

	private String setJjyhRequestMessage(TradeBean bean) {
		String message = getJjyhRequestMessageByCType(bean);
		return message;
	}

	/**
	 * 生成请求字符串.
	 *
	 * @param bean
	 * @return
	 */
	public String setRequestMessage(TradeBean bean) {
		String message = getRequestMessageByCType(bean);
		return message;
	}

	public static String getRequestHeader(String url) {
		if (url.indexOf("http:") != -1) {
			String[] str = url.split("//");
			return str[0] + "//" + str[1].split("/")[0];
		} else {
			return url.split("/")[0];
		}
	}

	private String getJjyhRequestMessageByCType(TradeBean bean) {
		StringBuilder builder = new StringBuilder();
		builder.append("allnum=" + bean.getTnum());
		builder.append("&amoney=" + bean.getMoney());
		builder.append("&baodinum=" + bean.getPnum());
		builder.append("&beishu=" + bean.getMulitys());
		builder.append("&buynum=" + bean.getBnum());
		builder.append("&codes=" + bean.getCodes());
		builder.append("&comeFrom=" + bean.getComeFrom());
		builder.append("&expect=" + bean.getExpect());
		builder.append("&initems=" + bean.getInitems());
		builder.append("&ishm=" + bean.getType());
		builder.append("&isshow=" + bean.getIsshow());
		builder.append("&items=" + bean.getItems());
		builder.append("&lotid=" + bean.getLotid());
		builder.append("&newcodes=" + bean.getNewcodes());
		builder.append("&source=" + bean.getSource());
		builder.append("&tcbili=" + bean.getTcbili());
		builder.append("&title=" + bean.getTitle());
		builder.append("&content=" + bean.getContent());
		builder.append("&upay=" + bean.getUpay());
		builder.append("&totalMoney=" + bean.getBalance());
		builder.append("&extendtype=" + bean.getExtendtype());
		builder.append("&yhfs=" + bean.getYhfs());

		String appScheme = "no";
		if (!StringUtil.isEmpty(bean.getAppScheme())) {
			appScheme = bean.getAppScheme();
		}
		builder.append("&appScheme=" + appScheme);
		builder.append("&cupacketid=" + bean.getCupacketid()); //用户红包id
		builder.append("&redpacket_money=" + bean.getRedpacket_money()); //使用红包金额
		builder.append("&cType=" + bean.getcType());
		builder.append("&startTime=" + new Date().getTime()); //时间戳
		builder.append("&func=" + bean.getFunc());

		builder.append("&logintype=" + bean.getLogintype());    //以下几个字段是token登录时使用
		builder.append("&appversion=" + bean.getAppversion());
		builder.append("&mtype=" + bean.getMtype());
		builder.append("&accesstoken=" + bean.getAccesstoken());
		builder.append("&appid=" + bean.getAppid());
		builder.append("&gid=" + bean.getGid());
		String message = builder.toString();
		return message;
	}

	private String getRequestMessageByCType(TradeBean bean) {
		StringBuilder builder = new StringBuilder();
		if (bean.getcType().equals("FQHM") || bean.getcType().equals("ZG") || "GM".equals(bean.getcType())) {
			builder.append("gid=" + bean.getGid());
			builder.append("&bnum=" + bean.getBnum());
			builder.append("&comeFrom=" + bean.getComeFrom());
			builder.append("&desc=" + bean.getDesc());
			builder.append("&endTime=" + bean.getEndTime());
			builder.append("&fflag=" + bean.getFflag());
			builder.append("&bdMoney=" + bean.getBdMoney());
			builder.append("&muli=" + bean.getMuli());
			builder.append("&name=" + bean.getName());
			builder.append("&oflag=" + bean.getOflag());
			builder.append("&paly=" + bean.getPlay());
			builder.append("&pnum=" + bean.getPnum());
			builder.append("&tnum=" + bean.getTnum());
			builder.append("&type=" + bean.getType());
			builder.append("&upay=" + bean.getUpay());
			builder.append("&wrate=" + bean.getWrate());
			builder.append("&zs=" + bean.getBnum());
			builder.append("&bs=" + bean.getMuli());
			builder.append("&comboid=" + bean.getComboid());
			builder.append("&hid=" + bean.getHid());
			builder.append("&imoneyrange=" + bean.getImoneyrange());
			builder.append("&iminrange=" + bean.getIminrange());
		} else if (bean.getcType().equals("RG")) {
			builder.append("bnum=" + bean.getBnum());
			builder.append("&hid=" + bean.getHid());
			builder.append("&gid=" + bean.getGid());
			builder.append("&wrate=" + bean.getWrate());
			builder.append("&bdMoney=" + bean.getBdMoney());
			builder.append("&views=" + bean.getViews());
			builder.append("&smoney=" + bean.getBalance());
		} else if (bean.getcType().equals("ZH")) {
			builder.append("gid=" + bean.getGid());
			builder.append("&mulitys=" + bean.getMulitys());
			builder.append("&ischase=" + bean.getIschase());
			builder.append("&zflag=" + bean.getZflag());
			builder.append("&upay=" + bean.getUpay());
			builder.append("&zs=" + bean.getBnum());
			builder.append("&bs=" + bean.getMuli());
			builder.append("&find=" + bean.getFind());
		} else if (bean.getcType().equals("JJYH")) {
			builder.append("&comboid=" + bean.getComboid());
		}

		String appScheme = "no";
		if (!StringUtil.isEmpty(bean.getAppScheme())) {
			appScheme = bean.getAppScheme();
		}
		builder.append("&appScheme=" + appScheme);
		builder.append("&cupacketid=" + bean.getCupacketid()); // 用户红包id
		builder.append("&redpacket_money=" + bean.getRedpacket_money()); // 使用红包金额
		builder.append("&money=" + bean.getMoney());
		builder.append("&totalMoney=" + bean.getBalance());
		builder.append("&cType=" + bean.getcType());
		builder.append("&startTime=" + new Date().getTime()); // 时间戳
		builder.append("&func=" + bean.getFunc());
		builder.append("&extendtype=" + bean.getExtendtype());
		builder.append("&source=" + bean.getSource());

		builder.append("&logintype=" + bean.getLogintype());    //以下几个字段是token登录时使用
		builder.append("&appversion=" + bean.getAppversion());
		builder.append("&mtype=" + bean.getMtype());
		builder.append("&accesstoken=" + bean.getAccesstoken());
		builder.append("&appid=" + bean.getAppid());

		String message = builder.toString();

		return message;
	}

	private JSONObject getExts(TradeBean bean) {
		String bdMoney = String.valueOf(bean.getBdMoney());
		String smoney = String.valueOf(bean.getBalance());
		String zs = String.valueOf(bean.getBnum());
		String bs = String.valueOf(bean.getMuli());
		String exts = "";
		String url = "";
		JSONObject json = new JSONObject();
		if (StringUtil.isEmpty(bdMoney) && StringUtil.isEmpty(smoney) && StringUtil.isEmpty(zs) && StringUtil.isEmpty(bs))
			url = "/trade/decode_bet_info.api";
		else {
			exts = "&bdMoney=" + bdMoney + "&smoney=" + smoney + "&zs=" + zs + "&bs=" + bs;
			url = "/trade/decode_bet_info.api";
		}
		json.put("url", url);
		json.put("exts", exts);
		return json;
	}

	public boolean checkUserWhiteGrade(TradeBean bean) {
		String whiteGrade = "0";
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(bean.getAppid());
		TokenBean tokenBean = (TokenBean) redisClient.getObject(cacheBean, TokenBean.class, log, SysCodeConstant.TRADECENTER);
		if(tokenBean!=null){
			String paramJson = tokenBean.getParamJson();
			if(!StringUtil.isEmpty(paramJson)){
				JSONObject json = JSONObject.parseObject(paramJson);
				whiteGrade = json.getString(UserConstants.OPENUSER);
				if(!StringUtil.isEmpty(whiteGrade)){
					bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
				}
			}
		}
		if(bean.getWhitelistGrade()<2) {
			whiteGrade = userBasicInfoWrapper.queryUserWhiteGrade(bean, log, SysCodeConstant.TRADECENTER);
			if (0 != bean.getBusiErrCode() || StringUtil.isEmpty(whiteGrade)) {
				return false;
			}
			bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
			if(bean.getWhitelistGrade()>=2){//删除白名单缓存
				CacheBean cacheBean1 = new CacheBean();
				cacheBean1.setKey(bean.getAppid());
				redisClient.delete(cacheBean1, log, SysCodeConstant.TRADECENTER);
			}
		}
		return true;
	}
}
