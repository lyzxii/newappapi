package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import bean.SafeBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.SpringBeanFactoryUtils;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.*;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.*;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.AlipayWrapper;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.BankCardWrapper;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.TenpayWrapper;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.WeiXinWrapper;
import com.caiyi.lottery.tradesystem.paycenter.service.BaseService;
import com.caiyi.lottery.tradesystem.paycenter.service.PayService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.clientwrapper.SafeCenterWrapper;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pay.bean.PayBean;
import pay.constant.RechargeTypeConstant;
import pay.dto.BindCardInfoDto;
import pay.dto.RechDto;
import pay.dto.UserRechCardDto;
import pay.pojo.RechCardChannelPojo;
import pay.pojo.RechCardPojo;
import pay.pojo.RechargeWayPojo;
import pojo.UserPojo;

import java.io.File;
import java.util.*;

@Slf4j
@Service
public class PayServiceImpl implements PayService{
	
	@Autowired
	UserBasicInfoWrapper userBasicInfoWrapper;
	@Autowired
	CpUserPayMapper cpUserPayMapper;
	@Autowired
	RechService rechService;
	@Autowired
	RedisClient redisClient;
	@Autowired
	RechargeWayMapper rechargeWayMapper;
	@Autowired
	BaseService baseService;
	@Autowired
	SafeCenterWrapper safeCenterWrapper;
	@Autowired
	RechCardMapper rechCardMapper;
    @Autowired
	RechCardChannelMapper rechCardChannelMapper;
    @Autowired
    RechCard_RechCardChannelMapper rechCard_rechCarChannelMapper;
    @Autowired
    UserPayMapper userPayMapper;
	
