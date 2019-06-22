package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.TokenBean;
import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.BaseConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import com.caiyi.lottery.tradesystem.usercenter.service.PersonalInfoService;
import com.caiyi.lottery.tradesystem.usercenter.service.TokenManageService;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import constant.UserConstants;
import dto.IdcardBindingDTO;
import dto.UserPersonalInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.Acct_UserPojo;
import pojo.UserPhotoCashPojo;
import pojo.UserPojo;
import response.UserPersonalInfoResq;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户和账户相关信息Service
 *
 * @author GJ
 * @create 2017-12-04 11:26
 **/
@Service("userCenterPersonalInfoService")
@Slf4j
public class PersonalInfoServiceImpl implements PersonalInfoService {

    @Autowired
    private UserPhotoCashMapper userPhotoCashMapper;
    @Autowired
    private Acct_UserMapper acct_userMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBankbindingMapper userBankbindingMapper;

    @Autowired
    RedisClient redisClient;

    @Autowired
    private Agent_UserMapper agent_userMapper;

    @Autowired
    private TokenManageService tokenManageService;

    @Override
    public UserPersonalInfoResq getUserWhitelistGrade(UserBean bean) {
        UserPersonalInfoResq userPersonalInfoResq = new UserPersonalInfoResq();
        log.info("查询用户白名单等级,nickid=" + bean.getUid());
        bean.setBusiErrCode(0);
        bean.setBusiErrDesc("查询成功");
        if (StringUtil.isEmpty(bean.getUid())){
            UserPersonalInfoDTO userPersonalInfoDTO = new UserPersonalInfoDTO();
            userPersonalInfoDTO.setWhitelistGrade("0");
            userPersonalInfoResq.setCode(String.valueOf( bean.getBusiErrCode()));
            userPersonalInfoResq.setDesc(bean.getBusiErrDesc());
            userPersonalInfoResq.setData(userPersonalInfoDTO);
            return userPersonalInfoResq;
        }
        UserPersonalInfoDTO userPersonalInfoDTO = new UserPersonalInfoDTO();
        try {
            Integer whitelistGrade = userMapper.queryUserWhitelistGrade(bean.getUid());
            if (whitelistGrade!=null) {
                log.info("白名单等级,nickid=" + bean.getUid() + ",grade=" + whitelistGrade);

            }
            if (1 == whitelistGrade || 2 == whitelistGrade || 3 == whitelistGrade || 4 == whitelistGrade || 5 == whitelistGrade) {
                whitelistGrade = (1 == whitelistGrade) ? 1 : 2;
            } else if (whitelistGrade != 100) {
                whitelistGrade = 0;
            }
            userPersonalInfoDTO.setWhitelistGrade(String.valueOf(whitelistGrade));
            userPersonalInfoResq.setCode(String.valueOf( bean.getBusiErrCode()));
            userPersonalInfoResq.setDesc(bean.getBusiErrDesc());
            userPersonalInfoResq.setData(userPersonalInfoDTO);
        } catch (Exception e) {
            log.info("查询用户白名单等级发生异常", e);

            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
            userPersonalInfoResq.setCode(String.valueOf( bean.getBusiErrCode()));
            userPersonalInfoResq.setDesc(bean.getBusiErrDesc());
            userPersonalInfoResq.setData(userPersonalInfoDTO);
        }
        return userPersonalInfoResq;
    }


