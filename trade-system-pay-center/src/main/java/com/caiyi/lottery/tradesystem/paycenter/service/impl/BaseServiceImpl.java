package com.caiyi.lottery.tradesystem.paycenter.service.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.UserPayMapper;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.paycenter.service.BaseService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.safecenter.clientwrapper.SafeCenterWrapper;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import bean.SafeBean;
import bean.SourceConstant;
import bean.TokenBean;
import constant.UserConstants;
import dto.RechargeCardDTO;
import lombok.extern.slf4j.Slf4j;
import pay.bean.PayBean;
import pay.pojo.RechCardPojo;


@Slf4j
@Service
public class BaseServiceImpl implements BaseService{
	@Autowired
	SafeCenterInterface safeCenterInterface;
	@Autowired
	RedisClient redisClient;
	@Autowired
	UserBasicInfoWrapper userBasicInfoWrapper;
	@Autowired
	SafeCenterWrapper safeCenterWrapper;
	@Autowired
	UserPayMapper userPayMapper;

	//从安全中心获取用户真实身份信息
	@Override
	public boolean getUserIdenInfo(PayBean bean) {
		SafeBean safeBean = new SafeBean();
		safeBean.setNickid(bean.getUid());
		safeBean.setUsersource(SourceConstant.CAIPIAO);
		BaseReq<SafeBean> req = new BaseReq<>(safeBean, SysCodeConstant.PAYCENTER);
		BaseResp<SafeBean> resp = safeCenterInterface.getUserTable(req);
		if(!"0".equals(resp.getCode())){
			bean.setBusiErrCode(Integer.parseInt(resp.getCode()));
			bean.setBusiErrDesc(resp.getDesc());
			log.info("从安全中心获取用户真实信息失败,用户名:"+bean.getUid()+" code:"+resp.getCode()+" desc:"+resp.getDesc());
			return false;
		}
		safeBean = resp.getData();
		bean.setIdcard(safeBean.getIdcard());
		bean.setRealName(safeBean.getRealname());
		return true;
	}

	//获取用户白名单
	@Override
	public boolean getUserWhiteGrade(PayBean bean) {
		String whiteGrade = "0";
		CacheBean cacheBean = new CacheBean();
		cacheBean.setKey(bean.getAppid());
		TokenBean tokenBean = (TokenBean) redisClient.getObject(cacheBean, TokenBean.class, log, SysCodeConstant.PAYCENTER);
		if(tokenBean!=null){
			String paramJson = tokenBean.getParamJson();
			if(!StringUtil.isEmpty(paramJson)){
				JSONObject json = JSONObject.parseObject(paramJson);
				whiteGrade = json.getString(UserConstants.OPENUSER);
				if(!StringUtil.isEmpty(whiteGrade)){
					bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
					return true;
				}
			}
		}
		whiteGrade = userBasicInfoWrapper.queryUserWhiteGrade(bean, log, SysCodeConstant.PAYCENTER);
		if(0!=bean.getBusiErrCode()||StringUtil.isEmpty(whiteGrade)){
			return false;
		}
		bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
		return true;
	}
	
	//获取对应bankCode指定的银行名称和图片地址
	@Override
	public Map<String,String> getBankCardMap(String bankCode) {
		Map<String,String> map = new HashMap<String, String>();
		JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.SUPPORT_BANK_INFO));
		List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("row");
		for (JXmlWrapper row : xmlNodeList) {
			String bankcode = row.getStringValue("@bankcode");
			if(bankCode.equals(bankcode)){
				String linkimg = row.getStringValue("@linkimg");
				String bankname = row.getStringValue("@bankname");
				map.put("linkimg", linkimg);
				map.put("bankname", bankname);
				return map;
			}
		}
		return map;
	}

	@Override
	public void getCardNoByApplyid(PayBean bean) {
		BaseReq<SafeBean> req = new BaseReq<>(SysCodeConstant.PAYCENTER);
		SafeBean safeBean = new SafeBean();
		//先到tb_user_pay查询safeKey
		String safeKey = userPayMapper.querySafeKey(bean.getApplyid());
		//安全中心用safeKey换取卡号
		bean.setSafeKey(safeKey);
		log.info("safeKey查询结果-->safeKey=="+bean.getSafeKey()+",applyid=="+bean.getApplyid()+",=="+bean.getUid() +",cardNo=="+bean.getCardNo());
	}

	@Override
	public List<SafeBean> getRealCardList(List<RechCardPojo> cardList, PayBean bean) {
		if(null == cardList || cardList.size() == 0){
			return null;
		}
		List<String> cardSafeKeyList = new ArrayList<>();
		for(RechCardPojo rechCard : cardList){
			cardSafeKeyList.add(rechCard.getSafeKey());
		}
		RechargeCardDTO rechCardDto = new RechargeCardDTO();
		rechCardDto.setNickid(bean.getUid());
		rechCardDto.setUsersource(SourceConstant.CAIPIAO);
		rechCardDto.setRechargeList(cardSafeKeyList);
		List<SafeBean> rechCardList = safeCenterWrapper.queryRechargeByRechargeId(rechCardDto, log, SysCodeConstant.PAYCENTER);
		return rechCardList;
	}
}
