package com.caiyi.lottery.tradesystem.tradeweb.service;

import org.dom4j.DocumentException;
import pay.bean.PayBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TradeBaseService {

    boolean isOrderPay(HttpServletRequest request);

    String createUrl(PayBean payBean, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
