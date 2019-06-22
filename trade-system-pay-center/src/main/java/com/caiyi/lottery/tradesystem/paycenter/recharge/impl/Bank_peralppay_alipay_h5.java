package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IAlipayRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.HTTPSSecureProtocolSocketFactory;
import pay.util.PayUtil;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Component("Bank_peralppay_alipay_h5")
public class Bank_peralppay_alipay_h5 implements IAlipayRech {
    // 应用id
    private static String APP_ID = "e5d620ad535a7446";
    // 加密密钥
    private static String PP_MD5_KEY = "8a9d443761cd28565fc02f66e053d432";
    // 回调地址
    private static String defaultcallbackurl = PayConstant.NOTIFY_HOST + "/pay/plb_wx_notify.api";
    // 拉起支付api
    public static final String PP_PAY_EXCHANGE_URL = "https://api.peralppay.com/api/v1/pay_exchange";

    private static String defaultfrontbackurl = "https://5.9188.com/new/#/recharge/result";
    // 创建支付订单api
    private static String targetUrl = "https://api.peralppay.com/api/v1/create_order";
    private static String payType = "11";

    @Autowired
    private NotifyService notifyService;

    public static void main(String[] args) {
        Bank_peralppay_alipay_h5 pay = new Bank_peralppay_alipay_h5();
        PayBean bean = new PayBean();
        bean.setApplyid("18F4BC32");
        bean.setAddmoney(100);
        bean.setCardtype(0);
        bean.setBankCode("CMB");
        bean.setRealName("刘研擘");
        bean.setMobileNo("17602112430");
        bean.setIdcard("410311199105012511");
        bean.setCardNo("6230580000089121557");
        bean.setMerchantId("1481792479326986");
        bean.setMerchantKey("lidYFeLwhBc4RIqCl2SMMObtT9YgP5NE");
//        bean.setCvv("21/09");
        Logger log = LoggerFactory.getLogger("TEST");
        pay.addmoney(bean);
        //该付款方式已禁用 TODO
    }


    @Override
    public RechDto addmoney(PayBean bean) {
        String payUrl = "";
        RechDto rechDto = new RechDto();
        try {
            String resp = createPayment(bean);
            if (StringUtil.isEmpty(resp)) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败");
                log.error("发送请求到派洛贝支付失败,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid());
                return rechDto;
            }
            String payNo = saveConfirmId(bean, resp);
            if (StringUtil.isEmpty(payNo)) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("下单失败,请稍后重试");
                return rechDto;
            }
            payUrl = PP_PAY_EXCHANGE_URL + "?version=1&payNo=" + payNo + "&payType=" + payType;
            log.info("订单号" + bean.getApplyid() + "支付调起地址payUrl==" + payUrl);
            payUrl = URLEncoder.encode(payUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("订单号" + bean.getApplyid() + "URLEncoder出现错误--payUrl==>", e);
        } catch (Exception e) {
            log.error("订单号" + bean.getApplyid() + "订单创建失败--payUrl==>", e);
        }
        PayParam payParam=new PayParam();
        payParam.setPrepayUrl(payUrl);
        rechDto.setApplyid(bean.getApplyid());
        rechDto.setAddmoney(bean.getAddmoney());
        rechDto.setPayParam(payParam);
        return rechDto;
    }

