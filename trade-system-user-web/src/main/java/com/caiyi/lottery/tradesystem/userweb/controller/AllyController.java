package com.caiyi.lottery.tradesystem.userweb.controller;

import bean.AlipayLoginBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.usercenter.client.AllyInterface;
import dto.AlipayLoginDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import response.AlipayLoginResq;

/**
 * 快登
 *
 * @author GJ
 * @create 2017-12-19 18:27
 **/
@Slf4j
@RestController
public class AllyController {
    @Autowired
    private AllyInterface allyInterface;

    @RequestMapping(value = "/user/alipay_bind.api", method = RequestMethod.POST)
    public Result alipayBind( AlipayLoginBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        AlipayLoginResq alipayLoginResq = allyInterface.alipayBind(baseReq);
        Result result = new Result();
        result.setCode(alipayLoginResq.getCode());
        result.setDesc(alipayLoginResq.getDesc());
        result.setData(alipayLoginResq.getData());
        return  result;

    }

    @RequestMapping(value = "/user/alipay_login_check.api", method = RequestMethod.POST)
    public Result alipayLogin(AlipayLoginBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        AlipayLoginResq alipayLoginResq = allyInterface.alipayLogin(baseReq);
        Result result = new Result();
        result.setCode(alipayLoginResq.getCode());
        result.setDesc(alipayLoginResq.getDesc());
        result.setData(alipayLoginResq.getData());
        return  result;
    }

    @RequestMapping(value = "/user/alipay_authinfo.api", method = RequestMethod.POST)
    public Result getAuthInfo(AlipayLoginBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        AlipayLoginResq alipayLoginResq = allyInterface.getAuthInfo(baseReq);
        Result result = new Result();
        result.setCode(alipayLoginResq.getCode());
        result.setDesc(alipayLoginResq.getDesc());
        result.setData(alipayLoginResq.getData());
        return  result;
    }

    @RequestMapping(value = "/user/alipay_bindmobileno2caiyi.api", method = RequestMethod.POST)
    public Result bindmobileno2caiyi(AlipayLoginBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp<AlipayLoginDTO>  alipayLoginResq = allyInterface.bindmobileno2caiyi(baseReq);
        Result result = new Result();
        result.setCode(alipayLoginResq.getCode());
        result.setDesc(alipayLoginResq.getDesc());
        result.setData(alipayLoginResq.getData());
        return  result;
    }

    @RequestMapping(value = "/user/alipay_get_caiyi_account.api", method = RequestMethod.POST)
    public Result zfbgetcaiyiaccount(AlipayLoginBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp<AlipayLoginDTO>  alipayLoginResq = allyInterface.zfbgetcaiyiaccount(baseReq);
        Result result = new Result();
        result.setCode(alipayLoginResq.getCode());
        result.setDesc(alipayLoginResq.getDesc());
        result.setData(alipayLoginResq.getData());
        return  result;
    }

    @RequestMapping(value = "/user/alipay_bind2caiyi.api", method = RequestMethod.POST)
    public Result zfbbind2caiyi(AlipayLoginBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp<AlipayLoginDTO>  alipayLoginResq = allyInterface.zfbbind2caiyi(baseReq);
        Result result = new Result();
        result.setCode(alipayLoginResq.getCode());
        result.setDesc(alipayLoginResq.getDesc());
        result.setData(alipayLoginResq.getData());
        return  result;
    }

    @RequestMapping(value = "/user/alipay_upatepwd.api", method = RequestMethod.POST)
    public Result Upatepwd(AlipayLoginBean bean){
        BaseReq baseReq = new BaseReq(SysCodeConstant.USERWEB);
        baseReq.setData(bean);
        BaseResp resp = allyInterface.Upatepwd(baseReq);
        Result result = new Result();
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        return result;
    }

}
