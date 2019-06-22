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
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.PayUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static pay.constant.PayConstant.NOTIFY_HOST;

@Slf4j
@Component("Bank_swiftpass_wx_sdk")
public class Bank_swiftpass_wx_sdk implements IWeiXinRech{

    @Autowired
    private NotifyService notifyService;

    private static final String charset = "UTF-8";

    private static String defaultcallbackurl = NOTIFY_HOST+"/pay/swiftpass_wx_sdk_notify.api";
    private static String targetUrl = "https://pay.swiftpass.cn/pay/gateway";

    @Override
    public RechDto addmoney(PayBean bean) {
        log.info("威富通微信sdk请求支付开始  订单号:"+bean.getApplyid()+"用户名:"+bean.getUid());
        String mchId = bean.getMerchantId();
        String mchKey = bean.getMerchantKey();
        String appId = bean.getRechargeAppid();
        String result = createPayment(bean,mchId,mchKey);
        if(StringUtil.isEmpty(result)){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("发送支付请求至威富通微信sdk失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid());
        }else{
            JXmlWrapper xml = JXmlWrapper.parse(result);
            String status = xml.getStringValue("status");
            if("0".equals(status)){
                try{
                    RechDto dto = handleChargeSuc(bean, mchKey, appId, result, xml, status);
                    if (dto != null) return dto;
                }catch (Exception e){
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("支付请求失败，请重新尝试");
                    log.info("威富通微信sdk充值异常,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status+" 返回信息:"+xml.getStringValue("message"));
                }

            }else{
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.info("威富通微信sdk充值失败,订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status+" 返回信息:"+xml.getStringValue("message"));
            }
        }
        return null;
    }

    private RechDto handleChargeSuc(PayBean bean, String mchKey, String appId, String result, JXmlWrapper xml, String status) throws Exception {
        Map<String,String> map = XmlUtil.toMap(result.getBytes(), "utf-8");
        String respSign = map.get("sign");
        map.remove("sign");
        String sign = PayUtil.makeSign(map, mchKey);
        if(respSign.equalsIgnoreCase(sign)){
            RechDto dto=new RechDto();
            PayParam payParam=new PayParam();
            dto.setApplyid(bean.getApplyid());
            int addmoney = (int)bean.getAddmoney()*100;//单位为分
            dto.setAddmoney(bean.getAddmoney());//单位为元
            Map<String,String> paramMap=new HashMap<>();
            paramMap.put("tokenId",xml.getStringValue("token_id"));
            paramMap.put("services",xml.getStringValue("services"));
            paramMap.put("sign",xml.getStringValue("sign"));
            paramMap.put("amount",addmoney+"");
            paramMap.put("appid",appId);
            payParam.setPrepayContent(paramMap);
            dto.setPayParam(payParam);
            log.info("威富通微信sdk验签成功,原始签名respSign:"+respSign+"本地签名:"+sign+",订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
            return dto;
        }else{
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("威富通微信sdk验签失败,原始签名respSign:"+respSign+"本地签名:"+sign+",订单号:"+bean.getApplyid()+" 用户名:"+bean.getUid()+" 订单状态:"+status);
        }
        return null;
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }

    private String createPayment(PayBean bean, String mchId, String mchKey) {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("version", "2.0");
        dataMap.put("charset", charset);
        dataMap.put("service", "unified.trade.pay");
        dataMap.put("sign_type", "MD5");
        dataMap.put("mch_id", mchId);
        dataMap.put("out_trade_no", bean.getApplyid());
        dataMap.put("body", "充值");
        int addmoney = (int)bean.getAddmoney()*100;//单位为分
		dataMap.put("total_fee",addmoney+"");//单位为分
        dataMap.put("mch_create_ip",bean.getClientIp());
        dataMap.put("nonce_str", String.valueOf(new Date().getTime()));
        dataMap.put("notify_url", defaultcallbackurl);
        String signature= PayUtil.makeSign(dataMap, mchKey);
        dataMap.put("sign", signature.toUpperCase());
        try {
           String requsetData = XmlUtil.parseXML(dataMap);
           log.info("requestData------>{}",requsetData);
           return sendToSwiftpass(requsetData,bean);
        } catch (Exception e) {
            log.info("请求充值至威富通微信sdk失败,用户名:"+bean.getUid()+" 订单号:"+bean.getApplyid());
        }
        return null;
    }

    private String sendToSwiftpass(String requestData, PayBean bean) throws IOException {
        HttpClient client = new HttpClient();

        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        PostMethod postMethod  = new PostMethod(targetUrl);
        postMethod.setRequestHeader("Content-Type","text/xml;charset=UTF-8");
        postMethod.setRequestEntity(new StringRequestEntity(requestData,"text/xml","UTF-8"));

        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String requestBody = baos.toString();
        log.info("威富通微信sdk支付请求内容,订单号:"+bean.getApplyid()+" ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("威富通支付微信sdk响应内容,订单号:"+bean.getApplyid()+" ------>: " + responseBody);

        return responseBody;
    }
}
