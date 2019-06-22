package com.caiyi.lottery.tradesystem.payweb.controller;


import com.alibaba.fastjson.JSON;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.client.PayNotifyInterface;
import com.caiyi.lottery.tradesystem.payweb.service.NotifyWebService;
import com.caiyi.lottery.tradesystem.util.Base64;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.bean.PayBean;
import pay.constant.RechargeTypeConstant;
import pay.util.PayUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
public class NotifyRequestController {


    @Autowired
    NotifyWebService notifyWebService;

    @Autowired
    private PayNotifyInterface payNofifyInterface;


    @RequestMapping(value = "/pay/web_ldys_notify.api")
    public void ldpayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception{
        notifyWebService.ldpay_notify(new PayBean(),request,response);
    }

    @RequestMapping(value = "/pay/web_sftNotify.api")
    public void shengpayNotify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        notifyWebService.shengpPay_notify(new PayBean(), request,response);
    }

    @RequestMapping(value = "/pay/umpayh5_notify_api.api")
    public void umpayH5Notify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        notifyWebService.umpayh5_notify(new PayBean(), request,response);
    }

    @RequestMapping(value = "/pay/swiftpass_alipay_sdk_notify.api")
    public void swiftpassAlipaySdk(HttpServletRequest request, HttpServletResponse response)throws Exception{
        notifyWebService.swiftpass_alipay_sdk_notify(new PayBean(), request,response);
    }

    @RequestMapping(value="/pay/web_ipaynow_notify.api")
    public void ipaynow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        notifyWebService.ipayNow_notify(new PayBean(),request,response);
    }


    /**
     *派洛贝微信回调
     */
    @RequestMapping(value="/pay/plb_wx_notify.api")
    public void plbwxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String transData=request.getParameter("transData");
        if(StringUtil.isEmpty(transData)){
            log.error("派洛贝微信H5回调返回内容为空");
            return;
        }
        log.info("派洛贝微信H5回调内容:{}",transData);
        PayBean bean=new PayBean();
        bean.setParamString(transData);
        bean.setClassName("bank_plbpay_wx");//设置spring bean name
        notifyWebService.plbWebNotify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭
            payNofifyInterface.basicNotify(new BaseReq<>(bean, SysCodeConstant.PAYWEB));
        }
    }

    /**
     * 威富通微信wap
     */
    @RequestMapping(value="/pay/swiftpass_wx_h5_notify.api")
    public void swiftpass_wx_h5_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder reportBuilder = new StringBuilder();
        String tempStr = "";
        while((tempStr = reader.readLine()) != null){
            reportBuilder.append(tempStr);
        }
        String reportContent = reportBuilder.toString();
        log.info("威富通微信h5支付回调通知:"+reportContent);
        PayBean bean=new PayBean();
        bean.setParamString(reportContent);
        bean.setClassName("Bank_swiftpass_wx_h5");
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_WEIXIN);
        notifyWebService.swiftpass_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     * 威富通微信sdk
     */
    @RequestMapping(value="/pay/swiftpass_wx_sdk_notify.api")
    public void swiftpass_wx_sdk_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder reportBuilder = new StringBuilder();
        String tempStr = "";
        while((tempStr = reader.readLine()) != null){
            reportBuilder.append(tempStr);
        }
        String reportContent = reportBuilder.toString();
        log.info("威富通微信sdk支付回调通知:"+reportContent);
        PayBean bean=new PayBean();
        bean.setParamString(reportContent);
        bean.setClassName("Bank_swiftpass_wx_sdk");
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_WEIXIN);
        notifyWebService.swiftpass_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     * 威富通qq支付
     */
    @RequestMapping(value="/pay/swiftpass_tenpay_h5_notify.api")
    public void swiftpass_tenpay_h5_Notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder reportBuilder = new StringBuilder();
        String tempStr = "";
        while((tempStr = reader.readLine()) != null){
            reportBuilder.append(tempStr);
        }
        String reportContent = reportBuilder.toString();
        log.info("威富通qq支付回调通知:"+reportContent);
        PayBean bean=new PayBean();
        bean.setParamString(reportContent);
        bean.setClassName("Bank_swiftpass_tenpay_h5");
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_TENPAY);
        notifyWebService.swiftpass_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     * 威富通支付宝h5
     */
    @RequestMapping(value="/pay/swiftpass_alipay_h5_notify.api")
    public void swiftpass_alipay_h5_notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader reader = request.getReader();
        StringBuilder reportBuilder = new StringBuilder();
        String tempStr = "";
        while((tempStr = reader.readLine()) != null){
            reportBuilder.append(tempStr);
        }
        String reportContent = reportBuilder.toString();
        log.info("威富通支付宝H5支付回调通知:"+reportContent);
        PayBean bean=new PayBean();
        bean.setParamString(reportContent);
        bean.setClassName("Bank_swiftpass_alipay_h5");
        bean.setRechargeType(RechargeTypeConstant.RECHARGETYPE_ALIPAY);
        notifyWebService.swiftpass_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     * 京东快捷
     */
    @RequestMapping(value="/pay/jdpay_fast_notify.api")
    public void jdpay_fast_notify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        //获取回调密文data
        String data = request.getParameter("resp");
        String xmlResult = new String(Base64.decode(data.getBytes()));
        log.info("京东快捷支付返回内容:{}",xmlResult);
        PayBean bean=new PayBean();
        bean.setParamString(xmlResult);
        bean.setClassName("Bank_jdpay_fast");
        notifyWebService.jdpay_fast_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭;
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     * 京东钱包
     */
    @RequestMapping(value="/pay/jdpay_h5_notify.api")
    public void jdpay_h5_notify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        //获取回调密文data
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            log.info("京东钱包支付异步通知原始数据:" + sb);
        } catch (IOException e) {
            log.error("京东钱包支付异步通知原始数据异常:" + e);
            return;
        }
        PayBean bean=new PayBean();
        bean.setParamString(sb.toString());
        bean.setClassName("Bank_jdpay_h5");
        notifyWebService.jdpay_h5_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭;
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     *连连支付
     */
    @RequestMapping(value="/pay/llzf_app_notify.api")
    public void llzf_app_notify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        StringBuilder params = new StringBuilder();
        request.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
        String ln="";
        while((ln=br.readLine())!=null){
            params.append(ln) ;
        }
        log.info("连连支付通知内容:"+params.toString());
        PayBean bean=new PayBean();
        bean.setParamString(params.toString());
        bean.setClassName("Bank_llzf_app");
        notifyWebService.llzf_app_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            Map<String,String> map=new HashMap<>();
            map.put("ret_code","0000");
            map.put("ret_msg","交易成功");
            response.getWriter().write(JSON.toJSONString(map));
            response.getWriter().close();//必须关闭;
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /**
     *连连支付
     */
    @RequestMapping(value="/pay/llzf_h5_notify.api")
    public void llzf_h5_notify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        StringBuilder params = new StringBuilder();
        request.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
        String ln="";
        while((ln=br.readLine())!=null){
            params.append(ln) ;
        }
        log.info("连连支付通知内容:"+params.toString());
        PayBean bean=new PayBean();
        bean.setParamString(params.toString());
        bean.setClassName("Bank_llzf_h5");
        notifyWebService.llzf_app_notify(bean);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            Map<String,String> map=new HashMap<>();
            map.put("ret_code","0000");
            map.put("ret_msg","交易成功");
            response.getWriter().write(JSON.toJSONString(map));
            response.getWriter().close();//必须关闭;
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

    /***
     * 合利宝快捷支付
     */
    @RequestMapping(value="/pay/bank_helipay_notify.api")
    public void bank_helipay_notify(HttpServletRequest request, HttpServletResponse response)throws Exception{
        log.info("合利宝银行卡快捷支付,处理回调通知");
        request.setCharacterEncoding("utf-8");
        Map<String, String> resultMap= PayUtil.getValueFromRequest(request);
        log.info("合利宝银行卡快捷支付回调通知内容:{}",resultMap);
        PayBean bean=new PayBean();
        bean.setClassName("Bank_helipay_fast");
        notifyWebService.bank_helipay_notify(bean,resultMap);
        if(bean.getBusiErrCode()==0){//参数、验签都ok
            response.getWriter().write("success");
            response.getWriter().close();//必须关闭;
            payNofifyInterface.basicNotify(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        }
    }

}
