package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSON;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.dto.RechDto;
import pay.pojo.PayParam;
import sun.misc.BASE64Decoder;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

import static pay.constant.PayConstant.NOTIFY_HOST;

/**
 *  连连支付wap
 *
 */
@Slf4j
@Component("Bank_llzf_h5")
public class Bank_llzf_h5 implements IBankCardRech{

    // 商家应用唯一标识
    private final static String MerchantNo="201406031000001272";

    private final static String patternZfb = "yyyyMMddHHmmss";

    private final static String order_url= "https://wap.lianlianpay.com/payment.htm";

    private final static String notify_url=NOTIFY_HOST +"/pay/llzf_h5_notify.api";

    private final static String defalut_callback_url="http://t2015.9188.com/user/mlottery.go";

    /** 商户私钥 商户通过openssl工具生成公私钥，公钥通过商户站上传，私钥用于加签，替换下面的值 . */
    private static final String BUSINESS_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANDfNVlmVMH7ygcaGgWkH+Xl3UNpIfqBnqEZYIuB9z2GqRI10dFNHvmpPz3Zud9/ucBKvICsG0a8UhsJFC+8RuSVdxop3EPJjydcxMyqxZGZw1uL/ALVwHrRvhPLTdPJNwwZSQRA/Nw0ZXLV7vyQoV2/FtBR6x2phmtp1YkYk8S/AgMBAAECgYBLJsRvMh503T9HZuDN/strUFVYF8+d7l1A4XyXNV/bx0O1xZ+EixcGBRs4CcqS28XdpgZE1afKv59bEt0sf7WRimlsx6U3nNIAZ00hf11+rLQ5ZKV6/aCLPMi7eoe4CiquHzC7JUq3ry/SS8t343kkxI6cuxcCvMI2DTfJUupeQQJBAPZhu8ezEr/mtnsymevJkWH0ep7QwE7w84ENVRVwbTT5ZerxzEKB1MBtrVXX6ADoa8+Mil/UW4XUY2xFrJqcS6cCQQDZBpzGoOInIIu0ZAbsVS1QfITaP0iSlZaLJJq3vaveFHpBonOj6nOFbkREUOJ1ZQrM+rDX8ulbLzMei9akwAEpAkBZJrAt6IwmSDNLjNnJSdyDV1VvVhXf+qwBzHM3GMFLY9sPEoNYpWX4YzdPUpquiWtJZI9Fca/UUy/Oo797CocNAkByYDzBpcpqSom2IT1Q0jcWgvlVOCLcNf/oBvuPOGPan7oq2x3M1mPZf1p1Epe2vPCrRLdsp/V5++8Z+yK9Iv+5AkBFLzd8eirzzcjoZGU23CA57pU9iQ+sqjzNY7MpRqAtaryIPjb3tc1no49QTRxotTvI0Zp0S5sKX1vuj0kGNARo";

    @Autowired
    private NotifyService notifyService;

    @Override
    public RechDto addmoney(PayBean bean) {
        try {
            if(StringUtil.isEmpty(bean.getWebcallbackurl())){
               bean.setWebcallbackurl(defalut_callback_url);
            }
            String payHtml=organizeOrder(bean);
            RechDto rechDto=new RechDto();
            PayParam payParam=new PayParam();
            rechDto.setAddmoney(bean.getAddmoney());
            rechDto.setApplyid(bean.getApplyid());
            payParam.setPrepayHtml(URLEncoder.encode(payHtml,"UTF-8"));
            rechDto.setPayParam(payParam);
            return rechDto;
        } catch (Exception e) {
            log.error("用户:{} 请求连连支付下单失败,订单号:{}",bean.getUid(),bean.getApplyid());
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("连连支付下单失败，请重新尝试");
        }
        return null;
    }

    private String organizeOrder(PayBean bean){
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("version","1.1");
        paramMap.put("oid_partner",MerchantNo);//商户号
        paramMap.put("app_request","3");
        paramMap.put("user_id",bean.getUserid());
        paramMap.put("sign_type","RSA");
        paramMap.put("busi_partner","101001");
        paramMap.put("no_order",bean.getApplyid());
        paramMap.put("dt_order",new SimpleDateFormat(patternZfb).format(new Date()));
        paramMap.put("name_goods","9188充值");
        paramMap.put("money_order",bean.getAddmoney()+"");
        paramMap.put("notify_url",notify_url);
        paramMap.put("url_return",bean.getWebcallbackurl());
        paramMap.put("id_no",bean.getIdcard());//身份证号
        paramMap.put("acct_name",bean.getRealName());//真实姓名
        paramMap.put("card_no",bean.getCardNo());//银行卡号
        Map<String,Object> riskParamMap=new HashMap<>();
        riskParamMap.put("frms_ware_category","1002");
        riskParamMap.put("user_info_bind_phone",bean.getMobileNo());
        riskParamMap.put("user_info_dt_register",new SimpleDateFormat(patternZfb).format(new Date()));
        riskParamMap.put("risk_state","1");
        paramMap.put("risk_item", JSON.toJSONString(riskParamMap));//风险控制参数
        String signStr=getSignData(paramMap);
        log.info("连连支付H5,用户:{},签名字符串:{}",bean.getUid(),signStr);
        String sign=sign(BUSINESS_PRIVATE_KEY,signStr);//签名字符串
        paramMap.put("sign",sign);
        String requestStr=JSON.toJSONString(paramMap);//支付请求json字符串
        return mkPayHtml(requestStr);
    }

    private String mkPayHtml(String requestStr) {
        String contents = "<meta http-equiv=\"Cache-Control\" content=\"no-cache\"/> \r\n";
        contents += "<form name=\"payForm1\" method=\"post\" action=\""+order_url+"\">\r\n";
        contents += "<input type=\"hidden\" name=\"req_data\" value=\'"+requestStr+"\'/>\r\n";
        contents += "<input type=\"submit\" name=\"正在进入连连支付\"	value=\"正在进入连连支付\">\r\n";
        contents += "</form>\r\n";
        contents += "<script language=\"javascript\">document.payForm1.submit();</script>";
        return contents;
    }


    @Override
    public void backNotify(PayBean bean) {
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }


    public static String getSignData(Map<String, String> params) {
        StringBuffer content = new StringBuffer();

        // 按照key做首字母升序排列
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            // sign 和ip_client 不参与签名
            if ("sign".equals(key)) {
                continue;
            }
            String value = params.get(key);
            // 空串不参与签名
            if (StringUtil.isEmpty(value)) {
                continue;
            }
            content.append((i == 0 ? "" : "&") + key + "=" + value);

        }
        String signSrc = content.toString();
        if (signSrc.startsWith("&")) {
            signSrc = signSrc.replaceFirst("&", "");
        }
        return signSrc;
    }

    public static String sign(String prikeyvalue, String sign_str)
    {
        try
        {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    getBytesBASE64(prikeyvalue));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey myprikey = keyf.generatePrivate(priPKCS8);
            // 用私钥对信息生成数字签名
            java.security.Signature signet = java.security.Signature
                    .getInstance("MD5withRSA");
            signet.initSign(myprikey);
            signet.update(sign_str.getBytes("UTF-8"));
            byte[] signed = signet.sign(); // 对信息的数字签名
            return new String(
                    org.apache.commons.codec.binary.Base64.encodeBase64(signed));
        } catch (Exception e)
        {
        }
        return null;
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static byte[] getBytesBASE64(String s)
    {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            byte[] b = decoder.decodeBuffer(s);
            return b;
        } catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public RechDto agreePay(PayBean bean) throws Exception {
        return null;
    }
}
