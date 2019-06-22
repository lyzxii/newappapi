package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.UserBean;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import com.caiyi.lottery.tradesystem.usercenter.service.UserRecordService;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import dto.FeedBackDTO;
import dto.OperationReCordDTO;
import dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.CalculateNeterrorPojo;
import pojo.UserLogPojo;
import pojo.UserpingNeterrorPojo;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用户产品相关记录
 *
 * @author GJ
 * @create 2017-12-01 15:07
 **/
@Service
public class UserRecordServiceImpl implements UserRecordService {
    private Logger logger = LoggerFactory.getLogger(UserRecordServiceImpl.class);

    @Autowired
    private OperationRecordMapper operationRecordMapper;

    @Autowired
    private UserLogMapper userLogMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserpingNeterrorMapper userpingNeterrorMapper;
    @Autowired
    private UserNeterrorMapper userNeterrorMapper;
    @Autowired
    private UserBreakdownMapper userBreakdownMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public void addUserOperLog(BaseBean bean, String type, String memo){
        if(StringUtil.isEmpty(bean.getUid())){
            return;
        }
        UserLogPojo userLogPojo = new UserLogPojo();
        userLogPojo.setCnickid(bean.getUid());
        userLogPojo.setCipaddr(bean.getIpAddr());
        userLogPojo.setCmemo(memo);
        userLogPojo.setCtype(type);
        logger.info("1="+bean.getUid()+" 2="+bean.getIpAddr()+" 3="+memo+" 4="+type);
        userLogMapper.insertIntoUserLog(userLogPojo);

        //只用用户登录成功，才能够触发将流失用户升级为VIP的任务
        if (("用户登录".equalsIgnoreCase(type) || "token用户登录".equalsIgnoreCase(type))
                &&"[成功]".equalsIgnoreCase(memo)) {
            //判断用户是否为流失用户并且为第一次登录，如果登录的话，设置变量弹出提示窗口
            int record = userMapper.queryPublicTaskCount(bean.getUid());
            if(record > 0){
                //说明弹窗任务完成的iflag 修改成 0，标志任务准备执行
                userMapper.updatePublicTask(bean.getUid());
                //修改变量值
                bean.setHasVip("isVIP");
            }
        }

    }

    //*********************************************XQH service START****************************************************

    /**
     * 保存用户反馈记录
     * @param bean
     * @return
     */
    @Override
    public int addProductFeedBack(UserBean bean) {
        //查询用户绑定信息
        String banging = queryUserBanging(bean);
        //用户白名单等级
        int whitelistGrade = bean.getWhitelistGrade();
        FeedBackDTO feedBackDTO  = new FeedBackDTO();
        feedBackDTO.setCid(UUID.randomUUID().toString());
        feedBackDTO.setCbanginginfo(banging);
        feedBackDTO.setCclientname(bean.getClientName());
        feedBackDTO.setCclientvarsion(bean.getAppversion());
        feedBackDTO.setCcontactway(bean.getMobileNo());
        feedBackDTO.setCfeedbackcontent(bean.getFeedContent());
        feedBackDTO.setCfeedbackpicone(bean.getPicone());
        feedBackDTO.setCfeedbackpictwo(bean.getPictwo());
        feedBackDTO.setCfeedbackpicthree(bean.getPicthree());
        feedBackDTO.setCip(bean.getComeFrom());
        feedBackDTO.setIwhitegrade(whitelistGrade);
        feedBackDTO.setCsource(String.valueOf(bean.getSource()));
        feedBackDTO.setCposition(bean.getCityid());
        feedBackDTO.setCphonesys(bean.getPhoneSys());
        feedBackDTO.setCphonemodel(bean.getPhoneModel());
        feedBackDTO.setClogintype(Integer.valueOf(bean.getNewValue()));
        feedBackDTO.setCnetwork(bean.getNetWork());
        feedBackDTO.setCnickid(bean.getUid());
        return operationRecordMapper.addProductFeedBack(feedBackDTO);
    }

