package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.SafeBean;
import bean.SourceConstant;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.usercenter.dao.BankCardMapMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.UserImeiMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.UserLogMapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.UserMapper;
import com.caiyi.lottery.tradesystem.usercenter.mq.Producers;
import com.caiyi.lottery.tradesystem.usercenter.service.ModifyUserInfoService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserCenterService;
import com.caiyi.lottery.tradesystem.usercenter.util.UserBeanCheck;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.google.common.collect.Maps;
import constant.UserConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.UserImeiPojo;
import pojo.UserLogPojo;
import pojo.UserPojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 更新用户信息Service
 *
 * @author GJ
 * @create 2017-12-04 20:29
 **/
@Service
public class ModifyUserInfoServiceImpl implements ModifyUserInfoService {
    private Logger logger = LoggerFactory.getLogger(ModifyUserInfoServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserImeiMapper userImeiMapper;
    @Autowired
    private BankCardMapMapper bankCardMapMapper;
    @Autowired
    private UserLogMapper userLogMapper;
    @Autowired
    private SafeCenterInterface safeCenterInterface;

    @Autowired
	private UserCenterService userCenterService;
    @Autowired
    private Producers producers;
    
    @Override
    public int openMobilenoLogin(UserBean bean) throws Exception {
    	//判断是否手机号用作用户名
        int num = userMapper.queryMobilenoLoginCount(bean.getMobileNo());
        if (num < 1) {
            num = userMapper.updateMobilenoLogin(bean.getUid());
            if (1 != num) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_AUTOSTART_FAIL));
                bean.setBusiErrDesc("自动开启手机号登录失败");
            }
        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_OCCUPY));
            bean.setBusiErrDesc("已被用作账户名的手机号不能开启手机号登录");
        }
        return num;
    }

    @Override
    public void saveimei(BaseBean bean) {
        try {
            if(StringUtil.isEmpty(bean.getImei()) || StringUtil.isEmpty(bean.getUid())){
                return;
            }else {
                int count = userImeiMapper.queryUserImeiCount(bean.getUid());
                if(count>0){
                    return;
                }else{
                    UserImeiPojo userImeiPojo = new UserImeiPojo();
                    userImeiPojo.setCnickid(bean.getUid());
                    userImeiPojo.setCimei(bean.getImei());
                    userImeiPojo.setIsource(bean.getSource());
                    userImeiPojo.setCagentid(bean.getComeFrom());
                    int result = userImeiMapper.insertUserImei(userImeiPojo);
                    if (result != 1) {
                        logger.info("保存手机imei失败,imei:"+bean.getImei()+" 用户名:"+bean.getUid());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("保存手机号imei出错,用户名:"+bean.getUid(), e);
        }
    }

    /**
     * 绑定用户提款银行卡
     * @throws Exception 
     */
	@Override
	@Transactional(rollbackFor = {Exception.class})
	public void bindUserBankCard(UserBean bean) throws Exception {
		try {
			verifyBankCardInfo(bean);
			checkSubBankName(bean);
			if(bean.getBusiErrCode()!=0){
				return;
			}
			updateBankCardInfo(bean);
		} catch (Exception e) {
			logger.error("更新用户提款银行卡失败,errorCode:"+bean.getBusiErrCode()+" errorDesc:"+bean.getBusiErrDesc()+" 用户名:"+bean.getUid(), e);
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_UPDATE_DRAWCARD_ERROR));
			bean.setBusiErrDesc("绑定银行卡信息是失败");
			throw new Exception("绑定银行卡失败");
		}
	}

	//检测用户支行信息
	private void checkSubBankName(UserBean bean) {
		if (StringUtil.isEmpty(bean.getBankName()) || "(null)".equals(bean.getBankName())) {
			bean.setBankName("");
		}
		String bankName = bean.getBankName().replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "");
		bean.setBankName(bankName);
		String drawBankCode = bean.getDrawBankCode();
		if(!(("1".equals(drawBankCode))||"2".equals(drawBankCode)||"13".equals(drawBankCode)||"3".equals(drawBankCode)||"6".equals(drawBankCode))){
			if(StringUtil.isEmpty(bean.getBankName())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_SUBBANK_ERROR));
				bean.setBusiErrDesc("请选择您的支行信息");
				logger.info("未选择支行信息,用户名:"+bean.getUid()+" 提款银行code:"+bean.getDrawBankCode());
				return;
			}
		}
	}

	/**
	 * 更新用户提款银行卡信息
	 * @param bean
	 * @throws Exception 
	 */
	private void updateBankCardInfo(UserBean bean) throws Exception {
		logger.info("用户修改银行卡信息 用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawCode:"+bean.getDrawBankCode()+
				" flag:"+bean.getFlag()+" provid:"+bean.getProvid()+" cityid:"+bean.getCityid());
		if(!StringUtil.isEmpty(bean.getMobileNo())){
			bean.setMd5Mobile(MD5Helper.md5Hex(bean.getMobileNo()));
		}
		if(!StringUtil.isEmpty(bean.getBankCard())){
			bean.setMd5BankCard(MD5Helper.md5Hex(bean.getBankCard()));
		}
		if(1==bean.getFlag()){
			UserBeanCheck.check(UserBeanCheck.UPDATE_BANK, bean);
			if(bean.getBusiErrCode()!=0){
				return;
			}
			SafeBean orginSafeBean=null;
		 	SafeBean safeBean = new SafeBean();
		 	safeBean.setNickid(bean.getUid());
			safeBean.setUsersource(SourceConstant.CAIPIAO);
			BaseReq<SafeBean> req1 = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
			BaseResp<SafeBean> res = safeCenterInterface.getUserTable(req1);
			if (res == null||BusiCode.FAIL.equals(res.getCode())|| res.getData() == null) {
				bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_ADD_SAFEINFO_ERROR));
				bean.setBusiErrDesc("查询用户信息用户基本信息出错");
				throw new Exception("更新提款银行卡出错");
			}else if (BusiCode.NOT_EXIST.equals(res.getCode())) {
			}else {
				//查询有数据时，记录下来，以便后面回滚
				orginSafeBean = res.getData();
				orginSafeBean.setNickid(bean.getUid());
			}
		 	safeBean.setBankcard(bean.getBankCard());
		 	safeBean.setCardmobile(bean.getMobileNo());
		 	BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.USERCENTER);
		 	BaseResp<SafeBean> resp = safeCenterInterface.addUserTable(req);
		 	if(!"0".equals(resp.getCode())||resp.getData()==null){
		 		bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_ADD_SAFEINFO_ERROR));
		 		bean.setBusiErrDesc("添加用户基本信息出错");
		 		logger.info("添加用户提款银行卡至用户安全中心信息出错,用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawCode:"+bean.getDrawBankCode());
		 		throw new Exception("添加银行卡至安全中心出错");
		 	}
		 	UserBean encryptBean = new UserBean();
		 	BeanUtilWrapper.copyPropertiesIgnoreNull(bean, encryptBean);
		 	if(!StringUtil.isEmpty(encryptBean.getMobileNo())){
		 		String mobile = encryptBean.getMobileNo();
		 		encryptBean.setMobileNo(mobile.substring(0, 3)+"****"+mobile.substring(mobile.length()-4));
		 	}
		 	int ret = userMapper.updateUserDrawBankCard(encryptBean);
			if(ret!=1){
				bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_UPDATE_DRAWCARD_ERROR));
				bean.setBusiErrDesc("绑定银行卡失败，请重试");
				logger.info("用户绑定提款银行卡失败,用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawBankCode:"+bean.getDrawBankCode()+
						" flag:"+bean.getFlag()+" provid:"+bean.getProvid()+" cityid:"+bean.getCityid());
				if (orginSafeBean!=null) {
					logger.info("updateBankCardInfo-安全中心调用出错,用户名:{},安全中心进行事务补偿" , bean.getUid());
					transactionalCompensateSafeCenter(orginSafeBean, UserConstants.ROLLBACK_BANKCARDINFO);
				}
				throw new Exception("银行卡更新失败");
			}
		}else{
			int ret = userMapper.modifyUserDrawBankCard(bean);
			if(ret!=1){
				bean.setBusiErrCode(Integer.parseInt(ErrorCode.USER_UPDATE_DRAWCARD_ERROR));
				bean.setBusiErrDesc("修改开户行失败，请重试");
				logger.info("用户修改开户行失败,用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawBankCode:"+bean.getDrawBankCode());
				throw new Exception("银行卡更新失败");
			}
		}
		UserLogPojo logo = new UserLogPojo();
		StringBuilder builder = new StringBuilder();
		builder.append("[成功]");
		builder.append("手机=").append(bean.getMobileNo()).append(";");
		builder.append("银行卡号=").append(bean.getBankCard()).append(";");
		builder.append("银行省份=").append(bean.getProvid()).append(";");
		builder.append("银行城市=").append(bean.getCityid()).append(";");
		builder.append("银行名称=").append(bean.getBankName()).append(";");
		logo.setCnickid(bean.getUid());
		logo.setCmemo(builder.toString());
		logo.setCipaddr(bean.getIpAddr());
		logo.setCtype("更新银行卡和身份证");
		int ret = userLogMapper.insertIntoUserLog(logo);
		if(ret!=1){
			logger.info("添加用户日志失败,用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawBankCode:"+bean.getDrawBankCode());
		}
		bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
		bean.setBusiErrDesc("修改成功");
		logger.info("用户绑定提款银行卡成功,用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawBankCode:"+bean.getDrawBankCode());
	}

	/**
	 * 安全中心回滚
	 * @param bean
	 */
	private void transactionalCompensateSafeCenter(SafeBean bean, String source){
		String target1 = "tb_user_vice";
		String commitOperation1 = "update";
		//插入失败，重新插入
		String rollbackOperation1 = "update";
		Map<String, Object> map1 = Maps.newHashMap();
		map1.put("sysdate", new Date());
		map1.put("usersource", SourceConstant.CAIPIAO);
		map1.put("object", bean);


		RollbackDTO rollbackDTO1 = new RollbackDTO(commitOperation1, rollbackOperation1, target1,source, map1);
		List<RollbackDTO> rollbackDTOList = new ArrayList<>();
		rollbackDTOList.add(rollbackDTO1);

		producers.sendSafeCenterList(rollbackDTOList);
	}

	/**
	 * 验证用户提款银行卡信息
	 * @param bean
	 */
	private void verifyBankCardInfo(UserBean bean) {
		String cardNo = CardMobileUtil.decryptCard(bean.getBankCard());
		bean.setBankCard(cardNo);
		String mobileNo = CardMobileUtil.decryptMobile(bean.getMobileNo());
		bean.setMobileNo(mobileNo);
		logger.info("验证用户的真实手机号和银行卡,cardno:"+bean.getBankCard()+" mobileNo:"+bean.getMobileNo()+" 用户名:"+bean.getUid());
		//表示修改银行卡不需要鉴权
		if(2!=bean.getFlag()){
	    	if(StringUtil.isEmpty(bean.getBankCode())||"NOCHECK".equals(bean.getBankCode())){
	    		String bankCode = bankCardMapMapper.getBankCodeByDrawCode(bean.getDrawBankCode());
	    		if(StringUtil.isEmpty(bankCode)){
	    			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCODE_QUERY_ERROR));
	    			bean.setBusiErrDesc("用户银行卡真实code查询失败");
	    			logger.info("查询用户真实银行卡bankCode失败,用户名:"+bean.getUid()+" drawBankCode:"+bean.getDrawBankCode());
	    			return;
	    		}else{
	    			bean.setBankCode(bankCode);
	    		}
	    	}
	    	if(!CardMobileUtil.checkBankCard(cardNo)){
	    		bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_ERROR));
	    		bean.setBusiErrDesc("银行卡号错误，请检查您的银行卡号是否填写正确");
	    		logger.info("查询用户真实银行卡bankCode失败,用户名:"+bean.getUid()+" drawBankCode:"+bean.getDrawBankCode());
    			return;
	    	}
	    	//银行卡号为空，不进行鉴权
	    	if(StringUtil.isEmpty(bean.getBankCard())){
	    		bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_ERROR));
	    		bean.setBusiErrDesc("银行卡号为空");
	    		logger.info("用户银行卡号为空,用户名:"+bean.getUid()+" 手机号："+bean.getMobileNo()+" 卡号:"+bean.getBankCard());
	    		return;
	    	}
	    	//鉴权开关
	    	if(authenticationSwitch()){
	    		logger.info("鉴权被关闭……该卡不参与鉴权！");
	    		return;
	    	}
	       	//鉴权未关闭的情况检测手机号是否为空
	    	if(StringUtil.isEmpty(bean.getMobileNo())){
	    		bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_MOBILE_ERROR));
	    		bean.setBusiErrDesc("手机号为空");
	    		logger.info("用户手机号为空,用户名:"+bean.getUid()+" 手机号："+bean.getMobileNo()+" 卡号:"+bean.getBankCard());
	    		return;
	    	}
	    	bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_BANKCARD_AUTH_ERROR));
	    	bean.setBusiErrDesc("暂时没有可用的鉴权渠道");
	    	logger.info("暂时没有可鉴权渠道 用户名:"+bean.getUid()+" bankCard:"+bean.getBankCard()+" drawBankCard:"+bean.getBankCard());
	    	return;
		}
	}
	
    /**
	 * 鉴权开关
	 * @return
	 */
	public boolean authenticationSwitch(){
		JXmlWrapper xml = null;
    	boolean result = false;
		try {
			xml = JXmlWrapper.parse(new File(FileConstant.AUTH_SWITCH));
			JXmlWrapper authSwitch = xml.getXmlNode("switch");
			String state = authSwitch.getStringValue("@authSwitch");
			if("0".equals(state)){
				result = true;
			}
			return result;
		} catch (Exception e) {
			logger.error("解析鉴权是否打开失败，请检查配置文件:"+FileConstant.AUTH_SWITCH+"是否正确",e);
			return false;
		}
	}

	/**
	 * 更换手机号检查
	 * @param bean
	 * @throws Exception
	 */
	@Override
	public void changeMobileCheck(UserBean bean) throws Exception {
		String mobileNo = CardMobileUtil.decryptMobile(bean.getMobileNo());
		String newNo = CardMobileUtil.decryptMobile(bean.getNewValue());
		logger.info("换绑手机号检测,原手机号:{} 新手机号:{} 用户名:{}", mobileNo, newNo, bean.getUid());

		if(!CheckUtil.isMobilephone(mobileNo)){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHANGE_MOBILE_CHECK_FORMAT_OLD_ERROR));
			bean.setBusiErrDesc("原手机号格式错误,请检查后重新填写");
			return;
		}
		if(!CheckUtil.isMobilephone(newNo)){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHANGE_MOBILE_CHECK_FORMAT_NEW_ERROR));
			bean.setBusiErrDesc("新手机号格式错误,请检查后重新填写");
			return;
		}
		if(mobileNo.equals(newNo)){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHANGE_MOBILE_CHECK_SAME));
			bean.setBusiErrDesc("新老手机号相同");
			return;
		}

		UserBean user = new UserBean();
		user.setMobileNo(newNo);
		userCenterService.checkMobileAccount(user);
		if(0 != user.getBusiErrCode()){
			bean.setBusiErrCode(user.getBusiErrCode());
			bean.setBusiErrDesc(user.getBusiErrDesc());
			return;
		}

		UserPojo userPojo= userMapper.getUserMobileByNickId(bean.getUid());
		if (userPojo != null && !StringUtil.isEmpty(userPojo.getMobileNoMD5())) {
			if(!userPojo.getMobileNoMD5().equals(MD5Helper.md5Hex(mobileNo))){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHANGE_MOBILE_CHECK_FAIL));
				bean.setBusiErrDesc("您的原手机号码不正确");
				return;
			}
		}else{
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_CHANGE_MOBILE_CHECK_NONE));
			bean.setBusiErrDesc("未查询到您的原有手机号");
			return;
		}
		bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
		bean.setBusiErrDesc("验证成功");
	}
}