    private String saveConfirmId(PayBean bean, String resp) {
        log.info("派洛贝创建用户[" + bean.getUid() + "]订单返回内容：" + resp);
        JSONObject json = JSON.parseObject(resp);
        String errMsg = json.getString("errMsg");
        if (json.containsKey("retCode") && json.getIntValue("retCode") != 0) {
            log.info("用户[" + bean.getUid() + "]失败原因==>" + errMsg);
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc(errMsg);
            log.error("发送请求到派洛贝支付失败,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid());
            return "";
        }

        // 得到派洛订单号
        String payNo = json.getJSONObject("data").getString("payNo");
        log.info("用户[" + bean.getUid() + "]订单号[" + bean.getApplyid() + "]下单返回订单流水号payNo==>" + payNo);
        bean.setDealid(payNo);
//        rechService.updateUserPayDealid(bean);
//        if (bean.getBusiErrCode() == 0) {
//            log.info("用户[" + bean.getUid() + "]派洛贝支付宝下单支付商号cconfirmid[" + bean.getDealid() + "]成功,订单号:" + bean.getApplyid());
//        } else {
//            log.info("用户[" + bean.getUid() + "]派洛贝支付宝下单支付商号cconfirmid[" + bean.getDealid() + "]出错,订单号:" + bean.getApplyid() + "原因：" + bean.getBusiErrDesc());
//        }
        return payNo;
    }

    private String createPayment(PayBean bean) throws Exception {
        log.info("派洛贝请求支付开始  订单号:" + bean.getApplyid() + "用户名:" + bean.getUid());
        Map<String, String> requestData = createParamData(bean);
        String resp = sendToPerlPay(requestData, bean);
        return resp;
    }

    private static String sendToPerlPay(Map<String, String> dataMap, PayBean bean) throws Exception {
        Protocol https = new Protocol("https", new HTTPSSecureProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", https);
        HttpClient client = new HttpClient();

        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        PostMethod postMethod = null;
        postMethod = new PostMethod(targetUrl);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        for (String key : dataMap.keySet()) {
            String data = dataMap.get(key);
            postMethod.addParameter(key, data);
        }

        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String requestBody = baos.toString();
        log.info("派洛贝支付支付请求内容,订单号:" + bean.getApplyid() + " ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("派洛贝支付支付响应内容,订单号:" + bean.getApplyid() + " ------>: " + responseBody);
        Protocol.unregisterProtocol("https");
        return responseBody;
    }

    private Map<String, String> createParamData(PayBean bean) throws Exception {
        Map<String, String> merchantMap = PayUtil.getMerchantInfo(bean, APP_ID, PP_MD5_KEY, "alipay");
        String mchId = merchantMap.get("mchId");
        String mchKey = merchantMap.get("mchKey");

        Map<String, String> paramsMap = new TreeMap<String, String>();
        makeParamData(bean, paramsMap, mchId, mchKey);
        return paramsMap;
    }

    private void makeParamData(PayBean bean, Map<String, String> paramsMap, String mchId, String mchKey) throws Exception {
        // 交易的超时时间,当前时间加2天
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 2);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String mDateTime = formatter.format(c.getTime());
        String strExpire = mDateTime.substring(0, 14);
        String expireTime = strExpire;

        String outTradeNo = bean.getApplyid();
        String payAmount = String.valueOf((int) bean.getAddmoney() * 100);// 单位为分
        paramsMap.put("version", "1");// 版本号，目前固定1
        paramsMap.put("appId", mchId);// 应用id
        paramsMap.put("outTradeNo", outTradeNo);//
        paramsMap.put("payType", payType);// 支付类型:支付宝H5版
        paramsMap.put("payAmount", payAmount);
        paramsMap.put("spUno", "cy" + bean.getUid());// 可选
        paramsMap.put("subject", "充值");
        paramsMap.put("notifyUrl", defaultcallbackurl);
        paramsMap.put("signType", "md5");// 签名方式，md5
        String webcallbackurl = bean.getWebcallbackurl();
        if (StringUtil.isEmpty(webcallbackurl)) {
            webcallbackurl = defaultfrontbackurl;
        }
        paramsMap.put("returnUrl", webcallbackurl);
        paramsMap.put("expireTime", expireTime);// 可选

        String sign = PayUtil.getMd5WithKey(paramsMap, mchKey);
        paramsMap.put("sign", sign);
        log.info("请求参数==paramsMap==" + paramsMap.toString());
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }
}
