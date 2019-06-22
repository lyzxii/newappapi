package com.caiyi.lottery.tradesystem.userweb.filter;

import com.caiyi.lottery.tradesystem.util.HeZuoUtil;
import com.caiyi.lottery.tradesystem.util.IPUtils;
import com.caiyi.lottery.tradesystem.userweb.config.ParameterRequestWrapper;
import constant.UserConstants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * IP过滤器
 *
 * @author GJ
 * @create 2017-12-04 19:28
 **/
public class IpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        HeZuoUtil.getSite();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(httpRequest);
        String ip = IPUtils.getRealIp(requestWrapper, UserConstants.IP_PROXY_FILE_PATH, UserConstants.IP_PROXY_FILE_NAME).trim();
        requestWrapper.addParameter("ipAddr",ip);
        String comFrom = IPUtils.getComeFrom(requestWrapper,null);
        requestWrapper.addParameter("comFrom",comFrom);
        filterChain.doFilter(requestWrapper,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
