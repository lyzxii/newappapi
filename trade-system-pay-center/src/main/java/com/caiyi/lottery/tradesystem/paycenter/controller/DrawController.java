package com.caiyi.lottery.tradesystem.paycenter.controller;

import bean.SafeBean;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.paycenter.dao.UserCashMapper;
import com.caiyi.lottery.tradesystem.paycenter.service.BankCardMapService;
import com.caiyi.lottery.tradesystem.paycenter.service.UserLogService;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.bean.PayBean;
import pay.pojo.BankBranchPojo;
import pay.pojo.BankCardMapPojo;
import pay.pojo.UserAcctPojo;
import pay.pojo.UserLogPojo;
import pojo.UserPojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DrawController {
    @Autowired
    BankCardMapService bankCardMapService;
    @Autowired
    UserLogService userLogService;

    /**
     * 通过binno查询银行卡信息进行充值银行卡卡bin校验
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/bankcardbybinno.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<BankCardMapPojo>> getBankCardInfoByBinno(@RequestBody BaseReq<UserBean> req){
        BaseResp<List<BankCardMapPojo>> baseResp = new BaseResp<List<BankCardMapPojo>>();
        UserBean bean = req.getData();
        List<BankCardMapPojo> bankCardMapPojos = bankCardMapService.getBankCardInfoByBinno(bean.getBankCard());
        baseResp.setData(bankCardMapPojos);
        baseResp.setCode(bean.getBusiErrCode()+"");
        baseResp.setDesc(bean.getBusiErrDesc());
        return baseResp;
    }

    /**
     * 根据cbinno查询银行卡信息进行提款银行卡检测
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/drawbankcardbybinno.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<BankCardMapPojo>> drawBankCardInfoByBinno(@RequestBody BaseReq<UserBean> req){
        BaseResp<List<BankCardMapPojo>> baseResp = new BaseResp<List<BankCardMapPojo>>();
        UserBean bean = req.getData();
        List<BankCardMapPojo> bankCardMapPojos = bankCardMapService.drawBankCardInfoByBinno(bean.getBankCard());
        baseResp.setData(bankCardMapPojos);
        baseResp.setCode(bean.getBusiErrCode()+"");
        baseResp.setDesc(bean.getBusiErrDesc());
        return baseResp;
    }


    /**
     * 根据银行对应的自定义编码，省市查询银行支行
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/bankbranchbybpc.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<PayBean>> getBankBranchByBcodeProCity(@RequestBody BaseReq<PayBean> req) {
        BaseResp<List<PayBean>> baseResp = new BaseResp<List<PayBean>>();
        PayBean bean = req.getData();
//        BankBranchPojo bankBranchPojo = new BankBranchPojo();
//        BeanUtils.copyProperties(bean,bankBranchPojo);
        List<PayBean> bankBranchPojos = bankCardMapService.getBankBranchByBcodeProCity(bean);
        baseResp.setData(bankBranchPojos);
        return baseResp;
    }

    /**
     * 新版提款接口
     */
    @RequestMapping("/pay/newdrawmoney.api")
    public BaseResp<PayBean> newDrawTakeMoney(@RequestBody BaseReq<PayBean> req){
        BaseResp<PayBean> baseResp = new BaseResp<PayBean>();
        PayBean bean = req.getData();
        HashMap<String,Object> map = new HashMap<String,Object>();
        userLogService.newtakeMoney(bean,map);
        baseResp.setData(bean);
        return baseResp;
    }

    /**
     *	银行卡号校验
     */
    @RequestMapping("/pay/checkCardNo.api")
    public BaseResp<PayBean> checkCardNo(@RequestBody BaseReq<PayBean> req){
        BaseResp<PayBean> baseResp = new BaseResp<PayBean>();
        PayBean bean = req.getData();
        userLogService.checkCardNo(bean);
        baseResp.setData(bean);
        return baseResp;
    }
    /**
     *	查询银行卡额度信息
     */
    @RequestMapping("/pay/bankCardLimitInfo.api")
    public BaseResp<PayBean> queryBankCardLimitInfo(@RequestBody BaseReq<PayBean> req){
        BaseResp<PayBean> baseResp = new BaseResp<PayBean>();
        PayBean bean = req.getData();
        userLogService.queryBankCardLimitInfo(bean);
        baseResp.setData(bean);
        return baseResp;
    }

    /**
     * 代理商用户申请转款
     */
    @RequestMapping("/pay/agent_transfer.api")
    public BaseResp agentTransfer(@RequestBody BaseReq<BaseBean> req){
        return  bankCardMapService.agentTransfer(req.getData());
    }

    /**
     * 提款-合法性校验
     */
    @RequestMapping("/pay/checkDrawMoneyValidity.api")
    public BaseResp<PayBean> checkDrawMoneyValidity(@RequestBody BaseReq<PayBean> req){
        BaseResp<PayBean> baseResp = new BaseResp<PayBean>();
        PayBean bean = req.getData();
        userLogService.checkDrawMoneyValidity(bean);
        baseResp.setData(bean);
        return baseResp;
    }
}
