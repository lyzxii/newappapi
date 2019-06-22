package com.caiyi.lottery.tradesystem.usercenter.controller;

import bean.UserBean;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.service.*;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pojo.Acct_UserPojo;
import pojo.UserAcctPojo;
import pojo.UserPojo;
import util.UserErrCode;


@Slf4j
@RestController
public class UserBasicInfoController {
	
	@Autowired
	ModifyUserInfoService modifyUserInfoService;
	@Autowired
	UserRecordService userRecordService;
	@Autowired
	UserBasicInfoService userBasicInfoService;
	@Autowired
	TokenManageService tokenManageService;

	@Autowired
	PersonalInfoService personalInfoService;

	@Autowired
	RegisterService  registerService;

	/**
	 * 用户提款银行卡绑定
	 * @param baseReq
	 * @return
	 */
	@RequestMapping(value="/user/bind_bankcard.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<BaseBean> bankCardBind(@RequestBody BaseReq<UserBean> baseReq){
		UserBean bean = baseReq.getData();
		try {
			modifyUserInfoService.bindUserBankCard(bean);
		} catch (Exception e) {
			log.info("用户修改提款银行卡失败,用户名:"+bean.getUid()+" 银行卡号:"+bean.getBankCard()+" flag:"+bean.getFlag());
		}
		BaseResp<BaseBean> resp = new BaseResp<>();
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		log.info("用户绑定提款银行卡返回结果,用户名:"+bean.getUid()+" result:"+resp.toJson());
		return resp;
	}

	/**
	 * 统计APP崩溃信息
	 * @param baseReq
	 * @return
	 */
	@RequestMapping(value = "/user/calculate_breakdown_error.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<BaseBean> calculate_breakdown_error(@RequestBody BaseReq<UserBean> baseReq){
		UserBean bean = baseReq.getData();
		BaseResp<BaseBean> resp = new BaseResp<>();
		log.info("进入统计记录系统崩溃程序，信息:" + bean.getUserInputs());
		try {
			String userNetErrorData = bean.getUserInputs();
			JSONArray userNetError = JSONArray.parseArray(userNetErrorData);
			for (int i = 0; i < userNetError.size();i++) {
				JSONObject errorData = userNetError.getJSONObject(i);
				int update = userRecordService.calculateBreakdownError(errorData);
				if(update > 0){
					log.info("插入用户崩溃信息成功");
				}else{
					resp.setCode(BusiCode.FAIL);
					resp.setDesc("插入用户崩溃信息失败");
					log.info("插入用户崩溃信息失败");
				}
			}
			resp.setCode(BusiCode.SUCCESS);
			resp.setDesc("存储用户崩溃信息成功");
		} catch (Exception e) {
			log.error("存储用户信息失败,req:"+baseReq.toJson(),e);
			resp.setCode(BusiCode.FAIL);
			resp.setDesc("存储用户崩溃信息失败");
		}
		return resp;
	}

    /**
     * 查询用户是否存在
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/user/checkIsExist.api",produces={"application/json;charset=UTF-8"})
    public BaseResp<BaseBean> checkIsExist(@RequestBody  BaseReq<UserBean> baseReq) {
		UserBean bean = baseReq.getData();
        BaseResp<BaseBean> resp = new BaseResp<>();
        try {
            if (bean.getBusiErrCode() == 0) {
				String result = userRecordService.checkUserExist(bean.getUid());
                if (!StringUtil.isEmpty(result)) {
					resp.setCode("1");
					resp.setDesc("用户名已经存在");
                } else {
					resp.setCode(BusiCode.SUCCESS);
					resp.setDesc("用户可以使用");
                }
            }
        } catch (Exception e) {
			resp.setCode(UserErrCode.ERR_EXCEPTION + "");
			resp.setDesc(UserErrCode.getErrDesc(bean.getBusiErrCode()));
            log.error("UserInfoBeanStub::checkIsExist ", e);
        }
        return resp;
    }
    
    /**
     * 查询用户白名单等级
     */
    @RequestMapping(value = "/user/user_whitegrade.api",produces={"application/json;charset=UTF-8"})
    public BaseResp<String> queryUserWhiteGrade(@RequestBody BaseReq<BaseBean> baseReq){
		BaseBean bean = baseReq.getData();
		String whiteGrade = userBasicInfoService.queryUserWhiteGrade(bean);
    	BaseResp<String> resp = new BaseResp<>(whiteGrade);
    	if(StringUtil.isEmpty(whiteGrade)){
    		resp.setCode(ErrorCode.USER_QUERY_USER_WHITEGRADE_ERROR);
	    	resp.setDesc("查询用户白名单失败");
    	}else{
	    	resp.setCode(BusiCode.SUCCESS);
	    	resp.setDesc("查询成功");
    	}
    	return resp;
    }
    
