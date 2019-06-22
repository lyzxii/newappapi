package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.TokenBean;
import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.dao.TokenManageMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.UserMapper;
import com.caiyi.lottery.tradesystem.usercenter.service.LoginService;
import com.caiyi.lottery.tradesystem.usercenter.service.ModifyUserInfoService;
import com.caiyi.lottery.tradesystem.usercenter.service.TokenManageService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserRecordService;
import com.caiyi.lottery.tradesystem.usercenter.util.TokenGenerator;
import com.caiyi.lottery.tradesystem.util.*;
import com.google.common.collect.Maps;
import constant.CodeDict;
import constant.UserConstants;
import dto.UserInfoDTO;
import dto.UserLoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import response.UserLoginResq;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 登入相关接口
 *
 * @author GJ
 * @create 2017-12-04 20:15
 **/
@Service
public class LoginServiceImpl implements LoginService {
    private Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private TokenManageService tokenManageService;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRecordService userLogRecordService;

    @Autowired
    private ModifyUserInfoService modifyUserInfoService;

    @Autowired
    private TokenManageMapper tokenManageMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public void login(UserBean bean) {
        logger.info("当前登录用户为uid=" + bean.getUid() + ",source=" + bean.getSource());
        try {

            if (CheckUtil.isMobilephone(bean.getUid())) {
                // 输入的是手机号
                login9188ByPhone(bean);
            } else {
                // 输入的非手机号,只需要走普通用户名登录流程
                if (BaseUtil.isWebsiteLotteryUser(bean) || BaseUtil.isTouchUser(bean)) {
                    if (BaseUtil.isH5User(bean.getSource())) {
                        //先AES解密
                        String name = bean.getUid();
                        String rename = SecurityTool.h5decrypt(name);
                        bean.setUid(rename);
                    }
                }
                login9188ByNickid(bean);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_LOGIN_ERROR));
            bean.setBusiErrDesc("登录异常");
            logger.error("9188用户登录失败:source=" + bean.getSource() + ",uid=" + bean.getUid(), e);
        }
    }

    @Override
    public boolean checkLoginParam(BaseBean bean) throws Exception {
        boolean isCorrect = false;
        if (CheckUtil.isNullString(bean.getSignmsg())) {
            logger.info("登录参数signmsg为空,mtype=" + bean.getMtype() + ",appversion=" + bean.getAppversion() + ",uid=" + bean.getUid() + ", signmsg="+bean.getSignmsg());
        } else if (CheckUtil.isNullString(bean.getSigntype()) || !"1".equals(bean.getSigntype())) {
            logger.info("登录参数signtype错误,mtype=" + bean.getMtype() + ",appversion=" + bean.getAppversion() + ",uid=" + bean.getUid() + ", signtype="+bean.getSigntype());
        } else if (CheckUtil.isNullString(bean.getMerchantacctid()) || !UserConstants.loginhezuo.containsKey(bean.getMerchantacctid())) {
            logger.info("登录参数merchantacctid错误,mtype=" + bean.getMtype() + ",appversion=" + bean.getAppversion() + ",uid=" + bean.getUid() + ", merchantacctid="+bean.getMerchantacctid());
        } else if (CheckUtil.isNullString(bean.getUid())) {
            logger.info("登录参数uid为空,mtype=" + bean.getMtype() + ",appversion=" + bean.getAppversion() + ",uid=" + bean.getUid());
        } else if (CheckUtil.isNullString(bean.getPwd())){
            logger.info("登录参数pwd为空,mtype=" + bean.getMtype() + ",appversion=" + bean.getAppversion() + ",uid=" + bean.getUid() + ",pwd=" + bean.getPwd());
        } else {
            // 生成加密签名串
            // /请务必按照如下顺序和规则组成加密串！
            Map<String, String> signMsgMap = Maps.newLinkedHashMap();
            signMsgMap.put("signtype", bean.getSigntype());
            signMsgMap.put("merchantacctid", bean.getMerchantacctid());
            signMsgMap.put("uid", bean.getUid());
            signMsgMap.put("pwd", bean.getPwd());
            signMsgMap.put("key", UserConstants.loginhezuo.get(bean.getMerchantacctid()));


            String serverSignMsg = signMsg(signMsgMap);
            if (serverSignMsg.equals(bean.getSignmsg().toUpperCase())){
                isCorrect = true;
                logger.info("验签成功");
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("签名失败");
                saveSignMsgError(bean,  serverSignMsg);
            }
        }
        return isCorrect;
    }

    @Override
    public UserLoginResq afterLogin(UserBean bean) {
        UserLoginResq userLoginResq = new UserLoginResq();
        if (bean.getBusiErrCode() != 0) {
            userLoginResq.setCode(bean.getBusiErrCode()+"");
            userLoginResq.setDesc(bean.getBusiErrDesc());
            return userLoginResq;
        }
        if (bean.getLogintype() == 1){ //token登录
            logger.info("登录成功,更新token,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token=" + bean.getAccesstoken());
            generateNewToken(bean,CodeDict.AESENCRYPT);
        }
        String stime = "";
        try {
            stime = getLastLoginTime(bean.getLstime());
        } catch (Exception e) {
            logger.error("用户登入-{}，时间-{}转换出错",bean.getUid(),bean.getLstime());
        }
        String noticeFlag = getNoticeFlag(bean, stime);
        String whitelist = getIOSWebpayWhitelist(bean);
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setNotice(noticeFlag);
        userLoginDTO.setUid(bean.getUid());
        userLoginDTO.setUserid(bean.getCuserId());
        userLoginDTO.setAppid(bean.getAppid());
        userLoginDTO.setAccesstoken(bean.getAccesstoken());
        userLoginDTO.setWhitelist(whitelist);
        userLoginDTO.setHasVip(bean.getHasVip());
        userLoginResq.setCode(bean.getBusiErrCode()+"");
        userLoginResq.setDesc(bean.getBusiErrDesc());
        userLoginResq.setData(userLoginDTO);
        return userLoginResq;
    }

    /**
     * 返回token登录信息
     * @param bean
     */
    @Override
    public void generateNewToken(UserBean bean, CodeDict codeDict) {
        String pwd = bean.getPwd();
        //传入的加密串是什么方式，最终密码生成是MD5加密
        if (codeDict == CodeDict.AESENCRYPT) {
            String depwd = SecurityTool.iosdecrypt((bean.getPwd()));
            pwd = needMd5Encrypt(depwd);
        } else if (codeDict == CodeDict.NOENCRYPT) {
            pwd = needMd5Encrypt(bean.getPwd());
        }
        String appid = UniqueStrCreator.createUniqueString("lt");
        String accessToken = TokenGenerator.createToken(appid); //生成token
        bean.setAppid(appid);
        bean.setAccesstoken(accessToken);

        TokenBean tokenBean = new TokenBean();
        tokenBean.setUid(bean.getUid());
        tokenBean.setPwd(pwd);
        tokenBean.setAccessToken(accessToken);
        tokenBean.setParamJson(bean.getParamJson());
        tokenBean.setAppid(appid);
        //tokenBean.setCuserid(bean.getCuserId()==null?"":bean.getCuserId());

        //放入数据库中
        UserBean bean1 = new UserBean();
        bean1.setAccesstoken(accessToken);
        bean1.setUid(bean.getUid());
        bean1.setPwd(pwd);
        bean1.setSource(bean.getSource());
        bean1.setAppid(appid);
        bean1.setMtype(bean.getMtype());
        bean1.setParamJson(bean.getParamJson());
        tokenManageService.registerToken(bean1);
        if (bean1.getBusiErrCode() == -1) {
            logger.info("token入库失败！" + bean1.getBusiErrDesc());
        } else {
            CacheBean cacheBean = new CacheBean();
            //放入缓存中
            JSONObject tokenBeanJson = (JSONObject)JSONObject.toJSON(tokenBean);
            // 更新到缓存
            cacheBean.setKey(bean.getAppid());
            cacheBean.setValue(tokenBeanJson.toJSONString());
            cacheBean.setTime(Constants.TIME_DAY);
            redisClient.setString(cacheBean, logger, SysCodeConstant.USERCENTER);
        }
    }

    private String  needMd5Encrypt(String source){
        try {
            String md5pwd= MD5Helper.md5Hex(source);
            return md5pwd;
        } catch (Exception e) {
            logger.error("加密出错",e);
        }
        return source;
    }

    /**
     * 获取上次登入时间
     * @param lstime
     * @return
     * @throws ParseException
     */
    private String getLastLoginTime(String lstime) throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        Calendar lasttime = Calendar.getInstance();
        if (StringUtil.isEmpty(lstime)) {
            lasttime.add(Calendar.DAY_OF_MONTH, -7);
        } else {
            lasttime.setTime(ConcurrentSafeDateUtil.parse(lstime, pattern));
            Calendar now = Calendar.getInstance();
            int noLoginDays = Integer.parseInt(String.valueOf((now.getTimeInMillis() - lasttime.getTimeInMillis()) / 86400000));
            if (noLoginDays > 7) {
                now.add(Calendar.DAY_OF_MONTH, -7);
                lasttime.setTime(now.getTime());
            }
        }
        return ConcurrentSafeDateUtil.format(lasttime.getTime(), pattern);
    }

    /**
     * 获取用户信息消息通知标识
     * @param bean
     * @param stime
     * @return
     */
    private String getNoticeFlag(UserBean bean,  String stime) {
        UserBean userBean = new UserBean();
        userBean.setUid(bean.getUid());
        userBean.setPwd(bean.getPwd());
        userBean.setFlag(42); //查看用户账号是否有变动
        userBean.setStime(stime); //开始时间
        Boolean flag = IsChangeOFUserAccount(bean);
        String notice = "0";
        if (flag) {
            notice = "1";
        }
        return notice;
    }

    /**
     * 用户账号是否有变动
     * @param bean
     */
    public Boolean IsChangeOFUserAccount(UserBean bean) {
        try {
            if(!BaseUtil.checkTimeOrCode(bean)){
                return false;
            }
            String res = userMapper.queryUserAccount(bean.getUid(), bean.getStime());
            if (StringUtil.isEmpty(res)) {
                return false;
            }

        } catch (Exception e) {
            logger.error("查询用户是否有变动出错-IsChangeOFUserAccount", e);
        }
        return true;
    }


    private String getIOSWebpayWhitelist(UserBean bean) {

        int source = bean.getSource();
        String whitelist = null;
        if (source >= 2000 && source <= 3000) { //ios登陆时 根据用户的消费情况加入 白名单或黑名单
            CacheBean cacheBean = new CacheBean();
            String key = "ioswebpay_whitelist_" + bean.getUid();
            cacheBean.setKey(key);
            String obj = redisClient.getString(cacheBean, logger, SysCodeConstant.USERCENTER);
            whitelist = "1";
            boolean setByAdmin = false;
            if (obj != null) {
                setByAdmin = true;
                whitelist = obj.toString();
                logger.info("从缓存查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + bean.getUid());
            } else {
                getWebpayWhitelist(bean.getUid());
                setByAdmin = true;
                logger.info("从数据库查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + bean.getUid());
            }
            if (!setByAdmin) {
                int webpayWhitelist = Integer.parseInt(whitelist);
                int buyWhitelist = bean.getWhitelistGrade();
                if (webpayWhitelist == 1 && buyWhitelist >= 2) {
                    whitelist = "0";
                }
            }
        }
        return whitelist;
    }

    private void getWebpayWhitelist(String nickid) {
        String whitelist = "1";
        CacheBean cacheBean = new CacheBean();
        String key = "ioswebpay_whitelist_" + nickid;
        cacheBean.setKey(key);
        String obj = redisClient.getString(cacheBean, logger, SysCodeConstant.USERCENTER);

        if (obj != null) {
            whitelist = obj.toString();
            if ("1".equals(whitelist)) {//0-app支付状态，1-web支付状态
                //查询白名单等级，>=2设置成 0
                Integer iopen = userMapper.queryUserIsopen(nickid);
                if (iopen != null && iopen >= 2) {
                    whitelist = "0";
                    logger.info("从数据库查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
                    // 保存10天
                    cacheBean.setValue(whitelist);
                    cacheBean.setTime(UserConstants.TENDAY_EXPIRESTIME);
                    redisClient.setString(cacheBean, logger, SysCodeConstant.USERCENTER);
                }
            }
            logger.info("从缓存查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
        } else {
            List<String> itypelist = userMapper.queryWebPayItype(nickid);
            if (itypelist != null && itypelist.size() == 1) {
                whitelist = itypelist.get(0);
            } else {
                double amount = 0;
                List<String> amountList = userMapper.queryUserAccAmount(nickid);
                if (amountList != null && amountList.size() == 1) {
                    amount = Double.parseDouble(amountList.get(0));
                }
                if (amount > 0) {
                    whitelist = "0";
                }
            }
            logger.info("从数据库查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
            // 保存10天
            cacheBean.setValue(whitelist);
            cacheBean.setTime(UserConstants.TENDAY_EXPIRESTIME);
            redisClient.setString(cacheBean, logger, SysCodeConstant.USERCENTER);
        }
        logger.info("用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
    }

    /**
     * 验签操作
     * @param signMsgMap
     * @throws Exception
     */
    private String signMsg( Map<String, String> signMsgMap) throws Exception{
        StringBuilder signMsgVal = new StringBuilder();
        for(Map.Entry<String,String> entry:signMsgMap.entrySet()){
            StringUtil.appendParam(signMsgVal, entry.getKey(), entry.getValue());
        }
        logger.info("signMsgVal="+signMsgVal);
        String serverSignMsg = BankUtil.md5Hex(signMsgVal.toString().getBytes("UTF-8")).toUpperCase();
        logger.info("serverSignMsg="+serverSignMsg);
        return serverSignMsg;
    }

    private void saveSignMsgError(BaseBean bean, String serverSignMsg) {
        UserBean userBean = new UserBean();
        userBean.setUid(bean.getUid());
        userBean.setPwd(bean.getPwd());
        userBean.setIpAddr(bean.getIpAddr());
        userBean.setImei(bean.getImei());
        userBean.setSignmsg(serverSignMsg);
        userBean.setMerchantacctid(bean.getMerchantacctid());
        userBean.setYzm(bean.getSignmsg()); //客户端签名结果
        userBean.setSigntype(bean.getSigntype());
        saveSignMsgErr(userBean);
    }

    public void saveSignMsgErr(UserBean bean){
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("签名失效：");
            sb.append(" 服务端签名结果为："+bean.getSignmsg());
            sb.append(" 客户端签名结果为："+bean.getYzm());
            sb.append(" signtype="+bean.getSigntype());
            sb.append(" merchantacctid="+bean.getMerchantacctid());
            sb.append(" uid="+bean.getUid());
            sb.append(" pwd="+bean.getPwd());
            userLogRecordService.addUserOperLog(bean, "登录签名失败", sb.toString());
        } catch (Exception e) {
            logger.error("UserInfoBeanStub::saveSignMsgErr ", e);
        }
    }

    /**
     * 登录前预处理登录密码
     */
    private String  preparePwd(UserBean bean) throws Exception {
        String md5pwd=bean.getPwd();
        if (BaseUtil.isWebsiteLotteryUser(bean) || BaseUtil.isTouchUser(bean)) {
            if(BaseUtil.isH5User(bean.getSource())){
                //先AES解密
                String value = bean.getPwd();
                String repwd =SecurityTool.h5decrypt(value);

                bean.setPwd(repwd);
//                String name = bean.getUid();
//                String rename=SecurityTool.h5decrypt(name);
//                bean.setUid(rename);
                md5pwd= MD5Helper.md5Hex(repwd);
            }
        }else {
            String depwd = SecurityTool.iosdecrypt((bean.getPwd()));
            md5pwd= MD5Helper.md5Hex(depwd);
        }
        return md5pwd;
    }
    /**
     * 根据用户状态获取加密串,加密用户登录密码,并设置加密串到bean对象中.
     */
    @Override
    public String encryptPwd(BaseBean bean, String plainPwd) throws Exception {
        String privateKey = getUserPrivateKey(bean);
        bean.setPrivateKey(privateKey);
        return MD5Util.compute(plainPwd + privateKey);
    }
    @Override
    public String encryptPwdNoSql(BaseBean bean, String plainPwd) throws Exception {
        String privateKey = UserConstants.DEFAULT_MD5_KEY;
        bean.setPrivateKey(privateKey);
        return MD5Util.compute(plainPwd + privateKey);
    }

    /**
     * 非注册操作时从数据库读取加密因子
     */
    private String getUserPrivateKey(BaseBean bean) {
        String privateKey = UserConstants.DEFAULT_MD5_KEY;
        // 昵称可修改次数大于0表示是惠刷卡新用户,使用惠刷卡加密串加密的登录密码
        Integer count = getNickidModifyCount(bean);
        if (count!=null&&count > 0) {
            privateKey = UserConstants.HSK_MD5_KEY;
        }
        return privateKey;
    }

    /**
     * 根据用户手机号或昵称查询昵称可修改次数
     */
    public Integer getNickidModifyCount(BaseBean bean) {
        if (CheckUtil.isMobilephone(bean.getUid())) {
            return userMapper.queryNickidModifyCountByPhone(MD5Helper.md5Hex(bean.getUid()));
        } else {
            return userMapper.queryNickidModifyCountByNickname(bean.getUid());
        }
    }

    /**
     * 通过手机号登录9188.
     * @param bean
     * @throws Exception
     */
    private void login9188ByPhone(UserBean bean) throws Exception {
        // 把手机号当用户名,查询用户密码
        // 如果有把该手机号当用户名的账户,直接走用户名登录,失败后不再走手机号登录流程
        String userPwd = userMapper.queryUserPwd(bean.getUid());
        if (!CheckUtil.isNullString(userPwd)) {
            String md5pwd=preparePwd(bean);
            if (userPwd.equals(md5pwd)) {
                login9188ByNickid(bean);
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_LOGIN_PASSWORD_ERROR));
                bean.setBusiErrDesc("密码错误");
                userLogRecordService.addUserOperLog(bean, "用户登录", "[失败] " + bean.getBusiErrDesc());
                logger.info("9188用户登录密码错误uid=" + bean.getUid());
            }
        } else {
            // 如果没有,走手机号登录流程,失败后不再走用户名登录流程
            // 检测绑定该手机号的账户个数
            String md5mobileno = MD5Helper.md5Hex(bean.getUid());
            int count = userMapper.queryMobilenoBindCount(md5mobileno, false);
            if (count == 0) {
                // 没有账户绑定该手机号,提示账户不存在
                bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_LOGIN_NAME_NOTEXIST));
                bean.setBusiErrDesc("用户名不存在");
                userLogRecordService.addUserOperLog(bean, "用户登录", "[失败] " + bean.getBusiErrDesc());
            } else if (count == 1) {
                // 如果手机号只绑定了一个帐户,检测密码
                List<UserInfoDTO> loginInfoList = userMapper.queryLoginInfoByMobileno(md5mobileno, false);
                UserInfoDTO loginInfo = null;
                if (loginInfoList != null && loginInfoList.size() == 1) {
                    loginInfo = loginInfoList.get(0);
                }
                check_phone_login(bean, loginInfo);
            } else if (count > 1) {
                // 绑定多个账户,检测是否有账户开启手机号登录
                count = userMapper.queryMobilenoBindCount(md5mobileno, true);
                if (count == 1){
                    // 有一个账户开启了手机号登录,检测密码
                    List<UserInfoDTO> loginInfoList = userMapper.queryLoginInfoByMobileno(md5mobileno, true);
                    UserInfoDTO loginInfo = null;
                    if (loginInfoList != null && loginInfoList.size() == 1) {
                        loginInfo = loginInfoList.get(0);
                    }
                    check_phone_login(bean, loginInfo);

                } else if (count < 1) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_LOGIN_BINDLOTNAME));
                    bean.setBusiErrDesc("您的手机与多个账户绑定,请使用用户名登录!");
                    userLogRecordService.addUserOperLog(bean, "用户登录", "[失败] " + bean.getBusiErrDesc());
                }
            }
        }
    }

    /**
     * 通过用户名登录9188.
     * @param bean
     * @throws Exception
     */
    private void login9188ByNickid(UserBean bean) throws Exception {
        List<UserInfoDTO> loginInfoList = userMapper.queryLoginInfoByNickid(bean.getUid());
        UserInfoDTO loginInfo = null;
        if (loginInfoList != null && loginInfoList.size() ==1) {
            loginInfo = loginInfoList.get(0);
        }
        verifyLoginData(bean,loginInfo);
    }

    /**
     * 校验登录数据是否正确.
     */
    private void verifyLoginData(UserBean bean, UserInfoDTO loginInfo) {
        if (loginInfo == null) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_LOGIN_NAME_NOTEXIST));
            bean.setBusiErrDesc("用户不存在");
        } else if(loginInfo.getUserType() == 1){
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_LOGIN_ALIPAYUSER));
            bean.setBusiErrDesc("请去alipay.9188.com登陆");
        } else if(loginInfo.getState() != 0){
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_FORBID_ACCT));
            if(loginInfo.getState()==1){
                bean.setBusiErrDesc("账户已注销");
            }else{
                bean.setBusiErrDesc("账户已冻结");
            }
        } else {
            try{
                verifyLoginPwd(bean, loginInfo);
            }catch (Exception e){
                logger.error("用户名登入校验失败",e);
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setDesc("登入失败");
            }
        }
        logger.info("用户登录结果=" + bean.getBusiErrDesc() + ",uid=" + bean.getUid() + ",source=" + bean.getSource());
        try {
            if (bean.getBusiErrCode() == 0) {
                userLogRecordService.addUserOperLog(bean, "用户登录", "[成功]");
            } else {
                userLogRecordService.addUserOperLog(bean, "用户登录", "[失败] " + bean.getBusiErrDesc());
            }
        } catch (Exception e) {
            logger.error("日志入库错误,uid:{}",bean.getUid(),e);
        }

    }


    /**
     * 登录成功返回用户基本数据.数据转换
     * @param bean
     * @param loginInfo
     */
    private void returnUserBasicInfo(UserBean bean, UserInfoDTO loginInfo) {
        bean.setMobileNo(loginInfo.getUserId());
        bean.setUid(loginInfo.getNickid());
        bean.setCuserId(loginInfo.getUserId());
        bean.setWhitelistGrade(loginInfo.getWhitelistGrade());
    }

    /**
     * 校验登录密码是否正确.
     * @param bean
     * @param loginInfo
     */
    private void verifyLoginPwd(UserBean bean, UserInfoDTO loginInfo) throws Exception{
        String md5pwd = preparePwd(bean);
        if (md5pwd.equals(loginInfo.getPwd())) {
            returnUserBasicInfo(bean, loginInfo);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
            bean.setBusiErrDesc("登录成功");
        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_LOGIN_PASSWORD_ERROR));
            bean.setBusiErrDesc("密码错误");
        }
    }

    /**
     * 手机号登录校验登录数据是否正确.
     * @param bean
     * @param loginInfo
     * @throws Exception
     */
    private void check_phone_login(UserBean bean, UserInfoDTO loginInfo) throws Exception{
        verifyLoginData(bean,loginInfo);
        if (bean.getBusiErrCode() == 0) {
            // 更新user表,提示登录成功, 并得到用户昵称
            if(loginInfo.getPhoneLoginFlag() == 0) {
                modifyUserInfoService.openMobilenoLogin(bean);
            }


        }
    }

    @Override
    public String loginout(BaseBean bean) throws Exception {

        // token登录
        if (bean.getLogintype() == 1) {
            // 注销缓存信息
            CacheBean cacheBean = new CacheBean();
            cacheBean.setKey(bean.getAppid());
            redisClient.delete(cacheBean, logger, SysCodeConstant.USERCENTER);
            // 设置token注销
            int resCount = tokenManageMapper.disableToken(bean.getAppid(), bean.getAccesstoken());
            if (resCount > 0) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("注销成功");
                logger.info("注销成功：[uid:{},appid:[],accessToken:[]]", bean.getUid(), bean.getAppid(), bean.getAccesstoken());
            } else {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_LOGINOUT_FAIL));
                bean.setBusiErrDesc("注销失败");
                logger.error("注销失败：[uid:{},appid:[],accessToken:[]]", bean.getUid(), bean.getAppid(), bean.getAccesstoken());
            }
        }
        return bean.getBusiErrCode()+"";
    }
}
