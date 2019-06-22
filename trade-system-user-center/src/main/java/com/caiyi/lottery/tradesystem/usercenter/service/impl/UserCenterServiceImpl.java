package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.safecenter.clientwrapper.SafeCenterWrapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import com.caiyi.lottery.tradesystem.usercenter.mq.Producers;
import com.caiyi.lottery.tradesystem.usercenter.service.*;
import com.caiyi.lottery.tradesystem.usercenter.util.DecryptUtil;
import com.caiyi.lottery.tradesystem.usercenter.util.UserActionBase;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.Base64;
import com.caiyi.lottery.tradesystem.util.push.PushUtil;
import com.caiyi.lottery.tradesystem.util.push.bean.PushChannel;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.google.common.collect.Maps;
import com.jnewsdk.connection.client.HttpClient;
import com.jnewsdk.connection.client.HttpSSLClient;
import com.jnewsdk.util.SignUtil;
import constant.UserConstants;
import dto.IdBankBindingDTO;
import dto.UserAutoDTO;
import dto.UserInfoDTO;
import dto.UserPhotoDTO;
import integral.bean.IntegralParamBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.*;
import util.UserErrCode;
import util.UserUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;
import static com.caiyi.lottery.tradesystem.returncode.BusiCode.SUCCESS;
import static com.caiyi.lottery.tradesystem.returncode.ErrorCode.USER_ID_BINDING_OUTLIMIT_ERROR;
import static com.caiyi.lottery.tradesystem.util.BaseUtil.*;
import static com.caiyi.lottery.tradesystem.util.CardMobileUtil.decryptCard;
import static util.UserUtil.check;
import static util.UserUtil.isLeaglIdcardRes;

/**
 * 用户中心service实现类
 *
 * @author GJ
 * @create 2017-11-24 18:00
 **/
@Slf4j
@Service("userCenterService")
public class UserCenterServiceImpl implements UserCenterService {

    private static final Map<String, String> supportBankMap = new HashMap<>();

    static {
        supportBankMap.put("ICBC", "工商银行");
        supportBankMap.put("PSBC", "邮储银行");
        supportBankMap.put("CMBC", "民生银行");
        supportBankMap.put("BOS", "上海银行");
        supportBankMap.put("ABC", "农业银行");
        supportBankMap.put("CMB", "招商银行");
        supportBankMap.put("SPDB", "浦发银行");
        supportBankMap.put("HXB", "华夏银行");
        supportBankMap.put("BOC", "中国银行");
        supportBankMap.put("CITIC", "中信银行");
        supportBankMap.put("GDB", "广发银行");
        supportBankMap.put("SHRCB", "上海农村商业银行");
        supportBankMap.put("CCB", "建设银行");
        supportBankMap.put("CEB", "光大银行");
        supportBankMap.put("SZPAB", "平安银行");
        supportBankMap.put("COMM", "交通银行");
        supportBankMap.put("CIB", "兴业银行");
        supportBankMap.put("BCCB", "北京银行");
        supportBankMap.put("HKBEA", "东亚银行");
    }

    public static final Set<String> base64Key = new HashSet<>();

    static {
        base64Key.add("subject");
        base64Key.add("body");
        base64Key.add("remark");
        base64Key.add("customerInfo");
        base64Key.add("accResv");
        base64Key.add("riskRateInfo");
        base64Key.add("billpQueryInfo");
        base64Key.add("billDetailInfo");
        base64Key.add("respMsg");
        base64Key.add("resv");
    }

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TokenManageMapper tokenManageMapper;
    @Autowired
    private UserAutoMapper userAutoMapper;
    @Autowired
    private UserAutoLogMapper userAutoLogMapper;
    @Autowired
    private UserPhotoCashMapper userPhotoCashMapper;
    @Autowired
    private TokenManageService tokenManageService;
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private UserRedpacketMapper userRedpacketMapper;
    @Autowired
    private CpUserMapper cpUserMapper;
    @Autowired
    private Agent_UserMapper agentUserMapper;
    @Autowired
    private PushZJZHSwitchMapper pushZJZHSwitchMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserRecordService userLogRecordService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private ActiveDataMapper activeDataMapper;
    @Autowired
    private SafeCenterInterface safeCenterInterface;
    @Autowired
    private UserAcctMapper userAcctMapper;
    @Autowired
    private UserBankbindingMapper userBankbindingMapper;
    @Autowired
    private UserCashMapper userCashMapper;
    @Autowired
    private SmsMapper smsMapper;
    @Autowired
    private BindMsgMapper bindMsgMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private PushDeviceDataMapper pushDeviceDataMapper;
    @Autowired
    private BankCardMapMapper bankCardMapMapper;

    @Autowired
    Acct_UserMapper acct_userMapper;

    @Autowired
    private Grade_UserMapper grade_userMapper;

    @Autowired
    private SafeCenterWrapper safeCenterWrapper;
    @Autowired
    private Producers producers;
    //*********************************************WXY service start****************************************************


    /**
     * 用户图像上传
     *
     * @return
     */
    @Override
    public BaseResp upLoadUserPhoto(UserPhotoDTO userPhotoDTO) throws Exception {
        BaseResp baseResp = new BaseResp();
        log.info("用户图像上传，uid==" + userPhotoDTO.getUid());

        String cnickid = userPhotoDTO.getUid();
        //利用memcache的分布式锁原理,防止用户短时间内多次请求生成多条申请
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(cnickid + "_uploadUserPhoto");
        cacheBean.setTime(5 * 1000);
        cacheBean.setValue("1");
        boolean flag = redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
        if (!flag) {
            log.info("用户5秒内头像重复上传,用户名:" + cnickid);
            baseResp.setCode(BusiCode.USER_UPLOADPHOTO_IN_FIVE_SECONDS);
            baseResp.setDesc("头像上传成功");
            return baseResp;
        }
        // 判断用户真实性
        int count = userMapper.selectNickidCount(cnickid);
        if (count <= 0) {
            log.info("用户名检查失败,用户名:" + cnickid);
            baseResp.setCode(BusiCode.USER_UPLOADPHOTO_IN_FIVE_SECONDS);
            baseResp.setDesc("用户名不存在");
            return baseResp;
        }
        //用户图像
        String cuploadphoto = userPhotoDTO.getPhotoPath();
        int executeUpdate = 0;
        int recordStatus0 = userPhotoCashMapper.getPhotoCash0Num(cnickid);
        if (recordStatus0 > 0) {
            baseResp.setCode(BusiCode.USER_UPLOADPHOTO_AUDITING);
            baseResp.setDesc("头像正在审核中，不允许修改");
            return baseResp;
        }

        String currentDate = DateTimeUtil.getCurrentDate();
        String beginDate = DateTimeUtil.getBeforeXDayTime(currentDate, 15);//当前15天
        int recordStatus1 = userPhotoCashMapper.getPhotoCash1Num(cnickid, beginDate);
        if (recordStatus1 > 0) {
            baseResp.setCode(BusiCode.USER_UPLOADPHOTO_DURING_15DAYS);
            baseResp.setDesc("十五日之内仅可修改一次头像");
            return baseResp;
        }
        //图像有修改记录，且修改时间在15天以内  && 审核不通过已经3次
        int confNum = userPhotoCashMapper.getBefore15PhotoCashNum(cnickid, beginDate);
        if (confNum >= 3) {
            //计算时间差，向上取整
            String adddate = userPhotoCashMapper.getPhotoInfo(cnickid, beginDate);
            int days = 15;
            if (adddate != null) {
                days = 15 - DateTimeUtil.getDateInterval(DateTimeUtil.getCurrentFormatDate("yyyy-MM-dd HH:mm:ss"), adddate);
            }
            baseResp.setCode(BusiCode.USER_UPLOADPHOTO_MORE_THEN_THREE);
            baseResp.setDesc("三次审核未通过，修改头像功能暂不可用，请于" + days + "工作日之后再进行尝试");
            return baseResp;
        }

        //记录长传流水
        String cid = UUID.randomUUID().toString();
        executeUpdate = userPhotoCashMapper.addUserPhoto(cid, cnickid, cuploadphoto, "0");
        if (executeUpdate == 1) {
            log.info("用户图像上传成功  uid==" + cnickid);
            baseResp.setCode(BusiCode.SUCCESS);
            baseResp.setDesc("头像上传成功");
        } else {
            log.info("用户图像上传失败  uid==" + cnickid);
            baseResp.setCode(BusiCode.USER_UPLOADPHOTO_FAIL);
            baseResp.setDesc("头像上传失败");
        }
        return baseResp;
    }

    /**
     * 用户绑定验证
     *
     * @param bean
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindUserCheck(UserBean bean) throws Exception {
        check(bean, UserUtil.INFO_DINDYZ);
        if (bean.getBusiErrCode() == 0) {
            // 保留更新前用户数据以便回滚
        //    UserPojo userPojo = userMapper.queryUserInfo(bean.getUid());
            CpUserPojo cpUserPojo = new CpUserPojo();
            cpUserPojo.setUid(bean.getUid());
            cpUserPojo.setVerificationCode(bean.getYzm());
            cpUserPojo.setFlag(bean.getFlag());

            // 解密手机号
            String mobileNo = CardMobileUtil.decryptMobile(bean.getMobileNo());
            // 更新安全中心保存的手机号
            SafeBean safeBean = new SafeBean();
            safeBean.setUsersource(SourceConstant.CAIPIAO);
            safeBean.setNickid(bean.getUid());
            safeBean.setMobileno(mobileNo);
            SafeBean orginSafeBean=null;
            try {
                BaseResp<SafeBean> res = getSafeData(bean);
                if (res == null||BusiCode.FAIL.equals(res.getCode())|| res.getData() == null) {
                    bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
                    bean.setBusiErrDesc("查询用户信息用户基本信息出错");
                    throw new Exception();
                }else if (BusiCode.NOT_EXIST.equals(res.getCode())) {
                }else {
                    //查询有数据时，记录下来，以便后面回滚
                    orginSafeBean = res.getData();
                    orginSafeBean.setNickid(bean.getUid());
                }
                boolean flag = safeCenterWrapper.addUserTable(safeBean, log, SysCodeConstant.USERCENTER);
                if (!flag) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    bean.setBusiErrDesc("绑定校验更新安全中心异常");
                    throw new Exception("用户绑定处理更新安全中心异常");
                }
            } catch (Exception e) {
                log.error("用户绑定验证异常",e);
                throw new Exception("用户绑定处理更新安全中心异常");
            }
            try {
                cpUserMapper.userBindCheck(cpUserPojo);
                if (cpUserPojo.getBusiErrCode() != null && cpUserPojo.getBusiErrCode().intValue() != 0) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BINDCHECK_CHECK_FAIL));
                    bean.setBusiErrDesc("验证失败");
                } else {
                    if (bean.getFlag() == 1) {// 手机绑定通过时进行代理商归属的二次判断
                        agentCheck(bean, 1);
                    }

                }
            } catch (Exception e) {
                log.error("绑定校验出错",e);
                if (orginSafeBean!=null) {
                    log.info("bindUserCheck-安全中心调用出错,用户名:{},安全中心进行事务补偿" , bean.getUid());
                    transactionalCompensateSafeCenter(orginSafeBean,UserConstants.ROLLBACK_USERBINDCHECK);
                }
                throw new Exception(e);
            }

        }
    }

    private BaseResp<SafeBean> getSafeData(UserBean bean) throws Exception{
        SafeBean safeBean = new SafeBean();
        safeBean.setNickid(bean.getUid());
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        BaseReq<SafeBean> req = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
        BaseResp<SafeBean> resp = safeCenterInterface.getUserTable(req);
        return resp;
    }

    /**
     * @Author: tiankun
     * @Description: 修改用户登录密码.
     * @Date: 14:28 2017/11/30
     */
    @Transactional(rollbackFor = {Exception.class})
    public Result modifyLoginPwd(UserBean bean, Result res) throws Exception {
        log.info("修改用户登录密码,用户名=" + bean.getUid());
        Result result = checkModifyLoginPwdParam(bean, res);
        log.info("返回结果=" + result.toJson());
        if (!result.getCode().equals("0")) {
            log.info("修改用户登录密码参数错误,用户名=" + bean.getUid() + ",错误原因=" + result.getDesc());
            return result;
        }

        String newValue = SecurityTool.iosdecrypt(bean.getNewValue());
        String newPwd = loginService.encryptPwdNoSql(bean, newValue);
        bean.setNewValue(newPwd);
        String upwd = SecurityTool.iosdecrypt(bean.getUpwd());
        String oldPwd = loginService.encryptPwd(bean, upwd);
        bean.setUpwd(oldPwd);
        String uid = bean.getUid();
        int nums;
        //校验登录密码是否正确
        nums = userMapper.verifyLoginPwdSql(uid, oldPwd);
        if (nums <= 0) {
            result.setCode(FAIL);
            result.setDesc("原登录密码输入错误");
            log.info("修改用户登录密码失败,原登录密码输入错误,用户名=" + bean.getUid());
            return result;
        }
        synchronized (this) {
            //修改用户登录密码.
            nums = userMapper.updateUserPwd(newPwd, bean.getUid(), oldPwd);
            if (nums == 1) {
                //更新密码成功
                //设置昵称可修改次数为0
                int cou;
                if (CheckUtil.isMobilephone(bean.getUid())) {
                    cou = userMapper.updateNickidModifyAs0ByCmobileno(MD5Helper.md5Hex(bean.getUid()));
                    if (cou == 1) {
                        log.info("设置昵称可修改次数为0,更新成功,cnickid=" + bean.getUid());
                    }
                } else {
                    cou = userMapper.updateNickidModifyAs0ByCnickid(bean.getUid());
                    if (cou == 1) {
                        log.info("设置昵称可修改次数为0,更新成功,cnickid=" + bean.getUid());
                    }
                }
                result.setCode(SUCCESS);
                result.setDesc("修改登录密码成功");
                log.info("成功修改用户登录密码,注销用户token,用户名=" + bean.getUid());
            } else {
                result.setCode(FAIL);
                result.setDesc("修改登录密码失败");
                log.info("修改用户登录密码失败,用户名=" + bean.getUid());
                throw new Exception("修改登录密码失败");
            }
        }
        try {
            userLogRecordService.addUserOperLog(bean, "修改登录密码", bean.getDesc());
        } catch (Exception e) {
            log.error("日志入库异常",e);
        }
        return result;
    }

    private String disableUserToken(List<TokenBean> availableTokens) {
        StringBuilder appidstr = new StringBuilder();
        for (TokenBean token : availableTokens) {
            appidstr.append(disUserToken(token));
        }
        return appidstr.toString();
    }

    /**
     * 注销指定token信息.
     */
    @Transactional
    public String disUserToken(TokenBean token) {
        int count = tokenManageMapper.disableToken(token.getAppid(), token.getAccessToken());
        if (count == 1) {
            return token.getAppid() + ",";
        }
        return "";
    }

    /**
     * 根据用户名校验用户密码是否正确
     */
    private boolean verifyPwd(UserBean bean) {
        boolean res = false;
        List<UserInfoDTO> loginInfoList = userMapper.queryLoginInfoByNickid(bean.getUid());
        UserInfoDTO loginInfo = null;
        if (loginInfoList != null && loginInfoList.size() == 1) {
            loginInfo = loginInfoList.get(0);
        }
        if (bean.getPwd().equals(loginInfo.getPwd())) {
            res = true;
        }
        return res;
    }

    /**
     * 忘记密码-修改密码前检测参数正确性
     */
    private Result checkModifyLoginPwdParam(UserBean bean, Result result) {
        result.setCode("-1");
        if (StringUtil.isEmpty(bean.getUid())) {
            result.setDesc("用户名不能为空");
        } else if (StringUtil.isEmpty(bean.getUpwd())) { //TODO bean.getUpwd()
            result.setDesc("老密码不能为空");
        } else if (StringUtil.isEmpty(bean.getNewValue())) {
            result.setDesc("新密码不能为空");
        } else if (SecurityTool.iosdecrypt(bean.getNewValue()).length() < 6 || SecurityTool.iosdecrypt(bean.getNewValue()).length() > 20) {
            result.setDesc("密码长度必须是6-20个字符");
        } else {
            result.setCode("0");
        }
        return result;
    }

