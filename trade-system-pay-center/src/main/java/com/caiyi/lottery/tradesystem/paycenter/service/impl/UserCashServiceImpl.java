package com.caiyi.lottery.tradesystem.paycenter.service.impl;

import bean.SafeBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.dao.*;
import com.caiyi.lottery.tradesystem.paycenter.service.UserLogService;
import com.caiyi.lottery.tradesystem.paycenter.utils.DrawingsTimeUtil;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.util.*;
import com.netflix.discovery.converters.Auto;
import constant.UserConstants;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pay.bean.PayBean;
import pay.pojo.*;
import pojo.UserPojo;
import util.UserErrCode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by XQH on 2017/12/26.
 */
@Slf4j
@Service
public class UserCashServiceImpl implements UserLogService {
    @Autowired
    private UserLogMapper userLogMapper;
    @Autowired
    private UserCashMapper userCashMapper;
    @Autowired
    private UserAcctMapper userAcctMapper;
    @Autowired
    private UmpayProtocalMapper umpayProtocalMapper;
    @Autowired
    private BankCardSupportChannMapper bankCardSupportChannMapper;
    @Autowired
    private RechCardMapper rechCardMapper;
    @Autowired
    private SafeCenterInterface safeCenterInterface;

    private final static double TAKEMONEY_POUNDAGE = 0d; //提款手续费

    /**
     * 添加日志
     * @param userLogPojo
     * @return
     */
    public int saveLog(UserLogPojo userLogPojo){
        return userLogMapper.addUserLog(userLogPojo);
    }

    /**
     * 获取用户当天的提款次数
     * @param nickid
     * @return
     */
    public int getTakeMoneyDailyNum(String nickid){
        return  userCashMapper.getTakeMoneyDailyNum(nickid);
    }

