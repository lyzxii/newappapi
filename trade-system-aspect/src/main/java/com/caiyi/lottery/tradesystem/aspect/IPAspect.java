package com.caiyi.lottery.tradesystem.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.util.IPUtils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * IP切面
 */
@Slf4j
@Component
@Aspect
@Order(value = 1)
public class IPAspect {

    // 配置前置通知
    @Before("@annotation(com.caiyi.lottery.tradesystem.annotation.RealIP) && args(bean)")
    public void before(JoinPoint joinPoint,BaseBean bean) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = IPUtils.getIpAddr(request);
        String comeFrom = IPUtils.getComeFrom(request,log);// comeFrom
        bean.setIpAddr(ip);
        bean.setComeFrom(comeFrom);
        log.info("登录用户名:" + bean.getUid() +",ip:"+bean.getIpAddr()+",comFrom:"+bean.getComeFrom());
    }
}
