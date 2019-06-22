package com.caiyi.lottery.tradesystem.paycenter.service;

import pay.bean.PayBean;
import pay.pojo.UserAcctPojo;
import pay.pojo.UserLogPojo;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public interface UserLogService {

    /**
     * 添加日志
     * @param userLogPojo
     * @return
     */
    public int saveLog(UserLogPojo userLogPojo);

    /**
     * 新版提款接口
     * @param bean
     * @param resultmap
     */
    public void newtakeMoney(PayBean bean, HashMap<String,Object> resultmap);

    //查询银行卡额度信息
    public HashMap queryBankCardLimitInfo(PayBean bean);

    /**
     *	银行卡号校验
     * @param bean
     * @return
     */
    public void checkCardNo(PayBean bean);

    /**
     * 提款-合法性校验
     */
    public void checkDrawMoneyValidity(PayBean bean);

}
