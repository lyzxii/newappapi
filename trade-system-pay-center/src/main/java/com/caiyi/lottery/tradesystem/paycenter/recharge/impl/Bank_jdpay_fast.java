package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.sign.DES;
import com.caiyi.lottery.tradesystem.util.sign.MD5;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;

import pay.bean.PayBean;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.HTTPSSecureProtocolSocketFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static pay.constant.PayConstant.NOTIFY_HOST;

/**
 * 京东快捷支付
 * @author 9188
 *
 */
@Slf4j
@Component("Bank_jdpay_fast")
public class Bank_jdpay_fast implements IBankCardRech{

	@Autowired
	NotifyService notifyService;

	// 生产环境
	private static final String version = "1.1.0";
	private static final String terminal = "00000001";
	private static final String charset = "UTF-8";

	//秘钥信息
	private static final String mch_id="110263491002";
	private static final String mechkey="XorkapfpGbqQlLqxQxdgpyrvFTfMjKTb";
	private static final String deskey="p2iSGSA9kQt8QCnsXRbcbel2VEof3Orx";

	private static final String targetUrl = "https://tmapi.jdpay.com/express.htm";
	private static String defaultcallbackurl = NOTIFY_HOST+"/pay/jdpay_fast_notify.api";

	//本地bankCode与京东bankCode的映射关系
	private static final Map<String, String> bankCodeMap = new HashMap<String, String>();
	static{
		bankCodeMap.put("ICBC", "ICBC");//中国工商银行
		bankCodeMap.put("CCB", "CCB");//中国建设银行
		bankCodeMap.put("BOC", "BOC");//中国银行
		bankCodeMap.put("ABC", "ABC");//中国农业银行
		bankCodeMap.put("COMM", "BCM");//中国交通银行
		bankCodeMap.put("PSBC", "PSBC");//中国邮储银行
		bankCodeMap.put("CMB", "CMB");//招商银行
		bankCodeMap.put("CITIC", "CITIC");//中信银行
		bankCodeMap.put("SPDB", "SPDB");//浦发银行
		bankCodeMap.put("CIB", "CIB");//兴业银行
		bankCodeMap.put("CMBC", "CMBC");//民生银行
		bankCodeMap.put("CEB", "CEB");//光大银行
		bankCodeMap.put("SZPAB", "PAB");//平安银行
		bankCodeMap.put("HXB", "HXB");//华夏银行
		bankCodeMap.put("BCCB", "BOB");//北京银行
		bankCodeMap.put("GDB", "CGB");//广发银行
		bankCodeMap.put("BOS", "BOS");//上海银行
		bankCodeMap.put("JSB", "JSB");//江苏银行
		bankCodeMap.put("NJCB", "NJCB");//南京银行
		bankCodeMap.put("NBCB", "NBCB");//宁波银行
		bankCodeMap.put("HSBANK", "HSB");//徽商银行
		bankCodeMap.put("HCCB", "HZB");//杭州银行
	}

