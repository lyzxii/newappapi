package com.caiyi.lottery.tradesystem.usercenter.client;

import bean.AlipayLoginBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.usercenter.clienterror.AllyInterfaceError;
import dto.AlipayLoginDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import response.AlipayLoginResq;

/**
 * 快登
 *
 * @author GJ
 * @create 2017-12-19 18:24
 **/
@FeignClient(name = "tradecenter-system-usercenter-center")
public interface AllyInterface {
    @RequestMapping(value = "/user/alipay_bind.api")
     AlipayLoginResq alipayBind(@RequestBody BaseReq<AlipayLoginBean> baseReq);

    @RequestMapping(value = "/user/alipay_login_check.api")
     AlipayLoginResq alipayLogin(@RequestBody BaseReq<AlipayLoginBean> baseReq);

    @RequestMapping(value = "/user/alipay_authinfo.api")
     AlipayLoginResq getAuthInfo(@RequestBody BaseReq<AlipayLoginBean> baseReq);

    @RequestMapping(value = "/user/alipay_bindmobileno2caiyi.api")
     BaseResp<AlipayLoginDTO> bindmobileno2caiyi(@RequestBody BaseReq<AlipayLoginBean> bean);

    @RequestMapping(value = "/user/alipay_get_caiyi_account.api")
     BaseResp<AlipayLoginDTO> zfbgetcaiyiaccount(@RequestBody BaseReq<AlipayLoginBean> baseReq);

    @RequestMapping(value = "/user/alipay_bind2caiyi.api")
    public BaseResp<AlipayLoginDTO> zfbbind2caiyi(@RequestBody BaseReq<AlipayLoginBean> baseReq);

    @RequestMapping(value = "/user/alipay_upatepwd.api")
    BaseResp Upatepwd(@RequestBody BaseReq<AlipayLoginBean> baseReq);





}
