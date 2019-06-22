package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IWeiXinRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
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
@Component("bank_plbpay_wx")
public class Bank_plbpay_wx implements IWeiXinRech{

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private RechService rechService;

    // 正式id
    private static final String APP_ID = "e5d620ad535a7446";
    // 正式key
    private static final String PP_MD5_KEY = "8a9d443761cd28565fc02f66e053d432";

    // 下单url
    private static final String ORDER_URL = "https://api.peralppay.com/api/v1/create_order";

    // 回调通知url
    private static final String notify_url = "http://t2015.9188.com/user/Bank_plbpay_wx.go";

    // 拉起支付url
    private static final String pullpay_url = "https://api.peralppay.com/api/v1/pay_exchange?version=1&payType=21&payNo=#";

    //跳转url
    private static String defaultfrontbackurl = "https://5.9188.com/new/#/recharge/result";


    @Override
    public RechDto addmoney(PayBean bean) {
        log.info("派洛贝微信支付,请求支付开始  订单号:{},用户名:{}", new Object[] { bean.getApplyid(), bean.getUid() });
        String webcallbackurl =bean.getWebcallbackurl();
        if(StringUtil.isEmpty(webcallbackurl)){
            webcallbackurl = defaultfrontbackurl;
        }
        String clientIp=bean.getClientIp();
        String result = createPayment(bean, clientIp,webcallbackurl);
        log.info("返回结果:" + result);
        if (StringUtil.isEmpty(result)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("发送支付请求至派洛贝微信支付,订单号:{},用户名:{}", new Object[] { bean.getApplyid(), bean.getUid() });
        } else {
            try {
                JSONObject json = JSONObject.parseObject(result);
                if (json.containsKey("retCode")) {
                    RechDto dto = handleRequestSuc(bean ,json);
                    if (dto != null) return dto;
                } else {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("支付请求失败，请重新尝试");
                    log.info("发送支付请求至派洛贝微信支付失败,订单号:{},用户名:{}", new Object[] { bean.getApplyid(), bean.getUid() });
                }
            } catch (Exception e) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.error("派洛贝微信支付：处理支付响应失败,订单号:{},用户名:{}", bean.getApplyid(), bean.getUid(),e);
            }
        }
        return null;
    }

    private RechDto handleRequestSuc(PayBean bean, JSONObject json) throws UnsupportedEncodingException {
        int retCode = json.getIntValue("retCode");
        if (retCode == 0) {
            JSONObject data = (JSONObject) json.get("data");
            String payNo=data.getString("payNo");
            log.info("派洛贝微信支付,本地生成单号:{},第三方返回单号:{}",new Object[]{bean.getApplyid(),payNo});
            bean.setDealid(payNo);
            rechService.updateUserPayDealid(bean);
            if(bean.getBusiErrCode()==0){
                RechDto dto=new RechDto();
                PayParam payParam=new PayParam();
                String payUrl = pullpay_url.replace("#", data.getString("payNo"));
                dto.setApplyid(bean.getApplyid());
                dto.setAddmoney(bean.getAddmoney());
                payParam.setPrepayUrl(URLEncoder.encode(payUrl, "UTF-8"));
                dto.setPayParam(payParam);
                return dto;
            }else{
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.info("派洛贝微信支付更新三方支付订单号失败,订单号:{},用户名:{}", new Object[] { bean.getApplyid(), bean.getUid() });
            }
        } else {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.info("派洛贝微信支付显示请求失败,订单号:{},用户名:{}", new Object[] { bean.getApplyid(), bean.getUid() });
        }
        return null;
    }

    private String createPayment(PayBean bean, String clientIp, String webcallbackurl) {
        Map<String, String> paramsMap = new TreeMap<String, String>();
        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 2);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String mDateTime = formatter.format(c.getTime());
            String strExpire = mDateTime.substring(0, 14);
            // 充值金额
            String addmoney = (int) (bean.getAddmoney() * 100) + "";
            // 必要参数
            paramsMap.put("version", "1");// 版本号，目前固定1
            paramsMap.put("subject", "9188充值");
            paramsMap.put("outTradeNo", bean.getApplyid());//
            paramsMap.put("payAmount", addmoney);
            paramsMap.put("signType", "md5");// 签名方式，md5
            paramsMap.put("appId", APP_ID);// 应用id
            // 可选参数
            paramsMap.put("returnUrl", webcallbackurl);
            paramsMap.put("subjectDesc", "9188充值");
            paramsMap.put("expireTime", strExpire);
            paramsMap.put("privateInfo", "9188");
            paramsMap.put("notifyUrl", notify_url);
            paramsMap.put("spUno", "9188_" + bean.getUid());
            paramsMap.put("clientIp", clientIp);
            paramsMap.put("payType", "21");// 支付类型
            String sign = PayUtil.makeSign(paramsMap,PP_MD5_KEY);
            if (StringUtil.isEmpty(sign)) {
                log.error("派洛贝微信支付,用户:{},订单号:{},产生签名错误", new Object[] { bean.getUid(), bean.getApplyid() });
                return null;
            }
            paramsMap.put("sign", sign);
            return sendOder(paramsMap,bean);
        } catch (Exception e) {
            log.error("派洛贝微信支付,用户:{},订单号:{},创建订单错误", bean.getUid(), bean.getApplyid(),e);
        }
        return null;
    }

    private String sendOder(Map<String, String> dataMap, PayBean bean) throws Exception {
        Protocol https = new Protocol("https", new HTTPSSecureProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", https);
        HttpClient client = new HttpClient();

        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        PostMethod postMethod = new PostMethod(ORDER_URL);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        for (String key : dataMap.keySet()) {
            String data = dataMap.get(key);
            postMethod.addParameter(key, data);
        }

        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String requestBody = baos.toString();
        log.info("派洛贝微信支付请求内容,订单号:" + bean.getApplyid() + " ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("派洛贝微信支付响应内容,订单号:" + bean.getApplyid() + " ------>: " + responseBody);
        Protocol.unregisterProtocol("https");
        return responseBody;
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }

}