    /**
     * 产品操作记录
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int productOperationInfo(UserBean bean) {
        logger.info("产品操作记录信息，uid==" + bean.getUid());
        try {
            //查询用户绑定信息
            String banging = queryUserBanging(bean);
            //用户白名单等级
            int whitelistGrade = bean.getWhitelistGrade();
            String cid = UUID.randomUUID().toString();
            String cnickid = bean.getUid();
            //登陆方式
            String logintype = bean.getNewValue();
            //客户端名称
            String clientName = bean.getClientName();
            String appversion = bean.getAppversion();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date());
            StringBuilder builder = new StringBuilder();
            builder.append(cnickid+"_").append(banging+"_").append(logintype+"_").append(bean.getSource()+"_").append(appversion+"_").append(bean.getPhoneModel()+"_").append(bean.getPhoneSys()+"_").append(bean.getNetWork()+"_").append(bean.getIpAddr());
            CacheBean cacheBean = new CacheBean();
            cacheBean.setKey(date + "_" + cnickid + "_" + "operationRecord");
            String res = redisClient.getString(cacheBean, logger, SysCodeConstant.USERCENTER);
            if(res==null){
                cacheBean.setValue(builder.toString());
                cacheBean.setTime(Constants.TIME_DAY);
                redisClient.setString(cacheBean, logger, SysCodeConstant.USERCENTER);
            }else{
                String record = res;
                if(record.equals(builder.toString())){
                    logger.info("用户操作记录已经存储过类似数据，now:"+builder.toString()+" cache:"+record+" cacheKey:"+date+"_"+cnickid+"_"+"operationRecord");
                    return 0;
                }
            }
            OperationReCordDTO operationReCordDTO =  new OperationReCordDTO();
            operationReCordDTO.setCid(cid);
            operationReCordDTO.setCnickid(bean.getUid());
            operationReCordDTO.setIwhitegrade(bean.getWhitelistGrade());
            operationReCordDTO.setCbanginginfo(banging);
            operationReCordDTO.setClogintype(logintype);
            operationReCordDTO.setCclientname(clientName);
            operationReCordDTO.setCsource(String.valueOf(bean.getSource()));
            operationReCordDTO.setCclientvarsion(appversion);
            operationReCordDTO.setCphonemodel(bean.getPhoneModel());
            operationReCordDTO.setCphonesys(bean.getPhoneSys());
            operationReCordDTO.setCnetwork(bean.getNetWork());
            operationReCordDTO.setCip(bean.getComeFrom());
            operationReCordDTO.setCposition(bean.getCityid());
            int rec = operationRecordMapper.addUserOperationRecord(operationReCordDTO);
            if(rec == 1){
                logger.info("产品操作记录信息成功  uid=="+cnickid + " cid==" + cid);
                bean.setBusiErrDesc("产品操作记录信息成功");
            }else{
                logger.info("产品操作记录信息失败  uid=="+cnickid + " cid==" + cid);
                bean.setBusiErrDesc("产品操作记录信息失败");
                return  -1;
            }
        } catch (Exception e) {
            logger.info("记录产品操作信息出错  ",e);
            bean.setBusiErrDesc("记录产品操作信息出错");
            return  -1;
        }
        return 0;
    }

    private String queryUserBanging(UserBean bean) {
        Map<String, String> userInfo = queryMobilenoIdcard(bean);
        return bindInfoCheck(bean, userInfo);
    }

    /**
     * 根据用户昵称查询绑定手机号和身份证号.
     */
    public Map<String, String> queryMobilenoIdcard(UserBean bean){
        Map<String, String> map = new HashMap<String, String>();
        try{
            List<UserInfoDTO> userInfoDTOS = userMapper.queryUserBankinfoByNickid(bean.getUid());

            if (userInfoDTOS != null && userInfoDTOS.size() > 0) {
                UserInfoDTO userInfoDTO = userInfoDTOS.get(0);
                map.put("mobbind", String.valueOf(userInfoDTO.getMobileBindFlag()));
                map.put("idcard", userInfoDTO.getIdcardNo());
                map.put("mobileno", userInfoDTO.getMobileNo());
                map.put("bankcard", userInfoDTO.getBankcardNo());
                map.put("bankcode", userInfoDTO.getBankCode());
                map.put("bankpro", userInfoDTO.getBankProvince());
                map.put("bankcity", userInfoDTO.getBankCity());
                bean.setWhitelistGrade(userInfoDTO.getIopen());
            }
        }catch(Exception e){
            logger.info("查询用户绑定信息出错",e);
        }
        return map;
    }

