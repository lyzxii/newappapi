package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;


import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IRecharge;
import com.caiyi.lottery.tradesystem.paycenter.service.BaseService;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.jd.jr.pay.gate.signature.util.BASE64;
import com.jd.jr.pay.gate.signature.util.SignUtil;
import com.jd.jr.pay.gate.signature.util.ThreeDesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import pay.pojo.jdpay.BasePayOrderInfo;
import pay.util.PayUtil;
import pay.util.jdpayutil.CertUtil;
import pay.util.jdpayutil.StringEscape;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 京东钱包
 */
@Slf4j
@Component("Bank_jdpay_h5")
public class Bank_jdpay_h5 implements IRecharge{

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private BaseService baseService;

    //秘钥信息
    private static final String MERCHANT = "110263491003";
    private static final String RsaPrivateKey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANKMzKyMTKUfSZDuf6wT4akGamJzjpe1IEKC17DFMrM1SPhCxMNsPglj/i49ffpNauZRcOt32O8gLftUCjgqXsSfyxs0pCBd3j4dAxpAMH4L4bFryGxuYpQQmoFNNYrNrrI8WU25qC0Wkxq1UUEpKWC8X0rFVBnaiaswOUmhHd8HAgMBAAECgYBsZjsV9uGa/aG1cWTf5hh/GiN5bn8SUhk6xPxhMRWqOBvKXS3KYxcJZKa/jb8nN+Z6j6FwSXKxokKqK5lbU00YRQFTWLae6NCldRxDJRySqxGoImBOSkKDCTrcqOzh/zCjAysPzAY/vd+LNPFN4mw+wQmdCZhbIhoo+53Chs85qQJBAO2ORYC072hBMu04uF1WZp94t//R0Y6euHZjrpHO49xy4Pa8+AmCdQhrRt7x3LUbNUs/mCNapADaW3webENVIisCQQDi5cDzmU6RqbZXPROFyhOaLHMY+JhpWl7g1D5QC6L1XxixGR09FFLB7qwlH7yt8dxIS1TM1ORepf5c3oHkAPSVAkAMjIVzZRt4gGk2StbCZ3wIVJIzu1u8kvnflyhzn2A+Fvt3TUg1CGUySqh1woUMNCG+ld9tjq43NVG73seNTwP9AkAyGpopMMlw0RE64nnVKXJ6sSYZdYvyd8pLy5KXCnrJxF4nfWw/eKVtvc61w/ReVPJX5IqtULW9UNqCl4AHYU6hAkB1zf5A8+86bRM1sSzzbjzvZWT95HwQJoLGYheKnjdQDXcitNz2QU1AADcq36dndYb3AtYZooj4fvvMn4zUtLIZ";
    private static final String deskey="L9l/kh8OkQKwVKEfXiZFa+qSbVtdFQIZ";

    //支付成功APP返回接口
    private static String defaultwebcallbackurl = "http://t2015.9188.com/user/mlottery.go";

    //回调通知  -- 时间点分别为支付完成后、支付完成后1分钟、支付完成后3分钟、支付完成后10分钟、支付完成后120分钟
    private static String NOTIFY_URL = PayConstant.NOTIFY_HOST + "/pay/jdpay_h5_notify.api";

    //京东下单请求URL
    private static final String SAVEORDER_URL ="https://h5pay.jd.com/jdpay/saveOrder";

    private static Map<String,String> statusMap = new HashMap<>();
    static{
        statusMap.put("0","创建");
        statusMap.put("1","处理中");
        statusMap.put("2","成功");
        statusMap.put("3","失败");
        statusMap.put("4","关闭");
    }