    /**
     * 新版提款接口
     * @param bean
     * @param resultmap
     */
    public void newtakeMoney(PayBean bean,HashMap<String,Object> resultmap){
        try {
            double tkMoney = bean.getTkMoney();
            if (tkMoney >= 5000000) {
                bean.setBusiErrCode(205);
                bean.setBusiErrDesc("提款金额不能超过5000000元");
                userLogMapper.addUserLog(new UserLogPojo(bean.getUid(), "支付网关编号:" + bean.getBankid() + "-金额:" + tkMoney + "-错误:" + bean.getBusiErrDesc(), bean.getIpAddr(), "提现失败"));
//            userLogMapper.addUserLog(new UserLogPojo()bean, "提现失败", "支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney() + "-错误:" + bean.getBusiErrDesc());
                return;
            }
            if (tkMoney < 10) {
                bean.setBusiErrCode(205);
                bean.setBusiErrDesc("提款金额不能低于10元");
                userLogMapper.addUserLog(new UserLogPojo(bean.getUid(), "支付网关编号:" + bean.getBankid() + "-金额:" + tkMoney + "-错误:" + bean.getBusiErrDesc(), bean.getIpAddr(), "提现失败"));
//            userLogMapper.addUserLog(bean, "提现失败", "支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney() + "-错误:" + bean.getBusiErrDesc());
                return;
            }
            // 0  提款到银行    1  提款到支付宝
            if (bean.getTkType() != 0 && bean.getTkType() != 1) {
                bean.setBusiErrCode(1000);
                bean.setBusiErrDesc("不支持的提款方式:" + bean.getBankid());
            }

            //进行提款次数检测
            int num = userCashMapper.getTakeMoneyDailyNum(bean.getUid());
            if ((3 - num) <= 0) {
                bean.setBusiErrCode(-3);
                bean.setBusiErrDesc("今日提款次数已使用完");
                log.info("该用户今日已退款" + num + "次，无法再次提款，用户名:" + bean.getUid());
                return;
            }
            //查询用户的真实姓名
            UserPojo userPojo = userCashMapper.findRealName(bean.getUid());
            bean.setApplydate(userPojo.getAddDate());
            bean.setCardnum(userPojo.getIdcard());
            bean.setRealName(userPojo.getRealName());
            if (!StringUtil.isEmpty(userPojo.getBankCode())) {
                bean.setApplydate(DrawingsTimeUtil.predictTime(Integer.valueOf(userPojo.getBankCode())));//预计到账时间
            }
//        bean.setBusiErrCode(0);
//        bean.setBusiErrDesc("获取成功");
            //提款-手续费检测,小于100元收取2元手续费(优先从不可提款余额收取),大于100则不收手续费
            double balance = 0;
            double nomoney = 0;
            List<UserAcctPojo> userAcctPojos = userAcctMapper.checkBrokerage(tkMoney, bean.getUid());
            if (userAcctPojos == null || userAcctPojos.size() != 1) {
                log.info("提款-手续费检测失败,查询不到指定用户账户:" + bean.getUid());
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("账户不存在~");
                userLogMapper.addUserLog(new UserLogPojo(bean.getUid(), "支付网关编号:" + bean.getBankid() + "-金额:" + tkMoney + "-错误:" + bean.getBusiErrDesc(), bean.getIpAddr(), "提现失败"));
//            savelog(bean, "提现失败", "支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney() + "-错误:" + bean.getBusiErrDesc(),jcn);
                return;
            } else {
                UserAcctPojo userAcctPojo = userAcctPojos.get(0);
                balance = userAcctPojo.getIbalance();
                nomoney = userAcctPojo.getNodrawmoney();
            }
            if (balance >= 0) {
                //无需手续费
                if (tkMoney >= 100) {
                    bean.setHandmoney(0);
                } else {
                    //需手续费
                    if (nomoney < TAKEMONEY_POUNDAGE) {
                        //提款小于100且不可提现余额不足2元，则从提款金额扣款
                        bean.setTkMoney(tkMoney - TAKEMONEY_POUNDAGE);
                        bean.setHandmoney(TAKEMONEY_POUNDAGE);
                    } else {
                        bean.setHandmoney(TAKEMONEY_POUNDAGE);
                    }
                }
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("可提款金额不足 ：" + tkMoney);
                return;
            }
            HashMap map = new HashMap();
            map.put("uid", bean.getUid());
            map.put("pwd", bean.getPwd());
            map.put("realName", bean.getRealName());
            map.put("tkMoney", tkMoney);
            map.put("tkType", bean.getTkType());
            map.put("handmoney", bean.getHandmoney());
            map.put("applydate", bean.getApplydate());
            map.put("busiErrCode", "");
            map.put("busiErrDesc", "");
            map.put("applyid", "");
            userCashMapper.userDrawMoney(map);//调用存储过程
            Integer ret = (Integer) map.get("busiErrCode");
            bean.setApplyid((String) map.get("applyid"));
            bean.setBusiErrCode((Integer) map.get("busiErrCode"));
            bean.setBusiErrDesc((String) map.get("busiErrDesc"));
//            if (ret != 0) {
//                bean.setBusiErrCode(1001);
//                bean.setBusiErrDesc(UserErrCode.getErrDesc(bean.getBusiErrCode()));
//                userLogMapper.addUserLog(new UserLogPojo(bean.getUid(), "支付网关编号:" + bean.getBankid() + "-金额:" + tkMoney + "-错误:" + bean.getBusiErrDesc(), bean.getIpAddr(), "提现失败"));
////            savelog(bean, "提现失败", "支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney() + "-错误:" + bean.getBusiErrDesc(), jcn);
//            } else {
                UserCashPojo userCashPojo = userCashMapper.findDrawMoneyStatus(bean.getApplyid());
                if (userCashPojo != null) {
                    Integer success = userCashPojo.getIsuccess();
                    String state = "";
                    if (success == 0 || success == 4 || success == 7 || success == 11) {
                        state = "处理中";
                    } else if (success == 1 || success == 5 || success == 8 || success == 12) {
                        state = "提款成功";
                    } else if (success == 2 || success == 6 || success == 13) {
                        state = "提款失败";
                    } else if (success == 3) {
                        state = "银行处理失败";
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    resultmap.put("cashid", userCashPojo.getIcashid());
                    resultmap.put("money", userCashPojo.getImoney());
                    resultmap.put("rate", userCashPojo.getIrate());
                    resultmap.put("cashdate", userCashPojo.getCcashdate());
                    if(userCashPojo.getCcashdate()!=null){
                        resultmap.put("cashdate", simpleDateFormat.format(userCashPojo.getCcashdate()));
                    }
                    resultmap.put("memo", userCashPojo.getCmemo());
                    resultmap.put("cconfdate", userCashPojo.getCconfdate());
                    if(userCashPojo.getCconfdate()!=null){
                        resultmap.put("cconfdate", simpleDateFormat.format(userCashPojo.getCconfdate()));
                    }
                    resultmap.put("cpredicttime", userCashPojo.getCpredicttime());
                    resultmap.put("success", userCashPojo.getIsuccess());
                    resultmap.put("state", state);
                    bean.setBusiXml(JSONObject.toJSONString(resultmap));
                }
//            }
            userLogMapper.addUserLog(new UserLogPojo(bean.getUid(), "支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney(), bean.getIpAddr(), "提现"));
//        savelog(bean, "提现", "支付网关编号:"+bean.getBankid()+"-金额:"+bean.getTkMoney(), jcn);
        }catch (Exception e){
            bean.setBusiErrCode(UserErrCode.ERR_EXCEPTION);
            bean.setBusiErrDesc(UserErrCode.getErrDesc(bean.getBusiErrCode()));
            log.error("BankBeanStub::takeMoney", e);
            userLogMapper.addUserLog(new UserLogPojo(bean.getUid(),"支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney() + "-错误:" + bean.getBusiErrDesc(), bean.getIpAddr(), "提现失败"));
//            savelog(bean, "提现失败", "支付网关编号:" + bean.getBankid() + "-金额:" + bean.getTkMoney() + "-错误:" + bean.getBusiErrDesc(), jcn);
        }
    }

    /**
     *	银行卡号校验
     * @param bean
     * @return
     */
    public void checkCardNo(PayBean bean){
        try {
            String cardNo = bean.getCardNo().contains("%2B") ? bean.getCardNo().replaceAll("%2B", "+") : bean.getCardNo();
            //将银行卡进行解密
            String regex = "[0-9]*";
            if(!cardNo.matches(regex)){//卡号不是纯数字即为密文
//                if(bean.getMtype() == 2 ){
                    String decrypt = SecurityTool.iosdecrypt(cardNo);
                    cardNo =decrypt;
//                }else{
//                    String decrypt = CaiyiEncrypt.dencryptStr(cardNo);
//                    cardNo =decrypt;
//                }
            }
            bean.setBusiErrDesc("银行卡号输入正确");
            Luhn luhn = new Luhn(cardNo);
            boolean check = luhn.check();
            if(!check){
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("您输入的银行卡号有误");
            }
//            checkLocaCardNo(bean, pool, tid);
            //选择已绑定的卡无需校验
            if(!StringUtil.isEmpty(bean.getCardPass()) && "1".equals(bean.getCardPass())){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("银行卡号输入正确");
                return;
            }
            log.info("检查银行卡前6后4,用户id:"+bean.getUid());
//            List<UmpayProtocalPojo> umpayProtocalPojos = umpayProtocalMapper.findUmpayByNickid(bean.getUid());
            List<RechCardPojo> rechCardPojos = rechCardMapper.findRechCardByNickid(bean.getUid());
            int rs = checkCardNoFromDB(bean,cardNo,rechCardPojos);
            if(rs==-1){
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("该卡已存在请勿重复添加");
            }else if(rs==-3){
                bean.setBusiErrCode(-2);
                bean.setBusiErrDesc("银行卡检测发生错误");
            }else if(rs==-2){
                bean.setBusiErrCode(-2);
                bean.setBusiErrDesc("该卡已存在请勿重复添加");
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("您输入的银行卡号有误");
            log.error("银行卡号输入错误,银行卡号:"+bean.getCardNo(),e);
        }
    }

    private  int checkCardNoFromDB(PayBean bean,  String cardNo, List<RechCardPojo> rechCardPojos) {
        try {
            String firstSixCardNo = cardNo.substring(0, 6);
            String lastFourCardNo = cardNo.substring(cardNo.length() - 4, cardNo.length());
            if (rechCardPojos != null && rechCardPojos.size() > 0) {
                for (RechCardPojo rechCardPojo : rechCardPojos) {
                    String safekey = rechCardPojo.getSafeKey();
                    // 通过safekey 获取银行卡号
                    BaseReq<SafeBean> req = new BaseReq<>(SysCodeConstant.TRADECENTER);
                    SafeBean safeBean = new SafeBean();
                    safeBean.setBankcardId(safekey);
                    req.setData(safeBean);
                    BaseResp<SafeBean> resp = safeCenterInterface.bankCard(req);
                    SafeBean safeBeanResult = resp.getData();
                    if (safeBeanResult == null) break;
//                String cardno = rechCardPojo.getCardNo();
                    String cardno = safeBeanResult.getBankcard();
                    //将银行卡进行解密
//                String card_encry = "";
//                if(cardno.length() > 24){
//                    String fore = cardno.substring(0, 24);
//                    String encrypt = CaiyiEncrypt.dencryptStr(fore);
//                    String ss = cardno.substring(24, cardno.length());
//                    card_encry= encrypt + ss;
//                    log.info("cardno： " + cardno  + "encrypt:" + encrypt);
//                    cardno = card_encry;
//                }
                    String firstSixLocalCardNo = cardno.substring(0, 6);
                    String lastFourLocalCardNo = cardno.substring(cardno.length() - 4, cardno.length());
                    //判断事都是前6位和后四位相等
                    if (firstSixCardNo.equals(firstSixLocalCardNo) && lastFourCardNo.equals(lastFourLocalCardNo)) {
                        //判断是否是同一张卡
                        if (cardNo.equals(cardno)) {
                            //判断是否显示
                            String cstatus = String.valueOf(rechCardPojo.getStatus());
                            if ("0".equals(cstatus)) {
                                log.info("该卡已绑定但是未显示,cardno:" + cardNo + "用户信息：" + bean.getUid());
                                return 1;
                            } else {
                                log.info("该卡已经绑定，且以显示,cardno:" + cardNo + "用户信息：" + bean.getUid());
                                return -2;
                            }
                        } else {
                            log.info("已存在前六位，后四位相同的银行卡号,cardno:" + cardNo + "用户信息：" + bean.getUid());
                            return -1;
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("该卡检测发生错误",e);
        }
        log.info("没有相同的银行卡存在,cardno:"+cardNo+"用户信息："+bean.getUid());
        return 0;
    }

    //查询银行卡额度信息
    public HashMap queryBankCardLimitInfo(PayBean bean){
        HashMap map = new HashMap();
        try {
            BankCardSupportChannel bankCardSupportChannel = null ;
            if(StringUtil.isEmpty(bean.getChannel())&&StringUtil.isEmpty(bean.getProduct())){
                bankCardSupportChannel = bankCardSupportChannMapper.findLimitLinesByBankcodeType(bean.getBankCode(),String.valueOf(bean.getCardtype()));
            }else{
                bankCardSupportChannel = bankCardSupportChannMapper.findLimitLinesByBankcodeTypeCP(bean.getBankCode(),String.valueOf(bean.getCardtype()),bean.getChannel(),bean.getProduct());
            }
            if(bankCardSupportChannel !=null){
                String maxlimit = bankCardSupportChannel.getCmaxlimit();
                if(StringUtil.isEmpty(maxlimit)){
                    maxlimit = "暂无";
                }
                map.put("maxlimit", maxlimit);
                String daylimit = bankCardSupportChannel.getCdaylimit();
                if(StringUtil.isEmpty(daylimit)){
                    daylimit = "暂无";
                }
                map.put("daylimit", daylimit);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("查询成功");
                bean.setBusiXml(JSONObject.toJSONString(map));
            }else{
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("暂不支持该银行卡充值");
                log.info("没有支持该卡的渠道,银行卡编码:"+bean.getBankCode()+" 银行卡类型:"+bean.getCardtype());
            }
        }catch(Exception e){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询银行卡额度出错！");
            log.error("查询银行卡额度出错,银行卡编码:"+bean.getBankCode()+" 银行卡类型:"+bean.getCardtype(),e);
        }
        return map;
    }

    /**
     * 提款-合法性校验
     */
    public void checkDrawMoneyValidity(PayBean bean){

        log.info("提款-手续费检测开始,nickid:" + bean.getUid() + ",tkmoney:" + bean.getTkMoney());
        //用户填写的密码进行匹配验证
        try
        {
            String contents = bean.getContents();
            contents = MD5Util.compute(SecurityTool.iosdecrypt(contents)+ UserConstants.DEFAULT_MD5_KEY);
            if(!((bean.getPwd()).equals(contents))){
                bean.setBusiErrCode(-3);
                bean.setBusiErrDesc("密码错误,请重新填写");
                log.info("该用户填写的密码错误，用户名:"+bean.getUid()+" 密码:"+contents);
                return;
            }
            //提款最小金额校验
            double tkMoney = bean.getTkMoney();
            //提款最大金额校验
            if(bean.getTkMoney() >= 5000000)
            {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("提款金额不能超过5000000元");
                return;
            }
            //用户当前可提款金额校验
            double balance = 0d;
            double currbalance = 0d;	//用户当前余额
            double nomoney = 0d;		//用户当前不可提款余额
            double alldrawmoney = 0d;	//用户当前可提款余额
            List<UserAcctPojo> userAcctPojos = userAcctMapper.checkDrawMoneyValidity(bean.getUid());
//            String sql = "select ibalance - ? balance,ibalance currbalance,nodrawmoney nomoney, alldrawmoney alldrawmoney from tb_user_acct where cnickid = ?";
//            JdbcRecordSet jrs = jcn.executeQuery(sql, new Object[] {tkMoney,bean.getUid()});
            if(userAcctPojos == null || userAcctPojos.size() != 1) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("账户不存在!");
                return;
            }else{
                UserAcctPojo userAcctPojo = userAcctPojos.get(0);
                currbalance =userAcctPojo.getIbalance();
                balance = userAcctPojo.getIbalance()-bean.getTkMoney();
                nomoney = userAcctPojo.getNodrawmoney();
                alldrawmoney = StringUtil.getNullDouble(userAcctPojo.getAlldrawmoney());
                if(currbalance > 0 && alldrawmoney < 10){
                    bean.setBusiErrCode(110);
                    bean.setBusiErrDesc("您当前账户可提现金额不足10元，不能进行提款操作；若有问题，请联系客服");
                    return;
                }

                if(currbalance <= 0)
                {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("您的账户余额为" + currbalance + ",无法进行提款!");
                    return;
                }
                if(alldrawmoney < tkMoney)
                {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("您本次最多可提款" + alldrawmoney + "元!");
                    return;
                }

            }
            if(tkMoney < 10)
            {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("提款金额不能小于10元!");
                return;
            }
            //检测提款所需手续费
//            String resultxml = "";
            HashMap<String,String> map = new HashMap<String,String>();
            String desc = "注:提款金额小于100元，手续费2元/笔";
            DecimalFormat df = new DecimalFormat("#0.00");
            if(tkMoney < 0 && nomoney < 0) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("提款金额必须大于" + 0 + "元!");
                return;
            }
            if(balance >= 0){
                //提款金额>=100,则不收取手续费
                if(tkMoney >= 100) {
                    map.put("accmoney",df.format(bean.getTkMoney()));
                    map.put("deductmoney",df.format(bean.getTkMoney()));
                    map.put("rate",df.format(0));
                    map.put("tkdesc",desc);
                    bean.setBusiXml(JSONObject.toJSONString(map));
                }else{
                    //需手续费
                    if(TAKEMONEY_POUNDAGE > 0) {
                        bean.setBusiErrCode(101);
                        if(nomoney < 0) {
                            //不可提现余额不足2元,从提款金额扣款
                            map.put("accmoney",df.format((bean.getTkMoney() - TAKEMONEY_POUNDAGE)));
                            map.put("deductmoney",df.format(bean.getTkMoney()));
                            map.put("rate",df.format(TAKEMONEY_POUNDAGE));
                            map.put("tkdesc",desc);
                            bean.setBusiXml(JSONObject.toJSONString(map));
                        }else {
                            map.put("accmoney",df.format(bean.getTkMoney()));
                            map.put("deductmoney",df.format(bean.getTkMoney()+TAKEMONEY_POUNDAGE));
                            map.put("rate",df.format(TAKEMONEY_POUNDAGE));
                            map.put("tkdesc",desc);
                            bean.setBusiXml(JSONObject.toJSONString(map));
                        }
                    }
                }
            } else{
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("您的可提款余额不足!");
                return;
            }
        }catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("检测提款金额出现异常!");
            log.info("提款-合法性校验发生异常,cnickid:" + bean.getUid() + ",tkmoney:" + bean.getTkMoney() + ",异常信息:" + e.getMessage());
        }
    }
}