    /**
     * 用户手机号和身份证绑定检测.
     */
    private String bindInfoCheck(UserBean bean, Map<String, String> userInfo) {
        String result = "";
        int bindflag = 0;
        if (StringUtil.isEmpty(userInfo.get("idcard"))) {
            bindflag += 1;
        }
        if (StringUtil.isEmpty(userInfo.get("mobileno")) || (userInfo.get("mobbind").equals("0"))) {
            bindflag += 2;
        }
        if (StringUtil.isEmpty(userInfo.get("bankcard"))) {
            bindflag += 4;
        }
        switch (bindflag) {
            case 1 : {
//            	bean.setBusiErrCode(101);
//                bean.setBusiErrDesc("身份证未绑定");
                result = "101";
                logger.info("未实名认证uid=" + bean.getUid());
                break;
            }
            case 2 : {
//            	bean.setBusiErrCode(102);
//                bean.setBusiErrDesc("手机号未绑定");
                result = "102";
                logger.info("未绑定手机号uid=" + bean.getUid());
                break;
            }
            case 3 : {
//            	bean.setBusiErrCode(103);
//                bean.setBusiErrDesc("手机号和身份证未绑定");
                result = "103";
                logger.info("未绑定手机号和实名认证uid=" + bean.getUid());
                break;
            }
            case 4 : {
//            	bean.setBusiErrCode(104);
//                bean.setBusiErrDesc("银行卡未绑定");
                result = "104";
                logger.info("未绑定银行卡uid=" + bean.getUid());
                break;
            }
            case 5 : {
//            	bean.setBusiErrCode(105);
//                bean.setBusiErrDesc("身份证和银行卡未绑定");
                result = "105";
                logger.info("未绑定身份证和银行卡uid=" + bean.getUid());
                break;
            }
            case 6 : {
//            	bean.setBusiErrCode(106);
//                bean.setBusiErrDesc("手机号和银行卡未绑定");
                result = "106";
                logger.info("未绑定手机号和实名认证uid=" + bean.getUid());
                break;
            }
            case 7 : {
//            	bean.setBusiErrCode(107);
//                bean.setBusiErrDesc("身份证、手机号、银行卡均未绑定");
                result = "107";
                logger.info("身份证、手机号、银行卡均未绑定uid=" + bean.getUid());
                break;
            }
            default : {
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("手机号和身份证都已绑定");
                result = "0";
            }
        }
        return result;
    }
    //*********************************************XQH service END****************************************************


    //*********************************************TTK service START****************************************************

