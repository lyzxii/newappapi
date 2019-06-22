package com.caiyi.lottery.tradesystem.safecenter.client;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import dto.RechargeCardDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 数据安全中心客户端接口
 */
@FeignClient(name = "tradecenter-system-safecenter-center")
public interface SafeCenterInterface {

    /**
     * 存储用户表加密数据
     */
    @RequestMapping(value = "/safe_center/addUserTable.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addUserTable(@RequestBody BaseReq<SafeBean> req);

    /**
     * 获取用户表解密数据
     */
    @RequestMapping(value = "/safe_center/getUserTable.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getUserTable(@RequestBody BaseReq<SafeBean> req);

    /**
     * 通过手机号码获取所有用户名
     * @return
     */
    @RequestMapping(value = "/safe_center/getAllUserInfo.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> getAllUserInfo(@RequestBody BaseReq<SafeBean> req);

    /**
     * 通过身份证号获取所有用户名
     * @return
     */
    @RequestMapping(value = "/safe_center/getAllUserInfoByIdcard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> getAllUserInfoByIdcard(@RequestBody BaseReq<SafeBean> req);

    /**
     * 存储提款表加密数据
     */
    @RequestMapping(value = "/safe_center/addUserCash.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addUserCash(@RequestBody BaseReq<SafeBean> req);

    /**
     * 获取提款表解密数据
     */
    @RequestMapping(value = "/safe_center/getUserCash.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getUserCash(@RequestBody BaseReq<SafeBean> req);


    /**
     * 存储短信表加密数据
     */
    @RequestMapping(value = "/safe_center/addSms.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addSms(@RequestBody BaseReq<SafeBean> req);

    /**
     * 获取短信表解密数据
     */
    @RequestMapping(value = "/safe_center/getSms.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getSms(@RequestBody BaseReq<SafeBean> req);

    /**
     * 存储充值银行卡表加密数据
     */
    @RequestMapping(value = "/safe_center/addUserPayLimit.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addUserPayLimit(@RequestBody BaseReq<SafeBean> req);

    /**
     * 获取充值银行卡表解密数据
     */
    @RequestMapping(value = "/safe_center/getUserPayLimit.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getUserPayLimit(@RequestBody BaseReq<SafeBean> req);

    /**
     * 存储用户支付协议表加密数据
     */
    @RequestMapping(value = "/safe_center/addRechargeCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addRechargeCard(@RequestBody BaseReq<SafeBean> req);

    /**
     * 获取用户支付协议表解密数据
     */
    @RequestMapping(value = "/safe_center/getRechargeCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getRechargeCard(@RequestBody BaseReq<SafeBean> req);

    /**
     * 获取用户支付协议表全部银行卡号
     * @return
     */
    @RequestMapping(value = "/safe_center/getRechargeAllBankcard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> getRechargeAllBankcard(@RequestBody BaseReq<SafeBean> req) ;

    /**
     * 查询充值银行卡副表信息
     * @return
     */
    @RequestMapping(value = "/safe_center/queryRechargeCardInfo.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> queryRechargeCardInfo(@RequestBody BaseReq<SafeBean> req) ;

    /**
     * 获取指定用户支付协议表信息
     * @return
     */
    @RequestMapping(value = "/safe_center/queryRechargeByRechargeId.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> queryRechargeByRechargeId(@RequestBody BaseReq<RechargeCardDTO> req) ;

    /**
     * 存取手机号码数据
     */
    @RequestMapping(value = "/safe_center/mobileNo.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> mobileNo(@RequestBody BaseReq<SafeBean> req);

    /**
     * 存取银行卡号码数据
     */
    @RequestMapping(value = "/safe_center/bankCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> bankCard(@RequestBody BaseReq<SafeBean> req);

    /**
     * 存取身份证号数据
     */
    @RequestMapping(value = "/safe_center/idCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> idCard(@RequestBody BaseReq<SafeBean> req);

    /**
     * 存取真实姓名数据
     */
    @RequestMapping(value = "/safe_center/realname.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> realname(@RequestBody BaseReq<SafeBean> req);
}