    @Override
    public UserPersonalInfoResq personalCenterInfo(UserBean bean) {
        UserPersonalInfoResq userPersonalInfoResq = new UserPersonalInfoResq();
        log.info("用户个人中心数据，uid==" + bean.getUid());
        try{
            //查询图像状态
            UserPhotoCashPojo userPhotoCashPojo = userPhotoCashMapper.queryUserPhotoStatus(bean.getUid());
            String status = "0",ipoint = "0",cuserphoto = "",flag = "0",rebackflag = "0",igradeid = "0";
            if (userPhotoCashPojo != null) {
                status = userPhotoCashPojo.getStatus();
                rebackflag = userPhotoCashPojo.getRebackFlag();
            } else {
                status ="-1";//-1--尚未上传，0--已上传待审核，1-审核成功，2--审核不通过
            }

            //图像是否可修改标记
            flag = getFlagValue(bean);
            String desc = "";
            if("2".equals(status) && !"1".equals(rebackflag)){
                desc = "未通过审核的原因可能是头像图片中出现了带有广告、色情、暴力、赌博等性质的logo或者水印，请检查后重新上传";
            }
            if("2".equals(status) && "1".equals(rebackflag)){
                status = "3"; //审核未通过且已通知用户，状态设置为3
            }
            if("-1".equals(flag)){
                desc = bean.getBusiErrDesc();
                bean.setBusiErrDesc("");//取值后置空
            }
            Acct_UserPojo acct_userPojo = acct_userMapper.queryIpointAndUserPhoto(bean.getUid());
            if (acct_userPojo != null) {
                bean.setUserImgPath(acct_userPojo.getUserImg());
                ipoint = String.valueOf(acct_userPojo.getUserpoint());
                igradeid = acct_userPojo.getGradeid();
            }
            if(StringUtil.isEmpty(bean.getUserImgPath()) || "0".equals(status)){
                if (userPhotoCashPojo != null) {
                    if("0".equals(status)){
                        bean.setUserImgPath(UserConstants.MOBILE_URL+userPhotoCashPojo.getUserImg());
                    }
                    //审核失败 && 用户尚未收到通知
                    if("2".equals(status) && !"1".equals(rebackflag)){
                        bean.setUserImgPath(UserConstants.MOBILE_URL+userPhotoCashPojo.getUserImg());
                    }
                }
            }
            String newUserPhoto = "";
            cuserphoto = bean.getUserImgPath();
            // 相对路径时使用
            if(!StringUtil.isEmpty(cuserphoto) && !cuserphoto.startsWith("http://")){
                newUserPhoto = cuserphoto;
            }
            //绝对地址使用时
            if(cuserphoto!=null && cuserphoto.startsWith("http://")){
                newUserPhoto = cuserphoto.substring(22);
            }
            UserPersonalInfoDTO userPersonalInfoDTO = new UserPersonalInfoDTO();
            userPersonalInfoDTO.setStatus(status);
            userPersonalInfoDTO.setUserpoint(ipoint);
            userPersonalInfoDTO.setGrade(igradeid);
            userPersonalInfoDTO.setFlag(flag);
            userPersonalInfoDTO.setUserImg(newUserPhoto);
            userPersonalInfoDTO.setDesc(desc);

            userPersonalInfoResq.setCode("0");
            userPersonalInfoResq.setDesc("查询成功");
            userPersonalInfoResq.setData(userPersonalInfoDTO);

        }catch(Exception e){
            log.error("用户个人中心数据 ",e);
            userPersonalInfoResq.setCode("-1");
            userPersonalInfoResq.setDesc("查询失败");
        }
        return userPersonalInfoResq;
    }
    @Override
    public String getFlagValue(UserBean bean) {
        String flag = "0";
        String cnickid = bean.getUid();
        int recordStatus0 = userPhotoCashMapper.getPhotoCash0Num(cnickid);
        if(recordStatus0 > 0){
            bean.setBusiErrDesc("图像正在审核中，不允许修改");
            flag = "-1";
        }
        String currentDate = DateTimeUtil.getCurrentDate();
        String beginDate = DateTimeUtil.getBeforeXDayTime(currentDate, 15);//当前15天
        int recordStatus1 = userPhotoCashMapper.getPhotoCash1Num(cnickid,beginDate);
        if(recordStatus1 > 0){
            bean.setBusiErrDesc("十五日之内仅可修改一次头像");
            flag = "-1";
        }
        //图像有修改记录，且修改时间在15天以内  && 审核不通过已经3次
        int confNum = userPhotoCashMapper.getBefore15PhotoCashNum(cnickid, beginDate);
        if(confNum >= 3){
            //计算时间差，向上取整
            String res = userPhotoCashMapper.getPhotoInfo(cnickid, beginDate);
            int days = 15;
            if (res != null) {
                days = 15 - DateTimeUtil.getDateInterval(DateTimeUtil.getCurrentDateTime(), res);
            }
            bean.setBusiErrDesc("三次审核未通过，修改头像功能暂不可用，请于"+days+"工作日之后再进行尝试");
            flag = "-1";
        }
        return flag;
    }

    /**
     * 提交银行卡号修改申请前，查看是否有提交资格
     * @param bean
     * @return 是否有提交资格
     * @throws Exception
     */
    @Override
    public boolean checkBeforeSubmit(BaseBean bean) throws Exception {
        // 查询用户是否有待审核的申请
        int applyPending = userBankbindingMapper.getAppllyNumByNickidAndState0(bean.getUid());
        if(applyPending > 0) {
            log.info("已经有待审核的申请，不能提交新申请:[nickid:{}]", bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHECKAPPLYELIGIBLE_APPLY_DENDING));
            bean.setBusiErrDesc("您有待审核的申请");
            return false;
        }

