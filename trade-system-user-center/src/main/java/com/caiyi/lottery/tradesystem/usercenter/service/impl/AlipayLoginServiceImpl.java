package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.AlipayLoginBean;
import bean.SafeBean;
import bean.SourceConstant;
import bean.UserBean;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserUserinfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserUserinfoShareResponse;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import com.caiyi.lottery.tradesystem.usercenter.mq.Producers;
import com.caiyi.lottery.tradesystem.usercenter.service.*;
import com.caiyi.lottery.tradesystem.util.ConcurrentSafeDateUtil;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import com.caiyi.lottery.tradesystem.util.SecurityTool;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.google.common.collect.Maps;
import constant.AlipayLoginConstants;
import constant.CodeDict;
import constant.UserConstants;
import dto.AccountBindCaiyiDTO;
import dto.AlipayLoginDTO;
import dto.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.Ally_UserPojo;
import pojo.UserPojo;
import response.AlipayLoginResq;
import util.UserUtil;

import java.util.*;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.SUCCESS;
import static com.caiyi.lottery.tradesystem.returncode.ErrorCode.USER_ALLY_PWD_EXIST_FAIL;

/**
 * 快捷登入Service
 *
 * @author GJ
 * @create 2017-12-14 16:16
 **/
@Slf4j
@Service
public class AlipayLoginServiceImpl implements AlipayLoginService {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private Ally_UserMapper allyUserMapper;

    @Autowired
    private Ally_UserMapper ally_userMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SafeCenterInterface safeCenterInterface;

    @Autowired
    private LoginService loginService;
    @Autowired
    private AppagentMapper appagentMapper;
    @Autowired
    private CpUserMapper cpUserMapper;
    @Autowired
    private UserRecordService userRecordService;
    @Autowired
    private UserCenterService userCenterService;
    @Autowired
    private AllyMapper allyMapper;
    @Autowired
    private AgentRateMapper agentRateMapper;
    @Autowired
    private DualMapper dualMapper;
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private TotalBuyMoneyMapper totalBuyMoneyMapper;
    @Autowired
    private TokenManageService tokenManageService;
    @Autowired
    private AllyLogMapper allyLogMapper;

    @Autowired
    private Producers producers;

    @Override
    public void setAlipayUserAsVip(AlipayLoginBean bean) throws Exception {
        if (!StringUtil.isEmpty(bean.getCertNo())) {
            String md5idcard = MD5Helper.md5Hex(bean.getCertNo());
            int moneyCount=totalBuyMoneyMapper.getAbout500MoneyByidCard(md5idcard);

            if (moneyCount > 0) {
                List<String> nickidList=userMapper.selectIdBycard(md5idcard);
                boolean flg = true;
                for (String temp : nickidList) {
                    int agentcount = agentMapper.getCountAgent(temp);
                    if (agentcount > 0) {
                        flg = false;
                        log.info("该用户在主站已经是会员："+bean.getUid()+" 身份证："+bean.getCertNo());
                        break;
                    }

                }
                if(flg){
                    String nextval = dualMapper.getAgentNextval();
                    String agentnextval = "a" + nextval;
                    int res = agentMapper.addAgent(agentnextval, bean.getUid(), bean.getUid());
                    if (res == 1) {
                        userMapper.updateAgentid(agentnextval, bean.getUid());
                        String[] games = UserConstants.games;
                        for (int i=0;i<games.length;i++) {
                            agentRateMapper.addAgentRate(agentnextval, games[i]);
                        }
                    }
                }

            } else {
                log.info("该用户在支付宝的消费不够500："+bean.getUid()+" 身份证："+bean.getCertNo());
            }

        }else {
            log.info("该用户不存在或是用户没实名制："+bean.getUid());
        }

    }

