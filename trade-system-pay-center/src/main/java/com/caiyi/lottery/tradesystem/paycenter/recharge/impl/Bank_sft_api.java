package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.redis.util.CacheUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.BankUtil;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.sign.SNKRSA;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.bean.PaySftBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.util.RechargeComUtil;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component("Bank_sft_api")
public class Bank_sft_api implements IBankCardRech {

    @Autowired
    RechService rechService;

    @Autowired
    RedisInterface redisInterface;

    @Autowired
    NotifyService notifyService;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static String charset = "UTF-8";
    private static final String shengpayUrl = "https://mgw.shengpay.com/mas/";
    private static String createOrder = "api-acquire-channel/services/express/createPaymentOrder";
    private static String smsOrder = "api-acquire-channel/services/express/precheckForPayment";
    private static String agreeOrder = "api-acquire-channel/services/express/payment";
    private static String mch_id = "150101";
    private static String mer_key = "OTE4OF9zZnRfMjAxMzAyMjVfa2V5";
    private static String notifyUrl = PayConstant.NOTIFY_HOST + "/pay/web_sftNotify.api";
    private static final String publicKey = "ACQAAASAAACUAAAABgIAAAAkAABSU0EyAAQAAAEAAQCrTJTlKZuEV4WLWoI2lIwdJxexLYZJSesm4lmIvRybVeoBvIDT5Ytm9zupmELzSWgAQR6AjZvy3LbTzW538CiJNCKRF3lMkqexHpzlz52bZymEFmVfT0bdPMinWfkBC6S294NKfGHCPOdZpaYfO8w3T++CPb4WxEQY/VVbivf2ug==";

    private static List<String> notifyParams = new ArrayList<String>();

    static {
        notifyParams.add("Name");// 版本名称
        notifyParams.add("Version");// 版本号
        notifyParams.add("Charset");// 字符集
        notifyParams.add("TraceNo");// 报文发起唯一标识
        notifyParams.add("MsgSender");// 发送方标识
        notifyParams.add("SendTime");// 发送支付请求时间
        notifyParams.add("InstCode");// 银行编码
        notifyParams.add("OrderNo");// 商户订单号
        notifyParams.add("OrderAmount");// 支付金额
        notifyParams.add("TransNo");// 盛付通交易号
        notifyParams.add("TransAmount");// 盛付通实际支付金额
        notifyParams.add("TransStatus");// 支付状态
        notifyParams.add("TransType");// 盛付通交易类型
        notifyParams.add("TransTime");// 盛付通交易时间
        notifyParams.add("MerchantNo");// 商户号
        notifyParams.add("ErrorCode");// 错误代码
        notifyParams.add("ErrorMsg");// 错误代码
        notifyParams.add("Ext1");// 扩展1
        notifyParams.add("SignType");// 签名类型
    }

    private static Map<String, String> TransStatusMap = new HashMap<String, String>();

    static {
        TransStatusMap.put("00", "等待付款中");
        TransStatusMap.put("01", "付款成功");
        TransStatusMap.put("02", "付款失败");
        TransStatusMap.put("03", "过期");
        TransStatusMap.put("04", "撤销成功");
        TransStatusMap.put("05", "退款中");
        TransStatusMap.put("06", "退款成功");
        TransStatusMap.put("07", "退款失败");
        TransStatusMap.put("08", "部分退款成功");
    }

    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("#####0.00");
        System.out.println(df.format(0.01));
        PayBean bean = new PayBean();
        PaySftBean sftBean = new PaySftBean();
        bean.setUid("abc");
        bean.setRemark("测试");

        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, sftBean);
        System.out.println(sftBean.getUid() + "," + bean.getRemark());
        System.out.println(sftBean.toString());


        Bank_sft_api pay = new Bank_sft_api();
        bean.setApplyid("17BA4T4C14");
        bean.setAddmoney(1);
        bean.setCardtype(0);
        bean.setBankCode("CMB");
        bean.setRealName("刘研擘");
        bean.setMobileNo("17602112430");
        bean.setIdcard("410311199105012511");
        bean.setCardNo("6214852115958996");
        bean.setMerchantId("150101");
        bean.setMerchantKey("OTE4OF9zZnRfMjAxMzAyMjVfa2V5");
        bean.setPrivateKey("OTE4OF9zZnRfMjAxMzAyMjVfa2V5");
        bean.setIpAddr("116.231.55.171");
        bean.setCuserId("f1fd9e5f-88ba-431e-8612-4fe5bfd0f517");
        bean.setUid("lyb123");