    /**
     * @Author: tiankun
     * @Description: 用户检测网络统计错误信息
     * @Date: 20:43 2017/12/6
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response calcUserpingNeterror(UserBean bean,Response resp) throws Exception {
        logger.info("用户检测统计网络错误信息，信息:"+bean.getUserInputs());
        String userNetErrorData = bean.getUserInputs();
        JSONArray userNetError =  JSONArray.parseArray(userNetErrorData);
        for (int i = 0; i < userNetError.size();i++){
            JSONObject mainData = userNetError.getJSONObject(i);
            String errorDesc = mainData.getString("errorDesc")==null ? "" : mainData.getString("errorDesc");
            if(errorDesc.length()>2000){
                errorDesc=errorDesc.substring(0, 2000);
            }
            String netType = mainData.getString("netType");
            String operatorType = mainData.getString("operatorType");
            String osVersion = mainData.getString("osVersion");
            String dnsResolution = mainData.getString("dnsResolution")==null ? "" : mainData.getString("dnsResolution");
            if(dnsResolution.length()>2000){
                dnsResolution=dnsResolution.substring(0, 2000);
            }
            String userName = mainData.getString("userName");
            String errorTime = mainData.getString("errorTime");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String errorTimeStr = "";
            try {
                Date date = sdf.parse(errorTime);
                errorTimeStr = sdf.format(date);
            } catch (ParseException e) {
                logger.error("时间格式不符合,时间格式:" + errorTime + "",e);
                errorTimeStr = sdf.format(new Date());
            }
            errorTime = errorTimeStr;
            String appName = mainData.getString("appName");
            String appPlatform = mainData.getString("appPlatform");
            String appVersion = mainData.getString("appVersion");
            String localIP = mainData.getString("localIP");
            String localDNS = mainData.getString("localDNS");

            String  pingResult =mainData.getString("pingResult");
            pingResult = pingResult.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            pingResult = URLDecoder.decode(pingResult, "UTF-8");
            UserpingNeterrorPojo unp = new UserpingNeterrorPojo();
            unp.setAppname(appName);
            unp.setAppplatform(appPlatform);
            unp.setAppversion(appVersion);
            unp.setDnsresolution(dnsResolution);
            unp.setErrordesc(errorDesc);
            unp.setErrortime(errorTime);
            unp.setLocaldns(localDNS);
            unp.setLocalip(localIP);
            unp.setNettype(netType);
            unp.setOperatortype(operatorType);
            unp.setOsversion(osVersion);
            unp.setPingresult(pingResult);
            unp.setUsername(userName);
            int insert = userpingNeterrorMapper.insertUserImei(unp);
            if (insert == 1) {
                logger.info("插入用户检测网络统计执行成功");
            }else {
                logger.info("插入用户检测网络统计信息更新数据库失败");
                resp.setCode(String.valueOf(BusiCode.FAIL));
                resp.setDesc("插入用户检测网络统计信息更新数据库失败");
                return resp;
            }
        }
        resp.setCode(String.valueOf(BusiCode.SUCCESS));
        resp.setDesc("存储用户检测网络信息成功");
        return resp;
    }

     /**
       * @Author: tiankun
       * @Description: 统计网络错误信息
       * @Date: 10:05 2017/12/7
       */
     @Override
    @Transactional(rollbackFor = Exception.class)
    public Response calculateNeterror(UserBean bean,Response resp) throws Exception {
        logger.info("进入统计网络错误程序，信息:"+bean.getUserInputs());
        String userNetErrorData = bean.getUserInputs();
        JSONArray userNetError =  JSONArray.parseArray(userNetErrorData);
        for (int i = 0; i < userNetError.size();i++) {
            JSONObject errorData = userNetError.getJSONObject(i);
            //获取错误信息的key
            //Iterator<String> it = errorData.keys();
            Set<String> keySet = errorData.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String ip = it.next();
                JSONObject mainData = errorData.getJSONObject(ip);
                String userName = mainData.getString("userName");
                String errorTime = mainData.getString("errorTime");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String errorTimeStr = "";
                try {
                    Date date = sdf.parse(errorTime);
                    errorTimeStr = sdf.format(date);
                } catch (ParseException e) {
                    logger.info("时间格式不符合,时间格式:"+errorTime+"");
                    errorTimeStr = sdf.format(new Date());
                }
                errorTime = errorTimeStr;
                String appName = mainData.getString("appName");
                String appPlatform = mainData.getString("appPlatform");
                String appVersion = mainData.getString("appVersion");
                String source = mainData.getString("source");
                //增加显示网络错误详细信息字段
                String localDNS = mainData.getString("localDNS")==null ? "" : mainData.getString("localDNS");
                String pingResult = mainData.getString("pingResult")==null ? "" : mainData.getString("pingResult");
                pingResult = pingResult.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                pingResult=URLDecoder.decode(pingResult, "UTF-8");
                if(pingResult.length()>1000){
                    pingResult=pingResult.substring(0, 1000);
                }
                String dnsResolution = mainData.getString("dnsResolution")==null ? "" : mainData.getString("dnsResolution");
                if(dnsResolution.length()>100){
                    dnsResolution=dnsResolution.substring(0, 100);
                }
                String localIP = mainData.getString("localIP")==null ? "" : mainData.getString("localIP");
                String responContent = mainData.getString("responContent")==null ? "" : mainData.getString("responContent");
                responContent = responContent.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                responContent=URLDecoder.decode(responContent, "UTF-8");
                if(responContent.length()>2000){
                    responContent=responContent.substring(0, 2000);
                }
                String ipAddress = mainData.getString("ipAddress");
                String ipAddressDetail = "";
//					String ipAddressDetail = getIpDetail("http://www.ip138.com/ips138.asp", "ip="+ipAddress+"&action=2");
                String city = mainData.getString("city");
                String orignalDomain = mainData.getString("orignalDomain");
                String switchDomain = mainData.getString("switchDomain");
                String netType = "";
                if(mainData.getString("netType") != null){
                    netType = mainData.getString("netType");
                }
                String operatorType = "";
                if(mainData.getString("operatorType") != null){
                    operatorType = mainData.getString("operatorType");
                }
                String osVersion = "";
                if(mainData.getString("osVersion") != null){
                    osVersion = mainData.getString("osVersion");
                }
                String errorUrl = mainData.getString("errorUrl") == null ? "" : mainData.getString("errorUrl");
                if(errorUrl.length()>300){
                    errorUrl=errorUrl.substring(0, 300);
                }
                String errorDesc = mainData.getString("errorDesc") == null?"":mainData.getString("errorDesc");
                if(errorDesc.length()>2000){
                    errorDesc=errorDesc.substring(0, 2000);
                }
                String urlType = "0";
                //如果查询的url类型为空，则默认为数据查询url"0"
                if(mainData.getString("urlType") != null){
                    urlType = mainData.getString("urlType");
                }
                //更新数据库
                CalculateNeterrorPojo pojo = new CalculateNeterrorPojo();
                pojo.setCappname(appName);
                pojo.setCappplatform(appPlatform);
                pojo.setCappversion(appVersion);
                pojo.setCcity(city);
                pojo.setCerrordesc(errorDesc);
                pojo.setCerrortime(errorTime);
                pojo.setCerrorurl(errorUrl);
                pojo.setCipaddress(ipAddress);
                pojo.setCnickid(userName);
                pojo.setCoriginaldomain(orignalDomain);
                pojo.setCsource(source);
                pojo.setCurltype(urlType);
                pojo.setCswitchdomain(switchDomain);
                pojo.setIpdetail(ipAddressDetail);
                pojo.setDnsresolution(dnsResolution);
                pojo.setLocaldns(localDNS);
                pojo.setLocalip(localIP);
                pojo.setNettype(netType);
                pojo.setOperatortype(operatorType);
                pojo.setOsversion(osVersion);
                pojo.setPingresult(pingResult);
                pojo.setResponcontent(responContent);
                pojo.setUploaddomain(bean.getNewValue());
                int count = userNeterrorMapper.insertUserNeterror(pojo);
                if (count == 1) {
                    logger.info("插入网络异常信息执行成功");
                }else {
                    logger.info("插入网络异常信息执行失败,数据库异常");
                    resp.setCode(String.valueOf(BusiCode.FAIL));
                    resp.setDesc("插入网络异常信息执行失败");
                    return resp;
                }
            }
        }
        resp.setCode(String.valueOf(BusiCode.SUCCESS));
        resp.setDesc("存储用户网络异常信息成功");
        return resp;
    }


    //*********************************************TTK service END****************************************************

    /**
     * @Author: lichuang
     * @Description: 统计APP崩溃信息
     * @Date: 20:43 2017/12/8
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int calculateBreakdownError(JSONObject errorData){
        //获取错误信息的key
        String userName = errorData.getString("userName");
        String errorTime = errorData.getString("errorTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String errorTimeStr = "";
        try {
            Date date = sdf.parse(errorTime);
            errorTimeStr = sdf.format(date);
        } catch (ParseException e) {
            logger.info("时间格式不符合,时间格式:"+errorTime+"");
            errorTimeStr = sdf.format(new Date());
        }
        errorTime = errorTimeStr;
        String appName = errorData.getString("appName");
        String appPlatform = errorData.getString("appPlatform");
        String appVersion = errorData.getString("appVersion");
        String osVersion = errorData.getString("osVersion");
        String mobileType = errorData.getString("mobileType");
        String source = errorData.getString("source");
        String errorDesc = errorData.getString("errorDesc");
        //int update = jcn.executeUpdate(sql, new Object[]{userName,errorTime,appName,appPlatform,appVersion,source,osVersion,mobileType,errorDesc});
        int count = userBreakdownMapper.addUserBreakdownInfo(userName,errorTime,appName,appPlatform,appVersion,source,osVersion,mobileType,errorDesc);
        //String sql = "INSERT INTO TB_USER_BREAKDOWN (ID, CNICKID, CERRORTIME, CAPPNAME, CAPPPLATFORM,CAPPVERSION,CSOURCE,COSVERSION,CMOBILETYPE,CERRORDESC) "
        //		+ "VALUES(seq_user_neterror.NEXTVAL,?,to_date(?,'yyyy-MM-dd hh24:mi:ss'),?,?,?,?,?,?,?)";

        return count;
    }

    /**
     * @Author: lichuang
     * @Description: 查询用户是否存在
     * @Date: 20:43 2017/12/8
     */
    @Override
    public String checkUserExist(String cnickid){
        String result = userMapper.checkUserExist(cnickid);
        return  result;
    }

}
