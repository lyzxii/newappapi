package com.caiyi.lottery.tradesystem.payweb.service;

import pay.bean.PayBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface NotifyWebService {
    void ldpay_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void ipayNow_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void shengpPay_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void umpayh5_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void plbWebNotify(PayBean bean);

    void swiftpass_notify(PayBean bean) throws Exception;

    void jdpay_fast_notify(PayBean bean) throws Exception;

    void jdpay_h5_notify(PayBean bean) throws Exception;

    void llzf_app_notify(PayBean bean) throws Exception;

    void swiftpass_alipay_sdk_notify(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void bank_helipay_notify(PayBean bean, Map<String, String> resultMap) throws UnsupportedEncodingException;

    ;
}
