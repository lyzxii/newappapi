package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IWeiXinRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
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
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.PayUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 威富通微信wap
 */
@Slf4j
@Component("Bank_swiftpass_wx_h5")
public class Bank_swiftpass_wx_h5 implements IWeiXinRech{

    private static final String charset = "UTF-8";
    //正式

    private static String defaultcallbackurl = PayConstant.NOTIFY_HOST+"/pay/swiftpass_wx_h5_notify.api";

    private static String defaultfrontbackurl = "https://5.9188.com/new/#/recharge/result";

    private static String targetUrl = "https://pay.swiftpass.cn/pay/gateway";

    @Autowired
    private NotifyService notifyService;

    @Override
    public RechDto addmoney(PayBean bean){
        log.info("威富通微信H5请求支付开始  订单号:"+bean.getApplyid()+"用户名:"+bean.getUid());
        String mchId = bean.getMerchantId();
        String mchKey = bean.getMerchantKey();
        String webcallbackurl =bean.getWebcallbackurl();
        if(StringUtil.isEmpty(webcallbackurl)){
            webcallbackurl = defaultfrontbackurl;
        }
        String result = createPayment(bean,webcallbackurl,mchId,mchKey);
        if(StringUtil.isEmpty(result)){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("发送支付请求至威富通微信H5失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid());
        }else{
            try{
                RechDto dto = handleRechargeSuc(bean, mchKey, result);
                if (dto != null) return dto;
            }catch (Exception e){
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.info("威富通微信H5充值异常,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid(),e);
            }
        }
        return null;
    }

    private RechDto handleRechargeSuc(PayBean bean, String mchKey, String result) throws Exception {
        JXmlWrapper xml = JXmlWrapper.parse(result);
        String status = xml.getStringValue("status");
        String resultCode = xml.getStringValue("result_code");
        if ("0".equals(status) && "0".equals(resultCode)) {
            Map<String, String> map = XmlUtil.toMap(result.getBytes(), "utf-8");
            String respSign = map.get("sign");
            map.remove("sign");
            String sign = PayUtil.makeSign(map,mchKey);
            if (respSign.equalsIgnoreCase(sign)) {
                String payUrl = xml.getStringValue("pay_info");
                RechDto dto = new RechDto();
                PayParam payParam=new PayParam();
                dto.setApplyid(bean.getApplyid());
                dto.setAddmoney(bean.getAddmoney());
                payParam.setPrepayUrl(URLEncoder.encode(payUrl, "UTF-8"));
                dto.setPayParam(payParam);
                log.info("威富通微信H5下单url:{}", payUrl);
                return dto;
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.info("威富通微信H5验签失败,原始签名respSign:" + respSign + "本地签名:" + sign + ",订单号:" + bean.getApplyid() + " 用户名:" + bean.getUid() + " 订单状态:" + status);
            }
        }else{
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("威富通微信H5充值失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
        }
        return null;
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }


    private String createPayment(PayBean bean, String webcallbackurl, String mchId, String mchKey) {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("version", "2.0");
        dataMap.put("charset", charset);
        dataMap.put("service", "pay.weixin.wappay");
        dataMap.put("sign_type", "MD5");
        dataMap.put("mch_id", mchId);
        dataMap.put("out_trade_no", bean.getApplyid());
        dataMap.put("body", "充值");
        int addmoney = (int)bean.getAddmoney()*100;//单位为分
		dataMap.put("total_fee", addmoney+"");//单位为分
        dataMap.put("mch_create_ip", bean.getClientIp());
        dataMap.put("nonce_str", String.valueOf(new Date().getTime()));
        dataMap.put("notify_url", defaultcallbackurl);
        dataMap.put("callback_url", webcallbackurl);
        if(bean.getMtype()==2){//IOS
            dataMap.put("device_info", "iOS_WAP");
        }else if(bean.getMtype()==1){//Android
            dataMap.put("device_info", "AND_WAP");
        }else{//H5
            dataMap.put("device_info", "iOS_WAP");
        }
        dataMap.put("mch_app_name", "9188官网");
        dataMap.put("mch_app_id", "http://5.9188.com");
        String signature = PayUtil.makeSign(dataMap, mchKey);
        dataMap.put("sign", signature.toUpperCase());
        try {
            String requsetData = XmlUtil.parseXML(dataMap);
            return sendToSwiftpass(requsetData,bean);
        } catch (Exception e) {
            log.info("请求充值至威富通微信H5失败,用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid());
        }
        return null;
    }

    private String sendToSwiftpass(String requestData, PayBean bean) throws IOException {
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
        log.info("威富通微信支付H5请求内容,订单号:"+bean.getApplyid()+" ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("威富通微信支付H5响应内容,订单号:"+bean.getApplyid()+" ------>: " + responseBody);

        return responseBody;
    }


}
