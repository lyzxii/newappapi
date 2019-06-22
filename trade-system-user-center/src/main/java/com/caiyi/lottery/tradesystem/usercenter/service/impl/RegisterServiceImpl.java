package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.SafeBean;
import bean.SourceConstant;
import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.BaseConstant;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.usercenter.dao.BindMsgMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.CpUserMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.UserMapper;
import com.caiyi.lottery.tradesystem.usercenter.mq.Producers;
import com.caiyi.lottery.tradesystem.usercenter.service.LoginService;
import com.caiyi.lottery.tradesystem.usercenter.service.ModifyUserInfoService;
import com.caiyi.lottery.tradesystem.usercenter.service.RegisterService;
import com.caiyi.lottery.tradesystem.util.*;
import constant.CodeDict;
import constant.UserConstants;
import dto.UserRegistDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.AppagentPojo;
import pojo.CpUserPojo;
import pojo.UserPojo;
import response.UserRegistResp;
import util.UserUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.SUCCESS;
import static com.caiyi.lottery.tradesystem.util.BaseUtil.*;
import static util.UserUtil.check;

/**
 * 注册相关接口
 *
 * @author GJ
 * @create 2017-12-04 21:24
 **/
@Service
public class RegisterServiceImpl implements RegisterService {
    private Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ModifyUserInfoService modifyUserInfoService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private SafeCenterInterface safeCenterInterface;
    @Autowired
    private CpUserMapper cpUserMapper;
    @Autowired
    private BindMsgMapper bindMsgMapper;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private Producers producers;

    private static Pattern pattern = Pattern.compile("\\s");

