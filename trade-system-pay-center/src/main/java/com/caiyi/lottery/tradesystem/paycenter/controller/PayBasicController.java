package com.caiyi.lottery.tradesystem.paycenter.controller;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.paycenter.service.PayBasicService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.bean.PayBean;
import pay.dto.RechDto;

/**
 * @author wxy
 * @create 2017-12-27 19:37
 **/
@Slf4j
@RestController
public class PayBasicController {
    @Autowired
    private PayBasicService payBasicService;

    @Autowired
    private NotifyService notifyService;

    /**
     * 查询首次充值是否小于20
     * @param req
     * @return
     */
    @RequestMapping(value = "/pay/query_first_is_lower20.api")
    BaseResp<RechDto> queryFirstIsLower20(@RequestBody BaseReq<PayBean> req) {
        BaseBean bean = req.getData();
        BaseResp baseResp = new BaseResp();
        try {
            Integer num = payBasicService.queryFirstIsLower20(bean);
            baseResp.setCode(BusiCode.SUCCESS);
            baseResp.setDesc("查询失败");
            baseResp.setData(num);
        } catch (Exception e) {
            log.error("查询首次充值少于20失败，[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }

    @RequestMapping(value = "/pay/query_bankid.api")
    public BaseResp<PayBean> queryBankId(@RequestBody BaseReq<PayBean> req) {
        BaseResp<PayBean> baseResp = new BaseResp<>();
        PayBean bean = req.getData();
        log.info("支付中心-->开始查询bankid,applyid=="+bean.getApplyid());
        notifyService.queryBankId(bean);
        log.info("根据订单号查询支付网关code:" + bean.getBusiErrCode() +",applyid:"+bean.getApplyid());
        if (bean.getBusiErrCode() != 0 || StringUtil.isEmpty(bean.getBankid())) {
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
            return baseResp;
        }
        log.info("支付中心-->bankid查询结束，bankid:"+bean.getBankid()+",uid=="+bean.getUid()+",safeKey=="+bean.getSafeKey());
        baseResp.setCode(BusiCode.SUCCESS);
        baseResp.setDesc("查询成功");
        baseResp.setData(bean);
        return baseResp;
    }

    @RequestMapping(value = "/pay/query_whitelist_status.api")
    BaseResp<Integer> queryWhitelistStatus(@RequestBody BaseReq<BaseBean> req){
        BaseResp<Integer> resp=new BaseResp<>();
        BaseBean bean=req.getData();
        Integer itype=payBasicService.queryWhitelistStatus(bean);
        resp.setCode(bean.getBusiErrCode()+"");
        resp.setDesc(bean.getBusiErrDesc());
        if(itype!=null&&bean.getBusiErrCode()==0){
            resp.setData(itype);
        }
        return resp;
    }
}
