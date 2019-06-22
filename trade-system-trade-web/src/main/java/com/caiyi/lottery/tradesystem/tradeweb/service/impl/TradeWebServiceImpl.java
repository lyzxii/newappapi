package com.caiyi.lottery.tradesystem.tradeweb.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.tradeweb.service.TradeBaseService;
import com.caiyi.lottery.tradesystem.tradeweb.service.TradeWebServcie;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.IPUtils;
import constant.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import pay.bean.PayBean;
import trade.bean.TradeBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class TradeWebServiceImpl implements TradeWebServcie {
    @Autowired
    TradeBaseService baseSservice;
    @Autowired
    UserBasicInfoInterface userBasicInfoInterface;
    @Autowired
    UserBaseInterface userBaseInterface;

    @Override
    public boolean go_login(TradeBean bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (bean.getBusiErrCode() == 0) {
            if (baseSservice.isOrderPay(request)) {
                HttpSession session = request.getSession();
                if (session.getAttribute(UserConstants.UID_KEY) == null) {
                    request.getSession().setAttribute(UserConstants.SYSPAY, bean.getPayorderid() + "_" + bean.getGopaymoney());
                    response.sendRedirect("/user/allylogin.go?type=1");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void project_pay(TradeBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (bean.getBusiErrCode() == 0) {
            if (bean.getZflag() != 1) {//不是追号则进行推送
                try { //购彩成功，插入信息到代理商购彩订单推送表
                    log.info("errcode---:" + bean.getBusiErrCode() + ";desc:" + bean.getBusiErrDesc());
                } catch (Exception e) {
                    log.info("插入购彩信息到订单通知表。。。");
                }
            }
                if (baseSservice.isOrderPay(request)) {
                    createUrlAndSend(bean,request,response);
                }
        }
    }

    @Override
    public ModelAndView dispatcherJjyhForward(TradeBean bean, HttpServletRequest request, HttpServletResponse response) {
        log.info("dispatcherJjyhForward-->登录检测,uid=" + bean.getUid() + ",logintype=" + bean.getLogintype() + ",appid=" + bean.getAppid() + ",accesstoken=" + bean.getAccesstoken());
        BaseReq<BaseBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        req.setData(bean);
        BaseResp<BaseBean> respBean = userBaseInterface.checkLogin(req);
        BaseBean baseBean = respBean.getData();
        bean.setUid(baseBean.getUid());
        log.info("dispatcherJjyhForward-->检测登陆结束，accessionToken=" + bean.getAccesstoken() + "，uid=" + bean.getUid());
        Map<String, String> maps = bean.getMap();
        long startTime = Long.parseLong(maps.get("startTime"));
        long endTime = new Date().getTime();
        log.info("startTime==" + startTime + ",endTime==" + endTime +",时间差=="+(endTime-startTime));
        ModelAndView mav = new ModelAndView();
        if ((endTime - startTime) / (60 * 1000) > 1) {
            mav.setViewName("redirect:/jsp/webexpired.jsp");
            mav.addObject("betInfo", maps);
        } else {
            log.info("dispatcherJjyhForward-->查询白名单，uid==" + bean.getUid());
            req.setData(bean);
            BaseResp<String> resp = userBasicInfoInterface.queryUserWhiteGrade(req);
            String level = resp.getData();
            bean.setWhitelistGrade(Integer.valueOf(level));
            log.info("dispatcherJjyhForward-->查询白名单，whiteGrade==" + bean.getWhitelistGrade() + ",maps==" + maps);
            String url = "redirect:/jsp/jjyhwebpay.jsp";
            if (bean.getWhitelistGrade() == 2 || bean.getWhitelistGrade() == 100) {
                url = "redirect:/jsp/jjyhwebpay-yy.jsp";
            }
            mav.setViewName(url);
            mav.addObject("betInfo", maps);
        }
        return mav;
    }

    @Override
    public ModelAndView dispatcherForward(TradeBean bean,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("dispatcherForward-->登录检测,uid=" + bean.getUid() + ",logintype=" + bean.getLogintype() + ",appid=" + bean.getAppid() + ",accesstoken=" + bean.getAccesstoken());
        BaseReq<BaseBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        req.setData(bean);
        BaseResp<BaseBean> respBean = userBaseInterface.checkLogin(req);
        BaseBean baseBean = respBean.getData();
        bean.setUid(baseBean.getUid());
        log.info("dispatcherForward-->检测登陆结束，accessionToken=" + bean.getAccesstoken() + "，uid=" + bean.getUid());
        Map<String, String> maps = bean.getMap();
        long startTime = Long.parseLong(maps.get("startTime"));
        long endTime = new Date().getTime();
        log.info("startTime==" + startTime + ",endTime==" + endTime +",时间差=="+(endTime-startTime));
        ModelAndView mav = new ModelAndView();
        if ((endTime - startTime) / (60 * 1000) > 1) {
            mav.setViewName("redirect:/jsp/webexpired.jsp");
            mav.addObject("betInfo", maps);
//            request.setAttribute("betInfo", maps);
//            request.getRequestDispatcher("/jsp/webexpired.jsp").forward(request, response);
        } else {
            log.info("dispatcherForward-->查询白名单，uid=="+bean.getUid());
            req.setData(bean);
            BaseResp<String> resp = userBasicInfoInterface.queryUserWhiteGrade(req);
            String level = resp.getData();
            bean.setWhitelistGrade(Integer.valueOf(level));
            log.info("dispatcherForward-->查询白名单，whiteGrade==" + bean.getWhitelistGrade() + ",maps==" + maps);
            String url = "redirect:/jsp/webpay.jsp";
            if (bean.getWhitelistGrade() == 2 || bean.getWhitelistGrade() == 100) {
                url = "redirect:/jsp/webpay-yy.jsp";
            }
//            request.setAttribute("betInfo", maps);
//            request.getRequestDispatcher(url).forward(request, response);
            mav.setViewName(url);
            mav.addObject("betInfo", maps);
        }
        return mav;
    }

    private void createUrlAndSend(TradeBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayBean payBean = new PayBean();
        payBean.setApplyid(bean.getPayorderid());
        payBean.setBankType("00");
        payBean.setAddmoney(Integer.valueOf(bean.getGopaymoney()));
        payBean.setIpAddr(IPUtils.getRealIp(request).trim());
        String url = baseSservice.createUrl(payBean, request, response);
        write_html_response(url, response);
    }

    public static void write_html_response(String contents, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=" + UserConstants.ENCODING);
        response.setCharacterEncoding(UserConstants.ENCODING);
        DataOutputStream out = new DataOutputStream(response.getOutputStream());
        StringBuffer buffer = new StringBuffer();
        buffer.append(contents);
        out.write((new String(buffer)).getBytes(UserConstants.ENCODING));
    }
}