    @Deprecated
    @Override
    public void mobileRegisterCheck(UserBean bean) {
        //解密
        bean.setMobileNo(SecurityTool.iosdecrypt(bean.getMobileNo()));
        String mobileNo = bean.getMobileNo();
        try {
            int res = verifyMobileno(bean, mobileNo,false);
            if (res != 1) {
                return;
            }
            String md5mobileno = MD5Helper.md5Hex(mobileNo);
            int nums = userMapper.queryMobilenoBindCount(md5mobileno, false);
            // 检测用户输入的手机号是否已经被绑定注册
            int rsp = judgeRsp(bean, nums, UserConstants.PHONEFLAG);
            if (-1 == rsp) {
                return;
            }
            int num = checkMobileRegisterCount(bean, mobileNo);
            if (num==0) {
                return;
            }

            // 如果手机号可用,发送验证码
            String yzm = CheckUtil.randomNum();
            bean.setYzm(yzm);
            int siteType = getSiteType(bean.getSource());
            bean.setFlag(siteType);
            stopSMSbomb(bean);
            if(bean.getBusiErrCode() != 0){
                logger.info("发送短信验证失败,mobileNo=" + mobileNo);
                return ;
            }
            CpUserPojo cpUserPojo = new CpUserPojo();
            BeanUtilWrapper.copyPropertiesIgnoreNull(bean, cpUserPojo);
            BaseResp<SafeBean> safeResp = invokeSafeGetMidByMobileNo(mobileNo);
            if(safeResp==null||safeResp.getData()==null||StringUtils.isEmpty(safeResp.getData().getMobileId())){
                logger.info("调用安全中心,获取手机序列号为空");
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
                bean.setBusiErrDesc("保存手机号失败!");
                return;
            }
            //发送短信
            cpUserMapper.userSendMsg(cpUserPojo);
            if (cpUserPojo.getBusiErrCode() != 0) {
                logger.info("短信发送失败,mobileNo="+mobileNo+",errdesc=" +bean.getBusiErrDesc()+",ipaddr="+bean.getIpAddr() + ",source:"+bean.getSource());
                if (bean.getBusiErrCode() == UserConstants.PROCEDURES_ERROR) {
                    bean.setBusiErrDesc("验证码发送失败,请重试.");
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                } else if ((bean.getBusiErrCode() == UserConstants.PROCEDURES_CODE)) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_MOBILEREGISTER_LIMIT));
                }
            }else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                bean.setBusiErrDesc("验证码发送成功");
                bean.setTemporaryId(cpUserPojo.getTemporaryId());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("验证码发送失败,请重试.");
            logger.info("检测手机号注册资格出错:" + mobileNo, e);
        }
    }

    @Override
    public UserRegistResp phoneRegisterResult(UserBean bean) {
        UserRegistResp userRegistResp = new UserRegistResp();
        int code = bean.getBusiErrCode();
        switch (code) {
            case -1: {
                logger.info("注册失败,请重试,errDesc=" + bean.getBusiErrDesc());
                bean.setBusiErrDesc("注册失败,请重试!");
                break;
            }
            case 0: {
                logger.info("注册成功uid=" + bean.getUid());
                bean.setBusiErrDesc("注册成功,祝您中大奖!");
                break;
            }
            default: {
                logger.info("注册失败,请重试,errDesc=" + bean.getBusiErrDesc());
            }
        }
        if (bean.getLogintype() == 1 && bean.getBusiErrCode() == 0) {
            logger.info("注册成功,更新token,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token=" + bean.getAccesstoken());
            bean.setPwd(SecurityTool.iosencrypt((bean.getPwd())));
            loginService.generateNewToken(bean, CodeDict.AESENCRYPT);
        }
        userRegistResp.setCode(bean.getBusiErrCode() + "");
        userRegistResp.setDesc(bean.getBusiErrDesc());
        if (code == 0) {
            UserRegistDTO userRegistDTO = new UserRegistDTO();
            userRegistDTO.setUid(bean.getUid());
            userRegistDTO.setAppid(bean.getAppid());
            userRegistDTO.setAccesstoken(bean.getAccesstoken());
            userRegistResp.setData(userRegistDTO);
        }
        return userRegistResp;
    }

    @Override
    public void checkYzm(UserBean bean) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey("yzmtomobregister" + bean.getMobileNo());
        String str = redisClient.getString(cacheBean, logger, SysCodeConstant.USERCENTER);
        if (str == null || str.toString().isEmpty()) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_REGISTER_VERIFICATION_EMPTY));
            bean.setBusiErrDesc("验证码已过期或者不存在");
        }
        if (bean.getBusiErrCode() != 0) {
            return ;
        }
        if (bean.getBusiErrCode() == 0) {
            logger.info("手机号注册标识设置");
            bean.setFunc(BaseConstant.AUTO_BIND_PHONE);
        }
    }

    /**
     * 注册结果处理
     *
     * @param bean
     * @param userRegistDTO
     * @return
     */
    @Override
    public int registerResult(UserBean bean, UserRegistDTO userRegistDTO) {
        try {
            if (1 == bean.getLogintype() && Integer.parseInt(BusiCode.SUCCESS) == bean.getBusiErrCode() ) {
                logger.info("注册成功,更新token,nickid=" + bean.getUid() + ",appid=" + bean.getAppid() + ",token=" + bean.getAccesstoken());
                bean.setPwd(SecurityTool.iosencrypt((bean.getPwd())));
                loginService.generateNewToken(bean,CodeDict.AESENCRYPT);
                userRegistDTO.setAppid(bean.getAppid());
                userRegistDTO.setAccesstoken(bean.getAccesstoken());
            }
        } catch (Exception e) {
            logger.info("注册结果信息存储异常:{}", e);
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void verifyMobCode(BaseBean bean ,String mobileNo,String yzm,String type,boolean isAddCache) throws Exception {
        logger.info("校验短信验证码,mphone=" + mobileNo + ",smsType=" + type);
        String mobileMd5 = MD5Helper.md5Hex(mobileNo);
        int bindMsgCount = bindMsgMapper.updateBindMsgCount(mobileMd5, yzm, type);
        if (1 != bindMsgCount) {
            logger.info("更新短信验证码校验次数失败,mphone=" + mobileNo + ",smsType=" + type);
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_AUTHCODE_ERROR));
            bean.setBusiErrDesc("验证码错误");
            return;
        }
        int bindMsg = bindMsgMapper.updateBindMsg(mobileMd5, yzm, type);
        if (1 == bindMsg) {
            logger.info("短信验证码正确,mphone=" + mobileNo + ",smsType=" + type);
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_AUTHCODE_ERROR));
            bean.setBusiErrDesc("验证码错误");
            logger.info("短信验证码错误,mphone=" + mobileNo + ",smsType=" + type);
            throw new Exception("验证码错误");
        }
        if (isAddCache) {
            redisSetCache("yzmtomobregister" + mobileNo,yzm,Constants.TIME_MINUTE_FIVE,null);
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("短信验证成功");
    }

    /**
     * 调用安全中心-根据手机号获取手机序列号
     * @param mobileNo
     */
    private BaseResp<SafeBean> invokeSafeGetMidByMobileNo(String mobileNo) {
        SafeBean safeBean = new SafeBean();
        safeBean.setMobileno(mobileNo);
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.USERCENTER);
        BaseResp<SafeBean> safeRsp = safeCenterInterface.mobileNo(req);
        return safeRsp;
    }

    @Override
    public int checkParamByVerifySms(UserBean bean) {
        int ret = verifyMobileno(bean, bean.getMobileNo(), false);
        if (0 == ret || Integer.parseInt(BusiCode.SUCCESS) != bean.getBusiErrCode()) {
            return 0;
        }
        if (StringUtil.isEmpty(bean.getYzm())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SMSNULL_ERROR));
            bean.setBusiErrDesc("请输入短信验证码");
            logger.info("checkParam4VerifySms验证码为空,mobileno=" + bean.getMobileNo() + ",yzm=" + bean.getYzm());
            return 0;
        }
        return ret;
    }

    @Override
    public int verifyMobileno(BaseBean bean, String mobileNo,boolean permitEmpty) {
        int ret = 1;
        if (permitEmpty) {
            if (!CheckUtil.isNullString(mobileNo) && !CheckUtil.isMobilephone(mobileNo)) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PHONENOFORMAT_ERROR));
                bean.setBusiErrDesc("手机号码格式不正确");
                logger.info("用户输入的手机号不正确,nickid=" + bean.getUid() + ",mphone=" + mobileNo);
                ret = 0;
            }
        } else {
            if (CheckUtil.isNullString(mobileNo)) {
                    bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PHONENONULL_ERROR));
                    bean.setBusiErrDesc("手机号码不能为空");
                logger.info("用户输入的手机号为空,nickid=" + bean.getUid() + ",mphone=" + mobileNo);
                ret = 0;
            } else if (!CheckUtil.isMobilephone(mobileNo)) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PHONENOFORMAT_ERROR));
                bean.setBusiErrDesc("手机号码格式不正确");
                logger.info("用户输入的手机号不正确,nickid=" + bean.getUid() + ",mphone=" + mobileNo);
                ret = 0;
            }
        }
        return ret;
    }

    @Override
    public int setBaseData(UserBean bean) {
        if (0 == bean.getSource()) {
            bean.setSource(UserConstants.WEB_SOURCE);
        }
        int source = bean.getSource();
        if (BaseUtil.isHskUser(source) || BaseUtil.isAiduobaoUser(source)
                || BaseUtil.isLicaidiUser(source) || BaseUtil.isGongjijingUser(source)
                || BaseUtil.isFinancialManageUser(source)) {
            //是慧刷卡及理财 渠道不需要设置IP
            bean.setIpAddr("");
        }
        return 1;
    }

    @Override
    public void registerSourceUser(UserBean bean) {
        queryagentid(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return;
        }
        //新版用户名注册，根据flag来确定需要绑定手机号
        bean.setFlag(UserConstants.VERSION);
        //注册用户
        registerUser(bean);
    }


    @Override
    public void queryagentid(UserBean bean) {
        // 自动删除用户名中的空格
        bean.setUid(bean.getUid().replaceAll("\\s*", ""));
        logger.info("查询用户代理商id,cnickid:" + bean.getUid() + "  comfrom----:" + bean.getComeFrom() + ",source=" + bean.getSource());
        try {
            //未传代理商根据source值取
            if (CheckUtil.isNullString(bean.getComeFrom()) || UserConstants.NORMAL.equals(bean.getComeFrom())) {
                List<AppagentPojo> agents = userMapper.selectAgentBySource(bean.getSource());
                if (agents != null && agents.size() > 0) {
                    AppagentPojo agent = agents.get(0);
                    bean.setComeFrom(agent.getAgentid());
                }
            } else {
                int agentCount = userMapper.selectAgent(bean.getComeFrom());
                if (0 == agentCount) {
                    bean.setComeFrom(UserConstants.NORMAL);
                } else {
                    int agentCascadeCount = userMapper.selectAgentCascade(bean.getComeFrom());
                    if (agentCascadeCount > 5) {
                        bean.setComeFrom(UserConstants.NORMAL);
                    }
                }
            }
            logger.info("查询APP代理商成功  cnickid:" + bean.getUid() + "  source:" + bean.getSource() + "  ComeFrom:" + bean.getComeFrom());
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_APPAGENT_EXCEPTION));
            bean.setBusiErrDesc("查询APP代理商异常");
            logger.error("UserInfoBeanStub:queryagentid查询APP代理商异常", e);
        }
    }


    /**
     * 彩票用户注册
     *
     * @param bean
     */
    @Override
    public void registerUser(UserBean bean) {
        try {
            int source = bean.getSource();
            if (isLotteryUser(source)) {
                //彩票
                UserRegisterLottery(bean);
            } else {
                logger.info("错误的用户来源值,source=" + source + ",uid=" + bean.getUid() + ",mobileNo=" + bean.getMobileNo());
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SOURCERANGE_ERROR));
                bean.setBankCode("错误的用户来源值");
                return;
            }
        }catch (Exception e){
            logger.info("用户名注册发生异常");
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REGISTEXCEPTION_ERROR));
            bean.setBusiErrDesc("用户名注册发生异常");
        }
    }
    @Override
    public void checkminsRegister(UserBean bean){
        int count = check20MinMsg(bean.getMobileNo());
        if (count == 0) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_REGISTER_YZM_20MIN));
            bean.setBusiErrDesc("短信验证码校验出错，请稍后重试");
            return;
        }
        count = checkOneMinAccount(bean.getMobileNo());
        if (count > 0) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_REGISTER_REPEAT_1MIN));
            bean.setBusiErrDesc("账户已注册成功，请直接登录");
            return;
        }
        bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
    }

    /**
     * 检查是否20分钟内有验证过成功的短信验证码
     * @param mobileno
     */
    private int check20MinMsg(String mobileno){
        String md5mobileno = MD5Helper.md5Hex(mobileno);
        int count = bindMsgMapper.selectLt20MinMsg(md5mobileno);
        if (count == 0) {
            logger.info("手机号-{}，MD5-{}，20分钟内没有验证过成功的短信验证码", mobileno, md5mobileno);
        }
        return count;
    }

    /**
     * 检查是否1分钟内，重复注册用户
     * @param mobileno
     * @return
     */
    private int checkOneMinAccount(String mobileno){
        String md5mobileno = MD5Helper.md5Hex(mobileno);
        int count = userMapper.selectLtOneMinMsg(md5mobileno);
        if (count > 0) {
            logger.info("手机号-{}，MD5-{}，1分钟内重复注册-{}次", mobileno, md5mobileno,count);
        }
        return count;
    }


    /**
     * 彩票新用户注册
     * @param bean
     */
    private void UserRegisterLottery(UserBean bean) {
        logger.info("开始彩票新用户注册,uid=" + bean.getUid() + ",source=" + bean.getSource()+"mobileNo="+bean.getMobileNo());
        try {
            checkRegisterLottery(bean);
            if (Integer.parseInt(BusiCode.SUCCESS) != bean.getBusiErrCode()) {
                logger.info("彩票新用户注册参数错误,uid=" + bean.getUid() + ",错误原因=" + bean.getBusiErrDesc() + ",mobileNo=" + bean.getMobileNo());
                return;
            }
            String encryptedPwd = encryptPwdGeneral(bean, bean.getPwd());
            String originpwd = bean.getPwd();
            bean.setPwd(encryptedPwd);

            invokeSafeCenterByStoreNo(bean);
            bean.setCuserId(UUID.randomUUID().toString());
            UserPojo userPojo = null;
            userPojo = packagePojo(bean);
            //调用用户注册存储过程
            cpUserMapper.cpuserRegister(userPojo);
            if (userPojo != null && Integer.parseInt(BusiCode.SUCCESS) == userPojo.getBusiErrCode()) {
                bean.setBusiErrDesc("注册成功");
                logger.info("彩票新用户注册成功,uid=" + bean.getUid() + ",source=" + bean.getSource());
                autoMobilenoBind(bean);
                autoMobilenoLogin(bean);
            } else {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REGISTFAIL_ERROR));
                bean.setBusiErrDesc(userPojo.getBusiErrDesc());
                logger.info("彩票新用户注册失败,uid=" + bean.getUid() + ",source=" + bean.getSource() + ",失败原因=" + bean.getBusiErrDesc());
            }
            bean.setPwd(originpwd);

        } catch (Exception e) {
            logger.info("彩票新用户注册出现异常,uid=" + bean.getUid() + ",source=" + bean.getSource(), e);
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REGISTFAIL_ERROR));
            bean.setBusiErrDesc("注册失败");
        }
    }

    /**
     * 调用安全中心--存储存储手机号
     *
     * @param bean
     */
    private void invokeSafeCenterByStoreNo(UserBean bean) throws Exception{
        //调用安全中心存储(手机号)
        SafeBean safeBean = new SafeBean();
        safeBean.setNickid(bean.getUid());
        safeBean.setMobileno(bean.getMobileNo());
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.USERCENTER);
        try {
            BaseResp<SafeBean> resp = safeCenterInterface.addUserTable(req);
            if (resp==null||!SUCCESS.equals(resp.getCode()) || resp.getData() == null) {
                logger.info("添加用户信息至用户安全中心信息出错,用户名:" + bean.getUid());
                throw new Exception();
            }
        } catch (Exception e) {
            logger.error("调用安全中心出错",e);
            throw new Exception(e);
        }
        return ;
    }


    /**
     * 彩票用户注册传入参数校验
     *
     * @param bean
     */
    private void checkRegisterLottery(UserBean bean) throws IOException {
        check(bean, UserUtil.INFO_RGISTER);
        if (bean.getBusiErrCode() != 0) {
            return;
        }
        if (!CheckUtil.isNullString(bean.getMobileNo())) {
            if (!CheckUtil.isMobilephone(bean.getMobileNo())) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PHONENOFORMAT_ERROR));
                bean.setBusiErrDesc("手机号码格式不正确");
                return;
            }
        }
        if (bean.getPwd().length() < 6 || bean.getPwd().length() > 20) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PWDLENGTH_ERROR));
            bean.setBusiErrDesc("密码长度为6-20个字符");
            return;
        }
        Matcher matcher = pattern.matcher(bean.getUid());
        if (matcher.find()) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_USERNAMEBLANK_ERROR));
            bean.setBusiErrDesc("用户昵称不能包含空格");
            return;
        }
        if (UserUtil.length(bean.getUid()) < 4
                || UserUtil.length(bean.getUid()) > 16) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_NAMELENGTH_ERROR));
            bean.setBusiErrDesc("用户名长度为4-16个字符");
            return;
        }
        if (!CheckUtil.CheckUserName(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_USERNAMEILLEGAL_ERROR));
            bean.setBusiErrDesc("用户名不合法，可由中英文、数字、下划线组成");
            return;
        }
        //TODO 加密乱码
        /*if (bean.getImei().length() > 0) {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] arr = decoder.decodeBuffer(bean.getImei());
            bean.setImei(new String(arr));
        }*/
    }

    /**
     * 使用彩票加密串加密用户登录密码,并设置加密串到bean对象中.
     *
     * @param bean
     * @param plainPwd 登录密码原文
     * @return 加密后的密码密文
     * @throws Exception
     */
    public String encryptPwdGeneral(UserBean bean, String plainPwd) throws Exception {
        String privateKey = UserConstants.DEFAULT_MD5_KEY;
        bean.setPrivateKey(privateKey);
        return MD5Util.compute(plainPwd + privateKey);
    }

    /**
     * 组装对象
     *
     * @param bean
     * @return
     */
    private UserPojo packagePojo(UserBean bean) {
        UserPojo userPojo = new UserPojo();
        userPojo.setUid(bean.getUid());
        userPojo.setPwd(bean.getPwd());
        userPojo.setMobileNo(bean.getMobileNo());
        userPojo.setMailAddr(bean.getMailAddr());
        userPojo.setComeFrom(bean.getComeFrom());
        userPojo.setIpAddr(bean.getIpAddr());
        userPojo.setCuserId(bean.getCuserId());
        userPojo.setSource(bean.getSource() + "");
        userPojo.setImei(bean.getImei());
        userPojo.setPrivateKey(bean.getPrivateKey());
        if (!StringUtil.isEmpty(bean.getMobileNo())) {
            userPojo.setMobileMd5(MD5Helper.md5Hex(bean.getMobileNo()));
        }

        return userPojo;
    }

    /**
     * 绑定手机号
     *
     * @param bean
     * @throws Exception
     */
     public void autoMobilenoBind(UserBean bean){
        //如果是新版的用户名注册，则绑定手机号码，老板的注册不走进行errorcode和func判断
        if ( UserConstants.VERSION != bean.getFlag()) {
            if (Integer.parseInt(BusiCode.SUCCESS) != bean.getBusiErrCode()  || !UserConstants.AUTO_BIND_PHONE.equals(bean.getFunc())) {
                return;
            }
        }
         String md5mobile = MD5Helper.md5Hex(bean.getMobileNo());
        logger.info("手机号注册成功,自动绑定手机号,用户名=" + bean.getUid());
        int ret = userMapper.bindMobileno(bean.getUid(), md5mobile);
        if (1 == ret) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("手机号绑定成功");
        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("手机号绑定失败");
        }
    }

    /**
     * 手机号注册成功并自动绑定手机号成功后自动开启手机号登录
     *
     * @param bean
     */
     public void autoMobilenoLogin(UserBean bean) throws Exception {
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS) || !UserConstants.AUTO_BIND_PHONE.equals(bean.getFunc())) {
            return;
        }

        int count = userMapper.queryUserBindMobile(bean.getMobileNo(), true);
        int rsp = judgeRsp(bean, count, UserConstants.PHONEFLAG);
        if (-1 == rsp) {
            return;
        }
        logger.info("自动绑定手机号成功,开启手机号登录,用户名=" + bean.getUid());
        modifyUserInfoService.openMobilenoLogin(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            logger.info("开启手机号登录失败,用户名=" + bean.getUid() + ",失败原因=" + bean.getBusiErrDesc());
        }
    }

    /**
     * 判断回值
     *
     * @param bean
     * @param count
     * @param flag
     */
    private int judgeRsp(UserBean bean, int count, String flag) {
        if (UserConstants.PHONEFLAG.equals(flag) && count > 0) {
            logger.info("registerUser手机号已被注册绑定,mobileNo=" + bean.getMobileNo());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_PHONEREGIST_EXIST));
            bean.setBusiErrDesc("这个手机号已经注册啦,请直接登录!");
            return -1;
        }
        return 0;
    }

    public int checkMobileRegisterCount(UserBean bean ,String mobileno){
        int num = userMapper.countMobileNo(mobileno);
        // 检测用户输入的手机号是否已经被注册5次或5次以上
        if (num >= UserConstants.MAXCOUNT) {
            logger.info("手机号已被注册过多次,mobileNo=" + mobileno + ",次数=" + num);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_REGISTER_GT_MOBILE));
            bean.setBusiErrDesc("该手机号已经注册多个账号,请更换手机号!");
            return 0;
        }
        return 1;
    }

    /**
     * 根据用户source值判断用户来源.
     * @param source 用户来源
     * @return 用户来源,9188彩票 - 0,东方网彩票 - 1,惠刷卡 - 2,爱夺宝 - 3,理财帝 - 4,公积金 - 5
     */
    public int getSiteType(int source) {
        int siteType = 0;
        List<String> list = BaseUtil.getEastdaySource(FileConstant.EASTDAYSOURCEPATH);
        if (list.contains(String.valueOf(source))) {
            siteType = 1;
        } else if (isHskUser(source)) {
            siteType = 2;
        } else if (isAiduobaoUser(source)) {
            siteType = 3;
        } else if (isLicaidiUser(source)) {
            siteType = 4;
        } else if (isGongjijingUser(source)) {
            siteType = 5;
        }
        return siteType;
    }

    /**
     *发送短信前进行校验
     */
    public static void stopSMSbomb(UserBean bean) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if(StringUtil.isEmpty(bean.getSignmsg())){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("无需验签 ");
            return ;
        }
        String mobileNo = bean.getImNo();
        String time = bean.getStime();
        //生成服务器签名串
        StringBuilder sb = new StringBuilder();
        sb.append("imNo");
        sb.append("=");
        sb.append(mobileNo);
        sb.append("&");
        sb.append("timestamp");
        sb.append("=");
        sb.append(time);
        sb.append("&");
        sb.append("key");
        sb.append("=");
        sb.append("1.0^adhfjkas565a4sdf36a4s6df46^");//随机字符串
        String serverSignMsg = SecurityTool.getMD5Str(sb.toString());
        //校验
        if (serverSignMsg.equals(bean.getSignmsg())){//验签成功
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("验签成功 ");
        } else {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_MOBILEREGISTER_FAIL));
            bean.setBusiErrDesc("验证不通过");
        }
    }

    /**
     * 设置redis缓存
     * @param key 键
     * @param value 值
     * @param time 超时时间
     * @param obj 对象
     */
    private void redisSetCache(String key, String value, Integer time, Object obj) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        if(null != obj){
            JSONObject jsonObject = (JSONObject)JSONObject.toJSON(obj);
            cacheBean.setValue(jsonObject.toJSONString());
        }else{
            cacheBean.setValue(value);
        }
        if(null != time){
            cacheBean.setTime(time);
        }
        redisClient.setString(cacheBean, logger, SysCodeConstant.USERCENTER);
    }
}
