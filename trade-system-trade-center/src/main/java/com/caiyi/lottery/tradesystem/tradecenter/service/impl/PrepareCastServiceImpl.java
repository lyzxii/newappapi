package com.caiyi.lottery.tradesystem.tradecenter.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.client.PayBasicInterface;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.tradecenter.service.PrepareCastService;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.proj.ProjUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.Acct_UserPojo;
import redpacket.bean.RedPacketBean;
import trade.bean.TradeBean;
import trade.dto.PrepareCastDto;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PrepareCastServiceImpl implements PrepareCastService {

    @Autowired
    UserBasicInfoInterface userBasicInterface;

    @Autowired
    RedPacketCenterInterface redPacketCenterInterface;

    @Autowired
    RedisClient redisClient;

    @Autowired
    PayBasicInterface payBasicInterface;


    @Override
    public PrepareCastDto prepare4Pay(TradeBean bean) throws Exception {
        log.info("开始执行投注前检测uid=" + bean.getUid());
        if (StringUtil.isEmpty(bean.getTrade_isource())) {
            bean.setTrade_isource("2145");
        }
        int source = Integer.valueOf(bean.getTrade_isource());
        PrepareCastDto result = new PrepareCastDto();
        BaseResp<Acct_UserPojo> resp = userBasicInterface.queryUserAccountInfo(new BaseReq<>(bean, SysCodeConstant.TRADECENTER));
        if (BusiCode.FAIL.equals(resp.getCode())) {
            log.error("投注前,查询用户:{}可用余额失败", bean.getUid());
            throw new RuntimeException("查询用户可用余额失败");
        }
        Acct_UserPojo acct_user = resp.getData();
        //添加余额
        result.setUsermoney(acct_user.getBalance());
        //添加红包
        appendUserRedpacket(acct_user, bean, result);
        appendBuyCount(acct_user,bean,result);
        // 查询iOS web支付白名单状态
        appendIOSWebpay(bean, source, result, acct_user);
        //数字彩添加期次信息
        appendSzcPeroidInfo(bean, result);
        return result;
    }


    private void appendSzcPeroidInfo(TradeBean bean, PrepareCastDto result) throws ParseException {
        String[] info = getSzcPhaseInfo(bean.getTrade_gameid());
        if(info!=null){
           Map<String,Object> szMap=new HashMap<>();
           szMap.put("t",info[0]);
           szMap.put("pid",info[1]);
           result.setSzc(szMap);
        }
    }


    private void appendIOSWebpay(TradeBean bean, int source, PrepareCastDto result, Acct_UserPojo acct_user) {
        if (source >= 2000 && source <= 3000) {
            String whitegrade = acct_user.getWhitegrade();
            int iopen=StringUtil.isEmpty(whitegrade)?0:Integer.valueOf(whitegrade);
            String amount =acct_user.getAmount();
            Map<String, Object> webpayMap = new HashMap<>();
            webpayMap.put("whitelist",getWebpayWhitelist(bean.getUid(),iopen,amount));
            result.setIoswebpay(webpayMap);
        }
    }

    //添加红包
    private void appendUserRedpacket(Acct_UserPojo acct_user, TradeBean bean, PrepareCastDto result) {
        String tradeImoney = bean.getTrade_imoney();
        String tradeGameid = bean.getTrade_gameid();
        RedPacketBean redPacketBean = new RedPacketBean();
        redPacketBean.setTrade_imoney(tradeImoney);
        redPacketBean.setTrade_isource(bean.getTrade_isource());
        redPacketBean.setTrade_gameid(tradeGameid);
        redPacketBean.setIstate(1);
        redPacketBean.setFlag(11);
        redPacketBean.setUid(bean.getUid());
        redPacketBean.setCmemo(acct_user.getAgentid());
        BaseResp<Page> resp = redPacketCenterInterface.queryRedpacket4Pay(new BaseReq<>(redPacketBean, SysCodeConstant.TRADECENTER));
        if (BusiCode.FAIL.equals(resp.getCode())) {
            log.error("查询用户可用红包失败,uid:{}",bean.getUid());
            resp.setData(new Page());
        }
        result.setRedpacket(resp.getData().getDatas());
    }

    private void appendBuyCount(Acct_UserPojo acct_user, TradeBean bean, PrepareCastDto result) {
        String idcard = acct_user.getIdcard();
        String realname = acct_user.getRealName();
        int mobbind = acct_user.getMobbindFlag();
        String bankcard = acct_user.getDrawBankCard();
        String idcardbind = StringUtil.isEmpty(idcard) || StringUtil.isEmpty(realname) ? "0" : "1";
        String bankcardbind = StringUtil.isEmpty(bankcard) ? "0" : "1";
        BaseResp<Integer> resp=redPacketCenterInterface.countGroupBuy(new BaseReq<>(bean, SysCodeConstant.TRADECENTER));
        if(BusiCode.FAIL.equals(resp.getCode())){
            log.error("查询用户:{}合买次数失败",bean.getUid());
            //throw new RuntimeException("查询用户合买次数失败");
        }
        int groupBuyCnt=resp.getData()==null?0:resp.getData();
        resp=userBasicInterface.countSelfBuy(new BaseReq<>(bean, SysCodeConstant.TRADECENTER));
        if(BusiCode.FAIL.equals(resp.getCode())){
            log.error("查询用户:{}自买次数失败",bean.getUid());
            //throw new RuntimeException("查询用户自买次数失败");
        }
        int selfBuyCnt=resp.getData()==null?0:resp.getData();
        Map<String, Object> buycntMap = new HashMap<>();
        buycntMap.put("buycount",groupBuyCnt+selfBuyCnt);
        buycntMap.put("mobbind",mobbind);
        buycntMap.put("idcardbind",idcardbind);
        buycntMap.put("bankcardbind",bankcardbind);
        result.setBuycount(buycntMap);
    }

    //获取白名单等级
    private String getWebpayWhitelist(String nickid, int iopen,String amount) {
        String whitelist = "1";
        String key = "ioswebpay_whitelist_" + nickid;
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(key);
        Object obj = redisClient.getString(cacheBean,log, SysCodeConstant.TRADECENTER);
        if (obj != null&&!"".equals(obj.toString())) {
            whitelist = obj.toString();
            if("1".equals(whitelist)){//0-app支付状态，1-web支付状态
                //查询白名单等级，>=2设置成 0
                if(iopen >= 2){
                    whitelist = "0";
                    putCache(key,whitelist);
                }
            }
            log.info("从缓存查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
        } else {
            BaseBean bean=new BaseBean();
            BaseResp<Integer> resp=payBasicInterface.queryWhitelistStatus(new BaseReq<>(bean, SysCodeConstant.TRADECENTER));
            if (BusiCode.SUCCESS.equals(resp.getCode())&&resp.getData()!=null) {
                whitelist = resp.getData()+"";
            } else {
                double damount = 0;
                if(StringUtil.isEmpty(amount)){
                    damount = 0;
                }else{
                    damount = Double.parseDouble(amount);
                }
                if (damount > 0) {
                    whitelist = "0";
                }
            }
            log.info("从数据库查用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
            // 保存10天
            putCache(key,whitelist);
        }
        log.info("用户iOS web支付白名单状态=" + whitelist + ",nickid=" + nickid);
        return whitelist;
    }

    private void putCache(String cacheKey, Object obj) {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(cacheKey);
        cacheBean.setValue(obj.toString());
        cacheBean.setTime(10*Constants.TIME_DAY);// 1天
        redisClient.setString(cacheBean, log, SysCodeConstant.TRADECENTER);
    }

    /**
     * 查询数字彩当前期次信息.
     * @param gid 彩种id
     */
    private String[] getSzcPhaseInfo(String gid) throws ParseException {
        String[] info = null;
        if (!ProjUtils.isSzc(gid)) {
            return info;
        }
        String xmlpath = FileConstant.DATA_DIR + File.separator + "phot" + File.separator + gid;
        JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath, "s.xml"));
        int count = xml.countXmlNodes("row");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        Calendar endtime = Calendar.getInstance();
        // 截止时间
        String et = null;
        // 期次
        String pid = null;
        for (int i = 0; i < count; i++) {
            et = xml.getStringValue("row[" + i + "].@et");
            pid = xml.getStringValue("row[" + i + "].@pid");
            endtime.setTime(df.parse(et));
            // 截止时间大于系统时间
            if (endtime.compareTo(now) > 0) {
                info = new String[2];
                info[0] = et;
                info[1] = pid;
                break;
            }
        }
        return info;
    }
}
