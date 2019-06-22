package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import bean.SafeBean;
import bean.SourceConstant;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.*;
import com.caiyi.lottery.tradesystem.paycenter.service.BaseService;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pay.bean.PayBean;
import pay.bean.PaySftBean;
import pay.constant.RechargeTypeConstant;
import pay.dto.OrderStatusDto;
import pay.pojo.RechCardChannelPojo;
import pay.pojo.RechCardPojo;
import pay.pojo.UserPayPojo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RechServiceImpl implements RechService{
	
	@Autowired
	SafeCenterInterface safeCenterInterface;
	@Autowired
	UserPayMapper userPayMapper;
	@Autowired
	RechCardMapper rechCardMapper;
	@Autowired
	RechCardChannelMapper rechCardChannelMapper;
	@Autowired
	BaseService baseService;

	@Autowired
	RechErrorInfoMapper rechErrorInfoMapper;

	@Autowired
	ShengpayOrderMapper shengpayOrderMapper;

	@Autowired
	NotifyService notifyService;

	//记录充值银行卡信息
	@Transactional(rollbackFor = {Exception.class})
	public void recordBankCardInfo(PayBean bean) throws Exception{
		log.info("记录银行卡信息,bankid:"+bean.getBankid()+" 用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+
				" mobile:"+bean.getMobileNo()+" cardNo:"+bean.getCardNo());
		if(!checkRechargeInfo(bean)){
			return;
		}
		setRechCardInfo(bean);
		PayBean encryptBean = encryptSentiveData(bean);
		int exist = rechCardMapper.userCardExsit(bean.getSafeKey(), bean.getUid());
		if(exist == 0){//没有充值银行卡
			checkLocalCardNo(bean);
			if(bean.getBusiErrCode()!=0){
				return;
			}
			int retRech = rechCardMapper.insertRechCard(encryptBean);
			int retChannel = rechCardChannelMapper.insertRechCardChannel(encryptBean);
			if(retRech!=1||retChannel!=1){
				log.info("插入银行卡信息出错,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" retRech:"+retRech+" retChannel:"+retChannel);
				bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_SAFE_CARD_FAIL));
				bean.setBusiErrDesc("订单出错,请稍后重试");
				throw new Exception("插入用户银行卡信息出错");
			}
			if(!saveCardToSafeCenter(bean)){
				log.info("首次存储银行卡至安全中心失败,用户名:"+bean.getUid()+" 卡号:"+bean.getCardNo());
				bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_CENTER_SAFE_FAIL));
				bean.setBusiErrDesc("订单出错,请稍后重试");
				throw new Exception("保存协议号至安全中心出错");
			}
		} else {//存在用户银行卡,检查是否存在充值成功的协议号
			int successCount = rechCardChannelMapper.countSuccessChannel(bean.getUid(), bean.getSafeKey());
			if(!StringUtil.isEmpty(bean.getMobileNo())){
				int noMobileCount = rechCardMapper.userCardExsitNoMobie(bean.getSafeKey(), bean.getUid());
				if(successCount == 0 || noMobileCount > 0){//没有充值成功的协议渠道号或有银行卡没有手机号,更新手机号码
					int retUpdate = rechCardMapper.updateRechCardMobile(encryptBean);
					if(retUpdate!=1){
						log.info("更新用户手机号失败,用户名:"+bean.getUid()+" 手机号:"+bean.getMobileNo()+" safeKey:"+bean.getSafeKey());
						bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_UPDATE_RECH_MOBILE_EXCEPTION));
						bean.setBusiErrDesc("订单出错,请稍后重试");
						throw new Exception("更新用户充值手机号失败");
					}
					if(!saveCardToSafeCenter(bean)){
						log.info("更新存储银行卡至安全中心失败,用户名:"+bean.getUid()+" 卡号:"+bean.getCardNo());
						bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_CENTER_SAFE_FAIL));
						bean.setBusiErrDesc("订单出错,请稍后重试");
						throw new Exception("保存协议号至安全中心出错");
					}
				}
			}
			if(successCount == 0){
				int updateRet = rechCardMapper.updateRechCardInfo(encryptBean);
				if(updateRet != 1){
					log.info("更新银行卡信息出错,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" retChannel:"+updateRet);
					bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_UPDATE_RECH_CARD_EXCEPTION));
					bean.setBusiErrDesc("订单出错,请稍后重试");
					throw new Exception("更新用户银行卡信息出错");
				}
			}
			int count = rechCardChannelMapper.countUserChannelCard(bean);
			if(count == 0){
				int retChannel = rechCardChannelMapper.insertRechCardChannel(encryptBean);
				if(retChannel!=1){
					log.info("插入银行卡信息出错,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" retChannel:"+retChannel);
					bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_SAFE_CARD_FAIL));
					bean.setBusiErrDesc("订单出错,请稍后重试");
					throw new Exception("插入用户银行卡信息出错");
				}
			}
		}
		int ret = userPayMapper.updateUerPaySafeKey(bean.getSafeKey(), bean.getApplyid());
		if(ret!=1){
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_SAVE_PAY_SAFEKEY_FAIL));
			bean.setBusiErrDesc("订单出错,请稍后重试");
			log.info("更新用户支付表安全中心对应数据出错,用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid()+" 安全中心对应key:"+bean.getSafeKey());
			throw new Exception("更新银行卡号至订单出错");
		}
	}
	
	//加密敏感数据
	private PayBean encryptSentiveData(PayBean bean) {
		PayBean encryptBean = new PayBean();
		BeanUtilWrapper.copyPropertiesIgnoreNull(bean, encryptBean);
		String cardNo = encryptBean.getCardNo();
		encryptBean.setCardNo(cardNo.substring(0, 4)+"********"+cardNo.substring(cardNo.length()-4));
		if(!StringUtil.isEmpty(encryptBean.getMobileNo())){
			String mobileNo = encryptBean.getMobileNo();
			encryptBean.setMobileNo(mobileNo.substring(0, 3)+"****"+mobileNo.substring(mobileNo.length()-4));
			encryptBean.setMd5Mobile(MD5Helper.md5Hex(mobileNo));
		}
		return encryptBean;
	}

	//检测数据库银行卡是否存在前六后四的银行卡
	private void checkLocalCardNo(PayBean bean){
		SafeBean safeBean = new SafeBean();
		safeBean.setNickid(bean.getUid());
		safeBean.setUsersource(SourceConstant.CAIPIAO);
		BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.PAYCENTER);
		BaseResp<List<SafeBean>> resp = safeCenterInterface.getRechargeAllBankcard(req);
		if(!"0".equals(resp.getCode())&&!BusiCode.NOT_EXIST.equals(resp.getCode())){
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_QUERY_SAFE_ALLCARD_FAIL));
			bean.setBusiErrDesc("生成订单失败,请稍后重试");
			log.info("获取安全用户所有银行卡失败,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid());
			return;
		}
		String firstSixCardNo = bean.getCardNo().substring(0, 6);
		String lastFourCardNo = bean.getCardNo().substring(bean.getCardNo().length()-4,bean.getCardNo().length());
		List<SafeBean> safeBeanList = resp.getData();
		if(!BusiCode.NOT_EXIST.equals(resp.getCode())){
			for(SafeBean safeData : safeBeanList){
				String bankCard = safeData.getBankcard();
				String firstSixLocalCardNo = bankCard.substring(0,6);
				String lastFourLocalCardNo = bankCard.substring(bankCard.length()-4,bankCard.length());
				//判断事都是前6位和后四位相等
				if(firstSixCardNo.equals(firstSixLocalCardNo)&&lastFourCardNo.equals(lastFourLocalCardNo)){
					//判断是否是同一张卡
					if(bean.getCardNo().equals(bankCard)){
						//判断是否显示
						RechCardPojo rechCard = rechCardMapper.queryCardByKey(safeData.getRechargeCardId(),bean.getUid());
						if(null == rechCard){
							log.info("本地数据库中不存在该银行卡,用户名:"+bean.getUid()+" safeKey:"+safeData.getRechargeCardId());
							return;
						}
						if(0 == rechCard.getStatus()){
							log.info("该卡已绑定但是未显示,cardno:"+bean.getCardNo()+"用户信息："+bean.getUid());
						}else{
							bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CARD_SHOW));
							bean.setBusiErrDesc("该银行卡已存在并且已经显示");
							log.info("该卡已经绑定，且以显示,cardno:"+bean.getCardNo()+"用户信息："+bean.getUid());
							return;
						}
					}else{
						bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CARD_SIMILAR));
						bean.setBusiErrDesc("该银行卡的前六后四相同，中间号码不一致");
						log.info("已存在前六位，后四位相同的银行卡号,cardno:"+bean.getCardNo()+"用户信息："+bean.getUid());
						return;
					}
				}
			}
		}
	}

	//设置充值银行卡基础信息
	private void setRechCardInfo(PayBean bean) {
		Map<String,String> supportBankMap = baseService.getBankCardMap(bean.getBankCode());
		bean.setBankName(supportBankMap.get("bankname"));
		if(bean.getCardtype()==1){
			bean.setCardName("信用卡");
		}else{
			bean.setCardName("借记卡");
		}
		String lastFourCardNo = bean.getCardNo().substring(bean.getCardNo().length()-4,bean.getCardNo().length());
		bean.setLastFourCardNum(lastFourCardNo);
		if(StringUtil.isEmpty(bean.getAuthFlag())){
			bean.setAuthFlag("0");//未鉴权
		}
		//银行卡的MD5值
		bean.setSafeKey(MD5Helper.md5Hex(bean.getCardNo()));
	}

	//保存银行卡到安全中心
	private boolean saveCardToSafeCenter(PayBean bean) {
		SafeBean safeBean = new SafeBean();
		safeBean.setNickid(bean.getUid());
		safeBean.setBankcard(bean.getCardNo());
		safeBean.setMobileno(bean.getMobileNo());
		safeBean.setUsersource(SourceConstant.CAIPIAO);
		BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.PAYCENTER);
		BaseResp<SafeBean> resp = safeCenterInterface.addRechargeCard(req);
		if(!"0".equals(resp.getCode())){
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_CENTER_SAFE_FAIL));
			bean.setBusiErrDesc("系统下单失败,请重新尝试");
			log.info("保存用户充值信息至安全中心失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return false;
		}
		return true;
	}
	
	//检测充值银行卡信息
	@Override
	public boolean checkRechargeInfo(PayBean bean){
		String cardNo = CardMobileUtil.decryptCard(bean.getCardNo());
		String mobileNo = CardMobileUtil.decryptMobile(bean.getMobileNo());
		if(StringUtil.isEmpty(cardNo)){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CARDNO_ERROR));
			bean.setBusiErrDesc("银行卡号不能为空");
			log.info("用户的银行卡为空,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" bankid:"+bean.getBankid());
			return false;
		}
		bean.setCardNo(cardNo);
		bean.setMobileNo(mobileNo);
		log.info("用户名充值保存银行卡,解密后 银行卡:"+bean.getCardNo()+" 手机号:"+bean.getMobileNo()+" 用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid());
		if(!CardMobileUtil.checkBankCard(cardNo)){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CARDNO_ERROR));
			bean.setBusiErrDesc("银行卡号格式错误");
			log.info("用户的银行卡为空,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" bankid:"+bean.getBankid()+" cardNo:"+bean.getCardNo());
			return false;
		}
		if(!StringUtil.isEmpty(mobileNo)){
			if(!CheckUtil.isMobilephone(mobileNo)){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_MOBILE_ERROR));
				bean.setBusiErrDesc("银行卡手机号格式错误");
				log.info("用户的手机号格式格式错误,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" bankid:"+bean.getBankid()+" cardNo:"+bean.getCardNo());
				return false;
			}
		}
		if(StringUtil.isEmpty(bean.getBankCode())){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_PARAM_ERROR));
			bean.setBusiErrDesc("缺少充值要素,请重试");
			log.info("缺少充值要素,用户名:"+bean.getUid()+" applyid:"+bean.getApplyid()+" bankName:"+bean.getBankName()+" bankCode:"+bean.getBankCode());
			return false;
		}
		return true;
	}

	@Override
	public void  saveUserPayErrorInfo(PayBean bean) {
		log.info("更新用户充值错误信息……");
		saveUserPayErrorImage(bean);
	}

	private void saveUserPayErrorImage(PayBean bean) {
		String currentDate = DateTimeUtil.getCurrentDate();
		//tb_user_pay查询csafekey,拿到safekey
		baseService.getCardNoByApplyid(bean);
		//查询bankcode
		RechCardPojo pojo = rechCardMapper.getBankCode(bean.getUid(), bean.getSafeKey());
		//保存错误信息
		if (pojo == null) {
			log.info("保存错误信息出错，未查询到bankCode,uid==" + bean.getUid() + ",safeKey==" + bean.getSafeKey());
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
			bean.setBusiErrDesc("保存错误信息出错，未查询到bankCode");
			return;
		}
		bean.setBankCode(pojo.getBankCode());
		bean.setCardtype(pojo.getCardtype());
		bean.setApplydate(currentDate);
		String remark = bean.getRechargeCode() + " :" + bean.getRechargeDesc();
		bean.setRemark(remark);
		log.info("更新错误信息-->product=="+bean.getProduct()+",channel=="+bean.getChannel()+",applyid=="+bean.getApplyid() +",bankCode=="+bean.getBankCode()+",cardType=="+bean.getCardtype()+",applydate=="+bean.getApplydate()+",remark=="+bean.getRemark());
		int i = rechErrorInfoMapper.insertErrorInfo(bean);
		if (i == 1) {
			log.info("保存用户充值错误信息成功  csatday==" + currentDate + " channelCode==" + bean.getChannel() + "  uid==" + bean.getUid() + "  cardno==" + bean.getCardNo());
		} else {
			log.info("保存用户充值错误信息失败  csatday==" + currentDate + " channelCode==" + bean.getChannel() + "  uid==" + bean.getUid() + "  cardno==" + bean.getCardNo());
		}
	}

	private void readRechargeInfo(PayBean bean) {
		//读取recharge-info.xml获取product，channel，bankcode
		JXmlWrapper rechargeInfo = JXmlWrapper.parse(new File(FileConstant.RECHARGE_INFO));
		if(StringUtil.isEmpty(bean.getRechargeType())){
			for(String rechargeType : RechargeTypeConstant.RECHARGETYPE_ARR){
				bean.setRechargeType(rechargeType);
				JXmlWrapper rechTypeNode = rechargeInfo.getXmlNode(rechargeType);
				getProduct(bean,rechTypeNode);
				if(bean.getBusiErrCode()==0){
					return;
				}
			}
		}else{
			JXmlWrapper rechTypeNode = rechargeInfo.getXmlNode(bean.getRechargeType());
			getProduct(bean, rechTypeNode);
		}

	}

	private void getProduct(PayBean bean, JXmlWrapper rechTypeNode) {
		bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
		List<JXmlWrapper> rechargeInfoList = rechTypeNode.getXmlNodeList("row");
		for (JXmlWrapper xml : rechargeInfoList) {
			String bankids = xml.getStringValue("@bankid");
			String[] bankidArr = bankids.split(",");
			for (String bankid : bankidArr) {
				if (bean.getBankid().trim().equals(bankid.trim())) {
					String channel = xml.getStringValue("@channel");
					String product = xml.getStringValue("@product");
					bean.setChannel(channel);
					bean.setProduct(product);
					bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
				}
			}
		}
	}

	//查询订单状态信息
	@Override
	public OrderStatusDto queryOrderStatus(PayBean bean) {
		log.info("查询订单号状态  applyid==" + bean.getApplyid());
		UserPayPojo userPay = userPayMapper.queryPayInfo(bean.getApplyid());
		if(null == userPay){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_NOT_EXIST_APPLYID));
			bean.setBusiErrDesc("未查询到订单的相关信息");
			log.info("未查询到订单号的详细信息,订单号:"+bean.getApplyid());
			return null;
		}
		OrderStatusDto orderStatus = new OrderStatusDto();
		orderStatus.setMoney(userPay.getAddmoney()+"");
		orderStatus.setApplyStatus(userPay.getIsSuccess()+"");
		orderStatus.setRechargeName(XmlUtil.CHONGZHI.get(userPay.getBankid()));
		bean.setBusiErrCode(0);
		bean.setBusiErrDesc("查询成功");
		return orderStatus;
	}

	/****
	 *记录第三方支付单号
	 * @param bean
	 */
	@Override
	public void updateUserPayDealid(PayBean bean) {
		log.info("更新下单支付商号 cconfirmid["+bean.getDealid()+"],订单号="+bean.getApplyid());
		try {
			int flag=userPayMapper.updateUserPayDealid(bean);
			if(flag==1){
                log.info("更新下单支付商号 cconfirmid["+bean.getDealid()+"],结果：[成功],订单号="+bean.getApplyid());
            }else{
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("更新下单支付商号cconfirmid出错！");
                log.info("更新下单支付商号cconfirmid["+bean.getDealid()+"],结果：[失败],订单号="+bean.getApplyid());
            }
		} catch (Exception e) {
			log.info("updateUserPayDealid异常,下单支付商号cconfirmid["+bean.getDealid()+"],订单号="+bean.getApplyid()+" Exception: " + e.getMessage(),e);
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
			bean.setBusiErrDesc("更新下单支付商号cconfirmid["+bean.getDealid()+"]出错!,订单号="+bean.getApplyid());
		}
	}

	public int saveShengpayOrderInfo(PaySftBean sftBean) {
		//支付金额
		DecimalFormat df=new DecimalFormat( "#####0.00");
		double s=sftBean.getAddmoney() + sftBean.getHandmoney();
		String amount=df.format(s);
		sftBean.setAmount(amount);
		String desc = "盛付通创建支付订单成功，等待确认支付";
		sftBean.setDesc(desc);
		return shengpayOrderMapper.saveShengpayOrderInfo(sftBean);

	}
}
