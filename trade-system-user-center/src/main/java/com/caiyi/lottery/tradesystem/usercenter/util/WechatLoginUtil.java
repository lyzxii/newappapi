package com.caiyi.lottery.tradesystem.usercenter.util;

import bean.WeChatBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import util.UserErrCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wxy
 * @create 2017-12-20 18:41
 **/
public class WechatLoginUtil {
    public static final int JOINMATCH = 0;
    public static final int JUDE9188USERID = 1;
    public static final int BIND9188USERID2WXAPPID = 2;
    public static final int SENDMOBILEVERIFYCODE = 3;
    public static final int REGISTERANDBIND9188USER = 4;
    public static final int DRAWBONUS = 5;
    public static final int JUDEBINDUID = 6;
    public static final int DRAWUSERINFO = 7;
    public static final int BIND9188USER = 8;
    /**
     * 通过code获取access_token.
     * @param code
     * @param appid
     * @param appsecret
     * @return
     */
    public static String getAccessToken(String code, String appid, String appsecret, Logger logger) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        String requestUrl = url.replace("APPID", appid).replace("SECRET", appsecret).replaceAll("CODE", code);

        HttpClient client = new HttpClient();
        GetMethod mothod = new GetMethod(requestUrl);
        mothod.getParams().setContentCharset("utf-8");
        String respStr = "";
        try {
            client.executeMethod(mothod); // 发送http请求
            respStr = mothod.getResponseBodyAsString();

            logger.info("微信提供的用户access_token={}",respStr);
        } catch (Exception e) {
            logger.error("通过code获取access_token时异常:[appid:{}])", appid, e);
        } finally {
            mothod.releaseConnection();
        }
        return respStr;
    }

    /**通过access_token 获取用户个人信息.
     * @param accessToken
     * @param openid
     * @return
     */
    public static String getUserInfo(String accessToken, String openid, Logger logger){
        String url = " https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        String requestUrl = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);

        HttpClient client = new HttpClient();
        GetMethod mothod = new GetMethod(requestUrl);
        mothod.getParams().setContentCharset("utf-8");
        String respStr = "";
        try {
            client.executeMethod(mothod);
            respStr = mothod.getResponseBodyAsString();

            logger.info("微信提供的userinfo={}", respStr);
        } catch (Exception e) {
            logger.error("通过access_token获取用户个人信息时异常:[accessToken:{}, openid:{}]", accessToken, openid, e);
        } finally {
            mothod.releaseConnection();
        }
        return respStr;
    }

    public static void verifyWechatOpenid(WeChatBean bean) {
        if (CheckUtil.isNullString(bean.getOpenid())) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("微信号不能为空");
            return;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("验证通过");
    }

    /**
     * 微信授权信息accesstoken
     * @return
     */
    public static void verifyWechatAccesstoken(WeChatBean bean) {
        if (!StringUtil.isEmpty(bean.getWeChatToken())) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("验证通过");
            return;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
        bean.setBusiErrDesc("微信授权信息不能为空");
    }

    /**
     * 参与微信竞猜活动参数校验
     * @param bean
     * @param num
     */
    public static void check(WeChatBean bean, int num) {

        if(num == JUDE9188USERID){
            if (!CheckUtil.isNullString(bean.getMphone())) {
                if(!CheckUtil.isMobilephone(bean.getMphone())){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("手机号码格式不正确");
                    return;
                }

            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }
        }
        if(num == BIND9188USERID2WXAPPID){
            if (!CheckUtil.isNullString(bean.getMphone())) {
                if(!CheckUtil.isMobilephone(bean.getMphone())){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("手机号码格式不正确");
                    return;
                }

            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }

            if (CheckUtil.isNullString(bean.getUid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不能为空");
                return;
            }

            if (CheckUtil.isNullString(bean.getOpenid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("微信号不能为空");
                return;
            }

            if (CheckUtil.isNullString(bean.getPwd())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("密码不能为空");
                return;
            }
        }
        if(num == SENDMOBILEVERIFYCODE){
            if (!CheckUtil.isNullString(bean.getMphone())) {
                if(!CheckUtil.isMobilephone(bean.getMphone())){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("手机号码格式不正确");
                    return;
                }

            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }
        }
        if(num == REGISTERANDBIND9188USER){
            if (!CheckUtil.isNullString(bean.getMphone())) {
                if(!CheckUtil.isMobilephone(bean.getMphone())){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("手机号码格式不正确");
                    return;
                }

            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }

            if(CheckUtil.isNullString(bean.getOpenid())){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("参与人微信号不能为空！");
                return ;
            }
            if(CheckUtil.isNullString(bean.getVerycode())){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("手机验证码不能为空");
                return;
            }
            if(!CheckUtil.isNullString(bean.getPwd())){
                if (bean.getPwd().length() < 6 || bean.getPwd().length() > 20) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("密码长度为6-20个字符");
                    return;
                }
            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("密码不能为空");
                return;
            }

            if(!CheckUtil.isNullString(bean.getUid())){
                if (bean.getUid().length() < 4 || bean.getUid().length() > 16) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("用户名长度为4-16个字符");
                    return;
                }
            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不能为空");
                return;
            }

            if (!CheckUtil.CheckUserName(bean.getUid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不合法，可由中英文、数字、下划线组成");
                return;
            }

            Pattern pattern = Pattern.compile("习近平|李克强|法轮功");
            Matcher matcher = pattern.matcher(bean.getUid());
            while (matcher.find()) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不能包含敏感词语");
                return;
            }

            pattern = Pattern.compile("QQ|qq|9188");
            matcher = pattern.matcher(bean.getUid());
            while (matcher.find()) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不能包含QQ、qq、9188等禁用词");
                return;
            }

            pattern = Pattern.compile("\\d{7,}");
            matcher = pattern.matcher(bean.getUid());
            while (matcher.find()) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不能包含6个以上连续数字");
                return;
            }
        }

        if(num == JUDEBINDUID || num == DRAWUSERINFO ){
            if (CheckUtil.isNullString(bean.getOpenid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("微信号不能为空");
                return;
            }
        }

        if(num == BIND9188USER){
            if (!CheckUtil.isNullString(bean.getMphone())) {
                if(!CheckUtil.isMobilephone(bean.getMphone())){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                    bean.setBusiErrDesc("手机号码格式不正确");
                    return;
                }

            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getVerycode())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("验证码错误");
                return;
            }
            if (CheckUtil.isNullString(bean.getUid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("用户名不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getPwd())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("密码不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getOpenid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("微信号不能为空");
                return;
            }
        }
    }
}