	//错误码与后续类型的映射
	private static final Map<String, Integer> ErrorToPageMap = new HashMap<String, Integer>();
	static{
		//10-留在当前页面
		ErrorToPageMap.put("EEB0025", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
		ErrorToPageMap.put("EEB0023", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
		ErrorToPageMap.put("EES0027", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
		ErrorToPageMap.put("EEN0017", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
		ErrorToPageMap.put("EES0035", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
		//20-错误提示,不重新支付
		ErrorToPageMap.put("EEB0014",Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_UNREPAY));//20
		//30-重新支付
	}
	@Override
	public RechDto addmoney(PayBean bean) {
		log.info("京东快捷充值,applyid:"+bean.getApplyid()+" bankid:"+bean.getBankid()+" uid:"+bean.getUid()+" addmoney:"+bean.getAddmoney());
		try {
			bean.setMerchantId(mch_id);
			bean.setMerchantKey(mechkey);
			bean.setDesKey(deskey);
			String reqData = createSignInfo(bean);
			if(bean.getBusiErrCode()!=0){
				log.error("京东快捷充值下单创建签名信息错误,applyid:{},uid:{},bankid:{}",bean.getApplyid(),bean.getBankid());
				bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
				bean.setBusiErrDesc("支付请求失败，请重新尝试");
				return null;
			}
			String result = sendToJdPay(reqData,bean);
			return parseResult(result,bean);
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
			bean.setBusiErrDesc("支付请求失败，请重新尝试");
			log.error("京东快捷充值下单出现异常,applyid:{},uid:{},bankid:{}",bean.getApplyid(),bean.getBankid(),e);
		}
		return null;
	}


	@Override
	public void backNotify(PayBean bean) {
		//更新账户，加款操作
		notifyService.applyAccountSuc(bean);
	}

	@Override
	public RechDto agreePay(PayBean bean) throws Exception {
		bean.setMerchantId(mch_id);
		bean.setMerchantKey(mechkey);
		bean.setDesKey(deskey);
		String reqData = createConsumeInfo(bean);
		String result = sendToJdPay(reqData,bean);
		return parseResult(result,bean);
	}

	//组织用户签约信息
	private static String createSignInfo(PayBean bean) throws Exception {
		StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<CHINABANK>");
		builder.append("<VERSION>").append(version).append("</VERSION>");
		builder.append("<MERCHANT>").append(bean.getMerchantId()).append("</MERCHANT>");
		builder.append("<TERMINAL>").append(terminal).append("</TERMINAL>");
		StringBuilder dataBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		dataBuilder.append("<DATA>");
		organizeCardInfo(bean, dataBuilder);

		dataBuilder.append("<TRADE>");
		dataBuilder.append("<TYPE>V</TYPE>");
		dataBuilder.append("<ID>").append(bean.getApplyid()).append("</ID>");
		int addmoney = (int)bean.getAddmoney()*100;//单位为分
		//int addmoney = 1;
		dataBuilder.append("<AMOUNT>").append(addmoney).append("</AMOUNT>");
		dataBuilder.append("<CURRENCY>CNY</CURRENCY>");
		dataBuilder.append("</TRADE>");
		dataBuilder.append("</DATA>");
		log.info("京东支付签约data数据:"+dataBuilder.toString()+" 订单号:"+bean.getApplyid());
		//获取加密后的DATA信息
		String data = DES.encrypt(dataBuilder.toString(),bean.getDesKey(), "UTF-8");
		builder.append("<DATA>").append(data).append("</DATA>");
		//获取签名内容
		String sign = MD5.sign(version+bean.getMerchantId()+terminal+data,bean.getMerchantKey(), charset);
		builder.append("<SIGN>").append(sign.toLowerCase()).append("</SIGN>");
		builder.append("</CHINABANK>");
		log.info("京东签约req的数据内容:"+builder.toString()+" 订单号:"+bean.getApplyid());
		return builder.toString();
	}

	private static void organizeCardInfo(PayBean bean, StringBuilder dataBuilder) {
		String type = "";
		String exp = "";
		String cvv2 = "";
		if(StringUtil.isEmpty(bean.getMobileNo())){
			bean.setBusiErrCode(-1);
			bean.setBusiErrDesc("手机号不能为空");
			log.info("手机号不能为空,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid());
			return;
		}
		if(1==bean.getCardtype()){//信用卡
			type = "C";
			exp = bean.getValidDate();
			cvv2 = bean.getCvv();
			if(StringUtil.isEmpty(exp)){
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("信用卡有效期不可为空");
				log.info("用户名的信用卡有效期为空,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 有效期:"+exp);
				return;
			}
			if(StringUtil.isEmpty(cvv2)){
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("信用卡验证码不可为空");
				log.info("用户名的信用卡验证码为空,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 验证码:"+cvv2);
				return;
			}
		}else{//借记卡
			type = "D";
		}
		dataBuilder.append("<CARD>");
		dataBuilder.append("<BANK>").append(bankCodeMap.get(bean.getBankCode())).append("</BANK>");
		dataBuilder.append("<TYPE>").append(type).append("</TYPE>");
		dataBuilder.append("<NO>").append(bean.getCardNo()).append("</NO>");
		dataBuilder.append("<EXP>").append(exp).append("</EXP>");
		dataBuilder.append("<CVV2>").append(cvv2).append("</CVV2>");
		dataBuilder.append("<NAME>").append(bean.getRealName()).append("</NAME>");
		dataBuilder.append("<IDTYPE>I</IDTYPE>");
		dataBuilder.append("<IDNO>").append(bean.getIdcard()).append("</IDNO>");
		dataBuilder.append("<PHONE>").append(bean.getMobileNo()).append("</PHONE>");
		dataBuilder.append("</CARD>");
	}

	//发送给京东
	private static String sendToJdPay(String reqData,PayBean bean) throws Exception {
		Protocol https = new Protocol("https", new HTTPSSecureProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", https);
		HttpClient client = new HttpClient();

		HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
		httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);

		PostMethod postMethod = new PostMethod(targetUrl);
		postMethod.setRequestHeader("Content-Type","text/xml;charset=UTF-8");
		reqData = Base64.encode(reqData.getBytes(),"UTF-8");
		postMethod.addParameter("req", reqData);
		postMethod.addParameter("charset",charset);

		RequestEntity requestEntity = postMethod.getRequestEntity();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		requestEntity.writeRequest(baos);
		String requestBody = baos.toString();
		log.info("京东支付请求内容,订单号:"+bean.getApplyid()+" ------>: " + requestBody);

		client.executeMethod(postMethod);
		String responseBody = postMethod.getResponseBodyAsString();
		log.info("京东支付响应内容,订单号:"+bean.getApplyid()+" ------>: " + responseBody);
		Protocol.unregisterProtocol("https");

		return responseBody;
	}

	private RechDto parseResult(String result, PayBean bean) throws Exception {
		if(!StringUtil.isEmpty(result)){
			JXmlWrapper xml = null;
			String respData = null;
			String returnCode = null;
			String returnDesc = null;
			try {
				String respResut = result.substring(result.indexOf("=") + 1);
				String xmlResult = new String(Base64.decode(respResut.getBytes()));
				log.info("京东支付响应内容:"+xmlResult+" 订单号:"+bean.getApplyid());
				xml = JXmlWrapper.parse(xmlResult);
				respData = xml.getStringValue("DATA");
				log.info("京东支付响应DATA内容:"+respData+" 订单号:"+bean.getApplyid());

				String decData = DES.decrypt(respData,bean.getDesKey(), charset);

				JXmlWrapper dataXml = JXmlWrapper.parse(decData);
				JXmlWrapper returnData = dataXml.getXmlNode("RETURN");
				returnCode = returnData.getStringValue("CODE");
				returnDesc = returnData.getStringValue("DESC");
				log.info("京东快捷支付响应,返回码:"+returnCode+" 返回描述:"+returnDesc+" 订单号:"+bean.getApplyid());
			} catch (Exception e) {
				returnCode="00001";
				returnDesc=result.substring(result.indexOf(",")+1,result.length());
			}
			if(!"0000".equals(returnCode)){
				bean.setBusiErrCode(1);
				bean.setBusiErrDesc(returnDesc);
				convertError(returnCode,bean);
				//记录渠道错误信息
				bean.setRechargeCode(returnCode);
				bean.setRechargeDesc(returnDesc);
			}else{
				//获取返回的签名
				String returnSign = xml.getStringValue("SIGN");
				String version = xml.getStringValue("VERSION");
				String merchant = xml.getStringValue("MERCHANT");
				String terminal = xml.getStringValue("TERMINAL");
				//本地签名
				String sign = MD5Helper.md5Hex(version+merchant+terminal+respData,bean.getMerchantKey()).toLowerCase();
				if(returnSign.equals(sign)){
					RechDto dto=new RechDto();
					dto.setApplyid(bean.getApplyid());
					dto.setAddmoney(bean.getAddmoney());
					dto.setPayParam(new PayParam());
					log.info("京东快捷支付验签成功,原始签名:"+returnSign+" 本地签名:"+sign+" 订单号:"+bean.getApplyid());
					return dto;
				}else{
					log.info("京东快捷支付验签失败,原始签名:"+returnSign+" 本地签名:"+sign+" 订单号:"+bean.getApplyid());
				}
			}
		}else{
			log.info("京东快捷支付,未收到返回信息 订单号:"+bean.getApplyid());
		}
		return null;
	}

	private void convertError(String returnCode, PayBean bean) {
		Integer returnType = ErrorToPageMap.get(returnCode);
		if(returnType==null){
			//切换到下一个渠道
			bean.setBusiErrCode(30);
		}else{
			bean.setBusiErrCode(returnType);
		}
	}

	//组织消费数据
	private static String createConsumeInfo(PayBean bean) throws Exception {
		StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<CHINABANK>");
		builder.append("<VERSION>").append(version).append("</VERSION>");
		builder.append("<MERCHANT>").append(bean.getMerchantId()).append("</MERCHANT>");
		builder.append("<TERMINAL>").append(terminal).append("</TERMINAL>");
		StringBuilder dataBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		dataBuilder.append("<DATA>");
		organizeCardInfo(bean, dataBuilder);

		dataBuilder.append("<TRADE>");
		dataBuilder.append("<TYPE>S</TYPE>");
		dataBuilder.append("<ID>").append(bean.getApplyid()).append("</ID>");

		int addmoney = (int)bean.getAddmoney()*100;//单位为分
		//int addmoney = 1;
		dataBuilder.append("<AMOUNT>").append(addmoney).append("</AMOUNT>");
		dataBuilder.append("<CURRENCY>CNY</CURRENCY>");
		dataBuilder.append("<NOTICE>").append(defaultcallbackurl).append("</NOTICE>");
		dataBuilder.append("<NOTE>充值</NOTE>");
		dataBuilder.append("<CODE>").append(bean.getVerifycode()).append("</CODE>");
		dataBuilder.append("</TRADE>");
		dataBuilder.append("</DATA>");
		log.info("京东支付消费data数据:"+dataBuilder.toString()+" 订单号:"+bean.getApplyid());
		//获取加密后的DATA信息
		String data = DES.encrypt(dataBuilder.toString(),bean.getDesKey(), "UTF-8");
		builder.append("<DATA>").append(data).append("</DATA>");
		//获取签名内容
		String sign = MD5.sign(version+bean.getMerchantId()+terminal+data,bean.getMerchantKey(), charset);
		builder.append("<SIGN>").append(sign.toLowerCase()).append("</SIGN>");
		builder.append("</CHINABANK>");
		log.info("京东消费req的数据内容:"+builder.toString()+" 订单号:"+bean.getApplyid());
		return builder.toString();
	}
}