    /**
     * @Author: tiankun
     * @Description: 绑定身份证
     * @Date: 15:56 2017/11/30
     */
    @Transactional(rollbackFor = {Exception.class})
    public Result bindIdcard(UserBean bean, Result res) throws Exception {
        log.info("绑定身份证,用户名=" + bean.getUid());

        if (bean.getIdCardNo() != null & bean.getIdCardNo().length() > 10) {
            String idNum = SecurityTool.iosdecrypt(bean.getIdCardNo());
            log.info("解密后身份证号IdCardNo=" + idNum);
            bean.setIdCardNo(idNum);
        }
        Result result = checkBindIdcardParam(bean, res);
        if (!result.getCode().equals("0")) {
            log.info("绑定身份证失败,用户名=" + bean.getUid() + ",失败原因=" + result.getDesc());
            return result;
        }
        String pwd = SecurityTool.iosdecrypt(bean.getUpwd());
        String upwd = loginService.encryptPwd(bean, pwd);
        bean.setUpwd(upwd);
        String userLoginPwd = userMapper.queryUserPwd(bean.getUid());
        if (bean.getSource() == 3021) {//得宝合作的就不校验密码了
            bean.setUpwd(userLoginPwd);
        }
        if (!checkIdCardBindCount(bean)) {
            result.setCode(USER_ID_BINDING_OUTLIMIT_ERROR);
            result.setDesc("您的身份证绑定账户超过限制，如有疑问请联系客服");
            log.info("用户的身份证绑定账户超过限制,用户名:" + bean.getUid() + " 身份证号:" + bean.getIdCardNo());
            return result;
        }
        log.info("bindIdcard-----> upwd=" + upwd + ",userLoginPwd=" + userLoginPwd);
        if (!bean.getUpwd().equals(userLoginPwd)) {
            result.setCode(FAIL);
            result.setDesc("您输入的登录密码错误");
            log.info("绑定身份证失败,用户名=" + bean.getUid() + ",失败原因=" + result.getDesc());
        } else {
            String realName = bean.getRealName();
            String idCardNo = bean.getIdCardNo();
            String uid = bean.getUid();
            String upwdd = bean.getUpwd();
            //验证用户是否绑定过身份证和真实姓名
            int i = userMapper.queryCountByidcardAndRealname(uid);
            if (i == 1) {
                result.setCode(ErrorCode.USER_CARD_EXIST_ERROR);
                result.setDesc("此账户已经绑定过身份证和真实姓名");
                log.info("此账户已经绑定过身份证和真实姓名,用户名:" + bean.getUid());
                return result;
            }
            SafeBean orginSafeBean=null;
            try {
                BaseResp<SafeBean> res1 = getSafeData(bean);
                if (res1 == null||BusiCode.FAIL.equals(res1.getCode())|| res1.getData() == null) {
                    result.setCode(ErrorCode.USER_ADD_SAFEINFO_ERROR);
                    result.setDesc("查询用户信息用户基本信息出错");
                    throw new Exception();
                }else if (BusiCode.NOT_EXIST.equals(res1.getCode())) {
                }else {
                    //查询有数据时，记录下来，以便后面回滚
                    orginSafeBean = res1.getData();
                    orginSafeBean.setNickid(bean.getUid());
                }
                //调用安全中心存数据
                SafeBean safeBean = new SafeBean();
                safeBean.setNickid(uid);
                safeBean.setRealname(realName);
                safeBean.setIdcard(idCardNo);
                safeBean.setUsersource(SourceConstant.CAIPIAO);
                BaseReq<SafeBean> req = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
                BaseResp<SafeBean> resp = safeCenterInterface.addUserTable(req);
                if (!"0".equals(resp.getCode()) || resp.getData() == null) {
                    result.setCode(ErrorCode.USER_ADD_SAFEINFO_ERROR);
                    result.setDesc("添加用户基本信息到安全中心出错");
                    log.info("添加用户身份证至用户安全中心信息出错,用户名:" + bean.getUid());
                    throw new Exception();
                }
            } catch (Exception e) {
                log.error("调用安全中心出错", e);
                 throw new Exception(e);
            }
            //对敏感数据md5加密
            String idCardMD5 = MD5Helper.md5Hex(idCardNo);
            String realNameMD5 = MD5Helper.md5Hex(realName);
            try {
                log.info("从前端传来的身份证 idcard:" + idCardNo + " 真实姓名 realname:" + realName);
                int ret = userMapper.bindIdcard(idCardMD5, realNameMD5, realName, idCardNo, uid, upwdd);
                log.info("ret=" + ret);
                if (ret == 1) {
                    int flag = 7;
                    log.info("开始代理商检查!");
                    agentCheck(bean, flag);
                    result.setCode(SUCCESS);
                    result.setDesc("绑定身份证成功");
                    log.info("绑定身份证成功,送验证值,用户名=" + bean.getUid());
                } else {
                    result.setCode(FAIL);
                    result.setDesc("绑定身份证更新数据库失败!");
                }
            } catch (Exception e) {
                log.error("绑定身份证异常",e);
                if (orginSafeBean!=null) {
                    log.info("bindIdcard-安全中心调用出错,用户名:{},安全中心进行事务补偿" , bean.getUid());
                    transactionalCompensateSafeCenter(orginSafeBean,UserConstants.ROLLBACK_BINDIDCARD);
                }
                throw new Exception(e);
            }


        }
        try {
            userLogRecordService.addUserOperLog(bean, "绑定身份证", result.getDesc());
        } catch (Exception e) {
            log.error("日志入库异常",e);
        }
        log.info("result=" + result.toJson());
        return result;
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
        map1.put("usersource", SourceConstant.CAIPIAO);

        map1.put("sysdate", new Date());
        map1.put("object", bean);

        RollbackDTO rollbackDTO1 = new RollbackDTO(commitOperation1, rollbackOperation1, target1,source, map1);
        List<RollbackDTO> rollbackDTOList = new ArrayList<>();
        rollbackDTOList.add(rollbackDTO1);

        producers.sendSafeCenterList(rollbackDTOList);
    }