	public boolean checkRechargeChannel(PayBean bean) {
		Map<String, Map<String, String>> rechargeDataBaseAllMap = getRechargeDataBaseAllMap();
		Map<String, String> channelContent = rechargeDataBaseAllMap.get(bean.getChannel()+"_"+bean.getProduct()+"_"+bean.getKey()+"_database");
		if(channelContent == null){
			RechargeWayPojo rechWay = rechargeWayMapper.queryRechWayByPK(bean);
			if(null == rechWay){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CONFIG_ERROR));
				bean.setBusiErrDesc("该渠道未配置正确");
				log.info("该渠道未配置正确,channel:"+bean.getChannel()+" product:"+bean.getProduct()+
						" key:"+bean.getKey()+" channelContentCache:"+channelContent);
				return false;
			}else{
				channelContent = new HashMap<>();
				channelContent.put("channel", rechWay.getChannel());
				channelContent.put("product", rechWay.getProduct());
				channelContent.put("key", rechWay.getKey());
				channelContent.put("minlimit", rechWay.getMinlimit());
				channelContent.put("maxlimit", rechWay.getMaxlimit());
				channelContent.put("daylimit", rechWay.getDaylimit());
				channelContent.put("openflag", rechWay.getOpenflag());
				channelContent.put("bindIdCard", rechWay.getBindIdcard());
				rechargeDataBaseAllMap.put(bean.getChannel()+"_"+bean.getProduct()+"_"+bean.getKey()+"_database", channelContent);
				CacheBean cacheBean = new CacheBean();
				cacheBean.setKey("rechargeDataBaseAll");
				cacheBean.setValue(JSONObject.toJSONString(rechargeDataBaseAllMap));
				cacheBean.setTime(Constants.TIME_DAY*7);
				redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
			}
		}
		String openFlag = channelContent.get("openflag");
		String bindIdCard = channelContent.get("bindIdCard");
		if("0".equals(openFlag)){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CHANNEL_CLOSE));
			bean.setBusiErrDesc("该渠道已关闭");
			log.info("该渠道已关闭,channel:"+bean.getChannel()+" product:"+bean.getProduct()+" key:"+bean.getKey());
			return false;
		}
		if("2".equals(bindIdCard)){
			if(!baseService.getUserIdenInfo(bean)){
				return false;
			}
			if(StringUtil.isEmpty(bean.getIdcard())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_NEED_IDCARD));
				bean.setBusiErrDesc("为保证账户安全，充值前，请先绑定身份信息");
				log.info("用户未绑定身份证,用户名:"+bean.getUid()+" bankid:"+bean.getBankid());
				return false;
			}
		}
		return true;
	}

	//创建充值订单
	public boolean createApplyid(PayBean bean) {
		String rid = UUID.randomUUID().toString();
		String applydate = DateTimeUtil.formatDate(new Date(), "yyyyMMddHHmmss");
		int ii = (bean.getUid() + applydate + bean.getAddmoney() + rid).hashCode();
		String applyid = Integer.toHexString(ii).toUpperCase();
		applyid = applydate.substring(2, 8) + StringUtil.LeftPad(applyid, "F", 8);
		bean.setApplyid(applyid);
		bean.setApplydate(applydate);
		bean.setComeFrom("");
		//查询用户基本信息
		UserPojo pojo = userBasicInfoWrapper.queryUserInfo(bean, log, SysCodeConstant.PAYCENTER);
		if(0!=bean.getBusiErrCode()||null == pojo){
			return false;
		}
		bean.setUserid(pojo.getCuserId());
		getRechargeInfo(bean);
		if(bean.getBusiErrCode()!=0){
			log.info("充值未找到对应的充值渠道信息,用户名:"+bean.getUid()+" bankid:"+bean.getBankid()+" rechargeType:"+bean.getRechargeType());
			return false;
		}
		checkPayInfo(bean);
		if(bean.getBusiErrCode()!=0){
			log.info("充值信息检测错误,用户名:"+bean.getUid()+" desc:"+bean.getBusiErrDesc()+" bankid:"+bean.getBankid());
			return false;
		}
		cpUserPayMapper.createPayOrder(bean);
		if(bean.getBusiErrCode()!=0){
			log.info("创建用户订单失败,用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid()+" 密码:"+bean.getPwd()+" bankid:"+bean.getBankid()+
					" minConsume:"+bean.getMinConsume()+" handmoney:"+bean.getHandmoney()+" remark:"+bean.getRemark()+
					" code:"+bean.getBusiErrCode()+" desc:"+bean.getBusiErrDesc());
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_CREATE_ORDER_FAIL));
			bean.setBusiErrDesc("创建订单失败");
			return false;
		}
		userPayMapper.updateUserMerchantId(bean);
		return true;
	}

	//检查支付信息
	public void checkPayInfo(PayBean bean) {
		if (bean.getAddmoney() + bean.getHandmoney() < 1) {// 检查参数充值金额
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_AMONEY_AMOUNT_ERROR));
			bean.setBusiErrDesc("充值金额不能少于1元");
		}

		if (bean.getHandmoney() < 0 || bean.getHandmoney() >= bean.getAddmoney()) {// 检查手续费用
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_HANDMONEY_ERROR));
			bean.setBusiErrDesc("手续费用不能够为负数且不可以大于充值金额");
		}

		if (StringUtil.isEmpty(bean.getApplyid())) {// 检查订单号
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_APPLYID_ERROR));
			bean.setBusiErrDesc("订单号不能为空");
		}

		if (StringUtil.isEmpty(bean.getApplyid())) {// 检查申请时间
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_APPLYDATE_ERROR));
			bean.setBusiErrDesc("申请时间不能为空");
		}
	}

	//从配置文件获取充值信息
	public void getRechargeInfo(PayBean bean) {
		JXmlWrapper rechargeInfo = JXmlWrapper.parse(new File(FileConstant.RECHARGE_INFO));
		if(StringUtil.isEmpty(bean.getRechargeType())){
			for(String rechargeType : RechargeTypeConstant.RECHARGETYPE_ARR){
				bean.setRechargeType(rechargeType);
				JXmlWrapper rechTypeNode = rechargeInfo.getXmlNode(rechargeType);
				injectRechargeInfo(bean,rechTypeNode);
				if(bean.getBusiErrCode()==0){
					return;
				}
			}
		}else{
			JXmlWrapper rechTypeNode = rechargeInfo.getXmlNode(bean.getRechargeType());
			injectRechargeInfo(bean, rechTypeNode);
		}
	}

	//注入该充值的基础内容
	public void injectRechargeInfo(PayBean bean, JXmlWrapper rechTypeNode) {
		List<JXmlWrapper> rechargeInfoList = rechTypeNode.getXmlNodeList("rows");
		for(JXmlWrapper rechargeInfo : rechargeInfoList){
			String bankids = rechargeInfo.getStringValue("@bankid");
			String[] bankidArr = bankids.split(",");
			for(String xmlBankid : bankidArr){
				if(xmlBankid.trim().equals(bean.getBankid())){
					String channel = rechargeInfo.getStringValue("@channel");
					if(StringUtil.isEmpty(bean.getChannel())){//如果为空则设置配置文件中的channel
						bean.setChannel(channel);
					}
					if(!channel.equals(bean.getChannel())){
						bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CHANNEL_MATCH_ERROR));
						bean.setBusiErrDesc("渠道配置不匹配");
						log.info("客户端上传的渠道与配置文件渠道配置不匹配,用户名:"+bean.getUid()+" bankid:"+bean.getBankid()+" appChannel:"+bean.getChannel()+
								" configChannel:"+channel);
						continue;
					}
					//计算手续费
					double handrate = rechargeInfo.getDoubleValue("@handrate");
					double handmoney = Math.round(bean.getAddmoney() * handrate) / 100.0;
					double newaddmoney = bean.getAddmoney() - handmoney;
					bean.setAddmoney(newaddmoney);
					bean.setHandmoney(handmoney);
					//计算最低消费金额
					double consumerate = rechargeInfo.getDoubleValue("@consumerate");
					bean.setMinConsume(consumerate*bean.getAddmoney());
					String className = rechargeInfo.getStringValue("@className");
					bean.setClassName(className);
					String product = rechargeInfo.getStringValue("@product");
					if(!StringUtil.isEmpty(product)){
						bean.setProduct(product);
					}
					//是否轮询
					String cycle = rechargeInfo.getStringValue("@cycle");
					List<JXmlWrapper> rechInfoDetailList = rechargeInfo.getXmlNodeList("row");
					if("1".equals(cycle)){//轮询
						CacheBean cacheBean = new CacheBean();
						cacheBean.setKey(bean.getChannel()+"_"+bean.getProduct()+"_lastMerchant");
						String lastMerchantId = redisClient.getString(cacheBean, log, SysCodeConstant.PAYCENTER);
						if(StringUtil.isEmpty(lastMerchantId)){//如果为空,取第一个
							JXmlWrapper rechInfoDetail = rechInfoDetailList.get(0);
							injectRechDetailInfo(bean, rechInfoDetail);
						}else{
							for(int i = 0; i < rechInfoDetailList.size(); i++){
								JXmlWrapper rechInfoDetail = rechInfoDetailList.get(i);
								String merchantId = rechInfoDetail.getStringValue("@mch_id");
								if(lastMerchantId.equals(merchantId)){//如果商户号一样取下一个商户号
									if(i == rechInfoDetailList.size() - 1){//如果该商户号是最后一个,取第一个
										rechInfoDetail = rechInfoDetailList.get(0);
										injectRechDetailInfo(bean, rechInfoDetail);
										break;
									} else {//如果该商户号非最后一个,取下一个
										rechInfoDetail = rechInfoDetailList.get(i+1);
										injectRechDetailInfo(bean, rechInfoDetail);
										break;
									}
								} 
								if(i == rechInfoDetailList.size() - 1){//没有一样的商户号取第一个
									rechInfoDetail = rechInfoDetailList.get(0);
									injectRechDetailInfo(bean, rechInfoDetail);
								}
							}
						}
					}else{//不轮询
						JXmlWrapper rechInfoDetail = rechInfoDetailList.get(0);
						injectRechDetailInfo(bean, rechInfoDetail);
					}
					CacheBean cacheBean = new CacheBean();
					cacheBean.setKey(bean.getChannel()+"_"+bean.getProduct()+"_lastMerchant");
					cacheBean.setValue(bean.getMerchantId());
					cacheBean.setTime(Constants.TIME_DAY*7);
					redisClient.setString(cacheBean, log, SysCodeConstant.PAYCENTER);
					bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
					bean.setBusiErrDesc("");
					return;
				}
			}
		}
		bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_NOT_FIND));
		bean.setBusiErrDesc("未找到相应的充值渠道");
	}

	//注入具体的充值信息
	private void injectRechDetailInfo(PayBean bean, JXmlWrapper rechInfoDetail) {
		String merchantId = rechInfoDetail.getStringValue("@mch_id");
		bean.setMerchantId(merchantId);
		String merchantKey = rechInfoDetail.getStringValue("@mch_key");
		if(!StringUtil.isEmpty(merchantKey)){
			bean.setMerchantKey(merchantKey);
		}
		String rechargeAppid = rechInfoDetail.getStringValue("@appid");
		bean.setRechargeAppid(rechargeAppid);
		String deskey = rechInfoDetail.getStringValue("@deskey");
		if(!StringUtil.isEmpty(deskey)){
			bean.setDesKey(deskey);
		}
		String rsapublickey = rechInfoDetail.getStringValue("@rsapublickey");
		if(!StringUtil.isEmpty(rsapublickey)){
			bean.setRsapublickey(rsapublickey);
		}
		String rsaprivatekey = rechInfoDetail.getStringValue("@rsaprivatekey");
		if(!StringUtil.isEmpty(rsaprivatekey)){
			bean.setRsaprivatekey(rsaprivatekey);
		}
	}

	/**
	 * 为充值订单支付
	 */
	public RechDto payForRecOrder(PayBean bean) {
		try {
			if(StringUtil.isEmpty(bean.getClassName())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_AMONEY_AMOUNT_ERROR));
				bean.setBusiErrDesc("充值渠道配置错误");
				log.info("充值渠道反射类名配置错误,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" bankid:"+bean.getBankid());
				return null;
			}
			IRecharge recharge = (IRecharge) SpringBeanFactoryUtils.getBean(bean.getClassName());
			if(RechargeTypeConstant.RECHARGETYPE_BANKCARD.equals(bean.getRechargeType())){//银行卡
				if(recharge instanceof IBankCardRech){
					IBankCardRech bankCardRech = (IBankCardRech)recharge;
					BankCardWrapper bankCardWrapper = (BankCardWrapper) SpringBeanFactoryUtils.getBean("BankCardWrapper");
					return bankCardWrapper.addmoney(bean, bankCardRech);
				}
			}else if(RechargeTypeConstant.RECHARGETYPE_WEIXIN.equals(bean.getRechargeType())){//微信
				if(recharge instanceof IWeiXinRech){
					IWeiXinRech weixinRech = (IWeiXinRech)recharge;
					WeiXinWrapper weixinWrapper = (WeiXinWrapper)SpringBeanFactoryUtils.getBean("WeiXinWrapper");
					return weixinWrapper.addmoney(bean, weixinRech);
				}
			}else if(RechargeTypeConstant.RECHARGETYPE_ALIPAY.equals(bean.getRechargeType())){//支付宝
				if(recharge instanceof IAlipayRech){
					IAlipayRech alipayRech = (IAlipayRech)recharge;
					AlipayWrapper alipayWrapper = (AlipayWrapper) SpringBeanFactoryUtils.getBean("AlipayWrapper");
					return alipayWrapper.addmoney(bean, alipayRech);
				}
			}else if(RechargeTypeConstant.RECHARGETYPE_TENPAY.equals(bean.getRechargeType())){//QQ支付
				if(recharge instanceof ITenpayRech){
					ITenpayRech tenpayRech = (ITenpayRech)recharge;
					TenpayWrapper tenpayWrapper = (TenpayWrapper) SpringBeanFactoryUtils.getBean("TenpayWrapper");
					return tenpayWrapper.addmoney(bean, tenpayRech);
				}
			}else if(RechargeTypeConstant.RECHARGETYPE_OTHER.equals(bean.getRechargeType())){
				return recharge.addmoney(bean);
			}
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CONFIG_ERROR));
			bean.setBusiErrDesc("充值渠道匹配错误");
			log.info("充值渠道类名类型匹配错误,用户名:"+bean.getUid()+" bankid:"+bean.getBankid()+" className:"+bean.getClassName()
					+" rechargeType:"+bean.getRechargeType()+" applyid:"+bean.getApplyid());
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_CLASS_REFLECT_FAIL));
			bean.setBusiErrDesc("充值异常,请稍后重试");
			log.error("充值实例化具体充值渠道失败,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+
					" className:"+bean.getClassName()+" bankid:"+bean.getBankid(),e);
		}
		return null;
	}

	//用户充值
	@Override
	public RechDto addmoney(PayBean bean) {
		if(!baseService.getUserWhiteGrade(bean)){
			return null;
		}
		if(!checkRechargeChannel(bean)){//检测充值渠道是否可用
			return null;
		}
		if(!createApplyid(bean)){//创建订单号
			return null;
		}
		return payForRecOrder(bean);
	}

	//银行卡确认消费接口
	@Override
	public RechDto agreeConsume(PayBean bean) {
		try {
			if(!baseService.getUserIdenInfo(bean)){
				return null;
			}
			if(!rechService.checkRechargeInfo(bean)){
				return null;
			}
			getAgreeConsumeRechargeInfo(bean);
			log.info("确认支付未找到对应的充值渠道信息,用户名:" + bean.getUid() + " channel:" + bean.getChannel());
			if(bean.getBusiErrCode()!=0){
				log.info("确认支付未找到对应的充值渠道信息,用户名:"+bean.getUid()+" bankid:"+bean.getBankid()+" rechargeType:"+bean.getRechargeType());
				return null;
			}
			IBankCardRech bankCardRech = (IBankCardRech) SpringBeanFactoryUtils.getBean(bean.getClassName());
			RechDto rech = bankCardRech.agreePay(bean);
			return rech;
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_CLASS_REFLECT_FAIL));
			bean.setBusiErrDesc("确认支付异常,请稍后重试");
			log.error("确认支付实例化具体充值渠道失败,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+
					" className:"+bean.getClassName()+" bankid:"+bean.getBankid(),e);
		} 
		return null;
	}

	//获取银行卡确认支付指定信息
	private void getAgreeConsumeRechargeInfo(PayBean bean) {
		JXmlWrapper rechargeInfo = JXmlWrapper.parse(new File(FileConstant.RECHARGE_INFO));
		JXmlWrapper rechTypeNode = rechargeInfo.getXmlNode(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
		injectAgreeConsumeRechargeInfo(bean,rechTypeNode);
		if(bean.getBusiErrCode()==0){
			return;
		}
	}

	//注入银行卡确认支付内容
	private void injectAgreeConsumeRechargeInfo(PayBean bean, JXmlWrapper rechTypeNode) {
		List<JXmlWrapper> rechargeInfoList = rechTypeNode.getXmlNodeList("rows");
		for(JXmlWrapper rechargeInfo : rechargeInfoList){
			String bankids = rechargeInfo.getStringValue("@bankid");
			String[] bankidArr = bankids.split(",");
			for(String xmlBankid : bankidArr){
				if(xmlBankid.trim().equals(bean.getBankid())){
					String channel = rechargeInfo.getStringValue("@channel");
					if(StringUtil.isEmpty(bean.getChannel())){//如果为空则设置配置文件中的channel
						bean.setChannel(channel);
					}
					if(!channel.equals(bean.getChannel())){
						bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CHANNEL_MATCH_ERROR));
						bean.setBusiErrDesc("渠道配置不匹配");
						log.info("客户端上传的渠道与配置文件渠道配置不匹配,用户名:"+bean.getUid()+" bankid:"+bean.getBankid()+" appChannel:"+bean.getChannel()+
								" configChannel:"+channel);
						continue;
					}
					//计算手续费
					double handrate = rechargeInfo.getDoubleValue("@handrate");
					double handmoney = Math.round(bean.getAddmoney() * handrate) / 100.0;
					double newaddmoney = bean.getAddmoney() - handmoney;
					bean.setAddmoney(newaddmoney);
					bean.setHandmoney(handmoney);
					//计算最低消费金额
					double consumerate = rechargeInfo.getDoubleValue("@consumerate");
					bean.setMinConsume(consumerate*bean.getAddmoney());
					String className = rechargeInfo.getStringValue("@className");
					bean.setClassName(className);
					String product = rechargeInfo.getStringValue("@product");
					if(!StringUtil.isEmpty(product)){
						bean.setProduct(product);
					}
					String payMerchantId = userPayMapper.queryMerchantId(bean.getApplyid());
					List<JXmlWrapper> rechInfoDetailList = rechargeInfo.getXmlNodeList("row");
					for(int i = 0; i < rechInfoDetailList.size(); i++){
						JXmlWrapper rechInfoDetail = rechInfoDetailList.get(i);
						String merchantId = rechInfoDetail.getStringValue("@mch_id");
						if(payMerchantId.equals(merchantId)){//如果商户号一样取下一个商户号
							injectRechDetailInfo(bean, rechInfoDetail);
							bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
							bean.setBusiErrDesc("");
							return;
						} 
					}
				}
			}
		}
		bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_NOT_FIND));
		bean.setBusiErrDesc("未找到相应的充值渠道");
	}

	@Override
	public UserRechCardDto userRechCardList(PayBean bean) {
		UserRechCardDto userRechCardDto = new UserRechCardDto();
		appendRechCardIdenInfo(bean, userRechCardDto);
		getUserRechCardList(bean, userRechCardDto);
		return userRechCardDto;
	}

	//获取用户充值银行卡信息
	private void getUserRechCardList(PayBean bean, UserRechCardDto userRechCardDto) {
		List<RechCardPojo> cardList = new ArrayList<>();
		if(StringUtil.isEmpty(bean.getChannel())){
			cardList = rechCardMapper.queryUserVisibleCard(bean.getUid());
		}else{
			cardList = rechCard_rechCarChannelMapper.queryUserChannelVisibleCard(bean);
		}
		List<SafeBean> realCardNoList = baseService.getRealCardList(cardList, bean);
		if(null == realCardNoList){
			log.info("用户名:"+bean.getUid()+"没有显示的银行卡");
			return;
		}
		for (RechCardPojo rechCard : cardList) {// 数据库中的银行卡信息
			for (SafeBean safeBean : realCardNoList) {// 安全中心银行卡信息
				if (rechCard.getSafeKey().equals(safeBean.getRechargeCardId())) {
					JSONObject cardJson = new JSONObject();
					cardJson.put("bankcode", rechCard.getBankCode());
					cardJson.put("bankName", rechCard.getBankName());
					cardJson.put("cardtype", rechCard.getCardtype());
					Map<String, String> bankCardLinkMap = baseService.getBankCardMap(rechCard.getBankCode());
					cardJson.put("linkimg", bankCardLinkMap.get("linkimg"));
					String mobile = "";
		   			if(!StringUtil.isEmpty(safeBean.getMobileno())){
		   				mobile = SecurityTool.iosencrypt(safeBean.getMobileno());
		   			} 
		   			cardJson.put("mobile", mobile);
		   			String cardNo = "";
		   			if(!StringUtil.isEmpty(safeBean.getBankcard())){
		   				cardNo = SecurityTool.iosencrypt(safeBean.getBankcard());
		   			}
		   			cardJson.put("cardno", cardNo);
		   			userRechCardDto.getRechCardList().add(cardJson);
				}
			}
		}
	}

	private void appendRechCardIdenInfo(PayBean bean, UserRechCardDto userRechCardDto) {
		boolean flag = baseService.getUserIdenInfo(bean);
		if(flag){
			userRechCardDto.setRealName(bean.getRealName());
			String idCard = bean.getIdcard();
			String encryptIdCard = SecurityTool.iosencrypt(idCard);
			userRechCardDto.setIdcard(encryptIdCard);
		} else {
			userRechCardDto.setRealName("");
			userRechCardDto.setIdcard("");
		}
	}

	@Override
	public List<BindCardInfoDto> deleteRechargeCard(PayBean bean){
		log.info("充值路由-删除用户卡信息  uid=="+bean.getUid()+"  cardno=="+ bean.getCardNo());
		String cardno = CardMobileUtil.decryptCard(bean.getCardNo());
		if(StringUtil.isEmpty(cardno)){
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
			bean.setBusiErrDesc("参数有误！");
		}
		List<BindCardInfoDto> bindCardInfoList= null;
		try {
			String cuserid="";
			//存储用于第三方解绑的信息
			UserPojo user=userBasicInfoWrapper.queryUserInfo(bean,log, SysCodeConstant.PAYCENTER);
			if(user!=null){
				cuserid=user.getCuserId();
			}
			bean.setSafeKey(MD5Helper.md5Hex(cardno));
			List<RechCardChannelPojo> rechCardInfoList=rechCardChannelMapper.queryRechCardBindInfo(bean);
			bindCardInfoList = new ArrayList<>();
			if(rechCardInfoList!=null&&rechCardInfoList.size()!=0){
				for(RechCardChannelPojo rechCardChannel:rechCardInfoList){
					BindCardInfoDto dto=new BindCardInfoDto();
					dto.setChannel(rechCardChannel.getChannel());
					dto.setUserPayId(rechCardChannel.getUserpayid());
					dto.setUserId(cuserid);
					bindCardInfoList.add(dto);
				}
				//删除银行卡和银行卡渠道绑定信息
				rechCardMapper.updateRechCardStatus(bean);
				rechCardChannelMapper.updateRechCardBindChannel(bean);
			}else{
				log.info("未查询到相关卡信息："+cardno);
			}
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
			log.error("删除用户卡信息失败,uid:{},cardNo:{}",bean.getUid(),bean.getCardNo(),e);
		}
		return bindCardInfoList;
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
}
