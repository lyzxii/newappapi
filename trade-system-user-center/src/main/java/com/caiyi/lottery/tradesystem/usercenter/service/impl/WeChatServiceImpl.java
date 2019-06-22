package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.SafeBean;
import bean.SourceConstant;
import bean.UserBean;
import bean.WeChatBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import com.caiyi.lottery.tradesystem.usercenter.mq.Producers;
import com.caiyi.lottery.tradesystem.usercenter.service.AlipayLoginService;
import com.caiyi.lottery.tradesystem.usercenter.service.LoginService;
import com.caiyi.lottery.tradesystem.usercenter.service.RegisterService;
import com.caiyi.lottery.tradesystem.usercenter.service.WeChatService;
import com.caiyi.lottery.tradesystem.usercenter.util.DecryptUtil;
import com.caiyi.lottery.tradesystem.usercenter.util.WechatLoginUtil;
import com.caiyi.lottery.tradesystem.util.*;
import com.google.common.collect.Maps;
import constant.CodeDict;
import constant.UserConstants;
import dto.AccountBindCaiyiDTO;
import dto.WeChatDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.CpUserPojo;
import pojo.UserLogPojo;
import pojo.UserPojo;
import pojo.WxUserBindPojo;
import util.UserUtil;

import java.util.*;

import static util.UserUtil.verifyCaiyiNickid;
import static util.UserUtil.verifyLoginPwd;