    @Transactional
    public Result modifyUser(UserBean bean, Result result) {
        String errDesc = "";
        String sucDesc = "";
        String key = "u_update_" + bean.getFlag();
        int flag = bean.getFlag();
        try {
            UserUtil.check(bean, flag);
            if (bean.getBusiErrCode() == 0) {
                switch (flag) {
                    case UserUtil.UPDATE_BANK: { // 修改银行卡信息
                        if (bean.getSource() == 3021) {
                            String userPwd = userMapper.queryUserPwd(bean.getUid());
                            bean.setUpwd(userPwd);
                        } else {
                            String upwd = loginService.encryptPwd(bean, bean.getUpwd());
                            bean.setUpwd(upwd);
                        }

                        if (CheckUtil.isNullString(bean.getTid())) {
                            key += "_0";// 设置银行卡信息
                            errDesc = "首次设置银行卡信息失败,密码错误或者已经绑定过银行卡";
                            sucDesc = "首次设置银行卡信息成功";
                        } else { // 修改银行卡信息
                            key += "_1";
                            errDesc = "修改银行信息失败,密码错误";
                            sucDesc = "修改银行信息成功";
                        }
                        break;
                    }
                    case UserUtil.UPDATE_BASE: {
                        errDesc = "修改基本信息失败";
                        sucDesc = "修改基本信息成功";
                        break;
                    }
                    case UserUtil.UPDATE_PASS: {
                        String pwd = loginService.encryptPwdNoSql(bean, bean.getNewValue());
                        bean.setNewValue(pwd);

                        pwd = loginService.encryptPwd(bean, bean.getUpwd());
                        bean.setUpwd(pwd);

                        errDesc = "修改密码失败，老密码不正确";
                        sucDesc = "修改密码成功";
                        break;
                    }
                    case UserUtil.UPDATE_SAFE: {
                        if (CheckUtil.isNullString(bean.getTid())) {
                            key += "_0";// 设置密保问题
                            errDesc = "设置密保问题失败,已经设置过密保问题";
                            sucDesc = "设置密保问题成功";
                        } else {// 修改密保问题
                            key += "_1";
                            errDesc = "修改密保问题失败,旧答案不正确";
                            sucDesc = "修改密保问题成功";
                        }
                        break;
                    }
                    case UserUtil.UPDATE_MOBIL: {
                        errDesc = "修改手机号码失败，老号码不正确";
                        sucDesc = "修改手机号码成功";
                        break;
                    }
                    case UserUtil.UPDATE_EMAIL: {
                        errDesc = "修改电子邮件失败，老邮件地址不正确";
                        sucDesc = "修改电子邮件成功";
                        break;
                    }
                    case UserUtil.UPDATE_NAME: {
                        String upwd = loginService.encryptPwd(bean, bean.getUpwd());
                        bean.setUpwd(upwd);
                        errDesc = "用户实名失败,密码错误";
                        sucDesc = "用户实名成功";
                        break;
                    }
                    case UserUtil.UPDATE_AUTOBUY: {
                        errDesc = "设置自动跟单状态失败";
                        sucDesc = "设置自动跟单状态成功";
                        UserAutoDTO userAutoDTO = userAutoMapper.queryUserFromUserauto(bean.getUid(), bean.getGid(), bean.getOwner());
                        if (userAutoDTO != null) {
                            String istate = String.valueOf(userAutoDTO.getIstate());
                            String ctype = istate.equals("0") ? "2" : "1";
                            String cdes = "执行了" + (ctype.equals("2") ? "禁用" : "启用") + "操作";
                            UserAutoLogPojo logPojo = new UserAutoLogPojo();
                            logPojo.setCdes(cdes);
                            logPojo.setCgameid(userAutoDTO.getCgameid());
                            logPojo.setCnickid(userAutoDTO.getCnickid());
                            logPojo.setCowner(userAutoDTO.getCowner());
                            logPojo.setCtype(userAutoDTO.getItype());
                            logPojo.setIbmoney(userAutoDTO.getIbmoney());
                            logPojo.setIminmoney(userAutoDTO.getIminmoney());
                            logPojo.setImaxmoney(userAutoDTO.getImaxmoney());
                            logPojo.setIbuy(userAutoDTO.getIbuy());
                            logPojo.setIlimit(userAutoDTO.getIlimit());
                            logPojo.setIrate(userAutoDTO.getIrate());
                            logPojo.setItype(userAutoDTO.getItype());
                            logPojo.setItimes(userAutoDTO.getItimes());
                            int i = userAutoLogMapper.insertUserAutoLog(logPojo);
                            if (i == 1) {
                                log.info("插入表 tb_user_auto_log 成功,用户名cnickid=" + userAutoDTO.getCnickid());
                            } else {
                                log.info("插入表 tb_user_auto_log 失败,用户名cnickid=" + userAutoDTO.getCnickid());
                            }
                        }
                        break;
                    }
                }
                //循环更新,,,,通了再做
                int ret = 1;
                if (ret == 1) {
                    result.setCode(SUCCESS);
                    result.setDesc(sucDesc);

                    if (flag == UserUtil.UPDATE_PASS) {
                        bean.setPwd(bean.getNewValue());
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("[").append(sucDesc).append("]");
                    sb.append("flag=").append(bean.getFlag()).append(";");
                    sb.append("性别=").append(bean.getGender()).append(";");
                    sb.append("省份=").append(bean.getProvid()).append(";");
                    sb.append("城市=").append(bean.getCityid()).append(";");
                    sb.append("QQ=").append(bean.getImNo()).append(";");
                    sb.append("手机=").append(bean.getMobileNo()).append(";");
                    sb.append("问题=").append(bean.getRid()).append(";");
                    sb.append("答案=").append(bean.getAid()).append(";");
                    sb.append("银行卡号=").append(bean.getBankCard()).append(";");
                    sb.append("银行省份=").append(bean.getProvid()).append(";");
                    sb.append("银行城市=").append(bean.getCityid()).append(";");
                    sb.append("银行名称=").append(bean.getBankName()).append(";");
                    sb.append("修改后内容=").append(bean.getNewValue());
                    userLogRecordService.addUserOperLog(bean, "修改用户信息", sb.toString());
                    if (flag == UserUtil.UPDATE_NAME) {// 用户实名时进行代理商归属的二次判断
                        agentCheck(bean, flag);
                        if (bean.getSource() == 2190 || bean.getSource() == 1309) { //送彩票活动客户端
                            ////万年历活动绑定成功送彩票   已废弃
                        }
                    }
                } else {
                    result.setCode(String.valueOf(UserErrCode.ERR_CHECK));
                    result.setDesc(errDesc);
                    log.error("用户操作失败：uid=" + bean.getUid() + " key=" + key + "desc=" + bean.getBusiErrDesc());
                }


            }
        } catch (Exception e) {
            result.setCode(String.valueOf(UserErrCode.ERR_EXCEPTION));
            result.setDesc(UserErrCode.getErrDesc(bean.getBusiErrCode()));
            log.error("UserInfoBeanStub::modifyUserInfo", e);
        }
        return result;
    }

    /**
     * 身份证绑定账号次数检测
     * 账户绑定身份证数大于5 返回false，否则返回true
     */
    private boolean checkIdCardBindCount(UserBean bean) {
        log.info("身份证绑定次数检测,用户名:" + bean.getUid() + " 身份证:" + bean.getIdCardNo());
        int count = userMapper.queryCountByidCardNo(bean.getIdCardNo());
        if (count > 5) {
            return false;
        }
        return true;
    }

    private Result checkBindIdcardParam(UserBean bean, Result result) {
        result.setCode(String.valueOf(UserErrCode.ERR_CHECK));
        if (CheckUtil.isNullString(bean.getUpwd())) {
            result.setDesc("密码不能为空");
        } else if (CheckUtil.isNullString(bean.getRealName())) {
            result.setDesc("真实姓名不能为空");
        } else if (!CheckUtil.CheckRealName(bean.getRealName())) {
            result.setDesc("真实姓名有误，请确认后重新输入");
        } else if (bean.getRealName().length() < 2 || bean.getRealName().length() > 20) {
            result.setDesc("真实姓名有误，请确认后重新输入");
        } else {
            result = isLeaglIdcardRes(bean, result);
        }
        return result;
    }

    /**
     * 代理商检查
     *
     * @param bean
     * @param flag 1：手机绑定
     */
    @Override
    public void agentCheck(UserBean bean, int flag) {
        //已经有返点的用户不跳转
        log.info("代理商检查开始");
        String nickid = bean.getUid();
        Agent_UserPojo bindInfo = agentUserMapper.getBindInfo(nickid);

        if (bindInfo != null) {
            String idcardMD5 = bindInfo.getIdCardMD5();
            String mobilenoMD5 = bindInfo.getMobilenoMD5();
            int mobbind = bindInfo.getMobileBind();
            String agentid = bindInfo.getAgentId();
            log.info("绑定信息非空[idcardMD5:{},mobileNoMD5:{},mobbind:{},agentid:{}]",idcardMD5 ,mobilenoMD5 ,mobbind ,agentid);
            boolean bln = true;
            //1 绑定电话号
            //2 初次绑定身份证和银行卡
            //7 用户实名时进行代理商归属的二次判断
            if (flag != 1) {
                if (!StringUtil.isEmpty(idcardMD5)) {
                    String oldagentid = "";
                    List<Agent_UserPojo> agentIsAgentList = agentUserMapper.getAgentByIdCardAndNidcidWithIsAgent(nickid, idcardMD5);
                    if (agentIsAgentList != null && agentIsAgentList.size() > 0) {// 判断系统中是否有其他这个身份证号码注册的用户
                        // 绑定在 VIP层级关系下
                        bln = false;
                        oldagentid = agentIsAgentList.get(0).getAgentId();
                        if (!oldagentid.equalsIgnoreCase(agentid)) {// 更新该用户的代理商
                            log.info("更新1[agentid:{},oldagentid:{}]", agentid,oldagentid);
                            if (userMapper.updateAgentIdByNickId(oldagentid, nickid, 1) == 1) {
                                try {
                                    userLogRecordService.addUserOperLog(bean, "二次验证用户代理商", "更新" + bean.getUid() + "代理商归属:" + agentid + "=>" + oldagentid + " 判断依据用户的身份证号码之前挂在其他代理商名下");
                                    log.info("更新操作日志1");
                                } catch (Exception e) {
                                    log.error("日志入库异常",e);
                                }
                            }
                        }
                    } else {
                        List<Agent_UserPojo> agentList = agentUserMapper.getAgentByIdCardAndNidcid(nickid, idcardMD5);
                        if (agentList != null && agentList.size() > 0) {// 判断系统中是否有其他这个身份证号码注册的用户
                            // 绑定在 VIP层级关系下
                            bln = false;
                            oldagentid = agentList.get(0).getAgentId();
                            //遍历父节点代理权限
                            List<String> parentAgengIdList = agentMapper.getParenAgentId(oldagentid);
                            if (parentAgengIdList != null && parentAgengIdList.size() > 0) {
                                oldagentid = parentAgengIdList.get(0);
                                if (!oldagentid.equalsIgnoreCase(agentid)) {// 更新该用户的代理商
                                    log.info("更新2[agentid:{},oldagentid:{}]", agentid, oldagentid);
                                    if (userMapper.updateAgentIdByNickId(oldagentid, nickid, 1) == 1) {
                                        try {
                                            userLogRecordService.addUserOperLog(bean, "二次验证用户代理商", "更新" + bean.getUid() + "代理商归属:" + agentid + "=>" + oldagentid + " 判断依据用户的身份证号码之前挂在其他代理商名下");
                                            log.info("操作日志更新2");
                                        } catch (Exception e) {
                                            log.error("日志入库异常",e);
                                        }
                                    }
                                }
                            }
                        } else {//注册vip下边的用户 ，如果是媒体用户 转回媒体
                            List<String> vipLevelsList = agentMapper.getVipLevelByAgentId(agentid);
                            if (vipLevelsList != null && vipLevelsList.size() > 0) { //在vip名下 进行媒体判断跳转
                                List<Agent_UserPojo> medialAgentList = agentUserMapper.getAgentByIdCardAndNickidWithNormal(nickid, idcardMD5);
                                if (medialAgentList != null && medialAgentList.size() > 0) {// 判断系统中是否有其他这个身份证号码注册的用户 媒体用户
                                    // 绑定在 VIP层级关系下
                                    bln = false;
                                    oldagentid = medialAgentList.get(0).getAgentId();
                                    if (!oldagentid.equalsIgnoreCase(agentid)) {//
                                        log.info("更新3[agentid:{},oldagentid:{}]", agentid, oldagentid);
                                        if (userMapper.updateAgentIdByNickId(oldagentid, nickid, 0) == 1) {
                                            try {
                                                userLogRecordService.addUserOperLog(bean, "二次验证用户代理商", "更新" + bean.getUid() + "代理商归属:" + agentid + "=>" + oldagentid + " 判断依据用户的身份证号码之前挂在其他名下");
                                                log.info("操作日志更新3");
                                            } catch (Exception e) {
                                                log.error("日志入库异常",e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (mobbind == 1 && bln && !StringUtil.isEmpty(mobilenoMD5)) {
                //已经是VIP用户不在变更代理
                List<Agent_UserPojo> agentIsAgentList = agentUserMapper.getAgentByMobilenoAndNickidWidthIsAgent(nickid, mobilenoMD5);
                if (agentIsAgentList != null && agentIsAgentList.size() > 0) {// 判断系统中是否有其他已经绑定验证手机号码的用户
                    // 绑定在 VIP层级关系下
                    String oldagentid = agentIsAgentList.get(0).getAgentId();
                    if (!oldagentid.equalsIgnoreCase(agentid)) {// 更新该用户的代理商
                        log.info("更新4[agentid:{},oldagentid:{}]", agentid, oldagentid);
                        if (userMapper.updateAgentIdByNickId(oldagentid, nickid, 1) == 1) {
                            try {
                                userLogRecordService.addUserOperLog(bean, "二次验证用户代理商", "更新" + bean.getUid() + "代理商归属:" + agentid + "=>" + oldagentid + " 判断依据用户绑定的手机号码之前挂在其他代理商名下");
                                log.info("操作日志更新4");
                            } catch (Exception e) {
                                log.error("日志入库异常",e);
                            }
                        }
                    }
                } else {
                    List<Agent_UserPojo> agentList = agentUserMapper.getAgentByMobilenoAndNickid(nickid, mobilenoMD5);
                    if (agentList != null && agentList.size() > 0) {// 判断系统中是否有其他已经绑定验证手机号码的用户
                        // 绑定在 VIP层级关系下
                        bln = false;
                        String oldagentid = agentList.get(0).getAgentId();
                        //遍历父节点代理权限
                        List<String> parentAgentList = agentMapper.getParenAgentId(oldagentid);
                        if (parentAgentList != null && parentAgentList.size() > 0) {
                            oldagentid = parentAgentList.get(0);
                            if (!oldagentid.equalsIgnoreCase(agentid)) {// 更新该用户的代理商
                                log.info("更新5[agentid:{},oldagentid:{}]", agentid, oldagentid);
                                if (userMapper.updateAgentIdByNickId(oldagentid, nickid, 1) == 1) {
                                    try {
                                        userLogRecordService.addUserOperLog(bean, "二次验证用户代理商", "更新" + bean.getUid() + "代理商归属:" + agentid + "=>" + oldagentid + " 判断依据用户绑定的手机号码之前挂在其他代理商名下");
                                        log.info("操作日志更新5");
                                    } catch (Exception e) {
                                        log.error("日志入库异常",e);
                                    }
                                }
                            }
                        }
                    } else {//注册vip下边的用户 ，如果是媒体用户 转回媒体
                        List<String> vipLevelList = agentMapper.getVipLevelByAgentId(agentid);
                        if (vipLevelList != null && vipLevelList.size() > 0) { //在vip名下 进行媒体判断跳转
                            List<Agent_UserPojo> mediaAgentList = agentUserMapper.getAgentByMobilenoAndNickidWithNormal(nickid, mobilenoMD5);
                            if (mediaAgentList != null && mediaAgentList.size() > 0) {// 判断系统中是否有其他这个身份证号码注册的用户 媒体用户
                                // 绑定在 VIP层级关系下
                                bln = false;
                                String oldagentid = mediaAgentList.get(0).getAgentId();
                                if (!oldagentid.equalsIgnoreCase(agentid)) {// 更新该用户的代理商
                                    log.info("更新6[agentid:{},oldagentid:{}]", agentid, oldagentid);
                                    if (userMapper.updateAgentIdByNickId(oldagentid, nickid, 0) == 1) {
                                        try {
                                            userLogRecordService.addUserOperLog(bean, "二次验证用户代理商", "更新" + bean.getUid() + "代理商归属:" + agentid + "=>" + oldagentid + " 判断依据用户手机号码之前挂在其他名下");
                                            log.info("操作日志更新6");
                                        } catch (Exception e) {
                                            log.error("日志入库异常",e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int updateWinAndChaseNumberSwitch(UserBean bean) throws Exception {
        int count = pushZJZHSwitchMapper.countByNicdid(bean.getUid());
        // 判断中奖追号推送开关表中是否存在当前用户的信息，不存在就插入一条，存在就更新。
        if (count == 0) {
            int resCount = pushZJZHSwitchMapper.insert(bean.getUid(), 1, 1);
            if (resCount != 1) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_WINANDCHASENUMBERSWITCH_SAVE_ERROR));
                log.error("用户：{},开关设置参数保存出错", bean.getUid());
                return -100;
            }
        } else {
            int resCount = pushZJZHSwitchMapper.updateByNickid(bean.getWinSwitch(), bean.getChaseSwitch(), bean.getUid());
            if (resCount != 1) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_WINANDCHASENUMBERSWITCH_SAVE_ERROR));
                log.error("用户：{},开关设置参数更新出错", new Object[]{bean.getUid()});
                return -100;
            }
        }
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(bean.getUid() + UserConstants.CACHEKEY_OWNER_MAP);
        Map<String, Integer> map = (Map<String, Integer>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.USERCENTER);
        if (map != null) {
            log.info("用户: {}原始设置参数,中奖推送:{},追号提醒:{}", bean.getUid(), map.get(UserConstants.CACHEKEY_OWNER_MAP_AWARD), map.get(UserConstants.CACHEKEY_OWNER_MAP_ZHUIHAO));
        }
        Map<String, Integer> valuemap = new HashMap<String, Integer>();
        valuemap.put(UserConstants.CACHEKEY_OWNER_MAP_AWARD, bean.getWinSwitch());
        valuemap.put(UserConstants.CACHEKEY_OWNER_MAP_ZHUIHAO, bean.getChaseSwitch());

        cacheBean.setKey(bean.getUid() + UserConstants.CACHEKEY_OWNER_MAP);
        cacheBean.setTime(7 * Constants.TIME_DAY);
        cacheBean.setValue(JSONObject.toJSONString(valuemap));
        redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
        log.info("用户: {}更新设置参数,中奖推送:{},追号提醒:{}", bean.getUid(), bean.getWinSwitch(), bean.getChaseSwitch());
        return 0;
    }

    /**
     * 保存激活数据
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public int saveActiveData(UserBean bean) throws Exception {
        int count = 0;
        if (bean.getMtype() == 2) {//ios有限比对idfa
            if (!StringUtil.isEmpty(bean.getIdfa())) {
                count = activeDataMapper.countByIdfa(bean.getIdfa());
            }
        } else {
            if (!StringUtil.isEmpty(bean.getImei())) {
                bean.setIdfa("");
                count = activeDataMapper.countByImei(bean.getImei());
            }
        }

        if (count == 0) {
            ActiveDataPojo activeDate = new ActiveDataPojo();
            BeanUtilWrapper.copyPropertiesIgnoreNull(bean, activeDate);
            int resCount = activeDataMapper.insert(activeDate);
            if (resCount == 1) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("激活数据保存成功");
            } else {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SAVEACTIVE_SAVE_ERROR));
                bean.setBusiErrDesc("数据存储失败");
                log.error("激活信息存储失败,数据内容:[idfa:{},imei:{},source:{}]", bean.getIdfa(), bean.getImei(), bean.getSource());
            }
        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ACTIVEDATA_DIVIDE_DATA_EXIST));
            bean.setBusiErrDesc("设备信息已存在");
            log.info("该设备信息已存在:[idfa:{},imei:{}]", bean.getIdfa(), bean.getImei());
        }
        return bean.getBusiErrCode();
    }

    @Override
    public PushBean queryGtTag(UserBean bean) {
        PushBean pushBean = new PushBean();
        //查询tag前查询用户是否有中奖推送和追号提醒
        String zh = "1";
        String zj = "1";
        log.info("queryOpenKeyDetail参数==[uid=" + bean.getUid() + ",normalTag=" + bean.getNormalTag() + ",+MiTag=" + bean.getMiTag() + "]");
        try {
            if (StringUtil.isNotEmpty(bean.getUid())) {//已登录
                log.info("用户已登录，开始查询openKey");
                //查询缓存
                log.info("查询缓存中的用户开关");
                //查询缓存中的用户开关
                CacheBean cacheBean = new CacheBean();
                cacheBean.setKey(bean.getUid() + UserConstants.CACHEKEY_OWNER_MAP);
                Map<String, Integer> map = (Map<String, Integer>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.USERCENTER);

                if (null == map) {
                    log.info("用户[" + bean.getUid() + "]获取缓存keyMap为空，开始查库");
                    //从用户开关表查询开关
                    queryUserOpenKey(pushBean, bean, zh, zj);
                } else {
                    log.info("用户[" + bean.getUid() + "]获取缓存keyMap==>" + map.toString());
                    pushBean.setOpenKey(getOpenKey(map));
                }
            }
        } catch (Exception e) {
            log.error("查询openKey失败", e);
        }
        return pushBean;
    }

    @Override
    public PushBean queryGtTagDetail(PushBean pushBean, UserBean userBean) {

//        int code = userBean.getBusiErrCode();
        if (StringUtil.isEmpty(userBean.getUid())) {
            pushBean.setOpenKey("0" + "," + "0");
        }
        log.info("queryGtTagDetail用户名:" + userBean.getUid());
        String normalTag = userBean.getNormalTag();
        String miTag = userBean.getMiTag();
        String packageName = userBean.getPackageName();
        if (StringUtil.isEmpty(normalTag)) {
            normalTag = "";
        }
        if (StringUtil.isEmpty(miTag)) {
            miTag = "";
        }
        if (StringUtil.isEmpty(packageName)) {
            packageName = "";
        }
        log.info("queryGtTagDetail用户名:"+ userBean.getUid() +",packageName:" + packageName);
        Map<String, String> map = new HashMap<String, String>();
        map.put(PushChannel.GT.getKey(), normalTag);
        map.put(PushChannel.Mi.getKey(), miTag);

        PushUtil push = new PushUtil();
        Set<String> set = new HashSet<>();
        log.info("queryGtTagDetail用户名:"+ userBean.getUid() +",查询tag入参==" + map.toString());
        set = push.queryTag(map, packageName);
        userBean.setSet(set);
        return pushBean;
    }

    public PushBean setData(PushBean pushBean, UserBean userBean) {
        Set<String> set = userBean.getSet();
        StringBuilder setBuilder = new StringBuilder();
        for (String c : set) {
            setBuilder.append(c).append(",");
        }

        log.info("查询tag的set集合==" + set.toString());
        if (!setBuilder.toString().equals("")) {
            pushBean.setTag(setBuilder.substring(0, setBuilder.toString().length() - 1));
        } else {
            pushBean.setTag("");
        }
        log.info("" + pushBean.getTag());
        StringBuilder resp = new StringBuilder();
        userBean.setBusiErrCode(0);
        return pushBean;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertIntoData(UserBean bean) throws Exception {
        try {
            String id = "";
            String channel = "";
            if (!StringUtil.isEmpty(bean.getNormalTag()) && StringUtil.isEmpty(bean.getMiTag())) {// ios只传normalTag，miTag为空
                id = bean.getNormalTag();
                channel = "GT";
                insertPushData(bean, id, channel);
            }
            if (!StringUtil.isEmpty(bean.getNormalTag()) && !StringUtil.isEmpty(bean.getMiTag())) {
                id = bean.getNormalTag();
                channel = "GT";
                insertPushData(bean, id, channel);
                id = bean.getMiTag();
                channel = "Mi";
                insertPushData(bean, id, channel);
            }
            if (StringUtil.isEmpty(bean.getNormalTag()) && !StringUtil.isEmpty(bean.getMiTag())) {
                id = bean.getMiTag();
                channel = "Mi";
                insertPushData(bean, id, channel);
            }

        } catch (Exception e) {
            log.info("插入" + bean.getUid() + "tag信息出错");
            throw new Exception(e);
        }
    }

    public void insertPushData(UserBean bean, String id, String channel) throws Exception {
        Set<String> set = bean.getSet();
        log.info("保存tag参数：uid=" + bean.getUid() + ",id=" + bean.getNormalTag() + ",set=" + set.toString() + ",channel==" + channel);
        int i = pushDeviceDataMapper.queryHistory(id);

        if (StringUtil.isNotEmpty(bean.getUid())) {
            log.info("用户[" + bean.getUid() + "]已登录，进行保存或更新tag操作");
            if (1 == i) {
                log.info("用户[" + bean.getUid() + "]存在，更新最后时间");
                int j = pushDeviceDataMapper.updateFinalTime(channel, set.toString(), bean.getUid(), id);
                if (1 == j) {
                    log.info("用户[" + bean.getUid() + "]已登录，更新成功");
                } else {
                    log.info("已登录，更新失败，事务回滚");
                    throw new Exception("用户[" + bean.getUid() + "]已登录，更新失败，事务回滚");
                }
            } else {
                //插入
                log.info("用户[" + bean.getUid() + "]已登录，插入tag操作");
                int k = pushDeviceDataMapper.insertTagRecord(id, channel, set.toString(), bean.getUid());
                if (1 == k) {
                    log.info("用户[" + bean.getUid() + "]已登录，插入成功");
                } else {
                    log.info("用户[" + bean.getUid() + "]已登录，插入失败，事务回滚");
                    throw new Exception("用户[" + bean.getUid() + "]已登录，插入失败，事务回滚");
                }
            }
        }
        if (StringUtil.isEmpty(bean.getUid())) {
            log.info("未登录且记录不存在，插入tag操作");
            if (1 == i) {
                log.info("用户[" + bean.getUid() + "]已存在，更新最后时间");
                int m = pushDeviceDataMapper.updateTime(id);
                if (m == 1) {
                    log.info("未登录，更新成功");
                } else {
                    log.info("未登录，更新失败，事务回滚");
                    throw new Exception("用户[" + bean.getUid() + "]未登录，更新失败，事务回滚");
                }
            } else {
                //插入
                int k = pushDeviceDataMapper.insertTag(id, channel, set.toString());
                if (k == 1) {
                    log.info("未登录，插入成功");
                } else {
                    log.info("未登录，插入失败，事务回滚");
                    throw new Exception("用户[" + bean.getUid() + "]未登录，插入失败，事务回滚");
                }
            }
        }
    }


    public void queryUserOpenKey(PushBean resultBean, UserBean bean, String zh, String zj) throws Exception {

        PushPojo pushBean = pushZJZHSwitchMapper.findSwitch(bean.getUid());
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(bean.getUid() + UserConstants.CACHEKEY_OWNER_MAP);
        cacheBean.setTime(7 * Constants.TIME_DAY);
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        if (null != pushBean) {//有就传回去
            zj = pushBean.getZjSwitch();
            zh = pushBean.getZhSwitch();
            log.info("用户[" + bean.getUid() + "]开关,zj=" + zj + ",zh=" + zh);
            //加入缓存
            resultMap.put(UserConstants.CACHEKEY_OWNER_MAP_AWARD, Integer.valueOf(zj));
            resultMap.put(UserConstants.CACHEKEY_OWNER_MAP_ZHUIHAO, Integer.valueOf(zh));
            log.info("准备放入缓存的resultMap==>" + resultMap.toString());

            cacheBean.setValue(JSONObject.toJSONString(resultMap));
            redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
            resultBean.setOpenKey(zj + "," + zh);
        } else if (null == pushBean) {//没有插一条
            log.info("没有查到，向表中插入默认开关");
            int i = pushZJZHSwitchMapper.insertDefaultOpenKey(bean.getUid(), "1", "1");
            if (i == 1) {
                //加入缓存
                log.info("用户[" + bean.getUid() + "]开关插入成功,zj=" + zj + ",zh=" + zh);
                resultMap.put(UserConstants.CACHEKEY_OWNER_MAP_AWARD, Integer.valueOf(zj));
                resultMap.put(UserConstants.CACHEKEY_OWNER_MAP_ZHUIHAO, Integer.valueOf(zh));
                log.info("插入该用户后准备放入缓存的resultMap==>" + resultMap.toString());
                cacheBean.setValue(JSONObject.toJSONString(resultMap));
                redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
                resultBean.setOpenKey(zj + "," + zh);
            } else {
                log.info("用户[" + bean.getUid() + "]开关插入失败，回滚事物");
                resultBean.setOpenKey("0" + "," + "0");
                throw new Exception("用户[" + bean.getUid() + "]开关插入失败，回滚事物");
            }
        }
    }

    private static String getOpenKey(Map<String, Integer> keyMap) {
        StringBuilder builder = new StringBuilder();
        int zj = keyMap.get(UserConstants.CACHEKEY_OWNER_MAP_AWARD);
        int zh = keyMap.get(UserConstants.CACHEKEY_OWNER_MAP_ZHUIHAO);
        return builder.append(zj).append(",").append(zh).toString();
    }

    /**
     * 忘记密码-合法参数校验
     *
     * @param bean
     * @return
     */
    @Override
    public int checkParamByForget(UserBean bean) {
        if(checkUserFindpwdCnt(bean)==0){
            return 0;
        }
        //用户名检测
        if (StringUtil.isEmpty(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_USERNAMENULL_ERROR));
            bean.setBusiErrDesc("用户名不可为空");
            return 0;
        }
        //手机号码检测
        int ret = registerService.verifyMobileno(bean, bean.getMobileNo(), false);
        if (ret == 0 || bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return 0;
        }
        return 1;
    }

    //用户找回密码每天允许10次
    private int checkUserFindpwdCnt(UserBean bean) {
        int maxTryTimes = 10;
        String key = "checkUserFindpwdCnt_" + DateUtil.getDateTime((new Date()).getTime(), "yyyyMMddHH") + "_" + bean.getUid();
        CacheBean cacheBean=new CacheBean();
        cacheBean.setKey(key);
        cacheBean.setTime(Constants.TIME_DAY);
        int accessTimes = 0;
        String cacheTimes=(String) redisClient.getObject(cacheBean,String.class,log,SysCodeConstant.USERCENTER);
        if (!StringUtil.isEmpty(cacheTimes)) {
           accessTimes=Integer.valueOf(cacheTimes);
        } else {
            cacheBean.setValue(1+"");
            redisClient.setString(cacheBean,log,SysCodeConstant.USERCENTER);
        }
        log.info("用户:{}找回密码,已使用次数:{},总次数:{}",bean.getUid(),accessTimes,maxTryTimes);
        if (accessTimes >= maxTryTimes) {
            log.info("访问过于频繁,uid=" + bean.getUid() + ",maxAccessTimes=" + maxTryTimes);
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_FOUND_PWD_FIND_CNT));
            bean.setBusiErrDesc("用户找回密码每天允许10次~");
            return 0;
        } else {
            cacheBean.setValue(accessTimes+1+"");
            redisClient.setString(cacheBean,log,SysCodeConstant.USERCENTER);
            return 1;
        }
    }
    /**
     * 忘记密码-用户名手机号匹配
     *
     * @param bean
     */
    @Override
    public void matchUidAndMobile(UserBean bean) {
        log.info("忘记密码用户名手机号匹配,用户名:" + bean.getUid() + " 手机号:" + bean.getMobileNo());
        try {
            Map<String, String> md5Map = createMd5Key(null, null, null, bean.getMobileNo());
            if (null == md5Map || StringUtils.isEmpty(md5Map.get(UserConstants.MOBILENO_KEY))) {
                log.info("生成手机号md5序列号失败");
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CREATEMD5_FAIL));
                bean.setBusiErrDesc("生成手机号md5序列号失败");
                return;
            }
            int count = userMapper.queryCountByNickidAndMobileNo(bean.getUid(), md5Map.get(UserConstants.MOBILENO_KEY));
            if (count > 0) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("用户名手机号检测通过");
            } else {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_NAMEPHONENO_MATCH_ERROR));
                bean.setBusiErrDesc("您的用户名与手机号不匹配,请检查后重新输入");
                return;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_NAMEPHONENO_MATCH_EXCEPTION));
            bean.setBusiErrDesc("用户名手机号匹配异常");
            log.error("用户名手机号匹配异常,用户名:" + bean.getUid() + ",手机号:" + bean.getMobileNo(), e);
        }
    }

    /**
     * 设置新密码-参数合法性校验
     *
     * @param bean
     * @return
     */
    @Override
    public int checkParamBySet(UserBean bean) {
        if (!bean.getPwd().equals(bean.getConfupwd())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_DIFFPWD_ERROR));
            bean.setBusiErrDesc("您两次输入的密码不匹配请重新输入");
            return 0;
        }

        if (StringUtil.isEmpty(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_USERNAMENULL_ERROR));
            bean.setBusiErrDesc("您的用户名不能为空");
            return 0;
        }
        return 1;
    }

    /**
     * 设置新密码
     *
     * @param bean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setNewPwd(UserBean bean) throws Exception {
        log.info("用户设置新密码,用户名:" + bean.getUid());
        //检查验签
        int signRsp = checkSign(bean);
        if (1 != signRsp) {
            return;
        }
        //解密密码
        int decryptRsp = decryptPwd(bean);
        if (1 != decryptRsp) {
            return;
        }
        //加密密码
        String md5Key = encryptPwd(bean, bean.getPwd());
        bean.setPwd(md5Key);
        //设置密码
        settingPwd(bean);
    }

    /**
     * 设置密码
     *
     * @param bean
     */
    private void settingPwd(UserBean bean) throws Exception {
        int ret;
        if (1 == bean.getFlag()) {
            //快登设置密码
            ret = shortcutSetPwd(bean);
        } else {
            //普通设置密码
            ret = userMapper.updatePwdRests(bean.getUid(), bean.getPwd());
        }
        if (ret == 1) {
            tokenManageService.updateTokenPassword(bean, bean.getPwd());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("设置密码成功");
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SETPWDFAIL_ERROR));
            bean.setBusiErrDesc("设置密码失败");
        }
    }

    /**
     * 支付宝，微信快登设置密码
     *
     * @param bean
     */
    private int shortcutSetPwd(UserBean bean) {
        int ret = 0;
        int count = userMapper.selectPwd(bean.getUid());
        if (count > 0) {
            ret = userMapper.updatePwd(bean.getUid(), bean.getPwd());
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PWDREPET_ERROR));
            bean.setBusiErrDesc("您已经设置过首次密码,不可重复设置");
            log.info("支付宝,微信快登用户首次设置过密码，不可重复设置,用户名:" + bean.getUid());
        }
        return ret;
    }