    @Override
    public void addAlipayInfo(AlipayLoginBean bean) throws Exception{
        UserBean ub = new UserBean();
        ub.setUid(bean.getUid());
        ub.setUpwd(bean.getPwd());
        ub.setIdCardNo(bean.getCertNo());
        ub.setRealName(bean.getRealName());
        ub.setNewValue(bean.getUserId());
        userCenterService.agentCheck(ub ,1);
        UserUtil.isLeaglIdcard(ub);
        if (ub.getBusiErrCode() == 0 && !StringUtil.isEmpty(ub.getRealName())) {
            try {
                String realnamemd5 = MD5Helper.md5Hex(ub.getRealName());
                String idcardmd5 = MD5Helper.md5Hex(ub.getIdCardNo());
                userMapper.bindIdcard(idcardmd5,realnamemd5,ub.getRealName(),ub.getIdCardNo(),ub.getUid(),ub.getUpwd());
                try {
                    userRecordService.addUserOperLog(bean, "支付宝联合登录", "绑定用户真实信息成功：身份证=" + ub.getIdCardNo() + " 真实姓名=" + ub.getRealName());
                } catch (Exception e) {
                    log.error("日志入库异常",e);
                }
                log.info("支付宝联合登录绑定用户真实姓名[成功]: 身份证 =" + ub.getIdCardNo() + " 真实姓名=" + ub.getRealName());

            } catch (Exception e) {
                log.error("支付宝联合登录绑定用户真实姓名[失败]: 身份证 =" + ub.getIdCardNo() + " 真实姓名=" + ub.getRealName(),e);
                throw new Exception("支付宝联合登录绑定用户真实姓名失败");
            }
            // 用户实名认证后进行代理商归属跳转
            userCenterService.agentCheck(ub,  7);
        }
        if (!StringUtil.isEmpty(ub.getNewValue()) && !"null".equals(ub.getNewValue())) {
            int ret1=userMapper.updateAliyId(ub.getNewValue(), ub.getUid(), ub.getUpwd());
            if (ret1 == 1) {
                try {
                    userRecordService.addUserOperLog(bean, "支付宝联合登录", "绑定支付宝账号成功：账号=" + ub.getNewValue());
                } catch (Exception e) {
                    log.error("日志入库异常",e);
                }
                log.info("支付宝联合登录绑定支付宝账号[成功]：账号=" + ub.getNewValue());
            }else {
                log.info("支付宝联合登录绑定支付宝账号[失败]：账号=" + ub.getNewValue());
                throw new Exception("支付宝联合登录绑定支付宝账号失败");
            }
        }
        try {
            bean.setIshuodong(bean.getAllyType() == 1 ? 1 : 0);
            allyMapper.updateAliypayGreade(bean.getIshuodong(), bean.getAllyType(), bean.getReferer(), bean.getUid());
            try {
                userRecordService.addUserOperLog(bean, "支付宝联合登录", "绑定支付宝账号成功：账号=" + ub.getNewValue());
                userRecordService.addUserOperLog(bean, "支付宝联合登录", "设置支付宝账号等级成功：等级=" + bean.getAllyType());
            } catch (Exception e) {
                log.error("日志入库失败",e);
            }
            log.info("支付宝联合登录设置支付宝账号等级[成功]：等级=" + bean.getAllyType());
        } catch (Exception e) {
            log.error("支付宝联合登录设置支付宝账号等级[失败]：等级=" + bean.getAllyType(),e);
            throw new Exception("支付宝联合登录设置支付宝账号等级失败");
        }

    }

