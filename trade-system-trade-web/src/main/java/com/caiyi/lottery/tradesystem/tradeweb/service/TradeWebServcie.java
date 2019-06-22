package com.caiyi.lottery.tradesystem.tradeweb.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import trade.bean.TradeBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TradeWebServcie {
    boolean go_login(TradeBean bean, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void project_pay(TradeBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception;

    ModelAndView dispatcherForward(TradeBean bean, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    ModelAndView dispatcherJjyhForward(TradeBean bean, HttpServletRequest request, HttpServletResponse response);
}
