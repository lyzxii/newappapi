package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import bean.SafeBean;
import bean.SourceConstant;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.SpringBeanFactoryUtils;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.*;
import com.caiyi.lottery.tradesystem.paycenter.recharge.inter.*;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.AlipayWrapper;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.BankCardWrapper;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.TenpayWrapper;
import com.caiyi.lottery.tradesystem.paycenter.recharge.wrapper.WeiXinWrapper;
import com.caiyi.lottery.tradesystem.paycenter.service.NotifyService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pay.bean.PayBean;
import pay.constant.PayConstant;
import pay.constant.RechargeTypeConstant;
import pay.pojo.BankCard;
import pay.pojo.RechCardPojo;
import pay.pojo.RechDayLimitPojo;
import pay.pojo.UserPayPojo;
import pay.util.PayUtil;
import pojo.UserPojo;
import util.UserErrCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private UserPayMapper userPayMapper;

    @Autowired
    private CpUserPayMapper cpUserPayMapper;

    @Autowired
    private RechCardMapper rechCardMapper;

    @Autowired
    private SafeCenterInterface safeCenterInterface;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RechDayLimitMapper rechLimitMapper;

    @Autowired
    private RechCardChannelMapper rechCardChannelMapper;

    @Autowired
    private RechargeCard_Channel rechargeCard_channel;

    @Autowired
    private UserBasicInfoInterface userBasicInfoInterface;

    @Autowired
    private RechCard_RechCardChannelMapper rechard_rechCardChannelMapper;


    /**
     * 充值成功调用接口
     *
     * @param bean
     */
    @Override
    public void applyAccountSuc(PayBean bean) {
        log.info("支付中心-->处理[" + bean.getClassName() + "]回调开始，applyid==" + bean.getApplyid() + ", uid==" + bean.getUid());
        if (0 != bean.getBusiErrCode()) {
            log.info("支付中心-->处理[" + bean.getClassName() + "]回调上一步错误，applyid==" + bean.getApplyid() + ", uid==" + bean.getUid());
            return;
        }
        try {
            log.info("类[" + bean.getClassName() + "]充值成功调用存储过程：u_addmoneysuc,applyid:{},addmoney:{},bankid:{},dealid{}",
                    bean.getApplyid(), bean.getAddmoney(), bean.getBankid(), bean.getDealid());
            if (bean.getBusiErrCode() == 0) {
                log.info("调用applyAccountSuc 参数,addmoney:" + bean.getAddmoney() + ", applyid:" + bean.getApplyid() + ", bankid" + bean.getBankid() + ",dealid:" + bean.getDealid());
                cpUserPayMapper.addmoneysuc(bean);
                if (bean.getBusiErrCode() != 0) {
                    log.error("u_addmoneysuc 失败，code:{},desc:{}", bean.getBusiErrCode(), bean.getBusiErrDesc());
                    bean.setBusiErrCode(UserErrCode.ERR_CALL_SP);
                    bean.setBusiErrDesc(UserErrCode.getErrDesc(bean.getBusiErrCode()));
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.PAY_RECHARGE_ADDMONEYSUC_ERROR));
            bean.setBusiErrDesc("充值成功调用存储过程异常");
            log.error("调用applyAccountSuc 参数失败,addmoney:" + bean.getAddmoney() + ", applyid:" + bean.getApplyid() + ", bankid" + bean.getBankid() + ",dealid:" + bean.getDealid(), e);
        }

    }

    @Override
    public void updateBankCardInfo(PayBean bean) throws Exception {
        if (bean.getBusiErrCode() != 0) {
            return;
        }
        getApplyidAndCardInfo(bean);//获取订单和银行卡信息
        updateLastRecCache(bean, bean.getBankCode() + bean.getCardtype());
        updateUserDayLimit(bean);//更新用户的每日限额
    }

    //更新用户的每日限额
    private void updateUserDayLimit(PayBean bean) throws Exception {
        if (bean.getProduct() != null && bean.getChannel() != null) {
            String channel = bean.getChannel();
            String product = bean.getProduct();
            String currentDate = DateTimeUtil.getCurrentDate();
            RechDayLimitPojo rechLimit = new RechDayLimitPojo();
            rechLimit.setChannel(channel);
            rechLimit.setProduct(product);
            rechLimit.setStatday(currentDate);
            rechLimit.setCsafekey(MD5Helper.md5Hex(bean.getCardNo()));//获取safekey
            int count = rechLimitMapper.countRechDayLimit(rechLimit);
            rechLimit.setApplyid(bean.getApplyid());
            rechLimit.setApplyids(bean.getApplyid() + ",");
            if (count > 0) {
                rechLimit.setAddmoney(bean.getAddmoney());
                updateRechDayLimit(bean, rechLimit);
            } else {
                rechLimit.setRechargemoney(bean.getAddmoney());
                rechLimit.setBankcode(bean.getBankCode());
                rechLimit.setCardno(bean.getCardNo());
                rechLimit.setCardtype(bean.getCardtype());
                insertRechDayLimit(bean, rechLimit);
            }
        } else {
            log.info("订单号为:" + bean.getApplyid() + " bankid为:" + bean.getBankid() + "的单子未找到对应的channel");
        }
    }

    /**
     * 更新用户每日限额
     */
    private void updateRechDayLimit(PayBean bean, RechDayLimitPojo rechLimit) throws Exception {
        int count = rechLimitMapper.countRechDayLimitWithApplyIds(rechLimit);
        if (count > 0) {
            log.info("订单号:" + bean.getApplyid() + " channel:" + rechLimit.getChannel() + " product:" + rechLimit.getProduct()
                    + "的订单已经加过单日限额,不在重复添加");
        } else {
            int flag = rechLimitMapper.updateRechDayLimit(rechLimit);
            if (flag == 1) {
                log.info("更新用户充值每日限额成功,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid()
                        + " channel:" + rechLimit.getChannel() + " 产品:" + rechLimit.getProduct());
                updateUserDayLimitCache(bean, rechLimit);
            } else {
                log.info("更新用户充值每日限额失败,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid() + " " +
                        "channel:" + rechLimit.getChannel() + " 产品:" + rechLimit.getProduct());
                throw new Exception("更新用户充值每日限额失败");
            }
        }
    }


    /**
     * 插入用户每日限额
     */
    private void insertRechDayLimit(PayBean bean, RechDayLimitPojo rechLimit) throws Exception {
        int flag = rechLimitMapper.insertRechDayLimit(rechLimit);
        if (flag == 1) {
            log.info("插入用户充值每日限额成功,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid()
                    + " channel:" + rechLimit.getChannel() + " 产品:" + rechLimit.getProduct());
            updateUserDayLimitCache(bean, rechLimit);
        } else {
            log.info("插入用户充值每日限额失败,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid()
                    + " channel:" + rechLimit.getChannel() + " 产品:" + rechLimit.getProduct());
            throw new Exception("插入用户充值每日限额失败");
        }
    }

    /**
     * 更新缓存
     */
    private void updateUserDayLimitCache(PayBean bean, RechDayLimitPojo rechLimit) {
        String cacheKey = rechLimit.getStatday() + "_" + rechLimit.getChannel() + "_"//缓存key
                + rechLimit.getProduct() + "_" + bean.getCardNo();
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(cacheKey);
        Map<String, String> userDayLimitMap = (Map<String, String>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
        if (userDayLimitMap == null || StringUtil.isEmpty(userDayLimitMap.get("addmoney"))) {
            String rechmoney = rechLimitMapper.queryRechMoney(rechLimit);
            userDayLimitMap = new HashMap<>();
            userDayLimitMap.put("addmoney", rechmoney);
        } else {
            String addmoney = userDayLimitMap.get("addmoney");
            double rechargeMoney = Double.parseDouble(addmoney) + bean.getAddmoney();
            userDayLimitMap.put("addmoney", rechargeMoney + "");
        }
        putCache(cacheKey, userDayLimitMap);//存入缓存
    }

    private void putCache(String cacheKey, Object obj) {
        CacheBean cacheBeanSet = new CacheBean();
        //放入缓存中
        JSONObject RechargeMapJson = (JSONObject) JSONObject.toJSON(obj);
        // 更新到缓存
        cacheBeanSet.setKey(cacheKey);
        cacheBeanSet.setValue(RechargeMapJson.toJSONString());
        cacheBeanSet.setTime(10*Constants.TIME_MINUTE);//10分钟
        redisClient.setString(cacheBeanSet, log, SysCodeConstant.PAYCENTER);
    }

    //更新最后一次充值缓存
    private void updateLastRecCache(PayBean bean, String cacheKey) throws Exception {
        String chargeBankId = String.valueOf(bean.getBankid());
        if (!StringUtil.isEmpty(bean.getUid())) {
            CacheBean cacheBean = new CacheBean();
            String primaryKey = bean.getUid() + "_RechRecordMap";
            log.info("更新最后一次充值缓存,用户:{},bankcode:{},cardtype:{}", bean.getUid(), bean.getBankCode(), bean.getCardtype());
            String secondKey = bean.getUid() + "_" + cacheKey;
            cacheBean.setKey(primaryKey);
            Map<String, Map<String, String>> fatherRechargeMap = (Map<String, Map<String, String>>) redisClient.getObject(cacheBean, Map.class, log, SysCodeConstant.PAYCENTER);
            if (fatherRechargeMap != null) {
                Map<String, String> userRechargeMap = fatherRechargeMap.get(secondKey);
                if (userRechargeMap == null) {
                    return;
                }
                String cacheBankid = userRechargeMap.get("bankid");
                if (!chargeBankId.equals(cacheBankid)) { //充值回调Key跟缓存key 不匹配，不更新缓存
                    return;
                }
                userRechargeMap.put("flag", "1");
                fatherRechargeMap.put(secondKey, userRechargeMap);
                putCache(primaryKey, fatherRechargeMap);
                log.info("更新充值缓存,key:" + bean.getUid() + "_" + cacheKey + " bankid:" + chargeBankId);
            } else {
                log.info("没有充值路由的相关缓存信息,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid() +
                        " 缓存key:" + bean.getUid() + "_" + cacheKey);
            }
        } else {
            log.info("更新最后一次充值信息失败,用户名为空,用户名:" + bean.getUid() + " 订单号:" + bean.getApplyid() +
                    " 缓存key:" + bean.getUid() + "_" + cacheKey);
        }
    }

    private void getApplyidAndCardInfo(PayBean bean) {
        UserPayPojo userpay = userPayMapper.queryPayInfo(bean.getApplyid());
        if (userpay != null) {
            bean.setUid(userpay.getUid());
            String csafekey = userpay.getSafeKey();//获取safekey
            SafeBean sbean = new SafeBean();
            sbean.setRechargeCardId(csafekey);
            sbean.setNickid(bean.getUid());
            sbean.setUsersource(SourceConstant.CAIPIAO);
            BaseResp<SafeBean> resp = safeCenterInterface.getRechargeCard(new BaseReq<>(sbean, SysCodeConstant.PAYCENTER));
            SafeBean safeBean = resp.getData();
            if ("0".equals(resp.getCode()) && safeBean != null) {
                String bankcard = safeBean.getBankcard();
                bean.setCardNo(bankcard);//可能获取银行卡失败
                List<RechCardPojo> rechCardList = rechard_rechCardChannelMapper.queryBankCardInfo(bean.getUid(), csafekey);
                if (rechCardList != null && rechCardList.size() > 0) {
                    bean.setBankCode(rechCardList.get(0).getBankCode());
                    bean.setCardtype(rechCardList.get(0).getCardtype());
                } else {
                    log.error("更新银行卡信息，获取充值银行卡信息失败,用户名:{},订单号:{}", csafekey, bean.getUid(), bean.getApplyid());
                    bean.setBusiErrCode(-1);
                }
            } else {
                bean.setBusiErrCode(-1);
                log.error("更新银行卡信息，从安全中心根据key:{}获取银行卡号失败,用户名:{},订单号:{}", csafekey, bean.getUid(), bean.getApplyid());
            }
        } else {
            bean.setBusiErrCode(-1);
            log.error("更新银行卡信息,根据订单号:{}没有从tb_user_pay表中获取到相关信息", bean.getApplyid());
        }
    }

    /**
     * @Desc 根据applyid 查询bankid、uid、dealid
     */
    @Override
    public void queryBankId(PayBean bean) {
        try {
            String applyid = bean.getApplyid();
            if (StringUtil.isEmpty(applyid)) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.PAY_RECHARGE_QUERY_BANKID_NULL_APPLYID));
            }
            UserPayPojo userpay = userPayMapper.queryPayInfo(applyid);
            if (userpay != null) {
                String bankid = userpay.getBankid();
                bean.setBankid(bankid);
                String uid = userpay.getUid();
                bean.setUid(uid);
                bean.setDealid(userpay.getConfirmid());
                bean.setMerchantId(userpay.getMerchantId());
                bean.setSafeKey(userpay.getSafeKey());
                bean.setCardNo(userpay.getCardNo());
                bean.setBusiErrCode(0);
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.PAY_RECHARGE_QUERY_BANKID_NO));
                bean.setBusiErrDesc("根据订单号没有查到bankid");
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.PAY_RECHARGE_QUERY_BANKID_ERROR));
            bean.setBusiErrDesc("根据订单号查询bankid异常");
            log.error("根据订单号:{},查询bankid异常", bean.getApplyid(), e);
        }
    }

    /**
     * 更新快捷银行卡协议信息
     */
    @Override
    public void bindCard(PayBean bean) throws Exception {
        log.info("根据订单号添加账户信息：" + bean.getApplyid() + ",channel==" + bean.getChannel());
        preHandle(bean);
        log.info("开始更新充值银行卡信息,用户名号==" + bean.getUid() + ",safeKey==" + bean.getSafeKey());
        if (StringUtil.isEmpty(bean.getSafeKey())) {
            //无新插入记录且没有状态更新
            log.info("更新" + bean.getChannel() + "用户充值协议safeKey为空");
            bean.setBusiErrCode(-1001);
            bean.setBusiErrDesc("safeKey为空");
            return;
        }
        if (queryProtocolStatus(bean) != 1) {//查询协议状态
            log.info("订单号==" + bean.getUid() + "协议未显示");
            //更新用户协议
            int i = rechCardMapper.updateRechCstatus(bean.getUid(), bean.getSafeKey());
            if (i == 1) {
                log.info("记录更新tb_recharge_card用户[" + bean.getUid() + "]支付协议信息成功!");
                //更新tb_recharge_card成功，更新tb_recharge_card_channel
                log.info("dealid==" + bean.getDealid() + ",userbusiid==" + bean.getUserbusiid() + ",userpayid==" + bean.getUserpayid() + ",safeKey==" + bean.getSafeKey() + ",channel==" + bean.getChannel());
                int j = rechCardChannelMapper.updateBindStatus(bean);
                if (j == 1) {
                    log.info("记录更新tb_recharge_card_channel用户[" + bean.getUid() + "]支付协议信息成功!");
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("协议更新成功");
                } else {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    bean.setBusiErrDesc("协议更新失败");
                    log.info("记录更新tb_recharge_card_channel用户[" + bean.getUid() + "]支付协议信息出错!");
                    throw new Exception("记录更新tb_recharge_card_channel支付协议信息失败!");
                }
            } else {
                log.info("记录tb_recharge_card用户[" + bean.getUid() + "]支付协议信息出错!");
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("协议更新失败");
                throw new Exception("记录更新tb_recharge_card支付协议信息失败!");
            }
        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
            bean.setBusiErrDesc("协议处于显示状态");
            log.info("订单号==" + bean.getUid() + "协议处于显示状态");
        }

        if (bean.getBusiErrCode() == 0) {
            log.error("回调银行卡充值成功，回调className:" + bean.getClassName() + "，记录用户充值协议接口调用成功" + bean.getApplyid());
        } else {
            log.error("回调银行卡充值失败，回调className:" + bean.getClassName() + "，记录用户充值协议接口调用成功" + bean.getApplyid());
        }
        bean.setBusiErrCode(0);
        bean.setBusiErrDesc("绑卡成功");
    }

    private void preHandle(PayBean bean) {
        //京东快捷不需要进行签约，用户名称作为用户请求签约号和签约成功号进行存储
        String userbusiid = bean.getUserbusiid();
        String userpayid = bean.getUserpayid();
        if (StringUtil.isEmpty(userbusiid) || StringUtil.isEmpty(userpayid)) {
            queryUserInfoByApplyid(bean);
        }
        if (StringUtil.isEmpty(userbusiid)) {
            bean.setUserbusiid(bean.getCuserId());//用户请求签约号//TODO 12/20
        }
        if (StringUtil.isEmpty(userpayid)) {
            bean.setUserpayid(bean.getCuserId());//用户请求签约号//TODO 12/20
        }
    }


    private int queryProtocolStatus(PayBean bean) {
        //tb_recharge_card 查询status是否为0
        return rechargeCard_channel.queryProtocolStatus(bean.getUid(), bean.getSafeKey(), bean.getChannel());
    }

    private void queryUserInfoByApplyid(PayBean bean) {
        //拿uid去tb_user查询cuserid
        BaseReq<BaseBean> req = new BaseReq<>(SysCodeConstant.PAYCENTER);
        req.setData(bean);
        BaseResp<UserPojo> response = userBasicInfoInterface.queryUserInfo(req);
        UserPojo pojo = response.getData();
        if (pojo != null) {
            bean.setCuserId(pojo.getCuserId());
            log.info("根据订单号查询账户信息成功" + bean.getApplyid() + ",uid==" + pojo.getUid() + "userid==" + pojo.getCuserId());
        } else {
            log.info("根据订单号查询账户信息失败：" + bean.getApplyid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.PAY_RECHARGE_QUERY_USER_ID));
            bean.setBusiErrDesc("根据订单号查询用户信息失败");
        }
    }

    /**
     * 更新微信、支付宝充值信息
     *
     * @param bean
     */
    @Override
    public void updateWXAndZfbPayInfo(PayBean bean) {
        if (bean.getBusiErrCode() != 0) {
            return;
        }
        try {
//			baseService.getCardNoByApplyid(bean);
            updateLastRecCache(bean, bean.getRemark());
        } catch (Exception e) {
            bean.setBusiErrDesc("更新最后一次充值信息失败");
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            log.info("更新微信、支付宝路由充值信息出错：uid==" + bean.getUid() + " 订单号:" + bean.getApplyid(), e);
        }
    }

    //
    @Override
    public void updateRechargeCard(PayBean bean) {
        try {
            log.info("银行卡充值类[" + bean.getClassName() + "]特殊处理开始，bankCode:" + bean.getBankCode() + ",cardType:" + bean.getCardtype());
            BankCard bankCard = null;
            String bankcode = bean.getBankCode();
            if (PayConstant.llBankCard.containsKey(bankcode)) {
                bankCard = PayConstant.llBankCard.get(bankcode);
            } else if (PayConstant.llCardType.containsKey(bean.getBankCode())) {
                bankCard = new BankCard(bean.getBankCode(), PayConstant.llCardType.get(bean.getBankCode()));
            }

            if (bankCard != null) {
                bean.setBankCode(bankCard.getBankCode());
                bean.setBankName(bankCard.getBankName());
                bean.setCardtype(bean.getCardtype());
                bean.setCardName(bean.getCardtype() == 0 ? "借记卡" : "信用卡");
                int i = rechCardMapper.updateRechCard(bean);
                if (1 != i) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    log.error("更新[" + bean.getClassName() + "]银行卡信息失败,用户：{},订单号:{},bankcode:{},bankname:{},cardtype:{},cardname:{}",
                            bean.getUid(), bean.getApplyid(), bean.getBankCode(), bean.getBankName(), bean.getCardtype(), bean.getCardName());
                } else {
                    log.info("更正[" + bean.getClassName() + "]银行卡信息成功,用户：{},订单号:{},bankname:{},cardname:{}",
                            bean.getUid(), bean.getApplyid(), bean.getBankName(), bean.getCardName());
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            log.error("更新[" + bean.getClassName() + "]银行卡信息异常,用户：{},订单号:{},bankcode:{},bankname:{},cardtype:{},cardname:{}",
                    bean.getUid(), bean.getApplyid(), bean.getBankCode(), bean.getBankName(), bean.getCardtype(), bean.getCardName(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void defaultBankCardNotify(PayBean bean) throws Exception {
        if (0 == bean.getBusiErrCode()) {
            log.info("更新用户账户" + bean.getClassName() + ",errorCode=" + bean.getBusiErrCode() + ",product==" + bean.getProduct() + ",channel==" + bean.getChannel());
            bindCard(bean);
            updateBankCardInfo(bean);
        }
    }

    @Override
    public void defaultRechNotify(PayBean bean) {
        log.info("支付中心-->处理[" + bean.getClassName() + "]回调开始，applyid==" + bean.getApplyid() + ", uid==" + bean.getUid());
        try {
            if (0 == bean.getBusiErrCode()) {
                log.info("更新用户账户类" + bean.getClassName() + ",errorCode=" + bean.getBusiErrCode() + ",product==" + bean.getProduct() + ",channel==" + bean.getChannel());
                bean.setRemark(bean.getRechargeType());
                updateWXAndZfbPayInfo(bean);
            } else {
                log.info("威富通支付宝H5充值失败，nickid={},applyid={}", new Object[]{bean.getUid(), bean.getApplyid()});
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("回调出错");
            log.error("类" + bean.getClassName() + "订单号[" + bean.getApplyid() + "]回调出错==", e);
        }
    }


    @Override
    public void basicNotifyService(PayBean bean) {
        log.info("支付中心-->中心处理[" + bean.getClassName() + "]开始，rechargeType:" + bean.getRechargeType() + "applyid==" + bean.getApplyid() + ", uid==" + bean.getUid());
        PayUtil.ReadAccountInfo(bean);//配置文件依靠className读取channel
        if (StringUtil.isEmpty(bean.getBankid())) {
            log.info("支付中心-->[" + bean.getClassName() + "]回调bankid为空，applyid==" + bean.getApplyid() + ", uid==" + bean.getUid());
            queryBankId(bean);
        }

        IRecharge recharge = (IRecharge) SpringBeanFactoryUtils.getBean(bean.getClassName());
        switch (bean.getRechargeType()) {
            case RechargeTypeConstant.RECHARGETYPE_BANKCARD: {//银行卡
                IBankCardRech bankCardRech = (IBankCardRech)recharge;
                BankCardWrapper bankCardWrapper = (BankCardWrapper) SpringBeanFactoryUtils.getBean("BankCardWrapper");
                bankCardWrapper.backNotify(bean,bankCardRech);
                break;
            }
            case RechargeTypeConstant.RECHARGETYPE_ALIPAY: {//支付宝
                IAlipayRech alipayRech = (IAlipayRech)recharge;
                AlipayWrapper alipayWrapper = (AlipayWrapper) SpringBeanFactoryUtils.getBean("AlipayWrapper");
                alipayWrapper.backNotify(bean,alipayRech);
                break;
            }
            case RechargeTypeConstant.RECHARGETYPE_WEIXIN: {//微信
                IWeiXinRech weixinRech = (IWeiXinRech)recharge;
                WeiXinWrapper weixinWrapper = (WeiXinWrapper)SpringBeanFactoryUtils.getBean("WeiXinWrapper");
                weixinWrapper.backNotify(bean,weixinRech);
                break;
            }
            case RechargeTypeConstant.RECHARGETYPE_TENPAY: {//qq支付
                ITenpayRech tenpayRech = (ITenpayRech)recharge;
                TenpayWrapper tenpayWrapper = (TenpayWrapper) SpringBeanFactoryUtils.getBean("TenpayWrapper");
                tenpayWrapper.backNotify(bean,tenpayRech);
                break;
            }
            case RechargeTypeConstant.RECHARGETYPE_OTHER: {//other
                recharge.backNotify(bean);
                break;
            }
            default: {
                log.info("支付中心-->确认失败，中心处理未知充值方式,rechargeType:" + bean.getRechargeType() + "，applyid==" + bean.getApplyid() + ", uid==" + bean.getUid());
            }
        }
    }

}
