package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.RechService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.CardMobileUtil;
import com.caiyi.lottery.tradesystem.util.HttpClientUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.umpay.api.common.ReqData;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.api.paygate.v40.Plat2Mer_v40;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component("Bank_ldpay_fast")
public class Bank_ldpay_fast implements IBankCardRech {

    @Autowired
    RechService rechService;

    @Autowired
    NotifyService notifyService;

    /**
     * 联动优势 手机充值
     */
    private static String service_name = "apply_pay_shortcut"; // 接口名称
    private static String service_sms_name = "sms_req_shortcut"; // 请求短信接口名称
    private static String confirm_sms_name = "confirm_pay_shortcut";// 确认短信验证码接口
    private static String mer_id = "6329"; // 商户编号
    private static String notify_url = PayConstant.NOTIFY_HOST + "/pay/web_ldys_notify.api";
    private static String char_set = "UTF-8"; // 编码格式
    private static String sign_type = "RSA"; // 签名方式
    private static String version = "4.0"; // 版本号
    private static String amt_type = "RMB"; // 付款币种
    private static String media_type = "MOBILE";// 媒介类型
    private static SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
    public static HashMap<String, String> cardType = new HashMap<String, String>();

    static {
        cardType.put("0", "DEBITCARD");
        cardType.put("1", "CREDITCARD");
    }

    //错误码与后续类型的映射
    private static final Map<String, Integer> ErrorToPageMap = new HashMap<String, Integer>();

