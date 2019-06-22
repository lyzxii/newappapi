package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.BankBranchMapper;
import com.caiyi.lottery.tradesystem.paycenter.dao.BankCardMapMapper;
import com.caiyi.lottery.tradesystem.paycenter.dao.CpUserPayMapper;
import com.caiyi.lottery.tradesystem.paycenter.service.BankCardMapService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pay.bean.PayBean;
import pay.pojo.BankCardMapPojo;

import java.util.List;

/**
 * Created by XQH on 2017/12/20.
 */
@Slf4j
@Service
public class BankCardMapServiceImpl implements BankCardMapService {

    @Autowired
    private BankCardMapMapper bankCardMapMapper;

    @Autowired
    private BankBranchMapper bankBranchMapper;

    @Autowired
    RedisClient redisClient;

    @Autowired
    UserBasicInfoInterface userInfoInterface;

    @Autowired
    CpUserPayMapper cpUserPayMapper;

    /**
     * 根据cbinno查询银行卡信息进行充值银行卡卡bin校验
     *
     * @param cbinno
     * @return
     */
    public List<BankCardMapPojo> getBankCardInfoByBinno(String cbinno) {
        return bankCardMapMapper.getBankCardInfoByBinno(cbinno);
    }

    /**
     * 根据cbinno查询银行卡信息进行提款银行卡检测
     *
     * @param cbinno
     * @return
     */
    public List<BankCardMapPojo> drawBankCardInfoByBinno(String cbinno) {
        return bankCardMapMapper.drawBankCardInfoByBinno(cbinno);
    }

    /**
     * 根据银行对应的自定义编码，省市查询银行支行
     *
     * @param bankBranchPojo
     * @return
     */
    public List<PayBean> getBankBranchByBcodeProCity(PayBean bankBranchPojo) {
        return bankBranchMapper.getBankBranchByBcodeProCity(bankBranchPojo);
    }

    /**
     *代理商转款
     */
    @Override
    public BaseResp agentTransfer(BaseBean bean) {
        BaseResp res = new BaseResp();
        BaseResp resp = userInfoInterface.check_level(new BaseReq<>(bean, SysCodeConstant.PAYCENTER));
        if (!"0".equals(resp.getCode())) {//检查等级出错
            res.setCode(resp.getCode());
            res.setDesc(resp.getDesc());
            return res;
        }
        cpUserPayMapper.agentTransfer(bean);
        res.setCode(bean.getBusiErrCode() + "");
        res.setDesc(bean.getBusiErrDesc());
        return res;
    }


}