@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SafeCenterInterface safeCenterInterface;

    @Autowired
    private BindMsgMapper bindMsgMapper;
    @Autowired
    private AppagentMapper appagentMapper;
    @Autowired
    private UserAcctMapper userAcctMapper;
    @Autowired
    private WxUserBindMapper wxUserBindMapper;
    @Autowired
    private UserLogMapper userLogMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    private CpUserMapper cpUserMapper;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private AlipayLoginService alipayLoginService;
    @Autowired
    private Producers producers;

    /**
     *
     *@author: wang tao
     *@desc: tips: 此处发短信，验证短信用到表tb_bind_msg中手机号需要从安全中心获取，后期需要重新改造
     *
     */
    @Override
    public void checkParam4VerifySmsCodeWechat(WeChatBean bean) {
        int ret=registerService.verifyMobileno(bean,bean.getMphone(),false);
        if (ret == 0 || bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_CAIYI_ACCOUNT_BINDING_QUERY_MOBILENO_ERROR));
            bean.setBusiErrDesc("手机号为空或者格式不正确");
            return ;
        }
        if (StringUtil.isEmpty(bean.getVerycode())) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_CAIYI_ACCOUNT_BINDING_QUERY_SMS_ERROR));
            bean.setBusiErrDesc("请输入验证码");
            log.info("checkParam4VerifySmsCode短信验证码为空,mtype=" + bean.getMtype() + ",appversion=" + bean.getAppversion() + ",mphone=" + bean.getMphone() + ",verycode=" + bean.getVerycode());
            return ;
        }
        return ;
    }

    /**
     * 校验短信验证码，查询手机号绑定彩亿账户列表
     */
    @Override
    public WeChatDTO getMobileBindAccountWechat(WeChatBean bean) throws Exception{
        log.info("校验短信验证码,查询手机号绑定彩亿账户列表,mphone:{},verifycode:{}",bean.getMphone(),bean.getVerycode());
        List<AccountBindCaiyiDTO> accoutList=new ArrayList<>();
        WeChatDTO weChatDTO = new WeChatDTO();
        if("445".equals(bean.getAppversion())&&1==bean.getMtype()){
            registerService.verifyMobCode(bean, bean.getMphone(), bean.getVerycode(), "2", false);
        }else{
            registerService.verifyMobCode(bean, bean.getMphone(), bean.getVerycode(), "0", false);
        }
        if(bean.getBusiErrCode() != 0){
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_VERIFY_FAIL));
            log.error("校验短信验证码失败,[mphone:{},verifycode:{}]", bean.getMphone(), bean.getVerycode());
            return weChatDTO;
        }
        alipayLoginService.getAlipayAccountList(bean.getMphone(),accoutList);
        weChatDTO.setAccounts(accoutList);
        return weChatDTO;
    }


    /**
     * 微信注册前校验注册信息是否合法
     * @param bean
     * @return
     */
    @Override
    public int beforeWechatRegister(WeChatBean bean) throws Exception {
        String uri = bean.getRequestURI();
        boolean allowEmptyPwd = false;
        boolean allowEmptyMobileno = false;
        if (uri.endsWith("regeister_bind_uid.api")) {
            allowEmptyPwd = false;
            allowEmptyMobileno = false;
        } else if (uri.endsWith("wx_register.api")) {
            allowEmptyPwd = false;
            allowEmptyMobileno = true;
        } else if (uri.endsWith("register_user.api")) {
            allowEmptyPwd = true;
            allowEmptyMobileno = true;
        }
        log.info("beforeWechatRegister,uri=" + uri + ",allowEmptyPwd=" + allowEmptyPwd + ",allowEmptyMobileno=" + allowEmptyMobileno);
        return checkWechatRegisterInfo(bean, allowEmptyPwd, allowEmptyMobileno);
    }

    /**
     * 校验微信注册用户名和微信openid,unionid是否合法
     * @param bean
     * @param allowEmptyPwd 密码不能为空
     * @param allowEmptyMobileno 手机号不能为空
     * @return
     */
    @Override
    public int checkWechatRegisterInfo(WeChatBean bean, boolean allowEmptyPwd, boolean allowEmptyMobileno) {
        String errDesc = verifyCaiyiNickid(bean.getUid());
        bean.setMphone(CardMobileUtil.decryptMobile(bean.getMphone()));
        if (!StringUtil.isEmpty(errDesc)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_UID_ERROR));
            bean.setBusiErrDesc(errDesc);
            log.info("微信用户输入的昵称不正确,nickid=" + bean.getUid() + ",errDesc=" + errDesc);
            return 0;
        }

        // 某些微信注册接口输入参数里可以没有密码,注册时设置默认密码
        if (!allowEmptyPwd) {
            errDesc = verifyLoginPwd(bean.getPwd());
            if (!StringUtil.isEmpty(errDesc)) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_PWD_ERROR));
                bean.setBusiErrDesc(errDesc);
                log.info("微信用户输入的登录密码不正确,nickid=" + bean.getUid() + ",errDesc=" + errDesc);
                return 0;
            }
        }

        if (!allowEmptyMobileno) {
            int ret = registerService.verifyMobileno(bean, bean.getMphone(), allowEmptyMobileno);
            if (ret == 0 || bean.getBusiErrCode() != 0) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_PHONE_ERROR));
                return 0;
            }
        }

        if(CheckUtil.isNullString(bean.getOpenid())){
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WEBCHAR_OPENID_ERROR));
            bean.setBusiErrDesc("微信openid不能为空");
            return 0;
        }

        if(CheckUtil.isNullString(bean.getUnionid())){
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_UNIOID_ERROR));
            bean.setBusiErrDesc("微信unionid不能为空");
            return 0;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("验证通过");
        return 1;
    }

    /**
     * 获取微信用户信息
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public void getWechatUserInfo(WeChatBean bean) throws Exception {
        WeChatDTO weChatDTO = new WeChatDTO();
        if (StringUtil.isEmpty(bean.getWeChatToken())||StringUtil.isEmpty(bean.getOpenid())) {
            log.info("微信授权参数未传递，[uid:{}]", bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_ERROR));
            bean.setBusiErrDesc("微信授权参数未传递");
            return ;
        }
        try {
            JSONObject userInfo = getUserInfo(bean.getWeChatToken(), bean.getOpenid());
            bean.setNickName(userInfo.getString("nickname"));
            bean.setSex(userInfo.getString("sex"));
            bean.setProvince(userInfo.getString("province"));
            bean.setCity(userInfo.getString("city"));
            bean.setCountry(userInfo.getString("country"));
            bean.setHeadImgUrl(userInfo.getString("headimgurl"));
            bean.setUnionid(userInfo.getString("unionid"));
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("获取微信用户信息成功");
        } catch (Exception e) {
            log.error("获取微信用户信息失败,openid=" + bean.getOpenid() + ",weChatToken=" + bean.getWeChatToken());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_GETINFO_FAIL));
            bean.setBusiErrDesc("获取微信用户信息失败");
        }
    }

    /**
     * 通过access_token 获取用户个人信息.
     * @param accessToken 网页授权接口调用凭证
     * @param openid 用户的唯一标识
     * @return
     * @throws Exception
     */
    private JSONObject getUserInfo(String accessToken, String openid) throws Exception{
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        String requestUrl = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
        String respStr = HttpClientUtil.callHttpGet(requestUrl);
        log.info("微信提供的用户userinfo=" + respStr);
        if (StringUtil.isEmpty(respStr) || respStr.indexOf("errcode") >= 0) {
            throw new Exception("获取微信userinfo失败,userinfo=" + respStr);
        }
        return JSONObject.parseObject(respStr);
    }

    /**
     * 微信注册并绑定9188账号
     * @param bean
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerUser(WeChatBean bean) throws Exception {
        log.info("微信注册,用户名="+bean.getUid()+",unionid=" + bean.getUnionid());
        bean.setMphone(CardMobileUtil.decryptMobile(bean.getMphone()));
        if (CheckUtil.isNullString(bean.getUnionid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_GETINFO_FAIL));
            bean.setBusiErrDesc("获取微信信息失败!");
            log.info("微信注册失败,未取到unionid,source："+bean.getSource() + " comefrom: "+bean.getComeFrom());
            return ;
        }
        if(CheckUtil.isNullString(bean.getUid()) || CheckUtil.isNullString(bean.getPwd())){
            log.info("微信注册失败,用户名或密码为空,unionid:" + bean.getUnionid());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_ERROR));
            bean.setBusiErrDesc("用户名和密码不能为空");
            return ;
        }

        if(bean.getSource() >= 1000){
            String agentid = appagentMapper.queryAgentId(bean.getSource());
            if(!StringUtil.isEmpty(agentid)){
                bean.setAppAgentId(agentid);
            }
        }
        //微信用户默认密码
        String randomPwd = UUID.randomUUID().toString().replace("-", "").toString();
        String pwd = DecryptUtil.encryptPwd(bean, randomPwd);
        bean.setPwd(pwd);
        int userCount = userMapper.queryUserCountByNickid(bean.getUid());
        if(userCount > 0){
            log.info("微信注册失败,用户名已存在,unionid:" + bean.getUnionid() + "用户名:"+bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_USERNAME_EXIST));
            bean.setBusiErrDesc("用户名已经存在");
            return ;
        }
        synchronized(this) {
            boolean isSuccess = registerAndBindWechatUser(bean);
            if (isSuccess) {
                UserLogPojo userLogPojo = new UserLogPojo();
                userLogPojo.setCnickid(bean.getUid());
                userLogPojo.setCipaddr(bean.getIpAddr());
                userLogPojo.setCmemo("微信登录");
                userLogPojo.setCtype("微信注册并登录");
                userLogMapper.insertIntoUserLog(userLogPojo);
                int rs = userMapper.updatePwdFlag(bean.getUid());
                if(rs==1 && bean.getBusiErrCode()==0){
                    bean.setPwdflag("0");
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("注册成功");
                    log.info("微信注册成功,unionid:" + bean.getUnionid() + "用户名:"+bean.getUid());
                    return;
                }
            }
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_REGIST_FAIL));
            bean.setBusiErrDesc("注册失败");
            log.info("微信修改密码flag失败,unionid:" + bean.getUnionid() + "用户名:"+bean.getUid());
        }
    }

    /**
     * 微信账号注册并绑定9188账号
     */
    private boolean registerAndBindWechatUser(WeChatBean bean) throws Exception {
        String cuserid = UUID.randomUUID().toString();
        bean.setCuserId(cuserid);
        //插入用户信息表
        int ucount;
        UserPojo userPojo = new UserPojo();
        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, userPojo);
        userPojo.setAgentid(bean.getAppAgentId());
        userPojo.setSource(bean.getSource() + "");
        if (!StringUtil.isEmpty(bean.getMphone())) {
            //安全中心调用失败，流程成功，对安全中心进行事务补偿
            try {
                // 手机号保存到安全中心
                SafeBean safeBean = new SafeBean();
                BaseReq<SafeBean> safeBeanBaseReq = new BaseReq<>(safeBean, SysCodeConstant.USERCENTER);
                safeBean.setUsersource(SourceConstant.CAIPIAO);
                safeBean.setNickid(bean.getUid());
                safeBean.setMobileno(bean.getMphone());
                BaseResp<SafeBean> safeBeanBaseResp = safeCenterInterface.addUserTable(safeBeanBaseReq);
                if (!BusiCode.SUCCESS.equals(safeBeanBaseResp.getCode())) {
                    log.error("调用安全中心保存手机号失败，[uid:{},mobileno:{}]", bean.getUid(), bean.getMphone());
                    throw new Exception("调用安全中心保存手机号失败，[uid:" + bean.getUid() +",mobileno:"+ bean.getMphone() + "]");
                }
            } catch (Exception e) {
                log.error("调用安全中心出错",e);
                throw new Exception(e);
            }
            userPojo.setMobileNoMD5(MD5Helper.md5Hex(bean.getMphone()));
            userPojo.setMobileNo(bean.getMphone());
            ucount = userMapper.insertWithMobile(userPojo);

        } else {
            ucount = userMapper.insertWithoutMobile(userPojo);
        }
        //插入用户账户信息表
        int uacount = userAcctMapper.insertWithNickid(bean.getUid());
        //插入微信表
        String returnInfo = "";
        if(1==bean.getMtype()){
            returnInfo = "andriod";
        }else if(2==bean.getMtype()){
            returnInfo = "ios";
        }
        int wxcount = wxUserBindMapper.insertWxBind(bean.getOpenid(), bean.getUid(), bean.getUnionid(), returnInfo);
        boolean isSuccess = false;
        if(ucount == 1 && uacount == 1 && wxcount == 1){
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 安全中心回滚
     * @param bean
     */
    private void transactionalCompensateSafeCenter(SafeBean bean,String source){
        String commitOperation1 = "update";
        //插入失败，重新插入
        String rollbackOperation1 = "update";
        String target1 = "tb_user_vice";
        Map<String, Object> map1 = Maps.newHashMap();

        map1.put("sysdate", new Date());
        map1.put("object", bean);
        map1.put("usersource", SourceConstant.CAIPIAO);


        RollbackDTO rollbackDTO1 = new RollbackDTO(commitOperation1, rollbackOperation1, target1,source, map1);
        List<RollbackDTO> rollbackDTOList = new ArrayList<>();
        rollbackDTOList.add(rollbackDTO1);

        producers.sendSafeCenterList(rollbackDTOList);
    }


    @Override
    public WeChatDTO weChatSetDate(WeChatBean bean) throws Exception {
        WeChatDTO weChatDTO = new WeChatDTO();
        UserBean userBean = new UserBean();

        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, userBean);
        loginService.generateNewToken(userBean, CodeDict.MD5ENCRYPT);
        String isDefaultPwd = StringUtil.isEmpty(bean.getPwdflag()) ? "1" : bean.getPwdflag();

        BeanUtilWrapper.copyPropertiesIgnoreNull(userBean, weChatDTO);
        weChatDTO.setPwdflag(isDefaultPwd);
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("注册成功");
        return weChatDTO;
    }

    /**
     * 通过code 登录/注册 用户
     * @param bean
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WeChatDTO wechatLogin(WeChatBean bean) throws Exception {
        log.info("开始微信登录,[appid:{},code:{}]", bean.getWechatAppid() ,bean.getCode());
        WeChatDTO weChatDTO = new WeChatDTO();
        String accessTokenStr = WechatLoginUtil.getAccessToken(bean.getCode(), bean.getWechatAppid(), bean.getSecret(), log);

        if (StringUtil.isEmpty(accessTokenStr) || accessTokenStr.indexOf("errcode") >= 0) {
            log.info("获取微信accessToken失败,[accessToken:{},code:{}]", accessTokenStr, bean.getCode());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_GETACCESSTOKEN_FAIL));
            bean.setBusiErrDesc("获取微信accessToken失败");
            return null;
        }

        JSONObject tokenJson = JSONObject.parseObject(accessTokenStr);
        String openid = tokenJson.getString("openid"); //授权用户唯一标识
        String accessToken = tokenJson.getString("access_token"); //接口调用凭证
        weChatDTO.setWeChatToken(accessToken);
        bean.setWeChatToken(accessToken);

        String info = WechatLoginUtil.getUserInfo(accessToken, openid, log); //获取用户个人信息
        if (StringUtil.isEmpty(info) || info.indexOf("errcode") >= 0){
            log.info("获取用户个人信息失败,[userinfo:{}]", info);
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_GETUSERINFO_FAIL));
            bean.setBusiErrDesc("获取微信用户个人信息失败");
            return null;
        }

        JSONObject infoJson = JSONObject.parseObject(info);
        String unionid = infoJson.getString("unionid"); //用户统一标识
        weChatDTO.setUnionid(unionid);
        weChatDTO.setOpenid(openid);
        bean.setUnionid(unionid);
        bean.setOpenid(openid);
        log.info("调用微信登录远程服务,unionid=" + unionid);

        weChatLoginProcess(bean);

        if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.USER_ALLY_WECHAT_NOT_BIND)) { //没有注册
            String nickname = infoJson.getString("nickname"); //普通用户昵称
            log.info("用户还未绑定9188,[unionid:{},微信nickname:{}]", unionid, nickname);
            //用户昵称过滤后的名字
            String uid = checkWechatName(nickname);
            weChatDTO.setBind("-1");
            weChatDTO.setNickname(infoJson.getString("nickname"));
            weChatDTO.setUnionid(bean.getUnionid());
            weChatDTO.setOpenid(bean.getOpenid());
            weChatDTO.setHeadimgurl(infoJson.getString("headimgurl"));
            weChatDTO.setWeChatToken(bean.getWeChatToken());
            weChatDTO.setUid(uid);
        } else if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.SUCCESS)) {
            log.info("用户登录成功,unionid=" + unionid + ",nickid=" + bean.getUid());
            //检测用户是否绑定了手机号
            checkMobileBind(bean);
            //手机号未绑定
            if(bean.getBusiErrCode() == Integer.parseInt(BusiCode.USER_ALLY_WECHAT_MOBILE_BIND_NO)){
                weChatDTO.setNickname(infoJson.getString("nickname"));
                weChatDTO.setUnionid(bean.getUnionid());
                weChatDTO.setOpenid(bean.getOpenid());
                weChatDTO.setHeadimgurl(infoJson.getString("headimgurl"));
                weChatDTO.setWeChatToken(bean.getWeChatToken());
                return weChatDTO;
            }else if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.FAIL)){
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_QUERY_BIND_FAIL));
                bean.setBusiErrDesc("查询手机号码绑定状态失败");
                log.info("查讯手机号码绑定状态失败，用户名:"+bean.getUid());
                return null;
            }
            weChatDTO = weChatSetDate(bean);
        } else {
            log.info("用户登录失败,[errcode:{},errDesc:{}]", bean.getBusiErrCode(), bean.getBusiErrDesc());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_LOGIN_FAIL));
            bean.setBusiErrDesc("登陆失败");
            return null;
        }
        return weChatDTO;
    }

    /**
     * 微信登录
     * @param bean
     */
    private void weChatLoginProcess(WeChatBean bean) {
        log.info("微信登录,[unionid:{}]",bean.getUnionid());
        try {
            String nickid = wxUserBindMapper.queryNickidByUnionid(bean.getUnionid());
            if (StringUtil.isEmpty(nickid)) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_NOT_BIND));
                bean.setBusiErrDesc("还未绑定");
            } else {
                bean.setUid(nickid);
                CpUserPojo cpUserPojo = new CpUserPojo();
                cpUserPojo.setUnionid(bean.getUnionid());
                cpUserPojo.setOpenid(bean.getOpenid());
                cpUserPojo.setUid(bean.getUid());
                cpUserPojo.setIpAddr(bean.getIpAddr());
                cpUserMapper.wechatLogin(cpUserPojo);
                if (cpUserPojo.getBusiErrCode() == 0) {
                    bean.setPwd(cpUserPojo.getPwd());
                    // 登录成功后查询用户密码加密因子
                    UserPojo userPojo = userMapper.queryPrivateKeyAndUseridByNickid(bean.getUid());
                    BeanUtilWrapper.copyPropertiesIgnoreNull(userPojo, bean);
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("登录成功");
                } else {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_LOGIN_FAIL));
                    bean.setBusiErrDesc(cpUserPojo.getBusiErrDesc());
                    log.info("微信登录失败[{}]，[unionid:{},uid:{}]",cpUserPojo.getBusiErrDesc(), bean.getUnionid(), bean.getUid());
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("登录失败");
            log.error("微信登录出现异常,[unionid:{},uid:{}]", bean.getUnionid(), bean.getUid(), e);
        }
    }

    /**
     * 微信用户名
     * @param nickname
     * @return
     * @throws Exception
     */
    public String checkWechatName(String nickname) throws Exception {
        if (StringUtil.isEmpty(nickname)) {
            nickname = "";
            return nickname;
        } else {
            //过滤后的名称
            String filterName = CheckUtil.FilterUserName(nickname);
            //检测名称长度
            int nameLength = CheckUtil.length(filterName);
            if (nameLength < 4 || nameLength > 16) {
                log.info("用户的微信名长度不符合要求,原微信名:" + nickname + " 过滤后:" + filterName);
                return "";
            } else {
                //检查用户名的唯一性
                UserBean user = new UserBean();
                user.setUid(filterName);
                int num = userMapper.selectNickidCount(filterName);
                if (num > 0) {
                    log.info("用户的微信名过滤后已存在,原微信名:" + nickname + " 过滤后:" + filterName);
                    return "";
                } else {
                    return filterName;
                }
            }
        }
    }

    /**
     * 检测用户的手机号码是否绑定
     * @param bean
     */
    public void checkMobileBind(WeChatBean bean){
        try{
            Integer mobbind = userMapper.queryUserMobileBind(bean.getUid());
            if(mobbind != null){
                if(0 == mobbind.intValue()){
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_MOBILE_BIND_NO));
                    bean.setBusiErrDesc("手机号码未绑定");
                    return;
                }else{
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_MOBILE_BIND_YES));
                    bean.setBusiErrDesc("已绑定手机号");
                    return;
                }
            }else{
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("查询手机号是否绑定失败");
                log.info("查询手机号是否绑定失败,[uid:{}]", bean.getUid());
                return;
            }
        }catch(Exception e){
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询手机号是否绑定失败");
            log.error("查询手机号是否绑定失败,[uid:{}]", bean.getUid(), e);
            return;
        }
    }

    /**
     * 绑定微信账户到彩亿账户前检测接口参数
     * @param bean
     * @throws Exception
     */
    @Override
    public void bindWechatParamCheck(WeChatBean bean) throws Exception {
        String mobileNo = CardMobileUtil.decryptMobile(bean.getMphone());
        int ret = registerService.verifyMobileno(bean, mobileNo, false);
        if (ret == 0 || bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_CHECK_FAIL));
            return;
        }
        ret = UserUtil.verifyCaiyiNickidAndPwd(bean);
        if (ret == 0 || bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_CHECK_FAIL));
            return;
        }
        WechatLoginUtil.verifyWechatOpenid(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_CHECK_FAIL));
            return ;
        }
        WechatLoginUtil.verifyWechatAccesstoken(bean);
        if(bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_CHECK_FAIL));
            return ;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("参数验证通过");
    }

    /**
     * 绑定9188ID到微信AppID
     * @param bean
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bind9188UserId2WXAppId(WeChatBean bean) throws Exception {
        log.info("绑定9188ID到微信AppID,[手机号:{},用户ID:{},微信OpenID:{}]", bean.getMphone(), bean.getUid(), bean.getOpenid());
        bean.setMphone(CardMobileUtil.decryptMobile(bean.getMphone()));
        WechatLoginUtil.check(bean, WechatLoginUtil.BIND9188USERID2WXAPPID);
        if(bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)){
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_CHECK_FAIL));
            return ;
        }
        String moblieNoMD5 = MD5Helper.md5Hex(bean.getMphone());
        SafeBean safeBean;
        BaseReq safeReq;
        UserPojo userPojo = userMapper.queryLoginByNickid(bean.getUid());
        if(userPojo != null){
            String mobileno = userPojo.getMobileNoMD5();
            String password = userPojo.getPwd();
            Integer type = userPojo.getType();
            if(!mobileno.equals(moblieNoMD5)){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_UID_MOBILE_NOT_MATCH));
                bean.setBusiErrDesc("用户名与手机号不匹配");
                return;
            }
            String pwd = MD5Helper.md5Hex(bean.getPwd());

            if(!password.equals(pwd)){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_UID_PWD_NOT_MATCH));
                bean.setBusiErrDesc("用户名或密码不正确");
                return;
            }

            if(1 == type.intValue()){
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_TYPE_NOT_MATCH));
                bean.setBusiErrDesc("用户类型不匹配");
                return;
            }
        }else{
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_USER_NOT_EXIT));
            bean.setBusiErrDesc("用户名不存在");
            return;
        }

        int s = wxUserBindMapper.countByOpenidOrUid(bean.getOpenid(), bean.getUid(), moblieNoMD5);

        if(s > 0){
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_USER_EXIT));
            bean.setBusiErrDesc("9188账户ID已绑定，不能重复绑定");
            return;
        }

        String returnInfo = "";
        if(1==bean.getMtype()){
            returnInfo = "andriod";
        }else if(2==bean.getMtype()){
            returnInfo = "ios";
        }


        WxUserBindPojo wxUserBindPojo = new WxUserBindPojo();
        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, wxUserBindPojo);
        wxUserBindPojo.setMobileNo(bean.getMphone());
        wxUserBindPojo.setMobilenoMD5(moblieNoMD5);
        try {
            // 手机号保存到安全中心
            safeBean = new SafeBean();
            safeBean.setMobileno(bean.getMphone());
            safeReq = new BaseReq(safeBean, SysCodeConstant.USERCENTER);
            BaseResp<SafeBean> safeResp = safeCenterInterface.mobileNo(safeReq);
            if (!BusiCode.SUCCESS.equals(safeResp.getCode())) {
                log.error("安全中心保存手机号失败，mobile：{}", bean.getMphone());
                throw new Exception("安全中心保存手机号失败，mobile：" + bean.getMphone());
            }
        } catch (Exception e) {
            log.error("调用安全中心出错",e);
            throw new Exception(e);
        }
        int count = wxUserBindMapper.insertBindUser(wxUserBindPojo);
        if(count == 1){
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("微信关注ID与9188账户ID绑定成功");
            String pwdFlage = userMapper.queryPwdFlag(bean.getUid());
            bean.setPwdflag(pwdFlage);
            log.info("绑定成功[mphone:{},openid:{},uid:{}]", bean.getMphone(), bean.getOpenid(), bean.getUid());
            return;
        }
        log.info("绑定失败[mphone:{},openid:{},uid:{}]", bean.getMphone(), bean.getOpenid(), bean.getUid());
        bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALLY_WECHAT_BIND_FAIL));
        bean.setBusiErrDesc("微信关注ID与9188账户ID绑定失败");


    }

    /**
     * 绑定手机号到彩亿账户前检测接口参数
     * @param bean
     * @throws Exception
     */
    @Override
    public void bindWechatMobilenoParamCheck(WeChatBean bean) throws Exception {
        String mobileNo = CardMobileUtil.decryptMobile(bean.getMphone());
        int ret = registerService.verifyMobileno(bean, mobileNo, false);
        if (ret == 0 || bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_ERROR));
            return;
        }
        WechatLoginUtil.verifyWechatOpenid(bean);
        if (bean.getBusiErrCode() != 0) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_ERROR));
            return;
        }
        WechatLoginUtil.verifyWechatAccesstoken(bean);
        if(bean.getBusiErrCode()!=0) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_PARAM_ERROR));
            return;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("参数正确");
    }

    /**
     * 校验短信验证码，绑定手机号到9188账号并登录
     * @param bean
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindMobilenoToCaiyi(WeChatBean bean) throws Exception {
        log.info("校验短信验证码，绑定手机号到9188账号并登录,mphone=" + bean.getMphone() + ",verifycode=" + bean.getVerycode());
        bean.setMphone(CardMobileUtil.decryptMobile(bean.getMphone()));
        if("445".equals(bean.getAppversion())&&1==bean.getMtype()){
            registerService.verifyMobCode(bean, bean.getMphone(), bean.getVerycode(), "2", false);
        }else{
            registerService.verifyMobCode(bean, bean.getMphone(), bean.getVerycode(), "0", false);
        }
        if(bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)){
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_VERIFY_FAIL));
            log.error("校验短信验证码失败,[mphone:{},verifycode:{}]", bean.getMphone(), bean.getVerycode());
            return;
        }
        String nickid = wxUserBindMapper.queryNickidByUnionid(bean.getUnionid());
        bean.setUid(nickid);
        SafeBean orginSafeBean=null;
        try {
            BaseResp<SafeBean> res = getSafeData(bean);
            if (res == null||BusiCode.FAIL.equals(res.getCode())|| res.getData() == null) {
                bean.setBusiErrDesc("查询用户信息用户基本信息出错");
                bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
                throw new Exception();
            }else if (BusiCode.NOT_EXIST.equals(res.getCode())) {
            }else {
                //查询有数据时，记录下来，以便后面回滚
                orginSafeBean = res.getData();
                orginSafeBean.setNickid(bean.getUid());
            }
            // 安全中心保存手机号
            SafeBean safeBean = new SafeBean();
            BaseReq<SafeBean> safeReq = new BaseReq<>(SysCodeConstant.USERCENTER);
            safeBean.setUsersource(SourceConstant.CAIPIAO);
            safeBean.setNickid(nickid);
            safeBean.setMobileno(bean.getMphone());
            safeReq.setData(safeBean);
            BaseResp<SafeBean> safeResp = safeCenterInterface.addUserTable(safeReq);

            if (!BusiCode.SUCCESS.equals(safeResp.getCode())) {
                log.error("绑定手机号至彩亿账户失败,手机号："+bean.getMphone()+" 用户名:"+nickid);
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_MOBILE_BIND_FAIL));
                bean.setBusiErrDesc("绑定手机号至彩亿账户出错");
                throw new Exception("安全中心保存手机号出错，mobile:" + bean.getMphone());
            }
        } catch (Exception e) {
            log.error("调用安全中心出错",e);
            throw new Exception(e);
        }
        try {
            int result = userMapper.bindMobilenoToCaiyi(bean.getMphone(),MD5Helper.md5Hex(bean.getMphone()), bean.getUid());
            if (result == 1) {
                log.info("短信验证码正确,mphone=" + bean.getMphone() + ",verifycode=" + bean.getVerycode());
                UserPojo userPojo = userMapper.queryLoginByNickid(nickid);
                bean.setPwd(userPojo.getPwd());
                bean.setPwdflag(userPojo.getPwdflag());
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("通过校验");
            }else{
                log.error("绑定手机号至彩亿账户失败,手机号："+bean.getMphone()+" 用户名:"+nickid);
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ALLY_WECHAT_MOBILE_BIND_FAIL));
                bean.setBusiErrDesc("绑定手机号至彩亿账户出错");
            }
        } catch (Exception e) {
            if (orginSafeBean!=null) {
                log.info("bindMobilenoToCaiyi-安全中心调用出错,用户名:{},安全中心进行事务补偿" , bean.getUid());
                transactionalCompensateSafeCenter(orginSafeBean,UserConstants.ROLLBACK_WECHATBINDMOBILENO);
            }
            throw new Exception(e);
        }

    }

    private BaseResp<SafeBean> getSafeData(WeChatBean bean) throws Exception{
        SafeBean safeBean = new SafeBean();
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        safeBean.setNickid(bean.getUid());
        BaseReq<SafeBean> req = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
        BaseResp<SafeBean> resp = safeCenterInterface.getUserTable(req);
        return resp;
    }
}
