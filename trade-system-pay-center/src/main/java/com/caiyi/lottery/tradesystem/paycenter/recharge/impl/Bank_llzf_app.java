package com.caiyi.lottery.tradesystem.paycenter.recharge.impl;

import com.alibaba.fastjson.JSON;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.IBankCardRech;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.utils.ConcurrentSafeDateUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.lianpay.api.util.TraderRSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.dto.RechDto;
import pay.pojo.PayOrder;
import pay.pojo.PayParam;
import pay.util.BaseHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 连连支付
 */
@Slf4j
@Component("Bank_llzf_app")
public class Bank_llzf_app implements IBankCardRech{

    public final static String patternZfb = "yyyyMMddHHmmss";
    public final static String patternDatabase = "yyyy-MM-dd HH:mm:ss";

    public final static String MerchantNo="201406031000001272";

    public final static String rsaPrivateKey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANDfNVlmVMH7ygcaGgWkH+Xl3UNpIfqBnqEZYIuB9z2GqRI10dFNHvmpPz3Zud9/ucBKvICsG0a8UhsJFC+8RuSVdxop3EPJjydcxMyqxZGZw1uL/ALVwHrRvhPLTdPJNwwZSQRA/Nw0ZXLV7vyQoV2/FtBR6x2phmtp1YkYk8S/AgMBAAECgYBLJsRvMh503T9HZuDN/strUFVYF8+d7l1A4XyXNV/bx0O1xZ+EixcGBRs4CcqS28XdpgZE1afKv59bEt0sf7WRimlsx6U3nNIAZ00hf11+rLQ5ZKV6/aCLPMi7eoe4CiquHzC7JUq3ry/SS8t343kkxI6cuxcCvMI2DTfJUupeQQJBAPZhu8ezEr/mtnsymevJkWH0ep7QwE7w84ENVRVwbTT5ZerxzEKB1MBtrVXX6ADoa8+Mil/UW4XUY2xFrJqcS6cCQQDZBpzGoOInIIu0ZAbsVS1QfITaP0iSlZaLJJq3vaveFHpBonOj6nOFbkREUOJ1ZQrM+rDX8ulbLzMei9akwAEpAkBZJrAt6IwmSDNLjNnJSdyDV1VvVhXf+qwBzHM3GMFLY9sPEoNYpWX4YzdPUpquiWtJZI9Fca/UUy/Oo797CocNAkByYDzBpcpqSom2IT1Q0jcWgvlVOCLcNf/oBvuPOGPan7oq2x3M1mPZf1p1Epe2vPCrRLdsp/V5++8Z+yK9Iv+5AkBFLzd8eirzzcjoZGU23CA57pU9iQ+sqjzNY7MpRqAtaryIPjb3tc1no49QTRxotTvI0Zp0S5sKX1vuj0kGNARo";

    @Autowired
    private NotifyService notifyService;

    private final static String notify_url= PayConstant.NOTIFY_HOST + "/pay/llzf_app_notify.api";

    @Override
    public RechDto addmoney(PayBean bean){
        if(StringUtil.isEmpty(bean.getRealName())){
              bean.setBusiErrCode(-1);
              bean.setBusiErrDesc("真实姓名不能为空");
              return null;
        }
        if(StringUtil.isEmpty(bean.getIdcard())){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("身份证号不能为空");
            return null;
        }

        bean.setRsaprivatekey(rsaPrivateKey);
        bean.setCardnum(bean.getIdcard());
        String user_id = bean.getUid(); // 该用户在商系统中的唯一编号，要求是该编号在商户系统中唯一标识该用户
        String sign_type = "RSA";
        String busi_partner = "101001"; // 外部账户充值
        String no_order = bean.getApplyid();
        String dt_order = ConcurrentSafeDateUtil.format(new Date(), patternZfb);
        String name_goods = "9188用户充值";
        DecimalFormat df = new DecimalFormat("#####0.00");
        double s = bean.getAddmoney() + bean.getHandmoney();
        String money_order = df.format(s);
        bean.setChannel("lianlianpay");
        String realName = "";
        String idcard = "";
        String flag_modify = "1"; // 修改标记  1-不可修改

        String id_type = "0"; // 证件类型 0-身份证
        String riskJsonStr = "";
        Map<String, String> riskmap = new HashMap<>();
        riskmap.put("frms_ware_category", "1007"); // 彩票类目
        riskmap.put("user_info_mercht_userno", user_id); // 商户用户唯一标识
        try{
            if(!StringUtil.isEmpty(bean.getApplydate())){ //注册时间
                riskmap.put("user_info_dt_register", ConcurrentSafeDateUtil.convert(bean.getApplydate(), patternDatabase, patternZfb)); // 注册日期
            }
        }catch (Exception e){

        }
        if(!StringUtil.isEmpty(bean.getRealName())){
            riskmap.put("user_info_full_name", bean.getRealName()); // 用户注册姓名
            realName = bean.getRealName();
        }
        if(!StringUtil.isEmpty(bean.getCardnum())){
            riskmap.put("user_info_id_no", bean.getCardnum()); // 用户注册证件号码
            idcard = bean.getCardnum();
        }
        //是否实名认证  1：:是  0：无认证
        riskmap.put("user_info_identify_state", "1");
        //实名认证方式
        riskmap.put("user_info_identify_type", "3"); //1：银行卡认证2：现场认证3：身份证远程认证4：其它认证
        riskJsonStr = JSON.toJSONString(riskmap);

        PayOrder order = new PayOrder();
        order.setOid_partner(MerchantNo);
        order.setUser_id(user_id);
        order.setSign_type(sign_type);
        order.setBusi_partner(busi_partner);
        order.setNo_order(no_order);
        order.setDt_order(dt_order);
        order.setName_goods(name_goods);
        order.setInfo_order(user_id);
        order.setMoney_order(money_order);
        order.setRisk_item(riskJsonStr);
        order.setNotify_url(notify_url);

        String card_no = bean.getCardNo().replaceAll("\\s*", "");
        order.setCard_no(card_no);

        if(!StringUtil.isEmpty(realName)){
            order.setId_no(idcard);
            order.setAcct_name(realName);
            order.setFlag_modify(flag_modify);
            order.setId_type(id_type);
        }
        String content = BaseHelper.sortParam(order);
        String sign = TraderRSAUtil.sign(bean.getRsaprivatekey(), content);
        order.setSign(sign);

        String content4Pay = JSON.toJSONString(order);
        log.info("连连支付content4Pay=" + content4Pay+" 订单号:"+bean.getApplyid());
        try {
            RechDto rechDto=new RechDto();
            PayParam payParam=new PayParam();
            rechDto.setApplyid(bean.getApplyid());
            rechDto.setAddmoney(bean.getAddmoney());
            payParam.setPrepayString(URLEncoder.encode(content4Pay, "utf-8"));
            rechDto.setPayParam(payParam);
            return rechDto;
        } catch (UnsupportedEncodingException e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("请求失败，请重新尝试");
            log.error("请求失败，applyid:{}",bean.getApplyid(),e);
        }
        return null;
    }

    @Override
    public void backNotify(PayBean bean) {
        notifyService.updateRechargeCard(bean);;//例外情况
        //更新账户，加款操作
        notifyService.applyAccountSuc(bean);
    }

    @Override
    public RechDto agreePay(PayBean bean) throws Exception {
        return null;
    }

}
