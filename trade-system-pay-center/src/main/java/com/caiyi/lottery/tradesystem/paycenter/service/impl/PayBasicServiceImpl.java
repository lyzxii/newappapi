package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.paycenter.dao.UserPayMapper;
import com.caiyi.lottery.tradesystem.paycenter.dao.WebPayMapper;
import com.caiyi.lottery.tradesystem.paycenter.service.PayBasicService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wxy
 * @create 2017-12-27 19:42
 **/

@Slf4j
@Service
public class PayBasicServiceImpl implements PayBasicService {
    @Autowired
    private UserPayMapper userPayMapper;

    @Autowired
    private WebPayMapper webPayMapper;

    /**
     * 查询首次充值是否小于20
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public Integer queryFirstIsLower20(BaseBean bean) throws Exception {
        return userPayMapper.queryFirstIsLower20(bean.getUid());
    }

    @Override
    public Integer queryWhitelistStatus(BaseBean bean) {
        try {
            bean.setBusiErrDesc("查询iOS web支付白名单状态成功");
            return webPayMapper.queryWhiteliststatus(bean.getUid());
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("查询iOS web支付白名单状态失败");
            log.error("查询iOS web支付白名单状态失败,uid:{}",bean.getUid(),e);
        }
        return  null;
    }
}
