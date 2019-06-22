package com.caiyi.lottery.tradesystem.aspect;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * 验证用户是否登录——切面
 */
@Slf4j
@Component
@Aspect
@Order(value = 4)
public class CheckLoginAspect {

    @Autowired
    private UserBaseInterface userCenterBaseInterface;

    /**
     * 环绕通知——验证用户是否登录
     *
     * @param pjp
     * @param bean
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.caiyi.lottery.tradesystem.annotation.CheckLogin) && args(bean)")
    public Object around(ProceedingJoinPoint pjp, BaseBean bean) throws Throwable {
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        Method method = signature.getMethod();
        CheckLogin login = method.getAnnotation(CheckLogin.class);
        String sysCode = login.sysCode();
        Result<BaseBean> result = new Result<BaseBean>();
        bean.setAccesstoken(bean.getAccesstoken().replaceAll(" ", "+"));
        BaseResp<BaseBean> baseResp = check_login(bean,sysCode);
		result.setCode(baseResp.getCode());
		result.setDesc(baseResp.getDesc());
		log.info("check_login code=" + result.getCode() + " desc=" + result.getDesc());


		if (BusiCode.SUCCESS.equals(result.getCode())) {
		    bean.setUid(baseResp.getData().getUid());
		    bean.setPwd(baseResp.getData().getPwd());
		    bean.setAppid(baseResp.getData().getAppid());
		    bean.setAccesstoken(baseResp.getData().getAccesstoken().replaceAll(" ", "+"));
		    bean.setParamJson(baseResp.getData().getParamJson());
		    bean.setBusiErrCode((Integer.valueOf(result.getCode())));
		    bean.setBusiErrDesc(result.getDesc());
		    log.info("登录用户名：{}",bean.getUid());
            return pjp.proceed();
		}else {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 记录下请求内容
            String url = request.getRequestURL().toString();
            //神单详情特殊处理检测登入，未登入也让过
		    if (url.indexOf("/order/godShareDetail.api")!=-1){
                return pjp.proceed();
            }
        }
        return result;
    }

    /**
     * 验证用户是否登录
     *
     * @param bean
     * @return
     */
    public BaseResp check_login(BaseBean bean, String sysCode) {
        BaseReq baseReq = new BaseReq(sysCode);
        baseReq.setData(bean);
        String code = BusiCode.FAIL;
        String desc = "操作失败";
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = userCenterBaseInterface.checkLogin(baseReq);
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(code));
            bean.setBusiErrDesc(desc);
            baseResp.setCode(code);
            baseResp.setDesc(desc);
            log.error("异常信息", e);
        }
        return baseResp;
    }
}
