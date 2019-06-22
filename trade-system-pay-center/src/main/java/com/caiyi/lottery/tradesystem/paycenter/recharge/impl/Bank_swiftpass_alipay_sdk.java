package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSON;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IAlipayRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.PayUtil;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 威富通支付宝SDK
 */
@Slf4j
@Component("Bank_swiftpass_alipay_sdk")
public class Bank_swiftpass_alipay_sdk implements IAlipayRech {

    @Autowired
    NotifyService notifyService;

    private static final String charset = "UTF-8";
    //威富通正式
    private static final String mch_id = "105590063681";//测试商户号
    private static final String mch_key = "b962c8d61aa85b78f022851708c6a011";//测试密钥

    private static String defaultcallbackurl = PayConstant.NOTIFY_HOST + "/pay/swiftpass_alipay_sdk_notify.api";
    private static String targetUrl = "https://pay.swiftpass.cn/pay/gateway";
    private static String backurl = "http://t2015.9188.com/user/mlottery.go";

    public static void main(String[] args) {
        Bank_swiftpass_alipay_sdk pay = new Bank_swiftpass_alipay_sdk();
        PayBean bean = new PayBean();
        bean.setApplyid("17BA494C73");
        bean.setAddmoney(100);
        bean.setUid("lyb123");
        bean.setCardtype(0);
        bean.setBankCode("CMB");
        bean.setRealName("刘研擘");
        bean.setMobileNo("17602112430");
        bean.setIdcard("410311199105012511");
        bean.setClientIp("116.231.55.171");
        bean.setCardNo("6214852115958996");
        bean.setCvv("21/09");
        Logger log = LoggerFactory.getLogger("TEST");
        bean.setUserid("f1fd9e5f-88ba-431e-8612-4fe5bfd0f517");
        pay.addmoney(bean);
        bean.setVerifycode("912858");
        bean.setTradeNo("3801031118758203");
//        pay.agreePay(bean);
    }

    @Override
    public RechDto addmoney(PayBean bean) {
        log.info("盛付通支付宝sdk-->订单号:" + bean.getApplyid() + "用户名:" + bean.getUid());
        RechDto rechDto = new RechDto();
        try {
            String result = createPayment(bean, log);
            if (StringUtil.isEmpty(result)) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.info("发送支付请求至威富通支付宝sdk失败,订单号:" + bean.getApplyid() + " 用户名:" + bean.getUid());
            } else {
                JXmlWrapper xml = JXmlWrapper.parse(result);
                String status = xml.getStringValue("status");
                if ("0".equals(status)) {
                    Map<String, String> dataMap = XmlUtil.toMap(result.getBytes(), "utf-8");
                    String respSign = dataMap.remove("sign");
                    String sign = PayUtil.getMd5WithKey(dataMap, bean.getMerchantKey());
                    if (respSign.equalsIgnoreCase(sign)) {
                        int addmoney = (int) bean.getAddmoney() * 100;//单位为分
                        rechDto.setAddmoney(addmoney);
                        rechDto.setApplyid(bean.getApplyid());
                        PayParam payParam = new PayParam();
                        Map<String, String> map = new HashMap<>();
                        map.put("tokenId", xml.getStringValue("token_id"));
                        map.put("services", xml.getStringValue("services"));
                        map.put("sign", xml.getStringValue("sign"));
                        map.put("callBackUrl", backurl);
                        payParam.setPrepayContent(map);
                        rechDto.setPayParam(payParam);
                        log.info("威富通支付宝sdk-->验签成功,原始签名respSign:" + respSign + "本地签名:" + sign + ",订单号:" + bean.getApplyid() + " 用户名:" + bean.getUid() + " 订单状态:" + status);
                    } else {
                        bean.setBusiErrCode(-1);
                        bean.setBusiErrDesc("支付请求失败，请重新尝试");
                        log.info("威富通支付宝sdk-->验签失败,原始签名respSign:" + respSign + "本地签名:" + sign + ",订单号:" + bean.getApplyid() + " 用户名:" + bean.getUid() + " 订单状态:" + status);
                    }
                } else {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("支付请求失败，请重新尝试");
                    log.info("威富通支付宝sdk-->充值失败,订单号:" + bean.getApplyid() + " 用户名:" + bean.getUid() + " 订单状态:" + status + " 返回信息:" + xml.getStringValue("message"));
                }
            }
        } catch (Exception e) {
            log.error("威富通支付宝sdk-->充值失败,,applyid==" + bean.getApplyid() + ",失败", e);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("支付请求失败");
        }
        log.info("威富通支付宝sdk-->返回rechDto：" + JSON.toJSONString(rechDto));
        return rechDto;
    }

    private String createPayment(PayBean bean, Logger log) throws Exception {
        Map<String, String> merchantMap = PayUtil.getMerchantInfo(bean, mch_id, mch_key, "alipay");
        String mchId = merchantMap.get("mchId");
        String mchKey = merchantMap.get("mchKey");
        bean.setMerchantKey(mchKey);
        log.info("盛付通支付宝sdk-->订单号:" + bean.getApplyid() + "用户名:" + bean.getUid() + "mchid==" + mchId + ",mchKey==" + mchKey);
        String requestData = createDataMap(bean, mchId, mchKey);
        return sendToSwiftpass(bean, requestData, log);
    }

    private String createDataMap(PayBean bean, String mchId, String mchKey) throws Exception {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("version", "2.0");
        dataMap.put("charset", charset);
        dataMap.put("service", "unified.trade.pay");
        dataMap.put("sign_type", "MD5");
        dataMap.put("mch_id", mchId);
        dataMap.put("out_trade_no", bean.getApplyid());
        dataMap.put("body", "充值");
        int addmoney = (int) bean.getAddmoney() * 100;//单位为分
        dataMap.put("total_fee", addmoney + "");//单位为分
        dataMap.put("mch_create_ip", bean.getClientIp());
        dataMap.put("nonce_str", String.valueOf(new Date().getTime()));
        dataMap.put("notify_url", defaultcallbackurl);
        String signature = PayUtil.getMd5WithKey(dataMap, mchKey);
        dataMap.put("sign", signature.toUpperCase());
        String requsetData = XmlUtil.parseXML(dataMap);
        return requsetData;
    }

    private static String sendToSwiftpass(PayBean bean, String requestData, Logger log) throws Exception {
        HttpClient client = new HttpClient();

        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        PostMethod postMethod = null;
        postMethod = new PostMethod(targetUrl);
        postMethod.setRequestHeader("Content-Type", "text/xml;charset=UTF-8");
        postMethod.setRequestEntity(new StringRequestEntity(requestData,"text/xml","UTF-8"));

        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String requestBody = baos.toString();
        log.info("威富通支付宝sdk支付请求内容,订单号:" + bean.getApplyid() + " ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("威富通支付支付宝sdk响应内容,订单号:" + bean.getApplyid() + " ------>: " + responseBody);

        return responseBody;
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }
}