    static {
        ErrorToPageMap.put("00200083", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        ErrorToPageMap.put("00200086", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        ErrorToPageMap.put("00200090", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_CODE));
        ErrorToPageMap.put("00060780", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_UNREPAY));
        ErrorToPageMap.put("00080730", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_UNREPAY));
        ErrorToPageMap.put("00080537", Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_UNREPAY));
    }

    @Override
    public RechDto addmoney(PayBean bean) {
        RechDto rechDto = new RechDto();
        try {
            String html = createPayment(bean);
            rechDto = parseAndSendSms(bean, html);
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请求下单出错");
            log.error("联动优势-->用户" + bean.getUid() + "返回数据错误,订单号[" + bean.getApplyid() + "]", e);
        }
        return rechDto;
    }

    private RechDto parseAndSendSms(PayBean bean, String html) throws Exception {
        RechDto rechDto = new RechDto();
        if (StringUtil.isEmpty(html)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请求支付出了点小问题!");
            return rechDto;
        }
        Map<String, String> resultMap = Plat2Mer_v40.getResData(html);

        log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "的html下单解析结果：" + resultMap);
        String ret_code = resultMap.get("ret_code");
        String ret_msg = resultMap.get("ret_msg");
        String trade_no = resultMap.get("trade_no");
        log.info("联动优势-->ret_code==[" + ret_code + "],trade_no==[" + trade_no + "],ret_msg==[" + ret_msg + "]");
        bean.setTradeNo(trade_no);
        if ("0000".equals(ret_code)) {
            rechDto.setApplyid(bean.getApplyid());
            rechDto.setAddmoney(bean.getAddmoney());
            PayParam payParam = new PayParam();
            payParam.setTradeNo(bean.getTradeNo());
            rechDto.setPayParam(payParam);
            log.info("联动优势-->订单" + bean.getApplyid() + "开始请求短信");
            send_sms(bean);
        } else {
            bean.setBusiErrCode(Integer.valueOf(ret_code));
            bean.setBusiErrDesc(ret_msg);
        }
        return rechDto;

    }

    /**
     * 向平台请求发送短信
     */
    private void send_sms(PayBean bean) throws Exception {
        log.info("联动优势-->bean--" + bean.getCardNo() + ",uid=" + bean.getUid() + ",idcard=" + bean.getIdcard());
        Map<String, String> paramMap = new HashMap<String, String>();
        createSmsParamMap(bean, service_sms_name, paramMap);

        if ("CREDITCARD".equals(cardType.get("" + bean.getCardtype()))) {//信用卡
            paramMap.put("valid_date", bean.getValidDate().replace("/", ""));
            paramMap.put("cvv2", bean.getCvv());
        }
        log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "[请求短信]参数==>" + paramMap);
        String html = sendRequest(bean, paramMap);
        log.info("联动优势-->返回发送短信html结果--" + html);
        if (StringUtil.isEmpty(html)) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("请求平台发送短信失败");
            log.info("联动优势-->订单[" + bean.getApplyid() + "]请求短信失败");
            return;
        }
        Map<String, String> resultMap = Plat2Mer_v40.getResData(html);
        log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "[请求短信]结果>" + resultMap);
        String ret_code = resultMap.get("ret_code");
        String ret_msg = resultMap.get("ret_msg");
        if ("0000".equals(ret_code)) {
            bean.setBusiErrCode(Integer.valueOf(ret_code));
            bean.setBusiErrDesc(ret_msg);
            log.info("联动优势-->请求平台发送短信成功");
            return;
        } else {
            bean.setBusiErrCode(Integer.valueOf(ret_code));
            bean.setBusiErrDesc(ret_msg);
            log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "请求平台发送短信失败，ret_code---" + ret_code);
            return;
        }
    }

    private void createSmsParamMap(PayBean bean, String type, Map<String, String> paramMap) {
        paramMap.put("service", type);
        paramMap.put("mer_id", getMerId(bean));
        paramMap.put("charset", char_set);
        paramMap.put("sign_type", sign_type);
        paramMap.put("version", version);
        paramMap.put("trade_no", bean.getTradeNo());
        paramMap.put("media_id", bean.getMobileNo());
        paramMap.put("media_type", media_type);
        paramMap.put("card_id", bean.getCardNo());
        paramMap.put("identity_type", "IDENTITY_CARD");
        paramMap.put("identity_code", bean.getIdcard());
        paramMap.put("card_holder", bean.getRealName());

        if (!StringUtil.isEmpty(bean.getVerifycode())) {
            paramMap.put("verify_code", bean.getVerifycode());
        }
    }

    private String getMerId(PayBean bean) {
        boolean flag = StringUtil.isEmpty(bean.getMerchantId());
        if (flag) {
            bean.setMerchantKey("");
            bean.setMerchantId(mer_id);
            return mer_id;
        }
        if(StringUtil.isEmpty(bean.getMerchantKey())){
            bean.setMerchantKey("");
        }
        return bean.getMerchantId();
    }

    private String createPayment(PayBean bean) throws Exception {
        log.info("联动优势-->下单开始，订单号：" + bean.getApplyid());
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("service", service_name);
        paramMap.put("charset", char_set);
        paramMap.put("mer_id", getMerId(bean));
        paramMap.put("sign_type", sign_type);
        paramMap.put("notify_url", notify_url);
        paramMap.put("version", version);
        paramMap.put("order_id", bean.getApplyid());
        paramMap.put("mer_date", sdf.format(new Date()));
        paramMap.put("amt_type", amt_type);
        paramMap.put("pay_type", cardType.get("" + bean.getCardtype()));
        paramMap.put("gate_id", bean.getBankCode());

        String amount = parseAmount(bean);//总金额，以分为单位
        paramMap.put("amount", amount);
        log.info("联动优势-->联动优势参数列==>" + paramMap);

        String html = sendRequest(bean, paramMap);
        log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "返回的html信息" + html);
        if (StringUtil.isEmpty(html)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请求下单失败");
            log.info("联动优势-->用户" + bean.getUid() + "的联动优势订单" + bean.getApplyid() + "返回参数为空" + html);
        }
        return html;
    }

    private static Map<String, String> parseHtml(String html) {
        Map<String, String> resultMap = new HashMap<String, String>();
        Document doc = Jsoup.parse(html);
        Elements e = doc.getElementsByTag("META");
        for (int i = 0; i < e.size(); i++) {
            String metaValue = e.get(i).attr("NAME");
            if ("MobilePayPlatform".equals(metaValue)) {
                String content = e.get(i).attr("CONTENT");
                return parseToMap(content, resultMap);
            }
        }
        return resultMap;
    }

    private static Map<String, String> parseToMap(String content, Map<String, String> resultMap) {
        String[] group = content.split("&");
        String[] text;
        for (int i = 0; i < group.length; i++) {
            text = group[i].split("=");
            if (1 == text.length) {//可能有key无值
                resultMap.put(text[0], "");
                continue;
            }
            resultMap.put(text[0], text[1]);
        }
        return resultMap;
    }

    private String sendRequest(PayBean bean, Map<String, String> reqMap) throws Exception {
        ReqData reqDataGet = Mer2Plat_v40.makeReqDataByGet(reqMap);
        log.info("联动优势-->用户" + bean.getUid() + "订单" + bean.getApplyid() + "联动优势[下单]返回结果==>" + reqDataGet);
        if (reqDataGet != null) {
            String html = HttpClientUtil.callHttpGet(reqDataGet.getUrl());
            return html;
        } else {
            log.info("联动优势-->用户" + bean.getUid() + "订单" + bean.getApplyid() + "联动优势[下单]返回结果为空");
            return "";
        }
    }

    private String parseAmount(PayBean bean) {
        double dValue = getRound((bean.getAddmoney() + bean.getHandmoney()) * 100, 0);
        Double D1 = new Double(dValue);
        int addmoney = D1.intValue();
        return addmoney + "";
    }

    public static double getRound(double m, int num) {
        BigDecimal dec = new BigDecimal(m);
        BigDecimal one = new BigDecimal("1");
        return dec.divide(one, num, BigDecimal.ROUND_CEILING).doubleValue();
    }


    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }

    @Override
    public RechDto agreePay(PayBean bean) {
        RechDto rechDto = new RechDto();
        try {
            log.info("联动优势-->verifycode[" + bean.getVerifycode() + "],applyid[" + bean.getApplyid() + "],tradeNo==" + bean.getTradeNo());
            if (checkparam(bean)) return rechDto;
            // 解密银行卡和手机号
            log.info("联动优势-->uid[" + bean.getUid() + "],applyid[" + bean.getApplyid() + "],mobileNo==" + bean.getMobileNo() + ",cardNo==" + bean.getCardNo());
            Map<String, String> paramMap = new HashMap<String, String>();
            createSmsParamMap(bean, confirm_sms_name, paramMap);
            if ("CREDITCARD".equals(cardType.get("" + bean.getCardtype()))) {//信用卡
                paramMap.put("valid_date", bean.getValidDate().replace("/", ""));
                paramMap.put("cvv2", bean.getCvv());
            }
            log.info("联动优势-->短信确认 paramMap--" + paramMap);
            String html = sendRequest(bean, paramMap);
            if (!StringUtil.isEmpty(html)) {
                log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "返回的[短信确认]html信息++`" + html);
                Map<String, String> resultMap = Plat2Mer_v40.getResData(html);
                log.info("联动优势-->用户" + bean.getUid() + "的订单" + bean.getApplyid() + "[短信确认]结果:" + resultMap);
                String ret_code = resultMap.get("ret_code");
                String ret_msg = resultMap.get("ret_msg");
                if (!"0000".equals(ret_code)) {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("短信确认扣款失败");
                    if ("00200090".equals(ret_code))
                        bean.setBusiErrDesc("验证码已失效");
                    if ("00060700".equals(ret_code))
                        bean.setBusiErrDesc("验证码错误");
                    log.info("联动优势-->用户[" + bean.getUid() + "]短信确认扣款失败,参数=[ret_code--" + ret_code + ",trade_no--[" + bean.getTradeNo() + "]");
                    bean.setRechargeCode(ret_code);
                    bean.setRechargeDesc(ret_msg);
                    //存储扣款失败信息
                    rechService.saveUserPayErrorInfo(bean);
                    convertError(ret_code, bean);
                    rechDto.setApplyid(bean.getApplyid());
                    rechDto.setAddmoney(bean.getAddmoney());
                    return rechDto;
                }
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("短信确认扣款请求失败");
                log.info("联动优势-->reqDataGet短信确认扣款请求失败==>" + html);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("短信确认请求失败");
            log.info("联动优势-->返回短信确认数据错误", e);
        }
        rechDto.setApplyid(bean.getApplyid());
        rechDto.setAddmoney(bean.getAddmoney());
        return rechDto;
    }

    //错误码转换
    private static void convertError(String returnCode, PayBean bean) {
        Integer returnType = ErrorToPageMap.get(returnCode);
        if (returnType == null) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.PAY_RECHARGE_WRONG_BUSI_REPAY));
        } else {
            bean.setBusiErrCode(returnType);
        }
    }

    private void decryptCarcAndMobile(PayBean bean) {
        String cardNo = CardMobileUtil.decryptCard(bean.getCardNo());
        String mobileNo = CardMobileUtil.decryptMobile(bean.getMobileNo());
        bean.setCardNo(cardNo);
        bean.setMobileNo(mobileNo);
    }

    private boolean checkparam(PayBean bean) {
        if (StringUtil.isEmpty(bean.getApplyid())) {
            log.info("联动优势-->联动优势LD充值校验,applyid为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("充值订单号为空");
            return true;
        }
        if (StringUtil.isEmpty(bean.getVerifycode())) {
            log.info("联动优势-->联动优势LD充值校验,短信验证码为空,nickid=" + bean.getUid());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("短信验证码不能为空");
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        Bank_ldpay_fast pay = new Bank_ldpay_fast();
        PayBean bean = new PayBean();
        bean.setApplyid("17BA494C73");
        bean.setAddmoney(100);
        bean.setCardtype(0);
        bean.setBankCode("CMB");
        bean.setRealName("刘研擘");
        bean.setMobileNo("17602112430");
        bean.setIdcard("410311199105012511");
        bean.setCardNo("6214852115958996");
        bean.setCvv("21/09");
        Logger log = LoggerFactory.getLogger("TEST");
        pay.addmoney(bean);
        bean.setVerifycode("912858");
        bean.setTradeNo("3801031118758203");
        pay.agreePay(bean);

    }
}
