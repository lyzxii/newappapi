package com.caiyi.lottery.tradesystem.aspect;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
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


/**
 * 设置用户数据——切面
 */
@Slf4j
@Component
@Aspect
@Order(value = 5)
public class SetUserDataAspect {

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
    @Around("@annotation(com.caiyi.lottery.tradesystem.annotation.SetUserData) && args(bean)")
    public Object around(ProceedingJoinPoint pjp, BaseBean bean) throws Throwable {
        Result<BaseBean> result = new Result<BaseBean>();
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        SetUserData userData = signature.getMethod().getAnnotation(SetUserData.class);
        BaseResp<BaseBean> baseResp = set_user_data(bean,userData.sysCode());
		result.setCode(baseResp.getCode());
		result.setDesc(baseResp.getDesc());
		log.info("set_user_data code==" + bean.getBusiErrCode() + " desc=" + bean.getBusiErrDesc() + " uid=" + bean.getUid());
		if ("0".equals(result.getCode())) {
		    bean.setUid(baseResp.getData().getUid());
		    bean.setPwd(baseResp.getData().getPwd());
		    bean.setAppid(baseResp.getData().getAppid());
		    bean.setAccesstoken(baseResp.getData().getAccesstoken().replaceAll(" ", "+"));
		    bean.setParamJson(baseResp.getData().getParamJson());
		    bean.setBusiErrCode(baseResp.getData().getBusiErrCode());
		    bean.setBusiErrDesc(baseResp.getData().getBusiErrDesc());
		}
		return pjp.proceed();
    }

    /**
     * 设置用户数据
     *
     * @param bean
     * @return
     */
    public BaseResp<BaseBean> set_user_data(BaseBean bean,String sysCode) {
        BaseReq baseReq = new BaseReq(sysCode);
        baseReq.setData(bean);
        String code = "-1";
        String desc = "验证登录失败";
        BaseResp<BaseBean> baseResp = new BaseResp<BaseBean>();
        try {
            baseResp = userCenterBaseInterface.setUserData(baseReq);
        } catch (Exception e) {
            baseResp.setCode(code);
            baseResp.setDesc(desc);
            log.error("异常信息", e);
        }
        return baseResp;
    }
}
