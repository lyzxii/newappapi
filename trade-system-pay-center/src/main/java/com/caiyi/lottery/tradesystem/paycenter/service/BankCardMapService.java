package com.caiyi.lottery.tradesystem.paycenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Bean;
import pay.bean.PayBean;
import pay.pojo.BankBranchPojo;
import pay.pojo.BankCardMapPojo;

import java.util.List;

/**
 * Created by XQH on 2017/12/20.
 */
public interface BankCardMapService {

    /**
     * 根据cbinno查询银行卡信息进行充值银行卡卡bin校验
     * @param cbinno
     * @return
     */
    public List<BankCardMapPojo> getBankCardInfoByBinno(String cbinno);


    /**
     * 根据cbinno查询银行卡信息进行提款银行卡检测
     * @param cbinno
     * @return
     */
    public List<BankCardMapPojo> drawBankCardInfoByBinno(String cbinno);

    /**
     * 根据银行对应的自定义编码，省市查询银行支行
     * @param bankBranchPojo
     * @return
     */
    public List<PayBean> getBankBranchByBcodeProCity(PayBean bankBranchPojo);

    /**
     * 代理商用户申请转款
     * @param bean
     * @throws Exception
     */
    BaseResp agentTransfer(BaseBean bean);

}
