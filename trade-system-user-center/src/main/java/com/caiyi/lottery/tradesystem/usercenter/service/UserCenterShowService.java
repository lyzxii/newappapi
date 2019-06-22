package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import dto.MyLotteryDTO;
import dto.UserAccountDTO;

import java.util.List;

public interface UserCenterShowService {
    /**
     * 查询我的彩票页面数据
     * @param bean
    * @return 
     */
	 MyLotteryDTO queryMyLotteryData(UserBean bean);

    /**
     * 查询账户明细
     * @param bean
     * @return
     * @throws Exception
     */
    Page<List<UserAccountDTO>> queryAccount(UserBean bean) throws Exception;
}
