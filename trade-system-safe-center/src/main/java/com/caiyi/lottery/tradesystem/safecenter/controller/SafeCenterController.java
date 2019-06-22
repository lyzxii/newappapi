package com.caiyi.lottery.tradesystem.safecenter.controller;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.util.AESUtils;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.safecenter.service.*;
import dto.RechargeCardDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据安全中心
 */
@RestController
public class SafeCenterController {

    private Logger logger = LoggerFactory.getLogger(SafeCenterController.class);
    private String key = "l8i9KqIw4AN0gj3ihny7OVnG";

    @Autowired
    private TbUserViceService tbUserViceService;
    @Autowired
    private TbUserCashViceService tbUserCashViceService;
    @Autowired
    private TbSmsViceService tbSmsViceService;
    @Autowired
    private TbUserPayLimitViceService tbUserPayLimitViceService;
    @Autowired
    private TbRechargeCardViceService tbRechargeCardViceService;
    @Autowired
    private TbMobileService tbMobileService;
    @Autowired
    private TbBankCardService tbBankCardService;
    @Autowired
    private TbIdCardService tbIdCardService;
    @Autowired
    private TbRealNameService tbRealNameService;

    @RequestMapping(value = "/safe_center/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("安全中心safe-center启动运行正常");
        return response;
    }
    /**
     * 存储用户表加密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/addUserTable.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addUserTable(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String realname = bean.getRealname(); // 真实姓名
        String idcard = bean.getIdcard(); // 身份证号码
        String mobileno = bean.getMobileno(); // 手机号码
        String bankcard = bean.getBankcard(); // 银行卡号码
        String cardmobile = bean.getCardmobile(); //提款银行卡对应的手机号
        String usersource = bean.getUsersource();// 用户来源
        String cnickid = bean.getNickid(); // 用户别名号码
        logger.info("realname=" + realname + " idcard=" + idcard + " mobileno=" + mobileno + " bankcard=" + bankcard + " cardmobile=" + cardmobile + " usersource=" + usersource);
        try {
            if(!StringUtil.isEmpty(usersource) && !StringUtil.isEmpty(cnickid)){
                String crealname = !StringUtil.isEmpty(realname) ? AESUtils.aesEncode(key,realname) : null;
                String cidcard = !StringUtil.isEmpty(idcard) ? AESUtils.aesEncode(key,idcard) : null;
                String cmobileno = !StringUtil.isEmpty(mobileno) ? AESUtils.aesEncode(key,mobileno) : null;
                String cbankcard = !StringUtil.isEmpty(bankcard) ? AESUtils.aesEncode(key,bankcard) : null;
                String ccardmobile = !StringUtil.isEmpty(cardmobile) ? AESUtils.aesEncode(key,cardmobile) : null;
                int count = tbUserViceService.addUserVice(crealname, cidcard,cmobileno, cbankcard ,ccardmobile , usersource,cnickid);
                if(count > 0){
                    bean = new SafeBean();
                    bean.setNickid(cnickid);
                    bean.setUsersource(usersource);
                    if(cmobileno != null) {
                        String md5mobileno = MD5Helper.md5Hex(mobileno);
                        SafeBean safeBean1 = tbMobileService.addMobileVice(cmobileno, md5mobileno);
                        if (safeBean1 != null) {
                            bean.setMobileId(safeBean1.getMobileId());
                        }
                    }
                    if(cbankcard != null) {
                        String md5bankcard = MD5Helper.md5Hex(bankcard);
                        SafeBean safeBean2 = tbBankCardService.addBankCard(cbankcard, md5bankcard);
                        if (safeBean2 != null) {
                            bean.setBankcardId(safeBean2.getBankcardId());
                        }
                    }
                    if(cidcard != null) {
                        String md5idcard = MD5Helper.md5Hex(idcard);
                        SafeBean safeBean3 = tbIdCardService.addIdCard(cidcard, md5idcard);
                        if (safeBean3 != null) {
                            bean.setIdCardId(safeBean3.getIdCardId());
                        }
                    }
                    if(crealname != null) {
                        String md5realname = MD5Helper.md5Hex(realname);
                        SafeBean safeBean4 = tbRealNameService.addRealName(crealname, md5realname);
                        if (safeBean4 != null) {
                            bean.setRealnameId(safeBean4.getRealnameId());
                        }
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据保存成功");
                    result.setData(bean);
                    logger.info("数据保存成功 cnickid=" + cnickid );
                } else {
                    result.setCode(BusiCode.FAIL);
                    result.setDesc("数据保存失败");
                    logger.debug("数据保存失败 cnickid=" + cnickid );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("用户表存储失败");
            logger.info("用户表存储失败", e);
        }
        return result;
    }

    /**
     * 获取用户表解密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getUserTable.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getUserTable(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String cnickid = bean.getNickid(); // 用户别名号码
        String usersource = bean.getUsersource();// 用户来源
        try {
           if(!StringUtil.isEmpty(cnickid) && !StringUtil.isEmpty(usersource)){
                // 获取
                List<SafeBean> list = tbUserViceService.queryUserViceByCnickid(cnickid, usersource);
                if(list != null && list.size() > 0){
                    bean = new SafeBean();
                    String realname = list.get(0).getRealname();
                    String idcard = list.get(0).getIdcard();
                    String mobileno = list.get(0).getMobileno();
                    String bankcard = list.get(0).getBankcard();
                    String cardmobile = list.get(0).getCardmobile();
                    bean.setRealname(!StringUtil.isEmpty(realname) ? AESUtils.aesDncode(key,realname) : "");
                    bean.setIdcard(!StringUtil.isEmpty(idcard) ? AESUtils.aesDncode(key,idcard) : "");
                    bean.setMobileno(!StringUtil.isEmpty(mobileno) ? AESUtils.aesDncode(key,mobileno) : "");
                    bean.setBankcard(!StringUtil.isEmpty(bankcard) ? AESUtils.aesDncode(key,bankcard) : "");
                    bean.setCardmobile(!StringUtil.isEmpty(cardmobile) ? AESUtils.aesDncode(key,cardmobile) : "");
                    bean.setUsersource(list.get(0).getUsersource());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(bean);
                    logger.info("查询成功 cnickid=" + cnickid );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
               result.setCode(BusiCode.FAIL);
               result.setDesc("有用参数不得为空");
               logger.debug("有用参数不得为空" );
           }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("用户表查询失败");
            logger.info("用户表查询失败", e);
        }
        return result;
    }

    /**
     * 通过手机号码获取所有用户名
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getAllUserInfo.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> getAllUserInfo(@RequestBody BaseReq<SafeBean> req){
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        List<SafeBean> listResult;
        String mobileno = bean.getMobileno(); // 手机号码
        String usersource = bean.getUsersource();// 用户来源
        logger.info("mobileno=" + mobileno );
        try{
            if(!StringUtil.isEmpty(mobileno) && !StringUtil.isEmpty(usersource)){
                mobileno = AESUtils.aesEncode(key,mobileno);
                List<SafeBean> list = tbUserViceService.queryUserInfoByMobileno(mobileno, usersource);
                if(list != null && list.size() > 0){
                    listResult = new ArrayList<>();
                    for(SafeBean tmp : list) {
                        bean = new SafeBean();
                        bean.setNickid(tmp.getNickid());
                        bean.setUsersource(tmp.getUsersource());
                        listResult.add(bean);
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(listResult);
                    logger.info("查询成功 mobileno=" + mobileno );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据查询失败");
            logger.info("数据查询失败", e);
        }
        return result;
    }

    /**
     * 通过身份证号获取所有用户名
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getAllUserInfoByIdcard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> getAllUserInfoByIdcard(@RequestBody BaseReq<SafeBean> req){
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        List<SafeBean> listResult;
        String idcard = bean.getIdcard(); // 用户身份证号
        String usersource = bean.getUsersource();// 用户来源
        logger.info("idcard=" + idcard );
        try{
            if(!StringUtil.isEmpty(idcard) && !StringUtil.isEmpty(usersource)){
                idcard = AESUtils.aesEncode(key,idcard);
                List<SafeBean> list = tbUserViceService.queryUserInfoByIdcard(idcard, usersource);
                if(list != null && list.size() > 0){
                    listResult = new ArrayList<>();
                    for(SafeBean tmp : list) {
                        bean = new SafeBean();
                        bean.setNickid(tmp.getNickid());
                        bean.setUsersource(tmp.getUsersource());
                        listResult.add(bean);
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(listResult);
                    logger.info("查询成功 idcard=" + idcard );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据查询失败");
            logger.info("数据查询失败", e);
        }
        return result;
    }

    /**
     * 存储提款表加密数据
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/addUserCash.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addUserCash(@RequestBody BaseReq<SafeBean> req){
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String realname = bean.getRealname(); // 真实姓名
        String bankcard = bean.getBankcard(); // 银行卡号码
        String usersource = bean.getUsersource();// 用户来源
        String icashid = bean.getCashid(); // 提现记录编号
        logger.info("realname=" + realname + " bankcard=" + bankcard + " usersource=" + usersource + " icashid=" + icashid );
        try {
            if(!StringUtil.isEmpty(realname) && !StringUtil.isEmpty(bankcard) && !StringUtil.isEmpty(icashid) && !StringUtil.isEmpty(usersource)){
                // 存储
                String crealname = AESUtils.aesEncode(key,realname);
                String cbankcard = AESUtils.aesEncode(key,bankcard);
                int count = tbUserCashViceService.addUserCashVice(crealname, cbankcard , usersource,icashid);
                if(count == 1){
                    bean = new SafeBean();
                    bean.setCashid(icashid);
                    String md5bankcard = MD5Helper.md5Hex(bankcard);
                    SafeBean safeBean = tbBankCardService.addBankCard(cbankcard, md5bankcard);
                    if(safeBean != null){
                        bean.setBankcardId(safeBean.getBankcardId());
                    }
                    String md5realname = MD5Helper.md5Hex(realname);
                    SafeBean safeBean1 = tbRealNameService.addRealName(crealname, md5realname);
                    if(safeBean1 != null){
                        bean.setRealnameId(safeBean.getRealnameId());
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据保存成功");
                    result.setData(bean);
                    logger.info("数据保存成功 icashid=" + icashid );
                } else {
                    result.setCode(BusiCode.FAIL);
                    result.setDesc("数据保存失败");
                    logger.debug("数据保存失败 icashid=" + icashid );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据存储失败");
            logger.info("数据存储失败", e);
        }
        return result;
    }

    /**
     * 获取提款表解密数据
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getUserCash.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getUserCash(@RequestBody BaseReq<SafeBean> req){
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String icashid = bean.getCashid(); // 提现记录编号
        String usersource = bean.getUsersource();// 用户来源
        try {
            if(!StringUtil.isEmpty(icashid) && !StringUtil.isEmpty(usersource)){
                // 获取
                List<SafeBean> list = tbUserCashViceService.queryUserCashViceByIcashid(icashid, usersource);
                if(list != null && list.size() > 0){
                    bean = new SafeBean();
                    bean.setRealname(AESUtils.aesDncode(key,list.get(0).getRealname()));
                    bean.setBankcard(AESUtils.aesDncode(key,list.get(0).getBankcard()));
                    bean.setUsersource(list.get(0).getUsersource());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(bean);
                    logger.info("查询成功 icashid=" + icashid );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("提款表存储失败");
            logger.info("提款表存储失败", e);
        }
        return result;
    }

    /**
     * 存储短信表加密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/addSms.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addSms(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String mobileno = bean.getMobileno(); // 手机号码
        String usersource = bean.getUsersource(); // 用户来源
        String ismsid = bean.getSmsid(); // 提现记录编号
        logger.info("mobileno=" + mobileno +"usersource=" + usersource + " ismsid=" + ismsid);
        try {
            if(!StringUtil.isEmpty(mobileno) && !StringUtil.isEmpty(ismsid) && !StringUtil.isEmpty(usersource)){
                // 存储
                String cmobileno = AESUtils.aesEncode(key,mobileno);
                int count = tbSmsViceService.addSmsVice(cmobileno,usersource,ismsid);
                if(count == 1){
                    bean = new SafeBean();
                    bean.setSmsid(ismsid);
                    String md5mobileno = MD5Helper.md5Hex(mobileno);
                    SafeBean safeBean = tbMobileService.addMobileVice(cmobileno, md5mobileno);
                    if(safeBean != null){
                        bean.setMobileId(safeBean.getMobileId());
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据保存成功");
                    result.setData(bean);
                    logger.info("数据保存成功 ismsid=" + ismsid );
                } else {
                    result.setCode(BusiCode.FAIL);
                    result.setDesc("数据保存失败");
                    logger.debug("数据保存失败 ismsid=" + ismsid );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据存储失败");
            logger.info("数据存储失败", e);
        }
        return result;
    }

    /**
     * 获取短信表解密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getSms.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getSms(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String ismsid = bean.getSmsid(); // 提现记录编号
        String usersource = bean.getUsersource(); // 用户来源
        try {
            if(!StringUtil.isEmpty(ismsid) && !StringUtil.isEmpty(usersource)){
                // 获取
                List<SafeBean> list = tbSmsViceService.querySmsViceByIsmsid(ismsid, usersource);
                if(list != null && list.size() > 0){
                    bean = new SafeBean();
                    bean.setMobileno(AESUtils.aesDncode(key,list.get(0).getMobileno()));
                    bean.setUsersource(list.get(0).getUsersource());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(bean);
                    logger.info("查询成功 ismsid=" + ismsid );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }


    /**
     * 存储充值银行卡表加密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/addUserPayLimit.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addUserPayLimit(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String bankcard = bean.getBankcard(); // 银行卡号码
        String usersource = bean.getUsersource(); // 用户来源
        String cid = bean.getCid(); // 充值银行卡编号
        logger.info("bankcard=" + bankcard + "usersource=" + usersource + " cid=" + cid);
        try {
            if(!StringUtil.isEmpty(bankcard) && !StringUtil.isEmpty(cid) && !StringUtil.isEmpty(usersource)){
                String cbankcard = AESUtils.aesEncode(key,bankcard);
                int count = tbUserPayLimitViceService.addUserPayLimitVice(cbankcard, usersource, cid);
                if(count == 1){
                    bean = new SafeBean();
                    bean.setCid(cid);
                    String md5bankcard = MD5Helper.md5Hex(bankcard);
                    SafeBean safeBean = tbBankCardService.addBankCard(cbankcard, md5bankcard);
                    if(safeBean != null){
                        bean.setBankcardId(safeBean.getBankcardId());
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据保存成功");
                    result.setData(bean);
                    logger.info("数据保存成功 cid=" + cid );
                } else {
                    result.setCode(BusiCode.FAIL);
                    result.setDesc("数据保存失败");
                    logger.debug("数据保存失败 cid=" + cid );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据存储失败");
            logger.info("数据存储失败", e);
        }
        return result;
    }

    /**
     * 获取充值银行卡表解密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getUserPayLimit.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getUserPayLimit(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String cid = bean.getCid(); // 充值银行卡编号
        String usersource = bean.getUsersource(); // 用户来源
        try {
            if(!StringUtil.isEmpty(cid) && !StringUtil.isEmpty(usersource)){
                // 获取
                List<SafeBean> list = tbUserPayLimitViceService.queryUserPayLimitViceByCid(cid, usersource);
                if(list != null && list.size() > 0){
                    bean = new SafeBean();
                    bean.setBankcard(AESUtils.aesDncode(key,list.get(0).getBankcard()));
                    bean.setUsersource(list.get(0).getUsersource());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(bean);
                    logger.info("查询成功 cid=" + cid );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 存储用户支付协议表加密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/addRechargeCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> addRechargeCard(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String mobileno = bean.getMobileno(); // 手机号码
        String bankcard = bean.getBankcard(); // 银行卡号码
        String usersource = bean.getUsersource(); // 用户来源
        String cnickid = bean.getNickid(); // 用户名称
        logger.info("mobileno=" + mobileno + "bankcard=" + bankcard + "usersource=" + usersource + " cnickid=" + cnickid);
        try {
            if(!StringUtil.isEmpty(bankcard)  && !StringUtil.isEmpty(cnickid)   && !StringUtil.isEmpty(usersource)){
                String md5mobileno = MD5Helper.md5Hex(mobileno);
                String md5bankcard = MD5Helper.md5Hex(bankcard);
                //String cmobileno = AESUtils.aesEncode(key,mobileno);
                String cmobileno = !StringUtil.isEmpty(mobileno) ? AESUtils.aesEncode(key,mobileno) : null;
                String cbankcard = AESUtils.aesEncode(key,bankcard);
                int count = tbRechargeCardViceService.addRechargeCardVice(cmobileno,cbankcard, md5bankcard,usersource,cnickid);
                if(count == 1){
                    bean = new SafeBean();
                    String sequence = tbRechargeCardViceService.getRechargeCardSequence(cnickid, usersource,cbankcard);
                    bean.setRechargeCardId(sequence);
                    if(cmobileno != null) {
                        SafeBean safeBean1 = tbMobileService.addMobileVice(cmobileno, md5mobileno);
                        if (safeBean1 != null) {
                            bean.setMobileId(safeBean1.getMobileId());
                        }
                    }
                    if(cbankcard != null) {
                        SafeBean safeBean2 = tbBankCardService.addBankCard(cbankcard, md5bankcard);
                        if (safeBean2 != null) {
                            bean.setBankcardId(safeBean2.getBankcardId());
                        }
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据保存成功");
                    result.setData(bean);
                    logger.info("数据保存成功 cnickid=" + cnickid );
                } else {
                    result.setCode(BusiCode.FAIL);
                    result.setDesc("数据保存失败");
                    logger.debug("数据保存失败 cnickid=" + cnickid );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据存储失败");
            logger.info("数据存储失败", e);
        }
        return result;
    }

    /**
     * 获取用户支付协议表解密数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getRechargeCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> getRechargeCard(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String pid = bean.getRechargeCardId(); // 充值银行卡副表序列号
        String cnickid = bean.getNickid(); // 用户名称
        String usersource = bean.getUsersource(); // 用户来源
        try {
            if(!StringUtil.isEmpty(pid) && !StringUtil.isEmpty(cnickid) && !StringUtil.isEmpty(usersource)){
                List<SafeBean> list = tbRechargeCardViceService.queryRechargeCardViceByPid(pid, cnickid, usersource);
                if(list != null && list.size() > 0){
                    bean = new SafeBean();
                    bean.setNickid(list.get(0).getNickid());
                    bean.setUsersource(list.get(0).getUsersource());
                    bean.setMobileno(AESUtils.aesDncode(key,list.get(0).getMobileno()));
                    bean.setBankcard(AESUtils.aesDncode(key,list.get(0).getBankcard()));
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(bean);
                    logger.info("查询成功 pid=" + pid );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 获取用户支付协议表表全部银行卡号
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/getRechargeAllBankcard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> getRechargeAllBankcard(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        List<SafeBean> listResult ;
        String nickid = bean.getNickid(); // 用户名称
        String usersource = bean.getUsersource(); // 用户来源
        try {
            if(!StringUtil.isEmpty(nickid) && !StringUtil.isEmpty(usersource)){
                // 获取
                List<SafeBean> list = tbRechargeCardViceService.queryRechargeCardViceByNickid(nickid, usersource);
                if(list != null && list.size() > 0){
                    listResult = new ArrayList<>();
                    for(SafeBean tmp : list) {
                        bean = new SafeBean();
                        bean.setNickid(tmp.getNickid());
                        bean.setUsersource(tmp.getUsersource());
                        bean.setRechargeCardId(tmp.getRechargeCardId());
                        bean.setMobileno(AESUtils.aesDncode(key, tmp.getMobileno()));
                        bean.setBankcard(AESUtils.aesDncode(key, tmp.getBankcard()));
                        bean.setRechargeCardId(tmp.getRechargeCardId());
                        listResult.add(bean);
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(listResult);
                    logger.info("查询成功 nickid=" + nickid );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 查询充值银行卡副表信息
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/queryRechargeCardInfo.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> queryRechargeCardInfo(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        SafeBean resultBean = new SafeBean();
        String nickid = bean.getNickid(); // 用户名称
        String usersource = bean.getUsersource(); // 用户来源
        String bankcard = bean.getBankcard();// 用户银行卡号
        try {
            if(!StringUtil.isEmpty(nickid) && !StringUtil.isEmpty(usersource) && !StringUtil.isEmpty(bankcard)){
                bankcard = AESUtils.aesEncode(key,bankcard);
                List<SafeBean> list = tbRechargeCardViceService.queryRechargeCardInfo(nickid, usersource, bankcard);
                //logger.info("list.size = " + list.size());
                if(list != null && list.size() > 0){
                    SafeBean tmp = list.get(0);
                    resultBean.setNickid(tmp.getNickid());
                    resultBean.setUsersource(tmp.getUsersource());
                    resultBean.setRechargeCardId(tmp.getRechargeCardId());
                    resultBean.setMobileno(AESUtils.aesDncode(key, tmp.getMobileno()));
                    resultBean.setBankcard(AESUtils.aesDncode(key, tmp.getBankcard()));
                    resultBean.setRechargeCardId(tmp.getRechargeCardId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(resultBean);
                    logger.info("查询成功 nickid=" + nickid + ", usersource=" + usersource + " bankcard=" + bankcard);
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 获取指定用户支付协议表信息
     * @return
     */
    @RequestMapping(value = "/safe_center/queryRechargeByRechargeId.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<List<SafeBean>> queryRechargeByRechargeId(@RequestBody BaseReq<RechargeCardDTO> req) {
    //public BaseResp<List<SafeBean>> queryRechargeByRechargeId(RechargeCardDTO bean) {
        BaseResp result = new BaseResp();
        RechargeCardDTO bean = req.getData();
        List<SafeBean> resultList;
        String nickid = bean.getNickid(); // 用户名称
        String usersource = bean.getUsersource(); // 用户来源
        List<String> conditionList = bean.getRechargeList();
        /*List<String> conditionList = new ArrayList<>();
        conditionList.add("12");
        conditionList.add("13");
        conditionList.add("14");*/
        try {
            if(!StringUtil.isEmpty(nickid) && !StringUtil.isEmpty(usersource) && conditionList != null && conditionList.size() > 0){
                List<SafeBean> list = tbRechargeCardViceService.queryRechargeByRechargeId(nickid, usersource, conditionList);
                if(list != null && list.size() > 0){
                    resultList = new ArrayList<>();
                    for(SafeBean tmp : list) {
                        SafeBean resultBean = new SafeBean();
                        resultBean.setNickid(tmp.getNickid());
                        resultBean.setUsersource(tmp.getUsersource());
                        resultBean.setMobileno(AESUtils.aesDncode(key, tmp.getMobileno()));
                        resultBean.setBankcard(AESUtils.aesDncode(key, tmp.getBankcard()));
                        resultBean.setRechargeCardId(tmp.getRechargeCardId());
                        resultList.add(resultBean);
                    }
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("查询成功");
                    result.setData(resultList);
                    logger.info("查询成功 nickid=" + nickid + ", usersource=" + usersource );
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据 list.size()=" + list.size() );
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 存取手机号码数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/mobileNo.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> mobileNo(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String cmobileno = bean.getMobileno(); // 手机号
        String mid = bean.getMobileId(); // 序列号
        try {
            if(!StringUtil.isEmpty(cmobileno)){
                String md5mobileno = MD5Helper.md5Hex(cmobileno);
                cmobileno = AESUtils.aesEncode(key,cmobileno);
                SafeBean safeBean = tbMobileService.addMobileVice(cmobileno, md5mobileno);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setMobileId(safeBean.getMobileId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else if (!StringUtil.isEmpty(mid)){
                SafeBean safeBean = tbMobileService.getMobileVice(mid);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setMobileno(AESUtils.aesDncode(key,safeBean.getMobileno()));
                    //bean.setMobileId(safeBean.getMobileId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 存取银行卡号码数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/bankCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> bankCard(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String cbankcard = bean.getBankcard(); // 手机号
        String bid = bean.getBankcardId(); // 序列号
        try {
            if(!StringUtil.isEmpty(cbankcard)){
                String md5bankcard = MD5Helper.md5Hex(cbankcard);
                cbankcard = AESUtils.aesEncode(key,cbankcard);
                SafeBean safeBean = tbBankCardService.addBankCard(cbankcard, md5bankcard);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setBankcardId(safeBean.getBankcardId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else if (!StringUtil.isEmpty(bid)){
                SafeBean safeBean = tbBankCardService.getBankCard(bid);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setBankcard(AESUtils.aesDncode(key,safeBean.getBankcard()));
                    //bean.setBankcardId(safeBean.getBankcardId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 存取身份证号数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/idCard.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> idCard(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String idcard = bean.getIdcard();// 身份证号
        String cid = bean.getIdCardId();// 序列号
        try {
            if(!StringUtil.isEmpty(idcard)){
                String md5idcard = MD5Helper.md5Hex(idcard);
                idcard = AESUtils.aesEncode(key,idcard);
                SafeBean safeBean = tbIdCardService.addIdCard(idcard, md5idcard);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setIdCardId(safeBean.getIdCardId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else if (!StringUtil.isEmpty(cid)){
                SafeBean safeBean = tbIdCardService.getIdCard(cid);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setIdcard(AESUtils.aesDncode(key,safeBean.getIdcard()));
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    /**
     * 存取真实姓名数据
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/safe_center/realname.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<SafeBean> realname(@RequestBody BaseReq<SafeBean> req) {
        BaseResp result = new BaseResp();
        SafeBean bean = req.getData();
        String realname = bean.getRealname();// 真实姓名
        String rid = bean.getRealnameId(); // 序列号
        try {
            if(!StringUtil.isEmpty(realname)){
                String md5realname = MD5Helper.md5Hex(realname);
                realname = AESUtils.aesEncode(key,realname);
                SafeBean safeBean = tbRealNameService.addRealName(realname, md5realname);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setRealnameId(safeBean.getRealnameId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else if (!StringUtil.isEmpty(rid)){
                SafeBean safeBean = tbRealNameService.getRealName(rid);
                if(safeBean != null ){
                    bean = new SafeBean();
                    bean.setRealname(AESUtils.aesDncode(key,safeBean.getRealname()));
                    //bean.setBankcardId(safeBean.getBankcardId());
                    result.setCode(BusiCode.SUCCESS);
                    result.setDesc("数据操作成功");
                    result.setData(bean);
                    logger.info("数据操作成功");
                } else {
                    result.setCode(BusiCode.NOT_EXIST);
                    result.setDesc("没有查到数据");
                    logger.debug("没有查到数据");
                }
            } else {
                result.setCode(BusiCode.FAIL);
                result.setDesc("有用参数不得为空");
                logger.debug("有用参数不得为空" );
            }
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("数据获取失败");
            logger.info("数据获取失败", e);
        }
        return result;
    }

    public static void main(String[] args){
        String str = "6214831210407778";
        System.out.println(MD5Helper.md5Hex(str));
    }

}
