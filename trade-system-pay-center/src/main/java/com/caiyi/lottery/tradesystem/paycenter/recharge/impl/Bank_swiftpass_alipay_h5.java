package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.caiyi.lottery.tradesystem.constants.BaseConstant;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IAlipayRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.dto.RechDto;
import pay.pojo.PayParam;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.*;

import static pay.constant.PayConstant.NOTIFY_HOST;

@Slf4j
@Component("Bank_swiftpass_alipay_h5")
public class Bank_swiftpass_alipay_h5 implements IAlipayRech{

	//正式
	private static final String mch_id = "101560019260";

	private static final String mch_key = "6b2e15786035716b923922e3074edd19";

	private static String defaultfrontbackurl = "https://5.9188.com/new/#/recharge/result";

	private static String targetUrl = "https://pay.swiftpass.cn/pay/gateway";

	private static String notify_url=NOTIFY_HOST+"/pay/swiftpass_alipay_h5_notify.api";


	@Autowired
	private NotifyService notifyService;

	@Override
	public RechDto addmoney(PayBean bean) {
		try {
			RechDto rechDto = new RechDto();
			log.info("威富通支付宝H5请求支付开始  订单号:"+bean.getApplyid()+"用户名:"+bean.getUid());
			String mchId=mch_id;
			String mchKey=mch_key;
			if(!StringUtil.isEmpty(bean.getMerchantId())){
                mchId=bean.getMerchantId();
			}
			if(!StringUtil.isEmpty(bean.getMerchantKey())){
               mchKey=bean.getMerchantKey();
			}
			String result = createPayment(bean,mchId,mchKey);
			if(StringUtil.isEmpty(result)){
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("支付请求失败，请重新尝试");
				log.info("发送支付请求至威富通支付宝H5失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid());
			}else{
				rechDto = parseRechargeRet(bean, mchKey, result);
			}
			return rechDto;
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.PAY_RECHARGE_ADDMONEY_EXCEPTION));
			bean.setBusiErrDesc("支付请求失败，请重新尝试");
			log.error("支付请求失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid(),e);
		}
		return null;
	}

	private RechDto parseRechargeRet(PayBean bean, String mchKey, String result) throws Exception {
		RechDto rechDto = new RechDto();
		JXmlWrapper xml = JXmlWrapper.parse(result);
		String status = xml.getStringValue("status");
		String resultCode = xml.getStringValue("result_code");
		if("0".equals(status)&&"0".equals(resultCode)){
			Map<String,String> map = XmlUtil.toMap(result.getBytes(), "utf-8");
			String respSign = map.get("sign");
		    map.remove("sign");
		    String sign = getFormDataParamMD5(map, "&key="+mchKey);
		    if(respSign.equalsIgnoreCase(sign)){
				String payUrl = xml.getStringValue("code_url");
				RechDto dto = new RechDto();
				PayParam payParam=new PayParam();
				dto.setApplyid(bean.getApplyid());
				dto.setAddmoney(bean.getAddmoney());
				payParam.setPrepayUrl(URLEncoder.encode(payUrl, "UTF-8"));
				dto.setPayParam(payParam);
				log.info("威富通支付宝H5验签成功,原始签名respSign:"+respSign+"本地签名:"+sign+",订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
		    }else{
		    	bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_SIGN_ERROR));
		    	bean.setBusiErrDesc("支付请求失败，请重新尝试");
		    	log.info("威富通支付宝H5验签失败,原始签名respSign:"+respSign+"本地签名:"+sign+",订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
		    }
		}else{
			bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_ADDMONEY_FAIL));
			bean.setBusiErrDesc("支付请求失败，请重新尝试");
			log.info("威富通支付宝H5充值失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
		}
		return rechDto;
	}

	@Override
	public void backNotify(PayBean bean) {
		//更新账户，加款操作
		notifyService.applyAccountSuc(bean);
	}
	
	private String createPayment(PayBean bean, String mchId, String mchKey) {
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("version", "2.0");
		dataMap.put("charset", BaseConstant.charset);
		dataMap.put("service", "paycenter.alipay.native");
		dataMap.put("sign_type", "MD5");
		dataMap.put("mch_id", mchId);
		dataMap.put("out_trade_no", bean.getApplyid());
		dataMap.put("body", "充值");
		int addmoney = (int)bean.getAddmoney()*100;//单位为分
		dataMap.put("total_fee", addmoney+"");//单位为分
		dataMap.put("mch_create_ip", bean.getIpAddr());
		dataMap.put("nonce_str", String.valueOf(new Date().getTime()));
		dataMap.put("notify_url",notify_url);
		if(StringUtil.isEmpty(bean.getWebcallbackurl())){
			bean.setWebcallbackurl(defaultfrontbackurl);
		}
		dataMap.put("callback_url", bean.getWebcallbackurl());
		if(bean.getMtype()==2){//IOS
			dataMap.put("device_info", "iOS_WAP");
		}else if(bean.getMtype()==1){//Android
			dataMap.put("device_info", "AND_WAP");
		}else{
			dataMap.put("device_info", "iOS_WAP");
		}
		dataMap.put("mch_app_name", "9188官网");
		dataMap.put("mch_app_id", "http://5.9188.com");
		String signature = getFormDataParamMD5(dataMap, "&key="+mchKey);
		dataMap.put("sign", signature.toUpperCase());
		try {
			String requsetData = XmlUtil.parseXML(dataMap);
			String result = sendToSwiftpass(requsetData,bean);
			return result;
		} catch (Exception e) {
			log.info("请求充值至威富通支付宝H5失败,用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid());
		}
		return null;
	}
	
	private String sendToSwiftpass(String requestData, PayBean bean) throws Exception {
		HttpClient client = new HttpClient();
		
		HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
		httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		
		PostMethod postMethod = null;
		postMethod = new PostMethod(targetUrl);
		postMethod.setRequestHeader("Content-Type","text/xml;charset=UTF-8");
		postMethod.setRequestEntity(new StringRequestEntity(requestData,"text/xml","UTF-8"));

		RequestEntity requestEntity = postMethod.getRequestEntity();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		requestEntity.writeRequest(baos);
		String requestBody = baos.toString();
		log.info("威富通支付宝H5请求内容,订单号:"+bean.getApplyid()+" ------>: " + requestBody);
		
		client.executeMethod(postMethod);
		String responseBody = postMethod.getResponseBodyAsString();
		log.info("威富通支付宝H5响应内容,订单号:"+bean.getApplyid()+" ------>: " + responseBody);
		
		return responseBody;
	}
    
    public String getFormDataParamMD5(Map<String,String> dataMap,String securityKey){
        if(dataMap == null) return null;

        Set<String> keySet = dataMap.keySet();
        List<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList);

        StringBuilder toMD5StringBuilder = new StringBuilder();
        for(String key : keyList){
            String value = dataMap.get(key);

            if(value != null && value.length()>0){
                toMD5StringBuilder.append(key+"="+ value+"&");
            }
        }

        toMD5StringBuilder.setLength(toMD5StringBuilder.length()-1);
        try{
            String securityKeyMD5 = MD5Helper.sign(toMD5StringBuilder.toString(), securityKey);

            return securityKeyMD5;
        }catch (Exception ex){
            //ignore
            return "";
        }
    }
}