//        bean.setCvv("21/09");
        Logger log = LoggerFactory.getLogger("TEST");
        pay.addmoney(bean);
        bean.setSessionToken("6FAA6F3F8ED0324A6C7657C1495031629D8298D3D7D2645F1AF71266C808178B0A441D91202F31E7");
        bean.setVerifycode("429550");
//        pay.agreePay(bean);

    }


    //错误码与后续类型的映射
    private static final Map<String, Integer> ErrorToPageMap = new HashMap<String, Integer>();

    static {
        ErrorToPageMap.put("输入的验证码有误!", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        ErrorToPageMap.put("手机验证码不正确", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        ErrorToPageMap.put("余额不足", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_UNREPAY));
    }

    @Override
    public RechDto addmoney(PayBean bean) {
        RechDto rechDto = new RechDto();
        log.info("盛付通-->开始创建订单[" + bean.getApplyid() + "]  cardno=" + bean.getCardNo() + ",mobile=" + bean.getMobileNo());
        try {
            String result = createPayment(bean);//创建订单
            if (StringUtil.isEmpty(result)) return rechDto;
            JSONObject json = JSONObject.parseObject(result);
//            Map<String, String> createPaymentOrder = saveShengpayOrder(bean, json, log);//保存吗？TODO
//            String sftOrderNo = createPaymentOrder.get("sftOrderNo");
            String sftOrderNo = json.getString("sftOrderNo");
            if (!StringUtil.isEmpty(sftOrderNo)) {
                //支付预校验,发送短信
                precheckForPayment(bean, json);
            }
            rechDto.setApplyid(bean.getApplyid());
            PayParam payParam = new PayParam();
            payParam.setSessionToken(json.getString("sessionToken"));
            rechDto.setPayParam(payParam);
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("盛付通下单未成功");
            log.error("盛付通下单失败,订单[" + bean.getApplyid() + "]", e);
        }

        return rechDto;
    }

    @Override
    public RechDto agreePay(PayBean bean) {
        RechDto rechDto = new RechDto();
        try {
            log.info("盛付通支付确认,nickid=" + bean.getUid() + ",applyid=" + bean.getApplyid());
            if (verifyParam(bean)) return rechDto;

            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            paramList.add(new NameValuePair("merchantNo", getMerId(bean)));
            paramList.add(new NameValuePair("charset", charset));
            paramList.add(new NameValuePair("requestTime", sdf.format(new Date())));
            paramList.add(new NameValuePair("sessionToken", bean.getSessionToken()));
            paramList.add(new NameValuePair("validateCode", bean.getVerifycode()));
            paramList.add(new NameValuePair("isSign", "true"));
            paramList.add(new NameValuePair("userIp", bean.getClientIp()));
            rechDto.setApplyid(bean.getApplyid());
            String result = sendToShengpay(bean, paramList, agreeOrder);
            if (StringUtil.isEmpty(result)) return rechDto;

            JSONObject json = JSON.parseObject(result);
            if ("SUCCESS".equals(json.getString("returnCode"))) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                bean.setBusiErrDesc("请求已提交，稍后确认是否成功");
                bean.setDealid(json.getString("sftOrderNo"));//盛付通订单号
                String agreementNo = json.getString("agreementNo");//盛付通签约号
                String signStatus = json.getString("signStatus");//签约号状态
                log.info("盛付通支付确认成功Applyid=" + bean.getApplyid() + ",nickid=" + bean.getUid() + ",agreementNo=" + agreementNo + ",signStatus=" + signStatus);
//                //记录用户签约协议
//                bean.setUserbusiid("sft_auto_sign_state==" + signStatus);//用户签约号状态
//                bean.setUserpayid(agreementNo);//签约成功号
//                notifyService. updateStfSignProtocol(bean, context);
//                updateShengPayOrder(bean, context);
            } else {
                String returnCode = json.getString("returnCode");
                String returnMessage = json.getString("returnMessage");
                log.info("盛付通支付确认失败,returnCode==" + json.getString("returnCode") + "   returnMessage=" + returnMessage + " applyid ==" + bean.getApplyid());
//                updateShengPayOrder(bean, context);
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(returnMessage);
                //记录渠道错误信息
                bean.setRechargeCode(returnCode);
                bean.setRechargeDesc(returnMessage);
                //存储扣款失败信息
                rechService.saveUserPayErrorInfo(bean);
                convertError(returnMessage, bean);
                rechDto.setApplyid(bean.getApplyid());
                rechDto.setAddmoney(bean.getAddmoney());
                return rechDto;
            }


        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("确认付款失败");
            log.error("盛付通确认支付失败，订单号：" + bean.getApplyid(), e);
        }

        rechDto.setApplyid(bean.getApplyid());
        rechDto.setAddmoney(bean.getAddmoney());
        return rechDto;
    }

    //错误码转换
    private static void convertError(String returnMessage, PayBean bean) {
        Integer returnType = ErrorToPageMap.get(returnMessage);
        if (returnType == null) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_REPAY));
        } else {
            bean.setBusiErrCode(returnType);
        }
    }

    private boolean verifyParam(PayBean bean) {
        boolean flag = true;
        if (StringUtil.isEmpty(bean.getApplyid())) {
            log.info("盛付通支付确认,applyid为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("充值订单号为空");
            return flag;
        }
        if (StringUtil.isEmpty(bean.getVerifycode())) {
            log.info("盛付通支付确认,短信验证码为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("短信验证码不能为空");
            return flag;
        }
        if (StringUtil.isEmpty(bean.getSessionToken())) {
            log.info("盛付通支付确认,sessionToken为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("会话信息不能为空");
            return flag;
        }

        String key = "";
        StringBuilder builder = new StringBuilder();
        builder.append("shengpay").append("_").append(bean.getUid()).append("_").append(bean.getVerifycode()).append("_").append(bean.getApplyid());
        try {
            key = MD5Util.compute(builder.toString()).toUpperCase();
        } catch (Exception e1) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("会话信息不能为空");
            log.error("shengpay加密串加密失败：", e1);
            return flag;
        }//加密串作为key

        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        String val = CacheUtil.getString(cacheBean, log, redisInterface, SysCodeConstant.PAYCENTER);
        if (!StringUtil.isEmpty(val)) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("您的支付请求已提交，请勿重复操作");
            return flag;
        } else {
            cacheBean.setTime(7200000);
            cacheBean.setValue(bean.getApplyid());
            CacheUtil.setString(cacheBean, log, redisInterface, SysCodeConstant.PAYCENTER);//有限期2个小时
        }
        return !flag;
    }

    private Map<String, String> saveShengpayOrder(PayBean bean, JSONObject json) {
        Map<String, String> map = new HashMap<>();
        PaySftBean sftBean = new PaySftBean();
        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, sftBean);
        if ("SUCCESS".equals(json.get("returnCode"))) {
            String sftOrderNo = json.get("sftOrderNo").toString();//盛付通订单号
            String sessionToken = json.get("sessionToken").toString();
            String orderCreateTime = json.get("orderCreateTime").toString();//盛付通订单创建时间
            //订单信息
            sftBean.setSftOrderNo(sftOrderNo);
            sftBean.setSessionToken(sessionToken);
            sftBean.setOrderCreateTime(orderCreateTime);

            map.put("sftOrderNo", sftOrderNo);
            map.put("sessionToken", sessionToken);
            map.put("orderCreateTime", orderCreateTime);

            log.info("盛付通创建支付订单成功,保存支付订单信息--saveShengpayOrderInfo ,nickid=" + bean.getUid() + ",applyid=" + bean.getApplyid() + ",sftOrderNo=" + sftOrderNo);
//            int nums = rechService.saveShengpayOrderInfo(sftBean);
//            if (nums == 1) {
//                bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
//                bean.setBusiErrDesc("盛付通创建支付订单成功");
//                log.info("保存支付订单信息成功,nickid=" + bean.getUid() + ",applyid=" + bean.getApplyid() + ",sftOrderNo=" + sftOrderNo);
//            } else {
//                log.info("保存支付订单信息失败,applyid=" + bean.getApplyid() + ",nums=" + nums + ",code=" + bean.getBusiErrCode());
//            }
        } else {
            log.info("盛付通创建支付订单失败,nickid=" + bean.getUid() + ",applyid=" + bean.getApplyid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("盛付通创建支付订单失败");
        }
        return map;
    }

    private void precheckForPayment(PayBean bean, JSONObject json) throws Exception {
        log.info("盛付通支付预校验  cardno=" + bean.getCardNo() + ",bankCode=" + bean.getBankCode() + ",userid==" + bean.getUserid());
        if (checkParam(bean, json)) return;
        List<NameValuePair> paramList = createSmsParam(bean, json);
        String result = sendToShengpay(bean, paramList, smsOrder);
        if (StringUtil.isEmpty(result)) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            return;
        }
        json = JSON.parseObject(result);
        if ("SUCCESS".equals(json.getString("returnCode"))) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
            bean.setBusiErrDesc("支付预校验成功");
            log.info("盛付通支付预校验成功,applyid ==" + bean.getApplyid());
        } else {
            String returnCode = json.getString("returnCode");
            String returnMessage = json.getString("returnMessage");
            log.info("盛付通支付预校验失败,returnMessage=" + returnMessage + " applyid ==" + bean.getApplyid());
            if (RechargeComUtil.SHENGPAY_ERROR_CODE.containsKey(returnCode) && RechargeComUtil.checkErrorDesc("shengpay", returnMessage)) {
                bean.setBusiErrCode(100);
                bean.setBusiErrDesc(returnMessage);
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc(returnMessage);
                //记录渠道错误信息
                bean.setRechargeCode(returnCode);
                bean.setRechargeDesc(returnMessage);
                //存储扣款失败信息
                rechService.saveUserPayErrorInfo(bean);
            }
        }
    }

    private boolean checkParam(PayBean bean, JSONObject json) {
        boolean flag = true;
        log.info("盛付通支付预校验  cardno=" + bean.getCardNo() + ",bankCode=" + bean.getBankCode());
        if (bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            log.info("订单[" + bean.getApplyid() + "]预校验失败");
            return flag;
        }
        log.info("盛付通支付预校验,nickid=" + bean.getUid() + ",applyid=" + bean.getApplyid() + ",realname" + bean.getRealName());
        if (StringUtil.isEmpty(bean.getApplyid())) {
            log.info("盛付通支付预校验,applyid为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("充值订单号不能为空");
            return flag;
        }

        if (StringUtil.isEmpty(bean.getCardNo())) {
            log.info("盛付通支付预校验,银行卡信息为空,cardno=" + bean.getCardNo() + ",bankCode=" + bean.getBankCode());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("银行卡信息不能为空");
            return flag;
        }

        if (StringUtil.isEmpty(json.getString("sessionToken"))) {
            log.info("盛付通支付预校验,sessionToken为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("会话信息不能为空");
            return flag;
        }

        if (StringUtil.isEmpty(bean.getRealName()) || StringUtil.isEmpty(bean.getMobileNo())) {
            log.info("盛付通支付预校验,用户信息为空,nickid=" + bean.getUid() + ",realname:" + bean.getRealName() + ",mobile" + bean.getMobileNo());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("用户信息不能为空");
            return flag;
        }
        return !flag;

    }

    private List<NameValuePair> createSmsParam(PayBean bean, JSONObject json) {
        int cardtype = bean.getCardtype();
        List<NameValuePair> paramList = new ArrayList<>();
        String type = "";
        if (0 == cardtype) {
            type = "DR";
        } else if (1 == cardtype) {
            type = "CR";
            paramList.add(new NameValuePair("cvv2", bean.getCvv()));
            paramList.add(new NameValuePair("validThru", getValidate(bean)));
        } else {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("卡类型错误！");
        }
        //基本参数
        paramList.add(new NameValuePair("merchantNo", getMerId(bean)));
        paramList.add(new NameValuePair("charset", charset));
        paramList.add(new NameValuePair("requestTime", sdf.format(new Date())));

        paramList.add(new NameValuePair("sessionToken", json.getString("sessionToken")));
        paramList.add(new NameValuePair("isResendValidateCode", "false"));
        paramList.add(new NameValuePair("outMemberId", bean.getUserid()));
        paramList.add(new NameValuePair("realName", bean.getRealName()));
        paramList.add(new NameValuePair("idNo", bean.getIdcard()));//证件号
        paramList.add(new NameValuePair("idType", "IC"));//证件类型
        paramList.add(new NameValuePair("mobileNo", bean.getMobileNo()));
        paramList.add(new NameValuePair("userIp", bean.getClientIp()));
        paramList.add(new NameValuePair("bankCode", bean.getBankCode()));
        paramList.add(new NameValuePair("bankCardType", type));
        paramList.add(new NameValuePair("bankCardNo", bean.getCardNo()));

        JSONObject riskExt = new JSONObject();
        riskExt.put("outMemberId", bean.getUserid());
        riskExt.put("outMemberRegistTime", sdf.format(new Date()));
        riskExt.put("outMemberRegistIP", bean.getClientIp());
        riskExt.put("outMemberVerifyStatus", "1");//是否实名
        riskExt.put("outMemberName", bean.getRealName());
        riskExt.put("outMemberMobile", bean.getMobileNo());
        paramList.add(new NameValuePair("riskExtItems", riskExt.toJSONString()));
        return paramList;
    }

    private static String getValidate(PayBean bean) {
        String validate = bean.getValidDate();
        //2108
        StringBuilder builder = new StringBuilder(validate);
        if(StringUtil.isNotEmpty(validate) && validate.length() == 4){
            builder.insert(2, "/");
        }
        return builder.toString();
    }

    private String createPayment(PayBean bean) throws Exception {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new NameValuePair("merchantOrderNo", bean.getApplyid()));
        paramList.add(new NameValuePair("productName", "充值"));
        paramList.add(new NameValuePair("currency", "CNY"));
        paramList.add(new NameValuePair("merchantNo", getMerId(bean)));
        paramList.add(new NameValuePair("charset", charset));
        //支付金额
        DecimalFormat df = new DecimalFormat("#####0.00");
        double m = bean.getAddmoney() + bean.getHandmoney();
        String amount = df.format(m);
        paramList.add(new NameValuePair("amount", amount));
        paramList.add(new NameValuePair("notifyUrl", notifyUrl));
        paramList.add(new NameValuePair("userIp", bean.getClientIp()));
        paramList.add(new NameValuePair("requestTime", sdf.format(new Date())));

        return sendToShengpay(bean, paramList, createOrder);
    }

    private String getMerId(PayBean bean) {
        boolean flag = StringUtil.isEmpty(bean.getMerchantId());
        if (flag) {
            bean.setMerchantKey(mer_key);
            bean.setMerchantId(mch_id);
            return mch_id;
        }
        return bean.getMerchantId();
    }

    private String sendToShengpay(PayBean bean, List<NameValuePair> paramList, String type) throws Exception {
        PostMethod postMethod = responseFromSft(bean, paramList, type);
        String responseBody = postMethod.getResponseBodyAsString();
        log.info("盛付通-->Applyid[" + bean.getApplyid() + "]http 请求响应 body : " + responseBody);
        Header signMsgHeader = postMethod.getResponseHeader("signMsg");
        Header signTypeHeader = postMethod.getResponseHeader("signType");
        if (null != signMsgHeader) {
            String responseSign = signMsgHeader.getValue();
            String responseSignType = signTypeHeader.getValue();
            log.info("盛付通-->Applyid[" + bean.getApplyid() + "]返回加密方式 : " + responseSignType);
            log.info("盛付通-->Applyid[" + bean.getApplyid() + "]返回签名信息 : " + responseSign);
            boolean signResult = false;
            if ("RSA".equalsIgnoreCase(responseSignType)) {
                signResult = SNKRSA.verify(responseBody, responseSign, publicKey, charset);
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("盛付通未知验证类型");
                log.info("盛付通-->Applyid[" + bean.getApplyid() + "]未知的signType,signType=" + responseSign);
            }
            if (!signResult) {
                log.info("盛付通-->Applyid[" + bean.getApplyid() + "]验签失败");
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("盛付通验签失败");
            }
        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("盛付通下单失败");
            log.info("盛付通-->无签名信息，验证签名失败Applyid[" + bean.getApplyid() + "]");
        }
        return responseBody;
    }

    private PostMethod responseFromSft(PayBean bean, List<NameValuePair> paramList, String type) throws Exception {
        NameValuePair[] params = new NameValuePair[paramList.size()];
        int index = 0;
        for (NameValuePair param : paramList) {
            params[index++] = param;
        }
        HttpClient client = new HttpClient();
        HttpConnectionManagerParams httpParams = client.getHttpConnectionManager().getParams();
        httpParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
        PostMethod postMethod = null;
        postMethod = new PostMethod(shengpayUrl + type);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        postMethod.addParameters(params);
        RequestEntity requestEntity = postMethod.getRequestEntity();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        requestEntity.writeRequest(baos);
        String requestBody = baos.toString();
        log.info("盛付通-->Applyid[" + bean.getApplyid() + "]send Request Body ------>: " + requestBody);
        String signMsg = BankUtil.sign(requestBody, bean.getMerchantKey(), charset);
        postMethod.addRequestHeader("signType", "MD5");
        postMethod.addRequestHeader("signMsg", signMsg);
        int httpCode = client.executeMethod(postMethod);
        log.info("盛付通-->Applyid[" + bean.getApplyid() + "]http请求响应状态码 : " + httpCode);
        return postMethod;
    }


    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }
}
