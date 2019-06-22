package com.caiyi.lottery.tradesystem.aspect;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.IPUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 记录日志——切面
 */
@Component
@Aspect
@Order(value = 2)
public class LoggerAspect {

    private static Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    // 配置前置通知
    @Before("execution(* com.caiyi.lottery..*web.controller.*.*(..))&&args(bean)")
    public void beforeweb(JoinPoint joinPoint,BaseBean bean) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String invokeIp = IPUtils.getIpAddr(request);
        MDC.put("ip",invokeIp);
        String ip = bean.getIpAddr();
        Enumeration pNames = request.getParameterNames();
        StringBuffer sb = new StringBuffer();
        while (pNames.hasMoreElements()) {
            String name = (String) pNames.nextElement();
            String value = request.getParameter(name);
            sb.append(name).append("=").append(value).append("&");
        }
        logger.info(url + " ---> " + method + " ---> form:" + sb.toString()+" body:"+bean.toJsonString());
    }
    
    // 配置前置通知
    @Before("execution(* com.caiyi.lottery..*center.controller.*.*(..))&&args(req)")
    public void beforecenter(JoinPoint joinPoint,BaseReq req) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String invokeIp = IPUtils.getIpAddr(request);
        MDC.put("ip",invokeIp);
        MDC.put("sysCode",req.getSysCode());
        // 记录下请求内容
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        Enumeration pNames = request.getParameterNames();
        StringBuffer sb = new StringBuffer();
        while (pNames.hasMoreElements()) {
            String name = (String) pNames.nextElement();
            String value = request.getParameter(name);
            sb.append(name).append("=").append(value).append("&");
        }
        logger.info(url + " ---> " + method + " ---> form:" + sb.toString()+" body:"+req.toJson());
    }
    
    @Around("execution(* com.caiyi.lottery..*web.controller.*.*(..))")
    public Object aroundweb(ProceedingJoinPoint pjp) throws Throwable {
    	try{
    		return pjp.proceed();
    	}catch(Exception e){
    		Result result = new Result<>();
        	result.setCode(BusiCode.FAIL);
        	result.setDesc("系统异常,请稍后重试");
        	logger.error("切面异常统一处理:"+e.getMessage(),e);
        	return result;
    	}
    }

    @Around("execution(* com.caiyi.lottery..*center.controller.*.*(..))")
    public Object aroundcenter(ProceedingJoinPoint pjp) throws Throwable {
        try{
            return pjp.proceed();
        }catch(Exception e){
            BaseResp baseResp = new BaseResp();
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("系统异常,请稍后重试");
            logger.error("切面异常统一处理:"+e.getMessage(),e);
            return baseResp;
        }
    }
    
    @AfterReturning(pointcut="execution(* com.caiyi.lottery..*web.controller.*.*(..))&&args(bean)",returning="ret")
    public void afterweb(JoinPoint joinPoint, BaseBean bean, Result ret) {
    	logger.info(joinPoint.getSignature().getName()+" 返回值:"+ret.toJson());
    }

    @AfterReturning(pointcut="execution(* com.caiyi.lottery..*center.controller.*.*(..))&&args(req)",returning="ret")
    public void aftercenter(JoinPoint joinPoint, BaseReq req, BaseResp ret) {
    	logger.info(joinPoint.getSignature().getName()+" 返回值:"+ret.toJson());
    }
}