    /**
     * 检验签名延签
     *
     * @param bean
     */
    private int checkSign(UserBean bean) throws Exception {
        int flag = 1;
        //检查签名验签
        String signMsg = bean.getSignmsg();
        String encrypted = MD5Util.compute(bean.getPwd() + UserConstants.ENCRYPT_KEY);
        //检查手机号与用户名是否匹配
        if (!signMsg.equals(encrypted)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SIGNFAIL_ERROR));
            bean.setBusiErrDesc("签名验签错误");
            flag = 0;
        }
        return flag;
    }

    /**
     * 解密密码
     *
     * @param bean
     */
    private int decryptPwd(UserBean bean) throws Exception {
        int flag = 0;
        Map<String, String> map = DecryptUtil.decryptByAesBase64(bean.getPwd(), null, null, null, bean.getConfupwd());
        if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.PWD_KEY)) && !StringUtils.isEmpty(map.get(UserConstants.ADD_KEY))) {
            bean.setPwd(map.get(UserConstants.PWD_KEY));
            bean.setConfupwd(map.get(UserConstants.ADD_KEY));
            flag = 1;
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
            bean.setBusiErrDesc("参数解密失败");
            log.info("参数解密失败");
        }
        return flag;
    }

    /**
     * 发送手机验证码
     *
     * @param bean
     */
    private void sendMobileVerifyCode(UserBean bean) {
        try {
            //绑定和忘记手机号的代理商id根据用户名来查找,如果不存在则置空
            String desc = "";
            if (0 == bean.getFlag()) {
                desc = "绑定";
                bean.setComeFrom("");
            } else if (1 == bean.getFlag()) {
                desc = "忘记密码获取短信";
                bean.setComeFrom("");
            } else if (2 == bean.getFlag()) {
                desc = "注册";
            }

            BaseResp<SafeBean> safeResp = invokeSafeStoreMobileNo(bean.getMobileNo());
            if (null == safeResp || null == safeResp.getData() || StringUtils.isEmpty(safeResp.getData().getMobileId())) {
                log.info("调用安全中心Mobileid为空");
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_PARAM_NULL));
                bean.setBusiErrDesc("暂无数据");
                return;
            }
            String mobileMd5 = MD5Helper.md5Hex(bean.getMobileNo());
            //查询验证码次数
            int count = bindMsgMapper.selectMobileMsg(bean.getFlag(), mobileMd5);
            if (count >= 5) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SURPASSMAXBINDNO_ERROR));
                bean.setBusiErrDesc("对不起，" + desc + "次数已超过每天限制次数(5次)！");
                return;
            }

            int ipMsg = smsMapper.selectIpMsg(bean.getIpAddr());
            if (ipMsg >= 100) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REQUESTSUPASS_ERROR));
                bean.setBusiErrDesc("对不起，该IP地址请求次数过于频繁！");
                log.info("短信发送接口,IP地址请求过于频繁,手机号:" + bean.getMobileNo() + " ip地址:" + bean.getIpAddr());
                return;
            }

            String verycode = CheckUtil.randomNum();
            String uid = UserConstants.PLATFORM;
            if (!StringUtil.isEmpty(bean.getUid()) && !"(null)".equals(bean.getUid())) {
                uid = bean.getUid();
            }

            String smsContent = "";
            if (bean.getFlag() == 0) {
                smsContent = "尊敬的用户，您本次绑定手机的验证码为: " + verycode + "。【9188彩票网】";
            } else if (bean.getFlag() == 1) {
                smsContent = "尊敬的9188用户，您本次找回密码的验证码为: " + verycode + "。【9188彩票网】";
            } else if (bean.getFlag() == 2) {
                smsContent = "尊敬的9188用户，您本次注册用户的验证码为: " + verycode + "。【9188彩票网】";
            }

            //如果用户名不为空,查询用户的代理商id
            if (!StringUtil.isEmpty(bean.getUid())) {
                List<PartPojo> agents = userMapper.selectUserAgent(bean.getUid());
                if (agents != null && agents.size() > 0) {
                    PartPojo agent = agents.get(0);
                    bean.setComeFrom(agent.getCagentid());
                }
            }

            //将之前的未进行验证的短信验证码置未验证证状态
            bindMsgMapper.updateOverDueMsg(mobileMd5, bean.getFlag());
            int rs = bindMsgMapper.insertBindMsg(bean.getMobileNo(), verycode, bean.getFlag(), uid, mobileMd5);
            int rss;
            if (UserConstants.VOICE.equals(bean.getSigntype())) {
                //语音验证码只传数字
                smsContent = verycode;
                rss = smsMapper.insertMsgByVoice(bean.getMobileNo(), smsContent, bean.getComeFrom(), bean.getIpAddr(), mobileMd5);
            } else {
                rss = smsMapper.insertMsg(bean.getMobileNo(), smsContent, bean.getComeFrom(), bean.getIpAddr(), mobileMd5);
            }

            if (rs != 1 && rss != 1) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_AUTHCREATEFAIL_ERROR));
                bean.setBusiErrDesc("对不起，验证码生成失败");
                return;
            }
            log.info("验证码生成成功[mphone]=" + bean.getMobileNo());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("验证码已生成");
            return;
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SENDSMS_ERROR));
            bean.setBusiErrDesc("发送手机验证码发生异常");
            log.error("UserBeanStub::sendMobileVerifyCode", e);
        }
    }

    /**
     * 调用安全中心-存储/获取手机号或手机号id
     *
     * @param mobileNo
     */
    private BaseResp<SafeBean> invokeSafeStoreMobileNo(String mobileNo) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERCENTER);
        SafeBean safeBean = new SafeBean();
        safeBean.setMobileno(mobileNo);
        baseReq.setData(safeBean);
        BaseResp<SafeBean> baseResp = safeCenterInterface.mobileNo(baseReq);
        return baseResp;
    }

    /**
     * 检测手机号的绑定状态
     *
     * @param bean
     */
    @Override
    public void checkMobileAccount(UserBean bean) {
        try {
            String md5Mobile = MD5Helper.md5Hex(bean.getMobileNo());
            //忘记密码不需要检测账户个数
            if (!(1 == bean.getFlag())) {
                int count = userMapper.selectAccountNum(md5Mobile);
                if (count >= 5) {
                    bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SURPASSMAXBINDNO_ERROR));
                    bean.setBusiErrDesc("该手机号码绑定的账户过多,请更换号码");
                    return;
                }
            }

            log.info("手机号码账户检测,手机号" + bean.getMobileNo());
            //如果是手机注册,需要检测是否已存在手机注册的账号
            if ("mobRegister".equals(bean.getFunc())) {
                int count = userMapper.selectRegist(md5Mobile);
                if (count > 0) {
                    bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REGIST_PHONENOREPET_ERROR));
                    bean.setBusiErrDesc("该手机号已注册过账号，请直接登录");
                    return;
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKPHONESTATUS_EXCEPTION));
            bean.setBusiErrDesc("检测手机号绑定状态出错");
            log.error("检测手机号绑定状态出错,手机号:" + bean.getMobileNo(), e);
        }
    }

    @Override
    public Result checkBankCard(UserBean bean) {
        log.info("校验提款银行卡号  uid==" + bean.getUid());
        Result result = new Result();
        if (null != bean.getBankCard()) {
            String realBankCard = decryptCard(bean.getBankCard());
            String md5BankCard = MD5Helper.md5Hex(realBankCard);
            //查询身份证MD5
            String md5 = userMapper.selectMd5BankCard(bean.getUid());
            if (!md5BankCard.equals(md5)) {
                result.setCode(BusiCode.FAIL);
                result.setDesc("卡号不一致");
                result.setData(BusiCode.FAIL);
                return result;
            }

            Luhn luhn = new Luhn(realBankCard);
            boolean check = luhn.check();
            if (!check) {
                result.setCode(BusiCode.FAIL);
                result.setDesc("卡号不一致");
                result.setData(BusiCode.FAIL);
                return result;
            }
            result.setCode(BusiCode.SUCCESS);
            result.setDesc("卡号输入正确");
            result.setData(BusiCode.SUCCESS);
        } else {
            result.setCode(BusiCode.FAIL);
            result.setDesc("卡号为空");
            result.setData(BusiCode.FAIL);
        }
        return result;
    }

    /**
     * 检测手机号imei
     *
     * @param bean
     */
    private void imeiExist(UserBean bean) {
        try {
            int ret = userMapper.selectImei(bean.getImei());
            if (0 == ret) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_IMEINULL_ERROR));
                bean.setBusiErrDesc("imei不存在");
                log.info("imei参数不存在,imei:" + bean.getImei());
                return;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKIMEI_ERROR));
            bean.setBusiErrDesc("Imei检测查询出错");
            log.error("检测手机Imei出错", e);
        }
    }

    /**
     * 检测重复短信
     *
     * @param bean
     */
    private void checkRepeatSms(UserBean bean) {
        String rs = redisGetCacheStr("repeatSms_" + bean.getMobileNo());
        if (StringUtils.isEmpty(rs)) {
            redisSetCache("repeatSms_" + bean.getMobileNo(), "1", Constants.TIME_MINUTE, null);
            log.info("1min重复发送短息首次添加,手机号:" + bean.getMobileNo());
            return;
        } else {
            Integer repeatCount = Integer.parseInt(rs);
            if (1 == repeatCount) {
                redisSetCache("repeatSms_" + bean.getMobileNo(), "2", Constants.TIME_MINUTE, null);
                log.info("1min重复发送短息第二次添加,手机号:" + bean.getMobileNo());
            } else {
                redisSetCache("repeatSms_" + bean.getMobileNo(), (repeatCount + 1) + "", Constants.TIME_MINUTE, null);
                log.info("1min重复发送短信超过限制,手机号:" + bean.getMobileNo() + "重复次数:" + (repeatCount + 1));
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SENDSMSSUPASS_ERROR));
                bean.setBusiErrDesc("您的访问过于频繁，休息一下吧~");
            }
        }
    }

