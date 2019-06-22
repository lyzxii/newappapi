package com.caiyi.lottery.tradesystem.payweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.client.PayBasicInterface;
import com.caiyi.lottery.tradesystem.paycenter.client.PayNotifyInterface;
import com.caiyi.lottery.tradesystem.payweb.service.NotifyWebService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.sign.DES;
import com.caiyi.lottery.tradesystem.util.sign.MD5;
import com.caiyi.lottery.tradesystem.util.sign.RSA;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import com.jd.jr.pay.gate.signature.util.JdPayUtil;
import com.lianpay.api.util.TraderRSAUtil;
import com.umpay.api.paygate.v40.Mer2Plat_v40;
import com.umpay.api.paygate.v40.Plat2Mer_v40;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pay.bean.PayBean;
import pay.constant.RechargeTypeConstant;
import pay.pojo.jdpay.AsynNotifyResponse;
import pay.util.PayUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@Service
public class NotifyWebServiceImpl implements NotifyWebService {

    private static final String charset = "UTF-8";

    //京东钱包
    private static final String jdh5_RsaPublickey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKE5N2xm3NIrXON8Zj19GNtLZ8xwEQ6uDIyrS3S03UhgBJMkGl4msfq4Xuxv6XUAN7oU1XhV3/xtabr9rXto4Ke3d6WwNbxwXnK5LSgsQc1BhT5NcXHXpGBdt7P8NMez5qGieOKqHGvT0qvjyYnYA29a8Z4wzNR7vAVHp36uD5RwIDAQAB";

    private static final String jdh5_deskey="L9l/kh8OkQKwVKEfXiZFa+qSbVtdFQIZ";

    //连连支付
    private static final String ll_RsaPublickey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSS/DiwdCf/aZsxxcacDnooGph3d2JOj5GXWi+q3gznZauZjkNP8SKl3J2liP0O6rU/Y/29+IUe+GTMhMOFJuZm1htAtKiu5ekW0GlBMWxf4FPkYlQkPE0FtaoMP3gYfh+OwI+fIRrpW3ySn3mScnc6Z700nU/VYrRkfcSCbSnRwIDAQAB";

    //京东快捷
    private static final String jdfast_mechkey="XorkapfpGbqQlLqxQxdgpyrvFTfMjKTb";

    private static final String jdfast_deskey="p2iSGSA9kQt8QCnsXRbcbel2VEof3Orx";

    //盛付通支付宝sdk
    private static final String swiftpass_mch_id = "105590063681";//测试商户号
    private static final String swiftpass_mch_key = "b962c8d61aa85b78f022851708c6a011";//测试密钥

    //合利宝快捷支付
    private static final String heli_MD5_KEY = "ByNvtb4QGYtu0dTc1gSLKCXksiYB4acy";// md5秘钥


    @Autowired
    PayNotifyInterface payNofifyInterface;

    @Autowired
    PayBasicInterface payBasicInterface;


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