    @Override
    public RechDto addmoney(PayBean bean) {
        log.info("京东支付用户下单开始，applyid==" + bean.getApplyid());
        String version = "V2.0";
        String sign = "";
        String merchant = MERCHANT;
        String tradeNum = bean.getApplyid();//交易流水号
        String tradeName = "9188AddMoney";
        String tradeDesc = "9188jdpay";
        String tradeTime = DateTimeUtil.getCurrentFormatDate("yyyyMMddHHmmss");
        double s = PayUtil.getRound((bean.getAddmoney()) * 100,0);
        Double D1 = new Double(s);
        int addmoney = D1.intValue();
        String amount = addmoney + "";// 总金额，以分为单位
        String orderType = "1";//订单类型  0-实物，1-虚拟
        String currency = "CNY";
        String webcallbackurl = bean.getWebcallbackurl();
        log.info("充值成功后的返回页面:"+webcallbackurl);
        if(StringUtil.isEmpty(webcallbackurl)){
            webcallbackurl = defaultwebcallbackurl;
        }
        String callbackUrl = webcallbackurl;//页面回调
        String notifyUrl = NOTIFY_URL;//异步回调
        String ip = bean.getIpAddr(); //获取客户端的IP地址
        baseService.getUserIdenInfo(bean);//获取用户真实身份信息
        bean.setRsaprivatekey(RsaPrivateKey);
        bean.setDesKey(deskey);
        //todo 查询真实姓名 身份证号
        if(StringUtil.isEmpty(bean.getIdcard())||StringUtil.isEmpty(bean.getRealName())){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("未查询身份证信息，请确认是否实名");
            log.info("未查询到用户身份证信息  uid==" + bean.getUid());
            return null;
        }
        String expireTime = "1200";//订单的失效时长，单位：秒
        BasePayOrderInfo basePayOrderInfo = new BasePayOrderInfo();
        basePayOrderInfo.setVersion(version);
        basePayOrderInfo.setMerchant(merchant);
        basePayOrderInfo.setTradeNum(tradeNum);
        basePayOrderInfo.setTradeName(tradeName);
        basePayOrderInfo.setTradeDesc(tradeDesc);
        basePayOrderInfo.setTradeTime(tradeTime);
        basePayOrderInfo.setAmount(amount);
        basePayOrderInfo.setCurrency(currency);
        basePayOrderInfo.setCallbackUrl(callbackUrl);
        basePayOrderInfo.setNotifyUrl(notifyUrl);
        basePayOrderInfo.setIp(ip);
        basePayOrderInfo.setUserId(bean.getUserid());
        basePayOrderInfo.setExpireTime(expireTime);
        basePayOrderInfo.setOrderType(orderType);
        basePayOrderInfo.setSpecId(bean.getIdcard());
        basePayOrderInfo.setSpecName(bean.getRealName());
        basePayOrderInfo.toString();
        createSign(basePayOrderInfo,bean.getRsaprivatekey(),bean.getDesKey());
        sign = basePayOrderInfo.getSign();
        try {
            return makeOrderContent(bean, sign, basePayOrderInfo);
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("京东钱包充值失败");
            log.error("京东钱包充值失败,订单号:{},用户名:{}",bean.getApplyid(),bean.getUid(),e);
        }
        return null;
    }

    private RechDto makeOrderContent(PayBean bean, String sign, BasePayOrderInfo basePayOrderInfo) throws UnsupportedEncodingException {
        //发送POST请求
        String contents = "<meta http-equiv=\"Cache-Control\" content=\"no-cache\"/> \r\n";
        contents += "<form name=\"payForm1\" method=\"post\" action=\""+SAVEORDER_URL+"\">\r\n";
        contents += "<input type=\"hidden\" name=\"version\" value=\""+basePayOrderInfo.getVersion()+"\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"merchant\" value=\"" + basePayOrderInfo.getMerchant() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"tradeNum\" value=\"" + basePayOrderInfo.getTradeNum() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"tradeName\" value=\"" + basePayOrderInfo.getTradeName() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"tradeDesc\" value=\"" + basePayOrderInfo.getTradeDesc() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"tradeTime\" value=\"" + basePayOrderInfo.getTradeTime() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"amount\" value=\"" + basePayOrderInfo.getAmount() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"currency\" value=\"" + basePayOrderInfo.getCurrency() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"callbackUrl\" value=\"" + basePayOrderInfo.getCallbackUrl() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"notifyUrl\" value=\"" + basePayOrderInfo.getNotifyUrl() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"ip\" value=\"" + basePayOrderInfo.getIp() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"userId\" value=\"" + basePayOrderInfo.getUserId() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"expireTime\" value=\"" + basePayOrderInfo.getExpireTime() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"orderType\" value=\"" + basePayOrderInfo.getOrderType() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"specId\" value=\"" + basePayOrderInfo.getSpecId() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"specName\" value=\"" + basePayOrderInfo.getSpecName() + "\"/>\r\n";
        contents += "<input type=\"hidden\" name=\"sign\" value=\"" + sign + "\"/>\r\n";
        contents += "<input type=\"submit\" name=\"正在进入京东支付\"	value=\"正在进入京东支付\">\r\n";
        contents += "</form>\r\n";
        contents += "<script language=\"javascript\">document.payForm1.submit();</script>";
        RechDto rechDto=new RechDto();
        PayParam payParam=new PayParam();
        rechDto.setAddmoney(bean.getAddmoney());
        rechDto.setApplyid(bean.getApplyid());
        payParam.setPrepayHtml(URLEncoder.encode(contents,"UTF-8"));
        rechDto.setPayParam(payParam);
        return rechDto;
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }

    /**
     * 加密
     * @param basePayOrderInfo
     * @param signKey
     * @return
     */
    private void createSign(BasePayOrderInfo basePayOrderInfo, String signKey,String desKey) {
        filterCharProcess(basePayOrderInfo);
        String cert = CertUtil.getCert();
        // 有证书则证书验证模式、无则配置模式
        if (cert != null && !cert.equals("")) {
            basePayOrderInfo.setCert(cert);
        }
        List<String> unSignedKeyList = new ArrayList<String>();
        unSignedKeyList.add("sign");
        basePayOrderInfo.setSign(SignUtil.signRemoveSelectedKeys(basePayOrderInfo, signKey, unSignedKeyList));

        byte[] key = BASE64.decode(desKey);
        if (StringUtil.isNotEmpty(basePayOrderInfo.getDevice())) {
            basePayOrderInfo.setDevice(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getDevice()));
        }
        basePayOrderInfo.setTradeNum(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getTradeNum()));
        if (StringUtil.isNotEmpty(basePayOrderInfo.getTradeName())) {
            basePayOrderInfo.setTradeName(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getTradeName()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getTradeDesc())) {
            basePayOrderInfo.setTradeDesc(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getTradeDesc()));
        }
        basePayOrderInfo.setTradeTime(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getTradeTime()));
        basePayOrderInfo.setAmount(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getAmount()));
        basePayOrderInfo.setCurrency(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getCurrency()));
        if (StringUtil.isNotEmpty(basePayOrderInfo.getNote())) {
            basePayOrderInfo.setNote(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getNote()));
        }
        basePayOrderInfo.setCallbackUrl(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getCallbackUrl()));
        basePayOrderInfo.setNotifyUrl(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getNotifyUrl()));
        basePayOrderInfo.setIp(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getIp()));
        if (StringUtil.isNotEmpty(basePayOrderInfo.getUserType())) {
            basePayOrderInfo.setUserType(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getUserType()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getUserId())) {
            basePayOrderInfo.setUserId(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getUserId()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getExpireTime())) {
            basePayOrderInfo.setExpireTime(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getExpireTime()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getOrderType())) {
            basePayOrderInfo.setOrderType(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getOrderType()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getIndustryCategoryCode())) {
            basePayOrderInfo.setIndustryCategoryCode(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getIndustryCategoryCode()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getSpecCardNo())) {
            basePayOrderInfo.setSpecCardNo(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getSpecCardNo()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getSpecId())) {
            basePayOrderInfo.setSpecId(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getSpecId()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getSpecName())) {
            basePayOrderInfo.setSpecName(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getSpecName()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getVendorId())) {
            basePayOrderInfo.setVendorId(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getVendorId()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getGoodsInfo())) {
            basePayOrderInfo.setGoodsInfo(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getGoodsInfo()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getOrderGoodsNum())) {
            basePayOrderInfo.setOrderGoodsNum(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getOrderGoodsNum()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getTermInfo())) {
            basePayOrderInfo.setTermInfo(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getTermInfo()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getReceiverInfo())) {
            basePayOrderInfo.setReceiverInfo(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getReceiverInfo()));
        }
        if (StringUtil.isNotEmpty(basePayOrderInfo.getCert())) {
            basePayOrderInfo.setCert(ThreeDesUtil.encrypt2HexStr(key, basePayOrderInfo.getCert()));
        }
    }

    /**
     *
     * @Title: filterCharProcess
     * @Description: 特殊字符处理
     * @param: @param basePayOrderInfo
     * @return: void
     * @throws
     */
    private void filterCharProcess(BasePayOrderInfo basePayOrderInfo) {
        basePayOrderInfo.setVersion(doFilterCharProcess(basePayOrderInfo.getVersion()));
        basePayOrderInfo.setMerchant(doFilterCharProcess(basePayOrderInfo.getMerchant()));
        basePayOrderInfo.setDevice(doFilterCharProcess(basePayOrderInfo.getDevice()));
        basePayOrderInfo.setTradeNum(doFilterCharProcess(basePayOrderInfo.getTradeNum()));
        basePayOrderInfo.setTradeName(doFilterCharProcess(basePayOrderInfo.getTradeName()));
        basePayOrderInfo.setTradeDesc(doFilterCharProcess(basePayOrderInfo.getTradeDesc()));
        basePayOrderInfo.setTradeTime(doFilterCharProcess(basePayOrderInfo.getTradeTime()));
        basePayOrderInfo.setAmount(doFilterCharProcess(basePayOrderInfo.getAmount()));
        basePayOrderInfo.setCurrency(doFilterCharProcess(basePayOrderInfo.getCurrency()));
        basePayOrderInfo.setNote(doFilterCharProcess(basePayOrderInfo.getNote()));
        basePayOrderInfo.setCallbackUrl(doFilterCharProcess(basePayOrderInfo.getCallbackUrl()));
        basePayOrderInfo.setNotifyUrl(doFilterCharProcess(basePayOrderInfo.getNotifyUrl()));
        basePayOrderInfo.setIp(doFilterCharProcess(basePayOrderInfo.getIp()));
        basePayOrderInfo.setUserType(doFilterCharProcess(basePayOrderInfo.getUserType()));
        basePayOrderInfo.setUserId(doFilterCharProcess(basePayOrderInfo.getUserId()));
        basePayOrderInfo.setExpireTime(doFilterCharProcess(basePayOrderInfo.getExpireTime()));
        basePayOrderInfo.setOrderType(doFilterCharProcess(basePayOrderInfo.getOrderType()));
        basePayOrderInfo.setIndustryCategoryCode(doFilterCharProcess(basePayOrderInfo.getIndustryCategoryCode()));
        basePayOrderInfo.setSpecCardNo(doFilterCharProcess(basePayOrderInfo.getSpecCardNo()));
        basePayOrderInfo.setSpecId(doFilterCharProcess(basePayOrderInfo.getSpecId()));
        basePayOrderInfo.setSpecName(doFilterCharProcess(basePayOrderInfo.getSpecName()));
        basePayOrderInfo.setVendorId(doFilterCharProcess(basePayOrderInfo.getVendorId()));
        basePayOrderInfo.setGoodsInfo(doFilterCharProcess(basePayOrderInfo.getGoodsInfo()));
        basePayOrderInfo.setOrderGoodsNum(doFilterCharProcess(basePayOrderInfo.getOrderGoodsNum()));
        basePayOrderInfo.setTermInfo(doFilterCharProcess(basePayOrderInfo.getTermInfo()));
        basePayOrderInfo.setReceiverInfo(doFilterCharProcess(basePayOrderInfo.getReceiverInfo()));
    }

    /**
     *
     * @Title: doFilterCharProcess
     * @Description: 执行特殊字符处理
     * @param: @param param
     * @param: @return
     * @return: String
     * @throws
     */
    private String doFilterCharProcess(String param) {
        if (param == null || param.equals("")) {
            return param;
        } else {
            return StringEscape.htmlSecurityEscape(param);
        }
    }
}