    @Override
    public void checkFirstBindParam(AlipayLoginBean bean) {
        bean.setBusiErrCode(Integer.valueOf(SUCCESS));
        if(StringUtil.isEmpty(bean.getAccesstoken())){
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_ALIYCODE_NULL));
            bean.setBusiErrDesc("支付宝授权信息为空");
            return ;
        }
        if(StringUtil.isEmpty(bean.getAliypayid())) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_ALIYID_NULL));
            bean.setBusiErrDesc("支付宝账户id为空");
            return ;
        }
        String res = UserUtil.verifyCaiyiNickid(bean.getUid());
        if (res != null) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_NAME_FOMAT_ERROR));
            bean.setBusiErrDesc(res);
            return ;
        }
        registerService.verifyMobileno(bean, bean.getMobileNo(), false);
        if(bean.getBusiErrCode()!=0){
            return ;
        }

    }

    @Override
    public void alipayFirstBind(AlipayLoginBean bean) {
        log.info("支付宝首次绑定, alipayuserid=" + bean.getAliypayid()+" 绑定的账号用户名:"+bean.getUid());
        //设置支付宝快登的默认密码
        String pwd = UUID.randomUUID().toString().replace("-", "").toString();
        bean.setPwd(pwd);
        //解密
        bean.setMobileNo(SecurityTool.iosdecrypt(bean.getMobileNo()));

        checkFirstBindParam(bean);
        if (bean.getBusiErrCode() != 0) {
            return;
        }
        bean.setIsNew(0);
        // 是首次登录,继续获取用户信息
        getAlipayUserInfo(bean,false);
        //设置用户类型
        bean.setUsertype(0);
        //设置登录信息为
        bean.setType(1);
        try{
            String encryptedPwd = loginService.encryptPwd(bean, bean.getPwd());
            bean.setPwd(encryptedPwd);
        }catch (Exception e){
            log.error("快登密码加密出错",e);
        }

        bean.setCuserId(UUID.randomUUID().toString());
        if (bean.getSource()>=1000){
            String comfrom = appagentMapper.queryAgentId(bean.getSource());
            bean.setComeFrom(comfrom);
            log.info("移动端支付宝快登用户绑定9188,uid=" + bean.getUid() + ",comform=" + bean.getComeFrom());
        }
        int res = userMapper.selectUserCount(bean.getUid());
        if (res > 0) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_REPET_ERROR));
            bean.setBusiErrDesc("用户名已存在，请换个用户名试试");
            return;
        }

    }

    private BaseResp<SafeBean> safeData(AlipayLoginBean bean) throws Exception{
        //todo 安全中心存储
        SafeBean safeBean = new SafeBean();
        safeBean.setNickid(bean.getUid());
        safeBean.setRealname(bean.getRealName());
        safeBean.setIdcard(bean.getCertNo());
        safeBean.setMobileno(bean.getMobileNo());
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        BaseReq<SafeBean> req = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
        BaseResp<SafeBean> resp = safeCenterInterface.addUserTable(req);
        return resp;
    }

    private BaseResp<SafeBean> getSafeData(AlipayLoginBean bean) throws Exception{
        SafeBean safeBean = new SafeBean();
        safeBean.setUsersource(SourceConstant.CAIPIAO);
        safeBean.setNickid(bean.getUid());
        BaseReq<SafeBean> req = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
        BaseResp<SafeBean> resp = safeCenterInterface.getUserTable(req);
        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindData(AlipayLoginBean bean) throws Exception {
        bean.setMd5Mobile(StringUtil.isEmpty(bean.getMobileNo())?null:MD5Helper.md5Hex(bean.getMobileNo()));
        try {
            BaseResp<SafeBean> resp = safeData(bean);
            //该业务下调用出错不往下执行
            if (resp==null||!SUCCESS.equals(resp.getCode()) || resp.getData() == null) {
                bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
                bean.setBusiErrDesc("添加用户基本信息出错");
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("调用安全中心出错",e);
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
            bean.setBusiErrDesc("添加用户基本信息异常");
            throw new Exception(e);
        }

        cpUserMapper.allyRegister(bean);
        try {
            userRecordService.addUserOperLog(bean, "支付宝用户绑定", "[成功]");
        } catch (Exception e) {
            log.error("日志入库失败",e);
        }
        log.info("支付宝用户绑定成功,uid=" + bean.getUid() + ",comform=" + bean.getComeFrom() + ",errDesc=" + bean.getBusiErrDesc());
        if (bean.getType() == UserConstants.ALIPAY) {
            bean.setSource(0);
            if (bean.getMtype() == 1) {
                bean.setReferer("andriod");
            } else if (bean.getMtype() == 2) {
                bean.setReferer("ios");
            }
            addAlipayInfo(bean);
        } else {
            bean.setSource(10);
        }
        setAlipayUserAsVip(bean);
        userMapper.updatePwdFlag(bean.getUid());
        bean.setPwdflag("0");

        bean.setBusiErrCode(Integer.valueOf(SUCCESS));
        bean.setBusiErrDesc("绑定成功");


    }


    /**
     * 安全中心回滚
     * @param bean
     */
    private void transactionalCompensateSafeCenter(SafeBean bean, String source){
        String commitOperation1 = "update";
        //插入失败，重新插入
        String rollbackOperation1 = "update";
        String target1 = "tb_user_vice";
        Map<String, Object> map1 = Maps.newHashMap();
        map1.put("sysdate", new Date());
        map1.put("usersource", SourceConstant.CAIPIAO);
        map1.put("object", bean);


        RollbackDTO rollbackDTO1 = new RollbackDTO(commitOperation1, rollbackOperation1, target1,source, map1);
        List<RollbackDTO> rollbackDTOList = new ArrayList<>();
        rollbackDTOList.add(rollbackDTO1);

        producers.sendSafeCenterList(rollbackDTOList);
    }

    @Override
    public void generateNewToken(UserBean bean) {
        loginService.generateNewToken(bean, CodeDict.MD5ENCRYPT);
    }

    @Override
    public void setloginData(AlipayLoginBean bean, AlipayLoginResq alipayLoginResq) {
        UserBean userBean = new UserBean();
        userBean.setUid(bean.getUid());
        userBean.setPwd(bean.getPwd());
        userBean.setSource(bean.getSource());
        userBean.setMtype(bean.getMtype());
        generateNewToken(userBean);
        AlipayLoginDTO alipayLoginDTO = alipayLoginResq.getData();
        Boolean flag= true;
        if (alipayLoginDTO == null) {
            flag= false;
            alipayLoginDTO = new AlipayLoginDTO();
        }
        alipayLoginDTO.setPwdflag("0".equals(bean.getPwdflag())?"0":"1");
        alipayLoginDTO.setAppid(userBean.getAppid());
        alipayLoginDTO.setAccesstoken(userBean.getAccesstoken());
        if (!flag) {
            alipayLoginResq.setCode(bean.getBusiErrCode() + "");
            alipayLoginResq.setDesc(bean.getBusiErrDesc());
            alipayLoginResq.setData(alipayLoginDTO);
        }

    }


    @Override
    public void loginByAlipay(AlipayLoginBean bean ,AlipayLoginResq alipayLoginResq) {
        try {
            //不可以直接可登录的情况
            if (Integer.valueOf(BusiCode.USER_ALLY_CHECK_SUCCESS).intValue()!=bean.getBusiErrCode()){
                return ;
            }else {
                setloginData(bean, alipayLoginResq);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("返回前端数据出错");
        }

        return;
    }

    @Override
    public AlipayLoginResq alipayAuthCheck(AlipayLoginBean bean) {
        AlipayLoginResq alipayLoginResq = new AlipayLoginResq();
        AlipayLoginDTO alipayLoginDTO = new AlipayLoginDTO();
        log.info("检测是否首次支付宝快登,alipayid=" + bean.getAliypayid()+ ",accesstoken=" + bean.getAccesstoken());
        if (bean.getBusiErrCode() != Integer.valueOf(SUCCESS)|| StringUtil.isEmpty(bean.getAliypayid()) || StringUtil.isEmpty(bean.getAccesstoken())) {
            log.info("支付宝快登授权信息或用户信息获取失败,alipyuserid=" + bean.getAliypayid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("获取支付宝快登信息获取失败");
            alipayLoginResq.setCode(bean.getBusiErrCode()+"");
            alipayLoginResq.setDesc(bean.getBusiErrDesc());
            return alipayLoginResq;
        }
        List<AccountBindCaiyiDTO> accountBindCaiyiDTOList = new ArrayList<>();
        try {
            List<Ally_UserPojo> ally_userPojoList = ally_userMapper.queryAllyBindCaiyiAccount(bean.getAliypayid());
            //用户是否是首次登陆，首次登陆为0，非首次登录为1
            String isFirst = "0";
            //是否需要用户输入手机号码，需要为1，不需要为0
            String inputmobileno = "0";
            if (ally_userPojoList == null || ally_userPojoList.isEmpty()||ally_userPojoList.size()>1) {
                //todo 测试首次没有获取到手机号
                if (bean.getPartner() != null&&bean.getPartner().equals("test")) {
                    bean.setMobileNo("");
                }
                //检测是否获取到支付宝手机号
                if(StringUtil.isEmpty(bean.getMobileNo())){
                    //未获取到手机号,需要用户输入手机号码
                    inputmobileno = "1";
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_FIRSTLOGIN));
                    bean.setBusiErrDesc("首次登陆，请输入用户的手机号码");
                }else {
                    // 支付宝手机号已绑定彩亿账号,返回所绑定的彩亿账号列表
                    getAlipayAccountList(bean.getMobileNo(), accountBindCaiyiDTOList);
                    bean.setBusiErrCode(Integer.valueOf(SUCCESS));
                    bean.setBusiErrDesc("查询成功");
                }
            }else{
                Ally_UserPojo ally_userPojo = ally_userPojoList.get(0);

                //不是用户的首次登陆，检测彩亿账号是否绑定手机号码
                isFirst = "1";
                //todo 测试非首次没有获取到手机号
                if (bean.getPartner() != null&&bean.getPartner().equals("test")) {
                    ally_userPojo.setMobileNo("");
                }
                //已经绑定了手机号码
                if(!StringUtil.isEmpty(ally_userPojo.getMobileNo())&&ally_userPojo.getMobileBind().intValue()==1){
                    //用户已经绑定手机号，直接登录
                    bean.setUid(ally_userPojo.getUid());
                    bean.setPwd(ally_userPojo.getPwd());
                    //所有条件符合，直接登录
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_CHECK_SUCCESS));
                    bean.setBusiErrDesc("操作成功");
                }else{
                    inputmobileno = "1";
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_NOTFIRSTLOGIN));
                    bean.setBusiErrDesc("非首次登录，请输入手机号码");
                }
                //默认值1
                String pwdflag = userMapper.queryPwdFlag(bean.getUid());
                if (StringUtil.isEmpty(pwdflag)){
                    pwdflag = "1";
                }
                bean.setPwdflag(pwdflag);
            }

            alipayLoginDTO.setIsfirst(isFirst);
            alipayLoginDTO.setInputmobileno(inputmobileno);
            //首次登陆的话，返回支付宝的手机号，唯一id和授权token信息
            if ("0".equals(isFirst)) {
                alipayLoginDTO.setAliypayid(bean.getAliypayid());
                alipayLoginDTO.setAlipayMobileno(bean.getMobileNo());
                alipayLoginDTO.setAlipayaccesstoken(bean.getAccesstoken());
            }
            else {//不是首次登陆则返回用户的uid
                alipayLoginDTO.setUid(ally_userPojoList.get(0).getUid());
            }
            alipayLoginDTO.setAccounts(accountBindCaiyiDTOList);
            alipayLoginResq.setCode(bean.getBusiErrCode()+"");
            alipayLoginResq.setDesc(bean.getBusiErrDesc());
            alipayLoginResq.setData(alipayLoginDTO);
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("登录失败");
            alipayLoginResq.setCode(bean.getBusiErrCode()+"");
            alipayLoginResq.setDesc(bean.getBusiErrDesc());
            log.error("检测是否首次支付宝快登发生异常,alipyuserid=" + bean.getAliypayid() + ",accesstoken=" + bean.getAccesstoken(),e);
        }
        return alipayLoginResq;
    }
    @Override
    public void getAlipayAccountList(String mobileno,  List<AccountBindCaiyiDTO> accountBindCaiyiDTOList) {
        String md5mobileno = MD5Helper.md5Hex(mobileno);
        List<UserPojo> userPojoList =  userMapper.getUserMobileBindInfoByMobileno(md5mobileno);
        for (UserPojo userPojo : userPojoList) {
            AccountBindCaiyiDTO accountBindCaiyiDTO = new AccountBindCaiyiDTO();
            accountBindCaiyiDTO.setUid(userPojo.getUid());
            accountBindCaiyiDTO.setLogo(UserConstants.LOGO_9188);
            accountBindCaiyiDTOList.add(accountBindCaiyiDTO);
        }
    }
    @Override
    public void getAlipayUserInfo(AlipayLoginBean bean,Boolean setMobile) {
        AlipayClient client =
                new DefaultAlipayClient(AlipayLoginConstants.GATEWAY_URL, AlipayLoginConstants.APP_ID,
                        AlipayLoginConstants.RSA_PRIVATE, AlipayLoginConstants.FORMAT,
                        AlipayLoginConstants.CHARSET);
        AlipayUserUserinfoShareRequest req = new AlipayUserUserinfoShareRequest();
        req.setProdCode(AlipayLoginConstants.PRODUCT_CODE);
        try {
            AlipayUserUserinfoShareResponse res = client.execute(req, bean.getAccesstoken());
            if (!StringUtil.isEmpty(res.getErrorCode())) {
                log.info("getAlipayUserInfo获取支付宝用户信息失败,accesstoken=" + bean.getAccesstoken() + ",errcode=" + res.getErrorCode());
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("获取支付宝用户信息失败");
            }
            if ("0".equals(res.getCertTypeValue())) {
                bean.setCertNo(res.getCertNo());
            }
            bean.setRealName(res.getRealName());
            if (setMobile) {
                bean.setMobileNo(res.getMobile());
            }
            //用户id
            bean.setUserId(res.getUserId());
            bean.setHost(UserConstants.HOST);
            //性别
            bean.setGender(res.getGender());
            //省
            bean.setProvince(res.getProvince());
            //市
            bean.setCity(res.getCity());
            //用户头像
            bean.setAvatar(res.getAvatar());
            //用户类型
            bean.setAllyType(res.getUserTypeValue()==null?0:Integer.parseInt(res.getUserTypeValue()));
            //用户身份证不符合规则，则实名设为空
            if(!UserUtil.verifyIDCard(bean.getCertNo())){
                bean.setCertNo("");
                bean.setRealName("");
            }

        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("访问支付宝用户信息失败");
            log.error("访问支付宝授权信息出错",e);
        }
    }

    @Override
    public void getAlipayOauthData(AlipayLoginBean bean) {
        log.info("getAlipayOauthData,authcode=" + bean.getAuthcode());
        AlipayClient client =
                new DefaultAlipayClient(AlipayLoginConstants.GATEWAY_URL, AlipayLoginConstants.APP_ID,
                        AlipayLoginConstants.RSA_PRIVATE, AlipayLoginConstants.FORMAT,
                        AlipayLoginConstants.CHARSET);
        AlipaySystemOauthTokenRequest req = new AlipaySystemOauthTokenRequest();
        req.setCode(bean.getAuthcode());
        req.setGrantType(AlipayLoginConstants.AUTHORIZATION_CODE);
        try {
            AlipaySystemOauthTokenResponse res = client.execute(req);
            if (!StringUtil.isEmpty(res.getErrorCode())) {
                log.info("getOauthData获取支付宝授权信息失败,authcode=" + bean.getAuthcode() + ",errcode=" + res.getErrorCode());
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("获取支付宝授权信息失败");
            }
            //支付宝的授权token信息
            bean.setAccesstoken(res.getAccessToken());
            //支付宝的唯一ID
            bean.setAliypayid(res.getAlipayUserId());
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("访问支付宝授权信息失败");
            log.error("访问支付宝授权信息出错",e);
        }
        if (bean.getBusiErrCode()!=Integer.valueOf(SUCCESS)){
            return;
        }
        getAlipayUserInfo(bean,true);

    }

    @Override
    public void checkParam4zfbBindCheck(AlipayLoginBean bean) {
        if (StringUtil.isEmpty(bean.getAuthcode())) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_ALIYCODE_NULL));
            bean.setBusiErrDesc("支付宝授权码为空");
            log.info("checkParam4zfbBindCheck支付宝授权code为空");
        }else if (bean.getType() != 1) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_ALLY_TYPE_ERROR));
            bean.setBusiErrDesc("快登类型错误");
            log.info("checkParam4zfbBindCheck快登类型错误,type=" + bean.getType());
        }else {
            bean.setBusiErrCode(Integer.valueOf(SUCCESS));
        }
    }

    @Override
    public AlipayLoginResq getAuthInfo(AlipayLoginBean bean) {
        StringBuilder authInfo = new StringBuilder();
        // 服务接口名称， 固定值
        authInfo.append("apiname=\"com.alipay.account.auth\"");
        // 商户签约拿到的app_id，如：2013081700024223
        authInfo.append("&app_id=" + "\"").append(AlipayLoginConstants.APP_ID).append("\"");
        // 商户类型标识， 固定值
        authInfo.append("&app_name=\"mc\"");
        // 授权类型，授权常量值为"AUTHACCOUNT", 登录常量值为"LOGIN"
        authInfo.append("&auth_type=\"AUTHACCOUNT\"");
        // 业务类型， 固定值
        authInfo.append("&biz_type=\"openservice\"");
        // 商户签约拿到的pid，如：2088102123816631
        authInfo.append("&pid=\"").append(AlipayLoginConstants.PARTNER).append("\"");
        // 产品码， 固定值
        authInfo.append("&product_id=\"WAP_FAST_LOGIN\"");
        // 授权范围， 固定值
        authInfo.append("&scope=\"kuaijie\"");
        // 商户标识该次用户授权请求的ID，该值在商户端应保持唯一，如：kkkkk091125
        authInfo.append("&target_id=\"").append("target_id_9188").append("\"");
        // 签名时间戳
        authInfo.append("&sign_date=\"").append(ConcurrentSafeDateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss")).append("\"");

        AlipayLoginResq alipayLoginResq = new AlipayLoginResq();
        AlipayLoginDTO alipayLoginDTO = new AlipayLoginDTO();
        try {
            String encryptAuthInfo = sign(authInfo.toString(), AlipayLoginConstants.RSA_PRIVATE);
            alipayLoginDTO.setSign(encryptAuthInfo);
            alipayLoginDTO.setAuthInfo(authInfo.toString());
            alipayLoginDTO.setSignType(UserConstants.SIGNTYPE_RSA);
            alipayLoginResq.setCode(SUCCESS);
            alipayLoginResq.setDesc("签名成功");
            alipayLoginResq.setData(alipayLoginDTO);
        } catch (Exception e) {
            alipayLoginResq.setCode(BusiCode.FAIL);
            alipayLoginResq.setDesc("签名失败");
        }

        return alipayLoginResq;
    }

    /**
     * 对参数进行签名
     *
     * @param signData 待签名数据，key rsa商户私钥
     * @return
     */
    private String sign(String signData,String key) throws Exception{
        log.info("appkd,to be encrypted sign Data=" + signData + ",key=" + key);
        String sign = SecurityTool.sign(signData, key);
        log.info("appkd,encrypted sing data=" + sign);
        return sign;
    }

    @Override
    public AlipayLoginResq bindmobileno2caiyi(AlipayLoginBean bean) {
        AlipayLoginResq response = new AlipayLoginResq();
        try {
            //解密
            bean.setMobileNo(SecurityTool.iosdecrypt(bean.getMobileNo()));
            checkParam4bindMobileno2Caiyi(bean);
            if (0 != bean.getBusiErrCode()) {
                //校验失败
                response.setCode(bean.getBusiErrCode() +"");
                response.setDesc(bean.getBusiErrDesc());
                return response;
            }
            bindMobileno2Caiyi(bean);
            if(0 != bean.getBusiErrCode()){
                //绑定失败
                response.setCode(bean.getBusiErrCode() +"");
                response.setDesc(bean.getBusiErrDesc());
                return response;
            }
            setloginData(bean,response);
            AlipayLoginDTO alipayLoginDTO = response.getData();
            alipayLoginDTO.setUid(bean.getUid());
            response.setCode(bean.getBusiErrCode()+"");
            response.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            response.setCode(BusiCode.FAIL);
            response.setDesc("绑定手机号到已有彩亿账号发生异常");
            log.error("绑定手机号到已有彩亿账号发生异常,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid(),e);
        }
        return response;
    }

    /**
     * 校验短信验证码，绑定手机号到已有彩亿账号
     */
    public void bindMobileno2Caiyi(AlipayLoginBean bean){
        log.info("绑定手机号到已有彩亿账号,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid());
        try {
            registerService.verifyMobCode(bean,bean.getMobileNo(),bean.getYzm(),"0",false);
            if(bean.getBusiErrCode() != 0){
                throw new Exception ("校验短信验证码失败,mphone=" + bean.getMobileNo() + ",verifycode=" + bean.getYzm());
            }
            int i = userMapper.bindMobilenoToCaiyi(bean.getMobileNo(),MD5Helper.md5Hex(bean.getMobileNo()),bean.getUid());
            if (i == 1) {
                bean.setBusiErrCode(Integer.valueOf(SUCCESS));
                bean.setBusiErrDesc("手机号绑定成功");
                log.info("绑定手机号到已有彩亿账号成功,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid());
                String pwd = userMapper.checkUserExist(bean.getUid());
                String memo = userMapper.selectPwdFlag(bean.getUid());
                bean.setPwdflag(StringUtil.isEmpty(memo)?"1":memo);
                bean.setUid(bean.getUid());
                bean.setPwd(pwd);
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("该用户已绑定手机号，手机号绑定失败!");
                log.info("绑定手机号到已有彩亿账号失败,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("绑定失败");
            log.error("绑定手机号到已有彩亿账号发生异常,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid(), e);
        }
    }


    private void checkParam4bindMobileno2Caiyi(AlipayLoginBean bean) {
        int ret = registerService.verifyMobileno(bean, bean.getMobileNo(),false);
        if (ret == 0 || bean.getBusiErrCode() != 0) {

            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("格式验证未通过");
            return;
        }

        if (StringUtil.isEmpty(bean.getYzm())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请输入短信验证码");
            log.info("checkParam4getCaiyiAccount验证码为空,mobileno=" + bean.getMobileNo() + ",yzm=" + bean.getYzm());
            return;
        }

        String errDesc = UserUtil.verifyCaiyiNickid(bean.getUid());
        if (!StringUtil.isEmpty(errDesc)) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_NAME_FOMAT_ERROR));
            bean.setBusiErrDesc(errDesc);
            log.info("用户输入的昵称不正确,nickid=" + bean.getUid() + ",errDesc=" + errDesc);
        }
    }


    @Override
    public BaseResp zfbgetcaiyiaccount(AlipayLoginBean bean) {
        BaseResp response = new BaseResp();
        try {
            //解密
            bean.setMobileNo(SecurityTool.iosdecrypt(bean.getMobileNo()));
            int ret = registerService.verifyMobileno(bean, bean.getMobileNo(), false);
            if (ret == 0 || bean.getBusiErrCode() != 0) {
                response.setCode(bean.getBusiErrCode() + "");
                response.setDesc(bean.getBusiErrDesc());
                return response;
            }
            log.info("校验短信验证码,查询手机号绑定彩亿账户列表,mphone=" + bean.getMobileNo() + ",verifycode=" + bean.getYzm());
            registerService.verifyMobCode(bean,bean.getMobileNo(),bean.getYzm(),"0",false);
            if (0 != bean.getBusiErrCode()) {
                response.setCode(bean.getBusiErrCode() + "");
                response.setDesc(bean.getBusiErrDesc());
                log.info("校验短信验证码失败,mphone=" + bean.getMobileNo() + ",verifycode=" + bean.getYzm());
                return response;
            }
            List<AccountBindCaiyiDTO> accountBindCaiyiDTOList = new ArrayList<>();
            getAlipayAccountList(bean.getMobileNo(), accountBindCaiyiDTOList);
            AlipayLoginDTO alipayLoginDTO = new AlipayLoginDTO();
            alipayLoginDTO.setAccounts(accountBindCaiyiDTOList);
            response.setCode(SUCCESS);
            response.setDesc("查询结束");
            response.setData(alipayLoginDTO);
            log.info("短信验证码正确,mphone=" + bean.getMobileNo());
        } catch (Exception e) {
            response.setCode(BusiCode.FAIL);
            response.setDesc("查询手机号绑定彩亿账户列表失败");
            log.error("校验短信验证码,查询手机号绑定彩亿账户列表失败,mphone=" + bean.getMobileNo() + ",verifycode=" + bean.getYzm() + ":" , e);
            return response;
        }
        return response;
    }

    @Override
    public AlipayLoginResq checkAlipayInfo(AlipayLoginBean bean) {
        AlipayLoginResq response = new AlipayLoginResq();

        //解密
        bean.setMobileNo(SecurityTool.iosdecrypt(bean.getMobileNo()));

        checkFirstBindParam(bean);//检验参数
        if (0 != bean.getBusiErrCode()) {
            response.setCode(bean.getBusiErrCode() + "");
            response.setDesc(bean.getBusiErrDesc());
            return response;
        }
        //支付宝获取信息
        log.info("getAlipayUserData,accesstoken=" + bean.getAccesstoken());
        bean.setIsNew(1);
        getAlipayUserInfo(bean, false);
        if (bean.getBusiErrCode() != 0) {
            response.setCode(bean.getBusiErrCode() + "");
            response.setDesc("支付宝获取信息失败");
            return response;
        }
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        return response;
    }

    /**
     * @Description: 支付宝用户修改默认登录密码
     * @Date: 13:58 2017/12/20
     * @param bean
     * @return:
     */
    @Deprecated
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public BaseResp Upatepwd(AlipayLoginBean bean) throws Exception {
        BaseResp response = new BaseResp();
        log.info("支付宝用户修改默认登录密码,nickid=" + bean.getUid());
        String mingPwd ="";
        String miwenPwd = bean.getPwd();
        if (miwenPwd != null || !miwenPwd.isEmpty()) {
            mingPwd = SecurityTool.iosdecrypt(miwenPwd);
        }
        String pwd = loginService.encryptPwd(bean, mingPwd);
        bean.setPwd(pwd);
        List<Object> list = userMapper.selectPwdBycpwdflag(bean.getUid());
        if (list != null && list.size() > 0) {
            response.setCode(USER_ALLY_PWD_EXIST_FAIL);
            response.setDesc("您已经设置过首次密码,不可重复设置");
            log.info("支付宝,微信快登用户首次设置过密码，不可重复设置,用户名:"+bean.getUid());
            return response;
        }
        int ret = userMapper.updatePwd(bean.getUid(), bean.getPwd());
        if (ret == 1) {
            log.info("支付宝用户修改默认登录密码成功,nickid=" + bean.getUid());
            ret =  userMapper.setNickidModifyAs0(bean.getMobileNo(), bean.getUid());
            if (ret == 1) {
                tokenManageService.updateTokenPassword(bean,bean.getPwd());
                bean.setMemGetNo(bean.getAppid());
                log.info("更新token传递的密码成功, nickid="+bean.getUid());
                response.setCode(SUCCESS);
                response.setDesc("支付宝用户修改默认登录密码成功,更新token传递的密码成功");
            }else {
                throw new Exception("修改登录密码后设置昵称可修改次数失败");
            }
        }else {
            throw new Exception("支付宝用户修改默认登录密码失败");
        }
        return response;
    }

    @Override
    public UserInfoDTO checkAccountInfo(AlipayLoginBean bean) {
        UserInfoDTO userInfo=null;
        try {
            List<UserInfoDTO> userList = userMapper.queryLoginInfoByNickid(bean.getUid());
            if (userList == null) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("您绑定的账户不存在，请重新选择绑定帐号！");
                log.info("彩亿账户不存在,alipayuserid=" + bean.getUid() + ",nickid=" + bean.getUid());
                return null;
            }
             userInfo = userList.get(0);
            String depwd = SecurityTool.iosdecrypt((bean.getPwd()));
            String md5pwd= MD5Helper.md5Hex(depwd);
            if (!md5pwd.equals(userInfo.getPwd())) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("您输入的密码不正确，请重新输入");
                return null;
            }
            int count = userMapper.queryMobilenoBindCount(MD5Helper.md5Hex(bean.getMobileNo()), false);
            if (count < 1) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("您输入的手机号未绑定任何彩亿账户，请重新输入手机号！");
                log.info("手机号未绑定任何彩亿账户,alipayuserid=" + bean.getUid() + ",mobileno=" + bean.getMobileNo());
                return null;
            }
            count = allyUserMapper.queryAllyBindCaiyiCount(bean.getAliypayid());
            if (count > 0) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("您的支付宝账户已绑定过，请直接登录！");
                log.info("支付宝账户已绑定过用户,alipayuserid=" + bean.getUid());
                return null;
            }

            count = allyMapper.queryCaiyiCountBindAlly(bean.getUid());
            if (count > 0) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("该彩亿账号已绑定过支付宝账号，不能重复绑定");
                log.info("彩亿账号已经绑定过支付宝账号，彩亿账号:" + bean.getUid());
                return null;
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("支付宝绑定到已有彩亿账号失败");
            log.error("绑定支付宝绑定到已有彩亿账号失败,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid() + ",alipayuserid=" + bean.getUid() + ",Exception==>", e);
        }
        return userInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void bindAlipay2Caiyi(AlipayLoginBean bean, UserInfoDTO userInfo) throws Exception{
        String type = "1";
        String returnInfo = "";
        int count=0;
        if(1==bean.getMtype()){
            returnInfo = "andriod";
        }else if(2==bean.getMtype()){
            returnInfo = "ios";
        }
        bean.setType(Integer.valueOf(type));
        bean.setReturnInfo(returnInfo);

        bean.setMd5Mobile(StringUtil.isEmpty(bean.getMobileNo())?null:MD5Helper.md5Hex(bean.getMobileNo()));
        bean.setMd5RealName(StringUtil.isEmpty(bean.getRealName())?null:MD5Helper.md5Hex(bean.getRealName()));
        bean.setMd5IdCard(StringUtil.isEmpty(bean.getCertNo())?null:MD5Helper.md5Hex(bean.getCertNo()));
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
            BaseResp<SafeBean> resp = safeData(bean);
            if (resp==null||!BusiCode.SUCCESS.equals(resp.getCode()) || resp.getData() == null) {
                bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
                bean.setBusiErrDesc("添加用户基本信息出错");
                log.info("添加用户信息至用户安全中心信息出错,用户名:" + bean.getUid());
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("调用安全中心出错",e);
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
            bean.setBusiErrDesc("添加用户基本信息异常");
            throw new Exception(e);
        }
        try {
            count = allyMapper.insertIntoTbAlly(bean);
            if (count != 1) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("绑定支付宝账号到彩亿账号失败");
                throw new Exception("绑定支付宝账号到彩亿账号失败");
            }

            count = allyLogMapper.insertIntoTbAllyLog(bean.getUid(), type, bean.getHost());
            if (count != 1) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("记录支付宝账号用户操作失败");
                throw new Exception("记录支付宝账号用户操作失败");
            }
            if (count == 1) {
                bean.setPwdflag(getPwdFlag(bean.getUid()));
                bean.setCuserId(userInfo.getUserId());
                bean.setPwd(userInfo.getPwd());
                log.info("绑定支付宝到已有彩亿账号成功,mobileno=" + bean.getMobileNo() + ",nickid=" + bean.getUid() + ",alipayuserid=" + bean.getUid());
                bean.setBusiErrCode(Integer.valueOf(SUCCESS));
                bean.setBusiErrDesc("绑定成功");
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("支付宝绑定到已有彩亿账号失败");
            }
        } catch (Exception e) {
            log.error("绑定已有彩亿账号出错",e);
            if (orginSafeBean!=null) {
                log.info("bindAlipay2Caiyi-安全中心调用出错,用户名:{},安全中心进行事务补偿" , bean.getUid());
                transactionalCompensateSafeCenter(orginSafeBean,UserConstants.ROLLBACK_BINDALIPAY);
            }
            throw new Exception(e);
        }



    }

    private String getPwdFlag(String uid) {
        String pwdFlag = userMapper.selectPwdFlag(uid);
        return StringUtil.isEmpty(pwdFlag) ? "1" : pwdFlag;
    }



}
