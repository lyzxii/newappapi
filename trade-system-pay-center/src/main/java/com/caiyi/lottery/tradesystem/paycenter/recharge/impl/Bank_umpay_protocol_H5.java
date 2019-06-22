package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.umpay.api.common.ReqData;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component("Bank_umpay_protocol_H5")
public class Bank_umpay_protocol_H5 implements IBankCardRech {

    @Autowired
    NotifyService notifyService;

    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
    private static String defaultwebcallbackurl = "https://5.9188.com/new/#/recharge/result";
    private static String SERVICE = "pay_req_h5_frontpage";
    private static String CHAR_SET = "UTF-8"; //编码格式
    private static String SIGN_TYPE = "RSA"; //签名方式
    private static String VERSION = "4.0"; //版本号
    private static String AMT_TYPE = "RMB"; //付款币种
    private static String MER_ID = "6329"; //商户编号
    private static String RES_FORMAT = "HTML";//数据格式
    private static String GOODS_ID = "9188";
    private static String GOODS_INF = "9188pay";
    private static String can_modify_flag = "0";//是否允许用户修改支付要素   0：不允许修改
    private static String NOTIFY_URL = PayConstant.NOTIFY_HOST + "/pay/umpayh5_notify_api.api";

    //联动优势bankCode与本地bankCode不同的映射
    private static final Map<String, String> bankCodeMap = new HashMap<String, String>();
    public static HashMap<String, String> UMPAY_CARD_TYPE = new HashMap<String, String>();

    static {
        bankCodeMap.put("SPAB", "SZPAB");//平安银行
        bankCodeMap.put("NBB", "NBCB");//宁波银行
        bankCodeMap.put("HSB", "HSBANK");//徽商银行
        bankCodeMap.put("HZCB", "HCCB");//杭州银行
        bankCodeMap.put("SHB", "BOS");//上海银行
        bankCodeMap.put("BJB", "BCCB");//北京银行

        UMPAY_CARD_TYPE.put("CREDITCARD", "信用卡");
        UMPAY_CARD_TYPE.put("DEBITCARD", "借记卡");
    }

    public static void main(String[] args) {
        Bank_umpay_protocol_H5 pay = new Bank_umpay_protocol_H5();
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
        RechDto rechDto = new RechDto();
        try {
            String h5Url = getH5Url(bean);//获取H5收银台地址
            log.info("联动收银台-->支付请求接口（H5收银台）,applyid==" + bean.getApplyid() + ",h5url==" + h5Url);
            rechDto.setAddmoney(bean.getAddmoney());
            rechDto.setApplyid(bean.getApplyid());
            PayParam param = new PayParam();
            param.setPrepayUrl(URLEncoder.encode(h5Url,"UTF-8"));
            JSONObject json = new JSONObject();
            json.put("callBackUrl", defaultwebcallbackurl);
            param.setPrepayContent(json);
            rechDto.setPayParam(param);
            log.info("联动收银台返回前端信息，rechDto==" + rechDto);
            return rechDto;
        } catch (Exception e) {
            log.error("联动收银台-->支付请求接口（H5收银台）,applyid==" + bean.getApplyid() + ",失败", e);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("支付请求失败");
        }
        return rechDto;
    }