        // 申请未通过的一天只能只能3次【即每天不能超过三次被审核驳回】
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String stime = format.format(new Date()).concat(" 00:00:00");
        String etime = format.format(new Date()).concat(" 23:59:59");
        int apply_of_day = userBankbindingMapper.getApplyNumByNickidByNickidAndState2(bean.getUid(), stime, etime);
        if (apply_of_day >= 3) {
            log.info("今天已经申请过3次，不能提交新申请:[nickid:{}]", bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHECKAPPLYELIGIBLE_APPLY_MORETHEN3_INDAY));
            bean.setBusiErrDesc("每天最多只能申请3次");
            return false;
        }

        // 每15天只能提交一次申请【即每15天只能有一条待审核和已审核通过的变更申请】.
        int apply_of_month = userBankbindingMapper.getApplyNumByNickidAndIn15Days(bean.getUid());
        if (apply_of_month > 0) {
            log.info("15天内已经申请过，不能提交新申请[nickid:{}]", bean.getUid());
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHECKAPPLYELIGIBLE_APPLY_IN15DAYS));
            bean.setBusiErrDesc("每15天只能修改1次");
            return false;
        }
        return true;
    }

    /**
     * 检查等级
     * @author wang tao
     * @param bean
     */
    @Override
    public void check_level(BaseBean bean) {
        log.info("check_level_uid:"+bean.getUid()+" bean.getBusiErrCode()="+bean.getBusiErrCode() +" bean.getLogintype()="
                +bean.getLogintype()+" bean.getAppid()="+bean.getAppid());
        if (bean.getBusiErrCode() == 0){
            String level = "";
            if (bean.getLogintype() == 1) {
                log.info("check_level_uid1:"+bean.getUid());
                CacheBean cacheBean=new CacheBean();
                cacheBean.setKey(bean.getAppid());
                TokenBean tokenBean= (TokenBean) redisClient.getObject(cacheBean, TokenBean.class,log, SysCodeConstant.USERCENTER);
                if(tokenBean!=null){
                    String param = tokenBean.getParamJson();
                    log.info("check_level_uid2:"+bean.getUid() +" param:"+param);
                    if (!StringUtil.isEmpty(param)) {
                        JSONObject  jsObj = JSONObject.parseObject(param);
                        Object object = jsObj.get(BaseConstant.VLEVEL);
                        if (object != null) {
                            level = object.toString();
                        } else {
                            log.info("check_level token 未检测到用户类型");
                        }
                    }
                }
            }
            //缓存中查询不到vip等级数据时从数据库查询,并更新到缓存中
            if(StringUtil.isEmpty(level)){
                log.info("缓存中查询不到vip等级,从数据库查询,用户名=" + bean.getUid());
                UserPojo user=queryUserVipAndWhitelistLevel(bean);
                if(user!=null){
                    level=user.getIsvip()+"";
                    tokenManageService.updateToken(level,user.getState()+"",bean);
                }
            }
            log.info("check_level_uid3:"+bean.getUid() +" level:"+level);
            if ("3".equals(level) || "4".equals(level) || "5".equals(level) || "6".equals(level)){
                bean.setBusiErrCode(0);
                return;
            }
        }
        bean.setBusiErrCode(-1);
        bean.setBusiErrDesc("获取信息失败,请重新登录");
    }

    //查询用户vip级别和白名单等级.
    private UserPojo queryUserVipAndWhitelistLevel(BaseBean bean){
        log.info("查询用户vip级别和白名单等级,用户名=" + bean.getUid());
        UserPojo user=agent_userMapper.queryUserVipAndWhitelistLevel(bean.getUid());
        if(user!=null){
            return user;
        }else{
            log.error("查询用户vip级别和白名单等级错误,uid:{}",bean.getUid());
            return null;
        }
    }
    /**
     * 查看身份绑定信息
     * @author  wang tao
     * @param bean
     * @throws Exception
     */
    @Override
    public IdcardBindingDTO queryUserInfoBind(BaseBean bean){
        IdcardBindingDTO dto= null;
        try {
            dto = new IdcardBindingDTO();
            UserPojo user=userMapper.queryUserIdcardBinding(bean.getUid());
            int isBindIdCard = 0;
            int isBindMobile = 0;
            if(user!=null){
                String idcard=user.getIdcard();
                int mobbind=user.getMobbindFlag();
                if(!StringUtil.isEmpty(idcard)){
                    isBindIdCard = 1;
                }
                bean.setBusiErrDesc("查询成功");
                isBindMobile = mobbind;
            }else{
                bean.setBusiErrCode(-1001);//暂定
                bean.setBusiErrDesc("无身份证绑定信息");
            }
            dto.setIsBindIdCard(isBindIdCard);
            dto.setIsBindMobile(isBindMobile);
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_YYDBBINDING_QUERY_ERROR));//暂定
            bean.setBusiErrDesc("查询身份绑定信息异常");
            log.error("查询身份绑定信息异常,用户名：{}",bean.getUid(),e);
        }
        return dto;
    }
}
