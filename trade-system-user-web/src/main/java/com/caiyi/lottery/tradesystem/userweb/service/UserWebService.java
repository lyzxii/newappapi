package com.caiyi.lottery.tradesystem.userweb.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserInterface;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import constant.UserConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import response.UserRegistResp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户中心-前端逻辑处理
 * @create 2017-11-30 17:27:10
 */
@Service
public class UserWebService {

    @Autowired
    RedisClient redisClient;

    private Logger logger = LoggerFactory.getLogger(UserWebService.class);

    /**
     * 清理验证码
     * @param session
     */
    public void clearAuthCode( HttpSession session) {
        session.setAttribute(UserConstants.SESSION_YZM, "");
    }

    /**
     * 处理session
     * @param resp
     * @param session
     */
    public void handleSession(UserRegistResp resp, HttpSession session) {
        if (1 != resp.getData().getLogintype() && BusiCode.SUCCESS.equals(resp.getCode())) {
            session.setAttribute(UserConstants.UID_KEY, resp.getData().getUid());
            session.setAttribute(UserConstants.PWD_KEY, resp.getData().getPwd());
            clearAuthCode(session);
        }
    }

    /**
     * 设置密码后清除缓存
     * @param bean
     * @param session
     */
    public int deleteCacheAfterSetNewPwd(UserBean bean, HttpSession session) {
        Object obj = session.getAttribute(UserConstants.UID_KEY);
        if (obj != null) {
            logger.info("session.uid==" + obj);
        } else {
            logger.info("session.uid==" + obj);
        }
        session.setAttribute(UserConstants.PWD_KEY, bean.getPwd());
//        deleteUserTokenInCache(bean.getUid(), bean.getAppid());/
        return 1;
    }

    /**
     * 删除缓存中指定用户Token
     * @param nickid
     * @param appidstr
     * @return
     */
    public void deleteUserTokenInCache(String nickid, String appidstr) {
        logger.info("清除缓存中的token信息,uid=" + nickid + ",appids=" + appidstr);
        if (StringUtil.isEmpty(appidstr)) {
            return;
        }
//        CacheClient cc = CacheClient.getInstance();
        String[] appids = appidstr.split(",");
        for (String appid : appids) {
            if (StringUtil.isEmpty(appid)) {
                continue;
            }
//            logger.info("before delete,appid=" + appid + ",token=" + cc.get(appid));
            redisDeletCache(appid);
//            cc.delete(appid);
//            logger.info("after delete,appid=" + appid + ",token=" + cc.get(appid));
        }
    }

    /**
     * 检验图形验证码
     * @param bean
     * @param request
     * @return
     */
    public Result checkPicYzm(UserBean bean,HttpServletRequest request){
        HttpSession session = request.getSession(false);
        String yzm = (String) session.getAttribute(UserConstants.SESSION_YZM);
        logger.info("校验图片验证码时sessionid={},session={}，图片验证码={}",session.getId(),session.toString(),yzm);
        String nyzm = bean.getYzm();
        if (! CheckUtil.isNullString(yzm) && ! CheckUtil.isNullString(nyzm)) {
            if ( yzm.equalsIgnoreCase(nyzm)) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                bean.setBusiErrDesc("验证码正确");
            } else {
                bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_PICAUTH_ERROR));
                bean.setBusiErrDesc("验证码错误");
            }
        } else {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_PICAUTH_ERROR));
            bean.setBusiErrDesc("验证码错误");
        }
        Result result = new Result(bean.getBusiErrCode() + "", bean.getBusiErrDesc());
        return result;
    }

    /**
     * 清除图片验证码
     * @param bean
     * @param request
     */
    public void clearPicYzm(UserBean bean,HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("rand", "");
        bean.setYzm("");

    }

    /**
     * 指定key删除cache
     * @param key
     */
    private void redisDeletCache(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        redisClient.delete(cacheBean,logger, SysCodeConstant.USERWEB);
    }
}