    /**
     * 查询用户基础信息
     */
    @RequestMapping(value = "/user/user_basic_info.api",produces={"application/json;charset=UTF-8"})
    public BaseResp<UserPojo> queryUserInfo(@RequestBody BaseReq<BaseBean> baseReq){
		BaseBean bean = baseReq.getData();
    	BaseResp<UserPojo> resp = new BaseResp<>();
    	UserPojo user = userBasicInfoService.queryUserInfo(bean);
    	resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
    	if(bean.getBusiErrCode()==0){
    		resp.setData(user);
    	}
    	return resp;
    }
	/**
	 * 查询用户基础信息 ---卡密兑换红包使用
	 */
	@RequestMapping(value = "/user/query_userinfo_cardCharge.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<UserPojo> queryUserInfoForCardCharge(@RequestBody BaseReq<BaseBean> baseReq){
		BaseBean bean = baseReq.getData();
		BaseResp<UserPojo> resp = new BaseResp<>();
		UserPojo user = userBasicInfoService.queryUserInfoForCardCharge(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode()==0){
			resp.setData(user);
		}
		return resp;
	}

	/**
	 * 获取用户积分
	 */
	@RequestMapping(value = "/user/query_userpoint.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<UserAcctPojo> getUserPoint(@RequestBody BaseReq<BaseBean> req){
		BaseBean bean = req.getData();
		UserAcctPojo userAcct=userBasicInfoService.queryUserPoint(bean);
		BaseResp<UserAcctPojo> resp = new BaseResp<>();
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(bean.getBusiErrCode()==0){
			resp.setData(userAcct);
		}
		return resp;
	}

	/**
	 * 更新用户积分
	 */
	@RequestMapping(value = "/user/update_userpoint.api",produces={"application/json;charset=UTF-8"})
	public BaseResp updateUserPoint(@RequestBody BaseReq<UserBean> req){
		UserBean bean=req.getData();
		BaseResp resp=new BaseResp();
		try {
			int flag=userBasicInfoService.updateUserPoint(bean);
			if(flag!=1){//更新失败
                resp.setCode(ErrorCode.USER_POINT_UPDATE_ERROR);
                resp.setDesc("更新用户积分失败");
            }else{
                resp.setCode(BusiCode.SUCCESS);
                resp.setDesc("更新用户积分成功");
            }
		} catch (Exception e) {
			resp.setCode(ErrorCode.USER_POINT_UPDATE_ERROR);
			resp.setDesc("更新用户积分失败");
		}
		return resp;
	}

	@RequestMapping(value = "/user/count_user_charge.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<Integer> countUserCharge(@RequestBody BaseReq<BaseBean> req){
		BaseBean bean=req.getData();
		BaseResp<Integer> resp=new BaseResp<>();
        Integer cnt=userBasicInfoService.countUserCharge(bean);
        if(bean.getBusiErrCode()!=0||cnt==null){
			resp.setCode(-1+"");
			return resp;
		}
		resp.setCode(0+"");
        resp.setDesc("查询成功");
        resp.setData(cnt);
        return resp;
	}

	@RequestMapping(value = "/user/query_uservip_agentcnt.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<Integer> queryUserVipAgentCount(@RequestBody BaseReq<BaseBean> req){
		BaseBean bean=req.getData();
		BaseResp<Integer> resp=new BaseResp<>();
		Integer cnt=userBasicInfoService.queryUserVipAgentCount(bean);
		if(bean.getBusiErrCode()!=0||cnt==null){
			resp.setCode(-1+"");
			return resp;
		}
		resp.setCode(0+"");
		resp.setDesc("查询成功");
		resp.setData(cnt);
		return resp;
	}

	@RequestMapping(value = "/user/query_app_agentid.api",produces={"application/json;charset=UTF-8"})
	public BaseResp<String> queryAppagentId(@RequestBody BaseReq<BaseBean> req){
		BaseResp<String> resp=new BaseResp<>();
		String appAgentid=userBasicInfoService.queryAppAgentId(req.getData());
		if(appAgentid==null){
			resp.setCode(-1+"");
			return resp;
		}
		resp.setCode(0+"");
		resp.setDesc("查询成功");
		resp.setData(appAgentid);
		return resp;
	}