    private String getH5Url(PayBean bean) throws Exception {
        log.info("联动收银台-->支付请求接口（H5收银台）,uid==" + bean.getUid());
        Map<String, String> paramMap = craeteParamMap(bean);
        log.info("联动收银台-->getH5Url组织参数==>" + paramMap);
        String html = sendRequest(bean, paramMap);
        if (StringUtil.isEmpty(html)) {
            log.info("联动收银台-->支付请求接口（H5收银台）结果为空，applyid==" + bean.getApplyid() + ",uid==" + bean.getUid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("请求支付出了点小问题");
            return "";
        }
        return html;
    }

    private String sendRequest(PayBean bean, Map<String, String> reqMap) throws Exception {
        ReqData reqDataGet = Mer2Plat_v40.makeReqDataByGet(reqMap);
        log.info("联动优势-->用户" + bean.getUid() + "订单" + bean.getApplyid() + "联动优势[下单]返回结果==>" + reqDataGet);
        if (reqDataGet != null) {
            List list = URLGet(reqDataGet.getUrl());
            String html = list.toString();
            int indexOf = html.indexOf("\"");
            int lastIndexOf = html.lastIndexOf("\"");
            String substring = html.substring(indexOf + 1, lastIndexOf);
            return substring;
        } else {
            log.info("联动优势-->用户" + bean.getUid() + "订单" + bean.getApplyid() + "联动优势[下单]返回结果为空");
            return "";
        }
    }

    public static List URLGet(String strUrl) throws IOException {
        List result = new ArrayList();
        URL url = new URL(strUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setUseCaches(false);
        con.setFollowRedirects(true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            else {
                result.add(line);
            }
        }
        in.close();
        return (result);
    }

    private Map<String, String> craeteParamMap(PayBean bean) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("service", SERVICE);
        paramMap.put("charset", CHAR_SET);
        paramMap.put("mer_id", getMerId(bean));
        paramMap.put("sign_type", SIGN_TYPE);
        paramMap.put("notify_url", NOTIFY_URL);
        paramMap.put("version", VERSION);
        paramMap.put("ret_url", getWecallbackurl(bean));
        paramMap.put("res_format", RES_FORMAT);
        paramMap.put("order_id", bean.getApplyid());
        paramMap.put("mer_date", fmt.format(new Date()));
        paramMap.put("amount", getAmount(bean));
        paramMap.put("amt_type", AMT_TYPE);
        paramMap.put("goods_id", GOODS_ID);
        paramMap.put("goods_inf", GOODS_INF);
        paramMap.put("card_id", bean.getCardNo());
        paramMap.put("identity_type", "1");
        paramMap.put("identity_code", bean.getIdcard());
        paramMap.put("card_holder", bean.getRealName());
        paramMap.put("mer_cust_id", getMerCustId(bean.getUserid()));
        paramMap.put("user_ip", bean.getClientIp());
        paramMap.put("expire_time", "1440");//订单过期时长  单位为分钟，默认1440分钟（24小时）
        paramMap.put("can_modify_flag", can_modify_flag);
        return paramMap;

    }

    private static String getMerCustId(String cuserId) {
        String result = "";
        if (cuserId.length() >= 26) {
            result = cuserId.replaceAll("-", "");
        } else {
            result = "umpay_" + cuserId;
        }
        return result;
    }

    private String getAmount(PayBean bean) {
        //  订单金额
        // /以分为单位，必须是整型数字
        // /比方2，代表0.02元
        double s = getRound((bean.getAddmoney() + bean.getHandmoney()) * 100, 0);
        Double D1 = new Double(s);
        int addmoney = D1.intValue();
        String amount = addmoney + "";// 总金额，以分为单位
        return amount;
    }

    public static double getRound(double m, int num) {
        BigDecimal dec = new BigDecimal(m);
        BigDecimal one = new BigDecimal("1");
        return dec.divide(one, num, BigDecimal.ROUND_CEILING).doubleValue();
    }

    private String getWecallbackurl(PayBean bean) {
        if (StringUtil.isEmpty(bean.getWebcallbackurl())) {
            return defaultwebcallbackurl;
        }
        return bean.getWebcallbackurl();
    }

    private String getMerId(PayBean bean) {
        boolean flag = StringUtil.isEmpty(bean.getMerchantId());
        if (flag) {
            bean.setMerchantKey("");
            bean.setMerchantId(MER_ID);
            return MER_ID;
        }
        if(StringUtil.isEmpty(bean.getMerchantKey())){
            bean.setMerchantKey("");
        }
        return bean.getMerchantId();
    }

    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.updateRechargeCard(bean);
        notifyService.applyAccountSuc(bean);
    }

    @Override
    public RechDto agreePay(PayBean bean) throws Exception {
        return null;
    }
}
