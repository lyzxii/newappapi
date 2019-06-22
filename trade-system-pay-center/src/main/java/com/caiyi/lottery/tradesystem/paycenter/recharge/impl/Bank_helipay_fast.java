package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.dto.RechDto;
import pay.pojo.PayParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static pay.constant.PayConstant.NOTIFY_HOST;


@Slf4j
@Component("Bank_helipay_fast")
public class Bank_helipay_fast implements IBankCardRech{

    private static final String pay_url = "http://pay.trx.helipay.com/trx/quickPayApi/interface.action";
    //TODO
    private static final String P23_serverCallbackUrl = NOTIFY_HOST + "/pay/bank_helipay_notify.api";
    private static final String MD5_KEY = "ByNvtb4QGYtu0dTc1gSLKCXksiYB4acy";// md5秘钥
    private static final String P2_customerNumber = "C1800001823"; // 商户编号
    private static final String P7_idCardType = "IDCARD";
    private static final String P14_currency = "CNY";

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private RechService rechService;


    //错误码与后续类型的映射
    private static final Map<String, Integer> ErrorToPageMap = new HashMap<String, Integer>();
    static{
        //10-留在当前页面
        ErrorToPageMap.put("8009", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        ErrorToPageMap.put("8015", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        //20-错误提示,不重新支付
        //30-重新支付
    }

    @Override
    @SuppressWarnings("unchecked")
    public RechDto addmoney(PayBean bean) {
        try {
            if(StringUtil.isEmpty(bean.getMerchantKey())){
                bean.setMerchantKey(MD5_KEY);
            }
            if(StringUtil.isEmpty(bean.getMerchantId())){
                bean.setMerchantId(P2_customerNumber);
            }
            String rect = sendOrder(bean);
            if (rect == null) {
                log.info("合利宝银行卡快捷支付,下单失败，用户名:{},订单号:{}", bean.getUid(), bean.getApplyid());
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                return null;
            }
            log.info("合利宝银行卡快捷支付,用户名:{},下单返回内容:{}", bean.getUid(),rect);
            Map<String, String> paramsMap = JSONObject.parseObject(rect, HashMap.class);
            String returnSign=paramsMap.get("sign");
            if ("0000".equals(paramsMap.get("rt2_retCode"))) {// 请求ok
                String signstr = getSigned(paramsMap,null,"t", bean);
                String lockstr = DigestUtils.md5Hex(signstr.getBytes("UTF-8"));// 本地签名
                if (lockstr.equals(returnSign)){// 签名ok
                    RechDto dto=new RechDto();
                    dto.setApplyid(bean.getApplyid());
                    dto.setAddmoney(bean.getAddmoney());
                    dto.setPayParam(new PayParam());
                    sendSms(bean);// 发短信
                    return dto;
                }else{
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("支付请求失败，请重新尝试");
                    log.error("合利宝银行卡快捷支付：验签错误,订单号:{},用户名:{}",bean.getApplyid(), bean.getUid());
                }
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("支付请求失败，请重新尝试");
                log.error("合利宝银行卡快捷支付：支付请求失败,订单号:{},用户名:{}", bean.getApplyid(), bean.getUid());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("支付请求失败，请重新尝试");
            log.error("合利宝银行卡快捷支付：支付请求异常,订单号:{},用户名:{}", bean.getApplyid(), bean.getUid(),e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void sendSms(PayBean bean) throws UnsupportedEncodingException {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("P1_bizType", "QuickPaySendValidateCode");
        paraMap.put("P2_customerNumber",bean.getMerchantId());
        paraMap.put("P3_orderId", bean.getApplyid());
        paraMap.put("P4_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        paraMap.put("P5_phone", bean.getMobileNo());
        paraMap.put("P6_smsSignature","");
        String signstr = getSigned(paraMap, new String[] {"P6_smsSignature"},"P", bean);// 短信请求明文
        String sign = DigestUtils.md5Hex(signstr.getBytes("UTF-8"));
        paraMap.put("sign", sign);
        String rect = getHttpResp(paraMap, pay_url);
        if (rect == null) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请求短信验证码错误,请重新尝试");
            return;
        }
        Map<String, String> paramsMap = JSONObject.parseObject(rect, HashMap.class);
        String returnSign=paramsMap.get("sign");
        if ("0000".equals(paramsMap.get("rt2_retCode"))) {// 请求ok
            String str = getSigned(paramsMap, null,"t", bean);
            String localSign= DigestUtils.md5Hex(str.getBytes("UTF-8"));// 本地签名
            if (localSign.equals(returnSign)) {// 签名ok
                log.info("合利宝银行卡快捷支付,用户:{},订单号:{},手机号:{}请求支付短信成功",
                        bean.getUid(), bean.getApplyid(), bean.getMobileNo());
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("请求短信验证码错误,请重新尝试");
            }
        } else {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请求短信验证码错误,请重新尝试");
        }
    }

    private String sendOrder(PayBean bean) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        map.put("P1_bizType", "QuickPayCreateOrder");
        map.put("P2_customerNumber",bean.getMerchantId());
        map.put("P3_userId", bean.getUserid());
        map.put("P4_orderId", bean.getApplyid());
        map.put("P5_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        map.put("P6_payerName", bean.getRealName());// 真实姓名
        map.put("P7_idCardType", P7_idCardType);
        map.put("P8_idCardNo", bean.getIdcard());// 身份证号
        map.put("P9_cardNo", bean.getCardNo());// 银行卡号
        if (bean.getCardtype() == 1) {// 信用卡
            String validdate = bean.getValidDate();
            log.info("合利宝银行卡快捷支付信用卡，用户名:{},validdate:{}",bean.getUid(),validdate);
            if(validdate.contains("/")){
                map.put("P10_year",validdate.split("/")[0]);
                map.put("P11_month",validdate.split("/")[1]);
            }else{
                map.put("P10_year",validdate.substring(0, 2));
                map.put("P11_month",validdate.substring(2, 4));
            }
            map.put("P12_cvv2", bean.getCvv());
        }else{
            map.put("P10_year","");
            map.put("P11_month","");
            map.put("P12_cvv2", "");
        }
        map.put("P13_phone", bean.getMobileNo());
        map.put("P14_currency", P14_currency);
        map.put("P15_orderAmount", bean.getAddmoney() + "");// 充值金额
        map.put("P16_goodsName", "9188充值");
        map.put("P17_goodsDesc","");
        map.put("P18_terminalType", "IMEI");
        map.put("P19_terminalId", bean.getImei());
        map.put("P20_orderIp",bean.getIpAddr());
        map.put("P21_period","");
        map.put("P22_periodUnit","");
        map.put("P23_serverCallbackUrl", P23_serverCallbackUrl);
        String md5Str = getSigned(map,null,"P",bean);
        log.info("合利宝银行卡快捷支付，加密前的明文:{}", md5Str );
        String sign = DigestUtils.md5Hex(md5Str.getBytes("UTF-8"));
        log.info("合利宝银行卡快捷支付，签名后的密文:{}",sign);
        map.put("sign", sign);
        return getHttpResp(map, pay_url);
    }

    private TreeMap<String, String> getSortMap(String flag) {
        final String rc=flag;
        TreeMap<String, String> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i = Integer.valueOf(o1.substring(o1.indexOf(rc) + 1, o1.lastIndexOf("_")));
                int j = Integer.valueOf(o2.substring(o2.indexOf(rc) + 1, o2.lastIndexOf("_")));
                return i > j ? 1 : i < j ? -1 : 0;
            }
        });
        return map;
    }

    private String getSigned(Map<String, String> map, String[] excludes, String flag, PayBean bean) {
        StringBuffer sb = new StringBuffer();
        if("t".equals(flag)){
            map.remove("sign");
        }
        TreeMap<String, String> treemap=getSortMap(flag);
        treemap.putAll(map);
        Set<String> excludeSet = new HashSet<String>();
        excludeSet.add("sign");
        if (excludes != null) {
            for (String exclude : excludes) {
                excludeSet.add(exclude);
            }
        }
        for (String key : treemap.keySet()) {
            if (!excludeSet.contains(key)) {
                String value = treemap.get(key);
                value = (value == null ? "" : value);
                sb.append("&");
                sb.append(value);
            }
        }
        sb.append("&");
        sb.append(bean.getMerchantKey());
        return sb.toString();
    }

    private String getHttpResp(Map<String, String> reqMap, String httpUrl) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(httpUrl);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(300000));
        String response = "";
        try {
            NameValuePair[] nvps = getNameValuePair(reqMap);
            method.setRequestBody(nvps);
            int rescode = client.executeMethod(method);
            if (rescode == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String curline = "";
                while ((curline = reader.readLine()) != null) {
                    response += curline;
                }
                return response;
            }
        } catch (Exception e) {
            throw new RuntimeException("合利宝银行卡快捷发送网络请求错误", e);
        } finally {
            method.releaseConnection();
        }
        return null;
    }

    private NameValuePair[] getNameValuePair(Map<String, String> bean) {
        List<NameValuePair> x = new ArrayList<>();
        for (Iterator<String> iterator = bean.keySet().iterator(); iterator.hasNext();) {
            String type = iterator.next();
            x.add(new NameValuePair(type, String.valueOf(bean.get(type))));
        }
        Object[] y = x.toArray();
        NameValuePair[] n = new NameValuePair[y.length];
        System.arraycopy(y, 0, n, 0, y.length);
        return n;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RechDto agreePay(PayBean bean) throws Exception {
        log.info("进入合利宝确认支付:用户名:{},订单号:{}",bean.getUid(),bean.getApplyid());
        try {
            if (StringUtil.isEmpty(bean.getVerifycode())) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("验证码不能为空");
                return null;
            }
            if(StringUtil.isEmpty(bean.getMerchantKey())){
                bean.setMerchantKey(MD5_KEY);
            }
            if(StringUtil.isEmpty(bean.getMerchantId())){
                bean.setMerchantId(P2_customerNumber);
            }
            Map<String, String> map = new HashMap<>();
            map.put("P1_bizType", "QuickPayConfirmPay");
            map.put("P2_customerNumber",bean.getMerchantId());
            map.put("P3_orderId", bean.getApplyid());
            map.put("P4_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            map.put("P5_validateCode", bean.getVerifycode());
            map.put("P6_orderIp",bean.getIpAddr());
            String signedStr = getSigned(map, null,"P", bean);
            String sign = DigestUtils.md5Hex(signedStr.getBytes("UTF-8"));
            map.put("sign", sign);
            String rect = getHttpResp(map, pay_url);
            log.info("合利宝确认支付:用户名:{},合利宝返回信息:{}",bean.getUid(),rect);
            if (rect == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("确认支付错误,请重新尝试");
                return null;
            }
            Map<String, String> paramsMap = JSONObject.parseObject(rect, HashMap.class);
            String returnSign=paramsMap.get("sign");
            if ("0000".equals(paramsMap.get("rt2_retCode"))) {
                String str = getSigned(paramsMap, null,"t", bean);
                String lockstr = DigestUtils.md5Hex(str.getBytes("UTF-8"));// 本地签名
                if (lockstr.equals(returnSign)) {// 签名ok
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("支付成功");
                    RechDto dto=new RechDto();
                    dto.setApplyid(bean.getApplyid());
                    dto.setAddmoney(bean.getAddmoney());
                    return dto;
                }
            } else {
                // 存储扣款失败信息
                bean.setRechargeCode(paramsMap.get("rt2_retCode"));
                bean.setRechargeDesc(paramsMap.get("rt3_retMsg"));
                bean.setBusiErrCode(-1);
                if(StringUtil.isEmpty(paramsMap.get("rt3_retMsg"))){
                    bean.setBusiErrDesc("短信确认扣款失败");
                }else{
                    bean.setBusiErrDesc(paramsMap.get("rt3_retMsg"));
                }
                //存储扣款失败信息
                rechService.saveUserPayErrorInfo(bean);
                convertError(paramsMap.get("rt2_retCode"), bean);
                log.info("合利宝银行卡快捷支付,用户:{},短信扣款失败,错误信息:{}",bean.getUid(), bean.getBusiErrDesc());
                return null;
            }
        } catch (Exception e) {
            log.info("合利宝确认支付出现异常:用户名:{},订单号:{},异常信息:{}",bean.getUid(),bean.getApplyid(),e);
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("短信确认扣款失败");
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

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }
}
