package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.Memo;
import bean.SafeBean;
import bean.SourceConstant;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderInterface;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.redis.util.CacheUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.safecenter.clientwrapper.SafeCenterWrapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.*;
import com.caiyi.lottery.tradesystem.usercenter.service.TokenManageService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserCenterShowService;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.AccountDetails;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import dto.MyLotteryDTO;
import dto.UserAccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.*;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.caiyi.lottery.tradesystem.usercenter.util.MemoConvert.showCmemo;
import static com.caiyi.lottery.tradesystem.util.xml.XmlUtil.*;


@Slf4j
@Service
public class UserCenterShowImpl implements UserCenterShowService{

	@Autowired
	private Acct_UserMapper acct_userMapper; 
	@Autowired
	private AgentMapper agentMapper;
	@Autowired
	private Grade_UserMapper grade_userMapper;
	@Autowired
	private TokenManageService tokenManageService;
	@Autowired
	private UserChargeMapper userChargeMapper;
	@Autowired
	private UserPayMapper userPayMapper;
	@Autowired
	private UserCashMapper userCashMapper;
	@Autowired
	private RedisInterface redisInterface;
	@Autowired
	private SafeCenterWrapper safeCenterWrapper;
	@Autowired
	OrderInterface orderInterface;
	
	@Override
	public MyLotteryDTO queryMyLotteryData(UserBean bean) {
	 	Acct_UserPojo useracct = acct_userMapper.queryMlotteryData(bean.getUid());
	 	queryUserIdenInfo(bean,useracct);
	 	Map<String, String> discountMap = discountStatus(bean, useracct);
	 	String showDiscount = discountMap.get("showDiscount");
	 	String viplevel = discountMap.get("viplevel");
		Grade_UserPojo levelPojo = grade_userMapper.queryLevelTitle(bean.getUid());
		String levelTitle = levelPojo.getLevelTitle();
		int unawardnum = 0;
		BaseReq<BaseBean> req = new BaseReq<BaseBean>(bean, SysCodeConstant.USERCENTER);
		BaseResp<Integer> resp = orderInterface.queryUserUnbeginNum(req);
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			unawardnum = resp.getData();
		}
		MyLotteryDTO dto = buildMyLotterData(useracct,showDiscount,levelTitle,unawardnum);
		tokenManageService.updateToken(viplevel,useracct.getWhitegrade(),bean);
		return dto;
	}
	
    private Map<String,String> discountStatus(UserBean bean, Acct_UserPojo useracct){
	 	CacheBean cacheBean = new CacheBean();
	 	cacheBean.setKey(bean.getUid()+"-isagent");
	 	String showDiscount = CacheUtil.getString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
	 	cacheBean.setKey(bean.getUid()+"-viplevel");
		String viplevel = CacheUtil.getString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
		if(StringUtil.isEmpty(showDiscount)||StringUtil.isEmpty(viplevel)){
			AgentPojo agentPojo = agentMapper.queryAgentLevel(useracct.getAgentid(), useracct.getUid());
			if(agentPojo==null){
				agentPojo = new AgentPojo();
				showDiscount = "0";
				viplevel = "-1";
			}else{
				int agentFlag = agentPojo.getAgentFlag();
				viplevel = agentPojo.getAgentLevel()+"";
				if(agentFlag==1 && (3<=agentPojo.getAgentLevel()&&agentPojo.getAgentLevel()<=6)){
					showDiscount = "1";
				}else{
					showDiscount = "0";
				}
			}
			cacheBean.setKey(bean.getUid()+"-viplevel");
			cacheBean.setValue(viplevel);
			cacheBean.setTime(Constants.TIME_DAY);
			CacheUtil.setString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
			//将是否显示我的优惠放入缓存，缓存时间为一天
			cacheBean.setKey(bean.getUid()+"-isagent");
			cacheBean.setValue(showDiscount);
			cacheBean.setTime(Constants.TIME_DAY);
			CacheUtil.setString(cacheBean, log, redisInterface, SysCodeConstant.USERCENTER);
		}
		Map<String, String> discountMap = new HashMap<>();
		discountMap.put("showDiscount", showDiscount);
		discountMap.put("viplevel", viplevel);
		return discountMap;
    }


	private void queryUserIdenInfo(UserBean bean, Acct_UserPojo useracct ) {
		SafeBean safeBean = new SafeBean();
	 	safeBean.setNickid(bean.getUid());
	 	safeBean.setUsersource(SourceConstant.CAIPIAO);
	 	safeBean = safeCenterWrapper.getUserTable(safeBean, log, SysCodeConstant.USERCENTER);
	 	if(safeBean==null){
	 		useracct.setMobileNo("");
	 		useracct.setIdcard("");
	 		log.info("查询用户安全中心信息信息为空,用户名:"+bean.getUid());
	 	}else{
		 	bean.setMobileNo(safeBean.getMobileno());
		 	bean.setIdCardNo(safeBean.getIdcard());
		 	useracct.setMobileNo(safeBean.getMobileno());
		 	useracct.setIdcard(safeBean.getIdcard());
	 	}
	}


	//组织我的彩票返回数据
	private MyLotteryDTO buildMyLotterData(Acct_UserPojo useracct, String showDiscount, String levelTitle, int unaward) {
		MyLotteryDTO dto = new MyLotteryDTO();
		covertUserBasicInfo(useracct);
		String userPhoto = useracct.getUserImg();
		//绝对地址使用时
		if(!StringUtil.isEmpty(userPhoto) && userPhoto.startsWith("http://")){
			useracct.setUserImg(userPhoto.substring(22));
		}
		
		if("1".equals(showDiscount)){
			double mydiscount = agentMapper.getVipReturnMoney(useracct.getAgentid());
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			dto.setMydiscount(nf.format(mydiscount)+"");
			dto.setAgentFlag(1);
		}
		dto.setUnawardnum(unaward);
		dto.setLevelTitle(levelTitle);
		
		int authSwitch = checkAuthSwitch();
		dto.setAuthSwitch(authSwitch);
		BeanUtilWrapper.copyPropertiesIgnoreNull(useracct, dto);
		return dto;
	}

	//转换用户真实姓名银行卡，手机号等保密数据
	private void covertUserBasicInfo(Acct_UserPojo useracct) {
		if(StringUtil.isEmpty(useracct.getRealName())){
			useracct.setRealName("");
		}else{
			if("null".equals(useracct.getRealName())){
				useracct.setRealName("");
			}else{
				if(useracct.getRealName().length()<1){
					useracct.setRealName("");
				}else{
					useracct.setRealName("*"+useracct.getRealName().substring(1));
				}
			}
		}
		if(StringUtil.isEmpty(useracct.getIdcard())){
			useracct.setIdcard("");
		}else{
			if("null".equals(useracct.getIdcard())){
				useracct.setIdcard("");
			}else{
				String eidcard = SecurityTool.iosencrypt(useracct.getIdcard());
				useracct.setIdcard(eidcard);
			}
		}
		
		if(StringUtil.isEmpty(useracct.getMobileNo())){
			useracct.setMobileNo("");
		}else{
			if("null".equals(useracct.getMobileNo())){
				useracct.setMobileNo("");
			}else{
				String emobile = SecurityTool.iosencrypt(useracct.getMobileNo());
				useracct.setMobileNo(emobile);
			}
		}
		if(StringUtil.isEmpty(useracct.getDrawBankCard())){
			useracct.setDrawBankCard("");
		}else{
			String drawBankCard = useracct.getDrawBankCard();
			if("null".equals(drawBankCard)){
				useracct.setDrawBankCard("");
			}else{
				if(useracct.getDrawBankCard().length()<5){
					useracct.setDrawBankCard(drawBankCard+"********");
				}else{
					useracct.setDrawBankCard(drawBankCard.substring(0, 3)+"********"+drawBankCard.substring(drawBankCard.length()-4));
				}
			}
		}
	}
	
	//检测鉴权开关
  	public int checkAuthSwitch() {
		JXmlWrapper xml = null;
    	int result = 0;
		try {
			xml = JXmlWrapper.parse(new File(FileConstant.AUTH_SWITCH));
			JXmlWrapper authSwitch = xml.getXmlNode("switch");
			String state = authSwitch.getStringValue("@authSwitch");
			if(!"0".equals(state)){
				result = 1;
			}
			return result;
		} catch (Exception e) {
			log.error("解析鉴权是否打开失败，请检查配置文件:"+FileConstant.AUTH_SWITCH+"是否正确",e);
			return 0;
		}
  	}

	@Override
	public Page<List<UserAccountDTO>> queryAccount(UserBean bean) throws Exception {
		Page<List<UserAccountDTO>> page = new Page<>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(df.parse(DateTimeUtil.getCurrentDateTime()));
		// 账户明细只显示近7天的记录
		c2.add(Calendar.DAY_OF_MONTH, -62);

		bean.setStime(df.format(c2.getTime()));
		bean.setEtime(df.format(c1.getTime()));
		int flag = bean.getFlag();
		switch (flag) {
			//充值明细
			case 14:
				page = queryPayRecords(bean);
				break;
			//提款明细
			case 15:
				page = queryCashRecords(bean);
				break;
			//收支明细，以下条件在sqlmap中区分
			case 13:
			//购买彩票
			case 16:
			//奖金派送
			case 17:
			//中奖打赏
			case 37:
			//返款明细
			case 38:
				page = queryChargeRecords(bean);
				break;
			//提款进度
			case 67:
				page = queryCashDetail(bean);
				break;
			default:
				log.info("未知的查询类型：[uid:{},flag:{}]", bean.getUid(), bean.getFlag());
				bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_UNDEFINE_FLAG));
				bean.setBusiErrDesc("未知的查询类型");
				break;
		}
		return page;
	}

	/**
	 * 分页查询交易明细、购彩明细、奖金派送、中奖打赏、返款明细
	 * @param bean
	 * @return
	 */
	private Page<List<UserAccountDTO>> queryChargeRecords(UserBean bean) {
		PageHelper.startPage(bean.getPn(),bean.getPs());
		List<UserChargePojo> chargePojoList = userChargeMapper.getChargeByNickidAndDate(bean.getUid(),bean.getStime(),bean.getEtime(),bean.getFlag());
		PageInfo<UserChargePojo> pageInfo = new PageInfo<>(chargePojoList);

		Page<List<UserAccountDTO>> page = new Page<>();
		List<UserAccountDTO> accountDTOList;
		UserAccountDTO accountDTO;
		page.setPageNumber(bean.getPn());
		page.setPageSize(bean.getPs());
		page.setTotalPages(pageInfo.getPages());
		page.setTotalRecords(pageInfo.getTotal());

		accountDTOList = new ArrayList<>();
		for (UserChargePojo charge : pageInfo.getList()) {
			accountDTO = new UserAccountDTO();
			BeanUtilWrapper.copyPropertiesIgnoreNull(charge, accountDTO);

			if (bean.getFlag() == 13) {
				String memo = AccountDetails.ShowCmemo(charge.getTradeType(), charge.getMemo());
				if (!StringUtil.isEmpty(memo)) {
					String[] arr = memo.split("\\,");
					if (arr.length >= 2) {
						//充值方式  且彩种编号跟网关编号相同
						if (charge.getTradeType() == 200) {
							accountDTO.setPlayKind("0");
						} else {
							accountDTO.setPlayKind(arr[0]);
						}
						accountDTO.setPlayId(arr[1]);
					} else {
						//充值方式  且彩种编号跟网关编号相同
						if (charge.getTradeType() == 200) {
							accountDTO.setPlayKind("0");
						} else {
							accountDTO.setPlayKind(arr[0]);
						}
						accountDTO.setPlayId("");
					}
				} else {
					accountDTO.setPlayKind("");
					accountDTO.setPlayId("");
				}
				accountDTO.setMemo(null);
			} else {
				String memoStr = charge.getMemo();
				Memo memo = showCmemo(charge.getTradeType() + "", memoStr);
				accountDTO.setId(charge.getChargeId()+"");
				accountDTO.setPlayKind(memo.getGid());
				accountDTO.setPlayId(memo.getHid());
				accountDTO.setMemo(memo.getMemo());
				accountDTO.setTradeTypeName(getBiztype(charge.getTradeType() + ""));
			}
			accountDTOList.add(accountDTO);
		}
		if (pageInfo.getTotal() == 0) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
			bean.setBusiErrDesc("暂无数据");
		} else {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
			bean.setBusiErrDesc("查询成功");
		}
		page.setDatas(accountDTOList);
		return page;
	}

	/**
	 * 查询支付明细
	 * @param bean
	 * @return
	 */
	private Page<List<UserAccountDTO>> queryPayRecords(UserBean bean) {
		Page<List<UserAccountDTO>> page = new Page<>();
		PageHelper.startPage(bean.getPn(), bean.getPs());
		List<UserPayPojo> payPojoList = userPayMapper.getPayRecordByNickidAndDate(bean.getUid(), bean.getStime(), bean.getEtime(), null);
		PageInfo<UserPayPojo> pageInfo = new PageInfo<>(payPojoList);

		page.setPageNumber(bean.getPn());
		page.setPageSize(bean.getPs());
		page.setTotalPages(pageInfo.getPages());
		page.setTotalRecords(pageInfo.getTotal());

		List<UserAccountDTO> userAccountDTOList = new ArrayList<>();
		UserAccountDTO accountDTO;
		String memo = "";
		for (UserPayPojo payPojo : pageInfo.getList()) {
			accountDTO = new UserAccountDTO();
			BeanUtilWrapper.copyPropertiesIgnoreNull(payPojo, accountDTO);
			accountDTO.setId(payPojo.getApplyID());

			if (!StringUtil.isEmpty(payPojo.getMemo())) {
				memo = payPojo.getMemo().replace("代理", "");
			}
			accountDTO.setTradeTime(payPojo.getConfirmTime());
			accountDTO.setMemo(memo);
			accountDTO.setTradeTypeName(CHONGZHI.get(payPojo.getBankId()));
			userAccountDTOList.add(accountDTO);
		}
		if (pageInfo.getTotal() == 0) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
			bean.setBusiErrDesc("暂无数据");
		} else {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
			bean.setBusiErrDesc("查询成功");
		}
		page.setDatas(userAccountDTOList);
		return page;
	}

	/**
	 * 提款明细
	 * @param bean
	 * @return
	 */
	private Page<List<UserAccountDTO>> queryCashRecords(UserBean bean) {
		Page<List<UserAccountDTO>> page = new Page<>();
		PageHelper.startPage(bean.getPn(), bean.getPs());
		List<UserCashPojo> cashPojoList = userCashMapper.getCashByNickidAndDate(bean.getUid(), bean.getStime(), bean.getEtime());
		PageInfo<UserCashPojo> pageInfo = new PageInfo<>(cashPojoList);

		page.setPageNumber(bean.getPn());
		page.setPageSize(bean.getPs());
		page.setTotalPages(pageInfo.getPages());
		page.setTotalRecords(pageInfo.getTotal());

		UserAccountDTO userAccountDTO;
		List<UserAccountDTO> userAccountDTOList = new ArrayList<>();
		for (UserCashPojo pojo : pageInfo.getList()) {
			userAccountDTO = new UserAccountDTO();
			BeanUtilWrapper.copyPropertiesIgnoreNull(pojo, userAccountDTO);

			userAccountDTO.setId(pojo.getCashId()+"");
			userAccountDTO.setTradeTime(pojo.getCashTime());
			userAccountDTO.setOperationTime(pojo.getConfTime());
			userAccountDTO.setState(TIKUAN.get(pojo.getSuccess()+""));

			userAccountDTOList.add(userAccountDTO);
		}
		if (pageInfo.getTotal() == 0) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
			bean.setBusiErrDesc("暂无数据");
		} else {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
			bean.setBusiErrDesc("查询成功");
		}
		page.setDatas(userAccountDTOList);
		return page;
	}

	/**
	 * 提款进度
	 * @param bean
	 * @return
	 */
	private Page<List<UserAccountDTO>> queryCashDetail(UserBean bean) {
		Page<List<UserAccountDTO>> page = new Page<>();
		UserCashPojo userCashPojo = userCashMapper.getCashByNickidAndCashid(bean.getUid(), Integer.parseInt(bean.getCashId()));

		page.setPageNumber(0);
		page.setPageSize(0);
		page.setTotalPages(0);
		page.setTotalRecords(0L);

		List<UserAccountDTO> userAccountDTOList = new ArrayList<>();
		UserAccountDTO userAccountDTO = new UserAccountDTO();
		if (userCashPojo == null) {
			page.setDatas(userAccountDTOList);
			bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
			bean.setBusiErrDesc("暂无数据");
			return page;
		}
		BeanUtilWrapper.copyPropertiesIgnoreNull(userCashPojo, userAccountDTO);
		userAccountDTO.setId(userCashPojo.getCashId()+"");
		userAccountDTO.setTradeTime(userCashPojo.getCashTime());
		userAccountDTO.setOperationTime(userCashPojo.getConfTime());
		userAccountDTO.setState(userCashPojo.getState() + "");

		userAccountDTOList.add(userAccountDTO);
		page.setDatas(userAccountDTOList);
		bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
		bean.setBusiErrDesc("查询成功");
		return page;
	}
}
