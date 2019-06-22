package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("Bank_ipaynow")
public class Bank_ipaynow implements IAlipayRech {

    @Autowired
    NotifyService notifyService;

    private static final String mch_id = "1481792479326986";
    private static final String mch_key = "lidYFeLwhBc4RIqCl2SMMObtT9YgP5NE";

    private static String charset = "UTF-8";
    private static final String funcode = "WP001";
    private static String defaultfrontbackurl = "https://5.9188.com/new/#/recharge/result";
    private static String defaultcallbackurl = PayConstant.NOTIFY_HOST + "/pay/web_ipaynow_notify.api";
    private static String targetUrl = "https://pay.ipaynow.cn";

    private static Map<String, String> statusMap = new HashMap<String, String>();

    static {
        statusMap.put("A00I", "订单未处理");
        statusMap.put("A001", "订单支付成功");
        statusMap.put("A002", "订单支付失败");
        statusMap.put("A003", "支付结果未知");
        statusMap.put("A004", "订单受理成功");
        statusMap.put("A005", "订单受理失败");
        statusMap.put("A006", "交易关闭");
    }

    public static void main(String[] args) {
        Bank_ipaynow pay = new Bank_ipaynow();
        PayBean bean = new PayBean();
        bean.setApplyid("13F4BA332");
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
//2018/1/2被风控，回调无法测试
    }

    @Override
    public RechDto addmoney(PayBean bean) {
        RechDto rechDto = new RechDto();
        String html = createPayment(bean);
        if (StringUtil.isEmpty(html)) {
            bean.setBusiErrCode(-2);
            bean.setBusiErrDesc("支付请求失败,请稍后重试");
        } else {
            Map<String, String> dataMap = PayUtil.parseFormDataByDecode(html, "UTF-8", "UTF-8");
            //去除签名类型和签名值
            String signature = dataMap.remove("signature");
            //验证签名
            String mhtSignature = PayUtil.getFormDataParamMD5(dataMap, bean.getMerchantKey(), charset);
            boolean isValidSignature = mhtSignature.equalsIgnoreCase(signature);
            log.info("请求支付response,订单号:" + bean.getApplyid() + "验签结果：" + isValidSignature);
            if (!isValidSignature) {
                bean.setBusiErrCode(-2);
                bean.setBusiErrDesc("支付请求失败,请稍后重试");
                return rechDto;
            }
            String payUrl = parseHtml(html, bean);
            log.info("payUrl---" + payUrl);
            rechDto.setApplyid(bean.getApplyid());
            PayParam payParam=new PayParam();
            payParam.setPrepayUrl(payUrl);
            rechDto.setPayParam(payParam);
            //rechDto.setPayUrl(payUrl);
        }
        return rechDto;
    }

    //解析response内容,获取tn值
    private static String parseHtml(String html, PayBean bean) {
        int mType = bean.getMtype();
        String[] rem = html.split("&");
        String res = "";
        for (int i = 0; i < rem.length; i++) {
            if (rem[i].startsWith("tn")) {
                res = rem[i].substring(3);
            }
        }
        try {
            if (2 == mType) { // ios 对 tn 再处理
                String[] iosTn = URLDecoder.decode(res, "UTF-8").split("&");
                String bizContent = "biz_content=";
                for (int i = 0; i < iosTn.length; i++) {
                    if (iosTn[i].startsWith(bizContent)) {
                        String dec = iosTn[i].substring(bizContent.length());
                        String enc = URLDecoder.decode(dec, "UTF-8");
                        iosTn[i] = bizContent + enc;
                        break;
                    }
                }
                // 取出biz_content,对其encode并拼接url
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < iosTn.length; i++) {
                    String str = "&";
                    if (i == (iosTn.length - 1))
                        str = "";
                    builder.append(iosTn[i]).append(str);
                }
                res = URLEncoder.encode(builder.toString(), "UTF-8");
                log.info("现在支付ios返回url---" + res+" applyid:"+bean.getApplyid());
            }
        } catch (UnsupportedEncodingException e) {
            log.error("现在支付解析Html异常,applyid:"+bean.getApplyid(),e);
        }
        return res;
    }

    private String createPayment(PayBean bean) {
        Map<String, String> dataMap = new HashMap<>();
        String html = "";
        try {
            String mhtSignature = createParamData(bean, dataMap);
            html = sendToIpaynow(dataMap, bean, mhtSignature);
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败");
            log.error("发送请求到现在支付失败,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid(), e);
        }
        return html;
    }

    private static String sendToIpaynow(Map<String, String> dataMap, PayBean bean, String mhtSignature) throws Exception {
        Protocol https = new Protocol("https", new HTTPSSecureProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", https);
        HttpClient client = new HttpClient();

        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        PostMethod postMethod = null;
        postMethod = new PostMethod(targetUrl);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        postMethod.addParameter("mhtSignature", mhtSignature);
        for (String key : dataMap.keySet()) {
            String data = dataMap.get(key);
            postMethod.addParameter(key, data);
        }

        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String requestBody = baos.toString();
        log.info("现在支付支付请求内容,订单号:" + bean.getApplyid() + " ------>: " + requestBody);

        client.executeMethod(postMethod);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("现在支付支付响应内容,订单号:" + bean.getApplyid() + " ------>: " + responseBody);
        Protocol.unregisterProtocol("https");
        return responseBody;
    }

    private String createParamData(PayBean bean, Map<String, String> dataMap) {
        //做MD5签名
        dataMap.put("version", "1.0.0");//功能码
        dataMap.put("appId", getMerId(bean));
        dataMap.put("mhtOrderNo", bean.getApplyid());
        dataMap.put("mhtOrderName", "充值");
        dataMap.put("mhtCurrencyType", "156");
        int addmoney = (int) bean.getAddmoney() * 100;//单位为分
        dataMap.put("mhtOrderAmt", addmoney + "");//单位为分
        dataMap.put("mhtOrderDetail", "充值" + bean.getAddmoney());
        dataMap.put("mhtOrderType", "01");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dataMap.put("mhtOrderStartTime", dateFormat.format(new Date()));
        String webcallbackurl = bean.getWebcallbackurl();
        log.info("充值成功后的返回页面:" + webcallbackurl);
        if (StringUtil.isEmpty(webcallbackurl)) {
            webcallbackurl = defaultfrontbackurl;
        }
        dataMap.put("notifyUrl", defaultcallbackurl);
        dataMap.put("frontNotifyUrl", webcallbackurl);
        dataMap.put("mhtCharset", charset);
        //待定
        dataMap.put("payChannelType", "12");//用户所选渠道类型，银联：11  支付宝：12  微信： 13  手Q：25
        //商户保留域， 可以不用填。 如果商户有需要对每笔交易记录一些自己的东西，可以放在这个里面
        dataMap.put("mhtReserved", "");
        //待定
        dataMap.put("outputType", "1");//输出格式
        dataMap.put("mhtSignType", "MD5");
        dataMap.put("funcode", funcode);
        dataMap.put("deviceType", "0601");//手机网页
        String mhtSignature = PayUtil.getFormDataParamMD5(dataMap, bean.getMerchantKey(), charset);
        return mhtSignature;
    }

    private String getMerId(PayBean bean) {
        boolean flag = StringUtil.isEmpty(bean.getMerchantId());
        if (flag) {
            bean.setMerchantKey(mch_key);
            bean.setMerchantId(mch_id);
            return mch_id;
        }
        return bean.getMerchantId();
    }

//    @Override
//    public RechDto agreePay(PayBean bean) {
//        return null;
//    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }
}