//    public int checkAccessIpAddr(UserBean bean, HttpServletRequest request) {
//        String ipAddr = "";
//        int maxAccessTimes = 0;
//        maxAccessTimes = IPUtils.readMaxAccessTimes();
//        ipAddr = IPUtils.getRealIp(request).trim();
//        String key = "ipAccessTimes_" + DateTimeUtil.getDateTime((new Date()).getTime(), "yyyyMMddHH") + "_" + ipAddr;
//        CacheClient cc = CacheClient.getInstance();
//        int accessTimes = 0;
//        if (cc.keyExists(key)) {
//            Object obj = cc.get(key);
//            if (obj != null) {
//                accessTimes = Integer.parseInt(obj.toString());
//            }
//        } else {
//            cc.set(key, 1, Constants.TIME_HOUR);
//        }
//        if (accessTimes >= maxAccessTimes) {
//            log.info("访问过于频繁,ipAddr=" + ipAddr + ",maxAccessTimes=" + maxAccessTimes);
//            bean.setBusiErrCode(-1);
//            bean.setBusiErrDesc("您的访问过于频繁，休息一下吧~");
//            return 0;
//        } else {
//            cc.set(key, accessTimes + 1, Constants.TIME_HOUR);
//            return 1;
//        }
//    }

    /**
     * 发送短信前参数校验
     *
     * @param bean
     * @return
     */
    @Override
    public int checkParamByMobSms(UserBean bean) {
        try {
            if (!initMobParams(bean)) {
                return 0;
            }
            if (!checkSignmsg(bean)) {
                return 0;
            }
            bean.setIpAddr(bean.getIpAddr());
            String result = mobileNoDecrypt(bean);
            if (BusiCode.FAIL.equals(result)) {
                log.info("手机号解密失败");
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
                bean.setBusiErrDesc("参数解密失败");
                return 0;
            }
            int ret = registerService.verifyMobileno(bean, bean.getMobileNo(), false);
            if (ret == 0 || Integer.parseInt(BusiCode.SUCCESS) != bean.getBusiErrCode()) {
                return 0;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKSMS_ERROR));
            bean.setBusiErrDesc("发送短信校验异常");
            log.info("发送短信校验异常：{}", e);
        }
        return 1;
    }

    /**
     * 手机号解密
     *
     * @param bean
     * @return
     * @throws Exception
     */
    private String mobileNoDecrypt(UserBean bean) throws Exception {
        String ret = BusiCode.FAIL;
        Map<String, String> map = DecryptUtil.decryptByAesBase64(null, null, bean.getMobileNo(), null, null);
        if (map.size() > 0 && !StringUtils.isEmpty(map.get(UserConstants.MOBILENO_KEY))) {
            bean.setMobileNo(map.get(UserConstants.MOBILENO_KEY));
            ret = BusiCode.SUCCESS;
        } else {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PARAMDECODE_ERROR));
            bean.setBusiErrDesc("参数解密失败");
        }
        return ret;
    }


    /**
     * 验证签名
     *
     * @param bean
     * @return
     */
    private boolean checkSignmsg(UserBean bean) throws Exception {
        int count = 0;
        if (StringUtil.isEmpty(bean.getSignmsg())) {
            log.info("发送短信签名为空 手机号:" + bean.getMobileNo() + " 签名:" + bean.getSignmsg());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_SIGNSMS_NULL));
            bean.setBusiErrDesc("出错啦,请稍后重试~");
            return false;
        }
        int signKeyCount = 0;
        String signKey = redisGetCacheStr(bean.getMobileNo() + bean.getSignmsg());
        if (!StringUtils.isEmpty(signKey)) {
            signKeyCount = Integer.parseInt(signKey);
        }
//        if (null != signKey){//旧
        if (signKeyCount > 2) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_SIGNSMS_REAPET));
            bean.setBusiErrDesc("出错啦,请稍后重试~");
            log.info("手机号与验签组合已存在,signKey:" + bean.getMobileNo() + bean.getSignmsg());
            return false;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("flag=").append(bean.getFlag()).append("&mobileNo=").append(bean.getMobileNo()).
                append("&stime=").append(bean.getStime()).append("&key=").append(UserConstants.MD5_KEY);
        String localSign = SecurityTool.getMD5Str(builder.toString());
        if (!localSign.equals(bean.getSignmsg())) {
            log.info("发送短信签名验签失败,signmsg:" + bean.getSignmsg() + " localSign:" + localSign + " mobile:" + bean.getMobileNo() +
                    " stime:" + bean.getStime() + " newValue:" + bean.getNewValue());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SIGNFAIL_ERROR));
            bean.setBusiErrDesc("验签失败");
            return false;
        }
