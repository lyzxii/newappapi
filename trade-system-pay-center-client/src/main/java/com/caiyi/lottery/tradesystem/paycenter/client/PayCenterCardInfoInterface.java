package com.caiyi.lottery.tradesystem.paycenter.client;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pay.bean.PayBean;
import pay.pojo.BankBranchPojo;
import pay.pojo.BankCardMapPojo;
import pay.pojo.UserAcctPojo;

import java.util.List;

/**
 * Created by XQH on 2017/12/20.
 */
@FeignClient(name = "tradecenter-system-paycenter-center")
public interface PayCenterCardInfoInterface {

    /**
     * 通过binno查询银行卡信息进行充值银行卡卡bin校验
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/bankcardbybinno.api")
    public BaseResp<List<BankCardMapPojo>> getBankCardInfoByBinno(@RequestBody BaseReq<UserBean> req);

    /**
     * 通过binno查询银行卡信息进行进行提款银行卡检测
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/drawbankcardbybinno.api")
    public BaseResp<List<BankCardMapPojo>> drawBankCardInfoByBinno(@RequestBody BaseReq<UserBean> req);

    /**
     * 根据银行对应的自定义编码，省市查询银行支行
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/bankbranchbybpc.api")
    public BaseResp<List<PayBean>> getBankBranchByBcodeProCity(@RequestBody BaseReq<PayBean> req);


    @RequestMapping("/pay/agent_transfer.api")
    BaseResp agentTransfer(@RequestBody BaseReq<BaseBean> req);

    /**
     * 新版提款接口
     */
    @RequestMapping("/pay/newdrawmoney.api")
    public BaseResp<PayBean> newDrawTakeMoney(@RequestBody BaseReq<PayBean> req);

    /**
     *	银行卡号校验
     */
    @RequestMapping("/pay/checkCardNo.api")
    public BaseResp<PayBean> checkCardNo(@RequestBody BaseReq<PayBean> req);

    /**
     *	查询银行卡额度信息
     */
    @RequestMapping("/pay/bankCardLimitInfo.api")
    public BaseResp<PayBean> queryBankCardLimitInfo(@RequestBody BaseReq<PayBean> req);

    /**
     * 提款-合法性校验
     */
    @RequestMapping("/pay/checkDrawMoneyValidity.api")
    public BaseResp<PayBean> checkDrawMoneyValidity(@RequestBody BaseReq<PayBean> req);

}
