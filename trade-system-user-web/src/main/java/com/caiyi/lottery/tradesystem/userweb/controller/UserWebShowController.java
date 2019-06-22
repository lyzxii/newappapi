package com.caiyi.lottery.tradesystem.userweb.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.usercenter.client.UserShowInterface;
import dto.MyLotteryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 客户端页面显示
 * @author A-0205
 *
 */
@RestController
public class UserWebShowController {

    private Logger logger = LoggerFactory.getLogger(UserWebShowController.class);

    @Autowired
    private UserShowInterface userCenterShowInterface;
    
    /**
     * 登入操作
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.USERWEB)
    @RequestMapping(value = "/user/mlottery.api" ,produces={"application/json;charset=UTF-8"})
    public Result<MyLotteryDTO> mlottery(UserBean bean){
        BaseReq<UserBean> baseReq = new BaseReq<>(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
    	Result<MyLotteryDTO> result = new Result<>();
    	BaseResp<MyLotteryDTO> resp = userCenterShowInterface.mlottery(baseReq);
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if("0".equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			logger.info("我的彩票用户中心业务出错,用户名:"+bean.getUid());
		}
        return result;
    }

}