//        redisSetCache(bean.getMobileNo() + bean.getSignmsg(),"1",Constants.TIME_DAY,null);//旧
        count = count + 1;
        redisSetCache(bean.getMobileNo() + bean.getSignmsg(), count + "", Constants.TIME_DAY, null);
        return true;
    }

    /**
     * 初始化发送短信参数
     *
     * @param bean
     * @return
     */
    private boolean initMobParams(UserBean bean) throws Exception {
        byte[] byteArr = GeneralRSAUtil.decryptByPrivateKey(GeneralBase64Utils.decode(bean.getData()), GeneralRSAUtil.SMS_PRIVATE_KEY);
        String jsonData = new String(byteArr,"UTF-8");
        log.info("短信加密参数內容:" + jsonData);
        JSONObject json = JSON.parseObject(jsonData);
        injectIntoUserBean(json, bean);
        //检测配置文件
        if (!checkConfig(bean)) {
            return false;
        }
        return true;
    }

    /**
     * 将json参数注入到UserBean中
     *
     * @param json
     * @param bean
     */
    private void injectIntoUserBean(JSONObject json, UserBean bean) throws Exception {
        Set<String> keySet = json.keySet();
        Method[] methods = bean.getClass().getMethods();
        for (String key : keySet) {
            String firstChar = key.substring(0, 1).toUpperCase();
            String fieldName = firstChar + key.substring(1);
            String setter = "set" + fieldName;
            for (Method method : methods) {
                if (setter.equalsIgnoreCase(method.getName())) {
                    String classtype = method.getParameterTypes()[0].getName();
                    Object value = json.get(key);
                    if ("int".equals(classtype)) {
                        value = Integer.parseInt(json.getString(key));
                    }
                    method.invoke(bean, value);
                    break;
                }
            }
        }
    }

    /**
     * 检测配置文件
     *
     * @param bean
     * @return
     */
    private boolean checkConfig(UserBean bean) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(UserConstants.SMS_CONFIG));
        JXmlWrapper smsNode = xml.getXmlNode("sms");
        String value = smsNode.getStringValue("@value");
        if ("1".equals(value) && StringUtil.isEmpty(bean.getYzm())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PICAUTHNULL_ERROR));
            bean.setBusiErrDesc("请输入图形验证码");
            return false;
        }
        return true;
    }

    /**
     * 发送短信验证码(新)
     *
     * @param bean
     */
    @Override
    public void sendMobMsg(UserBean bean) {
        checkRepeatSms(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return;
        }
        if (StringUtil.isEmpty(bean.getYzm())) {
            checkDeviceId(bean);
            if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
                return;
            }
            checkSendCount(bean);
            if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
                return;
            }
        } else {
            log.info("发送短信超过2次,session中的图片验证码={},参数中的图形验证码={}", bean.getCode(), bean.getYzm());
            if (StringUtils.isEmpty(bean.getCode()) || !bean.getCode().equalsIgnoreCase(bean.getYzm())) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PICAUTH_ERROR));
                bean.setBusiErrDesc("图形验证码错误");
                return;
            }
        }
        log.info("对手机号进行检测,用户的手机号为:" + bean.getMobileNo());
        checkMobileAccount(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return;
        }
        registerService.queryagentid(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return;
        }
        sendMobileVerifyCode(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return;
        }
    }

    /**
     * 检测短信发送次数
     *
     * @param bean
     */
    private void checkSendCount(UserBean bean) {
        try {
            String md5mobile = MD5Helper.md5Hex(bean.getMobileNo());
            int count = userMapper.selectSendSmsCount(md5mobile);
            if (count >= 2) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PICAUTHNULL_ERROR));
                bean.setBusiErrDesc("请输入图形验证码");
                log.info("手机号超过24小时两次发送限制,需要输入图形验证码:" + bean.getIdfa());
                return;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKIDFA_ERROR));
            bean.setBusiErrDesc("系统异常,请稍后重试");
            log.error("检测手机idfa出错", e);
        }
    }

    /**
     * 检测手机设备号
     *
     * @param bean
     */
    private void checkDeviceId(UserBean bean) {
        String imeiCache;
        if (bean.getMtype() == 1) {
            imeiCache = redisGetCacheStr("sms_" + bean.getImei() + "_" + bean.getFlag());
        } else {
            imeiCache = redisGetCacheStr("sms_" + bean.getIdfa() + "_" + bean.getFlag());
        }
        if (StringUtils.isEmpty(imeiCache)) {
            if (bean.getMtype() == 1) {
                redisSetCache("sms_" + bean.getImei() + "_" + bean.getFlag(), "1", Constants.TIME_HOUR, null);
            } else {
                redisSetCache("sms_" + bean.getIdfa() + "_" + bean.getFlag(), "1", Constants.TIME_HOUR, null);
            }
            log.info("手机设备号缓存首次添加,key:sms_" + bean.getImei() + "_" + bean.getFlag() + " mobileNo:" + bean.getMobileNo());
        } else {
            Integer count = Integer.parseInt(imeiCache);
            if (bean.getMtype() == 1) {
                imeiExist(bean);
            } else {
                idfaExist(bean);
            }

            if (bean.getBusiErrCode() != 0) {
                log.info("手机Imei存在检测错误 errcode:" + bean.getBusiErrCode() + " errdesc:" + bean.getBusiErrDesc()
                        + " imei:" + bean.getImei() + " mobileNo:" + bean.getMobileNo());
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PICAUTHNULL_ERROR));
                bean.setBusiErrDesc("请输入图形验证码");
                return;
            }
            if (count < 10) {
                count = count + 1;
                log.info("手机设备号缓存已存在且未超过次数,key:sms_" + bean.getImei() + "_" + bean.getFlag() + " 当前次数:" + count + " mobileNo:" + bean.getMobileNo());
                if (bean.getMtype() == 1) {
                    redisSetCache("sms_" + bean.getImei() + "_" + bean.getFlag(), count + "", Constants.TIME_HOUR, null);
                } else {
                    redisSetCache("sms_" + bean.getIdfa() + "_" + bean.getFlag(), count + "", Constants.TIME_HOUR, null);
                }
            } else {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_IMEICACHE_OVERFOLLOW));
                bean.setBusiErrDesc("系统异常,请稍后重试。"+ErrorCode.USER_IMEICACHE_OVERFOLLOW);
                if (1 == bean.getMtype()) {
                    redisSetCache("sms_" + bean.getImei() + "_" + bean.getFlag(), count + "", Constants.TIME_HOUR, null);
                } else {
                    redisSetCache("sms_" + bean.getIdfa() + "_" + bean.getFlag(), count + "", Constants.TIME_HOUR, null);
                }
                log.info("手机设备号缓存已存在且已超过次数,key:sms_" + bean.getImei() + "_" + bean.getFlag() + " 当前次数:" + count + " mobileNo:" + bean.getMobileNo());
                return;
            }
        }
        return;
    }

    /**
     * 检测手机号idfa
     *
     * @param bean
     */
    private void idfaExist(UserBean bean) {
        try {
            if (UserConstants.IDFALIST.equals(bean.getIdfa())) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_GETIDFA_ERROR));
                bean.setBusiErrDesc("ios设备的idfa获取不正确");
                return;
            }
            int count = userMapper.selectActiveData(bean.getIdfa());
            if (count == 0) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_IDFANULL_ERROR));
                bean.setBusiErrDesc("idfa不存在");
                log.info("idfa参数不存在,idfa:" + bean.getIdfa());
                return;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKIDFA_ERROR));
            bean.setBusiErrDesc("idfa检测查询出错");
            log.error("检测手机idfa出错", e);
        }
    }

    /**
     * 检测用户名
     *
     * @param bean
     * @return
     */
    @Override
    public int checkParamByCheckUserNick(UserBean bean) {
        if (StringUtil.isEmpty(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_USERNAMENULL_ERROR));
            bean.setBusiErrDesc("用户名不可为空");
            return -1;
        }
        if (!CheckUtil.CheckUserName(bean.getUid())) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_NAME_FOMAT_ERROR));
            bean.setBusiErrDesc("用户名格式错误");
            return -1;
        }
        if (bean.getUid().length() < 4 || bean.getUid().length() > 16) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_NAMELENGTH_ERROR));
            bean.setBusiErrDesc("用户名长度为4-16个字符");
            return -1;
        }
        return 0;
    }

    /**
     * 检测用户名是否重复
     *
     * @param bean
     */
    @Override
    public void checkUserNick(UserBean bean) {
        int count = userMapper.selectUserCount(bean.getUid());
        if (count > 0) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_REPET_ERROR));
            bean.setBusiErrDesc("该用户名已存在");
            return;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("用户名检测通过");
    }

    @Override
    public String queryUserDefaultPwd(UserBean bean) {
        String flag;
        flag = userMapper.selectPwdFlag(bean.getUid());
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        if ("1".equals(flag)) {
            bean.setBusiErrDesc("可直接修改密码");
        } else {
            bean.setBusiErrDesc("不可直接修改密码");
            flag = "0";
        }
        return flag;
    }

    @Override
    public String getHotLineString(UserBean bean) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.SERVICE_HOTLINE));
        List<JXmlWrapper> hotlineList = xml.getXmlNodeList("hotline");
        StringBuilder builder = new StringBuilder(BaseBean.XML_HEAD);
        builder.append("<Resp code=\"" + 0 + "\" desc=\"" + "查询成功" + "\">");
        //将XML文件下的每个content进行解析
        for (int i = 0; i < hotlineList.size(); i++) {
            JXmlWrapper generalRules = hotlineList.get(i).getXmlNode("general-rules");
            //通用规则解析
            boolean flag = ParseGeneralRulesUtil.parseGeneralRules(generalRules, bean);
            if (flag) {
                ParseGeneralRulesUtil.writeToBuilder(hotlineList.get(i), "row", builder, UserConstants.SERVICE_HOTLINE_KEY);
            }
        }
        builder.append("</Resp>");
        return builder.toString();
    }

    @Override
    public int rebackUserPhotoStatus(UserBean bean) throws Exception {
        log.info("用户头像反馈，uid==" + bean.getUid());
        String userImgPath = bean.getUserImgPath();
        //图片路径及名称
        int result = 0;
        if (!StringUtil.isEmpty(userImgPath)) {
            String photo = userImgPath.replace(UserConstants.MOBILE_URL, "");
            result = userMapper.updateUserPhotoReback(bean.getUid());
        } else {
            //图片路径为空
            result = -1;
        }
        return result;
    }

    /**
     * 查询身份证银行卡绑定信息
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public BaseResp queryIdBankBinding(BaseBean bean) throws Exception {
        BaseResp baseResp = new BaseResp();
        baseResp.setCode(BusiCode.SUCCESS);
        baseResp.setDesc("查询成功");
        IdBankBindingDTO idBankBindingDTO = new IdBankBindingDTO();

        idBankBindingDTO = queryUserBinding(bean);

        if (bean.getBusiErrCode() < 0) {
            baseResp.setCode(BusiCode.NOT_EXIST);
            baseResp.setDesc("无身份证银行卡绑定信息");
            return baseResp;
        }

        // 设置银行卡账号
        String bankCardNo = idBankBindingDTO.getBankCard();
        bankCardNo = StringUtil.isEmpty(bankCardNo) ? "" : bankCardNo;
        int length = bankCardNo.length();
        if (length > 5) {
            bankCardNo = bankCardNo.substring(0, 4).concat("********").concat(bankCardNo.substring(length - 4, length));
        } else if (length > 0) {
            bankCardNo = bankCardNo.concat("********");
        } else {
            baseResp.setCode(BusiCode.USER_IDBANK_BINDING_NOBINDING);
            baseResp.setDesc("未绑定银行卡");
        }

        String bankName = BankUtil.bankInfoMap.get(idBankBindingDTO.getBcode());
        bankName = StringUtil.isEmpty(bankName) ? "" : bankName;
        idBankBindingDTO.setBankName(bankName);
        idBankBindingDTO.setBankCard(bankCardNo);

        //增加进行中的提款查询
        int num = userCashMapper.getProgressTakeMoney(bean.getUid());
        idBankBindingDTO.setDrawingMoneyNum(num);
        if (Integer.parseInt(BusiCode.USER_IDBANK_BINDING_EXIST_APPLY) == bean.getBusiErrCode()) {
            idBankBindingDTO.setDrawMoneyDesc("银行卡变更信息审核中，暂不能进行提款");
        } else {
            idBankBindingDTO.setDrawMoneyDesc("借记卡充值消费50%后可提现，信用卡充值不可提现");
        }

        // 取得提款次数
        num = userCashMapper.getTakeMoneyDailyNum(bean.getUid());
        int drawNum = 3 - num;
        idBankBindingDTO.setTodayDrawNum(num);
        idBankBindingDTO.setDrawNum(drawNum <= 0 ? 0 : drawNum);

        // 取得图片链接
        String imageUrl = BankUtil.bankImageMap.get(idBankBindingDTO.getBcode());
        idBankBindingDTO.setLinkimg(StringUtil.isEmpty(imageUrl) ? "" : imageUrl);

        baseResp.setData(idBankBindingDTO);
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());

        return baseResp;
    }

    /**
     * 查询身份证银行卡绑定信息
     *
     * @param bean
     * @return
     */
    private IdBankBindingDTO queryUserBinding(BaseBean bean) {
        log.info("查询身份证银行卡绑定信息：[uid:{}]", bean.getUid());

        IdBankBindingDTO idBankBindingDTO = new IdBankBindingDTO();
        UserPojo userPojo = userMapper.queryIdBankBinding(bean.getUid());
        UserAcctPojo acctPojo = userAcctMapper.getBalanceByNickid(bean.getUid());

        if (userPojo != null && acctPojo != null) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询成功");

            int safeIndex = 30;
            if (!StringUtil.isEmpty(userPojo.getIdcard())) {
                safeIndex += 20;
            }
            if (!StringUtil.isEmpty(userPojo.getMobileNo())) {
                safeIndex += 20;
            }
            if (!StringUtil.isEmpty(userPojo.getBankCard())) {
                safeIndex += 20;
            }

            BeanUtilWrapper.copyPropertiesIgnoreNull(userPojo, idBankBindingDTO);
            idBankBindingDTO.setSafeIndex(safeIndex);
            idBankBindingDTO.setBankBranch(userPojo.getBankName());
            idBankBindingDTO.setBcode(userPojo.getBankCode());

            UserBankbindPojo bankbindPojo = userBankbindingMapper.getBankInfoByNickid(bean.getUid());
            if (bankbindPojo != null) {
                BeanUtilWrapper.copyPropertiesIgnoreNull(bankbindPojo, idBankBindingDTO);
                idBankBindingDTO.setBankBranch(bankbindPojo.getSubBankName() == null ? "" : bankbindPojo.getSubBankName());
                idBankBindingDTO.setBcode(bankbindPojo.getBankCode());
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_IDBANK_BINDING_EXIST_APPLY));
                bean.setBusiErrDesc("有待审核的申请，不能重复申请");
            } else {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("该卡可以正常提款");
            }
            if (UserActionBase.isNewApp(bean, "setPhoneLogin")) {
                // 返回手机号登录设置给新版客户端
                idBankBindingDTO.setLoginPhone(userPojo.getLoginPhone());
            }

            idBankBindingDTO.setAllDrowMoney(acctPojo.getAllDrowMoney() == null ? 0.0 : acctPojo.getAllDrowMoney());

        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
        }

        return idBankBindingDTO;
    }


    @Override
    public void applyModifyBankCard(UserBean bean) throws Exception {
        log.info("准备保存用户修改银行卡申请信息,用户名:" + bean.getUid());
        try {
            //审核申请资格
            if (!checkBeforeSubmit(bean)) {
                return;
            }
            //校验申请数据
            if (!checkDatum(bean)) {
                return;
            }
            //查询真实姓名和银行卡
            UserPojo userPojo = userMapper.queryRealNameAndIdCard(bean.getUid());
            if (userPojo != null && !StringUtils.isEmpty(userPojo.getIdcard()) && !StringUtils.isEmpty(userPojo.getRealName())) {
                bean.setIdCardNo(userPojo.getIdcard());
                bean.setRealName(userPojo.getRealName());
            } else {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_REALNANMEORIDCARD_NULL));
                bean.setBusiErrDesc("查询不到该用户可用信息");
                log.info("查询不到身份证号码或真实姓名");
                return;
            }
            SafeBean orginSafeBean=null;
            SafeBean safeBean = new SafeBean();
            safeBean.setNickid(bean.getUid());
            safeBean.setUsersource(SourceConstant.CAIPIAO);
            BaseReq<SafeBean> req1 = new BaseReq<>(safeBean, SysCodeConstant.USERCENTER);
            BaseResp<SafeBean> res = safeCenterInterface.getUserTable(req1);
            if (res == null||BusiCode.FAIL.equals(res.getCode())|| res.getData() == null) {
                bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
                bean.setBusiErrDesc("查询用户信息用户基本信息出错");
                throw new Exception();
            }else if (BusiCode.NOT_EXIST.equals(res.getCode())) {
            }else {
                //查询有数据时，记录下来，以便后面回滚
                orginSafeBean = res.getData();
                orginSafeBean.setNickid(bean.getUid());
            }
            //设置md5
            setMd5(bean);
            //验证通过，保存申请信息
            try {
                saveSupplyData(bean);
            } catch (Exception e) {
                log.error("申请银行卡修改异常，保存数据");
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("申请失败，请重试");
                if (orginSafeBean!=null) {
                    log.info("applyModifyBankCard-安全中心调用出错,用户名:{},安全中心进行事务补偿" , bean.getUid());
                    transactionalCompensateSafeCenter(orginSafeBean,UserConstants.ROLLBACK_MODIFYBANKCARD);
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("申请失败，请重试");
        }
    }

    /**
     * 设置md5
     *
     * @param bean
     */
    private void setMd5(UserBean bean) {
        //生成md5值
        Map<String, String> map = createMd5KeyByNameAndDoubleCard(bean);
            if(null == map){
                bean.setMd5RealName("");
                bean.setMd5IdCard("");
                bean.setMd5BankCard("");
                bean.setMd5Mobile("");
                return;
            }
            if(!StringUtils.isEmpty(map.get(UserConstants.REALNAME_KEY))){
                bean.setMd5RealName(map.get(UserConstants.REALNAME_KEY));
            }else{
                bean.setMd5RealName("");
            }
            if(!StringUtils.isEmpty(map.get(UserConstants.IDCARD_KEY))){
                bean.setMd5IdCard(map.get(UserConstants.IDCARD_KEY));
            }else{
                bean.setMd5IdCard("");
            }
            if(!StringUtils.isEmpty(map.get(UserConstants.BANKCARD_KEY))){
                bean.setMd5BankCard(map.get(UserConstants.BANKCARD_KEY));
            }else{
                bean.setMd5BankCard("");
            }
            if(!StringUtils.isEmpty(map.get(UserConstants.MOBILENO_KEY))){
                bean.setMd5Mobile(map.get(UserConstants.MOBILENO_KEY));
            }else{
                bean.setMd5Mobile("");
            }
    }

    /**
     * 调用安全中心-存储用户信息
     *
     * @param bean
     * @return
     */
    private BaseResp<SafeBean> invokeSafeCenterStoreUser(UserBean bean) {
        SafeBean safeBean = new SafeBean();
        safeBean.setNickid(bean.getUid());
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        if (!StringUtils.isEmpty(bean.getRealName())) {
            safeBean.setRealname(bean.getRealName());
        }
        if (!StringUtils.isEmpty(bean.getBankCard())) {
            safeBean.setBankcard(bean.getBankCard());
        }
        if (!StringUtils.isEmpty(bean.getIdCardNo())) {
            safeBean.setIdcard(bean.getBankCard());
        }
        if (!StringUtils.isEmpty(bean.getMobileNo())) {
            safeBean.setMobileno(bean.getMobileNo());
        }

        BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.USERCENTER);
        BaseResp<SafeBean> userTable = safeCenterInterface.addUserTable(req);
        return userTable;
    }

    /**
     * 生成md5值 - 真实姓名、身份证号、银行卡号
     *
     * @param bean
     */
    private Map<String, String> createMd5KeyByNameAndDoubleCard(UserBean bean) {
        String mobileNo = null;
        String bankCard = null;
        String idCardNo = null;
        String realName = null;
        if(!StringUtils.isEmpty(bean.getRealName())){
            realName = bean.getRealName();
        }
        if(!StringUtils.isEmpty(bean.getIdCardNo())){
            idCardNo = bean.getIdCardNo();
        }
        if(!StringUtils.isEmpty(bean.getBankCard())){
            bankCard = bean.getBankCard();
        }
        if(!StringUtils.isEmpty(bean.getMobileNo())){
            mobileNo = bean.getMobileNo();
        }
        try {
            Map<String, String> md5Map = createMd5Key(realName, idCardNo, bankCard, mobileNo);
            if (md5Map.size() > 0) {
                return md5Map;
            }
        } catch (Exception e) {
            log.info("生成md5值发生异常,{}", e);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("md5生成异常");
        }
        return null;
    }

    /**
     * 生成md5值-通用
     *
     * @param realName 真实姓名
     * @param idCardNo 身份证
     * @param bankCard 银行卡
     * @param mobileNo 手机号
     */
    private Map<String, String> createMd5Key(String realName, String idCardNo, String bankCard, String mobileNo) throws Exception {
        Map<String, String> map = new HashMap<>();
        if (!StringUtils.isEmpty(realName)) {
            map.put(UserConstants.REALNAME_KEY, MD5Helper.md5Hex(realName));
        }

        if (!StringUtils.isEmpty(idCardNo)) {
            map.put(UserConstants.IDCARD_KEY, MD5Helper.md5Hex(idCardNo));
        }

        if (!StringUtils.isEmpty(bankCard)) {
            map.put(UserConstants.BANKCARD_KEY, MD5Helper.md5Hex(bankCard));
        }

        if (!StringUtils.isEmpty(mobileNo)) {
            map.put(UserConstants.MOBILENO_KEY, MD5Helper.md5Hex(mobileNo));
        }
        return map;
    }

    /**
     * 组装安全中心Id
     *
     * @param bean
     * @param rsp
     */
    private void packageSafeId(UserBean bean, BaseResp<SafeBean> rsp) {
        if (!StringUtils.isEmpty(rsp.getData().getMobileId())) {
            bean.setSafeMobileId(rsp.getData().getMobileId());
        }

        if (!StringUtils.isEmpty(rsp.getData().getIdcard())) {
            bean.setSafeIdCardId(rsp.getData().getIdcard());
        }

        if (!StringUtils.isEmpty(rsp.getData().getRealnameId())) {
            bean.setSafeRealNameId(rsp.getData().getRealnameId());
        }

        if (!StringUtils.isEmpty(rsp.getData().getBankcardId())) {
            bean.setSafeBankCardId(rsp.getData().getBankcardId());
        }
    }

    /**
     * 保存用户申请修改银行信息
     *
     * @param bean
     */
    private void saveSupplyData(UserBean bean) throws Exception {
        int rsp = userBankbindingMapper.insertSupplyAlterInfo(bean);
        if (rsp == 1) {
            log.info("保存用户修改银行卡申请信息成功");
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("申请成功");
            StringBuilder key = new StringBuilder();
            key.append("applykey|");
            key.append(bean.getUid());
            key.append("|");
            key.append(bean.getIdCardNo());
            key.append("|");
            key.append(bean.getBankCard());
            redisSetCache(key.toString(), "pending", null, null);
        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_APPLY_FAIL));
            bean.setBusiErrDesc("申请失败，请重试!");
        }
    }

    /**
     * 校验申请数据
     *
     * @param bean
     */
    private boolean checkDatum(UserBean bean) {
        //检测银行卡号
        if (bean.getBankCard().indexOf("*") > -1) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.PAY_RECHARGE_CARDNO_ERROR));
            bean.setBusiErrDesc("银行卡号不能包含星号，请重试");
            log.info("银行卡号不能包含星号");
            return false;
        }

        //检测支行
        if (!("1".equals(bean.getBankCode()) || "2".equals(bean.getBankCode()) || "13".equals(bean.getBankCode()) || "3".equals(bean.getBankCode()) || "6".equals(bean.getBankCode()))) {
            if (StringUtil.isEmpty(bean.getSubbankName())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_SUBBANK_NULL));
                bean.setBusiErrDesc("请选择您的支行信息");
                log.info("未选择支行信息,用户名:" + bean.getUid() + " 提款码:" + bean.getBankCode());
                return false;
            }
            String subBankName = bean.getSubbankName().replaceAll("[^\\u4e00-\\u9fa5]", "");
            bean.setSubbankName(subBankName);
        }
        return true;
    }

    /**
     * 提交银行卡号修改申请前，查看是否有提交资格
     *
     * @param bean
     * @return
     */
    private boolean checkBeforeSubmit(UserBean bean) {
        boolean proceed = true;
        // 查询用户是否有待审核的申请
        int rsp = userBankbindingMapper.selectCheckApply(bean.getUid());
        if (rsp > 0) {
            log.info("已经有待审核的申请，不能提交新申请,nickid=" + bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_COMMIT_AUTH));
            bean.setBusiErrDesc("您有待审核的申请");
            proceed = false;
        }
        if (proceed) {
            // 申请未通过的一天只能只能3次【即每天不能超过三次被审核驳回】
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String stime = format.format(new Date()).concat(" 00:00:00");
            String etime = format.format(new Date()).concat(" 23:59:59");
            int limitRsp = userBankbindingMapper.selectExceedReject(bean.getUid(), stime, etime);
            if (limitRsp >= 3) {
                log.info("今天已经申请过3次，不能提交新申请,nickid=" + bean.getUid());
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_EXCEEDSUPPLY_AUTH));
                bean.setBusiErrDesc("每天最多只能申请3次");
                proceed = false;
            }
        }
        if (proceed) {
            // 每15天只能提交一次申请【即每15天只能有一条待审核和已审核通过的变更申请】
            int authRsp = userBankbindingMapper.selectAuthSupply(bean.getUid());
            if (authRsp > 0) {
                log.info("15天内已经申请过，不能提交新申请,nickid=" + bean.getUid());
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_ALTERSUPPLY_AUTH));
                bean.setBusiErrDesc("每15天只能修改1次");
                proceed = false;
            }
        }
        return proceed;
    }


    @Override
    public void CheckYZM(UserBean bean) {
        // 惠刷卡和爱夺宝等应用不检测图片验证码
        if (isHskUser(bean.getSource()) || isAiduobaoUser(bean.getSource())) {
            return;
        }

        // 老版客户端不发短信,提示升级新版,华龙网渠道不提示  !(UserConstants.VERSION + "").equals(bean.getAppversion())
        if (!isHualong(bean.getSource()) && !BaseUtil.isNewApp(bean, "yzmcheckformsg")) {
            log.info("旧版客户端不获取短信验证码，提示升级客户端,uid=" + bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_FORGETPWD_OLDVERSION_ERROR));// -10090
            bean.setBusiErrDesc("检测到您的客户端版本较低，无法获取到短信验证码，请升级客户端！");//
            return;
        }

        // 联合登录类型值为1 支付宝 不检测图片验证码
        if (bean.getHztype() == 1) {
            return;
        }

        if (StringUtils.isNotBlank(bean.getYzm()) && StringUtils.isNotBlank(bean.getRand())) {
            if (bean.getRand().equalsIgnoreCase(bean.getYzm())) {
                log.info("通过验证码校验,uid=" + bean.getUid() + ",rand=" + bean.getRand() + ",inputrand=" + bean.getYzm());
                return;
            } else {
                log.info("图形验证码验证错误,uid=" + bean.getUid() + ",rand=" + bean.getRand() + ",inputrand=" + bean.getYzm());
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PICAUTH_ERROR));// -10024 图形验证码错误
                bean.setBusiErrDesc("验证码错误");
            }
        } else {
            log.info("图形验证码为空,uid=" + bean.getUid() + ",rand=" + bean.getRand() + ",inputrand=" + bean.getYzm());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PICAUTH_ERROR));// -10024 图形验证码错误
            bean.setBusiErrDesc("验证码错误");
        }

    }

    @Override
    public void forgetPwdCheckPreCondition(UserBean bean) {
        checkPreCheckParam(bean);
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return;
        }
        try {
            if (isLotteryUser(bean.getSource())) {
                forgetPwdSendSmsYzm(bean);
            } else {
                forgetPwdCheckMobilenoLogin(bean);
            }
            // if (bean.getBusiErrCode() == Integer.parseInt(BusiCode.SUCCESS)) {
            // String yzm = CheckUtil.randomNum();
            // bean.setYzm(yzm);
            // // userCenterService.sendMobMsg(bean);
            // cpUserMapper.userSendMobSms(bean);// 发送短信
            // log.info("调用Pro结果：code:" + bean.getBusiErrCode() + ",desc:" +
            // bean.getBusiErrDesc());
            // if (bean.getBusiErrCode() == 0) {
            // bean.setBusiErrDesc("发送成功");
            // userRecordService.addUserOperLog(bean, "忘记密码-发送短信验证码",
            // bean.getBusiErrDesc());
            //
            // } else {
            // userRecordService.addUserOperLog(bean, "忘记密码-发送短信验证码",
            // bean.getBusiErrCode() + ":" + bean.getBusiErrDesc());
            // if (bean.getBusiErrCode() == 9999) {
            // bean.setBusiErrDesc("系统错误，发送失败");
            // }
            // }
            // }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CHECKSMS_ERROR));// "-10029" 发送短信校验异常
            bean.setBusiErrDesc("发送短信校验异常");
            log.error("忘记密码-检测用户输入手机号是否已经注册绑定出现异常,用户名=" + bean.getUid(), e);
        }

    }

    private void forgetPwdSendSmsYzm(UserBean bean) {
        UserPojo userDB = null;
        if (StringUtils.isNotBlank(bean.getUid())) {
            userDB = userMapper.getUserMobileBindInfoByNickId(bean.getUid());
        } else if (StringUtils.isNotBlank(bean.getMobileNo()) && CheckUtil.isMobilephone(bean.getMobileNo())) {
            userDB = userMapper.getUserMobileBindInfoByNickId(bean.getUid());
        }
        if (userDB == null) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_FORGETPWD_USERNOEXIST_ERROR));// "-10092" 用户名不存在
            bean.setBusiErrDesc("用户名不存在");
            log.error("忘记密码-用户名不存在,用户名=" + bean.getUid());
        } else if (StringUtils.isBlank(userDB.getMobileNo()) || userDB.getMobbindFlag() == 0) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_FORGETPWD_MOBILENOBIND_ERROR));// "-10093";//忘记密码：手机号未绑定
            bean.setBusiErrDesc("手机号码未绑定");
        } else {
            //验签
            if (!SecurityTool.stopSMSbomb(bean.getSignmsg(), bean.getImNo(), bean.getStime())) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SIGNFAIL_ERROR));//  "-10031"
                bean.setBusiErrDesc("验签失败");
                log.error("忘记密码-验签失败,用户名=" + bean.getUid());
                return;
            }

            // 主站彩票用户找回密码时需要手动输入手机号
            if (!isWebsiteLotteryUser(bean) && StringUtils.isBlank(bean.getNewValue())) {
                bean.setNewValue(userDB.getMobileNo());
                // 安卓找回密码时用imNo传递手机号
                if (StringUtils.isBlank(bean.getNewValue())) {
                    bean.setNewValue(bean.getImNo());
                }
            }

            String yzm = CheckUtil.randomNum();
            bean.setYzm(yzm);
            log.info("发送短信验证码手机号：" + bean.getMobileNo() + ",生成的验证码：" + yzm);
            cpUserMapper.userSendMobSms(bean);// 发送短信
            log.info("调用Pro结果：code:" + bean.getBusiErrCode() + ",desc:" + bean.getBusiErrDesc());
            try {
                if (bean.getBusiErrCode() == 0) {
                    bean.setBusiErrDesc("发送成功");
                    userLogRecordService.addUserOperLog(bean, "忘记密码-发送短信验证码", bean.getBusiErrDesc());
                } else {
                    userLogRecordService.addUserOperLog(bean, "忘记密码-发送短信验证码",
                            bean.getBusiErrCode() + ":" + bean.getBusiErrDesc());
                    if (bean.getBusiErrCode() == 9999) {
                        bean.setBusiErrDesc("系统错误，发送失败");
                    }
                }
            } catch (Exception e) {
                log.error("日志入库异常",e);
            }


        }

    }

    private void forgetPwdCheckMobilenoLogin(UserBean bean) {

        int num = userMapper.queryMobilenoBindCount(bean.getMobileNo(), bean.getPhoneLoginFlag());
        if (num < 1) {
            log.info("用户输入手机号未注册,手机号=" + bean.getMobileNo());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_FORGETPWD_MOBILENOREGISTER_ERROR));// "-10094";//忘记密码：手机号未注册
            bean.setBusiErrDesc("您输入的手机号还未注册哦");
        } else {
            bean.setBusiErrDesc("可以发送验证码");
        }
    }


    private void checkPreCheckParam(UserBean bean) {
        if (isLotteryUser(bean.getSource())) {
            if (bean.getFlag() != 1) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_FORGETPWD_NOSUPPORT_EDIT_ERROR));// -10091
                bean.setBusiErrDesc("不支持的找回密码方式");
                log.info("不支持的找回密码方式,用户名=" + bean.getUid() + ",bean.getFlag()=" + bean.getFlag());
            } else if (StringUtils.isBlank(bean.getUid())) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_USERNAMENULL_ERROR));// = "-10019";//用户名为空
                bean.setBusiErrDesc("用户名不能为空");
                log.info("用户名为空=" + bean.getUid());
            }
        } else {
            if (StringUtils.isBlank(bean.getMobileNo())) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_PHONENONULL_ERROR));// "-10021" 手机号号为空
                bean.setBusiErrDesc("手机号不能为空");
                log.info("手机号为空=" + bean.getMobileNo());
            }
        }
    }

    /**
     * lilei
     * 忘记密码重置密码功能
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Response forgetPwdRestPwd(UserBean bean) throws Exception {
        Response response = new Response();
        response.setCode("-1");
        String uid = URLDecoder.decode(bean.getUid(), "utf-8");
        bean.setUid(uid);
        checkResetPwdParam(bean);
        if (bean.getBusiErrCode() != 0) {
            String msg = "忘记密码-重置密码参数错误,用户名=" + bean.getUid() + ",手机号=" + bean.getMobileNo() + ",错误原因="
                    + bean.getBusiErrDesc();
            log.info(msg);
            response.setDesc(msg);
            return response;
        }
        if (isLotteryUser(bean.getSource())) {
            forgetPwdVerifyYzm(bean);
        } else {
            forgetPwdResetPwd(bean);
        }
        if (bean.getBusiErrCode() == 0) {
            response.setCode("0");
            response.setDesc("修改成功");
        }
        if (bean.getBusiErrCode() == 0) {
            List<TokenBean> tokenBeans = tokenManageMapper.queryAvailableTokenByNickid(bean.getUid());
            log.info("注销token,nickid=" + bean.getUid());
            String appidstr = disableUserToken(tokenBeans);
            if (StringUtil.isEmpty(appidstr)) {
                log.info("注销token失败,nickid=" + bean.getUid() + ",appids=" + appidstr);
            } else {
                bean.setMemGetNo(appidstr);
                log.info("注销token成功,nickid=" + bean.getUid() + ",appids=" + appidstr);
            }
        }
        return response;
    }


    @Override
    public String encryptPwd(BaseBean bean, String plainPwd) throws Exception {
        String privateKey = constant.UserConstants.DEFAULT_MD5_KEY;
        bean.setPrivateKey(privateKey);
        return MD5Util.compute(plainPwd + privateKey);
    }

    /**
     * 忘记密码-重置密码前检测参数正确性
     */
    private void checkResetPwdParam(UserBean bean) {
        bean.setBusiErrCode(UserErrCode.ERR_CHECK);
        if (isLotteryUser(bean.getSource())) {
            log.info("忘记密码-校验短信验证码并重置密码,用户名=" + bean.getUid());
            if (CheckUtil.isNullString(bean.getUid())) {
                bean.setBusiErrDesc("用户名不能为空");
            } else if (bean.getFlag() != 0 && bean.getFlag() != 1) {
                bean.setBusiErrDesc("不支持的找回密码方式");
            } else if (CheckUtil.isNullString(bean.getYzm())) {
                bean.setBusiErrDesc("验证信息不能为空");
            } else {
                bean.setBusiErrCode(0);
            }
        } else {
            log.info("忘记密码-重置密码,手机号=" + bean.getMobileNo() + ",source=" + bean.getSource());
            if (CheckUtil.isNullString(bean.getMobileNo())) {
                bean.setBusiErrDesc("手机号不能为空");
            } else if (CheckUtil.isNullString(bean.getPwd())) {
                bean.setBusiErrDesc("新密码不能为空");
            } else if (bean.getPwd().length() < 6 || bean.getPwd().length() > 20) {
                bean.setBusiErrDesc("密码长度必须是6-20个字符");
            } else {
                bean.setBusiErrCode(0);
            }
        }
    }

    /**
     * 忘记密码-彩票用户校验短信验证码并重置密码
     */
    private void forgetPwdVerifyYzm(UserBean bean) throws Exception {
        String newPwd = CheckUtil.randomNum();
        bean.setNewValue(encryptPwd(bean, newPwd));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", bean.getUid());
        //0邮箱1手机
        map.put("flag", bean.getFlag());
        map.put("newValue", newPwd);
        map.put("yzm", bean.getYzm());
        map.put("busiErrCode", "");
        map.put("busiErrDesc", "");
        synchronized (this) {
            cpUserMapper.forgetPwdVerifyYzm(bean);
            if (bean.getBusiErrCode() == 0) {
                userMapper.setNickidModifyAs0(bean.getMobileNo(), bean.getUid());
                bean.setBusiErrDesc("验证码正确");
                log.info("忘记密码-通过校验短信验证码,用户名=" + bean.getUid());
                bean.setPwd(newPwd);
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("验证码错误");
                log.info("忘记密码-短信验证码错误,用户名=" + bean.getUid());
            }
        }
        return;
    }

    /**
     * lilei
     * 忘记密码-爱夺宝惠刷卡等用户重置密码
     *
     * @throws Exception
     */
    private void forgetPwdResetPwd(UserBean bean) throws Exception {
        String newPwd = encryptPwd(bean, bean.getPwd());
        synchronized (this) {
            int num = userMapper.forgetPwdResetPwd(newPwd, bean.getMobileNo());
            if (num == 1) {
                // 设置昵称可修改的次数
                userMapper.setNickidModifyAs0(bean.getMobileNo(), bean.getUid());
                log.info("忘记密码-重置密码成功,手机号=" + bean.getMobileNo());
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("重置密码成功");
            } else {
                log.info("忘记密码-重置密码失败,手机号=" + bean.getMobileNo());
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("重置密码失败");
            }
        }
    }

    /**
     * 设置redis缓存
     *
     * @param key   键
     * @param value 值
     * @param time  超时时间
     * @param obj   对象
     */
    private void redisSetCache(String key, String value, Integer time, Object obj) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        if (null != obj) {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
            cacheBean.setValue(jsonObject.toJSONString());
        } else {
            cacheBean.setValue(value);
        }
        if (null != time) {
            cacheBean.setTime(time);
        }
        redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
    }

    /**
     * 获取缓存value值-String
     *
     * @param key
     */
    private String redisGetCacheStr(String key) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        String result = redisClient.getString(cacheBean, log, SysCodeConstant.USERCENTER);
        return result;
    }

    /**
     * 获取缓存value值-object
     *
     * @param key
     * @param clazz
     * @return
     */
    private Object redisGetCacheObj(String key, Class clazz) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        Object object = redisClient.getObject(cacheBean, clazz, log, SysCodeConstant.USERCENTER);
        return object;
    }

    @Override
    public BaseResp querySmsAuthCode(UserBean bean) {
        BaseResp baseResp = new BaseResp();
        try {
            List<String> code = smsMapper.selectSmsAutoCode(bean.getMobileNo());
            if (null != code && code.size() > 0) {
                String authCode = code.get(0);
                if (!StringUtil.isEmpty(authCode)) {
                    baseResp.setData(authCode);
                    baseResp.setCode(BusiCode.SUCCESS);
                    baseResp.setDesc("查询成功");
                    return baseResp;
                }
            }
        } catch (Exception e) {
            log.info("查询验证码发生异常");
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询验证码发生异常");
        }
        baseResp.setCode(BusiCode.SUCCESS);
        baseResp.setDesc("暂无数据");
        return baseResp;
    }


    /**
     * 鉴权银行卡
     *
     * @param bean
     */
    @Override
    public boolean authenticBankCard(UserBean bean) {
        //银行卡为空，不进行鉴权
        if (StringUtil.isEmpty(bean.getBankCard())) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_NULL));
            bean.setBusiErrDesc("银行卡号为空");
            log.info("用户银行卡号为空,用户名:" + bean.getUid() + " 手机号：" + bean.getMobileNo() + " 卡号:" + bean.getBankCard());
            return false;
        }
        //检测bankcode
        checkRealBankCode(bean);
        if (0 != bean.getBusiErrCode()) {
            return false;
        }

        //检测银行卡号
        int bankCardValidity = checkBankCardValidity(bean);
        if (0 != bankCardValidity) {
            return false;
        }

        //鉴权开关
        boolean open = authenticationSwitch();
        if (open) {
            log.info("鉴权被关闭……该卡不参与鉴权！");
            return true;
        }

        //鉴权未关闭的情况检测手机号是否为空
        if (StringUtil.isEmpty(bean.getMobileNo())) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_MOBILENO_NULL));
            bean.setBusiErrDesc("手机号为空");
            log.info("用户手机号为空,用户名:" + bean.getUid() + " 手机号：" + bean.getMobileNo() + " 卡号:" + bean.getBankCard());
            return false;
        }

        try {
            //卡鉴权
            authenticCard(bean);
            if (Integer.parseInt(BusiCode.SUCCESS) != bean.getBusiErrCode()) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CARDAUTH_CHECK_FAIL));
                bean.setBusiErrDesc("鉴权失败");
                log.info("用户提款银行卡鉴权失败  uid==" + bean.getUid() + " cardno==" + bean.getBankCard());
                return false;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("鉴权失败,用户名:" + bean.getUid() + ",银行卡号:" + bean.getBankCard() + ",电话:" + bean.getMobileNo());
            log.info("用户提款银行卡鉴权失败  uid==" + bean.getUid() + " cardno==" + bean.getBankCard() + ",卡鉴权异常：{}", e);
            return false;
        }
        return true;
    }

    /**
     * 卡鉴权
     *
     * @param bean
     */
    private void authenticCard(UserBean bean) {
        bean.setCardAuthFlag("0");//初始化鉴权标记
        if (StringUtil.isEmpty(bean.getBankCode()) || !supportBankMap.containsKey(bean.getBankCode())) {
            log.info("爱农银行卡鉴权不支持改银行：bankType==" + bean.getBankType());
            return;
        }

        //卡鉴权申请，得到受理订单号
        log.info("JnewPayUtil.checkCardAuthentication--userdna  查询用户信息");

        //易联手机支付
        Userdna(bean);
        String rname, idcard;
        if (Integer.parseInt(BusiCode.SUCCESS) == bean.getBusiErrCode()) {
            JXmlWrapper lxml = JXmlWrapper.parse(bean.toString());
            JXmlWrapper row = lxml.getXmlNode("row");
            rname = row.getStringValue("@rname");//用户名
            idcard = row.getStringValue("@idcard");//身份证
            bean.setRealName(rname);
            bean.setIdCardNo(idcard);
            bean.setCuserId(row.getStringValue("@userid"));
        } else {
            return;
        }
        log.info("JnewPayUtil.checkCardAuthentication--" + "  用户身份证信息：" + bean.getIdCardNo());
        log.info("JnewPayUtil.checkCardAuthentication--applyForCheck  卡鉴权申请");
        applyForCheck(bean);
    }

    /**
     * 卡健全信息查询
     *
     * @param bean
     */
    private void applyForCheck(UserBean bean) {
        log.info("爱农卡鉴权请求开始：nickid==" + bean.getUid());
        try {
            Map<String, String> paramMap = parseParam(bean);
            // 设置签名
            log.info("爱农卡鉴权请求，签名方式==" + paramMap.get("signMethod"));
            setSignature(paramMap);
            // 特殊字段数据转换
            converData(paramMap);
            String url = UserConstants.JNEW_URL;
            //发送请求
            String msg = sendMsg(url, paramMap);
            if (StringUtils.isEmpty(msg)) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SENDMSG_NULLORFAIL));
                bean.setBusiErrDesc("报文发送失败或应答消息为空");
                return;
            } else {
                log.info("爱农--卡健全请求收到结果：" + msg);
                Map map = parseMsg(msg);
                boolean result = verifySign(map);
                log.info("签名验证结果：" + result);
                if (result) {
                    if ("1001".equals(map.get("respCode"))) {//TODO 1001 set 地方
                        bean.setCardAuthFlag("1");//鉴权通过，置标记
                        return;
                    } else {
                        String respCode = map.get("respCode").toString();
                        log.info("RespCode ==" + respCode + "RespMsg ==" + map.get("respMsg"));
//						if("9999".equals(respCode)){//系统繁忙
//							bean.setBusiErrCode(0);
//							bean.setBusiErrDesc("");
//							return;
//						}
                        bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));//Todo
                        bean.setBusiErrDesc(map.get("respMsg").toString() + "-->状态码需要更换的地方2");
                        return;
                    }
                } else {
                    if (!StringUtils.isEmpty((String) map.get("respCode"))) {
                        log.info("RespCode ==" + map.get("respCode") + "RespMsg ==" + map.get("respMsg"));
                        bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));//todo
                        bean.setBusiErrDesc(map.get("respMsg").toString() + "--->状态码需要更换的地方1");
                        return;
                    } else {
                        log.info("验签失败");
                        bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_SIGNFAIL_ERROR));
                        bean.setBusiErrDesc("验签失败");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.info("卡鉴权校验出错", e);
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_CARDAUTH_CHECK_ERROR));
            bean.setBusiErrDesc("卡鉴权校验出错");
        }
    }

    /**
     * 验签
     *
     * @param paramMap
     * @return
     */
    public boolean verifySign(Map paramMap) {
        // 计算签名
        Set<String> removeKey = new HashSet<>();
        removeKey.add("signMethod");
        removeKey.add("signature");
        String signedMsg = SignUtil.getSignMsg(paramMap, removeKey);
        String signMethod = (String) paramMap.get("signMethod");
        String signature = (String) paramMap.get("signature");
        // 密钥
        String key = UserConstants.SIGN_KEY;
        return SignUtil.verifySign(signMethod, signedMsg, signature, key, UserConstants.ENCODING);
    }


    /**
     * 转换报文格式及特殊字段base64解码
     *
     * @param msg
     * @return
     */
    public Map parseMsg(String msg) {
        Map map = SignUtil.parseResponse(msg);
        // 特殊字段base64解码
        for (Iterator iterator = base64Key.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String) map.get(key);
            if (!StringUtils.isEmpty(value)) {
                try {
                    String text = new String(com.jnewsdk.util.Base64.decode(value.toCharArray()), UserConstants.ENCODING);
                    map.put(key, text);
                } catch (Exception e) {
                }
            }
        }
        return map;
    }

    /**
     * 往渠道发送数据
     *
     * @param url      通讯地址
     * @param paramMap 发送参数
     * @return 应答消息
     */
    protected String sendMsg(String url, Map<String, String> paramMap) {
        try {
            HttpClient http = new HttpSSLClient(url, "60000");
            http.setRequestMethod("POST");
            http.connect();
            // 转换参数格式
            String webForm = getWebForm(paramMap);
            http.send(webForm.getBytes());
            byte[] rspMsg = http.getRcvData();
            String msg = new String(rspMsg, UserConstants.ENCODING);
            return msg;
        } catch (Exception e) {
            log.error("sendMsg error url:"+url+" paramMap:"+paramMap.toString(),e);

        }
        return null;
    }


    /**
     * 将map转化为形如key1=value1&key2=value2...
     *
     * @param map
     * @return
     */
    protected static String getWebForm(Map<String, String> map) {
        if (null == map || map.keySet().size() == 0) {
            return "";
        }
        StringBuffer url = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String str = (value != null ? value : "");
            try {
                str = URLEncoder.encode(str, UserConstants.ENCODING);
            } catch (UnsupportedEncodingException e) {
                log.error("getWebForm");
            }
            url.append(entry.getKey()).append("=").append(str).append(UserConstants.URL_PARAM_CONNECT_FLAG);
        }
        // 最后一个键值对后面的“&”需要去掉。
        String strURL = "";
        strURL = url.toString();
        if (UserConstants.URL_PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
            strURL = strURL.substring(0, strURL.length() - 1);
        }
        return (strURL);
    }


    /**
     * 特殊字段经行bsae64转换
     *
     * @param paramMap
     */
    protected void converData(Map paramMap) {
        for (int i = 0; i < UserConstants.base64Keys.length; i++) {
            String key = UserConstants.base64Keys[i];
            String value = (String) paramMap.get(key);
            if (!StringUtil.isEmpty(value)) {
                try {
                    //                    String text = new String(Base64.encode(value.getBytes(UserConstants.ENCODING))); TODO， 原rbc-f3base64编码
                    String text = new String(Base64.encode(value.getBytes(), UserConstants.ENCODING));
                    // 更新请求参数
                    paramMap.put(key, text);
                } catch (Exception e) {
                }
            }
        }
        for (int i = 0; i < UserConstants.base64JsonKeys.length; i++) {
            String key = UserConstants.base64JsonKeys[i];
            String value = (String) paramMap.get(key);
            if (!StringUtil.isEmpty(value)) {
                try {
//                    String text = new String(Base64.encode(value.getBytes(UserConstants.ENCODING))); TODO， 原rbc-f3base64编码
                    String text = new String(Base64.encode(value.getBytes(), UserConstants.ENCODING));
                    // 更新请求参数
                    paramMap.put(key, text);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 设置签名
     *
     * @param paramMap
     */
    protected void setSignature(Map<String, String> paramMap) {
        String key = UserConstants.SIGN_KEY;
        String signMethod = UserConstants.SIGN_TYPE;
        Set<String> signKey = new HashSet<>();
        signKey.add("signMethod");
        signKey.add("signature");
        String signMsg = SignUtil.getSignMsg(paramMap, signKey);
        String signature = SignUtil.sign(signMethod, signMsg, key, UserConstants.ENCODING);
        paramMap.put("signature", signature);
    }

    /**
     * 拼接参数
     *
     * @param bean
     * @return
     * @throws UnsupportedEncodingException
     */
    private Map<String, String> parseParam(UserBean bean) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String signMethod = UserConstants.SIGN_TYPE;//签名方法
        String signature = "";//签名信息
        String version = "1.0.0";//消息版本号
        String txnType = "72";//交易类型
        String txnSubType = "01";//交易子类型
        String accessType = "0";//接入类型
        String accessMode = "01";//接入方式
        String merId = UserConstants.MER_ID;//商户号
        String merOrderId = UUID.randomUUID().toString().substring(0, 30).replace("-", "");//商户订单号
        String accNo = bean.getBankCard();//银行卡卡号
        String accType = "01";//卡类型：01借记卡，02信用卡
        String customerInfo = getCustomerInfo(bean);//银行卡验证信
        String userId = bean.getCuserId().replace("-", "");//用户号
        if (userId.length() > 30) {
            userId = userId.substring(0, 29);
        }
        map.put("signMethod", signMethod);
        map.put("version", version);
        map.put("txnType", txnType);
        map.put("txnSubType", txnSubType);
        map.put("accType", accType);
        map.put("accessType", accessType);
        map.put("accessMode", accessMode);
        map.put("merId", merId);
        map.put("merOrderId", merOrderId);
        map.put("accNo", accNo);
        map.put("customerInfo", customerInfo);
        map.put("userId", userId);

        Set<String> keySet = map.keySet();
        StringBuilder builder = new StringBuilder();
        for (String key : keySet) {
            builder.append(key).append("==").append(map.get(key)).append("|");
        }
        log.info(builder.toString());
        return map;
    }

    /**
     * 银行卡验证信息及身份信息,不支持信用卡
     *
     * @param bean
     * @return
     */
    private String getCustomerInfo(UserBean bean) {
        String certifTp = "01";//证件类型
        String certify_id = bean.getIdCardNo();//证件号码
        String customerNm = bean.getRealName();
        String phoneNo = bean.getMobileNo();
        Map<String, String> map = new HashMap<String, String>();
        map.put("certifTp", certifTp);
        map.put("certify_id", certify_id);
        map.put("customerNm", customerNm);
        map.put("phoneNo", phoneNo);
        return hashMapToJson(map);
    }

    public String hashMapToJson(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        Map.Entry<String, String> entry = null;
        StringBuilder retstr = new StringBuilder();
        retstr.append("{");
        while (it.hasNext()) {
            entry = it.next();
            retstr.append("\"");
            retstr.append(entry.getKey());
            retstr.append("\":");
            retstr.append("\"");
            retstr.append(entry.getValue());
            retstr.append("\",");
        }
        retstr.append("}");
        int lastComma = retstr.lastIndexOf(",");
        retstr.replace(lastComma, lastComma + 1, "");
        return retstr.toString();
    }

    /**
     * 易联手机支付
     *
     * @param bean
     */
    private void Userdna(UserBean bean) {
        log.info("查询用户实名信息及银行卡信息 :" + bean.getUid());
        try {
            if (CheckUtil.isNullString(bean.getUid())) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_NAME_NULL));
                bean.setBusiErrDesc("用户名不能为空");
            }

            UserPojo userPojo = userMapper.selectUserdnaParam(bean.getUid());
            if (null != userPojo) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                bean.setBusiErrDesc("获取成功");
            }
        } catch (Exception e) {
            log.info("查询发生异常,{}", e);
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询发生异常");
        }

    }

    /**
     * 鉴权开关
     *
     * @return
     */
    public boolean authenticationSwitch() {
        JXmlWrapper xml;
        boolean result = false;
        try {
            xml = JXmlWrapper.parse(new File(UserConstants.EIGHTYSEVEN_PATH));
            JXmlWrapper authSwitch = xml.getXmlNode("switch");
            String state = authSwitch.getStringValue("@authSwitch");
            if ("0".equals(state)) {
                result = true;
            }
            return result;
        } catch (Exception e) {
            log.info("解析鉴权是否打开失败，请检查配置文件:" + UserConstants.EIGHTYSEVEN_PATH + "是否正确", e);
            return false;
        }
    }

    /**
     * 检测银行卡的合法性
     *
     * @param bean
     */
    private int checkBankCardValidity(UserBean bean) {
        String regex = "[0-9]*";
        if (!bean.getBankCard().matches(regex)) {
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_BANKNO_INPUT_ERROR));
            bean.setBusiErrDesc("请检查银行卡号输入是否正确");
            return -1;
        }
        Luhn luhn = new Luhn(bean.getBankCard());
        if (luhn.check()) {
            log.info("银行卡号检测通过:" + bean.getBankCard() + " 用户名:" + bean.getUid());
            return 0;
        } else {
            log.info("银行卡号检测不通过:" + bean.getBankCard() + " 用户名:" + bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_BANKNO_CHECK_ERROR));
            bean.setBusiErrDesc("银行卡号错误，请检查您的银行卡号是否填写正确");
            return -1;
        }
    }

    /**
     * 检测bankcode
     *
     * @param bean
     */
    private void checkRealBankCode(UserBean bean) {
        String realBankCode = bean.getRealBankCode();
        if (StringUtils.isEmpty(realBankCode) || "NOCHECK".equals(realBankCode)) {
            String code;
            try {
                code = bankCardMapMapper.getBankCodeByDrawCode(bean.getRealBankCode());
                if (!StringUtils.isEmpty(code)) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBankCode(code);
                } else {
                    log.info("查询真实银行编码失败，bcode:" + bean.getRealBankCode());
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCODE_QUERY_ERROR));
                    bean.setBusiErrDesc("查询真实银行编码失败");
                    return;
                }
            } catch (Exception e) {
                bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
                bean.setBusiErrDesc("查询真实银行编码失败");
                log.info("查询真实银行编码失败", e);
            }
        }
    }

    @Override
    public Acct_UserPojo integralQueryBasicInfo(String uid) {
        return acct_userMapper.queryIpointAndUserPhoto(uid);
    }

    @Override
    public UserPojo integralQueryIdBankBinding(String uid) {
        return userMapper.queryIdBankBinding(uid);
    }

    @Override
    public String cannotSign(String uid) {
        return userAcctMapper.cannotSign(uid);
    }

    @Override
    public int clickToGetPoints(IntegralParamBean params) {
        return userMapper.clickToGetPoints(Integer.valueOf(params.getItask()), params.getUid(), params.getTask(), params.getBitand());
    }

    @Override
    public UserRecordPojo queryVipUserInfo(String uid) {
        UserRecordPojo userRecordPojo = new UserRecordPojo();
        //查询经验，头像，等级
        Acct_UserPojo pojo = acct_userMapper.queryIpointAndUserPhoto(uid);
        //查询等级昵称
        Grade_UserPojo levelPojo = grade_userMapper.queryLevelTitle(uid);
        userRecordPojo.setLevelTitle(levelPojo.getLevelTitle());
        userRecordPojo.setLevelExper(levelPojo.getLevelExper());
        userRecordPojo.setExpir(pojo.getExpir());
        userRecordPojo.setUserImg(pojo.getUserImg());
        userRecordPojo.setGradeid(pojo.getGradeid());
        return userRecordPojo;
    }

    @Override
    public String queryLevelExper(String level) {
        return grade_userMapper.queryLevelExper(level);
    }
}