	@RequestMapping(value = "/user/update_agentid.api",produces={"application/json;charset=UTF-8"})
	public BaseResp updageAgentId(@RequestBody BaseReq<UserBean> req){
		UserBean bean=req.getData();
		BaseResp resp=new BaseResp();
		try {
			int flag=userBasicInfoService.updateAgentid(bean);
			if(flag!=1){//更新失败
				resp.setCode(-1+"");
				resp.setDesc("更新appagentid失败");
			}else{
				resp.setCode(0+"");
				resp.setDesc("更新appagentid成功");
			}
		} catch (Exception e) {
			resp.setCode(-1+"");
			resp.setDesc("更新appagentid异常");
			log.error("更新appagentid异常,uid:{}",bean.getUid(),e);
		}
		return resp;
	}

	/**
	 *检查等级
	 */
	@RequestMapping("/user/check_level")
	public BaseResp check_level(@RequestBody BaseReq<BaseBean> req){
		BaseResp resp=new BaseResp();
		BaseBean bean=req.getData();
		personalInfoService.check_level(bean);//检测等级
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		return resp;
	}

	/**
	 * 一年能的消费次数
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/user/count_out_nickid_inayear.api")
	BaseResp<Integer> countOutByNickidInAYear(BaseReq<BaseBean> req) {
		BaseBean bean = req.getData();
		BaseResp baseResp = new BaseResp();
		try {
			Integer count = userBasicInfoService.countOutByNickidInAYear(bean);
			baseResp.setCode(BusiCode.SUCCESS);
			baseResp.setDesc("查询成功");
			baseResp.setData(count);
		} catch (Exception e) {
			log.error("一年能的消费次数查询失败，[uid:{}]", bean.getUid(), e);
			baseResp.setCode(BusiCode.FAIL);
			baseResp.setDesc("查询失败");
		}
		return baseResp;
	}

	/**
	 * 查询是否新用户
	 * @return
	 */
	@RequestMapping(value = "/user/is_new_user.api")
	public BaseResp<Integer> isNewUser(BaseReq<BaseBean> req) {
		BaseBean bean = req.getData();
		BaseResp baseResp = new BaseResp();
		try {
			Integer num = userBasicInfoService.isNewUser(bean);
			baseResp.setCode(BusiCode.SUCCESS);
			baseResp.setDesc("查询成功");
			baseResp.setData(num);
		} catch (Exception e) {
			log.error("查询是否新用户失败，[uid:{}]", bean.getUid(), e);
			baseResp.setCode(BusiCode.FAIL);
			baseResp.setDesc("查询失败");
		}
		return baseResp;
	}

	@RequestMapping(value = "/user/query_useraccout_info.api")
	public BaseResp<Acct_UserPojo> queryUserAccountInfo(@RequestBody BaseReq<BaseBean> req){
		BaseBean bean = req.getData();
		BaseResp<Acct_UserPojo> resp=new BaseResp<>();
		Acct_UserPojo acct_user=userBasicInfoService.queryUserAccountInfo(bean);
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(acct_user!=null&&bean.getBusiErrCode()==0){
			resp.setData(acct_user);
		}
		return resp;
	}


	/**
	 * 查询自买次数
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/user/count_selfbuy.api",method = RequestMethod.POST)
	BaseResp<Integer> countSelfBuy(@RequestBody BaseReq<BaseBean> req){
		BaseBean bean=req.getData();
		BaseResp<Integer> resp = new BaseResp<>();
//		Integer cnt=userBasicInfoService.countSelfBuy(bean);
		Integer cnt = 10;// 查询购买次数，新老项目一起写死为10次
		resp.setCode(bean.getBusiErrCode()+"");
		resp.setDesc(bean.getBusiErrDesc());
		if(cnt!=null&&bean.getBusiErrCode()==0){
			resp.setData(cnt);
		}
		return resp;
	}


	@RequestMapping(value = "/user/query_agent_id.api",method = RequestMethod.POST)
	BaseResp<String> queryAgentId(@RequestBody BaseReq<UserBean> req){
		BaseResp<String> resp=new BaseResp<>();
		UserBean bean=req.getData();
        registerService.queryagentid(bean);
        if(bean.getBusiErrCode()!=0){
           resp.setCode(BusiCode.FAIL);
           resp.setDesc("查询代理商ID失败");
           return resp;
		}
		resp.setCode(BusiCode.SUCCESS);
		resp.setDesc("查询代理商ID成功");
		resp.setData(bean.getComeFrom());
		return resp;
	}
}
