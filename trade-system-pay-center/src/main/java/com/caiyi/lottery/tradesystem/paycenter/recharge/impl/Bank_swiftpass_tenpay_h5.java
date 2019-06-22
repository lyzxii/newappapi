package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.ITenpayRech;
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
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.PayUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static pay.constant.PayConstant.NOTIFY_HOST;

/**
 * 威富通  手机QQ充值
 */
@Slf4j
@Component("Bank_swiftpass_tenpay_h5")
public class Bank_swiftpass_tenpay_h5 implements ITenpayRech {

    @Autowired
    private NotifyService notifyService;

    private static final String charset = "UTF-8";
    //正式
    private static String defaultcallbackurl = NOTIFY_HOST+"/user/swiftpass_tenpay_notify.go";
    private static String defaultfrontbackurl = "https://5.9188.com/new/#/recharge/result";
    private static String targetUrl = "https://pay.swiftpass.cn/pay/gateway";

    @Override
    public RechDto addmoney(PayBean bean) {
        log.info("威富通QQ支付H5请求支付开始  订单号:"+bean.getApplyid()+"用户名:"+bean.getUid());
        //Map<String, String> merchantMap = PayUtil.getMerchantInfo(bean,mch_id,mch_key,"tenpay");
        String mchId = bean.getMerchantId();
        String mchKey =bean.getMerchantKey();
        String webcallbackurl =bean.getWebcallbackurl();
        if(StringUtil.isEmpty(webcallbackurl)){
            webcallbackurl = defaultfrontbackurl;
        }
        String result = createPayment(bean,webcallbackurl,mchId,mchKey);
        if(StringUtil.isEmpty(result)){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("发送支付请求至威富通QQ支付H5失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid());
        }else{
            try{
                JXmlWrapper xml = JXmlWrapper.parse(result);
                String status = xml.getStringValue("status");
                String resultCode = xml.getStringValue("result_code");
                if("0".equals(status)&&"0".equals(resultCode)){
                    Map<String,String> map = XmlUtil.toMap(result.getBytes(), "utf-8");
                    String respSign = map.get("sign");
                    map.remove("sign");
                    String sign = PayUtil.makeSign(map, "&key="+mchKey);
                    if(respSign.equalsIgnoreCase(sign)){
                        RechDto dto=new RechDto();
                        PayParam payParam=new PayParam();
                        payParam.setPrepayUrl(xml.getStringValue("pay_info"));
                        dto.setPayParam(payParam);
                        dto.setApplyid(bean.getApplyid());
                        dto.setAddmoney(bean.getAddmoney());
                        return dto;
                    }else{
                        bean.setBusiErrCode(-1);
                        bean.setBusiErrDesc("支付请求失败，请重新尝试");
                        log.info("威富通QQ支付H5验签失败,原始签名respSign:"+respSign+"本地签名:"+sign+",订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
                    }
                }else{
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("支付请求失败，请重新尝试");
                    log.info("威富通QQ支付H5充值失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
                }
            }catch (Exception e){
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.info("威富通QQ支付H5充值异常,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid());
            }

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
        dataMap.put("service", "pay.tenpay.wappay");
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
        String signature = PayUtil.makeSign(dataMap, mchKey);
        dataMap.put("sign", signature.toUpperCase());
        try {
            String requsetData = XmlUtil.parseXML(dataMap);
            String result = sendToSwiftpass(requsetData,bean);
            return result;
        } catch (Exception e) {
            log.info("请求充值至威富通QQ支付H5失败,用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid());
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
        log.info("威富通QQ支付H5支付请求内容,订单号:"+bean.getApplyid()+" ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("威富通QQ支付H5响应内容,订单号:"+bean.getApplyid()+" ------>: " + responseBody);

        return responseBody;
    }
}