    @Override
    public void ldpay_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> reqMap = getParamMap(request);
        // 获取UMPAY平台请求商户的支付结果通知数据,并对请求数据进行验签
        Map<String, String> reqData = Plat2Mer_v40.getPlatNotifyData(reqMap);
        log.info("联动优势-->回调验签结果Map==>" + reqData.toString());
        Map<String, String> resData = createResData(reqMap);
        putValues(bean,reqMap);
        if (!"0000".equals(reqData.get("error_code"))) {
            log.info("联动优势-->回调验签失败applyid=="+bean.getApplyid()+"，error_code==>" + reqData.get("error_code"));
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("回调验签失败");
            return;
        }
        if ("TRADE_SUCCESS".equals(reqMap.get("trade_state"))) {
            resData.put("ret_code", "0000");
            resData.put("ret_msg", "OK");
            write_response(resData, response);
            queryBankId(bean);
            //回调接口
            BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
            bean.setClassName("Bank_ldpay_fast");
            bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
            req.setData(bean);
            payNofifyInterface.basicNotify(req);
            // 充值业务返回成功标记
        } else {
            resData.put("ret_code", "1111");
            resData.put("ret_msg", "Error");
            write_response(resData, response);
        }
    }

    private void queryBankId(PayBean bean) {
        BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
        req.setData(bean);
        BaseResp<PayBean> resp = payBasicInterface.queryBankId(req);
        if(null != resp&&(BusiCode.SUCCESS).equals(resp.getCode())){
            bean.setBankid(resp.getData().getBankid());
            bean.setUid(resp.getData().getUid());
            bean.setSafeKey(resp.getData().getSafeKey());
            bean.setCardNo(resp.getData().getCardNo());
            if(StringUtil.isEmpty(bean.getDealid())){
                bean.setDealid(resp.getData().getDealid());
            }
            bean.setMerchantId(resp.getData().getMerchantId());
        }else{
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }
    }

    private void putValues(PayBean bean,Map<String,String> reqData) {
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
        double d_total_fee = Double.parseDouble(reqData.get("amount"));
        String Amount = (d_total_fee / 100) + "";
        log.info("Bank_ldpay_fast.notify: money==" + d_total_fee + " applyid==" + reqData.get("order_id"));
        bean.setAddmoney(Double.parseDouble(Amount));
        bean.setApplyid(reqData.get("order_id"));
        bean.setDealid(reqData.get("trade_no"));
        String payType = reqData.get("pay_type");

        if("DEBITCARD".equals(payType)){
            bean.setCardtype(0);
        };
        if("CREDITCARD".equals(payType)){
            bean.setCardtype(1);
        };

        bean.setBankCode(reqData.get("gate_id"));
        bean.setLastFourCardNum(reqData.get("last_four_cardid"));

        bean.setUserbusiid(reqData.get("usr_busi_agreement_id"));//用户业务协议号
        bean.setUserpayid(reqData.get("usr_pay_agreement_id"));//支付协议号
    }

    @Override
    public void ipayNow_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取通知数据需要从body中流式读取
        BufferedReader reader = request.getReader();
        StringBuilder reportBuilder = new StringBuilder();
        String tempStr = "";
        while ((tempStr = reader.readLine()) != null) {
            reportBuilder.append(tempStr);
        }
        String reportContent = reportBuilder.toString();
        log.info("现在支付支付宝回调通知:" + reportContent);
        Map<String, String> dataMap = PayUtil.parseFormDataByDecode(reportContent, "UTF-8", "UTF-8");
        //去除签名类型和签名值
        String signature = dataMap.remove("signature");
        String mhtSignture = PayUtil.getFormDataParamMD5(dataMap, bean.getDesKey(), signature);
        //验证签名
        boolean isValidSignature = mhtSignture.equalsIgnoreCase(signature);
        String order_id = dataMap.get("mhtOrderNo");
        if (isValidSignature) {
            String transStatus = dataMap.get("transStatus");
            if ("A001".equals(transStatus)) {
                log.info("现在支付-->回调验签成功,订单号[" + order_id + "]");
                queryBankId(bean);
                bean.setClassName("Bank_ipaynow");
                putIpayNowValues(bean, dataMap);
                BaseReq req = new BaseReq(SysCodeConstant.PAYWEB);
                req.setData(bean);
                payNofifyInterface.basicNotify(req);

                response.getOutputStream().write("success=Y".getBytes());
                response.getOutputStream().close();
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("充值失败");
                log.info("订单支付失败,订单状态:" + transStatus + " 订单号:" + bean.getApplyid());
            }
        } else {
            log.info("现在支付-->回调验签失败,订单号[" + dataMap.get("mhtOrderNo") + "]");
            response.getOutputStream().write("success=N".getBytes());
        }

    }

    private void putIpayNowValues(PayBean bean, Map<String, String> dataMap) {
        String amount = dataMap.get("mhtOrderAmt");
        String realAmount = Double.parseDouble(amount) / 100 + "";
        String applyid = dataMap.get("mhtOrderNo");
        log.info("现在支付["+applyid+"]实际扣款金额：[" + realAmount + "]");
        bean.setAddmoney(Double.parseDouble(realAmount));
        bean.setApplyid(applyid);
        bean.setDealid(dataMap.get("nowPayOrderNo"));
    }

    @Override
    public void shengpPay_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> name2Value = new HashMap<String, String>();
        getShengPayParam(request, name2Value);
        StringBuilder values = new StringBuilder();
        for (String key : notifyParams) {
            if (!StringUtil.isEmpty(name2Value.get(key))) {
                values.append(name2Value.get(key)).append("|");
            }
        }
        putSftValues(bean,name2Value);
        String signMsg = request.getParameter("SignMsg").trim();//签名字符串
        String orderNo = request.getParameter("OrderNo").trim();//商户订单号
        String signMsgVal = values.toString();
        String notifyPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC69veKW1X9GETEFr49gu9PN8w7H6alWec8wmF8SoP3tqQLAflZp8g83UZPX2UWhClnm53P5ZwesaeSTHkXkSI0iSjwd27N07bc8puNgB5BAGhJ80KYqTv3Zovl04C8AepVmxy9iFniJutJSYYtsRcnHYyUNoJai4VXhJsp5ZRMqwIDAQAB";
        log.info("sft-notify_" + orderNo + ",同步返回签名参数signMsgVal：" + signMsgVal + ", 获取到签名串signMsg：" + signMsg);
        boolean signResult = RSA.verify(signMsgVal, signMsg, notifyPublicKey, "utf-8");
        log.info("本地签名merchantSignMsgResult：" + signResult);
        response.setHeader("Cache-Control", "no-cache");
        String transStatus = request.getParameter("TransStatus").trim();
        String transNo = request.getParameter("TransNo").trim();//盛付通交易号
        String outResult = "FAIL";
        if (signResult && "01".equals(transStatus)) {
            outResult = "OK";
            response.getWriter().write(outResult);
            response.getWriter().close();
            queryBankId(bean);
            //回调接口
            BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
            bean.setClassName("Bank_sft_api");
            req.setData(bean);
            payNofifyInterface.basicNotify(req);
        } else {
            outResult = "Error";
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc(request.getParameter("ErrorMsg"));
            log.info("盛付通支付回调通知,支付失败,nickid=" + bean.getUid() + ",applyid=" + bean.getApplyid() + ",sftTransNo=" + transNo + ",signResult=" + signResult + ",transStatus=" + transStatus + ",ErrorMsg=" + request.getParameter("ErrorMsg"));
            response.getWriter().write(outResult);
        }

    }

    private void putSftValues(PayBean bean, Map<String, String> name2Value) {
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
        double d_total_fee = Double.parseDouble(name2Value.get("TransAmount"));
//        String Amount = (d_total_fee / 100) + "";
        log.info("Bank_sft_api.notify: money==" + d_total_fee + " applyid==" + name2Value.get("order_id"));
        bean.setAddmoney(d_total_fee);
        bean.setApplyid(name2Value.get("OrderNo"));

        bean.setBankCode(name2Value.get("InstCode"));
        bean.setDealid(name2Value.get("TransNo"));

    }

    @Override
    public void umpayh5_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> reqMap = getParamMap(request);
        // 获取UMPAY平台请求商户的支付结果通知数据,并对请求数据进行验签
        Map<String, String> reqData = Plat2Mer_v40.getPlatNotifyData(reqMap);
        log.info("联动优势收银台-->回调验签结果Map==>" + reqData.toString());
        Map<String, String> resData = createResData(reqMap);
        putValues(bean, reqData);
        if (!"0000".equals(reqData.get("error_code"))) {
            log.info("联动优势收银台-->回调验签失败applyid==" + bean.getApplyid() + "，error_code==>" + reqData.get("error_code"));
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("回调验签失败");
            return;
        }
        if ("TRADE_SUCCESS".equals(reqMap.get("trade_state"))) {
            resData.put("ret_code", "0000");
            resData.put("ret_msg", "OK");
            write_response(resData, response);
            queryBankId(bean);
            //回调接口
            BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
            bean.setClassName("Bank_umpay_protocol_H5");
            bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
            req.setData(bean);
            payNofifyInterface.basicNotify(req);
            // 充值业务返回成功标记
        } else {
            resData.put("ret_code", "1111");
            resData.put("ret_msg", "Error");
            write_response(resData, response);
        }
    }

    private void getShengPayParam(HttpServletRequest request, Map<String, String> name2Value) {
        Enumeration<String> names = request.getParameterNames();
        String name = null;
        String value = null;
        while (names.hasMoreElements()) {
            name = names.nextElement();
            if (StringUtil.isEmpty(name)) {
                continue;
            }
            if (notifyParams.contains(name)) {
                value = request.getParameter(name);
                log.info("回调通知请求参数" + name + "=" + value);
                value = StringUtil.isEmpty(value) ? "" : value;
                name2Value.put(name, value);
            }
        }
        log.info("盛付通支付回调开始，回调通知参数列==>"+name2Value);
    }


    private Map<String, String> createResData(Map<String, String> ht) {
        Map<String, String> resData = new HashMap<>();
        resData.put("mer_id", ht.get("mer_id"));
        resData.put("sign_type", ht.get("sign_type"));
        resData.put("version", ht.get("version"));
        resData.put("order_id", ht.get("order_id"));
        resData.put("mer_date", ht.get("mer_date"));
        return resData;
    }

    private void write_response(Map<String, String> resData, HttpServletResponse response) throws Exception {
        String data = Mer2Plat_v40.merNotifyResData(resData);
        String contents = "";
        contents = "<META NAME=\"MobilePayPlatform\" CONTENT=\"" + data + "\" />";
        response.getWriter().write(contents);
        response.getWriter().close();
    }

    /**
     * 获取request参数到Map
     *
     * @param request
     * @return
     */
    private Map<String, String> getParamMap(HttpServletRequest request) {
        Map requestParams = request.getParameterMap();
        log.info("联动优势回调通知payWeb开始开始，参数=="+requestParams);
        Map<String, String> ht = new HashMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            log.info("联动优势回调函数参数 :" + name + " == " + valueStr);
            ht.put(name, valueStr);
        }
        return ht;
    }


    @Override
    public void plbWebNotify(PayBean bean){
        String transData=bean.getParamString();
        TreeMap<String, String> paramsMap = JSONObject.parseObject(transData,
                new TypeReference<TreeMap<String, String>>() {
                });
        String sign = paramsMap.get("sign");
        String orderNo =  paramsMap.get("outTradeNo");
        String payNo=paramsMap.get("payNo");
        bean.setApplyid(orderNo);// 订单号
        queryBankId(bean);//根据订单号查询用户名、bankid、safekey
        bean.setDealid(payNo);//第三方单号
        int state = Integer.valueOf(paramsMap.get("state"));
        double addmoney = Double.parseDouble(paramsMap.get("payAmount"));
        bean.setAddmoney(addmoney / 100);// 充值金额
        if (state != 1) {// 返回状态
            log.error("订单编号:{},充值金额:{},派洛贝微信H5支付回调显示支付失败", orderNo, bean.getAddmoney());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            return;
        }
        paramsMap.remove("sign");
        if (!StringUtil.isEmpty(sign)){// 验证签名
            bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_WEIXIN);
            PayUtil.ReadAccountInfo(bean);//从配置文件读取秘钥信息
            String localSign = PayUtil.makeSign(paramsMap,bean.getMerchantKey());;
            if (!sign.equals(localSign)) {
                log.error("订单编号:{},充值金额:{},派洛贝微信H5支付回调签名错误,返回签名:{},本地签名:{}",
                        orderNo, bean.getAddmoney(), sign, localSign);
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            }
        }else{
            log.error("订单编号:{},充值金额:{},派洛贝微信H5支付回调,签名为空",orderNo, bean.getAddmoney());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }
    }

    @Override
    public void swiftpass_notify(PayBean bean) throws Exception {
        String reportContent=bean.getParamString();
        Map<String,String> map = XmlUtil.toMap(reportContent.getBytes(), "utf-8");
        String amount = map.get("total_fee");
        String realAmount = Double.parseDouble(amount)/100+"";
        String applyid = map.get("out_trade_no");
        bean.setAddmoney(Double.parseDouble(realAmount));
        bean.setApplyid(applyid);
        queryBankId(bean);//根据订单号查询用户名、bankid、safekey
        if("0".equals(map.get("status")) && "0".equals(map.get("result_code"))){
            String respSign = map.get("sign");
            map.remove("sign");
            PayUtil.ReadAccountInfo(bean);//从配置文件读取秘钥信息
            String sign = PayUtil.makeSign(map,bean.getMerchantKey());
            if(!respSign.equalsIgnoreCase(sign)){
                log.error("订单编号:{},充值金额:{},派洛贝微信支付回调签名错误,返回签名:{},本地签名:{}",
                        applyid, bean.getAddmoney(), respSign,sign);
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            }
        }else{
            log.error("订单编号:{},充值金额:{},派洛贝微信支付回调显示支付失败", applyid, bean.getAddmoney());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }
    }

    /**
     *京东支付不支持从配置文件读取秘钥信息
     */
    @Override
    public void jdpay_fast_notify(PayBean bean) throws Exception{
        String xmlResult=bean.getParamString();
        JXmlWrapper xml = JXmlWrapper.parse(xmlResult);
        String respData =  xml.getStringValue("DATA");
        String version = xml.getStringValue("VERSION");
        String merchant = xml.getStringValue("MERCHANT");
        String terminal = xml.getStringValue("TERMINAL");
        String decData = DES.decrypt(respData,jdfast_deskey,charset);
        JXmlWrapper dataXml = JXmlWrapper.parse(decData);
        //获取return数据
        JXmlWrapper returnData = dataXml.getXmlNode("RETURN");
        String returnCode = returnData.getStringValue("CODE");
        String returnDesc = returnData.getStringValue("DESC");
        log.info("京东快捷支付异步回调内容:xmlResult:"+xmlResult+" decData:"+decData+" 返回Code:"+returnCode+ " 返回信息:"+returnDesc);
        if(!"0000".equals(returnCode)){
            log.error("京东快捷支付异步回调显示失败,具体内容:{}",returnDesc);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            return ;
        }
        //获取trade数据
        JXmlWrapper tradeData = dataXml.getXmlNode("TRADE");
        String applyid = tradeData.getStringValue("ID");
        String amount = tradeData.getStringValue("AMOUNT");
        String status = tradeData.getStringValue("STATUS");
        //获取返回的签名
        String returnSign = xml.getStringValue("SIGN");
        //本地签名
        String sign = MD5.sign(version+merchant+terminal+respData,jdfast_mechkey,charset).toLowerCase();
        if(!returnSign.equals(sign)){
            log.info("京东快捷支付签名验签失败,返回签名:"+returnSign+" 本地签名:"+sign+" 订单号:"+applyid);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }else {
            if ("0".equals(status)) {
                bean.setApplyid(applyid);
                queryBankId(bean);//根据订单号查询用户名、bankid、safekey
                bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
                PayUtil.ReadAccountInfo(bean);
                String realAmount = Double.parseDouble(amount)/100+"";
                bean.setAddmoney(Double.parseDouble(realAmount));
            }else{
                log.error("订单编号:{},充值金额:{},京东快捷支付回调显示支付失败", applyid, bean.getAddmoney());
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            }
        }
    }

    /**
     *京东支付不支持从配置文件读取秘钥信息
     */
    @Override
    public void jdpay_h5_notify(PayBean bean) throws Exception{
        String sb=bean.getParamString();
        AsynNotifyResponse anRes = null;
        try {
            anRes = JdPayUtil.parseResp(jdh5_RsaPublickey,jdh5_deskey, sb, AsynNotifyResponse.class);
            log.info("京东钱包异步通知解析数据:" + anRes);
            log.info("京东钱包异步通知订单号：" + anRes.getTradeNum() + ",状态：" + anRes.getStatus() + "成功!!!!");
        } catch (Exception e) {
            log.error("京东钱包解析回调通知XML参数错误", e);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            return ;
        }
        String code = anRes.getResult().getCode();
        if("000000".equals(code)){
            String status = anRes.getStatus();
            if(!"2".equals(status)){
                log.error("订单编号:{},充值金额:{},京东钱包回调显示支付失败",anRes.getTradeNum(), bean.getAddmoney());
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                return ;
            }
            String orderNo = anRes.getTradeNum();//订单号
            String amount = anRes.getAmount() + "";//金额  单位：分
            String merchant2 = anRes.getMerchant();//商户号
            bean.setApplyid(orderNo);
            queryBankId(bean);//根据订单号查询用户名、bankid、safekey
            bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_OTHER);
            PayUtil.ReadAccountInfo(bean);
            bean.setAddmoney(Double.parseDouble(amount)/100);
            bean.setDealid(merchant2);
        }else{
            log.error("订单编号:{},充值金额:{},京东钱包回调显示支付异常,code:{}",
                    anRes.getTradeNum(), bean.getAddmoney(),code);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }
    }

    @Override
    public void llzf_app_notify(PayBean bean) throws Exception{
        String params=bean.getParamString();
        String str=params.replaceAll("\"", "\'");
        JSONObject obj=JSONObject.parseObject(str);
        Map mapType = JSON.parseObject(str,Map.class);
        log.info("realname-->"+mapType.get("acct_name"));
        mapType.remove("sign");
        String pms=PayUtil.formatFormData(mapType).toString();
        if (pms.endsWith("&")) {
            pms = pms.substring(0, pms.length() - 1);
        }
        String encodemd5_sign = obj.get("sign").toString();
        String return_status = obj.get("result_pay").toString();
        String no_order = obj.get("no_order").toString();
        int pay_type=Integer.valueOf(obj.getString("pay_type"));
        bean.setApplyid(no_order);
        queryBankId(bean);//根据订单号查询用户名、bankid、safekey
        String amount = obj.get("money_order").toString();
        String dealid = obj.get("oid_paybill").toString(); // 外部单号
        String no_agree = obj.get("no_agree").toString();//支付协议号
        bean.setUserpayid(no_agree);
        bean.setBankCode(obj.getString("bank_code"));//回调bankcode
        bean.setCardtype(pay_type-2);//回调卡类型
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
        PayUtil.ReadAccountInfo(bean);//从配置文件读取秘钥信息
        if(StringUtil.isEmpty(bean.getRsapublickey())){
            bean.setRsapublickey(ll_RsaPublickey);
        }
        boolean flag = TraderRSAUtil.checksign(bean.getRsapublickey(), pms, encodemd5_sign);
        if (flag) {
            if (return_status.equalsIgnoreCase("SUCCESS")) {
                bean.setApplyid(no_order);
                PayUtil.ReadAccountInfo(bean);
                bean.setAddmoney(Double.parseDouble(amount));
                bean.setDealid(dealid);
            }else{
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                log.info("连连支付回调显示失败return_status=" + return_status+" 订单号:"+bean.getApplyid());
            }
        }else{
            log.info("连连支付签名验签失败,返回签名:"+encodemd5_sign+" 订单号:"+no_order);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }
    }

    @Override
    public void swiftpass_alipay_sdk_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取通知数据需要从body中流式读取
        BufferedReader reader = request.getReader();
        StringBuilder reportBuilder = new StringBuilder();
        String tempStr = "";
        while ((tempStr = reader.readLine()) != null) {
            reportBuilder.append(tempStr);
        }
        String reportContent = reportBuilder.toString();
        log.info("威富通支付宝sdk-->payweb回调通知参数:" + reportContent);
        Map<String,String> map = XmlUtil.toMap(reportContent.getBytes(), "utf-8");
        if("0".equals(map.get("status")) && "0".equals(map.get("result_code"))){
            log.info("威富通支付宝sdk-->payweb回调通知状态，status:" + map.get("status") + ",result_code:" + map.get("result_code"));
            putSwiftpassAlipaysdkValues(bean,map);
            getKeyAndId(bean);
            log.info("威富通支付宝sdk-->payweb回调通知状态，mer_id:" + bean.getMerchantId() + ",mer_key:" + bean.getMerchantKey());
            String respSign = map.get("sign");
            map.remove("sign");
            String sign = PayUtil.getMd5WithKey(map, bean.getMerchantKey());
            if(respSign.equalsIgnoreCase(sign)){
                response.getWriter().write("success");
                response.getWriter().close();
                //回调接口
                BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
                bean.setClassName("Bank_swiftpass_alipay_sdk");
                bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_ALIPAY);
                req.setData(bean);
                payNofifyInterface.basicNotify(req);
            }else {
                log.info("威富通支付宝sdk-->payweb回调验签失败，sign:" + sign + ",respSign:" + respSign);
                response.getWriter().write("error");
                response.getWriter().close();
            }
        }else{
            response.getWriter().write("error");
            response.getWriter().close();
            log.info("威富通支付宝sdk回调通知业务失败,status:"+map.get("status")+" result_code:"+map.get("result_code"));
        }
    }

    @Override
    public void bank_helipay_notify(PayBean bean, Map<String, String> resultMap) throws UnsupportedEncodingException {
        String applyid = resultMap.get("rt5_orderId");// 订单号
        double addmoney = Double.valueOf(resultMap.get("rt8_orderAmount"));// 订单金额 元
        bean.setApplyid(applyid);
        bean.setAddmoney(addmoney);
        bean.setDealid(resultMap.get("rt4_customerNumber"));
        queryBankId(bean);//根据订单号查询用户名、bankid、safekey
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_BANKCARD);
        PayUtil.ReadAccountInfo(bean);//从配置文件读取秘钥信息
        if(StringUtil.isEmpty(bean.getMerchantKey())){
            bean.setMerchantKey(heli_MD5_KEY);
        }
        if ("0000".equals(resultMap.get("rt2_retCode")) && "SUCCESS".equals(resultMap.get("rt9_orderStatus"))) {// 响应ok
            String returnsign = resultMap.get("sign");
            String md5Str = getSigned(resultMap, null, "t",bean.getMerchantKey());
            String localSign = DigestUtils.md5Hex(md5Str.getBytes("UTF-8"));// 本地签名
            if (!localSign.equals(returnsign)) {// 签名错误
                log.error("订单编号:{},充值金额:{},合利宝快捷支付回调签名错误,返回签名:{},本地签名:{}",
                        bean.getApplyid(), bean.getAddmoney(), returnsign, localSign);
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            }
        }else{
            log.info("合利宝快捷支付验签失败,回调显示订单支付失败,订单号:{}",bean.getApplyid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
        }
    }

    private void putSwiftpassAlipaysdkValues(PayBean bean, Map<String, String> map) {
        String applyid = map.get("out_trade_no");
        String amount = map.get("total_fee");
        String realAmount = Double.parseDouble(amount)/100+"";
        bean.setAddmoney(Double.parseDouble(realAmount));
        bean.setApplyid(applyid);
        bean.setDealid(map.get("transaction_id"));
        log.info("测试真实金额：realAmount=="+realAmount+",伪造金额 money=="+bean.getAddmoney());
    }

    private void getKeyAndId(PayBean bean) {
        queryBankId(bean);
        Map<String, String> merchantMap = PayUtil.getMerchantInfo(bean, swiftpass_mch_id, swiftpass_mch_key, "alipay");
        String mchId = merchantMap.get("mchId");
        String mchKey = merchantMap.get("mchKey");
        bean.setMerchantKey(mchKey);
        bean.setMerchantId(mchId);
    }


    //合利宝快捷支付专用
    private static String getSigned(Map<String, String> map, String[] excludes, String flag, String merchantKey) {
        StringBuffer sb = new StringBuffer();
        if("t".equals(flag)){
            map.remove("sign");
        }
        TreeMap<String, String> treemap=PayUtil.getSortMap(flag);
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
        sb.append(merchantKey);
        return sb.toString();
    }
}
